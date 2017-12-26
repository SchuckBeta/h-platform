package com.hch.platform.pcore.modules.pw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.pw.entity.PwBillRule;
import com.hch.platform.pcore.modules.pw.dao.PwBillRuleDao;

/**
 * 费用规则Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwBillRuleService extends CrudService<PwBillRuleDao, PwBillRule> {

	public PwBillRule get(String id) {
		return super.get(id);
	}

	public List<PwBillRule> findList(PwBillRule pwBillRule) {
		return super.findList(pwBillRule);
	}

	public Page<PwBillRule> findPage(Page<PwBillRule> page, PwBillRule pwBillRule) {
		return super.findPage(page, pwBillRule);
	}

	@Transactional(readOnly = false)
	public void save(PwBillRule pwBillRule) {
		super.save(pwBillRule);
	}

	@Transactional(readOnly = false)
	public void delete(PwBillRule pwBillRule) {
		super.delete(pwBillRule);
	}

}