package com.oseasy.initiate.modules.impdata.entity;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * 导入数据错误信息表Entity
 * @author 9527
 * @version 2017-05-16
 */
public class ImpInfoErrmsg extends DataEntity<ImpInfoErrmsg> {
	
	private static final long serialVersionUID = 1L;
	private String impId;		// 导入信息表主键
	private String dataId;		// 导入错误数据表主键
	private String colname;		// 错误字段名称
	private String errmsg;		// 错误信息
	
	public ImpInfoErrmsg() {
		super();
	}

	public ImpInfoErrmsg(String id) {
		super(id);
	}

	@Length(min=0, max=64, message="导入信息表主键长度必须介于 0 和 64 之间")
	public String getImpId() {
		return impId;
	}

	public void setImpId(String impId) {
		this.impId = impId;
	}
	
	@Length(min=0, max=64, message="导入错误数据表主键长度必须介于 0 和 64 之间")
	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	
	@Length(min=0, max=128, message="错误字段名称长度必须介于 0 和 128 之间")
	public String getColname() {
		return colname;
	}

	public void setColname(String colname) {
		this.colname = colname;
	}
	
	@Length(min=0, max=512, message="错误信息长度必须介于 0 和 512 之间")
	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	
}