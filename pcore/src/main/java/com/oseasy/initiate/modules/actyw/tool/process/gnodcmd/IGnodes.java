package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd;

import java.util.List;

/**
 *
 * 当前节点及关联节点集合.
 * @author chenhao
 *
 */
public interface IGnodes<T extends IGnode> {
  /**
   * 当前节点.
   * @return
   */
  public IGnode getCurrent();

  /**
   * 当前节点关联的变更节点集合(包含当前结点).
   * @return
   */
  public List<IGnode> getList();
}
