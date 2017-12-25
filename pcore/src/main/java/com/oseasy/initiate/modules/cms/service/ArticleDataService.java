/**
 * 
 */
package com.oseasy.initiate.modules.cms.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.cms.dao.ArticleDataDao;
import com.oseasy.initiate.modules.cms.entity.ArticleData;

/**
 * 站点Service


 */
@Service
@Transactional(readOnly = true)
public class ArticleDataService extends CrudService<ArticleDataDao, ArticleData> {

}
