package com.oseasy.initiate.modules.impdata.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.oseasy.initiate.modules.promodel.dao.ProModelDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.impdata.dao.ProMdMidErrorDao;
import com.oseasy.initiate.modules.impdata.entity.ProMdMidError;
import com.oseasy.initiate.modules.promodel.entity.ProModel;
import com.oseasy.initiate.modules.proprojectmd.dao.ProModelMdDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 民大项目中期检查导入错误数据Service.
 * @author 9527
 * @version 2017-10-18
 */
@Service
@Transactional(readOnly = true)
public class ProMdMidErrorService extends CrudService<ProMdMidErrorDao, ProMdMidError> {
	@Autowired
	private ProModelMdDao proModelMdDao;
	@Autowired
	private ProModelDao proModelDao;
	@Autowired
	private ActTaskService actTaskService;
	public List<Map<String,String>> getListByImpId(String impid) {
		return dao.getListByImpId(impid);
	}
	@Transactional(readOnly = false)
	public void saveProMdMid(String result,ProModel pm) {
		if("0".equals(result)){//未通过
			proModelDao.updateState("1",pm.getId());
			actTaskService.suspendProcess(pm.getProcInsId());
		}
		if("1".equals(result)){//通过
			actTaskService.runNextProcess(pm);
		}
		proModelMdDao.updateMidResult(result, pm.getId());
	}
	@Transactional(readOnly = false)
	public void insert(ProMdMidError studentError) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getId())) {
			studentError.setUpdateBy(user);
			studentError.setCreateBy(user);
		}
		studentError.setUpdateDate(new Date());
		studentError.setCreateDate(studentError.getUpdateDate());
		dao.insert(studentError);
	}
	public ProMdMidError get(String id) {
		return super.get(id);
	}

	public List<ProMdMidError> findList(ProMdMidError proMdMidError) {
		return super.findList(proMdMidError);
	}

	public Page<ProMdMidError> findPage(Page<ProMdMidError> page, ProMdMidError proMdMidError) {
		return super.findPage(page, proMdMidError);
	}

	@Transactional(readOnly = false)
	public void save(ProMdMidError proMdMidError) {
		super.save(proMdMidError);
	}

	@Transactional(readOnly = false)
	public void delete(ProMdMidError proMdMidError) {
		super.delete(proMdMidError);
	}

}