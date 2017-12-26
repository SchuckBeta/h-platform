package com.hch.platform.pcore.modules.team.vo;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hch.platform.putil.common.utils.StringUtil;

public class TeamStudentVo {
  private String name;    // name
  private String no;    // no
  private String orgName;    // org_name
  private String professional;    // professional
  private String domain;    // domain
  private String mobile;    // mobile
  private String instudy;    // instudy
  private String userType;    // user_type
  private String weightVal;    // weightVal
  private String id;    // id
  private String userId;    // userId
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getNo() {
    return no;
  }
  public void setNo(String no) {
    this.no = no;
  }
  public String getOrgName() {
    return orgName;
  }
  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }
  public String getProfessional() {
    return professional;
  }
  public void setProfessional(String professional) {
    this.professional = professional;
  }
  public String getDomain() {
    return domain;
  }
  public void setDomain(String domain) {
    this.domain = domain;
  }
  public String getMobile() {
    return mobile;
  }
  public void setMobile(String mobile) {
    this.mobile = mobile;
  }
  public String getInstudy() {
    return instudy;
  }
  public void setInstudy(String instudy) {
    this.instudy = instudy;
  }
  public String getUserType() {
    return userType;
  }
  public void setUserType(String userType) {
    this.userType = userType;
  }
  public String getWeightVal() {
    return weightVal;
  }
  public void setWeightVal(String weightVal) {
    this.weightVal = weightVal;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public static List<TeamStudentVo> converts(List<Map<String, String>> tsMaps) {
    List<TeamStudentVo> tsVos = Lists.newArrayList();
    for (Map<String, String> ts : tsMaps) {
      TeamStudentVo tsVo = new TeamStudentVo();
      String id = ts.get("id");
      String no = ts.get("no");
      String name = ts.get("name");
      String orgName = ts.get("org_name");
      String professional = ts.get("professional");
      String domain = ts.get("domain");
      String mobile = ts.get("mobile");
      String instudy = ts.get("instudy");
      String userType = ts.get("user_type");
//      String weightVal = ts.get("weightVal");
      String userId = ts.get("userId");

      if(StringUtil.isNotEmpty(id)){
        tsVo.setId(id);
      }
      if(StringUtil.isNotEmpty(no)){
        tsVo.setNo(no);
      }
      if(StringUtil.isNotEmpty(name)){
        tsVo.setName(name);
      }
      if(StringUtil.isNotEmpty(orgName)){
        tsVo.setOrgName(orgName);
      }
      if(StringUtil.isNotEmpty(professional)){
        tsVo.setProfessional(professional);
      }
      if(StringUtil.isNotEmpty(domain)){
        tsVo.setDomain(domain);
      }
      if(StringUtil.isNotEmpty(mobile)){
        tsVo.setMobile(mobile);
      }
      if(StringUtil.isNotEmpty(instudy)){
        tsVo.setInstudy(instudy);
      }
      if(StringUtil.isNotEmpty(userType)){
        tsVo.setUserType(userType);
      }
//      if(StringUtil.isNotEmpty(weightVal)){
//        tsVo.setWeightVal(weightVal);
//      }
      if(StringUtil.isNotEmpty(userId)){
        tsVo.setUserId(userId);
      }
      tsVos.add(tsVo);
    }
    return tsVos;
  }
}
