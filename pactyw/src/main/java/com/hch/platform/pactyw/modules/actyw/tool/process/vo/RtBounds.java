/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_RtBounds_]]文件
 * @date 2017年6月2日 下午2:16:17
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

/**
 * 流程BPMNDi 画布边界.
 *
 * @author chenhao
 * @date 2017年6月2日 下午2:16:17
 *
 */
public class RtBounds {
  /**
   * lowerRight : {"x":1200,"y":2348} upperLeft : {"x":0,"y":0}.
   */

  private RtLowerRight lowerRight;
  private RtUpperLeft upperLeft;

  public RtLowerRight getLowerRight() {
    return lowerRight;
  }

  public void setLowerRight(RtLowerRight lowerRight) {
    this.lowerRight = lowerRight;
  }

  public RtUpperLeft getUpperLeft() {
    return upperLeft;
  }

  public void setUpperLeft(RtUpperLeft upperLeft) {
    this.upperLeft = upperLeft;
  }
}
