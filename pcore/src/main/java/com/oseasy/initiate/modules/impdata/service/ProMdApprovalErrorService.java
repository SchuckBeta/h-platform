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
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.impdata.dao.ProMdApprovalErrorDao;
import com.oseasy.initiate.modules.impdata.entity.ProMdApprovalError;
import com.oseasy.initiate.modules.promodel.entity.ProModel;
import com.oseasy.initiate.modules.proprojectmd.dao.ProModelMdDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 民大项目立项审核导入错误数据表Service.
 * @author 9527
 * @version 2017-09-22
 */
@Service
@Transactional(readOnly = true)
public class ProMdApprovalErrorService extends CrudService<ProMdApprovalErrorDao, ProMdApprovalError> {
	@Autowired
	private ProModelMdDao proModelMdDao;
	@Autowired
	private ProModelDao proModelDao;
	@Autowired
	private ActTaskService actTaskService;
	
	public List<Map<String,String>> getListByImpId(String impid,String sheet) {
		return dao.getListByImpIdAndSheet(impid,sheet);
	}
	//校验项目编号是否存在，true 存在
	public boolean checkMdProNumber(String pnum,String pid,ProModel pm){
		if(StringUtil.isEmpty(pnum)||StringUtil.isEmpty(pid)||pm==null){
			return false;
		}else{
			if(proModelMdDao.checkMdProNumber(pnum, pid,pm.getType())>0){
				return true;
			}else {
				return false;
			}
		}
	}
	@Transactional(readOnly = false)
	public void saveProMdApproval(String pnumber,String result,ProModel pm) {
		if("0".equals(result)){//未通过
			proModelDao.updateState("1",pm.getId());
			actTaskService.suspendProcess(pm.getProcInsId());
		}
		if("1".equals(result)){//通过
			actTaskService.runNextProcess(pm);
		}
		proModelMdDao.updatePnum(pnumber, pm.getId());
		proModelMdDao.updateApprovalResult(result, pm.getId());
	}
	@Transactional(readOnly = false)
	public void insert(ProMdApprovalError studentError) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getId())) {
			studentError.setUpdateBy(user);
			studentError.setCreateBy(user);
		}
		studentError.setUpdateDate(new Date());
		studentError.setCreateDate(studentError.getUpdateDate());
		dao.insert(studentError);
	}
	public ProMdApprovalError get(String id) {
		return super.get(id);
	}

	public List<ProMdApprovalError> findList(ProMdApprovalError proMdApprovalError) {
		return super.findList(proMdApprovalError);
	}

	public Page<ProMdApprovalError> findPage(Page<ProMdApprovalError> page, ProMdApprovalError proMdApprovalError) {
		return super.findPage(page, proMdApprovalError);
	}

	@Transactional(readOnly = false)
	public void save(ProMdApprovalError proMdApprovalError) {
		super.save(proMdApprovalError);
	}

	@Transactional(readOnly = false)
	public void delete(ProMdApprovalError proMdApprovalError) {
		super.delete(proMdApprovalError);
	}

}