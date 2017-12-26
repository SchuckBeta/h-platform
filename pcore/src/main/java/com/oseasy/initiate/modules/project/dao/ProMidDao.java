package com.hch.platform.pcore.modules.project.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.project.entity.ProMid;

/**
 * 国创项目中期检查表单DAO接口
 * @author 9527
 * @version 2017-03-29
 */
@MyBatisDao
public interface ProMidDao extends CrudDao<ProMid> {
	
	public ProMid getByProjectId(String pid);
}