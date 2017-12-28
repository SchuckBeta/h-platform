package com.oseasy.initiate.modules.actyw.entity;

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.TreeEntity;
import com.hch.platform.putil.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.IvalidateRtpl;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.User;


/**
 * 项目流程Entity.
 * @author chenhao
 * @version 2017-05-23
 */
public class ActYwGnode extends TreeEntity<ActYwGnode> implements IvalidateRtpl{
  public static final Integer L_ALL = 1;//所有节点
  public static final Integer L_YW = 2;//所有业务节点(level == (1/2) type = (19/70))
  public static final Integer L_PROCESS = 3;//所有子流程节点(level == 2 type = 19)
  public static final String DEFAULT_PLUX = "0";
  public static final String DEFAULT_PLUY = "200.0";

  private static final long serialVersionUID = 1L;
  private ActYwGnode parent; // 父级编号
  private String parentIds; // 所有父级编号
  private String groupId; // 自定义流程编号
  private String type; // 流程节点类型
  private String typefun; // 流程节点功能类型:0、不可选（子流程,结构-连接线），1、可选择（网关,任务）
  private String nodeId; // 流程节点编号
  private Boolean isShow; // 显示:1、默认（显示）；0、隐藏
  private Boolean isForm; // 是否为表单节点:0、默认（否）；1、是
  private Boolean hasGroup; // 是否有组
  private String formId; // 表单标识
  private Integer sort;   // 排序
  private String flowGroup; // 流程执行用户或角色（默认用户）
  private Office office; // 节点所属机构:1、默认（系统全局）；
  private ActYwGroup group; // 自定义流程
  private ActYwNode node; // 流程节点
  private ActYwForm form; // 前一个流程节点
  private Role role; // 角色
  private ActYwGtime actYwGtime;
  private String preId; // 前一个流程编号
  private String preIds; // 前一个流程编号
  private String preIdss; // 前置所有流程编号
  private String preFunId; // 前一个业务流程编号
  private String nextId; // 后一个自定义流程编号
  private String nextFunId; // 后一个业务自定义流程编号
  private String nextIds; // 后一个自定义流程编号
  private String nextIdss; // 后置所有自定义流程编号
  private ActYwGnode preGnode; // 前一个流程节点
  private ActYwGnode nextGnode; // 后一个流程节点
  private ActYwGnode preFunGnode; // 前一个业务流程节点
  private ActYwGnode nextFunGnode; // 后一个业务流程节点
  private ActYwGnode processGnode; // 子流程节点（当业务节点为二级节点时，该属性不为空）
  private List<ActYwGnode> preGnodes; // 前一个流程节点列表
  private List<ActYwGnode> preGnodess; // 前一个流程节点列表
  private List<ActYwGnode> nextGnodes; // 后一个流程节点列表
  private List<ActYwGnode> nextGnodess; // 后一个流程节点列表
  private List<ActYwGnode> preFunGnodes; // 前一个流程节点列表
  private List<ActYwGnode> preFunGnodess; // 前一个流程节点列表
  private List<ActYwGnode> nextFunGnodes; // 后一个流程节点列表
  private List<ActYwGnode> nextFunGnodess; // 后一个流程节点列表
  private List<ActYwGnode> childGnodes; // 当前结点子节点
  private List<User> users; // 角色用户

  private boolean isYw; //是否业务节点
  private boolean hasGateway; //是否有网关
  private String name; // 流程节点名称
  private String iconUrl; // 流程节点名称

  private List<String> preidssx; // 前一个流程节点列表
  private List<String> nextidssx; // 后一个流程节点列表

  private String posLux; // 左上坐标X
  private String posLuy; // 左上坐标Y
  private Float width; // 宽度
  private Float height; // 高度

  private String posAlux; // 左上坐标X
  private String posAluy; // 左上坐标Y
  private Float widtha; // 宽度
  private Float heighta; // 高度

  private boolean suspended; //是否挂起
  public ActYwGnode() {
    super();
  }

  public ActYwGnode(String id) {
    super(id);
  }

