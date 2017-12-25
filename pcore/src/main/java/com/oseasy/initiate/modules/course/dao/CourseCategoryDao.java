package com.oseasy.initiate.modules.course.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.course.entity.CourseCategory;

import java.util.List;

/**
 * 课程分类DAO接口.
 * @author zhangzheng
 * @version 2017-06-28
 */
@MyBatisDao
public interface CourseCategoryDao extends CrudDao<CourseCategory> {
    public List<CourseCategory> getByCourseId(String courseId);

    public void deleteByCourseId(String courseId);
}