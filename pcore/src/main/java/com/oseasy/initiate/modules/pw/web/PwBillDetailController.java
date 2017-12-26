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
import com.hch.platform.pcore.modules.pw.entity.PwBillDetail;
import com.hch.platform.pcore.modules.pw.service.PwBillDetailService;

/**
 * 账单明细Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwBillDetail")
public class PwBillDetailController extends BaseController {

	@Autowired
	private PwBillDetailService pwBillDetailService;

	@ModelAttribute
	public PwBillDetail get(@RequestParam(required=false) String id) {
		PwBillDetail entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwBillDetailService.get(id);
		}
		if (entity == null){
			entity = new PwBillDetail();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwBillDetail:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwBillDetail pwBillDetail, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwBillDetail> page = pwBillDetailService.findPage(new Page<PwBillDetail>(request, response), pwBillDetail);
		model.addAttribute("page", page);
		return "modules/pw/pwBillDetailList";
	}

	@RequiresPermissions("pw:pwBillDetail:view")
	@RequestMapping(value = "form")
	public String form(PwBillDetail pwBillDetail, Model model) {
		model.addAttribute("pwBillDetail", pwBillDetail);
		return "modules/pw/pwBillDetailForm";
	}

	@RequiresPermissions("pw:pwBillDetail:edit")
	@RequestMapping(value = "save")
	public String save(PwBillDetail pwBillDetail, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwBillDetail)){
			return form(pwBillDetail, model);
		}
		pwBillDetailService.save(pwBillDetail);
		addMessage(redirectAttributes, "保存账单明细成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwBillDetail/?repage";
	}

	@RequiresPermissions("pw:pwBillDetail:edit")
	@RequestMapping(value = "delete")
	public String delete(PwBillDetail pwBillDetail, RedirectAttributes redirectAttributes) {
		pwBillDetailService.delete(pwBillDetail);
		addMessage(redirectAttributes, "删除账单明细成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwBillDetail/?repage";
	}

}