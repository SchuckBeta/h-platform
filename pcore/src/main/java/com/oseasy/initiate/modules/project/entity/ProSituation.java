package com.hch.platform.pcore.modules.project.entity;

import org.hibernate.validator.constraints.Length;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

import com.hch.platform.pcore.common.persistence.DataEntity;

/**
 * 国创项目完成情况表单Entity
 * @author 9527
 * @version 2017-03-29
 */
public class ProSituation extends DataEntity<ProSituation> {
	
	private static final long serialVersionUID = 1L;
	private String fId;		// 中期检查表单id、结项表单id
	private String type;		// 1-中期检查表单,2-结项表单
	private AbsUser user;		// 成员id
	private String division;		// 项目分工
	private String situation;		// 完成情况
	
	public ProSituation() {
		super();
	}

	public ProSituation(String id) {
		super(id);
	}

	public String getFId() {
		return fId;
	}

	public void setFId(String fId) {
		this.fId = fId;
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public AbsUser getUser() {
		return user;
	}

	public void setUser(AbsUser user) {
		this.user = user;
	}
	

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}
	

	public String getSituation() {
		return situation;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}
	
}