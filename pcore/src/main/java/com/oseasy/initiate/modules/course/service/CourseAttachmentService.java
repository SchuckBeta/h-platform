package com.oseasy.initiate.modules.course.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.course.entity.CourseAttachment;
import com.oseasy.initiate.modules.course.dao.CourseAttachmentDao;

/**
 * 课程课件Service.
 * @author zhangzheng
 * @version 2017-06-28
 */
@Service
@Transactional(readOnly = true)
public class CourseAttachmentService extends CrudService<CourseAttachmentDao, CourseAttachment> {

	public CourseAttachment get(String id) {
		return super.get(id);
	}

	public List<CourseAttachment> findList(CourseAttachment courseAttachment) {
		return super.findList(courseAttachment);
	}

	public Page<CourseAttachment> findPage(Page<CourseAttachment> page, CourseAttachment courseAttachment) {
		return super.findPage(page, courseAttachment);
	}

	@Transactional(readOnly = false)
	public void save(CourseAttachment courseAttachment) {
		super.save(courseAttachment);
	}

	@Transactional(readOnly = false)
	public void delete(CourseAttachment courseAttachment) {
		super.delete(courseAttachment);
	}

	public List<CourseAttachment>  getByCourseId(String courseId){
		return dao.getByCourseId(courseId);
	}

}