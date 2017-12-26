package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.impl;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;

public class GnodeRoot extends AbsGnode<ActYwGnode>{
  private static final long serialVersionUID = 1L;

  @Override
  public GnodeType getGnodeType() {
    return GnodeType.GT_ROOT;
  }
}
