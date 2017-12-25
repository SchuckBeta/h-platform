package com.oseasy.initiate.modules.impdata.dao;

import java.util.List;
import java.util.Map;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.impdata.entity.ImpInfoErrmsg;

/**
 * 导入数据错误信息表DAO接口
 * @author 9527
 * @version 2017-05-16
 */
@MyBatisDao
public interface ImpInfoErrmsgDao extends CrudDao<ImpInfoErrmsg> {
	public List<Map<String,String>> getListByImpId(String impid);
	public void deleteByImpId(String impid);
}