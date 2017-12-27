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
import com.hch.platform.pcore.modules.pw.entity.PwCosMaterialPrecords;
import com.hch.platform.pcore.modules.pw.service.PwCosMaterialPrecordsService;

/**
 * 耗材购买记录Controller.
 * @author chenh
 * @version 2017-11-26
 */
@Controller
@RequestMapping(value = "${adminPath}/pw/pwCosMaterialPrecords")
public class PwCosMaterialPrecordsController extends BaseController {

	@Autowired
	private PwCosMaterialPrecordsService pwCosMaterialPrecordsService;

	@ModelAttribute
	public PwCosMaterialPrecords get(@RequestParam(required=false) String id) {
		PwCosMaterialPrecords entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = pwCosMaterialPrecordsService.get(id);
		}
		if (entity == null){
			entity = new PwCosMaterialPrecords();
		}
		return entity;
	}

	@RequiresPermissions("pw:pwCosMaterialPrecords:view")
	@RequestMapping(value = {"list", ""})
	public String list(PwCosMaterialPrecords pwCosMaterialPrecords, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<PwCosMaterialPrecords> page = pwCosMaterialPrecordsService.findPage(new Page<PwCosMaterialPrecords>(request, response), pwCosMaterialPrecords);
		model.addAttribute("page", page);
		return "modules/pw/pwCosMaterialPrecordsList";
	}

	@RequiresPermissions("pw:pwCosMaterialPrecords:view")
	@RequestMapping(value = "form")
	public String form(PwCosMaterialPrecords pwCosMaterialPrecords, Model model) {
		model.addAttribute("pwCosMaterialPrecords", pwCosMaterialPrecords);
		return "modules/pw/pwCosMaterialPrecordsForm";
	}

	@RequiresPermissions("pw:pwCosMaterialPrecords:edit")
	@RequestMapping(value = "save")
	public String save(PwCosMaterialPrecords pwCosMaterialPrecords, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, pwCosMaterialPrecords)){
			return form(pwCosMaterialPrecords, model);
		}
		pwCosMaterialPrecordsService.save(pwCosMaterialPrecords);
		addMessage(redirectAttributes, "保存耗材购买记录成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwCosMaterialPrecords/?repage";
	}

	@RequiresPermissions("pw:pwCosMaterialPrecords:edit")
	@RequestMapping(value = "delete")
	public String delete(PwCosMaterialPrecords pwCosMaterialPrecords, RedirectAttributes redirectAttributes) {
		pwCosMaterialPrecordsService.delete(pwCosMaterialPrecords);
		addMessage(redirectAttributes, "删除耗材购买记录成功");
		return "redirect:"+Global.getAdminPath()+"/pw/pwCosMaterialPrecords/?repage";
	}

}