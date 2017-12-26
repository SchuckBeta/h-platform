package com.hch.platform.pcore.modules.impdata.dao;

import java.util.List;
import java.util.Map;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.impdata.entity.TeacherError;

/**
 * 导入导师错误数据表DAO接口
 * @author 9527
 * @version 2017-05-22
 */
@MyBatisDao
public interface TeacherErrorDao extends CrudDao<TeacherError> {
	public List<Map<String,String>> getListByImpId(String impid);
	public void deleteByImpId(String impid);
}