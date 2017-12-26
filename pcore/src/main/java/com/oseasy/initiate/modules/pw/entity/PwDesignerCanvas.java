package com.oseasy.initiate.modules.pw.entity;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * 画布表Entity.
 * @author zy
 * @version 2017-12-18
 */
public class PwDesignerCanvas extends DataEntity<PwDesignerCanvas> {

	private static final long serialVersionUID = 1L;
	private String floorId;		// 楼层ID
	private String papersize;		// 画布大小
	private String pwsize;		// 画布调整尺寸
	private String picUrl;		// 画布图片url

	public PwDesignerCanvas() {
		super();
	}

	public PwDesignerCanvas(String id){
		super(id);
	}

	@Length(min=1, max=64, message="楼层ID长度必须介于 1 和 64 之间")
	public String getFloorId() {
		return floorId;
	}

	public void setFloorId(String floorId) {
		this.floorId = floorId;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	@Length(min=1, max=64, message="画布大小长度必须介于 1 和 64 之间")
	public String getPapersize() {
		return papersize;
	}

	public void setPapersize(String papersize) {
		this.papersize = papersize;
	}

	@Length(min=0, max=64, message="画布调整尺寸长度必须介于 0 和 64 之间")
	public String getPwsize() {
		return pwsize;
	}

	public void setPwsize(String pwsize) {
		this.pwsize = pwsize;
	}

}