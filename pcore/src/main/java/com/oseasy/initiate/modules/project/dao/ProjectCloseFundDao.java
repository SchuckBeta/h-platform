package com.oseasy.initiate.modules.project.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.project.entity.ProjectCloseFund;

import java.util.List;

/**
 * project_close_fundDAO接口
 * @author zhangzheng
 * @version 2017-03-29
 */
@MyBatisDao
public interface ProjectCloseFundDao extends CrudDao<ProjectCloseFund> {

    public void deleteByCloseId(String closeId);

    public List<ProjectCloseFund> getByCloseId(String closeId);
	
}