package com.hch.platform.pcore.modules.project.dao;

import java.util.List;
import java.util.Map;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.project.entity.ProjectAnnounce;

/**
 * 项目通告DAO接口
 * @author zdk
 * @version 2017-03-30
 */
@MyBatisDao
public interface ProjectAnnounceDao extends CrudDao<ProjectAnnounce> {
	public List<Map<String,String>> findCurInfo(Map<String,String> map);
	public ProjectAnnounce getProjectByName(String name);
	public int getProjectByNameId(Map<String,String> param);
}