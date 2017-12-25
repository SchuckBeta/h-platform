package com.oseasy.initiate.modules.interactive.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.interactive.entity.SysLikes;

/**
 * 点赞表DAO接口.
 * @author 9527
 * @version 2017-06-30
 */
@MyBatisDao
public interface SysLikesDao extends CrudDao<SysLikes> {
	public int getExistsLike(SysLikes sl);
	public List<Map<String,Object>> getAllExistsLike(Map<String,Object> param);
}