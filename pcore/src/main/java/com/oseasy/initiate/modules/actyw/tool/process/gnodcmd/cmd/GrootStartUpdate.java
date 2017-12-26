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
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootStartValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;

public class GrootStartUpdate extends IGnodeOper<ActYwGnode, ActYwEngineImpl, GrootStartValidate> implements IGnodeRunner<GnodeVrtpl, Ggroup>{

  public GrootStartUpdate() {
    super();
  }

  public GrootStartUpdate(ActYwEngineImpl engine, GrootStartValidate validate) {
    super(engine, validate);
  }

  public GrootStartUpdate(ActYwEngineImpl engine) {
    super(engine);
  }

  @Override
  public Gcmd getCmd() {
    return Gcmd.GROOT_UPDATE_START;
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
    }
    rstatus = validate.isCanUpdate(new GnodeVptpl(gnodes), param);

    if (rstatus.getStatus()) {
      ActYwGnode gnode = param.getGnode();
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

      actYwGnode.setPreGnode(null);
      actYwGnode.setPreGnodes(null);
      actYwGnode.setPreFunGnode(null);
      actYwGnode.setPreFunGnodes(null);

      actYwGnode.setNextGnode(nextGnode);
      actYwGnode.setNextFunGnode(nextFunGnode);
      actYwGnode.setNextGnodes(Lists.newArrayList());
      actYwGnode.getNextGnodes().add(nextGnode);

      actYwGnode.setNextId(actYwGnode.getNextGnode().getId());
      actYwGnode.setNextIds(ActYwGnode.genIds(actYwGnode.getNextGnodes()));
      actYwGnode.setNextIdss(ActYwGnode.genIdss(actYwGnode.getNextGnode().getNextIdss(), actYwGnode.getNextId()));
      actYwGnode.setNextFunId(actYwGnode.getNextFunGnode().getId());
      engine.service().save(actYwGnode);
      rstatus.setStatus(true);
      GnodeVrtpl gvrtpl = new GnodeVrtpl();
      gvrtpl.setPre(actYwGnode);
      gvrtpl.setGnodes(Lists.newArrayList(actYwGnode));
      rstatus.setDatas(gvrtpl);
    }
    return rstatus;
  }
}
