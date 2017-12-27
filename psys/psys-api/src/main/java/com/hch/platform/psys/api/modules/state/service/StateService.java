package com.hch.platform.pcore.modules.state.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hch.platform.pcore.common.service.CommonService;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.attachment.dao.SysAttachmentDao;
import com.hch.platform.pcore.modules.attachment.enums.FileStepEnum;
import com.hch.platform.pcore.modules.attachment.enums.FileTypeEnum;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.project.dao.ProjectDeclareDao;
import com.hch.platform.pcore.modules.project.dao.ProjectPlanDao;
import com.hch.platform.pcore.modules.project.entity.ProjectDeclare;
import com.hch.platform.pcore.modules.project.entity.ProjectPlan;
import com.hch.platform.pcore.modules.project.service.ProjectAuditInfoService;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.state.vo.MidVo;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.team.entity.TeamUserHistory;

import net.sf.json.JSONObject;

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
    @Autowired
    private ProjectDeclareService projectDeclareService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ProjectAuditInfoService projectAuditInfoService;
	@Autowired
	private CommonService commonService;
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
    public void saveEdit(ProjectDeclare projectDeclare) {
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

    }
    @Transactional(readOnly = false)
    public JSONObject saveModify(ProjectDeclare projectDeclare,String modifyPros){
    	JSONObject js=new JSONObject();
    	List<TeamUserHistory> stus=projectDeclare.getStudentList();
		List<TeamUserHistory> teas=projectDeclare.getTeacherList();
		String actywId=projectDeclare.getActywId();
		String teamId=projectDeclare.getTeamId();
		String proId=projectDeclare.getId();
		String category=projectDeclare.getType();
		js=commonService.checkProjectOnModify(stus,teas,proId, actywId,category, teamId);
		if("0".equals(js.getString("ret"))){
			return js;
		}
    	saveEdit(projectDeclare);
    	commonService.disposeTeamUserHistoryForModify(stus, teas, actywId, teamId, proId);
        sysAttachmentService.saveByVo(projectDeclare.getAttachMentEntity(),projectDeclare.getId(),FileTypeEnum.S0,FileStepEnum.S100);

        //保存变更的审核信息
        if("1".equals(modifyPros)){
	        projectAuditInfoService.updateInfoList(projectDeclare.getAuditInfoList(),projectDeclare.getMidAuditList(),
	                projectDeclare.getCloseAuditList(),  projectDeclare.getId(),projectDeclare.getLevel(),
	                projectDeclare.getNumber(),projectDeclare.getMidScore(),projectDeclare.getFinalScore());
        }
        //变更学分信息
        ProjectDeclare pd=projectDeclareService.getScoreConfigure(projectDeclare.getId());
        if(pd!=null&&StringUtil.isNotEmpty(pd.getLevel())&&StringUtil.isNotEmpty(pd.getFinalResult())){
        	projectDeclareService.saveScore(projectDeclare.getId());
        }
        if("1".equals(modifyPros)){
	        if (StringUtils.equals("1",projectDeclare.getStatus())||StringUtils.equals("2",projectDeclare.getStatus())||StringUtils.equals("3",projectDeclare.getStatus())||
	                StringUtils.equals("4",projectDeclare.getStatus())||StringUtils.equals("5",projectDeclare.getStatus())||StringUtils.equals("6",projectDeclare.getStatus())||
	                StringUtils.equals("7",projectDeclare.getStatus())    ) {
	            runtimeService.deleteProcessInstance(projectDeclare.getProcInsId(),"");
	        }
        }
        js.put("msg", "保存成功");
        return js;
    }

}
