/**
 * 
 */
package com.oseasy.initiate.modules.sys.dao;

import java.util.List;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.entity.gContestUndergo;
import org.apache.ibatis.annotations.Param;

/**
 * 用户DAO接口

 * @version 2014-05-16
 */
@MyBatisDao
public interface UserDao extends CrudDao<User> {
	public User getByNo(User user);
	public User getByMobile(User user);
	public User getByMobileWithId(User user);
	public void updateMobile(User user);

	public  String getTeacherTypeByUserId(@Param("userId") String userId);
	/**
	 * 根据登录名称查询用户
	 * @param loginName
	 * @return
	 */
	public User getByLoginName(User user);

	/**
	 * 根据登录名或者学号查询用户
	 * @param loginNameOrNo 录名或者学号
	 * @return User
     */
	public User getByLoginNameOrNo(String  loginNameOrNo);

	/**
	 * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<User> findUserByOfficeId(User user);

	/**
	 * 通过professionalId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<User> findUserByProfessionId(User user);

	/**
	 * 查询全部用户数目
	 * @return
	 */
	public long findAllCount(User user);
	
	/**
	 * 更新用户密码
	 * @param user
	 * @return
	 */
	public int updatePasswordById(User user);

	/**
	 * 更新用户照片
	 * @param user
	 * @return
	 */
	public int updateUserPhoto(User user);
	/**
	 * 更新登录信息，如：登录IP、登录时间
	 * @param user
	 * @return
	 */
	public int updateLoginInfo(User user);

	/**
	 * 删除用户角色关联数据
	 * @param user
	 * @return
	 */
	public int deleteUserRole(User user);
	
	/**
	 * 插入用户角色关联数据
	 * @param user
	 * @return
	 */
	public int insertUserRole(User user);
	
	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateUserInfo(User user);

	public List<User> findListByRoleName(String ename);
	public List<User> getCollegeSecs(String id);
	public List<User> getCollegeExperts(String id);
	public List<User> getSchoolSecs();
	public List<User> getSchoolExperts();

	public List<User> findByType(User user);
	
	public int insert(User user);
	
	public void updateUserByPhone(User user);

	public User findUserByLoginName(String loginName);
	

	public List<gContestUndergo> findContestByUserId(String userId);//根据userid获取大赛经历
	

	public User getUserByName(String name);
	
	public List<User> findListTree(User user);
	
	public List<User> getStuByCdn(@Param("no") String no,@Param("name") String name);
	public List<User> getTeaByCdn(@Param("no") String no,@Param("name") String name);
}
