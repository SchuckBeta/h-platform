package com.oseasy.initiate.modules.actyw.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.service.SystemService;
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
import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.service.ActYwFormService;
import com.oseasy.initiate.modules.actyw.service.ActYwNodeService;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwSten;

/**
 * 项目流程节点Controller
 * @author chenhao
 * @version 2017-05-23
 */
@Controller
@RequestMapping(value = "${adminPath}/actyw/actYwNode")
public class ActYwNodeController extends BaseController {

	@Autowired
	private ActYwNodeService actYwNodeService;
	@Autowired
	private ActYwFormService actYwFormService;
	@Autowired
	private SystemService systemService;

	@ModelAttribute
	public ActYwNode get(@RequestParam(required=false) String id) {
		ActYwNode entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = actYwNodeService.get(id);
		}
		if (entity == null) {
			entity = new ActYwNode();
		}
		return entity;
	}

	@RequiresPermissions("actyw:actYwNode:view")
	@RequestMapping(value = {"list", ""})
	public String list(ActYwNode actYwNode, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ActYwNode> page = actYwNodeService.findPage(new Page<ActYwNode>(request, response), actYwNode);
		model.addAttribute("page", page);
		return "modules/actyw/actYwNodeList";
	}

	@RequiresPermissions("actyw:actYwNode:view")
	@RequestMapping(value = "form")
	public String form(ActYwNode actYwNode, Model model) {
		model.addAttribute("actYwStenStencils", ActYwSten.genActYwStenStencils());

    if(StringUtil.isEmpty(actYwNode.getIsGroup())){
      actYwNode.setIsGroup("1");
    }

    ActYwForm pactYwForm = new ActYwForm();
    List<ActYwForm> actYwForms = actYwFormService.findList(pactYwForm);
    model.addAttribute("actYwForms", actYwForms);

    List<Role>  roleList = systemService.findAllRole();
    model.addAttribute("roleList",roleList);
		model.addAttribute("actYwNode", actYwNode);
		return "modules/actyw/actYwNodeForm";
	}

	@RequiresPermissions("actyw:actYwNode:edit")
	@RequestMapping(value = "save")
	public String save(ActYwNode actYwNode, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actYwNode)) {
			return form(actYwNode, model);
		}
		actYwNodeService.save(actYwNode);
		addMessage(redirectAttributes, "保存流程节点成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwNode/?repage";
	}

	@RequiresPermissions("actyw:actYwNode:edit")
	@RequestMapping(value = "delete")
	public String delete(ActYwNode actYwNode, RedirectAttributes redirectAttributes) {
		actYwNodeService.delete(actYwNode);
		addMessage(redirectAttributes, "删除流程节点成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwNode/?repage";
	}

}