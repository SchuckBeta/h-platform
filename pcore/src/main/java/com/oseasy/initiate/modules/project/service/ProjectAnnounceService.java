package com.oseasy.initiate.modules.project.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.project.entity.ProjectAnnounce;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.project.dao.ProjectAnnounceDao;

/**
 * 项目通告Service
 * @author zdk
 * @version 2017-03-30
 */
@Service
@Transactional(readOnly = true)
public class ProjectAnnounceService extends CrudService<ProjectAnnounceDao, ProjectAnnounce> {

	public ProjectAnnounce get(String id) {
		return super.get(id);
	}
	
	public List<ProjectAnnounce> findList(ProjectAnnounce projectAnnounce) {
		return super.findList(projectAnnounce);
	}
	
	public Page<ProjectAnnounce> findPage(Page<ProjectAnnounce> page, ProjectAnnounce projectAnnounce) {
		return super.findPage(page, projectAnnounce);
	}
	
	@Transactional(readOnly = false)
	public void save(ProjectAnnounce projectAnnounce) {
		super.save(projectAnnounce);
		UserUtils.removeCache(UserUtils.CACHE_PROJECT_ANNOUNCE);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProjectAnnounce projectAnnounce) {
		super.delete(projectAnnounce);
		UserUtils.removeCache(UserUtils.CACHE_PROJECT_ANNOUNCE);
	}
	
	public List<Map<String,String>> findCurInfo(Map<String,String> map) {
		return dao.findCurInfo(map);
	}
	
	public Page<ProjectAnnounce> findProjectPage(Page<ProjectAnnounce> page, ProjectAnnounce projectAnnouce) {
		page = UserUtils.getProjectAnnouceList(page, projectAnnouce);
		return page;
		
	}
	public ProjectAnnounce getProjectByName(String name) {
		return dao.getProjectByName(name);

	}
	public int getProjectByName(Map<String,String> param) {
			return dao.getProjectByNameId(param);
	}
}