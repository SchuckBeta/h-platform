package com.hch.platform.pcore.modules.impdata.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.impdata.entity.ImpInfoErrmsg;

/**
 * 导入数据错误信息表DAO接口
 * @author 9527
 * @version 2017-05-16
 */
@MyBatisDao
public interface ImpInfoErrmsgDao extends CrudDao<ImpInfoErrmsg> {
	public List<Map<String,String>> getListByImpIdAndSheet(@Param("impid")String impid,@Param("sheet")String sheet);
	public List<Map<String,String>> getListByImpId(String impid);
	public void deleteByImpId(String impid);
}