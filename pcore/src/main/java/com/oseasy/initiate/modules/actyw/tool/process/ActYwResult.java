/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process
 * @Description [[_ActYwResult_]]文件
 * @date 2017年6月2日 下午2:05:19
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process;

import java.util.List;

import com.oseasy.initiate.common.utils.json.JsonAliUtils;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwPtpl;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRtpl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtBounds;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtChildShapes;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtProperties;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtStencil;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtStencilset;

/**
 * 工作流结果集(Json).
 *
 * @author chenhao
 * @date 2017年6月2日 下午2:05:19
 *
 */
public class ActYwResult implements ActYwRtpl, ActYwPtpl{
  /**
   * . resourceId : b0f5e4b9748c416ba206a672dd013086 properties :
   * {"process_id":"state_project_audit","name":"国创项目审核流程","documentation":"","process_author":"zhangzheng","process_version":"","process_namespace":"http://www.activiti.org/processdef","executionlisteners":"","eventlisteners":"","signaldefinitions":"","messagedefinitions":"","conditionsequenceflow":"${pass==1}"}
   * stencil : {"id":"BPMNDiagram"}
   * childShapes : 子节点
   * bounds : {"lowerRight":{"x":1200,"y":2348},"upperLeft":{"x":0,"y":0}} stencilset :
   * {"url":"stencilsets/bpmn2.0/bpmn2.0.json","namespace":"http://b3mn.org/stencilset/bpmn2.0#"}
   * ssextensions : []
   */

  private String resourceId;
  private RtProperties properties;
  private RtStencil stencil;
  private RtBounds bounds;
  private RtStencilset stencilset;
  private List<RtChildShapes> childShapes;
  private List<?> ssextensions;

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public RtProperties getProperties() {
    return properties;
  }

  public void setProperties(RtProperties properties) {
    this.properties = properties;
  }

  public RtStencil getStencil() {
    return stencil;
  }

  public void setStencil(RtStencil stencil) {
    this.stencil = stencil;
  }

  public RtBounds getBounds() {
    return bounds;
  }

  public void setBounds(RtBounds bounds) {
    this.bounds = bounds;
  }

  public RtStencilset getStencilset() {
    return stencilset;
  }

  public void setStencilset(RtStencilset stencilset) {
    this.stencilset = stencilset;
  }

  public List<RtChildShapes> getChildShapes() {
    return childShapes;
  }

  public void setChildShapes(List<RtChildShapes> childShapes) {
    this.childShapes = childShapes;
  }

  public List<?> getSsextensions() {
    return ssextensions;
  }

  public void setSsextensions(List<?> ssextensions) {
    this.ssextensions = ssextensions;
  }

  /**
   * 根据json生成对象.
   * @author chenhao
   * @param jsons json
   * @return ActYwResult
   */
  public static ActYwResult genActYwResult(String jsons) {
    return genActYwResults(jsons).get(0);
  }

  /**
   * 根据json生成对象.
   * @author chenhao
   * @param json json
   * @return List
   */
  public static List<ActYwResult> genActYwResults(String json) {
    return (List<ActYwResult>) JsonAliUtils.toBean("[" + json + "]", ActYwResult.class);
  }
}
