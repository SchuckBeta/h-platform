package com.oseasy.initiate.modules.actyw.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.TreeEntity;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.sys.entity.Office;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;


/**
 * 项目流程节点组Entity.
 * @author chenhao
 * @version 2017-05-23
 */
public class ActYwGnode extends TreeEntity<ActYwGnode> {

  private static final long serialVersionUID = 1L;
  private ActYwGnode parent; // 父级编号
  private String parentIds; // 所有父级编号
  private String groupId; // 流程组编号
  private String type; // 流程节点类型
  private String typefun; // 流程节点功能类型:0、不可选（子流程,结构-连接线），1、可选择（网关,任务）
  private String nodeId; // 流程节点编号
  private Boolean isShow; // 显示:1、默认（显示）；0、隐藏
  private Boolean isForm; // 是否为表单节点:0、默认（否）；1、是
  private String formId; // 表单标识
  private Integer sort;   // 排序
  private String flowGroup; // 流程执行用户或角色（默认用户）
  private Office office; // 节点所属机构:1、默认（系统全局）；
  private ActYwGroup group; // 流程组
  private ActYwNode node; // 流程节点
  private ActYwForm form; // 前一个流程节点

  private String preId; // 前一个流程编号
  private String preIds; // 前一个流程编号
  private String preIdss; // 前置所有流程编号
  private String preFunId; // 前一个业务流程编号
  private String nextId; // 后一个流程组编号
  private String nextFunId; // 后一个业务流程组编号
  private String nextIds; // 后一个流程组编号
  private String nextIdss; // 后置所有流程组编号
  private ActYwGnode preGnode; // 前一个流程节点
  private ActYwGnode nextGnode; // 后一个流程节点
  private ActYwGnode preFunGnode; // 前一个业务流程节点
  private ActYwGnode nextFunGnode; // 后一个业务流程节点
  private ActYwGnode processGnode; // 子流程节点（当业务节点为二级节点时，该属性不为空）
  private List<ActYwGnode> preGnodes; // 前一个流程节点列表
  private List<ActYwGnode> nextGnodes; // 后一个流程节点列表

  public ActYwGnode() {
    super();
  }

  public ActYwGnode(String id) {
    super(id);
  }

  public ActYwGnode(ActYwGroup group) {
    super();
    this.group = group;
  }

  public String getFlowGroup() {
    return flowGroup;
  }

  public void setFlowGroup(String flowGroup) {
    this.flowGroup = flowGroup;
  }

  @JsonBackReference
  @NotNull(message = "父级编号不能为空")
  public ActYwGnode getParent() {
    return parent;
  }

  public void setParent(ActYwGnode parent) {
    this.parent = parent;
  }

  public String getParentIds() {
    return parentIds;
  }

  public void setParentIds(String parentIds) {
    this.parentIds = parentIds;
  }

