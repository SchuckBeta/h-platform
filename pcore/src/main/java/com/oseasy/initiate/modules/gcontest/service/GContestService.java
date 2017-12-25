package com.oseasy.initiate.modules.gcontest.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.StringUtils;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.http.impl.cookie.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.IdUtils;
import com.oseasy.initiate.modules.act.dao.ActDao;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.gcontest.dao.GContestDao;
import com.oseasy.initiate.modules.gcontest.entity.GAuditInfo;
import com.oseasy.initiate.modules.gcontest.entity.GContest;
import com.oseasy.initiate.modules.gcontest.entity.GContestAnnounce;
import com.oseasy.initiate.modules.gcontest.entity.GContestAward;
import com.oseasy.initiate.modules.gcontest.enums.AuditStatusEnum;
import com.oseasy.initiate.modules.gcontest.enums.GContestStatusEnum;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 大赛信息Service
 * @author zy
 * @version 2017-03-11
 */
@Service
@Transactional(readOnly = true)
public class GContestService extends CrudService<GContestDao, GContest> {
	@Autowired
	ActTaskService actTaskService;

	@Autowired
	UserService userService;

	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	IdentityService identityService;

	@Autowired
	GAuditInfoService gAuditInfoService;

	@Autowired
	GContestAwardService gContestAwardService;

	@Autowired
	private SysAttachmentService sysAttachmentService;

	@Autowired
	private GContestAnnounceService gContestAnnounceService;

	@Autowired
	private OaNotifyService oaNotifyService;

	@Autowired
	TaskService taskService;

	@Autowired
	ActDao actDao;

	public GContest get(String id) {
		return super.get(id);
	}

	public List<GContest> findList(GContest gContest) {
		return super.findList(gContest);
	}

	public Page<GContest> findPage(Page<GContest> page, GContest gContest) {
		return super.findPage(page, gContest);
	}

