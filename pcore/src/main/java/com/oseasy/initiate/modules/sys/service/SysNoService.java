package com.oseasy.initiate.modules.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.sys.dao.SysNoDao;
import com.oseasy.initiate.modules.sys.entity.SysNo;
import com.oseasy.initiate.modules.sys.enums.SysNoType;

/**
 * 系统编号Service
 * @author chenh
 * @version 2017-05-05
 */
@Service
@Transactional(readOnly = true)
public class SysNoService extends CrudService<SysNoDao, SysNo> {
	@Autowired
	private SysNoDao sysNoDao;

	public SysNo get(String id) {
		return super.get(id);
	}

	public List<SysNo> findList(SysNo sysNo) {
		return super.findList(sysNo);
	}

	public Page<SysNo> findPage(Page<SysNo> page, SysNo sysNo) {
		return super.findPage(page, sysNo);
	}

	@Transactional(readOnly = false)
	public void save(SysNo sysNo) {
		super.save(sysNo);
	}

	@Transactional(readOnly = false)
	public void delete(SysNo sysNo) {
		super.delete(sysNo);
	}
	/******************************************************************************
	 * 获取全局序号最大值
	 * @param officeId
	 * @return
	 */
	public synchronized SysNo getMaxNo() {
		return getMaxNo(false, null);
	}

	/**
	 * 获取机构序号最大值
	 * @param officeId
	 * @return
	 */
	public synchronized SysNo getMaxNo(String officeId) {
		return getMaxNo(officeId, false, null);
	}


	/**
	 * 获取全局序号最大值
	 * @param isAddOne 是否加1
	 * @param snType 加1的编号类型
	 * @return
	 */
	public synchronized SysNo getMaxNo(Boolean isAddOne, SysNoType snType) {
		SysNo entity = null;
		if ((isAddOne) && (snType != null)) {
			entity = sysNoDao.getMaxNo();

			if (entity == null) {
				try {
					throw new Exception("系统编号表却是id为0的记录！");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (SysNoType.NO_OFFICE.equals(snType)) {
				entity.setOfficeNo(entity.getOfficeNo()+1);
			}else if (SysNoType.NO_ORDER.equals(snType)) {
				entity.setOrderNo(entity.getOrderNo()+1);
			}else if (SysNoType.NO_SITE.equals(snType)) {
				entity.setSiteNo(entity.getSiteNo()+1);
			}
			sysNoDao.update(entity);
		}else{
			entity = sysNoDao.getMaxNo();

			if (entity == null) {
				try {
					throw new Exception("系统编号表却是id为0的记录！");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return entity;
	}

	/**
	 * 获取机构序号最大值
	 * @param officeId 机构ID
	 * @param isAddOne 是否加1
	 * @param snType 加1的编号类型
	 * @return
	 */
	public synchronized SysNo getMaxNo(String officeId, Boolean isAddOne, SysNoType snType) {
		SysNo entity = null;
		if (StringUtil.isEmpty(officeId)) {
			return entity;
		}

		if ((isAddOne) && (snType != null)) {
			entity = sysNoDao.getMaxNoByOffice(officeId);
			if (SysNoType.NO_OFFICE.equals(snType)) {
				entity.setOfficeNo(entity.getOfficeNo()+1);
			}else if (SysNoType.NO_ORDER.equals(snType)) {
				entity.setOrderNo(entity.getOrderNo()+1);
			}else if (SysNoType.NO_SITE.equals(snType)) {
				entity.setSiteNo(entity.getSiteNo()+1);
			}
			sysNoDao.update(entity);
		}else{
			entity = sysNoDao.getMaxNoByOffice(officeId);
		}
		return entity;
	}
}