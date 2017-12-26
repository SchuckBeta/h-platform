package com.hch.platform.pcore.modules.project.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.project.entity.ProjectClose;

/**
 * project_closeDAO接口
 * @author zhangzheng
 * @version 2017-03-29
 */
@MyBatisDao
public interface ProjectCloseDao extends CrudDao<ProjectClose> {
	public ProjectClose getByProjectId(String pid);
}