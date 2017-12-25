package com.oseasy.initiate.modules.course.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.course.entity.CourseTeacher;
import com.oseasy.initiate.modules.course.dao.CourseTeacherDao;

/**
 * 课程教师Service.
 * @author zhangzheng
 * @version 2017-06-28
 */
@Service
@Transactional(readOnly = true)
public class CourseTeacherService extends CrudService<CourseTeacherDao, CourseTeacher> {

	public CourseTeacher get(String id) {
		return super.get(id);
	}

	public List<CourseTeacher> findList(CourseTeacher courseTeacher) {
		return super.findList(courseTeacher);
	}

	public Page<CourseTeacher> findPage(Page<CourseTeacher> page, CourseTeacher courseTeacher) {
		return super.findPage(page, courseTeacher);
	}

	@Transactional(readOnly = false)
	public void save(CourseTeacher courseTeacher) {
		super.save(courseTeacher);
	}

	@Transactional(readOnly = false)
	public void delete(CourseTeacher courseTeacher) {
		super.delete(courseTeacher);
	}

	public List<CourseTeacher> getByCourseId(String courseId) {
		return dao.getByCourseId(courseId);
	}

}