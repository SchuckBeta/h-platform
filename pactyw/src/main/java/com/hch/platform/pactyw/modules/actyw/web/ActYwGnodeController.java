package com.oseasy.initiate.modules.actyw.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hch.platform.pconfig.common.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.service.ActYwFormService;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.service.ActYwGroupService;
import com.oseasy.initiate.modules.actyw.service.ActYwNodeService;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRunner;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeView;
import com.oseasy.initiate.modules.actyw.tool.process.vo.NodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenFuntype;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 项目流程Controller
 * @author chenhao
 * @version 2017-05-23
 */
@Controller
@RequestMapping(value = "${adminPath}/actyw/actYwGnode")
public class ActYwGnodeController extends BaseController {

  @Autowired
  private ActTaskService actTaskService;
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
	@Autowired
	RepositoryService repositoryService;
	@Autowired
	RuntimeService runtimeService;

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


	@RequestMapping(value = {"list", ""})
	public String list(ActYwGnode actYwGnode, String groupId, Boolean isYw, HttpServletRequest request, HttpServletResponse response, Model model) {
    List<ActYwGnode> sourcelist = Lists.newArrayList();
    List<ActYwGnode> list = Lists.newArrayList();
    List<ActYwGroup> actYwGroups = actYwGroupService.findListByNoDeploy();

    if (StringUtil.isNotEmpty(groupId)) {
        actYwGnode.setGroup(actYwGroupService.get(new ActYwGroup(groupId)));
    }else if ((actYwGnode.getGroup() == null) && actYwGroups.size() > 0) {
        actYwGnode.setGroup(actYwGroups.get(0));
    }

    /**
     * 判断流程组不能为空，否则可能出现没有流程组(流程发布了)，列表有数据.
     */
    if (actYwGnode.getGroup() != null) {
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
    }

		model.addAttribute("list", list);
		model.addAttribute("isYw", isYw);
		model.addAttribute("actYwGroups", actYwGroups);
		return "modules/actyw/actYwGnodeList";
	}

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

	@RequestMapping(value = "form")
	public String form(ActYwGnode actYwGnode, Model model, RedirectAttributes redirectAttributes) {
	  design(actYwGnode, model, redirectAttributes);
		return "modules/actyw/actYwGnodeForm";
	}

	@RequestMapping(value = "formProp")
	public String formProp(ActYwGnode actYwGnode, Model model, RedirectAttributes redirectAttributes) {
	  design(actYwGnode, model, redirectAttributes);
	  return "modules/actyw/actYwGnodeFormProp";
	}

	/**
   * 根据流程实例ID定位流程跟踪(自定义的项目、大赛使用).
	 * @param groupId 流程ID
	 * @param proInsId 流程实例ID
	 * @param model 模型
	 * @param request 请求
	 * @return
	 */
  @RequestMapping(value = "designView")
  public String designView(String groupId, String proInsId, String grade, Model model, HttpServletRequest request) {
      if(StringUtil.isNotEmpty(grade)){
        model.addAttribute("grade", grade);
      }

      if(StringUtil.isNotEmpty(groupId)){
        model.addAttribute("group", actYwGroupService.get(groupId));
        model.addAttribute("groupId", groupId);
      }

      if(StringUtil.isNotEmpty(proInsId)){
        model.addAttribute("proInsId", proInsId);
      }
      model.addAttribute("faUrl", "a");
      return "modules/actyw/actYwGnodeDesignView";
	}

  /**
   * 根据流程标识查询带状态业务节点(流程设计图运行状态查看).
   * 1、根据Activity的History接口查询流程进度，获取正在执行的节点
   * 2、得到ActYwGnode对应的节点
   * 3、根据findListByYwGroupAndPreIdss方法找到对应的已执行的节点列表（一级节点）
   * 4、根据findListByYwGroupAndPreIdss方法找到对应的全部的节点列表（一级节点）
   * @param groupId 流程标识
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "/queryStatusTree/{groupId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<List<GnodeView>> queryStatusTree(@PathVariable String groupId, @RequestParam(required=false) String proInsId, @RequestParam(required=false) String grade) {
    return actYwGnodeService.queryStatusTree(groupId, proInsId, grade, actTaskService);
  }

  /**
   * 根据状态和流程ID 定位流程跟踪(旧的项目、大赛使用).
   * @param idx 状态
   * @param groupId 流程ID
   * @param model 模型
   * @return String
   */
  @RequestMapping(value = "designView/{idx}")
  public String designView(@PathVariable String idx, @RequestParam(required=true)String groupId, String grade, Model model) {
    if(StringUtil.isNotEmpty(grade)){
      model.addAttribute("grade", grade);
    }
    model.addAttribute("gnode", actYwGnodeService.getGnodeByStatus(idx, groupId, model));
    model.addAttribute("faUrl", "a");
    return "modules/actyw/actYwGnodeDesignViewPro";
  }

