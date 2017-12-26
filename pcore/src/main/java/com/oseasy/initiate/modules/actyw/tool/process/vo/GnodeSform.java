package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.sys.entity.Role;
/**
 * 流程设计加载表单参数.
 * @author chenhao
 */
public class GnodeSform {
  private List<String> nodeTypes;
  private String groupId;
  private String groupName;
  private String parentId;
  private String parentName;
  private Boolean isShow;
  private String remarks;

  private ActYwGnode root;//根节点
  private ActYwGnode start;//开始节点
  private ActYwGnode end;//结束节点
  private List<ActYwNode> nodes;//可选的节点
  private List<ActYwForm> forms;//表单
  private List<Role> roles;//角色

  private GnodeSpvo gnode;//新增的节点
  private List<ActYwGnode> pregnodes;//前置业务节点
  private List<ActYwGnode> nextgnodes;//后置业务节点

  public GnodeSform() {
    super();
  }

  public List<String> getNodeTypes() {
    return nodeTypes;
  }

  public void setNodeTypes(List<String> nodeTypes) {
    this.nodeTypes = nodeTypes;
  }

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

  public GnodeSpvo getGnode() {
    return gnode;
  }

  public void setGnode(GnodeSpvo gnode) {
    this.gnode = gnode;
  }

  public ActYwGnode getRoot() {
    return root;
  }

  public void setRoot(ActYwGnode root) {
    this.root = root;
  }

  public ActYwGnode getStart() {
    return start;
  }

  public void setStart(ActYwGnode start) {
    this.start = start;
  }

  public ActYwGnode getEnd() {
    return end;
  }

  public void setEnd(ActYwGnode end) {
    this.end = end;
  }

  public List<ActYwGnode> getPregnodes() {
    return pregnodes;
  }

  public void setPregnodes(List<ActYwGnode> pregnodes) {
    this.pregnodes = pregnodes;
  }

  public List<ActYwGnode> getNextgnodes() {
    return nextgnodes;
  }

  public void setNextgnodes(List<ActYwGnode> nextgnodes) {
    this.nextgnodes = nextgnodes;
  }
}
