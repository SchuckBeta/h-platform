package com.oseasy.initiate.modules.sys.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.entity.gContestUndergo;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * Created by zhangzheng on 2017/2/23.
 */
@Service
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private UserDao userDao;
    public List<User> getTeaByCdn(String no,String name) {
        return userDao.getTeaByCdn( no, name);
    }
    public List<User> getStuByCdn(String no,String name) {
        return userDao.getStuByCdn( no, name);
    }
    public List<User> findListByRoleName(String ename) {
        return userDao.findListByRoleName(ename);
    }

    public List<User> findListByUserId(String userid) {
        return userDao.findListByRoleName(userid);
    }

    public List<User> findByType(User user) {
        return userDao.findByType(user);
    }

    public User findUserById(String id) {
           return userDao.get(id);
       }

    public List<String> getRolesByName(String roleName) {
        List<User> users=findListByRoleName(roleName);
        List<String> roles=new ArrayList<String>();
        for (User user:users) {
            roles.add(user.getLoginName());
        }
        return roles;
    }

    //根据学生id找到对应学院的教学秘书
    public List<String> getCollegeSecs(String userid) {
        List<User> users= userDao.getCollegeSecs(userid);
        List<String> list=new ArrayList<String>();
        for (User user:users) {
            list.add(user.getLoginName());
        }
        return list;
    }

    //根据学生id找到院级专家
    public  List<String> getCollegeExperts(String userid) {
        List<User> users= userDao.getCollegeExperts(userid);
        List<String> list=new ArrayList<String>();
        for (User user:users) {
            list.add(user.getLoginName());
        }
        return list;
    }
    
    //找到院级专家
    public List<User> getCollegeExpertUsers(String userid) {
        List<User> users= userDao.getCollegeExperts(userid);
        return users;
    }

    //根据学生id找到对应学院的教学秘书
    public User getCollegeSecUsers(String userid) {
        List<User> users= userDao.getCollegeSecs(userid);
        if (users.size()>0) {
        	return users.get(0);
        }
        return null;
    }
    
    //找到学校管理员
    public User getSchoolSecUsers() {
        List<User> users= userDao.getSchoolSecs();
        if (users.size()>0) {
        	return users.get(0);
        }
        return null;
    }

    //找到院级专家
    public List<User> getSchoolExpertUsers() {
        List<User> users= userDao.getSchoolExperts();
        return users;
    }

    
    //找到学校管理员
    public  List<String> getSchoolSecs() {
        List<User> users= userDao.getSchoolSecs();
        List<String> list=new ArrayList<String>();
        for (User user:users) {
            list.add(user.getLoginName());
        }
        return list;
    }

    public User getSchoolSecUser() {
        List<User> users= userDao.getSchoolSecs();
        if (users.size()>0) {
            return users.get(0);
        }
        return null;
    }

    //找到校级专家
    public  List<String> getSchoolExperts() {
        List<User> users= userDao.getSchoolExperts();
        List<String> list=new ArrayList<String>();
        for (User user:users) {
            list.add(user.getLoginName());
        }
        return list;
    }

    //根据手机号查找用户
    public User getByMobile(User User) {
		return userDao.getByMobile(User);
    	
    }

    @Transactional(readOnly = false)
    public void updateMobile(User user) {
        userDao.updateMobile(user);
    }

    @Transactional(readOnly = false)
    public int saveUser(User user) {
		return userDao.insert(user);
    	
    }
    @Transactional(readOnly = false)
    public void updateUser(User user) {
    	userDao.update(user);
    	UserUtils.clearCache(user);
    }

    @Transactional(readOnly = false)
    public void updateUserPhoto(User user) {
        userDao.updateUserPhoto(user);
        UserUtils.clearCache(user);
    }

    @Transactional(readOnly = false)
    public void delete(User user) {
        userDao.delete(user);
        UserUtils.clearCache(user);
    }

	public User findUserByLoginName(String loginName) {
		return userDao.findUserByLoginName(loginName);
	}
	
	public List<gContestUndergo> findContestByUserId(String userId) {
		return userDao.findContestByUserId(userId);
	}    
	public User getUserByName(String name) {
		return userDao.getUserByName(name);
	}

    public User getByLoginNameOrNo(String loginNameOrNo) {
        return userDao.getByLoginNameOrNo(loginNameOrNo);
    }
}
