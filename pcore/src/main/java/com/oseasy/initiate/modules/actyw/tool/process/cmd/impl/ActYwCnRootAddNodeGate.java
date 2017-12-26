/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd.impl
 * @Description [[_ActYwCnRootAddGateGate_]]文件
 * @date 2017年6月19日 上午10:03:56
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
import com.oseasy.initiate.modules.actyw.tool.process.rest.GnodeMargeVo;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenEsubType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenFuntype;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 业务流程根节点操作类-业务根节点-添加业务节点（已存在一个业务节点-添加网关）.
 * B:当存在 1个节点时新增业务节点（添加网关）:
 * 该方法在流程添加第二个业务节点的时候调用：
 *  1、新增或更新业务节点
 *  2、新增业务节点和结束节点连接线,并关联新增业务节点.
 *  3、更新业务节点下个节点连接线.
 *  4、更新根节点开始节点与业务节点连接线.
 *  5、新增网关
 *  6、新增网关节点和上一节点连接线,并关联网关节点和上一节点.
 *  7、上一节点关联网关连接线节点
 *  8、网关关联网关连接线节点
 *  9、上一节点连接线关联到节点
 *  10、按顺序返回所有节点.
 *
 * @author chenhao
 * @date 2017年6月19日 上午10:03:56
 *
 */
@Service
public class ActYwCnRootAddNodeGate extends ActYwAbsEngine<ActYwEngineImpl> implements ActYwCommand<ActYwPgroot>, ActYwCommandCheck<ActYwPgroot>{
  protected static final Logger LOGGER = Logger.getLogger(ActYwCnRootAddNodeGate.class);

  public ActYwCnRootAddNodeGate() {
    super();
  }

  public ActYwCnRootAddNodeGate(ActYwEngineImpl engine) {
    super(engine);
  }

  /**
   *执行必要参数 .
   *  group 自定义流程不能为空
   *  gnode 新增业务节点不能为空
   *  startGnode 新增业务上一节点 startGnode节点不能为空
   *  endGnode 新增业务下一节点 endGnode节点不能为空
   *  startSflowGnodes 新增业务上一节点 startGnode节点所有的连接线列表不能为空
   * @author chenhao
   * @param tpl 模板参数
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
      ActYwGnode endGnode = tpl.getEndGnode();
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
      gnode.setPreGnodes(Lists.newArrayList());
      gnode.setNextGnodes(Lists.newArrayList());
      gnode.setRemarks("RootAddNodeGate"+node.getName());

      //TODO 保存或更新 gnode
      gnode.setPreGnode(startGnode);
      gnode.setPreFunGnode(preFunGnode);
      gnode.setNextGnode(new ActYwGnode(nextFunGnode.getId()));
      gnode.setNextFunGnode(nextFunGnode);
      GnodeMargeVo gnodeMargeVo = engine.service().margeList(group, gnode);


      /**
       * 新增业务节点和结束节点连接线,并关联新增业务节点.
       */
      ActYwGnode endSflowGnodeNew = new ActYwGnode();
      endSflowGnodeNew.setParent(startGnode.getParent());
      endSflowGnodeNew.setGroup(group);
      endSflowGnodeNew.setTypefun(StenFuntype.SFT_NOT_SELECT.getVal());
      if ((node.getLevel()).equals(RtSvl.RtLevelVal.RT_LV1)) {
        endSflowGnodeNew.setType(GnodeType.getByActYwNodeAndNodekey(node, node.getLevel(), StenType.ST_FLOW_SEQUENCE.getKey()).getId());
      } else if ((node.getLevel()).equals(RtSvl.RtLevelVal.RT_LV2)) {
        endSflowGnodeNew.setType(GnodeType.getByActYwNodeAndNodekey(node, RtSvl.RtLevelVal.RT_LV1, StenType.ST_FLOW_SEQUENCE.getKey()).getId());
      }
      endSflowGnodeNew.setNodeId(StenType.ST_FLOW_SEQUENCE.getId());
      endSflowGnodeNew.setName(StenType.ST_FLOW_SEQUENCE.getRemark());
      endSflowGnodeNew.setIsShow(true);
      endSflowGnodeNew.setIsForm(false);
      endSflowGnodeNew.setSort(100);
      endSflowGnodeNew.setOffice(UserUtils.getAdminOffice());
      endSflowGnodeNew.setRemarks("RootAddNodeGate" + StenType.ST_FLOW_SEQUENCE.getRemark());
      //TODO 保存 endSflowGnodeNew

//    endSflowGnodeNew.setPreGnode(gnode);
//    endSflowGnodeNew.setPreFunGnode(gnode);
      endSflowGnodeNew.setPreGnode(gnodeMargeVo.getGnode());
      endSflowGnodeNew.setPreFunGnode(gnodeMargeVo.getGnode());
      endSflowGnodeNew.setPreGnodes(Lists.newArrayList());
      endSflowGnodeNew.setNextGnode(nextFunGnode);
      endSflowGnodeNew.setNextFunGnode(nextFunGnode);
//      endSflowGnodeNew.setNextGnode(gnodeMargeVo.getGnode().getNextGnode());
//      endSflowGnodeNew.setNextFunGnode(gnodeMargeVo.getGnode().getNextFunGnode());
      endSflowGnodeNew.setNextGnodes(Lists.newArrayList());
      //TODO 保存 endSflowGnodeNew
       gnodeMargeVo = engine.service().margeList(group, endSflowGnodeNew);


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

