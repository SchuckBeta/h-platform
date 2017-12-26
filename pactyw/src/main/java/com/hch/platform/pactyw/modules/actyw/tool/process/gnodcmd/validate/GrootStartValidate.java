package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;

public class GrootStartValidate implements IGnodeValidate<GnodeVptpl, GnodeVrtpl, Ggroup> {
  @Override
  public ActYwRstatus<GnodeVrtpl> isCanAdd(GnodeVptpl vptpl, Ggroup param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean sizeIs0 = true;
    if(gnodes.size() != 0){
      sizeIs0 = false;
      rstatus.setMsg("根节点开始节点操作条件不符合,当且仅当当前流程数据(size == 0)才能执行！");
    }
    rstatus.setStatus(sizeIs0);
    rstatus.setDatas(new GnodeVrtpl(gnodes));
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanUpdate(GnodeVptpl vptpl, Ggroup param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    if(StringUtil.isEmpty(param.getId())){
      rstatus.setStatus(false);
      rstatus.setMsg("流程标识不能为空！");
      return rstatus;
    }

    if (param.getSnode() == null) {
      rstatus.setStatus(false);
      rstatus.setMsg("流程开始节点不能为空");
      return rstatus;
    }

    if (param.getNextGnode() == null) {
      rstatus.setStatus(false);
      rstatus.setMsg("流程开始节点和下一业务节点-连接线不能为空");
      return rstatus;
    }

    if (param.getNextFunGnode() == null) {
      rstatus.setStatus(false);
      rstatus.setMsg("流程开始下一业务节点不能为空");
      return rstatus;
    }

    Boolean hasRootStart = false;
    GnodeVrtpl rgnodes = new GnodeVrtpl();
    for (ActYwGnode gnode : gnodes) {
      if((gnode.getType()).equals(GnodeType.GT_ROOT_START.getId())){
        hasRootStart = true;
        rgnodes.setPre(gnode);
      }
    }

    rstatus.setStatus(hasRootStart);
    rstatus.setDatas(rgnodes);
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanDelete(GnodeVptpl vptpl, Ggroup param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean canDelete = true;
    List<ActYwGnode> deleteLists = Lists.newArrayList();

    if(StringUtil.isEmpty(param.getId())){
      canDelete = false;
      rstatus.setMsg("流程标识不能为空！");
      return rstatus;
    }

    if((gnodes == null) || (gnodes.size() <= 0)){
      canDelete = false;
      rstatus.setMsg("数据库无数据！");
      return rstatus;
    }

    for (ActYwGnode gnode : gnodes) {
      if((gnode.getType()).equals(GnodeType.GT_ROOT_START.getId())){
        canDelete = false;
        deleteLists.add(gnode);
      }
    }

    rstatus.setStatus(canDelete);
    rstatus.setDatas(new GnodeVrtpl(deleteLists));
    return rstatus;
  }
}
