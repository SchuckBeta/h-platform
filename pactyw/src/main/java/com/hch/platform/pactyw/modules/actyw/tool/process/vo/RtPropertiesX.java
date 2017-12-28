/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_RtPropertiesX_]]文件
 * @date 2017年6月2日 下午2:19:27
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

import static org.hamcrest.CoreMatchers.equalTo;

import com.hch.platform.putil.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwTool;

/**
 * 流程节点基本属性.
 *
 * @author chenhao
 * @date 2017年6月2日 下午2:19:27
 *
 */
public class RtPropertiesX {
  public static final String EXCLUSIVEDEFINITION_TRUE = "true";
  public static final String MULTIINSTANCE_TYPE_PARALLEL = "Parallel";
  public static final String MULTIINSTANCE_TYPE_NONE = "None";
  public static final String ASYNCHRONOUSDEFINITION_FALSE = "fasle";
  public static final String ISFORCOMPENSATION_FALSE = "fasle";
  public static final String SHOWDIAMONDMARKER_FALSE = "fasle";
  public static final String DEFAULT_FLOW_FALSE = "fasle";
  private String overrideid;
  private String name;
  private String documentation;
  private String executionlisteners;
  private String initiator;
  private String formkeydefinition;
  private RtPxFormproperties formproperties;

  private String asynchronousdefinition;//fasle
  private String exclusivedefinition;//true
  private String multiinstance_type;//Parallel

  private String multiinstance_cardinality;//""
  private String multiinstance_collection;//${managers}
  private String multiinstance_variable;//
  private String multiinstance_condition;//""
  private String isforcompensation;//false
  private RtPxUsertaskassignment usertaskassignment;//""
  private String duedatedefinition;//""
  private String prioritydefinition;//""
  private RtPxTasklisteners tasklisteners;//""
  private String conditionsequenceflow;
  private String defaultflow;
  private String showdiamondmarker;

  public RtPropertiesX() {
    super();
  }

  /**
   * 根据 overrideid和名称生成RtPropertiesX对象.
   * @author chenhao
   * @param overrideid 标识ID
   * @param name 名称
   */
  public RtPropertiesX(String overrideid, String name) {
    super();
    this.overrideid = overrideid;
    this.name = name;
  }

  public String getOverrideid() {
    return overrideid;
  }

