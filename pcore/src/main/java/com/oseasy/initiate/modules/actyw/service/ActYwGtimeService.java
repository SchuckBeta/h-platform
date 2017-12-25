package com.oseasy.initiate.modules.actyw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.actyw.entity.ActYwGtime;
import com.oseasy.initiate.modules.actyw.dao.ActYwGtimeDao;

/**
 * 时间和组件关联关系Service.
 * @author zy
 * @version 2017-06-27
 */
@Service
@Transactional(readOnly = true)
public class ActYwGtimeService extends CrudService<ActYwGtimeDao, ActYwGtime> {

	public ActYwGtime get(String id) {
		return super.get(id);
	}

	public List<ActYwGtime> findList(ActYwGtime actYwGtime) {
		return super.findList(actYwGtime);
	}

	public Page<ActYwGtime> findPage(Page<ActYwGtime> page, ActYwGtime actYwGtime) {
		return super.findPage(page, actYwGtime);
	}

	@Transactional(readOnly = false)
	public void save(ActYwGtime actYwGtime) {
		super.save(actYwGtime);
	}

	@Transactional(readOnly = false)
	public void delete(ActYwGtime actYwGtime) {
		super.delete(actYwGtime);
	}

}