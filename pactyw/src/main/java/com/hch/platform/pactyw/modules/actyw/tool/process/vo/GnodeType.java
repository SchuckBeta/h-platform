/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_GnodeType_]]文件
 * @date 2017年6月27日 下午4:02:08
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

import com.hch.platform.putil.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;

/**
 * 业务流程节点类型.
 * @author chenhao
 * @date 2017年6月27日 下午4:02:08
 *
 */
public enum GnodeType {
  GT_ROOT("0", RtSvl.RtLevelVal.RT_LV0, "根节点"),
  GT_ROOT_START("11", RtSvl.RtLevelVal.RT_LV1, "根节点开始"),
  GT_ROOT_FLOW("12", RtSvl.RtLevelVal.RT_LV1, "根节点结构"),
  GT_ROOT_END("13", RtSvl.RtLevelVal.RT_LV1, "根节点结束"),
  GT_PROCESS("19", RtSvl.RtLevelVal.RT_LV1, "流程"),
  GT_PROCESS_START("30", RtSvl.RtLevelVal.RT_LV2, "流程开始"),
  GT_PROCESS_END("40", RtSvl.RtLevelVal.RT_LV2, "流程结束"),
  GT_PROCESS_GATEWAY("50", RtSvl.RtLevelVal.RT_LV2, "网关"),
  GT_PROCESS_FLOW("60", RtSvl.RtLevelVal.RT_LV2, "结构"),
  GT_PROCESS_TASK("70", RtSvl.RtLevelVal.RT_LV2, "业务节点");

  private String id;
  private String level;
  private String remark;

  private GnodeType(String id, String level, String remark) {
    this.id = id;
    this.level = level;
    this.remark = remark;
  }

  /**
   * 根据id获取GnodeType .
   * @author chenhao
   * @param id id惟一标识
   * @return GnodeType
   */
  public static GnodeType getById(String id) {
    GnodeType[] entitys = GnodeType.values();
    for (GnodeType entity : entitys) {
      if ((id).equals(entity.getId())) {
        return entity;
      }
    }
    return null;
  }

  public static GnodeType getByActYwNode(ActYwNode node, String ntlevel) {
    return getByLevelTypeNodekey(node.getLevel(), ntlevel, node.getType(), node.getNodeKey());
  }

  public static GnodeType getByActYwNodeAndNodekey(ActYwNode node, String ntlevel, String nodekey) {
    String type = node.getType();
    if (StringUtil.isNotEmpty(nodekey)) {
      StenType stenType = StenType.getByKey(nodekey);
//      if (stenType == null) {
//        type = RtSvl.RtTypeVal.RT_T1;
//      }else if (!(stenType.getSubtype().equals(StenEsubType.SES_TASK) || stenType.getSubtype().equals(StenEsubType.SES_JG) || stenType.getSubtype().equals(StenEsubType.SES_GATEWAY))) {
//        type = RtSvl.RtTypeVal.RT_T0;
//      }else{
//        return null;
//      }
      if (stenType != null) {
        if ((!((stenType.getSubtype()).equals(StenEsubType.SES_TASK)) || (stenType.getSubtype()).equals(StenEsubType.SES_JG) || (stenType.getSubtype()).equals(StenEsubType.SES_GATEWAY))) {
          type = RtSvl.RtTypeVal.RT_T0;
        }else{
          return null;
        }
      }
    }
    return getByLevelTypeNodekey(node.getLevel(), ntlevel, type, nodekey);
  }

  public static GnodeType getByLevelTypeNodekey(String level, String ntlevel, String type, String nodekey) {
    return getByLevelNodeType(level, NodeType.getByLevelTypeStype(ntlevel, type, nodekey));
  }

  public static GnodeType getByLevelNodeType(String level, NodeType nodeType) {
    if (level == null) {
      return null;
    }

    if ((level).equals(RtSvl.RtLevelVal.RT_LV0)) {
      return GnodeType.GT_ROOT;
    }else if ((level).equals(RtSvl.RtLevelVal.RT_LV1)) {
      if ((nodeType.getStype().getSubtype()).equals(StenEsubType.SES_EVENT_START)) {
        return GnodeType.GT_ROOT_START;
      }else if ((nodeType.getStype().getSubtype()).equals(StenEsubType.SES_EVENT_END)) {
        return GnodeType.GT_ROOT_END;
      }else if ((nodeType.getStype().getSubtype()).equals(StenEsubType.SES_JG)) {
        if ((nodeType.getStype()).equals(StenType.ST_JG_SUB_PROCESS) || (nodeType.getStype()).equals(StenType.ST_JG_SUB_PROCESS_EVENT) || (nodeType.getStype()).equals(StenType.ST_JG_CALL_ACTIVITY)) {
            return GnodeType.GT_PROCESS;
        }
      }else if ((nodeType.getStype().getSubtype()).equals(StenEsubType.SES_FLOW)) {
        if ((nodeType.getStype()).equals(StenType.ST_FLOW_SEQUENCE)) {
          return GnodeType.GT_ROOT_FLOW;
        }
      }else if ((nodeType.getStype().getSubtype()).equals(StenEsubType.SES_GATEWAY)) {
        if ((nodeType.getStype()).equals(StenType.ST_GATEWAY_EXCLUSIVE)) {
          return GnodeType.GT_PROCESS_GATEWAY;
        }
      }
    }else if ((level).equals(RtSvl.RtLevelVal.RT_LV2)) {
      if ((nodeType.getStype().getSubtype()).equals(StenEsubType.SES_TASK)) {
        return GnodeType.GT_PROCESS_TASK;
      }else if ((nodeType.getStype().getSubtype()).equals(StenEsubType.SES_GATEWAY)) {
        return GnodeType.GT_PROCESS_GATEWAY;
      }else if ((nodeType.getStype().getSubtype()).equals(StenEsubType.SES_EVENT_START)) {
        return GnodeType.GT_PROCESS_START;
      }else if ((nodeType.getStype().getSubtype()).equals(StenEsubType.SES_EVENT_END)) {
        return GnodeType.GT_PROCESS_END;
      }else if ((nodeType.getStype().getSubtype()).equals(StenEsubType.SES_FLOW)) {
        if ((nodeType.getStype()).equals(StenType.ST_FLOW_ASSOCIATION) || (nodeType.getStype()).equals(StenType.ST_FLOW_ASSOCIATION_DATA) || (nodeType.getStype()).equals(StenType.ST_FLOW_MESSAGE) || (nodeType.getStype()).equals(StenType.ST_FLOW_SEQUENCE)) {
          return GnodeType.GT_PROCESS_FLOW;
        }
      }
    }
    return null;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getRemark() {
    return remark;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }
}
