/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd.vo
 * @Description [[_ActYwPgroot_]]文件
 * @date 2017年6月18日 下午2:25:33
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd.vo;

import java.util.List;

import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwPtpl;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;

/**
 * 业务流程参数-业务根节点.
 * 1、根据自定义流程判断是否需要生成基本节点（开始和结束）
 * 2、
 * @author chenhao
 * @date 2017年6月18日 下午2:25:33
 *
 */
public class ActYwPgroot extends ActYwRstatus<ActYwGnode> implements ActYwPtpl{
  private ActYwGroup group; //自定义流程
  private ActYwGnode gnode; //当前新增的业务节点(node 为 StenType.ST_TASK_USER)
  private ActYwGnode startGnode; //流程前一节点
  private ActYwGnode endGnode; //流程后一节点
  private ActYwGnode preFunGnode; //流程前一业务节点
  private ActYwGnode nextFunGnode; //流程后一业务节点
  private List<ActYwGnode> startSflowGnode; //流程根节点开始节点序列线
  private List<ActYwGnode> endSflowGnode; //流程根节点结束节点序列线

  public ActYwPgroot() {
    super();
  }

  public ActYwPgroot(ActYwGroup group) {
    super();
    this.group = group;
  }

  public ActYwPgroot(ActYwGnode gnode) {
    super();
    this.gnode = gnode;
  }

  public ActYwPgroot(ActYwGroup group, ActYwGnode gnode, ActYwGnode startGnode, ActYwGnode endGnode,
      List<ActYwGnode> startSflowGnode, List<ActYwGnode> endSflowGnode) {
    super();
    this.group = group;
    this.gnode = gnode;
    this.startGnode = startGnode;
    this.endGnode = endGnode;
    this.startSflowGnode = startSflowGnode;
    this.endSflowGnode = endSflowGnode;
  }

  public ActYwPgroot(ActYwGroup group, ActYwGnode gnode, ActYwGnode startGnode, ActYwGnode endGnode, ActYwGnode preFunGnode, ActYwGnode nextFunGnode,
      List<ActYwGnode> startSflowGnode, List<ActYwGnode> endSflowGnode) {
    super();
    this.group = group;
    this.gnode = gnode;
    this.startGnode = startGnode;
    this.endGnode = endGnode;
    this.preFunGnode = preFunGnode;
    this.nextFunGnode = nextFunGnode;
    this.startSflowGnode = startSflowGnode;
    this.endSflowGnode = endSflowGnode;
  }

  public ActYwGroup getGroup() {
    return group;
  }

  public void setGroup(ActYwGroup group) {
    this.group = group;
  }

  public ActYwGnode getGnode() {
    return gnode;
  }

  public void setGnode(ActYwGnode gnode) {
    this.gnode = gnode;
  }

  public ActYwGnode getStartGnode() {
    return startGnode;
  }

  public void setStartGnode(ActYwGnode startGnode) {
    this.startGnode = startGnode;
  }

  public ActYwGnode getEndGnode() {
    return endGnode;
  }

  public void setEndGnode(ActYwGnode endGnode) {
    this.endGnode = endGnode;
  }

  public List<ActYwGnode> getStartSflowGnode() {
    return startSflowGnode;
  }

  public void setStartSflowGnode( List<ActYwGnode> startSflowGnode) {
    this.startSflowGnode = startSflowGnode;
  }

  public List<ActYwGnode> getEndSflowGnode() {
    return endSflowGnode;
  }

  public void setEndSflowGnode( List<ActYwGnode> endSflowGnode) {
    this.endSflowGnode = endSflowGnode;
  }

  public ActYwGnode getPreFunGnode() {
    return preFunGnode;
  }

  public void setPreFunGnode(ActYwGnode preFunGnode) {
    this.preFunGnode = preFunGnode;
  }

  public ActYwGnode getNextFunGnode() {
    return nextFunGnode;
  }

  public void setNextFunGnode(ActYwGnode nextFunGnode) {
    this.nextFunGnode = nextFunGnode;
  }

  public static ActYwPgroot toActYwPgroot(ActYwGnode actYwGnode, List<ActYwGnode> startSflowGnode, List<ActYwGnode> endSflowGnode) {
    return new ActYwPgroot(actYwGnode.getGroup(), actYwGnode, actYwGnode.getPreGnode(), actYwGnode.getNextGnode(),
        actYwGnode.getPreFunGnode(), actYwGnode.getNextFunGnode(), startSflowGnode, endSflowGnode);
  }

  /**
   * 下一个业务节点为空时，初始化下一个节点为结束节点.
   * @author chenhao
   * @throws
   */
  public static ActYwPgroot initNextFunGnode(ActYwPgroot param, ActYwGnode endGnode, ActYwGnode nextFunGnode) {
    if (param.getNextFunGnode() == null) {
      param.setNextFunGnode(endGnode);
    }
    if (StringUtil.isEmpty(param.getNextFunGnode().getId())) {
      param.setNextFunGnode(endGnode);
    }
    if (param.getNextFunGnode().getNode() == null) {
      param.setNextFunGnode(endGnode);
    }
    if (StringUtil.isEmpty(param.getNextFunGnode().getNode().getId())) {
      param.setNextFunGnode(endGnode);
    }

    if (!(param.getNextFunGnode()).equals(endGnode)) {
      param.setNextFunGnode(nextFunGnode);
    }

    return param;
  }
}