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

public class GrootEndValidate implements IGnodeValidate<GnodeVptpl, GnodeVrtpl, Ggroup> {
  @Override
  public ActYwRstatus<GnodeVrtpl> isCanAdd(GnodeVptpl vptpl, Ggroup param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean sizeIs1 = true;
    if(gnodes.size() != 2){
      sizeIs1 = false;
      rstatus.setMsg("根节点操作条件不符合,当且仅当当前流程数据(size == 2)才能执行！");
    }
    rstatus.setStatus(sizeIs1);
    rstatus.setDatas(new GnodeVrtpl(gnodes));
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanUpdate(GnodeVptpl vptpl, Ggroup param) {
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean hasRootEnd = false;

    List<ActYwGnode> rgnodes = Lists.newArrayList();
    for (ActYwGnode gnode : gnodes) {
      if((gnode.getType()).equals(GnodeType.GT_ROOT_END.getId())){
        hasRootEnd = true;
        rgnodes.add(gnode);
      }
    }
    rstatus.setDatas(new GnodeVrtpl(rgnodes));
    rstatus.setStatus(hasRootEnd);
    return rstatus;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> isCanDelete(GnodeVptpl vptpl, Ggroup param) {
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    Boolean canDelete = true;
    List<ActYwGnode> gnodes = vptpl.getGnodes();
    if(StringUtil.isEmpty(param.getId())){
      rstatus.setStatus(false);
      rstatus.setMsg("流程标识不能为空！");
    }

    List<ActYwGnode> deleteLists = Lists.newArrayList();
    for (ActYwGnode gnode : gnodes) {
      if((gnode.getType()).equals(GnodeType.GT_ROOT_END.getId())){
        canDelete = false;
        deleteLists.add(gnode);
      }
    }

    if((deleteLists == null) || (deleteLists.size() <= 0)){
      canDelete = false;
    }

    rstatus.setStatus(canDelete);
    rstatus.setDatas(new GnodeVrtpl(deleteLists));
    return rstatus;
  }
}
