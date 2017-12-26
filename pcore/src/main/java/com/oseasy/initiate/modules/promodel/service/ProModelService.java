package com.oseasy.initiate.modules.promodel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowProjectType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.promodel.entity.ProCloseSubmit;
import com.oseasy.initiate.modules.promodel.entity.ProMidSubmit;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CommonService;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.FloatUtils;
import com.oseasy.initiate.common.utils.IdUtils;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.dao.ActDao;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.service.ActYwService;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwTool;
import com.oseasy.initiate.modules.attachment.enums.FileStepEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.excellent.entity.ExcellentShow;
import com.oseasy.initiate.modules.excellent.service.ExcellentShowService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.promodel.dao.ProModelDao;
import com.oseasy.initiate.modules.promodel.entity.ActYwAuditInfo;
import com.oseasy.initiate.modules.promodel.entity.ProModel;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.sco.dao.ScoAffirmCriterionDao;
import com.oseasy.initiate.modules.sco.dao.ScoAffirmDao;
import com.oseasy.initiate.modules.sco.dao.ScoAllotRatioDao;
import com.oseasy.initiate.modules.sco.dao.ScoScoreDao;
import com.oseasy.initiate.modules.sco.entity.ScoAffirm;
import com.oseasy.initiate.modules.sco.entity.ScoScore;
import com.oseasy.initiate.modules.sco.vo.ScoAffrimCriterionVo;
import com.oseasy.initiate.modules.sco.vo.ScoRatioVo;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.service.TeamUserHistoryService;

import net.sf.json.JSONObject;

/**
 * proModelService.
 * @author zy
 * @version 2017-07-13
 */
@Service
@Transactional(readOnly = true)
public class ProModelService extends CrudService<ProModelDao, ProModel> {
	@Autowired
	ProModelDao proModelDao;
	@Autowired
	ActYwAuditInfoService actYwAuditInfoService;
	@Autowired
	ActTaskService actTaskService;
	@Autowired
	ActYwService actYwService;
	@Autowired
	UserService userService;
	@Autowired
	SystemService systemService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	IdentityService identityService;
	@Autowired
	TaskService taskService;
	@Autowired
	ActDao actDao;
	@Autowired
	private SysAttachmentService sysAttachmentService;
	@Autowired
	private ExcellentShowService excellentShowService;
	@Autowired
	TeamUserHistoryService teamUserHistoryService;

	@Autowired
	ScoAffirmDao scoAffirmDao;
	@Autowired
	ScoScoreDao scoScoreDao;
	@Autowired
	ScoAllotRatioDao scoAllotRatioDao;
	@Autowired
	ScoAffirmCriterionDao scoAffirmCriterionDao;
	@Autowired
	ProjectDeclareService projectDeclareService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private ProMidSubmitService proMidSubmitService;
	@Autowired
	private ProCloseSubmitService proCloseSubmitService;
	@Autowired
	private OaNotifyService oaNotifyService;

	public ProModel get(String id) {
		return super.get(id);
	}

	public List<ProModel> findList(ProModel proModel) {
		return super.findList(proModel);
	}

	public Page<ProModel> findPage(Page<ProModel> page, ProModel proModel) {
		return super.findPage(page, proModel);
	}

	@Transactional(readOnly = false)
	public void save(ProModel proModel) {
		super.save(proModel);
	}

	@Transactional(readOnly = false)
	public void delete(ProModel proModel) {
		super.delete(proModel);
	}
	@Transactional(readOnly = false)
	public String startFromProcess(ProModel proModel){
		return "";
	}


