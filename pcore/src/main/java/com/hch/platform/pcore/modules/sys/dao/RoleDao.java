/**
 *
 */
package com.hch.platform.pcore.modules.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.AbsRole;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

/**
 * 角色DAO接口


 */
@MyBatisDao
public interface RoleDao extends CrudDao<AbsRole> {

	public AbsRole getByName(AbsRole role);

	public AbsRole getByEnname(AbsRole role);

	/**
	 * 维护角色与菜单权限关系
	 * @param role
	 * @return
	 */
	public int deleteRoleMenu(AbsRole role);

	public int insertRoleMenu(AbsRole role);

	/**
	 * 维护角色与公司部门关系
	 * @param role
	 * @return
	 */
	public int deleteRoleOffice(AbsRole role);

	public int insertRoleOffice(AbsRole role);

	public List<AbsRole> findListByUserId(@Param(value = "userId") String userId);

	public AbsRole getNamebyId(String id);


	public List<AbsRole> findListByIds(@Param("ids") List<String> ids);
}
