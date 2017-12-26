package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;

public class GnodeInitValidate implements IGnodeValidate<GnodeVptpl, GnodeVrtpl, Ggroup> {

  /**
   * vptpl 节点参数为所有节点.
   */
  @Override
  public ActYwRstatus<GnodeVrtpl> isCanAdd(GnodeVptpl vptpl, Ggroup param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean sizeIs0 = true;
    if((gnodes != null) && (gnodes.size() != 0)){
      sizeIs0 = false;
      rstatus.setMsg("根节点操作条件不符合,当且仅当数据库数据(size == 0)才能执行！");
    }
    rstatus.setStatus(sizeIs0);
    rstatus.setDatas(new GnodeVrtpl(gnodes));
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanUpdate(GnodeVptpl vptpl, Ggroup param) {
    return new ActYwRstatus<GnodeVrtpl>(false, "根节点只能初始化，不能做修改");
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanDelete(GnodeVptpl vptpl, Ggroup param) {
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    Boolean sizeIs1 = true;
    if((gnodes.size() != 1)){
      sizeIs1 = false;
      rstatus.setMsg("根节点操作条件不符合,当且仅当数据库数据(size == 1)才能执行！");
    }
    rstatus.setStatus(sizeIs1);
    rstatus.setDatas(new GnodeVrtpl(gnodes));
    return rstatus;
  }
}
