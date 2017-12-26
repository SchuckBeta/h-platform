package com.hch.platform.pcore.modules.pw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.pw.entity.PwCosMaterialPrecords;
import com.hch.platform.pcore.modules.pw.dao.PwCosMaterialPrecordsDao;

/**
 * 耗材购买记录Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwCosMaterialPrecordsService extends CrudService<PwCosMaterialPrecordsDao, PwCosMaterialPrecords> {

	public PwCosMaterialPrecords get(String id) {
		return super.get(id);
	}

	public List<PwCosMaterialPrecords> findList(PwCosMaterialPrecords pwCosMaterialPrecords) {
		return super.findList(pwCosMaterialPrecords);
	}

	public Page<PwCosMaterialPrecords> findPage(Page<PwCosMaterialPrecords> page, PwCosMaterialPrecords pwCosMaterialPrecords) {
		return super.findPage(page, pwCosMaterialPrecords);
	}

	@Transactional(readOnly = false)
	public void save(PwCosMaterialPrecords pwCosMaterialPrecords) {
		super.save(pwCosMaterialPrecords);
	}

	@Transactional(readOnly = false)
	public void delete(PwCosMaterialPrecords pwCosMaterialPrecords) {
		super.delete(pwCosMaterialPrecords);
	}

}