package com.oseasy.initiate.modules.project.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.project.entity.ProMid;

/**
 * 国创项目中期检查表单DAO接口
 * @author 9527
 * @version 2017-03-29
 */
@MyBatisDao
public interface ProMidDao extends CrudDao<ProMid> {
	
	public ProMid getByProjectId(String pid);
}