package com.oseasy.initiate.modules.actyw.tool.process.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.config.SysJkey;
import com.oseasy.initiate.common.utils.StringUtil;
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
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.GnodeInit;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.GnodePostionUpdate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.GrootAdd;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Gpoint;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Gpostion;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GnodeInitValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GnodePostionValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodePvo;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeSform;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeSpvo;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.NodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenFuntype;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

import net.sf.json.JSONObject;

@RestController
@RequestMapping(value = "${adminPath}/actyw/gnode")
public class ActYwGnodeRestResource {
  /**
   * 空流程节点ID.
   */
  private static final String NodeType_JG_BLV1 = "991000";
  private static final String NodeType_TASK_BLV2 = "992000";
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

  private ActYwEngineImpl initEngine() {
    return runner.setEngine(new ActYwEngineImpl(actYwGnodeService, actYwNodeService)).getEngine();
  }

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

  /**
   * 根据流程标识查询业务节点(流程设计图布局).
   * @param groupId 流程标识
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "/queryTree/{groupId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<Map<String, Object>> queryTree(@PathVariable String groupId) {
    ActYwRstatus<Map<String, Object>> rstatus = new ActYwRstatus<Map<String, Object>>();
    Map<String, Object> result = new HashMap<String, Object>();
    ActYwGnode pactYwGnode = new ActYwGnode();
    pactYwGnode.setGroupId(groupId);

    /**
     * 查询根节点.
     **/
    ActYwGroup group = actYwGroupService.get(groupId);
    ActYwGnode rootNode = actYwGnodeService.get(SysIds.SYS_TREE_ROOT.getId());

    ActYwGnode rootStartNode = null;
    ActYwGnode paGnode = new ActYwGnode();
    paGnode.setGroup(group);
    paGnode.setParent(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
    paGnode.setType(GnodeType.GT_ROOT_START.getId());
    List<ActYwGnode> rootStartNodes = actYwGnodeService.findList(paGnode);
    if((rootStartNodes != null) && (rootStartNodes.size() == 1)){
      rootStartNode = rootStartNodes.get(0);
    }else{
      rootStartNode = new ActYwGnode();
    }

    ActYwGnode rootEndNode = null;
    paGnode.setType(GnodeType.GT_ROOT_END.getId());
    List<ActYwGnode> rootEndNodes = actYwGnodeService.findList(paGnode);
    if((rootStartNodes != null) && (rootStartNodes.size() == 1)){
      rootEndNode = rootEndNodes.get(0);
    }else{
      rootEndNode = new ActYwGnode();
    }

    if((group == null)){
      rstatus.setStatus(false);
      rstatus.setMsg("参数错误，没有找到流程标识！");
    }

    //pactYwGnode.setHasGateway(true);
    List<ActYwGnode> actYwGnodes = actYwGnodeService.findListByYwGroup(pactYwGnode);

    if((actYwGnodes == null) || (actYwGnodes.size() <= 0)){
      actYwGnodes = Lists.newArrayList();
      rstatus.setMsg("没有数据！");
    }
    result.put(SysJkey.JK_LISTS, actYwGnodes);
    result.put(ActYwGroup.JK_GROUP, group);
    result.put(SysJkey.JK_ROOT, rootNode);
    result.put(SysJkey.JK_ROOT_START, rootStartNode);
    result.put(SysJkey.JK_ROOT_END, rootEndNode);
    rstatus.setDatas(result);
    return rstatus;
  }

  /**
   * 根据流程标识和节点父标识加载新增表单数据(流程设计图新增表单).
   * @param groupId 流程标识
   * @param parentId 流程父标识
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "/query/{groupId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<GnodeSform> queryForm(@PathVariable("groupId") String groupId, @RequestParam(required=false) String parentId) {
    ActYwRstatus<GnodeSform> gsftatus = new ActYwRstatus<GnodeSform>();
    ActYwGnode actYwGnode = null;
    if(StringUtil.isNotEmpty(groupId)){
      actYwGnode = new ActYwGnode(new ActYwGroup(groupId));
    }else{
      gsftatus.setStatus(false);
      gsftatus.setMsg("参数错误！");
      return gsftatus;
    }

    if(StringUtil.isNotEmpty(parentId)){
      actYwGnode.setParent(new ActYwGnode(parentId));
    }

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
     * 查询根节点、开始节点、结束节点.
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
    ActYwGnode pactYwPpNnGnodes = new ActYwGnode();
    pactYwPpNnGnodes.setGroup(actYwGnode.getGroup());
    pactYwPpNnGnodes.setTypefun(StenFuntype.SFT_SELECT.getVal());
    List<ActYwGnode> actYwPpNnGnodes = actYwGnodeService.findList(pactYwPpNnGnodes);*/

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
    if ((actYwGnode.getNode() != null) && StringUtil.isNotEmpty(actYwGnode.getNode().getType())) {
      String ntype = actYwGnode.getNode().getType();
      if (!((ntype).equals(NodeType.NT_YW_JG_BLV1.getType()) || (ntype).equals(NodeType.NT_YW_TASK_BLV2.getType()))) {
        pactYwForm.setType(actYwGnode.getNode().getType());
      }
    }
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


