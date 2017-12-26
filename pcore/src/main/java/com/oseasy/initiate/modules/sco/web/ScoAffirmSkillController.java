package com.hch.platform.pcore.modules.sco.web;

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
import com.hch.platform.pcore.modules.sco.entity.ScoAffirmSkill;
import com.hch.platform.pcore.modules.sco.service.ScoAffirmSkillService;

/**
 * 技能学分认定Controller.
 * @author chenhao
 * @version 2017-07-18
 */
@Controller
@RequestMapping(value = "${adminPath}/sco/scoAffirmSkill")
public class ScoAffirmSkillController extends BaseController {

	@Autowired
	private ScoAffirmSkillService scoAffirmSkillService;

	@ModelAttribute
	public ScoAffirmSkill get(@RequestParam(required=false) String id) {
		ScoAffirmSkill entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = scoAffirmSkillService.get(id);
		}
		if (entity == null){
			entity = new ScoAffirmSkill();
		}
		return entity;
	}

	@RequiresPermissions("sco:scoAffirmSkill:view")
	@RequestMapping(value = {"list", ""})
	public String list(ScoAffirmSkill scoAffirmSkill, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ScoAffirmSkill> page = scoAffirmSkillService.findPage(new Page<ScoAffirmSkill>(request, response), scoAffirmSkill);
		model.addAttribute("page", page);
		return "modules/sco/scoAffirmSkillList";
	}

	@RequiresPermissions("sco:scoAffirmSkill:view")
	@RequestMapping(value = "form")
	public String form(ScoAffirmSkill scoAffirmSkill, Model model) {
		model.addAttribute("scoAffirmSkill", scoAffirmSkill);
		return "modules/sco/scoAffirmSkillForm";
	}

	@RequiresPermissions("sco:scoAffirmSkill:edit")
	@RequestMapping(value = "save")
	public String save(ScoAffirmSkill scoAffirmSkill, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, scoAffirmSkill)){
			return form(scoAffirmSkill, model);
		}
		scoAffirmSkillService.save(scoAffirmSkill);
		addMessage(redirectAttributes, "保存技能学分认定成功");
		return "redirect:"+Global.getAdminPath()+"/sco/scoAffirmSkill/?repage";
	}

	@RequiresPermissions("sco:scoAffirmSkill:edit")
	@RequestMapping(value = "delete")
	public String delete(ScoAffirmSkill scoAffirmSkill, RedirectAttributes redirectAttributes) {
		scoAffirmSkillService.delete(scoAffirmSkill);
		addMessage(redirectAttributes, "删除技能学分认定成功");
		return "redirect:"+Global.getAdminPath()+"/sco/scoAffirmSkill/?repage";
	}

}