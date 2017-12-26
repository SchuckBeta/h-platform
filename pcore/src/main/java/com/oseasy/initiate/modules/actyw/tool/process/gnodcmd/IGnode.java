package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd;

import java.util.List;

import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeType;

/**
 * 节点实体.
 * @author chenhao
 *
 */
public interface IGnode<T> {
  /**
   * 获取节点类型.
   * @return GnodeType
   */
  public GnodeType getGnodeType();

  /**
   * 前置节点初始化.
   * @return T
   */
  public ActYwRstatus<T> initPreGnode(T gnode);

  /**
   * 后置节点初始化.
   * @return T
   */
  public ActYwRstatus<T> initNextGnode(T gnode);

  /**
   * 前置业务节点初始化.
   * @return T
   */
  public ActYwRstatus<T> initPreFunGnode(T gnode);

  /**
   * 后置业务节点初始化.
   * @return T
   */
  public ActYwRstatus<T> initNextFunGnode(T gnode);

  /**
   * 当前节点初始化.
   * @return T
   */
  public ActYwRstatus<T> initCurrentGnode(T gnode);

  /**
   * 父节点初始化.
   * @return T
   */
  public ActYwRstatus<T> initParentGnode(T gnode);

  /**
   * 所有前置节点.
   * @return T
   */
  public ActYwRstatus<T> initPreGnodes(T gnode);

  /**
   * 所有后置节点.
   * @return T
   */
  public ActYwRstatus<T> initNextGnodes(T gnode);

  /**
   * 所有前置节点.
   * @return T
   */
  public List<ActYwRstatus<T>> initPreFunGnodes(T gnode);

  /**
   * 所有后置节点.
   * @return T
   */
  public List<ActYwRstatus<T>> initNextFunGnodes(T gnode);

  /**
   * 所有子节点.
   * @return T
   */
  public List<ActYwRstatus<T>> initChildGnodes(T gnode);
}
