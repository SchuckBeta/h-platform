package com.oseasy.initiate.modules.project.vo;

import java.util.Date;

/**
 * Created by zhangzheng on 2017/4/13.
 * 项目经历
 */
public class ProjectExpVo {
    private String id; //项目id
    private String name; //项目名称
    private Date startDate; //项目开始时间
    private Date endDate; //项目结束时间
    private String roleName; //担任角色
    private String level;  //项目评级
    private String result; //项目结果

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
