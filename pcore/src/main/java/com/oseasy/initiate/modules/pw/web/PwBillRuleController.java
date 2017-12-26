package com.oseasy.initiate.modules.pw.web;

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
import com.oseasy.initiate.modules.pw.entity.PwBillRule;
import com.oseasy.initiate.modules.pw.service.PwBillRuleService;

/**
 * 费用规则Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwBillRule")
public class PwBillRuleController extends BaseController {

	@Autowired
	private PwBillRuleService pwBillRuleService;

	@ModelAttribute
	public PwBillRule get(@RequestParam(required=false) String id) {
		PwBillRule entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwBillRuleService.get(id);
		}
		if (entity == null){
			entity = new PwBillRule();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwBillRule:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwBillRule pwBillRule, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwBillRule> page = pwBillRuleService.findPage(new Page<PwBillRule>(request, response), pwBillRule);
		model.addAttribute("page", page);
		return "modules/pw/pwBillRuleList";
	}

	@RequiresPermissions("pw:pwBillRule:view")
	@RequestMapping(value = "form")
	public String form(PwBillRule pwBillRule, Model model) {
		model.addAttribute("pwBillRule", pwBillRule);
		return "modules/pw/pwBillRuleForm";
	}

	@RequiresPermissions("pw:pwBillRule:edit")
	@RequestMapping(value = "save")
	public String save(PwBillRule pwBillRule, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwBillRule)){
			return form(pwBillRule, model);
		}
		pwBillRuleService.save(pwBillRule);
		addMessage(redirectAttributes, "保存费用规则成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwBillRule/?repage";
	}

	@RequiresPermissions("pw:pwBillRule:edit")
	@RequestMapping(value = "delete")
	public String delete(PwBillRule pwBillRule, RedirectAttributes redirectAttributes) {
		pwBillRuleService.delete(pwBillRule);
		addMessage(redirectAttributes, "删除费用规则成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwBillRule/?repage";
	}

}