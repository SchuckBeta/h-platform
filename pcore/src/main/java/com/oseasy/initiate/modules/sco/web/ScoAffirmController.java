package com.oseasy.initiate.modules.sco.web;

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
import com.oseasy.initiate.modules.sco.entity.ScoAffirm;
import com.oseasy.initiate.modules.sco.service.ScoAffirmService;

/**
 * 创新、创业、素质学分认定表Controller.
 * @author chenhao
 * @version 2017-07-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sco/scoAffirm")
public class ScoAffirmController extends BaseController {

	@Autowired
	private ScoAffirmService scoAffirmService;

	@ModelAttribute
	public ScoAffirm get(@RequestParam(required=false) String id) {
		ScoAffirm entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = scoAffirmService.get(id);
		}
		if (entity == null){
			entity = new ScoAffirm();
		}
		return entity;
	}

	@RequiresPermissions("sco:scoAffirm:view")
	@RequestMapping(value = {"list", ""})
	public String list(ScoAffirm scoAffirm, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ScoAffirm> page = scoAffirmService.findPage(new Page<ScoAffirm>(request, response), scoAffirm);
		model.addAttribute("page", page);
		return "modules/sco/scoAffirmList";
	}

	@RequiresPermissions("sco:scoAffirm:view")
	@RequestMapping(value = "form")
	public String form(ScoAffirm scoAffirm, Model model) {
		model.addAttribute("scoAffirm", scoAffirm);
		return "modules/sco/scoAffirmForm";
	}

	@RequiresPermissions("sco:scoAffirm:edit")
	@RequestMapping(value = "save")
	public String save(ScoAffirm scoAffirm, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, scoAffirm)){
			return form(scoAffirm, model);
		}
		scoAffirmService.save(scoAffirm);
		addMessage(redirectAttributes, "保存创新、创业、素质学分认定表成功");
		return "redirect:"+Global.getAdminPath()+"/sco/scoAffirm/?repage";
	}

	@RequiresPermissions("sco:scoAffirm:edit")
	@RequestMapping(value = "delete")
	public String delete(ScoAffirm scoAffirm, RedirectAttributes redirectAttributes) {
		scoAffirmService.delete(scoAffirm);
		addMessage(redirectAttributes, "删除创新、创业、素质学分认定表成功");
		return "redirect:"+Global.getAdminPath()+"/sco/scoAffirm/?repage";
	}

}