      /**********************************************************************
       * 新增网关节点和上一节点连接线,并关联网关节点和上一节点.
       */
      ActYwGnode gatewaySflowGnodeNew = new ActYwGnode();
      gatewaySflowGnodeNew.setParent(gnode.getParent());
      gatewaySflowGnodeNew.setGroup(group);
      gatewaySflowGnodeNew.setTypefun(StenFuntype.SFT_NOT_SELECT.getVal());
      gatewaySflowGnodeNew.setType(GnodeType.getByActYwNodeAndNodekey(node, RtSvl.RtLevelVal.RT_LV1, StenType.ST_FLOW_SEQUENCE.getKey()).getId());
      gatewaySflowGnodeNew.setNodeId(StenType.ST_FLOW_SEQUENCE.getId());
      gatewaySflowGnodeNew.setName(StenType.ST_FLOW_SEQUENCE.getRemark());
      gatewaySflowGnodeNew.setIsShow(true);
      gatewaySflowGnodeNew.setIsForm(false);
      gatewaySflowGnodeNew.setSort(100);
      gatewaySflowGnodeNew.setOffice(UserUtils.getAdminOffice());
      gatewaySflowGnodeNew.setPreGnode(preFunGnode);
      gatewaySflowGnodeNew.setPreFunGnode(preFunGnode);
      gatewaySflowGnodeNew.setPreGnodes(Lists.newArrayList());
      gatewaySflowGnodeNew.setNextGnode(null);
      gatewaySflowGnodeNew.setNextFunGnode(null);
      gatewaySflowGnodeNew.setNextGnodes(Lists.newArrayList());
      gatewaySflowGnodeNew.setRemarks("RootAddNodeGate111" + StenType.ST_FLOW_SEQUENCE.getRemark());
      //TODO 保存 gatewaySflowGnodeNew
      engine.service().save(gatewaySflowGnodeNew);

      /**
       * 新增网关节点.
       */
      ActYwGnode gatewayGnodeNew = new ActYwGnode();
      gatewayGnodeNew.setParent(gnode.getParent());
      gatewayGnodeNew.setGroup(group);
      gatewayGnodeNew.setTypefun(StenFuntype.SFT_NOT_SELECT.getVal());
      gatewayGnodeNew.setType(GnodeType.getByActYwNodeAndNodekey(node, node.getLevel(), StenType.ST_GATEWAY_EXCLUSIVE.getKey()).getId());
      gatewayGnodeNew.setNodeId(StenType.ST_GATEWAY_EXCLUSIVE.getId());
      gatewayGnodeNew.setName(StenType.ST_GATEWAY_EXCLUSIVE.getRemark());
      gatewayGnodeNew.setIsShow(true);
      gatewayGnodeNew.setIsForm(false);
      gatewayGnodeNew.setSort(100);
      gatewayGnodeNew.setOffice(UserUtils.getAdminOffice());
      gatewayGnodeNew.setPreGnode(gatewaySflowGnodeNew);
      gatewayGnodeNew.setPreFunGnode(preFunGnode);
      gatewayGnodeNew.setPreGnodes(Lists.newArrayList());
      gatewayGnodeNew.getPreGnodes().add(gatewaySflowGnodeNew);

      gatewayGnodeNew.setNextGnode(null);
      gatewayGnodeNew.setNextFunGnode(null);
      gatewayGnodeNew.setNextGnodes(Lists.newArrayList());
//      gatewayGnodeNew.getNextGnodes().addAll(engine.service().findSlibingsByParentId(gnode));
      gatewayGnodeNew.getNextGnodes().add(gnode);
      gatewayGnodeNew.setNextFunGnodes(Lists.newArrayList());
      gatewayGnodeNew.getNextFunGnodes().add(gnode);
//      gatewayGnodeNew.getNextFunGnodes().add(engine.service().findSlibingsByParentId(gnode));
      gatewayGnodeNew.setRemarks("RootAddNodeGate" + StenType.ST_GATEWAY_EXCLUSIVE.getRemark());
      //TODO 保存 gatewayGnodeNew
      engine.service().save(gatewayGnodeNew);

