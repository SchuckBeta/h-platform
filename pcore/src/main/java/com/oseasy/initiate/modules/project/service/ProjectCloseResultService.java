package com.oseasy.initiate.modules.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.project.entity.ProjectCloseResult;
import com.oseasy.initiate.modules.project.dao.ProjectCloseResultDao;

/**
 * project_close_resultService
 * @author zhangzheng
 * @version 2017-03-29
 */
@Service
@Transactional(readOnly = true)
public class ProjectCloseResultService extends CrudService<ProjectCloseResultDao, ProjectCloseResult> {

	public ProjectCloseResult get(String id) {
		return super.get(id);
	}
	
	public List<ProjectCloseResult> findList(ProjectCloseResult projectCloseResult) {
		return super.findList(projectCloseResult);
	}
	
	public Page<ProjectCloseResult> findPage(Page<ProjectCloseResult> page, ProjectCloseResult projectCloseResult) {
		return super.findPage(page, projectCloseResult);
	}
	
	@Transactional(readOnly = false)
	public void save(ProjectCloseResult projectCloseResult) {
		super.save(projectCloseResult);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProjectCloseResult projectCloseResult) {
		super.delete(projectCloseResult);
	}

	public List<ProjectCloseResult> getByCloseId(String closeId) {
		return dao.getByCloseId(closeId);
	}
	
}