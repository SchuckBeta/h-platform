package com.oseasy.initiate.modules.project.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oseasy.initiate.common.persistence.ActEntity;

/**
 * 项目申报Entity
 * @author 9527
 * @version 2017-03-11
 */
public class ProjectDeclare extends ActEntity<ProjectDeclare> {

	private static final long serialVersionUID = 1L;
	private String orgId;		// 学院
	private String planContent;		// 项目实施预案
	private Date planStartDate;		// 项目预案时间起始
	private Date planEndDate;		// 项目预案时间截止
	private String planStep;		// 项目预案措施
	private String resultType;		// 成果形式
	private String resultContent;		// 成果说明
	private String level;		// 立项评级结果(1A+、2A、3B、4不合格 提交给学校评级) 字典type==project_degree
	private String type;		// 项目类型
	private String name;		// 项目名称
	private String leader;		// 项目负责人
	private String number;		// 项目编号
	private String introduction;		// 项目简介
	private Date applyTime;		// 申请日期
	private String innovation;		// 项目前期调研准备
	private String budget;		// 经费预算
	private int    midCount;    // 中期报告整改次数（如果等于1不能再整改了）
	private String midResult;		// 中期检查结果(0合格，2不合格)
	private float midScore;		// 中期检查评分
	private float finalScore;		// 结项评分
	private float replyScore;      //答辩评分
	//字典值project_result jsp页面这么写${fns:getDictLabel(projectDeclare.finalResult, "project_result", projectDeclare.finalResult)}
	private String finalResult;		// 项目结果(0合格，1优秀，2不合格，3立项不合格，4中期不合格,5延期结项）
	private String development;		// 项目拓展
	private String source;		// 项目来源
	private String status;		//(0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止,9.项目结项)


	private String teamId;		// 团队id
	private String templateId;		// 项目通告id

	private String comment;   //审核意见  addBy zhangzheng
	private String pass;      //是否通过 addBy zhangzheng

	private String snames;  //项目组成员  addBy zhangzheng
	private String tnames;  //指导老师 addBy zhangzheng
	private String typeString;
	private String leaderString;
	private String levelString;
	private String  finalResultString;
	private String  keyword;

	private String startDateStr;
	private Date startDate;
	private Date endDate;
	private Date approvalDate;//立项审核时间

	private List<ProjectAuditInfo> auditInfoList;  //专家评审意见 addBy zhangzheng

	private List<ProjectAuditInfo> midAuditList;  //中期评分意见 addBy zhangzheng
	private List<ProjectAuditInfo> closeAuditList; //结项评分意见 addBy zhangzheng

	private List<ProjectPlan> planList;  //任务分工 addBy zhangzheng

	private String financeGrant;//财政拨款（元）
	private String universityGrant;//校拨（元）
	private String totalGrant;//总经费（元）
	private String province;//省（区、市）
	private String universityCode;//高校代码
	private String universityName;//高校名称


	public Date getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getUniversityCode() {
		return universityCode;
	}

	public void setUniversityCode(String universityCode) {
		this.universityCode = universityCode;
	}

	public String getUniversityName() {
		return universityName;
	}

	public void setUniversityName(String universityName) {
		this.universityName = universityName;
	}

	public String getFinanceGrant() {
		return financeGrant;
	}

	public void setFinanceGrant(String financeGrant) {
		this.financeGrant = financeGrant;
	}

	public String getUniversityGrant() {
		return universityGrant;
	}

	public void setUniversityGrant(String universityGrant) {
		this.universityGrant = universityGrant;
	}

	public String getTotalGrant() {
		return totalGrant;
	}

	public void setTotalGrant(String totalGrant) {
		this.totalGrant = totalGrant;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getStartDateStr() {
		return startDateStr;
	}

	public void setStartDateStr(String startDateStr) {
		this.startDateStr = startDateStr;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<ProjectAuditInfo> getAuditInfoList() {
		return auditInfoList;
	}

	public void setAuditInfoList(List<ProjectAuditInfo> auditInfoList) {
		this.auditInfoList = auditInfoList;
	}

	public List<ProjectAuditInfo> getMidAuditList() {
		return midAuditList;
	}

	public void setMidAuditList(List<ProjectAuditInfo> midAuditList) {
		this.midAuditList = midAuditList;
	}

	public List<ProjectAuditInfo> getCloseAuditList() {
		return closeAuditList;
	}

	public void setCloseAuditList(List<ProjectAuditInfo> closeAuditList) {
		this.closeAuditList = closeAuditList;
	}

	public ProjectDeclare() {
		super();
	}

	public ProjectDeclare(String id) {
		super(id);
	}

	public int getMidCount() {
		return midCount;
	}

	public void setMidCount(int midCount) {
		this.midCount = midCount;
	}

	@Length(min=0, max=64, message="学院长度必须介于 0 和 64 之间")
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getPlanContent() {
		return planContent;
	}

	public void setPlanContent(String planContent) {
		this.planContent = planContent;
	}

	//@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPlanStartDate() {
		return planStartDate;
	}

	public void setPlanStartDate(Date planStartDate) {
		this.planStartDate = planStartDate;
	}

	//@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPlanEndDate() {
		return planEndDate;
	}

