/**
 * 
 */
package com.hch.platform.pcore.modules.cms.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.cms.entity.Site;

/**
 * 站点DAO接口

 * @version 2013-8-23
 */
@MyBatisDao
public interface SiteDao extends CrudDao<Site> {

}