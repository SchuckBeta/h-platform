package com.oseasy.initiate.modules.excellent.web;

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
import com.oseasy.initiate.modules.excellent.entity.ExcellentKeyword;
import com.oseasy.initiate.modules.excellent.service.ExcellentKeywordService;

/**
 * 优秀展示关键词Controller.
 * @author 9527
 * @version 2017-06-23
 */
@Controller
@RequestMapping(value = "${adminPath}/excellent/excellentKeyword")
public class ExcellentKeywordController extends BaseController {

	@Autowired
	private ExcellentKeywordService excellentKeywordService;

	@ModelAttribute
	public ExcellentKeyword get(@RequestParam(required=false) String id) {
		ExcellentKeyword entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = excellentKeywordService.get(id);
		}
		if (entity == null) {
			entity = new ExcellentKeyword();
		}
		return entity;
	}

	@RequiresPermissions("excellent:excellentKeyword:view")
	@RequestMapping(value = {"list", ""})
	public String list(ExcellentKeyword excellentKeyword, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ExcellentKeyword> page = excellentKeywordService.findPage(new Page<ExcellentKeyword>(request, response), excellentKeyword);
		model.addAttribute("page", page);
		return "modules/excellent/excellentKeywordList";
	}

	@RequiresPermissions("excellent:excellentKeyword:view")
	@RequestMapping(value = "form")
	public String form(ExcellentKeyword excellentKeyword, Model model) {
		model.addAttribute("excellentKeyword", excellentKeyword);
		return "modules/excellent/excellentKeywordForm";
	}

	@RequiresPermissions("excellent:excellentKeyword:edit")
	@RequestMapping(value = "save")
	public String save(ExcellentKeyword excellentKeyword, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, excellentKeyword)) {
			return form(excellentKeyword, model);
		}
		excellentKeywordService.save(excellentKeyword);
		addMessage(redirectAttributes, "保存优秀展示关键词成功");
		return "redirect:"+Global.getAdminPath()+"/excellent/excellentKeyword/?repage";
	}

	@RequiresPermissions("excellent:excellentKeyword:edit")
	@RequestMapping(value = "delete")
	public String delete(ExcellentKeyword excellentKeyword, RedirectAttributes redirectAttributes) {
		excellentKeywordService.delete(excellentKeyword);
		addMessage(redirectAttributes, "删除优秀展示关键词成功");
		return "redirect:"+Global.getAdminPath()+"/excellent/excellentKeyword/?repage";
	}

}