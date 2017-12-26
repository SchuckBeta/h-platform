package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.IcmdPtpl;

public class Ggroup implements IcmdPtpl, Cloneable{
  private String id;//流程标识
  private ActYwGnode snode;//需要修改的节点
  private ActYwGnode gnode;//需要修改的节点参数
  private ActYwGnode parent;
  private ActYwGnode preGnode;
  private ActYwGnode preFunGnode;
  private List<ActYwGnode> preGnodes;
  private List<ActYwGnode> preFunGnodes;
  private ActYwGnode nextGnode;
  private ActYwGnode nextFunGnode;
  private List<ActYwGnode> nextGnodes;
  private List<ActYwGnode> nextFunGnodes;

  public Ggroup() {
    super();
  }

  public Ggroup(String id) {
    super();
    this.id = id;
  }

  public Ggroup(ActYwGnode preFunGnode, ActYwGnode nextFunGnode) {
    super();
    this.preFunGnode = preFunGnode;
    this.nextFunGnode = nextFunGnode;
  }

  public Ggroup(String id, ActYwGnode preFunGnode, ActYwGnode nextFunGnode) {
    super();
    this.id = id;
    this.preFunGnode = preFunGnode;
    this.nextFunGnode = nextFunGnode;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ActYwGnode getGnode() {
    return gnode;
  }

  public void setGnode(ActYwGnode gnode) {
    this.gnode = gnode;
  }

  public ActYwGnode getParent() {
    return parent;
  }

  public void setParent(ActYwGnode parent) {
    this.parent = parent;
  }

  public ActYwGnode getPreGnode() {
    return preGnode;
  }

  public void setPreGnode(ActYwGnode preGnode) {
    this.preGnode = preGnode;
  }

  public ActYwGnode getPreFunGnode() {
    return preFunGnode;
  }

  public void setPreFunGnode(ActYwGnode preFunGnode) {
    this.preFunGnode = preFunGnode;
  }

  public List<ActYwGnode> getPreGnodes() {
    return preGnodes;
  }

  public void setPreGnodes(List<ActYwGnode> preGnodes) {
    this.preGnodes = preGnodes;
  }

  public List<ActYwGnode> getPreFunGnodes() {
    return preFunGnodes;
  }

  public void setPreFunGnodes(List<ActYwGnode> preFunGnodes) {
    this.preFunGnodes = preFunGnodes;
  }

  public ActYwGnode getNextGnode() {
    return nextGnode;
  }

  public void setNextGnode(ActYwGnode nextGnode) {
    this.nextGnode = nextGnode;
  }

  public ActYwGnode getNextFunGnode() {
    return nextFunGnode;
  }

  public void setNextFunGnode(ActYwGnode nextFunGnode) {
    this.nextFunGnode = nextFunGnode;
  }

  public List<ActYwGnode> getNextGnodes() {
    return nextGnodes;
  }

  public void setNextGnodes(List<ActYwGnode> nextGnodes) {
    this.nextGnodes = nextGnodes;
  }

  public List<ActYwGnode> getNextFunGnodes() {
    return nextFunGnodes;
  }

  public void setNextFunGnodes(List<ActYwGnode> nextFunGnodes) {
    this.nextFunGnodes = nextFunGnodes;
  }

  public ActYwGnode getSnode() {
    return snode;
  }

  public void setSnode(ActYwGnode snode) {
    this.snode = snode;
  }

  @Override
  public Ggroup clone() {
    Ggroup cl = null;
    try{
      cl = (Ggroup) super.clone();
    }catch(CloneNotSupportedException e){
      e.printStackTrace();
    }
    return cl;
  }
}
