package com.hch.platform.pcore.modules.pw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.pw.entity.PwEnterRel;
import com.hch.platform.pcore.modules.pw.dao.PwEnterRelDao;

/**
 * 入驻申报关联Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwEnterRelService extends CrudService<PwEnterRelDao, PwEnterRel> {

	public PwEnterRel get(String id) {
		return super.get(id);
	}

	public List<PwEnterRel> findList(PwEnterRel pwEnterRel) {
		return super.findList(pwEnterRel);
	}

	public Page<PwEnterRel> findPage(Page<PwEnterRel> page, PwEnterRel pwEnterRel) {
		return super.findPage(page, pwEnterRel);
	}

	@Transactional(readOnly = false)
	public void save(PwEnterRel pwEnterRel) {
		super.save(pwEnterRel);
	}

	@Transactional(readOnly = false)
	public void delete(PwEnterRel pwEnterRel) {
		super.delete(pwEnterRel);
	}

}