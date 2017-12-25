/**
 * 
 */
package com.oseasy.initiate.modules.oa.entity;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.modules.sys.entity.User;

import java.util.Date;

/**
 * 通知通告记录Entity

 * @version 2014-05-16
 */
public class OaNotifyRecord extends DataEntity<OaNotifyRecord> {
	
	private static final long serialVersionUID = 1L;
	private OaNotify oaNotify;		// 通知通告ID
	private User user;		// 接受人
	private String readFlag;		// 阅读标记（0：未读；1：已读）
	private Date readDate;		// 阅读时间
	private String operateFlag;  //操作标记（0：未操作；1：已操作）

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
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
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