package com.oseasy.initiate.modules.sys.entity;

import com.oseasy.initiate.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * 系统编号Entity
 * @author chenh
 * @version 2017-05-05
 */
public class SysNo extends DataEntity<SysNo> {
	
	private static final long serialVersionUID = 1L;
	private Office office;		// 机构ID
	private String officeNo;		// 机构编号
	private String orderNo;		// 订单编号
	private String siteNo;		// 站点编号
	
	public SysNo() {
		super();
	}

	public SysNo(String id) {
		super(id);
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@Length(min=0, max=11, message="机构编号长度必须介于 0 和 11 之间")
	public String getOfficeNo() {
		return officeNo;
	}

	public void setOfficeNo(String officeNo) {
		this.officeNo = officeNo;
	}
	
	@Length(min=0, max=11, message="订单编号长度必须介于 0 和 11 之间")
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	@Length(min=0, max=11, message="站点编号长度必须介于 0 和 11 之间")
	public String getSiteNo() {
		return siteNo;
	}

	public void setSiteNo(String siteNo) {
		this.siteNo = siteNo;
	}
	
}