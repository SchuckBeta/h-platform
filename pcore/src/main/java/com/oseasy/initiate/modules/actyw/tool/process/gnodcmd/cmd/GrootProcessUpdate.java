package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.Gcmd;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeOper;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.IGnodeRunner;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggnode;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootProcessValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenFuntype;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

public class GrootProcessUpdate extends IGnodeOper<ActYwGnode, ActYwEngineImpl, GrootProcessValidate> implements IGnodeRunner<GnodeVrtpl, Ggnode>{

  public GrootProcessUpdate() {
    super();
  }

  public GrootProcessUpdate(ActYwEngineImpl engine, GrootProcessValidate validate) {
    super(engine, validate);
  }

  public GrootProcessUpdate(ActYwEngineImpl engine) {
    super(engine);
  }

  @Override
  public Gcmd getCmd() {
    return Gcmd.GPROCESS_GNODE_UPDATE;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> exec(Ggnode param) {
    ActYwRstatus<GnodeVrtpl> rstatus = new ActYwRstatus<GnodeVrtpl>();
    ActYwGnode curEndGnode = null;
    List<ActYwGnode> gnodes = null;
    if(StringUtil.isNotEmpty(param.getId())){
      gnodes = engine.service().findListByGroup(new ActYwGnode(new ActYwGroup(param.getId())));
      curEndGnode = engine.service().getGnode(param.getId(), GnodeType.GT_ROOT_END.getId(), SysIds.SYS_TREE_ROOT.getId());
    }else{
      rstatus.setStatus(false);
      rstatus.setMsg("流程标识不存在！");
      return rstatus;
    }

    if((param.getSnode() != null) && StringUtil.isNotEmpty(param.getSnode().getId())){
      param.setSnode(engine.service().get(param.getSnode().getId()));
    }

    if((param.getNextFunGnode() == null) || StringUtil.isEmpty(param.getNextFunGnode().getId())){
      param.setNextFunGnode(curEndGnode);
    }else {
      if((param.getNextFunGnode().getId()).equals(curEndGnode.getId())){
        param.setNextFunGnode(curEndGnode);
      }else{
        param.setNextFunGnode(engine.service().get(param.getNextFunGnode().getId()));
      }
    }

    if((param.getNextFunGnode() == null) || StringUtil.isEmpty(param.getNextFunGnode().getId())){
      param.setNextFunGnode(curEndGnode);
    }else {
      if((param.getNextFunGnode().getId()).equals(curEndGnode.getId())){
        param.setNextFunGnode(curEndGnode);
      }else{
        param.setNextFunGnode(engine.service().get(param.getNextFunGnode().getId()));
      }
    }

    if((param.getPreGnode() != null) && StringUtil.isNotEmpty(param.getPreGnode().getId())){
      param.setPreGnode(engine.service().get(param.getPreGnode().getId()));
    }

    if((param.getNextGnode() != null) && StringUtil.isNotEmpty(param.getNextGnode().getId())){
      param.setNextGnode(engine.service().get(param.getNextGnode().getId()));
    }

    rstatus = validate.isCanUpdate(new GnodeVptpl(gnodes), param);

    if (rstatus.getStatus()) {
      ActYwGnode gnode = param.getSnode();
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
