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
import com.oseasy.initiate.modules.sys.entity.TeacherKeyword;
import com.oseasy.initiate.modules.sys.service.TeacherKeywordService;

/**
 * teacherKeywordController.
 * @author zy
 * @version 2017-07-03
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/teacherkeyword/teacherKeyword")
public class TeacherKeywordController extends BaseController {

	@Autowired
	private TeacherKeywordService teacherKeywordService;

	@ModelAttribute
	public TeacherKeyword get(@RequestParam(required=false) String id) {
		TeacherKeyword entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = teacherKeywordService.get(id);
		}
		if (entity == null){
			entity = new TeacherKeyword();
		}
		return entity;
	}

	@RequiresPermissions("sys:teacherkeyword:teacherKeyword:view")
	@RequestMapping(value = {"list", ""})
	public String list(TeacherKeyword teacherKeyword, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TeacherKeyword> page = teacherKeywordService.findPage(new Page<TeacherKeyword>(request, response), teacherKeyword);
		model.addAttribute("page", page);
		return "modules/sys/teacherkeyword/teacherKeywordList";
	}

	@RequiresPermissions("sys:teacherkeyword:teacherKeyword:view")
	@RequestMapping(value = "form")
	public String form(TeacherKeyword teacherKeyword, Model model) {
		model.addAttribute("teacherKeyword", teacherKeyword);
		return "modules/sys/teacherkeyword/teacherKeywordForm";
	}

	@RequiresPermissions("sys:teacherkeyword:teacherKeyword:edit")
	@RequestMapping(value = "save")
	public String save(TeacherKeyword teacherKeyword, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, teacherKeyword)){
			return form(teacherKeyword, model);
		}
		teacherKeywordService.save(teacherKeyword);
		addMessage(redirectAttributes, "保存teacherKeyword成功");
		return "redirect:"+Global.getAdminPath()+"/sys/teacherkeyword/teacherKeyword/?repage";
	}

	@RequiresPermissions("sys:teacherkeyword:teacherKeyword:edit")
	@RequestMapping(value = "delete")
	public String delete(TeacherKeyword teacherKeyword, RedirectAttributes redirectAttributes) {
		teacherKeywordService.delete(teacherKeyword);
		addMessage(redirectAttributes, "删除teacherKeyword成功");
		return "redirect:"+Global.getAdminPath()+"/sys/teacherkeyword/teacherKeyword/?repage";
	}

}