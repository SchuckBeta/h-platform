package com.oseasy.initiate.modules.attachment.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;

/**
 * 附件信息表DAO接口
 * @author zy
 * @version 2017-03-23
 */
@MyBatisDao
public interface SysAttachmentDao extends CrudDao<SysAttachment> {
	public List<Map<String,String>> getFileInfo(Map<String,String> map);
	public SysAttachment getByUrl(String url);
	public void deleteByUid(String uid);
	public void updateAtt(@Param("gid")String gid,@Param("oldGidId")String oldGidId);
}