package com.hch.platform.pcore.modules.project.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.project.entity.ProjectPlan;

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