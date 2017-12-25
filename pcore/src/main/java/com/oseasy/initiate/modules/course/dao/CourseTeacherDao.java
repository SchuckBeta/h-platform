package com.oseasy.initiate.modules.course.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.course.entity.CourseTeacher;

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