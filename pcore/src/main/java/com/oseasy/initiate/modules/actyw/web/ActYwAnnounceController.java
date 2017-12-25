package com.oseasy.initiate.modules.actyw.web;

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
import com.oseasy.initiate.modules.actyw.entity.ActYwAnnounce;
import com.oseasy.initiate.modules.actyw.service.ActYwAnnounceService;

/**
 * 项目流程通告Controller
 * @author chenhao
 * @version 2017-05-23
 */
@Controller
@RequestMapping(value = "${adminPath}/actyw/actYwAnnounce")
public class ActYwAnnounceController extends BaseController {

	@Autowired
	private ActYwAnnounceService actYwAnnounceService;
	
	@ModelAttribute
	public ActYwAnnounce get(@RequestParam(required=false) String id) {
		ActYwAnnounce entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = actYwAnnounceService.get(id);
		}
		if (entity == null) {
			entity = new ActYwAnnounce();
		}
		return entity;
	}
	
	@RequiresPermissions("actyw:actYwAnnounce:view")
	@RequestMapping(value = {"list", ""})
	public String list(ActYwAnnounce actYwAnnounce, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ActYwAnnounce> page = actYwAnnounceService.findPage(new Page<ActYwAnnounce>(request, response), actYwAnnounce); 
		model.addAttribute("page", page);
		return "modules/actyw/actYwAnnounceList";
	}

	@RequiresPermissions("actyw:actYwAnnounce:view")
	@RequestMapping(value = "form")
	public String form(ActYwAnnounce actYwAnnounce, Model model) {
		model.addAttribute("actYwAnnounce", actYwAnnounce);
		return "modules/actyw/actYwAnnounceForm";
	}

	@RequiresPermissions("actyw:actYwAnnounce:edit")
	@RequestMapping(value = "save")
	public String save(ActYwAnnounce actYwAnnounce, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actYwAnnounce)) {
			return form(actYwAnnounce, model);
		}
		actYwAnnounceService.save(actYwAnnounce);
		addMessage(redirectAttributes, "保存项目通告成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwAnnounce/?repage";
	}
	
	@RequiresPermissions("actyw:actYwAnnounce:edit")
	@RequestMapping(value = "delete")
	public String delete(ActYwAnnounce actYwAnnounce, RedirectAttributes redirectAttributes) {
		actYwAnnounceService.delete(actYwAnnounce);
		addMessage(redirectAttributes, "删除项目通告成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwAnnounce/?repage";
	}

}