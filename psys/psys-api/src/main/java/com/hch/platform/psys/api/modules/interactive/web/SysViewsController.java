package com.hch.platform.pcore.modules.interactive.web;

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
import com.hch.platform.pcore.modules.interactive.entity.SysViews;
import com.hch.platform.pcore.modules.interactive.service.SysViewsService;

/**
 * 浏览表Controller.
 * @author 9527
 * @version 2017-06-30
 */
@Controller
@RequestMapping(value = "${adminPath}/interactive/sysViews")
public class SysViewsController extends BaseController {

	@Autowired
	private SysViewsService sysViewsService;

	@ModelAttribute
	public SysViews get(@RequestParam(required=false) String id) {
		SysViews entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = sysViewsService.get(id);
		}
		if (entity == null){
			entity = new SysViews();
		}
		return entity;
	}

	@RequiresPermissions("interactive:sysViews:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysViews sysViews, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysViews> page = sysViewsService.findPage(new Page<SysViews>(request, response), sysViews);
		model.addAttribute("page", page);
		return "modules/interactive/sysViewsList";
	}

	@RequiresPermissions("interactive:sysViews:view")
	@RequestMapping(value = "form")
	public String form(SysViews sysViews, Model model) {
		model.addAttribute("sysViews", sysViews);
		return "modules/interactive/sysViewsForm";
	}

	@RequiresPermissions("interactive:sysViews:edit")
	@RequestMapping(value = "save")
	public String save(SysViews sysViews, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysViews)){
			return form(sysViews, model);
		}
		sysViewsService.save(sysViews);
		addMessage(redirectAttributes, "保存浏览表成功");
		return "redirect:"+Global.getAdminPath()+"/interactive/sysViews/?repage";
	}

	@RequiresPermissions("interactive:sysViews:edit")
	@RequestMapping(value = "delete")
	public String delete(SysViews sysViews, RedirectAttributes redirectAttributes) {
		sysViewsService.delete(sysViews);
		addMessage(redirectAttributes, "删除浏览表成功");
		return "redirect:"+Global.getAdminPath()+"/interactive/sysViews/?repage";
	}

}