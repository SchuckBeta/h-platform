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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.sco.entity.ScoApply;
import com.oseasy.initiate.modules.sco.entity.ScoCourse;
import com.oseasy.initiate.modules.sco.service.ScoApplyService;
import com.oseasy.initiate.modules.sco.service.ScoCourseService;

/**
 * 课程Controller.
 * @author 张正
 * @version 2017-07-13
 */
@Controller
@RequestMapping(value = "${adminPath}/sco/scoCourse")
public class ScoCourseController extends BaseController {

	@Autowired
	private ScoCourseService scoCourseService;
	@Autowired
	private ScoApplyService scoApplyService;
	@ModelAttribute
	public ScoCourse get(@RequestParam(required=false) String id) {
		ScoCourse entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = scoCourseService.get(id);
		}
		if (entity == null){
			entity = new ScoCourse();
		}
		return entity;
	}

	@RequiresPermissions("scocourse:scoCourse:view")
	@RequestMapping(value = {"list", ""})
	public String list(ScoCourse scoCourse, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ScoCourse> page = scoCourseService.findPage(new Page<ScoCourse>(request, response), scoCourse);
		model.addAttribute("page", page);
		return "modules/sco/scoCourseList";
	}

	@RequiresPermissions("scocourse:scoCourse:view")
	@RequestMapping(value = "form")
	public String form(ScoCourse scoCourse, Model model) {
		model.addAttribute("scoCourse", scoCourse);
		return "modules/sco/scoCourseForm";
	}

	@RequiresPermissions("scocourse:scoCourse:edit")
	@RequestMapping(value = "save")
	public String save(ScoCourse scoCourse, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, scoCourse)){
			return form(scoCourse, model);
		}
		scoCourseService.save(scoCourse);
		addMessage(redirectAttributes, "保存课程成功");
		return "redirect:"+Global.getAdminPath()+"/sco/scoCourse/?repage";
	}

	@RequiresPermissions("scocourse:scoCourse:edit")
	@RequestMapping(value = "delete")
	public String delete(ScoCourse scoCourse, RedirectAttributes redirectAttributes) {
		ScoApply scoApply=new ScoApply();
		scoApply.setCourseId(scoCourse.getId());
		if(!scoApplyService.findList(scoApply).isEmpty()){
			addMessage(redirectAttributes, "删除失败，该课程已被选择认定");
		}else{
			scoCourseService.delete(scoCourse);
			addMessage(redirectAttributes, "删除课程成功");
		}
		return "redirect:"+Global.getAdminPath()+"/sco/scoCourse/?repage";
	}
	@RequestMapping(value = "checkCode")
	@ResponseBody
	public boolean checkCode(String id,String code) {
		if (scoCourseService.checkCode(id, code)>0) {
			return false;
		}
		return true;
	}
	@RequestMapping(value = "checkName")
	@ResponseBody
	public boolean checkName(String id,String name) {
		if (scoCourseService.checkName(id, name)>0) {
			return false;
		}
		return true;
	}
}