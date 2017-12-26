/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_NodeType_]]文件
 * @date 2017年6月27日 下午2:38:07
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 节点类型说明.
 * 字典：act_node_type.
 * @author chenhao
 * @date 2017年6月27日 下午2:38:07
 *
 */
public enum NodeType {
  NT_YW_ROOT(new String[]{RtSvl.RtLevelVal.RT_LV0}, RtSvl.RtTypeVal.RT_T1, null, "流程根节点"),
  NT_YW_JG_LXSH(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T1, StenType.ST_JG_SUB_PROCESS, "流程结构节点(子流程-立项审核)"),
  NT_YW_JG_ZQJC(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T2, StenType.ST_JG_SUB_PROCESS, "流程结构节点(子流程-中期检查)"),
  NT_YW_JG_JXSH(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T3, StenType.ST_JG_SUB_PROCESS, "流程结构节点(子流程-结项检查)"),
  NT_YW_JG_DBPF(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T4, StenType.ST_JG_SUB_PROCESS, "流程结构节点(子流程-答辩评分)"),
  NT_YW_JG_JGPD(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T5, StenType.ST_JG_SUB_PROCESS, "流程结构节点(子流程-结果评定)"),
  NT_YW_JG_SCORE(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T10, StenType.ST_JG_SUB_PROCESS, "流程结构节点(子流程-学分认定)"),
  NT_YW_JG_WP(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T20, StenType.ST_JG_SUB_PROCESS, "流程结构节点(子流程-网评)"),
  NT_YW_JG_LY(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T30, StenType.ST_JG_SUB_PROCESS, "流程结构节点(子流程-路演)"),
  NT_YW_JG_PJ(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T40, StenType.ST_JG_SUB_PROCESS, "流程结构节点(子流程-评级)"),
  NT_YW_JG_BLV1(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_TBLACK, StenType.ST_JG_SUB_PROCESS, "空白节点(一级)"),

  NT_YW_TASK_BLV2(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_TBLACK, StenType.ST_TASK_USER, "空白节点(二级)"),
  NT_YW_TASK_LXSH(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T1, StenType.ST_TASK_USER, "流程任务节点(用户任务-立项审核)"),
  NT_YW_TASK_ZQJC(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T2, StenType.ST_TASK_USER, "流程任务节点(用户任务-中期检查)"),
  NT_YW_TASK_JXSH(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T3, StenType.ST_TASK_USER, "流程任务节点(用户任务-结项检查)"),
  NT_YW_TASK_DBPF(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T4, StenType.ST_TASK_USER, "流程任务节点(用户任务-答辩评分)"),
  NT_YW_TASK_JGPD(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T5, StenType.ST_TASK_USER, "流程任务节点(用户任务-结果评定)"),
  NT_YW_TASK_SCORE(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T10, StenType.ST_TASK_USER, "流程任务节点(用户任务-学分认定)"),
  NT_YW_TASK_WP(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T20, StenType.ST_TASK_USER, "流程任务节点(用户任务-网评审核)"),
  NT_YW_TASK_LY(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T30, StenType.ST_TASK_USER, "流程任务节点(用户任务-网评审核)"),
  NT_YW_TASK_PJ(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T40, StenType.ST_TASK_USER, "流程任务节点(用户任务-评级审核)"),


  NT_YW_EVENT_START_NONE(new String[]{RtSvl.RtLevelVal.RT_LV1, RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T0, StenType.ST_START_EVENT_NONE, "流程开始"),
  NT_YW_EVENT_END_NONE(new String[]{RtSvl.RtLevelVal.RT_LV1, RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T0, StenType.ST_END_EVENT_NONE, "流程结束"),
  NT_YW_EVENT_SUB_PROCESS(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T0, StenType.ST_JG_SUB_PROCESS_EVENT, "事件子流程"),
  NT_YW_SUB_PROCESS(new String[]{RtSvl.RtLevelVal.RT_LV1}, RtSvl.RtTypeVal.RT_T0, StenType.ST_JG_SUB_PROCESS, "子流程"),

  NT_YW_GATEWAY_EVENT(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T0, StenType.ST_GATEWAY_EVENT, "事件网关"),
  NT_YW_GATEWAY_INCLUSIVE(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T0, StenType.ST_GATEWAY_INCLUSIVE, "包容性网关"),
  NT_YW_GATEWAY_PARALLEL(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T0, StenType.ST_GATEWAY_PARALLEL, "并行网关"),
  NT_YW_GATEWAY_EXCLUSIVE(new String[]{RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T0, StenType.ST_GATEWAY_EXCLUSIVE, "互斥网关"),
  NT_YW_FLOW_SEQUENCE(new String[]{RtSvl.RtLevelVal.RT_LV1, RtSvl.RtLevelVal.RT_LV2}, RtSvl.RtTypeVal.RT_T0, StenType.ST_FLOW_SEQUENCE, "序列连接线");

  private String[] level;
  private String type;
  private StenType stype;
  private String remark;

  private NodeType(String[] level, String type, StenType stype, String remark) {
    this.level = level;
    this.type = type;
    this.stype = stype;
    this.remark = remark;
  }

  /**
   * 根据ID获取枚举 .
   *
   * @author chenhao
   * @param level 枚举标识
   * @return List
   */
  public static List<NodeType> getByLevel(String level) {
    NodeType[] entitys = NodeType.values();
    List<NodeType> etitys = Lists.newArrayList();
    for (NodeType entity : entitys) {
      if ((level != null) && Arrays.asList(entity.getLevel()).contains(level)) {
        etitys.add(entity);
      }
    }
    return etitys;
  }

  /**
   * 根据ID获取枚举 .
   *
   * @author chenhao
   * @param level 等级
   * @param type 类型
   * @return List
   */
  public static List<NodeType> getByLevelType(String level, String type) {
    NodeType[] entitys = NodeType.values();
    List<NodeType> etitys = Lists.newArrayList();
    for (NodeType entity : entitys) {
      if (((level != null) && Arrays.asList(entity.getLevel()).contains(level)) && ((type != null) && (type).equals(entity.getType()))) {
        etitys.add(entity);
      }
    }
    return etitys;
  }

  /**
   * 根据ID获取枚举 .
   *
   * @author chenhao
   * @param level 等级
   * @param type 类型
   * @param stype 子类型
   * @return List
   */
  public static NodeType getByLevelTypeStype(String level, String type, String stype) {
    return getByLevelTypeStype(level, type, StenType.getByKey(stype));
  }
  public static NodeType getByLevelTypeStype(String level, String type, StenType stype) {
    NodeType[] entitys = NodeType.values();
    for (NodeType entity : entitys) {
      if ((level != null) && (type != null) && (stype != null)) {
        if ((type).equals(entity.getType()) && Arrays.asList(entity.getLevel()).contains(level) && ((stype).equals(entity.getStype()))) {
          return entity;
        }
      }
    }
    return null;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public StenType getStype() {
    return stype;
  }

  public void setStype(StenType stype) {
    this.stype = stype;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String[] getLevel() {
    return level;
  }

  public void setLevel(String[] level) {
    this.level = level;
  }
}
