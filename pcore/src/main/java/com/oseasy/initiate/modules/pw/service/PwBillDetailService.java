package com.oseasy.initiate.modules.pw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.pw.entity.PwBillDetail;
import com.oseasy.initiate.modules.pw.dao.PwBillDetailDao;

/**
 * 账单明细Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwBillDetailService extends CrudService<PwBillDetailDao, PwBillDetail> {

	public PwBillDetail get(String id) {
		return super.get(id);
	}

	public List<PwBillDetail> findList(PwBillDetail pwBillDetail) {
		return super.findList(pwBillDetail);
	}

	public Page<PwBillDetail> findPage(Page<PwBillDetail> page, PwBillDetail pwBillDetail) {
		return super.findPage(page, pwBillDetail);
	}

	@Transactional(readOnly = false)
	public void save(PwBillDetail pwBillDetail) {
		super.save(pwBillDetail);
	}

	@Transactional(readOnly = false)
	public void delete(PwBillDetail pwBillDetail) {
		super.delete(pwBillDetail);
	}

}