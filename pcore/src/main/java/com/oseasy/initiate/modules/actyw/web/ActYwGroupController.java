package com.oseasy.initiate.modules.actyw.web;

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
import com.oseasy.initiate.common.config.SysJkey;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.service.ActYwGroupService;
import com.oseasy.initiate.modules.actyw.service.ActYwNodeService;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRunner;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;

import java.util.List;

/**
 * 项目流程组Controller
 * @author chenhao
 * @version 2017-05-23
 */
@Controller
@RequestMapping(value = "${adminPath}/actyw/actYwGroup")
public class ActYwGroupController extends BaseController {

	@Autowired
	private ActYwGroupService actYwGroupService;
	@Autowired
	private ActYwGnodeService actYwGnodeService;
	@Autowired
	private ActYwNodeService actYwNodeService;
	@Autowired
	private SystemService systemService;

	@Autowired
	private ActYwRunner runner;

	@ModelAttribute
	public ActYwGroup get(@RequestParam(required=false) String id) {
		ActYwGroup entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = actYwGroupService.get(id);
		}
		if (entity == null) {
			entity = new ActYwGroup();
		}
		return entity;
	}

	@RequiresPermissions("actyw:actYwGroup:view")
	@RequestMapping(value = {"list", ""})
	public String list(ActYwGroup actYwGroup, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ActYwGroup> page = actYwGroupService.findPage(new Page<ActYwGroup>(request, response), actYwGroup);
		model.addAttribute("page", page);
		return "modules/actyw/actYwGroupList";
	}

	@RequiresPermissions("actyw:actYwGroup:view")
	@RequestMapping(value = "form")
	public String form(ActYwGroup actYwGroup, Model model) {
		model.addAttribute("actYwGroup", actYwGroup);
		return "modules/actyw/actYwGroupForm";
	}

	@RequiresPermissions("actyw:actYwGroup:edit")
	@RequestMapping(value = "save")
	public String save(ActYwGroup actYwGroup, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actYwGroup)) {
			return form(actYwGroup, model);
		}

		Boolean isNew = actYwGroup.getIsNewRecord();
		actYwGroupService.save(actYwGroup);

		ActYwRstatus result = actYwGnodeService.saveAuto(runner.setEngine(new ActYwEngineImpl(actYwGnodeService, actYwNodeService)), actYwGroup, null);
    if (result.getStatus()) {
      addMessage(redirectAttributes, "流程组["+actYwGroup.getName()+"] "+result.getMsg());
    }else{
      if (isNew) {
        actYwGroupService.delete(actYwGroup);
      }
      addMessage(redirectAttributes, "流程组["+actYwGroup.getName()+"] 保存失败");
    }

		return "redirect:"+Global.getAdminPath()+"/actyw/actYwGroup/?repage";
	}

	@RequiresPermissions("actyw:actYwGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(ActYwGroup actYwGroup, RedirectAttributes redirectAttributes) {
		actYwGroupService.delete(actYwGroup);
		addMessage(redirectAttributes, "删除项目流程组成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwGroup/?repage";
	}

}