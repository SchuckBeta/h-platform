package com.oseasy.initiate.modules.promodel.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.promodel.entity.ActYwAuditInfo;
import com.oseasy.initiate.modules.promodel.dao.ActYwAuditInfoDao;

/**
 * 自定义审核信息Service.
 * @author zy
 * @version 2017-11-01
 */
@Service
@Transactional(readOnly = true)
public class ActYwAuditInfoService extends CrudService<ActYwAuditInfoDao, ActYwAuditInfo> {

	public ActYwAuditInfo get(String id) {
		return super.get(id);
	}

	public List<ActYwAuditInfo> findList(ActYwAuditInfo actYwAuditInfo) {
		return super.findList(actYwAuditInfo);
	}

	public Page<ActYwAuditInfo> findPage(Page<ActYwAuditInfo> page, ActYwAuditInfo actYwAuditInfo) {
		return super.findPage(page, actYwAuditInfo);
	}

	@Transactional(readOnly = false)
	public void save(ActYwAuditInfo actYwAuditInfo) {
		super.save(actYwAuditInfo);
	}

	@Transactional(readOnly = false)
	public void delete(ActYwAuditInfo actYwAuditInfo) {
		super.delete(actYwAuditInfo);
	}

}