  /**
   * 根据流程标识查询带状态业务节点(旧的项目、大赛使用).
   * 1、得到ActYwGnode对应的节点
   * 2、根据findListByYwGroupAndPreIdss方法找到对应的已执行的节点列表（一级节点）
   * 3、根据findListByYwGroupAndPreIdss方法找到对应的全部的节点列表（一级节点）
   * @param groupId 流程标识
   * @param gnodeId 流程节点标识
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "/queryStatusTreeByGnode/{groupId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<List<GnodeView>> queryStatusTreeByGnode(@PathVariable String groupId, @RequestParam(required=true) String gnodeId, @RequestParam(required=false) String grade) {
    return actYwGnodeService.queryStatusTreeByGnode(groupId, gnodeId, grade);
  }

  @RequestMapping(value = "design")
  public String design(ActYwGnode actYwGnode, Model model, RedirectAttributes redirectAttributes) {
    if ((actYwGnode.getGroup() == null) || StringUtil.isEmpty(actYwGnode.getGroup().getId())) {
      addMessage(redirectAttributes, "自定义流程为空，操作失败！");
      return "redirect:"+Global.getAdminPath()+"/actyw/actYwGnode/?repage";
    }
    designDeal(actYwGnode, model, redirectAttributes);
	  return "modules/actyw/actYwGnodeDesign";
	}

  @RequestMapping(value = "designNew")
  public String designNew(ActYwGnode actYwGnode, Model model, RedirectAttributes redirectAttributes) {
    if ((actYwGnode.getGroup() == null) || StringUtil.isEmpty(actYwGnode.getGroup().getId())) {
      addMessage(redirectAttributes, "自定义流程为空，操作失败！");
      return "redirect:"+Global.getAdminPath()+"/actyw/actYwGnode/?repage";
    }
    designDeal(actYwGnode, model, redirectAttributes);
    return "modules/actyw/actYwGnodeDesignNew";
  }

  private void designDeal(ActYwGnode actYwGnode, Model model, RedirectAttributes redirectAttributes) {
	  if (actYwGnode.getParent() != null && StringUtil.isNotBlank(actYwGnode.getParent().getId())) {
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
	  } else {
	    actYwGnode.setParent(actYwGnodeService.get(SysIds.SYS_TREE_ROOT.getId()));
	  }

	  if (actYwGnode.getSort() == null) {
	    actYwGnode.setSort(30);
	  }

	  if (StringUtil.isNotEmpty(actYwGnode.getGroup().getId())) {
      actYwGnode.setGroupId(actYwGnode.getGroup().getId());
	  }
    if (StringUtil.isNotEmpty(actYwGnode.getGroupId())) {
	    actYwGnode.setGroup(actYwGroupService.get(actYwGnode.getGroupId()));
	  }

	  /**
	   * 如果没有机构默认获取当前机构
	   */
	  if (actYwGnode.getOffice() == null) {
	    actYwGnode.setOffice(UserUtils.getAdminOffice());
	  }

    if (actYwGnode.getIsForm() == null) {
      actYwGnode.setIsForm(false);
    }

    /**
     * 查询所属的流程组.
     */
	  List<ActYwGroup> actYwGroups = actYwGroupService.findList(new ActYwGroup());

    /**
     * 查询根节点.
     */
    ActYwGnode endGnode = null;
    ActYwGnode startGnode = null;
    ActYwGnode rootNode = actYwGnodeService.get(SysIds.SYS_TREE_ROOT.getId());

