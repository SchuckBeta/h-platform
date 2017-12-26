package com.hch.platform.pcore.modules.gcontesthots.web;

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
import com.hch.platform.pcore.modules.gcontesthots.entity.GcontestHotsKeyword;
import com.hch.platform.pcore.modules.gcontesthots.service.GcontestHotsKeywordService;

/**
 * 大赛热点关键字Controller.
 * @author 9527
 * @version 2017-07-12
 */
@Controller
@RequestMapping(value = "${adminPath}/gcontesthots/gcontestHotsKeyword")
public class GcontestHotsKeywordController extends BaseController {

	@Autowired
	private GcontestHotsKeywordService gcontestHotsKeywordService;

	@ModelAttribute
	public GcontestHotsKeyword get(@RequestParam(required=false) String id) {
		GcontestHotsKeyword entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = gcontestHotsKeywordService.get(id);
		}
		if (entity == null){
			entity = new GcontestHotsKeyword();
		}
		return entity;
	}

	@RequiresPermissions("gcontesthots:gcontestHotsKeyword:view")
	@RequestMapping(value = {"list", ""})
	public String list(GcontestHotsKeyword gcontestHotsKeyword, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<GcontestHotsKeyword> page = gcontestHotsKeywordService.findPage(new Page<GcontestHotsKeyword>(request, response), gcontestHotsKeyword);
		model.addAttribute("page", page);
		return "modules/gcontesthots/gcontestHotsKeywordList";
	}

	@RequiresPermissions("gcontesthots:gcontestHotsKeyword:view")
	@RequestMapping(value = "form")
	public String form(GcontestHotsKeyword gcontestHotsKeyword, Model model) {
		model.addAttribute("gcontestHotsKeyword", gcontestHotsKeyword);
		return "modules/gcontesthots/gcontestHotsKeywordForm";
	}

	@RequiresPermissions("gcontesthots:gcontestHotsKeyword:edit")
	@RequestMapping(value = "save")
	public String save(GcontestHotsKeyword gcontestHotsKeyword, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, gcontestHotsKeyword)){
			return form(gcontestHotsKeyword, model);
		}
		gcontestHotsKeywordService.save(gcontestHotsKeyword);
		addMessage(redirectAttributes, "保存大赛热点关键字成功");
		return "redirect:"+Global.getAdminPath()+"/gcontesthots/gcontestHotsKeyword/?repage";
	}

	@RequiresPermissions("gcontesthots:gcontestHotsKeyword:edit")
	@RequestMapping(value = "delete")
	public String delete(GcontestHotsKeyword gcontestHotsKeyword, RedirectAttributes redirectAttributes) {
		gcontestHotsKeywordService.delete(gcontestHotsKeyword);
		addMessage(redirectAttributes, "删除大赛热点关键字成功");
		return "redirect:"+Global.getAdminPath()+"/gcontesthots/gcontestHotsKeyword/?repage";
	}

}