	@Transactional(readOnly = false)
	public JSONObject submit(ProModel proModel,JSONObject js) {
		User user = UserUtils.getUser();
		ActYw actYw=actYwService.get(proModel.getActYwId());
		List<String> roles=new ArrayList<String>();
		//proModel.setCompetitionNumber(IdUtils.getGContestNumberByDb());
		//super.save(gContest);
		//启动大赛工作流
		if(actYw!=null){
			if(actYw.getProProject().getProType().contains(FlowProjectType.PMT_XM.getKey())){
				js=commonService.checkProjectApplyOnSubmit(
					proModel.getId(),proModel.getActYwId(), proModel.getProCategory(),proModel.getTeamId());
			}else if(actYw.getProProject().getProType().contains(FlowProjectType.PMT_DASAI.getKey())){
				js=commonService.checkGcontestApplyOnSubmit(
					proModel.getId(),proModel.getActYwId(),proModel.getTeamId());
			}
			if("0".equals(js.get("ret"))){
				return js;
			}

			ProProject proProject=actYw.getProProject();
			String nodeRoleId=actTaskService.getProcessStartRoleName(actYw.getPkey(actYw.getGroup(),actYw.getProProject()));  //从工作流中查询 下一步的角色集合
			String roleId=nodeRoleId.substring(ActYwTool.FLOW_ROLE_ID_PREFIX.length());

			Role role= systemService.getNamebyId(roleId);
			if(role!=null){
				//启动节点
				String roleName=role.getName();
				if(roleName.contains(SysIds.ISCOLLEGE.getRemark())||roleName.contains(SysIds.ISMS.getRemark())){
					roles=userService.getRolesByName(role.getEnname(),proModel.getDeclareId());
				}else{
					roles=userService.getRolesByName(role.getEnname());
				}
				/*//后台学生角色id
				if(roleId.equals(SysIds.SYS_ROLE_USER.getId())){
					roles.clear();
					roles.add(userService.findUserById(proModel.getDeclareId()).getName());
				}*/
				if (roles.size()>0) {
					proModel.setCompetitionNumber(IdUtils.getGContestNumberByDb());
					super.save(proModel);
					Map<String,Object> vars=new HashMap<String,Object>();
					vars=proModel.getVars();
					vars.put(nodeRoleId+"s",roles);
					String key=actYw.getPkey(actYw.getGroup(),actYw.getProProject());
					String userId = UserUtils.getUser().getLoginName();
					identityService.setAuthenticatedUserId(userId);
					ProcessInstance procIns=runtimeService.startProcessInstanceByKey(key, "pro_model:"+proModel.getId(),vars);
					//流程id返写业务表
					if(procIns!=null){
						Act act = new Act();
						act.setBusinessTable("pro_model");// 业务表名
						act.setBusinessId(proModel.getId());	// 业务表ID
						act.setProcInsId(procIns.getId());
						actDao.updateProcInsIdByBusinessId(act);
						proModel.setProcInsId(act.getProcInsId());
						proModel.setSubTime(new Date());
						proModel.setProType(proProject.getProType());
						proModel.setType(proProject.getType());
						super.save(proModel);
						/*提交时处理TeamUserHistory信息*/
						commonService.disposeTeamUserHistoryOnSubmit(proModel.getTeamUserHistoryList(), proModel.getActYwId(),proModel.getTeamId(),proModel.getId());
						sysAttachmentService.saveByVo(proModel.getAttachMentEntity(),proModel.getId(), FileTypeEnum.S2, FileStepEnum.S300);
						//保存优秀展示
						String ecxType=ExcellentShow.Type_Project;
						if("7,".equals(proModel.getProType())){
							ecxType=ExcellentShow.Type_Gcontest;
						}
						excellentShowService.saveExcellentShow(proModel.getIntroduction(), proModel.getTeamId(),ecxType,proModel.getId(),proModel.getActYwId());
						js.put("ret", 1);
						js.put("msg","恭喜您，申报成功!");
						return  js;
					}else{
						js.put("ret", 0);
						js.put("msg","流程配置故障（审核流程未启动），请联系管理员!");
						return  js;
					}
				}
			}
			js.put("ret", 0);
			js.put("msg","流程配置故障（流程角色未配置），请联系管理员!");
			return  js;
		}
		js.put("ret", 0);
		js.put("msg","流程配置故障（审核流程不存在），请联系管理员!");
		return  js;
	}


