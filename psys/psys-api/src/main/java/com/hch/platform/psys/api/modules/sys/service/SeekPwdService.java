package com.hch.platform.pcore.modules.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

@Service
public class SeekPwdService {
	@Autowired
	private UserDao userDao;
	public AbsUser findUserByPhone(String phoneMailNumber) {
		AbsUser user = new AbsUser();
		user.setMobile(phoneMailNumber);
		user.setDelFlag("0");
		return userDao.getByMobile(user);
	}
	public void resetPassWord(String password,String phone) {
		AbsUser user = new AbsUser();
		user.setMobile(phone);
	    password = SystemService.entryptPassword(password);
		user.setPassword(password);
		userDao.updateUserByPhone(user);
	}
}
