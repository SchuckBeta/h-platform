package com.oseasy.initiate.modules.project.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.FloatUtils;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.IdUtils;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.dao.ActDao;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.enums.FileSourceEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.project.dao.ProMidDao;
import com.oseasy.initiate.modules.project.dao.ProjectCloseDao;
import com.oseasy.initiate.modules.project.dao.ProjectDeclareDao;
import com.oseasy.initiate.modules.project.dao.ProjectPlanDao;
import com.oseasy.initiate.modules.project.dao.weekly.ProjectWeeklyDao;
import com.oseasy.initiate.modules.project.entity.ProMid;
import com.oseasy.initiate.modules.project.entity.ProjectAuditInfo;
import com.oseasy.initiate.modules.project.entity.ProjectClose;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.entity.ProjectPlan;
import com.oseasy.initiate.modules.project.enums.ProjectFinalResultEnum;
import com.oseasy.initiate.modules.project.enums.ProjectMidResultEnum;
import com.oseasy.initiate.modules.project.enums.ProjectStatusEnum;
import com.oseasy.initiate.modules.project.exception.ProjectNameDuplicateException;
import com.oseasy.initiate.modules.project.vo.ProjectDeclareVo;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 项目申报Service
 * @author 9527
 * @version 2017-03-11
 */
@Service
@Transactional(readOnly = true)
public class ProjectDeclareService extends CrudService<ProjectDeclareDao, ProjectDeclare> {
    public static final String OPEN_TIME_LIMIT = Global.getConfig("openTimeLimit");
	@Autowired
	private ProjectWeeklyDao projectWeeklyDao;
	@Autowired
	private ProjectPlanDao projectPlanDao;
	@Autowired
	SysAttachmentService sysAttachmentService;
	@Autowired
	ProjectAuditInfoService projectAuditInfoService;
	@Autowired
	ActTaskService actTaskService;
	@Autowired
	TaskService taskService;
	@Autowired
	UserService userService;
	@Autowired
	UserDao userDao;
	@Autowired
	IdentityService identityService;
	@Autowired
	RuntimeService runtimeService;
	@Autowired
	ActDao actDao;
	@Autowired
	ProMidDao proMidDao;
	@Autowired
	ProjectCloseDao projectCloseDao;
	@Autowired
	OaNotifyService oaNotifyService;
	
	public  List<ProjectDeclare> getProjectByCdn(String num,String name,String uid) {
		return dao.getProjectByCdn( num, name, uid);
	}
	public List<Map<String,String>> getValidProjectAnnounce() {
		return dao.getValidProjectAnnounce();
	}
	public List<Map<String,String>> getCurProjectInfo(String uid) {
		return dao.getCurProjectInfo(uid);
	}
	public List<Map<String,String>> getCurProjectInfoByTeam(String tid) {
		return dao.getCurProjectInfoByTeam(tid);
	}
	
