package com.oseasy.initiate.modules.promodel.entity;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * proModelEntity.
 * @author zy
 * @version 2017-07-13
 */
public class ProModel extends DataEntity<ProModel> {

	private static final long serialVersionUID = 1L;
	private String pName;		// 项目名称
	private String declareId;		// 申报人ID
	private String type;		// 项目类型
	private String level;		// 项目级别
	private String introduction;		// 项目简介
	private String financingStat;		// 融资情况 0 未，1 100w一下 2 100w以上
	private String teamId;		// 团队ID
	private String procInsId;		// 流程实例id
	private String proMark;		// 项目标识
	private String source;		// source
	private String competitionNumber;		// 大赛编号
	private String grade;		// 大赛结果
	private String gScore;		// 学校评分

	public ProModel() {
		super();
	}

	public ProModel(String id){
		super(id);
	}

	@Length(min=0, max=128, message="项目名称长度必须介于 0 和 128 之间")
	public String getPName() {
		return pName;
	}

	public void setPName(String pName) {
		this.pName = pName;
	}

	@Length(min=0, max=64, message="申报人ID长度必须介于 0 和 64 之间")
	public String getDeclareId() {
		return declareId;
	}

	public void setDeclareId(String declareId) {
		this.declareId = declareId;
	}

	@Length(min=0, max=20, message="项目类型长度必须介于 0 和 20 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=0, max=20, message="项目级别长度必须介于 0 和 20 之间")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	@Length(min=0, max=20, message="融资情况 0 未，1 100w一下 2 100w以上长度必须介于 0 和 20 之间")
	public String getFinancingStat() {
		return financingStat;
	}

	public void setFinancingStat(String financingStat) {
		this.financingStat = financingStat;
	}

	@Length(min=0, max=64, message="团队ID长度必须介于 0 和 64 之间")
	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	@Length(min=0, max=64, message="流程实例id长度必须介于 0 和 64 之间")
	public String getProcInsId() {
		return procInsId;
	}

	public void setProcInsId(String procInsId) {
		this.procInsId = procInsId;
	}

	@Length(min=0, max=64, message="项目标识长度必须介于 0 和 64 之间")
	public String getProMark() {
		return proMark;
	}

	public void setProMark(String proMark) {
		this.proMark = proMark;
	}

	@Length(min=0, max=64, message="source长度必须介于 0 和 64 之间")
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Length(min=1, max=64, message="大赛编号长度必须介于 1 和 64 之间")
	public String getCompetitionNumber() {
		return competitionNumber;
	}

	public void setCompetitionNumber(String competitionNumber) {
		this.competitionNumber = competitionNumber;
	}

	@Length(min=0, max=20, message="大赛结果长度必须介于 0 和 20 之间")
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getGScore() {
		return gScore;
	}

	public void setGScore(String gScore) {
		this.gScore = gScore;
	}

}