package com.oseasy.initiate.modules.state.web;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.FileUpUtils;
import com.oseasy.initiate.common.utils.FloatUtils;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.act.service.ProjectActTaskService;
import com.oseasy.initiate.modules.attachment.enums.FileSourceEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.project.entity.*;
import com.oseasy.initiate.modules.project.service.*;
import com.oseasy.initiate.modules.state.service.StateService;
import com.oseasy.initiate.modules.sys.entity.SysStudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SysStudentExpansionService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangzheng on 2017/3/10.
 * 国创大赛后台
 */
@Controller
@RequestMapping(value = "${adminPath}/state")
public class StateController extends BaseController {

    @Autowired
    ActTaskService actTaskService;
    @Autowired
    ProjectActTaskService  projectActTaskService;
    @Autowired
    ProjectDeclareService projectDeclareService;

    @Autowired
    ProjectAuditInfoService projectAuditInfoService;

    @Autowired
    TeamService teamService;

    @Autowired
    TeamUserRelationService teamUserRelationService;

    @Autowired
    ProjectPlanService projectPlanService;

    @Autowired
    SysStudentExpansionService sysStudentExpansionService;

    @Autowired
    SysAttachmentService sysAttachmentService;

    @Autowired
    StateService stateService;
    @Autowired
    UserService userService;

    @Autowired
     RuntimeService runtimeService;

    @Autowired
    HistoryService historyService;

    @Autowired
    ProMidService proMidService;

    @Autowired
    ProjectCloseService projectCloseService;


    @ModelAttribute
    public  ProjectDeclare get(@RequestParam(required=false) String id) {
        ProjectDeclare  projectDeclare = null;
        if (StringUtils.isNotBlank(id)) {
            projectDeclare = projectDeclareService.get(id);
        }
        if (projectDeclare == null) {
            projectDeclare = new ProjectDeclare();
        }
        return projectDeclare;
    }


   //学院立项待审核列表页面跳转 查询待审核任务
   @RequestMapping(value = "setAuditList")
    public String setAuditList(Act act, HttpServletRequest request,
                               HttpServletResponse response, Model model) {
       //如果是学校管理员跳转到
       User user = UserUtils.getUser();
       if (StringUtils.equals(user.getUserType(),"6")) { //学校管理员
           return "redirect:"+"/a/state/schoolSetList";
       }

       act.setProcDefKey("state_project_audit");  //国创项目流程名称
       act.setTaskDefKey("set1");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
       Page<Act> pageForSearch =new Page<Act>(request, response);
       Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
       model.addAttribute("page",page);
       return "modules/state/setAuditList";
    }




