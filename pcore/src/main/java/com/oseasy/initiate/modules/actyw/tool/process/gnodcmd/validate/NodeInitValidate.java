package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate;

import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.NodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.NodeVrtpl;

public class NodeInitValidate implements IGnodeValidate<NodeVptpl, NodeVrtpl, Ggroup> {
  @Override
  public ActYwRstatus<NodeVrtpl> isCanAdd(NodeVptpl vptpl, Ggroup param) {
    return new ActYwRstatus<NodeVrtpl>(false, "基础节点信息，不能做新增操作");
  }

  @Override
  public ActYwRstatus<NodeVrtpl> isCanUpdate(NodeVptpl vptpl, Ggroup param) {
    return new ActYwRstatus<NodeVrtpl>(false, "基础节点信息，不能做修改操作");
  }

  @Override
  public ActYwRstatus<NodeVrtpl> isCanDelete(NodeVptpl vptpl, Ggroup param) {
    return new ActYwRstatus<NodeVrtpl>(false, "基础节点信息，不能做删除操作");
  }
}
