package com.oseasy.initiate.modules.actyw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.actyw.entity.ActYwConfig;
import com.oseasy.initiate.modules.actyw.dao.ActYwConfigDao;

/**
 * 业务配置项Service.
 * @author chenh
 * @version 2017-11-09
 */
@Service
@Transactional(readOnly = true)
public class ActYwConfigService extends CrudService<ActYwConfigDao, ActYwConfig> {

	public ActYwConfig get(String id) {
		return super.get(id);
	}

	public List<ActYwConfig> findList(ActYwConfig actYwConfig) {
		return super.findList(actYwConfig);
	}

	public Page<ActYwConfig> findPage(Page<ActYwConfig> page, ActYwConfig actYwConfig) {
		return super.findPage(page, actYwConfig);
	}

	@Transactional(readOnly = false)
	public void save(ActYwConfig actYwConfig) {
		super.save(actYwConfig);
	}

	@Transactional(readOnly = false)
	public void delete(ActYwConfig actYwConfig) {
		super.delete(actYwConfig);
	}

}