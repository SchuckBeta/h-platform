package com.hch.platform.pcore.modules.impdata.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.putil.common.utils.IdGen;
import com.hch.platform.pcore.modules.impdata.dao.BackUserErrorDao;
import com.hch.platform.pcore.modules.impdata.entity.BackUserError;
import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.SystemService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

/**
 * 后台用户导入Service
 * @author 9527
 * @version 2017-05-23
 */
@Service
@Transactional(readOnly = true)
public class BackUserErrorService extends CrudService<BackUserErrorDao, BackUserError> {
	@Autowired
	private UserDao userDao;
	public BackUserError get(String id) {
		return super.get(id);
	}
	
	public List<BackUserError> findList(BackUserError backUserError) {
		return super.findList(backUserError);
	}
	
	public Page<BackUserError> findPage(Page<BackUserError> page, BackUserError backUserError) {
		return super.findPage(page, backUserError);
	}
	
	@Transactional(readOnly = false)
	public void save(BackUserError backUserError) {
		super.save(backUserError);
	}
	
	@Transactional(readOnly = false)
	public void delete(BackUserError backUserError) {
		super.delete(backUserError);
	}
	public List<Map<String,String>> getListByImpId(String impid) {
		return dao.getListByImpId(impid);
	}
	@Transactional(readOnly = false)
	public void insert(BackUserError backUserError) {
		AbsUser user = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getId())) {
			backUserError.setUpdateBy(user);
			backUserError.setCreateBy(user);
		}
		backUserError.setUpdateDate(new Date());
		backUserError.setCreateDate(backUserError.getUpdateDate());
		dao.insert(backUserError);
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	public void saveBackUser(AbsUser nuser) {
		AbsUser user = UserUtils.getUser();
		nuser.setPassword(SystemService.entryptPassword("123456"));
		nuser.setId(IdGen.uuid());
		if (StringUtils.isNotBlank(user.getId())) {
			nuser.setUpdateBy(user);
			nuser.setCreateBy(user);
		}
		nuser.setSource("1");
		nuser.setUpdateDate(new Date());
		nuser.setCreateDate(nuser.getUpdateDate());
		userDao.insert(nuser);
		userDao.insertUserRole(nuser);
	}
}