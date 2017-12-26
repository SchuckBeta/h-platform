package com.oseasy.initiate.modules.actyw.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.oseasy.initiate.common.persistence.TreeDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;

/**
 * 项目流程DAO接口.
 * @author chenhao
 * @version 2017-05-23
 */
@MyBatisDao
public interface ActYwGnodeDao extends TreeDao<ActYwGnode> {
	/**
	 * 找到所有子节点,流程设计专用.
	 * @param entity
	 * @return
	 */
	public List<ActYwGnode> findByPidsLike(ActYwGnode entity);


  /**
 	 * 根据流程ID获取开始节点.
   * @param groupId 流程ID
 	 * @return ActYwGnode
 	 */
 	public ActYwGnode getStart(@Param("groupId")String groupId);

 	/**
 	 * 根据流程ID获取和子流程ID获取子流程开始节点.
   * @param groupId
   * @param processId 子流程节点ID
   * @return ActYwGnode
   */
 	public ActYwGnode getProcessStart(@Param("groupId")String groupId, @Param("processId")String processId);

  /**
 	 * 根据流程ID获取结束节点.
   * @param groupId 流程ID
 	 * @return ActYwGnode
 	 */
 	public ActYwGnode getEnd(@Param("groupId")String groupId);

  /**
   * 根据流程ID获取和子流程ID获取子流程结束节点.
   * @param groupId
   * @param processId 子流程节点ID
   * @return ActYwGnode
   */
  public ActYwGnode getProcessEnd(@Param("groupId")String groupId, @Param("processId")String processId);

 	/**
   * 根据流程ID获取结束节点的前一业务节点.
   * @param groupId 流程ID
 	 * @return ActYwGnode
 	 */
 	public ActYwGnode getEndPreFun(@Param("groupId")String groupId);

 	/**
 	 * 根据流程ID获取结束节点的前一业务节点（子节点）.
 	 * @param groupId 流程ID
 	 * @param processId 子流程节点ID
 	 * @return ActYwGnode
 	 */
 	public ActYwGnode getProcessEndPreFun(@Param("groupId")String groupId, @Param("processId")String processId);

  /**
 	 * 找到所有子节点.
 	 * @param entity
 	 * @return
 	 */
 	public List<ActYwGnode> findByYwParentIdsLike(ActYwGnode entity);

	public ActYwGnode getWithGtime(@Param("id")String id,@Param("ppid")String ppid);

  public List<ActYwGnode> findListByinIds(@Param("ids") List<String> ids);
  public List<ActYwGnode> findListByinPids(@Param("ids") List<String> ids);
  public List<ActYwGnode> findByParentIdsLike(ActYwGnode gnode);
  public List<ActYwGnode> findNextByGpreIdsLike(ActYwGnode gnode);
  public List<ActYwGnode> findNextByGpreIdssLike(@Param("gnode") ActYwGnode gnode, @Param("cascade") Boolean cascade, @Param("ids") List<String> ids);
  public List<ActYwGnode> findPreByGnextIdsLike(ActYwGnode gnode);
  public List<ActYwGnode> findPreByGnextIdssLike(@Param("gnode") ActYwGnode gnode, @Param("cascade") Boolean cascade, @Param("ids") List<String> ids);

  public int updateGpreId(ActYwGnode gnode);
  public int updateGpreIds(ActYwGnode gnode);
  public int updateGpreIdss(ActYwGnode gnode);
  public int updateGpreFunId(ActYwGnode gnode);
  public int updateGnextId(ActYwGnode gnode);
  public int updateGnextIds(ActYwGnode gnode);
  public int updateGnextIdss(ActYwGnode gnode);
  public int updateGnextFunId(ActYwGnode gnode);

  /**
   * 批量新增.
   * @param gnodes
   * @return int
   */
  public boolean insertPL(@Param("gnodes") List<ActYwGnode> gnodes);

  /**
   * 批量更新ID属性.
   * @param gnodes
   * @return int
   */
  public boolean updatePropPL(@Param("gnodes") List<ActYwGnode> gnodes);

  /**
   * 物理删除.
   * @param gnode
   * @return
   */
  public boolean deleteWL(ActYwGnode gnode);

