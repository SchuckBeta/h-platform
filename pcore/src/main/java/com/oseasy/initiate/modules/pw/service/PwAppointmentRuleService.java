package com.hch.platform.pcore.modules.pw.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.pw.entity.PwAppointmentRule;
import com.hch.platform.pcore.modules.pw.dao.PwAppointmentRuleDao;

/**
 * 预约规则Service.
 * @author chenh
 * @version 2017-11-26
 */
@Service
@Transactional(readOnly = true)
public class PwAppointmentRuleService extends CrudService<PwAppointmentRuleDao, PwAppointmentRule> {

	public PwAppointmentRule get(String id) {
		return super.get(id);
	}

	public List<PwAppointmentRule> findList(PwAppointmentRule pwAppointmentRule) {
		return super.findList(pwAppointmentRule);
	}

	public Page<PwAppointmentRule> findPage(Page<PwAppointmentRule> page, PwAppointmentRule pwAppointmentRule) {
		return super.findPage(page, pwAppointmentRule);
	}

	@Transactional(readOnly = false)
	public void save(PwAppointmentRule pwAppointmentRule) {
		super.save(pwAppointmentRule);
	}

	@Transactional(readOnly = false)
	public void delete(PwAppointmentRule pwAppointmentRule) {
		super.delete(pwAppointmentRule);
	}

	public PwAppointmentRule getPwAppointmentRule() {
		return dao.getPwAppointmentRule();
	}
}