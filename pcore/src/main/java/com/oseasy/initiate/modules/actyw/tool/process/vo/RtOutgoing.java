/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_RtOutgoing_]]文件
 * @date 2017年6月2日 下午2:22:56
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

/**
 * 流程节点关联关系.
 *
 * @author chenhao
 * @date 2017年6月2日 下午2:22:56
 *
 */
public class RtOutgoing {
  /**
   * resourceId : sid-EF1E73EA-D33F-4970-8BB4-059B7202490D.
   */

  private String resourceId;

  public RtOutgoing() {
    super();
  }

  public RtOutgoing(String resourceId) {
    super();
    this.resourceId = resourceId;
  }

  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }
}
