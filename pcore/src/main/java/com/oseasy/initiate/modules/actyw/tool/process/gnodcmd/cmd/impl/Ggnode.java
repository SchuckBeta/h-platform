package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;

import net.sf.json.JSONObject;

public class Ggnode extends Ggroup{
//  group.id//id
//  nextFunGnode.id
//  nextFunGnode.node.id
//  nextFunGnode.node.name
//  parent.id
//  preFunGnode.id
//  preFunGnode.nextId
//  preFunGnode.node.id
//  preFunGnode.node.name
//  preFunGnode.preId
//  node.id
//  node.name

//  parent.id
//  private String gnodeId;//需要修改的记录ID，对应snode和gnode
//  private String preFunId;//上一个业务节点
//  private String nextFunId;//下一个业务节点

  private ActYwNode node;//当前业务节点.
  private String flowGroup;//角色
  private String formId;//表单
  private String isForm;//需要修改的记录ID，对应snode和gnode
  private Boolean isShow;//需要修改的记录ID，对应snode和gnode
  private String remarks;

  public Ggnode() {
    super();
  }

  public Ggnode(String id) {
    super(id);
  }

  public Ggnode(ActYwNode node, String flowGroup, String formId, String isForm, Boolean isShow,
      String remarks) {
    super();
    this.node = node;
    this.flowGroup = flowGroup;
    this.formId = formId;
    this.isForm = isForm;
    this.isShow = isShow;
    this.remarks = remarks;
  }

  public Ggnode(ActYwGnode preFunGnode, ActYwGnode nextFunGnode, ActYwNode node, String flowGroup, String formId, String isForm, Boolean isShow,
      String remarks) {
    super(preFunGnode, nextFunGnode);
    this.node = node;
    this.flowGroup = flowGroup;
    this.formId = formId;
    this.isForm = isForm;
    this.isShow = isShow;
    this.remarks = remarks;
  }

  public Ggnode(ActYwGnode preFunGnode, ActYwGnode nextFunGnode, ActYwNode node, String id, String flowGroup, String formId, String isForm, Boolean isShow,
      String remarks) {
    super(id, preFunGnode, nextFunGnode);
    this.node = node;
    this.flowGroup = flowGroup;
    this.formId = formId;
    this.isForm = isForm;
    this.isShow = isShow;
    this.remarks = remarks;
  }

  public ActYwNode getNode() {
    return node;
  }

  public void setNode(ActYwNode node) {
    this.node = node;
  }

  public String getFlowGroup() {
    return flowGroup;
  }

  public void setFlowGroup(String flowGroup) {
    this.flowGroup = flowGroup;
  }

  public String getFormId() {
    return formId;
  }

  public void setFormId(String formId) {
    this.formId = formId;
  }

  public String getIsForm() {
    return isForm;
  }

  public void setIsForm(String isForm) {
    this.isForm = isForm;
  }

  public Boolean getIsShow() {
    return isShow;
  }

  public void setIsShow(Boolean isShow) {
    this.isShow = isShow;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  @Override
  public Ggnode clone() {
    return (Ggnode) super.clone();
  }
}
