/**
 * 
 */
package com.hch.platform.pcore.modules.cms.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.service.BaseService;
import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.pcore.modules.cms.dao.ArticleDao;
import com.hch.platform.pcore.modules.cms.entity.Category;
import com.hch.platform.pcore.modules.cms.entity.Site;
import com.hch.platform.pcore.modules.sys.entity.Office;

/**
 * 统计Service


 */
@Service
@Transactional(readOnly = true)
public class StatsService extends BaseService {

	@Autowired
	private ArticleDao articleDao;
	
	public List<Category> article(Map<String, Object> paramMap) {
		Category category = new Category();
		
		Site site = new Site();
		site.setId(Site.getCurrentSiteId());
		category.setSite(site);
		
		Date beginDate = DateUtil.parseDate(paramMap.get("beginDate"));
		if (beginDate == null) {
			beginDate = DateUtil.setDays(new Date(), 1);
			paramMap.put("beginDate", DateUtil.formatDate(beginDate, "yyyy-MM-dd"));
		}
		category.setBeginDate(beginDate);
		Date endDate = DateUtil.parseDate(paramMap.get("endDate"));
		if (endDate == null) {
			endDate = DateUtil.addDays(DateUtil.addMonths(beginDate, 1), -1);
			paramMap.put("endDate", DateUtil.formatDate(endDate, "yyyy-MM-dd"));
		}
		category.setEndDate(endDate);
		
		String categoryId = (String)paramMap.get("categoryId");
		if (categoryId != null && !("".equals(categoryId))) {
			category.setId(categoryId);
			category.setParentIds(categoryId);
		}
		
		String officeId = (String)(paramMap.get("officeId"));
		Office office = new Office();
		if (officeId != null && !("".equals(officeId))) {
			office.setId(officeId);
			category.setOffice(office);
		}else{
			category.setOffice(office);
		}
		
		List<Category> list = articleDao.findStats(category);
		return list;
	}

}
