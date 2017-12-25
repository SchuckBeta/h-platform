package com.oseasy.initiate.modules.project.service.weekly;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.enums.FileSourceEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.project.dao.weekly.ProjectWeeklyDao;
import com.oseasy.initiate.modules.project.entity.weekly.ProjectWeekly;
import com.oseasy.initiate.modules.project.vo.ProjectWeeklyVo;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 项目周报Service
 * @author 张正
 * @version 2017-03-29
 */
@Service
@Transactional(readOnly = true)
public class ProjectWeeklyService extends CrudService<ProjectWeeklyDao, ProjectWeekly> {
	@Autowired
	SysAttachmentService sysAttachmentService;
	public ProjectWeekly get(String id) {
		return super.get(id);
	}
	
	public List<ProjectWeekly> findList(ProjectWeekly projectWeekly) {
		return super.findList(projectWeekly);
	}
	
	public Page<ProjectWeekly> findPage(Page<ProjectWeekly> page, ProjectWeekly projectWeekly) {
		return super.findPage(page, projectWeekly);
	}
	
	@Transactional(readOnly = false)
	public void save(ProjectWeekly projectWeekly) {
		super.save(projectWeekly);
	}
	@Transactional(readOnly = false)
	public void submitVo(ProjectWeeklyVo vo) {
		super.save(vo.getProjectWeekly());
		if (vo.getFileInfo()!=null) {
			for(Map<String,String> map:vo.getFileInfo()) {
				SysAttachment sa=new SysAttachment();
				sa.setUid(vo.getProjectWeekly().getId());
				sa.setName(map.get("arrName"));
				String[] ss=map.get("arrName").split("\\.");
				sa.setSuffix(ss[ss.length-1]);
				sa.setType(FileSourceEnum.S0.getValue());
				sa.setFileStep(FileTypeEnum.S101.getValue());
				FtpUtil t = new FtpUtil();
				try {
					sa.setUrl(t.moveFile(t.getftpClient(),map.get("arrUrl")));
				} catch (Exception e) {
					e.printStackTrace();
				}
				sysAttachmentService.save(sa);
			}
		}
	}
	@Transactional(readOnly = false)
	public void saveVo(ProjectWeeklyVo vo) {
		vo.getProjectWeekly().setSuggestDate(new Date());
		User u=UserUtils.getUser();
		vo.getProjectWeekly().setUpdateBy(u);
		vo.getProjectWeekly().setUpdateDate(new Date());
		dao.saveSuggest(vo.getProjectWeekly());
		if (vo.getFileInfo()!=null) {
			for(Map<String,String> map:vo.getFileInfo()) {
				SysAttachment sa=new SysAttachment();
				sa.setUid(vo.getProjectWeekly().getId());
				sa.setName(map.get("arrName"));
				String[] ss=map.get("arrName").split("\\.");
				sa.setSuffix(ss[ss.length-1]);
				sa.setType(FileSourceEnum.S0.getValue());
				sa.setFileStep(FileTypeEnum.S101.getValue());
				FtpUtil t = new FtpUtil();
				try {
					sa.setUrl(t.moveFile(t.getftpClient(),map.get("arrUrl")));
				} catch (Exception e) {
					e.printStackTrace();
				}
				sysAttachmentService.save(sa);
			}
		}
	}
	@Transactional(readOnly = false)
	public void delete(ProjectWeekly projectWeekly) {
		super.delete(projectWeekly);
	}
	public ProjectWeekly getLast(Map<String,String> map) {
		return dao.getLast(map);
	}
}