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
import com.hch.platform.pcore.modules.pw.entity.PwBillPrecords;
import com.hch.platform.pcore.modules.pw.service.PwBillPrecordsService;

/**
 * 缴费记录Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwBillPrecords")
public class PwBillPrecordsController extends BaseController {

	@Autowired
	private PwBillPrecordsService pwBillPrecordsService;

	@ModelAttribute
	public PwBillPrecords get(@RequestParam(required=false) String id) {
		PwBillPrecords entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwBillPrecordsService.get(id);
		}
		if (entity == null){
			entity = new PwBillPrecords();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwBillPrecords:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwBillPrecords pwBillPrecords, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwBillPrecords> page = pwBillPrecordsService.findPage(new Page<PwBillPrecords>(request, response), pwBillPrecords);
		model.addAttribute("page", page);
		return "modules/pw/pwBillPrecordsList";
	}

	@RequiresPermissions("pw:pwBillPrecords:view")
	@RequestMapping(value = "form")
	public String form(PwBillPrecords pwBillPrecords, Model model) {
		model.addAttribute("pwBillPrecords", pwBillPrecords);
		return "modules/pw/pwBillPrecordsForm";
	}

	@RequiresPermissions("pw:pwBillPrecords:edit")
	@RequestMapping(value = "save")
	public String save(PwBillPrecords pwBillPrecords, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwBillPrecords)){
			return form(pwBillPrecords, model);
		}
		pwBillPrecordsService.save(pwBillPrecords);
		addMessage(redirectAttributes, "保存缴费记录成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwBillPrecords/?repage";
	}

	@RequiresPermissions("pw:pwBillPrecords:edit")
	@RequestMapping(value = "delete")
	public String delete(PwBillPrecords pwBillPrecords, RedirectAttributes redirectAttributes) {
		pwBillPrecordsService.delete(pwBillPrecords);
		addMessage(redirectAttributes, "删除缴费记录成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwBillPrecords/?repage";
	}

}