package com.hch.platform.pcore.modules.impdata.dao;

import java.util.List;
import java.util.Map;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.impdata.entity.StudentError;

/**
 * 导入学生错误数据表DAO接口
 * @author 9527
 * @version 2017-05-16
 */
@MyBatisDao
public interface StudentErrorDao extends CrudDao<StudentError> {
	public List<Map<String,String>> getListByImpId(String impid);
	public void deleteByImpId(String impid);
}