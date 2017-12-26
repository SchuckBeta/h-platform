//package com.oseasy.initiate.modules.pw.web;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.shiro.authz.annotation.RequiresPermissions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.oseasy.initiate.common.config.Global;
//import com.oseasy.initiate.common.persistence.Page;
//import com.oseasy.initiate.common.web.BaseController;
//import com.oseasy.initiate.common.utils.StringUtil;
//import com.oseasy.initiate.modules.pw.entity.PwFassetsnoRule;
//import com.oseasy.initiate.modules.pw.service.PwFassetsnoRuleService;
//
///**
// * 固定资产编号规则Controller.
// * @author pw
// * @version 2017-12-05
// */
//@Controller
//@RequestMapping(value = "${adminPath}/pw/pwFassetsnoRule")
//public class PwFassetsnoRuleController extends BaseController {
//
//	@Autowired
//	private PwFassetsnoRuleService pwFassetsnoRuleService;
//
//	@ModelAttribute
//	public PwFassetsnoRule get(@RequestParam(required=false) String id) {
//		PwFassetsnoRule entity = null;
//		if (StringUtil.isNotBlank(id)){
//			entity = pwFassetsnoRuleService.get(id);
//		}
//		if (entity == null){
//			entity = new PwFassetsnoRule();
//		}
//		return entity;
//	}
//
//	@RequiresPermissions("pw:pwFassetsnoRule:view")
//	@RequestMapping(value = {"list", ""})
//	public String list(PwFassetsnoRule pwFassetsnoRule, HttpServletRequest request, HttpServletResponse response, Model model) {
//		Page<PwFassetsnoRule> page = pwFassetsnoRuleService.findPage(new Page<PwFassetsnoRule>(request, response), pwFassetsnoRule);
//		model.addAttribute("page", page);
//		return "modules/pw/pwFassetsnoRuleList";
//	}
//
//	@RequiresPermissions("pw:pwFassetsnoRule:view")
//	@RequestMapping(value = "form")
//	public String form(PwFassetsnoRule pwFassetsnoRule, Model model) {
//		model.addAttribute("pwFassetsnoRule", pwFassetsnoRule);
//		return "modules/pw/pwFassetsnoRuleForm";
//	}
//
//	@RequiresPermissions("pw:pwFassetsnoRule:edit")
//	@RequestMapping(value = "save")
//	public String save(PwFassetsnoRule pwFassetsnoRule, Model model, RedirectAttributes redirectAttributes) {
//		if (!beanValidator(model, pwFassetsnoRule)){
//			return form(pwFassetsnoRule, model);
//		}
//		pwFassetsnoRuleService.save(pwFassetsnoRule);
//		addMessage(redirectAttributes, "保存固定资产编号规则成功");
//		return "redirect:"+Global.getAdminPath()+"/pw/pwFassetsnoRule/?repage";
//	}
//
//	@RequiresPermissions("pw:pwFassetsnoRule:edit")
//	@RequestMapping(value = "delete")
//	public String delete(PwFassetsnoRule pwFassetsnoRule, RedirectAttributes redirectAttributes) {
//		pwFassetsnoRuleService.delete(pwFassetsnoRule);
//		addMessage(redirectAttributes, "删除固定资产编号规则成功");
//		return "redirect:"+Global.getAdminPath()+"/pw/pwFassetsnoRule/?repage";
//	}
//
//}