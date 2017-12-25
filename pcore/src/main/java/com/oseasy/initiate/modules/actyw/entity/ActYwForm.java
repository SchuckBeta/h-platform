package com.oseasy.initiate.modules.actyw.entity;

import org.hibernate.validator.constraints.Length;
import com.oseasy.initiate.modules.sys.entity.Office;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * 项目流程表单Entity.
 *
 * @author chenhao
 * @version 2017-05-23
 */
public class ActYwForm extends DataEntity<ActYwForm> {

  private static final long serialVersionUID = 1L;
  private String name; // 业务模块（1-立项审核，2-中期检查，3-结项审核）
  private String proType;//项目类别
  private String type; // 业务模块（申报，打分）
  private String model; // 模式：（0-文件模板，1-地址模板，2-html模板）
  private String params; // 表单模板文件参数
  private String path; // 表单模板文件路径
  private String content; // 表单模板内容
  private Office office; // 节点所属机构:1、默认（系统全局）；

  public ActYwForm() {
    super();
  }

  public ActYwForm(String id) {
    super(id);
  }

  public String getProType() {
    return proType;
  }

  public void setProType(String proType) {
    this.proType = proType;
  }

  @Length(min = 0, max = 255, message = "业务模块（1-立项审核，2-中期检查，3-结项审核）长度必须介于 0 和 255 之间")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Length(min = 0, max = 1, message = "模式：（0-文件模板，1-地址模板，2-html模板）长度必须介于 0 和 1 之间")
  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  @Length(min = 0, max = 64, message = "表单模板文件参数长度必须介于 0 和 64 之间")
  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  @Length(min = 0, max = 64, message = "表单模板文件路径长度必须介于 0 和 64 之间")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return 获取content属性值.
   */
  public String getContent() {
    return content;
  }

  /**
   * 设置content属性值.
   */
  public void setContent(String content) {
    this.content = content;
  }

  public Office getOffice() {
    return office;
  }

  public void setOffice(Office office) {
    this.office = office;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}