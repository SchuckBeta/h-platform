package com.oseasy.initiate.modules.sys.entity;

import java.util.Date;

public class gContestUndergo {
    private String type;//大赛类型
    private Date createDate;//创建时间
    private String PName;//项目名称
    private String award;//获奖情况
    private String sponsor;//担任角色
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
	public String getPName() {
		return PName;
	}
	public void setPName(String pName) {
		PName = pName;
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
