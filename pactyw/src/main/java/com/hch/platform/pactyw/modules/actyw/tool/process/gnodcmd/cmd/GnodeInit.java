package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.Gcmd;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeOper;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeRunner;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Gnode;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GnodeInitValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl.RtLevelVal;

public class GnodeInit extends IGnodeOper<ActYwGnode, ActYwEngineImpl, GnodeInitValidate> implements IGnodeRunner<GnodeVrtpl, Ggroup>{
  @Override
  public Gcmd getCmd() {
    return Gcmd.GNODE_INIT;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> exec(Ggroup param) {
    List<ActYwGnode> gnodes = engine.service().findAll();
    ActYwRstatus<GnodeVrtpl> rstatus = validate.isCanDelete(new GnodeVptpl(gnodes), param);
    if(rstatus.getStatus()){
      engine.service().deletePLWL(rstatus.getDatas().getGnodes());
      rstatus.setDatas(null);
    }

    /**
     * 如果执行删除,应该拿删除后的数据做验证处理.
     */
    if(rstatus.getStatus()){
      rstatus = validate.isCanAdd(new GnodeVptpl(rstatus.getDatas().getGnodes()), param);
    }else{
      rstatus = validate.isCanAdd(new GnodeVptpl(gnodes), param);
    }

    if (rstatus.getStatus()) {
      rstatus.setStatus(false);
      ActYwGnode actYwGnode = new ActYwGnode();
      actYwGnode.setIsNewRecord(true);
      actYwGnode.setId(SysIds.SYS_TREE_ROOT.getId());
      actYwGnode.setParent(new ActYwGnode(SysIds.SYS_TREE_PROOT.getId()));
      ActYwNode actYwNode = new ActYwNode("0");
      actYwNode.setLevel(RtLevelVal.RT_LV0);
      actYwGnode.setNode(actYwNode);
      actYwGnode.setGroupId(null);
      actYwGnode.setTypefun("");
      actYwGnode.setTypefun("0");
      actYwGnode.setIsShow(false);
      actYwGnode.setIsForm(false);
      actYwGnode.setRemarks("系统内置，禁止删除");
      engine.service().save(actYwGnode);
      rstatus.setStatus(true);
      rstatus.setDatas(new GnodeVrtpl(Lists.newArrayList(actYwGnode)));
    }
    return rstatus;
  }
}
