package com.oseasy.initiate.modules.sys.web;

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
import com.oseasy.initiate.modules.sys.entity.SysNo;
import com.oseasy.initiate.modules.sys.service.SysNoService;

/**
 * 系统编号Controller
 * @author chenh
 * @version 2017-05-05
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/sysNo")
public class SysNoController extends BaseController {

	@Autowired
	private SysNoService sysNoService;
	
	@ModelAttribute
	public SysNo get(@RequestParam(required=false) String id) {
		SysNo entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = sysNoService.get(id);
		}
		if (entity == null) {
			entity = new SysNo();
		}
		return entity;
	}
	
	@RequiresPermissions("sys:sysNo:view")
	@RequestMapping(value = {"list", ""})
	public String list(SysNo sysNo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<SysNo> page = sysNoService.findPage(new Page<SysNo>(request, response), sysNo); 
		model.addAttribute("page", page);
		return "modules/sys/sysNoList";
	}

	@RequiresPermissions("sys:sysNo:view")
	@RequestMapping(value = "form")
	public String form(SysNo sysNo, Model model) {
		model.addAttribute("sysNo", sysNo);
		return "modules/sys/sysNoForm";
	}

	@RequiresPermissions("sys:sysNo:edit")
	@RequestMapping(value = "save")
	public String save(SysNo sysNo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, sysNo)) {
			return form(sysNo, model);
		}
		sysNoService.save(sysNo);
		addMessage(redirectAttributes, "保存系统编号成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysNo/?repage";
	}
	
	@RequiresPermissions("sys:sysNo:edit")
	@RequestMapping(value = "delete")
	public String delete(SysNo sysNo, RedirectAttributes redirectAttributes) {
		sysNoService.delete(sysNo);
		addMessage(redirectAttributes, "删除系统编号成功");
		return "redirect:"+Global.getAdminPath()+"/sys/sysNo/?repage";
	}

}