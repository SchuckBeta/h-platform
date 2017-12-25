/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd.impl
 * @Description [[_ActYwCnRootAddNode_]]文件
 * @date 2017年6月19日 上午9:44:05
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwAbsEngine;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwCommand;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwCommandCheck;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.vo.ActYwPgroot;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.vo.ActYwRgnode;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenEsubType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenFuntype;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 业务流程根节点操作类-业务根节点-添加业务节点（没有业务节点存在）.
 * A:当没有任何节点时新增业务节点:
 * 该方法在流程添加第一个业务节点的时候调用：
 *  1、新增或更新业务节点
 *  2、新增业务节点和结束节点连接线,并关联新增业务节点.
 *  3、更新业务节点下个节点连接线.
 *  4、更新根节点开始节点与业务节点连接线.
 *  5、按顺序返回所有节点.
 * @author chenhao
 * @date 2017年6月18日 下午2:22:47
 *
 */
@Service
public class ActYwCnRootAddNode extends ActYwAbsEngine<ActYwEngineImpl> implements ActYwCommand<ActYwPgroot>, ActYwCommandCheck<ActYwPgroot>{
  protected static final Logger LOGGER = Logger.getLogger(ActYwCnRootAddNode.class);

  public ActYwCnRootAddNode() {
    super();
  }

  public ActYwCnRootAddNode(ActYwEngineImpl engine) {
    super(engine);
  }

