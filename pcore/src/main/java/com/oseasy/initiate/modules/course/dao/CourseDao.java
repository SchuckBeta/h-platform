package com.hch.platform.pcore.modules.course.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.course.entity.Course;
import com.hch.platform.pcore.modules.course.entity.CourseTeacher;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 课程主表DAO接口.
 * @author 张正
 * @version 2017-06-28
 */
@MyBatisDao
public interface CourseDao extends CrudDao<Course> {
    //更新下载量
    public void updateDownloads(Course course);
    //更新浏览量
    public void updateViews(Course course);
    //查找所有能搜索的老师
    public List<CourseTeacher> findTeacherListForCourse();

    public List<Course> findFrontCourse();

    public List<Course> findRecommedList(Course course);

    public List<Course> findFrontList(Course course);
	public void updateComments(@Param("param") Map<String,Integer> param);
    public void updateViewsPlus(@Param("param") Map<String,Integer> param);
    public void updateLikes(@Param("param") Map<String,Integer> param);

}