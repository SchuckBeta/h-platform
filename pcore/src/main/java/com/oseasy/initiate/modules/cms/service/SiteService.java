/**
 *
 */
package com.oseasy.initiate.modules.cms.service;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.cms.dao.SiteDao;
import com.oseasy.initiate.modules.cms.entity.Site;
import com.oseasy.initiate.modules.cms.utils.CmsUtils;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 站点Service


 */
@Service
@Transactional(readOnly = true)
public class SiteService extends CrudService<SiteDao, Site> {
	@Autowired
	private CategoryService categoryService;

	public Page<Site> findPage(Page<Site> page, Site site) {
//		DetachedCriteria dc = siteDao.createDetachedCriteria();
//		if (StringUtil.isNotEmpty(site.getName())) {
//			dc.add(Restrictions.like("name", "%"+site.getName()+"%"));
//		}
//		dc.add(Restrictions.eq(Site.FIELD_DEL_FLAG, site.getDelFlag()));
//		//dc.addOrder(Order.asc("id"));
//		return siteDao.find(page, dc);

		site.getSqlMap().put("site", dataScopeFilter(site.getCurrentUser(), "o", "u"));

		return super.findPage(page, site);
	}

	@Transactional(readOnly = false)
	public boolean save(Site site, Office office) {
		if (site.getIsNewRecord()) {
			User user = UserUtils.getUser();
			if (user.getAdmin()) {
				categoryService.saveNew(site, office);
			}else{
				categoryService.saveNew(site, user.getCompany());
			}
			save(site);
			return true;
		}else{
      save(site);
		}
		return false;
	}

	@Transactional(readOnly = false)
	public void save(Site site) {
		if (site.getCopyright()!=null) {
			site.setCopyright(StringEscapeUtils.unescapeHtml4(site.getCopyright()));
		}

		super.save(site);
		CmsUtils.removeCache("site_"+site.getId());
		CmsUtils.removeCache("siteList");
	}

	@Transactional(readOnly = false)
	public void delete(Site site, Boolean isRe) {
		//siteDao.updateDelFlag(id, isRe!=null&&isRe?Site.DEL_FLAG_NORMAL:Site.DEL_FLAG_DELETE);
		site.setDelFlag(isRe!=null&&isRe?Site.DEL_FLAG_NORMAL:Site.DEL_FLAG_DELETE);
		super.delete(site);
		//siteDao.delete(id);
		CmsUtils.removeCache("site_"+site.getId());
		CmsUtils.removeCache("siteList");
	}

}
