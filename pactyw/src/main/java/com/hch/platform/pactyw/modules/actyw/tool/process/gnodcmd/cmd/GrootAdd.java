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
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootFlowValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootStartValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GrootValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;

public class GrootAdd extends IGnodeOper<ActYwGnode, ActYwEngineImpl, GrootValidate> implements IGnodeRunner<GnodeVrtpl, Ggroup>{

  public GrootAdd() {
    super();
  }

  public GrootAdd(ActYwEngineImpl engine, GrootValidate validate) {
    super(engine, validate);
  }

  public GrootAdd(ActYwEngineImpl engine) {
    super(engine);
  }

  @Override
  public Gcmd getCmd() {
    return Gcmd.GROOT_ADD;
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
      List<ActYwGnode> actYwGnodes = Lists.newArrayList();//最终所有变更节点列表集
      GrootStartAdd rootStartAdd = new GrootStartAdd(engine, new GrootStartValidate());
      GrootFlowAdd rootFlowAdd = new GrootFlowAdd(engine, new GrootFlowValidate());
      GrootEndAdd rootEndAdd = new GrootEndAdd(engine, new GrootEndValidate());
      GrootStartUpdate rootStartUpdate = new GrootStartUpdate(engine, rootStartAdd.getValidate());
      GrootFlowUpdate rootFlowUpdate = new GrootFlowUpdate(engine, rootFlowAdd.getValidate());
      GrootEndUpdate rootEndUpdate = new GrootEndUpdate(engine, rootEndAdd.getValidate());

      ActYwRstatus<GnodeVrtpl> preArstatus = rootStartAdd.exec(param);
      ActYwRstatus<GnodeVrtpl> flowArstatus = rootFlowAdd.exec(param);
      ActYwRstatus<GnodeVrtpl> nextArstatus = rootEndAdd.exec(param);
      ActYwGnode pre = preArstatus.getDatas().getPre();
      ActYwGnode flow = flowArstatus.getDatas().getFlow();
      ActYwGnode next = nextArstatus.getDatas().getNext();
      List<ActYwGnode> deleteGnodes = Lists.newArrayList();
      if(preArstatus.getStatus() && flowArstatus.getStatus() && nextArstatus.getStatus()){
        Ggroup cparampre = param.clone();
        Ggroup cparamflow = param.clone();
        Ggroup cparamnext = param.clone();

        cparamflow.setGnode(param.getGnode());
        cparamflow.setPreGnode(pre);
        //cparamflow.setPreGnodes(ActYwGnode.addGnNode(engine.service().findPreNextByGroupAndIdss(cparamflow.getPreGnode(), true), pre));
        cparamflow.setPreFunGnode(pre);
        cparamflow.setNextGnode(next);
        //cparamflow.setNextGnodes(ActYwGnode.addGnNode(engine.service().findPreNextByGroupAndIdss(cparamflow.getNextGnode(), false), next));
        cparamflow.setNextFunGnode(next);
        cparamflow.setSnode(flow);
        flowArstatus = rootFlowUpdate.exec(cparamflow);

        cparampre.setGnode(param.getGnode());
        cparampre.setNextGnode(flow);
        //cparampre.setNextGnodes(ActYwGnode.addGnNode(engine.service().findPreNextByGroupAndIdss(cparampre.getNextGnode(), false), flow));
        cparampre.setNextFunGnode(next);
        cparampre.setSnode(pre);
        preArstatus = rootStartUpdate.exec(cparampre);

        cparamnext.setGnode(param.getGnode());
        cparamnext.setPreGnode(flow);
        //cparamnext.setPreGnodes(ActYwGnode.addGnNode(engine.service().findPreNextByGroupAndIdss(cparamnext.getPreGnode(), true), flow));
        cparamnext.setPreFunGnode(pre);
        cparamnext.setSnode(next);
        nextArstatus = rootEndUpdate.exec(cparamnext);

        if(preArstatus.getStatus() && flowArstatus.getStatus() && nextArstatus.getStatus()){
          actYwGnodes.add(preArstatus.getDatas().getPre());
          actYwGnodes.add(flowArstatus.getDatas().getFlow());
          actYwGnodes.add(nextArstatus.getDatas().getNext());
        }
      }

      if(!(preArstatus.getStatus() && flowArstatus.getStatus() && nextArstatus.getStatus())){
        deleteGnodes.add(pre);
        deleteGnodes.add(flow);
        deleteGnodes.add(next);
        engine.service().deletePLWL(deleteGnodes);

        StringBuffer msgBuffer = new StringBuffer();
        if(!preArstatus.getStatus()){
          msgBuffer.append(preArstatus.getMsg());
          msgBuffer.append("|");
        }
        if(!flowArstatus.getStatus()){
          msgBuffer.append(flowArstatus.getMsg());
          msgBuffer.append("|");
        }
        if(!nextArstatus.getStatus()){
          msgBuffer.append(nextArstatus.getMsg());
          msgBuffer.append("|");
        }
        rstatus.setStatus(false);
        rstatus.setMsg(msgBuffer.toString());
        actYwGnodes = deleteGnodes;
      }

      GnodeVrtpl gvrtpl = new GnodeVrtpl();
      //gvrtpl.setGnodes(actYwGnodes);
      rstatus.setDatas(gvrtpl);
    }
    return rstatus;
  }
}
