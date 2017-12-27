package com.hch.platform.pcore.modules.project.vo;

import com.google.common.collect.Lists;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangzheng on 2017/4/13.
 * 项目经历
 */
public class ProjectExpVo {
	private static final long serialVersionUID = 1L;
    private String id; //项目id
    private String name; //项目名称
    private Date startDate; //项目开始时间
    private Date endDate; //项目结束时间
    private String roleName; //担任角色
    private String level;  //项目评级
    private String result; //项目结果
    private String proName; //项目类别
    private String finish;//0-进行中，1已结束
    private String leaderId;
    private String userType;

	private List<AbsUser> userList = Lists.newArrayList();;

	public List<AbsUser> getUserList() {
		return userList;
	}

	public void setUserList(List<AbsUser> userList) {
		this.userList = userList;
	}

	public String getLeaderId() {
		return leaderId;
	}
	public void setLeaderId(String leaderId) {
		this.leaderId = leaderId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getFinish() {
		return finish;
	}
	public void setFinish(String finish) {
		this.finish = finish;
	}
    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

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
