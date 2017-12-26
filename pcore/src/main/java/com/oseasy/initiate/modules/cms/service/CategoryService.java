/**
 *
 */
package com.oseasy.initiate.modules.cms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysCacheKeys;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.TreeService;
import com.oseasy.initiate.modules.cms.dao.CategoryDao;
import com.oseasy.initiate.modules.cms.entity.Category;
import com.oseasy.initiate.modules.cms.entity.Site;
import com.oseasy.initiate.modules.cms.utils.CmsUtils;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 栏目Service

 * @version 2013-5-31
 */
@Service
@Transactional(readOnly = true)
public class CategoryService extends TreeService<CategoryDao, Category> {

	public static final String CACHE_CATEGORY_LIST = "categoryList";

	private Category entity = new Category();

	@SuppressWarnings("unchecked")
	public List<Category> find(boolean isCurrentSite, String module) {

		List<Category> list = (List<Category>)UserUtils.getCache(CACHE_CATEGORY_LIST);
		if (list == null) {
			Category category = new Category();
			category.setOffice(new Office());
			category.setSite(new Site());
			category.setParent(new Category());
			list = dao.findList(category);
			// 将没有父节点的节点，找到父节点
			Set<String> parentIdSet = Sets.newHashSet();
			for (Category e : list) {
				if (e.getParent()!=null && StringUtils.isNotBlank(e.getParent().getId())) {
					boolean isExistParent = false;
					for (Category e2 : list) {
						if (e.getParent().getId().equals(e2.getId())) {
							isExistParent = true;
							break;
						}
					}
					if (!isExistParent) {
						parentIdSet.add(e.getParent().getId());
					}
				}
			}
			if (parentIdSet.size() > 0) {
				//FIXME 暂且注释，用于测试
//				dc = dao.createDetachedCriteria();
//				dc.add(Restrictions.in("id", parentIdSet));
//				dc.add(Restrictions.eq("delFlag", Category.DEL_FLAG_NORMAL));
//				dc.addOrder(Order.asc("site.id")).addOrder(Order.asc("sort"));
//				list.addAll(0, dao.find(dc));
			}
			UserUtils.putCache(CACHE_CATEGORY_LIST, list);
		}

		if (isCurrentSite) {
			List<Category> categoryList = Lists.newArrayList();
			for (Category e : list) {
				if (Category.isRoot(e.getId()) || (e.getSite()!=null && e.getSite().getId() !=null
						&& e.getSite().getId().equals(Site.getCurrentSiteId()))) {
					if (StringUtils.isNotEmpty(module)) {
						if (module.equals(e.getModule()) || "".equals(e.getModule())) {
							categoryList.add(e);
						}
					}else{
						categoryList.add(e);
					}
				}
			}
			return categoryList;
		}
		return list;
	}

	public List<Category> findByParentId(String parentId, String siteId) {
		Category parent = new Category();
		parent.setId(parentId);
		entity.setParent(parent);
		Site site = new Site();
		site.setId(siteId);
		entity.setSite(site);
		return dao.findByParentIdAndSiteId(entity);
	}

	/**
	 * 获取当前机构官网首页栏目菜单(支持2/3级节点)
	 * @param parentId
	 * @return
	 */
	public Map<String, List<Category>> getCategoryTrees(String parentId) {
		if (StringUtils.isEmpty(parentId)) {
			return null;
		}

		Map<String, List<Category>> trees = new HashMap<String, List<Category>>();
		List<Category> firstCategory=Lists.newArrayList();
		List<Category> secondCategorys=Lists.newArrayList();
		List<Category> threeCategorys=Lists.newArrayList();

		List<Category> list = Lists.newArrayList();
		Category m = new Category();
		m.setParentIds(SysIds.SITE_CATEGORYS_SYS_ROOT.getId());
		m.setInMenu(Global.SHOW);
		List<Category> sourcelist = findByParentIdsLike(m);
		Category.sortList(list, sourcelist, parentId);

		firstCategory.add(dao.get(parentId));
		trees.put(SysCacheKeys.SITE_CATEGORYS_INDEX_FIRST.getKey(), firstCategory);


		for (Category tempCategory:list) {
			if (tempCategory.getParent().getId().equals(parentId)) {
				if (StringUtils.equals("1",tempCategory.getInMenu())) {
					secondCategorys.add(tempCategory);
				}
			}else{
				threeCategorys.add(tempCategory);
			}
		}

		for (Category tempCategory2:secondCategorys) {
			List<Category> children=Lists.newArrayList();
			for (Category tempCategory3:threeCategorys) {
				if (tempCategory3.getParent().getId().equals(tempCategory2.getId())) {
					if (StringUtils.equals("1",tempCategory3.getInMenu())) {
						children.add(tempCategory3);
					}

				}
			}
			tempCategory2.setChildList(children);
		}

		trees.put(SysCacheKeys.SITE_CATEGORYS_INDEX_SENCOND.getKey(),secondCategorys);
		return trees;
	}