    GnodeSform datas = new GnodeSform();
    List<String> nodeTypes = Lists.newArrayList();
    nodeTypes.add(0, NodeType_JG_BLV1);
    nodeTypes.add(1, NodeType_TASK_BLV2);
    datas.setNodeTypes(nodeTypes);
    datas.setRoot(rootNode);
    datas.setStart(startGnode);
    datas.setEnd(endGnode);
    datas.setRoles(roleList);
    datas.setForms(actYwForms);
    datas.setGroupId(actYwGnode.getGroupId());
    datas.setGroupName(actYwGnode.getGroup().getName());
    datas.setParentId(actYwGnode.getParentId());
    datas.setParentName(actYwGnode.getParent().getName());
    datas.setIsShow(actYwGnode.getIsShow());
    datas.setRemarks(actYwGnode.getRemarks());
    datas.setNodes(actYwNodes);
    datas.setGnode(new GnodeSpvo(actYwGnode));
    datas.setPregnodes(actYwPpreGnodes);
    datas.setNextgnodes(actYwNnextGnodes);
    gsftatus.setDatas(datas);
    return gsftatus;
  }

  /**
   * 获取节点对应的角色.
   * @return List
   */
  @ResponseBody
  @RequestMapping(value = "/ajaxGnodeRole", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public List<Role> ajaxGnodeRole() {
    return systemService.findAllRole();
  }

  /**
   * 获取节点对应的表单.
   * @param flowType 流程类型
   * @param plevel 节点等级
   * @param proType 项目类型
   * @param type 节点类型
   * @return List
   */
  @ResponseBody
  @RequestMapping(value = "/ajaxGnodeForm/{flowType}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public List<ActYwForm> ajaxGnodeForm(@PathVariable("flowType") String flowType,  @RequestParam(required=false) String plevel, @RequestParam(required=false) String proType, @RequestParam(required=false) String type) {
    ActYwForm pactYwForm = new ActYwForm();
    ActYwForm allPactYwForm = new ActYwForm();
    allPactYwForm.setFlowType(FlowType.FWT_ALL.getKey());
    if (StringUtil.isNotEmpty(type)) {
      if (!((type).equals(NodeType.NT_YW_JG_BLV1.getType()) || (type).equals(NodeType.NT_YW_TASK_BLV2.getType()))) {
        pactYwForm.setType(type);
      }
    }
    if (StringUtil.isNotEmpty(proType)) {
      pactYwForm.setProType(proType);
    }
    if (StringUtil.isNotEmpty(flowType)) {
      pactYwForm.setFlowType(flowType);
    }

    List<ActYwForm> actYwForms = null;
    List<ActYwForm> allActYwForms = null;
    /**
     * 1级节点只显示List类型表单.
     * 非1级节点显示不含List类型表单.
     */
    if (StringUtil.isEmpty(plevel)) {
      plevel = RtSvl.RtLevelVal.RT_LV1;
    }
    if ((plevel).equals(RtSvl.RtLevelVal.RT_LV0)) {
      actYwForms = actYwFormService.findListByInStyleList(pactYwForm);
      allActYwForms = actYwFormService.findListByInStyleList(allPactYwForm);
    } else if ((plevel).equals(RtSvl.RtLevelVal.RT_LV1)) {
      actYwForms = actYwFormService.findListByInStyleNoList(pactYwForm);
      allActYwForms = actYwFormService.findListByInStyleNoList(allPactYwForm);
    }else{
      actYwForms = Lists.newArrayList();
    }
    actYwForms.addAll(allActYwForms);

    return actYwForms;
  }

  /**
   * 更新流程节点定位信息.
   * @param gpostion 定位对象
   * @return ActYwRstatus
   */
  @SuppressWarnings("rawtypes")
  @ResponseBody
  @RequestMapping(value = "/updateGpostion", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
  public ActYwRstatus<GnodeVrtpl> updateGpostion(@RequestBody JSONObject gpostion) {
    Map<String, Class> classMap = new HashMap<String, Class>();
    classMap.put("gnodes", Gpoint.class);
    Gpostion pgpostion = (Gpostion) JSONObject.toBean(gpostion, Gpostion.class, classMap);
    GnodePostionUpdate grunner = new GnodePostionUpdate();
    grunner.setEngine(initEngine());
    grunner.setValidate(new GnodePostionValidate());
    return grunner.exec(pgpostion);
  }

  /**
   * 流程节点保存.
   * @param pvo 节点参数实体.
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "/saveProcess", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
  public ActYwRstatus<ActYwGnode> saveProcess(@RequestBody GnodePvo pvo){
    return actYwGnodeService.saveByGnodePvo(pvo, runner.setEngine(new ActYwEngineImpl(actYwGnodeService, actYwNodeService)));
  }

  /**
   * 流程节点保存(组+节点).
   * @param pvo 节点参数实体.
   * @return ActYwRstatus
   */
  @ResponseBody
  @RequestMapping(value = "/saveGprocess", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
  public ActYwRstatus<ActYwGnode> saveGprocess(@RequestBody GnodePvo pvo){
    ActYwRstatus<ActYwGnode> rstatus = null;
    GnodePvo process = new GnodePvo();
    process.setPreFunId(pvo.getPreFunId());
    process.setName(pvo.getName());
    process.setNextFunId(pvo.getNextFunId());
    process.setRemarks(pvo.getRemarks());
    process.setHasGateway(pvo.getHasGateway());
    process.setNodeId(NodeType_JG_BLV1);
    process.setPosLux(pvo.getPosLux());
    process.setPosLuy(pvo.getPosLuy());
    process.setIconUrl(pvo.getIconUrl());
    process.setGroupId(pvo.getGroupId());
    process.setHasGroup(false);
    rstatus = actYwGnodeService.saveByGnodePvo(pvo, runner.setEngine(new ActYwEngineImpl(actYwGnodeService, actYwNodeService)));

    if(rstatus.getStatus()){
      ActYwGnode curStart = null;
      ActYwGnode curEnd = null;
      ActYwGnode gnode = rstatus.getDatas();
      for (ActYwGnode curgnode : gnode.getChildGnodes()) {
        if((curgnode.getType()).equals(GnodeType.GT_PROCESS_START.getId())){
          curStart = curgnode;
        }else if((curgnode.getType()).equals(GnodeType.GT_PROCESS_START.getId())){
          curEnd = curgnode;
        }
      }
      if((curStart == null) || (curEnd == null)){
        rstatus = new ActYwRstatus<ActYwGnode>(false, "前置、后置节点不能为空！");
      }else{
        GnodePvo ptask = new GnodePvo();
        ptask.setPreFunId(curStart.getId());
        ptask.setNextFunId(curEnd.getId());
        ptask.setName(pvo.getName());
        ptask.setRemarks(pvo.getRemarks());
        ptask.setHasGateway(pvo.getHasGateway());
        ptask.setNodeId(NodeType_TASK_BLV2);
        ptask.setPosLux(pvo.getPosLux());
        ptask.setPosLuy(pvo.getPosLuy());
        ptask.setIconUrl(pvo.getIconUrl());
        ptask.setGroupId(pvo.getGroupId());
        ptask.setHasGroup(false);
        rstatus = actYwGnodeService.saveByGnodePvo(pvo, runner.setEngine(new ActYwEngineImpl(actYwGnodeService, actYwNodeService)));
      }
    }
    return rstatus;

  }

  @ResponseBody
  @RequestMapping(value = "/updateProcess", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
  public ActYwRstatus<ActYwGnode> updateProcess(@RequestBody GnodePvo pvo){
    return actYwGnodeService.updateByGnodePvo(pvo);
  }

  @ResponseBody
  @RequestMapping(value = "/init", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<GnodeVrtpl> init() {
    GnodeInit grInit = new GnodeInit();
    grInit.setEngine(initEngine());
    grInit.setValidate(new GnodeInitValidate());
    ActYwRstatus<GnodeVrtpl> rstatus = grInit.exec(null);
    return rstatus;
  }

  @ResponseBody
  @RequestMapping(value = "/saveRoot/{groupId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<GnodeVrtpl> saveRoot(@PathVariable String groupId) {
    GrootAdd grRoot = new GrootAdd();
    grRoot.setEngine(initEngine());
    grRoot.setValidate(new GrootValidate());
    ActYwRstatus<GnodeVrtpl> rstatus = grRoot.exec(new Ggroup(groupId));
    return rstatus;
  }

  @ResponseBody
  @RequestMapping(value = "/reset/{groupId}", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
  public ActYwRstatus<ActYwGnode> reset(@PathVariable String groupId) {
    return actYwGnodeService.resetGroup(groupId, runner.setEngine(new ActYwEngineImpl(actYwGnodeService, actYwNodeService)));
  }

  @ResponseBody
  @RequestMapping(value = "/delete/{gnodeId}", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
  public ActYwRstatus<ActYwGnode> delete(@PathVariable String gnodeId) {
    return actYwGnodeService.deleteAuto(gnodeId, runner.setEngine(new ActYwEngineImpl(actYwGnodeService, actYwNodeService)));
  }
}
