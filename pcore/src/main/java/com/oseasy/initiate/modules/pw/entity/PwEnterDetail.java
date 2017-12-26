package com.oseasy.initiate.modules.pw.entity;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;

import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.team.entity.Team;

/**
 * 入驻申报详情Entity.
 * @author chenh
 * @version 2017-11-26
 */
public class PwEnterDetail extends DataEntity<PwEnterDetail> {

	private static final long serialVersionUID = 1L;
	@Transient
	private PwEnter pwEnter;		// 申报编号
  private String rid;   // 团队、项目、企业 id 业务编号
  @Transient
	private Team team;		// 团队
	@Transient
	private ProjectDeclare project;		// 项目
	@Transient
  private PwCompany pwCompany; //企业
	private String type;		// 类型：pw_enter_type/PwEnterType
	@Transient
	private String petype;		// 类型：0、团队；1、项目；2、企业
	@Transient
	private String pename;		// 类型：0、团队；1、项目；2、企业
  private String status;    // 状态:pw_enter_shstatus/PwEnterShStatus
	private String pteam;		// 是否使用项目团队：0、否；1、是

	public PwEnterDetail() {
		super();
	}

	public PwEnterDetail(String id){
		super(id);
	}

	public PwEnterDetail(Team team) {
    super();
    this.team = team;
  }

	public PwEnterDetail(ProjectDeclare project) {
	  super();
	  this.project = project;
	}

	public PwEnterDetail(PwCompany pwCompany) {
	  super();
	  this.pwCompany = pwCompany;
	}

	public PwEnterDetail(Team team, ProjectDeclare project, PwCompany pwCompany) {
	  super();
	  this.team = team;
	  this.project = project;
	  this.pwCompany = pwCompany;
	}

  public String getPename() {
    return pename;
  }

  public void setPename(String pename) {
    this.pename = pename;
  }

  public String getPetype() {
    return petype;
  }

  public void setPetype(String petype) {
    this.petype = petype;
  }

  public PwEnter getPwEnter() {
    return pwEnter;
  }

  public void setPwEnter(PwEnter pwEnter) {
    this.pwEnter = pwEnter;
  }

  @Length(min=1, max=64, message="业务编号长度必须介于 1 和 64 之间")
	public String getRid() {
		return rid;
	}

	public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setRid(String rid) {
		this.rid = rid;
	}

	@Length(min=0, max=1, message="类型：0、团队；1、项目；2、企业长度必须介于 0 和 1 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Length(min=0, max=1, message="是否使用项目团队：0、否；1、是长度必须介于 0 和 1 之间")
	public String getPteam() {
		return pteam;
	}

	public void setPteam(String pteam) {
		this.pteam = pteam;
	}

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public ProjectDeclare getProject() {
    return project;
  }

  public void setProject(ProjectDeclare project) {
    this.project = project;
  }

  public PwCompany getPwCompany() {
    return pwCompany;
  }

  public void setPwCompany(PwCompany pwCompany) {
    this.pwCompany = pwCompany;
  }
}