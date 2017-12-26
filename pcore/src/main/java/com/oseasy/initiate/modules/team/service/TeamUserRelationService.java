/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/oseasy/initiate">JeeSite</a> All rights reserved.
 */
package com.oseasy.initiate.modules.team.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.utils.sms.SMSUtilAlidayu;
import com.oseasy.initiate.common.utils.sms.impl.SendParam;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.sys.dao.BackTeacherExpansionDao;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.BackTeacherExpansion;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.sysconfig.utils.SysConfigUtil;
import com.oseasy.initiate.modules.sysconfig.vo.SysConfigVo;
import com.oseasy.initiate.modules.team.dao.TeamDao;
import com.oseasy.initiate.modules.team.dao.TeamUserRelationDao;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;

import net.sf.json.JSONObject;

/**
 * 团队人员关系表Service
 * @author zhangzheng
 * @version 2017-03-06
 */
@Service
@Transactional(readOnly = true)
public class TeamUserRelationService extends CrudService<TeamUserRelationDao, TeamUserRelation> {
	@Autowired
	TeamUserRelationDao teamUserRelationDao;
	@Autowired
	TeamDao teamDao;
	@Autowired
	UserDao userDao;
	@Autowired
	OaNotifyService oaNotifyService;
	@Autowired
	UserService userService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private BackTeacherExpansionDao backTeacherExpansionDao;
	public TeamUserRelation get(String id) {
		return super.get(id);
	}
	private void batchSendSms(List<SendParam> sps,String temp){
		if(sps!=null&&temp!=null){
			for(SendParam parm:sps){
				try {
					SMSUtilAlidayu.sendSmsTemplate(parm.getToMobile(),SMSUtilAlidayu.AILIDAYU_SMS_TemplateInvite,parm);
				} catch (Exception e) {
					logger.error("团队邀请短信发送失败："+parm.getToMobile()+e.getMessage());
				}
			}
		}
	}
	@Transactional(readOnly = false)
	public JSONObject pullIn(String offices,String userIds,String userType,String teamId) {
		JSONObject json=new JSONObject();
		try {
			//查询所有用户
			List<String> userList=findAllUserId(userType,offices,userIds);
			int ress=0;
			if (userList.size()>0) {
				for (String user1 : userList) {
					User user=userDao.get(user1);
					//插入申请记录
					JSONObject res= pullIn(user,teamId);
					if("1".equals(res.getString("ret"))){
						ress++;
					}
				}
			}
			Team team= teamService.get(teamId);
			if (team!=null) {
				TeamUserRelation teamUserRelation=new TeamUserRelation();
				teamUserRelation.setTeamId(teamId);
				repTeamstate(team);
			}

			json.put("success", true);
			json.put("res", ress);
		} catch (Exception e) {
			json.put("success", false);
			logger.error("负责人直接拉入失败："+e.getMessage());
		}
		return json;
	}
	//前台方法（不确定和后台的是否完全一致的逻辑，所以分开重构）
	@Transactional(readOnly = false)
	public JSONObject frontToInvite(String offices,String userIds,String userType,String teamId){
		JSONObject json=new JSONObject();
		try {
			//查询所有用户 
			List<String> userList=findAllUserId(userType,offices,userIds);
			int ress=0;
			if (userList.size()>0) {
				Team team=null;
				if (teamId!=null) {
					team= teamService.get(teamId);
				}
				if(team==null){
					json.put("success", false);
					json.put("msg", "邀请失败，未找到团队!");
					return json;
				}
				if(!"0".equals(team.getState())){
					json.put("success", false);
					json.put("msg", "邀请失败，只能为建设中的团队进行邀请!");
					return json;
				}
				List<SendParam> sps=new ArrayList<SendParam>();
				for (String user1 : userList) {
					if (StringUtil.isNotBlank(user1)) {
						User user=userDao.get(user1);
						if (user!=null) {
							//不需要回复 直接拉入团队
							//是否直接加入团队 1是有限制 0是无限制
							SysConfigVo scv=SysConfigUtil.getSysConfigVo();
							if(scv!=null && "0".equals(scv.getTeamConf().getJoinOnOff())) {
								//0成功   1已加入其他团队 2从已申请变更加入3新增插入
								JSONObject res=pullIn(user,teamId);
								if("1".equals(res.getString("ret"))){
									repTeamstate(team);
									ress++;
								}
							}else{
								int res= inseTeamUser(user,teamId,"2");
								if (res==3) {
									repTeamstate(team);
									User teamUser=userDao.get(team.getCreateBy().getId());
									SendParam parm = new SendParam(teamUser.getName(),team.getName(),user.getMobile());
									sps.add(parm);
									ress++;
									inseOaNotify(UserUtils.getUser(), user1,teamId);
								}

							}
						}
						
						
					}
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						batchSendSms(sps,SMSUtilAlidayu.AILIDAYU_SMS_TemplateInvite);
					}
				}).start();
			}
			json.put("success", true);
			json.put("res", ress);
		} catch (Exception e) {
			json.put("msg", "邀请失败，未知的系统错误!");
			json.put("success", false);
			logger.error("团队邀请出错："+e.getMessage());
		}
		return json;
	}
	//后台方法（不确定和前台的是否完全一致的逻辑，所以分开重构）
	@Transactional(readOnly = false)
	public JSONObject toInvite(String offices,String userIds,String userType,String teamId){
		JSONObject json=new JSONObject();
		try {
			//查询所有用户 
			List<String> userList=findAllUserId(userType,offices,userIds);
			Team team=null;
			if (teamId!=null) {
				team= teamService.get(teamId);
			}
			if(team==null){
				json.put("success", false);
				return json;
			}
			int ress=0;
			if (userList.size()>0) {
				List<SendParam> sps=new ArrayList<SendParam>();
				for (String user1 : userList) {
					if (StringUtil.isNotBlank(user1)) {
	        			User user=userDao.get(user1);
						User teamUser=team.getCreateBy();
	        			if (user!=null) {
	        				int res= inseTeamUser(user,teamId,"2");
	        				if (res==3) {
	        					ress++;
								SendParam parm = new SendParam(teamUser.getName(),team.getName(),user.getMobile());
								sps.add(parm);
							}
        					inseOaNotify(UserUtils.getUser(), user1,teamId);
        				}
        			}
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						batchSendSms(sps,SMSUtilAlidayu.AILIDAYU_SMS_TemplateInvite);
					}
				}).start();
			}
			json.put("success", true);
			json.put("res", ress);
		} catch (Exception e) {
			json.put("success", false);
			logger.error("团队邀请出错："+e.getMessage());
		}
		
		return json;
	}

	/**
	 * 判断是否以有申请记录   状态  !=3
	 * @param teamId
	 * @param userId
	 * @return
	 */
	public Integer findIsApplyTeam(String teamId,String userId) {
		return teamUserRelationDao.findIsApplyTeam(teamId, userId);
	}

	public List<TeamUserRelation> findList(TeamUserRelation teamUserRelation) {
		return super.findList(teamUserRelation);
	}

	public Page<TeamUserRelation> findPage(Page<TeamUserRelation> page, TeamUserRelation teamUserRelation) {
		return super.findPage(page, teamUserRelation);
	}

	@Transactional(readOnly = false)
	public void updateStateInTeam(TeamUserRelation teamUserRelation) {
		dao.updateStateInTeam(teamUserRelation);

	}

	public TeamUserRelation getByTeamAndUser(TeamUserRelation teamUserRelation) {
		return dao.getByTeamAndUser(teamUserRelation);
	}

	public List<TeamUserRelation> getStudents(TeamUserRelation teamUserRelation) {
		return dao.getStudents(teamUserRelation);
	}

	public List<TeamUserRelation> getTeachers(TeamUserRelation teamUserRelation) {
		return dao.getTeachers(teamUserRelation);
	}

	@Transactional(readOnly = false)
	public void save(TeamUserRelation teamUserRelation) {
		super.save(teamUserRelation);
	}

	@Transactional(readOnly = false)
	public void delete(TeamUserRelation teamUserRelation) {
		super.delete(teamUserRelation);
	}

	 public TeamUserRelation findUserInfo(TeamUserRelation teamUserRelation) {
		 return dao.findUserInfo(teamUserRelation);
	 }

	 @Transactional(readOnly = false)
	 public void updateByUserId(TeamUserRelation teamUserRelation) {
		 dao.updateByUserId(teamUserRelation);
	 }

	public TeamUserRelation getByUser(User user) {
		return teamUserRelationDao.getByUser(user);
	}

	 public TeamUserRelation findUserById(TeamUserRelation teamUserRelation) {
		 return teamUserRelationDao.findUserById(teamUserRelation);
	 }
	 public TeamUserRelation findUserWillJoinTeam(TeamUserRelation teamUserRelation) {
		 return teamUserRelationDao.findUserWillJoinTeam(teamUserRelation);
	 }
	 public TeamUserRelation findUserHasJoinTeam(String userid, String teamid) {
		 TeamUserRelation teamUserRelation=new TeamUserRelation();
		 teamUserRelation.setUser(new User(userid));
		 teamUserRelation.setTeamId(teamid);
		 return teamUserRelationDao.findUserHasJoinTeam(teamUserRelation);
	 }
	 public TeamUserRelation findUserHasJoinTeam(TeamUserRelation teamUserRelation) {
		 return teamUserRelationDao.findUserHasJoinTeam(teamUserRelation);
	 }
	/**插入团队申请记录
	 *
	 * @param teamId 申请的teamId
	 * @param applyUser 插入人userID
 	 * @return >0成功  1加入其他团队2已经申请过 3插入成功
	 */
	@Transactional(readOnly = false)
	public int inseTeamUser(User applyUser,String teamId,String appType ) {
		try {
			TeamUserRelation teamUserRelation=new TeamUserRelation();
			User user=new User();
			user.setId(applyUser.getId());
			teamUserRelation.setUser(user);
			teamUserRelation.setTeamId(teamId);

			TeamUserRelation teamUserRelation2=this.findUserInfo(teamUserRelation);//根据用户id查询用户是否存在
			if (teamUserRelation2!=null) {//如果用户存在并且状态是加入状态
				//判断是否已加入团队
				if ("0".equals(teamUserRelation2.getState())||"4".equals(teamUserRelation2.getState())) {
					return 1;
				}
				//判断是否已经申请过或被邀请过该团队
				if (teamUserRelation2.getTeamId()!=null&&teamId.equals(teamUserRelation2.getTeamId())
						&&(teamUserRelation2.getState().equals("1")||teamUserRelation2.getState().equals("2"))) {
					teamUserRelation2.setUpdateDate(new Date());
					if(!appType.equals(teamUserRelation2.getState())){
						teamUserRelation2.setState("0");
						save(teamUserRelation2);
						return 3;
					}else{
						teamUserRelation2.setState(appType);
						save(teamUserRelation2);
						return 2;
					}
				}
			}
			if(!checkTeamNum(teamId, applyUser)){
				return 0;
			}
			teamUserRelation.setId(IdGen.uuid());//添加id
			Date now = new Date();
			teamUserRelation.setUser(applyUser);
			teamUserRelation.setTeamId(teamId);
			teamUserRelation.setCreateDate(now);
			teamUserRelation.setCreateBy(applyUser);
			teamUserRelation.setUpdateDate(now);
			teamUserRelation.setUpdateBy(applyUser);
			teamUserRelation.setState(appType);
			teamUserRelation.setDelFlag("0");
			teamUserRelation.setUserType(applyUser.getUserType());
			teamUserRelation.setIsNewRecord(true);
			this.save(teamUserRelation);
			return 3;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0;
		}
	}
	private boolean checkTeamNum(String tid,User user){
		Team teamNums=teamDao.findTeamJoinInNums(tid);
		if("1".equals(user.getUserType())&&teamNums.getUserCount()>=teamNums.getMemberNum()){
			return false;
		}
		if("2".equals(user.getUserType())){
			BackTeacherExpansion teab=backTeacherExpansionDao.getByUserId(user.getId());
			if(teab==null){
				return false;
			}
			if("2".equals(teab.getTeachertype())&&teamNums.getEnterpriseNum()>=teamNums.getEnterpriseTeacherNum()){//企业导师
				return false;
			}
			if(!"2".equals(teab.getTeachertype())&&teamNums.getSchoolNum()>=teamNums.getSchoolTeacherNum()){//校内导师
				return false;
			}
		}
		return true;
	}


	/**直接插入团队人员
	 *
	 * @param teamId 申请的teamId
	 * @param applyUser 插入人userID
 	 * @return >0成功   1已加入其他团队 2从已申请变更加入3新增插入
	 */
	@Transactional(readOnly = false)
	public JSONObject pullIn(User applyUser,String teamId) {
		JSONObject js=new JSONObject();
		js.put("ret", "0");
		try {
			TeamUserRelation teamUserRelation=new TeamUserRelation();
			User user=new User();
			user.setId(applyUser.getId());
			teamUserRelation.setUser(user);
			teamUserRelation.setTeamId(teamId);

			TeamUserRelation teamUserRelation2=this.findUserInfo(teamUserRelation);//根据用户id查询用户是否存在
			if (teamUserRelation2!=null) {//如果用户存在并且状态是加入状态
				//判断是否已加入本团队
				if ("0".equals(teamUserRelation2.getState())||"4".equals(teamUserRelation2.getState())) {
					js.put("ret", "0");
					js.put("msg", "已经在团队中");
					return js;
				}
				//判断是否已经申请过该团队
				if (teamUserRelation2.getTeamId()!=null&&teamId.equals(teamUserRelation2.getTeamId())
						&&(teamUserRelation2.getState().equals("1")||teamUserRelation2.getState().equals("2"))) {
					teamUserRelation2.setUpdateDate(new Date());
					if("1".equals(teamUserRelation2.getState())){
						teamUserRelation2.setState("0");
						save(teamUserRelation2);
						js.put("ret", "1");
						return js;
					}
				}
			}
			if(!checkTeamNum(teamId, applyUser)){
				js.put("ret", "0");
				js.put("msg", "人数已满");
				return js;
			}
			teamUserRelation.setId(IdGen.uuid());//添加id
			Date now = new Date();
			teamUserRelation.setUser(applyUser);
			teamUserRelation.setTeamId(teamId);
			teamUserRelation.setCreateDate(now);
			teamUserRelation.setCreateBy(applyUser);
			teamUserRelation.setUpdateDate(now);
			teamUserRelation.setUpdateBy(applyUser);
			teamUserRelation.setState("0");
			teamUserRelation.setDelFlag("0");
			teamUserRelation.setUserType(applyUser.getUserType());
			teamUserRelation.setIsNewRecord(true);

			this.save(teamUserRelation);
			js.put("ret", "1");
			return js;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			js.put("ret", "0");
			js.put("msg", "发生了未知的错误");
			return js;
		}
	}

	/**
	 * 修改团队状态
	 * @param teamUserRelation
	 * @param team
	 * @return
	 */
	@Transactional(readOnly = false)
	public void repTeamstate(Team team) {
		if("0".equals(team.getState())||"1".equals(team.getState())){//根据人数处理团队状态只针对建设中、和建设完成的团队
			int  stuCount=0;
			int  te1Count=0;
			int  te2Count=0;
	
			stuCount= teamDao.findStuNumByTeamId(team.getId());
			te1Count= teamDao.findTe1NumByTeamId(team.getId());
			te2Count= teamDao.findTe2NumByTeamId(team.getId());
	
			if (stuCount==team.getMemberNum() && te1Count==team.getSchoolTeacherNum() && te2Count==team.getEnterpriseTeacherNum()) {
				team.setState("1");//将团队状态改为建设完毕
				teamDao.updateTeamState(team);
			}else{
				team.setState("0");//将团队状态改为建设中
				teamDao.updateTeamState(team);
			}
		}

	}


	/**
	 * 插入通知
	 * @param TeamSponsor 团队负责人User
	 * @param teamId 邀请Id
	 */
	@Transactional(readOnly = false)
	public int inseOaNotify(User TeamSponsor,String inseTeamId,String teamId) {
		try {
			User inseTeamUser= userService.findUserById(inseTeamId);
			Team team=new Team();
			team.setSponsor(TeamSponsor.getId());
			//List<Team> teamlist= teamDao.findList(team);
			Team teamTmp = teamDao.get(teamId);
			return this.inseOaNo(teamTmp,TeamSponsor,inseTeamUser) ;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0;
		}
	}

	/**
	 * 插入邀请通知 type=6
	 * @param team
	 * @param TeamSponsor
	 * @param inseTeamUser
	 * @return >0成功
	 */
	@Transactional(readOnly = false)
	public int inseOaNo(Team team,User TeamSponsor,User inseTeamUser) {
		try {
			OaNotifyRecord oaNotifyRecord=new OaNotifyRecord();
			OaNotify oaNotify=new OaNotify();
			oaNotify.setTitle(team.getName()+"团队邀请记录");
			oaNotify.setContent("收到"+team.getName()+"团队的邀请记录");
			oaNotify.setType("6");
			oaNotify.setsId(team.getId());
			oaNotify.setCreateBy(TeamSponsor);
			oaNotify.setCreateDate(new Date());
			oaNotify.setUpdateBy(TeamSponsor);
			oaNotify.setUpdateDate(new Date());
			oaNotify.setEffectiveDate(new Date());
			oaNotify.setSendType("2");
			oaNotify.setStatus("1");

			List<OaNotifyRecord>  recList=new ArrayList<OaNotifyRecord>();
			oaNotifyRecord.setId(IdGen.uuid());
			oaNotifyRecord.setOaNotify(oaNotify);
			oaNotifyRecord.setUser(inseTeamUser);
			oaNotifyRecord.setReadFlag("0");
			oaNotifyRecord.setOperateFlag("0");
			recList.add(oaNotifyRecord);

			oaNotify.setOaNotifyRecordList(recList);
			oaNotifyService.save(oaNotify);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0;

		}
		return 1;
	}

	/**
	 * 插入团队发布通知  type=7
	 * @param team
	 * @param TeamSponsor
	 * @param inseTeamUser
	 * @return >0成功
	 */
	@Transactional(readOnly = false)
	public int inseRelOaNo(Team team,User TeamSponsor,List<String> inseTeamUser) {
		try {
			OaNotify oaNotify=new OaNotify();
			oaNotify.setTitle(team.getName()+(team.getName()!=null&&team.getName().endsWith("团队")?"":"团队")+"创建成功");
			User  userTmp = systemService.getUser(team.getSponsor());
			oaNotify.setContent(userTmp.getName()+"的团队已发布。");
			oaNotify.setType("7");
			oaNotify.setsId(team.getId());
			oaNotify.setCreateBy(TeamSponsor);
			oaNotify.setCreateDate(new Date());
			oaNotify.setEffectiveDate(new Date());
			oaNotify.setUpdateBy(TeamSponsor);
			oaNotify.setUpdateDate(new Date());
			oaNotify.setSendType("2");
			oaNotify.setStatus("1");

			List<OaNotifyRecord>  recList=new ArrayList<OaNotifyRecord>();
			for (String userId : inseTeamUser) {
				if (!userId.equals(team.getSponsor())) {
				OaNotifyRecord oaNotifyRecord=new OaNotifyRecord();
				User user=new User();
				user.setId(userId);
				oaNotifyRecord.setUser(user);
				oaNotifyRecord.setOaNotify(oaNotify);
				oaNotifyRecord.setReadFlag("0");
				oaNotifyRecord.setId(IdGen.uuid());
				oaNotifyRecord.setOperateFlag("0");
				recList.add(oaNotifyRecord);
				}
			}

			oaNotify.setOaNotifyRecordList(recList);
			oaNotifyService.save(oaNotify);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0;

		}
		return 1;
	}

	/**公共方法 根据院校差所有user
	 * @param offices
	 * @param userIds
	 * @return
	 */
	public List<String> findAllUserId(String userType,String offices,String userIds) {
		List<String> officeList=new  ArrayList<String>();
		List<String> userList=new  ArrayList<String>();
		String[] officess=null;
		String[] userIdss=null;

		List<String> userListAll = new ArrayList<String>();


		try {

		if (StringUtils.isNotBlank(offices)) {
			officess= offices.split(",");
			officeList = Arrays.asList(officess);
		}
		if (StringUtils.isNotBlank(userIds)) {
			userIdss= userIds.split(",");
			userList = Arrays.asList(userIdss);
		}

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}

		userListAll.addAll(userList);
		//officeList.add("03f2ded17518420694d402ff64fae0eb");

		//officeList.add("03f2ded17518420694d402ff64fae0eb");

	try {
		if (officeList!=null&&officeList.size()>0) {
			userList = new ArrayList<String>();
			for (String officeId : officeList) {
				User user=new User();
				Office office=new Office();
				office.setId(officeId);
				user.setOffice(office);
				if (userType!=null) {
					user.setUserType(userType);
				}
				List<User> userList1= userDao.findList(user);
				if (userList1!=null&&userList1.size()>0) {
					for (User user2 : userList1) {

						if (user2!=null&&StringUtils.isNotBlank(user2.getId())) {
							if (!userListAll.contains(user2.getId())) {
								userList.add(String.valueOf(user2.getId()));
							}
						}

					}
				}
			}
			userListAll.addAll(userList);
		}
	} catch (Exception e) {
		logger.error(e.getMessage(),e);
	}
		return userListAll;
		//return userList;
	}

	public TeamUserRelation getByTeamUserRelation(TeamUserRelation teamUserRelation) {
		return teamUserRelationDao.getByTeamUserRelation(teamUserRelation);

	}
	@Transactional(readOnly = false)
	public void deleteTeamUserInfo(String userId,String teamId) {
		dao.deleteTeamUserInfo(userId, teamId);
	}
	
	@Transactional(readOnly = false)
	public int insertDeleteOaNo(Team team,User TeamSponsor,User delTeamUser) {
		try {
			OaNotifyRecord oaNotifyRecord=new OaNotifyRecord();
			OaNotify oaNotify=new OaNotify();
			oaNotify.setTitle(team.getName()+"团队删除记录");
			oaNotify.setContent(team.getName()+"团队已将你从团队中删除");
			oaNotify.setType("13");
			oaNotify.setsId(team.getId());
			oaNotify.setCreateBy(TeamSponsor);
			oaNotify.setCreateDate(new Date());
			oaNotify.setUpdateBy(TeamSponsor);
			oaNotify.setUpdateDate(new Date());
			oaNotify.setEffectiveDate(new Date());
			oaNotify.setSendType("2");
			oaNotify.setStatus("1");

			List<OaNotifyRecord>  recList=new ArrayList<OaNotifyRecord>();
			oaNotifyRecord.setId(IdGen.uuid());
			oaNotifyRecord.setOaNotify(oaNotify);
			oaNotifyRecord.setUser(delTeamUser);
			oaNotifyRecord.setReadFlag("0");
			oaNotifyRecord.setOperateFlag("0");
			recList.add(oaNotifyRecord);
			oaNotify.setOaNotifyRecordList(recList);
			oaNotifyService.save(oaNotify);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0;

		}
		return 1;
	}

	/**
	 * 同意回复插入通知
	 * @param team
	 * @param user
	 * @return
	 */

	@Transactional(readOnly = false)
	public int inseAgreeOaNo(Team team,String type,User user,User sentuser) {
		try {
			OaNotifyRecord oaNotifyRecord=new OaNotifyRecord();
			OaNotify oaNotify=new OaNotify();
			if ("5".equals(type)) {
				//团队负责人同意某某的申请插入的通知
				oaNotify.setTitle("申请"+team.getName()+"项目团队的回复记录");
				oaNotify.setContent(user.getName()+"同意你加入"+team.getName()+"项目团队");
				oaNotifyRecord.setUser(sentuser);
			}else {
				//某某同意团队的邀请插入的通知
				oaNotify.setTitle(user.getName()+"回复团队邀请记录");
				oaNotify.setContent(user.getName()+"同意加入"+team.getName()+"项目团队");
				oaNotifyRecord.setUser(team.getCreateBy());
			}
			oaNotify.setType("10");
			oaNotify.setsId(team.getId());
			oaNotify.setCreateBy(user);
			oaNotify.setCreateDate(new Date());
			oaNotify.setUpdateBy(user);
			oaNotify.setUpdateDate(new Date());
			oaNotify.setEffectiveDate(new Date());
			oaNotify.setSendType("2");
			oaNotify.setStatus("1");

			List<OaNotifyRecord>  recList=new ArrayList<OaNotifyRecord>();
			oaNotifyRecord.setId(IdGen.uuid());
			oaNotifyRecord.setOaNotify(oaNotify);
			oaNotifyRecord.setReadFlag("0");
			oaNotifyRecord.setOperateFlag("0");
			recList.add(oaNotifyRecord);

			oaNotify.setOaNotifyRecordList(recList);
			oaNotifyService.save(oaNotify);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0;

		}
		return 1;
	}
	/**
	 * 拒绝回复插入通知
	 * @param team
	 * @param user
	 * @return
	 */

	@Transactional(readOnly = false)
	public int inseRefuseOaNo(Team team,String type,User user,User sentuser) {
		try {
			OaNotifyRecord oaNotifyRecord=new OaNotifyRecord();
			OaNotify oaNotify=new OaNotify();
			if ("5".equals(type)) {
				//团队负责人拒绝某某的申请插入的通知
				oaNotify.setTitle("申请"+team.getName()+"项目团队的回复记录");
				oaNotify.setContent(user.getName()+"拒绝你加入"+team.getName()+"项目团队");
				oaNotifyRecord.setUser(sentuser);
			}else {
				//某某拒绝团队的邀请插入的通知
				oaNotify.setTitle(user.getName()+"回复团队邀请记录");
				oaNotify.setContent(user.getName()+"拒绝加入"+team.getName()+"项目团队");
				oaNotifyRecord.setUser(team.getCreateBy());
			}
			oaNotify.setType("11");
			oaNotify.setsId(team.getId());
			oaNotify.setCreateBy(user);
			oaNotify.setCreateDate(new Date());
			oaNotify.setUpdateBy(user);
			oaNotify.setUpdateDate(new Date());
			oaNotify.setEffectiveDate(new Date());
			oaNotify.setSendType("2");
			oaNotify.setStatus("1");

			List<OaNotifyRecord>  recList=new ArrayList<OaNotifyRecord>();
			oaNotifyRecord.setId(IdGen.uuid());
			oaNotifyRecord.setOaNotify(oaNotify);
			oaNotifyRecord.setReadFlag("0");
			oaNotifyRecord.setOperateFlag("0");
			recList.add(oaNotifyRecord);

			oaNotify.setOaNotifyRecordList(recList);
			oaNotifyService.save(oaNotify);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0;

		}
		return 1;
	}
	@Transactional(readOnly = false)
	public void hiddenDelete(TeamUserRelation teamUserRelation) {
		teamUserRelationDao.hiddenDelete(teamUserRelation);
	}

	@Transactional(readOnly = false)
	public void updateWeight(TeamUserRelation teamUserRelation)	{
		teamUserRelationDao.updateWeight(teamUserRelation);
	}

	public int getWeightTotalByTeamId(String teamId){
		return teamUserRelationDao.getWeightTotalByTeamId(teamId);
	}

	public String getTeamStudentName(String teamId) {
		return teamUserRelationDao.getTeamStudentName(teamId);
	}
	public String getTeamTeacherName(String teamId) {
		return teamUserRelationDao.getTeamTeacherName(teamId);
	}
}