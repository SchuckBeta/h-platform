package com.oseasy.initiate.modules.pw.entity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYwApply;
import com.oseasy.initiate.modules.pw.vo.PwEnterStatus;
import com.oseasy.initiate.modules.sys.entity.User;

/**
 * 入驻申报Entity.
 * @author chenh
 * @version 2017-11-26
 */
public class PwEnter extends DataEntity<PwEnter> {
	private static final long serialVersionUID = 1L;
  public static final String STATUS = "status";
	private String no;		// 申请编号
	private User applicant;		// 申请人
	private String status;		// 状态:pw_enter_status/PwEnterStatus
	private String isTemp;		// 状态:是否临时数据
	private Integer term;		// 期限,单位
	private Date startDate;		// 开始时间
	private Date endDate;		// 结束时间
  private ActYwApply apply;   //申报信息
  @Transient
  private Boolean isView;
  @Transient
  private Boolean isAudited;
  @Transient
  private String pstatus;    // 状态:pw_enter_status/PwEnterStatus
  @Transient
  private List<String> pstatuss;    // 状态:pw_enter_status/PwEnterStatus
  @Transient
  private String pstatussql;    // 状态:pw_enter_status/PwEnterStatus
  @Transient
  private PwEnterDetail eteam;    // 团队
  @Transient
  private PwEnterDetail eproject;   // 项目
  @Transient
  private PwEnterDetail ecompany;   // 企业
  @Transient
  private PwEnterRoom eroom;   // 入驻场地
  @Transient
  private List<String> ids;    // 查询ID
  @Transient
  @JsonIgnore
  private PwEnterDetail[] pwEnterDetails;

	public PwEnter() {
		super();
	}

	public PwEnter(PwEnterDetail eteam, PwEnterDetail eproject, PwEnterDetail ecompany) {
    super();
    this.eteam = eteam;
    this.eproject = eproject;
    this.ecompany = ecompany;
  }

  public PwEnter(String id){
		super(id);
	}

	public ActYwApply getApply() {
    return apply;
  }

  public void setApply(ActYwApply apply) {
    this.apply = apply;
  }

  public Boolean getIsView() {
    if(isView == null){
      isView = true;
    }
    return isView;
  }

  public void setIsView(Boolean isView) {
    this.isView = isView;
  }

  public List<String> getIds() {
    return ids;
  }

  public void setIds(List<String> ids) {
    this.ids = ids;
  }

  public Boolean getIsAudited() {
    if((this.status == null) || (PwEnterStatus.PES_DSH.getKey()).equals(this.status)){
      isAudited = false;
    }else{
      isAudited = true;
    }
    return isAudited;
  }

  public void setIsAudited(Boolean isAudited) {
    this.isAudited = isAudited;
  }

  public PwEnterDetail getEteam() {
    return eteam;
  }

  public void setEteam(PwEnterDetail eteam) {
    this.eteam = eteam;
  }

  public String getIsTemp() {
    return isTemp;
  }

  public void setIsTemp(String isTemp) {
    this.isTemp = isTemp;
  }

  public PwEnterDetail getEproject() {
    return eproject;
  }

  public void setEproject(PwEnterDetail eproject) {
    this.eproject = eproject;
  }

  public PwEnterDetail getEcompany() {
    return ecompany;
  }

  public void setEcompany(PwEnterDetail ecompany) {
    this.ecompany = ecompany;
  }

  public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public User getApplicant() {
    return applicant;
  }

  public void setApplicant(User applicant) {
    this.applicant = applicant;
  }

  @Length(min=0, max=1, message="状态长度必须介于 0 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getPstatuss() {
	  if(StringUtil.isNotEmpty(this.pstatus)){
      this.pstatuss = Arrays.asList((this.pstatus).split(StringUtil.DOTH));
    }
    return pstatuss;
  }

  public String getPstatussql() {
	  if(StringUtil.isNotEmpty(this.pstatus)){
      this.pstatussql = StringUtil.sqlInByStr(this.pstatus);
    }
    return pstatussql;
  }

  public void setPstatussql(String pstatussql) {
    this.pstatussql = pstatussql;
  }

  public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

  public String getPstatus() {
    return pstatus;
  }

  public void setPstatus(String pstatus) {
    this.pstatus = pstatus;
  }

  public PwEnterRoom getEroom() {
    return eroom;
  }

  public void setEroom(PwEnterRoom eroom) {
    this.eroom = eroom;
  }

  public PwEnterDetail[] getPwEnterDetails() {
    if(this.pwEnterDetails == null){
      this.pwEnterDetails = new PwEnterDetail[3];
    }

    if(this.eteam != null){
      pwEnterDetails[0] = this.eteam;
    }

    if(this.eproject != null){
      pwEnterDetails[1] = this.eteam;
    }

    if(this.ecompany != null){
      pwEnterDetails[2] = this.ecompany;
    }

    return pwEnterDetails;
  }
}