	public Page<Map<String,String>> getMyGcontestList(Page<Map<String,String>> page,Map<String,Object> param) {
		if (page.getPageNo()<=0) {
			page.setPageNo(1);
		}
		int count=dao.getMyGcontestListCount(param);
		param.put("offset", (page.getPageNo()-1)*page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String,String>> list=null;
		if (count>0) {
			list=dao.getMyGcontestList(param);
			if (list!=null) {
				List<String> ids=new ArrayList<String>();
				for(Map<String,String> map:list) {
					ids.add(map.get("id"));
					map.put("auditCode", map.get("auditState"));
					map.put("auditState", GContestStatusEnum.getNameByValue(map.get("auditState")));
				}
				if (ids.size()>0) {
					List<Map<String,String>> ps=dao.getMyGcontestListPerson(ids);
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
		}
		page.setCount(count);
		page.setList(list);
		page.initialize();
		return page;
	}

	public Page<Map<String,String>> getGcontestList(Page<Map<String,String>> page,Map<String,Object> param) {
		if (page.getPageNo()<=0) {
			page.setPageNo(1);
		}
		int count=dao.getGcontestListCount(param);
		param.put("offset", (page.getPageNo()-1)*page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String,String>> list=null;
		if (count>0) {
			list=dao.getGcontestList(param);
			if (list!=null) {
				List<String> ids=new ArrayList<String>();
				for(Map<String,String> map:list) {
					ids.add(map.get("id"));

					//判断专家是否已经审核过该数据
					User user= UserUtils.getUser();
					GAuditInfo pai=new GAuditInfo();
			        pai.setGId(map.get("id"));
			        pai.setAuditLevel(map.get("auditState"));
			        pai.setAuditId(user.getLoginName());
			        List<GAuditInfo> infos= gAuditInfoService.getInfo(pai);
					if (infos.size()>0) {
						//该专家已经审核过该数据
						map.put("isHave","1");
					}else{
						map.put("isHave","0");
					}
					GContest gContest=get(map.get("id"));
			    	if (gContest.getAct().getProcInsId()!=null) {
						String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
						Task task=actTaskService.getTask(taskId);
						if (task!=null) {
							map.put("taskDef",task.getProcessDefinitionId());
							map.put("taskIn",task.getProcessInstanceId());
							map.put("taskId",taskId);
						}
					}
			    	map.put("auditCode", map.get("auditState"));
					map.put("auditState", GContestStatusEnum.getNameByValue(map.get("auditState")));
				}
				if (ids.size()>0) {
					List<Map<String,String>> ps=dao.getMyGcontestListPerson(ids);
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
		}
		page.setCount(count);
		page.setList(list);
		page.initialize();
		return page;
	}

	public Page<Map<String,String>> getEndGcontestList(Page<Map<String,String>> page,Map<String,Object> param) {
		if (page.getPageNo()<=0) {
			page.setPageNo(1);
		}
		int count=dao.getEndGcontestListCount(param);
		param.put("offset", (page.getPageNo()-1)*page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String,String>> list=null;
		if (count>0) {
			list=dao.getEndGcontestList(param);
			if (list!=null) {
				List<String> ids=new ArrayList<String>();
				for(Map<String,String> map:list) {
					ids.add(map.get("id"));

					//判断专家是否已经审核过该数据
					User user= UserUtils.getUser();
					GAuditInfo pai=new GAuditInfo();
			        pai.setGId(map.get("id"));
			        pai.setAuditLevel(map.get("auditState"));
			        pai.setAuditId(user.getLoginName());
			        /* List<GAuditInfo> infos= gAuditInfoService.getInfo(pai);
					if (infos.size()>0) {
						//该专家已经审核过该数据
						map.put("isHave","1");
					}else{
						map.put("isHave","0");
					}*/
					GContest gContest=get(map.get("id"));
			    	if (gContest.getAct().getProcInsId()!=null) {
						String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
						Task task=actTaskService.getTask(taskId);
						if (task!=null) {
							map.put("taskDef",task.getProcessDefinitionId());
							map.put("taskIn",task.getProcessInstanceId());
						}
					}
			    	map.put("auditCode", map.get("auditState"));
					map.put("auditState", GContestStatusEnum.getNameByValue(map.get("auditState")));
				}
				if (ids.size()>0) {
					List<Map<String,String>> ps=dao.getMyGcontestListPerson(ids);
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
		}
		page.setCount(count);
		page.setList(list);
		page.initialize();
		return page;
	}


	public Page<Map<String,String>> auditContestList(Page<Map<String,String>> page,Map<String,Object> param) {
		if (page.getPageNo()<=0) {
			page.setPageNo(1);
		}
		int count=dao.getAuditListCount(param);
		param.put("offset", (page.getPageNo()-1)*page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String,String>> list=null;
		if (count>0) {
			list=dao.getAuditList(param);
			if (list!=null) {
				List<String> ids=new ArrayList<String>();
				for(Map<String,String> map:list) {
					ids.add(map.get("id"));

					//判断专家是否已经审核过该数据
					User user= UserUtils.getUser();
					GAuditInfo pai=new GAuditInfo();
			        pai.setGId(map.get("id"));
			        pai.setAuditLevel(map.get("auditState"));
			        pai.setAuditId(user.getLoginName());
			        List<GAuditInfo> infos= gAuditInfoService.getInfo(pai);
					if (infos.size()>0) {
						//该专家已经审核过该数据
						map.put("isHave","1");
					}else{
						map.put("isHave","0");
					}
					GContest gContest=get(map.get("id"));
			    	if (gContest.getAct().getProcInsId()!=null) {
						String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
						Task task=actTaskService.getTask(taskId);
						if (task!=null) {
							map.put("taskDef",task.getProcessDefinitionId());
							map.put("taskIn",task.getProcessInstanceId());
							map.put("taskId",taskId);
						}
					}
			    	map.put("auditCode", map.get("auditState"));
					map.put("auditState", GContestStatusEnum.getNameByValue(map.get("auditState")));
				}
				if (ids.size()>0) {
					List<Map<String,String>> ps=dao.getMyGcontestListPerson(ids);
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
		}
		page.setCount(count);
		page.setList(list);
		page.initialize();
		return page;
	}


	@Transactional(readOnly = false)
	public List<GContest> getGcontestByName(String gContestName) {
		List<GContest> gContests=dao.getGcontestByName(gContestName);
		return gContests;
	}

	@Transactional(readOnly = false)
	public void save(GContest gContest) {
		User user = UserUtils.getUser();
		gContest.setCompetitionNumber(IdUtils.getGContestNumberByDb());
		gContest.setSubTime(new Date());
		gContest.setUniversityId(user.getOffice().getId());
		gContest.setAuditState("0");
		super.save(gContest);
	}

	@Transactional(readOnly = false)
	public void udpate(GContest gContest) {

		super.save(gContest);
	}


	@Transactional(readOnly = false)
	public int submit(GContest gContest) {
		User user = UserUtils.getUser();
		gContest.setAuditState("1");
		gContest.setCompetitionNumber(IdUtils.getGContestNumberByDb());
		gContest.setSubTime(new Date());
		gContest.setUniversityId(user.getOffice().getId());
		//super.save(gContest);
		//启动大赛工作流
		String roleName=actTaskService.getStartNextRoleName("gcontest");  //从工作流中查询 下一步的角色集合
		List<String> roles=userService.getCollegeExperts(gContest.getDeclareId());
		if (roles.size()>0) {
			Map<String,Object> vars=new HashMap<String,Object>();
			vars=gContest.getVars();
			vars.put(roleName+"s",roles);
			//vars.put("projectName",gContest.getPName());
			//vars.put("collegeName", UserUtils.getUser().getCompany().getName());
			String key="gcontest";
			String userId = UserUtils.getUser().getLoginName();
			//vars.put("sumbitter",userId);
			identityService.setAuthenticatedUserId(userId);
			ProcessInstance procIns=runtimeService.startProcessInstanceByKey(key, "g_contest"+":"+gContest.getId(),vars);

			//流程id返写业务表
			Act act = new Act();
			act.setBusinessTable("g_contest");// 业务表名
			act.setBusinessId(gContest.getId());	// 业务表ID
			act.setProcInsId(procIns.getId());
			actDao.updateProcInsIdByBusinessId(act);
			gContest.setProcInsId(act.getProcInsId());
			gContest.setCurrentSystem("校赛");
			super.save(gContest);
			return 1;
		}else{
			return 0;
		}
	}

	@Transactional(readOnly = false)
	public String submitOld(GContest gContest) {
		User user =userService.findUserById(gContest.getDeclareId());
		GContest newGContest=new GContest();
		newGContest.setAuditState("1");
		newGContest.setId("");
		newGContest.setDeclareId(gContest.getDeclareId());
		newGContest.setIntroduction(gContest.getIntroduction());
		newGContest.setCurrentSystem("校赛");
		newGContest.setTeamId(gContest.getTeamId());
		newGContest.setFinancingStat(gContest.getFinancingStat());
		newGContest.setType(gContest.getType());
		newGContest.setLevel(gContest.getLevel());
		newGContest.setpName(gContest.getpName());
		if (gContest.getpId()!=null) {
			newGContest.setpId(gContest.getpId());
		}
		newGContest.setCompetitionNumber(gContest.getCompetitionNumber());
		newGContest.setSubTime(gContest.getSubTime());
		newGContest.setUniversityId(user.getOffice().getId());
		newGContest.setAnnounceId(gContest.getAnnounceId());
		//super.save(newGContest);
		//启动大赛工作流
		String roleName=actTaskService.getStartNextRoleName("gcontest");  //从工作流中查询 下一步的角色集合
		List<String> roles=userService.getCollegeExperts(newGContest.getDeclareId());
		if (roles.size()>0) {
			Map<String,Object> vars=new HashMap<String,Object>();
			vars=newGContest.getVars();
			vars.put(roleName+"s",roles);
			//vars.put("projectName",gContest.getPName());
			//vars.put("collegeName", UserUtils.getUser().getCompany().getName());
			String key="gcontest";
			String userId = user.getLoginName();
			//vars.put("sumbitter",userId);
			identityService.setAuthenticatedUserId(userId);
			ProcessInstance procIns=runtimeService.startProcessInstanceByKey(key, "g_contest"+":"+newGContest.getId(),vars);

			//流程id返写业务表
			Act act = new Act();
			act.setBusinessTable("g_contest");// 业务表名
			act.setBusinessId(newGContest.getId());	// 业务表ID
			act.setProcInsId(procIns.getId());
			actDao.updateProcInsIdByBusinessId(act);
			newGContest.setProcInsId(act.getProcInsId());

			super.save(newGContest);
		}else{
			return "";
		}
		return newGContest.getId();
	}

	@Transactional(readOnly = false)
	public void delete(GContest gContest) {
		super.delete(gContest);
	}

	@Transactional(readOnly = false)
	public void saveAudit1(GContest gContest,Act act) {
		//完成工作流
		String userId = UserUtils.getUser().getLoginName();
		//String comment=userId+" 的评分："+gContest.getGScore();
		GAuditInfo pai=new GAuditInfo();
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S1.getValue());  //1表示院内2表示校内
		pai.setAuditName(AuditStatusEnum.S1.getName());//网评 审核 路演 后面做成字典表
		pai.setAuditId(userId);
		if (gContest.getgScore()!=null) {
			pai.setScore(Float.parseFloat(gContest.getgScore()));
		}
		if (gContest.getGrade()!=null) {
			pai.setGrade(gContest.getGrade());
		}
		pai.setSuggest(gContest.getComment());
		gAuditInfoService.save(pai);
		User apply_User=UserUtils.getUser();
		User rec_User=new User();
		rec_User.setId(gContest.getDeclareId());
		oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"学院专家审核",
				"学院专家"+apply_User.getName()+"已对您的作品给出评分；","",gContest.getId());

		gContest=get(gContest.getId());
		Map<String,Object> vars=new HashMap<String,Object>();
		//查询本用户任务环节是否完成，改变业务表对应的状态
		//String taskDefKey = gContest.getAct().getTaskDefKey(); 	// 环节编号
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
		//Task task=actTaskService.getTask(taskId);
		boolean isLast=actTaskService.isMultiLast("gcontest",act.getTaskDefKey(),gContest.getAct().getProcInsId());
		if (isLast) { //如果当前任务环节完成了
			//String roleName=actTaskService.getNextRoleName(gContest.getAct().getTaskDefKey(),"gcontest");  //查找下一个环节的 roleName
			List<String> collegeSecs=new ArrayList<String>() ;
			gContest.setAuditState("2");//学院专家打完分
			vars=gContest.getVars();
			collegeSecs=userService.getCollegeSecs(gContest.getCreateBy().getId());

			vars.put("collegeSec",collegeSecs); //给学院教学秘书审批			vars.put("collegeSec",getCollegeSecs(gContest.getId()));
			float average=0;
			GAuditInfo infoSerch=new GAuditInfo();
			infoSerch.setGId(gContest.getId());
			infoSerch.setAuditLevel("1");
			average= gAuditInfoService.getAuditAvgInfo(infoSerch);
			gContest.setgScore(String.valueOf(average));
			gContest.setCollegeExportScore(average);
			gContest.setCollegeScore(average);
			super.dao.updateState(gContest);
			//如果当前任务环节完成了
			taskService.complete(taskId, vars);
			//免签收动作 只有这个角色可以审核。
			actTaskService.claimByProcInsId(gContest.getAct().getProcInsId(),collegeSecs);
		}else{
			taskService.complete(taskId, vars);
		}
	}
	//项目变更跨流程
	@Transactional(readOnly = false)
	public void saveAuditFirst(GContest gContest,Act act,GAuditInfo pai) {
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S1.getValue());  //1表示院内2表示校内
		pai.setAuditName(AuditStatusEnum.S1.getName());//网评 审核 路演 后面做成字典表
		//pai.setAuditId(userId);
		if (gContest.getgScore()!=null) {
			pai.setScore(Float.parseFloat(gContest.getgScore()));
		}
		pai.setSuggest(gContest.getComment());
		gAuditInfoService.saveByOther(pai);
		gContest=get(gContest.getId());
		Map<String,Object> vars=new HashMap<String,Object>();
		//查询本用户任务环节是否完成，改变业务表对应的状态
		//String taskDefKey = gContest.getAct().getTaskDefKey(); 	// 环节编号
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
		boolean isLast=actTaskService.isMultiLast("gcontest",act.getTaskDefKey(),gContest.getAct().getProcInsId());
		if (isLast) { //如果当前任务环节完成了
			List<String> collegeSecs=new ArrayList<String>() ;
			gContest.setAuditState("2");//学院专家打完分
			vars=gContest.getVars();
			collegeSecs=userService.getCollegeSecs(gContest.getCreateBy().getId());
			vars.put("collegeSec",collegeSecs); //给学院教学秘书审批			vars.put("collegeSec",getCollegeSecs(gContest.getId()));
			float average=0;
			GAuditInfo infoSerch=new GAuditInfo();
			infoSerch.setGId(gContest.getId());
			infoSerch.setAuditLevel("1");
			average= gAuditInfoService.getAuditAvgInfo(infoSerch);
			gContest.setgScore(String.valueOf(average));
			gContest.setCollegeExportScore(average);
			gContest.setCollegeScore(average);
			super.dao.updateState(gContest);
			//如果当前任务环节完成了
			taskService.complete(taskId, vars);
			//免签收动作 只有这个角色可以审核。
			actTaskService.claimByProcInsId(gContest.getAct().getProcInsId(),collegeSecs);
		}else{
			taskService.complete(taskId, vars);
		}
	}

	@Transactional(readOnly = false)
	public void  saveAudit2(GContest gContest,Act act) {
		GAuditInfo pai=new GAuditInfo();
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S2.getValue());  //1表示院内2表示校内
		pai.setAuditName(AuditStatusEnum.S2.getName());//网评 审核 路演 后面做成字典表
		if (gContest.getGrade()!=null) {
			pai.setGrade(gContest.getGrade());
		}
		String comment=gContest.getComment();
		/*String score=gContest.getgScore();*/
		String grade=gContest.getGrade();
		//保存分数
		gContest=get(gContest.getId());
		if (String.valueOf(gContest.getCollegeScore())!=null) {
			pai.setScore(gContest.getCollegeScore());
		}
		String userId = UserUtils.getUser().getLoginName();
		pai.setAuditId(userId);
		//保存审核意见
		pai.setSuggest(comment);
		gAuditInfoService.save(pai);
		User apply_User=UserUtils.getUser();
		User rec_User=new User();
		rec_User.setId(gContest.getDeclareId());
		oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"学院秘书审核",
				"学院秘书"+apply_User.getName()+"已对您的作品给出评分；","",gContest.getId());

		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
		Map<String,Object> vars=new HashMap<String,Object>();
		vars=gContest.getVars();
		if (grade!=null) {
			vars.put("grade",grade);
			if (grade.equals("1")) {
				List<String> schoolExperts=userService.getSchoolExperts();
				/*gContest.setCollegeScore(Integer.valueOf(score));*/
				gContest.setCollegeResult(grade);
				gContest.setCollegeSug(comment);
				gContest.setAuditState("3");
				vars.put("schoolExperts",schoolExperts);
				taskService.complete(taskId, vars);
			}else{
				gContest.setCollegeResult(grade);
				gContest.setCollegeSug(comment);
				gContest.setAuditState("9");
			}
		}else{
			vars.put("grade","0");
			gContest.setAuditState("9");
		}

		// 改变主表的审核状态
		gContest.preUpdate();
		dao.updateState(gContest);
	}

	@Transactional(readOnly = false)
	public void  saveAuditSecond(GContest gContest,Act act,GAuditInfo pai) {
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S2.getValue());  //1表示院内2表示校内
		pai.setAuditName(AuditStatusEnum.S2.getName());//网评 审核 路演 后面做成字典表
		if (gContest.getGrade()!=null) {
			pai.setGrade(gContest.getGrade());
		}
		String comment=gContest.getComment();
		if (String.valueOf(gContest.getCollegeExportScore())!=null) {
			pai.setScore(gContest.getCollegeExportScore());
		}
		String grade=gContest.getGrade();
		//保存分数
		gContest=get(gContest.getId());
		pai.setId(IdGen.uuid());
		//保存审核意见
		pai.setSuggest(comment);
		gAuditInfoService.saveByOther(pai);

		Map<String,Object> vars=new HashMap<String,Object>();
		vars=gContest.getVars();
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());

		if (grade!=null) {
			vars.put("grade",grade);
			if (grade.equals("1")) {
				List<String> schoolExperts=userService.getSchoolExperts();
				gContest.setCollegeScore(gContest.getCollegeExportScore());
				gContest.setCollegeResult(grade);
				gContest.setCollegeSug(comment);
				gContest.setAuditState("3");
				vars.put("schoolExperts",schoolExperts);
				taskService.complete(taskId, vars);
			}else{
				gContest.setCollegeResult(grade);
				gContest.setCollegeSug(comment);
				gContest.setAuditState("9");
			}
		}else{
			vars.put("grade","0");
			gContest.setAuditState("9");
		}

		// 改变主表的审核状态
		gContest.preUpdate();
		dao.updateState(gContest);
	}

	@Transactional(readOnly = false)
	public void saveAudit3(GContest gContest,Act act) {
		GAuditInfo pai=new GAuditInfo();
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S3.getValue());  //1表示院内2表示校内
		pai.setAuditName(AuditStatusEnum.S3.getName());//网评 审核 路演 后面做成字典表
		if (gContest.getgScore()!=null) {
			pai.setScore(Float.parseFloat(gContest.getgScore()));
		}
		if (gContest.getGrade()!=null) {
			pai.setGrade(gContest.getGrade());
		}
		pai.setSuggest(gContest.getComment());
		String userId = UserUtils.getUser().getLoginName();
		pai.setAuditId(userId);
		gAuditInfoService.save(pai);
		User apply_User=UserUtils.getUser();
		User rec_User=new User();
		rec_User.setId(gContest.getDeclareId());
		oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"学校专家审核",
				"学校专家"+apply_User.getName()+"已对您的作品给出评分；","",gContest.getId());
		gContest=get(gContest.getId());
		Map<String,Object> vars=new HashMap<String,Object>();
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());

		//查询本用户任务环节是否完成，改变业务表对应的状态
		//String taskDefKey = gContest.getAct().getTaskDefKey(); 	// 环节编号
		if (actTaskService.isMultiLast("gcontest",act.getTaskDefKey(),gContest.getAct().getProcInsId())) { //如果当前任务环节完成了
			List<String> schoolSecs=userService.getSchoolSecs();
			vars=gContest.getVars();
			vars.put("schoolSec",schoolSecs);
			gContest.setAuditState("4");//学院专家打完分
			float average=0;
			GAuditInfo infoSerch=new GAuditInfo();
			infoSerch.setGId(gContest.getId());
			infoSerch.setAuditLevel("3");
			average= gAuditInfoService.getAuditAvgInfo(infoSerch);
			gContest.setgScore(String.valueOf(average));
			gContest.setSchoolExportScore(average);
			gContest.setSchoolScore(average);
			taskService.complete(taskId, vars);
			actTaskService.claimByProcInsId(gContest.getAct().getProcInsId(),schoolSecs);
			dao.updateState(gContest);
		}else{
			taskService.complete(taskId, vars);
		}
	}

	@Transactional(readOnly = false)
	public void saveAuditThree(GContest gContest,Act act,GAuditInfo pai) {
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S3.getValue());  //1表示院内2表示校内
		pai.setAuditName(AuditStatusEnum.S3.getName());//网评 审核 路演 后面做成字典表
		if (gContest.getgScore()!=null) {
			pai.setScore(Float.parseFloat(gContest.getgScore()));
		}
		pai.setSuggest(gContest.getComment());
		gAuditInfoService.saveByOther(pai);
		gContest=get(gContest.getId());
		Map<String,Object> vars=new HashMap<String,Object>();
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
		//查询本用户任务环节是否完成，改变业务表对应的状态
		//String taskDefKey = gContest.getAct().getTaskDefKey(); 	// 环节编号
		//isMultiLast判断是否为最后一个审核人
		if (actTaskService.isMultiLast("gcontest",act.getTaskDefKey(),gContest.getAct().getProcInsId())) { //如果当前任务环节完成了
			List<String> schoolSecs=userService.getSchoolSecs();
			vars=gContest.getVars();
			vars.put("schoolSec",schoolSecs);
			gContest.setAuditState("4");//学院专家打完分
			float average=0;
			GAuditInfo infoSerch=new GAuditInfo();
			infoSerch.setGId(gContest.getId());
			infoSerch.setAuditLevel("3");
			average= gAuditInfoService.getAuditAvgInfo(infoSerch);
			gContest.setgScore(String.valueOf(average));
			gContest.setSchoolExportScore(average);
			gContest.setSchoolScore(average);
			taskService.complete(taskId, vars);
			actTaskService.claimByProcInsId(gContest.getAct().getProcInsId(),schoolSecs);
			dao.updateState(gContest);
		}else{
			taskService.complete(taskId, vars);
		}
	}

	@Transactional(readOnly = false)
	public void  saveAudit4(GContest gContest,Act act) {
		GAuditInfo pai=new GAuditInfo();
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S4.getValue());
		pai.setAuditName(AuditStatusEnum.S4.getName());
		if (gContest.getGrade()!=null) {
			pai.setGrade(gContest.getGrade());
		}
		//保存分数
		String comment=gContest.getComment();
		String grade=gContest.getGrade();
		gContest=get(gContest.getId());
		if (String.valueOf(gContest.getSchoolScore())!=null) {
			pai.setScore(gContest.getSchoolScore());
		}
		//保存审核意见
		String userId = UserUtils.getUser().getLoginName();
		pai.setAuditId(userId);
		pai.setSuggest(comment);
		gAuditInfoService.save(pai);
		User apply_User=UserUtils.getUser();
		User rec_User=new User();
		rec_User.setId(gContest.getDeclareId());
		oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"学校管理员审核",
				"学校管理员"+apply_User.getName()+"已对您的作品给出评分；","",gContest.getId());
		Map<String,Object> vars=new HashMap<String,Object>();
		vars=gContest.getVars();
		vars.put("grade",grade);
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
		if (grade!=null) {
			if (grade.equals("1")) {
				List<String> schoolSecs=userService.getSchoolSecs();
				vars.put("schoolSecSec",schoolSecs);
				gContest.setSchoolResult(grade);
				//gContest.setSchoolScore(Integer.valueOf(score));
				gContest.setSchoolSug(comment);
				gContest.setAuditState("5");//网评审核完成
				taskService.complete(taskId, vars);
				actTaskService.claimByProcInsId(gContest.getProcInsId(),schoolSecs);
			}else{
				gContest.setSchoolResult(grade);
				gContest.setSchoolSug(comment);
				gContest.setAuditState("8");
				//taskService.complete(taskId, vars);
			}
		}else{
			gContest.setSchoolResult(grade);
			gContest.setSchoolSug(comment);
			gContest.setAuditState("8");
			//taskService.complete(taskId, vars);
		}

