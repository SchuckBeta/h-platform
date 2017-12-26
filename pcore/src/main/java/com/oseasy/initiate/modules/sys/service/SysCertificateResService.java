package com.hch.platform.pcore.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.sys.entity.SysCertificateRes;
import com.hch.platform.pcore.modules.sys.dao.SysCertificateResDao;

/**
 * 系统证书资源Service.
 * @author chenh
 * @version 2017-11-06
 */
@Service
@Transactional(readOnly = true)
public class SysCertificateResService extends CrudService<SysCertificateResDao, SysCertificateRes> {

	public SysCertificateRes get(String id) {
		return super.get(id);
	}

	public List<SysCertificateRes> findList(SysCertificateRes sysCertificateRes) {
		return super.findList(sysCertificateRes);
	}

	public Page<SysCertificateRes> findPage(Page<SysCertificateRes> page, SysCertificateRes sysCertificateRes) {
		return super.findPage(page, sysCertificateRes);
	}

	@Transactional(readOnly = false)
	public void save(SysCertificateRes sysCertificateRes) {
		super.save(sysCertificateRes);
	}

	@Transactional(readOnly = false)
	public void delete(SysCertificateRes sysCertificateRes) {
		super.delete(sysCertificateRes);
	}

}