package com.hch.platform.pcore.modules.sys.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

/**
 * Created by zhangzheng on 2017/2/23.
 */
@Service
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private UserDao userDao;

    public List<AbsUser> getTeaByCdn(String no,String name) {
        return userDao.getTeaByCdn( no, name);
    }
    public AbsUser getByNo(String no) {
        return userDao.getByNo(no);
    }
    public List<AbsUser> getStuByCdn(String no,String name) {
        return userDao.getStuByCdn( no, name);
    }
    public List<AbsUser> findListByRoleName(String ename) {
        return userDao.findListByRoleName(ename);
    }

    public List<AbsUser> findListByRoleName(String enname,String userId) {
        return userDao.findListByRoleNameAndOffice(enname,userId);
    }

    public List<AbsUser> findListByUserId(String userid) {
        return userDao.findListByRoleName(userid);
    }

    public List<AbsUser> findByType(AbsUser user) {
        return userDao.findByType(user);
    }

    public AbsUser findUserById(String id) {
           return userDao.get(id);
       }

    public List<String> getRolesByName(String roleName) {
        List<AbsUser> users=findListByRoleName(roleName);
        List<String> roles=new ArrayList<String>();
        for (AbsUser user:users) {
            roles.add(user.getLoginName());
        }
        return roles;
    }

    public List<String> getRolesByName(String roleName,String userId) {
        List<AbsUser> users=findListByRoleName(roleName,userId);
        List<String> roles=new ArrayList<String>();
        for (AbsUser user:users) {
            roles.add(user.getLoginName());
        }
        return roles;
    }

    //根据手机号查找用户
    public AbsUser getByMobile(AbsUser User) {
		return userDao.getByMobile(User);

    }
    //根据手机号查找用户排除自己
    public AbsUser getByMobileWithId(AbsUser User) {
		return userDao.getByMobileWithId(User);

    }

    @Transactional(readOnly = false)
    public int saveUser(AbsUser user) {
		return userDao.insert(user);
    }

	public AbsUser findUserByLoginName(String loginName) {
		return userDao.findUserByLoginName(loginName);
	}

	public AbsUser getUserByName(String name) {
		return userDao.getUserByName(name);
	}

    public AbsUser getByLoginNameOrNo(String loginNameOrNo,String id) {
        return userDao.getByLoginNameOrNo(loginNameOrNo,id);
    }

    public List<String> findListByRoleId(String roleId) {
        return userDao.findListByRoleId(roleId);
    }
    public List<String> findUserByRepair() {
      return userDao.findUserByRepair();
    }
}
