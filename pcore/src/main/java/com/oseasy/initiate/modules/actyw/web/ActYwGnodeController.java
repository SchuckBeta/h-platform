package com.oseasy.initiate.modules.actyw.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.modules.actyw.tool.process.vo.*;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.service.SystemService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.config.SysJkey;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.service.ActYwFormService;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.service.ActYwGroupService;
import com.oseasy.initiate.modules.actyw.service.ActYwNodeService;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwEcmd;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRunner;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 项目流程节点组Controller
 * @author chenhao
 * @version 2017-05-23
 */
@Controller
@RequestMapping(value = "${adminPath}/actyw/actYwGnode")
public class ActYwGnodeController extends BaseController {

    @Autowired
    private ActYwGroupService actYwGroupService;
	@Autowired
	private ActYwGnodeService actYwGnodeService;
	@Autowired
	private ActYwNodeService actYwNodeService;
	@Autowired
	private ActYwFormService actYwFormService;
    @Autowired
   	private SystemService systemService;
    @Autowired
    private ActYwRunner runner;

	@ModelAttribute
	public ActYwGnode get(@RequestParam(required=false) String id) {
		ActYwGnode entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = actYwGnodeService.get(id);
		}
		if (entity == null) {
			entity = new ActYwGnode();
		}
		return entity;
	}

	@RequiresPermissions("actyw:actYwGnode:view")
	@RequestMapping(value = {"list", ""})
	public String list(ActYwGnode actYwGnode, String groupId, Boolean isYw, HttpServletRequest request, HttpServletResponse response, Model model) {
    List<ActYwGnode> sourcelist = Lists.newArrayList();
    List<ActYwGnode> list = Lists.newArrayList();
    List<ActYwGroup> actYwGroups = actYwGroupService.findList(new ActYwGroup());

    if (StringUtil.isNotEmpty(groupId)) {
        actYwGnode.setGroup(actYwGroupService.get(new ActYwGroup(groupId)));
    }else if ((actYwGnode.getGroup() == null) && actYwGroups.size() > 0) {
        actYwGnode.setGroup(actYwGroups.get(0));
    }

    ActYwGnode pactYwGnode = new ActYwGnode();
    pactYwGnode.setGroup(actYwGnode.getGroup());
    pactYwGnode.setIsShow(actYwGnode.getIsShow());

    isYw = ((isYw == null)?true:isYw);
    if(isYw){
      sourcelist = actYwGnodeService.findListByYw(pactYwGnode);
    }else{
      sourcelist = actYwGnodeService.findList(pactYwGnode);
    }
    ActYwGnode.sortList(list, sourcelist, ActYwGnode.getRootId(), true);

		model.addAttribute("list", list);
		model.addAttribute("isYw", isYw);
		model.addAttribute("actYwGroups", actYwGroups);
		return "modules/actyw/actYwGnodeList";
	}

    @RequiresPermissions("actyw:actYwGnode:view")
    @RequestMapping(value = {"/{groupId}/view"})
	public String view(@PathVariable String groupId, ActYwGnode actYwGnode, HttpServletRequest request, HttpServletResponse response, Model model) {
	    if (StringUtil.isEmpty(groupId)) {
            return "redirect:"+Global.getAdminPath()+"/actyw/actYwGnode/?repage";
	    }
	    List<ActYwGnode> sourcelist = Lists.newArrayList();
        List<ActYwGnode> list = Lists.newArrayList();

        actYwGnode.setGroup(actYwGroupService.get(new ActYwGroup(groupId)));
                sourcelist = actYwGnodeService.findListByYw(actYwGnode);
        ActYwGnode.sortList(list, sourcelist, ActYwGnode.getRootId(), true);
        model.addAttribute("list", list);


	    return "modules/actyw/actYwGnodeView";
	}

	@RequiresPermissions("actyw:actYwGnode:view")
	@RequestMapping(value = "form")
	public String form(ActYwGnode actYwGnode, Model model, RedirectAttributes redirectAttributes) {
	  if ((actYwGnode.getGroup() == null) || StringUtil.isEmpty(actYwGnode.getGroup().getId())) {
	    addMessage(redirectAttributes, "流程组为空，操作失败！");
	    return "redirect:"+Global.getAdminPath()+"/actyw/actYwGnode/?repage";
	  }

		if (actYwGnode.getParent()!=null && StringUtil.isNotBlank(actYwGnode.getParent().getId())) {
			actYwGnode.setParent(actYwGnodeService.get(actYwGnode.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtil.isBlank(actYwGnode.getId())) {
				ActYwGnode actYwGnodeChild = new ActYwGnode();
				actYwGnodeChild.setParent(new ActYwGnode(actYwGnode.getParent().getId()));
				List<ActYwGnode> list = actYwGnodeService.findList(actYwGnode);
				if (list.size() > 0) {
					actYwGnode.setSort(list.get(list.size()-1).getSort());
					if (actYwGnode.getSort() != null) {
						actYwGnode.setSort(actYwGnode.getSort() + 30);
					}
				}
			}
		}
		if (actYwGnode.getSort() == null) {
			actYwGnode.setSort(30);
		}

		/**
		 * 如果没有机构默认获取当前机构
		 */
    if (actYwGnode.getOffice() == null) {
       actYwGnode.setOffice(UserUtils.getAdminOffice());
    }

    List<ActYwGroup> actYwGroups = actYwGroupService.findList(new ActYwGroup());

    ActYwNode pactYwNode = new ActYwNode();
    if (actYwGnode.getParent() != null) {
      pactYwNode.setOffice(actYwGnode.getParent().getOffice());
      Integer lv = Integer.parseInt(actYwGnode.getParent().getNode().getLevel()) + 1;
      pactYwNode.setLevel(lv.toString());
    }else{
      pactYwNode.setOffice(actYwGnode.getOffice());
      pactYwNode.setLevel(RtSvl.RtLevelVal.RT_LV1);
    }
    List<ActYwNode> actYwNodes = actYwNodeService.findList(pactYwNode);

    ActYwForm pactYwForm = new ActYwForm();
    if (actYwGnode.getNode() != null) {
      pactYwForm.setType(actYwGnode.getNode().getType());
      pactYwForm.setProType(actYwGnode.getGroup().getType());
    }
    List<ActYwForm> actYwForms = actYwFormService.findList(pactYwForm);

    /**
     * 查询可以选择的业务节点.
     */
    ActYwGnode pactYwPpNnGnode = new ActYwGnode();
    pactYwPpNnGnode.setGroup(actYwGnode.getGroup());
    pactYwPpNnGnode.setTypefun(StenFuntype.SFT_SELECT.getVal());
    List<ActYwGnode> actYwPpNnGnodes = actYwGnodeService.findList(pactYwPpNnGnode);

    if (actYwGnode.getIsForm() == null) {
      actYwGnode.setIsForm(false);
    }
    List<Role> roleList = systemService.findAllRole();
    model.addAttribute("roleList",roleList);
    ActYwGnode rootNode = actYwGnodeService.get(SysIds.SYS_TREE_ROOT.getId());
    FormType[] formTypeEnums =FormType.values();
    model.addAttribute("formTypeEnums",formTypeEnums);
    model.addAttribute("actYwGroups", actYwGroups);
    model.addAttribute("rootNode", rootNode);
    model.addAttribute("actYwNodes", actYwNodes);
		model.addAttribute("actYwGnode", actYwGnode);
		model.addAttribute("actYwPpNnGnodes", actYwPpNnGnodes);
		model.addAttribute("actYwForms", actYwForms);
		return "modules/actyw/actYwGnodeForm";
	}

	@RequiresPermissions("actyw:actYwGnode:edit")
	@RequestMapping(value = "save")
	public String save(ActYwGnode actYwGnode, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actYwGnode)) {
			return form(actYwGnode, model, redirectAttributes);
		}

		if (actYwGnode == null) {
	    return "redirect:"+Global.getAdminPath()+"/actyw/actYwGnode/?repage";
    }

		if ((actYwGnode.getOffice() == null)) {
		  actYwGnode.setOffice(UserUtils.getAdminOffice());
		}

		ActYwRstatus result = actYwGnodeService.saveAuto(runner.setEngine(new ActYwEngineImpl(actYwGnodeService, actYwNodeService)), actYwGnode.getGroup(), actYwGnode);
    addMessage(redirectAttributes, result.getMsg());
    Map<String, Object> attributeValues = new HashMap<String, Object>();
    attributeValues.put("groupId", actYwGnode.getGroupId());
    redirectAttributes.addAllAttributes(attributeValues);
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwGnode/?repage";
	}

	@RequiresPermissions("actyw:actYwGnode:edit")
	@RequestMapping(value = "delete")
	public String delete(ActYwGnode actYwGnode, RedirectAttributes redirectAttributes) {
		actYwGnodeService.delete(actYwGnode);
		addMessage(redirectAttributes, "删除项目流程组成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwGnode/?repage";
	}


  /**
   * 批量修改排序
   */
  @RequiresPermissions("actyw:actYwGnode:edit")
  @RequestMapping(value = "updateSort")
  public String updateSort(String[] ids, Integer[] sorts, String groupId, RedirectAttributes redirectAttributes) {
    if (Global.isDemoMode()) {
      addMessage(redirectAttributes, "演示模式，不允许操作！");
      return "redirect:" + adminPath + "/actyw/actYwGnode";
    }
    for (int i = 0; i < ids.length; i++) {
      ActYwGnode gnode = new ActYwGnode(ids[i]);
      gnode.setSort(sorts[i]);
      actYwGnodeService.updateSort(gnode);
    }
    addMessage(redirectAttributes, "保存菜单排序成功!");
    if (StringUtil.isNotEmpty(groupId)) {
      Map<String, Object> attributeValues = new HashMap<String, Object>();
      attributeValues.put("groupId", groupId);
      redirectAttributes.addAllAttributes(attributeValues);
    }
    return "redirect:" + adminPath + "/actyw/actYwGnode/?repage";
  }


  /**
   * 获取机构JSON数据。
   * @param extId 排除的ID
   * @param isShowHide 是否显示
   * @param response
   * @return
   */
  @RequiresPermissions("user")
  @ResponseBody
  @RequestMapping(value = "treeData")
  public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, @RequestParam(required=false) String isShowHide, @RequestParam(required=false) Boolean isAll, HttpServletResponse response) {
    List<Map<String, Object>> mapList = Lists.newArrayList();
    List<ActYwGnode> list = actYwGnodeService.findAllList();
    for (int i=0; i<list.size(); i++) {
      ActYwGnode e = list.get(i);
      if (StringUtil.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)) {
        if ((isShowHide != null) && isShowHide.equals(Global.HIDE) && e.getIsShow()) {
          continue;
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", e.getId());
        map.put("pId", e.getParentId());
        map.put("name", e.getNode().getName());
        mapList.add(map);
      }
    }
    return mapList;
  }

}