/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd.vo
 * @Description [[_ActYwPgrootSub_]]文件
 * @date 2017年6月18日 下午2:37:48
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd.vo;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwPtpl;

/**
 * 业务流程参数-业务根节点.
 * @author chenhao
 * @date 2017年6月18日 下午2:37:48
 *
 */
public class ActYwPgrootSub implements ActYwPtpl{
  private ActYwGroup group;
  private List<ActYwGnode> preGnodes;
  private List<ActYwGnode> nextGnodes;

  public ActYwPgrootSub() {
    super();
  }

  public ActYwPgrootSub(ActYwGroup group, List<ActYwGnode> preGnodes, List<ActYwGnode> nextGnodes) {
    super();
    this.group = group;
    this.preGnodes = preGnodes;
    this.nextGnodes = nextGnodes;
  }

  public ActYwGroup getGroup() {
    return group;
  }

  public List<ActYwGnode> getPreGnodes() {
    return preGnodes;
  }

  public void setPreGnodes(List<ActYwGnode> preGnodes) {
    this.preGnodes = preGnodes;
  }

  public List<ActYwGnode> getNextGnodes() {
    return nextGnodes;
  }

  public void setNextGnodes(List<ActYwGnode> nextGnodes) {
    this.nextGnodes = nextGnodes;
  }
}