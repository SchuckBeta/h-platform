package com.oseasy.initiate.modules.project.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.modules.authorize.service.AuthorizeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CommonService;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.enums.FileStepEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.service.ProjectAnnounceService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.project.service.ProjectPlanService;
import com.oseasy.initiate.modules.project.vo.ProjectDeclareListVo;
import com.oseasy.initiate.modules.project.vo.ProjectDeclareVo;
import com.oseasy.initiate.modules.promodel.entity.ProModel;
import com.oseasy.initiate.modules.sco.service.ScoAllotRatioService;
import com.oseasy.initiate.modules.sco.vo.ScoRatioVo;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SysStudentExpansionService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserHistory;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamUserHistoryService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 项目申报Controller
 * @author 9527
 * @version 2017-03-11
 */
@Controller
public class ProjectDeclareController extends BaseController {
	public static Logger logger = Logger.getLogger(ProjectDeclareController.class);
	@Autowired
	ProjectAnnounceService projectAnnounceService;
	@Autowired
	SysStudentExpansionService sysStudentExpansionService;
	@Autowired
	SysAttachmentService sysAttachmentService;
	@Autowired
	private ProjectDeclareService projectDeclareService;
	@Autowired
	private ProjectPlanService projectPlanService;
	@Autowired
	ScoAllotRatioService scoAllotRatioService;
	@Autowired
	TeamUserRelationService teamUserRelationService;
	@Autowired
	TeamUserHistoryService teamUserHistoryService;
	@Autowired
	private CommonService commonService;
	@Autowired
	AuthorizeService authorizeService;