  /**
   *执行必要参数 .
   *  group 流程组不能为空
   *  gnode 新增业务节点不能为空
   *  startGnode 新增业务上一节点 startGnode节点不能为空
   *  endGnode 新增业务下一节点 endGnode节点不能为空
   *  startSflowGnodes 新增业务上一节点 startGnode节点和下一节点 endGnode节点的连接线不能为空
   * @author chenhao
   * @param tpl
   * @return ActYwRgnode
   */
  @Override
  public ActYwRgnode execute(ActYwPgroot tpl) {
    ActYwRgnode actYwRgnode = new ActYwRgnode();
    ActYwRstatus rstsusParams = checkParams(tpl);
    ActYwRstatus rstsusBeforeEx = checkBeforeExecute(tpl);
    actYwRgnode.setStatus(rstsusParams.getStatus() && rstsusBeforeEx.getStatus());
    if (actYwRgnode.getStatus()) {
      ActYwGroup group = tpl.getGroup();
      ActYwGnode gnode = tpl.getGnode();
      ActYwGnode startGnode = tpl.getStartGnode();
      ActYwGnode preFunGnode = tpl.getPreFunGnode();
      ActYwGnode nextFunGnode = tpl.getNextFunGnode();
      ActYwNode node = gnode.getNode();

      /**
       * 上一节点的所有连接线列表.
       */
      List<ActYwGnode> preFlowGnodes = Lists.newArrayList();

      /**
       * 下一节点的所有连接线列表.
       */
      List<ActYwGnode> nextFlowGnodes = Lists.newArrayList();

      /**
       * 所有节点列表.
       */
      List<ActYwGnode> gnodes = Lists.newArrayList();

      /**
       * 新增或更新业务节点.
       */
      gnode.setParent(startGnode.getParent());
      gnode.setOffice(UserUtils.getAdminOffice());
      gnode.setType(GnodeType.getByActYwNode(node, node.getLevel()).getId());
      gnode.setTypefun(StenFuntype.SFT_SELECT.getVal());
      gnode.setGroup(group);
      gnode.setPreGnode(startGnode);
      gnode.setPreFunGnode(preFunGnode);
      gnode.setPreGnodes(Lists.newArrayList());

      gnode.setNextFunGnode(nextFunGnode);
      gnode.setNextGnodes(Lists.newArrayList());
      gnode.setRemarks("RootAddNode"+node.getName());
      //TODO 保存或更新 gnode
      engine.service().save(gnode);

      /**
       * 新增业务节点和结束节点连接线,并关联新增业务节点.
       */
      ActYwGnode endSflowGnodeNew = new ActYwGnode();
      endSflowGnodeNew.setParent(startGnode.getParent());
      endSflowGnodeNew.setGroup(group);
      endSflowGnodeNew.setTypefun(StenFuntype.SFT_NOT_SELECT.getVal());
      if ((node.getLevel()).equals(RtSvl.RtLevelVal.RT_LV1)) {
        endSflowGnodeNew.setType(GnodeType.getByActYwNodeAndNodekey(node, node.getLevel(), StenType.ST_FLOW_SEQUENCE.getKey()).getId());
      }else if ((node.getLevel()).equals(RtSvl.RtLevelVal.RT_LV2)) {
        endSflowGnodeNew.setType(GnodeType.getByActYwNodeAndNodekey(node, RtSvl.RtLevelVal.RT_LV1, StenType.ST_FLOW_SEQUENCE.getKey()).getId());
      }
      endSflowGnodeNew.setNodeId(StenType.ST_FLOW_SEQUENCE.getId());
      endSflowGnodeNew.setIsShow(true);
      endSflowGnodeNew.setIsForm(false);
      endSflowGnodeNew.setSort(100);
      endSflowGnodeNew.setOffice(UserUtils.getAdminOffice());
      endSflowGnodeNew.setPreGnode(gnode);
      endSflowGnodeNew.setPreFunGnode(gnode);
      endSflowGnodeNew.setPreGnodes(Lists.newArrayList());
      endSflowGnodeNew.setNextGnode(nextFunGnode);
      endSflowGnodeNew.setNextFunGnode(nextFunGnode);
      endSflowGnodeNew.setNextGnodes(Lists.newArrayList());
      endSflowGnodeNew.setRemarks("RootAddNode" + StenType.ST_FLOW_SEQUENCE.getRemark());
      //TODO 保存 endSflowGnodeNew
      engine.service().save(endSflowGnodeNew);

      /**
       * 更新业务节点下个节点连接线.
       * 更新业务节点 gnode 的next_idss.
       */
      gnode.setNextGnode(endSflowGnodeNew);
      gnode.getNextGnodes().add(endSflowGnodeNew);
      gnode.setNextIdss(ActYwGnode.genIdss(endSflowGnodeNew.getNextIdss(), endSflowGnodeNew.getId()));
      //TODO 更新 gnode
      engine.service().save(gnode);

      /**
       * 更新开始节点与业务节点连接线.
       * 更新开始节点 startGnode 的next_id和next_fun_id和pre_ids.
       */
      startGnode.setNextGnode(gnode);
      startGnode.setNextId(gnode.getId());
      startGnode.setNextFunGnode(gnode);
      startGnode.setNextFunId(gnode.getId());


      /**
       * 更新后置业务节点 nextFunGnode 的pre_id和pre_fun_id和pre_ids.
       */
      nextFunGnode.setGroup(group);
      nextFunGnode.setPreGnode(endSflowGnodeNew);
      nextFunGnode.setPreId(endSflowGnodeNew.getId());
      nextFunGnode.setPreFunGnode(gnode);
      nextFunGnode.setPreFunId(gnode.getId());
      List<ActYwGnode> preGnodes = Lists.newArrayList();
      preGnodes.add(endSflowGnodeNew);
      nextFunGnode.setPreGnodes(preGnodes);
      nextFunGnode.setPreIds(ActYwGnode.genIds(preGnodes));
      nextFunGnode.setPreIdss(ActYwGnode.genIdss(endSflowGnodeNew.getPreIdss(), endSflowGnodeNew.getId()));
      StenType nextFnstype = StenType.getByKey(nextFunGnode.getNode().getNodeKey());
      if ((nextFnstype.getSubtype()).equals(StenEsubType.SES_EVENT_END)) {
        nextFunGnode.setNextGnode(null);
        nextFunGnode.setNextId("");
        nextFunGnode.setNextFunGnode(null);
        nextFunGnode.setNextFunId("");
        nextFunGnode.setNextGnodes(null);
        nextFunGnode.setNextIds("");
      }
      //TODO 更新 nextFunGnode
      engine.service().save(nextFunGnode);

      /**
       * 更新前置业务节点 preFunGnode 的pre_id和pre_fun_id和pre_ids.
       */
      preFunGnode.setGroup(group);


      /**
       * 更新 endSflowGnodeNew 的pre_ids和next_ids.
       */
      if (endSflowGnodeNew.getPreGnodes() == null) {
        endSflowGnodeNew.setPreGnodes(Lists.newArrayList());
      }
      if (endSflowGnodeNew.getNextGnodes() == null) {
        endSflowGnodeNew.setNextGnodes(Lists.newArrayList());
      }
      endSflowGnodeNew.getPreGnodes().add(gnode);
      endSflowGnodeNew.getNextGnodes().add(nextFunGnode);
      //TODO 保存 endSflowGnodeNew
      engine.service().save(endSflowGnodeNew);

      /**
       * 更新 gnode 的pre_ids和next_ids.
       */
      List<ActYwGnode> gnodePreGnodes = Lists.newArrayList();
      gnodePreGnodes.add(startGnode);
      gnode.setPreGnodes(gnodePreGnodes);
      gnode.setPreIds(ActYwGnode.genIds(gnodePreGnodes));

      List<ActYwGnode> gnodeNextGnodes = Lists.newArrayList();
      gnodeNextGnodes.add(endSflowGnodeNew);
      gnode.setNextGnodes(gnodeNextGnodes);
      gnode.setNextIds(ActYwGnode.genIds(gnodeNextGnodes));
      //TODO 保存 gnode
      engine.service().save(gnode);

      List<ActYwGnode> startNextGnodes = Lists.newArrayList();
      startNextGnodes.add(gnode);
      startGnode.setNextGnodes(startNextGnodes);
      startGnode.setNextIds(ActYwGnode.genIds(startNextGnodes));
      //TODO 更新 startSflowGnode
      startGnode.setNextIdss(ActYwGnode.genIdss(gnode.getNextIdss(), gnode.getId()));
      engine.service().save(startGnode);

       preFunGnode.setNextFunGnode(gnode);
       preFunGnode.setNextFunId(gnode.getId());
       List<ActYwGnode> nextGnodes = Lists.newArrayList();
       nextGnodes.add(preFunGnode.getNextGnode());
       preFunGnode.setNextGnodes(nextGnodes);
       preFunGnode.setNextIds(ActYwGnode.genIds(nextGnodes));
       StenType preFnstype = StenType.getByKey(preFunGnode.getNode().getNodeKey());
       if ((preFnstype.getSubtype()).equals(StenEsubType.SES_EVENT_START)) {
         preFunGnode.setPreGnode(null);
         preFunGnode.setPreId("");
         preFunGnode.setPreFunGnode(null);
         preFunGnode.setPreFunId("");
         preFunGnode.setPreGnodes(null);
         preFunGnode.setPreIds("");
       }
      startGnode.setNextIdss(ActYwGnode.genIdss(startGnode.getNextIdss(), startGnode.getId()));
       //TODO 更新 preFunGnode
       engine.service().save(preFunGnode);

      if (GnodeType.getById(gnode.getType()).equals(GnodeType.GT_PROCESS)) {
        executeSubProcess(gnode, preGnodes);
      }

      preFlowGnodes.add(startGnode);
      nextFlowGnodes.add(endSflowGnodeNew);
      if (tpl.getEndSflowGnode() != null) {
        nextFlowGnodes.addAll(tpl.getEndSflowGnode());
      }
      /**
       * 按顺序返回所有节点.
       */
      gnodes.add(startGnode);
      gnodes.addAll(preFlowGnodes);
      gnodes.add(gnode);
      gnodes.addAll(nextFlowGnodes);
      gnodes.add(nextFunGnode);

      actYwRgnode.setGnodes(gnodes);
      actYwRgnode.setStatus(true);
    }else{
      if (!rstsusParams.getStatus()) {
        actYwRgnode.setMsg(rstsusParams.getMsg());
      }
      if (!rstsusBeforeEx.getStatus()) {
        actYwRgnode.setMsg(rstsusBeforeEx.getMsg());
      }
    }
    return actYwRgnode;
  }

