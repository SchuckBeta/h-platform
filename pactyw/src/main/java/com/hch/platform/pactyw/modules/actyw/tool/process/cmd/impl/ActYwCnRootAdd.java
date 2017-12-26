/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd.impl
 * @Description [[_ActYwCnRootAdd_]]文件
 * @date 2017年6月18日 下午2:22:47
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwAbsEngine;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwCommand;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwCommandCheck;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.vo.ActYwRgroot;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenEsubType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenFuntype;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 业务流程根节点操作类-业务根节点-添加.
 * 该方法在自定义流程创建的时候调用，生成流程的基本节点数据
 *  1、开始无事件节点
 *  2、结束无事件节点
 *
 * @author chenhao
 * @date 2017年6月18日 下午2:22:47
 *
 */
@Service
public class ActYwCnRootAdd extends ActYwAbsEngine<ActYwEngineImpl> implements ActYwCommand<ActYwGroup>, ActYwCommandCheck<ActYwGroup>{
  protected static final Logger LOGGER = Logger.getLogger(ActYwCnRootAdd.class);

  public ActYwCnRootAdd() {
    super();
  }

  public ActYwCnRootAdd(ActYwEngineImpl engine) {
    super(engine);
  }

  @Override
  public ActYwRgroot execute(ActYwGroup group) {
    ActYwRgroot actYwRgroot = new ActYwRgroot();
    ActYwRstatus<ActYwGnode> rstsusParams = checkParams(group);
    ActYwRstatus<ActYwGnode> rstsusBeforeEx = checkBeforeExecute(group);
    actYwRgroot.setStatus(rstsusParams.getStatus() && rstsusBeforeEx.getStatus());
    if (actYwRgroot.getStatus()) {
      /**
       * 新增根节点开始节点.
       */
      ActYwGnode startGnodeNew = new ActYwGnode();
      startGnodeNew.setParent(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      startGnodeNew.setGroup(group);
      startGnodeNew.setTypefun(StenFuntype.SFT_SELECT.getVal());
      startGnodeNew.setType(GnodeType.GT_ROOT_START.getId());
      startGnodeNew.setNodeId(StenType.ST_START_EVENT_NONE.getId());
//      startGnodeNew.setName(StenType.ST_START_EVENT_NONE.getRemark());
      startGnodeNew.setName("开始");
      startGnodeNew.setIsShow(true);
      startGnodeNew.setIsForm(false);
      startGnodeNew.setSort(10);
      startGnodeNew.setOffice(UserUtils.getAdminOffice());
      startGnodeNew.setPreGnode(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      startGnodeNew.setPreGnodes(Lists.newArrayList());
      startGnodeNew.getPreGnodes().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      startGnodeNew.setPreGnodess(Lists.newArrayList());
      startGnodeNew.getPreGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      startGnodeNew.setPreFunGnode(new ActYwGnode(SysIds.SYS_TREE_PROOT.getId()));
      startGnodeNew.setNextGnodes(Lists.newArrayList());
      startGnodeNew.setNextGnodess(Lists.newArrayList());
      startGnodeNew.setRemarks("RootAdd"+StenType.ST_START_EVENT_NONE.getRemark());
      //TODO 保存 startGnodeNew
      engine.service().save(startGnodeNew);

      /**
       * 新增根节点开始节点和结束系欸但连接线,并关联开始节点.
       */
      ActYwGnode sflowGnodeNew = new ActYwGnode();
      sflowGnodeNew.setParent(startGnodeNew.getParent());
      sflowGnodeNew.setGroup(group);
      sflowGnodeNew.setTypefun(StenFuntype.SFT_NOT_SELECT.getVal());
      sflowGnodeNew.setType(GnodeType.GT_ROOT_FLOW.getId());
      sflowGnodeNew.setNodeId(StenType.ST_FLOW_SEQUENCE.getId());
      sflowGnodeNew.setName(StenType.ST_FLOW_SEQUENCE.getRemark());
      sflowGnodeNew.setIsShow(true);
      sflowGnodeNew.setIsForm(false);
      sflowGnodeNew.setSort(20);
      sflowGnodeNew.setOffice(UserUtils.getAdminOffice());
      sflowGnodeNew.setPreGnode(startGnodeNew);
      sflowGnodeNew.setPreGnodes(Lists.newArrayList());
      sflowGnodeNew.getPreGnodes().add(startGnodeNew);
      sflowGnodeNew.setPreGnodess(Lists.newArrayList());
      sflowGnodeNew.getPreGnodess().addAll(startGnodeNew.getPreGnodess());
      sflowGnodeNew.getPreGnodess().add(startGnodeNew);
      sflowGnodeNew.setPreFunGnode(startGnodeNew);
      sflowGnodeNew.setNextGnodes(Lists.newArrayList());
      sflowGnodeNew.setNextGnodess(Lists.newArrayList());
      sflowGnodeNew.setRemarks("RootAdd"+StenType.ST_FLOW_SEQUENCE.getRemark());
      //TODO 保存 sflowGnodeNew
      engine.service().save(sflowGnodeNew);

      /**
       * 新增根节点结束节点,并关联接线.
       */
      ActYwGnode endGnodeNew = new ActYwGnode();
      endGnodeNew.setParent(startGnodeNew.getParent());
      endGnodeNew.setGroup(group);
      endGnodeNew.setTypefun(StenFuntype.SFT_SELECT.getVal());
      endGnodeNew.setType(GnodeType.GT_ROOT_END.getId());
      endGnodeNew.setNodeId(StenType.ST_END_EVENT_NONE.getId());
//      endGnodeNew.setName(StenType.ST_END_EVENT_NONE.getRemark());
      endGnodeNew.setName("结束");
      endGnodeNew.setIsShow(true);
      endGnodeNew.setIsForm(false);
      endGnodeNew.setSort(30);
      endGnodeNew.setOffice(UserUtils.getAdminOffice());
      endGnodeNew.setPreGnode(sflowGnodeNew);
      endGnodeNew.setPreFunGnode(startGnodeNew);
      endGnodeNew.setPreGnodes(Lists.newArrayList());
      endGnodeNew.getPreGnodes().add(sflowGnodeNew);
      endGnodeNew.setPreGnodess(Lists.newArrayList());
      endGnodeNew.getPreGnodess().addAll(sflowGnodeNew.getPreGnodess());
      endGnodeNew.getPreGnodess().add(sflowGnodeNew);
      endGnodeNew.setNextGnode(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      endGnodeNew.setNextGnodes(Lists.newArrayList());
      endGnodeNew.getNextGnodes().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      endGnodeNew.setNextGnodess(Lists.newArrayList());
      endGnodeNew.getNextGnodess().add(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      endGnodeNew.setRemarks("RootAdd"+StenType.ST_END_EVENT_NONE.getRemark());
      //TODO 保存 endGnodeNew
      engine.service().save(endGnodeNew);

      /**
       * 更新连接线与结束节点关联.
       */
      sflowGnodeNew.setNextGnode(endGnodeNew);
      sflowGnodeNew.getNextGnodes().add(endGnodeNew);
      sflowGnodeNew.getNextGnodess().addAll(endGnodeNew.getNextGnodess());
      sflowGnodeNew.getNextGnodess().add(endGnodeNew);
      sflowGnodeNew.setNextFunGnode(endGnodeNew);

      sflowGnodeNew.setNextIds(ActYwGnode.genIds(sflowGnodeNew.getNextGnodes()));
      sflowGnodeNew.setNextIdss(ActYwGnode.genIdss(sflowGnodeNew.getNextGnode().getNextIdss(), sflowGnodeNew.getNextId()));
      //TODO 更新 sflowGnodeNew
      engine.service().save(sflowGnodeNew);

      /**
       * 更新开始节点与连接线关联.
       */
      startGnodeNew.setNextGnode(sflowGnodeNew);
      startGnodeNew.getNextGnodes().add(sflowGnodeNew);
      startGnodeNew.getNextGnodess().addAll(sflowGnodeNew.getNextGnodess());
      startGnodeNew.getNextGnodess().add(sflowGnodeNew);
      startGnodeNew.setNextFunGnode(endGnodeNew);

      startGnodeNew.setNextIds(ActYwGnode.genIds(startGnodeNew.getNextGnodes()));
      startGnodeNew.setNextIdss(ActYwGnode.genIdss(startGnodeNew.getNextGnode().getNextIdss(), startGnodeNew.getNextId()));
      //TODO 更新 startGnodeNew
      engine.service().save(startGnodeNew);

//      actYwRgroot.setStartGnode(startGnodeNew);
//      actYwRgroot.setSflowGnode(sflowGnodeNew);
//      actYwRgroot.setEndGnode(endGnodeNew);
      actYwRgroot.setStatus(true);
    }else{
      if (!rstsusParams.getStatus()) {
        actYwRgroot.setMsg(rstsusParams.getMsg());
      }
      if (!rstsusBeforeEx.getStatus()) {
        actYwRgroot.setMsg(rstsusBeforeEx.getMsg());
      }
    }
    return actYwRgroot;
  }

  /**
   * 判断自定义流程下没有节点说明需要新增.
   * @author chenhao
   * @param group ActYwGnode
   * @return Boolean
   */
  @Override
  public ActYwRstatus<ActYwGnode> checkBeforeExecute(ActYwGroup group) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>();

    List<ActYwGnode> nodes = engine.service().findList(new ActYwGnode(group));
    if(group.getIsNewRecord()){
      if ((nodes == null) || (nodes.isEmpty())) {
        return rstatus;
      }

      rstatus.setMsg("ActYwCnRootAdd 执行[checkBeforeExecute]条件不满足！");
      LOGGER.warn(rstatus.getMsg());
      rstatus.setStatus(false);
    }

    return rstatus;
  }

  /**
   * 判断自定义流程根节点是否完整.
   * @author chenhao
   * @param group ActYwGnode
   * @return Boolean
   */
  @Override
  public ActYwRstatus<ActYwGnode> checkPerfect(ActYwGroup group) {
    List<ActYwGnode> nodes = engine.service().findList(new ActYwGnode(group));
    Boolean isHasStart = false;
    Boolean isHasEnd = false;
    Boolean isHasFlow = false;

    for (ActYwGnode node : nodes) {
      StenType nstype = StenType.getByKey(node.getNode().getNodeKey());
      if ((nstype.getSubtype()).equals(StenEsubType.SES_EVENT_START)) {
        isHasStart = true;
      }else if ((nstype.getSubtype()).equals(StenEsubType.SES_EVENT_END)) {
        isHasEnd = true;
      }else if ((nstype.getSubtype()).equals(StenEsubType.SES_FLOW)) {
        isHasFlow = true;
      }
    }

    if (!(isHasStart && isHasEnd && isHasFlow)) {
      group.getRstatus().setStatus(false);
      group.getRstatus().setMsg("ActYwCnRootAdd 命令[checkPerfect]检查失败！");
      LOGGER.warn(group.getRstatus().getMsg());
    }
    return group.getRstatus();
  }

  @Override
  public ActYwRstatus<ActYwGnode> checkParams(ActYwGroup group) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>();
    if (group == null) {
      rstatus.setMsg("ActYwCnRootAdd 命令[group]不能为空！");
      LOGGER.warn(rstatus.getMsg());
      rstatus.setStatus(false);
    }
    return rstatus;
  }

  @Override
  public ActYwRstatus<ActYwGnode> initParams(ActYwGroup tpl) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>();
    // TODO Auto-generated method stub
    return rstatus;
  }
}

