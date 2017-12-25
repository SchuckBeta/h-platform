package com.oseasy.initiate.modules.actyw.entity;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwPtpl;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRtpl;

/**
 * 项目流程组Entity.
 * @author chenhao
 * @version 2017-05-23
 */
public class ActYwGroup extends DataEntity<ActYwGroup> implements ActYwPtpl, ActYwRtpl{

  private static final long serialVersionUID = 1L;
  private String name; // 流程名称
  private String status; // 状态:0、未启用；1、启用
  private String flowId; // 流程模型ID
//  private String flowdyId; // 流程部署ID
  private String type; // 项目类型
  private String keyss; // 流程唯一标识
  private String author; // 流程作者
  private String version; // 流程版本
  private Integer sort;   // 排序

  private ActYwRstatus rstatus;   // 状态

  public ActYwGroup() {
    super();
  }

  public ActYwGroup(String id) {
    super(id);
  }

  @Length(min = 0, max = 50, message = "流程名称长度必须介于 0 和 50 之间")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Length(min = 0, max = 1, message = "状态:0、未启用；1、启用长度必须介于 0 和 1 之间")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Length(min = 0, max = 1, message = "项目类型长度必须介于 0 和 1 之间")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public ActYwRstatus getRstatus() {
    if (this.rstatus == null) {
      this.rstatus = new ActYwRstatus();
    }
    return rstatus;
  }

  public void setRstatus(ActYwRstatus rstatus) {
    this.rstatus = rstatus;
  }
}