package com.oseasy.initiate.modules.project.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.project.entity.ProjectPlan;

import java.util.List;

/**
 * 项目任务DAO接口
 * @author 9527
 * @version 2017-03-11
 */
@MyBatisDao
public interface ProjectPlanDao extends CrudDao<ProjectPlan> {
    public List<ProjectPlan> findListByProjectId(String projectId);
    public void deleteByProjectId(String projectId);
}