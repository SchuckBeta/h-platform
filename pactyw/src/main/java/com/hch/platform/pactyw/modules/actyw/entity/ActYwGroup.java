package com.oseasy.initiate.modules.actyw.entity;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwPtpl;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRtpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowProjectType;

/**
 * 自定义流程Entity.
 * @author chenhao
 * @version 2017-05-23
 */
public class ActYwGroup extends DataEntity<ActYwGroup> implements ActYwPtpl, ActYwRtpl{
  private static final long serialVersionUID = 1L;
  public static final String GROUP_DEPLOY_0 = "0";
  public static final String GROUP_DEPLOY_1 = "1";
  public static final String JK_GROUP = "group";

  private String name; // 流程名称
  private String status; // 状态:0、未启用；1、启用
  private String flowId; // 流程模型ID
  private String flowType; // 流程类型
//  private String flowdyId; // 流程部署ID
  private String type; // 项目类型
  private String keyss; // 流程唯一标识
  private String author; // 流程作者
  private String version; // 流程版本
  private Integer sort;   // 排序
  private String datas;   // 设计前端JSON

  private List<ActYw> actYws; // 项目流程
  private ActYwRstatus<ActYwGnode> rstatus;   // 状态

  public ActYwGroup() {
    super();
  }

  public ActYwGroup(String id) {
    super(id);
  }

  public List<ActYw> getActYws() {
    return actYws;
  }

  public void setActYw(List<ActYw> actYws) {
    this.actYws = actYws;
  }

  @Length(min = 0, max = 50, message = "流程名称长度必须介于 0 和 50 之间")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFlowType() {
    return flowType;
  }

  public void setFlowType(String flowType) {
    this.flowType = flowType;
  }

  @Length(min = 0, max = 1, message = "状态:0、未启用；1、启用长度必须介于 0 和 1 之间")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getType() {
    return type;
  }

  public String[] getTypes() {
    String[] types = null;
    if(StringUtil.isNotEmpty(this.type) && (this.type.length() > 1)){
      types = this.type.substring(0, (this.type.length()-1)).split(",");
    }
    return types;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setType(FlowProjectType[] flowProjectTypes) {
    StringBuffer proType = new StringBuffer();
    for (FlowProjectType flowProjectType : flowProjectTypes) {
      proType.append(flowProjectType.getKey());
      proType.append(",");
    }
    this.type = proType.toString();
  }

  public Integer getSort() {
    return sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getFlowId() {
    return flowId;
  }

  public void setFlowId(String flowId) {
    this.flowId = flowId;
  }

  public String getKeyss() {
    return keyss;
  }

  public void setKeyss(String keyss) {
    this.keyss = keyss;
  }

  public ActYwRstatus<ActYwGnode> getRstatus() {
    if (this.rstatus == null) {
      this.rstatus = new ActYwRstatus<ActYwGnode>();
    }
    return rstatus;
  }

  public void setRstatus(ActYwRstatus<ActYwGnode> rstatus) {
    this.rstatus = rstatus;
  }

  public String getDatas() {
    return datas;
  }

  public void setDatas(String datas) {
    this.datas = datas;
  }
}