  /**
   * 批量更新 preIdss.
   * 不同记录更新同一个值.
   * @param ids 需要更新的ID
   * @param preIdss 更新的值
   * @return
   */
  public int updateGpreIdssPL(@Param("ids") List<String> ids, @Param("preIdss") String preIdss);
  /**
   * 批量更新 preIdss（方案一）.
   * 不同记录更新不同值.
   * @param gnodes 需要更新的ID
   * @return
   */
  public int updateGpreIdssByPL(@Param("gnodes") List<ActYwGnode> gnodes);
  /**
   * 批量更新 preIdss（方案二）.
   * 不同记录更新不同值.
   * @param gnodes 需要更新的ID
   * @return
   */
  public int updateGpreIdssByPL2(@Param("gnodes") List<ActYwGnode> gnodes);
  /**
   * 批量更新 nextIdss.
   * 不同记录更新同一个值.
   * @param ids 需要更新的ID
   * @param nextIdss 更新的值
   * @return
   */
  public int updateGnextIdssPL(@Param("ids") List<String> ids, @Param("nextIdss") String nextIdss);
  /**
   * 批量更新 nextIdss（方案一）.
   * 不同记录更新同一个值.
   * @param gnodes 需要更新的ID
   * @return
   */
  public int updateGnextIdssByPL(@Param("gnodes") List<ActYwGnode> gnodes);
  /**
   * 批量更新 nextIdss（方案二）.
   * 不同记录更新不同值.
   * @param gnodes 需要更新的ID
   * @return
   */
  public int updateGnextIdssByPL2(@Param("gnodes") List<ActYwGnode> gnodes);

  /**
   * 批量更新自定义流程节点排序 .
   * @author chenhao
   * @param gnode 实体.
   */
  public int updateSort(ActYwGnode gnode);

  /**
   * 查询所有节点列表.
   * @author chenhao
   * @param gnode 实体.
   */
  public List<ActYwGnode> findAll();

  /**
   * 根据自定义流程查询节点列表.
   * @author chenhao
   * @param gnode 实体.
   */
  public List<ActYwGnode> findListByGroup(ActYwGnode gnode);
  public List<ActYwGnode> findListByGroupAscPreIdss(ActYwGnode gnode);
  public List<ActYwGnode> findListByGroupAscNextIdss(ActYwGnode gnode);

  /**
   * 根据业务类型查询节点列表.
   * @author chenhao
   * @param gnode 实体.
   */
  public List<ActYwGnode> findListByYw(ActYwGnode gnode);
  public List<ActYwGnode> findListForTimeIndexByYw(ActYwGnode gnode);

  /**
   * 根据业务类型查询节点列表(包含网关).
   * @author chenhao
   * @param gnode 实体.
   */
  public List<ActYwGnode> findListByYwGate(ActYwGnode gnode);

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

  /**
   * 根据业务类型 19 查询子流程.
   * @author chenhao
   * @param actYwGnode 实体.
   */
  public void deletePLWL(@Param("ids")List<String> ids);

  /**
   * 根据流程ID清空流程.
   * @author chenhao
   * @param actYwGnode 实体.
   */
  public void deleteByGroup(@Param("groupId") String groupId);

  /**
   * 根据GroupId和Preidss或者NextIdss获取所有节点.
   * @param actYwGnode 实体.
   * @return List
   */
  public List<ActYwGnode> findPreByGroupAndIdss(ActYwGnode actYwGnode);

  /**
   * 根据GroupId和Preidss或者NextIdss获取所有节点.
   * @param actYwGnode 实体.
   * @return List
   */
  public List<ActYwGnode> findNextByGroupAndIdss(ActYwGnode actYwGnode);

 /**
  * 根据nodeId和ywId找到GnodeId.
  * @param nodeId 节点ID
  * @param ywId 流程ID
  * @return String
  */
  public String findGnodeIDByNode(@Param("nodeId") String nodeId,@Param("ywId") String ywId);

  /**
   * 查找兄弟节点.
   * 必填参数groupId和(preId或preFunId)
   * @param gnode 节点
   * @return List
   */
  public List<ActYwGnode> findSlibings(ActYwGnode gnode);
}