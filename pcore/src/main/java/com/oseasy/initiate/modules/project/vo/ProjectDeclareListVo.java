package com.oseasy.initiate.modules.project.vo;

import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.modules.project.enums.ProjectFinalResultEnum;
import com.oseasy.initiate.modules.project.enums.ProjectStatusEnum;

/**
 * 项目申报Vo
 * @author 9527
 * @version 2017-03-11
 */
public class ProjectDeclareListVo extends DataEntity<ProjectDeclareListVo> {

	/**
	 *
	 */
	private static final long serialVersionUID = -1926885497474088959L;

	private String number;
	private String project_name;
	private String type;
	private String level;
	private String leader;
	private String apply_time;
	private String status;
	private String final_result;
	private String state;
	private String grade;
	private String final_result_code;
	private String proc_ins_id;
	private String status_code;
	private String leaderId;
	private String create_date;
	private String snames;
	private String tnames;
	private String hasConfig;
	private String ratio;
	private String proType;
	private String proTypeStr;
	private String ftb;
	private String actywId;
	private String groupId;
	//cdn
	private String userid;

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getGroupId() {
    return groupId;
  }
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
  public String getActywId() {
		return actywId;
	}
	public void setActywId(String actywId) {
		this.actywId = actywId;
	}
	public String getLeaderId() {
		return leaderId;
	}
	public void setLeaderId(String leaderId) {
		this.leaderId = leaderId;
	}
	public String getFtb() {
		return ftb;
	}
	public void setFtb(String ftb) {
		this.ftb = ftb;
	}
	public String getProType() {
		return proType;
	}
	public void setProType(String proType) {
		this.proType = proType;
	}
	public String getProTypeStr() {
		return proTypeStr;
	}
	public void setProTypeStr(String proTypeStr) {
		this.proTypeStr = proTypeStr;
	}
	public String getHasConfig() {
		return hasConfig;
	}
	public void setHasConfig(String hasConfig) {
		this.hasConfig = hasConfig;
	}
	public String getRatio() {
		return ratio;
	}
	public void setRatio(String ratio) {
		this.ratio = ratio;
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
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProjectName(String project_name) {
		this.project_name = project_name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getLeader() {
		return leader;
	}
	public void setLeader(String leader) {
		this.leader = leader;
	}
	public String getApply_time() {
		return apply_time;
	}
	public void setApplyTime(String apply_time) {
		this.apply_time = apply_time;
	}
	public String getStatus() {
		if("-999".equals(status_code)){
			return this.status;
		}else{
			return ProjectStatusEnum.getNameByValue(status_code);
		}
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFinal_result() {
		return ProjectFinalResultEnum.getNameByValue(final_result_code);
	}
	public void setFinalResult(String final_result) {
		this.final_result = final_result;
	}
	public String getFinal_result_code() {
		return final_result_code;
	}
	public void setFinalResultCode(String final_result_code) {
		this.final_result_code = final_result_code;
	}
	public String getProc_ins_id() {
		return proc_ins_id;
	}
	public void setProcInsId(String proc_ins_id) {
		this.proc_ins_id = proc_ins_id;
	}
	public String getStatus_code() {
		return status_code;
	}
	public void setStatusCode(String status_code) {
		this.status_code = status_code;
	}
	public String getCreate_date() {
		return create_date;
	}
	public void setCreateDate(String create_date) {
		this.create_date = create_date;
	}

}