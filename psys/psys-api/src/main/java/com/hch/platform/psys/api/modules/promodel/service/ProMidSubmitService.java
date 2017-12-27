package com.hch.platform.pcore.modules.promodel.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.promodel.entity.ProMidSubmit;
import com.hch.platform.pcore.modules.promodel.dao.ProMidSubmitDao;

/**
 * 中期提交信息表Service.
 * @author zy
 * @version 2017-12-01
 */
@Service
@Transactional(readOnly = true)
public class ProMidSubmitService extends CrudService<ProMidSubmitDao, ProMidSubmit> {

	public ProMidSubmit get(String id) {
		return super.get(id);
	}

	public List<ProMidSubmit> findList(ProMidSubmit proMidSubmit) {
		return super.findList(proMidSubmit);
	}

	public Page<ProMidSubmit> findPage(Page<ProMidSubmit> page, ProMidSubmit proMidSubmit) {
		return super.findPage(page, proMidSubmit);
	}

	@Transactional(readOnly = false)
	public void save(ProMidSubmit proMidSubmit) {
		super.save(proMidSubmit);
	}

	@Transactional(readOnly = false)
	public void delete(ProMidSubmit proMidSubmit) {
		super.delete(proMidSubmit);
	}

	public ProMidSubmit getByGnodeId(String proModelId, String gnodeId) {
		return dao.getByGnodeId(proModelId, gnodeId) ;
	}
}