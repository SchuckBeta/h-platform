package com.oseasy.initiate.modules.actyw.tool.process.vo;

/**
 * resourceId指向的节点在Rt中的位置属性.
 * @author chenhao
 */
public class RtChildShapesMap {
  private Integer index;
  private RtChildShapes shapes;
  public RtChildShapesMap(Integer index, RtChildShapes shapes) {
    super();
    this.index = index;
    this.shapes = shapes;
  }
  public Integer getIndex() {
    return index;
  }
  public void setIndex(Integer index) {
    this.index = index;
  }
  public RtChildShapes getShapes() {
    return shapes;
  }
  public void setShapes(RtChildShapes shapes) {
    this.shapes = shapes;
  }
}
