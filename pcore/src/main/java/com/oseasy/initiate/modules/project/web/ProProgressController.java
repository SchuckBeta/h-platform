package com.hch.platform.pcore.modules.project.web;

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

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.project.entity.ProProgress;
import com.hch.platform.pcore.modules.project.service.ProProgressService;

/**
 * 国创项目进度表单Controller
 * @author 9527
 * @version 2017-03-29
 */
@Controller
@RequestMapping(value = "${frontPath}/project/proProgress")
public class ProProgressController extends BaseController {

	@Autowired
	private ProProgressService proProgressService;
	
	@ModelAttribute
	public ProProgress get(@RequestParam(required=false) String id) {
		ProProgress entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = proProgressService.get(id);
		}
		if (entity == null) {
			entity = new ProProgress();
		}
		return entity;
	}
	
	@RequiresPermissions("project:proProgress:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProProgress proProgress, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProProgress> page = proProgressService.findPage(new Page<ProProgress>(request, response), proProgress); 
		model.addAttribute("page", page);
		return "modules/project/proProgressList";
	}

	@RequiresPermissions("project:proProgress:view")
	@RequestMapping(value = "form")
	public String form(ProProgress proProgress, Model model) {
		model.addAttribute("proProgress", proProgress);
		return "modules/project/proProgressForm";
	}

	@RequiresPermissions("project:proProgress:edit")
	@RequestMapping(value = "save")
	public String save(ProProgress proProgress, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, proProgress)) {
			return form(proProgress, model);
		}
		proProgressService.save(proProgress);
		addMessage(redirectAttributes, "保存国创项目进度表单成功");
		return "redirect:"+Global.getAdminPath()+"/project/proProgress/?repage";
	}
	
	@RequiresPermissions("project:proProgress:edit")
	@RequestMapping(value = "delete")
	public String delete(ProProgress proProgress, RedirectAttributes redirectAttributes) {
		proProgressService.delete(proProgress);
		addMessage(redirectAttributes, "删除国创项目进度表单成功");
		return "redirect:"+Global.getAdminPath()+"/project/proProgress/?repage";
	}

}