/**
 *
 */
package com.oseasy.initiate.modules.sys.dao;

import java.util.List;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.sys.entity.Menu;

/**
 * 菜单DAO接口

 * @version 2014-05-16
 */
@MyBatisDao
public interface MenuDao extends CrudDao<Menu> {

	public List<Menu> findByParentIdsLike(Menu menu);

	public List<Menu> findByUserId(Menu menu);

	public int updateParentIds(Menu menu);

	public int updateSort(Menu menu);

	public Menu getMenuByName(String name);

	/**
	 * 根据ID获取菜单，不考虑删除状态.
	 * @param id 唯一标识
	 * @return Menu
	 */
	public Menu getById(String id);

}
