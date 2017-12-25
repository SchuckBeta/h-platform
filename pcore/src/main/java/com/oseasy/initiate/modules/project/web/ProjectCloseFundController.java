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
import com.oseasy.initiate.modules.project.entity.ProjectCloseFund;
import com.oseasy.initiate.modules.project.service.ProjectCloseFundService;

/**
 * project_close_fundController
 * @author zhangzheng
 * @version 2017-03-29
 */
@Controller
@RequestMapping(value = "${frontPath}/project/projectCloseFund")
public class ProjectCloseFundController extends BaseController {

	@Autowired
	private ProjectCloseFundService projectCloseFundService;
	
	@ModelAttribute
	public ProjectCloseFund get(@RequestParam(required=false) String id) {
		ProjectCloseFund entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = projectCloseFundService.get(id);
		}
		if (entity == null) {
			entity = new ProjectCloseFund();
		}
		return entity;
	}
	
	@RequiresPermissions("project:projectCloseFund:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectCloseFund projectCloseFund, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProjectCloseFund> page = projectCloseFundService.findPage(new Page<ProjectCloseFund>(request, response), projectCloseFund); 
		model.addAttribute("page", page);
		return "modules/project/projectCloseFundList";
	}

	@RequiresPermissions("project:projectCloseFund:view")
	@RequestMapping(value = "form")
	public String form(ProjectCloseFund projectCloseFund, Model model) {
		model.addAttribute("projectCloseFund", projectCloseFund);
		return "modules/project/projectCloseFundForm";
	}

	@RequiresPermissions("project:projectCloseFund:edit")
	@RequestMapping(value = "save")
	public String save(ProjectCloseFund projectCloseFund, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectCloseFund)) {
			return form(projectCloseFund, model);
		}
		projectCloseFundService.save(projectCloseFund);
		addMessage(redirectAttributes, "保存project_close_fund成功");
		return "redirect:"+Global.getAdminPath()+"/project/projectCloseFund/?repage";
	}
	
	@RequiresPermissions("project:projectCloseFund:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectCloseFund projectCloseFund, RedirectAttributes redirectAttributes) {
		projectCloseFundService.delete(projectCloseFund);
		addMessage(redirectAttributes, "删除project_close_fund成功");
		return "redirect:"+Global.getAdminPath()+"/project/projectCloseFund/?repage";
	}

}