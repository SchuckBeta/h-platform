package com.oseasy.initiate.modules.attachment.entity;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * 附件信息表Entity
 * @author zy
 * @version 2017-03-23
 */
public class SysAttachment extends DataEntity<SysAttachment> {
	
	private static final long serialVersionUID = 1L;
	private String type;		// 类型：0项目，1 项目通告，2 大赛，3大赛通告
	private String fileStep;		// 标识文件是中期检查报告或者阶段报告等,枚举
	private String uid;		// 对应表的id：如项目表ID
	private String url;		// url
	private String name;		// 名称
	private String size;		// 附件大小
	private String suffix;		// 后缀
	
	public SysAttachment() {
		super();
	}

	public SysAttachment(String id) {
		super(id);
	}

	@Length(min=1, max=2, message="类型：0项目，1 项目通告，2 大赛，3大赛通告长度必须介于 1 和 2 之间")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Length(min=0, max=64, message="标识文件是中期检查报告或者阶段报告等,枚举长度必须介于 0 和 64 之间")
	public String getFileStep() {
		return fileStep;
	}

	public void setFileStep(String fileStep) {
		this.fileStep = fileStep;
	}
	
	@Length(min=1, max=64, message="对应表的id：如项目表ID长度必须介于 1 和 64 之间")
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Length(min=0, max=128, message="url长度必须介于 0 和 128 之间")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Length(min=1, max=128, message="名称长度必须介于 1 和 128 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=32, message="附件大小长度必须介于 0 和 32 之间")
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	@Length(min=1, max=12, message="后缀长度必须介于 1 和 12 之间")
	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
}