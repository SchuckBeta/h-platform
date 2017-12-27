/**
 * 
 */
package com.hch.platform.pcore.modules.cms.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.cms.dao.ArticleDataDao;
import com.hch.platform.pcore.modules.cms.entity.ArticleData;

/**
 * 站点Service


 */
@Service
@Transactional(readOnly = true)
public class ArticleDataService extends CrudService<ArticleDataDao, ArticleData> {

}
