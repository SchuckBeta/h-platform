package com.oseasy.initiate.modules.team.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.IdUtils;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.utils.sms.SMSUtilAlidayu;
import com.oseasy.initiate.common.utils.sms.impl.SendParam;
import com.oseasy.initiate.modules.oa.dao.OaNotifyDao;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.sys.dao.BackTeacherExpansionDao;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.BackTeacherExpansion;
import com.oseasy.initiate.modules.sys.entity.GContestUndergo;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.OfficeService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.sysconfig.service.SysConfigService;
import com.oseasy.initiate.modules.sysconfig.utils.SysConfigUtil;
import com.oseasy.initiate.modules.sysconfig.vo.SysConfigVo;
import com.oseasy.initiate.modules.sysconfig.vo.TeamConf;
import com.oseasy.initiate.modules.team.dao.TeamDao;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamDetails;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;

import net.sf.json.JSONObject;

/**
 * 团队管理Service
 * @author 刘波
 * @version 2017-03-30
 */
@Service
@Transactional(readOnly = true)
public class TeamService extends CrudService<TeamDao, Team> {
	@Autowired
	private OfficeService officeService;
	@Autowired
	private OaNotifyService oaNotifyService;
	@Autowired
	private TeamUserRelationService teamUserRelationService;
	@Autowired
	private UserService userService;
	@Autowired
	private OaNotifyDao oaNotifyDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private BackTeacherExpansionDao backTeacherExpansionDao;
	@Autowired
	private SysConfigService sysConfigService;
	@Transactional(readOnly = false)
	public JSONObject refuseInviationByNotify(String oaNotifyId) {
		JSONObject js=new JSONObject();
		User user = UserUtils.getUser();
		js.put("ret", "0");
		OaNotify oaNotify = oaNotifyService.get(oaNotifyId);
		oaNotifyService.updateReadOperateFlag(oaNotify);
		String type = oaNotify.getType();
		User sentUser = oaNotify.getCreateBy();
		TeamUserRelation teamUserRelation = new TeamUserRelation();
		Team team = get(oaNotify.getsId());
		teamUserRelation.setTeamId(oaNotify.getsId());
		if ("5".equals(type)) {
			//当审批申请的时候查出当前用户所在的团队
			teamUserRelation.setUser(sentUser);
		}else{
			//当别人邀请时候，查出发出邀请的人所在的团队
			teamUserRelation.setUser(user);
		}
		js=refuseJoinTeam(teamUserRelation);
		if ("1".equals(js.getString("ret"))) {
			teamUserRelationService.inseRefuseOaNo(team, type, user, sentUser);
			oaNotify.setType("11");
			oaNotifyDao.update(oaNotify);
		}
		hiddenDeleteWithNotify(team.getId(), teamUserRelation.getUser().getId());//拒绝后，被拒绝加入人不用在列表看到团队
		return js;
	}
	@Transactional(readOnly = false)
	public JSONObject acceptInviationByNotify(String oaNotifyId) {
		JSONObject js=new JSONObject();
		User acceptUser = UserUtils.getUser();
		js.put("ret", "0");
		if (StringUtil.isEmpty(UserUtils.getUser().getId())) {
			js.put("msg", "会话已失效，请重新登录");
			return js;
		}
		if (UserUtils.checkInfoPerfect(acceptUser)) {
			js.put("msg", "请先完善个人信息,然后在消息列表中再次点击接受邀请来加入团队");
			return js;
		}
		OaNotify oaNotify = oaNotifyService.get(oaNotifyId);
		if (oaNotify==null) {
			js.put("msg", "无效的消息");
			return js;
		}
		String type = oaNotify.getType();
		oaNotifyService.updateReadOperateFlag(oaNotify);
		User sentuser = oaNotify.getCreateBy();
		TeamUserRelation teamUserRelation = new TeamUserRelation();
		teamUserRelation.setTeamId(oaNotify.getsId());
		if ("5".equals(type)) {
			teamUserRelation.setUser(sentuser);
		}else {
			teamUserRelation.setUser(acceptUser);
		}
		js= acceptJoinTeam(teamUserRelation);
		if ("1".equals(js.getString("ret"))) {
			teamUserRelationService.inseAgreeOaNo(get(teamUserRelation.getTeamId()), type, acceptUser, sentuser);
			//在我的消息里点击接受时该条通知记录改变通知状态为同意加入
			oaNotify.setType("10");
			oaNotifyDao.update(oaNotify);
		}
		return js;
	}
	public Team get(String id) {
		return super.get(id);
	}
	public Integer checkTeamIsInProject(String tid){
		return dao.checkTeamIsInProject(tid);
	}
	public Integer checkTeamIsInCyjd(String tid){
		return dao.checkTeamIsInCyjd(tid);
	}
	@Transactional(readOnly = false)
	public JSONObject autoCheck() {
		JSONObject js=new JSONObject();
		String uid=UserUtils.getUser().getId();
		if(StringUtil.isEmpty(uid)){
			js.put("ret", "0");
			js.put("msg", "会话已失效，请重新登录");
			return js;
		}
		SysConfigVo scv=SysConfigUtil.getSysConfigVo();
		if(scv==null){
			scv=new SysConfigVo();
		}
		TeamConf tc=scv.getTeamConf();
		if(tc==null){
			tc=new TeamConf();
		}
		tc.setTeamCheckOnOff("0");
		sysConfigService.saveSysConfigVo(scv);
		dao.auditAllBiuldIng(uid);
		dao.auditAllBiuldOver(uid);
		js.put("ret", "1");
		js.put("msg", "设置成功");
		return js;
	}
	@Transactional(readOnly = false)
	public JSONObject checkTeam(String teamId,String res) {
		String uid=UserUtils.getUser().getId();
		JSONObject js=new JSONObject();
		if(StringUtil.isEmpty(uid)){
			js.put("ret", "0");
			js.put("msg", "会话已失效，请重新登录");
			return js;
		}
		if(StringUtil.isEmpty(teamId)||StringUtil.isEmpty(res)){
			js.put("ret", "0");
			js.put("msg", "无效的数据");
			return js;
		}
		if("1".equals(res)){
			res="0";
		}else{
			res="4";
		}
		dao.auditOne(teamId, res, uid);
		if("0".equals(res)){
			teamUserRelationService.repTeamstate(get(teamId));
		}
		js.put("ret", "1");
		js.put("msg", "操作成功");
		return js;
	}
	@Transactional(readOnly = false)
	public JSONObject unAutoCheck() {
		String uid=UserUtils.getUser().getId();
		JSONObject js=new JSONObject();
		if(StringUtil.isEmpty(uid)){
			js.put("ret", "0");
			js.put("msg", "会话已失效，请重新登录");
			return js;
		}
		SysConfigVo scv=SysConfigUtil.getSysConfigVo();
		if(scv==null){
			scv=new SysConfigVo();
		}
		TeamConf tc=scv.getTeamConf();
		if(tc==null){
			tc=new TeamConf();
		}
		tc.setTeamCheckOnOff("1");
		sysConfigService.saveSysConfigVo(scv);
		js.put("ret", "1");
		js.put("msg", "设置成功");
		return js;
	}
	@Transactional(readOnly = false)
	public String batchDelete(Team team,String idsre) {
		String[] ids = idsre.split(",");
		String endString = "";
		String delname = "";
		String delfailname = "";
		if (ids.length > 0) {
			for (String string : ids) {
				Team delteam = get(string);
				if (delteam != null) {
					if (delteam.getState().equals("2")) {
						delname = delname + delteam.getName() + ",";
						delete(delteam);
					} else {
						delfailname = delfailname + delteam.getName() + ",";
					}
				}
			}
		}
		if (delname.isEmpty()) {

		} else {
			endString = endString + delname.substring(0, delname.length() - 1) + "删除团队成功。";
		}
		if (delfailname.isEmpty()) {

		} else {
			endString = endString + delfailname.substring(0, delfailname.length() - 1) + "团队未解散,不可删除。";
		}
		return endString;

	}
	public List<Team> findList(Team team) {
		return super.findList(team);
	}

