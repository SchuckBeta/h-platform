/**
 * 
 */
package com.hch.platform.pcore.modules.oa.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.oa.entity.OaNotify;
import com.hch.platform.pcore.modules.oa.entity.OaNotifyRecord;

/**
 * 通知通告记录DAO接口

 * @version 2014-05-16
 */
@MyBatisDao
public interface OaNotifyRecordDao extends CrudDao<OaNotifyRecord> {
	public String getReadFlag(@Param("oid")String oid,@Param("uid")String uid);
	/**
	 * 插入通知记录
	 * @param oaNotifyRecordList
	 * @return
	 */
	public int insertAll(List<OaNotifyRecord> oaNotifyRecordList);
	
	/**
	 * 根据通知ID删除通知记录
	 * @param oaNotifyId 通知ID
	 * @return
	 */
	public int deleteByOaNotifyId(String oaNotifyId);

	public OaNotifyRecord findMyList(OaNotify oaNotify);
	
	public OaNotifyRecord getMine(OaNotifyRecord oaNotifyRecord);
	public void updateOperateFlag(OaNotifyRecord oaNotifyRecord);
	public void updateReadFlag(OaNotifyRecord oaNotifyRecord);
	public void updateReadOperateFlag(OaNotifyRecord oaNotifyRecord);
	
	
}