  @Length(min = 1, max = 64, message = "流程组编号长度必须介于 1 和 64 之间")
  public String getGroupId() {
    if (StringUtil.isEmpty(this.groupId) && (this.group != null)) {
      this.groupId = this.group.getId();
    }
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  @Length(min = 1, max = 64, message = "流程节点编号长度必须介于 1 和 64 之间")
  public String getNodeId() {
    if (StringUtil.isEmpty(this.nodeId) && (this.node != null)) {
      this.nodeId = this.node.getId();
    }
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public Boolean getIsShow() {
    return isShow;
  }

  public void setIsShow(Boolean isShow) {
    this.isShow = isShow;
  }

  public Integer getSort() {
    return sort;
  }

  public void setSort(Integer sort) {
    this.sort = sort;
  }

  public Office getOffice() {
    return office;
  }

  public void setOffice(Office office) {
    this.office = office;
  }

  public String getParentId() {
    return parent != null && parent.getId() != null ? parent.getId()
        : SysIds.SYS_TREE_PROOT.getId();
  }

  @JsonIgnore
  public static void sortList(List<ActYwGnode> list, List<ActYwGnode> sourcelist, String parentId,
      boolean cascade) {
    for (int i = 0; i < sourcelist.size(); i++) {
      ActYwGnode e = sourcelist.get(i);
      if (e.getParent() != null && e.getParent().getId() != null
          && e.getParent().getId().equals(parentId)) {
        list.add(e);
        if (cascade) {
          // 判断是否还有子节点, 有则继续获取子节点
          for (int j = 0; j < sourcelist.size(); j++) {
            ActYwGnode child = sourcelist.get(j);
            if (child.getParent() != null && child.getParent().getId() != null && child.getParent().getId().equals(e.getId())) {
              sortList(list, sourcelist, e.getId(), true);
              break;
            }
          }
        }
      }
    }
  }

  @JsonIgnore
  public static String getRootId() {
    return SysIds.SYS_TREE_ROOT.getId();
  }

  /**
   * 设置group属性值.
   */
  public void setGroup(ActYwGroup group) {
    this.group = group;
    if ((this.group != null) && StringUtil.isEmpty(this.groupId)) {
      this.groupId = group.getId();
    }
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTypefun() {
    return typefun;
  }

  public void setTypefun(String typefun) {
    this.typefun = typefun;
  }

  public Boolean getIsForm() {
    return isForm;
  }

  public void setIsForm(Boolean isForm) {
    this.isForm = isForm;
  }

  public ActYwNode getNode() {
    return node;
  }

  public void setNode(ActYwNode node) {
    this.node = node;
  }

  public ActYwForm getForm() {
    return form;
  }

  public void setForm(ActYwForm form) {
    this.form = form;
  }

  public void setPreIdss(String preIdss) {
    this.preIdss = preIdss;
  }

  public void setNextIdss(String nextIdss) {
    this.nextIdss = nextIdss;
  }

  public ActYwGnode getPreGnode() {
    return preGnode;
  }

  public void setPreGnode(ActYwGnode preGnode) {
    this.preGnode = preGnode;
    if ((this.preGnode != null) && StringUtil.isNotEmpty(this.preGnode.getId())) {
      this.preId = this.preGnode.getId();
    }
  }

  public ActYwGnode getNextGnode() {
    return nextGnode;
  }

  public void setNextGnode(ActYwGnode nextGnode) {
    this.nextGnode = nextGnode;
    if ((this.nextGnode != null) && StringUtil.isNotEmpty(this.nextGnode.getId())) {
      this.nextId = this.nextGnode.getId();
    }
  }

  public ActYwGnode getPreFunGnode() {
    return preFunGnode;
  }

  public void setPreFunGnode(ActYwGnode preFunGnode) {
    this.preFunGnode = preFunGnode;
    if ((this.preFunGnode != null) && StringUtil.isNotEmpty(this.preFunGnode.getId())) {
      this.preFunId = this.preFunGnode.getId();
    }
  }

  public ActYwGnode getNextFunGnode() {
    return nextFunGnode;
  }

  public void setNextFunGnode(ActYwGnode nextFunGnode) {
    this.nextFunGnode = nextFunGnode;
    if ((this.nextFunGnode != null) && StringUtil.isNotEmpty(this.nextFunGnode.getId())) {
      this.nextFunId = this.nextFunGnode.getId();
    }
  }

  public List<ActYwGnode> getPreGnodes() {
    return preGnodes;
  }

  public void setPreGnodes(List<ActYwGnode> preGnodes) {
    this.preGnodes = preGnodes;
  }

  public List<ActYwGnode> getNextGnodes() {
    return nextGnodes;
  }

  public void setNextGnodes(List<ActYwGnode> nextGnodes) {
    this.nextGnodes = nextGnodes;
  }

  public ActYwGroup getGroup() {
    return group;
  }

  public void setFormId(String formId) {
    this.formId = formId;
  }

  public void setPreId(String preId) {
    this.preId = preId;
  }

  public void setPreIds(String preIds) {
    this.preIds = preIds;
  }

  public void setPreFunId(String preFunId) {
    this.preFunId = preFunId;
  }

  public void setNextId(String nextId) {
    this.nextId = nextId;
  }

  public void setNextFunId(String nextFunId) {
    this.nextFunId = nextFunId;
  }

  public void setNextIds(String nextIds) {
    this.nextIds = nextIds;
  }

  public String getPreId() {
    if (StringUtil.isEmpty(this.preId) && (this.preGnode != null)) {
      this.preId = this.preGnode.getId();
    }
    return preId;
  }

  public String getPreFunId() {
    if (StringUtil.isEmpty(this.preFunId) && (this.preFunGnode != null)) {
      this.preFunId = this.preFunGnode.getId();
    }
    return preFunId;
  }

  public String getNextId() {
    if (StringUtil.isEmpty(this.nextId) && (this.nextGnode != null)) {
      this.nextId = this.nextGnode.getId();
    }
    return nextId;
  }

  public String getNextFunId() {
    if (StringUtil.isEmpty(this.nextFunId) && (this.nextFunGnode != null)) {
      this.nextFunId = this.nextFunGnode.getId();
    }
    return nextFunId;
  }

  public String getFormId() {
    if (StringUtil.isEmpty(this.formId) && (this.form != null)) {
      this.formId = this.form.getId();
    }
    return formId;
  }

  public String getPreIds() {
    if (StringUtil.isEmpty(this.preIds) && ((this.preGnodes != null) && (!this.preGnodes.isEmpty()))) {
      StringBuffer buffer = new StringBuffer();
      for (ActYwGnode preGnode : this.preGnodes) {
        if (preGnode != null) {
          buffer.append(preGnode.getId());
          buffer.append(",");
        }
      }
      this.preIds = buffer.toString();
    }
    return preIds;
  }

  public String getPreIdss() {
    if (StringUtil.isEmpty(this.preIdss)) {
      StringBuffer buffer;
      if ((this.preGnode != null) && StringUtil.isNotEmpty(this.preGnode.getPreIdss())) {
        buffer = new StringBuffer(this.preGnode.getPreIdss());
        buffer.append(this.preGnode.getId());
      }else{
        buffer = new StringBuffer(SysIds.SYS_TREE_ROOT.getId());
      }
      buffer.append(",");
      this.preIdss = buffer.toString();
    }
    return preIdss;
  }

  public String getNextIds() {
    if (StringUtil.isEmpty(this.nextIds) && ((this.nextGnodes != null) && (!this.nextGnodes.isEmpty()))) {
      StringBuffer buffer = new StringBuffer();
      for (ActYwGnode nextGnode : this.nextGnodes) {
        if (nextGnode != null) {
          buffer.append(nextGnode.getId());
          buffer.append(",");
        }
      }
      this.nextIds = buffer.toString();
    }
    return nextIds;
  }

  public String getNextIdss() {
    if (StringUtil.isEmpty(this.nextIdss)) {
      StringBuffer buffer;
      if ((this.nextGnode != null) && StringUtil.isNotEmpty(this.nextGnode.getNextIdss())) {
        buffer = new StringBuffer(this.nextGnode.getNextIdss());
        buffer.append(this.nextGnode.getId());
      }else{
        buffer = new StringBuffer(SysIds.SYS_TREE_ROOT.getId());
      }
      buffer.append(",");
      this.nextIdss = buffer.toString();
    }
    return nextIdss;
  }

  /**
   * 根据节点列表生成当前结点ids .
   * @author chenhao
   * @param nodes 节点
   * @return String
   */
  public static String genIds(List<ActYwGnode> nodes) {
    if (!((nodes == null) || nodes.isEmpty())) {
      StringBuffer buffer = new StringBuffer();
      for (ActYwGnode node : nodes) {
        if (node != null) {
          buffer.append(node.getId());
          buffer.append(",");
        }
      }
      return buffer.toString();
    }
    return null;
  }
  public static String genIdss(String pnIdss, String id) {
    if (StringUtil.isNotEmpty(pnIdss) && StringUtil.isNotEmpty(id)) {
      StringBuffer buffer = new StringBuffer(pnIdss);
      buffer.append(id);
      buffer.append(",");
      return buffer.toString();
    }
    return null;
  }

  public ActYwGnode getProcessGnode() {
    return processGnode;
  }

  public void setProcessGnode(ActYwGnode processGnode) {
    this.processGnode = processGnode;
  }
}