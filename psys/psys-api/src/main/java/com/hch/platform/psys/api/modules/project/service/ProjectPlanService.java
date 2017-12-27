package com.hch.platform.pcore.modules.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.project.entity.ProjectPlan;
import com.hch.platform.pcore.modules.project.dao.ProjectPlanDao;

/**
 * 项目任务Service
 * @author 9527
 * @version 2017-03-11
 */
@Service
@Transactional(readOnly = true)
public class ProjectPlanService extends CrudService<ProjectPlanDao, ProjectPlan> {

	public ProjectPlan get(String id) {
		return super.get(id);
	}

	public List<ProjectPlan> findListByProjectId(String projectId) {
		if (projectId==null) {
			return null;
		}else {
			return dao.findListByProjectId(projectId);
		}
	}
	public List<ProjectPlan> findList(ProjectPlan projectPlan) {
		return super.findList(projectPlan);
	}
	
	public Page<ProjectPlan> findPage(Page<ProjectPlan> page, ProjectPlan projectPlan) {
		return super.findPage(page, projectPlan);
	}
	
	@Transactional(readOnly = false)
	public void save(ProjectPlan projectPlan) {
		super.save(projectPlan);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProjectPlan projectPlan) {
		super.delete(projectPlan);
	}
	
}