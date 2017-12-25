package com.oseasy.initiate.modules.state.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oseasy.initiate.common.utils.FileUpUtils;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.modules.act.dao.ActDao;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.attachment.dao.SysAttachmentDao;
import com.oseasy.initiate.modules.attachment.enums.FileSourceEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.project.dao.ProjectDeclareDao;
import com.oseasy.initiate.modules.project.dao.ProjectPlanDao;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.entity.ProjectPlan;
import com.oseasy.initiate.modules.state.vo.MidVo;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import org.activiti.engine.*;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangzheng on 2017/4/11.
 */
@Service
@Transactional(readOnly = true)
public class StateService {

    @Autowired
    private TaskService taskService;

    @Autowired
    ProjectDeclareDao projectDeclareDao;
    @Autowired
    ProjectPlanDao projectPlanDao;
    @Autowired
    SysAttachmentService sysAttachmentService;
    @Autowired
    SysAttachmentDao sysAttachmentDao;


    public static String procDefKey="state_project_audit";

    /**
     *
     * 根据分数获得中期评级流程中合格和不合格数
     * @param score
     * @return
     */
    public Map<String,List<MidVo>> getNoByScore(int score) {
         Map<String,List<MidVo>> map= Maps.newHashMap();
        String userId = UserUtils.getUser().getLoginName();
        TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(userId).active()
                .includeProcessVariables().orderByTaskCreateTime().desc();
        todoTaskQuery.processDefinitionKey(procDefKey);
        todoTaskQuery.processVariableValueGreaterThanOrEqual("scoreInt",score);
        todoTaskQuery.processVariableValueGreaterThanOrEqual("scorePoint",0);
        todoTaskQuery.taskDefinitionKeyLike("%middleRating%"); //中期评级步骤

        List<Task> todoList = todoTaskQuery.list();
        List<MidVo> voList = Lists.newArrayList();
        for (Task task : todoList) {
            MidVo midVo=new MidVo();
            System.out.println((String)task.getProcessVariables().get("id"));
            midVo.setBussinessId((String)task.getProcessVariables().get("id"));
            System.out.println(task.getId());
            midVo.setTaskId(task.getId());
            voList.add(midVo);
        }

        map.put("pass",voList);


        TaskQuery todoTaskQuery2 = taskService.createTaskQuery().taskAssignee(userId).active()
                .includeProcessVariables().orderByTaskCreateTime().desc();
        todoTaskQuery2.processDefinitionKey(procDefKey);
        todoTaskQuery2.processVariableValueLessThan("scoreInt",score);
        todoTaskQuery2.taskDefinitionKeyLike("%middleRating%"); //中期评级步骤

        List<Task> todoList2 = todoTaskQuery2.list();
        List<MidVo> voList2 = Lists.newArrayList();
        for (Task task : todoList2) {
            MidVo midVo=new MidVo();
            System.out.println((String)task.getProcessVariables().get("id"));
            midVo.setBussinessId((String)task.getProcessVariables().get("id"));
            System.out.println(task.getId());
            midVo.setTaskId(task.getId());
            voList2.add(midVo);
        }
        map.put("failed",voList2);


        return map;
    }




    /**
     * 保存项目变更
     * 1先保存主表信息
     * 2保存子表 任务分工 信息
     * 3附件信息
     * 4团队信息
     * 5评审信息
     */
    @Transactional(readOnly = false)
    public void saveEdit(ProjectDeclare projectDeclare, List<Map<String,String>> fileListMap) {
       // 1先保存主表信息
        projectDeclare.preUpdate();
        projectDeclareDao.update(projectDeclare);
       //2保存子表 任务分工 信息
        projectPlanDao.deleteByProjectId(projectDeclare.getId());
        int sort=1;
        for(ProjectPlan plan:projectDeclare.getPlanList()) {
            plan.setSort(sort+"");
            plan.setProjectId(projectDeclare.getId());
            plan.preInsert();
            projectPlanDao.insert(plan);
            sort++;
        }
        //3附件信息
        sysAttachmentDao.deleteByUid(projectDeclare.getId());
        sysAttachmentService.saveList(fileListMap,
                FileSourceEnum.S0.getValue(),
                FileTypeEnum.S100.getValue(),
                projectDeclare.getId());

    }

}
