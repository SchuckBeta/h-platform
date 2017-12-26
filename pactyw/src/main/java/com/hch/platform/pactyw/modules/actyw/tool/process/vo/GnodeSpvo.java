package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;

/**
 * 参数节点.
 * @author chenhao
 *
 */
public class GnodeSpvo {
  private Boolean isRoot;//是否有网关
  private Boolean isProcess;//是否有网关
  private Boolean hasGateWay;//是否有网关
  private Boolean hasReturnGnode;//是否有驳回

  private ActYwGnode root;//流程根节点(level=0)
  private ActYwGnode process;//子流程节点(level=1)
  private ActYwGnode gateWay;//网关节点(level=2)
  private ActYwGnode returnGnode;//驳回节点(level=2)
  private ActYwGnode ywGnode;//业务节点(level=2)
  private ActYwGnode ywPreGnode;//业务节点(level=2)-前一个
  private ActYwGnode ywNextGnode;//业务节点(level=2)-后一个

  private List<ActYwGnode> ywSlibGnodes;//同级别业务节点(level=2)-同级节点ID-不包含自己



  public GnodeSpvo() {
    super();
  }

  public GnodeSpvo(ActYwGnode gnode) {
    super();
    this.ywGnode = gnode;
  }

  public GnodeSpvo(Boolean isRoot, Boolean isProcess, Boolean hasGateWay, Boolean hasReturnGnode,
      ActYwGnode root, ActYwGnode process, ActYwGnode gateWay, ActYwGnode returnGnode,
      ActYwGnode ywGnode, ActYwGnode ywPreGnode, ActYwGnode ywNextGnode,
      List<ActYwGnode> ywSlibGnodes) {
    super();
    this.isRoot = isRoot;
    this.isProcess = isProcess;
    this.hasGateWay = hasGateWay;
    this.hasReturnGnode = hasReturnGnode;
    this.root = root;
    this.process = process;
    this.gateWay = gateWay;
    this.returnGnode = returnGnode;
    this.ywGnode = ywGnode;
    this.ywPreGnode = ywPreGnode;
    this.ywNextGnode = ywNextGnode;
    this.ywSlibGnodes = ywSlibGnodes;
  }

  public Boolean getIsRoot() {
    if(this.isRoot == null){
      this.isRoot = false;
    }
    return isRoot;
  }

  public Boolean getIsProcess() {
    if(this.isProcess == null){
      this.isProcess = false;
    }
    return isProcess;
  }

  public Boolean getHasGateWay() {
    if(this.hasGateWay == null){
      this.hasGateWay = false;
    }
    return hasGateWay;
  }

  public Boolean getHasReturnGnode() {
    if(this.hasReturnGnode == null){
      this.hasReturnGnode = false;
    }
    return hasReturnGnode;
  }

  public void setIsRoot(Boolean isRoot) {
    this.isRoot = isRoot;
  }

  public void setIsProcess(Boolean isProcess) {
    this.isProcess = isProcess;
  }

  public void setHasGateWay(Boolean hasGateWay) {
    this.hasGateWay = hasGateWay;
  }

  public void setHasReturnGnode(Boolean hasReturnGnode) {
    this.hasReturnGnode = hasReturnGnode;
  }

  public ActYwGnode getRoot() {
    return root;
  }
  public void setRoot(ActYwGnode root) {
    this.root = root;
  }
  public ActYwGnode getProcess() {
    return process;
  }
  public void setProcess(ActYwGnode process) {
    this.process = process;
  }
  public ActYwGnode getGateWay() {
    return gateWay;
  }
  public void setGateWay(ActYwGnode gateWay) {
    this.gateWay = gateWay;
  }
  public ActYwGnode getReturnGnode() {
    return returnGnode;
  }
  public void setReturnGnode(ActYwGnode returnGnode) {
    this.returnGnode = returnGnode;
  }
  public ActYwGnode getYwGnode() {
    return ywGnode;
  }
  public void setYwGnode(ActYwGnode ywGnode) {
    this.ywGnode = ywGnode;
  }
  public ActYwGnode getYwPreGnode() {
    return ywPreGnode;
  }
  public void setYwPreGnode(ActYwGnode ywPreGnode) {
    this.ywPreGnode = ywPreGnode;
  }
  public ActYwGnode getYwNextGnode() {
    return ywNextGnode;
  }
  public void setYwNextGnode(ActYwGnode ywNextGnode) {
    this.ywNextGnode = ywNextGnode;
  }
  public List<ActYwGnode> getYwSlibGnodes() {
    return ywSlibGnodes;
  }
  public void setYwSlibGnodes(List<ActYwGnode> ywSlibGnodes) {
    this.ywSlibGnodes = ywSlibGnodes;
  }
}
