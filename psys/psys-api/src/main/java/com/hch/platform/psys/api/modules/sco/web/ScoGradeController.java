package com.hch.platform.pcore.modules.sco.web;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.sco.entity.ScoAffirmConf;
import com.hch.platform.pcore.modules.sco.entity.ScoApply;
import com.hch.platform.pcore.modules.sco.entity.ScoAuditing;
import com.hch.platform.pcore.modules.sco.entity.ScoCourse;
import com.hch.platform.pcore.modules.sco.service.ScoAffirmService;
import com.hch.platform.pcore.modules.sco.service.ScoAuditingService;
import com.hch.platform.pcore.modules.sco.service.ScoCourseService;
import com.hch.platform.pcore.modules.sco.vo.ScoCourseVo;
import com.hch.platform.pcore.modules.sco.vo.ScoProjectVo;
import com.hch.platform.pcore.modules.sco.vo.ScoTeamRatioVo;
import com.hch.platform.pcore.modules.sys.entity.StudentExpansion;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.StudentExpansionService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by zy on 2017/7/20.
 * 获取学生的 创新学分 创业学分 素质学分 列表
 */
@Controller
@RequestMapping(value = "${adminPath}/sco/scoreGrade")
public class ScoGradeController extends BaseController {
    @Autowired
    ScoAffirmService scoAffirmService;
    @Autowired
    ScoCourseService scoCourseService;
    @Autowired
    StudentExpansionService studentExpansionService;
    @Autowired
    SysAttachmentService sysAttachmentService;
    @Autowired
    ScoAuditingService scoAuditingService;


    //创新学分认定页面
    @RequestMapping(value = "createList")
    public String createList(ScoProjectVo scoProjectVo,HttpServletRequest request, HttpServletResponse response, Model model){
        Page<ScoProjectVo> page = scoAffirmService.findScoProjectCreateVoPage(new Page<ScoProjectVo>(request, response), scoProjectVo);

        model.addAttribute("scoProjectVo",scoProjectVo);
        model.addAttribute("page",page);
        return "modules/sco/grade/scoCreateGradeList";
    }

    //创新学分详情页面
    @RequestMapping(value = "createView")
    public String CreateView(HttpServletRequest request, HttpServletResponse response, Model model){
        String id= request.getParameter("id");
        if(id != null){
            List<ScoProjectVo> scoProjectVo =scoAffirmService.getScoGradeCreate(id);
            model.addAttribute("projectName",scoProjectVo.get(0).getProjectDeclare().getName());
            model.addAttribute("scoProjectVoList",scoProjectVo);
        }
        return "modules/sco/grade/scoCreateGradeView";
    }
    //创业学分认定页面
    @RequestMapping(value = "startList")
    public String startList(ScoProjectVo scoProjectVo,HttpServletRequest request, HttpServletResponse response, Model model){
        Page<ScoProjectVo> page = scoAffirmService.findScoProjectStartVoPage(new Page<ScoProjectVo>(request, response), scoProjectVo);

        model.addAttribute("scoProjectVo",scoProjectVo);
        model.addAttribute("page",page);
        return "modules/sco/grade/scoStartGradeList";
    }

    //创新学分详情页面
    @RequestMapping(value = "startView")
    public String startView(HttpServletRequest request, HttpServletResponse response, Model model){
        String id= request.getParameter("id");
        if(id!=null){
            List<ScoProjectVo> scoProjectVo =scoAffirmService.getScoGradeStart(id);
            model.addAttribute("projectName",scoProjectVo.get(0).getProjectDeclare().getName());
            model.addAttribute("scoProjectVoList",scoProjectVo);
        }
        return "modules/sco/grade/scoStartGradeView";
    }

    //素质学分认定页面
    @RequestMapping(value = "qualityList")
    public String qualityList(ScoProjectVo scoProjectVo,HttpServletRequest request, HttpServletResponse response, Model model){
        Page<ScoProjectVo> page = scoAffirmService.findScoGontestVoPage(new Page<ScoProjectVo>(request, response), scoProjectVo);

        model.addAttribute("scoProjectVo",scoProjectVo);
        model.addAttribute("page",page);
        return "modules/sco/grade/scoQualityGradeList";
    }

    //素质学分认定页面
    @RequestMapping(value = "QualityView")
    public String QualityView(HttpServletRequest request, HttpServletResponse response, Model model){
        String id= request.getParameter("id");
        if(id!=null){
            List<ScoProjectVo> scoProjectVo =scoAffirmService.getScoGradeQuality(id);
            model.addAttribute("projectName",scoProjectVo.get(0).getGContest().getpName());
            model.addAttribute("scoProjectVoList",scoProjectVo);
        }
        return "modules/sco/grade/scoQualityGradeView";
    }


    //课程学分认定页面

    @RequestMapping(value = "courseList")
    public String courseList(ScoCourseVo scoCourseVo,HttpServletRequest request, HttpServletResponse response, Model model){
        Page<ScoCourseVo> page =  scoCourseService.findScoCourseVoPage(new Page<ScoCourseVo>(request, response), scoCourseVo);

        model.addAttribute("scoCourseVo",scoCourseVo);
        model.addAttribute("page",page);
        return "modules/sco/grade/scoCourseGradeList";
    }

}