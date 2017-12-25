package com.oseasy.initiate.modules.project.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.oseasy.initiate.modules.project.entity.ProjectPlan;
import com.oseasy.initiate.modules.project.service.ProjectPlanService;

/**
 * 项目任务Controller
 * @author 9527
 * @version 2017-03-11
 */
@Controller
@RequestMapping(value = "${frontPath}/project/projectPlan")
public class ProjectPlanController extends BaseController {

	@Autowired
	private ProjectPlanService projectPlanService;
	
	@ModelAttribute
	public ProjectPlan get(@RequestParam(required=false) String id) {
		ProjectPlan entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = projectPlanService.get(id);
		}
		if (entity == null) {
			entity = new ProjectPlan();
		}
		return entity;
	}
	
	@RequiresPermissions("project:projectPlan:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectPlan projectPlan, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProjectPlan> page = projectPlanService.findPage(new Page<ProjectPlan>(request, response), projectPlan); 
		model.addAttribute("page", page);
		return "modules/project/projectPlanList";
	}

	@RequiresPermissions("project:projectPlan:view")
	@RequestMapping(value = "form")
	public String form(ProjectPlan projectPlan, Model model) {
		model.addAttribute("projectPlan", projectPlan);
		return "modules/project/projectPlanForm";
	}

	@RequiresPermissions("project:projectPlan:edit")
	@RequestMapping(value = "save")
	public String save(ProjectPlan projectPlan, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectPlan)) {
			return form(projectPlan, model);
		}
		projectPlanService.save(projectPlan);
		addMessage(redirectAttributes, "保存项目任务成功");
		return "redirect:"+Global.getAdminPath()+"/project/projectPlan/?repage";
	}
	
	@RequiresPermissions("project:projectPlan:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectPlan projectPlan, RedirectAttributes redirectAttributes) {
		projectPlanService.delete(projectPlan);
		addMessage(redirectAttributes, "删除项目任务成功");
		return "redirect:"+Global.getAdminPath()+"/project/projectPlan/?repage";
	}

}