	public void setPlanEndDate(Date planEndDate) {
		this.planEndDate = planEndDate;
	}

	public String getPlanStep() {
		return planStep;
	}

	public void setPlanStep(String planStep) {
		this.planStep = planStep;
	}

	@Length(min=0, max=512, message="成果形式长度必须介于 0 和 512 之间")
	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public String getResultContent() {
		return resultContent;
	}

	public void setResultContent(String resultContent) {
		this.resultContent = resultContent;
	}

	@Length(min=0, max=64, message="项目评级长度必须介于 0 和 64 之间")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@Length(min=0, max=64, message="项目类型长度必须介于 0 和 64 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=0, max=128, message="项目名称长度必须介于 0 和 128 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min=0, max=64, message="项目负责人长度必须介于 0 和 64 之间")
	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	@Length(min=0, max=64, message="项目编号长度必须介于 0 和 64 之间")
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public String getInnovation() {
		return innovation;
	}

	public void setInnovation(String innovation) {
		this.innovation = innovation;
	}

	public String getBudget() {
		return budget;
	}

	public void setBudget(String budget) {
		this.budget = budget;
	}

	@Length(min=0, max=64, message="中期检查结果长度必须介于 0 和 64 之间")
	public String getMidResult() {
		return midResult;
	}

	public void setMidResult(String midResult) {
		this.midResult = midResult;
	}

	public float getMidScore() {
		return midScore;
	}

	public void setMidScore(float midScore) {
		this.midScore = midScore;
	}

	public float getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(float finalScore) {
		this.finalScore = finalScore;
	}

	@Length(min=0, max=64, message="结项审核结果长度必须介于 0 和 64 之间")
	public String getFinalResult() {
		return finalResult;
	}

	public void setFinalResult(String finalResult) {
		this.finalResult = finalResult;
	}

	@Length(min=0, max=512, message="项目拓展长度必须介于 0 和 512 之间")
	public String getDevelopment() {
		return development;
	}

	public void setDevelopment(String development) {
		this.development = development;
	}

	@Length(min=0, max=64, message="项目来源长度必须介于 0 和 64 之间")
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Length(min=0, max=64, message="项目状态长度必须介于 0 和 64 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Length(min=0, max=64, message="团队id长度必须介于 0 和 64 之间")
	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	@Length(min=0, max=64, message="项目通告id长度必须介于 0 和 64 之间")
	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public float getReplyScore() {
		return replyScore;
	}

	public void setReplyScore(float replyScore) {
		this.replyScore = replyScore;
	}

	public String getSnames() {
		return snames;
	}

	public void setSnames(String snames) {
		this.snames = snames;
	}

	public String getTnames() {
		return tnames;
	}

	public void setTnames(String tnames) {
		this.tnames = tnames;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public String getLeaderString() {
		return leaderString;
	}

	public void setLeaderString(String leaderString) {
		this.leaderString = leaderString;
	}

	public String getLevelString() {
		return levelString;
	}

	public void setLevelString(String levelString) {
		this.levelString = levelString;
	}

	public String getFinalResultString() {
		return finalResultString;
	}

	public void setFinalResultString(String finalResultString) {
		this.finalResultString = finalResultString;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<ProjectPlan> getPlanList() {
		return planList;
	}

	public void setPlanList(List<ProjectPlan> planList) {
		this.planList = planList;
	}

	public static String  getPrefix(String level) {
		if ("1".equals(level)) { //A+
		  return "A+";
		}
		if ("2".equals(level)) { //A
		 return "A";
		}
		if ("3".equals(level)) { //B
			return "B";
		}
		if ("4".equals(level)) { //C
			return "C";
		}
		return "";
	}

}