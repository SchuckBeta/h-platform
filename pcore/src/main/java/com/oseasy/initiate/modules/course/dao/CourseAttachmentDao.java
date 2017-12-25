package com.oseasy.initiate.modules.course.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.course.entity.CourseAttachment;

import java.util.List;

/**
 * 课程课件DAO接口.
 * @author zhangzheng
 * @version 2017-06-28
 */
@MyBatisDao
public interface CourseAttachmentDao extends CrudDao<CourseAttachment> {
    public List<CourseAttachment> getByCourseId(String courseId);

    public void deleteByCourseId(String courseId);
}