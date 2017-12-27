package com.hch.platform.pcore.modules.proprojectmd.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.config.SysIds;
import com.hch.platform.pcore.common.persistence.AttachMentEntity;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CommonService;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.act.dao.ActDao;
import com.hch.platform.pcore.modules.act.entity.Act;
import com.hch.platform.pcore.modules.act.service.ActTaskService;
import com.hch.platform.pcore.modules.actyw.entity.ActYw;
import com.hch.platform.pcore.modules.actyw.entity.ActYwGnode;
import com.hch.platform.pcore.modules.actyw.service.ActYwGnodeService;
import com.hch.platform.pcore.modules.actyw.service.ActYwService;
import com.hch.platform.pcore.modules.actyw.tool.process.ActYwTool;
import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.attachment.enums.FileStepEnum;
import com.hch.platform.pcore.modules.attachment.enums.FileTypeEnum;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.excellent.entity.ExcellentShow;
import com.hch.platform.pcore.modules.excellent.service.ExcellentShowService;
import com.hch.platform.pcore.modules.promodel.entity.ProCloseSubmit;
import com.hch.platform.pcore.modules.promodel.entity.ProMidSubmit;
import com.hch.platform.pcore.modules.promodel.entity.ProModel;
import com.hch.platform.pcore.modules.promodel.service.ProCloseSubmitService;
import com.hch.platform.pcore.modules.promodel.service.ProMidSubmitService;
import com.hch.platform.pcore.modules.promodel.service.ProModelService;
import com.hch.platform.pcore.modules.proproject.entity.ProProject;
import com.hch.platform.pcore.modules.proprojectmd.dao.ProModelMdDao;
import com.hch.platform.pcore.modules.proprojectmd.entity.ProModelMd;
import com.hch.platform.pcore.modules.sys.entity.BackTeacherExpansion;
import com.hch.platform.pcore.modules.sys.entity.Role;
import com.hch.platform.pcore.modules.sys.entity.StudentExpansion;
import com.hch.platform.pcore.modules.sys.service.SystemService;
import com.hch.platform.pcore.modules.sys.service.UserService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.entity.TeamUserHistory;
import com.hch.platform.pcore.modules.team.service.TeamService;
import com.hch.platform.pcore.modules.tpl.vo.IWparam;
import com.hch.platform.pcore.modules.tpl.vo.WtplType;
import com.hch.platform.pcore.modules.tpl.vo.Wtype;
import com.hch.platform.pcore.modules.tpl.vo.impl.WparamApply;
import com.hch.platform.pcore.modules.tpl.vo.impl.WparamReport;

import net.sf.json.JSONObject;

/**
 * proProjectMdService.
 * @author zy
 * @version 2017-09-18
 */
@Service
@Transactional(readOnly = true)
public class ProModelMdService extends CrudService<ProModelMdDao, ProModelMd> {
	@Autowired
    private ProModelMdDao proModelMdDao;
	@Autowired
	ProModelService proModelService;
	@Autowired
	ActYwGnodeService actYwGnodeService;
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
	private TeamService teamService;
	@Autowired
	private ExcellentShowService excellentShowService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private ProMidSubmitService proMidSubmitService;
	@Autowired
	private ProCloseSubmitService proCloseSubmitService;
	@Transactional(readOnly = false)
	public JSONObject saveModify(ProModelMd proModelMd) throws Exception{
		JSONObject js=new JSONObject();
		List<TeamUserHistory> stus=proModelMd.getProModel().getStudentList();
		List<TeamUserHistory> teas=proModelMd.getProModel().getTeacherList();
		String actywId=proModelMd.getProModel().getActYwId();
		String teamId=proModelMd.getProModel().getTeamId();
		String proId=proModelMd.getProModel().getId();
		js=commonService.checkProjectOnModify(stus,teas,proId, actywId,proModelMd.getProModel().getProCategory(), teamId);
		if("0".equals(js.getString("ret"))){
			return js;
		}
		proModelService.save(proModelMd.getProModel());
		this.save(proModelMd);
		commonService.disposeTeamUserHistoryForModify(stus, teas, actywId, teamId, proId);
		sysAttachmentService.saveByVo(proModelMd.getProModel().getAttachMentEntity(), proModelMd.getProModel().getId());
		js.put("msg", "保存成功");
		return js;
	}

