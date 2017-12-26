package com.hch.platform.pcore.modules.pw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.pw.entity.PwCosMaterial;
import com.hch.platform.pcore.modules.pw.dao.PwCosMaterialDao;

/**
 * 耗材Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwCosMaterialService extends CrudService<PwCosMaterialDao, PwCosMaterial> {

	public PwCosMaterial get(String id) {
		return super.get(id);
	}

	public List<PwCosMaterial> findList(PwCosMaterial pwCosMaterial) {
		return super.findList(pwCosMaterial);
	}

	public Page<PwCosMaterial> findPage(Page<PwCosMaterial> page, PwCosMaterial pwCosMaterial) {
		return super.findPage(page, pwCosMaterial);
	}

	@Transactional(readOnly = false)
	public void save(PwCosMaterial pwCosMaterial) {
		super.save(pwCosMaterial);
	}

	@Transactional(readOnly = false)
	public void delete(PwCosMaterial pwCosMaterial) {
		super.delete(pwCosMaterial);
	}

}