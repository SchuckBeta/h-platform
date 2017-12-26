package com.hch.platform.pcore.modules.course.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.course.entity.CourseTeacher;

import java.util.List;

/**
 * 课程教师DAO接口.
 * @author zhangzheng
 * @version 2017-06-28
 */
@MyBatisDao
public interface CourseTeacherDao extends CrudDao<CourseTeacher> {

    public List<CourseTeacher> getByCourseId(String courseId);

    public void deleteByCourseId(String courseId);

}