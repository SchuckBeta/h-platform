package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.List;

import com.google.common.collect.Lists;
import com.hch.platform.putil.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.vo.ActYwPgroot;

public class GnodeTool {
  /**
   * 如果新增的是一级节点时，需要更新当前结点的所有前置节点的子节点（添加当前结点ID）
   */
  public static boolean checkNeedUpdateChild(ActYwGnode gnode) {
    String level = GnodeType.getById(gnode.getType()).getLevel();
    if((level).equals(RtSvl.RtLevelVal.RT_LV1)){
      return true;
    }
    return false;
  }

  /**
   * 检查是否为二级节点.
   */
  public static boolean checkIsLv2(ActYwGnode gnode) {
    String level = GnodeType.getById(gnode.getType()).getLevel();
    if((level).equals(RtSvl.RtLevelVal.RT_LV2)){
      return true;
    }
    return false;
  }

  /**
   * 检查属性是否包含gnodeId.
   * @param sid 原属性
   * @param gnode 节点
   * @return String
   */
  public static boolean checkHaveGnodeId(String sid, ActYwGnode gnode) {
    return checkHaveGnodeId(sid, gnode, StringUtil.DOTH);
  }

  public static boolean checkHaveGnodeId(String sid, ActYwGnode gnode, String dt) {
    if(StringUtil.isEmpty(sid)){
      return false;
    }
    if(StringUtil.isEmpty(dt)){
      return (sid).contains(gnode.getId());
    }
    return (sid).contains(gnode.getId() + dt);
  }

  /**
   * 检查属性是否包含gnodeId，并删除.
   * @param sid 原属性
   * @param gnode 节点
   * @return String
   */
  public static String checkDelGnodeId(String sid, ActYwGnode gnode) {
    return checkDelGnodeId(sid, gnode, StringUtil.DOTH);
  }

  public static String checkDelGnodeId(String sid, ActYwGnode gnode, String dt) {
    if(checkHaveGnodeId(sid, gnode, StringUtil.DOTH)){
      return sid.replaceAll(gnode.getId() + dt, StringUtil.EMPTY);
    }
    return sid;
  }

  /**
   * 检查是否符合网关条件.
   * @param param
   * @param endGnode
   * @param nextFunGnode
   * @return
   */
  public static boolean checkIsGateWay(ActYwPgroot param) {
    if ((param == null) || (param.getGnode() == null)  || (param.getGnode().getPreFunGnode() == null)  || (param.getStartSflowGnode() == null) || (param.getStartSflowGnode().isEmpty())) {
      return false;
    }

    if (param.getGnode().isHasGateway()) {
      return true;
    }

    /**
     * 检查所有前置节点数量是否等于2.
     * 前置节点父节点ID与前一节点ID相同的
     * 排除子节点的前置节点也是当前节点的情况.
     */
    ActYwGnode pregnode = param.getGnode().getPreFunGnode();
    List<ActYwGnode> startSflowGnodes = Lists.newArrayList();
    for (ActYwGnode sfgnode : param.getStartSflowGnode()) {
      /**
       * 排除一级节点有字节出现误判网关情况.
       */
      if((pregnode.getId()).equals(sfgnode.getParent().getId())){
        continue;
      }

      /**
       * 排除节点类型不是连接线类型出现误判网关情况.
       */
      GnodeType gntype = GnodeType.getById(sfgnode.getType());
      if(((gntype).equals(GnodeType.GT_ROOT_FLOW) || (gntype).equals(GnodeType.GT_PROCESS_FLOW))){
        startSflowGnodes.add(sfgnode);
      }
    }

    if (startSflowGnodes.size() == 2) {
      return true;
    }

    return false;
  }
}
