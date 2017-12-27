package com.hch.platform.pcore.modules.project.web;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.utils.FileUpUtils;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.attachment.enums.FileTypeEnum;
import com.hch.platform.pcore.modules.attachment.enums.FileStepEnum;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.project.entity.*;
import com.hch.platform.pcore.modules.project.service.*;
import com.hch.platform.pcore.modules.sys.entity.SysStudentExpansion;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.SysStudentExpansionService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.entity.TeamUserRelation;
import com.hch.platform.pcore.modules.team.service.TeamService;
import com.hch.platform.pcore.modules.team.service.TeamUserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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
@RequestMapping(value = "${adminPath}/project/projectClose")
public class BackProjectCloseController extends BaseController {

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
			List<Map<String,String>> turStudents=projectDeclareService.findTeamStudentFromTUH(projectDeclare.getTeamId(),projectDeclare.getId());
			model.addAttribute("turStudents",turStudents);
			//查找导师
			List<Map<String,String>> turTeachers=projectDeclareService.findTeamTeacherFromTUH(projectDeclare.getTeamId(),projectDeclare.getId());
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
			SysAttachment sa=new SysAttachment();
			sa.setUid(projectClose.getProjectId());
			sa.setFileStep(FileStepEnum.S103);
			sa.setType(FileTypeEnum.S0);
			List<SysAttachment> fileListMap =  sysAttachmentService.getFiles(sa);
			model.addAttribute("fileListMap",fileListMap);


		}
		return "modules/project/projectCloseBackView";
	}

}