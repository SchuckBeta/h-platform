package com.oseasy.initiate.modules.actyw.dao;

import java.util.List;

import com.oseasy.initiate.common.persistence.TreeDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;

/**
 * 项目流程节点组DAO接口
 * @author chenhao
 * @version 2017-05-23
 */
@MyBatisDao
public interface ActYwGnodeDao extends TreeDao<ActYwGnode> {

  /**
   * 批量更新流程组节点排序 .
   * @author chenhao
   * @param gnode 实体.
   */
  public int updateSort(ActYwGnode gnode);

  /**
   * 根据流程组查询节点列表.
   * @author chenhao
   * @param gnode 实体.
   */
  public List<ActYwGnode> findListByGroup(ActYwGnode gnode);

  /**
   * 根据业务类型查询节点列表.
   * @author chenhao
   * @param gnode 实体.
   */
  public List<ActYwGnode> findListByYw(ActYwGnode gnode);

  /**
   * 根据节点查询上一节点或下一节点.
   *  查询所有前面节点时nextGnode 为空
   *  查询所有后面节点时preGnode 为空
   * @author chenhao
   * @param gnode 实体.
   */
  public List<ActYwGnode> findPreNextListByGroup(ActYwGnode gnode);

  /**
   * 根据业务类型 19 查询子流程.
   * @author chenhao
   * @param actYwGnode 实体.
   */
  List<ActYwGnode> findListByYwProcess(ActYwGnode actYwGnode);
}