package com.hch.platform.pcore.modules.interactive.dao;

import java.util.List;
import java.util.Map;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.interactive.entity.SysViews;

/**
 * 浏览表DAO接口.
 * @author 9527
 * @version 2017-06-30
 */
@MyBatisDao
public interface SysViewsDao extends CrudDao<SysViews> {
	public List<Map<String,String>> getBrowse(String uid);
	public List<Map<String,String>> getVisitors(String uid);
	public void insertBatch(List<SysViews> list);
}