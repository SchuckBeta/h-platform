package com.oseasy.initiate.modules.impdata.entity;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * 导入数据信息表Entity
 * @author 9527
 * @version 2017-05-16
 */
public class ImpInfo extends DataEntity<ImpInfo> {
	
	private static final long serialVersionUID = 1L;
	private String impTpye;		// 导入数据的类型
	private String total;		// 总数
	private String success;		// 成功数
	private String fail;		// 失败数
	private String isComplete;		// 是否结束：0-未结束，1-结束
	private String msg;//存储文件名称等信息
	private String filename;//文件名称
	private String errmsg;//文件级别错误信息
	public ImpInfo() {
		super();
	}

	public ImpInfo(String id) {
		super(id);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Length(min=0, max=64, message="导入数据的类型长度必须介于 0 和 64 之间")
	public String getImpTpye() {
		return impTpye;
	}

	public void setImpTpye(String impTpye) {
		this.impTpye = impTpye;
	}
	
	@Length(min=0, max=20, message="总数长度必须介于 0 和 20 之间")
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
	
	@Length(min=0, max=20, message="成功数长度必须介于 0 和 20 之间")
	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}
	
	@Length(min=0, max=20, message="失败数长度必须介于 0 和 20 之间")
	public String getFail() {
		return fail;
	}

	public void setFail(String fail) {
		this.fail = fail;
	}
	
	@Length(min=0, max=2, message="是否结束：0-未结束，1-结束长度必须介于 0 和 2 之间")
	public String getIsComplete() {
		return isComplete;
	}

	public void setIsComplete(String isComplete) {
		this.isComplete = isComplete;
	}
	
}