  public ActYwGtime getActYwGtime() {
	return actYwGtime;
}

public boolean getSuspended() {
	return suspended;
}

public void setSuspended(boolean suspended) {
	this.suspended = suspended;
}

public void setActYwGtime(ActYwGtime actYwGtime) {
	this.actYwGtime = actYwGtime;
}

public ActYwGnode(ActYwGroup group) {
    super();
    this.group = group;
  }

public ActYwGnode(ActYwGroup group, ActYwGnode parent) {
  super();
  this.group = group;
  this.parent = parent;
}

  public String getFlowGroup() {
    return flowGroup;
  }

  public void setFlowGroup(String flowGroup) {
    this.flowGroup = flowGroup;
  }

  public Boolean getHasGroup() {
    return hasGroup;
  }

  public void setHasGroup(Boolean hasGroup) {
    this.hasGroup = hasGroup;
  }

  public Float getWidth() {
    return width;
  }

  public void setWidth(Float width) {
    this.width = width;
  }

  public Float getHeight() {
    return height;
  }

  public void setHeight(Float height) {
    this.height = height;
  }

  public String getPosAlux() {
    return posAlux;
  }

  public void setPosAlux(String posAlux) {
    this.posAlux = posAlux;
  }

  public String getPosAluy() {
    return posAluy;
  }

  public void setPosAluy(String posAluy) {
    this.posAluy = posAluy;
  }

  public Float getWidtha() {
    return widtha;
  }

  public void setWidtha(Float widtha) {
    this.widtha = widtha;
  }

  public Float getHeighta() {
    return heighta;
  }

  public void setHeighta(Float heighta) {
    this.heighta = heighta;
  }

  @JsonBackReference
  @NotNull(message = "父级编号不能为空")
  public ActYwGnode getParent() {
    return parent;
  }

  public void setParent(ActYwGnode parent) {
    this.parent = parent;
  }

  public String getName() {
    if(StringUtil.isEmpty(this.name) && ((this.node != null) && StringUtil.isNotEmpty(this.node.getName()))){
      return this.node.getName();
    }
    return name;
  }

  public void setName(String name) {
    if(StringUtil.isEmpty(this.name) && ((this.node != null) && StringUtil.isNotEmpty(this.node.getName()))){
      this.name = this.node.getName();
    }else{
      this.name = name;
    }
  }

  public boolean isYw() {
    return isYw;
  }

  public void setYw(boolean isYw) {
    this.isYw = isYw;
  }

  public String getParentIds() {
    return parentIds;
  }

  public void setParentIds(String parentIds) {
    this.parentIds = parentIds;
  }

  @Length(min = 1, max = 64, message = "自定义流程编号长度必须介于 1 和 64 之间")
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

  public List<User> getUsers() {
    if(this.users == null){
      this.users = Lists.newArrayList();
    }
    return users;
  }

  public void setUsers(List<User> users) {
    this.users = users;
  }

  public Office getOffice() {
    return office;
  }

  public void setOffice(Office office) {
    this.office = office;
  }