	public Page<Category> find(Page<Category> page, Category category) {
//		DetachedCriteria dc = dao.createDetachedCriteria();
//		if (category.getSite()!=null && StringUtils.isNotBlank(category.getSite().getId())) {
//			dc.createAlias("site", "site");
//			dc.add(Restrictions.eq("site.id", category.getSite().getId()));
//		}
//		if (category.getParent()!=null && StringUtils.isNotBlank(category.getParent().getId())) {
//			dc.createAlias("parent", "parent");
//			dc.add(Restrictions.eq("parent.id", category.getParent().getId()));
//		}
//		if (StringUtils.isNotBlank(category.getInMenu()) && Category.SHOW.equals(category.getInMenu())) {
//			dc.add(Restrictions.eq("inMenu", category.getInMenu()));
//		}
//		dc.add(Restrictions.eq(Category.FIELD_DEL_FLAG, Category.DEL_FLAG_NORMAL));
//		dc.addOrder(Order.asc("site.id")).addOrder(Order.asc("sort"));
//		return dao.find(page, dc);
//		page.setSpringPage(dao.findByParentId(category.getParent().getId(), page.getSpringPage()));
//		return page;
		category.setPage(page);
		category.setInMenu(Global.SHOW);
		page.setList(dao.findModule(category));
		return page;
	}

	@Transactional(readOnly = false)
	public void save(Category category) {
		category.setSite(new Site(Site.getCurrentSiteId()));
		if (StringUtils.isNotBlank(category.getViewConfig())) {
            category.setViewConfig(StringEscapeUtils.unescapeHtml4(category.getViewConfig()));
        }
		super.save(category);
		UserUtils.removeCache(CACHE_CATEGORY_LIST);
		CmsUtils.removeCache("mainNavList_"+category.getSite().getId());
	}

	@Transactional(readOnly = false)
	public Category saveNew(Site site, Office office) {
		Category root = get(SysIds.SITE_CATEGORYS_SYS_ROOT.getId());
		Category category = new Category();
		category.setParent(root);
		category.setSite(site);
		category.setOffice(office);
		category.setInMenu(Global.SHOW);
		category.setInList(Global.SHOW);
		category.setShowModes("0");
		category.setAllowComment(Global.NO);
		category.setIsAudit(Global.NO);
		category.setName(site.getName()+"根栏目");
		category.setRemarks("系统自动创建["+site.getName()+"根栏目],禁止删除");
		super.save(category);

		UserUtils.removeCache(CACHE_CATEGORY_LIST);
		CmsUtils.removeCache("mainNavList_"+category.getSite().getId());
		return category;
	}

	@Transactional(readOnly = false)
	public void delete(Category category) {
		super.delete(category);
		UserUtils.removeCache(CACHE_CATEGORY_LIST);
		CmsUtils.removeCache("mainNavList_"+category.getSite().getId());
	}

	/**
	 * 通过编号获取栏目列表
	 */
	public List<Category> findByIds(String ids) {
		List<Category> list = Lists.newArrayList();
		String[] idss = StringUtils.split(ids,",");
		if (idss.length>0) {
//			List<Category> l = dao.findByIdIn(idss);
//			for (String id : idss) {
//				for (Category e : l) {
//					if (e.getId().equals(id)) {
//						list.add(e);
//						break;
//					}
//				}
//			}
			for(String id : idss) {
				Category e = dao.get(id);
				if (null != e) {
					//System.out.println("e.id:"+e.getId()+",e.name:"+e.getName());
					list.add(e);
				}
				//list.add(dao.get(id));

			}
		}
		return list;
	}

	/**
	 * 根据parentIds获取栏目分类
	 * @param parentIds
	 * @return
	 */
	public List<Category> findByParentIdsLike(String parentIds) {
		Category m = new Category();
		m.setParentIds(parentIds);
		return findByParentIdsLike(m);
	}
	public List<Category> findByParentIdsLike(Category m) {
		return dao.findByParentIdsLike(m);
	}

	public Category findByName(String meunname) {
		return dao.getCategoryByName(meunname);
	}
}
