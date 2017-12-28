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
import com.oseasy.initiate.common.utils.IdGen;
import com.hch.platform.putil.common.utils.StringUtil;
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
   *  group 自定义流程不能为空
   *  gnode 新增业务节点不能为空
   *  startGnode 新增业务上一节点 startGnode节点不能为空
   *  endGnode 新增业务下一节点 endGnode节点不能为空
   *  startSflowGnodes 新增业务上一节点 startGnode节点和下一节点 endGnode节点的连接线不能为空
   * @author chenhao
   * @param tpl 节点参数
   * @return ActYwRgnode
   */
  @Override
  public ActYwRgnode execute(ActYwPgroot tpl) {
    ActYwRgnode actYwRgnode = new ActYwRgnode();
    ActYwRstatus<ActYwGnode> rstsusParams = checkParams(tpl);
    ActYwRstatus<ActYwGnode> rstsusBeforeEx = checkBeforeExecute(tpl);
    actYwRgnode.setStatus(rstsusParams.getStatus() && rstsusBeforeEx.getStatus());
    if (actYwRgnode.getStatus()) {
      GnodeMargeVo gnodeMargeVo = null;
      GnodeMargeVo subgnodeMargeVo = null;
      ActYwGroup group = tpl.getGroup();
      ActYwGnode gnode = tpl.getGnode();
      ActYwGnode startGnode = tpl.getStartGnode();
      ActYwGnode preFunGnode = tpl.getPreFunGnode();
      ActYwGnode nextFunGnode = tpl.getNextFunGnode();
      ActYwNode node = gnode.getNode();

      /**
       * 新增或更新业务节点.
       */
      gnode.setParent(startGnode.getParent());
      gnode.setOffice(UserUtils.getAdminOffice());
      gnode.setType(GnodeType.getByActYwNode(node, node.getLevel()).getId());
      gnode.setTypefun(StenFuntype.SFT_SELECT.getVal());
      gnode.setGroup(group);

      String spfix = (((gnode.getType()).equals(GnodeType.GT_PROCESS_START.getId()))
          || ((gnode.getType()).equals(GnodeType.GT_PROCESS_END.getId()))
          || ((gnode.getType()).equals(GnodeType.GT_PROCESS_GATEWAY.getId()))
          || ((gnode.getType()).equals(GnodeType.GT_PROCESS_FLOW.getId()))
          || ((gnode.getType()).equals(GnodeType.GT_PROCESS_TASK.getId()))) ? "-SPT-" : "";
      gnode.setRemarks("RootAddNode" + spfix + node.getName());

      gnode.setPreGnode(startGnode);
      gnode.setPreFunGnode(preFunGnode);
      gnode.setNextGnode(new ActYwGnode(nextFunGnode.getId()));
      gnode.setNextFunGnode(nextFunGnode);
      gnodeMargeVo = engine.service().margeList(group, gnode);

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
     endSflowGnodeNew.setRemarks("RootAddNode" + spfix + StenType.ST_FLOW_SEQUENCE.getRemark());

     endSflowGnodeNew.setPreGnode(gnodeMargeVo.getGnode());
     endSflowGnodeNew.setPreFunGnode(gnodeMargeVo.getGnode());
     endSflowGnodeNew.setPreGnodes(Lists.newArrayList());
     endSflowGnodeNew.setNextGnode(gnodeMargeVo.getGnode().getNextGnode());
     endSflowGnodeNew.setNextFunGnode(gnodeMargeVo.getGnode().getNextFunGnode());
     endSflowGnodeNew.setNextGnodes(Lists.newArrayList());
     /**
      * 保存 endSflowGnodeNew.
      **/
      gnodeMargeVo = engine.service().margeList(group, endSflowGnodeNew);

      if (GnodeType.getById(gnode.getType()).equals(GnodeType.GT_PROCESS)) {
        subgnodeMargeVo = executeSubProcess(gnode, gnodeMargeVo.getPreGnodes(), gnodeMargeVo);
      }

      actYwRgnode = dealVo(actYwRgnode, gnodeMargeVo, subgnodeMargeVo);
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
   *
   * 处理返回结果.
   * @param tpl
   * @param actYwRgnode
   * @param gnodeMargeVo
   * @param subgnodeMargeVo
   * @return ActYwRgnode
   */
  private ActYwRgnode dealVo(ActYwRgnode actYwRgnode, GnodeMargeVo gnodeMargeVo, GnodeMargeVo subgnodeMargeVo) {
    /**
     * 处理当前节点执行状态.
     */
    if((gnodeMargeVo != null) && (!gnodeMargeVo.getStatus())){
      actYwRgnode.setMsg(gnodeMargeVo.getMsg());
      actYwRgnode.setStatus(gnodeMargeVo.getStatus());
      return actYwRgnode;
    }

    if(subgnodeMargeVo != null){
      /**
       * 处理当前节点的子节点执行状态.
       */
      if(!subgnodeMargeVo.getStatus()){
        actYwRgnode.setMsg(gnodeMargeVo.getMsg());
        actYwRgnode.setStatus(gnodeMargeVo.getStatus());
        return actYwRgnode;
      }

      gnodeMargeVo.getGnode().setChildGnodes(subgnodeMargeVo.getChilds());
    }

    if(gnodeMargeVo != null){
      actYwRgnode.setDatas(gnodeMargeVo.getGnode());
    }
    actYwRgnode.setStatus(true);
    return actYwRgnode;
  }

  /**
   * 添加子流程操作.
   * @author chenhao
   * @param gnode 当前节点
   * @param preGnodes  前置节点列表
   */
  private GnodeMargeVo executeSubProcess(ActYwGnode gnode, List<ActYwGnode> preGnodes, GnodeMargeVo gnodeMargeVo) {
    List<ActYwGnode> childs = Lists.newArrayList();
    GnodeMargeVo subgnodeMargeVo = new GnodeMargeVo();
    String subPstartGnodeNewId = IdGen.uuid();
    String subPsflowGnodeNewId = IdGen.uuid();
    String subPendGnodeNewId = IdGen.uuid();

    /**
     * 新增根节点开始节点.
     */
    ActYwGnode subPstartGnodeNew = new ActYwGnode();
    subPstartGnodeNew.setId(subPstartGnodeNewId);
    subPstartGnodeNew.setIsNewRecord(true);
    subPstartGnodeNew.setParent(gnode);
    subPstartGnodeNew.setGroup(gnodeMargeVo.getGroup());
    subPstartGnodeNew.setTypefun(StenFuntype.SFT_SELECT.getVal());
    subPstartGnodeNew.setType(GnodeType.GT_PROCESS_START.getId());
    subPstartGnodeNew.setNodeId(StenType.ST_START_EVENT_NONE.getId());
//    subPstartGnodeNew.setName(StenType.ST_START_EVENT_NONE.getRemark());
    subPstartGnodeNew.setName("开始");
    subPstartGnodeNew.setIsShow(true);
    subPstartGnodeNew.setIsForm(false);
    subPstartGnodeNew.setSort(10);
    subPstartGnodeNew.setOffice(UserUtils.getAdminOffice());
    subPstartGnodeNew.setPreGnodes(Lists.newArrayList());
    subPstartGnodeNew.setNextGnodes(Lists.newArrayList());
    subPstartGnodeNew.setRemarks("RootAdd-SP-" + StenType.ST_START_EVENT_NONE.getRemark());

    subPstartGnodeNew.setPreGnode(gnode);
    subPstartGnodeNew.setPreFunGnode(gnode);
    subPstartGnodeNew.setPreGnodes(Lists.newArrayList());
    subPstartGnodeNew.setNextGnode(gnode);
    subPstartGnodeNew.setNextFunGnode(gnode);
    subPstartGnodeNew.setNextGnodes(Lists.newArrayList());
    /**
     * 保存 subPstartGnodeNew
     **/
    subgnodeMargeVo = engine.service().margeList(gnodeMargeVo.getGroup(), subPstartGnodeNew);
    childs.add(subgnodeMargeVo.getGnode());

    /**
     * 新增根节点开始节点和结束系欸但连接线,并关联开始节点.
     */
    ActYwGnode subPsflowGnodeNew = new ActYwGnode();
    subPsflowGnodeNew.setId(subPsflowGnodeNewId);
    subPsflowGnodeNew.setIsNewRecord(true);
    subPsflowGnodeNew.setParent(subPstartGnodeNew.getParent());
    subPsflowGnodeNew.setGroup(gnodeMargeVo.getGroup());
    subPsflowGnodeNew.setTypefun(StenFuntype.SFT_NOT_SELECT.getVal());
    subPsflowGnodeNew.setType(GnodeType.GT_PROCESS_FLOW.getId());
    subPsflowGnodeNew.setNodeId(StenType.ST_FLOW_SEQUENCE.getId());
    subPsflowGnodeNew.setName(StenType.ST_FLOW_SEQUENCE.getRemark());
    subPsflowGnodeNew.setIsShow(true);
    subPsflowGnodeNew.setIsForm(false);
    subPsflowGnodeNew.setSort(20);
    subPsflowGnodeNew.setOffice(UserUtils.getAdminOffice());
    subPsflowGnodeNew.setRemarks("RootAdd-SP-" + StenType.ST_FLOW_SEQUENCE.getRemark());

    subPsflowGnodeNew.setPreGnode(subPstartGnodeNew);
    subPsflowGnodeNew.setPreFunGnode(subPstartGnodeNew);
    subPsflowGnodeNew.setPreGnodes(Lists.newArrayList());
    subPsflowGnodeNew.setNextGnode(gnode);
    subPsflowGnodeNew.setNextFunGnode(gnode);
    subPsflowGnodeNew.setNextGnodes(Lists.newArrayList());
    /**
     * 保存 subPsflowGnodeNew
     **/
    subgnodeMargeVo = engine.service().margeList(subgnodeMargeVo.getGroup(), subPsflowGnodeNew);
    childs.add(subgnodeMargeVo.getGnode());


    /**
     * 新增根节点结束节点,并关联接线.
     */
    ActYwGnode subPendGnodeNew = new ActYwGnode();
    subPendGnodeNew.setId(subPendGnodeNewId);
    subPendGnodeNew.setIsNewRecord(true);
    subPendGnodeNew.setParent(subPstartGnodeNew.getParent());
    subPendGnodeNew.setGroup(gnodeMargeVo.getGroup());
    subPendGnodeNew.setTypefun(StenFuntype.SFT_SELECT.getVal());
    subPendGnodeNew.setType(GnodeType.GT_PROCESS_END.getId());
    subPendGnodeNew.setNodeId(StenType.ST_END_EVENT_NONE.getId());
//    subPendGnodeNew.setName(StenType.ST_END_EVENT_NONE.getRemark());
    subPendGnodeNew.setName("结束");
    subPendGnodeNew.setIsShow(true);
    subPendGnodeNew.setIsForm(false);
    subPendGnodeNew.setSort(30);
    subPendGnodeNew.setOffice(UserUtils.getAdminOffice());
    subPendGnodeNew.setRemarks("RootAdd-SP-" + StenType.ST_END_EVENT_NONE.getRemark());

    subPendGnodeNew.setPreGnode(subPsflowGnodeNew);
    subPendGnodeNew.setPreFunGnode(subPstartGnodeNew);
    subPendGnodeNew.setPreGnodes(Lists.newArrayList());
    subPendGnodeNew.setNextGnode(gnode);
    subPendGnodeNew.setNextFunGnode(gnode);
    subPendGnodeNew.setNextGnodes(Lists.newArrayList());
    subgnodeMargeVo = engine.service().margeList(subgnodeMargeVo.getGroup(), subPendGnodeNew);
    childs.add(subgnodeMargeVo.getGnode());

    subgnodeMargeVo.setChilds(childs);
    return subgnodeMargeVo;
  }

  @Override
  public ActYwRstatus<ActYwGnode> checkBeforeExecute(ActYwPgroot tpl) {
    return new ActYwCnRootAdd(engine).checkPerfect(tpl.getGroup());
  }

  @Override
  public ActYwRstatus<ActYwGnode> checkParams(ActYwPgroot tpl) {
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

//      if ((tpl.getStartSflowGnode() == null) || (tpl.getStartSflowGnode().size() != 1)) {
//        tpl.setMsg("ActYwCnRootAddNode 命令[startSflowGnodes]不能为空，且只能为 1！");
//        LOGGER.warn(tpl.getMsg());
//        tpl.setStatus(false);
//      }
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