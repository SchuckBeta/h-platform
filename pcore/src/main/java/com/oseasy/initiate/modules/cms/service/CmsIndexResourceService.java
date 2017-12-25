package com.oseasy.initiate.modules.cms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.cms.entity.CmsIndexResource;
import com.oseasy.initiate.modules.cms.dao.CmsIndexResourceDao;

/**
 * 首页资源Service
 * @author daichanggeng
 * @version 2017-04-07
 */
@Service
@Transactional(readOnly = true)
public class CmsIndexResourceService extends CrudService<CmsIndexResourceDao, CmsIndexResource> {

	public CmsIndexResource get(String id) {
		return super.get(id);
	}
	
	public List<CmsIndexResource> findList(CmsIndexResource cmsIndexResource) {
		return super.findList(cmsIndexResource);
	}
	
	public Page<CmsIndexResource> findPage(Page<CmsIndexResource> page, CmsIndexResource cmsIndexResource) {
		return super.findPage(page, cmsIndexResource);
	}
	
	@Transactional(readOnly = false)
	public void save(CmsIndexResource cmsIndexResource) {
		super.save(cmsIndexResource);
	}
	
	@Transactional(readOnly = false)
	public void delete(CmsIndexResource cmsIndexResource) {
		super.delete(cmsIndexResource);
	}
	
}