    //学院立项已审核列表页面跳转 查询已审核任务
    @RequestMapping(value = "setAuditedList")
    public String setAuditedList(Act act, HttpServletRequest request,
                               HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("set1");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=actTaskService.historicList(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/setAuditedList";
    }

    //学校立项待审核列表页面跳转 查询待审核任务
    @RequestMapping(value = "schoolSetList")
    public String schoolSetList(Act act, HttpServletRequest request,
                               HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("set2");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/schoolSetList";
    }


    //学校立项已审核列表页面跳转 查询已审核任务
    @RequestMapping(value = "schoolSetedList")
    public String schoolSetedList(Act act, HttpServletRequest request,
                                 HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("set2");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=actTaskService.historicList(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/schoolSetedList";
    }


    //立项院级审核跳转页面
    @RequestMapping(value = "collegeSet")
    public String collegeSet(ProjectDeclare projectDeclare, Model model) {
        //根据id查询业务表，获得entity  在ModelAttribute中实现了
        model.addAttribute("projectDeclare",projectDeclare);
        return "modules/state/collegeSet";
    }

    //立项校级审核跳转页面
    @RequestMapping(value = "schoolSet")
    public String schoolSet(ProjectDeclare projectDeclare, Model model) {
        //根据id查询业务表，获得entity  在ModelAttribute中实现了
        model.addAttribute("projectDeclare",projectDeclare);
        //根据projectDeclare.id 查询立项评审意见
        List<ProjectAuditInfo> infos1= getInfo(projectDeclare.getId(),"1");
        model.addAttribute("infos1",infos1);
        return "modules/state/schoolSet";
    }


    //立项院级审核处理
    @RequestMapping(value = "collegeSetSave")
    public String collegeSetSave(ProjectDeclare projectDeclare, Model model) {
        projectDeclareService.collegeSetSave(projectDeclare);
        return "redirect:"+"/a/state/setAuditList";
    }



    //立项校级审核处理
    @RequestMapping(value = "schoolSetSave")
    public String schoolSetSave(ProjectDeclare projectDeclare, Model model) {
        projectDeclareService.schoolSetSave(projectDeclare);
        return "redirect:"+"/a/state/schoolSetList";
    }




    //提交中期报告跳转页面
    @RequestMapping(value = "submitMidReport")
    public String submitMidReport(ProjectDeclare projectDeclare, Model model) {
        //根据id查询业务表，获得entity  在ModelAttribute中实现了
        model.addAttribute("projectDeclare",projectDeclare);
        return "modules/state/submitMidReport";
    }

    //保存中期报告，并完成相应的工作流
    @RequestMapping(value = "midReportSave")
    public String midReportSave(ProjectDeclare projectDeclare, Model model) {
        projectDeclareService.midSave(projectDeclare);
        return "redirect:"+"/a/act/task/todo";
    }




    //中期评分列表页面跳转 查询待审核、已审核任务
    @RequestMapping(value = "middleAuditList")
    public String middleAuditList(Act act, HttpServletRequest request,
                               HttpServletResponse response, Model model) {
        //判断用户类型如果是学院秘书或者管理员进来，跳转到middleRatingList
        User user = UserUtils.getUser();
        if (StringUtils.equals(user.getUserType(),"3")||StringUtils.equals(user.getUserType(),"6")) { //学院秘书和学校管理员
            return "redirect:"+"/a/state/middleRatingList";  //跳转到中期评级页面
        }

        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("middleScore");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/middleAuditList";
    }


    //中期已评分列表页面跳转 查询已审核任务
    @RequestMapping(value = "middleAuditedList")
    public String middleAuditedList(Act act, HttpServletRequest request,
                                 HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("middleScore");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=actTaskService.historicList(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/middleAuditedList";
    }

    //中期评级列表页面跳转 查询审核任务
    @RequestMapping(value = "middleRatingList")
    public String middleRatingList(Act act, HttpServletRequest request,
                                  HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("middleRating");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/middleRatingList";
    }


    //中期已评级列表页面跳转 查询已审核任务
    @RequestMapping(value = "middleRatedList")
    public String middleRatedList(Act act, HttpServletRequest request,
                                    HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("middle");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=actTaskService.historicList(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/middleRatedList";
    }





    //专家中期评分页面
    @RequestMapping(value = "expCheck")
    public String expCheck(ProjectDeclare projectDeclare, Model model) {
        //根据id查询业务表，获得entity  在ModelAttribute中实现了
        model.addAttribute("projectDeclare",projectDeclare);
        //根据projectDeclare.id 查询立项评审意见
        List<ProjectAuditInfo> infos1= getInfo(projectDeclare.getId(),"1");
        model.addAttribute("infos1",infos1);
        ProMid proMid=proMidService.getByProjectId(projectDeclare.getId());
        model.addAttribute("proMidId",proMid.getId());
        return "modules/state/expCheck";
    }

    //中期评分结果保存 保存评分、意见到子表 执行一步工作流
    @RequestMapping(value = "expCheckSave")
    public String expCheckSave(ProjectDeclare projectDeclare, Model model) {
        projectDeclareService.expCheckSave(projectDeclare);
        return "redirect:"+"/a/state/middleAuditList";
    }

    //中期审核跳转页面
    @RequestMapping(value = "secCheck")
    public String secCheck(ProjectDeclare projectDeclare, Model model) {
        //根据id查询业务表，获得entity  在ModelAttribute中实现了
        model.addAttribute("projectDeclare",projectDeclare);
        //根据projectDeclare.id 查询立项评审意见
        List<ProjectAuditInfo> infos1= getInfo(projectDeclare.getId(),"1");
        model.addAttribute("infos1",infos1);
        //中期评分意见
        List<ProjectAuditInfo> infos2= getInfo(projectDeclare.getId(),"2");
        model.addAttribute("infos2",infos2);

        ProMid proMid=proMidService.getByProjectId(projectDeclare.getId());
        model.addAttribute("proMidId",proMid.getId());

        return "modules/state/secCheck";
    }

    //中期评级保存
    @RequestMapping(value = "secCheckSave")
    public String secCheckSave(ProjectDeclare projectDeclare, Model model) {
        if (StringUtils.equals(projectDeclare.getMidResult(),"4")) {
            projectDeclare.setPass("4");
        }
         projectDeclareService.secCheckSave(projectDeclare);
        return "redirect:"+"/a/state/middleRatingList";
    }


    //提交结项报告跳转页面
    @RequestMapping(value = "submitCloReport")
    public String submitCloReport(ProjectDeclare projectDeclare, Model model) {
        //根据id查询业务表，获得entity  在ModelAttribute中实现了
        model.addAttribute("projectDeclare",projectDeclare);
        return "modules/state/submitCloseReport";
    }

    //保存结项报告，并完成相应的工作流
    @RequestMapping(value = "closeReportSave")
    public String closeReportSave(ProjectDeclare projectDeclare, Model model) {
        projectDeclareService.closeSave(projectDeclare);
        return "redirect:"+"/a/act/task/todo";
    }


    //结项评分待审核列表页面跳转 查询待审核任务   秘书、管理员进来能看到专家们在评分
    @RequestMapping(value = "closeAuditList")
    public String closeAuditList(Act act, HttpServletRequest request,
                                  HttpServletResponse response, Model model) {
        //判断用户类型如果是学院秘书或者管理员进来，跳转到secCloseSearch
        User user = UserUtils.getUser();
        if (StringUtils.equals(user.getUserType(),"3")||StringUtils.equals(user.getUserType(),"6")) { //学院秘书和学校管理员
            return "redirect:"+"/a/state/secCloseSearch";
        }
        //如果是专家，则查看其待审核已审核数据
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("closeScore");   // 表示立项阶段 见流程图的userTask的id
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/closeAuditList";
    }

    //查看秘书、管理员中期检查的已审核任务，但审核状态在结项评分中
    @RequestMapping(value = "secCloseSearch")
    public String secCloseSearch(Act act, HttpServletRequest request,
                                 HttpServletResponse response, Model model ) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("middle");   // 中期检查的任务
        act.setStatus("closeScore");  //状态在结项评分中   closeScore2是学校专家结项评分  closeScore3 是学院专家评分 在流程图中主键ID可以看到
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=projectActTaskService.finishedListForPage(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/secCloseSearch";
    }


    //结项评已审核列表页面跳转 查询已审核任务
    @RequestMapping(value = "closeAuditedList")
    public String closeAuditedList(Act act, HttpServletRequest request,
                                    HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("closeScore");   // 表示立项阶段 见流程图的userTask的id
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=actTaskService.historicList(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/closeAuditedList";
    }

    //答辩评分待审核列表页面跳转 查询待审核任务
    @RequestMapping(value = "closeReplyingList")
    public String closeReplyingList(Act act, HttpServletRequest request,
                                 HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("closeReply");   // 表示答辩评分阶段 见流程图的userTask的id
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/closeReplyingList";
    }


    //答辩评已审核列表页面跳转 查询已审核任务
    @RequestMapping(value = "closeReplyedList")
    public String closeReplyedList(Act act, HttpServletRequest request,
                                   HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("closeReply");   // 表示答辩评分阶段 见流程图的userTask的id
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=actTaskService.historicList(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/closeReplyedList";
    }




    //专家结项评分页面
    @RequestMapping(value = "expClose")
    public String expClose(ProjectDeclare projectDeclare, Model model) {
        //根据id查询业务表，获得entity  在ModelAttribute中实现了
        model.addAttribute("projectDeclare",projectDeclare);
        //根据projectDeclare.id 查询立项评审意见
        List<ProjectAuditInfo> infos1= getInfo(projectDeclare.getId(),"1");
        model.addAttribute("infos1",infos1);
        List<ProjectAuditInfo> infos2= getInfo(projectDeclare.getId(),"2");
        Iterator<ProjectAuditInfo> it=infos2.iterator();
        User me= UserUtils.getUser();
        while (it.hasNext()) {
            ProjectAuditInfo info=it.next();
            if (!StringUtils.equals(info.getCreateBy().getId(),me.getId())) {
                it.remove();
            }

        }
        model.addAttribute("infos2",infos2);
        List<ProjectAuditInfo> infos3= getInfo(projectDeclare.getId(),"3");
        model.addAttribute("infos3",infos3);
        //查找中期报告id
        ProMid proMid=proMidService.getByProjectId(projectDeclare.getId());
        model.addAttribute("proMidId",proMid.getId());
         //查找结项报告id
        ProjectClose projectClose=projectCloseService.getByProjectId(projectDeclare.getId());
        model.addAttribute("proCloseId",projectClose.getId());
        return "modules/state/expClose";
    }

    //结项评分结果保存 保存评分、意见到子表 执行一步工作流
    @RequestMapping(value = "expCloseSave")
    public String expCloseSave(ProjectDeclare projectDeclare, Model model) {
        projectDeclareService.expCloseSave(projectDeclare);
        return "redirect:"+"/a/state/closeAuditList";
    }
    //答辩评分跳转页面
    @RequestMapping(value = "secClose")
    public String  secClose(ProjectDeclare projectDeclare, Model model) {
        //根据id查询业务表，获得entity  在ModelAttribute中实现了
        model.addAttribute("projectDeclare",projectDeclare);
        //根据projectDeclare.id 查询立项评审意见
        List<ProjectAuditInfo> infos1= getInfo(projectDeclare.getId(),"1");
        model.addAttribute("infos1",infos1);
        List<ProjectAuditInfo> infos2= getInfo(projectDeclare.getId(),"2");
        model.addAttribute("infos2",infos2);
        List<ProjectAuditInfo> infos3= getInfo(projectDeclare.getId(),"3");
        model.addAttribute("infos3",infos3);
        List<ProjectAuditInfo> infos4= getInfo(projectDeclare.getId(),"4");
        model.addAttribute("infos4",infos4);
        //查找中期报告id
        ProMid proMid=proMidService.getByProjectId(projectDeclare.getId());
        model.addAttribute("proMidId",proMid.getId());
        //查找结项报告id
        ProjectClose projectClose=projectCloseService.getByProjectId(projectDeclare.getId());
        model.addAttribute("proCloseId",projectClose.getId());

        return "modules/state/secClose";
    }

    //答辩评分结果保存
    @RequestMapping(value = "secCloseSave")
    public String secCloseSave(ProjectDeclare projectDeclare, Model model,RedirectAttributes redirectAttributes) {
        projectDeclareService.secCloseSave(projectDeclare);
        addMessage(redirectAttributes, "答辩评分保存成功");
        return "redirect:"+"/a/state/closeReplyingList";
    }

    //已审核 查看
    @RequestMapping(value = "infoView")
    public String infoView(ProjectDeclare projectDeclare, Model model,HttpServletRequest request) {
        model.addAttribute("projectDeclare",projectDeclare);
        String  taskDefinitionKey=request.getParameter("taskDefinitionKey");

        List<ProjectAuditInfo> infos1= getInfo(projectDeclare.getId(),"1");  //立项评级
        model.addAttribute("infos1",infos1);
        List<ProjectAuditInfo> infos2= getInfo(projectDeclare.getId(),"2");  //中期评分
        model.addAttribute("infos2",infos2);
        List<ProjectAuditInfo> infos3= getInfo(projectDeclare.getId(),"3");
        model.addAttribute("infos3",infos3);
        List<ProjectAuditInfo> infos4= getInfo(projectDeclare.getId(),"4");
        model.addAttribute("infos4",infos4);
        List<ProjectAuditInfo> infos5= getInfo(projectDeclare.getId(),"5");
        model.addAttribute("infos5",infos5);
        List<ProjectAuditInfo> infos6= getInfo(projectDeclare.getId(),"6");
        model.addAttribute("infos6",infos6);

        model.addAttribute("infos1",infos1);
        model.addAttribute("infos2",infos2);
        model.addAttribute("infos3",infos3);
        model.addAttribute("infos4",infos4);
        model.addAttribute("infos5",infos5);
        model.addAttribute("infos6",infos6);

        User me= UserUtils.getUser();
        int showMiddleScore=0; //是否显示中期平均分 0不显示 1, 显示
        int showFinalScore=0; //是否显示结项平均分 0不显示 1, 显示
        int show2=0,show3=0,show4=0,show5=0,show6=0;
        int showMiddleReport=0; //是否显示中期报告  0不显示 1显示
        int showCloseReport=0; //是否显示结项报告  0不显示 1显示
        //中期评分步骤，只能看到自己的评分
        if ("middleScore2".equals(taskDefinitionKey)
                ||"middleScore3".equals(taskDefinitionKey)) {
            Iterator<ProjectAuditInfo> it=infos2.iterator();
            while (it.hasNext()) {
                ProjectAuditInfo info=it.next();
               if (!StringUtils.equals(info.getCreateBy().getId(),me.getId())) {
                   it.remove();
               }

            }
            show2=1;
            showMiddleScore=1;
            showMiddleReport=1;
        }

        //中期评级步骤
        if ("middleRating4".equals(taskDefinitionKey)
                ||"middleRating5".equals(taskDefinitionKey)) {
            showMiddleScore=1;
            show2=1;
            show3=1;
            showMiddleReport=1;
        }

        //结项评分步骤
        if ("closeScore2".equals(taskDefinitionKey)
                ||"closeScore3".equals(taskDefinitionKey)) {
            Iterator<ProjectAuditInfo> it2=infos2.iterator();
            while (it2.hasNext()) {
                ProjectAuditInfo info=it2.next();
                if (!StringUtils.equals(info.getCreateBy().getId(),me.getId())) {
                    it2.remove();
                }

            }

            showMiddleScore=1;
            showFinalScore=1;
            show2=1;
            show3=1;
            show4=1;
            Iterator<ProjectAuditInfo> it=infos4.iterator();
            while (it.hasNext()) {
                ProjectAuditInfo info=it.next();
                if (!StringUtils.equals(info.getCreateBy().getId(),me.getId())) {
                    it.remove();
                }

            }
            showMiddleReport=1;
            showCloseReport=1;
        }

        //给学院秘书、学校管理员查看的数据
        if ("closeScore".equals(taskDefinitionKey)) {
            showMiddleScore=1;
            showFinalScore=1;
            show2=1;
            show3=1;
            show4=1;
            showMiddleReport=1;
            showCloseReport=1;
        }



        //   答辩评分步骤
        if ("closeReply4".equals(taskDefinitionKey)
                ||"closeReply5".equals(taskDefinitionKey)) {
            showMiddleScore=1;
            showFinalScore=1;
            show2=1;
            show3=1;
            show4=1;
            show5=1;
            showMiddleReport=1;
            showCloseReport=1;
        }

        //   答辩评分步骤
        if ("assess1".equals(taskDefinitionKey)
                ||"assess2".equals(taskDefinitionKey)) {
            showMiddleScore=1;
            showFinalScore=1;
            show2=1;
            show3=1;
            show4=1;
            show5=1;
            show6=1;
            showMiddleReport=1;
            showCloseReport=1;
        }

        if (showMiddleReport==1) { //查找中期报告id
            ProMid proMid=proMidService.getByProjectId(projectDeclare.getId());
            model.addAttribute("proMidId",proMid.getId());
        }
        if (showCloseReport==1) { //查找结项报告id
            ProjectClose projectClose=projectCloseService.getByProjectId(projectDeclare.getId());
            model.addAttribute("proCloseId",projectClose.getId());
        }

        model.addAttribute("showMiddleScore",showMiddleScore);
        model.addAttribute("showFinalScore",showFinalScore);

        model.addAttribute("show2",show2);
        model.addAttribute("show3",show3);
        model.addAttribute("show4",show4);
        model.addAttribute("show5",show5);
        model.addAttribute("show6",show6);
        model.addAttribute("showMiddleReport",showMiddleReport);
        model.addAttribute("showCloseReport",showCloseReport);

        return "modules/state/infoView";
    }



    //结果评定待审核列表页面跳转 查询待审核任务
    @RequestMapping(value = "assessList")
    public String assessList(Act act, HttpServletRequest request,
                                 HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("assess");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/assessList";
    }


    //结果评定已审核列表页面跳转 查询已审核任务
    @RequestMapping(value = "assessedList")
    public String assessedList(Act act, HttpServletRequest request,
                                   HttpServletResponse response, Model model) {
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        act.setTaskDefKey("assess");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        Page<Act> pageForSearch =new Page<Act>(request, response);
        Page<Act> page=actTaskService.historicList(pageForSearch,act);
        model.addAttribute("page",page);
        return "modules/state/assessedList";
    }


    //结果评定审核跳转页面
    @RequestMapping(value = "secAssess")
    public String  secAssess(ProjectDeclare projectDeclare, Model model) {
        //根据id查询业务表，获得entity  在ModelAttribute中实现了
        model.addAttribute("projectDeclare",projectDeclare);

        List<ProjectAuditInfo> infos1= getInfo(projectDeclare.getId(),"1");
        model.addAttribute("infos1",infos1);
        List<ProjectAuditInfo> infos2= getInfo(projectDeclare.getId(),"2");
        model.addAttribute("infos2",infos2);
        List<ProjectAuditInfo> infos3= getInfo(projectDeclare.getId(),"3");
        model.addAttribute("infos3",infos3);
        List<ProjectAuditInfo> infos4= getInfo(projectDeclare.getId(),"4");
        model.addAttribute("infos4",infos4);
        List<ProjectAuditInfo> infos5= getInfo(projectDeclare.getId(),"5");
        model.addAttribute("infos5",infos5);
        //计算平均分
        float averageScore=0;
        float replyScore = projectDeclare.getReplyScore();
        float finalScore = projectDeclare.getFinalScore();
        averageScore= FloatUtils.division(replyScore+finalScore,2);
        model.addAttribute("averageScore",averageScore);
        //查找中期报告id
        ProMid proMid=proMidService.getByProjectId(projectDeclare.getId());
        model.addAttribute("proMidId",proMid.getId());
        //查找结项报告id
        ProjectClose projectClose=projectCloseService.getByProjectId(projectDeclare.getId());
        model.addAttribute("proCloseId",projectClose.getId());
        return "modules/state/secAssess";
    }

    //结果评定结项审核保存
    @RequestMapping(value = "secAssessSave")
    public String secAssessSave(ProjectDeclare projectDeclare, Model model) {
        projectDeclareService.secAssessSave(projectDeclare);
        return "redirect:"+"/a/state/assessList";
    }

    //项目查询
    @RequestMapping(value = "allList")
    public String allList(ProjectDeclare projectDeclare,
                          HttpServletRequest request,
                          HttpServletResponse response,
                          Model model) throws ParseException {

        Page<ProjectDeclare> pageForSearch= new Page<ProjectDeclare>(request, response);
        if(StringUtils.isNotBlank(projectDeclare.getStartDateStr())){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Date startDate = sdf.parse(projectDeclare.getStartDateStr());
            projectDeclare.setStartDate(startDate);
            projectDeclare.setEndDate(DateUtil.addYear(projectDeclare.getStartDate(),1));
        }
        User user=UserUtils.getUser();
        String officeMap="";
        String levelMap="";
        Page<ProjectDeclare> page=null;
       if (StringUtils.equals(user.getUserType(),"3")||StringUtils.equals(user.getUserType(),"4")) {//3.学院教学秘书 4.学院专家
           officeMap="and syso.id = '"+user.getOffice().getId()+"'";
           projectDeclare.getSqlMap().put("officeMap",officeMap);
       }
        if (StringUtils.equals(user.getUserType(),"4")) { //4.学院专家
            levelMap = "and a.level in('3','4')";
            projectDeclare.getSqlMap().put("levelMap",levelMap);
        }
        if (StringUtils.equals(user.getUserType(),"5")) { //4.学校专家
            levelMap = "and a.level in('1','2')";
            projectDeclare.getSqlMap().put("levelMap",levelMap);
        }

        page = projectDeclareService.findPage(pageForSearch,projectDeclare);

        //根据procInsId 查找taskname
        if (page!=null) {
            List<ProjectDeclare> list=page.getList();
            for(ProjectDeclare item:list) {
                String procInsId=item.getProcInsId();
                if (StringUtils.isNotBlank(procInsId)) {
                    String taskName=actTaskService.getTaskNameByProcInsId(procInsId);
                    item.getAct().setTaskName(taskName);
                }
            }
        }
        model.addAttribute("page",page);
        return "modules/state/allList";
    }

    //查询项目表单
    @RequestMapping(value = "projectDetail")
    public String projectDetail(ProjectDeclare projectDeclare,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                Model model) {
        model.addAttribute("projectDeclare",projectDeclare);

        List<ProjectAuditInfo> infos1= getInfo(projectDeclare.getId(),"1");  //立项评级
        List<ProjectAuditInfo> infos2= getInfo(projectDeclare.getId(),"2");  //中期评分
        List<ProjectAuditInfo> infos3= getInfo(projectDeclare.getId(),"3");  //中期评级
        List<ProjectAuditInfo> infos4= getInfo(projectDeclare.getId(),"4"); //结项评分
        List<ProjectAuditInfo> infos5= getInfo(projectDeclare.getId(),"5"); //答辩评分
        List<ProjectAuditInfo> infos6= getInfo(projectDeclare.getId(),"6"); //结项审核


        User me=UserUtils.getUser();
        if (StringUtils.equals(me.getUserType(),"4")||StringUtils.equals(me.getUserType(),"5")) {  //如果是院级专家或者校级专家，只能看到自己的评分信息
            Iterator<ProjectAuditInfo> it2=infos2.iterator();
            while (it2.hasNext()) {
                ProjectAuditInfo info=it2.next();
                if (!StringUtils.equals(info.getCreateBy().getId(),me.getId())) {
                    it2.remove();
                }
            }

            Iterator<ProjectAuditInfo> it=infos4.iterator();
            while (it.hasNext()) {
                ProjectAuditInfo info=it.next();
                if (!StringUtils.equals(info.getCreateBy().getId(),me.getId())) {
                    it.remove();
                }
            }

        }


        model.addAttribute("infos1",infos1);
        model.addAttribute("infos2",infos2);
        model.addAttribute("infos3",infos3);
        model.addAttribute("infos4",infos4);
        model.addAttribute("infos5",infos5);
        model.addAttribute("infos6",infos6);

        //查找中期报告id
        ProMid proMid=proMidService.getByProjectId(projectDeclare.getId());
        if (proMid!=null) {
            model.addAttribute("proMidId",proMid.getId());
        }

        //查找结项报告id
        ProjectClose projectClose=projectCloseService.getByProjectId(projectDeclare.getId());
        if (projectClose!=null) {
            model.addAttribute("proCloseId",projectClose.getId());
        }

        return "modules/state/projectDetail";
    }

    //项目变更
    @RequestMapping(value = "projectEdit")
    public String projectEdit(ProjectDeclare projectDeclare,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              Model model) {
        model.addAttribute("projectDeclare",projectDeclare);
        //查询studentList
        List<SysStudentExpansion> studentList=sysStudentExpansionService.getStudentList();
        model.addAttribute("studentList",studentList);


        //查找项目团队相关信息 projectDeclare.id
        Team team=teamService.get(projectDeclare.getTeamId());
        model.addAttribute("team",team);

        //查找学生
        TeamUserRelation tur1=new TeamUserRelation();
        tur1.setTeamId(projectDeclare.getTeamId());
        List<TeamUserRelation> turStudents=teamUserRelationService.getStudents(tur1);
        model.addAttribute("turStudents",turStudents);
        //查找导师
        List<TeamUserRelation>  turTeachers=teamUserRelationService.getTeachers(tur1);
        model.addAttribute("turTeachers",turTeachers);

        //查找项目分工
        List<ProjectPlan> plans=projectPlanService.findListByProjectId(projectDeclare.getId());
        model.addAttribute("plans",plans);

        //查找项目附件
        Map<String,String> map=new HashMap<String,String>();
        map.put("file_step", FileTypeEnum.S100.getValue());
        map.put("uid", projectDeclare.getId());
        map.put("type", FileSourceEnum.S0.getValue());
        List<Map<String,String>> fileInfo = sysAttachmentService.getFileInfo(map);
        model.addAttribute("fileInfo",fileInfo);

        //查找审核信息
        //根据leader找到学院秘书，找到学校管理员，找到学院专家，找到学校专家
        User collegeSec =  userService.getCollegeSecUsers(projectDeclare.getLeader()); //学院秘书
        User schoolSec  = userService.getSchoolSecUser();  //学校管理员
        model.addAttribute("collegeSec",collegeSec);
        model.addAttribute("schoolSec",schoolSec);

        List<User> collegeExperts = userService.getCollegeExpertUsers(projectDeclare.getLeader()); // 院级专家
        List<User> schoolExperts = userService.getSchoolExpertUsers();   // 学校专家

        List<ProjectAuditInfo> infos1= getInfo(projectDeclare.getId(),"1");  //立项评级
        List<ProjectAuditInfo> infos2= getInfo(projectDeclare.getId(),"2");  //中期评分
        List<ProjectAuditInfo> infos3= getInfo(projectDeclare.getId(),"3");  //中期检查评级
        List<ProjectAuditInfo> infos4= getInfo(projectDeclare.getId(),"4");  //结项审核
        List<ProjectAuditInfo> infos5= getInfo(projectDeclare.getId(),"5");  //结项评级
        List<ProjectAuditInfo> infos6= getInfo(projectDeclare.getId(),"6");  //结果评定

        //对立项评级做处理 拆分为学院立项和学校立项
        ProjectAuditInfo info01=new ProjectAuditInfo();
        if (infos1 != null && infos1.size() > 0) {
            info01=infos1.get(0);
        } else {
            info01.setCreateBy(collegeSec);
            info01.setUserName(collegeSec.getName());
            info01.setUserId(collegeSec.getId());
        }

        ProjectAuditInfo info02;
        if (infos1 != null && infos1.size() == 2) {
            info02=infos1.get(1);
        }else{
            info02=new ProjectAuditInfo();
            info02.setCreateBy(schoolSec);
            info02.setUserName(schoolSec.getName());
            info02.setUserId(schoolSec.getId());
        }
        model.addAttribute("info01",info01);  //立项学院评审信息
        model.addAttribute("info02",info02);  //立项学校评审信息

        //中期检查评分记录处理
        List<ProjectAuditInfo>  info11 = new ArrayList<ProjectAuditInfo>();  // 院级专家中期评分
        List<ProjectAuditInfo> info12=new ArrayList<ProjectAuditInfo>();  // 校级专家中期评分
        for(User expt:collegeExperts) {
            ProjectAuditInfo info=new ProjectAuditInfo();
            info.setCreateBy(expt);
            info.setUserName(expt.getName());
            info.setUserId(expt.getId());
            for(ProjectAuditInfo auditInfo:infos2) {
                if (StringUtils.equals(expt.getId(),auditInfo.getCreateBy().getId())) {
                    info=auditInfo;
                }
            }
            info11.add(info);
        }

        for(User expt:schoolExperts) {
            ProjectAuditInfo info=new ProjectAuditInfo();
            info.setCreateBy(expt);
            info.setUserName(expt.getName());
            info.setUserId(expt.getId());
            for(ProjectAuditInfo auditInfo:infos2) {
                if (StringUtils.equals(expt.getId(),auditInfo.getCreateBy().getId())) {
                    info=auditInfo;
                }
            }
            info12.add(info);
        }

        model.addAttribute("info11",info11);
        model.addAttribute("info12",info12);


        //中期检查评级 处理
        ProjectAuditInfo info3=new ProjectAuditInfo();
        if (infos3==null||infos3.size()==0) {
            if (StringUtils.equals(projectDeclare.getLevel(),"1")||StringUtils.equals(projectDeclare.getLevel(),"2")) {
                info3.setCreateBy(schoolSec);
                info3.setUserName(schoolSec.getName());
                info3.setUserId(schoolSec.getId());
            }else{
                info3.setCreateBy(collegeSec);
                info3.setUserName(collegeSec.getName());
                info3.setUserId(collegeSec.getId());
            }
        }else{
            info3=infos3.get(0);
        }
        model.addAttribute("info3",info3);

        //结项评分处理
        List<ProjectAuditInfo>  info41 = new ArrayList<ProjectAuditInfo>();  // 院级专家结项评分
        List<ProjectAuditInfo> info42=new ArrayList<ProjectAuditInfo>();  // 校级专家结项评分
        for(User expt:collegeExperts) {
            ProjectAuditInfo info=new ProjectAuditInfo();
            info.setCreateBy(expt);
            info.setUserName(expt.getName());
            info.setUserId(expt.getId());
            for(ProjectAuditInfo auditInfo:infos4) {
                if (StringUtils.equals(expt.getId(),auditInfo.getCreateBy().getId())) {
                    info=auditInfo;
                }
            }
            info41.add(info);
        }

        for(User expt:schoolExperts) {
            ProjectAuditInfo info=new ProjectAuditInfo();
            info.setCreateBy(expt);
            info.setUserName(expt.getName());
            info.setUserId(expt.getId());
            for(ProjectAuditInfo auditInfo:infos4) {
                if (StringUtils.equals(expt.getId(),auditInfo.getCreateBy().getId())) {
                    info=auditInfo;
                }
            }
            info42.add(info);
        }
        model.addAttribute("info41",info41);
        model.addAttribute("info42",info42);

        //答辩评分处理
        ProjectAuditInfo info5=new ProjectAuditInfo();
        if (infos5==null||infos5.size()==0) {
            if (StringUtils.equals(projectDeclare.getLevel(),"1")||StringUtils.equals(projectDeclare.getLevel(),"2")) {
                info5.setCreateBy(schoolSec);
                info5.setUserName(schoolSec.getName());
                info5.setUserId(schoolSec.getId());
            }else{
                info5.setCreateBy(collegeSec);
                info5.setUserName(collegeSec.getName());
                info5.setUserId(collegeSec.getId());
            }
        }else{
            info5=infos5.get(0);
        }
        model.addAttribute("info5",info5);

        //结果评定处理
        ProjectAuditInfo info6=new ProjectAuditInfo();
        if (infos6==null||infos6.size()==0) {
            if (StringUtils.equals(projectDeclare.getLevel(),"1")||StringUtils.equals(projectDeclare.getLevel(),"2")) {
                info6.setCreateBy(schoolSec);
                info6.setUserName(schoolSec.getName());
                info6.setUserId(schoolSec.getId());
            }else{
                info6.setCreateBy(collegeSec);
                info6.setUserName(collegeSec.getName());
                info6.setUserId(collegeSec.getId());
            }
        }else{
            info6=infos6.get(0);
        }
        model.addAttribute("info6",info6);

        //查找中期报告id
        ProMid proMid=proMidService.getByProjectId(projectDeclare.getId());
        if (proMid!=null) {
            model.addAttribute("proMidId",proMid.getId());
        }

        //查找结项报告id
        ProjectClose projectClose=projectCloseService.getByProjectId(projectDeclare.getId());
        if (projectClose!=null) {
            model.addAttribute("proCloseId",projectClose.getId());
        }

        return "modules/state/projectEdit";

    }

    //保存项目变更
    @RequestMapping(value = "saveEdit")
    public String saveEdit(ProjectDeclare projectDeclare,
                           HttpServletRequest request) {
        String[] arrUrl= request.getParameterValues("arrUrl");
        String[] arrNames= request.getParameterValues("arrName");
        List<Map<String,String>> fileListMap = FileUpUtils.getFileListMap(arrUrl,arrNames);
        stateService.saveEdit(projectDeclare,fileListMap);

        //保存变更的审核信息
        projectAuditInfoService.updateInfoList(projectDeclare.getAuditInfoList(),projectDeclare.getMidAuditList(),
                                   projectDeclare.getCloseAuditList(),  projectDeclare.getId(),projectDeclare.getLevel(),
                                  projectDeclare.getNumber(),projectDeclare.getMidScore(),projectDeclare.getFinalScore());
        if (StringUtils.equals("1",projectDeclare.getStatus())||StringUtils.equals("2",projectDeclare.getStatus())||StringUtils.equals("3",projectDeclare.getStatus())||
           StringUtils.equals("4",projectDeclare.getStatus())||StringUtils.equals("5",projectDeclare.getStatus())||StringUtils.equals("6",projectDeclare.getStatus())||
           StringUtils.equals("7",projectDeclare.getStatus())    ) {
            runtimeService.deleteProcessInstance(projectDeclare.getProcInsId(),"");
        }
        return "redirect:"+"/a/state/allList";
    }

    //删除项目
    @RequestMapping(value="projectDelete")
    public String projectDelete(ProjectDeclare projectDeclare) {
        if (StringUtils.isNotBlank(projectDeclare.getProcInsId())) {
            try{
                runtimeService.deleteProcessInstance(projectDeclare.getProcInsId(),"");
            }catch (Exception e) {
                System.out.println("该项目的工作流已经走完");
            }
            try{
                historyService.deleteHistoricProcessInstance(projectDeclare.getProcInsId());
            }catch (Exception e) {
                System.out.println("该项目没有历史记录");
            }
        }
        projectDeclareService.delete(projectDeclare);
        return "redirect:"+"/a/state/allList";
    }


    /**
     * 获得评审意见、评分
     * @param projectId 项目id
     * @param auditStep 审核步骤 ('1','立项评审';'2','中期检查评分';'3','中期检查评级';'4','结项审核';'5','答辩分记录',6结果评定)
     * @return
     */
    private  List<ProjectAuditInfo> getInfo(String projectId,String auditStep) {
        ProjectAuditInfo pai=new ProjectAuditInfo();
        pai.setProjectId(projectId);
        pai.setAuditStep(auditStep);
        List<ProjectAuditInfo> infos= projectAuditInfoService.getInfo(pai);
        return infos;
    }





}
