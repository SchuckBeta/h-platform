/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd.vo
 * @Description [[_ActYwRgnode_]]文件
 * @date 2017年6月18日 下午2:10:19
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd.vo;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRtpl;

/**
 * TODO 添加类/接口功能描述.
 * @author chenhao
 * @date 2017年6月18日 下午2:10:19
 *
 */
public class ActYwRgnode extends ActYwRstatus implements ActYwRtpl{
  private List<ActYwGnode> gnodes;

  public ActYwRgnode() {
    super();
  }

  public ActYwRgnode(List<ActYwGnode> gnodes) {
    super();
    this.gnodes = gnodes;
  }

  public List<ActYwGnode> getGnodes() {
    return gnodes;
  }

  public void setGnodes(List<ActYwGnode> gnodes) {
    this.gnodes = gnodes;
  }
}
