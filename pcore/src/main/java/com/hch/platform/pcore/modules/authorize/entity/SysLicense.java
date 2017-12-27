package com.hch.platform.pcore.modules.authorize.entity;


import com.hch.platform.pcore.common.persistence.AbsDataEntity;

/**
 * 授权信息Entity
 * @author 9527
 * @version 2017-04-13
 */
public abstract class SysLicense extends AbsDataEntity<SysLicense> {

	private static final long serialVersionUID = 1L;
	private String license;		// 授权文件信息

	public SysLicense() {
		super();
	}

	public SysLicense(String id) {
		super(id);
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

}