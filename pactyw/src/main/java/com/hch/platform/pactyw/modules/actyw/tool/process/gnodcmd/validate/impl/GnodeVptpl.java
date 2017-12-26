package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.IvalidatePtpl;

public class GnodeVptpl implements IvalidatePtpl{
  private List<ActYwGnode> gnodes;

  public GnodeVptpl() {
    super();
  }

  public GnodeVptpl(List<ActYwGnode> gnodes) {
    super();
    this.gnodes = gnodes;
  }

  public List<ActYwGnode> getGnodes() {
    return gnodes;
  }

  public void setGnodes(List<ActYwGnode> gnodes) {
    this.gnodes = gnodes;
  }
}
