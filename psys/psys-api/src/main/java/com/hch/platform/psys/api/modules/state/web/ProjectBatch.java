package com.hch.platform.pcore.modules.state.web;

import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.act.entity.Act;
import com.hch.platform.pcore.modules.project.entity.ProjectDeclare;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.state.service.StateService;
import com.hch.platform.pcore.modules.state.vo.MidVo;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangzheng on 2017/3/28.
 * 国创项目审核批处理类
 */
@Controller
@RequestMapping(value = "${adminPath}/projectBatch")
public class ProjectBatch {

    public static Logger log = Logger.getLogger(ProjectBatch.class);

    @Autowired
    ProjectDeclareService projectDeclareService;

    @Autowired
    StateService stateService;

    /**
     * 学院立项审核批处理
     * @param ids  业务表id:任务id
     * @param level
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "collegeSetBatch")
    public boolean collegeBatch(String ids,String level) {
        String[] idStr=ids.split(",");
        long start=System.currentTimeMillis();
        for (int i=0;i<idStr.length;i++) {
            String[] idAndTaskId=idStr[i].split(":");
            String id=idAndTaskId[0];
            String taskId=idAndTaskId[1];
            ProjectDeclare projectDeclare=projectDeclareService.get(id);
            projectDeclare.setLevel(level);
            projectDeclare.getAct().setTaskId(taskId);
            projectDeclareService.collegeSetSave(projectDeclare);
        }
        long end=System.currentTimeMillis();
        log.info("学院立项审核批处理："+ (end-start)+"ms" +"数量："+idStr.length);
        return true;
    }

    /**
     * 学校立项审核批处理
     * @param ids  业务表id:任务id
     * @param level
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "schoolSetBatch")
    public boolean schoolSetBatch(String ids,String level) {
        String[] idStr=ids.split(",");
        long start=System.currentTimeMillis();
        for (int i=0;i<idStr.length;i++) {
            String[] idAndTaskId=idStr[i].split(":");
            String id=idAndTaskId[0];
            String taskId=idAndTaskId[1];
            ProjectDeclare projectDeclare=projectDeclareService.get(id);
            projectDeclare.setLevel(level);
            projectDeclare.getAct().setTaskId(taskId);
            projectDeclareService.schoolSetSave(projectDeclare);
        }
        long end=System.currentTimeMillis();
        log.info("学校立项审核批处理："+ (end-start)+"ms" +"数量："+idStr.length);
        return true;
    }

    /**
     * 秘书中期检查批处理 ajax请求
     *
     */
    @ResponseBody
    @RequestMapping(value = "middleRatingBatch")
    public boolean middleRatingBatch(String passIds,String passTaskIds,
                                     String failedIds, String failedTaskIds) {
        delMid(passIds,passTaskIds,"0"); //处理合格
        delMid(failedIds,failedTaskIds,"2"); //处理不合格
        return true;
    }


    public void delMid(String ids,String taskIds,String midResult ) {
        if (StringUtil.isNotBlank(ids)) {
            String[] passIdsStr=ids.split(",");
            String[]passTaskIdsStr=taskIds.split(",");
            for(int i=0;i<passIdsStr.length;i++) {
                String id=passIdsStr[i];
                String taskId=passTaskIdsStr[i];
                ProjectDeclare projectDeclare=projectDeclareService.get(id);
                projectDeclare.setMidResult(midResult);  //0代表合格 2代表不合格
                projectDeclare.getAct().setTaskId(taskId);
                projectDeclareService.secCheckSave(projectDeclare);
            }
        }
    }

    /**
     * 中期检查批量审核 ajax根据分数查询数量
     * @param score
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getNoByScore")
    public Map<String,List<MidVo>> getNoByScore(int score) {
        return stateService.getNoByScore(score);
    }



}
