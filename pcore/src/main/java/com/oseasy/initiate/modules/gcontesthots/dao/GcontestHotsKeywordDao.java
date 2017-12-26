package com.hch.platform.pcore.modules.gcontesthots.dao;

import java.util.List;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.gcontesthots.entity.GcontestHotsKeyword;

/**
 * 大赛热点关键字DAO接口.
 * @author 9527
 * @version 2017-07-12
 */
@MyBatisDao
public interface GcontestHotsKeywordDao extends CrudDao<GcontestHotsKeyword> {
	public List<String> findListByEsid(String esid);
	public void delByEsid(String esid);
}