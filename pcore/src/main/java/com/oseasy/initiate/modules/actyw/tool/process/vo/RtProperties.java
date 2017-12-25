/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_RtProperties_]]文件
 * @date 2017年6月2日 下午2:12:13
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

/**
 * 流程BPMNDi属性.
 *
 * @author chenhao
 * @date 2017年6月2日 下午2:12:13
 *
 */
public class RtProperties {
  /**
   * process_id : state_project_audit name : 国创项目审核流程 documentation : process_author : zhangzheng
   * process_version : process_namespace : http://www.activiti.org/processdef executionlisteners :
   * eventlisteners : signaldefinitions : messagedefinitions : conditionsequenceflow : ${pass==1}
   */

  private String process_id;
  private String name;
  private String documentation;
  private String process_author;
  private String process_version;
  private String process_namespace;
  private String executionlisteners;
  private String eventlisteners;
  private String signaldefinitions;
  private String messagedefinitions;
  private String conditionsequenceflow;

  public String getProcess_id() {
    return process_id;
  }

  public void setProcess_id(String process_id) {
    this.process_id = process_id;
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

  public String getProcess_author() {
    return process_author;
  }

  public void setProcess_author(String process_author) {
    this.process_author = process_author;
  }

  public String getProcess_version() {
    return process_version;
  }

  public void setProcess_version(String process_version) {
    this.process_version = process_version;
  }

  public String getProcess_namespace() {
    return process_namespace;
  }

  public void setProcess_namespace(String process_namespace) {
    this.process_namespace = process_namespace;
  }

  public String getExecutionlisteners() {
    return executionlisteners;
  }

  public void setExecutionlisteners(String executionlisteners) {
    this.executionlisteners = executionlisteners;
  }

  public String getEventlisteners() {
    return eventlisteners;
  }

  public void setEventlisteners(String eventlisteners) {
    this.eventlisteners = eventlisteners;
  }

  public String getSignaldefinitions() {
    return signaldefinitions;
  }

  public void setSignaldefinitions(String signaldefinitions) {
    this.signaldefinitions = signaldefinitions;
  }

  public String getMessagedefinitions() {
    return messagedefinitions;
  }

  public void setMessagedefinitions(String messagedefinitions) {
    this.messagedefinitions = messagedefinitions;
  }

  public String getConditionsequenceflow() {
    return conditionsequenceflow;
  }

  public void setConditionsequenceflow(String conditionsequenceflow) {
    this.conditionsequenceflow = conditionsequenceflow;
  }
}
