package com.hch.platform.pcore.modules.sys.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.GContestUndergo;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.sys.vo.UserVo;

/**
 * Created by zhangzheng on 2017/2/23.
 */
@Service
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private UserDao userDao;
    public List<UserVo> getTeaInfo(String[] idsArr){
    	return userDao.getTeaInfo(idsArr);
    }
    public List<UserVo> getStudentInfo(String[] idsArr){
    	return userDao.getStudentInfo(idsArr);
    }
    public Page<UserVo> findPageByVo(Page<UserVo> page, UserVo userVo) {
    	userVo.setPage(page);
		page.setList(userDao.findListByVo(userVo));
		return page;
	}
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

    //根据学生id找到对应学院的教学秘书
    public List<String> getCollegeSecs(String userid) {
        List<AbsUser> users= userDao.getCollegeSecs(userid);
        List<String> list=new ArrayList<String>();
        for (AbsUser user:users) {
            list.add(user.getLoginName());
        }
        return list;
    }

    //根据学生id找到院级专家
    public  List<String> getCollegeExperts(String userid) {
        List<AbsUser> users= userDao.getCollegeExperts(userid);
        List<String> list=new ArrayList<String>();
        for (AbsUser user:users) {
            list.add(user.getLoginName());
        }
        return list;
    }

    //找到院级专家
    public List<AbsUser> getCollegeExpertUsers(String userid) {
        List<AbsUser> users= userDao.getCollegeExperts(userid);
        return users;
    }

    //根据学生id找到对应学院的教学秘书
    public AbsUser getCollegeSecUsers(String userid) {
        List<AbsUser> users= userDao.getCollegeSecs(userid);
        if (users.size()>0) {
        	return users.get(0);
        }
        return null;
    }

    //找到学校管理员
    public AbsUser getSchoolSecUsers() {
        List<AbsUser> users= userDao.getSchoolSecs();
        if (users.size()>0) {
        	return users.get(0);
        }
        return null;
    }

    //找到院级专家
    public List<AbsUser> getSchoolExpertUsers() {
        List<AbsUser> users= userDao.getSchoolExperts();
        return users;
    }


    //找到学校管理员
    public  List<String> getSchoolSecs() {
        List<AbsUser> users= userDao.getSchoolSecs();
        List<String> list=new ArrayList<String>();
        for (AbsUser user:users) {
            list.add(user.getLoginName());
        }
        return list;
    }

    public AbsUser getSchoolSecUser() {
        List<AbsUser> users= userDao.getSchoolSecs();
        if (users.size()>0) {
            return users.get(0);
        }
        return null;
    }

    //找到校级专家
    public  List<String> getSchoolExperts() {
        List<AbsUser> users= userDao.getSchoolExperts();
        List<String> list=new ArrayList<String>();
        for (AbsUser user:users) {
            list.add(user.getLoginName());
        }
        return list;
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
    public void updateMobile(AbsUser user) {
        userDao.updateMobile(user);
        UserUtils.clearCache(user);
    }

    @Transactional(readOnly = false)
    public int saveUser(AbsUser user) {
		return userDao.insert(user);

    }
    @Transactional(readOnly = false)
    public void updateUser(AbsUser user) {
    	userDao.update(user);
    	UserUtils.clearCache(user);
    }

    @Transactional(readOnly = false)
    public void updateUserPhoto(AbsUser user) {
        userDao.updateUserPhoto(user);
        UserUtils.clearCache(user);
    }

    @Transactional(readOnly = false)
    public void delete(AbsUser user) {
        userDao.delete(user);
        UserUtils.clearCache(user);
    }

	public AbsUser findUserByLoginName(String loginName) {
		return userDao.findUserByLoginName(loginName);
	}

	public List<GContestUndergo> findContestByUserId(String userId) {
		return userDao.findContestByUserId(userId);
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
