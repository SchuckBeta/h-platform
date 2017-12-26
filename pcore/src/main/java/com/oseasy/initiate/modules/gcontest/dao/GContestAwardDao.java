package com.hch.platform.pcore.modules.gcontest.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.gcontest.entity.GContestAward;

/**
 * 大赛获奖表DAO接口
 * @author zy
 * @version 2017-03-11
 */
@MyBatisDao
public interface GContestAwardDao extends CrudDao<GContestAward> {

	GContestAward getByGid(String id);
	
}