	//判断是否需要重新保存附件,true 需要保存
	private boolean checkFile(String pid,AttachMentEntity a){
		SysAttachment s=new SysAttachment();
		s.setUid(pid);
		List<SysAttachment> list= sysAttachmentService.getFiles(s);
		if(list==null||list.size()==0||list.size()>1){
			return true;
		}else{
			if(a!=null&&a.getFielFtpUrl()!=null&&a.getFielFtpUrl().size()==1&&a.getFielFtpUrl().get(0).equals(list.get(0).getUrl())){
				return false;
			}else{
				return true;
			}
		}

	}
	public ProModelMd get(String id) {
		return super.get(id);
	}

	public List<ProModelMd> findList(ProModelMd proModelMd) {
		return super.findList(proModelMd);
	}

	public Page<ProModelMd> findPage(Page<ProModelMd> page, ProModelMd proModelMd) {
		return super.findPage(page, proModelMd);
	}

	@Transactional(readOnly = false)
	public JSONObject ajaxSaveProModelMd(ProModelMd proModelMd) {
		JSONObject js=new JSONObject();
		ProModel proModel=proModelMd.getProModel();
		/*if(StringUtil.isEmpty(proModel.getCompetitionNumber())){
			proModel.setCompetitionNumber(IdUtils.getGContestNumberByDb());
		}*/
		if(proModelMdDao.checkMdProName(proModel.getPName(), proModel.getId(),proModel.getType())>0){
			js.put("ret", 0);
			js.put("msg", "保存失败，该项目名称已经存在");
			return js;
		}
		proModelService.save(proModel);
		proModelMd.setModelId(proModel.getId());
		super.save(proModelMd);
		js.put("ret", 1);
		js.put("msg", "保存成功");
		return js;
	}


