/**
 * 
 */
package com.oseasy.initiate.modules.cms.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.cms.entity.Guestbook;

/**
 * 留言DAO接口

 * @version 2013-8-23
 */
@MyBatisDao
public interface GuestbookDao extends CrudDao<Guestbook> {

}
