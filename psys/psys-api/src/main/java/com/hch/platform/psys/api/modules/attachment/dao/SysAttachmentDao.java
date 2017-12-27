package com.hch.platform.pcore.modules.attachment.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;

/**
 * 附件信息表DAO接口
 * @author zy
 * @version 2017-03-23
 */
@MyBatisDao
public interface SysAttachmentDao extends CrudDao<SysAttachment> {
	public void deleteByCdnNotInSet(@Param("sa")SysAttachment s,@Param("urls")Set<String> set);
	public void deleteByCdn(SysAttachment s);
	public List<SysAttachment> getFilesNotInSet(@Param("sa")SysAttachment s,@Param("urls")Set<String> set);
	public List<SysAttachment> getFiles(SysAttachment s);
	public List<Map<String,String>> getFileInfo(Map<String,String> map);
	public SysAttachment getByUrl(String url);
	public void deleteByUid(String uid);
	public void updateAtt(@Param("gid")String gid,@Param("oldGidId")String oldGidId);

  public List<SysAttachment> findListInIds(SysAttachment entity);
}