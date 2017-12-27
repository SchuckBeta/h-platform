/**
 * 
 */
package com.hch.platform.pcore.modules.oa.entity;

import org.hibernate.validator.constraints.Length;

import com.hch.platform.pcore.common.persistence.DataEntity;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

import java.util.Date;

/**
 * 通知通告记录Entity

 * @version 2014-05-16
 */
public class OaNotifyRecord extends DataEntity<OaNotifyRecord> {
	
	private static final long serialVersionUID = 1L;
	private OaNotify oaNotify;		// 通知通告ID
	private AbsUser user;		// 接受人
	private String readFlag;		// 阅读标记（0：未读；1：已读）
	private Date readDate;		// 阅读时间
	private String operateFlag;  //操作标记（0：未操作；1：已操作）
	private String outTeam;//是否从团队中删除，1-删除，0-未删除（用于未加入团队的删除）
	
	
	public String getOutTeam() {
		return outTeam;
	}

	public void setOutTeam(String outTeam) {
		this.outTeam = outTeam;
	}

	public String getOperateFlag() {
		return operateFlag;
	}

	public void setOperateFlag(String operateFlag) {
		this.operateFlag = operateFlag;
	}

	public OaNotifyRecord() {
		super();
	}

	public OaNotifyRecord(String id) {
		super(id);
	}
	
	public OaNotifyRecord(OaNotify oaNotify) {
		this.oaNotify = oaNotify;
	}

	public OaNotify getOaNotify() {
		return oaNotify;
	}

	public void setOaNotify(OaNotify oaNotify) {
		this.oaNotify = oaNotify;
	}
	
	public AbsUser getUser() {
		return user;
	}

	public void setUser(AbsUser user) {
		this.user = user;
	}
	
	@Length(min=0, max=1, message="阅读标记（0：未读；1：已读）长度必须介于 0 和 1 之间")
	public String getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(String readFlag) {
		this.readFlag = readFlag;
	}
	
	public Date getReadDate() {
		return readDate;
	}

	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}
	
}