	  /**
     * 查询可以选择的业务节点.
     */
	  ActYwNode pactYwNode = new ActYwNode();
	  if (actYwGnode.getParent() != null) {
	    pactYwNode.setOffice(actYwGnode.getParent().getOffice());
	    Integer lv = Integer.parseInt(actYwGnode.getParent().getNode().getLevel()) + 1;
	    pactYwNode.setLevel(lv.toString());
	    if((pactYwNode.getLevel()).equals(RtSvl.RtLevelVal.RT_LV2)){
	      pactYwNode.setType(actYwGnode.getParent().getNode().getType());
	    }
	  }else{
	    pactYwNode.setOffice(actYwGnode.getOffice());
	    pactYwNode.setLevel(RtSvl.RtLevelVal.RT_LV1);
	  }
	  List<ActYwNode> actYwNodes = actYwNodeService.findListByTypeNoZero(pactYwNode);

	  /**
	   * 查询可以选择的前置和后置业务节点.
	   */
    ActYwGnode pactYwPpNnGnodes = new ActYwGnode();
    pactYwPpNnGnodes.setGroup(actYwGnode.getGroup());
    pactYwPpNnGnodes.setTypefun(StenFuntype.SFT_SELECT.getVal());
    List<ActYwGnode> actYwPpNnGnodes = actYwGnodeService.findList(pactYwPpNnGnodes);

    /**
     * 查询可以选择的前置业务节点.
     */
    ActYwGnode pactYwPpreGnode = new ActYwGnode();
    pactYwPpreGnode.setGroup(actYwGnode.getGroup());
    pactYwPpreGnode.setTypefun(StenFuntype.SFT_SELECT.getVal());
    pactYwPpreGnode.setParent(actYwGnode.getParent());
    List<ActYwGnode> actYwPpreGnodes = actYwGnodeService.findList(pactYwPpreGnode);

	  /**
	   * 查询可以选择的后置业务节点.
	   */
    ActYwGnode pactYwNnextGnode = new ActYwGnode();
    pactYwNnextGnode.setGroup(actYwGnode.getGroup());
    pactYwNnextGnode.setTypefun(StenFuntype.SFT_SELECT.getVal());
    pactYwNnextGnode.setParent(actYwGnode.getParent());
    List<ActYwGnode> actYwNnextGnodes = actYwGnodeService.findList(pactYwNnextGnode);

    /**
     * 查询可以选择的表单.
     */
    ActYwForm pactYwForm = new ActYwForm();
    ActYwForm allPactYwForm = new ActYwForm();
    allPactYwForm.setFlowType(FlowType.FWT_ALL.getKey());
    if (actYwGnode.getGroup() != null) {
      pactYwForm.setProType(actYwGnode.getGroup().getType());
      pactYwForm.setFlowType(actYwGnode.getGroup().getFlowType());
    }

    List<ActYwForm> actYwForms = null;
    List<ActYwForm> allActYwForms = null;
    if (actYwGnode.getParent() != null) {
      /**
       * 1级节点只显示List类型表单.
       * 非1级节点显示不含List类型表单.
       */
      String plevel = actYwGnode.getParent().getNode().getLevel();
      if ((plevel).equals(RtSvl.RtLevelVal.RT_LV0)) {
        actYwForms = actYwFormService.findListByInStyleList(pactYwForm);
        allActYwForms = actYwFormService.findListByInStyleList(allPactYwForm);

        startGnode = actYwGnodeService.getGnode(actYwGnode.getGroupId(), GnodeType.GT_ROOT_START.getId(), actYwGnode.getParent().getId());
        endGnode = actYwGnodeService.getGnode(actYwGnode.getGroupId(), GnodeType.GT_ROOT_END.getId(), actYwGnode.getParent().getId());
      } else if ((plevel).equals(RtSvl.RtLevelVal.RT_LV1)) {
        actYwForms = actYwFormService.findListByInStyleNoList(pactYwForm);
        allActYwForms = actYwFormService.findListByInStyleNoList(allPactYwForm);

        startGnode = actYwGnodeService.getGnode(actYwGnode.getGroupId(), GnodeType.GT_PROCESS_START.getId(), actYwGnode.getParent().getId());
        endGnode = actYwGnodeService.getGnode(actYwGnode.getGroupId(), GnodeType.GT_PROCESS_END.getId(), actYwGnode.getParent().getId());
      }else{
        actYwForms = Lists.newArrayList();
      }
      actYwForms.addAll(allActYwForms);
    }

