/**
 *
 */
package com.hch.platform.pcore.modules.sys.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.vo.UserVo;

/**
 * 用户DAO接口

 * @version 2014-05-16
 */
@MyBatisDao
public interface UserDao extends CrudDao<AbsUser> {
	public List<UserVo> findListByVo(AbsUser vo);
	public List<UserVo> getTeaInfo(@Param("idsArr")String[] idsArr);
	public List<UserVo> getStudentInfo(@Param("idsArr")String[] idsArr);
	public void updateLikes(@Param("param") Map<String,Integer> param);
	//批量更新浏览量
    public void updateViews(@Param("param") Map<String,Integer> param);
	public AbsUser getByMobile(AbsUser user);
	public AbsUser getByMobileWithId(AbsUser user);
	public void updateMobile(AbsUser user);

	public  String getTeacherTypeByUserId(@Param("userId") String userId);
	/**
	 * 根据登录名称查询用户
	 * @return
	 */
	public AbsUser getByLoginName(AbsUser user);

	/**
	 * 根据登录名或者学号查询用户
	 * @param loginNameOrNo 录名或者学号
	 * @return User
     */
	public AbsUser getByLoginNameOrNo(@Param("loginNameOrNo")String  loginNameOrNo,@Param("id")String  id);
	public AbsUser getByLoginNameAndNo(@Param("loginName")String  loginName,@Param("no")String no);

	/**
	 * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<AbsUser> findUserByOfficeId(AbsUser user);

	/**
	 * 通过professionalId获取用户列表，仅返回用户id和name（树查询用户时用）
	 * @param user
	 * @return
	 */
	public List<AbsUser> findUserByProfessionId(AbsUser user);

	/**
	 * 查询全部用户数目
	 * @return
	 */
	public long findAllCount(AbsUser user);

	/**
	 * 更新用户密码
	 * @param user
	 * @return
	 */
	public int updatePasswordById(AbsUser user);

	/**
	 * 更新用户照片
	 * @param user
	 * @return
	 */
	public int updateUserPhoto(AbsUser user);
	/**
	 * 更新登录信息，如：登录IP、登录时间
	 * @param user
	 * @return
	 */
	public int updateLoginInfo(AbsUser user);

	/**
	 * 删除用户角色关联数据
	 * @param user
	 * @return
	 */
	public int deleteUserRole(AbsUser user);

	/**
	 * 插入用户角色关联数据
	 * @param user
	 * @return
	 */
	public int insertUserRole(AbsUser user);

	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public int updateUserInfo(AbsUser user);

	public List<AbsUser> findListByRoleName(String enname);
	public List<AbsUser> getCollegeSecs(String id);
	public List<AbsUser> getCollegeExperts(String id);
	public List<AbsUser> getSchoolSecs();
	public List<AbsUser> getSchoolExperts();

	public List<AbsUser> findByType(AbsUser user);

	public int insert(AbsUser user);

	public void updateUserByPhone(AbsUser user);

	public AbsUser findUserByLoginName(String loginName);

	public AbsUser getUserByName(String name);

	public List<AbsUser> findListTree(AbsUser user);

	/**
	 * 查询学生.
   * @param user 用户
   * @return List
	 */
	public List<AbsUser> findListTreeByStudent(AbsUser user);

  /**
   * 查询导师.
   * @param user 用户
   * @return List
   */
	public List<AbsUser> findListTreeByTeacher(AbsUser user);

  /**
   * 查询用户（基本信息）.
   * @param user 用户
   * @return List
   */
	public List<AbsUser> findListTreeByUser(AbsUser user);
	public List<AbsUser> getStuByCdn(@Param("no") String no,@Param("name") String name);
	public List<AbsUser> getTeaByCdn(@Param("no") String no,@Param("name") String name);
	public AbsUser getByNo(@Param("no")String no);
	List<String> findListByRoleId(String roleId);

	List<AbsUser> findListByRoleNameAndOffice(@Param("enname") String enname, @Param("userId") String userId);
	/**
	 * 查询所有需要修复的学生.
	 */
  public List<String> findUserByRepair();
}
