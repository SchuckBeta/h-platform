package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd;

import java.util.List;

import com.google.common.collect.Lists;
import com.hch.platform.putil.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.Gcmd;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeOper;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeRunner;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootEndValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;

public class GrootEndUpdate extends IGnodeOper<ActYwGnode, ActYwEngineImpl, GrootEndValidate> implements IGnodeRunner<GnodeVrtpl, Ggroup>{

  public GrootEndUpdate() {
    super();
  }

  public GrootEndUpdate(ActYwEngineImpl engine, GrootEndValidate validate) {
    super(engine, validate);
  }

  public GrootEndUpdate(ActYwEngineImpl engine) {
    super(engine);
  }

  @Override
  public Gcmd getCmd() {
    return Gcmd.GROOT_UPDATE_END;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> exec(Ggroup param) {
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    List<ActYwGnode> gnodes = null;
    if(StringUtil.isNotEmpty(param.getId())){
      gnodes = engine.service().findListByGroup(new ActYwGnode(new ActYwGroup(param.getId())));
    }else{
      rstatus.setStatus(false);
      rstatus.setMsg("流程标识不存在！");
      return rstatus;
    }
    rstatus = validate.isCanUpdate(new GnodeVptpl(gnodes), param);

    if (rstatus.getStatus()) {
      ActYwGnode gnode = param.getGnode();
      ActYwGnode preGnode = param.getPreGnode();
      ActYwGnode preFunGnode = param.getPreFunGnode();

      ActYwGnode actYwGnode = param.getSnode();
      actYwGnode.setIsNewRecord(false);
      if(gnode != null){
        if(gnode.getSort() != null){
          actYwGnode.setSort(gnode.getSort());
        }

        if(StringUtil.isNotEmpty(gnode.getRemarks())){
          actYwGnode.setRemarks(gnode.getRemarks());
        }
      }

      actYwGnode.setPreGnode(preGnode);
      actYwGnode.setPreGnodes(Lists.newArrayList());
      actYwGnode.getPreGnodes().add(preGnode);
      actYwGnode.setPreFunGnode(preFunGnode);
      actYwGnode.setPreFunGnodes(Lists.newArrayList());
      actYwGnode.getPreFunGnodes().add(preFunGnode);

      actYwGnode.setNextGnode(null);
      actYwGnode.setNextGnodes(null);
      actYwGnode.setNextFunGnode(null);
      actYwGnode.setNextFunGnodes(null);

      actYwGnode.setPreId(actYwGnode.getPreGnode().getId());
      actYwGnode.setPreIds(ActYwGnode.genIds(actYwGnode.getPreGnodes()));
      actYwGnode.setPreIdss(ActYwGnode.genIdss(actYwGnode.getPreGnode().getPreIdss(), actYwGnode.getPreId()));
      actYwGnode.setPreFunId(actYwGnode.getPreFunGnode().getId());
      engine.service().save(actYwGnode);
      rstatus.setStatus(true);
      GnodeVrtpl gvrtpl = new GnodeVrtpl();
      gvrtpl.setNext(actYwGnode);
      gvrtpl.setGnodes(Lists.newArrayList(actYwGnode));
      rstatus.setDatas(gvrtpl);
    }
    return rstatus;
  }
}
