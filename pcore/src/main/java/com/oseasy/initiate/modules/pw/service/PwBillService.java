package com.oseasy.initiate.modules.pw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.pw.entity.PwBill;
import com.oseasy.initiate.modules.pw.dao.PwBillDao;

/**
 * 账单Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwBillService extends CrudService<PwBillDao, PwBill> {

	public PwBill get(String id) {
		return super.get(id);
	}

	public List<PwBill> findList(PwBill pwBill) {
		return super.findList(pwBill);
	}

	public Page<PwBill> findPage(Page<PwBill> page, PwBill pwBill) {
		return super.findPage(page, pwBill);
	}

	@Transactional(readOnly = false)
	public void save(PwBill pwBill) {
		super.save(pwBill);
	}

	@Transactional(readOnly = false)
	public void delete(PwBill pwBill) {
		super.delete(pwBill);
	}

}