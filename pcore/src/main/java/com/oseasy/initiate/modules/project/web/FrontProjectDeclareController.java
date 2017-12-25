package com.oseasy.initiate.modules.project.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.enums.FileSourceEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.ftp.service.FtpService;
import com.oseasy.initiate.modules.project.entity.ProjectAnnounce;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.exception.ProjectNameDuplicateException;
import com.oseasy.initiate.modules.project.service.ProjectAnnounceService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.project.service.ProjectPlanService;
import com.oseasy.initiate.modules.project.vo.ProjectDeclareVo;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SysStudentExpansionService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

import net.sf.json.JSONObject;

/**
 * 项目申报Controller
 * @author 9527
 * @version 2017-03-11
 */
@Controller
@RequestMapping(value = "${frontPath}/project/projectDeclare")
public class FrontProjectDeclareController extends BaseController {
	@Autowired
	ProjectAnnounceService projectAnnounceService;
	@Autowired
	SysStudentExpansionService sysStudentExpansionService;
	@Autowired
	SysAttachmentService sysAttachmentService;
	@Autowired
	private FtpService ftpService;
	@Autowired
	private ProjectDeclareService projectDeclareService;
	@Autowired
	private ProjectPlanService projectPlanService;
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
	
	/*@RequiresPermissions("project:projectDeclare:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectDeclare projectDeclare, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProjectDeclare> page = projectDeclareService.findPage(new Page<ProjectDeclare>(request, response), projectDeclare); 
		model.addAttribute("page", page);
		return "modules/project/projectDeclareList";
	}*/
	@RequestMapping(value = {"list"})
	public String list(ProjectDeclare projectDeclare, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String,Object> param =new HashMap<String,Object>();
		User user = UserUtils.getUser();
		param.put("userid", user.getId());
		Page<Map<String,String>> page = projectDeclareService.getMyProjectList(new Page<Map<String,String>>(request, response), param); 
		model.addAttribute("page", page);
		model.addAttribute("user", user);
		return "modules/project/projectDeclareList";
	}
	@RequestMapping(value = {"curProject"})
	public String curProject(ProjectDeclare projectDeclare, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		model.addAttribute("user", user);
		return "modules/project/projectTimeIndex";
	}
	@RequestMapping(value = "getTimeIndexData")
	@ResponseBody
	public JSONObject getTimeIndexData(HttpServletRequest request, HttpServletResponse response) {
		User user = UserUtils.getUser();
		return projectDeclareService.getTimeIndexData(user);
	}
	@RequestMapping(value = "form")
	public String form(ProjectDeclare projectDeclare, Model model) {
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
		model.addAttribute("sysdate", DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
		model.addAttribute("project_source", DictUtils.getDictList("project_source"));

		User user = UserUtils.getUser();
		model.addAttribute("teams", projectDeclareService.findTeams(user.getId(),projectDeclare.getTeamId()));
		model.addAttribute("user", user);
		model.addAttribute("studentExpansion", sysStudentExpansionService.getByUserId(user.getId()));
		vo.setPlans(projectPlanService.findListByProjectId(projectDeclare.getId()));
		vo.setProjectDeclare(projectDeclare);
		vo.setTeamStudent(projectDeclareService.findTeamStudent(projectDeclare.getTeamId()));
		vo.setTeamTeacher(projectDeclareService.findTeamTeacher(projectDeclare.getTeamId()));
		Map<String,String> map=new HashMap<String,String>();
		map.put("uid", projectDeclare.getId());
		map.put("file_step", FileTypeEnum.S100.getValue());
		map.put("type",FileSourceEnum.S0.getValue());
		vo.setFileInfo(sysAttachmentService.getFileInfo(map));
		model.addAttribute("projectDeclareVo", vo);
		return "modules/project/projectDeclareForm";
	}
	@RequestMapping(value = "viewForm")
	public String viewForm(ProjectDeclare projectDeclare, Model model) {
		ProjectDeclareVo vo=new ProjectDeclareVo();
		model.addAttribute("levelList", DictUtils.getDictList("project_degree"));
		model.addAttribute("resultTypeList", DictUtils.getDictList("project_result_type"));
		model.addAttribute("project_type", DictUtils.getDictList("project_type"));
		model.addAttribute("project_extend", DictUtils.getDictList("project_extend"));
		model.addAttribute("sysdate", DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
		model.addAttribute("project_source", DictUtils.getDictList("project_source"));

		User user = UserUtils.getUser();
		model.addAttribute("teams", projectDeclareService.findTeams(user.getId(),projectDeclare.getTeamId()));
		model.addAttribute("user", user);
		model.addAttribute("studentExpansion", sysStudentExpansionService.getByUserId(user.getId()));
		vo.setPlans(projectPlanService.findListByProjectId(projectDeclare.getId()));
		vo.setProjectDeclare(projectDeclare);
		vo.setTeamStudent(projectDeclareService.findTeamStudent(projectDeclare.getTeamId()));
		vo.setTeamTeacher(projectDeclareService.findTeamTeacher(projectDeclare.getTeamId()));
		Map<String,String> map=new HashMap<String,String>();
		map.put("uid", projectDeclare.getId());
		map.put("file_step", FileTypeEnum.S100.getValue());
		map.put("type",FileSourceEnum.S0.getValue());
		vo.setFileInfo(sysAttachmentService.getFileInfo(map));
		if (projectDeclare.getStatus()!=null&&!"0".equals(projectDeclare.getStatus())) {
			vo.setAuditInfo(projectDeclareService.getProjectAuditInfo(projectDeclare.getId()));
		}
		model.addAttribute("projectDeclareVo", vo);
		return "modules/project/projectDeclareFormView";
	}
	@RequestMapping(value = "delFile")
	@ResponseBody
	public JSONObject delFile( HttpServletRequest request) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "删除成功");
		String arrUrl= request.getParameter("arrUrl");
		String id= request.getParameter("id");
		try {
			if (id!=null&&!"null".equals(id))sysAttachmentService.delete(new SysAttachment(id));
			ftpService.del(arrUrl);
		} catch (Exception e) {
			js.put("ret", 0);
			js.put("msg", "删除失败");
		}
		return js;
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
	@RequestMapping(value = "save")
	@ResponseBody
	public JSONObject save(ProjectDeclareVo vo, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "保存成功");
		User user = UserUtils.getUser();
		if ("2".equals(user.getUserType())) {
			js.put("ret", 0);
			js.put("msg", "保存失败，导师不能申报项目");
			return js;
		}
		/*SysStudentExpansion  sse=sysStudentExpansionService.getByUserId(user.getId());
		if (sse==null) {
			js.put("ret", 0);
			js.put("msg", "保存失败，未找到学生信息，请联系管理员");
			return js;
		}
		if (checkIsGraduation(sse.getGraduation(), 5)) {
			js.put("ret", 0);
			js.put("msg", "保存失败，毕业五年的学生不能申报项目");
			return js;
		}*/
		if (StringUtil.isEmpty(vo.getProjectDeclare().getId())) {//新增
			List<Map<String, String>> alist=projectDeclareService.getValidProjectAnnounce();
			if (alist==null||alist.size()==0) {//没有有效的项目通告
				js.put("ret", 0);
				js.put("msg", "保存失败，不在项目申报有效时间内");
				return js;
			}else{
				vo.getProjectDeclare().setTemplateId(alist.get(0).get("id"));
			}
		}
		List<Map<String, String>> plist=projectDeclareService.getCurProjectInfo(user.getId());
		if (!StringUtil.isEmpty(vo.getProjectDeclare().getId())) {//修改
			ProjectDeclare pd=projectDeclareService.get(vo.getProjectDeclare().getId());
			if (pd!=null) {
				if ("1".equals(pd.getDelFlag())) {
					js.put("ret", 0);
					js.put("msg", "保存失败，项目已被删除");
					return js;
				}
			}
			if (plist!=null&&plist.size()!=0&&!vo.getProjectDeclare().getId().equals(plist.get(0).get("id"))) {
				js.put("ret", 0);
				js.put("msg", "保存失败，当前已存在未完成的项目");
				return js;
			}
		}else{//新增
			if (plist!=null&&plist.size()!=0) {
				js.put("ret", 0);
				js.put("msg", "保存失败，当前已存在未完成的项目");
				return js;
			}
		}
		if (StringUtil.isNotEmpty(vo.getProjectDeclare().getTeamId())) {
		List<Map<String, String>> tlist=projectDeclareService.getCurProjectInfoByTeam(vo.getProjectDeclare().getTeamId());
			if (!StringUtil.isEmpty(vo.getProjectDeclare().getId())) {//修改
				if (tlist!=null&&tlist.size()!=0&&!vo.getProjectDeclare().getId().equals(tlist.get(0).get("id"))) {
					js.put("ret", 0);
					js.put("msg", "保存失败，该团队已存在未完成的项目");
					return js;
				}
			}else{//新增
				if (tlist!=null&&tlist.size()!=0) {
					js.put("ret", 0);
					js.put("msg", "保存失败，该团队已存在未完成的项目");
					return js;
				}
			}
		}
		/*检查团队导师数量*/
		if (!StringUtil.isEmpty(vo.getProjectDeclare().getType())) {
			List<Map<String,String>> list2=projectDeclareService.findTeamTeacher(vo.getProjectDeclare().getTeamId());
			if ("1".equals(vo.getProjectDeclare().getType())
					||"2".equals(vo.getProjectDeclare().getType())) {//创新训练项目、创业训练项目）每个项目团队至少需要一个校园导师
				int tag=0;
				for(Map<String,String> map:list2) {
					if ("1".equals(map.get("ttv"))) {
						tag++;
						break;
					}
				}
				if (tag==0) {
					js.put("ret", 0);
					js.put("msg", "保存失败,创新训练项目、创业训练项目的团队至少需要一个校园导师");
					return js;
				}
			}
			if ("3".equals(vo.getProjectDeclare().getType())) {//创业实践项目必须要求有一位企业导师
				int tag=0;
				for(Map<String,String> map:list2) {
					if ("2".equals(map.get("ttv"))) {
						tag++;
						break;
					}
				}
				if (tag==0) {
					js.put("ret", 0);
					js.put("msg", "保存失败,创业实践项目的团队至少需要一个企业导师");
					return js;
				}
			}
		}
		/*检查团队导师数量*/
		ProjectDeclare projectDeclare=vo.getProjectDeclare();
		if (!beanValidator(model, projectDeclare)) {
			js.put("ret", 0);
			js.put("msg", "保存失败");
			return js;
		}
		String[] arrUrl= request.getParameterValues("arrUrl");
		String[] arrNames= request.getParameterValues("arrName");
		if (arrUrl!=null) {
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
			for(int i=0;i<arrUrl.length;i++) {
				Map<String,String> map=new HashMap<String,String>();
				map.put("arrUrl", arrUrl[i]);
				map.put("arrName", arrNames[i]);
				list.add(map);
			}
			vo.setFileInfo(list);
		}
		try {
			vo.getProjectDeclare().setCreateBy(user);
			projectDeclareService.saveProjectDeclareVo(vo);
			js.put("id", vo.getProjectDeclare().getId());
		} catch (ProjectNameDuplicateException e) {
			js.put("ret", 0);
			js.put("msg", "保存失败，已存在相同名称的项目");
		}
		return js;
	}
	@RequestMapping(value = "submit")
	@ResponseBody
	public JSONObject submit(ProjectDeclareVo vo, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "恭喜您，项目申报成功!");
		User user = UserUtils.getUser();
		if ("2".equals(user.getUserType())) {
			js.put("ret", 0);
			js.put("msg", "提交失败，导师不能申报项目");
			return js;
		}
		/*SysStudentExpansion  sse=sysStudentExpansionService.getByUserId(user.getId());
		if (sse==null) {
			js.put("ret", 0);
			js.put("msg", "提交失败，未找到学生信息，请联系管理员");
			return js;
		}
		if (checkIsGraduation(sse.getGraduation(), 5)) {
			js.put("ret", 0);
			js.put("msg", "提交失败，毕业五年的学生不能申报项目");
			return js;
		}*/
		if (!StringUtil.isEmpty(vo.getProjectDeclare().getTemplateId())) {//修改
			ProjectDeclare pd=projectDeclareService.get(vo.getProjectDeclare().getId());
			if (pd!=null) {
				if ("1".equals(pd.getDelFlag())) {
					js.put("ret", 0);
					js.put("msg", "提交失败，项目已被删除");
					return js;
				}
				if (!"0".equals(pd.getStatus())) {
					js.put("ret", 0);
					js.put("msg", "提交失败，项目已被提交");
					return js;
				}
			}
			ProjectAnnounce entity=new ProjectAnnounce();
			entity.setId(vo.getProjectDeclare().getTemplateId());
			entity.setDelFlag("0");
			entity.setProjectState("1");
			ProjectAnnounce p=projectAnnounceService.get(entity);
			Date now=new Date();
			if (p.getBeginDate()!=null&&p.getBeginDate().before(now)&&p.getEndDate()!=null&&p.getEndDate().after(now)) {
				
			}else{//不在申报有效期内
				js.put("ret", 0);
				js.put("msg", "提交失败，不在项目申报有效时间内");
				return js;
			}
		}else{//新增
			List<Map<String, String>> alist=projectDeclareService.getValidProjectAnnounce();
			if (alist==null||alist.size()==0) {//没有有效的项目通告
				js.put("ret", 0);
				js.put("msg", "提交失败，不在项目申报有效时间内");
				return js;
			}else{
				vo.getProjectDeclare().setTemplateId(alist.get(0).get("id"));
			}
		}
		if (StringUtil.isEmpty(vo.getProjectDeclare().getId())) {//新增
			List<Map<String, String>> plist=projectDeclareService.getCurProjectInfo(user.getId());
			if (plist!=null&&plist.size()!=0) {
				js.put("ret", 0);
				js.put("msg", "提交失败，当前已存在未完成的项目");
				return js;
			}
			List<Map<String, String>> tlist=projectDeclareService.getCurProjectInfoByTeam(vo.getProjectDeclare().getTeamId());
			if (tlist!=null&&tlist.size()!=0) {
				js.put("ret", 0);
				js.put("msg", "提交失败，该团队已存在未完成的项目");
				return js;
			}
		}
		/*检查团队导师数量*/
		if (StringUtil.isEmpty(vo.getProjectDeclare().getType())) {
			js.put("ret", 0);
			js.put("msg", "提交失败,请选择项目类别");
			return js;
		}
		List<Map<String,String>> list2=projectDeclareService.findTeamTeacher(vo.getProjectDeclare().getTeamId());
		if ("1".equals(vo.getProjectDeclare().getType())
				||"2".equals(vo.getProjectDeclare().getType())) {//创新训练项目、创业训练项目）每个项目团队至少需要一个校园导师
			int tag=0;
			for(Map<String,String> map:list2) {
				if ("1".equals(map.get("ttv"))) {
					tag++;
					break;
				}
			}
			if (tag==0) {
				js.put("ret", 0);
				js.put("msg", "提交失败,创新训练项目、创业训练项目的团队至少需要一个校园导师");
				return js;
			}
		}
		if ("3".equals(vo.getProjectDeclare().getType())) {//创业实践项目必须要求有一位企业导师
			int tag=0;
			for(Map<String,String> map:list2) {
				if ("2".equals(map.get("ttv"))) {
					tag++;
					break;
				}
			}
			if (tag==0) {
				js.put("ret", 0);
				js.put("msg", "提交失败,创业实践项目的团队至少需要一个企业导师");
				return js;
			}
		}
		/*检查团队导师数量*/
		ProjectDeclare projectDeclare=vo.getProjectDeclare();
		if (!beanValidator(model, projectDeclare)) {
			js.put("ret", 0);
			js.put("msg", "提交失败");
			return js;
		}
		String[] arrUrl= request.getParameterValues("arrUrl");
		String[] arrNames= request.getParameterValues("arrName");
		if (arrUrl!=null) {
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
			for(int i=0;i<arrUrl.length;i++) {
				Map<String,String> map=new HashMap<String,String>();
				map.put("arrUrl", arrUrl[i]);
				map.put("arrName", arrNames[i]);
				list.add(map);
			}
			vo.setFileInfo(list);
		}
		try {
			vo.getProjectDeclare().setCreateBy(user);
			projectDeclareService.submitProjectDeclareVo(vo);
		} catch (ProjectNameDuplicateException e) {
			js.put("ret", 0);
			js.put("msg", "提交失败，已存在相同名称的项目");
		}
		return js;
	}
	
	@RequestMapping(value = "delete")
	public String delete(ProjectDeclare projectDeclare, RedirectAttributes redirectAttributes) {
		projectDeclareService.delete(projectDeclare);
		addMessage(redirectAttributes, "删除项目申报成功");
		return "redirect:"+Global.getFrontPath()+"/project/projectDeclare/list?repage";
	}
	@ResponseBody
	@RequestMapping(value = "findTeamPerson")
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
		return list;
	}

}