package com.oseasy.initiate.modules.project.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.common.utils.FileUpUtils;
import com.oseasy.initiate.modules.attachment.enums.FileSourceEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.project.entity.*;
import com.oseasy.initiate.modules.project.enums.ProjectStatusEnum;
import com.oseasy.initiate.modules.project.service.*;
import com.oseasy.initiate.modules.sys.entity.SysStudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SysStudentExpansionService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.common.utils.StringUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * project_closeController
 * @author zhangzheng
 * @version 2017-03-29
 */
@Controller
@RequestMapping(value = "${frontPath}/project/projectClose")
public class ProjectCloseController extends BaseController {

	@Autowired
	private ProjectCloseService projectCloseService;

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
	ProSituationService proSituationService;

	@Autowired
	ProProgressService proProgressService;

	@Autowired
	ProjectCloseFundService projectCloseFundService;

	@Autowired
	ProjectCloseResultService projectCloseResultService;
	
	@ModelAttribute
	public ProjectClose get(@RequestParam(required=false) String id) {
		ProjectClose entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = projectCloseService.get(id);
		}
		if (entity == null) {
			entity = new ProjectClose();
		}
		return entity;
	}
	/**
	 * 创建结项报告报告
	 * 先查询项目相关信息
	 * @param projectId 项目id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="createClose")
	public String createClose(String projectId, Model model) {
		if (StringUtil.isNotBlank(projectId)) {
			ProjectDeclare projectDeclare = projectDeclareService.get(projectId);
			model.addAttribute("projectDeclare",projectDeclare);
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
			//组成项目组成员
			/*StringBuffer stuNames=new StringBuffer("");
			for(TeamUserRelation turStudent:turStudents) {
				String name=turStudent.getStudent().getName();
				stuNames.append(name+"/");
			}
			String teamList=stuNames.toString().substring(0,stuNames.toString().length()-1);
			model.addAttribute("teamList",teamList);*/


			//查找导师
			List<TeamUserRelation>  turTeachers=teamUserRelationService.getTeachers(tur1);
			model.addAttribute("turTeachers",turTeachers);
			//组成项目导师
			/*StringBuffer teaNames=new StringBuffer("");
			for (TeamUserRelation turTeacher:turTeachers) {
				String name=turTeacher.getTeacher().getName();
				teaNames.append(name+"/");
			}
			String teacher=teaNames.toString().substring(0,teaNames.toString().length()-1);
			model.addAttribute("teacher",teacher);*/

			//查找项目分工
			List<ProjectPlan> plans=projectPlanService.findListByProjectId(projectDeclare.getId());
			model.addAttribute("plans",plans);
		}

		return "modules/project/projectCloseForm";
	}

	@RequestMapping(value="edit")
    public String edit(ProjectClose projectClose,Model model) {
		String projectId=projectClose.getProjectId();
		if (StringUtil.isNotBlank(projectId)) {
			ProjectDeclare projectDeclare = projectDeclareService.get(projectId);
			model.addAttribute("projectDeclare",projectDeclare);
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
			//查找导师
			List<TeamUserRelation>  turTeachers=teamUserRelationService.getTeachers(tur1);
			model.addAttribute("turTeachers",turTeachers);

			//查找项目分工
			List<ProjectPlan> plans=projectPlanService.findListByProjectId(projectDeclare.getId());
			model.addAttribute("plans",plans);

			//查找组成员完成情况
			List<ProSituation> proSituationList=proSituationService.getByFid(projectClose.getId());
			model.addAttribute("proSituationList",proSituationList);

			//查找当前项目进度
			List<ProProgress> proProgressList=proProgressService.getByFid(projectClose.getId());
			model.addAttribute("proProgressList",proProgressList);

			//查找经费使用情况
			List<ProjectCloseFund> projectCloseFundList=projectCloseFundService.getByCloseId(projectClose.getId());
			model.addAttribute("projectCloseFundList",projectCloseFundList);

			//查找成果描述
			List<ProjectCloseResult> projectCloseResultList=projectCloseResultService.getByCloseId(projectClose.getId());
			model.addAttribute("projectCloseResultList",projectCloseResultList);

			//查找结项附件
			Map<String,String> map=new HashMap<String,String>();
			map.put("uid",projectClose.getProjectId());
			map.put("file_step", FileTypeEnum.S103.getValue());
			map.put("type",FileSourceEnum.S0.getValue());
			List<Map<String,String>> fileListMap=sysAttachmentService.getFileInfo(map);
			model.addAttribute("fileListMap",fileListMap);
		}

		User user= UserUtils.getUser();
		if (StringUtil.equals("2",user.getUserType())) {
			return "modules/project/projectCloseMasterEdit";
		}else{
			return "modules/project/projectCloseForm";
		}

	}


	@RequestMapping(value="view")
	public String view(ProjectClose projectClose, Model model) {
		String projectId=projectClose.getProjectId();
		if (StringUtil.isNotBlank(projectId)) {
			ProjectDeclare projectDeclare = projectDeclareService.get(projectId);
			model.addAttribute("projectDeclare",projectDeclare);
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
			//查找导师
			List<TeamUserRelation>  turTeachers=teamUserRelationService.getTeachers(tur1);
			model.addAttribute("turTeachers",turTeachers);

			//查找项目分工
			List<ProjectPlan> plans=projectPlanService.findListByProjectId(projectDeclare.getId());
			model.addAttribute("plans",plans);

			//查找组成员完成情况
			List<ProSituation> proSituationList=proSituationService.getByFid(projectClose.getId());
			model.addAttribute("proSituationList",proSituationList);

			//查找当前项目进度
			List<ProProgress> proProgressList=proProgressService.getByFid(projectClose.getId());
			model.addAttribute("proProgressList",proProgressList);

			//查找经费使用情况
			List<ProjectCloseFund> projectCloseFundList=projectCloseFundService.getByCloseId(projectClose.getId());
			model.addAttribute("projectCloseFundList",projectCloseFundList);

			//查找成果描述
			List<ProjectCloseResult> projectCloseResultList=projectCloseResultService.getByCloseId(projectClose.getId());
			model.addAttribute("projectCloseResultList",projectCloseResultList);

			//查找结项附件
			Map<String,String> map=new HashMap<String,String>();
			map.put("uid",projectClose.getProjectId());
			map.put("file_step", FileTypeEnum.S103.getValue());
			map.put("type",FileSourceEnum.S0.getValue());
			List<Map<String,String>> fileListMap=sysAttachmentService.getFileInfo(map);
			model.addAttribute("fileListMap",fileListMap);


		}
		return "modules/project/projectCloseView";
	}
	/**
	 * 保存结项报告（未实现），触发工作流（已完成）
	 *
	 */
	@RequestMapping(value = "save")
	public String save(ProjectClose projectClose,
					   	Model model,
					   HttpServletRequest request,
					   	RedirectAttributes redirectAttributes) {
		projectCloseService.save(projectClose);
		//附件处理
		String[] arrUrl= request.getParameterValues("arrUrl");
		String[] arrNames= request.getParameterValues("arrName");
		List<Map<String,String>> fileListMap = FileUpUtils.getFileListMap(arrUrl,arrNames);
		sysAttachmentService.saveList(fileListMap,
										FileSourceEnum.S0.getValue(),
										FileTypeEnum.S103.getValue(),
										projectClose.getProjectId());


		if (StringUtil.isNotBlank(projectClose.getProjectId())) {
			ProjectDeclare projectDeclare = projectDeclareService.get(projectClose.getProjectId());
			//判断当前状态是不是待提交结项报告
			if (StringUtil.equals(projectDeclare.getStatus(), "6")) {
				projectDeclareService.closeSave(projectDeclare);
			}
		}

		return "redirect:/f/project/projectDeclare/curProject";
	}

	@RequestMapping(value = "saveMaster")
	public String saveMaster(ProjectClose projectClose, Model model,
							 HttpServletRequest request) {
		projectClose.setSuggestDate(new Date());
		projectCloseService.saveSuggest(projectClose);
		return "redirect:/f/project/projectDeclare/curProject";
	}
	

	@RequestMapping(value = "delete")
	public String delete(ProjectClose projectClose, RedirectAttributes redirectAttributes) {
		projectCloseService.delete(projectClose);
		addMessage(redirectAttributes, "删除project_close成功");
		return "redirect:"+Global.getAdminPath()+"/project/projectClose/?repage";
	}

}