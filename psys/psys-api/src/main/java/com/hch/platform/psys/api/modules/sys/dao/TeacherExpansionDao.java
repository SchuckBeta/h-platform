/**
 * 
 */
package com.hch.platform.pcore.modules.sys.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.TeacherExpansion;

import java.util.List;

/**
 * 用户DAO接口

 * @version 2014-05-16
 */
@MyBatisDao
public interface TeacherExpansionDao extends CrudDao<TeacherExpansion> {
	
	/**
	 * 根据倒是名称查询用户
	 * @return
	 */
	public TeacherExpansion getByLoginName(TeacherExpansion eacherExpansion);

	/**
	 * 查询全部导师数目
	 * @return
	 */
	public long findAllCount(TeacherExpansion teacherExpansion);


}
