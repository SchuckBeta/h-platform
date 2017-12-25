package com.oseasy.initiate.modules.state.web;

import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.attachment.enums.FileSourceEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.entity.ProjectPlan;
import com.oseasy.initiate.modules.project.service.ProjectAuditInfoService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.project.service.ProjectPlanService;
import com.oseasy.initiate.modules.sys.entity.SysStudentExpansion;
import com.oseasy.initiate.modules.sys.service.SysStudentExpansionService;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangzheng on 2017/3/27.
 */
@Controller
@RequestMapping(value = "${adminPath}/projectBase")
public class ProjectBaseController extends BaseController {
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
    SysAttachmentService sysAttachmentService;

    @Autowired
    SysStudentExpansionService sysStudentExpansionService;

    @RequestMapping(value="baseInfo")
    public String baseInfo( Model model, HttpServletRequest request) {

        String id=request.getParameter("id");
        ProjectDeclare projectDeclare = projectDeclareService.get(id);
        model.addAttribute("projectDeclare",projectDeclare);

        //查找学生拓展信息
        SysStudentExpansion student=  sysStudentExpansionService.getByUserId(projectDeclare.getCreateBy().getId());
        model.addAttribute("student",student);

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
        map.put("uid", projectDeclare.getId());
        map.put("file_step", FileTypeEnum.S100.getValue());
        map.put("type", FileSourceEnum.S0.getValue());
        List<Map<String,String>> fileInfo = sysAttachmentService.getFileInfo(map);
        model.addAttribute("fileInfo",fileInfo);

        return "modules/state/baseInfo";
    }

    @ResponseBody
    @RequestMapping(value="getDownloadUrl")
    public String getDownloadUrl(HttpServletRequest request) {
        String id=request.getParameter("id");
        String flag=request.getParameter("flag");
        Map<String,String> map=new HashMap<String,String>();
        map.put("uid", id);
        if (StringUtil.equals("1",flag)) {
            map.put("file_step", FileTypeEnum.S100.getValue());
        }
        if (StringUtil.equals("2",flag)) {
            map.put("file_step", FileTypeEnum.S102.getValue());
        }
        if (StringUtil.equals("3",flag)) {
            map.put("file_step", FileTypeEnum.S103.getValue());
        }

        map.put("type", FileSourceEnum.S0.getValue());
        List<Map<String,String>> fileInfo = sysAttachmentService.getFileInfo(map);
        Map<String,String>  file=fileInfo.get(0);
        System.out.println(file.get("arrUrl"));
        String url=file.get("arrUrl");
        return url;
    }


}
