/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd.vo
 * @Description [[_ActYwPgnode_]]文件
 * @date 2017年6月18日 下午2:16:14
 *
 */

package com.oseasy.initiate.modules.actyw.tool.process.cmd.vo;

import java.util.List;

import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwPtpl;

/**
 * 业务流程参数-业务节点.
 * @author chenhao
 * @date 2017年6月18日 下午2:16:14
 *
 */
public class ActYwPgnode implements ActYwPtpl{
  private ActYwGnode pgnode;
  private ActYwGnode gnode;
  private List<ActYwGnode> preGnodes;
  private List<ActYwGnode> nextGnodes;

  public ActYwPgnode() {
    super();
  }

  public ActYwPgnode(ActYwGnode pgnode, ActYwGnode gnode, List<ActYwGnode> preGnodes,
      List<ActYwGnode> nextGnodes) {
    super();
    this.pgnode = pgnode;
    this.gnode = gnode;
    this.preGnodes = preGnodes;
    this.nextGnodes = nextGnodes;
  }

  public ActYwGnode getPgnode() {
    return pgnode;
  }

  public void setPgnode(ActYwGnode pgnode) {
    this.pgnode = pgnode;
  }

  public ActYwGnode getGnode() {
    return gnode;
  }

  public void setGnode(ActYwGnode gnode) {
    this.gnode = gnode;
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

