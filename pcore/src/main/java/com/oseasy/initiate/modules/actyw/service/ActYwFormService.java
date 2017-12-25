package com.oseasy.initiate.modules.actyw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.dao.ActYwFormDao;

/**
 * 项目流程表单Service.
 * @author chenhao
 * @version 2017-05-23
 */
@Service
@Transactional(readOnly = true)
public class ActYwFormService extends CrudService<ActYwFormDao, ActYwForm> {

	public ActYwForm get(String id) {
		return super.get(id);
	}

	public List<ActYwForm> findList(ActYwForm actYwForm) {
		return super.findList(actYwForm);
	}

	public Page<ActYwForm> findPage(Page<ActYwForm> page, ActYwForm actYwForm) {
		return super.findPage(page, actYwForm);
	}

	@Transactional(readOnly = false)
	public void save(ActYwForm actYwForm) {
		super.save(actYwForm);
	}

	@Transactional(readOnly = false)
	public void delete(ActYwForm actYwForm) {
		super.delete(actYwForm);
	}

}