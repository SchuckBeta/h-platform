package com.oseasy.initiate.modules.course.entity;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;

/**
 * 课程课件Entity.
 * @author zhangzheng
 * @version 2017-06-28
 */
public class CourseAttachment extends DataEntity<CourseAttachment> {

	private static final long serialVersionUID = 1L;
	private String courseId;		// 课程id
	private String url;		// 课件地址
	private String name;		// 课件名称
	private String size;		// 大小
	private String suffix;		// 后缀

	public CourseAttachment() {
		super();
	}

	public CourseAttachment(String id) {
		super(id);
	}

	@Length(min=0, max=64, message="课程id长度必须介于 0 和 64 之间")
	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	@Length(min=0, max=1024, message="课件地址长度必须介于 0 和 1024 之间")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Length(min=0, max=128, message="课件名称长度必须介于 0 和 128 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min=0, max=11, message="大小长度必须介于 0 和 11 之间")
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	@Length(min=0, max=12, message="后缀长度必须介于 0 和 12 之间")
	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

}