/**
 *
 */
package com.hch.platform.pcore.common.persistence;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hch.platform.putil.common.utils.IdGen;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

/**
 * 数据Entity类

 * @version 2014-05-16
 */
public abstract class AbsDataEntity<T> extends AbsBaseEntity<T> implements Serializable{

	private static final long serialVersionUID = 1L;

	protected String remarks;	// 备注
	protected AbsUser createBy;	// 创建者
	protected Date createDate;	// 创建日期
	protected AbsUser updateBy;	// 更新者
	protected Date updateDate;	// 更新日期
	protected String delFlag; 	// 删除标记（0：正常；1：删除；2：审核）

	public AbsDataEntity() {
		super();
		this.delFlag = DEL_FLAG_NORMAL;
	}

	public AbsDataEntity(String id) {
		super(id);
	}

	/**
	 * 插入之前执行方法，需要手动调用
	 */
	@Override
	public abstract void preInsert();

	/**
	 * 更新之前执行方法，需要手动调用
	 */
	@Override
	public abstract void preUpdate();

	@Length(min=0, max=255)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@JsonIgnore
	public AbsUser getCreateBy() {
		return createBy;
	}

	public void setCreateBy(AbsUser createBy) {
		this.createBy = createBy;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@JsonIgnore
	public AbsUser getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(AbsUser updateBy) {
		this.updateBy = updateBy;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@JsonIgnore
	@Length(min=1, max=1)
	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

}
