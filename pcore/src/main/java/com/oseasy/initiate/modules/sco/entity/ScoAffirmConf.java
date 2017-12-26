package com.hch.platform.pcore.modules.sco.entity;

import org.hibernate.validator.constraints.Length;

import com.hch.platform.pcore.common.persistence.DataEntity;

/**
 * 学分认定配置Entity.
 * @author 9527
 * @version 2017-07-18
 */
public class ScoAffirmConf extends DataEntity<ScoAffirmConf> {

	private static final long serialVersionUID = 1L;
	private String procId;		// 流程
	private String type;		// 学分类型
	private String item;		// 学分项
	private String category;		// 课程、项目、大赛、技能大类
	private String subdivision;		// 课程、项目、大赛小类
	
	private String skill_category;//技能证书分类
	private String project_category;//项目类型
	private String project_subdivision;//项目类别
	private String gcontest_category;//大赛类型
	private String gcontest_subdivision;//大赛类别
	

	public ScoAffirmConf() {
		super();
	}

	public ScoAffirmConf(String id){
		super(id);
	}
	
	

	public String getSkill_category() {
		if(skill_category!=null){
			return skill_category;
		}else{
			return category;
		}
	}

	public void setSkill_category(String skill_category) {
		this.skill_category = skill_category;
	}

	public String getProject_category() {
		if(project_category!=null){
			return project_category;
		}else{
			return category;
		}
	}

	public void setProject_category(String project_category) {
		this.project_category = project_category;
	}

	public String getProject_subdivision() {
		if(project_subdivision!=null){
			return project_subdivision;
		}else{
			return subdivision;
		}
	}

	public void setProject_subdivision(String project_subdivision) {
		this.project_subdivision = project_subdivision;
	}

	public String getGcontest_category() {
		if(gcontest_category!=null){
			return gcontest_category;
		}else{
			return category;
		}
	}

	public void setGcontest_category(String gcontest_category) {
		this.gcontest_category = gcontest_category;
	}

	public String getGcontest_subdivision() {
		if(gcontest_subdivision!=null){
			return gcontest_subdivision;
		}else{
			return subdivision;
		}
	}

	public void setGcontest_subdivision(String gcontest_subdivision) {
		this.gcontest_subdivision = gcontest_subdivision;
	}

	@Length(min=0, max=64, message="流程长度必须介于 1 和 64 之间")
	public String getProcId() {
		return procId;
	}

	public void setProcId(String procId) {
		this.procId = procId;
	}

	@Length(min=0, max=64, message="学分类型长度必须介于 0 和 64 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=0, max=64, message="学分项长度必须介于 0 和 64 之间")
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	@Length(min=0, max=64, message="课程、项目、大赛、技能大类长度必须介于 0 和 64 之间")
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Length(min=0, max=64, message="课程、项目、大赛小类长度必须介于 0 和 64 之间")
	public String getSubdivision() {
		return subdivision;
	}

	public void setSubdivision(String subdivision) {
		this.subdivision = subdivision;
	}

}