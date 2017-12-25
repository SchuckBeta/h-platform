/**
 * 
 */
package com.oseasy.initiate.modules.oa.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.common.utils.Collections3;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.sys.entity.User;

/**
 * 通知通告Entity

 * @version 2014-05-16
 */
public class OaNotify extends DataEntity<OaNotify> {
	
	private static final long serialVersionUID = 1L;
	private String type;		// 类型
	private String title;		// 标题
	private String content;		// 类型
	private String files;		// 附件
	private String status;		// 状态 0草稿 1发布

	private String readNum;		// 已读
	private String unReadNum;	// 未读
	
	private boolean isSelf;		// 是否只查询自己的通知
	
	private String readFlag;	// 本人阅读状态
	
	private Date effectiveDate;//生效日期
	private Date endDate;//结束日期
	
	private String sendType;//发送类型 1：广播  2：定向
	
	private List<OaNotifyRecord> oaNotifyRecordList = Lists.newArrayList();//定向发送时保存接收人集合


	
	private List<OaNotifyRecord> oaNotifyRecordListBroadcast = Lists.newArrayList();//广播发送时保存接收人集合
	
	private String oaNotifyRecordIdsBroadCast;//广播时的所以接收人ids
	
	private String sId;//大赛id或项目id
	
	private String protype;
	
	private String userId;
	
	private String publishDate;
	
	private String source;//来源
	
	private String views;//浏览量
	private List<String> keywords;
	
	
	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getViews() {
		return views;
	}

	public void setViews(String views) {
		this.views = views;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProtype() {
		return protype;
	}

	public void setProtype(String protype) {
		this.protype = protype;
	}

	public String getsId() {
		return sId;
	}

	public void setsId(String sId) {
		this.sId = sId;
	}

	public OaNotify() {
		super();
	}

	public OaNotify(String id) {
		super(id);
	}

	@Length(min=0, max=200, message="标题长度必须介于 0 和 200 之间")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@Length(min=0, max=2, message="类型长度必须介于 0 和 1 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=0, max=1, message="状态长度必须介于 0 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=0, max=2000, message="附件长度必须介于 0 和 2000 之间")
	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReadNum() {
		return readNum;
	}

	public void setReadNum(String readNum) {
		this.readNum = readNum;
	}

	public String getUnReadNum() {
		return unReadNum;
	}

	public void setUnReadNum(String unReadNum) {
		this.unReadNum = unReadNum;
	}
	
	public List<OaNotifyRecord> getOaNotifyRecordList() {
		return oaNotifyRecordList;
	}

	public void setOaNotifyRecordList(List<OaNotifyRecord> oaNotifyRecordList) {
		this.oaNotifyRecordList = oaNotifyRecordList;
	}
	
	/**
	 * 获取通知发送记录用户ID
	 * @return
	 */
	public String getOaNotifyRecordIds() {
		return Collections3.extractToString(oaNotifyRecordList, "user.id", ",") ;
	}
	
	/**
	 * 设置通知发送记录用户ID
	 * @return
	 */
	public void setOaNotifyRecordIds(String oaNotifyRecord) {
		this.oaNotifyRecordList = Lists.newArrayList();
		for (String id : StringUtil.split(oaNotifyRecord, ",")) {
			OaNotifyRecord entity = new OaNotifyRecord();
			entity.setId(IdGen.uuid());
			entity.setOaNotify(this);
			entity.setUser(new User(id));
			entity.setReadFlag("0");
			this.oaNotifyRecordList.add(entity);
		}
	}

	
	
	
	public String getOaNotifyRecordIdsBroadCast() {
		return Collections3.extractToString(oaNotifyRecordListBroadcast, "user.id", ",");
	}


	public void setOaNotifyRecordIdsBroadCast(String oaNotifyRecordIdsBroadCast) {
		this.oaNotifyRecordListBroadcast  = Lists.newArrayList();
		for (String id : StringUtil.split(oaNotifyRecordIdsBroadCast, ",")) {
			OaNotifyRecord entity = new OaNotifyRecord();
			entity.setId(IdGen.uuid());
			entity.setOaNotify(this);
			entity.setUser(new User(id));
			entity.setReadFlag("0");
			this.oaNotifyRecordListBroadcast.add(entity);
		}
	}

	public List<OaNotifyRecord> getOaNotifyRecordListBroadcast() {
		return oaNotifyRecordListBroadcast;
	}

	public void setOaNotifyRecordListBroadcast(List<OaNotifyRecord> oaNotifyRecordListBroadcast) {
		this.oaNotifyRecordListBroadcast = oaNotifyRecordListBroadcast;
	}

	/**
	 * 获取通知发送记录用户Name
	 * @return
	 */
	public String getOaNotifyRecordNames() {
		return Collections3.extractToString(oaNotifyRecordList, "user.name", ",") ;
	}



	/**
	 * 设置通知发送记录用户Name
	 * @return
	 */
	public void setOaNotifyRecordNames(String oaNotifyRecord) {
		// 什么也不做
	}

	public boolean getIsSelf() {
		return isSelf;
	}

	public void setIsSelf(boolean isSelf) {
		this.isSelf = isSelf;
	}

	public String getReadFlag() {
		return readFlag;
	}

	public void setReadFlag(String readFlag) {
		this.readFlag = readFlag;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}