	@Transactional(readOnly = false)
	public String submitMid(ProModel proModel) {
		User user = UserUtils.getUser();
		ActYw actYw=actYwService.get(proModel.getActYwId());
		if(actYw!=null){
			String key= ActYw.getPkey(actYw.getGroup(),actYw.getProProject());
			//String nextRoleId=actTaskService.getProcessNextRoleName(proModel.getAct().getTaskDefKey(),key);
			String  nextNodeRoleId=actTaskService.getProcessNextRoleName(proModel.getAct().getTaskDefKey(),key);  //从工作流中查询 下一步的角色集合
			String roleId=nextNodeRoleId.substring(ActYwTool.FLOW_ROLE_ID_PREFIX.length());
			Role role= systemService.getNamebyId(roleId);
			if(role!=null){
				//启动节点
				String roleName=role.getName();
				List<String> roles=new ArrayList<String>();

				if(roleName.contains(SysIds.ISCOLLEGE.getRemark())||roleName.contains(SysIds.ISMS.getRemark())){
					roles=userService.getRolesByName(role.getEnname(),proModel.getDeclareId());
				}else{
					roles=userService.getRolesByName(role.getEnname());
				}
				if (roles.size()>0) {
					proModel.setCompetitionNumber(IdUtils.getGContestNumberByDb());
					super.save(proModel);
					Map<String,Object> vars=new HashMap<String,Object>();
					vars=proModel.getVars();
					vars.put(nextNodeRoleId+"s",roles);
					String userId = UserUtils.getUser().getLoginName();
					super.save(proModel);
					sysAttachmentService.saveByVo(proModel.getAttachMentEntity(),proModel.getId(), FileTypeEnum.S1, FileStepEnum.S102);
					if(proModel.getAttachMentEntity()!=null&&proModel.getAttachMentEntity().getGnodeId()!=null){
						String gnodeId =proModel.getAttachMentEntity().getGnodeId();
						ProMidSubmit proMidSubmit=new ProMidSubmit();
						proMidSubmit.setGnodeId(gnodeId);
						proMidSubmit.setPromodelId(proModel.getId());
						proMidSubmit.setState("0");
						proMidSubmitService.save(proMidSubmit);
					}

					if("0".equals(proModel.getGrade())){

					}else {
						taskService.complete(proModel.getAct().getTaskId(), vars);
					}
					return  "恭喜您，提交中期报告成功!";
				}
				return "流程配置故障（流程角色未配置），请联系管理员";
			}
			return "流程配置故障（流程角色未配置），请联系管理员";
		}
		return "流程配置故障（审核流程不存在），请联系管理员";
	}

	@Transactional(readOnly = false)
	public String submitClose(ProModel proModel) {
		User user = UserUtils.getUser();
		ActYw actYw=actYwService.get(proModel.getActYwId());
		if(actYw!=null){
			String key=actYw.getPkey(actYw.getGroup(),actYw.getProProject());
			String nextRoleId=actTaskService.getProcessNextRoleName(proModel.getAct().getTaskDefKey(),key);
			Role role= systemService.getNamebyId(nextRoleId.substring(ActYwTool.FLOW_ROLE_ID_PREFIX.length()));
			if(role!=null){
				//启动节点
				String roleName=role.getName();
				List<String> roles=new ArrayList<String>();

				if(roleName.contains(SysIds.ISCOLLEGE.getRemark())||roleName.contains(SysIds.ISMS.getRemark())){
					roles=userService.getRolesByName(role.getEnname(),proModel.getDeclareId());
				}else{
					roles=userService.getRolesByName(role.getEnname());
				}
				if (roles.size()>0) {
					proModel.setCompetitionNumber(IdUtils.getGContestNumberByDb());
					super.save(proModel);
					Map<String,Object> vars=new HashMap<String,Object>();
					vars=proModel.getVars();
					vars.put(nextRoleId+"s",roles);
					super.save(proModel);
					sysAttachmentService.saveByVo(proModel.getAttachMentEntity(),proModel.getId(), FileTypeEnum.S1, FileStepEnum.S102);

					if(proModel.getAttachMentEntity()!=null&&proModel.getAttachMentEntity().getGnodeId()!=null){
						String gnodeId =proModel.getAttachMentEntity().getGnodeId();
						ProCloseSubmit proCloseSubmit=new ProCloseSubmit();
						proCloseSubmit.setGnodeId(gnodeId);
						proCloseSubmit.setPromodelId(proModel.getId());
						proCloseSubmit.setState("0");
						proCloseSubmitService.save(proCloseSubmit);
					}
					if("0".equals(proModel.getGrade())){

					}else {
						taskService.complete(proModel.getAct().getTaskId(), vars);
					}
					//taskService.complete(proModel.getAct().getTaskId(), vars);
					return  "恭喜您，提交结项报告成功!";
				}
				return "流程配置故障（流程角色未配置），请联系管理员";
			}
			return "流程配置故障（流程角色未配置），请联系管理员";
		}
		return "流程配置故障（审核流程不存在），请联系管理员";
	}

	public Boolean getMidOrCloseView(String proModelId,String gnodeId){
		ProMidSubmit proMidSubmit=proMidSubmitService.getByGnodeId(proModelId,gnodeId);
		if(proMidSubmit!=null){
			return true;
		}
		ProCloseSubmit proCloseSubmit=proCloseSubmitService.getByGnodeId(proModelId,gnodeId);
		if(proCloseSubmit!=null){
			return  true;
		}
		return false;
	}

