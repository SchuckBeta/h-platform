package com.hch.platform.pcore.modules.sys.utils;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.act.entity.Act;
import com.hch.platform.pcore.modules.act.service.ProjectActTaskService;
import com.hch.platform.pcore.modules.gcontest.service.GContestService;
import com.hch.platform.pcore.modules.project.dao.ProjectDeclareDao;
import com.hch.platform.pcore.modules.project.entity.ProjectDeclare;
import com.hch.platform.pcore.modules.proproject.dao.ProProjectDao;
import com.hch.platform.pcore.modules.proproject.entity.ProProject;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangzheng on 2017/3/18.
 */
public class GcontestUtils {

    private static GContestService gContestService = SpringContextHolder.getBean(GContestService.class);



    //获得专家审核的待办任务个数
    public static long collegeExportCount(){
        Map<String,Object> param =new HashMap<String,Object>();
        AbsUser user = UserUtils.getUser();
        param.put("auditUserName", user.getLoginName());
        String userType=user.getUserType();
        if (userType.equals("4")) {
            param.put("auditState", "1");
            param.put("collegeId",user.getOffice().getId());
        }else if (userType.equals("3")) {
            param.put("auditState", "2");
            param.put("collegeId",user.getOffice().getId());
        }else if (userType.equals("5")) {
            param.put("auditState", "3");
        }else if (userType.equals("6")) {
            param.put("auditState", "4");
        }else{
            return 0;
        }
        int todoCount=gContestService.todoCount(param);
        int hasdoCount=gContestService.hasdoCount(param);
        return (todoCount-hasdoCount);
    }


    //获得网评审核待办任务个数
    public static long  schoolActAuditList(){
        Map<String,Object> param =new HashMap<String,Object>();
        AbsUser user = UserUtils.getUser();
        param.put("auditState", "5");
        int todoCount=gContestService.todoCount(param);
        return todoCount;
    }

    //获得学校路演审核待办任务个数
    public static long  schoolEndAuditList(){
        Map<String,Object> param =new HashMap<String,Object>();
        AbsUser user = UserUtils.getUser();
        param.put("auditState", "6");
        int todoCount=gContestService.todoCount(param);
        return todoCount;
    }

}
