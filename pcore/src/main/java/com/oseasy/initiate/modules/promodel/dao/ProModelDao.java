package com.oseasy.initiate.modules.promodel.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.promodel.entity.ProModel;

/**
 * proModelDAO接口.
 * @author zy
 * @version 2017-07-13
 */
@MyBatisDao
public interface ProModelDao extends CrudDao<ProModel> {
	public void modifyLeaderAndTeam(@Param("uid")String uid,@Param("tid")String tid,@Param("pid")String pid);
	public void myDelete(String  id);

	int findProModelByTeamId(String id);

	ProModel getByProModel(ProModel proModel);
	Page<ProModel> getPromodelList(ProModel proModel);
	public void updateResult(@Param("result") String result,@Param("pid") String promodelid);

	int getProModelAuditListCount(ProModel proModel);

	public ProModel getProScoreConfigure(String proId);

  /**
   * 根据leaderId获取我的项目.
   * @param leaderId 团队负责人
   * @return List
   */
  public List<ProModel> findListByLeader(@Param("uid") String uid);

  /**
   * 根据leaderId获取我的项目和大赛.
   * @param leaderId 团队负责人
   * @return List
   */
  public List<ProModel> findListAllByLeader(@Param("uid") String uid);

	void updateState(@Param("state") String state,@Param("id") String id);
}