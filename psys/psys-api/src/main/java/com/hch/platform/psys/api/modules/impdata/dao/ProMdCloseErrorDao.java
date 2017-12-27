package com.hch.platform.pcore.modules.impdata.dao;

import java.util.List;
import java.util.Map;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.impdata.entity.ProMdCloseError;

/**
 * 民大项目结项导入错误数据DAO接口.
 * @author 9527
 * @version 2017-10-20
 */
@MyBatisDao
public interface ProMdCloseErrorDao extends CrudDao<ProMdCloseError> {
	public List<Map<String,String>> getListByImpId(String impid);
	public void deleteByImpId(String impid);
}