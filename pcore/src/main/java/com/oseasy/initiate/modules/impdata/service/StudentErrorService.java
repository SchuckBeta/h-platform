package com.oseasy.initiate.modules.impdata.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.modules.impdata.entity.StudentError;
import com.oseasy.initiate.modules.sys.dao.RoleDao;
import com.oseasy.initiate.modules.sys.dao.StudentExpansionDao;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.impdata.dao.StudentErrorDao;

/**
 * 导入学生错误数据表Service
 * @author 9527
 * @version 2017-05-16
 */
@Service
@Transactional(readOnly = true)
public class StudentErrorService extends CrudService<StudentErrorDao, StudentError> {
	@Autowired
	private StudentExpansionDao studentExpansionDao;
	@Autowired
	private RoleDao roleDao;
	@Autowired
	private UserDao userDao;
	public List<Map<String,String>> getListByImpId(String impid) {
		return dao.getListByImpId(impid);
	}
	public StudentError get(String id) {
		return super.get(id);
	}

	public List<StudentError> findList(StudentError studentError) {
		return super.findList(studentError);
	}

	public Page<StudentError> findPage(Page<StudentError> page, StudentError studentError) {
		return super.findPage(page, studentError);
	}

	@Transactional(readOnly = false)
	public void save(StudentError studentError) {
		super.save(studentError);
	}
	@Transactional(readOnly = false)
	public void insert(StudentError studentError) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getId())) {
			studentError.setUpdateBy(user);
			studentError.setCreateBy(user);
		}
		studentError.setUpdateDate(new Date());
		studentError.setCreateDate(studentError.getUpdateDate());
		dao.insert(studentError);
	}
	@Transactional(readOnly = false)
	public void delete(StudentError studentError) {
		super.delete(studentError);
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	public void saveStudent(StudentExpansion st) {
		User nuser=st.getUser();
		User user = UserUtils.getUser();
		nuser.setPassword(SystemService.entryptPassword("123456"));
		List<Role> roleList=new ArrayList<Role>();
		roleList.add(roleDao.get(SysIds.SYS_ROLE_USER.getId()));
		nuser.setRoleList(roleList);
		nuser.setId(IdGen.uuid());
		nuser.setUserType("1");
		nuser.setSource("1");
		nuser.setPassc("1");
		if (StringUtils.isNotBlank(user.getId())) {
			nuser.setUpdateBy(user);
			nuser.setCreateBy(user);
		}
		nuser.setUpdateDate(new Date());
		nuser.setCreateDate(nuser.getUpdateDate());
		userDao.insert(nuser);
		userDao.insertUserRole(nuser);
		st.setId(IdGen.uuid());
		if (StringUtils.isNotBlank(user.getId())) {
			st.setUpdateBy(user);
			st.setCreateBy(user);
		}
		st.setUpdateDate(new Date());
		st.setCreateDate(st.getUpdateDate());
		studentExpansionDao.insert(st);
	}
}