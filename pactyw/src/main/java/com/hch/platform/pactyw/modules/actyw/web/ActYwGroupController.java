package com.oseasy.initiate.modules.actyw.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.tool.SysNoType;
import com.oseasy.initiate.modules.sys.tool.SysNodeTool;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hch.platform.pconfig.common.Global;
import com.oseasy.initiate.common.config.SysJkey;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.service.ActYwGroupService;
import com.oseasy.initiate.modules.actyw.service.ActYwNodeService;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRunner;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;

import java.util.List;
import java.util.Map;

/**
 * 自定义流程Controller
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

	@RequestMapping(value = "ajaxDeploy")
	public String ajaxDeploy(ActYwGroup actYwGroup, Model model, RedirectAttributes redirectAttributes) {
    if(StringUtil.isNotEmpty(actYwGroup.getId()) && StringUtil.isNotEmpty(actYwGroup.getStatus())){
      ActYwGroup newActYwGroup = actYwGroupService.get(actYwGroup.getId());
      newActYwGroup.setStatus(actYwGroup.getStatus());
      return save(newActYwGroup, model, redirectAttributes);
    }

    return "redirect:"+Global.getAdminPath()+"/actyw/actYwGroup/?repage";
	}

	@RequiresPermissions("actyw:actYwGroup:edit")
	@RequestMapping(value = "save")
	public String save(ActYwGroup actYwGroup, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actYwGroup)) {
			return form(actYwGroup, model);
		}

		if(StringUtil.isEmpty(actYwGroup.getKeyss())){
		  actYwGroup.setKeyss(SysNodeTool.genByKeyss(SysNoType.NO_FLOW));
		}

	  Boolean isTrue = actYwGroupService.validKeyss(actYwGroup.getKeyss(), actYwGroup.getIsNewRecord());
    if(!isTrue){
      addMessage(redirectAttributes, "自定义流程惟一标识 ["+actYwGroup.getKeyss()+"] 已经存在!");
      return form(actYwGroup, model);
    }

    if(StringUtil.isNotEmpty(actYwGroup.getStatus()) && (actYwGroup.getStatus()).equals(ActYwGroup.GROUP_DEPLOY_1)){
      isTrue = actYwGnodeService.validateProcess(actYwGroup.getId());
    }

    if(!isTrue){
      addMessage(redirectAttributes, "流程不合法,没有流程节点!");
      return "redirect:"+Global.getAdminPath()+"/actyw/actYwGroup/?repage";
    }

		actYwGroupService.save(actYwGroup);

	  ActYwRstatus<ActYwGnode> result = actYwGnodeService.saveAuto(ActYwRunner.IS_ROOT, runner.setEngine(new ActYwEngineImpl(actYwGnodeService, actYwNodeService)), actYwGroup, null);
    if (result.getStatus()) {
      addMessage(redirectAttributes, "自定义流程["+actYwGroup.getName()+"] "+result.getMsg());
    }else{
      actYwGroupService.delete(actYwGroup);
      addMessage(redirectAttributes, "自定义流程["+actYwGroup.getName()+"] 保存失败");
    }

		return "redirect:"+Global.getAdminPath()+"/actyw/actYwGroup/?repage";
	}

	@RequiresPermissions("actyw:actYwGroup:edit")
	@RequestMapping(value = "delete")
	public String delete(ActYwGroup actYwGroup, RedirectAttributes redirectAttributes) {
		actYwGroupService.delete(actYwGroup);
		addMessage(redirectAttributes, "删除自定义流程成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwGroup/?repage";
	}
}