package com.oseasy.initiate.modules.auditstandard.web;

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
import com.oseasy.initiate.modules.auditstandard.entity.AuditStandardDetail;
import com.oseasy.initiate.modules.auditstandard.service.AuditStandardDetailService;

/**
 * 评审标准详情Controller.
 * @author 9527
 * @version 2017-07-28
 */
@Controller
@RequestMapping(value = "${adminPath}/auditstandard/auditStandardDetail")
public class AuditStandardDetailController extends BaseController {

	@Autowired
	private AuditStandardDetailService auditStandardDetailService;

	@ModelAttribute
	public AuditStandardDetail get(@RequestParam(required=false) String id) {
		AuditStandardDetail entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = auditStandardDetailService.get(id);
		}
		if (entity == null){
			entity = new AuditStandardDetail();
		}
		return entity;
	}

	@RequiresPermissions("auditstandard:auditStandardDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(AuditStandardDetail auditStandardDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AuditStandardDetail> page = auditStandardDetailService.findPage(new Page<AuditStandardDetail>(request, response), auditStandardDetail);
		model.addAttribute("page", page);
		return "modules/auditstandard/auditStandardDetailList";
	}

	@RequiresPermissions("auditstandard:auditStandardDetail:view")
	@RequestMapping(value = "form")
	public String form(AuditStandardDetail auditStandardDetail, Model model) {
		model.addAttribute("auditStandardDetail", auditStandardDetail);
		return "modules/auditstandard/auditStandardDetailForm";
	}

	@RequiresPermissions("auditstandard:auditStandardDetail:edit")
	@RequestMapping(value = "save")
	public String save(AuditStandardDetail auditStandardDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, auditStandardDetail)){
			return form(auditStandardDetail, model);
		}
		auditStandardDetailService.save(auditStandardDetail);
		addMessage(redirectAttributes, "保存评审标准详情成功");
		return "redirect:"+Global.getAdminPath()+"/auditstandard/auditStandardDetail/?repage";
	}

	@RequiresPermissions("auditstandard:auditStandardDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(AuditStandardDetail auditStandardDetail, RedirectAttributes redirectAttributes) {
		auditStandardDetailService.delete(auditStandardDetail);
		addMessage(redirectAttributes, "删除评审标准详情成功");
		return "redirect:"+Global.getAdminPath()+"/auditstandard/auditStandardDetail/?repage";
	}

}