package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd;

import java.util.List;
import java.util.UUID;

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
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GprocessFlowValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootStartValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenFuntype;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

public class GprocessEndAdd extends IGnodeOper<ActYwGnode, ActYwEngineImpl, GprocessFlowValidate> implements IGnodeRunner<GnodeVrtpl, Ggroup>{

  public GprocessEndAdd() {
    super();
  }

  public GprocessEndAdd(ActYwEngineImpl engine, GprocessFlowValidate validate) {
    super(engine, validate);
  }

  public GprocessEndAdd(ActYwEngineImpl engine) {
    super(engine);
  }

  @Override
  public Gcmd getCmd() {
    return Gcmd.GPROCESS_ADD_N_FLOW;
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

    rstatus = validate.isCanDelete(new GnodeVptpl(gnodes), param);
    if(rstatus.getStatus()){
      engine.service().deletePLWL(rstatus.getDatas().getGnodes());
      rstatus.setDatas(null);
    }

    /**
     * 如果执行删除,应该拿删除后的数据做验证处理.
     */
    gnodes = engine.service().findListByGroup(new ActYwGnode(new ActYwGroup(param.getId())));
    rstatus = validate.isCanAdd(new GnodeVptpl(gnodes), param);

    if (rstatus.getStatus()) {
      ActYwGnode parent = param.getParent();
      ActYwGnode preGnode = param.getPreGnode();
      ActYwGnode preFunGnode = param.getPreFunGnode();
      ActYwGnode nextGnode = param.getNextGnode();
      ActYwGnode nextFunGnode = param.getNextFunGnode();

      rstatus.setStatus(false);
      ActYwGnode actYwGnode = new ActYwGnode();
      actYwGnode.setId(IdGen.uuid());
      actYwGnode.setIsNewRecord(true);
      actYwGnode.setParent(parent);
      actYwGnode.setGroupId(param.getId());
      actYwGnode.setGroup(new ActYwGroup(param.getId()));
      actYwGnode.setType(GnodeType.GT_PROCESS_FLOW.getId());
      actYwGnode.setTypefun(StenFuntype.SFT_NOT_SELECT.getVal());
      actYwGnode.setNodeId(StenType.ST_FLOW_SEQUENCE.getId());
      actYwGnode.setIsShow(true);
      actYwGnode.setIsForm(false);
      actYwGnode.setSort(30);
      actYwGnode.setOffice(UserUtils.getAdminOffice());

      actYwGnode.setPreGnode(preGnode);
//      actYwGnode.setPreGnodes(Lists.newArrayList());
//      actYwGnode.getPreGnodes().add(preGnode);
      actYwGnode.setPreFunGnode(preFunGnode);
//      actYwGnode.setPreFunGnodes(Lists.newArrayList());
//      actYwGnode.getPreFunGnodes().add(preFunGnode);

      actYwGnode.setNextGnode(nextGnode);
//      actYwGnode.setNextGnodes(Lists.newArrayList());
//      actYwGnode.getNextGnodes().add(nextGnode);
      actYwGnode.setNextFunGnode(nextFunGnode);
//      actYwGnode.setNextFunGnodes(Lists.newArrayList());
//      actYwGnode.getNextFunGnodes().add(nextGnode);

      actYwGnode.setPreId(actYwGnode.getPreGnode().getId());
      actYwGnode.setPreIds(ActYwGnode.genIds(actYwGnode.getPreGnodes()));
      actYwGnode.setPreIdss(ActYwGnode.genIdss(actYwGnode.getPreGnode().getPreIdss(), actYwGnode.getPreId()));
      actYwGnode.setPreFunId(actYwGnode.getPreFunGnode().getId());

      actYwGnode.setNextId(actYwGnode.getNextGnode().getId());
      actYwGnode.setNextIds(ActYwGnode.genIds(actYwGnode.getNextGnodes()));
      actYwGnode.setNextIdss(ActYwGnode.genIdss(actYwGnode.getNextGnode().getNextIdss(), actYwGnode.getNextId()));
      actYwGnode.setNextFunId(actYwGnode.getNextFunGnode().getId());

      actYwGnode.setRemarks("ProcessFlowAdd"+StenType.ST_FLOW_SEQUENCE.getRemark());
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
