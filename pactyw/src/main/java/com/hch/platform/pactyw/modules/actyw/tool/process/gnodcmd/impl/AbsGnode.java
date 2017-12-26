package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.impl;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnode;

public abstract class AbsGnode<T extends ActYwGnode> extends ActYwGnode implements IGnode<ActYwGnode>{
  private static final long serialVersionUID = 1L;

  @Override
  public ActYwRstatus<ActYwGnode> initCurrentGnode(ActYwGnode gnode) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>();
    rstatus.setDatas(gnode);
    return rstatus;
  }


  @Override
  public ActYwRstatus<ActYwGnode> initPreGnode(ActYwGnode gnode) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>();
    rstatus.setDatas(gnode);
    return rstatus;
  }

  @Override
  public ActYwRstatus<ActYwGnode> initNextGnode(ActYwGnode gnode) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>();
    rstatus.setDatas(gnode);
    return rstatus;
  }

  @Override
  public ActYwRstatus<ActYwGnode> initPreFunGnode(ActYwGnode gnode) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>();
    rstatus.setDatas(gnode);
    return rstatus;
  }

  @Override
  public ActYwRstatus<ActYwGnode> initNextFunGnode(ActYwGnode gnode) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>();
    rstatus.setDatas(gnode);
    return rstatus;
  }

  @Override
  public ActYwRstatus<ActYwGnode> initParentGnode(ActYwGnode gnode) {
    ActYwRstatus<ActYwGnode> rstatus = new ActYwRstatus<ActYwGnode>();
    rstatus.setDatas(gnode);
    return rstatus;
  }

  @Override
  public ActYwRstatus<ActYwGnode> initPreGnodes(ActYwGnode gnode) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ActYwRstatus<ActYwGnode> initNextGnodes(ActYwGnode gnode) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ActYwRstatus<ActYwGnode>> initPreFunGnodes(ActYwGnode gnode) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ActYwRstatus<ActYwGnode>> initNextFunGnodes(ActYwGnode gnode) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ActYwRstatus<ActYwGnode>> initChildGnodes(ActYwGnode gnode) {
    // TODO Auto-generated method stub
    return null;
  }
}
