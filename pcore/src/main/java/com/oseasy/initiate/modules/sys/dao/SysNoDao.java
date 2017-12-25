package com.oseasy.initiate.modules.sys.dao;

import org.apache.ibatis.annotations.Param;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.SysNo;

/**
 * 系统编号DAO接口
 * @author chenh
 * @version 2017-05-05
 */
@MyBatisDao
public interface SysNoDao extends CrudDao<SysNo> {
	/**
	 * 获取全局序号最大值
	 * @return
	 */
	public SysNo getMaxNo();

	/**
	 * 获取机构序号最大值
	 * @param officeId
	 * @return
	 */
	public SysNo getMaxNoByOffice(@Param("officeId")String officeId);
}