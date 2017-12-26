package com.oseasy.initiate.modules.sys.entity;

import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class GContestUndergo {
	private String id;//id
    private String type;//大赛类型
    private Date createDate;//创建时间
    private String pName;//项目名称
    private String award;//获奖情况
    private String sponsor;//担任角色
    private String finish;//0-进行中，1已结束
    private String leaderId;
    private String userType;
	private String level;//大赛类别 competition_net_type
	private List<User> userList = Lists.newArrayList();

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

	public String getAward() {
		return award;
	}
	public void setAward(String award) {
		this.award = award;
	}
	public String getSponsor() {
		return sponsor;
	}
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}
	
}
