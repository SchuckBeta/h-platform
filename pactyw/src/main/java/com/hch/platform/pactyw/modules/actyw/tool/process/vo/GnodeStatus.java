package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.sys.entity.Role;

/**
 * 单个节点处理结果状态.
 * @author chenhao
 *
 */
public class GnodeStatus {
  private String groupId;
  private String groupName;
  private String parentId;
  private String parentName;
  private Boolean isShow;
  private String remarks;

  private List<ActYwNode> nodes;//可选的节点
  private List<ActYwForm> forms;//表单
  private List<Role> roles;//角色

  private GnodeSpvo gnode;//新增的节点
  private List<GnodeSpvo> gnodes;//新增处理过的所有节点

  public String getGroupId() {
    return groupId;
  }

  public String getGroupName() {
    return groupName;
  }

  public String getParentId() {
    return parentId;
  }

  public String getParentName() {
    return parentName;
  }

  public Boolean getIsShow() {
    return isShow;
  }

  public String getRemarks() {
    return remarks;
  }

  public List<ActYwNode> getNodes() {
    return nodes;
  }

  public List<ActYwForm> getForms() {
    return forms;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public List<GnodeSpvo> getGnodes() {
    return gnodes;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  public void setIsShow(Boolean isShow) {
    this.isShow = isShow;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public void setNodes(List<ActYwNode> nodes) {
    this.nodes = nodes;
  }

  public void setForms(List<ActYwForm> forms) {
    this.forms = forms;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  public void setGnodes(List<GnodeSpvo> gnodes) {
    this.gnodes = gnodes;
  }

  public GnodeSpvo getGnode() {
    return gnode;
  }

  public void setGnode(GnodeSpvo gnode) {
    this.gnode = gnode;
  }
}
