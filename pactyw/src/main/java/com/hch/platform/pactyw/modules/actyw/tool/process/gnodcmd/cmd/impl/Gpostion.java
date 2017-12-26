package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.IcmdPtpl;

@XmlRootElement(name = "gpostion")
public class Gpostion implements IcmdPtpl, Serializable{
  private static final long serialVersionUID = 1L;
  private String groupId;
  private List<Gpoint> gnodes;

  public Gpostion() {
    super();
  }

  public Gpostion(String groupId, List<Gpoint> gnodes) {
    super();
    this.groupId = groupId;
    this.gnodes = gnodes;
  }
  public String getGroupId() {
    return groupId;
  }
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
  public List<Gpoint> getGnodes() {
    return gnodes;
  }
  public void setGnodes(List<Gpoint> gnodes) {
    this.gnodes = gnodes;
  }

}
