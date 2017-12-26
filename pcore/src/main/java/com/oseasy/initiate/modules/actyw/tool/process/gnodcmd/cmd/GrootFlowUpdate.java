package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.Gcmd;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeOper;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeRunner;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootFlowValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;

public class GrootFlowUpdate extends IGnodeOper<ActYwGnode, ActYwEngineImpl, GrootFlowValidate> implements IGnodeRunner<GnodeVrtpl, Ggroup>{

  public GrootFlowUpdate() {
    super();
  }

  public GrootFlowUpdate(ActYwEngineImpl engine) {
    super(engine);
  }

  public GrootFlowUpdate(ActYwEngineImpl engine, GrootFlowValidate validate) {
    super(engine, validate);
  }

  @Override
  public Gcmd getCmd() {
    return Gcmd.GROOT_UPDATE_FLOW;
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
      ActYwGnode nextGnode = param.getNextGnode();
      ActYwGnode nextFunGnode = param.getNextFunGnode();

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

      actYwGnode.setNextGnode(nextGnode);
      actYwGnode.setNextGnodes(Lists.newArrayList());
      actYwGnode.getNextGnodes().add(nextGnode);
      actYwGnode.setNextFunGnode(nextFunGnode);
      actYwGnode.setNextFunGnodes(Lists.newArrayList());
      actYwGnode.getNextFunGnodes().add(nextGnode);


      actYwGnode.setPreId(actYwGnode.getPreGnode().getId());
      actYwGnode.setPreIds(ActYwGnode.genIds(actYwGnode.getPreGnodes()));
      actYwGnode.setPreIdss(ActYwGnode.genIdss(actYwGnode.getPreGnode().getPreIdss(), actYwGnode.getPreId()));
      actYwGnode.setPreFunId(actYwGnode.getPreFunGnode().getId());

      actYwGnode.setNextId(actYwGnode.getNextGnode().getId());
      actYwGnode.setNextIds(ActYwGnode.genIds(actYwGnode.getNextGnodes()));
      actYwGnode.setNextIdss(ActYwGnode.genIdss(actYwGnode.getNextGnode().getNextIdss(), actYwGnode.getNextId()));
      actYwGnode.setNextFunId(actYwGnode.getNextFunGnode().getId());
      engine.service().save(actYwGnode);
      rstatus.setStatus(true);
      GnodeVrtpl gvrtpl = new GnodeVrtpl();
      gvrtpl.setFlow(actYwGnode);
      gvrtpl.setGnodes(Lists.newArrayList(actYwGnode));
      rstatus.setDatas(gvrtpl);
    }
    return rstatus;
  }
}
