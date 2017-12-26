package com.oseasy.initiate.modules.promodel.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.persistence.ActEntity;
import com.oseasy.initiate.common.persistence.AttachMentEntity;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserHistory;

/**
 * proModelEntity.
 * @author zy
 * @version 2017-07-13
 */
public class ProModel extends ActEntity<ProModel> {

	private static final long serialVersionUID = 1L;
	private String actYwId;		//业务id
	private String pName;		// 项目名称
	private String declareId;		// 申报人ID
	private User deuser;
	private String proType;		//项目 大赛
	private String type;		         // 大创 小创    互联网+ 创青春
	private String proCategory;		//项目类别：创新,创业//项目类别：project_type；大赛类别：competition_net_type
	private String level;		        // 大赛级别
	private String introduction;		// 项目简介
	private String projectSource;		// 项目来源
	private String financingStat;		// 融资情况 0 未，1 100w一下 2 100w以上
	private String finalStatus;	    // 项目评级 abcd
	private String teamId;		// 团队ID
	private Team team;		// 团队ID
	private String procInsId;		// 流程实例id
	private String proMark;		// 项目标识
	private String source;		// source
	private String competitionNumber;		// 编号
	private String grade;		// 大赛和项目结果
	private String gScore;		// 评分
	private Date subTime;		// 提交时间
	private ActYw actYw;

	private String state;      //状态是否结束 1是结束 0是未结束

	private AttachMentEntity attachMentEntity; //附件
	private List<TeamUserHistory> teacherList = Lists.newArrayList();
	private List<TeamUserHistory> studentList = Lists.newArrayList();
	private List<TeamUserHistory> teamUserHistoryList = Lists.newArrayList(); //团队信息

	public List<TeamUserHistory> getTeacherList() {
		return teacherList;
	}

	public void setTeacherList(List<TeamUserHistory> teacherList) {
		this.teacherList = teacherList;
	}

	public List<TeamUserHistory> getStudentList() {
		return studentList;
	}

	public void setStudentList(List<TeamUserHistory> studentList) {
		this.studentList = studentList;
	}

	public List<TeamUserHistory> getTeamUserHistoryList() {
		return teamUserHistoryList;
	}

	public void setTeamUserHistoryList(List<TeamUserHistory> teamUserHistoryList) {
		this.teamUserHistoryList = teamUserHistoryList;
	}

	public HashMap<String,Object> getVars() {
		HashMap<String,Object> vars=new HashMap<String, Object>();
		vars.put("number",competitionNumber);  //编号
		vars.put("id",id);  //id
		vars.put("name",pName);              //项目名称
		vars.put("type",type);               //项目类型
		vars.put("proCategory",proCategory);               //项类别
		vars.put("level",level);             //项目级别
		vars.put("financingStat",financingStat);  //融资情况
		vars.put("projectSource",projectSource);  //项目来源
		vars.put("teamId",teamId);  //项目来源
		vars.put("finalStatus",finalStatus);             //项目评级
		vars.put("gScore",gScore);             //打分情况
		vars.put("grade",grade);             //审核
		vars.put("declareId",declareId);     //大赛申报人id
		if((deuser != null) && (deuser.getOffice() != null)){
	    vars.put("dofficeName",deuser.getOffice().getName());     //
		}
		return vars;
	}

	public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public User getDeuser() {
    return deuser;
  }

  	public void setDeuser(User deuser) {
    this.deuser = deuser;
  }

	public String getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(String finalStatus) {
		this.finalStatus = finalStatus;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public Date getSubTime() {
		return subTime;
	}

	public void setSubTime(Date subTime) {
		this.subTime = subTime;
	}

	public String getProCategory() {
		return proCategory;
	}

	public void setProCategory(String proCategory) {
		this.proCategory = proCategory;
	}

	public AttachMentEntity getAttachMentEntity() {
		return attachMentEntity;
	}

	public void setAttachMentEntity(AttachMentEntity attachMentEntity) {
		this.attachMentEntity = attachMentEntity;
	}

	public ProModel() {
		super();
	}

	public ProModel(String id){
		super(id);
	}

	public String getProjectSource() {
		return projectSource;
	}

	public void setProjectSource(String projectSource) {
		this.projectSource = projectSource;
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

	public String getgScore() {
		return gScore;
	}

	public void setgScore(String gScore) {
		this.gScore = gScore;
	}

	public String getActYwId() {
		return actYwId;
	}

	public void setActYwId(String actYwId) {
		this.actYwId = actYwId;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
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

  public ActYw getActYw() {
    return actYw;
  }

  public void setActYw(ActYw actYw) {
    this.actYw = actYw;
  }
}