  public String getParentId() {
    return parent != null && StringUtil.isNotEmpty(parent.getId()) ? parent.getId()
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

  public String getIconUrl() {
    if(StringUtil.isEmpty(this.iconUrl) && ((this.node != null) && StringUtil.isNotEmpty(this.node.getIconUrl()))){
      return this.node.getIconUrl();
    }
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  public ActYwNode getNode() {
    if((node != null) && StringUtil.isEmpty(node.getName()) && StringUtil.isNotEmpty(name)){
      node.setName(name);
    }
    if((node != null) && StringUtil.isEmpty(node.getIconUrl()) && StringUtil.isNotEmpty(iconUrl)){
      node.setIconUrl(iconUrl);
    }
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
    if((preGnodes == null) && StringUtil.isNotEmpty(preIds)){
      preGnodes = Lists.newArrayList();
      for (String curpreId : Arrays.asList(preIds.split(","))) {
        preGnodes.add(new ActYwGnode(curpreId));
      }
    }
    return preGnodes;
  }

  public void setPreGnodes(List<ActYwGnode> preGnodes) {
    this.preGnodes = preGnodes;
  }

  public List<ActYwGnode> getNextGnodes() {
    if((nextGnodes == null) && StringUtil.isNotEmpty(nextIds)){
      nextGnodes = Lists.newArrayList();
      for (String curnextId : Arrays.asList(nextIds.split(","))) {
        nextGnodes.add(new ActYwGnode(curnextId));
      }
    }
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

  public List<ActYwGnode> getPreFunGnodes() {
    return preFunGnodes;
  }

  public List<String> getPreidssx() {
    return preidssx;
  }

  public void setPreidssx(List<String> preidssx) {
    this.preidssx = preidssx;
  }

  public List<String> getNextidssx() {
    return nextidssx;
  }

  public void setNextidssx(List<String> nextidssx) {
    this.nextidssx = nextidssx;
  }

  public void setPreFunGnodes(List<ActYwGnode> preFunGnodes) {
    this.preFunGnodes = preFunGnodes;
  }

  public List<ActYwGnode> getNextFunGnodes() {
    return nextFunGnodes;
  }

  public void setNextFunGnodes(List<ActYwGnode> nextFunGnodes) {
    this.nextFunGnodes = nextFunGnodes;
  }

  public List<ActYwGnode> getChildGnodes() {
    return childGnodes;
  }

  public void setChildGnodes(List<ActYwGnode> childGnodes) {
    this.childGnodes = childGnodes;
  }

  public ActYwGnode getProcessGnode() {
    return processGnode;
  }

  public void setProcessGnode(ActYwGnode processGnode) {
    this.processGnode = processGnode;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String getPosLux() {
    return posLux;
  }

  public void setPosLux(String posLux) {
    this.posLux = posLux;
  }

  public String getPosLuy() {
    return posLuy;
  }

  public void setPosLuy(String posLuy) {
    this.posLuy = posLuy;
  }

  public boolean isHasGateway() {
    return hasGateway;
  }

  public void setHasGateway(boolean hasGateway) {
    this.hasGateway = hasGateway;
  }

  public String getPreId() {
    if ((this.preId == null) && (this.preGnode != null) && StringUtil.isNotEmpty(this.preGnode.getId())) {
      this.preId = this.preGnode.getId();
    }
    return preId;
  }

  public String getPreFunId() {
    if ((this.preFunId == null) && (this.preFunGnode != null) && StringUtil.isNotEmpty(this.preFunGnode.getId())) {
      this.preFunId = this.preFunGnode.getId();
    }
    return preFunId;
  }

  public String getNextId() {
    if ((this.nextId == null) && (this.nextGnode != null) && StringUtil.isNotEmpty(this.nextGnode.getId())) {
      this.nextId = this.nextGnode.getId();
    }
    return nextId;
  }

  public String getNextFunId() {
    if ((this.nextFunId == null) && (this.nextFunGnode != null) && StringUtil.isNotEmpty(this.nextFunGnode.getId())) {
      this.nextFunId = this.nextFunGnode.getId();
    }
    return nextFunId;
  }

  public String getFormId() {
    if ((this.formId  == null) && (this.form != null) && StringUtil.isNotEmpty(this.form.getId())) {
      this.formId = this.form.getId();
    }
    return formId;
  }

  public String getPreIds() {
    if ((this.preIds  == null) && ((this.preGnodes != null) && (!this.preGnodes.isEmpty()))) {
      StringBuffer buffer = new StringBuffer();
      for (ActYwGnode preGnode : this.preGnodes) {
        if ((preGnode != null) && StringUtil.isNotEmpty(preGnode.getId())) {
          buffer.append(preGnode.getId());
          buffer.append(",");
        }
      }
      this.preIds = buffer.toString();
    }else if (StringUtil.isEmpty(this.preIds) && StringUtil.isNotEmpty(this.preId)) {
      this.preIds = this.preId + ",";
    }
    return preIds;
  }

  public String getPreIdss() {
    StringBuffer buffer;
    if ((this.preIdss == null) && (this.preGnodess != null) && (this.preGnodess.size() > 0)) {
      buffer = new StringBuffer();
      for (ActYwGnode gnode : this.preGnodess) {
        buffer.append(gnode.getId());
        buffer.append(",");
      }
//    }else if(StringUtil.isEmpty(this.preIdss)){
//      buffer = new StringBuffer(SysIds.SYS_TREE_ROOT.getId());
//      buffer.append(",");
    }else{
      return preIdss;
    }
    return buffer.toString();
  }

  public String getNextIds() {
    if ((this.nextIds == null) && ((this.nextGnodes != null) && (!this.nextGnodes.isEmpty()))) {
      StringBuffer buffer = new StringBuffer();
      for (ActYwGnode nextGnode : this.nextGnodes) {
        if ((nextGnode != null) && StringUtil.isNotEmpty(nextGnode.getId())) {
          buffer.append(nextGnode.getId());
          buffer.append(",");
        }
      }
      this.nextIds = buffer.toString();
    }else if (StringUtil.isEmpty(this.nextIds) && StringUtil.isNotEmpty(this.nextId)) {
      this.nextIds = this.nextId + ",";
    }
    return nextIds;
  }

  public String getNextIdss() {
    StringBuffer buffer;
    if ((this.nextIdss == null) && (this.nextGnodess != null) && (this.nextGnodess.size() > 0)) {
      buffer = new StringBuffer();
      for (ActYwGnode gnode : this.nextGnodess) {
        buffer.append(gnode.getId());
        buffer.append(",");
      }
    }else{
      return nextIdss;
    }
    return buffer.toString();
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
        if ((node != null) && StringUtil.isNotEmpty(node.getId())) {
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

  /**
   * 新增节点.
   * @param srcGnodes 原列表
   * @param gnode 新节点
   * @return List
   */
  public static List<ActYwGnode> addGnNode(List<ActYwGnode> srcGnodes, ActYwGnode gnode) {
    if (srcGnodes == null) {
      srcGnodes = Lists.newArrayList();
    }

    if (gnode == null) {
      return srcGnodes;
    }

    srcGnodes.add(gnode);
    return srcGnodes;
  }

  public List<ActYwGnode> getPreGnodess() {
    if((preGnodess == null) && StringUtil.isNotEmpty(preIdss)){
      preGnodess = Lists.newArrayList();
      for (String curpreId : Arrays.asList(preIdss.split(","))) {
        preGnodess.add(new ActYwGnode(curpreId));
      }
    }
    return preGnodess;
  }

  public void setPreGnodess(List<ActYwGnode> preGnodess) {
    this.preGnodess = preGnodess;
  }

  public List<ActYwGnode> getNextGnodess() {
    if((nextGnodess == null) && StringUtil.isNotEmpty(nextIdss)){
      nextGnodess = Lists.newArrayList();
      for (String curnextId : Arrays.asList(nextIdss.split(","))) {
        nextGnodess.add(new ActYwGnode(curnextId));
      }
    }
    return nextGnodess;
  }

  public void setNextGnodess(List<ActYwGnode> nextGnodess) {
    this.nextGnodess = nextGnodess;
  }

  public List<ActYwGnode> getPreFunGnodess() {
    return preFunGnodess;
  }

  public void setPreFunGnodess(List<ActYwGnode> preFunGnodess) {
    this.preFunGnodess = preFunGnodess;
  }

  public List<ActYwGnode> getNextFunGnodess() {
    return nextFunGnodess;
  }

  public void setNextFunGnodess(List<ActYwGnode> nextFunGnodess) {
    this.nextFunGnodess = nextFunGnodess;
  }

  public static List<ActYwGnode> sortLinks(List<ActYwGnode> srcList, List<ActYwGnode> targList, String start, Boolean isPre) {
    return sortLinks(srcList, targList, new ActYwGnode(start), isPre);
  }

  /**
   * 节点排序.
   * @param srcList 原列表
   * @param targList 目标列表
   * @param start 开始节点
   * @return List
   */
  public static List<ActYwGnode> sortLinks(List<ActYwGnode> srcList, List<ActYwGnode> targList, ActYwGnode start, Boolean isPre) {
    if(targList == null){
      targList = Lists.newArrayList();
    }
    ActYwGnode nextStart = sortLinks(srcList, start, isPre);
    while(nextStart != null){
      targList.add(nextStart);
      nextStart = sortLinks(srcList, nextStart, isPre);
    }
    return targList;
  }

  public static ActYwGnode sortLinks(List<ActYwGnode> srcList, ActYwGnode start, Boolean isPre) {
    if(isPre){
      for (ActYwGnode curgnode : srcList) {
        if((start.getId()).equals(curgnode.getPreId())){
          return curgnode;
        }
      }
    }else{
      for (ActYwGnode curgnode : srcList) {
        if((start.getId()).equals(curgnode.getNextId())){
          return curgnode;
        }
      }
    }
    return null;
  }

  public static ActYwGnode instance(ActYwGnode gnode) {
    return instance(gnode, gnode.getIsNewRecord());
  }

  public static ActYwGnode instance(ActYwGnode gnode, Boolean isNewRecord) {
    ActYwGnode newgnode = new ActYwGnode();
    newgnode.setId(gnode.getId());
    newgnode.setIsNewRecord(isNewRecord);
    newgnode.setSort(gnode.getSort());   // 排序
    newgnode.setRemarks(gnode.getRemarks()); // 备注
    newgnode.setCreateBy(gnode.getCreateBy());  // 创建者
    newgnode.setCreateDate(gnode.getCreateDate());  // 创建日期
    newgnode.setUpdateBy(gnode.getUpdateBy());  // 更新者
    newgnode.setUpdateDate(gnode.getUpdateDate());  // 更新日期
    newgnode.setDelFlag(gnode.getDelFlag());   // 删除标记（0：正常；1：删除；2：审核）

    newgnode.setParent(gnode.getParent());
    newgnode.setParentIds(gnode.getParentIds());
    newgnode.setGroupId(gnode.getGroupId());
    newgnode.setType(gnode.getType());
    newgnode.setTypefun(gnode.getTypefun());
    newgnode.setNodeId(gnode.getNodeId());
    newgnode.setIsShow(gnode.getIsShow());
    newgnode.setIsForm(gnode.getIsForm());
    newgnode.setFormId(gnode.getFormId());
    newgnode.setFlowGroup(gnode.getFlowGroup());
    newgnode.setOffice(gnode.getOffice());
    newgnode.setGroup(gnode.getGroup());
    newgnode.setNode(gnode.getNode());
    newgnode.setForm(gnode.getForm());
    newgnode.setRole(gnode.getRole());
    newgnode.setPreId(gnode.getPreId());
    newgnode.setPreIds(gnode.getPreIds());
    newgnode.setPreIdss(gnode.getPreIdss());
    newgnode.setPreFunId(gnode.getPreFunId());
    newgnode.setNextId(gnode.getNextId());
    newgnode.setNextFunId(gnode.getNextFunId());
    newgnode.setNextIds(gnode.getNextIds());
    newgnode.setNextIdss(gnode.getNextIdss());
    newgnode.setPreGnode(gnode.getPreGnode());
    newgnode.setNextGnode(gnode.getNextGnode());
    newgnode.setPreFunGnode(gnode.getPreFunGnode());
    newgnode.setNextFunGnode(gnode.getNextFunGnode());
    newgnode.setProcessGnode(gnode.getProcessGnode());
    newgnode.setPreGnodes(gnode.getPreGnodes());
    newgnode.setPreGnodess(gnode.getPreGnodess());
    newgnode.setNextGnodes(gnode.getNextGnodes());
    newgnode.setNextGnodess(gnode.getNextGnodess());
    newgnode.setPreFunGnodes(gnode.getPreFunGnodes());
    newgnode.setPreFunGnodess(gnode.getPreFunGnodess());
    newgnode.setNextFunGnodes(gnode.getNextFunGnodes());
    newgnode.setNextFunGnodess(gnode.getNextFunGnodess());
    newgnode.setChildGnodes(gnode.getChildGnodes());
    newgnode.setYw(gnode.isYw());
    newgnode.setHasGateway(gnode.isHasGateway());
    newgnode.setPreidssx(gnode.getPreidssx());
    newgnode.setNextidssx(gnode.getNextidssx());
    newgnode.setPosLux(gnode.getPosLux());
    newgnode.setPosLuy(gnode.getPosLuy());
    return newgnode;
  }
}