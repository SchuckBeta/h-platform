package com.oseasy.initiate.modules.actyw.tool.process;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtChildShapes;

public class ActYwGnodeTool {
  private String rootId;
  private String rootStartId;
  private String rootEndId;

  /**
   * 需要处理的所有流程节点.
   */
  private List<ActYwGnode> actYwGnodes;
  /**
   * (level = 1)级流程节点.
   */
  private List<ActYwGnode> actYwGnodeProcess;
  /**
   * (level = 2)级流程节点.
   */
  private List<ActYwGnode> actYwGnodePsubs;

  public ActYwGnodeTool() {
    super();
  }

  public ActYwGnodeTool(List<ActYwGnode> actYwGnodes) {
    super();
    this.actYwGnodes = actYwGnodes;
  }

  public ActYwGnodeTool(List<ActYwGnode> actYwGnodes, List<ActYwGnode> actYwGnodeProcess,
      List<ActYwGnode> actYwGnodePsubs) {
    super();
    this.actYwGnodes = actYwGnodes;
    this.actYwGnodeProcess = actYwGnodeProcess;
    this.actYwGnodePsubs = actYwGnodePsubs;
  }

  public List<ActYwGnode> getActYwGnodes() {
    return actYwGnodes;
  }

  public void setActYwGnodes(List<ActYwGnode> actYwGnodes) {
    this.actYwGnodes = actYwGnodes;
  }

  public List<ActYwGnode> getActYwGnodeProcess() {
    return actYwGnodeProcess;
  }

  public List<ActYwGnode> getActYwGnodePsubs() {
    return actYwGnodePsubs;
  }

  public String getRootId() {
    return rootId;
  }

  public void setRootId(String rootId) {
    this.rootId = rootId;
  }

  public String getRootStartId() {
    return rootStartId;
  }

  public void setRootStartId(String rootStartId) {
    this.rootStartId = rootStartId;
  }

  public String getRootEndId() {
    return rootEndId;
  }

  public void setRootEndId(String rootEndId) {
    this.rootEndId = rootEndId;
  }

  public void setActYwGnodeProcess(List<ActYwGnode> actYwGnodeProcess) {
    this.actYwGnodeProcess = actYwGnodeProcess;
  }

  public void setActYwGnodePsubs(List<ActYwGnode> actYwGnodePsubs) {
    this.actYwGnodePsubs = actYwGnodePsubs;
  }

  /**
   * 根据 actYwGnodes 初始化一级、二级流程节点.
   * @param actYwGnodes
   * @return
   */
  public static ActYwGnodeTool gen(List<ActYwGnode> actYwGnodes) {
    if (actYwGnodes == null) {
      return null;
    }
    ActYwGnodeTool actYwGnodeTool = new ActYwGnodeTool();
    /**
     * (level = 1)级流程节点.
     */
    List<ActYwGnode> aywGnodeProcess = Lists.newArrayList();
    /**
     * (level = 2)级流程节点.
     */
    List<ActYwGnode> aywGnodePsubs = Lists.newArrayList();
    for (ActYwGnode actYgnode : actYwGnodes) {
      GnodeType gnodeType = GnodeType.getById(actYgnode.getType());
      if ((gnodeType).equals(GnodeType.GT_ROOT_START)){
        actYwGnodeTool.setRootStartId(actYgnode.getId());
      }
      if ((gnodeType).equals(GnodeType.GT_ROOT_END)){
        actYwGnodeTool.setRootEndId(actYgnode.getId());
      }

      if ((gnodeType).equals(GnodeType.GT_ROOT_START) || (gnodeType).equals(GnodeType.GT_ROOT_FLOW)
          || (gnodeType).equals(GnodeType.GT_ROOT_END)
          || (gnodeType).equals(GnodeType.GT_PROCESS)) {
        aywGnodeProcess.add(actYgnode);
      } else if ((gnodeType).equals(GnodeType.GT_PROCESS_START)
          || (gnodeType).equals(GnodeType.GT_PROCESS_FLOW)
          || (gnodeType).equals(GnodeType.GT_PROCESS_GATEWAY)
          || (gnodeType).equals(GnodeType.GT_PROCESS_END)
          || (gnodeType).equals(GnodeType.GT_PROCESS_TASK)) {
        aywGnodePsubs.add(actYgnode);
      }
    }

    actYwGnodeTool.setActYwGnodes(actYwGnodes);
    actYwGnodeTool.setActYwGnodeProcess(aywGnodeProcess);
    actYwGnodeTool.setActYwGnodePsubs(aywGnodePsubs);
    return actYwGnodeTool;
  }
}
