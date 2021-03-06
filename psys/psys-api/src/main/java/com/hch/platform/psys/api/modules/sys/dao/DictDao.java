/**
 * 
 */
package com.hch.platform.pcore.modules.sys.dao;

import java.util.List;
import java.util.Map;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.Dict;

/**
 * 字典DAO接口

 * @version 2014-05-16
 */
@MyBatisDao
public interface DictDao extends CrudDao<Dict> {
	public List<Map<String, String>> getDictListPlus(Map<String,Object> param);
	public int getDictListPlusCount(Map<String,Object> param);
	public List<Map<String, String>> getDictTypeListPlus();
	public List<String> findTypeList(Dict dict);
	public List<Dict> getAllData();
	public int getDictTypeCountByCdn(Map<String,String> param);
	public int getDictCountByCdn(Map<String,String> param);
	public void delDictType(String id);
}
