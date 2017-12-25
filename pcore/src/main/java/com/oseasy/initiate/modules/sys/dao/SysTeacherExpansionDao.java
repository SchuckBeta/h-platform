package com.oseasy.initiate.modules.sys.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.SysTeacherExpansion;

/**
 * 导师扩展信息表DAO接口
 * @author l
 * @version 2017-03-28
 */
@MyBatisDao
public interface SysTeacherExpansionDao extends CrudDao<SysTeacherExpansion> {
	
}