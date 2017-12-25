package com.oseasy.initiate.modules.oa.dao;

import java.util.List;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.oa.entity.OaNotifyKeyword;

/**
 * 通知通告关键词DAO接口.
 * @author 9527
 * @version 2017-07-11
 */
@MyBatisDao
public interface OaNotifyKeywordDao extends CrudDao<OaNotifyKeyword> {
	public List<String> findListByEsid(String esid);
	public void delByEsid(String esid);
}