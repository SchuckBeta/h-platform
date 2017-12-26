/**
 * 
 */
package com.hch.platform.pcore.modules.sys.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.service.TreeService;
import com.hch.platform.pcore.modules.sys.dao.OfficeDao;
import com.hch.platform.pcore.modules.sys.entity.Office;
import com.hch.platform.pcore.modules.sys.utils.OfficeUtils;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

/**
 * 机构Service

 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class OfficeService extends TreeService<OfficeDao, Office> {
	@Autowired 
	OfficeDao officeDao;
	public List<Office> findAll() {
		return UserUtils.getOfficeList();
	}

	public List<Office> findList(Boolean isAll) {
		if (isAll != null && isAll) {
			return UserUtils.getOfficeAllList();
		}else{
			return UserUtils.getOfficeList();
		}
	}
	
	public List<Office> findListFront(Boolean isAll) {
		return UserUtils.getOfficeListFront();
	}
	
	@Transactional(readOnly = true)
	public List<Office> findList(Office office) {
		if (office != null) {
			office.setParentIds(office.getParentIds()+"%");
			return dao.findByParentIdsLike(office);
		}
		return  new ArrayList<Office>();
	}
	
	@Transactional(readOnly = false)
	public void save(Office office) {
		super.save(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
		UserUtils.removeCache("officeListFront");
		OfficeUtils.clearCache();
	}
	
	@Transactional(readOnly = false)
	public void delete(Office office) {
		super.delete(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
		UserUtils.removeCache("officeListFront");
		OfficeUtils.clearCache();
	}

	@Transactional(readOnly = false)
	public void update(Office office) {
		officeDao.update(office);
		UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
		UserUtils.removeCache("officeListFront");
		OfficeUtils.clearCache();
	}

	public String selelctParentId(String id) {
		return officeDao.selelctParentId(id);
	}

	public List<Office> findColleges() {
		return officeDao.findColleges();
	}

	public List<Office> findProfessionals(String parentId) {
		return officeDao.findProfessionals(parentId);
	}

	public List<Office>  findProfessionByParentIdsLike(Office office) {
		return officeDao.findProfessionByParentIdsLike(office);
	}

	public  List<Office> findProfessionByParentIds(String officeIds) {
		return officeDao.findProfessionByParentIds(officeIds);
	}
	
}
