package com.oseasy.initiate.modules.actyw.entity;

import org.hibernate.validator.constraints.Length;

import com.hch.platform.pconfig.common.Global;
import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.validate.IvalidateRtpl;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.Role;

/**
 * 项目流程节点Entity.
 * @author chenhao
 * @version 2017-05-23
 */
public class ActYwNode extends DataEntity<ActYwNode> implements IvalidateRtpl{

  private static final long serialVersionUID = 1L;
  private String name; // 节点名称
  private String type; // 业务模块（0-流程节点，1-立项审核，2-中期检查，3-结项审核）
  private String level; // 节点等级
  private String isSys; // 是否为系统节点:0、默认（否）；1、是
  private Boolean isForm; // 是否为表单节点:0、默认（否）；1、是
  private Boolean isFlow; // 是否为为流程节点:0、默认（否）；1、是
  private Boolean isShow; // 显示:1、默认（显示）；0、隐藏
  private String isRequire; // 必要状态(act_node_require_type):1、默认（非必要）；2、必要；3、可选
  private String isGroup; // 是否按角色执行:0、默认（用户）；1、角色；
  private String nodeType; // 流程节点类型
  private String nodeKey; // 流程节点标识
  private String nodePrekey; // 流程节点前置标识
  private String nodeNextkey; // 流程节点后置标识
  private Office office; // 节点所属机构:1、默认（系统全局）；
  private String formId; // 表单标识
  private String flowId; // 流程标识
  private String flowName; // 流程标识
  private String flowGroup; // 流程执行用户或角色（默认用户）

  private ActYwForm form; // 前一个流程节点
  private Role role; // 角色

  private String iconUrl;  //图标地址

  public ActYwNode() {
    super();
  }

  public ActYwNode(String id) {
    super(id);
  }

  @Length(min = 1, max = 255, message = "节点名称长度必须介于 1 和 255 之间")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Length(min = 1, max = 255, message = "业务模块（1-立项审核，2-中期检查，3-结项审核）长度必须介于 1 和 255 之间")
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getIsSys() {
    return isSys;
  }

  public void setIsSys(String isSys) {
    this.isSys = isSys;
  }

  public Boolean getIsForm() {
    return isForm;
  }

  public void setIsForm(Boolean isForm) {
    this.isForm = isForm;
  }

  public Boolean getIsFlow() {
    return isFlow;
  }

  public void setIsFlow(Boolean isFlow) {
    this.isFlow = isFlow;
  }

  public Boolean getIsShow() {
    return isShow;
  }

  public void setIsShow(Boolean isShow) {
    this.isShow = isShow;
  }

  public String getIsRequire() {
    return isRequire;
  }

  public void setIsRequire(String isRequire) {
    this.isRequire = isRequire;
  }

  public String getFlowName() {
    return flowName;
  }

  public void setFlowName(String flowName) {
    this.flowName = flowName;
  }

  public Office getOffice() {
    return office;
  }

  public void setOffice(Office office) {
    this.office = office;
  }

  public String getFormId() {
    if(StringUtil.isEmpty(this.formId) && ((this.form != null) && StringUtil.isNotEmpty(this.form.getId()))){
      return this.form.getId();
    }
    return formId;
  }

  public void setFormId(String formId) {
    this.formId = formId;
  }

  @Length(min = 0, max = 64, message = "流程标识长度必须介于 0 和 64 之间")
  public String getFlowId() {
    return flowId;
  }

  public void setFlowId(String flowId) {
    this.flowId = flowId;
  }

  public String getNodeType() {
    return nodeType;
  }

  public void setNodeType(String nodeType) {
    this.nodeType = nodeType;
  }

  public String getNodeKey() {
    return nodeKey;
  }

  public void setNodeKey(String nodeKey) {
    this.nodeKey = nodeKey;
  }

  public String getIsGroup() {
    return isGroup;

  }

  public ActYwForm getForm() {
    return form;
  }

  public void setForm(ActYwForm form) {
    this.form = form;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Boolean isGroup() {
    if((isGroup != null) && ((isGroup).equals(Global.YES))){
      return true;
    }
    return false;
  }

  public void setIsGroup(String isGroup) {
    this.isGroup = isGroup;
  }

  public String getFlowGroup() {
    return flowGroup;
  }

  public void setFlowGroup(String flowGroup) {
    this.flowGroup = flowGroup;
  }

  public String getNodePrekey() {
    return nodePrekey;
  }

  public void setNodePrekey(String nodePrekey) {
    this.nodePrekey = nodePrekey;
  }

  public String getNodeNextkey() {
    return nodeNextkey;
  }

  public void setNodeNextkey(String nodeNextkey) {
    this.nodeNextkey = nodeNextkey;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }
}