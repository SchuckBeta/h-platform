package com.oseasy.initiate.modules.pw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.pw.entity.PwCompany;
import com.oseasy.initiate.modules.pw.dao.PwCompanyDao;

/**
 * 入驻企业Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwCompanyService extends CrudService<PwCompanyDao, PwCompany> {

	public PwCompany get(String id) {
		return super.get(id);
	}

	public List<PwCompany> findList(PwCompany pwCompany) {
		return super.findList(pwCompany);
	}

	public Page<PwCompany> findPage(Page<PwCompany> page, PwCompany pwCompany) {
		return super.findPage(page, pwCompany);
	}

	@Transactional(readOnly = false)
	public void save(PwCompany pwCompany) {
		super.save(pwCompany);
	}

	@Transactional(readOnly = false)
	public void delete(PwCompany pwCompany) {
		super.delete(pwCompany);
	}

}