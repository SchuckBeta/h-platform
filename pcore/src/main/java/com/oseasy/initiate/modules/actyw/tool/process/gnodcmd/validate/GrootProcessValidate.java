package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggnode;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;

public class GrootProcessValidate implements IGnodeValidate<GnodeVptpl, GnodeVrtpl, Ggnode> {
  @Override
  public ActYwRstatus<GnodeVrtpl> isCanAdd(GnodeVptpl vptpl, Ggnode param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean sizeGt3 = true;
    if(gnodes.size() < 3){
      sizeGt3 = false;
      rstatus.setMsg("根节点流程节点操作条件不符合,当且仅当当前流程数据(size >= 3)才能执行！");
    }

    if((param.getNode() == null) || StringUtil.isEmpty(param.getNode().getId())){
      rstatus.setStatus(false);
      rstatus.setMsg("根节点流程节点操作条件不符合,当前结点不能为空！");
      return rstatus;
    }

    if((param.getPreFunGnode() == null) || StringUtil.isEmpty(param.getPreFunGnode().getId())){
      rstatus.setStatus(false);
      rstatus.setMsg("根节点流程节点操作条件不符合,前一个业务结点不能为空！");
      return rstatus;
    }

    if((param.getNextFunGnode() == null)){
      rstatus.setStatus(false);
      rstatus.setMsg("根节点流程节点操作条件不符合,后一个业务结点不能为空！");
      return rstatus;
    }

    if((param.getPreGnode() == null) || StringUtil.isEmpty(param.getPreFunGnode().getId())){
      rstatus.setStatus(false);
      rstatus.setMsg("根节点流程节点操作条件不符合,前一个结点不能为空！");
      return rstatus;
    }

    rstatus.setStatus(sizeGt3);
    rstatus.setDatas(new GnodeVrtpl(gnodes));
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanUpdate(GnodeVptpl vptpl, Ggnode param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();

    if((param.getSnode() == null) || StringUtil.isEmpty(param.getSnode().getId())){
      rstatus.setStatus(false);
      rstatus.setMsg("根节点流程节点操作条件不符合, 需要修改的结点不能为空！");
      return rstatus;
    }

    if((param.getPreFunGnode() == null) || StringUtil.isEmpty(param.getPreFunGnode().getId())){
      rstatus.setStatus(false);
      rstatus.setMsg("根节点流程节点操作条件不符合,前一个业务结点不能为空！");
      return rstatus;
    }

    if((param.getNextFunGnode() == null)){
      rstatus.setStatus(false);
      rstatus.setMsg("根节点流程节点操作条件不符合,后一个业务结点不能为空！");
      return rstatus;
    }

    if((param.getPreGnode() == null) || StringUtil.isEmpty(param.getPreFunGnode().getId())){
      rstatus.setStatus(false);
      rstatus.setMsg("根节点流程节点操作条件不符合,前一个结点不能为空！");
      return rstatus;
    }

    if((param.getNextGnode() == null)){
      rstatus.setStatus(false);
      rstatus.setMsg("根节点流程节点操作条件不符合,后一个结点不能为空！");
      return rstatus;
    }
    rstatus.setDatas(new GnodeVrtpl(gnodes));
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanDelete(GnodeVptpl vptpl, Ggnode param) {
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
