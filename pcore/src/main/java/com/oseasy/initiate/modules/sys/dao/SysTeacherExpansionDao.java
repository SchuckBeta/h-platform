package com.hch.platform.pcore.modules.sys.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.SysTeacherExpansion;

/**
 * 导师扩展信息表DAO接口
 * @author l
 * @version 2017-03-28
 */
@MyBatisDao
public interface SysTeacherExpansionDao extends CrudDao<SysTeacherExpansion> {
	
}