	@Transactional(readOnly = false)
	public void audit(ProModel proModel) {
		Map<String,Object> vars=new HashMap<String,Object>();
		ActYw actYw=actYwService.get(proModel.getActYwId());

		if(actYw!=null){
			String key=actYw.getPkey(actYw.getGroup(),actYw.getProProject());
			String nextGnodeRoleId=actTaskService.getProcessNextRoleName(proModel.getAct().getTaskDefKey(),key);

			if(StringUtil.isNotEmpty(nextGnodeRoleId)){
				String nextRoleId=nextGnodeRoleId.substring(ActYwTool.FLOW_ROLE_ID_PREFIX.length());
				Role role= systemService.getNamebyId(nextRoleId);
				//启动节点
				String roleName=role.getName();
				List<String> roles=new ArrayList<String>();

				if(roleName.contains(SysIds.ISCOLLEGE.getRemark())||roleName.contains(SysIds.ISMS.getRemark())){
					roles=userService.getRolesByName(role.getEnname(),proModel.getDeclareId());
				}else{
					roles=userService.getRolesByName(role.getEnname());
				}
				//后台学生角色id
				if(nextRoleId.equals(SysIds.SYS_ROLE_USER.getId())){
					roles.clear();
					roles.add(userService.findUserById(proModel.getDeclareId()).getName());
				}
				vars=proModel.getVars();
				//List<String> roles=userService.getCollegeExperts(proModel.getDeclareId());
				vars.put(nextGnodeRoleId+"s",roles);
			}else{
				//流程没有角色为没有后续流程 将流程表示为已经结束
				proModel.setState("1");
				//更改完成后团队历史表中的状态
				teamUserHistoryService.updateFinishAsClose(proModel.getId());
			}
			//判断审核结果
			if(proModel.getGrade()!=null||proModel.getgScore()!=null){
				ActYwAuditInfo actYwAuditInfo =new ActYwAuditInfo();
				ActYwGnode actYwGnode=actTaskService.getNodeByProInsId(proModel.getProcInsId());
				actYwAuditInfo.setPromodelId(proModel.getId());
				actYwAuditInfo.setGnodeId(actYwGnode.getId());
				if(proModel.getGrade()!=null){
					actYwAuditInfo.setGrade(proModel.getGrade());
				}else {
					actYwAuditInfo.setScore(proModel.getgScore());
				}
				actYwAuditInfo.setAuditName(actYwGnode.getName());
				actYwAuditInfo.setSuggest(proModel.getSource());
				actYwAuditInfoService.save(actYwAuditInfo);


			}

			String typeName="";
			if(actYw.getProProject()!=null){
				typeName=actYw.getProProject().getProjectName();
			}
			ActYwGnode actYwGnode=actTaskService.getNodeByProInsId(proModel.getProcInsId());
			//判断审核结果 结果失败 标记为失败
			if(proModel.getGrade()!=null && proModel.getGrade().equals("0")){
				proModel.setState("1");
				super.save(proModel);
				//更改完成后团队历史表中的状态
				teamUserHistoryService.updateFinishAsClose(proModel.getId());

				User apply_User=UserUtils.getUser();
				User rec_User=new User();
				rec_User.setId(proModel.getDeclareId());
				oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"学校管理员审核",
					typeName+" "+proModel.getpName()+"项目，"+actYwGnode.getName()+"审核不合格", OaNotify.Type_Enum.TYPE14.getValue(),proModel.getId());
			}else {
				if(proModel.getGrade()!=null && proModel.getGrade().equals("1")){
					User apply_User=UserUtils.getUser();
					User rec_User=new User();
					rec_User.setId(proModel.getDeclareId());
					oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"学校管理员审核",
					typeName+" "+proModel.getpName()+"项目，"+actYwGnode.getName()+"审核合格", OaNotify.Type_Enum.TYPE14.getValue(),proModel.getId());
				}
				if (proModel.getAct().getTaskId() != null) {
					taskService.complete(proModel.getAct().getTaskId(), vars);
					super.save(proModel);
				}
			}
		}
	}

	@Transactional(readOnly = false)
	public void saveModelScore(String proId,String grade){
		ProModel proModel = dao.getProScoreConfigure(proId);

		//插入学分前，先根据项目id删除对应的学分信息（sco_affirm表和sco_score表）
		scoAffirmDao.deleteByProId(proModel.getId());
		scoScoreDao.deleteProject(proModel.getId());

		//保存学分到对应的表（先保存到sco_affirm表、再保存到soc_score表）
		//根据 type（学分类型)、item（学分项）、category（课程、项目、大赛、技能大类）、subdivision（课程、项目、大赛小类）、number(人数)查询后台配比
		ScoAffirm scoAffirm = new ScoAffirm();
		scoAffirm.setProId(proModel.getId());

		ScoRatioVo scoRatioVo = new ScoRatioVo();
		ScoAffrimCriterionVo scoAffrimCriterionVo = new ScoAffrimCriterionVo();

		scoRatioVo.setType("0000000125"); //设置查询的学分类型（素质学分）
		scoAffrimCriterionVo.setType("0000000125");

		scoRatioVo.setItem(proModel.getType()); //双创大赛
		scoAffrimCriterionVo.setItem(proModel.getType()); //双创大赛
		scoRatioVo.setCategory("1"); //互联网+大赛
		scoAffrimCriterionVo.setCategory("1");

		//scoRatioVo.setNumber(proModel.getSnumber());
		ScoRatioVo ratioResult = scoAllotRatioDao.findRatio(scoRatioVo);
		scoAffrimCriterionVo.setResult(grade);
		boolean hasConfig;  //判断后台是否配置了规则
		if(ratioResult!=null){
			hasConfig=true;
		}else{
			hasConfig=false;
		}
		//查找 学分认定标准 scoAffirmCriterion
		ScoAffrimCriterionVo criterionResult = scoAffirmCriterionDao.findCriter(scoAffrimCriterionVo);
		if(criterionResult!=null){  //有学分认定标准，则插入值
			//插入scoAffirm表
			scoAffirm.setScoreStandard(criterionResult.getScore());
			scoAffirm.setScoreVal(criterionResult.getScore());
			scoAffirm.setAffirmDate(new Date());
			scoAffirm.preInsert();
			scoAffirmDao.insert(scoAffirm);
			//查找组成员的信息
			List<Map<String,String>>  studentList=  projectDeclareService.findTeamStudentFromTUH(proModel.getTeamId(),proModel.getId());
			if(!hasConfig){ //如果后台没有配比规则、则所有成员一样的分数
				for(Map<String,String> teamUser:studentList ){
					ScoScore scoScore = new ScoScore();
					User user =new User();
					user.setId(teamUser.get("userId"));
					scoScore.setUser(user);
					float score = criterionResult.getScore();
					if(score>0&score<0.5){
						score = 0.5f;
					}
					scoScore.setCreditScore((double)score);
					scoScore.setCreditId(proId);
					scoScore.preInsert();
					scoScoreDao.insert(scoScore);
				}
			}else{ //有配比，则查询team_user_relation中的配比
				//查找学分分配权重之和
				//int weigthTotal = teamUserRelationDao.getWeightTotalByTeamId(gContest.getTeamId());
				int weigthTotal = teamUserHistoryService.getWeightTotalByTeamId(proModel.getTeamId(),proModel.getId());
				for(Map<String,String> teamUser:studentList ){
					ScoScore scoScore = new ScoScore();
					User user =new User();
					user.setId(teamUser.get("userId"));
					scoScore.setUser(user);
					String number=String.valueOf(teamUser.get("weightVal"));
					int weightVal = Integer.parseInt(number);
					float score = FloatUtils.getOnePoint( ((float)weightVal/(float)weigthTotal)*criterionResult.getScore() ) ;
					if(score>0&score<0.5){
						score = 0.5f;
					}
					scoScore.setCreditScore((double)score);
					scoScore.setCreditId(proId);
					scoScore.preInsert();
					scoScoreDao.insert(scoScore);

				}
			}
		}
	}




	public int findProModelByTeamId(String id) {
		return proModelDao.findProModelByTeamId(id);
	}

	public ProModel getByProModel(ProModel proModel) {
		return proModelDao.getByProModel(proModel);
	}

	public Page<ProModel> getPromodelList(ProModel proModel) {
		return proModelDao.getPromodelList(proModel);
	}

	public Page<ProModel> findInPage(Page<ProModel> page, ProModel proModel) {
		if(page.getPageSize()<0){
			page.setPageSize(10);
		}
		page =super.findPage(page, proModel);
		int count=dao.getProModelAuditListCount(proModel);
		page.setCount(count);

		return page;
	}
}