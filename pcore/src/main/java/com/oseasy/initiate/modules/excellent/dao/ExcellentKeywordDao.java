package com.oseasy.initiate.modules.excellent.dao;

import java.util.List;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.excellent.entity.ExcellentKeyword;

/**
 * 优秀展示关键词DAO接口.
 * @author 9527
 * @version 2017-06-23
 */
@MyBatisDao
public interface ExcellentKeywordDao extends CrudDao<ExcellentKeyword> {
	public List<String> findListByEsid(String esid);
	public void delByEsid(String esid);

}