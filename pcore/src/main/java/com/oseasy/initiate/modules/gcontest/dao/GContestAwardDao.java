package com.oseasy.initiate.modules.gcontest.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.gcontest.entity.GContestAward;

/**
 * 大赛获奖表DAO接口
 * @author zy
 * @version 2017-03-11
 */
@MyBatisDao
public interface GContestAwardDao extends CrudDao<GContestAward> {

	GContestAward getByGid(String id);
	
}