package com.oseasy.initiate.modules.course.service;

import java.util.Date;
import java.util.List;

import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.attachment.enums.FileStepEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.course.dao.CourseCategoryDao;
import com.oseasy.initiate.modules.course.dao.CourseTeacherDao;
import com.oseasy.initiate.modules.course.entity.CourseCategory;
import com.oseasy.initiate.modules.course.entity.CourseTeacher;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.course.entity.Course;
import com.oseasy.initiate.modules.course.dao.CourseDao;

/**
 * 课程主表Service.
 * @author 张正
 * @version 2017-06-28
 */
@Service
@Transactional(readOnly = true)
public class CourseService extends CrudService<CourseDao, Course> {

	@Autowired
	CourseCategoryDao courseCategoryDao;

	@Autowired
	CourseTeacherDao courseTeacherDao;


	@Autowired
	SysAttachmentService sysAttachmentService;

	public Course get(String id) {
		return super.get(id);
	}

	public List<Course> findList(Course course) {
		return super.findList(course);
	}

	public Page<Course> findPage(Page<Course> page, Course course) {
		return super.findPage(page, course);
	}

	@Transactional(readOnly = false)
	public void save(Course course) {
		//课程主表处理
		//更新发布时间
		if (StringUtil.equals(course.getPublishFlag(),"1")) {
			course.setPublishDate(new Date());
		}
		if (StringUtil.equals(course.getPublishFlag(),"0")) {
			course.setPublishDate(null);
		}
		super.save(course);
		//专业课程分类子表处理 先删除 再新增
		courseCategoryDao.deleteByCourseId(course.getId());
		List<String> categoryValueList = course.getCategoryValueList();
		if (categoryValueList!=null) {
			for(String value:categoryValueList) {
				CourseCategory category= new CourseCategory();
				category.setCourseId(course.getId());
				category.setValue(value);
				category.setLabel(DictUtils.getDictLabel(value,"0000000086",""));
				category.preInsert();
				courseCategoryDao.insert(category);
			}
		}
		//课程老师子表处理
		courseTeacherDao.deleteByCourseId(course.getId());
		List<CourseTeacher> teacherList=course.getTeacherList();
		if(teacherList!=null){
			for(CourseTeacher teacher:teacherList){
				teacher.setCourseId(course.getId());
				teacher.preInsert();
				courseTeacherDao.insert(teacher);
			}
		}

		//课件子表处理
//		courseAttachmentDao.deleteByCourseId(course.getId());
//		List<CourseAttachment> attachmentList=course.getAttachmentList();
//		if(attachmentList!=null){
//			for(CourseAttachment attachment:attachmentList){
//				attachment.setCourseId(course.getId());
//				attachment.preInsert();
//				courseAttachmentDao.insert(attachment);
//			}
//		}
		sysAttachmentService.saveByVo(course.getAttachMentEntity(),course.getId(), FileTypeEnum.S9, FileStepEnum.S900);




	}

	@Transactional(readOnly = false)
	public void delete(Course course) {
		super.delete(course);
	}
	//更新下载量
	@Transactional(readOnly = false)
	public void updateDownloads(Course course) {
		dao.updateDownloads(course);
	}

	//更新浏览量
	@Transactional(readOnly = false)
	public void updateViews(Course course) {
		dao.updateViews(course);
	}

	//查找所有能搜索的老师
	public List<CourseTeacher> findTeacherListForCourse() {
		return dao.findTeacherListForCourse();
	}

	//查找前台首页的课程 4个发布的按置顶，发布时间排序
	public List<Course> findFrontCourse(){
		return dao.findFrontCourse();
	}

	public List<Course> findRecommedList(Course course){
		return dao.findRecommedList(course);
	}

	public List<Course> findFrontList(Course course){
		return dao.findFrontList(course);
	}

	public Page<Course> findFrontPage(Page<Course> page, Course course) {
		course.setPage(page);
		page.setList(dao.findFrontList(course));
		return page;
	}


}