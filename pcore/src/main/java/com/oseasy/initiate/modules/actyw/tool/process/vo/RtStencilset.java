/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.vo
 * @Description [[_RtStencilset_]]文件
 * @date 2017年6月2日 下午2:18:13
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.vo;

/**
 * 流程BPMNDi命名空间信息.
 *
 * @author chenhao
 * @date 2017年6月2日 下午2:18:13
 *
 */
public class RtStencilset {
  /**
   * url : stencilsets/bpmn2.0/bpmn2.0.json namespace : http://b3mn.org/stencilset/bpmn2.0#
   */

  private String url;
  private String namespace;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }
}