      /**
       * 上一业务节点关联网关和网关连接线节点.
       */
      preFunGnode.setNextGnode(gatewaySflowGnodeNew);
      preFunGnode.setNextFunGnode(gatewayGnodeNew);
      preFunGnode.setNextGnodes(Lists.newArrayList());
      preFunGnode.getNextGnodes().add(gatewaySflowGnodeNew);
      preFunGnode.setNextFunGnodes(Lists.newArrayList());
      preFunGnode.getNextFunGnodes().add(gatewayGnodeNew);
      //TODO 更新 startGnode
      engine.service().save(preFunGnode);

      /**
       * 前置业务节点更新与网关节点关系.
       */
      startGnode.setPreGnode(gatewayGnodeNew);
      startGnode.setPreGnodes(Lists.newArrayList());
      startGnode.getPreGnodes().add(gatewayGnodeNew);
      startGnode.setPreFunGnode(gatewayGnodeNew);
      startGnode.setPreFunGnodes(Lists.newArrayList());
      startGnode.getPreFunGnodes().add(gatewayGnodeNew);
      //TODO 更新 startGnode
      engine.service().save(startGnode);

      /**
       * 业务节点更新与网关节点关系.
       */
      gnode.setPreGnode(startGnode);
      gnode.getPreGnodes().add(startGnode);
      gnode.setPreFunGnode(gatewayGnodeNew);
      gnode.getPreGnodes().add(gatewayGnodeNew);
      //TODO 更新 gnode
      engine.service().save(gnode);

      /**
       * 业务节点更新与网关节点关系.
       */
      if(endGnode == null){
        endGnode = gnode.getNextGnode();
      }
      endGnode.setPreGnode(gnode);
      endGnode.setPreGnodes(Lists.newArrayList());
      endGnode.getPreGnodes().add(gnode);
      endGnode.setPreFunGnode(gnode);
      endGnode.setPreFunGnodes(Lists.newArrayList());
      endGnode.getPreFunGnodes().add(gnode);
      //TODO 更新 gnode
      engine.service().save(endGnode);

      /**
       * 上一节点连接线关联到节点.
       */
      for (ActYwGnode nfGnode : nextFlowGnodes) {
        nfGnode.setPreGnode(gnode);
        nfGnode.setPreFunGnode(gnode);
        nfGnode.getPreGnodes().add(gnode);
        nfGnode.getPreFunGnodes().add(gnode);
        engine.service().save(nfGnode);
      }
      //TODO 批量更新 nextFlowGnodes

      List<ActYwGnode> slibingsGnodes = engine.service().findSlibings(group.getId(), startGnode.getId());
      for (ActYwGnode slibingsGnode : slibingsGnodes) {
        slibingsGnode.setPreGnode(gatewayGnodeNew);
        slibingsGnode.setPreFunGnode(gatewayGnodeNew);
        slibingsGnode.setPreGnodes(Lists.newArrayList());
        slibingsGnode.getPreGnodes().add(gatewayGnodeNew);
        slibingsGnode.setPreFunGnodes(Lists.newArrayList());
        slibingsGnode.getPreFunGnodes().add(gatewayGnodeNew);
        engine.service().save(slibingsGnode);
      }

      List<ActYwGnode> slibingsFunGnodes = engine.service().findFunSlibings(group.getId(), preFunGnode.getId());
      for (ActYwGnode slibingsFunGnode : slibingsFunGnodes) {
        slibingsFunGnode.setPreFunGnode(gatewayGnodeNew);
        slibingsFunGnode.setPreFunGnodes(Lists.newArrayList());
        slibingsFunGnode.getPreFunGnodes().add(gatewayGnodeNew);
        engine.service().save(slibingsFunGnode);
      }

      /**
       * 按顺序返回所有节点.
       */
      gnodes.add(startGnode);
      gnodes.add(gatewaySflowGnodeNew);
      gnodes.add(gatewayGnodeNew);
      gnodes.addAll(preFlowGnodes);
      gnodes.add(gnode);
      gnodes.addAll(nextFlowGnodes);
      gnodes.add(gnode.getNextGnode());

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
  public ActYwRstatus<ActYwGnode> checkPerfect(ActYwPgroot tpl) {
    // TODO Auto-generated method stub
    return tpl;
  }

  @Override
  public ActYwRstatus<ActYwGnode> initParams(ActYwPgroot tpl) {
    // TODO Auto-generated method stub
    return tpl;
  }
}