  /**
   * 添加子流程操作.
   * @author chenhao
   * @param tpl
   * @param preGnodes
   */
  private void executeSubProcess(ActYwGnode gnode, List<ActYwGnode> preGnodes) {
    /**
     * 新增根节点开始节点.
     */
    ActYwGnode subPstartGnodeNew = new ActYwGnode();
    subPstartGnodeNew.setParent(gnode);
    subPstartGnodeNew.setGroup(gnode.getGroup());
    subPstartGnodeNew.setTypefun(StenFuntype.SFT_SELECT.getVal());
    subPstartGnodeNew.setType(GnodeType.GT_PROCESS_START.getId());
    subPstartGnodeNew.setNodeId(StenType.ST_START_EVENT_NONE.getId());
    subPstartGnodeNew.setIsShow(true);
    subPstartGnodeNew.setIsForm(false);
    subPstartGnodeNew.setSort(10);
    subPstartGnodeNew.setOffice(UserUtils.getAdminOffice());
    subPstartGnodeNew.setPreGnodes(Lists.newArrayList());
    subPstartGnodeNew.setNextGnodes(Lists.newArrayList());
    subPstartGnodeNew.setRemarks("RootAdd-SP-" + StenType.ST_START_EVENT_NONE.getRemark());

    subPstartGnodeNew.setPreFunGnode(gnode);
    subPstartGnodeNew.setPreId(gnode.getId());
    List<ActYwGnode> subPstartpreGnodes = Lists.newArrayList();
    subPstartpreGnodes.add(gnode);
    subPstartGnodeNew.setPreGnodes(preGnodes);
    subPstartGnodeNew.setPreIds(ActYwGnode.genIds(subPstartpreGnodes));
    subPstartGnodeNew.setPreIdss(ActYwGnode.genIdss(gnode.getPreIdss(), gnode.getId()));

    //TODO 保存 subPstartGnodeNew
    engine.service().save(subPstartGnodeNew);

    /**
     * 新增根节点开始节点和结束系欸但连接线,并关联开始节点.
     */
    ActYwGnode subPsflowGnodeNew = new ActYwGnode();
    subPsflowGnodeNew.setParent(subPstartGnodeNew.getParent());
    subPsflowGnodeNew.setGroup(gnode.getGroup());
    subPsflowGnodeNew.setTypefun(StenFuntype.SFT_NOT_SELECT.getVal());
    subPsflowGnodeNew.setType(GnodeType.GT_PROCESS_FLOW.getId());
    subPsflowGnodeNew.setNodeId(StenType.ST_FLOW_SEQUENCE.getId());
    subPsflowGnodeNew.setIsShow(true);
    subPsflowGnodeNew.setIsForm(false);
    subPsflowGnodeNew.setSort(20);
    subPsflowGnodeNew.setOffice(UserUtils.getAdminOffice());
    subPsflowGnodeNew.setPreGnode(subPstartGnodeNew);
    subPsflowGnodeNew.setPreFunGnode(subPstartGnodeNew);
    subPsflowGnodeNew.setPreGnodes(Lists.newArrayList());
    subPsflowGnodeNew.getPreGnodes().add(subPstartGnodeNew);
    subPsflowGnodeNew.setNextGnodes(Lists.newArrayList());
    subPsflowGnodeNew.setRemarks("RootAdd-SP-" + StenType.ST_FLOW_SEQUENCE.getRemark());
    //TODO 保存 subPsflowGnodeNew

    subPsflowGnodeNew.setPreId(subPstartGnodeNew.getId());

    List<ActYwGnode> subPsflowGnodes = Lists.newArrayList();
    subPsflowGnodes.add(subPstartGnodeNew);
    subPsflowGnodeNew.setPreGnodes(subPsflowGnodes);
    subPsflowGnodeNew.setPreIds(ActYwGnode.genIds(subPsflowGnodes));
    subPsflowGnodeNew.setPreIdss(ActYwGnode.genIdss(subPstartGnodeNew.getPreIdss(), subPstartGnodeNew.getId()));
    engine.service().save(subPsflowGnodeNew);

    /**
     * 新增根节点结束节点,并关联接线.
     */
    ActYwGnode subPendGnodeNew = new ActYwGnode();
    subPendGnodeNew.setParent(subPstartGnodeNew.getParent());
    subPendGnodeNew.setGroup(gnode.getGroup());
    subPendGnodeNew.setTypefun(StenFuntype.SFT_SELECT.getVal());
    subPendGnodeNew.setType(GnodeType.GT_PROCESS_END.getId());
    subPendGnodeNew.setNodeId(StenType.ST_END_EVENT_NONE.getId());
    subPendGnodeNew.setIsShow(true);
    subPendGnodeNew.setIsForm(false);
    subPendGnodeNew.setSort(30);
    subPendGnodeNew.setOffice(UserUtils.getAdminOffice());
    subPendGnodeNew.setPreGnode(subPsflowGnodeNew);
    subPendGnodeNew.setPreFunGnode(subPstartGnodeNew);
    subPendGnodeNew.setPreGnodes(Lists.newArrayList());
    subPendGnodeNew.getPreGnodes().add(subPsflowGnodeNew);
    subPendGnodeNew.setNextGnodes(Lists.newArrayList());
    subPendGnodeNew.setRemarks("RootAdd-SP-" + StenType.ST_END_EVENT_NONE.getRemark());

    subPendGnodeNew.setPreId(subPsflowGnodeNew.getId());
    //保存前置节点
    List<ActYwGnode> subPendGnodes = Lists.newArrayList();
    subPendGnodes.add(subPsflowGnodeNew);
    subPendGnodeNew.setPreGnodes(subPendGnodes);
    subPendGnodeNew.setPreIds(ActYwGnode.genIds(subPsflowGnodes));
    subPendGnodeNew.setPreIdss(ActYwGnode.genIdss(subPsflowGnodeNew.getPreIdss(), subPsflowGnodeNew.getId()));

    //保存后置节点
    subPendGnodeNew.setNextGnode(gnode);
    subPendGnodeNew.setNextId(gnode.getId());
    subPendGnodeNew.setNextFunGnode(gnode);
    List<ActYwGnode> subPendNextGnodes = Lists.newArrayList();
    subPendNextGnodes.add(gnode);
    subPendGnodeNew.setNextGnodes(subPendNextGnodes);
    subPendGnodeNew.setNextIds(ActYwGnode.genIds(subPendNextGnodes));
    subPendGnodeNew.setNextIdss(ActYwGnode.genIdss(gnode.getNextIdss(), gnode.getId()));
    //TODO 保存 subPendGnodeNew
    engine.service().save(subPendGnodeNew);


    //更新子节点线的后置节点
    subPsflowGnodeNew.setNextGnode(subPendGnodeNew);
    subPsflowGnodeNew.getNextGnodes().add(subPendGnodeNew);
    subPsflowGnodeNew.setNextId(subPendGnodeNew.getId());
    subPsflowGnodeNew.setNextFunGnode(subPendGnodeNew);
    List<ActYwGnode> subPsflowNextGnodes = Lists.newArrayList();
    subPsflowNextGnodes.add(subPendGnodeNew);
    subPsflowGnodeNew.setNextGnodes(subPsflowNextGnodes);
    subPsflowGnodeNew.setNextIds(ActYwGnode.genIds(subPsflowNextGnodes));
    subPsflowGnodeNew.setNextIdss(ActYwGnode.genIdss(subPendGnodeNew.getNextIdss(), subPendGnodeNew.getId()));
     //TODO 更新 subPstartGnodeNew(子节点开始)
     engine.service().save(subPsflowGnodeNew);

    //更新子节点开始的后置节点
    subPstartGnodeNew.setNextGnode(subPsflowGnodeNew);
    subPstartGnodeNew.setNextId(subPsflowGnodeNew.getId());
    subPstartGnodeNew.setNextFunGnode(subPsflowGnodeNew);

    List<ActYwGnode> subPstartNextGnodes = Lists.newArrayList();
    subPstartNextGnodes.add(subPsflowGnodeNew);
    subPstartGnodeNew.setNextGnodes(subPstartNextGnodes);
    subPstartGnodeNew.setNextIds(ActYwGnode.genIds(subPstartNextGnodes));
    subPstartGnodeNew.setNextIdss(ActYwGnode.genIdss(subPsflowGnodeNew.getNextIdss(), subPsflowGnodeNew.getId()));
     //TODO 更新 subPstartGnodeNew(子节点开始)
     engine.service().save(subPstartGnodeNew);
  }

