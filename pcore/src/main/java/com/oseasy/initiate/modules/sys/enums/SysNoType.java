package com.oseasy.initiate.modules.sys.enums;

public enum SysNoType {
	NO_OFFICE("officeNo", "机构编号"),
	NO_ORDER("orderNo", "订单编号"),
	NO_SITE("siteNo", "站点编号");

	private String type;
	private String remarks;
	private SysNoType(String type, String remarks) {
		this.type = type;
		this.remarks = remarks;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public static SysNoType getByType(String type) {
		SysNoType[] entitys = SysNoType.values();
		for (SysNoType entity : entitys) {
			if ((type).equals(entity.getType())) {
				return entity;
			}
		}
		return null;
	}
}