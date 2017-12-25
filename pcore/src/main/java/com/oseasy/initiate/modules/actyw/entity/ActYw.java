package com.oseasy.initiate.modules.actyw.entity;

import java.sql.Timestamp;

import com.oseasy.initiate.modules.proproject.entity.ProProject;
import org.hibernate.validator.constraints.Length;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;


/**
 * 项目流程关联Entity.
 * @author chenhao
 * @version 2017-05-23
 */
public class ActYw extends DataEntity<ActYw> {

  private static final long serialVersionUID = 1L;
  private String relId; // 项目ID
  private String groupId; // 流程组ID
  private String flowId; // 流程部署ID
  private Boolean isDeploy; // 是否发布
  private ActYwGroup group; // 流程组
  private ProjectDeclare projectDeclare; // 项目
  private ProProject proProject;
/*  private Timestamp beginDate;   // 申报开始时间
  private Timestamp endDate;   // 申报结束时间*/

  public ActYw() {
    super();
  }

  public ActYw(String id) {
    super(id);
  }

  @Length(min = 1, max = 64, message = "项目长度必须介于 1 和 64 之间")
  public String getRelId() {
    return relId;
  }

  public void setRelId(String relId) {
    this.relId = relId;
  }

  @Length(min = 1, max = 64, message = "流程组长度必须介于 1 和 64 之间")
  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  /**
   * @return 获取isDeploy属性值.
   */
  public Boolean getIsDeploy() {
    return isDeploy;
  }

  /**
   * 设置isDeploy属性值.
   */
  public void setIsDeploy(Boolean isDeploy) {
    this.isDeploy = isDeploy;
  }

  /**
   * @return 获取projectDeclare属性值.
   */
  public ProjectDeclare getProjectDeclare() {
    return projectDeclare;
  }

  /**
   * 设置projectDeclare属性值.
   */
  public void setProjectDeclare(ProjectDeclare projectDeclare) {
    this.projectDeclare = projectDeclare;
  }

  /**
   * @return 获取group属性值.
   */
  public ActYwGroup getGroup() {
    return group;
  }

  /**
   * 设置group属性值.
   */
  public void setGroup(ActYwGroup group) {
    this.group = group;
  }

  public ProProject getProProject() {
    return proProject;
  }

  public void setProProject(ProProject proProject) {
    this.proProject = proProject;
  }

  /**
   * @return 获取beginDate属性值.
   */
 /* @Transactional
  public Timestamp getBeginDate() {
    if (projectDeclare != null && this.beginDate == null) {
      if (projectDeclare.getPlanStartDate() != null) {
        this.beginDate = new Timestamp(projectDeclare.getPlanStartDate().getTime());
      }
    }
    return beginDate;
  }*/

  /**
   * 设置beginDate属性值.
   */
  /*public void setBeginDate(Timestamp beginDate) {
    this.beginDate = beginDate;
  }*/

  /**
   * @return 获取endDate属性值.
   */
/*  @Transactional
  public Timestamp getEndDate() {
    if (projectDeclare != null && this.endDate == null) {
      if (projectDeclare.getPlanEndDate() != null) {
        this.endDate = new Timestamp(projectDeclare.getPlanEndDate().getTime());
      }
    }
    return endDate;
  }*/

  /**
   * 设置endDate属性值.
   */
  /*public void setEndDate(Timestamp endDate) {
    this.endDate = endDate;
  }*/

  public String getFlowId() {
    return flowId;
  }

  public void setFlowId(String flowId) {
    this.flowId = flowId;
  }
}