package com.oseasy.initiate.modules.gcontest.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.gcontest.entity.GContestAward;
import com.oseasy.initiate.modules.gcontest.dao.GContestAwardDao;

/**
 * 大赛获奖表Service
 * @author zy
 * @version 2017-03-11
 */
@Service
@Transactional(readOnly = true)
public class GContestAwardService extends CrudService<GContestAwardDao, GContestAward> {

	public GContestAward get(String id) {
		return super.get(id);
	}
	
	public List<GContestAward> findList(GContestAward gContestAward) {
		return super.findList(gContestAward);
	}
	
	public Page<GContestAward> findPage(Page<GContestAward> page, GContestAward gContestAward) {
		return super.findPage(page, gContestAward);
	}
	
	@Transactional(readOnly = false)
	public void save(GContestAward gContestAward) {
		super.save(gContestAward);
	}
	
	@Transactional(readOnly = false)
	public void delete(GContestAward gContestAward) {
		super.delete(gContestAward);
	}

	public GContestAward getByGid(String id) {
		return dao.getByGid(id);
	}
	
}