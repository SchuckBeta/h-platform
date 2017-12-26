package com.oseasy.initiate.modules.project.web.weekly;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.enums.FileStepEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.entity.weekly.ProjectWeekly;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.project.service.ProjectPlanService;
import com.oseasy.initiate.modules.project.service.weekly.ProjectWeeklyService;
import com.oseasy.initiate.modules.project.vo.ProjectWeeklyVo;
import com.oseasy.initiate.modules.promodel.entity.ProModel;
import com.oseasy.initiate.modules.promodel.service.ProModelService;
import com.oseasy.initiate.modules.sys.entity.SysStudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SysStudentExpansionService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

import net.sf.json.JSONObject;

/**
 * 项目周报Controller
 * @author 张正
 * @version 2017-03-29
 */
@Controller
@RequestMapping(value = "${frontPath}/project/weekly")
public class ProjectWeeklyController extends BaseController {

	@Autowired
	private ProjectWeeklyService projectWeeklyService;
	@Autowired
	ProjectDeclareService projectDeclareService;

	@Autowired
	SysStudentExpansionService sysStudentExpansionService;

	@Autowired
	TeamService teamService;

	@Autowired
	TeamUserRelationService teamUserRelationService;

	@Autowired
	ProjectPlanService projectPlanService;
	@Autowired
	SysAttachmentService sysAttachmentService;
	@Autowired
	ProModelService proModelService;
	@ModelAttribute
	public ProjectWeekly get(@RequestParam(value="id",required=false) String id,@RequestParam(value="projectId",required=false) String projectId) {
		ProjectWeekly entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = projectWeeklyService.get(id);
		}
		if (entity == null) {
			entity = new ProjectWeekly();
			entity.setProjectId(projectId);
		}
		return entity;
	}


    /**
	 * 创建周报
	 * 查询项目基本信息 （完成）
	 * 查询上周周报  （未做）
     */
	@RequestMapping(value = "createWeekly")
	public String createWeekly(ProjectWeekly projectWeekly, Model model) {
		if (StringUtil.isNotBlank(projectWeekly.getProjectId())) {
			String projectId=projectWeekly.getProjectId();
			ProjectWeeklyVo vo=new ProjectWeeklyVo();
			ProjectDeclare projectDeclare = projectDeclareService.get(projectId);
			model.addAttribute("projectDeclare",projectDeclare);
			User user= UserUtils.getUser();
			model.addAttribute("user",user);
			User cuser=null;
			if (StringUtil.isEmpty(projectWeekly.getId())) {
				cuser=user;
			}else{
				cuser= UserUtils.get(projectWeekly.getCreateBy().getId());
			}
			model.addAttribute("cuser",cuser);
			String duty="项目负责人";
			if (!StringUtil.equals(cuser.getId(),projectDeclare.getCreateBy().getId())) {
				duty="组成员";
			}
			model.addAttribute("duty",duty);

			//查找学生拓展信息
			SysStudentExpansion student=  sysStudentExpansionService.getByUserId(projectDeclare.getCreateBy().getId());
			model.addAttribute("student",student);

			//查找项目团队相关信息 projectDeclare.id
			Team team=teamService.get(projectDeclare.getTeamId());
			model.addAttribute("team",team);

			//查找学生
			TeamUserRelation tur1=new TeamUserRelation();
			tur1.setTeamId(projectDeclare.getTeamId());
			List<TeamUserRelation> turStudents=teamUserRelationService.getStudents(tur1);
			model.addAttribute("turStudents",turStudents);
			if (turStudents!=null&&turStudents.size()>0) {
				//组成项目组成员
				StringBuffer stuNames=new StringBuffer("");
				for(TeamUserRelation turStudent:turStudents) {
					String name=turStudent.getStudent().getName();
					stuNames.append(name+"/");
				}
				String teamList=stuNames.toString().substring(0,stuNames.toString().length()-1);
				model.addAttribute("teamList",teamList);
			}

			//查找导师
			List<TeamUserRelation>  turTeachers=teamUserRelationService.getTeachers(tur1);
			model.addAttribute("turTeachers",turTeachers);
			if (turTeachers!=null&&turTeachers.size()>0) {
			//组成项目导师
				StringBuffer teaNames=new StringBuffer("");
				for (TeamUserRelation turTeacher:turTeachers) {
					String name=turTeacher.getTeacher().getName();
					teaNames.append(name+"/");
				}
				String teacher=teaNames.toString().substring(0,teaNames.toString().length()-1);
				model.addAttribute("teacher",teacher);
			}

			ProjectWeekly lastpw=null;
			if (projectWeekly.getId()==null) {
				Map<String,String> map=new HashMap<String,String>();
				map.put("uid", user.getId());
				map.put("pid", projectId);
				lastpw=projectWeeklyService.getLast(map);
				if (lastpw!=null)projectWeekly.setLastId(lastpw.getId());
			}else{
				lastpw=projectWeeklyService.get(projectWeekly.getLastId());
			}
			Date currentDate=new Date();
			model.addAttribute("currentDate",currentDate);
			vo.setProjectWeekly(projectWeekly);
			vo.setLastpw(lastpw);
			SysAttachment sa=new SysAttachment();
			sa.setUid(projectWeekly.getId());
			sa.setFileStep(FileStepEnum.S101);
			sa.setType(FileTypeEnum.S0);
			vo.setFileInfo(sysAttachmentService.getFiles(sa));
			model.addAttribute("vo",vo);
			if (lastpw!=null&&lastpw.getEndDate()!=null) {
				model.addAttribute("minDate",DateUtil.formatDate(DateUtil.addDay(lastpw.getEndDate(), 1), "yyyy-MM-dd"));
			}
		}

		return "modules/project/weekly/projectWeeklyForm";
	}
	@RequestMapping(value = "createWeeklyPlus")
	public String createWeeklyPlus(ProjectWeekly projectWeekly, Model model) {
		if (StringUtil.isNotBlank(projectWeekly.getProjectId())) {
			String projectId=projectWeekly.getProjectId();
			ProjectWeeklyVo vo=new ProjectWeeklyVo();
			ProjectDeclare projectDeclare = new ProjectDeclare();
			ProModel pm=proModelService.get(projectId);
			projectDeclare.setCreateBy(pm.getCreateBy());
			projectDeclare.setTeamId(pm.getTeamId());
			projectDeclare.setId(pm.getId());
			projectDeclare.setName(pm.getpName());
			projectDeclare.setNumber(pm.getCompetitionNumber());
			projectDeclare.setCreateDate(pm.getCreateDate());
			model.addAttribute("projectDeclare",projectDeclare);
			User user= UserUtils.getUser();
			model.addAttribute("user",user);
			User cuser=null;
			if (StringUtil.isEmpty(projectWeekly.getId())) {
				cuser=user;
			}else{
				cuser= UserUtils.get(projectWeekly.getCreateBy().getId());
			}
			model.addAttribute("cuser",cuser);
			String duty="项目负责人";
			if (!StringUtil.equals(cuser.getId(),projectDeclare.getCreateBy().getId())) {
				duty="组成员";
			}
			model.addAttribute("duty",duty);

			//查找学生拓展信息
			SysStudentExpansion student=  sysStudentExpansionService.getByUserId(projectDeclare.getCreateBy().getId());
			model.addAttribute("student",student);

			//查找项目团队相关信息 projectDeclare.id
			Team team=teamService.get(projectDeclare.getTeamId());
			model.addAttribute("team",team);

			//查找学生
			TeamUserRelation tur1=new TeamUserRelation();
			tur1.setTeamId(projectDeclare.getTeamId());
			List<TeamUserRelation> turStudents=teamUserRelationService.getStudents(tur1);
			model.addAttribute("turStudents",turStudents);
			if (turStudents!=null&&turStudents.size()>0) {
				//组成项目组成员
				StringBuffer stuNames=new StringBuffer("");
				for(TeamUserRelation turStudent:turStudents) {
					String name=turStudent.getStudent().getName();
					stuNames.append(name+"/");
				}
				String teamList=stuNames.toString().substring(0,stuNames.toString().length()-1);
				model.addAttribute("teamList",teamList);
			}

			//查找导师
			List<TeamUserRelation>  turTeachers=teamUserRelationService.getTeachers(tur1);
			model.addAttribute("turTeachers",turTeachers);
			if (turTeachers!=null&&turTeachers.size()>0) {
			//组成项目导师
				StringBuffer teaNames=new StringBuffer("");
				for (TeamUserRelation turTeacher:turTeachers) {
					String name=turTeacher.getTeacher().getName();
					teaNames.append(name+"/");
				}
				String teacher=teaNames.toString().substring(0,teaNames.toString().length()-1);
				model.addAttribute("teacher",teacher);
			}

			ProjectWeekly lastpw=null;
			if (projectWeekly.getId()==null) {
				Map<String,String> map=new HashMap<String,String>();
				map.put("uid", user.getId());
				map.put("pid", projectId);
				lastpw=projectWeeklyService.getLast(map);
				if (lastpw!=null)projectWeekly.setLastId(lastpw.getId());
			}else{
				lastpw=projectWeeklyService.get(projectWeekly.getLastId());
			}
			Date currentDate=new Date();
			model.addAttribute("currentDate",currentDate);
			vo.setProjectWeekly(projectWeekly);
			vo.setLastpw(lastpw);
			SysAttachment sa=new SysAttachment();
			sa.setUid(projectWeekly.getId());
			sa.setFileStep(FileStepEnum.S101);
			sa.setType(FileTypeEnum.S0);
			vo.setFileInfo(sysAttachmentService.getFiles(sa));
			model.addAttribute("vo",vo);
			if (lastpw!=null&&lastpw.getEndDate()!=null) {
				model.addAttribute("minDate",DateUtil.formatDate(DateUtil.addDay(lastpw.getEndDate(), 1), "yyyy-MM-dd"));
			}
		}

		return "modules/project/weekly/projectWeeklyForm";
	}
	@RequestMapping(value = "submit")
	@ResponseBody
	public JSONObject submit(ProjectWeeklyVo vo, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "提交成功");
		try {
			projectWeeklyService.submitVo(vo);
		} catch (Exception e) {
			js.put("ret", 0);
			js.put("msg", "提交失败");
		}
		return js;
	}
	@RequestMapping(value = "save")
	@ResponseBody
	public JSONObject save(ProjectWeeklyVo vo, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "保存成功");
		try {
			projectWeeklyService.saveVo(vo);
		} catch (Exception e) {
			js.put("ret", 0);
			js.put("msg", "保存失败");
		}
		return js;
	}
	@RequestMapping(value = "view")
	public String view(ProjectWeekly projectWeekly, Model model) {
		String projectId=projectWeekly.getProjectId();
			ProjectWeeklyVo vo=new ProjectWeeklyVo();
			ProjectDeclare projectDeclare = projectDeclareService.get(projectId);
			model.addAttribute("projectDeclare",projectDeclare);
			User user= UserUtils.getUser();
			model.addAttribute("user",user);
			User cuser= UserUtils.get(projectWeekly.getCreateBy().getId());
			model.addAttribute("cuser",cuser);
			String duty="项目负责人";
			if (!StringUtil.equals(cuser.getId(),projectDeclare.getCreateBy().getId())) {
				duty="组成员";
			}
			model.addAttribute("duty",duty);

			//查找学生拓展信息
			SysStudentExpansion student=  sysStudentExpansionService.getByUserId(projectDeclare.getCreateBy().getId());
			model.addAttribute("student",student);

			//查找项目团队相关信息 projectDeclare.id
			Team team=teamService.get(projectDeclare.getTeamId());
			model.addAttribute("team",team);

			//查找学生
			TeamUserRelation tur1=new TeamUserRelation();
			tur1.setTeamId(projectDeclare.getTeamId());
			List<TeamUserRelation> turStudents=teamUserRelationService.getStudents(tur1);
			model.addAttribute("turStudents",turStudents);
			if (turStudents!=null&&turStudents.size()>0) {
				//组成项目组成员
				StringBuffer stuNames=new StringBuffer("");
				for(TeamUserRelation turStudent:turStudents) {
					String name=turStudent.getStudent().getName();
					stuNames.append(name+"/");
				}
				String teamList=stuNames.toString().substring(0,stuNames.toString().length()-1);
				model.addAttribute("teamList",teamList);
			}

			//查找导师
			List<TeamUserRelation>  turTeachers=teamUserRelationService.getTeachers(tur1);
			model.addAttribute("turTeachers",turTeachers);
			if (turTeachers!=null&&turTeachers.size()>0) {
			//组成项目导师
				StringBuffer teaNames=new StringBuffer("");
				for (TeamUserRelation turTeacher:turTeachers) {
					String name=turTeacher.getTeacher().getName();
					teaNames.append(name+"/");
				}
				String teacher=teaNames.toString().substring(0,teaNames.toString().length()-1);
				model.addAttribute("teacher",teacher);
			}

			ProjectWeekly lastpw=null;
			if (projectWeekly.getId()==null) {
				Map<String,String> map=new HashMap<String,String>();
				map.put("uid", user.getId());
				map.put("pid", projectId);
				lastpw=projectWeeklyService.getLast(map);
			}else{
				lastpw=projectWeeklyService.get(projectWeekly.getLastId());
			}
			Date currentDate=new Date();
			model.addAttribute("currentDate",currentDate);
			vo.setProjectWeekly(projectWeekly);
			vo.setLastpw(lastpw);
			SysAttachment sa=new SysAttachment();
			sa.setUid(projectWeekly.getId());
			sa.setFileStep(FileStepEnum.S101);
			sa.setType(FileTypeEnum.S0);
			vo.setFileInfo(sysAttachmentService.getFiles(sa));
			model.addAttribute("vo",vo);
			if (lastpw!=null&&lastpw.getEndDate()!=null) {
				model.addAttribute("minDate",DateUtil.formatDate(DateUtil.addDay(lastpw.getEndDate(), 1), "yyyy-MM-dd"));
			}
		return "modules/project/weekly/projectWeeklyView";
	}
	@RequestMapping(value = "viewPlus")
	public String viewPlus(ProjectWeekly projectWeekly, Model model) {
		String projectId=projectWeekly.getProjectId();
			ProjectWeeklyVo vo=new ProjectWeeklyVo();
			ProjectDeclare projectDeclare = new ProjectDeclare();
			ProModel pm=proModelService.get(projectId);
			projectDeclare.setCreateBy(pm.getCreateBy());
			projectDeclare.setTeamId(pm.getTeamId());
			projectDeclare.setId(pm.getId());
			projectDeclare.setName(pm.getpName());
			projectDeclare.setNumber(pm.getCompetitionNumber());
			projectDeclare.setCreateDate(pm.getCreateDate());
			model.addAttribute("projectDeclare",projectDeclare);
			User user= UserUtils.getUser();
			model.addAttribute("user",user);
			User cuser= UserUtils.get(projectWeekly.getCreateBy().getId());
			model.addAttribute("cuser",cuser);
			String duty="项目负责人";
			if (!StringUtil.equals(cuser.getId(),projectDeclare.getCreateBy().getId())) {
				duty="组成员";
			}
			model.addAttribute("duty",duty);

			//查找学生拓展信息
			SysStudentExpansion student=  sysStudentExpansionService.getByUserId(projectDeclare.getCreateBy().getId());
			model.addAttribute("student",student);

			//查找项目团队相关信息 projectDeclare.id
			Team team=teamService.get(projectDeclare.getTeamId());
			model.addAttribute("team",team);

			//查找学生
			TeamUserRelation tur1=new TeamUserRelation();
			tur1.setTeamId(projectDeclare.getTeamId());
			List<TeamUserRelation> turStudents=teamUserRelationService.getStudents(tur1);
			model.addAttribute("turStudents",turStudents);
			if (turStudents!=null&&turStudents.size()>0) {
				//组成项目组成员
				StringBuffer stuNames=new StringBuffer("");
				for(TeamUserRelation turStudent:turStudents) {
					String name=turStudent.getStudent().getName();
					stuNames.append(name+"/");
				}
				String teamList=stuNames.toString().substring(0,stuNames.toString().length()-1);
				model.addAttribute("teamList",teamList);
			}

			//查找导师
			List<TeamUserRelation>  turTeachers=teamUserRelationService.getTeachers(tur1);
			model.addAttribute("turTeachers",turTeachers);
			if (turTeachers!=null&&turTeachers.size()>0) {
			//组成项目导师
				StringBuffer teaNames=new StringBuffer("");
				for (TeamUserRelation turTeacher:turTeachers) {
					String name=turTeacher.getTeacher().getName();
					teaNames.append(name+"/");
				}
				String teacher=teaNames.toString().substring(0,teaNames.toString().length()-1);
				model.addAttribute("teacher",teacher);
			}

			ProjectWeekly lastpw=null;
			if (projectWeekly.getId()==null) {
				Map<String,String> map=new HashMap<String,String>();
				map.put("uid", user.getId());
				map.put("pid", projectId);
				lastpw=projectWeeklyService.getLast(map);
			}else{
				lastpw=projectWeeklyService.get(projectWeekly.getLastId());
			}
			Date currentDate=new Date();
			model.addAttribute("currentDate",currentDate);
			vo.setProjectWeekly(projectWeekly);
			vo.setLastpw(lastpw);
			SysAttachment sa=new SysAttachment();
			sa.setUid(projectWeekly.getId());
			sa.setFileStep(FileStepEnum.S101);
			sa.setType(FileTypeEnum.S0);
			vo.setFileInfo(sysAttachmentService.getFiles(sa));
			model.addAttribute("vo",vo);
			if (lastpw!=null&&lastpw.getEndDate()!=null) {
				model.addAttribute("minDate",DateUtil.formatDate(DateUtil.addDay(lastpw.getEndDate(), 1), "yyyy-MM-dd"));
			}
		return "modules/project/weekly/projectWeeklyView";
	}
	

	@RequestMapping(value = "delete")
	public String delete(ProjectWeekly projectWeekly, RedirectAttributes redirectAttributes) {
		projectWeeklyService.delete(projectWeekly);
		return "redirect:"+Global.getAdminPath()+"/project/weekly/projectWeekly/?repage";
	}

}