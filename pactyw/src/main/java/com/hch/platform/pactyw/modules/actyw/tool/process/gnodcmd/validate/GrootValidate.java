package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;

public class GrootValidate implements IGnodeValidate<GnodeVptpl, GnodeVrtpl, Ggroup> {
  @Override
  public ActYwRstatus<GnodeVrtpl> isCanAdd(GnodeVptpl vptpl, Ggroup param) {
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    Boolean sizeIs1 = true;
    if((gnodes != null) && (gnodes.size() != 0)){
      sizeIs1 = false;
      rstatus.setMsg("根节点操作条件不符合！");
    }
    rstatus.setStatus(sizeIs1);
    rstatus.setDatas(new GnodeVrtpl(gnodes));
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanUpdate(GnodeVptpl vptpl, Ggroup param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean sizeIs3 = false;
    Boolean hasRootStart = false;
    Boolean hasRootFlow = false;
    Boolean hasRootEnd = false;
    if(gnodes.size() != 3){
      sizeIs3 = false;
      rstatus.setMsg("根节点操作条件不符合！");
    }

    if(sizeIs3){
      List<ActYwGnode> rgnodes = Lists.newArrayList();
      for (ActYwGnode gnode : gnodes) {
        if((gnode.getType()).equals(GnodeType.GT_ROOT_START.getId())){
          hasRootStart = true;
          rgnodes.add(gnode);
          rstatus.getDatas().setPre(gnode);
        }
        if((gnode.getType()).equals(GnodeType.GT_ROOT_FLOW.getId())){
          hasRootFlow = true;
          rgnodes.add(gnode);
          rstatus.getDatas().setFlow(gnode);
        }
        if((gnode.getType()).equals(GnodeType.GT_ROOT_END.getId())){
          hasRootEnd = true;
          rgnodes.add(gnode);
          rstatus.getDatas().setNext(gnode);
        }
      }
      rstatus.setDatas(new GnodeVrtpl(rgnodes));
    }else{
      rstatus.setDatas(new GnodeVrtpl(gnodes));
    }
    rstatus.setStatus((sizeIs3 && hasRootStart && hasRootFlow && hasRootEnd));
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanDelete(GnodeVptpl vptpl, Ggroup param) {
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean canDelete = true;
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    if((gnodes == null) || (gnodes.size() <= 0)){
      canDelete = false;
    }

    rstatus.setStatus(canDelete);
    rstatus.setDatas(new GnodeVrtpl(gnodes));
    return rstatus;
  }
}