		gContest.preUpdate();
		dao.updateState(gContest);
	}

	@Transactional(readOnly = false)
	public void  saveAuditFour(GContest gContest,Act act,GAuditInfo pai) {
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S4.getValue());
		pai.setAuditName(AuditStatusEnum.S4.getName());
		if (gContest.getGrade()!=null) {
			pai.setGrade(gContest.getGrade());
		}
		//保存分数
		String comment=gContest.getComment();
		String grade=gContest.getGrade();
		gContest=get(gContest.getId());
		if (String.valueOf(gContest.getSchoolScore())!=null) {
			pai.setScore(gContest.getSchoolScore());
		}
		//保存审核意见
		pai.setSuggest(comment);
		gAuditInfoService.saveByOther(pai);

		Map<String,Object> vars=new HashMap<String,Object>();
		vars=gContest.getVars();
		vars.put("grade",grade);
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
		if (grade!=null) {
			if (grade.equals("1")) {
				List<String> schoolSecs=userService.getSchoolSecs();
				vars.put("schoolSecSec",schoolSecs);
				gContest.setSchoolResult(grade);
				gContest.setSchoolSug(comment);
				gContest.setAuditState("5");//网评审核完成
				taskService.complete(taskId, vars);
				actTaskService.claimByProcInsId(gContest.getProcInsId(),schoolSecs);
			}else{
				gContest.setSchoolResult(grade);
				gContest.setSchoolSug(comment);
				gContest.setAuditState("8");
				//taskService.complete(taskId, vars);
			}
		}else{
			gContest.setSchoolResult(grade);
			gContest.setSchoolSug(comment);
			gContest.setAuditState("8");
			//taskService.complete(taskId, vars);
		}
		gContest.preUpdate();
		dao.updateState(gContest);
	}

	@Transactional(readOnly = false)
	public void  saveAudit5(GContest gContest,Act act) {
		GAuditInfo pai=new GAuditInfo();
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S5.getValue());
		pai.setAuditName(AuditStatusEnum.S5.getName());
		//保存打分人员id
		String userId = UserUtils.getUser().getLoginName();
		pai.setAuditId(userId);
		//保存分数
		if (gContest.getgScore()!=null) {
			pai.setScore(Float.parseFloat(gContest.getgScore()));
		}
		//保存审核意见
		pai.setSuggest(gContest.getComment());
		gAuditInfoService.save(pai);
		User apply_User=UserUtils.getUser();
		User rec_User=new User();
		rec_User.setId(gContest.getDeclareId());
		oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"学校管理员审核",
				"学校管理员"+apply_User.getName()+"已对您的作品给出评分；","",gContest.getId());
		//完成工作流
		/*String roleName=actTaskService.getNextRoleName("audit2","gcontest");  //从工作流中查询 下一步的角色集合
		//List<String> roles=userService.getRolesByName(roleName);*/
		String comment=gContest.getComment();
		String score=gContest.getgScore();
		//String grade=gContest.getGrade();
		gContest=get(gContest.getId());
		Map<String,Object> vars=new HashMap<String,Object>();
		vars=gContest.getVars();
		/*taskService.complete(gContest.getAct().getTaskId(), vars);*/
		List<String> schoolSecs=userService.getSchoolSecs();
		//应该通过act来获取
		vars.put("schoolSecSec",schoolSecs);
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
		//gContest.setSchoolluyanResult(grade);
		gContest.setSchoolluyanScore(Integer.valueOf(score));
		gContest.setSchoolluyanSug(comment);
		gContest.setAuditState("6");//路演审核完成
		taskService.complete(taskId, vars);
		actTaskService.claimByProcInsId(gContest.getAct().getProcInsId(),schoolSecs);

		// 改变主表的审核状态
		//gContest.setAuditState("6");//审核完成
		gContest.preUpdate();
		dao.updateState(gContest);
	}

	@Transactional(readOnly = false)
	public void  saveAuditFive(GContest gContest,Act act,GAuditInfo pai) {
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S5.getValue());
		pai.setAuditName(AuditStatusEnum.S5.getName());
		//保存分数
		if (gContest.getgScore()!=null) {
			pai.setScore(Float.parseFloat(gContest.getgScore()));
		}
		//保存审核意见
		pai.setSuggest(gContest.getComment());
		gAuditInfoService.saveByOther(pai);
		//完成工作流
		/*String roleName=actTaskService.getNextRoleName("audit2","gcontest");  //从工作流中查询 下一步的角色集合
		//List<String> roles=userService.getRolesByName(roleName);*/
		String comment=gContest.getComment();
		String score=gContest.getgScore();
		//String grade=gContest.getGrade();
		gContest=get(gContest.getId());
		Map<String,Object> vars=new HashMap<String,Object>();
		vars=gContest.getVars();
		/*taskService.complete(gContest.getAct().getTaskId(), vars);*/
		List<String> schoolSecs=userService.getSchoolSecs();
		//应该通过act来获取
		vars.put("schoolSecSec",schoolSecs);
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
		//gContest.setSchoolluyanResult(grade);
		gContest.setSchoolluyanScore(Integer.valueOf(score));
		gContest.setSchoolluyanSug(comment);
		gContest.setAuditState("6");//路演审核完成
		taskService.complete(taskId, vars);
		actTaskService.claimByProcInsId(gContest.getAct().getProcInsId(),schoolSecs);
		// 改变主表的审核状态
		//gContest.setAuditState("6");//审核完成
		gContest.preUpdate();
		dao.updateState(gContest);
	}

	@Transactional(readOnly = false)
	public void  saveAudit6(GContest gContest,Act act) {
		GAuditInfo pai=new GAuditInfo();
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S6.getValue());
		pai.setAuditName(AuditStatusEnum.S6.getName());
		String userId = UserUtils.getUser().getLoginName();
		pai.setAuditId(userId);
		//保存审核意见
		pai.setSuggest(gContest.getComment());
		if (gContest.getGrade()!=null) {
			pai.setGrade(gContest.getGrade());
		}
		String comment=gContest.getComment();
		//String score=gContest.getgScore();
		String grade=gContest.getGrade();
		gContest=get(gContest.getId());
		float m=(gContest.getSchoolScore()+gContest.getSchoolluyanScore())/2;
		pai.setScore(m);
		gAuditInfoService.save(pai);
		User apply_User=UserUtils.getUser();
		User rec_User=new User();
		rec_User.setId(gContest.getDeclareId());
		oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"学校管理员审核",
				"学校管理员"+apply_User.getName()+"已对您的作品给出评分；","",gContest.getId());
		Map<String,Object> vars=new HashMap<String,Object>();
		vars=gContest.getVars();
		//应该通过act来获取
		List<String> schoolSecs=userService.getSchoolSecs();
		vars.put("schoolSecSec",schoolSecs);
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
		gContest.setSchoolendResult(grade);
		gContest.setSchoolendSug(comment);
		gContest.setAuditState("7");//审核完成
		gContest.setSchoolendScore(m);
		taskService.complete(taskId, vars);
		if (grade.equals("2")||grade.equals("3")||grade.equals("4")) {
			//保存获奖信息
			GContestAward gContestAward=new GContestAward();
			gContestAward.setContestId(gContest.getId());
			gContestAward.setAward(grade);
			gContestAward.setAwardLevel("3");
			String money=DictUtils.getDictLabel(grade,"competition_college_money","");
			if (money!=null) {
				gContestAward.setMoney(money);
			}
			gContestAwardService.save(gContestAward);
		}
		gContest.preUpdate();
		dao.updateState(gContest);
	}

	public void  saveAuditSix(GContest gContest,Act act,GAuditInfo pai) {
		pai.setGId(gContest.getId());  //主表id
		pai.setAuditLevel(AuditStatusEnum.S6.getValue());
		pai.setAuditName(AuditStatusEnum.S6.getName());
		//保存审核意见
		pai.setSuggest(gContest.getComment());
		if (gContest.getGrade()!=null) {
			pai.setGrade(gContest.getGrade());
		}
		String comment=gContest.getComment();
		String grade=gContest.getGrade();
		gContest=get(gContest.getId());
		float m=(gContest.getSchoolScore()+gContest.getSchoolluyanScore())/2;
		pai.setScore(m);
		gAuditInfoService.saveByOther(pai);
		Map<String,Object> vars=new HashMap<String,Object>();
		vars=gContest.getVars();
		//应该通过act来获取
		List<String> schoolSecs=userService.getSchoolSecs();
		vars.put("schoolSecSec",schoolSecs);
		String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
		gContest.setSchoolendResult(grade);
		gContest.setSchoolendSug(comment);
		gContest.setAuditState("7");//审核完成
		gContest.setSchoolendScore(m);
		taskService.complete(taskId, vars);
		if (grade.equals("2")||grade.equals("3")||grade.equals("4")) {
			//保存获奖信息
			GContestAward gContestAward=new GContestAward();
			gContestAward.setContestId(gContest.getId());
			gContestAward.setAward(grade);
			gContestAward.setAwardLevel("3");
			if (grade.equals("2")||grade.equals("3")||grade.equals("4")) {
				String money = DictUtils.getDictLabel(grade, "competition_college_money", "");
				if (money != null) {
					gContestAward.setMoney(money);
				}
			}
			gContestAwardService.save(gContestAward);
		}
		gContest.preUpdate();
		dao.updateState(gContest);
	}

	//得到当前项目
	public List<GContest> getGcontestInfo(String gcontestUserId) {
		return dao.getGcontestInfo(gcontestUserId);
	}
	//得到最后一个项目
	public GContest getLastGcontestInfo(String gcontestUserId) {
		// TODO Auto-generated method stub
		return dao.getLastGcontestInfo(gcontestUserId);
	}

	private  GAuditInfo getSortInfoByIdAndState(String gId,String auditStep,String collegeId) {
		GAuditInfo pai=new GAuditInfo();
        pai.setGId(gId);
        pai.setAuditLevel(auditStep);
        pai.setCollegeId(collegeId);
        GAuditInfo infos= gAuditInfoService.getSortInfoByIdAndState(pai);
        return infos;
    }

	private  GAuditInfo getSchoolSortInfoByIdAndState(String gId,String auditStep,String schoolId) {
		GAuditInfo pai=new GAuditInfo();
        pai.setGId(gId);
        pai.setAuditLevel(auditStep);
        pai.setSchoolId(schoolId);
        GAuditInfo infos= gAuditInfoService.getSortInfoByIdAndState(pai);
        return infos;
    }

	public JSONObject getListData(GContest gContest) {
       // User uesr=userService.findUserById(gContest.getDeclareId());
        User user = UserUtils.getUser();
        //jsondata 生产
    	JSONObject obj = new JSONObject();
		//项目基础信息表头
		JSONObject gContestobj = new JSONObject();
		if ( gContest!=null&&gContest.getCompetitionNumber()!=null) {
			gContestobj.put("code", gContest.getCompetitionNumber());
			gContestobj.put("name", gContest.getpName());
		}else{
			gContestobj.put("code", "");
			gContestobj.put("name", "");
		}
		//参赛组别
		//"初创组";
		String gcontestLevel="";
		if ( gContest!=null&&gContest.getLevel()!=null) {
			gcontestLevel=DictUtils.getDictLabel(gContest.getLevel(), "gcontest_level", "");//.getDictList("gcontest_level").;
		}
		//项目内容table_first
		JSONObject gContestContentlist = new JSONObject();
		//list
		JSONArray  gContestContentlistArray = new JSONArray ();
			//报名信息
			JSONObject arraySb= new JSONObject();
			arraySb.put("type", "0");
			//(从大赛申报表里面读取)
			GContestAnnounce gContestAnnounce=new GContestAnnounce();
			if ( gContest!=null&&gContest.getAnnounceId()!=null) {
				 gContestAnnounce=gContestAnnounceService.get(gContest.getAnnounceId());
			}else{
		        gContestAnnounce.setType("1");
		        gContestAnnounce.setStatus("1");
		        gContestAnnounce=gContestAnnounceService.getGContestAnnounce(gContestAnnounce);
			}
			if (gContestAnnounce!=null&&gContestAnnounce.getApplyStart()!=null) {
				arraySb.put("Date", DateUtils.formatDate(gContestAnnounce.getApplyStart(), "yyyy-MM-dd"));
			}else{
				arraySb.put("Date", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
			}

			arraySb.put("SpeedOfProgress", "提交报名表");

			//添加附件
			SysAttachment sysAttachment=new SysAttachment();
			JSONArray  arraySbAtt = new JSONArray ();
			if (gContest!=null&&gContest.getId()!=null) {
				sysAttachment.setUid(gContest.getId());
				List<SysAttachment> sysAttachments=sysAttachmentService.findList(sysAttachment);
				for(int i=0;i<sysAttachments.size();i++) {
					JSONObject attachmentobj = new JSONObject();
					attachmentobj.put("link", sysAttachments.get(i).getName());
					attachmentobj.put("url", sysAttachments.get(i).getUrl());
					arraySbAtt.add(attachmentobj);
				}
				arraySb.put("list", arraySbAtt);
			}else{
				arraySb.put("list", arraySbAtt);
			}
		gContestContentlistArray.add(arraySb);
		//院赛信息
		JSONObject arrayYs= new JSONObject();
		arrayYs.put("type", "1");
		//(从大赛申报表里面读取)

		if (gContestAnnounce!=null&&gContestAnnounce.getCollegeStart()!=null) {
			arrayYs.put("Date", DateUtils.formatDate(gContestAnnounce.getCollegeStart(), "yyyy-MM-dd"));
		}else{
			arrayYs.put("Date", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
		}
		arrayYs.put("SpeedOfProgress", "院级");
		arrayYs.put("type", "1");
		arrayYs.put("group", gcontestLevel);
		if ( user.getOffice()!=null&&user.getOffice().getName()!=null) {
			arrayYs.put("School", user.getOffice().getName());
		}else{
			arrayYs.put("School","");
		}
		int num=gAuditInfoService.getCollegeCount("2",user.getOffice().getId());
		//"30"; //从大赛表中查询出来大赛学院排名
		arrayYs.put("Number_of_entries",num);
		JSONArray  arrayYsList = new JSONArray ();
		//院赛信息排名
		JSONObject arrayPm= new JSONObject();
		if (gContest!=null&&gContest.getId()!=null) {
			GAuditInfo collegeinfos= getSortInfoByIdAndState(gContest.getId(),"2",user.getOffice().getId());
			if (collegeinfos!=null) {
				arrayPm.put("College_score",collegeinfos.getScore());
				arrayPm.put("Proposal", collegeinfos.getSuggest());
				arrayPm.put("ranking", collegeinfos.getSort());
				arrayYsList.add(arrayPm);
				arrayYs.put("list", arrayYsList);
			}else{
				arrayPm.put("College_score","");
				arrayPm.put("Proposal", "");
				arrayPm.put("ranking", "");
				arrayYsList.add(arrayPm);
				arrayYs.put("list", arrayYsList);
			}
		}else{
			arrayPm.put("College_score","");
			arrayPm.put("Proposal", "");
			arrayPm.put("ranking", "");
			arrayYsList.add(arrayPm);
			arrayYs.put("list", arrayYsList);
		}
		gContestContentlistArray.add(arrayYs);

		//校赛
		JSONObject arrayXs= new JSONObject();
		arrayXs.put("type", "2");

		if (gContestAnnounce!=null&&gContestAnnounce.getSchoolStart()!=null) {
			arrayXs.put("Date", DateUtils.formatDate(gContestAnnounce.getSchoolStart(), "yyyy-MM-dd"));
		}else{
			arrayXs.put("Date", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
		}
		arrayXs.put("SpeedOfProgress", "校赛");
		arrayXs.put("group", gcontestLevel);
		if ( user.getCompany()!=null&&user.getCompany().getName()!=null) {
			arrayXs.put("School", user.getCompany().getName());
		}else{
			arrayXs.put("School","");
		}

		//arrayXs.put("School", user.getCompany().getName());
		int numXs=gAuditInfoService.getSchoolCount("4",user.getOffice().getId());
		arrayXs.put("Number_of_entries",numXs);
		//从获奖表中获取
		if (gContest!=null&&gContest.getId()!=null) {
			GContestAward gca=gContestAwardService.getByGid(gContest.getId());
			if (gca!=null) {
				arrayXs.put("Awards",
						DictUtils.getDictLabel(gca.getAward(),"competition_college_prise","")
						);
				arrayXs.put("bonus",gca.getMoney());
			}else{
				arrayXs.put("Awards","");
				arrayXs.put("bonus","");
			}

			//String numXs="300"; //从大赛表中查询出来大赛参赛数排名
		}else{
			arrayXs.put("Awards","");
			arrayXs.put("bonus","");
		}
		//校赛信息
		JSONArray  arrayXsList = new JSONArray ();
		JSONObject xsWangpin= new JSONObject();
		if (gContest!=null&&gContest.getId()!=null&&user.getCompany()!=null) {
			GAuditInfo wangpinginfos= getSchoolSortInfoByIdAndState(gContest.getId(),"4",user.getCompany().getId());
			if (wangpinginfos!=null) {
				xsWangpin.put("getScore",wangpinginfos.getScore());
				xsWangpin.put("Review_the_content", wangpinginfos.getAuditName());
				xsWangpin.put("Current_rank", wangpinginfos.getSort());
				xsWangpin.put("advice", wangpinginfos.getSuggest());
				arrayXsList.add(xsWangpin);
			}else{
				xsWangpin.put("getScore","");
				xsWangpin.put("Review_the_content", "");
				xsWangpin.put("Current_rank", "");
				xsWangpin.put("advice", "");
				arrayXsList.add(xsWangpin);
			}
		}else{
			xsWangpin.put("getScore","");
			xsWangpin.put("Review_the_content", "");
			xsWangpin.put("Current_rank", "");
			xsWangpin.put("advice", "");
			arrayXsList.add(xsWangpin);
		}

		JSONObject xsLuyan= new JSONObject();
		if (gContest!=null&&gContest.getId()!=null&&user.getCompany()!=null) {
			GAuditInfo luyaninfos= getSchoolSortInfoByIdAndState(gContest.getId(),"5",user.getCompany().getId());
			if (luyaninfos!=null) {
				xsLuyan.put("getScore",luyaninfos.getScore());
				xsLuyan.put("Review_the_content", luyaninfos.getAuditName());
				xsLuyan.put("Current_rank", luyaninfos.getSort());
				xsLuyan.put("advice", luyaninfos.getSuggest());
				arrayXsList.add(xsLuyan);
			}else{
				xsLuyan.put("getScore","");
				xsLuyan.put("Review_the_content", "");
				xsLuyan.put("Current_rank", "");
				xsLuyan.put("advice", "");
				arrayXsList.add(xsLuyan);
			}
		}else{
			xsLuyan.put("getScore","");
			xsLuyan.put("Review_the_content", "");
			xsLuyan.put("Current_rank", "");
			xsLuyan.put("advice", "");
			arrayXsList.add(xsLuyan);
		}
		arrayXs.put("list", arrayXsList);
		gContestContentlistArray.add(arrayXs);
		gContestContentlist.put("list",gContestContentlistArray);

		obj.put("project_code", gContestobj);
		obj.put("table_first", gContestContentlist);
		return obj;
	}

	public List<GContest> getGcontestByNameNoId(String id, String pName) {
		List<GContest> gContests=dao.getGcontestByNameNoId(id,pName);
		return gContests;
	}

	public Page<Map<String, String>> getGcontestChangeList(Page<Map<String, String>> page, Map<String, Object> param) {

		if (page.getPageNo()<=0) {
			page.setPageNo(1);
		}
		int count=dao.getGcontestChangeListCount(param);
		param.put("offset", (page.getPageNo()-1)*page.getPageSize());
		param.put("pageSize", page.getPageSize());
		param.put("orderBy", page.getOrderBy());
		param.put("orderByType", page.getOrderByType());
		List<Map<String,String>> list=null;
		if (count>0) {
			list=dao.getGcontestChangeList(param);
			if (list!=null) {
				List<String> ids=new ArrayList<String>();
				for(Map<String,String> map:list) {
					ids.add(map.get("id"));
					GContest gContest=get(map.get("id"));
			    	if (gContest.getAct().getProcInsId()!=null) {
						String taskId=actTaskService.getTaskidByProcInsId(gContest.getAct().getProcInsId());
						Task task=actTaskService.getTask(taskId);
						if (task!=null) {
							map.put("taskDef",task.getProcessDefinitionId());
							map.put("taskIn",task.getProcessInstanceId());
						}
					}
			    	map.put("auditCode", map.get("auditState"));
					map.put("auditState", GContestStatusEnum.getNameByValue(map.get("auditState")));
				}
				List<Map<String,String>> ps=dao.getMyGcontestListPerson(ids);
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

	@Transactional(readOnly = false)
	public void changeCollegeExport(JSONArray myJsonArray, GContest gContest,boolean isAudit) {
		for(int i=0;i<myJsonArray.size();i++) {
			String auditName=(String) myJsonArray.getJSONObject(i).get("auditName");
			String auditId=(String) myJsonArray.getJSONObject(i).get("auditId");
			String auditSuggest=(String) myJsonArray.getJSONObject(i).get("auditSuggest");
			String auditScore=(String) myJsonArray.getJSONObject(i).get("auditScore");
			gContest.setComment(auditSuggest);
		    gContest.setgScore(auditScore);
		    GAuditInfo pai =new GAuditInfo();
		    if (isAudit) {
		    	pai.setId(auditId);
		    	pai.setSuggest(auditSuggest);
		    	pai.setScore(Float.parseFloat(auditScore));
		    	pai.setGId(gContest.getId());
		    	gAuditInfoService.updateData(pai);
		    	float average=0;
				GAuditInfo infoSerch=new GAuditInfo();
				infoSerch.setGId(gContest.getId());
				infoSerch.setAuditLevel("1");
				average= gAuditInfoService.getAuditAvgInfo(infoSerch);
				gContest.setCollegeExportScore(average);
				gContest.setCollegeScore(average);
				super.dao.updateState(gContest);
		    }else{
			    if (!auditId.isEmpty()) {
			    	pai.setId(auditId);
			    	pai.setSuggest(auditSuggest);
			    	pai.setGId(gContest.getId());
			    	pai.setScore(Float.parseFloat(auditScore));
			    	gAuditInfoService.updateData(pai);
			    	float average=0;
					GAuditInfo infoSerch=new GAuditInfo();
					infoSerch.setGId(gContest.getId());
					infoSerch.setAuditLevel("1");
					average= gAuditInfoService.getAuditAvgInfo(infoSerch);
					gContest.setCollegeExportScore(average);
					gContest.setCollegeScore(average);
					super.dao.updateState(gContest);
			    }else{
			    	Act act=new Act();
					act.setProcDefKey("gcontest");  //大赛流程名称
				    act.setTaskDefKey("audit1");   // 表示大赛流程阶段 见流程图的userTask的id
			    	User auditUser =userService.findUserById(auditName);
				    pai.setCreateBy(auditUser);
				    pai.setCreateDate(new Date());
				    pai.setAuditId(auditUser.getLoginName());
				    pai.setId(IdGen.uuid());
					saveAuditFirst(gContest,act,pai);
			    }
		    }
		}
	}

	@Transactional(readOnly = false)
	public void changeCollege(JSONObject collegeObject, GContest gContest,boolean isAudit) {
		String collegeSuggest=(String) collegeObject.get("collegeSuggest");
		String collegeResult=(String) collegeObject.get("collegeResult");
		gContest.setComment(collegeSuggest);
	    gContest.setGrade(collegeResult);
	    //找到学院秘书
    	User collegeUser = userService.getCollegeSecUsers(gContest.getDeclareId());
		if (isAudit) {
			GAuditInfo pai=new GAuditInfo();
			pai.setAuditLevel("2");
			pai.setGId(gContest.getId());
			pai=gAuditInfoService.getGAuditInfoByIdAndState(pai);
			pai.setGrade(collegeResult);
	    	pai.setSuggest(collegeSuggest);
	    	//审核update方法
	    	gAuditInfoService.updateData(pai);
	    	gContest.setCollegeSug(collegeSuggest);
			gContest.setCollegeResult(collegeResult);
			super.dao.updateState(gContest);
		}else{
        	Act act=new Act();
			act.setProcDefKey("gcontest");  //大赛流程名称
		    act.setTaskDefKey("audit2");   // 表示大赛流程阶段 见流程图的userTask的id
	    	GAuditInfo pai =new GAuditInfo();
	    	pai.setId(IdGen.uuid());
	    	pai.setCreateDate(new Date());
	    	pai.setCreateBy(collegeUser);
	    	pai.setCreateDate(new Date());
		    pai.setAuditId(collegeUser.getLoginName());
			saveAuditSecond(gContest,act,pai);
		}

	}

	@Transactional(readOnly = false)
	public void changeSchoolExport(JSONArray myJsonArray, GContest gContest, boolean isAudit) {
		for(int i=0;i<myJsonArray.size();i++) {
			String auditName=(String) myJsonArray.getJSONObject(i).get("auditName");
			String auditId=(String) myJsonArray.getJSONObject(i).get("auditId");
			String auditSuggest=(String) myJsonArray.getJSONObject(i).get("auditSuggest");
			String auditScore=(String) myJsonArray.getJSONObject(i).get("auditScore");
			gContest.setComment(auditSuggest);
		    gContest.setgScore(auditScore);
		    GAuditInfo pai =new GAuditInfo();
		    if (isAudit) {
		    	pai.setId(auditId);
		    	pai.setSuggest(auditSuggest);
		    	pai.setScore(Float.parseFloat(auditScore));
		       	pai.setGId(gContest.getId());
		    	gAuditInfoService.updateData(pai);
		    	float average=0;
				GAuditInfo infoSerch=new GAuditInfo();
				infoSerch.setGId(gContest.getId());
				infoSerch.setAuditLevel("3");
				average= gAuditInfoService.getAuditAvgInfo(infoSerch);
				gContest.setCollegeExportScore(average);
				gContest.setCollegeScore(average);
				super.dao.updateState(gContest);
		    }else{
			    if (!auditId.isEmpty()) {
			    	pai.setId(auditId);
			    	pai.setSuggest(auditSuggest);
			    	pai.setScore(Float.parseFloat(auditScore));
			    	gAuditInfoService.updateData(pai);
			    	float average=0;
					GAuditInfo infoSerch=new GAuditInfo();
					infoSerch.setGId(gContest.getId());
					infoSerch.setAuditLevel("1");
					average= gAuditInfoService.getAuditAvgInfo(infoSerch);
					gContest.setCollegeExportScore(average);
					gContest.setCollegeScore(average);
					super.dao.updateState(gContest);
			    }else{
			    	Act act=new Act();
					act.setProcDefKey("gcontest");  //大赛流程名称
				    act.setTaskDefKey("audit3");   // 表示大赛流程阶段 见流程图的userTask的id
			    	User auditUser =userService.findUserById(auditName);
			    	pai.setId(IdGen.uuid());
				    pai.setCreateBy(auditUser);
				    pai.setCreateDate(new Date());
				    pai.setAuditId(auditUser.getLoginName());
					saveAuditThree(gContest,act,pai);
			    }
		    }
		}
	}

	@Transactional(readOnly = false)
	public void changeSchool(JSONObject schoolObject, GContest gContest,boolean isAudit) {
		String schoolSuggest=(String)schoolObject.get("schoolSuggest");
		String schoolResult=(String)schoolObject.get("schoolResult");
		gContest.setComment(schoolSuggest);
	    gContest.setGrade(schoolResult);
		if (isAudit) {
			GAuditInfo pai=new GAuditInfo();
			pai.setAuditLevel("4");
			pai.setGId(gContest.getId());
			pai=gAuditInfoService.getGAuditInfoByIdAndState(pai);
			pai.setGrade(schoolResult);
	    	pai.setSuggest(schoolSuggest);
	    	//审核update方法
	    	gAuditInfoService.updateData(pai);
	    	gContest.setSchoolSug(schoolSuggest);
			gContest.setSchoolResult(schoolResult);
			super.dao.updateState(gContest);
		}else{
        	Act act=new Act();
			act.setProcDefKey("gcontest");  //大赛流程名称
		    act.setTaskDefKey("audit4");   // 表示大赛流程阶段 见流程图的userTask的id
		    //找到学校秘书
	    	User schoolUser = userService.getSchoolSecUsers();
	    	GAuditInfo pai =new GAuditInfo();
	    	pai.setId(IdGen.uuid());
	    	pai.setCreateDate(new Date());
	    	pai.setCreateBy(schoolUser);
	    	pai.setCreateDate(new Date());
		    pai.setAuditId(schoolUser.getLoginName());
			saveAuditFour(gContest,act,pai);
		}
	}

	@Transactional(readOnly = false)
	public void changeSchoolly(JSONObject schoolObject, GContest gContest,boolean isAudit) {
		String schoollySuggest=(String) schoolObject.get("schoollySuggest");
		String schoollyScore=(String) schoolObject.get("schoollyScore");
		gContest.setComment(schoollySuggest);
	    gContest.setgScore(schoollyScore);

		if (isAudit) {
			GAuditInfo pai=new GAuditInfo();
			pai.setAuditLevel("5");
			pai.setGId(gContest.getId());
			pai=gAuditInfoService.getGAuditInfoByIdAndState(pai);
			pai.setScore(Float.parseFloat(gContest.getgScore()));
	    	pai.setSuggest(schoollySuggest);
	    	//审核update方法
	    	gAuditInfoService.updateData(pai);
	    	gContest.setSchoolluyanSug(schoollySuggest);
			gContest.setSchoolluyanScore(Float.parseFloat(gContest.getgScore()));
			super.dao.updateState(gContest);
		}else{
        	Act act=new Act();
			act.setProcDefKey("gcontest");  //大赛流程名称
		    act.setTaskDefKey("audit5");   	//表示大赛流程阶段 见流程图的userTask的id
	    	GAuditInfo pai =new GAuditInfo();
	    	pai.setId(IdGen.uuid());
	    	pai.setCreateDate(new Date());
	    	 //找到学校秘书
	    	User schoolUser = userService.getSchoolSecUsers();
	    	pai.setCreateBy(schoolUser);
	    	pai.setCreateDate(new Date());
		    pai.setAuditId(schoolUser.getLoginName());
			saveAuditFive(gContest,act,pai);
		}
	}

	@Transactional(readOnly = false)
	public void changeSchoolend(JSONObject schoolObject, GContest gContest,boolean isAudit) {
		String schoolendSuggest=(String) schoolObject.get("schoolendSuggest");
		String schoolendResult=(String) schoolObject.get("schoolendResult");
		gContest.setComment(schoolendSuggest);
	    gContest.setGrade(schoolendResult);

		if (isAudit) {
			GAuditInfo pai=new GAuditInfo();
			pai.setAuditLevel("6");
			pai.setGId(gContest.getId());
			pai=gAuditInfoService.getGAuditInfoByIdAndState(pai);
			pai.setGrade(schoolendResult);
	    	pai.setSuggest(schoolendSuggest);
	    	//审核update方法
	    	gAuditInfoService.updateData(pai);
	    	gContest.setSchoolendSug(schoolendSuggest);
			gContest.setSchoolendResult(schoolendResult);
			super.dao.updateState(gContest);
			if (schoolendResult.equals("2")||schoolendResult.equals("3")||schoolendResult.equals("4")) {
				//保存获奖信息
				GContestAward gContestAward=gContestAwardService.getByGid(gContest.getId());
				if (gContestAward!=null) {
					if ((gContestAward.getAward()).equals(schoolendResult)) {
						gContestAward.setContestId(gContest.getId());
						gContestAward.setAward(schoolendResult);
						gContestAward.setAwardLevel("3");
						gContestAwardService.save(gContestAward);
					}
				}else{
					gContestAward=new GContestAward();
					gContestAward.setContestId(gContest.getId());
					gContestAward.setAward(schoolendResult);
					gContestAward.setAwardLevel("3");
					gContestAwardService.save(gContestAward);
				}

			}
		}else{
        	Act act=new Act();
			act.setProcDefKey("gcontest");  //大赛流程名称
		    act.setTaskDefKey("audit6");   	//表示大赛流程阶段 见流程图的userTask的id
	    	GAuditInfo pai =new GAuditInfo();
	    	pai.setId(IdGen.uuid());
	    	pai.setCreateDate(new Date());
	    	 //找到学校秘书
	    	User schoolUser = userService.getSchoolSecUsers();
	    	pai.setCreateBy(schoolUser);
	    	pai.setCreateDate(new Date());
		    pai.setAuditId(schoolUser.getLoginName());
			saveAuditSix(gContest,act,pai);
		}
	}

	@Transactional(readOnly = false)
	public void changeFailState(GContest gContest, String oldId) {
		sysAttachmentService.updateAtt(gContest.getId(),oldId);
		gAuditInfoService.deleteByGid(oldId);
		delete(get(oldId));
	}

	@Transactional(readOnly = false)
	public JSONObject changeCollegefail(JSONObject myJSONObject ,GContest gContest) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "大赛变更成功!");
		//后续添加失败流程
    	JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
		String oldId=gContest.getId();
		//gContest.setId("");
		String gid=submitOld(gContest);
		if (gid==null || gid.equals("")) {
			js.put("ret",0);
			js.put("msg", "该学院专家已被删除，变更失败!");
			return js;
		}
		gContest=get(gid);
		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			List<GAuditInfo> collegeinfos= getInfo(oldId,"1");
			if (collegeExport.size()==collegeinfos.size()) {
				for(int i=0;i<collegeExport.size();i++) {
    				((JSONObject) collegeExport.get(i)).put("auditId","");
    			}
				changeCollegeExport(collegeExport,gContest,false);
			}else{
        		for(GAuditInfo gAuditInfo:collegeinfos) {
        			for(int i=0;i<collegeExport.size();i++) {
        				String auditName=(String) collegeExport.getJSONObject(i).get("auditName");
        				User exportUser=userService.findUserById(auditName);
        				if (gAuditInfo.getAuditId().equals(exportUser.getLoginName())) {
        					((JSONObject) collegeExport.get(i)).put("auditId","");
        					break;
        				}
        			}
    				JSONObject exportObject=new JSONObject();
    				exportObject.put("auditScore",String.valueOf(gAuditInfo.getScore()));
    				exportObject.put("auditId", "");
    				exportObject.put("auditName", gAuditInfo.getCreateBy().getId());
    				exportObject.put("auditSuggest", gAuditInfo.getSuggest());
    				collegeExport.add(exportObject);
        		}
        		changeCollegeExport(collegeExport,gContest,false);
			}
		}else{
			JSONArray collegeExport=new JSONArray();
			List<GAuditInfo> collegeinfos= getInfo(oldId,"1");
			for(GAuditInfo gAuditInfo:collegeinfos) {
				JSONObject exportObject=new JSONObject();
				/*float score=gAuditInfo.getScore();
				DecimalFormat decimalFormat=new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
				String auditScore=decimalFormat.format(score);//format 返回的是字符串*/
				exportObject.put("auditScore", String.valueOf(gAuditInfo.getScore()));
				exportObject.put("auditId", "");
				exportObject.put("auditName", gAuditInfo.getCreateBy().getId());
				exportObject.put("auditSuggest", gAuditInfo.getSuggest());
				collegeExport.add(exportObject);
			}
			changeCollegeExport(collegeExport,gContest,false);
		}
		changeCollege(collegeObject,gContest,false);
		changeFailState(gContest,oldId);
		return js;
	}

	@Transactional(readOnly = false)
	public JSONObject changeSchoolfail(JSONObject myJSONObject ,GContest gContest) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "大赛变更成功!");
		//后续添加失败流程
		String oldId=gContest.getId();
		gContest.setId("");
		String gid=submitOld(gContest);
		if (gid.isEmpty()) {

			js.put("ret",0);
			js.put("msg", "该学校专家已被删除，变更失败!");
			return js;
		}
		gContest=get(gid);

		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			List<GAuditInfo> collegeinfos= getInfo(oldId,"1");
			if (collegeExport.size()==collegeinfos.size()) {
				for(int i=0;i<collegeExport.size();i++) {
    				((JSONObject) collegeExport.get(i)).put("auditId","");
    			}
				changeCollegeExport(collegeExport,gContest,false);
			}else{
        		for(GAuditInfo gAuditInfo:collegeinfos) {
        			for(int i=0;i<collegeExport.size();i++) {
        				String auditName=(String) collegeExport.getJSONObject(i).get("auditName");
        				User exportUser=userService.findUserById(auditName);
        				if (gAuditInfo.getAuditId().equals(exportUser.getLoginName())) {
        					((JSONObject) collegeExport.get(i)).put("auditId","");
        					break;
        				}
        			}
    				JSONObject exportObject=new JSONObject();
    				exportObject.put("auditScore",String.valueOf(gAuditInfo.getScore()));
    				exportObject.put("auditName", gAuditInfo.getCreateBy().getId());
    				exportObject.put("auditId", "");
    				exportObject.put("auditSuggest", gAuditInfo.getSuggest());
    				collegeExport.add(exportObject);
        		}
        		changeCollegeExport(collegeExport,gContest,false);
			}
		}else{
			JSONArray collegeExport=new JSONArray();
			List<GAuditInfo> collegeinfos= getInfo(oldId,"1");
			for(GAuditInfo gAuditInfo:collegeinfos) {
				JSONObject exportObject=new JSONObject();
				/*float score=gAuditInfo.getScore();
				DecimalFormat decimalFormat=new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
				String auditScore=decimalFormat.format(score);//format 返回的是字符串*/
				exportObject.put("auditScore", String.valueOf(gAuditInfo.getScore()));
				exportObject.put("auditId", "");
				exportObject.put("auditName", gAuditInfo.getCreateBy().getId());
				exportObject.put("auditSuggest", gAuditInfo.getSuggest());
				collegeExport.add(exportObject);
			}
			changeCollegeExport(collegeExport,gContest,false);
		}
		JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
		if (myJSONObject.get("college")!=null) {
			changeCollege(collegeObject,gContest,false);
		}else{
			List<GAuditInfo> collegeInfo=getInfo(oldId,"2");
			GAuditInfo gAuditInfo=collegeInfo.get(0);
			collegeObject=new JSONObject();
			collegeObject.put("collegeResult", gAuditInfo.getGrade());
			collegeObject.put("collegeSuggest", gAuditInfo.getSuggest());
			changeCollege(collegeObject,gContest,false);
		}
		if (myJSONObject.get("schoolExport")!=null) {
			JSONArray schoolExport=(JSONArray) myJSONObject.get("schoolExport");
			List<GAuditInfo> schoolinfos= getInfo(oldId,"3");
			if (schoolExport.size()==schoolinfos.size()) {
				for(int i=0;i<schoolExport.size();i++) {
    				((JSONObject) schoolExport.get(i)).put("auditId","");
    			}
				changeSchoolExport(schoolExport,gContest,false);
			}else{
        		for(GAuditInfo gAuditInfo:schoolinfos) {
        			for(int i=0;i<schoolExport.size();i++) {
        				String auditName=(String) schoolExport.getJSONObject(i).get("auditName");
        				User exportUser=userService.findUserById(auditName);
        				if (gAuditInfo.getAuditId().equals(exportUser.getLoginName())) {
        					((JSONObject) schoolExport.get(i)).put("auditId","");
        					break;
        				}
        			}
    				JSONObject exportObject=new JSONObject();
    				exportObject.put("auditScore",String.valueOf(gAuditInfo.getScore()));
    				exportObject.put("auditId", "");
    				exportObject.put("auditName", gAuditInfo.getCreateBy().getId());
    				exportObject.put("auditSuggest", gAuditInfo.getSuggest());
    				schoolExport.add(exportObject);
        		}
        		changeSchoolExport(schoolExport,gContest,false);
			}
		}else{
			JSONArray schoolExport=new JSONArray();
			List<GAuditInfo> schoolinfos= getInfo(oldId,"3");
			for(GAuditInfo gAuditInfo:schoolinfos) {
				JSONObject exportObject=new JSONObject();
				/*float score=gAuditInfo.getScore();
				DecimalFormat decimalFormat=new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
				String auditScore=decimalFormat.format(score);//format 返回的是字符串*/
				exportObject.put("auditScore", String.valueOf(gAuditInfo.getScore()));
				exportObject.put("auditId","");
				exportObject.put("auditName", gAuditInfo.getCreateBy().getId());
				exportObject.put("auditSuggest", gAuditInfo.getSuggest());
				schoolExport.add(exportObject);
			}
			changeSchoolExport(schoolExport,gContest,false);
		}
		JSONObject schoolObject=(JSONObject) myJSONObject.get("school");
		changeSchool(schoolObject,gContest,false);
		changeFailState(gContest,oldId);
		return js;
	}

	private  List<GAuditInfo> getInfo(String gId,String auditStep) {
		GAuditInfo pai=new GAuditInfo();
	    pai.setGId(gId);
	    pai.setAuditLevel(auditStep);
	    List<GAuditInfo> infos= gAuditInfoService.getInfo(pai);
	    return infos;
	}

	@Transactional(readOnly = false)

	public JSONObject updateFirst(JSONObject myJSONObject, GContest gContest) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "大赛变更成功!");
		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			changeCollegeExport(collegeExport,gContest,false);
			gContest=get(gContest.getId());
			//第一步完成才能进行第二步
			gContest=get(gContest.getId());
			if (gContest.getAuditState().equals("2")&&myJSONObject.get("college")!=null) {
				JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
				String collegeResult=(String) collegeObject.get("collegeResult");
				if (collegeResult.equals("1")) {
					changeCollege(collegeObject,gContest,false);
					//第二步完成才能进行第三步
					gContest=get(gContest.getId());
					if (gContest.getAuditState().equals("3")&&myJSONObject.get("schoolExport")!=null) {
						JSONArray schoolExport=(JSONArray) myJSONObject.get("schoolExport");
						changeSchoolExport(schoolExport,gContest,false);
						//第三步完成才能进行第四步
						gContest=get(gContest.getId());
						if (gContest.getAuditState().equals("4")&&myJSONObject.get("school")!=null) {
							JSONObject schoolObject=(JSONObject) myJSONObject.get("school");
							String schoolResult=(String) schoolObject.get("schoolResult");
							if (schoolResult.equals("1")) {
								changeSchool(schoolObject,gContest,false);
								//第四步完成才能进行第五步
								gContest=get(gContest.getId());
								if (gContest.getAuditState().equals("5")&&myJSONObject.get("schoolly")!=null) {
									JSONObject schoollyObject=(JSONObject) myJSONObject.get("schoolly");
									changeSchoolly(schoollyObject,gContest,false);
									//第五步完成才能进行第六步
									gContest=get(gContest.getId());
									if (gContest.getAuditState().equals("6")&& myJSONObject.get("schoolend")!=null) {
										JSONObject schoolendObject=(JSONObject) myJSONObject.get("schoolend");
										changeSchoolend(schoolendObject,gContest,false);
									}
								}
							}else{
								changeSchool(schoolObject,gContest,false);
								//校赛失败后续添加流程
							}
						}
					}
				}else{
					//院赛失败后续添加流程
					changeCollege(collegeObject,gContest,false);
				}
			}
		}
		return js;
	}

	@Transactional(readOnly = false)
	public JSONObject updateSecond(JSONObject myJSONObject, GContest gContest) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "大赛变更成功!");
		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			changeCollegeExport(collegeExport,gContest,true);
		}
		if (myJSONObject.get("college")!=null) {
			JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
			String collegeResult=(String) collegeObject.get("collegeResult");
			if (collegeResult.equals("1")) {
				changeCollege(collegeObject,gContest,false);
				//第二步完成才能进行第三步
				gContest=get(gContest.getId());
				if (gContest.getAuditState().equals("3")&&myJSONObject.get("schoolExport")!=null) {
					JSONArray schoolExport=(JSONArray) myJSONObject.get("schoolExport");
					changeSchoolExport(schoolExport,gContest,false);
					//第三步完成才能进行第四步
					gContest=get(gContest.getId());
					if (gContest.getAuditState().equals("4")&&myJSONObject.get("school")!=null) {
						JSONObject schoolObject=(JSONObject) myJSONObject.get("school");
						String schoolResult=(String) schoolObject.get("schoolResult");
						if (schoolResult.equals("1")) {
							changeSchool(schoolObject,gContest,false);
							//第四步完成才能进行第五步
							gContest=get(gContest.getId());
							if (gContest.getAuditState().equals("5")&&myJSONObject.get("schoolly")!=null) {
								JSONObject schoollyObject=(JSONObject) myJSONObject.get("schoolly");
								changeSchoolly(schoollyObject,gContest,false);
								//第五步完成才能进行第六步
								gContest=get(gContest.getId());
								if (gContest.getAuditState().equals("6")&& myJSONObject.get("schoolend")!=null) {
									JSONObject schoolendObject=(JSONObject) myJSONObject.get("schoolend");
									changeSchoolend(schoolendObject,gContest,false);
								}
							}
						}else{
							//校赛失败后续添加流程
							changeSchool(schoolObject,gContest,false);
						}
					}
				}
			}else{
				//后续添加失败流程
				changeCollege(collegeObject,gContest,false);
			}
		}
		return js;
	}

	@Transactional(readOnly = false)
	public JSONObject updateThree(JSONObject myJSONObject, GContest gContest) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "大赛变更成功!");
		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			changeCollegeExport(collegeExport,gContest,true);
		}
		if (myJSONObject.get("college")!=null) {
			JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
			String collegeResult=(String) collegeObject.get("collegeResult");
			if (collegeResult.equals("1")) {
				changeCollege(collegeObject,gContest,true);
			}else{
				//后续添加失败流程
				js=changeCollegefail(myJSONObject,gContest);
				myJSONObject=new JSONObject();
			}
		}
		if (myJSONObject.get("schoolExport")!=null) {
			JSONArray schoolExport=(JSONArray) myJSONObject.get("schoolExport");
			changeSchoolExport(schoolExport,gContest,false);
			//第三步完成才能进行第四步
			gContest=get(gContest.getId());
			if (gContest.getAuditState().equals("4")&&myJSONObject.get("school")!=null) {
				JSONObject schoolObject=(JSONObject) myJSONObject.get("school");
				String schoolResult=(String) schoolObject.get("schoolResult");
				if (schoolResult.equals("1")) {
					changeSchool(schoolObject,gContest,false);
					//第四步完成才能进行第五步
					gContest=get(gContest.getId());
					if (gContest.getAuditState().equals("5")&&myJSONObject.get("schoolly")!=null) {
						JSONObject schoollyObject=(JSONObject) myJSONObject.get("schoolly");
						changeSchoolly(schoollyObject,gContest,false);
						//第五步完成才能进行第六步
						gContest=get(gContest.getId());
						if (gContest.getAuditState().equals("6")&& myJSONObject.get("schoolend")!=null) {
							JSONObject schoolendObject=(JSONObject) myJSONObject.get("schoolend");
							changeSchoolend(schoolendObject,gContest,false);
						}
					}
				}else{
					//校赛失败后续添加流程
					changeSchool(schoolObject,gContest,false);
				}
			}
		}
		return js;
	}

	@Transactional(readOnly = false)
	public JSONObject updateFour(JSONObject myJSONObject, GContest gContest) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "大赛变更成功!");
		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			changeCollegeExport(collegeExport,gContest,true);
		}
		if (myJSONObject.get("college")!=null) {
			JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
			String collegeResult=(String) collegeObject.get("collegeResult");
			if (collegeResult.equals("1")) {
				changeCollege(collegeObject,gContest,true);
			}else{
				//后续添加失败流程
				js=changeCollegefail(myJSONObject,gContest);
				myJSONObject=new JSONObject();
			}
		}
		if (myJSONObject.get("schoolExport")!=null) {
			JSONArray schoolExport=(JSONArray) myJSONObject.get("schoolExport");
			changeSchoolExport(schoolExport,gContest,true);
		}
		if (myJSONObject.get("school")!=null) {
			JSONObject schoolObject=(JSONObject) myJSONObject.get("school");
			String schoolResult=(String) schoolObject.get("schoolResult");
			if (schoolResult.equals("1")) {
				changeSchool(schoolObject,gContest,false);
				//第四步完成才能进行第五步
				gContest=get(gContest.getId());
				if (gContest.getAuditState().equals("5")&&myJSONObject.get("schoolly")!=null) {
					JSONObject schoollyObject=(JSONObject) myJSONObject.get("schoolly");
					changeSchoolly(schoollyObject,gContest,false);
					//第五步完成才能进行第六步
					gContest=get(gContest.getId());
					if (gContest.getAuditState().equals("6")&& myJSONObject.get("schoolend")!=null) {
						JSONObject schoolendObject=(JSONObject) myJSONObject.get("schoolend");
						changeSchoolend(schoolendObject,gContest,false);
					}
				}
			}else{
				//校赛失败后续添加流程
				changeSchool(schoolObject,gContest,false);
			}
		}
		return js;
	}

	@Transactional(readOnly = false)
	public JSONObject updateFive(JSONObject myJSONObject, GContest gContest) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "大赛变更成功!");
		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			changeCollegeExport(collegeExport,gContest,true);
		}
		if (myJSONObject.get("college")!=null) {
			JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
			String collegeResult=(String) collegeObject.get("collegeResult");
			if (collegeResult.equals("1")) {
				changeCollege(collegeObject,gContest,true);
			}else if (collegeResult.equals("0")) {
				//后续添加失败流程
				js=changeCollegefail(myJSONObject,gContest);
				myJSONObject=new JSONObject();
			}
		}
		if (myJSONObject.get("schoolExport")!=null) {
			JSONArray schoolExport=(JSONArray) myJSONObject.get("schoolExport");
			changeSchoolExport(schoolExport,gContest,true);
		}
		if (myJSONObject.get("school")!=null) {
			JSONObject schoolObject=(JSONObject) myJSONObject.get("school");
			String schoolResult=(String) schoolObject.get("schoolResult");
			if (schoolResult.equals("1")) {
				changeSchool(schoolObject,gContest,true);
			}else{
				//校赛添加失败流程
				js=changeSchoolfail(myJSONObject,gContest);
				myJSONObject=new JSONObject();
			}
		}
		if (myJSONObject.get("schoolly")!=null) {
			JSONObject schoollyObject=(JSONObject) myJSONObject.get("schoolly");
			changeSchoolly(schoollyObject,gContest,false);
			//第五步完成才能进行第六步
			gContest=get(gContest.getId());
			if (gContest.getAuditState().equals("6")&& myJSONObject.get("schoolend")!=null) {
				JSONObject schoolendObject=(JSONObject) myJSONObject.get("schoolend");
				changeSchoolend(schoolendObject,gContest,false);
			}
		}
		return js;
	}

	@Transactional(readOnly = false)
	public JSONObject updateSix(JSONObject myJSONObject, GContest gContest) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "大赛变更成功!");
		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			changeCollegeExport(collegeExport,gContest,true);
		}
		if (myJSONObject.get("college")!=null) {
			JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
			String collegeResult=(String) collegeObject.get("collegeResult");
			if (collegeResult.equals("1")) {
				changeCollege(collegeObject,gContest,true);
			}else if (collegeResult.equals("0")) {
				//后续添加失败流程
				js=changeCollegefail(myJSONObject,gContest);
				myJSONObject=new JSONObject();
			}
		}
		if (myJSONObject.get("schoolExport")!=null) {
			JSONArray schoolExport=(JSONArray) myJSONObject.get("schoolExport");
			changeSchoolExport(schoolExport,gContest,true);
		}
		if (myJSONObject.get("school")!=null) {
			JSONObject schoolObject=(JSONObject) myJSONObject.get("school");
			String schoolResult=(String) schoolObject.get("schoolResult");
			if (schoolResult.equals("1")) {
				changeSchool(schoolObject,gContest,true);
			}else{
				//校赛后续添加失败流程
				js=changeSchoolfail(myJSONObject,gContest);
				myJSONObject=new JSONObject();
			}
		}
		if (myJSONObject.get("schoolly")!=null) {
			JSONObject schoollyObject=(JSONObject) myJSONObject.get("schoolly");
			changeSchoolly(schoollyObject,gContest,true);
		}
		//第五步完成才能进行第六步
		if (myJSONObject.get("schoolend")!=null) {
			JSONObject schoolendObject=(JSONObject) myJSONObject.get("schoolend");
			changeSchoolend(schoolendObject,gContest,false);
		}
		return js;
	}

	@Transactional(readOnly = false)
	public JSONObject updateSeven(JSONObject myJSONObject, GContest gContest) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "大赛变更成功!");
		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			changeCollegeExport(collegeExport,gContest,true);
		}
		if (myJSONObject.get("college")!=null) {
			JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
			String collegeResult=(String) collegeObject.get("collegeResult");
			if (collegeResult.equals("1")) {
				changeCollege(collegeObject,gContest,true);
			}else{
				//后续添加失败流程
				js=changeCollegefail(myJSONObject,gContest);
				myJSONObject=new JSONObject();
			}
		}
		if (myJSONObject.get("schoolExport")!=null) {
			JSONArray schoolExport=(JSONArray) myJSONObject.get("schoolExport");
			changeSchoolExport(schoolExport,gContest,true);
		}
		if (myJSONObject.get("school")!=null) {
			JSONObject schoolObject=(JSONObject) myJSONObject.get("school");
			String schoolResult=(String) schoolObject.get("schoolResult");
			if (schoolResult.equals("1")) {
				changeSchool(schoolObject,gContest,true);
			}else{
				//校赛添加失败流程
				js=changeSchoolfail(myJSONObject,gContest);
				myJSONObject=new JSONObject();
			}
		}
		if (myJSONObject.get("schoolly")!=null) {
			JSONObject schoollyObject=(JSONObject) myJSONObject.get("schoolly");
			changeSchoolly(schoollyObject,gContest,true);
		}
		//第五步完成才能进行第六步
		if (myJSONObject.get("schoolend")!=null) {
			JSONObject schoolendObject=(JSONObject) myJSONObject.get("schoolend");
			changeSchoolend(schoolendObject,gContest,true);
		}
		return js;
	}

	@Transactional(readOnly = false)
	public JSONObject updateSchoolFail(JSONObject myJSONObject, GContest gContest) {
		JSONObject js=new JSONObject();
		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			changeCollegeExport(collegeExport,gContest,true);
		}
		if (myJSONObject.get("college")!=null) {
			JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
			String collegeResult=(String) collegeObject.get("collegeResult");
			if (collegeResult.equals("1")) {
				changeCollege(collegeObject,gContest,true);
			}else{
				//后续添加失败流程
				js=changeCollegefail(myJSONObject,gContest);
				myJSONObject=new JSONObject();
			}
		}
		if (myJSONObject.get("schoolExport")!=null) {
			JSONArray schoolExport=(JSONArray) myJSONObject.get("schoolExport");
			changeSchoolExport(schoolExport,gContest,true);
		}
		if (myJSONObject.get("school")!=null) {
			JSONObject schoolObject=(JSONObject) myJSONObject.get("school");
			String schoolResult=(String) schoolObject.get("schoolResult");
			if (schoolResult.equals("1")) {
				List<GAuditInfo> schoolsecinfos= getInfo(gContest.getId(),"4");
				if (schoolsecinfos.size()>0) {
					gAuditInfoService.delete(schoolsecinfos.get(0));
				}
				changeSchool(schoolObject,gContest,false);
				gContest=get(gContest.getId());
				if (gContest.getAuditState().equals("5")&& myJSONObject.get("schoolly")!=null) {
					JSONObject schoollyObject=(JSONObject) myJSONObject.get("schoolly");
					changeSchoolly(schoollyObject,gContest,false);
					//第五步完成才能进行第六步
					gContest=get(gContest.getId());
					if (gContest.getAuditState().equals("6")&& myJSONObject.get("schoolend")!=null) {
						JSONObject schoolendObject=(JSONObject) myJSONObject.get("schoolend");
						changeSchoolend(schoolendObject,gContest,false);
					}
				}
			}else{
				changeSchool(schoolObject,gContest,true);
				//后续添加失败流程
			}
		}
		return js;
	}

	@Transactional(readOnly = false)
	public JSONObject updateCollegeFail(JSONObject myJSONObject, GContest gContest) {
		JSONObject js=new JSONObject();
		if (myJSONObject.get("collegeExport")!=null) {
			JSONArray collegeExport=(JSONArray) myJSONObject.get("collegeExport");
			changeCollegeExport(collegeExport,gContest,true);
		}
		if (myJSONObject.get("college")!=null) {
			JSONObject collegeObject=(JSONObject) myJSONObject.get("college");
			String collegeResult=(String) collegeObject.get("collegeResult");
			if (collegeResult.equals("1")) {
				List<GAuditInfo> collegeinfos= getInfo(gContest.getId(),"2");
				if (collegeinfos.size()>0) {
					gAuditInfoService.delete(collegeinfos.get(0));
				}
				changeCollege(collegeObject,gContest,false);
				//第二步完成才能进行第三步
				gContest=get(gContest.getId());
				if (gContest.getAuditState().equals("3")&&myJSONObject.get("schoolExport")!=null) {
					JSONArray schoolExport=(JSONArray) myJSONObject.get("schoolExport");
					changeSchoolExport(schoolExport,gContest,false);
					//第三步完成才能进行第四步
					gContest=get(gContest.getId());
					if (gContest.getAuditState().equals("4")&&myJSONObject.get("school")!=null) {
						JSONObject schoolObject=(JSONObject) myJSONObject.get("school");
						String schoolResult=(String) schoolObject.get("schoolResult");
						if (schoolResult.equals("1")) {
							changeSchool(schoolObject,gContest,false);
							//第四步完成才能进行第五步
							gContest=get(gContest.getId());
							if (gContest.getAuditState().equals("5")&&myJSONObject.get("schoolly")!=null) {
								JSONObject schoollyObject=(JSONObject) myJSONObject.get("schoolly");
								changeSchoolly(schoollyObject,gContest,false);
								//第五步完成才能进行第六步
								gContest=get(gContest.getId());
								if (gContest.getAuditState().equals("6")&& myJSONObject.get("schoolend")!=null) {
									JSONObject schoolendObject=(JSONObject) myJSONObject.get("schoolend");
									changeSchoolend(schoolendObject,gContest,false);
								}
							}
						}else if (schoolResult.equals("0")) {
							//校赛失败后续添加流程
							changeSchool(schoolObject,gContest,true);
						}
					}
				}
			}else{
				//后续添加失败流程
				changeCollege(collegeObject,gContest,true);
			}
		}
		return js;
	}

	public int findGcontestByTeamId(String id) {
		return dao.findGcontestByTeamId(id);
	}

	public int todoCount(Map<String, Object> param) {
		int count=dao.getTodoCount(param);
		return count;
	}

	public int hasdoCount(Map<String, Object> param) {
		int count=dao.getHasdoCount(param);
		return count;
	}
}