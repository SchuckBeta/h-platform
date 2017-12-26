package com.hch.platform.pcore.modules.pw.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.hch.platform.pcore.modules.pw.entity.PwCompany;
import com.hch.platform.pcore.modules.pw.entity.PwEnter;
import com.hch.platform.pcore.modules.pw.service.PwCompanyService;
import com.hch.platform.pcore.modules.pw.vo.PwEnterStatus;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

/**
 * 入驻企业Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwCompany")
public class PwCompanyController extends BaseController {

	@Autowired
	private PwCompanyService pwCompanyService;

	@ModelAttribute
	public PwCompany get(@RequestParam(required=false) String id) {
		PwCompany entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwCompanyService.get(id);
		}
		if (entity == null){
			entity = new PwCompany();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwCompany:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwCompany pwCompany, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwCompany> page = pwCompanyService.findPage(new Page<PwCompany>(request, response), pwCompany);
		model.addAttribute("page", page);
		return "modules/pw/pwCompanyList";
	}

	@RequiresPermissions("pw:pwCompany:view")
	@RequestMapping(value = "form")
	public String form(PwCompany pwCompany, Model model) {
		model.addAttribute("pwCompany", pwCompany);
		return "modules/pw/pwCompanyForm";
	}

	@RequiresPermissions("pw:pwCompany:edit")
	@RequestMapping(value = "save")
	public String save(PwCompany pwCompany, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwCompany)){
			return form(pwCompany, model);
		}
		pwCompanyService.save(pwCompany);
		addMessage(redirectAttributes, "保存入驻企业成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwCompany/?repage";
	}

	@RequiresPermissions("pw:pwCompany:edit")
	@RequestMapping(value = "delete")
	public String delete(PwCompany pwCompany, RedirectAttributes redirectAttributes) {
		pwCompanyService.delete(pwCompany);
		addMessage(redirectAttributes, "删除入驻企业成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwCompany/?repage";
	}
}