  @Override
  public ActYwRstatus checkBeforeExecute(ActYwPgroot tpl) {
    return new ActYwCnRootAdd(engine).checkPerfect(tpl.getGroup());
  }

  @SuppressWarnings("null")
  @Override
  public ActYwRstatus checkParams(ActYwPgroot tpl) {
    if (tpl == null) {
      tpl = new ActYwPgroot();
      tpl.setMsg("ActYwCnRootAddNode 命令[tpl]不能为空！");
      LOGGER.warn(tpl.getMsg());
      tpl.setStatus(false);
    }

    if (tpl.getGroup() == null) {
      tpl.setMsg("ActYwCnRootAddNode 命令[group]不能为空！");
      LOGGER.warn(tpl.getMsg());
      tpl.setStatus(false);
    }

    if (tpl.getGnode() == null) {
      tpl.setMsg("ActYwCnRootAddNode 命令[gnode]不能为空！");
      LOGGER.warn(tpl.getMsg());
      tpl.setStatus(false);
    }

    if (tpl.getGnode().getNode() == null) {
      tpl.setMsg("ActYwCnRootAddNode 命令[gnode.node]不能为空！");
      LOGGER.warn(tpl.getMsg());
      tpl.setStatus(false);
    }

    if (StringUtil.isNotEmpty(tpl.getGnode().getNode().getId())) {
      tpl.getGnode().setNode(engine.nodeservice().get(tpl.getGnode().getNode().getId()));
    }else{
      tpl.setMsg("ActYwCnRootAddNode 命令[preFunGnode.node]不能为空！");
      LOGGER.warn(tpl.getMsg());
      tpl.setStatus(false);
    }

    if (tpl.getPreFunGnode() == null) {
      tpl.setMsg("ActYwCnRootAddNode 命令[preFunGnode]不能为空！");
      LOGGER.warn(tpl.getMsg());
      tpl.setStatus(false);
    }

    if (StringUtil.isNotEmpty(tpl.getPreFunGnode().getId())) {
      if (tpl.getPreFunGnode().getGroup() == null) {
        tpl.setPreFunGnode(engine.service().get(tpl.getPreFunGnode().getId()));
      }

      if (tpl.getPreFunGnode().getGroup() == null) {
        tpl.setMsg("ActYwCnRootAddNode 命令[preFunGnode.group]不能为空！");
        LOGGER.warn(tpl.getMsg());
        tpl.setStatus(false);
      }
    }

    if ((tpl.getPreFunGnode().getNode() != null) && StringUtil.isNotEmpty(tpl.getPreFunGnode().getNode().getId())) {
      if (StringUtil.isEmpty(tpl.getPreFunGnode().getNode().getNodeKey())) {
        tpl.getPreFunGnode().setNode(engine.nodeservice().get(tpl.getPreFunGnode().getNode().getId()));
      }
    }else{
      tpl.setMsg("ActYwCnRootAddNode 命令[preFunGnode.node.id]不能为空！");
      LOGGER.warn(tpl.getMsg());
      tpl.setStatus(false);
    }


    if (tpl.getNextFunGnode() == null) {
      tpl.setMsg("ActYwCnRootAddNode 命令[nextFunGnode]不能为空！");
      LOGGER.warn(tpl.getMsg());
      tpl.setStatus(false);
    }

    if ((tpl.getNextFunGnode().getNode() != null) && StringUtil.isNotEmpty(tpl.getNextFunGnode().getNode().getId())) {
      if (StringUtil.isEmpty(tpl.getNextFunGnode().getNode().getNodeKey())) {
        tpl.getNextFunGnode().setNode(engine.nodeservice().get(tpl.getNextFunGnode().getNode().getId()));
      }
    }else{
      tpl.setMsg("ActYwCnRootAddNode 命令[nextFunGnode.node.id]不能为空！");
      LOGGER.warn(tpl.getMsg());
      tpl.setStatus(false);
    }


    if (tpl.getStartGnode() == null) {
      tpl.setMsg("ActYwCnRootAddNode 命令[startGnode]不能为空！");
      LOGGER.warn(tpl.getMsg());
      tpl.setStatus(false);
    }

    GnodeType gnodeType = GnodeType.getById(tpl.getGnode().getType());
    if ((gnodeType).equals(GnodeType.GT_PROCESS_TASK)) {
      GnodeType gnodePreFunType = GnodeType.getById(tpl.getPreFunGnode().getType());
      if (!((gnodePreFunType != null) && (gnodePreFunType.getLevel()).equals(RtSvl.RtLevelVal.RT_LV2))) {
        tpl.setMsg("ActYwCnRootAddNode 命令[preFunGnode]添加子流程业务节点时，前置业务节点等级只能为2！");
        LOGGER.warn(tpl.getMsg());
        tpl.setStatus(false);
      }

      if ((tpl.getStartSflowGnode() == null) || (tpl.getStartSflowGnode().size() != 1)) {
        tpl.setMsg("ActYwCnRootAddNode 命令[startSflowGnodes]不能为空，且只能为 1！");
        LOGGER.warn(tpl.getMsg());
        tpl.setStatus(false);
      }
    }
    return tpl;
  }

  @Override
  public ActYwRstatus checkPerfect(ActYwPgroot tpl) {
    return tpl;
  }
}