package com.hch.platform.pcore.modules.impdata.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.act.service.ActTaskService;
import com.hch.platform.pcore.modules.impdata.dao.ProMdCloseErrorDao;
import com.hch.platform.pcore.modules.impdata.entity.ProMdCloseError;
import com.hch.platform.pcore.modules.promodel.dao.ProModelDao;
import com.hch.platform.pcore.modules.promodel.entity.ProModel;
import com.hch.platform.pcore.modules.proprojectmd.dao.ProModelMdDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

/**
 * 民大项目结项导入错误数据Service.
 * @author 9527
 * @version 2017-10-20
 */
@Service
@Transactional(readOnly = true)
public class ProMdCloseErrorService extends CrudService<ProMdCloseErrorDao, ProMdCloseError> {
	@Autowired
	private ProModelDao proModelDao;
	@Autowired
	private ProModelMdDao proModelMdDao;
	@Autowired
	private ActTaskService actTaskService;
	public List<Map<String, String>> getListByImpId(String impid){
		return dao.getListByImpId(impid);
	}
	@Transactional(readOnly = false)
	public void saveProMdClose(String exc,String result,ProModel pm) {
		if("0".equals(result)){//未通过
			actTaskService.suspendProcess(pm.getProcInsId());
			proModelDao.updateState("1",pm.getId());
			proModelDao.updateResult("2", pm.getId());
		}
		if("1".equals(result)){//通过
			actTaskService.runNextProcess(pm);
			if(StringUtil.isNotEmpty(exc)){
				proModelDao.updateResult(exc, pm.getId());
			}else{
				proModelDao.updateResult("0", pm.getId());
			}
		}
		proModelMdDao.updateCloseResult(result, pm.getId());
	}
	@Transactional(readOnly = false)
	public void insert(ProMdCloseError studentError) {
		AbsUser user = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getId())) {
			studentError.setUpdateBy(user);
			studentError.setCreateBy(user);
		}
		studentError.setUpdateDate(new Date());
		studentError.setCreateDate(studentError.getUpdateDate());
		dao.insert(studentError);
	}
	public ProMdCloseError get(String id) {
		return super.get(id);
	}

	public List<ProMdCloseError> findList(ProMdCloseError proMdCloseError) {
		return super.findList(proMdCloseError);
	}

	public Page<ProMdCloseError> findPage(Page<ProMdCloseError> page, ProMdCloseError proMdCloseError) {
		return super.findPage(page, proMdCloseError);
	}

	@Transactional(readOnly = false)
	public void save(ProMdCloseError proMdCloseError) {
		super.save(proMdCloseError);
	}

	@Transactional(readOnly = false)
	public void delete(ProMdCloseError proMdCloseError) {
		super.delete(proMdCloseError);
	}

}