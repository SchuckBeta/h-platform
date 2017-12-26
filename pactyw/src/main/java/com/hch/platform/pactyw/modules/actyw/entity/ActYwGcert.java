package com.oseasy.initiate.modules.actyw.entity;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * 业务节点证书Entity.
 * @author chenh
 * @version 2017-11-09
 */
public class ActYwGcert extends DataEntity<ActYwGcert> {

	private static final long serialVersionUID = 1L;
	private String ywId;		// 项目流程id
	private String gnodeId;		// 节点id

	public ActYwGcert() {
		super();
	}

	public ActYwGcert(String id){
		super(id);
	}

	@Length(min=0, max=64, message="项目流程id长度必须介于 0 和 64 之间")
	public String getYwId() {
		return ywId;
	}

	public void setYwId(String ywId) {
		this.ywId = ywId;
	}

	@Length(min=0, max=64, message="节点id长度必须介于 0 和 64 之间")
	public String getGnodeId() {
		return gnodeId;
	}

	public void setGnodeId(String gnodeId) {
		this.gnodeId = gnodeId;
	}

}