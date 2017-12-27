package com.hch.platform.pcore.modules.impdata.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.pcore.modules.impdata.dao.BackUserErrorDao;
import com.hch.platform.pcore.modules.impdata.dao.GcontestErrorDao;
import com.hch.platform.pcore.modules.impdata.dao.ImpInfoDao;
import com.hch.platform.pcore.modules.impdata.dao.ImpInfoErrmsgDao;
import com.hch.platform.pcore.modules.impdata.dao.OfficeErrorDao;
import com.hch.platform.pcore.modules.impdata.dao.ProMdApprovalErrorDao;
import com.hch.platform.pcore.modules.impdata.dao.ProMdCloseErrorDao;
import com.hch.platform.pcore.modules.impdata.dao.ProMdMidErrorDao;
import com.hch.platform.pcore.modules.impdata.dao.ProjectErrorDao;
import com.hch.platform.pcore.modules.impdata.dao.ProjectHsErrorDao;
import com.hch.platform.pcore.modules.impdata.dao.StudentErrorDao;
import com.hch.platform.pcore.modules.impdata.dao.TeacherErrorDao;
import com.hch.platform.pcore.modules.impdata.entity.ImpInfo;

/**
 * 导入数据信息表Service
 *
 * @author 9527
 * @version 2017-05-16
 */
@Service
public class ImpInfoService extends CrudService<ImpInfoDao, ImpInfo> {
	@Autowired
	private ImpInfoErrmsgDao impInfoErrmsgDao;
	@Autowired
	private StudentErrorDao studentErrorDao;
	@Autowired
	private BackUserErrorDao backUserErrorDao;
	@Autowired
	private OfficeErrorDao officeErrorDao;
	@Autowired
	private TeacherErrorDao teacherErrorDao;
	@Autowired
	private ProjectErrorDao projectErrorDao;
	@Autowired
	private ProjectHsErrorDao projectHsErrorDao;
	@Autowired
	private ProMdCloseErrorDao proMdCloseErrorDao;
	@Autowired
	private ProMdApprovalErrorDao proMdApprovalErrorDao;
	@Autowired
	private ProMdMidErrorDao proMdMidErrorDao;
	@Autowired
	private GcontestErrorDao gcontestErrorDao;
	public static Logger logger = Logger.getLogger(ImpInfoService.class);
	public Page<Map<String, String>> getMdList(Page<Map<String, String>> page, Map<String, Object> param) {
		if (page.getPageNo() <= 0) {
			page.setPageNo(1);
		}
		int count = dao.getMdListCount(param);
		param.put("offset", (page.getPageNo() - 1) * page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String, String>> list = null;
		if (count > 0) {
			list = dao.getMdList(param);
		}
		page.setCount(count);
		page.setList(list);
		page.initialize();
		return page;
	}
	public Page<Map<String, String>> getList(Page<Map<String, String>> page, Map<String, Object> param) {
		if (page.getPageNo() <= 0) {
			page.setPageNo(1);
		}
		int count = dao.getListCount(param);
		param.put("offset", (page.getPageNo() - 1) * page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String, String>> list = null;
		if (count > 0) {
			list = dao.getList(param);
		}
		page.setCount(count);
		page.setList(list);
		page.initialize();
		return page;
	}
	public ImpInfo getImpInfo(String id) {
		ImpInfo ii=(ImpInfo)CacheUtils.get(CacheUtils.IMPDATA_CACHE, id);
		if (ii==null) {
			ii=super.get(id);
		}
		return ii;
	}
	public ImpInfo get(String id) {
		return super.get(id);
	}

	public List<ImpInfo> findList(ImpInfo impInfo) {
		return super.findList(impInfo);
	}

	public Page<ImpInfo> findPage(Page<ImpInfo> page, ImpInfo impInfo) {
		return super.findPage(page, impInfo);
	}
	@Transactional(readOnly = false)
//	@Transactional(propagation=Propagation.REQUIRED)
	public void save(ImpInfo impInfo) {
		super.save(impInfo);
	}

	@Transactional(readOnly = false)
	public void delete(ImpInfo impInfo) {
		String type=impInfo.getImpTpye();
		super.delete(impInfo);
		if (ImpDataService.impStu.equals(type)) {
			studentErrorDao.deleteByImpId(impInfo.getId());
		}else if (ImpDataService.impTea.equals(type)) {
			teacherErrorDao.deleteByImpId(impInfo.getId());
		}else if (ImpDataService.impBackUser.equals(type)) {
			backUserErrorDao.deleteByImpId(impInfo.getId());
		}else if (ImpDataService.impOrg.equals(type)) {
			officeErrorDao.deleteByImpId(impInfo.getId());
		}else if (ImpDataService.impProject.equals(type)) {
			projectErrorDao.deleteByImpId(impInfo.getId());
		}else if (ImpDataService.impMdAppr.equals(type)) {
			proMdApprovalErrorDao.deleteByImpId(impInfo.getId());
		}else if (ImpDataService.impMdMid.equals(type)) {
			proMdMidErrorDao.deleteByImpId(impInfo.getId());
		}else if (ImpDataService.impMdClose.equals(type)) {
			proMdCloseErrorDao.deleteByImpId(impInfo.getId());
		}else if (ImpDataService.impProjectHs.equals(type)) {
			projectHsErrorDao.deleteByImpId(impInfo.getId());
		}else if (ImpDataService.impGcontest.equals(type)) {
			gcontestErrorDao.deleteByImpId(impInfo.getId());
		}
		impInfoErrmsgDao.deleteByImpId(impInfo.getId());
	}

}