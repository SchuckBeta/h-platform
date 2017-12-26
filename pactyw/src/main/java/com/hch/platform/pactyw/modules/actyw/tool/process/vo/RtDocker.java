/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_RtDocker_]]文件
 * @date 2017年6月2日 下午2:20:32
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

/**
 * RtDocker关联坐标.
 *
 * @author chenhao
 * @date 2017年6月2日 下午2:20:32
 *
 */
public class RtDocker {
  private Double x;
  private Double y;

  public RtDocker() {
    super();
  }

  /**
   * 根据坐标生成RtDocker对象 .
   * @author chenhao
   * @param x x坐标
   * @param y y坐标
   */
  public RtDocker(Double x, Double y) {
    super();
    this.x = x;
    this.y = y;
  }

  public Double getX() {
    return x;
  }

  public void setX(Double x) {
    this.x = x;
  }

  public Double getY() {
    return y;
  }

  public void setY(Double y) {
    this.y = y;
  }
}
