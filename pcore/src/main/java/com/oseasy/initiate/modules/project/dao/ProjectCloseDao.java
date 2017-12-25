package com.oseasy.initiate.modules.project.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.project.entity.ProjectClose;

/**
 * project_closeDAO接口
 * @author zhangzheng
 * @version 2017-03-29
 */
@MyBatisDao
public interface ProjectCloseDao extends CrudDao<ProjectClose> {
	public ProjectClose getByProjectId(String pid);
}