  public void setOverrideid(String overrideid) {
    this.overrideid = overrideid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDocumentation() {
    return documentation;
  }

  public void setDocumentation(String documentation) {
    this.documentation = documentation;
  }

  public String getExecutionlisteners() {
    return executionlisteners;
  }

  public void setExecutionlisteners(String executionlisteners) {
    this.executionlisteners = executionlisteners;
  }

  public String getInitiator() {
    return initiator;
  }

  public void setInitiator(String initiator) {
    this.initiator = initiator;
  }

  public String getFormkeydefinition() {
    return formkeydefinition;
  }

  public void setFormkeydefinition(String formkeydefinition) {
    this.formkeydefinition = formkeydefinition;
  }

  public RtPxFormproperties getFormproperties() {
    return formproperties;
  }

  public void setFormproperties(RtPxFormproperties formproperties) {
    this.formproperties = formproperties;
  }

  public String getAsynchronousdefinition() {
    if(StringUtil.isEmpty(this.asynchronousdefinition)){
      this.asynchronousdefinition = ASYNCHRONOUSDEFINITION_FALSE;
    }
    return asynchronousdefinition;
  }

  public void setAsynchronousdefinition(String asynchronousdefinition) {
    this.asynchronousdefinition = asynchronousdefinition;
  }

  public String getExclusivedefinition() {
    if(StringUtil.isEmpty(this.exclusivedefinition)){
      this.exclusivedefinition = EXCLUSIVEDEFINITION_TRUE;
    }
    return exclusivedefinition;
  }

  public void setExclusivedefinition(String exclusivedefinition) {
    this.exclusivedefinition = exclusivedefinition;
  }

  public String getMultiinstance_type() {
    if(StringUtil.isEmpty(this.multiinstance_type)){
      this.multiinstance_type = MULTIINSTANCE_TYPE_PARALLEL;
    }
    return multiinstance_type;
  }

  public void setMultiinstance_type(String multiinstance_type) {
    this.multiinstance_type = multiinstance_type;
  }

  public String getMultiinstance_cardinality() {
    return multiinstance_cardinality;
  }

  public void setMultiinstance_cardinality(String multiinstance_cardinality) {
    this.multiinstance_cardinality = multiinstance_cardinality;
  }

  public String getMultiinstance_collection() {
    return multiinstance_collection;
  }

  public void setMultiinstance_collection(String multiinstance_collection) {
    this.multiinstance_collection = multiinstance_collection;
  }

  public String getMultiinstance_variable() {
    return multiinstance_variable;
  }

  public void setMultiinstance_variable(String multiinstance_variable) {
    this.multiinstance_variable = multiinstance_variable;
  }

  public String getMultiinstance_condition() {
    return multiinstance_condition;
  }

  public void setMultiinstance_condition(String multiinstance_condition) {
    this.multiinstance_condition = multiinstance_condition;
  }

  public String getShowdiamondmarker() {
    return showdiamondmarker;
  }

  public void setShowdiamondmarker(String showdiamondmarker) {
    this.showdiamondmarker = showdiamondmarker;
  }

  public String getIsforcompensation() {
    if(StringUtil.isEmpty(this.isforcompensation)){
      this.isforcompensation = ISFORCOMPENSATION_FALSE;
    }
    return isforcompensation;
  }

  public void setIsforcompensation(String isforcompensation) {
    this.isforcompensation = isforcompensation;
  }

  public RtPxUsertaskassignment getUsertaskassignment() {
    return usertaskassignment;
  }

  public void setUsertaskassignment(RtPxUsertaskassignment usertaskassignment) {
    this.usertaskassignment = usertaskassignment;
  }

  public String getDuedatedefinition() {
    return duedatedefinition;
  }

  public void setDuedatedefinition(String duedatedefinition) {
    this.duedatedefinition = duedatedefinition;
  }

  public String getPrioritydefinition() {
    return prioritydefinition;
  }

  public void setPrioritydefinition(String prioritydefinition) {
    this.prioritydefinition = prioritydefinition;
  }

  public RtPxTasklisteners getTasklisteners() {
    return tasklisteners;
  }

  public void setTasklisteners(RtPxTasklisteners tasklisteners) {
    this.tasklisteners = tasklisteners;
  }

  public String getConditionsequenceflow() {
    return conditionsequenceflow;
  }

  public void setConditionsequenceflow(String conditionsequenceflow) {
    this.conditionsequenceflow = conditionsequenceflow;
  }

  public String getDefaultflow() {
    return defaultflow;
  }

  public void setDefaultflow(String defaultflow) {
    this.defaultflow = defaultflow;
  }

  public static RtPropertiesX setUsertaskassignment(RtPropertiesX rtPropertiesX, String flowGroup) {
    if(rtPropertiesX == null){
      rtPropertiesX = new RtPropertiesX();
    }

    if(StringUtil.isEmpty(flowGroup)){
      return rtPropertiesX;
    }

    RtPxAssignment rtPxAssignment = new RtPxAssignment();
    rtPxAssignment.setAssignee(ActYwTool.FLOW_ROLE_PREFIX + ActYwTool.FLOW_ROLE_ID_PREFIX + flowGroup + ActYwTool.FLOW_ROLE_POSTFIX);
    rtPropertiesX.setUsertaskassignment(new RtPxUsertaskassignment(rtPxAssignment));
    rtPropertiesX.setMultiinstance_collection(ActYwTool.FLOW_ROLE_PREFIX + ActYwTool.FLOW_ROLE_ID_PREFIX + flowGroup + ActYwTool.FLOW_ROLE_POSTFIX_S + ActYwTool.FLOW_ROLE_POSTFIX);
    if((rtPropertiesX.getMultiinstance_type()).equals(MULTIINSTANCE_TYPE_PARALLEL)){
      rtPropertiesX.setMultiinstance_variable(ActYwTool.FLOW_ROLE_ID_PREFIX + flowGroup);
    }
    return rtPropertiesX;
  }
}
