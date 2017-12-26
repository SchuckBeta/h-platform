package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.IvalidateRtpl;

public class GnodeVrtpl implements IvalidateRtpl{
  private List<ActYwGnode> gnodes;
  private ActYwGnode pre;
  private ActYwGnode flow;
  private ActYwGnode next;

  public GnodeVrtpl() {
    super();
  }

  public GnodeVrtpl(List<ActYwGnode> gnodes) {
    super();
    this.gnodes = gnodes;
  }

  public GnodeVrtpl(List<ActYwGnode> gnodes, ActYwGnode pre, ActYwGnode flow, ActYwGnode next) {
    super();
    this.gnodes = gnodes;
    this.pre = pre;
    this.flow = flow;
    this.next = next;
  }

  public List<ActYwGnode> getGnodes() {
    return gnodes;
  }

  public void setGnodes(List<ActYwGnode> gnodes) {
    this.gnodes = gnodes;
  }

  public ActYwGnode getPre() {
    return pre;
  }

  public void setPre(ActYwGnode pre) {
    this.pre = pre;
  }

  public ActYwGnode getFlow() {
    return flow;
  }

  public void setFlow(ActYwGnode flow) {
    this.flow = flow;
  }

  public ActYwGnode getNext() {
    return next;
  }

  public void setNext(ActYwGnode next) {
    this.next = next;
  }
}
