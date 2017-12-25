package com.oseasy.initiate.modules.promodel.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.promodel.entity.ProModel;
import com.oseasy.initiate.modules.promodel.dao.ProModelDao;

/**
 * proModelService.
 * @author zy
 * @version 2017-07-13
 */
@Service
@Transactional(readOnly = true)
public class ProModelService extends CrudService<ProModelDao, ProModel> {

	public ProModel get(String id) {
		return super.get(id);
	}

	public List<ProModel> findList(ProModel proModel) {
		return super.findList(proModel);
	}

	public Page<ProModel> findPage(Page<ProModel> page, ProModel proModel) {
		return super.findPage(page, proModel);
	}

	@Transactional(readOnly = false)
	public void save(ProModel proModel) {
		super.save(proModel);
	}

	@Transactional(readOnly = false)
	public void delete(ProModel proModel) {
		super.delete(proModel);
	}

}