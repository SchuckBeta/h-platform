package com.oseasy.initiate.modules.actyw.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.DateUtil;
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
	@Autowired
	ActYwGtimeDao actYwGtimeDao;
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
		try {
			actYwGtime.setBeginDate(DateUtil.getStartDate(actYwGtime.getBeginDate()));
			actYwGtime.setEndDate(DateUtil.getEndDate(actYwGtime.getEndDate()));
		} catch (ParseException e) {
			logger.error(e.getMessage());
		}
		super.save(actYwGtime);
	}

	@Transactional(readOnly = false)
	public void delete(ActYwGtime actYwGtime) {
		super.delete(actYwGtime);
	}

	@Transactional(readOnly = false)
	public void deleteByGroupId(ActYwGtime actYwGtime) {
		actYwGtimeDao.deleteByGroupId(actYwGtime);
		}


	public ActYwGtime getTimeByGnodeId(ActYwGtime actYwGtime) {
		return actYwGtimeDao.getTimeByGnodeId(actYwGtime);
	}
}