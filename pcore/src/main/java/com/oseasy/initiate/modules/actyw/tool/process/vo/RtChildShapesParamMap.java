package com.oseasy.initiate.modules.actyw.tool.process.vo;

import java.util.List;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.utils.StringUtil;

/**
 * 更改添加到Rt的节点的属性.
 * @author chenhao
 */
public class RtChildShapesParamMap {
  private List<String> outgoingsResourceIds;
  private String targetResourceId;
  private String defaultflow;

  public RtChildShapesParamMap() {
    super();
  }

  public List<String> getOutgoingsResourceIds() {
    return outgoingsResourceIds;
  }

  public void setOutgoingsResourceIds(List<String> outgoingsResourceIds) {
    this.outgoingsResourceIds = outgoingsResourceIds;
  }

  public String getTargetResourceId() {
    return targetResourceId;
  }

  public void setTargetResourceId(String targetResourceId) {
    this.targetResourceId = targetResourceId;
  }

  public String getDefaultflow() {
    return defaultflow;
  }

  public void setDefaultflow(String defaultflow) {
    this.defaultflow = defaultflow;
  }

  /**
   * 更新节点属性，传递参数为空的属性不更新！
   * @param shapes 节点
   * @return RtChildShapes
   */
  public RtChildShapes update(RtChildShapes shapes) {
    if((this.outgoingsResourceIds != null) && (this.outgoingsResourceIds.size() > 0)){
      List<RtOutgoing> rtOutgoings = Lists.newArrayList();
      for (String ogresourceId : outgoingsResourceIds) {
        rtOutgoings.add(new RtOutgoing(ogresourceId));
      }
      shapes.setOutgoing(rtOutgoings);
    }

    if((shapes.getStencil().toString()).equals(StenType.ST_FLOW_SEQUENCE.getKey())){
      if(StringUtil.isNotEmpty(this.targetResourceId)){
        if(shapes.getTarget() == null){
          shapes.setTarget(new RtTarget(this.targetResourceId));
        }else{
          shapes.getTarget().setResourceId(this.targetResourceId);
        }
      }

      if(StringUtil.isNotEmpty(this.defaultflow)){
        RtPropertiesX properties = shapes.getProperties();
        if(properties == null){
          properties = new RtPropertiesX();
          properties.setDefaultflow(RtPropertiesX.DEFAULT_FLOW_FALSE);
        }else{
          properties.setDefaultflow(this.defaultflow);
        }
        shapes.setProperties(properties);
      }
    }
    return shapes;
  }
}
