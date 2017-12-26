package com.oseasy.initiate.modules.pw.vo;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.pw.entity.PwCompany;
import com.oseasy.initiate.modules.pw.entity.PwEnter;

/**
 * 预定日历Vo.
 * @author chenhao
 *
 */
public class PwEnterVo implements Serializable{
  private static final long serialVersionUID = 1L;
  private String eid;//入驻编号信息
  private Boolean isSave;//是否提交
  private Boolean hasTeam;//团队是否入驻
  private Boolean hasCompany;//企业是否入驻
  private Boolean hasProject;//项目是否入驻
  private String projectRemarks;//入驻项目说明
  private String teamRemarks;//入驻团队说明
  private String pwCompanyRemarks;//入驻企业说明
  private String projectPteam;//是否使用项目团队
  private List<File> tfiles;//团队附件
  private List<File> cfiles;//企业附件
  private List<File> pfiles;//项目附件
  private String projectId;//入驻编号信息
  private String teamId;//入驻编号信息
  private PwCompany pwCompany;

  public PwEnterVo() {
    super();
  }

  public PwEnterVo(String eid) {
    super();
    this.eid = eid;
  }

  public Boolean getIsSave() {
    return isSave;
  }

  public void setIsSave(Boolean isSave) {
    this.isSave = isSave;
  }

  public String getProjectPteam() {
    return projectPteam;
  }

  public void setProjectPteam(String projectPteam) {
    this.projectPteam = projectPteam;
  }

  public String getEid() {
    return eid;
  }

  public List<File> getTfiles() {
    return tfiles;
  }

  public void setTfiles(List<File> tfiles) {
    this.tfiles = tfiles;
  }

  public List<File> getCfiles() {
    return cfiles;
  }

  public void setCfiles(List<File> cfiles) {
    this.cfiles = cfiles;
  }

  public List<File> getPfiles() {
    return pfiles;
  }

  public void setPfiles(List<File> pfiles) {
    this.pfiles = pfiles;
  }

  public void setEid(String eid) {
    this.eid = eid;
  }
  public String getProjectId() {
    return projectId;
  }
  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }
  public String getTeamId() {
    return teamId;
  }
  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }
  public Boolean getHasTeam() {
    return hasTeam;
  }
  public void setHasTeam(Boolean hasTeam) {
    this.hasTeam = hasTeam;
  }
  public Boolean getHasCompany() {
    return hasCompany;
  }
  public void setHasCompany(Boolean hasCompany) {
    this.hasCompany = hasCompany;
  }
  public Boolean getHasProject() {
    return hasProject;
  }
  public void setHasProject(Boolean hasProject) {
    this.hasProject = hasProject;
  }
  public PwCompany getPwCompany() {
    return pwCompany;
  }
  public void setPwCompany(PwCompany pwCompany) {
    this.pwCompany = pwCompany;
  }

  public String getProjectRemarks() {
    return projectRemarks;
  }

  public void setProjectRemarks(String projectRemarks) {
    this.projectRemarks = projectRemarks;
  }

  public String getTeamRemarks() {
    return teamRemarks;
  }

  public void setTeamRemarks(String teamRemarks) {
    this.teamRemarks = teamRemarks;
  }

  public String getPwCompanyRemarks() {
    return pwCompanyRemarks;
  }

  public void setPwCompanyRemarks(String pwCompanyRemarks) {
    this.pwCompanyRemarks = pwCompanyRemarks;
  }

  public static PwEnterVo convert(PwEnter pwEnter) {
    PwEnterVo peVo = new PwEnterVo(pwEnter.getId());
    if((pwEnter.getEcompany() != null) && (pwEnter.getEcompany().getPwCompany() != null)){
      peVo.setHasCompany(true);
      peVo.setPwCompany(pwEnter.getEcompany().getPwCompany());
      peVo.setPwCompanyRemarks(pwEnter.getEcompany().getRemarks());
    }else{
      peVo.setHasCompany(false);
    }

    if((pwEnter.getEproject() != null) && (pwEnter.getEproject().getProject() != null) && StringUtil.isNotEmpty(pwEnter.getEproject().getProject().getId())){
      peVo.setHasProject(true);
      peVo.setProjectId(pwEnter.getEproject().getProject().getId());
      peVo.setProjectRemarks(pwEnter.getEproject().getRemarks());
      peVo.setProjectPteam(pwEnter.getEproject().getPteam());
    }else{
      peVo.setHasProject(false);
    }

    if((pwEnter.getEteam() != null) && (pwEnter.getEteam().getTeam() != null) && StringUtil.isNotEmpty(pwEnter.getEteam().getTeam().getId())){
      peVo.setHasTeam(true);
      peVo.setTeamRemarks(pwEnter.getEteam().getRemarks());
    }else{
      peVo.setHasTeam(false);
    }
    return peVo;
  }
}
