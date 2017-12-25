/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_RtPropertiesX_]]文件
 * @date 2017年6月2日 下午2:19:27
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

/**
 * 流程节点基本属性.
 *
 * @author chenhao
 * @date 2017年6月2日 下午2:19:27
 *
 */
public class RtPropertiesX {
  /**
   * overrideid : start.
   * name : 申报项目开始
   * documentation :
   * executionlisteners :
   * initiator : apply
   * formkeydefinition : formproperties :
   */

  private String overrideid;
  private String name;
  private String documentation;
  private String executionlisteners;
  private String initiator;
  private String formkeydefinition;
  private String formproperties;

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

  public String getFormproperties() {
    return formproperties;
  }

  public void setFormproperties(String formproperties) {
    this.formproperties = formproperties;
  }
}
