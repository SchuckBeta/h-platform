package com.oseasy.initiate.modules.project.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.project.entity.ProjectCloseResult;

import java.util.List;

/**
 * project_close_resultDAO接口
 * @author zhangzheng
 * @version 2017-03-29
 */
@MyBatisDao
public interface ProjectCloseResultDao extends CrudDao<ProjectCloseResult> {
    public void deleteByCloseId(String closeId);

    public List<ProjectCloseResult> getByCloseId(String closeId);
}