package com.hch.platform.pcore.modules.impdata.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.config.SysIds;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.putil.common.utils.IdGen;
import com.hch.platform.pcore.modules.impdata.dao.TeacherErrorDao;
import com.hch.platform.pcore.modules.impdata.entity.TeacherError;
import com.hch.platform.pcore.modules.sys.dao.BackTeacherExpansionDao;
import com.hch.platform.pcore.modules.sys.dao.RoleDao;
import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.BackTeacherExpansion;
import com.hch.platform.pcore.modules.sys.entity.Role;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.SystemService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

/**
 * 导入导师错误数据表Service
 * @author 9527
 * @version 2017-05-22
 */
@Service
@Transactional(readOnly = true)
public class TeacherErrorService extends CrudService<TeacherErrorDao, TeacherError> {
	@Autowired
	private BackTeacherExpansionDao backTeacherExpansionDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private UserDao userDao;
	public List<Map<String,String>> getListByImpId(String impid) {
		return dao.getListByImpId(impid);
	}

	public TeacherError get(String id) {
		return super.get(id);
	}

	public List<TeacherError> findList(TeacherError teacherError) {
		return super.findList(teacherError);
	}

	public Page<TeacherError> findPage(Page<TeacherError> page, TeacherError teacherError) {
		return super.findPage(page, teacherError);
	}

	@Transactional(readOnly = false)
	public void save(TeacherError teacherError) {
		super.save(teacherError);
	}

	@Transactional(readOnly = false)
	public void delete(TeacherError teacherError) {
		super.delete(teacherError);
	}
	@Transactional(readOnly = false)
	public void insert(TeacherError teacherError) {
		AbsUser user = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getId())) {
			teacherError.setUpdateBy(user);
			teacherError.setCreateBy(user);
		}
		teacherError.setUpdateDate(new Date());
		teacherError.setCreateDate(teacherError.getUpdateDate());
		dao.insert(teacherError);
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	public void saveTeacher(BackTeacherExpansion tc) {
		AbsUser nuser=tc.getUser();
		AbsUser user = UserUtils.getUser();
		nuser.setPassword(SystemService.entryptPassword("123456"));
		List<Role> roleList=new ArrayList<Role>();
		roleList.add(roleDao.get(SysIds.SYS_ROLE_USER.getId()));
		nuser.setRoleList(roleList);
		nuser.setId(IdGen.uuid());
		nuser.setUserType("2");
		nuser.setSource("1");
		if (StringUtils.isNotBlank(user.getId())) {
			nuser.setUpdateBy(user);
			nuser.setCreateBy(user);
		}
		nuser.setUpdateDate(new Date());
		nuser.setCreateDate(nuser.getUpdateDate());
		userDao.insert(nuser);
		userDao.insertUserRole(nuser);
		tc.setId(IdGen.uuid());
		if (StringUtils.isNotBlank(user.getId())) {
			tc.setUpdateBy(user);
			tc.setCreateBy(user);
		}
		tc.setUpdateDate(new Date());
		tc.setCreateDate(tc.getUpdateDate());
		backTeacherExpansionDao.insert(tc);
	}
}