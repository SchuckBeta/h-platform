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
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Gpoint;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl.Gpostion;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.GnodePostionValidate;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVptpl;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.impl.GnodeVrtpl;
import com.oseasy.initiate.modules.actyw.tool.process.impl.ActYwEngineImpl;

public class GnodePostionUpdate extends IGnodeOper<ActYwGnode, ActYwEngineImpl, GnodePostionValidate> implements IGnodeRunner<GnodeVrtpl, Gpostion>{
  private static final int HEIGHT = 50;
  private static final int WIDTH = 20;

  @Override
  public Gcmd getCmd() {
    return Gcmd.GNODE_UPDATE_POSTION;
  }

  @Override
  public ActYwRstatus<GnodeVrtpl> exec(Gpostion param) {
    ActYwRstatus<GnodeVrtpl> rstatus = null;
    List<ActYwGnode> gnodes = null;
    if(StringUtil.isNotEmpty(param.getGroupId())){
      gnodes = engine.service().findListByGroup(new ActYwGnode(new ActYwGroup(param.getGroupId())));
    }

    rstatus = validate.isCanUpdate(new GnodeVptpl(gnodes), param);
    if(rstatus.getStatus()){
      gnodes = dealPostion(param, gnodes);
      for (ActYwGnode gnode : gnodes) {
        engine.service().save(gnode);
      }
      rstatus.setDatas(new GnodeVrtpl(dealPostion(param, gnodes)));
    }
    return rstatus;
  }

  /**
   * 处理流程节点定位坐标.
   * @param param
   * @param gnodes
   */
  private List<ActYwGnode> dealPostion(Gpostion param, List<ActYwGnode> gnodes) {
    List<ActYwGnode> dealGnodes  = Lists.newArrayList();
    for (ActYwGnode curgnode : gnodes) {
      for (Gpoint curgpoint : param.getGnodes()) {
        if((curgnode.getId()).equals(curgpoint.getId())){
          curgnode.setPosLux(curgpoint.getPosLux());
          curgnode.setPosLuy(curgpoint.getPosLuy());
          curgnode.setWidth(curgpoint.getWidth());
          curgnode.setHeight(curgpoint.getHeight());

          curgnode.setPosAlux(curgpoint.getPosAlux());
          curgnode.setPosAluy(curgpoint.getPosAluy());
          curgnode.setWidtha(curgpoint.getWidtha());
          curgnode.setHeighta(curgpoint.getHeighta());
        }

        //TODO 需要处理与当前结点关联节点的坐标，包括前置、后置、前置业务、后置业务、子流程
        if(StringUtil.isNotEmpty(curgnode.getPreId()) && (curgnode.getPreId()).equals(curgpoint.getId())){
          curgnode.setPosLux(curgpoint.getPosLux());
          curgnode.setPosLuy(curgpoint.getPosLuy());
          curgnode.setWidth(curgpoint.getWidth());
          curgnode.setHeight(curgpoint.getHeight());

          curgnode.setPosAlux(curgpoint.getPosAlux());
          curgnode.setPosAluy(curgpoint.getPosAluy());
          curgnode.setWidtha(curgpoint.getWidtha());
          curgnode.setHeighta(curgpoint.getHeighta());
        }

        if(StringUtil.isNotEmpty(curgnode.getNextId()) && (curgnode.getNextId()).equals(curgpoint.getId())){
          curgnode.setPosLux(curgpoint.getPosLux());
          curgnode.setPosLuy(curgpoint.getPosLuy());
          curgnode.setWidth(curgpoint.getWidth());
          curgnode.setHeight(curgpoint.getHeight());

          curgnode.setPosAlux(curgpoint.getPosAlux());
          curgnode.setPosAluy(curgpoint.getPosAluy());
          curgnode.setWidtha(curgpoint.getWidtha());
          curgnode.setHeighta(curgpoint.getHeighta());
        }
      }
      dealGnodes.add(curgnode);
    }
    return dealGnodes;
  }
}
