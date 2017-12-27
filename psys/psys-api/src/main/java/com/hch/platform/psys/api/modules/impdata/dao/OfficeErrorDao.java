package com.hch.platform.pcore.modules.impdata.dao;

import java.util.List;
import java.util.Map;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.impdata.entity.OfficeError;

/**
 * 机构导入错误信息表DAO接口
 * @author 9527
 * @version 2017-05-24
 */
@MyBatisDao
public interface OfficeErrorDao extends CrudDao<OfficeError> {
	public List<Map<String,String>> getListByImpId(String impid);
	public void deleteByImpId(String impid);
}