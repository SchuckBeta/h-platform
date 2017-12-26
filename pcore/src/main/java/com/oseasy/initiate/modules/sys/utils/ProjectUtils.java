package com.oseasy.initiate.modules.sys.utils;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.act.service.ProjectActTaskService;
import com.oseasy.initiate.modules.project.dao.ProjectDeclareDao;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.enums.ProjectStatusEnum;
import com.oseasy.initiate.modules.project.vo.ProjectDeclareListVo;
import com.oseasy.initiate.modules.proproject.dao.ProProjectDao;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.sys.entity.User;

/**
 * Created by zhangzheng on 2017/3/18.
 */
public class ProjectUtils {
    private static ProjectDeclareDao projectDeclareDao = SpringContextHolder.getBean(ProjectDeclareDao.class);
    private static ProProjectDao proProjectDao=SpringContextHolder.getBean(ProProjectDao.class);
    private static ProjectActTaskService projectActTaskService = SpringContextHolder.getBean(ProjectActTaskService.class);

    public static ProjectDeclare get(String id) {
        ProjectDeclare projectDeclare = projectDeclareDao.get(id);
        return projectDeclare;
    }

    public static ProjectDeclareListVo getProjectDeclareListVoById(String id) {
        ProjectDeclareListVo projectDeclareListVo = projectDeclareDao.getProjectDeclareListVoById(id);
        return projectDeclareListVo;
    }

    public static String getProProjectName(String id) {
        ProProject proProject = proProjectDao.get(id);
        if (proProject!=null) {
            return proProject.getProjectName();
        }
        return null;
    }

    public static String getTeamNum(String name) {
         String[] statusStr= name.split("/");
         return String.valueOf(statusStr.length);
     }

    public static String getAuditStatus(String status) {
        String statusStr= ProjectStatusEnum.getNameByValue(status);
        return statusStr;
    }

    //根据projectId 获得该项目的状态
    public static String getStatus(String id) {
        String statusStr="";
        ProjectDeclare projectDeclare = projectDeclareDao.get(id);
        String status=projectDeclare.getStatus();
        if (StringUtil.equals("8",status)) {
            if (StringUtil.equals("3",projectDeclare.getFinalResult())) {
                statusStr="立项不合格";
            }
            if (StringUtil.equals("4",projectDeclare.getFinalResult())) {
                statusStr="项目终止";
            }
        }else if (StringUtil.equals("9",status)) {
            if (StringUtil.equals("5",projectDeclare.getFinalResult())) {
                statusStr="延期结项";
            }else{
                statusStr="项目结项";
            }
        }else{
            statusStr="";
        }
        return statusStr;

    }

    //获得立项审核的待办任务个数
    public static long getAuditListCount(){
        User user = UserUtils.getUser();
        Act act = new Act();
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        if (StringUtil.equals(user.getUserType(),"6")) { //学校管理员
            act.setTaskDefKey("set2");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        }else if(StringUtil.equals(user.getUserType(),"3")){  //学院教学秘书
            act.setTaskDefKey("set1");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        }else{
            return 0;
        }
        Page<Act> pageForSearch =new Page<Act>(1,10);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        return page.getTodoCount();
    }

    //获得中期检查的待办任务个数
    public static long getMidCount(){
        User user = UserUtils.getUser();
        Act act = new Act();
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        if (StringUtil.equals(user.getUserType(),"3")|| StringUtil.equals(user.getUserType(),"6")) { //学校管理员 学院秘书
            act.setTaskDefKey("middleRating");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        }else if(StringUtil.equals(user.getUserType(),"4")||StringUtil.equals(user.getUserType(),"5")){  //专家
            act.setTaskDefKey("middleScore");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        }else{
            return 0;
        }
        Page<Act> pageForSearch =new Page<Act>(1,10);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        return page.getTodoCount();
    }

    //获得结项审核待办任务个数
    public static long  closeAuditCount(){
        User user = UserUtils.getUser();
        Act act = new Act();
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
       if(StringUtil.equals(user.getUserType(),"4")||StringUtil.equals(user.getUserType(),"5")){  //专家
            act.setTaskDefKey("closeScore");   // 表示立项阶段 见流程图的userTask的id 所有的立项的userTask都是以set开始
        }else{
            return 0;
        }
        Page<Act> pageForSearch =new Page<Act>(1,10);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        return page.getTodoCount();
    }

    //答辩评分的待办数量
    public static long  closeReplyingCount(){
        User user = UserUtils.getUser();
        Act act = new Act();
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        if (StringUtil.equals(user.getUserType(),"3")|| StringUtil.equals(user.getUserType(),"6")) { //学校管理员 学院秘书
            act.setTaskDefKey("closeReply");
        }else{
            return 0;
        }
        Page<Act> pageForSearch =new Page<Act>(1,10);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        return page.getTodoCount();
    }

    // 结果评定的待办数量
    public static long  assessCount(){
        User user = UserUtils.getUser();
        Act act = new Act();
        act.setProcDefKey("state_project_audit");  //国创项目流程名称
        if (StringUtil.equals(user.getUserType(),"3")|| StringUtil.equals(user.getUserType(),"6")) { //学校管理员 学院秘书
            act.setTaskDefKey("assess");
        }else{
            return 0;
        }
        Page<Act> pageForSearch =new Page<Act>(1,10);
        Page<Act> page=projectActTaskService.allListForPage(pageForSearch,act);
        return page.getTodoCount();
    }

}
