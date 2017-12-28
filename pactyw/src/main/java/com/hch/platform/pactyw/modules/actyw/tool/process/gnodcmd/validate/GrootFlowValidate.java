package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate;

import java.util.List;

import com.google.common.collect.Lists;
import com.hch.platform.putil.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;

public class GrootFlowValidate implements IGnodeValidate<GnodeVptpl, GnodeVrtpl, Ggroup> {
  @Override
  public ActYwRstatus<GnodeVrtpl> isCanAdd(GnodeVptpl vptpl, Ggroup param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
//    Boolean sizeIs1 = true;
//    if(gnodes.size() != 1){
//      sizeIs1 = false;
//      rstatus.setMsg("根节点操作条件不符合,当且仅当当前流程数据(size == 1)才能执行！");
//    }
//    rstatus.setStatus(sizeIs1);
    rstatus.setDatas(new GnodeVrtpl(gnodes));
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanUpdate(GnodeVptpl vptpl, Ggroup param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean hasRootFlow = false;

    if(param == null){
      rstatus.setStatus(false);
      rstatus.setMsg("参数不能为空!");
      return rstatus;
    }

    if(param.getPreGnodes() == null){
      rstatus.setStatus(false);
      rstatus.setMsg("前置节点不能为空!");
      return rstatus;
    }

    if(param.getNextGnodes() == null){
      rstatus.setStatus(false);
      rstatus.setMsg("后置节点不能为空!");
      return rstatus;
    }
    List<ActYwGnode> rgnodes = Lists.newArrayList();
    for (ActYwGnode gnode : gnodes) {
      if((gnode.getType()).equals(GnodeType.GT_ROOT_FLOW.getId())){
        hasRootFlow = true;
        rgnodes.add(gnode);
      }
    }
    rstatus.setDatas(new GnodeVrtpl(rgnodes));
    rstatus.setStatus(hasRootFlow);
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanDelete(GnodeVptpl vptpl, Ggroup param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean canDelete = false;
    if(StringUtil.isEmpty(param.getId())){
      rstatus.setMsg("流程标识不能为空！");
    }

    List<ActYwGnode> deleteLists = Lists.newArrayList();
    for (ActYwGnode gnode : gnodes) {
      if((gnode.getType()).equals(GnodeType.GT_ROOT_FLOW.getId())){
        canDelete = true;
        deleteLists.add(gnode);
      }
    }

    rstatus.setStatus(canDelete);
    rstatus.setDatas(new GnodeVrtpl(deleteLists));
    return rstatus;
  }
}
