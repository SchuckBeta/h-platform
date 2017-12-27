package com.hch.platform.pcore.modules.project.service.weekly;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.attachment.enums.FileTypeEnum;
import com.hch.platform.pcore.modules.attachment.enums.FileStepEnum;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.project.dao.weekly.ProjectWeeklyDao;
import com.hch.platform.pcore.modules.project.entity.weekly.ProjectWeekly;
import com.hch.platform.pcore.modules.project.vo.ProjectWeeklyVo;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

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
		sysAttachmentService.saveByVo(vo.getAttachMentEntity(),vo.getProjectWeekly().getId(),FileTypeEnum.S0,FileStepEnum.S101);
	}
	@Transactional(readOnly = false)
	public void saveVo(ProjectWeeklyVo vo) {
		vo.getProjectWeekly().setSuggestDate(new Date());
		AbsUser u=UserUtils.getUser();
		vo.getProjectWeekly().setUpdateBy(u);
		vo.getProjectWeekly().setUpdateDate(new Date());
		dao.saveSuggest(vo.getProjectWeekly());
		sysAttachmentService.saveByVo(vo.getAttachMentEntity(),vo.getProjectWeekly().getId(),FileTypeEnum.S0,FileStepEnum.S101);
	}
	@Transactional(readOnly = false)
	public void delete(ProjectWeekly projectWeekly) {
		super.delete(projectWeekly);
	}
	public ProjectWeekly getLast(Map<String,String> map) {
		return dao.getLast(map);
	}
}