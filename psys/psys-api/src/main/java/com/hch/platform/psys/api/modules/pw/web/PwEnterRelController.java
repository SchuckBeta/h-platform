package com.hch.platform.pcore.modules.pw.web;

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
import com.hch.platform.pcore.modules.actyw.tool.process.vo.FlowType;
import com.hch.platform.pcore.modules.pw.entity.PwEnterRel;
import com.hch.platform.pcore.modules.pw.service.PwEnterRelService;

/**
 * 入驻申报关联Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwEnterRel")
public class PwEnterRelController extends BaseController {

	@Autowired
	private PwEnterRelService pwEnterRelService;

	@ModelAttribute
	public PwEnterRel get(@RequestParam(required=false) String id) {
		PwEnterRel entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwEnterRelService.get(id);
		}
		if (entity == null){
			entity = new PwEnterRel();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwEnterRel:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwEnterRel pwEnterRel, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwEnterRel> page = pwEnterRelService.findPage(new Page<PwEnterRel>(request, response), pwEnterRel);
		model.addAttribute("page", page);
		model.addAttribute("flowTypes", FlowType.values());
		return "modules/pw/pwEnterRelList";
	}

	@RequiresPermissions("pw:pwEnterRel:view")
	@RequestMapping(value = "form")
	public String form(PwEnterRel pwEnterRel, Model model) {
		model.addAttribute("pwEnterRel", pwEnterRel);
		return "modules/pw/pwEnterRelForm";
	}

	@RequiresPermissions("pw:pwEnterRel:edit")
	@RequestMapping(value = "save")
	public String save(PwEnterRel pwEnterRel, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwEnterRel)){
			return form(pwEnterRel, model);
		}
		pwEnterRelService.save(pwEnterRel);
		addMessage(redirectAttributes, "保存入驻申报关联成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwEnterRel/?repage";
	}

	@RequiresPermissions("pw:pwEnterRel:edit")
	@RequestMapping(value = "delete")
	public String delete(PwEnterRel pwEnterRel, RedirectAttributes redirectAttributes) {
		pwEnterRelService.delete(pwEnterRel);
		addMessage(redirectAttributes, "删除入驻申报关联成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwEnterRel/?repage";
	}

}