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
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Ggroup;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootEndValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootFlowValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootProcessValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenFuntype;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

public class GrootProcessAdd extends IGnodeOper<ActYwGnode, ActYwEngineImpl, GrootProcessValidate> implements IGnodeRunner<GnodeVrtpl, Ggnode>{

  public GrootProcessAdd() {
    super();
  }

  public GrootProcessAdd(ActYwEngineImpl engine, GrootProcessValidate validate) {
    super(engine, validate);
  }

  public GrootProcessAdd(ActYwEngineImpl engine) {
    super(engine);
  }

  @Override
  public Gcmd getCmd() {
    return Gcmd.GPROCESS_GNODE_ADD;
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

    if((param.getNextFunGnode() == null) || StringUtil.isEmpty(param.getNextFunGnode().getId())){
      param.setNextFunGnode(curEndGnode);
    }else {
      if((param.getNextFunGnode().getId()).equals(curEndGnode.getId())){
        param.setNextFunGnode(curEndGnode);
      }else{
        param.setNextFunGnode(engine.service().get(param.getNextFunGnode().getId()));
      }
    }

    param.setPreFunGnode(engine.service().get(param.getPreFunGnode().getId()));
    param.setPreGnode(engine.service().get(param.getPreFunGnode().getNextId()));
    rstatus = validate.isCanAdd(new GnodeVptpl(gnodes), param);

    if (rstatus.getStatus()) {
      ActYwGnode preGnode = param.getPreGnode();
      ActYwGnode preFunGnode = param.getPreFunGnode();
      ActYwGnode nextGnode = null;
      ActYwGnode nextFunGnode = param.getNextFunGnode();

      rstatus.setStatus(false);
      ActYwGnode actYwGnode = new ActYwGnode();
      actYwGnode.setId(IdGen.uuid());
      actYwGnode.setIsNewRecord(true);
      actYwGnode.setParent(new ActYwGnode(SysIds.SYS_TREE_ROOT.getId()));
      actYwGnode.setGroupId(param.getId());
      actYwGnode.setGroup(new ActYwGroup(param.getId()));
      actYwGnode.setType(GnodeType.GT_PROCESS.getId());
      actYwGnode.setTypefun(StenFuntype.SFT_SELECT.getVal());
      actYwGnode.setNodeId(param.getNode().getId());
      actYwGnode.setIsShow((param.getIsShow() != null)?param.getIsShow():true);
      actYwGnode.setIsForm((StringUtil.isEmpty(param.getFormId())?false:true));
      actYwGnode.setSort(30);
      actYwGnode.setOffice(UserUtils.getAdminOffice());

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

      /**
       * 新增并更新节点流程线.
       */
      GrootFlowAdd rootFlowAdd = new GrootFlowAdd(engine, new GrootFlowValidate());
      GrootFlowUpdate rootFlowUpdate = new GrootFlowUpdate(engine, rootFlowAdd.getValidate());
      ActYwRstatus<GnodeVrtpl> flowArstatus = rootFlowAdd.exec(param);
      Ggroup cparamflow = param.clone();
      cparamflow.setGnode(param.getGnode());
      cparamflow.setPreGnode(actYwGnode);
      cparamflow.setPreFunGnode(actYwGnode);
      cparamflow.setNextGnode(nextFunGnode);
      cparamflow.setNextFunGnode(nextFunGnode);
      cparamflow.setSnode(flowArstatus.getDatas().getFlow());
      flowArstatus = rootFlowUpdate.exec(cparamflow);
      nextGnode = flowArstatus.getDatas().getFlow();

      /**
       * 更新下一节点.
       */
      GrootEndUpdate rootEndUpdate = new GrootEndUpdate(engine, new GrootEndValidate());
      Ggroup cparamnext = param.clone();
      cparamnext.setGnode(param.getGnode());
      cparamnext.setPreGnode(nextGnode);
      cparamnext.setPreFunGnode(actYwGnode);
      cparamnext.setSnode(nextFunGnode);
      ActYwRstatus<GnodeVrtpl> nextArstatus = rootEndUpdate.exec(cparamnext);

      actYwGnode.setNextId(actYwGnode.getNextGnode().getId());
      actYwGnode.setNextIds(ActYwGnode.genIds(actYwGnode.getNextGnodes()));
      actYwGnode.setNextIdss(ActYwGnode.genIdss(actYwGnode.getNextGnode().getNextIdss(), actYwGnode.getNextId()));
      actYwGnode.setNextFunId(actYwGnode.getNextFunGnode().getId());

      actYwGnode.setRemarks("ProcessAdd"+StenType.ST_JG_SUB_PROCESS.getRemark());
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
