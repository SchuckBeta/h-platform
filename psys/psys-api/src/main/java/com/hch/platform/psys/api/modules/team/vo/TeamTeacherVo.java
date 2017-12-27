package com.hch.platform.pcore.modules.team.vo;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class TeamTeacherVo {
  private String name;    // name
  private String no;    // no
  private String orgName;    // org_name
  private String technicalTitle;    // technical_title
  private String domain;    // domain
  private String mobile;    // mobile
  private String education;    // education
  private String email;    // email
  private String userType;    // user_type
  private String teacherType;    // teacherType

  private String postTitle;    // postTitle
  private String ttv;    // ttv
  private String id;    // userId
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

  public String getTechnicalTitle() {
    return technicalTitle;
  }

  public void setTechnicalTitle(String technicalTitle) {
    this.technicalTitle = technicalTitle;
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

  public String getEducation() {
    return education;
  }

  public void setEducation(String education) {
    this.education = education;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUserType() {
    return userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public String getTeacherType() {
    return teacherType;
  }

  public void setTeacherType(String teacherType) {
    this.teacherType = teacherType;
  }

  public String getPostTitle() {
    return postTitle;
  }

  public void setPostTitle(String postTitle) {
    this.postTitle = postTitle;
  }

  public String getTtv() {
    return ttv;
  }

  public void setTtv(String ttv) {
    this.ttv = ttv;
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

  public static List<TeamTeacherVo> converts(List<Map<String, String>> ttMaps) {
    List<TeamTeacherVo> ttVos = Lists.newArrayList();
    for (Map<String, String> tt : ttMaps) {
      TeamTeacherVo ttVo = new TeamTeacherVo();
      ttVo.setId(tt.get("userId"));
      ttVo.setUserId(tt.get("userId"));
      ttVo.setNo(tt.get("no"));
      ttVo.setName(tt.get("name"));
      ttVo.setOrgName(tt.get("org_name"));
      ttVo.setTechnicalTitle(tt.get("technical_title"));
      ttVo.setDomain(tt.get("domain"));
      ttVo.setMobile(tt.get("mobile"));
      ttVo.setEducation(tt.get("education"));
      ttVo.setEmail(tt.get("email"));
      ttVo.setUserType(tt.get("user_type"));
      ttVo.setTeacherType(tt.get("teacherType"));
      ttVo.setPostTitle(tt.get("postTitle"));
      ttVo.setTtv(tt.get("ttv"));
      ttVos.add(ttVo);
    }
    return ttVos;
  }
}
