package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.utils.IdGen;
import com.hch.platform.putil.common.utils.StringUtil;
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
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenFuntype;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

public class GrootFlowAdd extends IGnodeOper<ActYwGnode, ActYwEngineImpl, GrootFlowValidate> implements IGnodeRunner<GnodeVrtpl, Ggroup>{
  public GrootFlowAdd() {
    super();
  }

  public GrootFlowAdd(ActYwEngineImpl engine, GrootFlowValidate validate) {
    super(engine, validate);
  }

  public GrootFlowAdd(ActYwEngineImpl engine) {
    super(engine);
  }

  @Override
  public Gcmd getCmd() {
    return Gcmd.GROOT_ADD_FLOW;
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
//    if(rstatus.getStatus()){
//      engine.service().deletePLWL(rstatus.getDatas().getGnodes());
//      rstatus.setDatas(null);
//    }

    /**
     * 如果执行删除,应该拿删除后的数据做验证处理.
     */
    gnodes = engine.service().findListByGroup(new ActYwGnode(new ActYwGroup(param.getId())));
    rstatus = validate.isCanAdd(new GnodeVptpl(gnodes), param);

    if (rstatus.getStatus()) {
      rstatus.setStatus(false);
      ActYwGnode actYwGnode = new ActYwGnode();
      actYwGnode.setId(IdGen.uuid());
      actYwGnode.setIsNewRecord(true);
      actYwGnode.setParent(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      actYwGnode.setGroupId(param.getId());
      actYwGnode.setGroup(new ActYwGroup(param.getId()));
      actYwGnode.setType(GnodeType.GT_ROOT_FLOW.getId());
      actYwGnode.setTypefun(StenFuntype.SFT_NOT_SELECT.getVal());
      actYwGnode.setNodeId(StenType.ST_FLOW_SEQUENCE.getId());
      actYwGnode.setIsShow(true);
      actYwGnode.setIsForm(false);
      actYwGnode.setSort(20);
      actYwGnode.setOffice(UserUtils.getAdminOffice());
      actYwGnode.setPreGnode(new ActYwGnode());
      actYwGnode.setPreGnodes(Lists.newArrayList());
      actYwGnode.setPreFunGnode(new ActYwGnode());
      actYwGnode.setPreFunGnodes(Lists.newArrayList());
      actYwGnode.setNextGnode(new ActYwGnode());
      actYwGnode.setNextGnodes(Lists.newArrayList());
      actYwGnode.setNextFunGnode(new ActYwGnode());
      actYwGnode.setNextFunGnodes(Lists.newArrayList());
      actYwGnode.setRemarks("RootAdd"+StenType.ST_FLOW_SEQUENCE.getRemark());
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
