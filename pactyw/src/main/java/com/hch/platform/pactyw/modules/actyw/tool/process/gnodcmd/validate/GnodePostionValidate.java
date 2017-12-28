package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate;

import java.util.List;

import com.hch.platform.putil.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Gpostion;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;

public class GnodePostionValidate implements IGnodeValidate<GnodeVptpl, GnodeVrtpl, Gpostion> {

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanAdd(GnodeVptpl vptpl, Gpostion param) {
    return new ActYwRstatus<GnodeVrtpl>(false, "更新坐标信息，不能做新增操作");
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanUpdate(GnodeVptpl vptpl, Gpostion param) {
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    if((param == null) || (param.getGnodes() == null) || (param.getGnodes().size() <= 0)){
      rstatus.setStatus(false);
      rstatus.setMsg("参数为空！");
      return rstatus;
    }

    if((param == null) || StringUtil.isEmpty(param.getGroupId())){
      rstatus.setStatus(false);
      rstatus.setMsg("流程标识为空！");
      return rstatus;
    }

    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanDelete(GnodeVptpl vptpl, Gpostion param) {
    return new ActYwRstatus<GnodeVrtpl>(false, "更新坐标信息，不能做删除操作");
  }
}
