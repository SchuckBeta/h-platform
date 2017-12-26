package com.hch.platform.pcore.modules.auditstandard.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.auditstandard.entity.AuditStandardDetailIns;
import com.hch.platform.pcore.modules.auditstandard.dao.AuditStandardDetailInsDao;

/**
 * 评审标准详情记录Service.
 * @author 9527
 * @version 2017-07-28
 */
@Service
@Transactional(readOnly = true)
public class AuditStandardDetailInsService extends CrudService<AuditStandardDetailInsDao, AuditStandardDetailIns> {

	public AuditStandardDetailIns get(String id) {
		return super.get(id);
	}

	public List<AuditStandardDetailIns> findList(AuditStandardDetailIns auditStandardDetailIns) {
		return super.findList(auditStandardDetailIns);
	}

	public Page<AuditStandardDetailIns> findPage(Page<AuditStandardDetailIns> page, AuditStandardDetailIns auditStandardDetailIns) {
		return super.findPage(page, auditStandardDetailIns);
	}

	@Transactional(readOnly = false)
	public void save(AuditStandardDetailIns auditStandardDetailIns) {
		super.save(auditStandardDetailIns);
	}

	@Transactional(readOnly = false)
	public void delete(AuditStandardDetailIns auditStandardDetailIns) {
		super.delete(auditStandardDetailIns);
	}

}