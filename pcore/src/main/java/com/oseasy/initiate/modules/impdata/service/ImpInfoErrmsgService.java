package com.hch.platform.pcore.modules.impdata.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.impdata.entity.ImpInfoErrmsg;
import com.hch.platform.pcore.modules.impdata.dao.ImpInfoErrmsgDao;

/**
 * 导入数据错误信息表Service
 * @author 9527
 * @version 2017-05-16
 */
@Service
@Transactional(readOnly = true)
public class ImpInfoErrmsgService extends CrudService<ImpInfoErrmsgDao, ImpInfoErrmsg> {
	public List<Map<String,String>> getListByImpId(String impid) {
		return dao.getListByImpId(impid);
	}
	public List<Map<String,String>> getListByImpIdAndSheet(String impid,String sheet) {
		return dao.getListByImpIdAndSheet(impid,sheet);
	}
	public ImpInfoErrmsg get(String id) {
		return super.get(id);
	}
	
	public List<ImpInfoErrmsg> findList(ImpInfoErrmsg impInfoErrmsg) {
		return super.findList(impInfoErrmsg);
	}
	
	public Page<ImpInfoErrmsg> findPage(Page<ImpInfoErrmsg> page, ImpInfoErrmsg impInfoErrmsg) {
		return super.findPage(page, impInfoErrmsg);
	}
	
	@Transactional(readOnly = false)
	public void save(ImpInfoErrmsg impInfoErrmsg) {
		super.save(impInfoErrmsg);
	}
	
	@Transactional(readOnly = false)
	public void delete(ImpInfoErrmsg impInfoErrmsg) {
		super.delete(impInfoErrmsg);
	}
	
}