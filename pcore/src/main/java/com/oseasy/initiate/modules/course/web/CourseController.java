package com.oseasy.initiate.modules.course.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import com.oseasy.initiate.modules.course.entity.CourseAttachment;
import com.oseasy.initiate.modules.course.entity.CourseCategory;
import com.oseasy.initiate.modules.course.entity.CourseTeacher;
import com.oseasy.initiate.modules.course.service.CourseAttachmentService;
import com.oseasy.initiate.modules.course.service.CourseCategoryService;
import com.oseasy.initiate.modules.course.service.CourseTeacherService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.course.entity.Course;
import com.oseasy.initiate.modules.course.service.CourseService;

import java.util.List;

/**
 * 课程主表Controller.
 * @author 张正
 * @version 2017-06-28
 */
@Controller
@RequestMapping(value = "${adminPath}/course")
public class CourseController extends BaseController {

	@Autowired
	private CourseService courseService;
	@Autowired
	CourseCategoryService courseCategoryService;
	@Autowired
	CourseTeacherService courseTeacherService;
	@Autowired
	CourseAttachmentService courseAttachmentService;



	@ModelAttribute
	public Course get(@RequestParam(required=false) String id) {
		Course entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = courseService.get(id);
		}
		if (entity == null){
			entity = new Course();
		}
		return entity;
	}

	@RequestMapping(value = {"list", ""})
	public String list(Course course, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Course> page = courseService.findPage(new Page<Course>(request, response), course);
		//专业课程分类处理
		for(Course courseItem:page.getList()){
			List<CourseCategory>  categoryList =courseCategoryService.getByCourseId(courseItem.getId());
			courseItem.setCategoryList(categoryList);
			List<CourseTeacher> teacherList =  courseTeacherService.getByCourseId(courseItem.getId());  //查找授课老师
			courseItem.setTeacherList(teacherList);
		}

		model.addAttribute("page", page);

		return "modules/course/courseList";
	}


	@RequestMapping(value = "form")
	public String form(Course course, Model model) {
		//查找所有老师
		List<CourseTeacher> teachers= courseService.findTeacherListForCourse();
		for (CourseTeacher teacher:teachers){
			if(teacher.getPostName()==null){
				teacher.setPostName("");
			}
			if(teacher.getCollegeName()==null){
				teacher.setCollegeName("");
			}
		}
		model.addAttribute("teachers", teachers);
		//查找课程专业分类 授课老师  课件
		if(StringUtil.isNotBlank(course.getId())){
			List<CourseCategory> categoryList = courseCategoryService.getByCourseId(course.getId());  //查找课程专业分类
			course.setCategoryList(categoryList);
			List<CourseTeacher> teacherList =  courseTeacherService.getByCourseId(course.getId());  //查找授课老师
			course.setTeacherList(teacherList);
			List<CourseAttachment> attachmentList = courseAttachmentService.getByCourseId(course.getId()); //查找课件
			course.setAttachmentList(attachmentList);
		}

		model.addAttribute("course", course);
		return "modules/course/courseForm";
	}

	@RequestMapping(value = "save")
	public String save(Course course, Model model, RedirectAttributes redirectAttributes) {
		courseService.save(course);
		addMessage(redirectAttributes, "保存课程成功");
		return "redirect:"+Global.getAdminPath()+"/course/?repage";
	}

	@RequestMapping(value = "saveJson")
	@ResponseBody
	public boolean saveJson(Course course){
		courseService.save(course);
		return true;
	}

	@RequestMapping(value = "delete")
	public String delete(Course course, RedirectAttributes redirectAttributes) {
		courseService.delete(course);
		addMessage(redirectAttributes, "删除课程成功");
		return "redirect:"+Global.getAdminPath()+"/course/list?repage";
	}

}