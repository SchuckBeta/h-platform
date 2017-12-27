package com.hch.platform.pcore.modules.interactive.dao;

import java.util.List;
import java.util.Map;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.interactive.entity.SysLikes;

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