	public  List<ProjectDeclare> getCurProjectInfoByLeader(String leaderId) {
		return dao.getCurProjectInfoByLeader(leaderId);
	}
	private boolean checkIsValid(String start,String end){
		if("N".equals(OPEN_TIME_LIMIT)){
			return true;
		}else{
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date now=new Date();
			Date startdt=null;
			Date enddt=null;
			try {
				startdt=sdf.parse(start+" 00:00:00");
			} catch (Exception e) {
			}
			try {
				enddt=sdf.parse(end+" 23:59:59");
			} catch (Exception e) {
			}
			if(startdt!=null&&now.before(startdt)){
				return false;
			}
			if(enddt!=null&&now.after(enddt)){
				return false;
			}
			return true;
		}
	}
	public JSONObject getTimeIndexData(User user) {
		String uid=user.getId();
		JSONObject data=new JSONObject();
		JSONArray contents=new JSONArray();
		JSONArray files=new JSONArray();
		List<Map<String, String>> ulist=dao.getLastProjectInfo(uid);
		if (ulist!=null&&ulist.size()!=0) {//查询到最近一次项目信息
			Map<String, String> umap=ulist.get(0);
			List<Map<String, String>> list=dao.getProjectAnnounceByid(umap.get("template_id"));
			if (list!=null&&list.size()!=0) {//有可用的国创项目通告才能继续
				String createBy=umap.get("create_by");
				Map<String, String> map=list.get(0);
				data.put("proname", umap.get("name"));
				data.put("number", umap.get("number"));
				data.put("pid", umap.get("id"));
				data.put("leader", umap.get("leader"));
				String pid=umap.get("id");
				List<ProjectPlan> plans=projectPlanDao.findListByProjectId(pid);
				fillPlans(plans, contents);//填装任务信息
				int st= Integer.parseInt(umap.get("status"));
				/*申报节点**************************************************/
				if (st==0) {//未提交
					JSONObject content=new JSONObject();
					content.put("type", "milestone");
					content.put("title", "项目申报");
					content.put("desc", map.get("begin_date")+"~"+map.get("end_date"));
					content.put("start_date", map.get("begin_date"));
					content.put("end_date",	map.get("end_date"));
					content.put("current",	"true");
					content.put("intro", "");
					if (createBy.equals(user.getId())) {
						JSONObject btn=new JSONObject();
						btn.put("name",	"提交项目");
						btn.put("url", "/f/project/projectDeclare/form?id="+pid);
						content.put("btn_option",btn);
					}else{
						JSONObject btn=new JSONObject();
						btn.put("name",	"查看项目");
						btn.put("url", "/f/project/projectDeclare/viewForm?id="+pid);
						content.put("btn_option",btn);
					}
					contents.add(content);
				}else if (st>=1) {//提交之后
					data.put("apply_time", umap.get("apply_time"));
					JSONObject content=new JSONObject();
					content.put("type", "milestone");
					content.put("title", "项目申报");
					content.put("desc", map.get("begin_date")+"~"+map.get("end_date"));
					content.put("start_date", map.get("begin_date"));
					content.put("end_date",	map.get("end_date"));
					content.put("intro", "");
					content.put("current",	"true");
					JSONObject btn=new JSONObject();
					btn.put("name",	"查看项目");
					btn.put("url", "/f/project/projectDeclare/viewForm?id="+pid);
					content.put("btn_option",btn);
					contents.add(content);
				}
				/*立项节点**************************************************/
				JSONObject content1=new JSONObject();
				content1.put("type", "milestone");
				content1.put("title", "项目立项");
				content1.put("desc", map.get("p_init_start")+"~"+map.get("p_init_end"));
				content1.put("start_date", map.get("p_init_start"));
				content1.put("end_date",	map.get("p_init_end"));
				content1.put("intro", "");
				if (!StringUtil.isEmpty(umap.get("level"))) {
					content1.put("approvalTime", umap.get("approval_date"));
					content1.put("approvalDesc", umap.get("level_str"));
					content1.put("current",	"true");
				}
				contents.add(content1);
				/*中期检查节点**********************************************/
				if (st<3) {//待提交中期报告之前
					JSONObject content=new JSONObject();
					content.put("type", "milestone");
					content.put("title", "中期检查");
					content.put("desc", map.get("mid_start_date")+"~"+map.get("mid_end_date"));
					content.put("start_date", map.get("mid_start_date"));
					content.put("end_date",	map.get("mid_end_date"));
					content.put("intro", "");
					contents.add(content);
				}else if (st==3) {//待提交中期报告
					boolean valid=checkIsValid(map.get("mid_start_date"), map.get("mid_end_date"));
					JSONObject content=new JSONObject();
					content.put("type", "milestone");
					content.put("title", "中期检查");
					content.put("desc", map.get("mid_start_date")+"~"+map.get("mid_end_date"));
					content.put("start_date", map.get("mid_start_date"));
					content.put("end_date",	map.get("mid_end_date"));
					content.put("intro", "");
					if(valid){
						content.put("current",	"true");
					}
					if (createBy.equals(user.getId())&&valid) {
						JSONObject btn=new JSONObject();
						btn.put("name","提交中期检查报告");
						btn.put("url", "/f/project/proMid/creatMid?projectId="+pid);
						content.put("btn_option",btn);
					}
					contents.add(content);
				}else if (st==4) {//待修改中期报告
					ProMid pm=proMidDao.getByProjectId(pid);
					JSONObject content=new JSONObject();
					content.put("type", "milestone");
					content.put("title", "中期检查");
					content.put("desc", map.get("mid_start_date")+"~"+map.get("mid_end_date"));
					content.put("start_date", map.get("mid_start_date"));
					content.put("end_date",	map.get("mid_end_date"));
					content.put("intro", "");
					content.put("current",	"true");
					if (pm!=null) {
						if (createBy.equals(user.getId())||"2".equals(user.getUserType())) {
								JSONObject btn=new JSONObject();
								btn.put("name","修改中期检查报告");
								btn.put("url", "/f/project/proMid/edit?id="+pm.getId());
								content.put("btn_option",btn);
						}else{
								JSONObject btn=new JSONObject();
								btn.put("name","中期检查报告");
								btn.put("url", "/f/project/proMid/view?id="+pm.getId());
								content.put("btn_option",btn);
						}
					}
					contents.add(content);
				}else if (st>=5) {//中期检查之后
					ProMid pm=proMidDao.getByProjectId(pid);
					JSONObject content=new JSONObject();
					content.put("type", "milestone");
					content.put("title", "中期检查");
					content.put("desc", map.get("mid_start_date")+"~"+map.get("mid_end_date"));
					content.put("start_date", map.get("mid_start_date"));
					content.put("end_date",	map.get("mid_end_date"));
					content.put("intro", "");
					content.put("current",	"true");
					if (pm!=null) {
						if ("2".equals(user.getUserType())) {
							JSONObject btn=new JSONObject();
							btn.put("name","中期检查报告");
							btn.put("url", "/f/project/proMid/edit?id="+pm.getId());
							content.put("btn_option",btn);
						}else{
							JSONObject btn=new JSONObject();
							btn.put("name","中期检查报告");
							btn.put("url", "/f/project/proMid/view?id="+pm.getId());
							content.put("btn_option",btn);
						}
					}
					contents.add(content);
				}
				/*结项节点****************************************************/
				if (st<6) {//待提交结项报告之前
					JSONObject content=new JSONObject();
					content.put("type", "milestone");
					content.put("title", "项目结项");
					content.put("desc", map.get("final_start_date")+"~"+map.get("final_end_date"));
					content.put("start_date", map.get("final_start_date"));
					content.put("end_date",	map.get("final_end_date"));
					content.put("intro", "");
					contents.add(content);
				}else if (st==6) {//待提交结项报告
					boolean valid=checkIsValid(map.get("final_start_date"), map.get("final_end_date"));
					JSONObject content=new JSONObject();
					content.put("type", "milestone");
					content.put("title", "项目结项");
					content.put("desc", map.get("final_start_date")+"~"+map.get("final_end_date"));
					content.put("start_date", map.get("final_start_date"));
					content.put("end_date",	map.get("final_end_date"));
					content.put("intro", "");
					if(valid){
						content.put("current",	"true");
					}
					if (createBy.equals(user.getId())&&valid) {
						JSONObject btn=new JSONObject();
						btn.put("name","提交结项报告");
						btn.put("url", "/f/project/projectClose/createClose?projectId="+pid);
						content.put("btn_option",btn);
					}
					contents.add(content);
				}else if (st>6) {//提交结项报告之后
					ProjectClose pc=projectCloseDao.getByProjectId(pid);
					JSONObject content=new JSONObject();
					content.put("type", "milestone");
					content.put("title", "项目结项");
					content.put("desc", map.get("final_start_date")+"~"+map.get("final_end_date"));
					content.put("start_date", map.get("final_start_date"));
					content.put("end_date",	map.get("final_end_date"));
					content.put("intro", "");
					content.put("current",	"true");
					if (pc!=null) {
						if ("2".equals(user.getUserType())) {
							JSONObject btn=new JSONObject();
							btn.put("name","结项报告");
							btn.put("url", "/f/project/projectClose/edit?id="+pc.getId());
							content.put("btn_option",btn);
						}else{
							JSONObject btn=new JSONObject();
							btn.put("name","结项报告");
							btn.put("url", "/f/project/projectClose/view?id="+pc.getId());
							content.put("btn_option",btn);
						}
					}
					contents.add(content);
				}
//				if (st>=3) {//立项之后，才有周报等信息
					List<Map<String, String>> wlist=projectWeeklyDao.getInfoByProjectId(pid);
					fillWeeklys(wlist, files,user);//填装周报信息
//				}
			}else{//最近一次项目信息里无关联的项目通告，Excel导入时产生的数据
				List<Map<String, String>> alist=dao.getValidProjectAnnounce();
				if (alist!=null&&alist.size()!=0) {//有可用的国创项目通告才能继续
					Map<String, String> map=alist.get(0);
					JSONObject content=new JSONObject();
					content.put("type", "milestone");
					content.put("title", "项目申报");
					content.put("desc", map.get("begin_date")+"~"+map.get("end_date"));
					content.put("start_date", map.get("begin_date"));
					content.put("end_date",	map.get("end_date"));
					content.put("intro", "");
					content.put("current",	"true");
					if ("1".equals(user.getUserType())) {
						JSONObject btn=new JSONObject();
						btn.put("name",	"创建项目");
						btn.put("url", "/f/project/projectDeclare/form");
						content.put("btn_option",btn);
					}
					contents.add(content);
					
					JSONObject content1=new JSONObject();
					content1.put("type", "milestone");
					content1.put("title", "项目立项");
					content1.put("desc", map.get("p_init_start")+"~"+map.get("p_init_end"));
					content1.put("start_date", map.get("p_init_start"));
					content1.put("end_date",	map.get("p_init_end"));
					content1.put("intro", "");
					contents.add(content1);
					
					JSONObject content2=new JSONObject();
					content2.put("type", "milestone");
					content2.put("title", "中期检查");
					content2.put("desc", map.get("mid_start_date")+"~"+map.get("mid_end_date"));
					content2.put("start_date", map.get("mid_start_date"));
					content2.put("end_date",	map.get("mid_end_date"));
					content2.put("intro", "");
					contents.add(content2);
					
					JSONObject content3=new JSONObject();
					content3.put("type", "milestone");
					content3.put("title", "项目结项");
					content3.put("desc", map.get("final_start_date")+"~"+map.get("final_end_date"));
					content3.put("start_date", map.get("final_start_date"));
					content3.put("end_date",	map.get("final_end_date"));
					content3.put("intro", "");
					contents.add(content3);
				}
			}
		}else{//无最近一次项目信息
			List<Map<String, String>> list=dao.getValidProjectAnnounce();
			if (list!=null&&list.size()!=0) {//有可用的国创项目通告才能继续
				Map<String, String> map=list.get(0);
				JSONObject content=new JSONObject();
				content.put("type", "milestone");
				content.put("title", "项目申报");
				content.put("desc", map.get("begin_date")+"~"+map.get("end_date"));
				content.put("start_date", map.get("begin_date"));
				content.put("end_date",	map.get("end_date"));
				content.put("intro", "");
				content.put("current",	"true");
				if ("1".equals(user.getUserType())) {
					JSONObject btn=new JSONObject();
					btn.put("name",	"创建项目");
					btn.put("url", "/f/project/projectDeclare/form");
					content.put("btn_option",btn);
				}
				contents.add(content);
				
				JSONObject content1=new JSONObject();
				content1.put("type", "milestone");
				content1.put("title", "项目立项");
				content1.put("desc", map.get("p_init_start")+"~"+map.get("p_init_end"));
				content1.put("start_date", map.get("p_init_start"));
				content1.put("end_date",	map.get("p_init_end"));
				content1.put("intro", "");
				contents.add(content1);
				
				JSONObject content2=new JSONObject();
				content2.put("type", "milestone");
				content2.put("title", "中期检查");
				content2.put("desc", map.get("mid_start_date")+"~"+map.get("mid_end_date"));
				content2.put("start_date", map.get("mid_start_date"));
				content2.put("end_date",	map.get("mid_end_date"));
				content2.put("intro", "");
				contents.add(content2);
				
				JSONObject content3=new JSONObject();
				content3.put("type", "milestone");
				content3.put("title", "项目结项");
				content3.put("desc", map.get("final_start_date")+"~"+map.get("final_end_date"));
				content3.put("start_date", map.get("final_start_date"));
				content3.put("end_date",	map.get("final_end_date"));
				content3.put("intro", "");
				contents.add(content3);
			}
		}
		data.put("contents", contents);
		data.put("files", files);
		return data;
	}
	private void fillWeeklys(List<Map<String, String>> wlist,JSONArray files,User user) {
		if (wlist!=null) {
			for(Map<String, String> map:wlist) {
				JSONObject content=new JSONObject();
				content.put("name", "项目周报"+map.get("start_date")+"~"+map.get("end_date"));
				if ("2".equals(user.getUserType())||map.get("create_by").equals(user.getId())) {
					content.put("url", "/f/project/weekly/createWeekly?id="+map.get("id"));
				}else{
					content.put("url", "/f/project/weekly/view?id="+map.get("id"));
				}
				content.put("create_date",map.get("create_date"));
				files.add(content);
			}
		}
	}
	private void fillPlans(List<ProjectPlan> plans,JSONArray contents) {
		if (plans!=null) {
			for(ProjectPlan p:plans) {
				JSONObject content=new JSONObject();
				content.put("type", "date");
				content.put("desc", DateUtil.formatDate(p.getStartDate())+"~"+DateUtil.formatDate(p.getEndDate()));
				content.put("start_date", DateUtil.formatDate(p.getStartDate()));
				content.put("end_date",	DateUtil.formatDate(p.getEndDate()));
				content.put("intro", p.getContent());
				contents.add(content);
			}
		}
	}
	public ProjectDeclare get(String id) {
		return super.get(id);
	}
	public Page<Map<String,String>>  getMyProjectList(Page<Map<String,String>> page,Map<String,Object> param) {
		if (page.getPageNo()<=0) {
			page.setPageNo(1);
		}
		int count=dao.getMyProjectListCount(param);
		param.put("offset", (page.getPageNo()-1)*page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String,String>> list=null;
		if (count>0) {
			list=dao.getMyProjectList(param);
			if (list!=null) {
				List<String> ids=new ArrayList<String>();
				for(Map<String,String> map:list) {
					ids.add(map.get("id"));
					map.put("status", ProjectStatusEnum.getNameByValue(map.get("status")));
					map.put("final_result", ProjectFinalResultEnum.getNameByValue(map.get("final_result")));
				}
				List<Map<String,String>> ps=dao.getMyProjectListPerson(ids);
				if (ps!=null&&ps.size()>0) {
					Map<String,String> psm=new HashMap<String,String>();
					for(Map<String,String> map:ps) {
						psm.put(map.get("id")+map.get("team_user_type"), map.get("pname"));
					}
					for(Map<String,String> map:list) {
						map.put("snames", psm.get(map.get("id")+"1"));
						map.put("tnames", psm.get(map.get("id")+"2"));
					}
				}
			}
		}
		page.setCount(count);
		page.setList(list);
		page.initialize();
		return page;
	}
	public ProjectDeclareVo getProjectDeclareVo(String projectid) {
		if (projectid==null) {
			return null;
		}else{
			ProjectDeclare pd=dao.get(projectid);
			ProjectDeclareVo vo=new ProjectDeclareVo();
			vo.setProjectDeclare(pd);
			vo.setPlans(projectPlanDao.findListByProjectId(projectid));
			vo.setTeamStudent(dao.findTeamStudent(pd.getTeamId()));
			vo.setTeamTeacher(dao.findTeamTeacher(pd.getTeamId()));
			return vo;
		}
	}

	/**
	 * 项目查询 updateBy zhangzheng
	 * @param projectDeclare
	 * @return
     */
	public List<ProjectDeclare> findList(ProjectDeclare projectDeclare) {

		return super.findList(projectDeclare);
	}
	
	public Page<ProjectDeclare> findPage(Page<ProjectDeclare> page, ProjectDeclare projectDeclare) {
		return super.findPage(page, projectDeclare);
	}
	public List<Map<String,String>> findTeamStudent(String teamid) {
		if (teamid==null) {
			return null;
		}else{
			return dao.findTeamStudent(teamid);
		}
	}
	public List<Map<String,String>> findTeamTeacher(String teamid) {
		if (teamid==null) {
			return null;
		}else{
			return dao.findTeamTeacher(teamid);
		}
	}
	public Map<String,String> getProjectAuditInfo(String projectId) {
		Map<String,String> psm=new HashMap<String,String>();
		List<Map<String, String>>  list1=dao.getProjectAuditResult(projectId);
		List<Map<String, String>>  list2=dao.getProjectAuditInfo(projectId);
		if (list1!=null) {
			Map<String, String> map=list1.get(0);
			if (map!=null) {
				psm.put("level",  map.get("level"));
				psm.put("mid_result",  ProjectMidResultEnum.getNameByValue(map.get("mid_result")));
				psm.put("final_result",  ProjectFinalResultEnum.getNameByValue(map.get("final_result")));
			}
		}
		if (list2!=null) {
			for(Map<String, String> map:list2) {
				if ("1".equals(map.get("audit_step"))) {
					psm.put("level_s",  map.get("suggest"));
					psm.put("level_d",  map.get("create_date"));
				}
				if ("3".equals(map.get("audit_step"))) {
					psm.put("mid_s",  map.get("suggest"));
					psm.put("mid_d",  map.get("create_date"));			
				}
				if ("4".equals(map.get("audit_step"))) {
					psm.put("final_s",  map.get("suggest"));
					psm.put("final_d",  map.get("create_date"));
				}
			}
		}
		return psm;
	}
	public List<Team> findTeams(String userid,String teamid) {
		return dao.findTeams(userid,teamid);
	}
	@Transactional(readOnly = false)
	public void save(ProjectDeclare projectDeclare) {
		super.save(projectDeclare);
	}


	//工作流查询的基本信息
	public HashMap<String,Object> getVars(String projectId) {
		ProjectDeclare project=dao.getVars(projectId);
		HashMap<String,Object> vars=new HashMap<String,Object>();
		vars.put("id",project.getId()); //项目id
		vars.put("number",project.getNumber());  // 项目编号
		vars.put("name",project.getName()); //项目名称
		vars.put("type",project.getType());  //项目类型
		vars.put("leader",project.getLeaderString());  //项目负责人
		vars.put("teamList",project.getSnames());  // 项目组成员
		vars.put("teacher",project.getTnames());  // 指导老师
		return vars;
	}

	/**
	 * 国创项目提交，触发工作流
	 * @param projectDeclare  必须包含id,createById
	 * @Author zhangzheng
     */
	@Transactional(readOnly = false)
	public void startPojectProcess(ProjectDeclare projectDeclare) {
		User user=userDao.get(projectDeclare.getCreateBy().getId());
		identityService.setAuthenticatedUserId(user.getLoginName());
		String procDefKey="state_project_audit";
		String businessTable="project_declare";
		String businessId=projectDeclare.getId();
		HashMap<String,Object> vars=getVars(businessId);
		List<String> claims=userService.getCollegeSecs(projectDeclare.getCreateBy().getId());
		vars.put("collegeSec",claims); //给学院教学秘书审批
		ProcessInstance procIns = runtimeService.startProcessInstanceByKey(procDefKey, businessTable+":"+businessId, vars);
		Act act = new Act();
		act.setBusinessTable(businessTable);// 业务表名
		act.setBusinessId(businessId);	// 业务表ID
		act.setProcInsId(procIns.getId());
		actDao.updateProcInsIdByBusinessId(act);

		//下个节点 自动签收
		actTaskService.claimByProcInsId(procIns.getId(),claims);
	}


	//addBy zhangzheng 院级立项评审  保存评审意见到子表 保存评级结果 执行一步工作流
	@Transactional(readOnly = false)
	public void  collegeSetSave(ProjectDeclare projectDeclare) {
		ProjectAuditInfo pai=new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId());  //主表id
		pai.setAuditStep("1");  //保存评级步骤 1代表立项审核 见字典值audit_step
		//如果level为c级、未通过则需要更新数据库的level。如果为其他则需要提交给校级评审，不需要更新状态
		if ("4".equals(projectDeclare.getLevel())||"5".equals(projectDeclare.getLevel())) {
			pai.setGrade(projectDeclare.getLevel());
		}
		if ("2".equals(projectDeclare.getLevel())) {
			pai.setGrade("6");
		}

		//保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);


		//完成工作流
		HashMap<String,Object> vars=new HashMap<String,Object>();
		List<String>  claims=new ArrayList<String>();
		if ("2".equals(projectDeclare.getLevel())) {//如果是2则是提交给学校评审
			//根据projectDeclare.id 查找vars 提交给学校立项审核 查询用
			vars=getVars(projectDeclare.getId());
			claims=userService.getSchoolSecs();
			vars.put("schoolSec",claims);
		}

		vars.put("grade",projectDeclare.getLevel());

		taskService.complete(projectDeclare.getAct().getTaskId(), vars);

	    //下个节点 自动签收
		actTaskService.claimByProcInsId(projectDeclare.getProcInsId(),claims);


		// 改变主表的审核状态
		if ("5".equals(projectDeclare.getLevel())) {  //不合格
			projectDeclare.preUpdate();
			//(0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
			projectDeclare.setStatus("8");
			// 项目结果(0合格，1优秀，2不合格，3立项不合格，4中期不合格）
			projectDeclare.setFinalResult("3"); //立项不合格
			projectDeclare.setApprovalDate(new Date());
			dao.updateStatus(projectDeclare);
			//给项目负责人发送消息 您的项目审核未通过（不合格项目）
			User apply_User= UserUtils.getUser();
			User rec_User=new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"项目审核通知","您的项目审核未通过（不合格项目）","","");
		}
		if ("4".equals(projectDeclare.getLevel())) { //c级
			projectDeclare.preUpdate();
			projectDeclare.setStatus("3"); //设置状态为 待提交中期报告
			projectDeclare.setApprovalDate(new Date());
			dao.updateStatus(projectDeclare);
			String number="C"+projectDeclare.getNumber();
			projectDeclare.setNumber(number);
			dao.updateNumber(projectDeclare);
			//给项目负责人发送消息 学院已对您的项目评级（C级项目）；
			User apply_User= UserUtils.getUser();
			User rec_User=new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"项目审核通知","学院已对您的项目评级（C级项目）","","");
		}
		if ("2".equals(projectDeclare.getLevel())) { //校级
			projectDeclare.preUpdate();
			projectDeclare.setLevel("");  //评级为空，表示需要校级评
			//(0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
			projectDeclare.setStatus("2"); //待学校立项审核
			dao.updateStatus(projectDeclare);
			//给项目负责人发送消息 学院已提交项目到校级（校级评级项目）；
			User apply_User= UserUtils.getUser();
			User rec_User=new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"项目审核通知","学院已提交项目到校级（校级评级项目）","","");

		}


	}


	//addBy zhangzheng 校级立项评审  保存评审意见到子表  执行一步工作流 保存评级结果
	@Transactional(readOnly = false)
	public void  schoolSetSave(ProjectDeclare projectDeclare) {
		ProjectAuditInfo pai=new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId());  //主表id
		pai.setAuditStep("1");  //保存评级步骤 1代表立项审核 见字典值audit_step
		pai.setGrade(projectDeclare.getLevel());  //保存评级
		//保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);

		//完成工作流
		HashMap<String,Object> vars=new HashMap<String,Object>();
		taskService.complete(projectDeclare.getAct().getTaskId(), vars);

		//如果审核不通过  改变主表的审核状态
		projectDeclare.preUpdate();
		//(0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
		projectDeclare.setStatus("3");
		projectDeclare.setApprovalDate(new Date());
		dao.updateStatus(projectDeclare);

		String number="";
		if ("1".equals(projectDeclare.getLevel())) { //A+
			number="A+"+projectDeclare.getNumber();
			projectDeclare.setNumber(number);
			//给项目负责人发送消息 学校已对您的项目评级（A+级、A级或B级项目）
			User apply_User= UserUtils.getUser();
			User rec_User=new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"项目审核通知","学校已对您的项目评级（A+级项目）","","");
		}
		if ("2".equals(projectDeclare.getLevel())) { //A
			number="A"+projectDeclare.getNumber();
			projectDeclare.setNumber(number);
			//给项目负责人发送消息 学校已对您的项目评级（A+级、A级或B级项目）
			User apply_User= UserUtils.getUser();
			User rec_User=new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"项目审核通知","学校已对您的项目评级（A级项目）","","");
		}
		if ("3".equals(projectDeclare.getLevel())) { //B
			 number="B"+projectDeclare.getNumber();
			projectDeclare.setNumber(number);
			//给项目负责人发送消息 学校已对您的项目评级（A+级、A级或B级项目）
			User apply_User= UserUtils.getUser();
			User rec_User=new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"项目审核通知","学校已对您的项目评级（B级项目）","","");
		}
		dao.updateNumber(projectDeclare);

	}




	/**
	 * 提交中期报告，触发工作流
	 * @param projectDeclare 必须包含id,level,procInsId
	 * @Author zhangzheng
     */
	@Transactional(readOnly = false)
	public void midSave(ProjectDeclare projectDeclare) {

		//封装 工作流的vars
		HashMap<String,Object> vars = getVars(projectDeclare.getId());  //工作流下一步需要展示的字段
		vars.put("level",projectDeclare.getLevel());
		//查询工作流下一步需要审批的人
		List<String> claims=new ArrayList<String>();
		if ("1".equals(projectDeclare.getLevel())||"2".equals(projectDeclare.getLevel())) {  //A级、A+级院级 校级审批
			claims=userService.getSchoolExperts();  //找到校级专家；
			vars.put("schoolExperts",claims);
		}
		if ("3".equals(projectDeclare.getLevel())||"4".equals(projectDeclare.getLevel())) {  //B级、B+级院级 院级审批
			claims=userService.getCollegeExperts(projectDeclare.getCreateBy().getId());  //根据创建人id，找到院级专家；
			vars.put("collegeExperts",claims);
		}
		vars.put("grade",projectDeclare.getLevel()); //网关流转条件
		String taskId=actTaskService.getTaskidByProcInsId(projectDeclare.getProcInsId());
		taskService.complete(taskId, vars); //执行工作流

		projectDeclare.preUpdate();
		//(0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
		projectDeclare.setStatus("5");
		dao.updateStatus(projectDeclare);



	}





	//addBy zhangzheng 中期评分    执行一步工作流 保存评分、意见到子表
	@Transactional(readOnly = false)
	public void expCheckSave(ProjectDeclare projectDeclare) {

		//完成工作流
		//判断当前节点是否是最后一个、如果是把平均分计算出来，保存到主表中和工作流中
		String taskDefinitionKey="";
		//判断项目等级 A+、A给学校秘书评级 taskDefinitionKey为middleScore2 (参见流程图的设置）
		if ("1".equals(projectDeclare.getLevel())||"2".equals(projectDeclare.getLevel())) {
			taskDefinitionKey="middleScore2";
		}
		if ("3".equals(projectDeclare.getLevel())||"4".equals(projectDeclare.getLevel())) {
			taskDefinitionKey="middleScore3";
		}
		boolean isLast=actTaskService.isMultiLast("state_project_audit",taskDefinitionKey,projectDeclare.getProcInsId());
		HashMap<String,Object> vars=new HashMap<String, Object>();
		if (isLast) {
			//取出原来的评分+现在的评分 ，取平均分
			ProjectAuditInfo infoSerch=new ProjectAuditInfo();
			infoSerch.setProjectId(projectDeclare.getId());
			infoSerch.setAuditStep("2");
			List<ProjectAuditInfo> infos= projectAuditInfoService.getInfo(infoSerch);
			float total=0;
			float average=0;
			int number=0;
			if (infos==null||infos.size()==0) {
				average=projectDeclare.getMidScore();
			}else{
				total=total+projectDeclare.getMidScore();
				number++;
				for (ProjectAuditInfo info:infos) {
					total=total+info.getScore();
					number++;
				}
				average= FloatUtils.division(total,number);
			}
			vars=getVars(projectDeclare.getId());
			vars.put("level",projectDeclare.getLevel());
			vars.put("midScore",average);
			vars.put("scoreInt",FloatUtils.getInt(average));
			vars.put("scorePoint",FloatUtils.getPoint(average));
			//更新主表中期评分分数
			ProjectDeclare pd=new ProjectDeclare();
			pd.setMidScore(average);
			pd.setId(projectDeclare.getId());
			dao.updateMidScore(pd);

			List<String> claims=new ArrayList<String>();
			if ("1".equals(projectDeclare.getLevel())||"2".equals(projectDeclare.getLevel())) {  //判断项目等级 A+、A给学校秘书评级
				claims=userService.getSchoolSecs();
				vars.put("schoolSec",claims);
			}
			if ("3".equals(projectDeclare.getLevel())||"4".equals(projectDeclare.getLevel())) {  //判断项目等级 B、C给学院秘书评级
				claims=userService.getCollegeSecs(projectDeclare.getCreateBy().getId());
				vars.put("collegeSec",claims);
			}
			taskService.complete(projectDeclare.getAct().getTaskId(), vars);

			//下个节点 自动签收
			actTaskService.claimByProcInsId(projectDeclare.getProcInsId(),claims);

		}else{
			taskService.complete(projectDeclare.getAct().getTaskId(), vars);
		}



		ProjectAuditInfo pai=new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId());  //主表id
		pai.setAuditStep("2");  //保存评级步骤 2代表中期评分 见字典值audit_step
		//评分
		pai.setScore(projectDeclare.getMidScore());
		//保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);


	}

	// 中期审核
	// 如果整改给 打回前删除中期评分的信息、执行工作流、更改主表midCount+1
	// 如果不整改 保存中期审核评级、执行工作流、更改主表状态
	@Transactional(readOnly = false)
	public void secCheckSave(ProjectDeclare projectDeclare) {
      if ("4".equals(projectDeclare.getPass())) {  //pass==4表示需要整改
		//删除中期评分的信息
		//处理评分 评分保存到子表中 project_audit_info
		ProjectAuditInfo pai=new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId());
	    pai.setAuditStep("2"); // 2代表中期检查评分
		projectAuditInfoService.deleteByPidAndStep(pai);
		//执行工作流
		HashMap<String,Object> vars=new HashMap<String,Object>();
		vars.put("pass",projectDeclare.getPass());
		taskService.complete(projectDeclare.getAct().getTaskId(), vars);

		projectDeclare.setMidCount(1);  //表示整改过一次
		dao.updateMidCount(projectDeclare);
		projectDeclare.preUpdate();
		//(0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
		projectDeclare.setStatus("4");
		dao.updateStatus(projectDeclare);
	  }else{//如果不整改 保存中期审核评级、执行工作流、改变主表状态

		  ProjectAuditInfo pai=new ProjectAuditInfo();
		  pai.setProjectId(projectDeclare.getId());  //主表id
		  pai.setAuditStep("3");  //审核阶段：1立项评审 2中期检查评分 3中期检查评级 4结项审核 5结项评级
		  pai.setSuggest(projectDeclare.getComment());
		  pai.setGrade(projectDeclare.getMidResult());  //保存评级
		  projectAuditInfoService.save(pai);


		  //完成工作流
		  HashMap<String,Object> vars=new HashMap<String,Object>();
		  vars.put("pass",projectDeclare.getMidResult());
		  vars.put("level",projectDeclare.getLevel());
		  taskService.complete(projectDeclare.getAct().getTaskId(), vars);

		  projectDeclare.preUpdate();
		  if ("0".equals(projectDeclare.getMidResult())) { //合格
			 //(0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
			 projectDeclare.setStatus("6");
		  }
		  if ("2".equals(projectDeclare.getMidResult())) {  //不合格
			  projectDeclare.setFinalResult("4");  //中期不合格
			  projectDeclare.setStatus("8");
		  }
		  dao.updateStatus(projectDeclare);
	  }
	}



	/**
	 * 提交结项报告，触发工作流
	 * @param projectDeclare 必须包含id,level,procInsId
	 * @Author zhangzheng
     */
	@Transactional(readOnly = false)
	public void closeSave(ProjectDeclare projectDeclare) {
		//封装 工作流的vars
		HashMap<String,Object> vars = getVars(projectDeclare.getId());  //工作流下一步需要展示的字段
		vars.put("level",projectDeclare.getLevel());
		//查询工作流下一步需要审批的人
		List<String> claims=new ArrayList<String>();
		if ("1".equals(projectDeclare.getLevel())||"2".equals(projectDeclare.getLevel())) {  //A级、A+级院级 校级审批
			 claims=userService.getSchoolExperts();  //找到校级专家；
			vars.put("schoolExperts",claims);

		}
		if ("3".equals(projectDeclare.getLevel())||"4".equals(projectDeclare.getLevel())) {  //B级、B+级院级 院级审批
			claims=userService.getCollegeExperts(projectDeclare.getCreateBy().getId());  //根据创建人id，找到院级专家；
			vars.put("collegeExperts",claims);
		}
		vars.put("grade",projectDeclare.getLevel()); //网关流转条件
		String  taskId=actTaskService.getTaskidByProcInsId(projectDeclare.getProcInsId());
		taskService.complete(taskId, vars); //执行工作流

		projectDeclare.preUpdate();
		//(0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
		projectDeclare.setStatus("7");
		dao.updateStatus(projectDeclare);

	}



	//addBy zhangzheng 结项评分    执行一步工作流 保存评分、意见到子表
	@Transactional(readOnly = false)
	public void expCloseSave(ProjectDeclare projectDeclare) {
		//完成工作流
		//判断当前节点是否是最后一个、如果是把平均分计算出来，保存到主表中和工作流中
		String taskDefinitionKey="";
		//判断项目等级 A+、A给学校秘书评级 taskDefinitionKey为closeScore2 (参见流程图的设置）
		if ("1".equals(projectDeclare.getLevel())||"2".equals(projectDeclare.getLevel())) {
			taskDefinitionKey="closeScore2";
		}
		if ("3".equals(projectDeclare.getLevel())||"4".equals(projectDeclare.getLevel())) {
			taskDefinitionKey="closeScore3";
		}
		boolean isLast=actTaskService.isMultiLast("state_project_audit",taskDefinitionKey,projectDeclare.getProcInsId());
		HashMap<String,Object> vars=new HashMap<String, Object>();
		if (isLast) {
			//取出原来的评分+现在的评分 ，取平均分
			ProjectAuditInfo infoSerch=new ProjectAuditInfo();
			infoSerch.setProjectId(projectDeclare.getId());
			// 审核阶段：1立项评审 2中期检查评分 3中期检查评级 4结项审核 5结项评级
			infoSerch.setAuditStep("4");
			List<ProjectAuditInfo> infos= projectAuditInfoService.getInfo(infoSerch);
			float total=0;
			float average=0;
			int number=0;
			if (infos==null||infos.size()==0) {
				average=projectDeclare.getFinalScore();
			}else{
				total=total+projectDeclare.getFinalScore();
				number++;
				for (ProjectAuditInfo info:infos) {
					total=total+info.getScore();
					number++;
				}
				average= FloatUtils.division(total,number);
			}
			vars=getVars(projectDeclare.getId());
			vars.put("level",projectDeclare.getLevel());
			vars.put("finalScore",average);
			//更新主表结项评分分数
			ProjectDeclare pd=new ProjectDeclare();
			pd.setFinalScore(average);
			pd.setId(projectDeclare.getId());
			dao.updateFinalScore(pd);
            List<String> claims=new ArrayList<String>();
			if ("1".equals(projectDeclare.getLevel())||"2".equals(projectDeclare.getLevel())) {  //判断项目等级 A+、A给学校秘书评级
				claims=userService.getSchoolSecs();
				vars.put("schoolSec",claims);
			}
			if ("3".equals(projectDeclare.getLevel())||"4".equals(projectDeclare.getLevel())) {  //判断项目等级 B、C给学院秘书评级
				claims=userService.getCollegeSecs(projectDeclare.getCreateBy().getId());
				vars.put("collegeSec",claims);
			}

			taskService.complete(projectDeclare.getAct().getTaskId(), vars);
			//下个节点 自动签收
			actTaskService.claimByProcInsId(projectDeclare.getProcInsId(),claims);

		}else{
			taskService.complete(projectDeclare.getAct().getTaskId(), vars);
		}



		ProjectAuditInfo pai=new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId());  //主表id
		// 审核阶段：1立项评审 2中期检查评分 3中期检查评级 4结项审核 5结项评级
		pai.setAuditStep("4");
		//评分
		pai.setScore(projectDeclare.getFinalScore());
		//保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);


	}

	//结项答辩分保存  执行工作流 保存子表评级意见信息  主表记录答辩分 自动签收
	@Transactional(readOnly = false)
	public void secCloseSave(ProjectDeclare projectDeclare) {
		HashMap<String,Object> vars=getVars(projectDeclare.getId());
		List<String> claims=new ArrayList<String>();
		if ("1".equals(projectDeclare.getLevel())||"2".equals(projectDeclare.getLevel())) {  //判断项目等级 A+、A给学校秘书评级
			claims=userService.getSchoolSecs();
			vars.put("sec",claims);
		}
		if ("3".equals(projectDeclare.getLevel())||"4".equals(projectDeclare.getLevel())) {  //判断项目等级 B、C给学院秘书评级
			claims=userService.getCollegeSecs(projectDeclare.getCreateBy().getId());
			vars.put("sec",claims);
		}

		vars.put("replyScore",projectDeclare.getReplyScore());
		//执行工作流
		taskService.complete(projectDeclare.getAct().getTaskId(),vars);

		//保存子表答辩分及意见信息
		ProjectAuditInfo pai=new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId());  //主表id
		// 审核阶段：1立项评审 2中期检查评分 3中期检查评级 4结项审核 5结项评级 6结果评定
		pai.setAuditStep("5");
		pai.setScore(projectDeclare.getReplyScore());
		//保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);

	   //主表记录答辩分
		projectDeclare.preUpdate();
		dao.updateStatus(projectDeclare);


        //下个节点 自动签收
		actTaskService.claimByProcInsId(projectDeclare.getProcInsId(),claims);

	}

	//结果评定  执行工作流 保存子表 修改主表状态
	@Transactional(readOnly = false)
	public void secAssessSave(ProjectDeclare projectDeclare) {
		//执行工作流
		taskService.complete(projectDeclare.getAct().getTaskId());

		//保存子表答辩分及意见信息
		ProjectAuditInfo pai=new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId());  //主表id
		// 审核阶段：1立项评审 2中期检查评分 3中期检查评级 4结项审核 5结项评级 6结果评定
		pai.setAuditStep("6");
		//评级 合格、不合格、优秀
		pai.setGrade(projectDeclare.getFinalResult());
		//保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);

		//修改主表状态
		projectDeclare.preUpdate();
		//(0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止,9.项目结项)
		projectDeclare.setStatus("9");
		dao.updateStatus(projectDeclare);


	}


	@Transactional(readOnly = false)
	public void saveProjectDeclareVo(ProjectDeclareVo vo) throws ProjectNameDuplicateException {
		vo.getProjectDeclare().setStatus(ProjectStatusEnum.S0.getValue());
		if (StringUtil.isEmpty(vo.getProjectDeclare().getNumber())) {
			vo.getProjectDeclare().setNumber(IdUtils.getProjectNumberByDb());
		}
		try {
			if (checkProjectName(vo.getProjectDeclare())) {//违反项目名-创建人唯一约束
				throw new ProjectNameDuplicateException();
			}
			super.save(vo.getProjectDeclare());
		} catch (DuplicateKeyException e) {
			if (e.getCause().getMessage().indexOf("'project_num'")!=-1) {//违反项目编号唯一约束
				saveProjectDeclareVo(vo);
			}
		}
		projectPlanDao.deleteByProjectId(vo.getProjectDeclare().getId());
		int sort=0;
		if (vo.getPlans()!=null) {
			for(ProjectPlan plan:vo.getPlans()) {
				plan.setSort(sort+"");
				plan.setId(IdGen.uuid());
				plan.setProjectId(vo.getProjectDeclare().getId());
				plan.setCreateDate(new Date());
				projectPlanDao.insert(plan);
				sort++;
			}
		}
		if (vo.getFileInfo()!=null) {
			for(Map<String,String> map:vo.getFileInfo()) {
				SysAttachment sa=new SysAttachment();
				sa.setUid(vo.getProjectDeclare().getId());
				sa.setName(map.get("arrName"));
				String[] ss=map.get("arrName").split("\\.");
				sa.setSuffix(ss[ss.length-1]);
				sa.setType(FileSourceEnum.S0.getValue());
				sa.setFileStep(FileTypeEnum.S100.getValue());
				FtpUtil t = new FtpUtil();
				try {
					sa.setUrl(t.moveFile(t.getftpClient(),map.get("arrUrl")));
				} catch (Exception e) {
					e.printStackTrace();
				}
				sysAttachmentService.save(sa);
			}
		}
	}

	private boolean checkProjectName(ProjectDeclare p) {
		Map<String,String> map=new HashMap<String,String>();
		map.put("name", p.getName());
		map.put("cby", p.getCreateBy().getId());
		if (!StringUtil.isEmpty(p.getId())) {
			map.put("id", p.getId());
		}
		if (dao.getProjectByName(map)>0) {
			return true;
		}
		return false;
	}
	@Transactional(readOnly = false)
	public void submitProjectDeclareVo(ProjectDeclareVo vo) throws ProjectNameDuplicateException {
		vo.getProjectDeclare().setStatus(ProjectStatusEnum.S1.getValue());
		vo.getProjectDeclare().setApplyTime(new Date());
		if (StringUtil.isEmpty(vo.getProjectDeclare().getNumber())) {
			vo.getProjectDeclare().setNumber(IdUtils.getProjectNumberByDb());
		}
		try {
			if (checkProjectName(vo.getProjectDeclare())) {//违反项目名-创建人唯一约束
				throw new ProjectNameDuplicateException();
			}
			super.save(vo.getProjectDeclare());
		} catch (DuplicateKeyException e) {
			if (e.getCause().getMessage().indexOf("'project_num'")!=-1) {//违反项目编号唯一约束
				saveProjectDeclareVo(vo);
			}
		}
		projectPlanDao.deleteByProjectId(vo.getProjectDeclare().getId());
		int sort=0;
		for(ProjectPlan plan:vo.getPlans()) {
			plan.setSort(sort+"");
			plan.setId(IdGen.uuid());
			plan.setProjectId(vo.getProjectDeclare().getId());
			plan.setCreateDate(new Date());
			projectPlanDao.insert(plan);
			sort++;
		}
		if (vo.getFileInfo()!=null) {
			for(Map<String,String> map:vo.getFileInfo()) {
				SysAttachment sa=new SysAttachment();
				sa.setUid(vo.getProjectDeclare().getId());
				sa.setName(map.get("arrName"));
				String[] ss=map.get("arrName").split("\\.");
				sa.setSuffix(ss[ss.length-1]);
				sa.setType(FileSourceEnum.S0.getValue());
				sa.setFileStep(FileTypeEnum.S100.getValue());
				FtpUtil t = new FtpUtil();
				try {
					sa.setUrl(t.moveFile(t.getftpClient(),map.get("arrUrl")));
				} catch (Exception e) {
					e.printStackTrace();
				}
				sysAttachmentService.save(sa);
			}
		}
		startPojectProcess(vo.getProjectDeclare());  //启动工作流 addBy zhangzheng
	}
	@Transactional(readOnly = false)
	public void delete(ProjectDeclare projectDeclare) {
		super.delete(projectDeclare);
	}
	
	public int findByTeamId(String teamId) {
		return dao.findByTeamId(teamId);
	}
	
	public ProjectDeclare getProjectByTimeId(String tid) {
		return dao.getProjectByTeamId(tid);
	}
	
//	public List<ProjectExpVo> getExps(String userId) {
//		return dao.getExps(userId);
//	}
	
	public List<ProjectExpVo> getExpsByUserId(String userId) {
		return dao.getExpsByUserId(userId);
	}
}