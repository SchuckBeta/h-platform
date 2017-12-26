package com.hch.platform.pcore.modules.project.vo;

import java.util.List;

import com.hch.platform.pcore.common.persistence.AttachMentEntity;
import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.project.entity.weekly.ProjectWeekly;

/**
 * 项目周报Vo
 * @author 9527
 * @version 2017-03-11
 */
public class ProjectWeeklyVo {
	private ProjectWeekly projectWeekly;
	private ProjectWeekly lastpw;
	private List<SysAttachment> fileInfo;
	private AttachMentEntity attachMentEntity;


	public AttachMentEntity getAttachMentEntity() {
		return attachMentEntity;
	}

	public void setAttachMentEntity(AttachMentEntity attachMentEntity) {
		this.attachMentEntity = attachMentEntity;
	}
	public ProjectWeekly getProjectWeekly() {
		return projectWeekly;
	}

	public void setProjectWeekly(ProjectWeekly projectWeekly) {
		this.projectWeekly = projectWeekly;
	}

	public ProjectWeekly getLastpw() {
		return lastpw;
	}

	public void setLastpw(ProjectWeekly lastpw) {
		this.lastpw = lastpw;
	}

	public List<SysAttachment> getFileInfo() {
		return fileInfo;
	}

	public void setFileInfo(List<SysAttachment> fileInfo) {
		this.fileInfo = fileInfo;
	}

}