package com.oseasy.initiate.modules.impdata.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.impdata.entity.ImpInfo;

/**
 * 导入数据信息表DAO接口
 * @author 9527
 * @version 2017-05-16
 */
@MyBatisDao
public interface ImpInfoDao extends CrudDao<ImpInfo> {
	public List<Map<String,String>> getList(Map<String,Object> param);
	public int getListCount(Map<String,Object> param);
}