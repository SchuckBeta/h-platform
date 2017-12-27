/**
 *
 */
package com.hch.platform.pcore.modules.sys.dao;

import java.util.List;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.AbsMenu;

/**
 * 菜单DAO接口

 * @version 2014-05-16
 */
@MyBatisDao
public interface MenuDao extends CrudDao<AbsMenu> {

	public List<AbsMenu> findByParentIdsLike(AbsMenu menu);

	public List<AbsMenu> findByUserId(AbsMenu menu);

	public int updateParentIds(AbsMenu menu);

	public int updateSort(AbsMenu menu);

	public AbsMenu getMenuByName(String name);

	/**
	 * 根据ID获取菜单，不考虑删除状态.
	 * @param id 唯一标识
	 * @return Menu
	 */
	public AbsMenu getById(String id);

}
