package com.oseasy.initiate.modules.actyw.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.proproject.service.ProProjectService;
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
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.service.ActYwFormService;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FormType;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目流程表单Controller
 * @author chenhao
 * @version 2017-05-23
 */
@Controller
@RequestMapping(value = "${adminPath}/actyw/actYwForm")
public class ActYwFormController extends BaseController {

	@Autowired
	private ActYwFormService actYwFormService;

	@Autowired
	private ProProjectService proProjectService;

	@ModelAttribute
	public ActYwForm get(@RequestParam(required=false) String id) {
		ActYwForm entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = actYwFormService.get(id);
		}
		if (entity == null) {
			entity = new ActYwForm();
		}
		return entity;
	}

	@RequiresPermissions("actyw:actYwForm:view")
	@RequestMapping(value = {"list", ""})
	public String list(ActYwForm actYwForm, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ActYwForm> page = actYwFormService.findPage(new Page<ActYwForm>(request, response), actYwForm);
		List<ProProject> proProjectList=new ArrayList<ProProject>();
			proProjectList=proProjectService.findList(new ProProject());
			model.addAttribute("proProjects",proProjectList);

			FormType[] formTypeEnums =FormType.values();
			model.addAttribute("formTypeEnums",formTypeEnums);
		model.addAttribute("page", page);
		return "modules/actyw/actYwFormList";
	}

	@RequiresPermissions("actyw:actYwForm:view")
	@RequestMapping(value = "form")
	public String form(ActYwForm actYwForm, Model model) {
		model.addAttribute("actYwForm", actYwForm);
		//大赛类别
		List<ProProject> proProjectList=new ArrayList<ProProject>();
		proProjectList=proProjectService.findList(new ProProject());
		model.addAttribute("proProjects",proProjectList);

		FormType[] formTypeEnums =FormType.values();
		model.addAttribute("formTypeEnums",formTypeEnums);
		return "modules/actyw/actYwFormForm";
	}

	@RequiresPermissions("actyw:actYwForm:edit")
	@RequestMapping(value = "save")
	public String save(ActYwForm actYwForm, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actYwForm)) {
			return form(actYwForm, model);
		}
		if (actYwForm.getProType()!=null && actYwForm.getType()!=null) {
			actYwForm.setPath("/template/form/"+actYwForm.getProType()+"/"+actYwForm.getType());
		}

		actYwFormService.save(actYwForm);
		addMessage(redirectAttributes, "保存流程表单成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwForm/?repage";
	}

	@RequiresPermissions("actyw:actYwForm:edit")
	@RequestMapping(value = "delete")
	public String delete(ActYwForm actYwForm, RedirectAttributes redirectAttributes) {
		actYwFormService.delete(actYwForm);
		addMessage(redirectAttributes, "删除流程表单成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwForm/?repage";
	}

}