	/**
	 * @param proModelMd
	 * @return
	 */
	@Transactional(readOnly = false)
	public String saveProModelMd(ProModelMd proModelMd) {
		ProModel proModel=proModelMd.getProModel();
		if(checkFile(proModel.getId(), proModel.getAttachMentEntity())){//附件
			SysAttachment s=new SysAttachment();
			s.setUid(proModel.getId());
			s.setType(FileTypeEnum.S10);
			s.setFileStep(FileStepEnum.S2000);
			sysAttachmentService.deleteByCdn(s);
			Map<String, SysAttachment> map=sysAttachmentService.saveByVo(proModel.getAttachMentEntity(),proModel.getId(), FileTypeEnum.S10, FileStepEnum.S2000);
			if(map!=null&&proModel.getAttachMentEntity()!=null){
				proModelMd.setFileUrl(map.get(proModel.getAttachMentEntity().getFielFtpUrl().get(0)).getUrl());
				proModelMd.setFileId(map.get(proModel.getAttachMentEntity().getFielFtpUrl().get(0)).getId());
			}
		}else{
			if(proModel.getAttachMentEntity()!=null){
				proModelMd.setFileUrl(proModel.getAttachMentEntity().getFielFtpUrl().get(0));
			}

		}
		//保存团队的学分分配权重
		commonService.disposeTeamUserHistoryOnSave(proModel.getTeamUserHistoryList(), proModel.getActYwId(),proModel.getTeamId(),proModel.getId());

		//proModelService.save(proModel);
		//proModelMd.setModelId(proModel.getId());
		//super.save(proModelMd);
		return "保存成功";
	}
	//检查当前时间是否在时间范围内 true 不在有效期内
	private boolean checkValidDate(Date s,Date e){
		if(s==null||e==null){
			return true;
		}
		if(s.after(new Date())){
			return true;
		}
		if(e.before(new Date())){
			return true;
		}
		return false;
	}
	@Transactional(readOnly = false)
	public JSONObject submit(ProModelMd proModelMd,JSONObject js) {
		ProModel proModel=proModelMd.getProModel();
		ActYw actYw=actYwService.get(proModel.getActYwId());
		if(actYw==null||actYw.getProProject()==null||checkValidDate(actYw.getProProject().getNodeStartDate(),actYw.getProProject().getNodeEndDate())){
			js.put("ret",0);
  			js.put("msg","提交失败，不在申报有效时间内");
  			return  js;
		}

		js=commonService.checkProjectApplyOnSubmit(
						proModel.getId(),proModel.getActYwId(), proModel.getProCategory(),proModel.getTeamId());
		if("0".equals(js.get("ret"))){
			return js;
		}
		List<String> roles=new ArrayList<String>();

		//启动大赛工作流
		if(actYw!=null){
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
				if (roles.size()>0) {
					Map<String,Object> vars=new HashMap<String,Object>();
					vars=proModel.getVars();
					vars.put(nodeRoleId+"s",roles);
					String key=ActYw.getPkey(actYw.getGroup(),actYw.getProProject());
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
						if(checkFile(proModel.getId(), proModel.getAttachMentEntity())){//附件
							SysAttachment s=new SysAttachment();
							s.setUid(proModel.getId());
							s.setType(FileTypeEnum.S10);
							s.setFileStep(FileStepEnum.S2000);
							sysAttachmentService.deleteByCdn(s);
							sysAttachmentService.saveByVo(proModel.getAttachMentEntity(),proModel.getId(), FileTypeEnum.S10, FileStepEnum.S2000);
						}else{
							proModelMd.setFileUrl(proModel.getAttachMentEntity().getFielFtpUrl().get(0));
						}
						proModel.setProcInsId(act.getProcInsId());
						proModel.setSubTime(new Date());
						proModel.setProType(proProject.getProType());
						proModel.setType(proProject.getType());
						proModelService.save(proModel);
						Team team=teamService.get(proModel.getTeamId());
						commonService.disposeTeamUserHistoryOnSubmit(proModel.getTeamUserHistoryList(), proModel.getActYwId(),proModel.getTeamId(),proModel.getId());

						excellentShowService.saveExcellentShow(team==null?"":team.getProjectIntroduction(), proModel.getTeamId(),ExcellentShow.Type_Project,proModel.getId(),proModel.getActYwId());//保存优秀展示
						js.put("msg","恭喜您，申报成功!");
						return  js;
					}else{
						js.put("ret",0);
						js.put("msg","流程配置故障（审核流程未启动），请联系管理员");
						return  js;
					}
				}
			}
			js.put("ret",0);
			js.put("msg","流程配置故障（流程角色未配置），请联系管理员");
			return  js;
		}
		js.put("ret",0);
		js.put("msg","流程配置故障（审核流程不存在），请联系管理员");
		return  js;
	}

	@Transactional(readOnly = false)
	public void delete(ProModelMd proModelMd) {
		super.delete(proModelMd);
	}

	/*
	 * 初始化Word参数.
	 * @param xyteachers 校园导师
	 * @param qyteachers 企业导师
	 * @param proModelMd 项目
	 * @param team 团队
	 * @param students 团队成员  @return IWparam
	*/
	public IWparam initIWparam(String type, String vsn, ProModel proModel, ProModelMd proModelMd, Team team, List<BackTeacherExpansion> xytes, List<BackTeacherExpansion> qytes, List<StudentExpansion> tms){
		Wtype wtype = null;
	  if(StringUtil.isNotEmpty(type)){
		  wtype = Wtype.getByKey(type);
		  if(wtype == null){
		    logger.info("Word 模板类型未定义！[type = "+type+"]");
		  }
		}

		IWparam wordParam = null;
		if(wtype != null){
		  if((wtype.getTpl()).equals(WtplType.TT_APPLY)){
		    wordParam = WparamApply.init(proModel, proModelMd, team, xytes, qytes, tms);
		  }else if((wtype.getTpl()).equals(WtplType.TT_REPORT_JX) || (wtype.getTpl()).equals(WtplType.TT_REPORT_ZQ)){
        wordParam = WparamReport.init(proModel, proModelMd, team, xytes, qytes, tms);
		  }

		  if(wordParam != null){
	      wordParam.setFileName(IWparam.getFileTplPreFix() + wtype.getName());
	      wordParam.setTplFileName(IWparam.FILE_TPL_PREFIX + wtype.getName());
		  }
		}
		return wordParam;
	}

	public ProModelMd getByDeclareId(String declareId,String actywId) {
		List<ProModelMd> pmd= proModelMdDao.getByDeclareId(declareId,actywId);
		if(pmd!=null&&pmd.size()>0){
			return pmd.get(0);
		}
		return null;
	}

	public List<String> getAllPromodelMd() {
		List<String> pmd= proModelMdDao.getAllPromodelMd();
		return pmd;
	}


	public ProModelMd getByProModelId(String proModelId) {
		return proModelMdDao.getByProModelId(proModelId);
	}

	@Transactional(readOnly = false)
	public JSONObject midSubmit(ProModelMd proModelMd, JSONObject js,String gnodeId) {
			ProModel proModel=proModelMd.getProModel();
		ActYw actYw=actYwService.get(proModel.getActYwId());
		List<String> roles=new ArrayList<String>();
		if(actYw!=null){
			String taskId=actTaskService.getTaskidByProcInsId(proModel.getProcInsId());
			String taskDefinitionKeyaskDefKey=actTaskService.getTask(taskId).getTaskDefinitionKey();
			String key=actYw.getPkey(actYw.getGroup(),actYw.getProProject());
			//String nextRoleId=actTaskService.getProcessNextRoleName(proModel.getAct().getTaskDefKey(),key);
			String  nextNodeRoleId=actTaskService.getProcessNextRoleName(taskDefinitionKeyaskDefKey,key);  //从工作流中查询 下一步的角色集合
			String nextroleId=nextNodeRoleId.substring(ActYwTool.FLOW_ROLE_ID_PREFIX.length());
			Role role= systemService.getNamebyId(nextroleId);
			if(role!=null){
				//启动节点
				String roleName=role.getName();
				if(roleName.contains(SysIds.ISCOLLEGE.getRemark())||roleName.contains(SysIds.ISMS.getRemark())){
					roles=userService.getRolesByName(role.getEnname(),proModel.getDeclareId());
				}else{
					roles=userService.getRolesByName(role.getEnname());
				}
				if (roles.size()>0) {
					Map<String,Object> vars=new HashMap<String,Object>();
					vars=proModel.getVars();
					vars.put(nextroleId+"s",roles);
					if("0".equals(proModelMd.getSetState())){

					}else {
						taskService.complete(taskId, vars);
					}
					proModelService.save(proModel);
					super.save(proModelMd);
					if(gnodeId!=null){
						ProMidSubmit proMidSubmit=new ProMidSubmit();
						proMidSubmit.setGnodeId(gnodeId);
						proMidSubmit.setPromodelId(proModel.getId());
						proMidSubmit.setState("0");
						proMidSubmitService.save(proMidSubmit);
					}
					if(checkFile(proModel.getId(), proModel.getAttachMentEntity())){//附件
						SysAttachment s=new SysAttachment();
						s.setUid(proModel.getId());
						s.setType(FileTypeEnum.S10);
						s.setFileStep(FileStepEnum.S2200);
						sysAttachmentService.deleteByCdn(s);
						if(StringUtil.isNotEmpty(gnodeId)){
							proModel.getAttachMentEntity().setGnodeId(gnodeId);
						}
						sysAttachmentService.saveByVo(proModel.getAttachMentEntity(),proModel.getId(), FileTypeEnum.S10, FileStepEnum.S2200);
					}else{
						proModelMd.setFileUrl(proModel.getAttachMentEntity().getFielFtpUrl().get(0));
					}
					js.put("ret",1);
					js.put("msg","中期申报成功");
					return  js;
				}
			}
			js.put("ret",0);
			js.put("msg","流程配置故障（流程角色未配置），请联系管理员");
			return  js;
		}
		js.put("ret",0);
		js.put("msg","流程配置故障（审核流程不存在），请联系管理员");
		return  js;
	}

	@Transactional(readOnly = false)
	public JSONObject closeSubmit(ProModelMd proModelMd, JSONObject js,String gnodeId) {
			ProModel proModel=proModelMd.getProModel();
		ActYw actYw=actYwService.get(proModel.getActYwId());
		List<String> roles=new ArrayList<String>();
		if(actYw!=null){
			String taskId=actTaskService.getTaskidByProcInsId(proModel.getProcInsId());
			String taskDefinitionKeyaskDefKey=actTaskService.getTask(taskId).getTaskDefinitionKey();
			String key=actYw.getPkey(actYw.getGroup(),actYw.getProProject());
			//String nextRoleId=actTaskService.getProcessNextRoleName(proModel.getAct().getTaskDefKey(),key);
			String  nextNodeRoleId=actTaskService.getProcessNextRoleName(taskDefinitionKeyaskDefKey,key);  //从工作流中查询 下一步的角色集合
			String nextroleId=nextNodeRoleId.substring(ActYwTool.FLOW_ROLE_ID_PREFIX.length());
			Role role= systemService.getNamebyId(nextroleId);
			if(role!=null){
				//启动节点
				String roleName=role.getName();
				if(roleName.contains(SysIds.ISCOLLEGE.getRemark())||roleName.contains(SysIds.ISMS.getRemark())){
					roles=userService.getRolesByName(role.getEnname(),proModel.getDeclareId());
				}else{
					roles=userService.getRolesByName(role.getEnname());
				}
				if (roles.size()>0) {
					Map<String,Object> vars=new HashMap<String,Object>();
					vars=proModel.getVars();
					vars.put(nextroleId+"s",roles);
					if("0".equals(proModelMd.getSetState()) || "0".equals(proModelMd.getMidState())){

					}else {
						taskService.complete(taskId, vars);
					}
					proModelService.save(proModel);
					super.save(proModelMd);
					if(gnodeId!=null){
						ProCloseSubmit proCloseSubmit=new ProCloseSubmit();
						proCloseSubmit.setGnodeId(gnodeId);
						proCloseSubmit.setPromodelId(proModel.getId());
						proCloseSubmit.setState("0");
						proCloseSubmitService.save(proCloseSubmit);
					}
					if(checkFile(proModel.getId(), proModel.getAttachMentEntity())){//附件
						SysAttachment s=new SysAttachment();
						s.setUid(proModel.getId());
						s.setType(FileTypeEnum.S10);
						s.setFileStep(FileStepEnum.S2300);
						sysAttachmentService.deleteByCdn(s);
						if(StringUtil.isNotEmpty(gnodeId)){
							proModel.getAttachMentEntity().setGnodeId(gnodeId);
						}
						sysAttachmentService.saveByVo(proModel.getAttachMentEntity(),proModel.getId(), FileTypeEnum.S10, FileStepEnum.S2300);
					}else{
						proModelMd.setFileUrl(proModel.getAttachMentEntity().getFielFtpUrl().get(0));
					}
					js.put("ret",1);
					js.put("msg","结项申报成功");
					return  js;
				}
			}
			js.put("ret",0);
			js.put("msg","流程配置故障（流程角色未配置），请联系管理员");
			return  js;
		}
		js.put("ret",0);
		js.put("msg","流程配置故障（审核流程不存在），请联系管理员");
		return  js;
	}


	public Page<ProModelMd> modelMdTodoList(Page<ProModelMd> page, String gnodeId,Act act, String keyName) {
		//通过流程获得ids
		List<String> actIds= actTaskService.modelMdtodoList(act,keyName);
		ActYwGnode actYwGnode=actYwGnodeService.get(gnodeId);
		//通过立项未通过获得ids
		if(actYwGnode!=null&& actYwGnode.getName().contains("立项")){
			List<String> mdNoPassIds=proModelMdDao.getBySetNoPassList();
			actIds.addAll(mdNoPassIds);
		}
		if(actYwGnode!=null&& actYwGnode.getName().contains("中期")){
			List<String> mdNoPassIds=proModelMdDao.getByMidNoPassList();
			actIds.addAll(mdNoPassIds);
		}
		if(actYwGnode!=null&& actYwGnode.getName().contains("结项")){
			List<String> mdNoPassIds=proModelMdDao.getByCloseNoPassList();
			actIds.addAll(mdNoPassIds);
		}
		page.setCount(actIds.size());
		int pageStart=(page.getPageNo()-1)*page.getPageSize();
		int pageEnd=actIds.size();
		if (actIds.size()>page.getPageNo()*page.getPageSize()) {
			pageEnd=page.getPageNo()*page.getPageSize();
		}
		List<String> idsList=actIds.subList(pageStart,pageEnd);
		/*String ids="";
		for(String id:idsList){
			ids=ids+id+",";
		}
		ids=ids.substring(0,ids.lastIndexOf(","));*/
		if(idsList.size()>0){
			List<ProModelMd> subList=proModelMdDao.getListByModelIds(idsList);
			page.setList(subList);
		}

		return page;

	}

	public Page<ProModelMd> findMdPage(Page<ProModelMd> page, ProModel proModel) {

		if(page.getPageSize()<0){
			page.setPageSize(10);
		}
		int count=proModelMdDao.getListByModelCount(proModel.getActYwId());
		page.setCount(count);
		if(count<1){
			return page;
		}
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("offset", (page.getPageNo()-1)*page.getPageSize());
		param.put("pageSize", page.getPageSize());
		param.put("actYwId", proModel.getActYwId());
		List<ProModelMd> subList=proModelMdDao.getListByModel(param);
		page.setList(subList);
		return page;
	}
}