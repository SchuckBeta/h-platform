package com.oseasy.initiate.modules.attachment.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.modules.attachment.dao.SysAttachmentDao;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;

/**
 * 附件信息表Service
 * @author zy
 * @version 2017-03-23
 */
@Service
@Transactional(readOnly = true)
public class SysAttachmentService extends CrudService<SysAttachmentDao, SysAttachment> {
	public List<Map<String,String>> getFileInfo(Map<String,String> map) {
		List<Map<String,String>> list=dao.getFileInfo(map);
		for(Map<String,String> m:list) {
			String extname = m.get("suffix").toLowerCase();
			switch(extname) {
				case "xls":
				case "xlsx":
					extname = "excel";
					break;
				case "doc":
				case "docx":
					extname = "word";
					break;
				case "ppt":
				case "pptx":
					extname = "ppt";
					break;
				case "jpg":
				case "jpeg":
				case "gif":
				case "png":
				case "bmp":
					extname = "image";
					break;
				case "rar":
				case "zip":
				case "txt":
				case "project":
					break;
				default:
					extname = "unknow";
			}
			m.put("imgType", extname);
		}
		return list;
	}
	public SysAttachment get(String id) {
		return super.get(id);
	}

	public List<SysAttachment> findList(SysAttachment sysAttachment) {
		return super.findList(sysAttachment);
	}

	public Page<SysAttachment> findPage(Page<SysAttachment> page, SysAttachment sysAttachment) {
		return super.findPage(page, sysAttachment);
	}

	@Transactional(readOnly = false)
	public void save(SysAttachment sysAttachment) {
		super.save(sysAttachment);
	}

	@Transactional(readOnly = false)
	public void delete(SysAttachment sysAttachment) {
		super.delete(sysAttachment);
	}

	@Transactional(readOnly = false)
	public void saveList(List<Map<String, String>> maps,String type,String fileStep,String uid) {
		if (maps!=null) {
			for(Map<String,String> map:maps) {
				SysAttachment sa=new SysAttachment();
				sa.setUid(uid);
				sa.setName(map.get("arrName"));
				String[] ss=map.get("arrName").split("\\.");
				sa.setSuffix(ss[ss.length-1]);
				sa.setType(type);
				sa.setFileStep(fileStep);
				FtpUtil t = new FtpUtil();
				try {
					sa.setUrl(t.moveFile(t.getftpClient(),map.get("arrUrl")));
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.save(sa);
			}
		}

	}


	public SysAttachment getByUrl(String url) {
		return dao.getByUrl(url);
	}
	public void updateAtt(String gId,String oldGidId) {
		dao.updateAtt(gId,oldGidId);
	}

}