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
import com.hch.platform.pcore.modules.project.entity.ProSituation;
import com.hch.platform.pcore.modules.project.service.ProSituationService;

/**
 * 国创项目完成情况表单Controller
 * @author 9527
 * @version 2017-03-29
 */
@Controller
@RequestMapping(value = "${frontPath}/project/proSituation")
public class ProSituationController extends BaseController {

	@Autowired
	private ProSituationService proSituationService;
	
	@ModelAttribute
	public ProSituation get(@RequestParam(required=false) String id) {
		ProSituation entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = proSituationService.get(id);
		}
		if (entity == null) {
			entity = new ProSituation();
		}
		return entity;
	}
	
	@RequiresPermissions("project:proSituation:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProSituation proSituation, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProSituation> page = proSituationService.findPage(new Page<ProSituation>(request, response), proSituation); 
		model.addAttribute("page", page);
		return "modules/project/proSituationList";
	}

	@RequiresPermissions("project:proSituation:view")
	@RequestMapping(value = "form")
	public String form(ProSituation proSituation, Model model) {
		model.addAttribute("proSituation", proSituation);
		return "modules/project/proSituationForm";
	}

	@RequiresPermissions("project:proSituation:edit")
	@RequestMapping(value = "save")
	public String save(ProSituation proSituation, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, proSituation)) {
			return form(proSituation, model);
		}
		proSituationService.save(proSituation);
		addMessage(redirectAttributes, "保存国创项目完成情况表单成功");
		return "redirect:"+Global.getAdminPath()+"/project/proSituation/?repage";
	}
	
	@RequiresPermissions("project:proSituation:edit")
	@RequestMapping(value = "delete")
	public String delete(ProSituation proSituation, RedirectAttributes redirectAttributes) {
		proSituationService.delete(proSituation);
		addMessage(redirectAttributes, "删除国创项目完成情况表单成功");
		return "redirect:"+Global.getAdminPath()+"/project/proSituation/?repage";
	}

}