package com.oseasy.initiate.modules.actyw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.actyw.entity.ActYwGcert;
import com.oseasy.initiate.modules.actyw.dao.ActYwGcertDao;

/**
 * 业务节点证书Service.
 * @author chenh
 * @version 2017-11-09
 */
@Service
@Transactional(readOnly = true)
public class ActYwGcertService extends CrudService<ActYwGcertDao, ActYwGcert> {

	public ActYwGcert get(String id) {
		return super.get(id);
	}

	public List<ActYwGcert> findList(ActYwGcert actYwGcert) {
		return super.findList(actYwGcert);
	}

	public Page<ActYwGcert> findPage(Page<ActYwGcert> page, ActYwGcert actYwGcert) {
		return super.findPage(page, actYwGcert);
	}

	@Transactional(readOnly = false)
	public void save(ActYwGcert actYwGcert) {
		super.save(actYwGcert);
	}

	@Transactional(readOnly = false)
	public void delete(ActYwGcert actYwGcert) {
		super.delete(actYwGcert);
	}

}