	@ModelAttribute
	public ProjectDeclare get(@RequestParam(required=false) String id) {
		ProjectDeclare entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = projectDeclareService.get(id);
		}
		if (entity == null) {
			entity = new ProjectDeclare();
		}
		return entity;
	}

	@RequestMapping(value = {"${frontPath}/project/projectDeclare/list"})
	public String list(HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		ProjectDeclareListVo vo=new ProjectDeclareListVo();
		vo.setUserid(user.getId());
		Page<ProjectDeclareListVo> page = projectDeclareService.getMyProjectListPlus(new Page<ProjectDeclareListVo>(request, response), vo);
		model.addAttribute("page", page);
		model.addAttribute("user", user);

		/*for(ProjectDeclareListVo map:page.getList()){
			String projectId = map.getId();
			ProjectDeclare pro =  projectDeclareService.getScoreConfigure(projectId);
			//根据 type（学分类型)、item（学分项）、category（课程、项目、大赛、技能大类）、subdivision（课程、项目、大赛小类）、number(人数)查询后台配比
			ScoRatioVo scoRatioVo = new ScoRatioVo();
			if(StringUtil.equals(pro.getType(),"1")||StringUtil.equals(pro.getType(),"2")){ //创新训练、创业训练
				scoRatioVo.setType("0000000123"); //设置查询的学分类型（创新学分）
			}
			if(StringUtil.equals(pro.getType(),"3")){ //创业实践
				scoRatioVo.setType("0000000124"); //设置查询的学分类型（创业学分）
			}
			scoRatioVo.setItem("0000000128"); //双创项目
			scoRatioVo.setCategory("1"); //大学生创新创业训练项目
			scoRatioVo.setSubdivision(pro.getType());
			scoRatioVo.setNumber(pro.getSnumber());
			ScoRatioVo ratioResult = scoAllotRatioService.findRatio(scoRatioVo);
			if(ratioResult!=null){
				map.setHasConfig("true");
				map.setRatio(ratioResult.getRatio());
			}else{
				map.setHasConfig("false");
			}

		}*/

		return "modules/project/projectDeclareList";
	}

	//学分配比页面
	@RequestMapping(value="${frontPath}/project/projectDeclare/scoreConfig")
	public String scoreConfig(String projectId,Model model){
		//查找后台配比规则 后台设置的配比
		ProjectDeclare pro =  projectDeclareService.getScoreConfigure(projectId);
		//根据 type（学分类型)、item（学分项）、category（课程、项目、大赛、技能大类）、subdivision（课程、项目、大赛小类）、number(人数)查询后台配比
		ScoRatioVo scoRatioVo = new ScoRatioVo();
		if(StringUtil.equals(pro.getType(),"1")||StringUtil.equals(pro.getType(),"2")){ //创新训练、创业训练
			scoRatioVo.setType("0000000123"); //设置查询的学分类型（创新学分）
		}
		if(StringUtil.equals(pro.getType(),"3")){ //创业实践
			scoRatioVo.setType("0000000124"); //设置查询的学分类型（创业学分）
		}
		scoRatioVo.setItem("0000000128"); //双创项目
		scoRatioVo.setCategory("1"); //大学生创新创业训练项目
		scoRatioVo.setSubdivision(pro.getType());
		scoRatioVo.setNumber(pro.getSnumber());
		ScoRatioVo ratioResult = scoAllotRatioService.findRatio(scoRatioVo);
		model.addAttribute("ratio", ratioResult.getRatio());
		//查找组成员的信息
		TeamUserRelation teamUserRelation = new TeamUserRelation();
		teamUserRelation.setTeamId(pro.getTeamId());
		//List<TeamUserRelation>  studentList=  teamUserRelationService.getStudents(teamUserRelation);
		List<Map<String,String>>  studentList= projectDeclareService.findTeamStudentFromTUH(pro.getTeamId(),pro.getId());
		model.addAttribute("studentList", studentList);
		//判断是否能保存配比信息（如果是提交立项报告前的阶段能保存，提交立项报告后的阶段只能查看）
		boolean isSubmit = false;
		if(StringUtil.equals(pro.getStatus(),"7")||StringUtil.equals(pro.getStatus(),"8")||StringUtil.equals(pro.getStatus(),"9")){
			isSubmit = false;
		}else{
			isSubmit = true;
		}
		model.addAttribute("isSubmit", isSubmit);

		return  "modules/project/scoreConfig";
	}

	//ajax 请求，页面传入type(项目类型），snumber（团队学生人数），返回后台的学分配比比例
	@RequestMapping("${frontPath}/project/projectDeclare/findRatio")
	@ResponseBody
	public String frontFindRatio(String type,int snumber ){
		return findRatioDis(type, snumber);
	}
	//ajax 请求，页面传入type(项目类型），snumber（团队学生人数），返回后台的学分配比比例
	@RequestMapping("${adminPath}/project/projectDeclare/findRatio")
	@ResponseBody
	public String backFindRatio(String type,int snumber ){
		return findRatioDis(type, snumber);
	}
	private String findRatioDis(String type,int snumber ){
		Boolean bl=authorizeService.checkMenuByNum(5);
		//是否授权
		if(bl) {
			ScoRatioVo scoRatioVo = new ScoRatioVo();
			if (StringUtil.equals(type, "1") || StringUtil.equals(type, "2")) { //创新训练、创业训练
				scoRatioVo.setType("0000000123"); //设置查询的学分类型（创新学分）
			}
			if (StringUtil.equals(type, "3")) { //创业实践
				scoRatioVo.setType("0000000124"); //设置查询的学分类型（创业学分）
			}
			scoRatioVo.setItem("0000000128"); //双创项目
			scoRatioVo.setCategory("1"); //大学生创新创业训练项目
			scoRatioVo.setSubdivision(type);
			scoRatioVo.setNumber(snumber);
			ScoRatioVo ratioResult = scoAllotRatioService.findRatio(scoRatioVo);
			if (ratioResult != null) {
				return ratioResult.getRatio();
			} else {
				return "";
			}
		}else{
			return "";
		}
	}

	//保存学分配比
	@RequestMapping(value="${frontPath}/project/projectDeclare/saveScoreConfig")
	@ResponseBody
	public boolean saveScoreConfig(ProjectDeclare projectDeclare){
		for (TeamUserHistory tur:projectDeclare.getTeamUserHistoryList()){
			teamUserHistoryService.updateWeight(tur);
		}
		return true;
	}

	//判断项目是否能提交结项报告 （如果后台配置了规则，但该项目没有给成员配比，则不能提交结项报告）
	@RequestMapping(value="${frontPath}/project/projectDeclare/canSumitClose")
	@ResponseBody
    public boolean canSumitClose(String projectId){
		//校验学分是否授权
		boolean isAuthorize=UserUtils.checkMenuByNum(5);
		//已授权
		if(isAuthorize){
			ProjectDeclare pro =  projectDeclareService.getScoreConfigure(projectId);
			//根据 type（学分类型)、item（学分项）、category（课程、项目、大赛、技能大类）、subdivision（课程、项目、大赛小类）、number(人数)查询后台配比
			ScoRatioVo scoRatioVo = new ScoRatioVo();
			if(StringUtil.equals(pro.getType(),"1")||StringUtil.equals(pro.getType(),"2")){ //创新训练、创业训练
				scoRatioVo.setType("0000000123"); //设置查询的学分类型（创新学分）
			}
			if(StringUtil.equals(pro.getType(),"3")){ //创业实践
				scoRatioVo.setType("0000000124"); //设置查询的学分类型（创业学分）
			}
			scoRatioVo.setItem("0000000128"); //双创项目
			scoRatioVo.setCategory("1"); //大学生创新创业训练项目
			scoRatioVo.setSubdivision(pro.getType());
			scoRatioVo.setNumber(pro.getSnumber());
			ScoRatioVo ratioResult = scoAllotRatioService.findRatio(scoRatioVo);
			boolean hasConfig=false;  //判断后台是否配置了规则
			if(ratioResult!=null){
				hasConfig=true;
			}else{
				hasConfig=false;
			}

			//判断该项目有没有给成员配比（如果team_user_relation表的weight_val的sum不为0则是配比了）
		  //  int weightTotal = teamUserRelationService.getWeightTotalByTeamId(pro.getTeamId());
			int weightTotal = teamUserHistoryService.getWeightTotalByTeamId(pro.getTeamId(),pro.getId());
			if(hasConfig&&weightTotal==0){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}


	@RequestMapping(value = {"${frontPath}/project/projectDeclare/curProject"})
	public String curProject(ProjectDeclare projectDeclare, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		model.addAttribute("user", user);
		List<Map<String, String>> list=projectDeclareService.getCurProProject();
		if(list!=null&&list.size()>0){
			model.addAttribute("pp",JSONArray.fromObject(list) );
		}
		return "modules/project/projectTimeIndex";
	}
	@RequestMapping(value = {"${frontPath}/project/projectDeclare/getTimeIndexSecondTabs"})
	@ResponseBody
	public JSONArray getTimeIndexSecondTabs(HttpServletRequest request) {
		String pptype=request.getParameter("pptype");
		String actywId=request.getParameter("actywId");
		User user = UserUtils.getUser();
		if(StringUtil.isNotEmpty(user.getId())&&StringUtil.isNotEmpty(pptype)&&StringUtil.isNotEmpty(actywId)){
			List<Map<String, String>> list=projectDeclareService.getTimeIndexSecondTabs(pptype,actywId,user.getId());
			if(list!=null&&list.size()>0){
				return JSONArray.fromObject(list);
			}
		}
		return new JSONArray();
	}
	@RequestMapping(value = "${frontPath}/project/projectDeclare/getTimeIndexData")
	@ResponseBody
	public JSONObject getTimeIndexData(String pptype,String actywId,String projectId,HttpServletRequest request, HttpServletResponse response) {
		try {
			return projectDeclareService.getTimeIndexData(pptype, actywId, projectId);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		return new JSONObject();
	}
	@RequestMapping(value = "${frontPath}/project/projectDeclare/form")
	public String form(ProjectDeclare projectDeclare, Model model,HttpServletRequest request) {
		User user = UserUtils.getUser();
		if(StringUtil.isNotEmpty(projectDeclare.getId())){
			if(StringUtil.isEmpty(projectDeclare.getLeader())
					||StringUtil.isEmpty(user.getId())||!user.getId().equals(projectDeclare.getLeader())){
				return "redirect:"+Global.getFrontPath()+"/project/projectDeclare/viewForm?id="+projectDeclare.getId();
			}
		}
		ProjectDeclareVo vo=new ProjectDeclareVo();
		/*if (projectDeclare.getId()==null) {
			Map<String,String> map=new HashMap<String,String>();
			map.put("projectType", "1");
			map.put("file_step", FileTypeEnum.S200.getValue());
			map.put("type",FileSourceEnum.S1.getValue());
			List<Map<String, String>> list=projectAnnounceService.findCurInfo(map);
			if (list==null||list.size()==0) {
				return "redirect:"+frontPath;
			}else{
				vo.setProjectAnnounce(list.get(0));
			}
		}*/
		model.addAttribute("levelList", DictUtils.getDictList("project_degree"));
		model.addAttribute("resultTypeList", DictUtils.getDictList("project_result_type"));
		model.addAttribute("project_type", DictUtils.getDictList("project_type"));
		model.addAttribute("project_extend", DictUtils.getDictList("project_extend"));
		model.addAttribute("sysdate", DateUtil.formatDate((projectDeclare.getCreateDate()==null?new Date():projectDeclare.getCreateDate()),"yyyy-MM-dd"));
		model.addAttribute("project_source", DictUtils.getDictList("project_source"));
		User leader = (projectDeclare.getLeader()==null?UserUtils.getUser():UserUtils.get(projectDeclare.getLeader()));
		User creater =(projectDeclare.getCreateBy()==null?UserUtils.getUser():UserUtils.get(projectDeclare.getCreateBy().getId()));
		model.addAttribute("teams", projectDeclareService.findTeams(leader.getId(),projectDeclare.getTeamId()));
		model.addAttribute("user", user);
		model.addAttribute("creater", creater);
		model.addAttribute("leader", leader);
		model.addAttribute("studentExpansion", sysStudentExpansionService.getByUserId(leader.getId()));
		vo.setPlans(projectPlanService.findListByProjectId(projectDeclare.getId()));
		vo.setProjectDeclare(projectDeclare);
		vo.setTeamStudent(projectDeclareService.findTeamStudentFromTUH(projectDeclare.getTeamId(),projectDeclare.getId()));
		vo.setTeamTeacher(projectDeclareService.findTeamTeacherFromTUH(projectDeclare.getTeamId(),projectDeclare.getId()));
		SysAttachment sa=new SysAttachment();
		sa.setUid(projectDeclare.getId());
		sa.setFileStep(FileStepEnum.S100);
		sa.setType(FileTypeEnum.S0);
		vo.setFileInfo(sysAttachmentService.getFiles(sa));
		if(StringUtil.isEmpty(projectDeclare.getActywId()))projectDeclare.setActywId(request.getParameter("actywId"));
		model.addAttribute("projectDeclareVo", vo);
		return "modules/project/projectDeclareForm";
	}
	@RequestMapping(value = "${frontPath}/project/projectDeclare/viewForm")
	public String viewForm(ProjectDeclare projectDeclare, Model model) {
		ProjectDeclareVo vo=new ProjectDeclareVo();
		model.addAttribute("levelList", DictUtils.getDictList("project_degree"));
		model.addAttribute("resultTypeList", DictUtils.getDictList("project_result_type"));
		model.addAttribute("project_type", DictUtils.getDictList("project_type"));
		model.addAttribute("project_extend", DictUtils.getDictList("project_extend"));
		model.addAttribute("sysdate", DateUtil.formatDate((projectDeclare.getCreateDate()==null?new Date():projectDeclare.getCreateDate()),"yyyy-MM-dd"));
		model.addAttribute("project_source", DictUtils.getDictList("project_source"));

		User user = UserUtils.getUser();
		User leader = UserUtils.get(projectDeclare.getLeader());
		User creater = UserUtils.get(projectDeclare.getCreateBy().getId());
		model.addAttribute("teams", projectDeclareService.findTeams(leader.getId(),projectDeclare.getTeamId()));
		model.addAttribute("user", user);
		model.addAttribute("creater", creater);
		model.addAttribute("leader", leader);
		model.addAttribute("studentExpansion", sysStudentExpansionService.getByUserId(leader.getId()));
		vo.setPlans(projectPlanService.findListByProjectId(projectDeclare.getId()));
		vo.setProjectDeclare(projectDeclare);
		vo.setTeamStudent(projectDeclareService.findTeamStudentFromTUH(projectDeclare.getTeamId(),projectDeclare.getId()));
		vo.setTeamTeacher(projectDeclareService.findTeamTeacherFromTUH(projectDeclare.getTeamId(),projectDeclare.getId()));
		SysAttachment sa=new SysAttachment();
		sa.setUid(projectDeclare.getId());
		sa.setFileStep(FileStepEnum.S100);
		sa.setType(FileTypeEnum.S0);
		vo.setFileInfo(sysAttachmentService.getFiles(sa));
		if (projectDeclare.getStatus()!=null&&!"0".equals(projectDeclare.getStatus())) {
			vo.setAuditInfo(projectDeclareService.getProjectAuditInfo(projectDeclare.getId()));
		}
		model.addAttribute("projectDeclareVo", vo);
		return "modules/project/projectDeclareFormView";
	}
/*	private boolean checkIsGraduation(Date date,int year) {
		if (date!=null) {
			Calendar now=Calendar.getInstance();
			Calendar c=Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.YEAR, year);
			if (c.before(now)) {
				return true;
			}
		}
		return false;
	}*/
	@RequestMapping(value = "${frontPath}/project/projectDeclare/save")
	@ResponseBody
	public JSONObject save(ProjectDeclareVo vo, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		JSONObject js=new JSONObject();
		User user = UserUtils.getUser();
		ProjectDeclare pd=vo.getProjectDeclare();
		js=commonService.checkProjectApplyOnSave(pd.getId(), pd.getActywId(),pd.getType(), pd.getTeamId());
		if("0".equals(js.getString("ret"))){
			return js;
		}
		if (!beanValidator(model, pd)) {
			js.put("ret", 0);
			js.put("msg", "保存失败");
			return js;
		}
		pd.setCreateBy(user);
		projectDeclareService.saveProjectDeclareVo(vo);
		js.put("id", pd.getId());
		js.put("ret", 1);
		js.put("msg", "保存成功");
		return js;
	}
	@RequestMapping(value = "${frontPath}/project/projectDeclare/submit")
	@ResponseBody
	public JSONObject submit(ProjectDeclareVo vo, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		JSONObject js=new JSONObject();
		User user = UserUtils.getUser();
		ProjectDeclare pd=vo.getProjectDeclare();
		js=commonService.checkProjectApplyOnSubmit(pd.getId(), pd.getActywId(),pd.getType(), pd.getTeamId());
		if("0".equals(js.getString("ret"))){
			return js;
		}
		pd.setCreateBy(user);
		projectDeclareService.submitProjectDeclareVo(vo);
		js.put("ret", 1);
		js.put("msg", "恭喜您，项目申报成功!");
		return js;
	}

	@RequestMapping(value = "${frontPath}/project/projectDeclare/delete")
	public String delete(ProjectDeclare projectDeclare, RedirectAttributes redirectAttributes,HttpServletRequest request) {
		String ftb=request.getParameter("ftb");
		projectDeclareService.delete(projectDeclare,ftb);
		addMessage(redirectAttributes, "删除项目申报成功");
		return "redirect:"+Global.getFrontPath()+"/project/projectDeclare/list?repage";
	}
	@ResponseBody
	@RequestMapping(value = "${frontPath}/project/projectDeclare/findTeamPerson")
	public List<Map<String,String>> findTeamPerson(@RequestParam(required=true) String id) {
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		List<Map<String,String>> list1=projectDeclareService.findTeamStudent(id);
		List<Map<String,String>> list2=projectDeclareService.findTeamTeacher(id);
		for(Map<String,String> map:list1) {
			list.add(map);
		}
		for(Map<String,String> map:list2) {
			list.add(map);
		}
		if(list.size()==0){
			list=null;
		}
		return list;
	}
	@RequestMapping("${frontPath}/project/projectDeclare/onProjectApply")
	@ResponseBody
	public JSONObject onProjectApply(String actywId){
		return commonService.onProjectApply(actywId);
	}
	@RequestMapping("${frontPath}/project/projectDeclare/checkProjectTeam")
	@ResponseBody
	public JSONObject checkProjectTeam(String proid,String actywId,String lowType,String teamid){
		return commonService.checkProjectTeam(proid, actywId, lowType, teamid);
	}

  /**
   * 根据负责人获取项目.
   * @param uid 用户ID
   * @return ActYwRstatus 结果状态
   */
  @ResponseBody
  @RequestMapping(value = "${frontPath}/project/projectDeclare/ajaxProByLeader/{uid}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<List<ProModel>> ajaxProByLeader(@PathVariable("uid") String uid) {
    List<ProModel> proModels = projectDeclareService.findListAllByLeader(uid);
    if ((proModels == null) || (proModels.size() <= 0)) {
      return new ActYwRstatus<List<ProModel>>(false, "查询失败或结果为空！");
    }
    return new ActYwRstatus<List<ProModel>>(true, "查询成功", proModels);
  }

  /**
   * 根据负责人获取所有团队.
   * @param uid 用户ID
   * @return ActYwRstatus 结果状态
   */
  @ResponseBody
  @RequestMapping(value = "${frontPath}/project/projectDeclare/ajaxTeamByLeader/{uid}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<List<Team>> ajaxTeamByLeader(@PathVariable("uid") String uid) {
    List<Team> teams = projectDeclareService.findTeams(uid, null);
    if ((teams == null) || (teams.size() <= 0)) {
      return new ActYwRstatus<List<Team>>(false, "查询失败或结果为空！");
    }
    return new ActYwRstatus<List<Team>>(true, "查询成功", teams);
  }
}