	public Page<Team> findPage(Page<Team> page, Team team) {
		page=super.findPage(page, team);
		if (page != null&&page.getList()!=null&&page.getList().size()>0) {
			List<Team> teamList = page.getList();
			/*查询当前登录人(学生)在每一个团队中是否已经加入，控制申请加入按钮*/
			List<Map<String, Object>> rl=dao.checkIsJoinInTeams(teamList, UserUtils.getUser().getId());//加入了团队的
			Map<String,Object> cm=new HashMap<String,Object>();
			if(rl!=null){
				for(Map<String, Object> m:rl){
					Object tid=m.get("team_id");
					Object cc=m.get("cc");
					if(tid!=null&&cc!=null){
						cm.put(tid.toString(), cc);
					}
				}
			}
			List<Map<String, Object>> rll=dao.checkIsJoinInTUR(teamList, UserUtils.getUser().getId());//加入团队或者发申请和被邀请
			Map<String,Object> cml=new HashMap<String,Object>();
			if(rll!=null){
				for(Map<String, Object> m:rll){
					Object tid=m.get("team_id");
					Object cc=m.get("cc");
					if(tid!=null&&cc!=null){
						cml.put(tid.toString(), cc);
					}
				}
			}
			for (Team tt : teamList) {
				if(cm.get(tt.getId())!=null){
					tt.setCheckJoin(true);
				}else{
					tt.setCheckJoin(false);
				}
				if(cml.get(tt.getId())!=null){
					tt.setCheckJoinTUR(true);
				}else{
					tt.setCheckJoinTUR(false);
				}
				if (StringUtil.isNotBlank(tt.getSponsor())) {
					User usertmp = userService.findUserById(tt.getSponsor());
					if (usertmp != null) {
						tt.setSponsorId(tt.getSponsor());
						tt.setSponsor(usertmp.getName());
					}
				}

				if (StringUtil.isNotBlank(tt.getLocalCollege())) {
					Office officetmp = officeService.get(tt.getLocalCollege());
					if (officetmp != null) {
						tt.setLocalCollege(officetmp.getName());
					}
				}

				String teamId = tt.getId();// 获取teamId
				List<Team> teamUserName = findTeamUserName(teamId);
				int qyCount = 0;
				int xyCount = 0;
				int xsCount = 0;
				StringBuffer xsbuffer = new StringBuffer();
				StringBuffer qybuffer = new StringBuffer();
				StringBuffer xybuffer = new StringBuffer();
				if (teamUserName != null) {
					for (Team teamTm : teamUserName) {
						if (teamTm != null) {
							if ("1".equals(teamTm.getTeamUserType())) {// 代表类型是学生
								xsCount++;
								xsbuffer.append(teamTm.getuName() + "/");
							} else if ("2".equals(teamTm.getTeamUserType())) {// 否则类型就是导师
								if ("2".equals(teamTm.getTeacherType())) {// 判断是否是企业导师
									qyCount++;
									qybuffer.append(teamTm.getuName() + "/");
								} else if ("1".equals(teamTm.getTeacherType())) {// 否则就是校园导师
									xyCount++;
									xybuffer.append(teamTm.getuName() + "/");
								}
							}
						}
					}
					if (xsbuffer.length() > 0) {
						tt.setUserName(xsbuffer.substring(0, xsbuffer.lastIndexOf("/")));
					}
					tt.setUserCount(xsCount);
					if (qybuffer.length() > 0) {
						tt.setEntName(qybuffer.substring(0, qybuffer.lastIndexOf("/")));
					}
					tt.setEnterpriseNum(qyCount);
					if (xybuffer.length() > 0) {
						tt.setSchName(xybuffer.substring(0, xybuffer.lastIndexOf("/")));
					}
					tt.setSchoolNum(xyCount);
				}
			}
			page.setList(teamList);
		}
		return page;
	}
	@Transactional(readOnly = false)
	public String hiddenDelete(String teamId,User curUser) {
		String resStr = "1";
		if (curUser == null) {
			resStr = "请先登录！";
			return resStr;
		}
		try {
			
			Team team = dao.get(teamId);
			if (team != null && team.getSponsor().equals(curUser.getId())) {
				dao.delete(team);
			}else{
				/*对于还未在团队中的，不想看到的，删除处理*/
				hiddenDeleteWithNotify(teamId, curUser.getId());
				
				TeamUserRelation teamUserRelation = new TeamUserRelation();
				teamUserRelation.setUser(curUser);
				teamUserRelation.setTeamId(teamId);
				teamUserRelation.setState("4");
				teamUserRelationService.hiddenDelete(teamUserRelation);
			}
		} catch (Exception e) {
			resStr = "操作异常";
			logger.error(e.getMessage(), e);
		}
		return resStr;
	}
	@Transactional(readOnly = false)
	public void hiddenDeleteWithNotify(String teamid,String userid){
		if(StringUtil.isNotEmpty(teamid)&&StringUtil.isNotEmpty(userid)){
			dao.hiddenDeleteWithNotify(teamid, userid);
		}
	}
	@Transactional(readOnly = false)
	public void batchDis(Team team){
		String[] ids = team.getId().split(",");
		if (ids.length > 0) {
			for (String string : ids) {
				Team disteam = new Team(string);
				disTeam(disteam);
			}
		}
	}
	@Transactional(readOnly = false)
	public String applyJoin(String teamId){
		User curUser = UserUtils.getUser();// 获取当前用户的信息
		String resStr = "";
		if (curUser == null) {
			resStr = "请先登录！";
			return resStr;
		}
		if (UserUtils.checkInfoPerfect(curUser)) {
			return "申请失败，个人信息未完善，";
		}
		Team team= dao.get(teamId);
		if (team == null) {
			resStr = "未找到团队！";
			return resStr;
		}
		try {
			// 是否直接加入团队 1是有限制 0是无限制
			SysConfigVo scv=SysConfigUtil.getSysConfigVo();
			if(scv!=null && "0".equals(scv.getTeamConf().getJoinOnOff())) {
				// 0成功 1已加入其他团队 2从已申请变更加入3新增插入
				JSONObject res = teamUserRelationService.pullIn(curUser, teamId);
				if("1".equals(res.getString("ret"))){
					if (teamId != null) {
						teamUserRelationService.repTeamstate(team);
					}
					resStr = "申请成功";
				} else {
					resStr = "申请失败,"+res.getString("msg");
				}
			} else {
				User team_User = userDao.get(team.getSponsor());
				int res = teamUserRelationService.inseTeamUser(curUser, teamId, "1");
				if (res == 3) {
					teamUserRelationService.repTeamstate(team);
					final Team finalTeam = team;
					final User final_User = team_User;
					new Thread(new Runnable() {
						@Override
						public void run() {
							SendParam parm = new SendParam(curUser.getName(), finalTeam.getName());
							try {
								SMSUtilAlidayu.sendSmsTemplate(final_User.getMobile(),
										SMSUtilAlidayu.AILIDAYU_SMS_TemplateApply, parm);
							} catch (Exception e) {
								logger.error("申请加入团队：" + e.getMessage());
							}
						}
					}).start();

					sendOaNotify(curUser, team_User, team);
					resStr = "申请成功";
				} else if (res >= 2) {
					resStr = "已发出过申请或已经被邀请，请查看信息列表";
				} else if (res == 1) {
					resStr = "申请失败,您已加入该团队";
				} else {
					resStr = "申请失败";
				}
			}
		} catch (Exception e) {
			resStr = "操作异常";
			logger.error(e.getMessage(), e);
		}
		return resStr;
	}
	@Transactional(readOnly = false)
	public void deleteTeamUserInfo(TeamUserRelation tur){
		String userId=tur.getUser().getId();
		String teamId=tur.getTeamId();
		User curUser = UserUtils.getUser();// 获取当前用户的信息
		Team team = dao.get(teamId);
		User delUser = userService.findUserById(userId);
		teamUserRelationService.deleteTeamUserInfo(userId,teamId);
		/*对于还未在团队中的，不想看到的，删除处理*/
		hiddenDeleteWithNotify(teamId, delUser.getId());
		if (team != null) {
			TeamUserRelation teamUserRelation = new TeamUserRelation();
			teamUserRelation.setTeamId(teamId);
			teamUserRelationService.repTeamstate(team);
		}
		if("0".equals(tur.getState())||"4".equals(tur.getState())){
			teamUserRelationService.insertDeleteOaNo(team, curUser, delUser);
		}
	}
	@Transactional(readOnly = false)
	public JSONObject refuseInviation(String turId,String teamId){
		
		JSONObject js=new JSONObject();
		User cuser = UserUtils.getUser();
		js.put("ret", "0");
		TeamUserRelation teamUserRelation = teamUserRelationService.get(turId);
		if(teamUserRelation==null){
			js.put("msg", "未找到数据");
		}
		OaNotify oaNotify = oaNotifyDao.findOaNotifyByTeamID(teamUserRelation.getUser().getId(), teamId);
		if(oaNotify!=null)oaNotifyService.updateReadOperateFlag(oaNotify);
		Team team = get(teamId);
		js=refuseJoinTeam(teamUserRelation);
		if ("1".equals(js.getString("ret"))) {
			teamUserRelationService.inseRefuseOaNo(team, "5", cuser, teamUserRelation.getUser());
			if(oaNotify!=null){
				oaNotify.setType("11");
				oaNotifyDao.update(oaNotify);
			}
		}
		hiddenDeleteWithNotify(team.getId(), teamUserRelation.getUser().getId());//拒绝后，被拒绝加入人不用在列表看到团队
		return js;
	}
	@Transactional(readOnly = false)
	public JSONObject acceptInviation(String userId,String teamId){
		User user = userService.findUserById(userId);
		TeamUserRelation teamUserRelation = new TeamUserRelation();
		teamUserRelation.setTeamId(teamId);
		teamUserRelation.setUser(user);
		OaNotify oaNotify = oaNotifyDao.findOaNotifyByTeamID(teamUserRelation.getUser().getId(), teamId);
		if(oaNotify!=null)oaNotifyService.updateReadOperateFlag(oaNotify);
		JSONObject t = acceptJoinTeam(teamUserRelation);
		if ("1".equals(t.getString("ret"))) {
			Team team = dao.get(teamId);
			User acceptUser = UserUtils.getUser();
			teamUserRelationService.inseAgreeOaNo(team, "5", acceptUser, user);
			if(oaNotify!=null){
				oaNotify.setType("10");
				oaNotifyDao.update(oaNotify);
			}
		}
		return t;
	}
	@Transactional(readOnly = false)
	public void saveTeam(Team team,User curUser) {
		if(StringUtil.isEmpty(team.getNumber())){
			team.setNumber(IdUtils.getTeamNumberByDb());
		}
		if(StringUtil.isEmpty(team.getId())){//新增的团队要根据配置看是否需要审核
			SysConfigVo scv=SysConfigUtil.getSysConfigVo();
			if(scv!=null){
				TeamConf tc=scv.getTeamConf();
				if(tc!=null&&"1".equals(tc.getTeamCheckOnOff())){
					team.setState("3");//待审核
				}
			}
		}
		this.save(team);
		Date now = new Date();
		TeamUserRelation teamUserRelation=new TeamUserRelation();
		teamUserRelation.setUser(curUser);
		teamUserRelation.setTeamId(team.getId());// 添加teamid
		teamUserRelation.setId(IdGen.uuid());// 添加id
		teamUserRelation.setState("0");// 添加状态
		teamUserRelation.setCreateDate(now);
		teamUserRelation.setCreateBy(curUser);
		teamUserRelation.setUpdateDate(now);
		teamUserRelation.setUpdateBy(curUser);
		teamUserRelation.setDelFlag("0");
		teamUserRelation.setUserType(curUser.getUserType());
		this.saveTeamUserRelation(teamUserRelation);
		teamUserRelationService.repTeamstate(team);
	}
	@Transactional(readOnly = false)
	public void editTeam(Team team) {
		TeamUserRelation teamUserRelation = new TeamUserRelation();
		teamUserRelation.setTeamId(team.getId());
		if (team.getEnterpriseTeacherNum() == null) {
			team.setEnterpriseTeacherNum(0);
		}
		if(StringUtil.isNotEmpty(team.getId())){//修改的团队需要根据信息内容是否有修改来判断是否需要审核
			boolean b=false;//是否需要审核
			SysConfigVo scv=SysConfigUtil.getSysConfigVo();
			if(scv!=null){
				TeamConf tc=scv.getTeamConf();
				if(tc!=null&&"1".equals(tc.getTeamCheckOnOff())){
					if("4".equals(team.getState())){//未通过的重新编辑需要审核
						b=true;
					}else{
						Team t=dao.get(team.getId());
						if(!b&&!t.getName().equals(team.getName())){
							b=true;
						}
						if(!b&&!t.getMembership().equals(team.getMembership())){
							b=true;
						}
						if(!b&&!t.getProjectIntroduction().equals(team.getProjectIntroduction())){
							b=true;
						}
						if(!b&&!t.getSummary().equals(team.getSummary())){
							b=true;
						}
					}
				}
			}
			if(b){
				team.setState("3");//待审核
			}else{
				team.setState("0");//审核通过
			}
		}
		this.save(team);
		teamUserRelationService.repTeamstate(team);
	}
	@Transactional(readOnly = false)
	public void save(Team team) {
		try {

			super.save(team);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@Transactional(readOnly = false)
	public void delete(Team team) {
		super.delete(team);
	}

	public TeamDetails findTeamDetails(String id) {
		return dao.findTeamDetails(id);
	}

	public List<TeamDetails> findTeamInfo(String id,String usertype) {
		return dao.findTeamInfo(id, usertype);
	}

	@Transactional(readOnly = false)
	public void saveTeamUserRelation(TeamUserRelation teamUserRelation) {
		dao.saveTeamUserRelation(teamUserRelation);
	}

	public List<Team> findTeamUserName(String teamId) {
		return dao.findTeamUserName(teamId);
	}

	@Transactional(readOnly = false)
	public void updateTeamState(Team team) {
		dao.updateTeamState(team);
	}


	/**
	 * 发送通知team负责人  type=5
	 * @param apply_User 申请人User
	 * @param team_User 团队负责人User
	 * @param team 申请团队
	 * @param post 职务
	 * @return >0 成功
	 */
	@Transactional(readOnly = false)
	public int sendOaNotify(User apply_User,User team_User,Team team) {
		try {
			OaNotifyRecord oaNotifyRecord=new OaNotifyRecord();
			OaNotify oaNotify=new OaNotify();
			oaNotify.setTitle(team.getName()+"团队申请记录");
			oaNotify.setContent("收到"+":"+apply_User.getName()+"的申请记录");
			oaNotify.setType("5");
			oaNotify.setsId(team.getId());
			oaNotify.setCreateBy(apply_User);
			oaNotify.setCreateDate(new Date());
			oaNotify.setUpdateBy(apply_User);
			oaNotify.setUpdateDate(new Date());
			oaNotify.setEffectiveDate(new Date());
			oaNotify.setStatus("1");
			oaNotify.setSendType("2");

			List<OaNotifyRecord>  recList=new ArrayList<OaNotifyRecord>();
			oaNotifyRecord.setId(IdGen.uuid());
			oaNotifyRecord.setOaNotify(oaNotify);
			oaNotifyRecord.setUser(team_User);
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
	 * 审核信息
	 * @TeamUserRelation param 申请人id  申请团队id
	 * @return 
	 */
	@Transactional(readOnly = false)
	public JSONObject acceptJoinTeam(TeamUserRelation param) {
		JSONObject js=new JSONObject();
		js.put("ret", "0");
		if(param==null||StringUtil.isEmpty(param.getTeamId())
				||param.getUser()==null||StringUtil.isEmpty(param.getUser().getId())){
			js.put("msg", "无效的数据");
			return js;
		}
		if(teamUserRelationService.findUserHasJoinTeam(param)!=null){
			js.put("msg", "已加入该团队");
			return js;
		}
		TeamUserRelation teamUserRelation = teamUserRelationService.findUserWillJoinTeam(param);
		if (teamUserRelation==null) {
			js.put("msg", "操作无效,请求已被处理或删除");
			return js;
		}
		Team team = dao.findTeamJoinInNums(teamUserRelation.getTeamId());
		if (team==null) {
			js.put("msg", "操作无效,该团队已经不存在");
			return js;
		}
		if (team.getState().equals("2")) {
			js.put("msg", "该团队已经解散，加入失败");
			teamUserRelation.setState("5");
			teamUserRelationService.updateStateInTeam(teamUserRelation);
			return js;
		}
		if (team.getState().equals("3")) {
			js.put("msg", "该团队正在审核中，加入失败");
			teamUserRelation.setState("5");
			teamUserRelationService.updateStateInTeam(teamUserRelation);
			return js;
		}
		if (team.getState().equals("4")) {
			js.put("msg", "该团队审核未通过，加入失败");
			teamUserRelation.setState("5");
			teamUserRelationService.updateStateInTeam(teamUserRelation);
			return js;
		}
		
		if (team.getState().equals("1")) {
			js.put("msg", "该团队已经建设完毕，加入失败");
			teamUserRelation.setState("5");
			teamUserRelationService.updateStateInTeam(teamUserRelation);
			return js;
		}
		User tems=UserUtils.get(teamUserRelation.getUser().getId());
		if(tems==null||"1".equals(tems.getDelFlag())){
			js.put("msg", "该用户已被删除，加入失败");
			teamUserRelation.setState("5");
			teamUserRelationService.updateStateInTeam(teamUserRelation);
			return js;
		}
		if(!"1".equals(tems.getUserType())&&!"2".equals(tems.getUserType())){
			js.put("msg", "该用户已不是学生或导师，加入失败");
			teamUserRelation.setState("5");
			teamUserRelationService.updateStateInTeam(teamUserRelation);
			return js;
		}
		//查询团队里面已经存在的组员人数和申请人的类型 后期做人数限制使用
		if("1".equals(teamUserRelation.getUserType())&&team.getUserCount()>=team.getMemberNum()){
			js.put("msg", "该团队成员已满，加入失败");
			teamUserRelation.setState("5");
			teamUserRelationService.updateStateInTeam(teamUserRelation);
			return js;
		}
		if("2".equals(teamUserRelation.getUserType())){
			BackTeacherExpansion teab=backTeacherExpansionDao.getByUserId(teamUserRelation.getUser().getId());
			if(teab==null){
				js.put("msg", "未找到导师，加入失败");
				teamUserRelation.setState("5");
				teamUserRelationService.updateStateInTeam(teamUserRelation);
				return js;
			}
			if("2".equals(teab.getTeachertype())&&team.getEnterpriseNum()>=team.getEnterpriseTeacherNum()){//企业导师
				js.put("msg", "该团队企业导师已满，加入失败");
				teamUserRelation.setState("5");
				teamUserRelationService.updateStateInTeam(teamUserRelation);
				return js;
			}
			if(!"2".equals(teab.getTeachertype())&&team.getSchoolNum()>=team.getSchoolTeacherNum()){//校内导师
				js.put("msg", "该团队校内导师已满，加入失败");
				teamUserRelation.setState("5");
				teamUserRelationService.updateStateInTeam(teamUserRelation);
				return js;
			}
		}
		teamUserRelation.setState("0");//将状态改为0加入状态
		teamUserRelationService.updateStateInTeam(teamUserRelation);
		teamUserRelationService.repTeamstate(team);
		js.put("ret", "1");
		js.put("msg", "加入成功");
		return js;
	}
	@Transactional(readOnly = false)
	public JSONObject refuseJoinTeam(TeamUserRelation param) {
		JSONObject js=new JSONObject();
		js.put("ret", "0");
		if(param==null||StringUtil.isEmpty(param.getTeamId())
				||param.getUser()==null||StringUtil.isEmpty(param.getUser().getId())){
			js.put("msg", "无效的数据");
			return js;
		}
		Team team = get(param.getTeamId());
		if (team==null) {
			js.put("msg", "操作无效,该团队已不存在");
			return js;
		}
		if(teamUserRelationService.findUserHasJoinTeam(param)!=null){
			js.put("msg", "操作无效,已加入该团队");
			return js;
		}
		TeamUserRelation teamUserRelation = teamUserRelationService.findUserWillJoinTeam(param);
		if (teamUserRelation==null) {
			js.put("msg", "操作无效,请求已被处理或删除");
			return js;
		}
		
		teamUserRelation.setState("3");
		teamUserRelationService.updateStateInTeam(teamUserRelation);
		js.put("ret", "1");
		js.put("msg", "拒绝成功");
		return js;
	}


	/**
	 * 拒绝申请
	 * @param teamUserRelation  拒绝的申请记录
	 *
	 */
	@Transactional(readOnly = false)
	public void disTeam(TeamUserRelation teamUserRelation) {
		try {
			teamUserRelation.setState("3");
			teamUserRelationService.save(teamUserRelation);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}

	}

	/**
	 * 解散团队
	 * @param team
	 *
	 */
	@Transactional(readOnly = false)
	public void disTeam(Team team) {
		try {
			team.setState("2");
			this.save(team);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}

	}

	public List<TeamDetails> findTeamByTeamId(String id,String usertype) {
		return dao.findTeamByTeamId(id, usertype);
	}

	public Long countBuildByUserId(User curUser) {
		return dao.countBuildByUserId(curUser);
	}

	public List<Team> selectTeamByName(String name) {
		return dao.selectTeamByName(name);
	}

	public  List<Team>   findListByCreatorId(Team team) {
		return  dao.findListByCreatorId(team);
	}
	public  List<Team>   findListByCreatorIdAndState(Team team) {
		return  dao.findListByCreatorIdAndState(team);
	}
	//判断两个user是不是在同一个团队
	public boolean findTeamByUserId(String user1Id,String user2Id){
		int number = dao.findTeamByUserId(user1Id,user2Id);
		if(number==0){
			return false;
		}else {
			return true;
		}
	}
	public boolean checkTeamNum(Team team){
		boolean bl=false;
		TeamUserRelation teamUserRelation=new TeamUserRelation ();
		teamUserRelation.setTeamId(team.getId());
		List<TeamUserRelation> stus= teamUserRelationService.getStudents(teamUserRelation);
		if(team.getEnterpriseNum()!=null&&team.getEnterpriseNum()==stus.size()){
			bl=true;
		}
		return bl;
	}

	public List<Team> findInTeamList(Team team) {
			return dao.findInTeamList(team);
	}

	public List<ProjectExpVo> findProjectByTeamId(String teamId) {
		return dao.findProjectByTeamId(teamId);
	}

	public List<GContestUndergo> findGContestByTeamId(String teamId) {
		return dao.findGContestByTeamId(teamId);
	}

	public Team findTeamJoinInNums(String id) {
		return dao.findTeamJoinInNums(id);
	}
}