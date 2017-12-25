/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_RtChildShapes_]]文件
 * @date 2017年6月2日 下午2:18:49
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 流程BPMNDi 画布节点.
 *
 * @author chenhao
 * @date 2017年6月2日 下午2:18:49
 *
 */
public class RtChildShapes {
  /**
   * resourceId : sid-E58DA607-553C-4937-BCBB-E5E4AB217966 properties :
   * {"overrideid":"start","name":"申报项目开始","documentation":"","executionlisteners":"","initiator":"apply","formkeydefinition":"","formproperties":""}
   * stencil : {"id":"StartNoneEvent"} childShapes : [] outgoing :
   * [{"resourceId":"sid-EF1E73EA-D33F-4970-8BB4-059B7202490D"}] bounds :
   * {"lowerRight":{"x":681.296875,"y":45},"upperLeft":{"x":651.296875,"y":15}} dockers : [] target
   * : {"resourceId":"sid-2F9D9364-C2AD-45E4-A602-F8037678929F"}
   */

  private String resourceId;
  private RtPropertiesX properties;
  private RtStencilX stencil;
  private RtBoundsX bounds;
  private List<RtOutgoing> outgoing;
  private RtTarget target;
  private List<RtChildShapes> childShapes;
  private List<RtDocker> dockers;

  public RtChildShapes() {
    super();
    this.childShapes = Lists.newArrayList();
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public RtPropertiesX getProperties() {
    return properties;
  }

  public void setProperties(RtPropertiesX properties) {
    this.properties = properties;
  }

  public RtStencilX getStencil() {
    return stencil;
  }

  public void setStencil(RtStencilX stencil) {
    this.stencil = stencil;
  }

  public RtBoundsX getBounds() {
    return bounds;
  }

  public void setBounds(RtBoundsX bounds) {
    this.bounds = bounds;
  }

  public RtTarget getTarget() {
    return target;
  }

  public void setTarget(RtTarget target) {
    this.target = target;
  }

  public List<RtChildShapes> getChildShapes() {
    return childShapes;
  }

  public void setChildShapes(List<RtChildShapes> childShapes) {
    this.childShapes = childShapes;
  }

  public List<RtOutgoing> getOutgoing() {
    return outgoing;
  }

  public void setOutgoing(List<RtOutgoing> outgoing) {
    this.outgoing = outgoing;
  }

  public List<RtDocker> getDockers() {
    return dockers;
  }

  public void setDockers(List<RtDocker> dockers) {
    this.dockers = dockers;
  }
}
