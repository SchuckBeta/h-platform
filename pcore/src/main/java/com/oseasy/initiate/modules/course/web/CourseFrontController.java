package com.hch.platform.pcore.modules.course.web;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.attachment.enums.FileStepEnum;
import com.hch.platform.pcore.modules.attachment.enums.FileTypeEnum;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.course.entity.Course;
import com.hch.platform.pcore.modules.course.entity.CourseCategory;
import com.hch.platform.pcore.modules.course.entity.CourseTeacher;
import com.hch.platform.pcore.modules.course.service.CourseCategoryService;
import com.hch.platform.pcore.modules.course.service.CourseService;
import com.hch.platform.pcore.modules.course.service.CourseTeacherService;
import com.hch.platform.pcore.modules.ftp.service.FtpService;
import com.hch.platform.pcore.modules.interactive.util.InteractiveUtil;

/**
 * Created by zhangzheng on 2017/6/29.
 */
@Controller
@RequestMapping(value = "${frontPath}/course")
public class CourseFrontController extends BaseController {

    @Autowired
    private FtpService ftpService;
    @Autowired
    private CourseService courseService;
    @Autowired
    CourseCategoryService courseCategoryService;
    @Autowired
    CourseTeacherService courseTeacherService;
    @Autowired
    SysAttachmentService sysAttachmentService;


    @ModelAttribute
    public Course get(@RequestParam(required=false) String id) {
        Course entity = null;
        if (StringUtil.isNotBlank(id)) {
            entity = courseService.get(id);
        }
        if (entity == null) {
            entity = new Course();
        }
        return entity;
    }

    @RequestMapping(value = "view")
    public String view(Course course, Model model, HttpServletRequest request){
        //更改浏览量
/*        course.setViews(course.getViews()+1);
        courseService.updateViews(course);*/
		/*更改浏览量 夏添的方法*/
        InteractiveUtil.updateViews(course.getId(),request,CacheUtils.COURSE_VIEWS_QUEUE);
      //查找课程专业分类 授课老师
        if(StringUtil.isNotBlank(course.getId())){
            List<CourseCategory> categoryList = courseCategoryService.getByCourseId(course.getId());  //查找课程专业分类
            course.setCategoryList(categoryList);
            List<CourseTeacher> teacherList =  courseTeacherService.getByCourseId(course.getId());  //查找授课老师
            course.setTeacherList(teacherList);
            //查找课件
            SysAttachment sa=new SysAttachment();
            sa.setUid(course.getId());
            sa.setType(FileTypeEnum.S9);
            sa.setFileStep(FileStepEnum.S900);
            List<SysAttachment> attachmentList =  sysAttachmentService.getFiles(sa);
            course.setAttachmentList(attachmentList);
        }

        model.addAttribute("course", course);
        //查找推荐课程  专业分类,状态分类相关的 按置顶，发布时间倒序排序
        List<Course> courseList =courseService.findRecommedList(course) ;
        model.addAttribute("courseList", courseList);

        return "modules/course/front/courseView";
    }


    @RequestMapping(value = "frontCourseList")
    public String frontCourseList(Course course, HttpServletRequest request, HttpServletResponse response, Model model){
        Page pageForSearche=new Page<Course>(request, response);
        pageForSearche.setPageSize(6);
        Page<Course> page = courseService.findFrontPage(pageForSearche, course);
        List<CourseTeacher>  allTeachers = Lists.newArrayList();
        //专业课程分类处理
        for(Course courseItem:page.getList()){
            List<CourseCategory>  categoryList =courseCategoryService.getByCourseId(courseItem.getId());
            courseItem.setCategoryList(categoryList);
            List<CourseTeacher> teacherList =  courseTeacherService.getByCourseId(courseItem.getId());  //查找授课老师
            courseItem.setTeacherList(teacherList);
            if(teacherList!=null && teacherList.size()>0){
                for (CourseTeacher teacher: teacherList){
                  if(!allTeachers.contains(teacher)&&allTeachers.size()<6){
                      allTeachers.add(teacher);
                  }
                }
            }

            //查找课件
            SysAttachment sa=new SysAttachment();
            sa.setUid(courseItem.getId());
            sa.setType(FileTypeEnum.S9);
            sa.setFileStep(FileStepEnum.S900);
            List<SysAttachment> attachmentList =  sysAttachmentService.getFiles(sa);
            courseItem.setAttachmentList(attachmentList);
        }

        model.addAttribute("page", page);
        model.addAttribute("allTeachers", allTeachers);
        return "modules/course/front/frontCourseList";
    }

    /**
     * 前台更改下载次数，下载课件
     * @param course
     * @param response
     */
    @RequestMapping(value = "downLoad")
    public void downLoad(Course course,String fileName,String url, HttpServletResponse response)
            throws ServletException, IOException {
        //更改下载次数
        course.setDownloads(course.getDownloads()+1);
        courseService.updateDownloads(course);
        //下载课件
        response.setCharacterEncoding("UTF-8");
        response.setContentType("multipart/form-data;charset=UTF-8");
        fileName= URLDecoder.decode(fileName,"UTF-8");
        ftpService.downloadUrlFile(url,fileName,response);
    }

}
