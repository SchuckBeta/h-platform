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

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGcert;
import com.oseasy.initiate.modules.actyw.service.ActYwGcertService;

/**
 * 业务节点证书Controller.
 * @author chenh
 * @version 2017-11-09
 */
@Controller
@RequestMapping(value = "${adminPath}/actyw/actYwGcert")
public class ActYwGcertController extends BaseController {

	@Autowired
	private ActYwGcertService actYwGcertService;

	@ModelAttribute
	public ActYwGcert get(@RequestParam(required=false) String id) {
		ActYwGcert entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = actYwGcertService.get(id);
		}
		if (entity == null){
			entity = new ActYwGcert();
		}
		return entity;
	}

	@RequiresPermissions("actyw:actYwGcert:view")
	@RequestMapping(value = {"list", ""})
	public String list(ActYwGcert actYwGcert, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ActYwGcert> page = actYwGcertService.findPage(new Page<ActYwGcert>(request, response), actYwGcert);
		model.addAttribute("page", page);
		return "modules/actyw/actYwGcertList";
	}

	@RequiresPermissions("actyw:actYwGcert:view")
	@RequestMapping(value = "form")
	public String form(ActYwGcert actYwGcert, Model model) {
		model.addAttribute("actYwGcert", actYwGcert);
		return "modules/actyw/actYwGcertForm";
	}

	@RequiresPermissions("actyw:actYwGcert:edit")
	@RequestMapping(value = "save")
	public String save(ActYwGcert actYwGcert, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actYwGcert)){
			return form(actYwGcert, model);
		}
		actYwGcertService.save(actYwGcert);
		addMessage(redirectAttributes, "保存业务节点证书成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwGcert/?repage";
	}

	@RequiresPermissions("actyw:actYwGcert:edit")
	@RequestMapping(value = "delete")
	public String delete(ActYwGcert actYwGcert, RedirectAttributes redirectAttributes) {
		actYwGcertService.delete(actYwGcert);
		addMessage(redirectAttributes, "删除业务节点证书成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwGcert/?repage";
	}

}