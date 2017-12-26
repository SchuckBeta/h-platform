package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.IvalidateRtpl;

public class NodeVrtpl implements IvalidateRtpl{
  private List<ActYwNode> nodes;

  public List<ActYwNode> getNodes() {
    return nodes;
  }

  public void setNodes(List<ActYwNode> nodes) {
    this.nodes = nodes;
  }
}
