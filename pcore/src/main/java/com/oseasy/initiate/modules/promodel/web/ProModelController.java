package com.oseasy.initiate.modules.promodel.web;

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
import com.oseasy.initiate.modules.promodel.entity.ProModel;
import com.oseasy.initiate.modules.promodel.service.ProModelService;

/**
 * proModelController.
 * @author zy
 * @version 2017-07-13
 */
@Controller
@RequestMapping(value = "${adminPath}/promodel/proModel")
public class ProModelController extends BaseController {

	@Autowired
	private ProModelService proModelService;

	@ModelAttribute
	public ProModel get(@RequestParam(required=false) String id) {
		ProModel entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = proModelService.get(id);
		}
		if (entity == null){
			entity = new ProModel();
		}
		return entity;
	}

	@RequiresPermissions("promodel:proModel:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProModel proModel, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProModel> page = proModelService.findPage(new Page<ProModel>(request, response), proModel);
		model.addAttribute("page", page);
		return "modules/promodel/proModelList";
	}

	@RequiresPermissions("promodel:proModel:view")
	@RequestMapping(value = "form")
	public String form(ProModel proModel, Model model) {
		model.addAttribute("proModel", proModel);
		return "modules/promodel/proModelForm";
	}

	@RequiresPermissions("promodel:proModel:edit")
	@RequestMapping(value = "save")
	public String save(ProModel proModel, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, proModel)){
			return form(proModel, model);
		}
		proModelService.save(proModel);
		addMessage(redirectAttributes, "保存proModel成功");
		return "redirect:"+Global.getAdminPath()+"/promodel/proModel/?repage";
	}

	@RequiresPermissions("promodel:proModel:edit")
	@RequestMapping(value = "delete")
	public String delete(ProModel proModel, RedirectAttributes redirectAttributes) {
		proModelService.delete(proModel);
		addMessage(redirectAttributes, "删除proModel成功");
		return "redirect:"+Global.getAdminPath()+"/promodel/proModel/?repage";
	}

}