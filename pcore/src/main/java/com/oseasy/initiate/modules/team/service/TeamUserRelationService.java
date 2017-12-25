/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/oseasy/initiate">JeeSite</a> All rights reserved.
 */
package com.oseasy.initiate.modules.team.service;

import java.util.*;

import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.team.dao.TeamDao;
import com.oseasy.initiate.modules.team.dao.TeamUserRelationDao;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;

import static org.apache.shiro.web.filter.mgt.DefaultFilter.user;

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
	private ProjectDeclareService projectDeclareService;

	public TeamUserRelation get(String id) {
		return super.get(id);
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

	//改变teamUserRelation 的state状态 改变team 的state状态
	@Transactional(readOnly = false)
	public void updateState(TeamUserRelation teamUserRelation) {
		dao.updateState(teamUserRelation);

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

	 public List<TeamUserRelation> findUserInfo(TeamUserRelation teamUserRelation) {
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
	//不根据team表state=0去查
	 public TeamUserRelation findUserById1(TeamUserRelation teamUserRelation) {
		 return teamUserRelationDao.findUserById1(teamUserRelation);
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
			TeamUserRelation teamUserRelationTmp =  this.findUserById(teamUserRelation);
			if (teamUserRelationTmp!=null) {
				logger.info("该用户已经加入到团队，不能直接拉入");
				return 0;
			}

			List<TeamUserRelation> teamUserInfo=this.findUserInfo(teamUserRelation);//根据用户id查询用户是否存在
			if (teamUserInfo.size()>0) {//如果用户存在并且状态是加入状态
				for (TeamUserRelation teamUserRelation2 : teamUserInfo) {
					//判断是否已加入团队
					if ("0".equals(teamUserRelation2.getState())) {
						return 1;
					}
					//判断是否已经申请过该团队
					if (teamUserRelation2.getTeamId()!=null&&teamId.equals(teamUserRelation2.getTeamId())
							&&(teamUserRelation2.getState().equals("1")||teamUserRelation2.getState().equals("2"))) {
						teamUserRelation.setUpdateDate(new Date());
						teamUserRelation.setState(appType);
						teamUserRelation.setIsNewRecord(false);
						this.updateByUserId(teamUserRelation);//根据用户id修改
						return 2;
					}
				}
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
			e.printStackTrace();
			return 0;
		}
	}



	/**直接插入团队人员
	 *
	 * @param teamId 申请的teamId
	 * @param applyUser 插入人userID
 	 * @return >0成功   1已加入其他团队 2从已申请变更加入3新增插入
	 */
	@Transactional(readOnly = false)
	public int pullIn(User applyUser,String teamId) {
		try {
			TeamUserRelation teamUserRelation=new TeamUserRelation();
			User user=new User();
			user.setId(applyUser.getId());
			teamUserRelation.setUser(user);
			//teamUserRelation.setTeamId(teamId);

			TeamUserRelation   teamUserRelationTmp =  this.findUserById(teamUserRelation);
			if (teamUserRelationTmp!=null) {
				logger.info("该用户已经加入团队中，不能再次加入！");
				return 1;
			}

			List<TeamUserRelation> teamUserInfo=this.findUserInfo(teamUserRelation);//根据用户id查询用户是否存在
			if (teamUserInfo.size()>0) {//如果用户存在并且状态是加入状态
				for (TeamUserRelation teamUserRelation2 : teamUserInfo) {
					//判断是否已加入本团队
					if ("0".equals(teamUserRelation2.getState())) {
						return 1;
					}
					//判断是否已经申请过该团队
					if (teamUserRelation2.getTeamId()!=null&&teamId.equals(teamUserRelation2.getTeamId())) {
						teamUserRelation.setUpdateDate(new Date());
						teamUserRelation.setState("0");
						this.updateByUserId(teamUserRelation);//根据用户id修改
						//int  res= this.pullIncheck(teamUserRelation);
						//if (res>0) {
						//}

						return 2;
					}
				}
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
		//	Team team= teamDao.get(teamId);
//			int  res= this.pullIncheck(teamUserRelation);
//			if (res>0) {
//			}
			return 3;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 修改团队状态
	 * @param teamUserRelation
	 * @param team
	 * @return
	 */
	@Transactional(readOnly = false)
	public void repTeamstate(TeamUserRelation teamUserRelation,Team team) {
		//List<Team> teamList= teamDao.findNumByTeamId(teamUserRelation);
		int  stuCount=0;
		int  te1Count=0;
		int  te2Count=0;
		/*if (teamList!=null&&teamList.size()>0) {
			for (int i = 0; i < teamList.size(); i++) {
				if (i==0) {
					stuCount=teamList.get(i).getUserCount();
				}else if (i==1) {
					te1Count=teamList.get(i).getUserCount();
				}else{
					te2Count=teamList.get(i).getUserCount();
				}
			}
		}*/

		stuCount= teamDao.findStuNumByTeamId(teamUserRelation);
		te1Count= teamDao.findTe1NumByTeamId(teamUserRelation);
		te2Count= teamDao.findTe2NumByTeamId(teamUserRelation);

		if (stuCount>=team.getMemberNum() && te1Count>=team.getSchoolTeacherNum() && te2Count>=team.getEnterpriseTeacherNum()) {
				team.setState("1");//将团队状态改为建设完毕
				teamDao.updateTeamState(team);
		}else{
			if (team.getState().equals("1")) {

			}else{
				team.setState("0");//将团队状态改为建设中
				teamDao.updateTeamState(team);
			}

		}


/*		for (Team team1 : teamList) {
			if (team1.getUserCount()>=team.getMemberNum() && team1.getUserCount()>=team.getEnterpriseTeacherNum()
					   && team1.getUserCount()>=team.getSchoolTeacherNum()) {
						team.setState("1");//将团队状态改为建设完毕
						teamDao.updateTeamState(team);
			}else{
						team.setState("0");//将团队状态改为建设中
						teamDao.updateTeamState(team);
			}
		}*/
	}

	/**
	 * 检测是否可拉入.
	 * @param teamUserRelation 申请人id  申请团队id
	 * @return int >0成功
	 */
	@Transactional(readOnly = false)
	public int pullIncheck(TeamUserRelation teamUserRelation) {
		try {
			if (teamUserRelation.getTeamId()!=null && !teamUserRelation.getTeamId().equals("")
			   && teamUserRelation.getUser().getId()!=null && !teamUserRelation.getUser().getId().equals("")) {
				//查询团队里面已经存在的组员人数和申请人的类型
				List<Team> teamNum=teamDao.findNumByTeamId(teamUserRelation);
				if (teamNum.size()>0) {
					Team teamUserNum=teamDao.findRealityNum(teamUserRelation.getTeamId());//查询出原始人数
					if (teamUserNum != null) {
						for (Team teamTmp : teamNum) {
							if ("1".equals(teamTmp.getTeamUserType())) {//类型为学生
								if (teamTmp.getUserCount()>=teamUserNum.getMemberNum()) {
									logger.info("学生类型人数已满");
									return -1;
								}
							}
							if ("1".equals(teamTmp.getTeacherType())) {//判断是否是企业导师
								if (teamTmp.getUserCount()>=teamUserNum.getEnterpriseTeacherNum()) {
									logger.info("企业导师人数已满");
									return -1;
								}
							}else if ("2".equals(teamTmp.getTeacherType())) {//判断是否是校园导师
								if (teamTmp.getUserCount()>=teamUserNum.getSchoolTeacherNum()) {
									logger.info("校园导师人数已满");
									return -1;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			oaNotify.setTitle(team.getName()+"团队创建成功");
			User  userTmp = systemService.getUser(team.getSponsor());
			oaNotify.setContent("创建人:"+userTmp.getName()+"的团队的已发布。");
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
			e.printStackTrace();
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

		//officeList.add("03f2ded17518420694d402ff64fae0eb");

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
	public int deleteTeamUserInfo(Team team,User TeamSponsor,User DelTeamUser) {
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
			oaNotifyRecord.setUser(DelTeamUser);
			oaNotifyRecord.setReadFlag("0");
			oaNotifyRecord.setOperateFlag("0");
			recList.add(oaNotifyRecord);
			oaNotify.setOaNotifyRecordList(recList);
			oaNotifyService.save(oaNotify);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;

		}
		dao.deleteTeamUserInfo(DelTeamUser.getId(), team.getId());
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
			e.printStackTrace();
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
	public int inseRefuseOaNo(Team team,String type,User user,User sentuser,TeamUserRelation teamUserRelation) {
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

		    if (StringUtils.isNotEmpty(teamUserRelation.getTeamId())) {
					oaNotify.setContent(user.getName()+"已加入别的团队");
				}else {
					oaNotify.setContent(user.getName()+"拒绝加入"+team.getName()+"项目团队");
				}
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
			e.printStackTrace();
			return 0;

		}
		return 1;
	}
	@Transactional(readOnly = false)
	public void updateTeamUserRelation(TeamUserRelation teamUserRelation) {
		teamUserRelationDao.updateState(teamUserRelation);
	}

	public void updateStateByInfo(TeamUserRelation teamUserRelation) {
		teamUserRelationDao.updateStateByInfo(teamUserRelation);
	}
}