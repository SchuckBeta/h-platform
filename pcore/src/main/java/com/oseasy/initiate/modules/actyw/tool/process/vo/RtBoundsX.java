/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_RtBoundsX_]]文件
 * @date 2017年6月2日 下午2:20:32
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

/**
 * 流程节点面板.
 *
 * @author chenhao
 * @date 2017年6月2日 下午2:20:32
 *
 */
public class RtBoundsX {
  /**
   * lowerRight : {"x":681.296875,"y":45} upperLeft : {"x":651.296875,"y":15}.
   */

  private RtLowerRightX lowerRight;
  private RtUpperLeftX upperLeft;

  public RtLowerRightX getLowerRight() {
    return lowerRight;
  }

  public void setLowerRight(RtLowerRightX lowerRight) {
    this.lowerRight = lowerRight;
  }

  public RtUpperLeftX getUpperLeft() {
    return upperLeft;
  }

  public void setUpperLeft(RtUpperLeftX upperLeft) {
    this.upperLeft = upperLeft;
  }
}