    /**
     * 查询可以选择的后置业务节点角色.
     */
	  List<Role> roleList = systemService.findAllRole();

	  model.addAttribute("actYwGroups", actYwGroups);
	  model.addAttribute("rootNode", rootNode);
	  model.addAttribute("actYwGnode", actYwGnode);
    model.addAttribute("actYwNodes", actYwNodes);
	  model.addAttribute("actYwPpNnGnodes", actYwPpNnGnodes);
	  model.addAttribute("actYwPpreGnodes", actYwPpreGnodes);
	  model.addAttribute("actYwNnextGnodes", actYwNnextGnodes);
	  model.addAttribute("actYwForms", actYwForms);
    model.addAttribute("roleList",roleList);
  }


	@RequestMapping(value = "save")
	public String save(ActYwGnode actYwGnode, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, actYwGnode)) {
			return form(actYwGnode, model, redirectAttributes);
		}

		if (actYwGnode == null) {
      return "redirect:"+Global.getAdminPath()+"/actyw/actYwGnode/?repage";
    }

    ActYwRstatus<ActYwGnode> result = actYwGnodeService.saveByGnodePvo(actYwGnode, runner.setEngine(new ActYwEngineImpl(actYwGnodeService, actYwNodeService)));
		addMessage(redirectAttributes, result.getMsg());
    Map<String, Object> attributeValues = new HashMap<String, Object>(1);
    attributeValues.put("groupId", actYwGnode.getGroupId());
    redirectAttributes.addAllAttributes(attributeValues);
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwGnode/?repage";
	}

	@RequestMapping(value = "delete")
	public String delete(ActYwGnode actYwGnode, RedirectAttributes redirectAttributes) {
		actYwGnodeService.delete(actYwGnode);
		addMessage(redirectAttributes, "删除自定义流程成功");
		return "redirect:"+Global.getAdminPath()+"/actyw/actYwGnode/?repage";
	}

  /**
   * 批量修改排序
   */
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
      Map<String, Object> attributeValues = new HashMap<String, Object>(1);
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
      Boolean isExt = StringUtil.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1);
      if (isExt) {
        if ((isShowHide != null) && isShowHide.equals(Global.HIDE) && e.getIsShow()) {
          continue;
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", e.getId());
        map.put("pId", e.getParentId());
        map.put("name", e.getName());
        mapList.add(map);
      }
    }
    return mapList;
  }

  /**
   * 获取已发布流程节点JSON数据.
   * @param extId 排除的ID
   * @param level 是否显示
   * @param isYw 是否业务节点,默认为true

   * @param response
   * @return
   */
  @RequiresPermissions("user")
  @ResponseBody
  @RequestMapping(value = "treeDataByYwId")
  public List<Map<String, Object>> treeDataByYwId(@RequestParam(required=false) String extId, @RequestParam(required=true) String ywId, @RequestParam(required=false) String level, @RequestParam(required=false) Boolean isDetail, @RequestParam(required=false) Integer isYw, @RequestParam(required=false) Boolean isAll, HttpServletResponse response) {
    return actYwGnodeService.treeDataByYwId(extId, ywId, level, isDetail, isYw, isAll);
  }

  /**
   * 获取已发布流程节点JSON数据.
   * @param extId 排除的ID
   * @param level 是否显示
   * @param isYw 是否业务节点,默认为true
   * @param response
   * @return
   */
  @RequiresPermissions("user")
  @ResponseBody
  @RequestMapping(value = "treeDataByGroup")
  public List<Map<String, Object>> treeDataByGroup(@RequestParam(required=false) String extId, @RequestParam(required=true) String groupId, @RequestParam(required=false) String level, @RequestParam(required=false) Boolean isDetail, @RequestParam(required=false) Integer isYw, @RequestParam(required=false) Boolean isAll, HttpServletResponse response) {
    return actYwGnodeService.treeDataByGroup(extId, groupId, level, isDetail, isYw, isAll);
  }
}