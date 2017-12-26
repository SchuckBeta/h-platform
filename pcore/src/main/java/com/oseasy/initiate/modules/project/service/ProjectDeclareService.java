package com.oseasy.initiate.modules.project.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.oseasy.initiate.common.service.CommonService;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.FloatUtils;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.IdUtils;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.dao.ActDao;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.actyw.dao.ActYwDao;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.attachment.enums.FileStepEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.auditstandard.dao.AuditStandardDetailDao;
import com.oseasy.initiate.modules.auditstandard.dao.AuditStandardDetailInsDao;
import com.oseasy.initiate.modules.auditstandard.entity.AuditStandardDetailIns;
import com.oseasy.initiate.modules.auditstandard.vo.AsdVo;
import com.oseasy.initiate.modules.excellent.entity.ExcellentShow;
import com.oseasy.initiate.modules.excellent.service.ExcellentShowService;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
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
import com.oseasy.initiate.modules.project.vo.ProjectDeclareListVo;
import com.oseasy.initiate.modules.project.vo.ProjectDeclareVo;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.project.vo.ProjectNodeVo;
import com.oseasy.initiate.modules.promodel.dao.ProModelDao;
import com.oseasy.initiate.modules.promodel.entity.ProModel;
import com.oseasy.initiate.modules.promodel.service.ProModelService;
import com.oseasy.initiate.modules.proprojectmd.dao.ProModelMdDao;
import com.oseasy.initiate.modules.proprojectmd.entity.ProModelMd;
import com.oseasy.initiate.modules.sco.dao.ScoAffirmCriterionDao;
import com.oseasy.initiate.modules.sco.dao.ScoAffirmDao;
import com.oseasy.initiate.modules.sco.dao.ScoAllotRatioDao;
import com.oseasy.initiate.modules.sco.dao.ScoScoreDao;
import com.oseasy.initiate.modules.sco.entity.ScoAffirm;
import com.oseasy.initiate.modules.sco.entity.ScoScore;
import com.oseasy.initiate.modules.sco.vo.ScoAffrimCriterionVo;
import com.oseasy.initiate.modules.sco.vo.ScoRatioVo;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.dao.TeamUserHistoryDao;
import com.oseasy.initiate.modules.team.dao.TeamUserRelationDao;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.service.TeamUserHistoryService;
import com.oseasy.initiate.modules.team.vo.TeamStudentVo;
import com.oseasy.initiate.modules.team.vo.TeamTeacherVo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 项目申报Service
 *
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
	@Autowired
	ScoAllotRatioDao scoAllotRatioDao;
	@Autowired
	ScoAffirmCriterionDao scoAffirmCriterionDao;
	@Autowired
	ScoAffirmDao scoAffirmDao;
	@Autowired
	TeamUserRelationDao teamUserRelationDao;
	@Autowired
	TeamUserHistoryService teamUserHistoryService;
	@Autowired
	ScoScoreDao scoScoreDao;

	@Autowired
	AuditStandardDetailDao auditStandardDetailDao;
	@Autowired
	AuditStandardDetailInsDao auditStandardDetailInsDao;
	@Autowired
	ActYwGnodeService actYwGnodeService;
	@Autowired
	ActYwDao actYwDao;
	@Autowired
	private ExcellentShowService excellentShowService;
	@Autowired
	private ProModelService proModelService;
	@Autowired
	private ProModelDao proModelDao;
	@Autowired
	private TeamUserHistoryDao teamUserHistoryDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	private ProModelMdDao proModelMdDao;

	private void updateTeamUserHistoryByStatus(ProjectDeclare projectDeclare) {
		if ("8".equals(projectDeclare.getStatus()) || "9".equals(projectDeclare.getStatus())) {
			teamUserHistoryDao.updateFinishAsClose(projectDeclare.getId());
		}
	}

	@Transactional(readOnly = false)
	public void updateStatus(ProjectDeclare projectDeclare) {
		dao.updateStatus(projectDeclare);
		updateTeamUserHistoryByStatus(projectDeclare);
	}

	public void getPersonNumForAsdIndex(AsdVo asdVo) {
		Map<String, BigDecimal> map = dao.getPersonNumForAsdIndex(asdVo.getDataYear() + "-01-01 00:00:00");
		Map<String, BigDecimal> map2 = dao.getProjectNumForAsdIndex(asdVo.getDataYear() + "-01-01 00:00:00");
		if (map != null) {
			asdVo.setApplyNum(StringUtil.getString(map.get("st")));
			asdVo.setTeacherNum(StringUtil.getString(map.get("te")));
		}
		if (map2 != null) {
			asdVo.setInnovateNum(StringUtil.getString(map2.get("st")));
			asdVo.setBusinessNum(StringUtil.getString(map2.get("te")));
		}
	}

	public void getPersonNumForAsdIndexFromModel(AsdVo asdVo) {
		Map<String, BigDecimal> map = dao.getPersonNumForAsdIndexFromModel(asdVo);
		Map<String, BigDecimal> map2 = dao.getProjectNumForAsdIndexFromModel(asdVo);
		if (map != null) {
			asdVo.setApplyNum(StringUtil.getString(map.get("st")));
			asdVo.setTeacherNum(StringUtil.getString(map.get("te")));
		}
		if (map2 != null) {
			asdVo.setInnovateNum(StringUtil.getString(map2.get("st")));
			asdVo.setBusinessNum(StringUtil.getString(map2.get("te")));
		}
	}

	public List<Map<String, String>> getCurProProject() {
		return dao.getCurProProject();
	}

	public List<Map<String, String>> getTimeIndexSecondTabs(String type, String actywId, String uid) {
		if ("1".equals(type)) {
			return dao.getTimeIndexSecondTabs(actywId, uid);
		} else {
			return dao.getTimeIndexSecondTabsFromModel(actywId, uid);
		}
	}
	public List<ProjectDeclare> getProjectByCdn(String num, String name, String uid) {
		return dao.getProjectByCdn(num, name, uid);
	}

	public List<Map<String, String>> getValidProjectAnnounce() {
		return dao.getValidProjectAnnounce();
	}

	public List<Map<String, String>> getCurProjectInfo(String uid) {
		return dao.getCurProjectInfo(uid);
	}

	public List<Map<String, String>> getCurProjectInfoByTeam(String tid) {
		return dao.getCurProjectInfoByTeam(tid);
	}

	public List<ProjectDeclare> getCurProjectInfoByLeader(String leaderId) {
		return dao.getCurProjectInfoByLeader(leaderId);
	}

	/**
	 * 根据项目负责人查找所有项目.
	 * @param uid 用户ID
	 * @return List
	 */
	public List<ProModel> findListByLeader(String uid) {
		return proModelDao.findListByLeader(uid);
	}

	/**
   * 根据项目负责人查找所有项目+大赛.
   * @param uid 用户ID
	 * @return List
	 */
	public List<ProModel> findListAllByLeader(String uid) {
	  return proModelDao.findListAllByLeader(uid);
	}

	private boolean checkIsValid(Date now, Date startdt, Date enddt) throws ParseException {
		if ("N".equals(OPEN_TIME_LIMIT)) {
			return true;
		} else {
			startdt = changeDate(startdt, " 00:00:00");
			enddt = changeDate(enddt, " 23:59:59");
			if (startdt != null && now.before(startdt)) {
				return false;
			}
			if (enddt != null && now.after(enddt)) {
				return false;
			}
			return true;
		}
	}

	private Date changeDate(Date d, String s) throws ParseException {
		if (d == null || StringUtil.isEmpty(s)) {
			return d;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.parse(sdf2.format(d) + s);
		}
	}

	public JSONObject getTimeIndexData(String pptype, String actywId, String projectId) throws ParseException {
		if ("1".equals(pptype)) {
			return getDcTimeIndexData(actywId, projectId);
		} else {
			return getActTimeIndexData(actywId, projectId);
		}
	}

	// 根据pro_model表查找时间轴数据
	private JSONObject getActTimeIndexData(String actywId, String projectId) throws ParseException {
		ActYw actYw = actYwDao.get(actywId);
		if (actYw == null) {
			return null;
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("projectId", projectId);
		param.put("uid", UserUtils.getUser().getId());
		param.put("actywId", actywId);
		param.put("startdate", DateUtil.formatDate(actYw.getProProject().getStartDate(), "yyyy-MM-dd HH:mm:ss"));
		param.put("enddate", DateUtil.formatDate(actYw.getProProject().getEndDate(), "yyyy-MM-dd HH:mm:ss"));
		Map<String, String> lastpro = null;
		if (StringUtil.isNotEmpty(projectId)) {
			lastpro = dao.getLastProjectInfoByActyw(param);
		}
		if (lastpro == null || lastpro.size() == 0) {
			return getInitActTimeIndexData(actYw);
		} else {
			return getProActTimeIndexData(actYw, lastpro);
		}
	}

	// 查找申报过项目的时间轴数据
	private JSONObject getProActTimeIndexData(ActYw actYw, Map<String, String> lastpro) throws ParseException {
		User user = UserUtils.getUser();
		JSONObject data = new JSONObject();
		JSONArray contents = new JSONArray();
		JSONArray files = new JSONArray();
		data.put("proname", lastpro.get("name"));
		data.put("number", lastpro.get("number"));
		data.put("pid", lastpro.get("id"));
		data.put("leader", lastpro.get("leader"));
		data.put("apply_time", lastpro.get("apply_time"));
		boolean isLeader = false;
		if (user.getId().equals(lastpro.get("leaderid"))) {
			isLeader = true;
		}
		fillReports(lastpro.get("id"), files, user, "");// 填装周报、经费等信息
		List<ProjectPlan> plans = projectPlanDao.findListByProjectId(lastpro.get("id"));
		fillPlans(plans, contents);// 填装任务信息
		List<Map<String, Object>> nodes = actYwGnodeService.treeDataForTimeIndexByYwId(null, actYw.getId(), "1", null,
				null, null);
		if (nodes != null && nodes.size() > 0) {
			Date now = new Date();
			JSONObject startcontent = new JSONObject();
			startcontent.put("type", "milestone");
			startcontent.put("title", "项目申报");
			startcontent.put("desc", getDateStrFromOb(actYw.getProProject().getNodeStartDate()) + "~"
					+ getDateStrFromOb(actYw.getProProject().getNodeEndDate()));
			startcontent.put("start_date", getDateStrFromOb(actYw.getProProject().getNodeStartDate()));
			startcontent.put("end_date", getDateStrFromOb(actYw.getProProject().getNodeEndDate()));
			startcontent.put("current", "true");
			startcontent.put("intro", "");

			String pocid = lastpro.get("proc_ins_id");
			if (StringUtil.isEmpty(pocid)) {// 保存未提交
				if (checkIsValid(now, actYw.getProProject().getNodeStartDate(), actYw.getProProject().getNodeEndDate())
						&& isLeader) {// 还在申报期内
					JSONObject startbtn = new JSONObject();
					startbtn.put("name", "提交项目");
					startbtn.put("url", "/f/cms/form/" + actYw.getProProject().getProjectMark() + "/applyForm?actywId="
							+ actYw.getId());
					startcontent.put("btn_option", startbtn);
				} else {// 只能查看
					JSONObject startbtn = new JSONObject();
					startbtn.put("name", "查看");
					startbtn.put("url", "/f/cms/form/" + actYw.getProProject().getProjectMark() + "/applyForm?actywId="
							+ actYw.getId() + "&promodelId=" + lastpro.get("id"));
					startcontent.put("btn_option", startbtn);
				}
				contents.add(startcontent);
				for (Map<String, Object> node : nodes) {
					JSONObject content = new JSONObject();
					content.put("type", "milestone");
					content.put("title", getStrFromOb(node.get("name")));
					content.put("desc",
							getDateStrFromOb(node.get("beginDate")) + "~" + getDateStrFromOb(node.get("endDate")));
					content.put("start_date", getDateStrFromOb(node.get("beginDate")));
					content.put("end_date", getDateStrFromOb(node.get("endDate")));
					content.put("intro", "");
					contents.add(content);
				}
			} else {// 已提交,只能查看
					// 申报节点
				JSONObject startbtn = new JSONObject();
				startbtn.put("name", "查看");
				startbtn.put("url", "/f/cms/form/" + actYw.getProProject().getProjectMark() + "/applyForm?actywId="
						+ actYw.getId() + "&promodelId=" + lastpro.get("id"));
				startcontent.put("btn_option", startbtn);
				contents.add(startcontent);
				boolean tag = false;// 是否达到当前节点
				ActYwGnode actYwGnode = actTaskService.getNodeByProInsId(pocid);// 当前执行到的节点
				if (actYwGnode == null || actYwGnode.getSuspended()) {// 流程已走完
					for (Map<String, Object> node : nodes) {
						JSONObject content = new JSONObject();
						content.put("type", "milestone");
						content.put("title", getStrFromOb(node.get("name")));
						content.put("desc",
								getDateStrFromOb(node.get("beginDate")) + "~" + getDateStrFromOb(node.get("endDate")));
						content.put("start_date", getDateStrFromOb(node.get("beginDate")));
						content.put("end_date", getDateStrFromOb(node.get("endDate")));
						content.put("intro", "");
						if (actTaskService.getGrateBygondeId(getStrFromOb(node.get("id")))) {
							content.put("approvalDesc", lastpro.get("level_str"));
						}

						// 判断此大节点是否包含学生前台提交的操作，需提供查看链接
						ActYwGnode stugnode = actTaskService.getFrontByGondeId(getStrFromOb(node.get("id")));
						if (stugnode != null) {
							if (proModelService.getMidOrCloseView(lastpro.get("id"), stugnode.getId())) {// 已提交过表单
								JSONObject btn = new JSONObject();
								btn.put("name", "查看");
								btn.put("url",
										"/f/cms/form/" + actYw.getProProject().getProjectMark() + "/"
												+ stugnode.getForm().getId() + "?actywId=" + actYw.getId() + "&gnodeId="
												+ stugnode.getId() + "&promodelId=" + lastpro.get("id"));
								content.put("btn_option", btn);
								content.put("current", "true");
							} else {
								if (checkIsValid(now, getDateFromOb(node.get("beginDate")),
										getDateFromOb(node.get("endDate"))) && isLeader) {// 还在申报期内
									JSONObject btn = new JSONObject();
									btn.put("name", "提交");
									btn.put("url",
											"/f/cms/form/" + actYw.getProProject().getProjectMark() + "/"
													+ stugnode.getForm().getId() + "?actywId=" + actYw.getId()
													+ "&gnodeId=" + stugnode.getId());
									content.put("btn_option", btn);
									content.put("current", "true");
								}
							}
						} else {// 不含学生提交操作的
							content.put("current", "true");
						}
						contents.add(content);
					}
				} else {
					String curid;
					if ("1".equals(actYwGnode.getParentId())) {
						curid = actYwGnode.getId();
					} else {
						curid = actYwGnode.getParentId();
					}
					for (Map<String, Object> node : nodes) {
						JSONObject content = new JSONObject();
						content.put("type", "milestone");
						content.put("title", getStrFromOb(node.get("name")));
						content.put("desc",
								getDateStrFromOb(node.get("beginDate")) + "~" + getDateStrFromOb(node.get("endDate")));
						content.put("start_date", getDateStrFromOb(node.get("beginDate")));
						content.put("end_date", getDateStrFromOb(node.get("endDate")));
						content.put("intro", "");
						if (actTaskService.getGrateBygondeId(getStrFromOb(node.get("id")))) {
							content.put("approvalDesc", lastpro.get("level_str"));
						}
						if (!tag) {// 已经执行过的节点
							content.put("current", "true");
							// 判断此大节点是否包含学生前台提交的操作，需提供查看链接
							ActYwGnode stugnode = checkHasStuForm(getStrFromOb(node.get("id")), actYwGnode);
							if (stugnode != null) {
								JSONObject btn = new JSONObject();
								btn.put("name", "查看");
								btn.put("url",
										"/f/cms/form/" + actYw.getProProject().getProjectMark() + "/"
												+ stugnode.getForm().getId() + "?actywId=" + actYw.getId() + "&gnodeId="
												+ stugnode.getId() + "&promodelId=" + lastpro.get("id"));
								content.put("btn_option", btn);
							}
						}
						if (curid.equals(getStrFromOb(node.get("id")))) {
							tag = true;
							if ("1".equals(actYwGnode.getForm().getClientType()) && !actYwGnode.getSuspended()) {// 流程执行到的当前节点是前台学生提交
								if (checkIsValid(now, getDateFromOb(node.get("beginDate")),
										getDateFromOb(node.get("endDate"))) && isLeader) {// 还在申报期内
									JSONObject btn = new JSONObject();
									btn.put("name", "提交");
									btn.put("url",
											"/f/cms/form/" + actYw.getProProject().getProjectMark() + "/"
													+ actYwGnode.getForm().getId() + "?actywId=" + actYw.getId()
													+ "&gnodeId=" + actYwGnode.getId());
									content.put("btn_option", btn);
								}
							}
						}
						contents.add(content);
					}
				}
			}
		}
		data.put("contents", contents);
		data.put("files", files);
		return data;
	}

	// 判断是否包含学生提交的表单
	/**
	 * @param gondeId
	 *            大节点
	 * @param curnode
	 *            当前执行到的节点，为null时说明流程已走完
	 * @return
	 */
	private ActYwGnode checkHasStuForm(String gondeId, ActYwGnode curnode) {
		ActYwGnode stu = actTaskService.getFrontByGondeId(gondeId);
		if (curnode == null || stu == null) {
			return stu;
		} else {
			if (curnode.getParentId().equals(stu.getParentId())) {
				if (curnode.getPreIdss() != null && curnode.getPreIdss().contains(stu.getId())) {
					return stu;
				} else {
					return null;
				}
			} else {
				if (curnode.getPreIdss() != null && curnode.getPreIdss().contains(stu.getParent().getId())) {
					return stu;
				} else {
					return null;
				}
			}
		}
	}

	// 查找没有申报过项目的初始化时间轴数据
	private JSONObject getInitActTimeIndexData(ActYw actYw) {
		JSONObject data = new JSONObject();
		JSONArray contents = new JSONArray();
		JSONArray files = new JSONArray();
		List<Map<String, Object>> nodes = actYwGnodeService.treeDataForTimeIndexByYwId(null, actYw.getId(), "1", null,
				null, null);
		if (nodes != null && nodes.size() > 0) {
			Date now = new Date();
			JSONObject startcontent = new JSONObject();
			startcontent.put("type", "milestone");
			startcontent.put("title", "项目申报");
			startcontent.put("desc", getDateStrFromOb(actYw.getProProject().getNodeStartDate()) + "~"
					+ getDateStrFromOb(actYw.getProProject().getNodeEndDate()));
			startcontent.put("start_date", getDateStrFromOb(actYw.getProProject().getNodeStartDate()));
			startcontent.put("end_date", getDateStrFromOb(actYw.getProProject().getNodeEndDate()));
			startcontent.put("current", "true");
			startcontent.put("intro", "");
			if (actYw.getProProject() != null && actYw.getProProject().getNodeStartDate() != null
					&& actYw.getProProject().getNodeEndDate() != null
					&& now.after(actYw.getProProject().getNodeStartDate())
					&& now.before(actYw.getProProject().getNodeEndDate())) {// 还在申报期内可已修改
				JSONObject startbtn = new JSONObject();
				startbtn.put("name", "创建项目");
				startbtn.put("url", "javascript:onProjectApply('/cms/form/" + actYw.getProProject().getProjectMark()
						+ "/applyForm','" + actYw.getId() + "');");
				startcontent.put("btn_option", startbtn);
			}
			contents.add(startcontent);
			for (Map<String, Object> node : nodes) {
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", getStrFromOb(node.get("name")));
				content.put("desc",
						getDateStrFromOb(node.get("beginDate")) + "~" + getDateStrFromOb(node.get("endDate")));
				content.put("start_date", getDateStrFromOb(node.get("beginDate")));
				content.put("end_date", getDateStrFromOb(node.get("endDate")));
				content.put("intro", "");
				contents.add(content);
			}
		}
		data.put("contents", contents);
		data.put("files", files);
		return data;
	}

	private String getStrFromOb(Object o) {
		if (o == null) {
			return "";
		} else {
			return o.toString();
		}
	}

	private Date getDateFromOb(Object o) {
		if (o == null) {
			return null;
		} else {
			Date d = (Date) o;
			return d;
		}
	}

	private String getDateStrFromOb(Object o) {
		if (o == null) {
			return "";
		} else {
			Date d = (Date) o;
			return DateUtil.formatDate(d, "yyyy-MM-dd");
		}
	}

	// 根据project_declare表查找时间轴数据
	private JSONObject getDcTimeIndexData(String actywId, String projectId) throws ParseException {
		ActYw actYw = actYwDao.get(actywId);
		if (actYw == null) {
			return null;
		}
		Map<String, String> param = new HashMap<String, String>();
		param.put("projectId", projectId);
		param.put("uid", UserUtils.getUser().getId());
		param.put("actywId", actywId);
		param.put("startdate", DateUtil.formatDate(actYw.getProProject().getStartDate(), "yyyy-MM-dd HH:mm:ss"));
		param.put("enddate", DateUtil.formatDate(actYw.getProProject().getEndDate(), "yyyy-MM-dd HH:mm:ss"));
		Map<String, String> lastpro = null;
		if (StringUtil.isNotEmpty(projectId)) {
			lastpro = dao.getLastDcInfoByActyw(param);
		}
		if (lastpro == null || lastpro.size() == 0) {
			return getInitDcTimeIndexData(actYw);
		} else {
			return getProDcTimeIndexData(actYw, lastpro);
		}
	}

	// 查找没有申报过的大创项目的初始化时间轴数据
	private JSONObject getInitDcTimeIndexData(ActYw actYw) {
		JSONObject data = new JSONObject();
		JSONArray contents = new JSONArray();
		JSONArray files = new JSONArray();
		List<Map<String, Object>> nodes = actYwGnodeService.treeDataForTimeIndexByYwId(null, actYw.getId(), "1", null,
				null, null);
		if (nodes != null && nodes.size() > 0) {
			Date now = new Date();
			JSONObject startcontent = new JSONObject();
			startcontent.put("type", "milestone");
			startcontent.put("title", "项目申报");
			startcontent.put("desc", getDateStrFromOb(actYw.getProProject().getNodeStartDate()) + "~"
					+ getDateStrFromOb(actYw.getProProject().getNodeEndDate()));
			startcontent.put("start_date", getDateStrFromOb(actYw.getProProject().getNodeStartDate()));
			startcontent.put("end_date", getDateStrFromOb(actYw.getProProject().getNodeEndDate()));
			startcontent.put("current", "true");
			startcontent.put("intro", "");
			if (actYw.getProProject() != null && actYw.getProProject().getNodeStartDate() != null
					&& actYw.getProProject().getNodeEndDate() != null
					&& now.after(actYw.getProProject().getNodeStartDate())
					&& now.before(actYw.getProProject().getNodeEndDate())) {// 还在申报期内可已修改
				JSONObject startbtn = new JSONObject();
				startbtn.put("name", "创建项目");
				startbtn.put("url",
						"javascript:onProjectApply('/project/projectDeclare/form','" + ProjectNodeVo.YW_ID + "');");
				startcontent.put("btn_option", startbtn);
			}
			contents.add(startcontent);
			JSONObject content1 = new JSONObject();
			content1.put("type", "milestone");
			content1.put("title", "立项审核");
			content1.put("desc", getDateStrFromOb(nodes.get(0).get("beginDate")) + "~"
					+ getDateStrFromOb(nodes.get(0).get("endDate")));
			content1.put("start_date", getDateStrFromOb(nodes.get(0).get("beginDate")));
			content1.put("end_date", getDateStrFromOb(nodes.get(0).get("endDate")));
			content1.put("intro", "");
			contents.add(content1);

			JSONObject content2 = new JSONObject();
			content2.put("type", "milestone");
			content2.put("title", "中期检查");
			content2.put("desc", getDateStrFromOb(nodes.get(1).get("beginDate")) + "~"
					+ getDateStrFromOb(nodes.get(1).get("endDate")));
			content2.put("start_date", getDateStrFromOb(nodes.get(1).get("beginDate")));
			content2.put("end_date", getDateStrFromOb(nodes.get(1).get("endDate")));
			content2.put("intro", "");
			contents.add(content2);

			JSONObject content3 = new JSONObject();
			content3.put("type", "milestone");
			content3.put("title", "项目结项");
			content3.put("desc", getDateStrFromOb(nodes.get(2).get("beginDate")) + "~"
					+ getDateStrFromOb(nodes.get(2).get("endDate")));
			content3.put("start_date", getDateStrFromOb(nodes.get(2).get("beginDate")));
			content3.put("end_date", getDateStrFromOb(nodes.get(2).get("endDate")));
			content3.put("intro", "");
			contents.add(content3);
		}
		data.put("contents", contents);
		data.put("files", files);
		return data;
	}

	// 查找有申报过的大创项目的初始化时间轴数据
	private JSONObject getProDcTimeIndexData(ActYw actYw, Map<String, String> umap) throws ParseException {
		User user = UserUtils.getUser();
		JSONObject data = new JSONObject();
		JSONArray contents = new JSONArray();
		JSONArray files = new JSONArray();
		List<Map<String, Object>> nodes = actYwGnodeService.treeDataForTimeIndexByYwId(null, actYw.getId(), "1", null,
				null, null);
		if (nodes != null && nodes.size() > 0) {
			Date now = new Date();
			String createBy = umap.get("create_by");
			data.put("proname", umap.get("name"));
			data.put("number", umap.get("number"));
			data.put("pid", umap.get("id"));
			data.put("leader", umap.get("leader"));
			String pid = umap.get("id");
			List<ProjectPlan> plans = projectPlanDao.findListByProjectId(pid);
			fillPlans(plans, contents);// 填装任务信息
			int st = Integer.parseInt(umap.get("status"));
			/* 申报节点 **************************************************/
			if (st == 0) {// 未提交
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "项目申报");
				content.put("desc", getDateStrFromOb(actYw.getProProject().getNodeStartDate()) + "~"
						+ getDateStrFromOb(actYw.getProProject().getNodeEndDate()));
				content.put("start_date", getDateStrFromOb(actYw.getProProject().getNodeStartDate()));
				content.put("end_date", getDateStrFromOb(actYw.getProProject().getNodeEndDate()));
				content.put("current", "true");
				content.put("intro", "");
				if (createBy.equals(user.getId())) {
					JSONObject btn = new JSONObject();
					btn.put("name", "提交项目");
					btn.put("url", "/f/project/projectDeclare/form?id=" + pid);
					content.put("btn_option", btn);
				} else {
					JSONObject btn = new JSONObject();
					btn.put("name", "查看项目");
					btn.put("url", "/f/project/projectDeclare/viewForm?id=" + pid);
					content.put("btn_option", btn);
				}
				contents.add(content);
			} else if (st >= 1) {// 提交之后
				data.put("apply_time", umap.get("apply_time"));
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "项目申报");
				content.put("desc", getDateStrFromOb(actYw.getProProject().getNodeStartDate()) + "~"
						+ getDateStrFromOb(actYw.getProProject().getNodeEndDate()));
				content.put("start_date", getDateStrFromOb(actYw.getProProject().getNodeStartDate()));
				content.put("end_date", getDateStrFromOb(actYw.getProProject().getNodeEndDate()));
				content.put("intro", "");
				content.put("current", "true");
				JSONObject btn = new JSONObject();
				btn.put("name", "查看项目");
				btn.put("url", "/f/project/projectDeclare/viewForm?id=" + pid);
				content.put("btn_option", btn);
				contents.add(content);
			}
			/* 立项节点 **************************************************/
			JSONObject content1 = new JSONObject();
			content1.put("type", "milestone");
			content1.put("title", "项目立项");
			content1.put("desc", getDateStrFromOb(nodes.get(0).get("beginDate")) + "~"
					+ getDateStrFromOb(nodes.get(0).get("endDate")));
			content1.put("start_date", getDateStrFromOb(nodes.get(0).get("beginDate")));
			content1.put("end_date", getDateStrFromOb(nodes.get(0).get("endDate")));
			content1.put("intro", "");
			if (!StringUtil.isEmpty(umap.get("level"))) {
				content1.put("approvalTime", umap.get("approval_date"));
				content1.put("approvalDesc", umap.get("level_str"));
				content1.put("current", "true");
			}
			contents.add(content1);
			/* 中期检查节点 **********************************************/
			if (st < 3) {// 待提交中期报告之前
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "中期检查");
				content.put("desc", getDateStrFromOb(nodes.get(1).get("beginDate")) + "~"
						+ getDateStrFromOb(nodes.get(1).get("endDate")));
				content.put("start_date", getDateStrFromOb(nodes.get(1).get("beginDate")));
				content.put("end_date", getDateStrFromOb(nodes.get(1).get("endDate")));
				content.put("intro", "");
				contents.add(content);
			} else if (st == 3) {// 待提交中期报告
				boolean valid = checkIsValid(now, getDateFromOb(nodes.get(1).get("beginDate")),
						getDateFromOb(nodes.get(1).get("endDate")));
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "中期检查");
				content.put("desc", getDateStrFromOb(nodes.get(1).get("beginDate")) + "~"
						+ getDateStrFromOb(nodes.get(1).get("endDate")));
				content.put("start_date", getDateStrFromOb(nodes.get(1).get("beginDate")));
				content.put("end_date", getDateStrFromOb(nodes.get(1).get("endDate")));
				content.put("intro", "");
				if (valid) {
					content.put("current", "true");
				}
				if (createBy.equals(user.getId()) && valid) {
					JSONObject btn = new JSONObject();
					btn.put("name", "提交中期检查报告");
					btn.put("url", "javascript:canSumitClose('" + pid + "','/f/project/proMid/creatMid')");
					content.put("btn_option", btn);
				}
				contents.add(content);
			} else if (st == 4) {// 待修改中期报告
				ProMid pm = proMidDao.getByProjectId(pid);
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "中期检查");
				content.put("desc", getDateStrFromOb(nodes.get(1).get("beginDate")) + "~"
						+ getDateStrFromOb(nodes.get(1).get("endDate")));
				content.put("start_date", getDateStrFromOb(nodes.get(1).get("beginDate")));
				content.put("end_date", getDateStrFromOb(nodes.get(1).get("endDate")));
				content.put("intro", "");
				content.put("current", "true");
				if (pm != null) {
					if (createBy.equals(user.getId()) || "2".equals(user.getUserType())) {
						JSONObject btn = new JSONObject();
						btn.put("name", "修改中期检查报告");
						btn.put("url", "/f/project/proMid/edit?id=" + pm.getId());
						content.put("btn_option", btn);
					} else {
						JSONObject btn = new JSONObject();
						btn.put("name", "中期检查报告");
						btn.put("url", "/f/project/proMid/view?id=" + pm.getId());
						content.put("btn_option", btn);
					}
				}
				contents.add(content);
			} else if (st >= 5 && st != 8) {// 中期检查之后
				ProMid pm = proMidDao.getByProjectId(pid);
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "中期检查");
				content.put("desc", getDateStrFromOb(nodes.get(1).get("beginDate")) + "~"
						+ getDateStrFromOb(nodes.get(1).get("endDate")));
				content.put("start_date", getDateStrFromOb(nodes.get(1).get("beginDate")));
				content.put("end_date", getDateStrFromOb(nodes.get(1).get("endDate")));
				content.put("intro", "");
				content.put("current", "true");
				if (pm != null) {
					if ("2".equals(user.getUserType())) {
						JSONObject btn = new JSONObject();
						btn.put("name", "中期检查报告");
						btn.put("url", "/f/project/proMid/edit?id=" + pm.getId());
						content.put("btn_option", btn);
					} else {
						JSONObject btn = new JSONObject();
						btn.put("name", "中期检查报告");
						btn.put("url", "/f/project/proMid/view?id=" + pm.getId());
						content.put("btn_option", btn);
					}
				}
				contents.add(content);
			} else if (st == 8) {// 立项失败
				boolean valid = checkIsValid(now, getDateFromOb(nodes.get(1).get("beginDate")),
						getDateFromOb(nodes.get(1).get("endDate")));
				ProMid pm = proMidDao.getByProjectId(pid);
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "中期检查");
				content.put("desc", getDateStrFromOb(nodes.get(1).get("beginDate")) + "~"
						+ getDateStrFromOb(nodes.get(1).get("endDate")));
				content.put("start_date", getDateStrFromOb(nodes.get(1).get("beginDate")));
				content.put("end_date", getDateStrFromOb(nodes.get(1).get("endDate")));
				content.put("intro", "");
				if (valid || pm != null) {
					content.put("current", "true");
				}
				if (pm != null) {
					if ("2".equals(user.getUserType())) {
						JSONObject btn = new JSONObject();
						btn.put("name", "中期检查报告");
						btn.put("url", "/f/project/proMid/edit?id=" + pm.getId());
						content.put("btn_option", btn);
					} else {
						JSONObject btn = new JSONObject();
						btn.put("name", "中期检查报告");
						btn.put("url", "/f/project/proMid/view?id=" + pm.getId());
						content.put("btn_option", btn);
					}
				} else {
					if (createBy.equals(user.getId()) && valid) {
						JSONObject btn = new JSONObject();
						btn.put("name", "提交中期检查报告");
						btn.put("url", "javascript:canSumitClose('" + pid + "','/f/project/proMid/creatMid')");
						content.put("btn_option", btn);
					}
				}
				contents.add(content);
			}
			/* 结项节点 ****************************************************/
			if (st < 6) {// 待提交结项报告之前
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "项目结项");
				content.put("desc", getDateStrFromOb(nodes.get(2).get("beginDate")) + "~"
						+ getDateStrFromOb(nodes.get(2).get("endDate")));
				content.put("start_date", getDateStrFromOb(nodes.get(2).get("beginDate")));
				content.put("end_date", getDateStrFromOb(nodes.get(2).get("endDate")));
				content.put("intro", "");
				contents.add(content);
			} else if (st == 6) {// 待提交结项报告
				boolean valid = checkIsValid(now, getDateFromOb(nodes.get(2).get("beginDate")),
						getDateFromOb(nodes.get(2).get("endDate")));
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "项目结项");
				content.put("desc", getDateStrFromOb(nodes.get(2).get("beginDate")) + "~"
						+ getDateStrFromOb(nodes.get(2).get("endDate")));
				content.put("start_date", getDateStrFromOb(nodes.get(2).get("beginDate")));
				content.put("end_date", getDateStrFromOb(nodes.get(2).get("endDate")));
				content.put("intro", "");
				if (valid) {
					content.put("current", "true");
				}
				if (createBy.equals(user.getId()) && valid) {
					JSONObject btn = new JSONObject();
					btn.put("name", "提交结项报告");
					btn.put("url", "javascript:canSumitClose('" + pid + "','/f/project/projectClose/createClose')");
					content.put("btn_option", btn);
				}
				contents.add(content);
			} else if (st > 6 && st != 8) {// 提交结项报告之后
				ProjectClose pc = projectCloseDao.getByProjectId(pid);
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "项目结项");
				content.put("desc", getDateStrFromOb(nodes.get(2).get("beginDate")) + "~"
						+ getDateStrFromOb(nodes.get(2).get("endDate")));
				content.put("start_date", getDateStrFromOb(nodes.get(2).get("beginDate")));
				content.put("end_date", getDateStrFromOb(nodes.get(2).get("endDate")));
				content.put("intro", "");
				content.put("current", "true");
				if (pc != null) {
					if ("2".equals(user.getUserType())) {
						JSONObject btn = new JSONObject();
						btn.put("name", "结项报告");
						btn.put("url", "/f/project/projectClose/edit?id=" + pc.getId());
						content.put("btn_option", btn);
					} else {
						JSONObject btn = new JSONObject();
						btn.put("name", "结项报告");
						btn.put("url", "/f/project/projectClose/view?id=" + pc.getId());
						content.put("btn_option", btn);
					}
				}
				contents.add(content);
			} else if (st == 8) {// 立项失败
				boolean valid = checkIsValid(now, getDateFromOb(nodes.get(2).get("beginDate")),
						getDateFromOb(nodes.get(2).get("endDate")));
				ProjectClose pc = projectCloseDao.getByProjectId(pid);
				JSONObject content = new JSONObject();
				content.put("type", "milestone");
				content.put("title", "项目结项");
				content.put("desc", getDateStrFromOb(nodes.get(2).get("beginDate")) + "~"
						+ getDateStrFromOb(nodes.get(2).get("endDate")));
				content.put("start_date", getDateStrFromOb(nodes.get(2).get("beginDate")));
				content.put("end_date", getDateStrFromOb(nodes.get(2).get("endDate")));
				content.put("intro", "");
				if (valid || pc != null) {
					content.put("current", "true");
				}
				if (pc != null) {
					if ("2".equals(user.getUserType())) {
						JSONObject btn = new JSONObject();
						btn.put("name", "结项报告");
						btn.put("url", "/f/project/projectClose/edit?id=" + pc.getId());
						content.put("btn_option", btn);
					} else {
						JSONObject btn = new JSONObject();
						btn.put("name", "结项报告");
						btn.put("url", "/f/project/projectClose/view?id=" + pc.getId());
						content.put("btn_option", btn);
					}
				} else {
					if (valid) {
						content.put("current", "true");
					}
					if (createBy.equals(user.getId()) && valid) {
						JSONObject btn = new JSONObject();
						btn.put("name", "提交结项报告");
						btn.put("url", "javascript:canSumitClose('" + pid + "','/f/project/projectClose/createClose')");
						content.put("btn_option", btn);
					}
				}
				contents.add(content);
			}
			fillReports(pid, files, user, "1");// 填装周报、经费等信息
		}
		data.put("contents", contents);
		data.put("files", files);
		return data;
	}

	private void fillReports(String pid, JSONArray files, User user, String pptype) {
		Map<String, JSONArray> ret = new LinkedHashMap<String, JSONArray>();
		fillWeeklys(ret, pid, user, pptype);// 周报
		if (ret.size() > 0) {
			for (String k : ret.keySet()) {
				JSONObject content = new JSONObject();
				content.put("create_date", k);
				content.put("files", ret.get(k));
				files.add(content);
			}
		}
	}

	private void fillWeeklys(Map<String, JSONArray> ret, String pid, User user, String pptype) {
		List<Map<String, String>> wlist = projectWeeklyDao.getInfoByProjectId(pid);
		if (wlist != null) {
			for (Map<String, String> map : wlist) {
				JSONObject content = new JSONObject();
				content.put("name", "项目周报" + map.get("start_date") + "~" + map.get("end_date") + "("
						+ map.get("create_name") + ")");
				if ("2".equals(user.getUserType()) || map.get("create_by").equals(user.getId())) {
					if ("1".equals(pptype)) {// 大创
						content.put("url", "/f/project/weekly/createWeekly?id=" + map.get("id"));
					} else {
						content.put("url", "/f/project/weekly/createWeeklyPlus?id=" + map.get("id"));
					}
				} else {
					if ("1".equals(pptype)) {// 大创
						content.put("url", "/f/project/weekly/view?id=" + map.get("id"));
					} else {
						content.put("url", "/f/project/weekly/viewPlus?id=" + map.get("id"));
					}

				}
				content.put("create_date", map.get("create_date"));
				JSONArray files = ret.get(map.get("create_date"));
				if (files == null) {
					files = new JSONArray();
				}
				files.add(content);
				ret.put(map.get("create_date"), files);
			}
		}
	}

	private void fillPlans(List<ProjectPlan> plans, JSONArray contents) {
		if (plans != null) {
			for (ProjectPlan p : plans) {
				JSONObject content = new JSONObject();
				content.put("type", "date");
				content.put("desc", DateUtil.formatDate(p.getStartDate()) + "~" + DateUtil.formatDate(p.getEndDate()));
				content.put("start_date", DateUtil.formatDate(p.getStartDate()));
				content.put("end_date", DateUtil.formatDate(p.getEndDate()));
				content.put("intro", p.getContent());
				contents.add(content);
			}
		}
	}

	public ProjectDeclare get(String id) {
		return super.get(id);
	}

	public Page<ProjectDeclareListVo> getMyProjectListPlus(Page<ProjectDeclareListVo> page, ProjectDeclareListVo vo) {
		vo.setPage(page);
		page.setList(dao.getMyProjectListPlus(vo));
		// 处理组成员、导师名称
		if (!page.getList().isEmpty()) {
			List<String> ids = new ArrayList<String>();
			for (ProjectDeclareListVo v : page.getList()) {
				ids.add(v.getId());
			}
			List<Map<String, String>> ps = dao.getMyProjectListPersonPlus(ids);
			Map<String, String> psm = new HashMap<String, String>();
			if (ps != null && ps.size() > 0) {
				for (Map<String, String> map : ps) {
					psm.put(map.get("id") + map.get("team_user_type"), map.get("pname"));
				}
			}
			for (ProjectDeclareListVo v : page.getList()) {
				if (psm.size() > 0) {
					v.setSnames(psm.get(v.getId() + "1"));
					v.setTnames(psm.get(v.getId() + "2"));
				}
				if ("-999".equals(v.getStatus_code())) {
					if (StringUtil.isEmpty(v.getProc_ins_id())) {
						v.setStatusCode("0");
					} else {
						ActYwGnode actYwGnode = actTaskService.getNodeByProInsId(v.getProc_ins_id());
						String actywId = v.getActywId();
						ActYw actYw = actYwDao.get(actywId);
						// 民大版本
						if (actYw.getKeyType() != null) {
							v.setStatus(getFres(v));
						} else {
							if (actYwGnode != null && !actYwGnode.getSuspended()) {
								if (v.getState() != null && v.getState().equals("1")) {
									v.setStatus(actYwGnode.getName() + "不通过");
								} else {
									v.setStatus("待" + actYwGnode.getName());
								}
							} else {
								// 表明流程走到最后一步
								String groupId = actYw.getGroupId();
								ActYwGnode newactYwGnode = actYwGnodeService.getEndPreFun(groupId);
								if (v.getGrade() != null && v.getGrade().equals("0")) {
									v.setStatus(newactYwGnode.getName() + "不通过");
								} else {
									v.setStatus(newactYwGnode.getName() + "通过");
								}
							}
						}
					}
				}
			}
		}
		return page;
	}

	private String getFres(ProjectDeclareListVo v) {
		String pmdid = v.getId();
		ProModelMd pmd = proModelMdDao.getByProModelId(pmdid);
		if (pmd != null) {
			if (!StringUtil.isEmpty(pmd.getCloseState())) {
				if ("1".equals(pmd.getCloseState())) {
					return "结项通过";
				} else if ("0".equals(pmd.getCloseState())) {
					v.setGrade("0");
					v.setState("1");
					return "结项不通过";
				}
			} else if (!StringUtil.isEmpty(pmd.getMidState())) {
				if ("1".equals(pmd.getMidState())) {
					return "中期检查通过";
				} else if ("0".equals(pmd.getMidState())) {
					v.setGrade("0");
					v.setState("1");
					return "中期检查不通过";
				}
			} else if (!StringUtil.isEmpty(pmd.getSetState())) {
				if ("1".equals(pmd.getSetState())) {
					return "立项通过";
				} else if ("0".equals(pmd.getSetState())) {
					v.setGrade("0");
					v.setState("1");
					return "立项不通过";
				}
			}
		}
		return "";
	}

	public Page<Map<String, String>> getMyProjectList(Page<Map<String, String>> page, Map<String, Object> param) {
		if (page.getPageNo() <= 0) {
			page.setPageNo(1);
		}
		int count = dao.getMyProjectListCount(param);
		param.put("offset", (page.getPageNo() - 1) * page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String, String>> list = null;
		if (count > 0) {
			list = dao.getMyProjectList(param);
			if (list != null) {
				List<String> ids = new ArrayList<String>();
				for (Map<String, String> map : list) {
					ids.add(map.get("id"));
					map.put("status", ProjectStatusEnum.getNameByValue(map.get("status")));
					map.put("final_result", ProjectFinalResultEnum.getNameByValue(map.get("final_result")));
				}
				List<Map<String, String>> ps = dao.getMyProjectListPerson(ids);
				if (ps != null && ps.size() > 0) {
					Map<String, String> psm = new HashMap<String, String>();
					for (Map<String, String> map : ps) {
						psm.put(map.get("id") + map.get("team_user_type"), map.get("pname"));
					}
					for (Map<String, String> map : list) {
						map.put("snames", psm.get(map.get("id") + "1"));
						map.put("tnames", psm.get(map.get("id") + "2"));
					}
				}
			}
		}
		page.setCount(count);
		page.setList(list);
		page.initialize();
		return page;
	}

	/**
	 * 项目查询 updateBy zhangzheng
	 *
	 * @param projectDeclare
	 * @return
	 */
	public List<ProjectDeclare> findList(ProjectDeclare projectDeclare) {

		return super.findList(projectDeclare);
	}

	public Page<ProjectDeclare> findPage(Page<ProjectDeclare> page, ProjectDeclare projectDeclare) {
		return super.findPage(page, projectDeclare);
	}

	/**
	 * 根据团队或项目获取团队学生.
	 * 
	 * @param teamid
	 *            团队ID
	 * @param proId
	 *            项目ID
	 * @return List
	 */
	public List<TeamStudentVo> findTeamStudentByTeamId(String teamid, String proId) {
			//logger.info("=============>proId"+proId+" teamid："+teamid);
		if (StringUtil.isNotEmpty(proId)) {
			List<TeamStudentVo> converts = TeamStudentVo.converts(dao.findTeamStudentFromTUH(teamid, proId));
			//logger.info("findTeamStudentFromTUH=====>"+JSONArray.fromObject(converts).toString());
			return converts;
		} else {
			List<TeamStudentVo> converts =TeamStudentVo.converts(dao.findTeamStudent(teamid));
			//logger.info("findTeamStudent=====>"+JSONArray.fromObject(converts).toString());
			return converts;
		}
	}

	/**
	 * 根据团队ID获取团队学生,不建议使用，请使用 findTeamStudentByTeamId.
	 * 
	 * @param teamid
	 * @return List
	 */
	@Deprecated
	public List<Map<String, String>> findTeamStudent(String teamid) {
		if (teamid == null) {
			return null;
		} else {
			return dao.findTeamStudent(teamid);
		}
	}

	/**
	 * 根据团队和项目获取团队学生,不建议使用，请使用 findTeamStudentByTeamId.
	 * 
	 * @param teamid
	 *            团队ID
	 * @param proId
	 *            项目ID
	 * @return List
	 */
	@Deprecated
	public List<Map<String, String>> findTeamStudentFromTUH(String teamid, String proId) {
		if (teamid == null) {
			return null;
		} else {
			return dao.findTeamStudentFromTUH(teamid, proId);
		}
	}

	/**
	 * 根据团队或项目获取团队导师.
	 * 
	 * @param teamid
	 *            团队ID
	 * @param proId
	 *            项目ID
	 * @return List
	 */
	public List<TeamTeacherVo> findTeamTeacherByTeamId(String teamid, String proId) {
		if (StringUtil.isNotEmpty(proId)) {
			return TeamTeacherVo.converts(dao.findTeamTeacherFromTUH(teamid, proId));
		} else {
			return TeamTeacherVo.converts(dao.findTeamTeacher(teamid));
		}
	}

	/**
	 * 根据团队和项目获取团队导师,不建议使用，请使用 findTeamTeacherByTeamId.
	 * 
	 * @param teamid
	 *            团队ID
	 * @return List
	 */
	@Deprecated
	public List<Map<String, String>> findTeamTeacher(String teamid) {
		if (teamid == null) {
			return null;
		} else {
			return dao.findTeamTeacher(teamid);
		}
	}

	/**
	 * 根据团队和项目获取团队导师,不建议使用，请使用 findTeamTeacherByTeamId.
	 * 
	 * @param teamid
	 *            团队ID
	 * @param proid
	 *            项目ID
	 * @return List
	 */
	@Deprecated
	public List<Map<String, String>> findTeamTeacherFromTUH(String teamid, String proid) {
		if (teamid == null) {
			return null;
		} else {
			return dao.findTeamTeacherFromTUH(teamid, proid);
		}
	}

	public Map<String, String> getProjectAuditInfo(String projectId) {
		Map<String, String> psm = new HashMap<String, String>();
		List<Map<String, String>> list1 = dao.getProjectAuditResult(projectId);
		List<Map<String, String>> list2 = dao.getProjectAuditInfo(projectId);
		if (list1 != null) {
			Map<String, String> map = list1.get(0);
			if (map != null) {
				psm.put("level", map.get("level"));
				psm.put("mid_result", ProjectMidResultEnum.getNameByValue(map.get("mid_result")));
				psm.put("final_result", ProjectFinalResultEnum.getNameByValue(map.get("final_result")));
			}
		}
		if (list2 != null) {
			for (Map<String, String> map : list2) {
				if ("1".equals(map.get("audit_step"))) {
					psm.put("level_s", map.get("suggest"));
					psm.put("level_d", map.get("create_date"));
				}
				if ("3".equals(map.get("audit_step"))) {
					psm.put("mid_s", map.get("suggest"));
					psm.put("mid_d", map.get("create_date"));
				}
				if ("4".equals(map.get("audit_step"))) {
					psm.put("final_s", map.get("suggest"));
					psm.put("final_d", map.get("create_date"));
				}
			}
		}
		return psm;
	}

	/**
	 * 根据团队负责人查找所相关的所有团队. 当 teamid = null 时，查所有相关团队 当 teamid != null 时，查当前项目对应团队
	 * 
	 * @param userid
	 *            负责人ID
	 * @param teamid
	 *            团队ID
	 * @return List
	 */
	public List<Team> findTeams(String userid, String teamid) {
		return dao.findTeams(userid, teamid);
	}

	@Transactional(readOnly = false)
	public void save(ProjectDeclare projectDeclare) {
		super.save(projectDeclare);
	}

	// 工作流查询的基本信息
	public HashMap<String, Object> getVars(String projectId) {
		ProjectDeclare project = dao.getVars(projectId);
		HashMap<String, Object> vars = new HashMap<String, Object>();
		vars.put("id", project.getId()); // 项目id
		vars.put("number", project.getNumber()); // 项目编号
		vars.put("name", project.getName()); // 项目名称
		vars.put("type", project.getType()); // 项目类型
		vars.put("leader", project.getLeaderString()); // 项目负责人
		vars.put("teamList", project.getSnames()); // 项目组成员
		vars.put("teacher", project.getTnames()); // 指导老师
		return vars;
	}

	/**
	 * 国创项目提交，触发工作流
	 *
	 * @param projectDeclare
	 *            必须包含id,createById
	 * @Author zhangzheng
	 */
	@Transactional(readOnly = false)
	public void startPojectProcess(ProjectDeclare projectDeclare) {
		User user = userDao.get(projectDeclare.getCreateBy().getId());
		identityService.setAuthenticatedUserId(user.getLoginName());
		String procDefKey = "state_project_audit";
		String businessTable = "project_declare";
		String businessId = projectDeclare.getId();
		HashMap<String, Object> vars = getVars(businessId);
		List<String> claims = userService.getCollegeSecs(projectDeclare.getCreateBy().getId());
		vars.put("collegeSec", claims); // 给学院教学秘书审批
		ProcessInstance procIns = runtimeService.startProcessInstanceByKey(procDefKey, businessTable + ":" + businessId,
				vars);
		Act act = new Act();
		act.setBusinessTable(businessTable);// 业务表名
		act.setBusinessId(businessId); // 业务表ID
		act.setProcInsId(procIns.getId());
		actDao.updateProcInsIdByBusinessId(act);

		// 下个节点 自动签收
		actTaskService.claimByProcInsId(procIns.getId(), claims);
	}

	// addBy zhangzheng 院级立项评审 保存评审意见到子表 保存评级结果 执行一步工作流
	@Transactional(readOnly = false)
	public void collegeSetSave(ProjectDeclare projectDeclare) {
		ProjectAuditInfo pai = new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId()); // 主表id
		pai.setAuditStep("1"); // 保存评级步骤 1代表立项审核 见字典值audit_step
		// 如果level为c级、未通过则需要更新数据库的level。如果为其他则需要提交给校级评审，不需要更新状态
		if ("4".equals(projectDeclare.getLevel()) || "5".equals(projectDeclare.getLevel())) {
			pai.setGrade(projectDeclare.getLevel());
		}
		if ("2".equals(projectDeclare.getLevel())) {
			pai.setGrade("6");
		}

		// 保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);

		// 完成工作流
		HashMap<String, Object> vars = new HashMap<String, Object>();
		List<String> claims = new ArrayList<String>();
		if ("2".equals(projectDeclare.getLevel())) {// 如果是2则是提交给学校评审
			// 根据projectDeclare.id 查找vars 提交给学校立项审核 查询用
			vars = getVars(projectDeclare.getId());
			claims = userService.getSchoolSecs();
			vars.put("schoolSec", claims);
		}

		vars.put("grade", projectDeclare.getLevel());
		String taskId = actTaskService.getTaskidByProcInsId(projectDeclare.getProcInsId());
		taskService.complete(taskId, vars);

		// 下个节点 自动签收
		actTaskService.claimByProcInsId(projectDeclare.getProcInsId(), claims);

		// 改变主表的审核状态
		if ("5".equals(projectDeclare.getLevel())) { // 不合格
			projectDeclare.preUpdate();
			// (0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
			projectDeclare.setStatus("8");
			// 项目结果(0合格，1优秀，2不合格，3立项不合格，4中期不合格）
			projectDeclare.setFinalResult("3"); // 立项不合格
			projectDeclare.setApprovalDate(new Date());
			updateStatus(projectDeclare);
			// 给项目负责人发送消息 你的项目审核未通过（不合格项目）
			User apply_User = UserUtils.getUser();
			User rec_User = new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User, rec_User, "项目审核通知",
					"你的" + DictUtils.getDictLabel("1", "project_style", "") + projectDeclare.getName() + "审核未通过（不合格项目）",
					OaNotify.Type_Enum.TYPE14.getValue(), projectDeclare.getTeamId());
		}
		if ("4".equals(projectDeclare.getLevel())) { // c级
			projectDeclare.preUpdate();
			projectDeclare.setStatus("3"); // 设置状态为 待提交中期报告
			projectDeclare.setApprovalDate(new Date());
			updateStatus(projectDeclare);
			String number = "C" + projectDeclare.getNumber();
			projectDeclare.setNumber(number);
			dao.updateNumber(projectDeclare);
			// 给项目负责人发送消息 学院已对你的项目评级（C级项目）；
			User apply_User = UserUtils.getUser();
			User rec_User = new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User, rec_User, "项目审核通知",
					"学院已对你的" + DictUtils.getDictLabel("1", "project_style", "") + projectDeclare.getName() + "评级（C级项目）",
					OaNotify.Type_Enum.TYPE14.getValue(), projectDeclare.getTeamId());
		}
		if ("2".equals(projectDeclare.getLevel())) { // 校级
			projectDeclare.preUpdate();
			projectDeclare.setLevel(""); // 评级为空，表示需要校级评
			// (0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
			projectDeclare.setStatus("2"); // 待学校立项审核
			updateStatus(projectDeclare);
			// 给项目负责人发送消息 学院已提交项目到校级（校级评级项目）；
			User apply_User = UserUtils.getUser();
			User rec_User = new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User,
					rec_User, "项目审核通知", "学院已提交你的" + DictUtils.getDictLabel("1", "project_style", "")
							+ projectDeclare.getName() + "到校级（校级评级项目）",
					OaNotify.Type_Enum.TYPE14.getValue(), projectDeclare.getTeamId());
		}

		// 保存审核标准
		saveStandardList(projectDeclare.getAuditStandardDetailInsList(), pai.getId());

	}

	// addBy zhangzheng 校级立项评审 保存评审意见到子表 执行一步工作流 保存评级结果
	@Transactional(readOnly = false)
	public void schoolSetSave(ProjectDeclare projectDeclare) {
		ProjectAuditInfo pai = new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId()); // 主表id
		pai.setAuditStep("1"); // 保存评级步骤 1代表立项审核 见字典值audit_step
		pai.setGrade(projectDeclare.getLevel()); // 保存评级
		// 保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);

		// 完成工作流
		HashMap<String, Object> vars = new HashMap<String, Object>();

		String taskId = actTaskService.getTaskidByProcInsId(projectDeclare.getProcInsId());
		taskService.complete(taskId, vars);

		// 如果审核不通过 改变主表的审核状态
		projectDeclare.preUpdate();
		// (0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
		projectDeclare.setStatus("3");
		projectDeclare.setApprovalDate(new Date());
		updateStatus(projectDeclare);

		String number = "";
		if ("1".equals(projectDeclare.getLevel())) { // A+
			number = "A+" + projectDeclare.getNumber();
			projectDeclare.setNumber(number);
			// 给项目负责人发送消息 学校已对你的项目评级（A+级、A级或B级项目）
			User apply_User = UserUtils.getUser();
			User rec_User = new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User,
					rec_User, "项目审核通知", "学校已对你的" + DictUtils.getDictLabel("1", "project_style", "")
							+ projectDeclare.getName() + "评级（A+级项目）",
					OaNotify.Type_Enum.TYPE14.getValue(), projectDeclare.getTeamId());
		}
		if ("2".equals(projectDeclare.getLevel())) { // A
			number = "A" + projectDeclare.getNumber();
			projectDeclare.setNumber(number);
			// 给项目负责人发送消息 学校已对你的项目评级（A+级、A级或B级项目）
			User apply_User = UserUtils.getUser();
			User rec_User = new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User, rec_User, "项目审核通知",
					"学校已对你的" + DictUtils.getDictLabel("1", "project_style", "") + projectDeclare.getName() + "评级（A级项目）",
					OaNotify.Type_Enum.TYPE14.getValue(), projectDeclare.getTeamId());
		}
		if ("3".equals(projectDeclare.getLevel())) { // B
			number = "B" + projectDeclare.getNumber();
			projectDeclare.setNumber(number);
			// 给项目负责人发送消息 学校已对你的项目评级（A+级、A级或B级项目）
			User apply_User = UserUtils.getUser();
			User rec_User = new User();
			rec_User.setId(projectDeclare.getLeader());
			oaNotifyService.sendOaNotifyByType(apply_User, rec_User, "项目审核通知",
					"学校已对你的" + DictUtils.getDictLabel("1", "project_style", "") + projectDeclare.getName() + "评级（B级项目）",
					OaNotify.Type_Enum.TYPE14.getValue(), projectDeclare.getTeamId());
		}
		dao.updateNumber(projectDeclare);

		// 保存审核标准
		saveStandardList(projectDeclare.getAuditStandardDetailInsList(), pai.getId());

	}

	/**
	 * 提交中期报告，触发工作流
	 *
	 * @param projectDeclare
	 *            必须包含id,level,procInsId
	 * @Author zhangzheng
	 */
	@Transactional(readOnly = false)
	public void threeStepSave(ProjectDeclare projectDeclare) {
		// 启动工作流
		startPojectProcess(projectDeclare);
		// 后台导师审核
		projectDeclare = get(projectDeclare.getId());
		if (projectDeclare.getLevel().equals("1") || projectDeclare.getLevel().equals("2")
				|| projectDeclare.getLevel().equals("3")) {
			String level = projectDeclare.getLevel();
			projectDeclare.setLevel("2");
			collegeSetSave(projectDeclare);
			projectDeclare.setLevel(level);
			schoolSetSave(projectDeclare);
		} else if (projectDeclare.getLevel().equals("4") || projectDeclare.getLevel().equals("5")) {
			collegeSetSave(projectDeclare);
		}
		// 保存中期报告
		/*
		 * projectDeclare=get(projectDeclare.getId());
		 *
		 * midSave(projectDeclare);
		 */
	}

	/**
	 * 提交中期报告，触发工作流
	 *
	 * @param projectDeclare
	 *            必须包含id,level,procInsId
	 * @Author zhangzheng
	 */
	@Transactional(readOnly = false)
	public void midSave(ProjectDeclare projectDeclare) {

		// 封装 工作流的vars
		HashMap<String, Object> vars = getVars(projectDeclare.getId()); // 工作流下一步需要展示的字段
		vars.put("level", projectDeclare.getLevel());
		// 查询工作流下一步需要审批的人
		List<String> claims = new ArrayList<String>();
		if ("1".equals(projectDeclare.getLevel()) || "2".equals(projectDeclare.getLevel())) { // A级、A+级院级
																								// 校级审批
			claims = userService.getSchoolExperts(); // 找到校级专家；
			vars.put("schoolExperts", claims);
		}
		if ("3".equals(projectDeclare.getLevel()) || "4".equals(projectDeclare.getLevel())) { // B级、B+级院级
																								// 院级审批
			claims = userService.getCollegeExperts(projectDeclare.getCreateBy().getId()); // 根据创建人id，找到院级专家；
			vars.put("collegeExperts", claims);
		}
		vars.put("grade", projectDeclare.getLevel()); // 网关流转条件
		String taskId = actTaskService.getTaskidByProcInsId(projectDeclare.getProcInsId());
		taskService.complete(taskId, vars); // 执行工作流

		projectDeclare.preUpdate();
		// (0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
		projectDeclare.setStatus("5");
		updateStatus(projectDeclare);

	}

	// addBy zhangzheng 中期评分 执行一步工作流 保存评分、意见到子表
	@Transactional(readOnly = false)
	public void expCheckSave(ProjectDeclare projectDeclare) {

		// 完成工作流
		// 判断当前节点是否是最后一个、如果是把平均分计算出来，保存到主表中和工作流中
		String taskDefinitionKey = "";
		// 判断项目等级 A+、A给学校秘书评级 taskDefinitionKey为middleScore2 (参见流程图的设置）
		if ("1".equals(projectDeclare.getLevel()) || "2".equals(projectDeclare.getLevel())) {
			taskDefinitionKey = "middleScore2";
		}
		if ("3".equals(projectDeclare.getLevel()) || "4".equals(projectDeclare.getLevel())) {
			taskDefinitionKey = "middleScore3";
		}
		boolean isLast = actTaskService.isMultiLast("state_project_audit", taskDefinitionKey,
				projectDeclare.getProcInsId());
		HashMap<String, Object> vars = new HashMap<String, Object>();
		if (isLast) {
			// 取出原来的评分+现在的评分 ，取平均分
			ProjectAuditInfo infoSerch = new ProjectAuditInfo();
			infoSerch.setProjectId(projectDeclare.getId());
			infoSerch.setAuditStep("2");
			List<ProjectAuditInfo> infos = projectAuditInfoService.getInfo(infoSerch);
			float total = 0;
			float average = 0;
			int number = 0;
			if (infos == null || infos.size() == 0) {
				average = projectDeclare.getMidScore();
			} else {
				total = total + projectDeclare.getMidScore();
				number++;
				for (ProjectAuditInfo info : infos) {
					total = total + info.getScore();
					number++;
				}
				average = FloatUtils.division(total, number);
			}
			vars = getVars(projectDeclare.getId());
			vars.put("level", projectDeclare.getLevel());
			vars.put("midScore", average);
			vars.put("scoreInt", FloatUtils.getInt(average));
			vars.put("scorePoint", FloatUtils.getPoint(average));
			// 更新主表中期评分分数
			ProjectDeclare pd = new ProjectDeclare();
			pd.setMidScore(average);
			pd.setId(projectDeclare.getId());
			dao.updateMidScore(pd);

			List<String> claims = new ArrayList<String>();
			if ("1".equals(projectDeclare.getLevel()) || "2".equals(projectDeclare.getLevel())) { // 判断项目等级
																									// A+、A给学校秘书评级
				claims = userService.getSchoolSecs();
				vars.put("schoolSec", claims);
			}
			if ("3".equals(projectDeclare.getLevel()) || "4".equals(projectDeclare.getLevel())) { // 判断项目等级
																									// B、C给学院秘书评级
				claims = userService.getCollegeSecs(projectDeclare.getCreateBy().getId());
				vars.put("collegeSec", claims);
			}
			taskService.complete(projectDeclare.getAct().getTaskId(), vars);

			// 下个节点 自动签收
			actTaskService.claimByProcInsId(projectDeclare.getProcInsId(), claims);

		} else {
			taskService.complete(projectDeclare.getAct().getTaskId(), vars);
		}

		ProjectAuditInfo pai = new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId()); // 主表id
		pai.setAuditStep("2"); // 保存评级步骤 2代表中期评分 见字典值audit_step
		// 评分
		pai.setScore(projectDeclare.getMidScore());
		// 保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);

		// 保存审核标准
		saveStandardList(projectDeclare.getAuditStandardDetailInsList(), pai.getId());

	}

	// 中期审核
	// 如果整改给 打回前删除中期评分的信息、执行工作流、更改主表midCount+1
	// 如果不整改 保存中期审核评级、执行工作流、更改主表状态
	@Transactional(readOnly = false)
	public void secCheckSave(ProjectDeclare projectDeclare) {
		User send_User = UserUtils.getUser();
		User rec_User = UserUtils.get(projectDeclare.getLeader());
		if ("4".equals(projectDeclare.getPass())) { // pass==4表示需要整改
			// 删除中期评分的信息
			// 处理评分 评分保存到子表中 project_audit_info
			ProjectAuditInfo pai = new ProjectAuditInfo();
			pai.setProjectId(projectDeclare.getId());
			pai.setAuditStep("2"); // 2代表中期检查评分
			projectAuditInfoService.deleteByPidAndStep(pai);
			// 执行工作流
			HashMap<String, Object> vars = new HashMap<String, Object>();
			vars.put("pass", projectDeclare.getPass());
			taskService.complete(projectDeclare.getAct().getTaskId(), vars);

			projectDeclare.setMidCount(1); // 表示整改过一次
			dao.updateMidCount(projectDeclare);
			projectDeclare.preUpdate();
			// (0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
			projectDeclare.setStatus("4");
			updateStatus(projectDeclare);
			oaNotifyService.sendOaNotifyByType(send_User, rec_User, "项目中期检查通知",
					"你的" + DictUtils.getDictLabel("1", "project_style", "") + projectDeclare.getName() + "需要整改",
					OaNotify.Type_Enum.TYPE14.getValue(), projectDeclare.getTeamId());
		} else {// 如果不整改 保存中期审核评级、执行工作流、改变主表状态

			ProjectAuditInfo pai = new ProjectAuditInfo();
			pai.setProjectId(projectDeclare.getId()); // 主表id
			pai.setAuditStep("3"); // 审核阶段：1立项评审 2中期检查评分 3中期检查评级 4结项审核 5结项评级
			pai.setSuggest(projectDeclare.getComment());
			pai.setGrade(projectDeclare.getMidResult()); // 保存评级
			projectAuditInfoService.save(pai);

			// 完成工作流
			HashMap<String, Object> vars = new HashMap<String, Object>();
			vars.put("pass", projectDeclare.getMidResult());
			vars.put("level", projectDeclare.getLevel());
			taskService.complete(projectDeclare.getAct().getTaskId(), vars);

			projectDeclare.preUpdate();
			if ("0".equals(projectDeclare.getMidResult())) { // 合格
				// (0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
				projectDeclare.setStatus("6");
			}
			if ("2".equals(projectDeclare.getMidResult())) { // 不合格
				projectDeclare.setFinalResult("4"); // 中期不合格
				projectDeclare.setStatus("8");
			}
			updateStatus(projectDeclare);
			// 中期不合格，需要给予学分
			if ("2".equals(projectDeclare.getMidResult())) {
				saveScore(projectDeclare.getId());
			}
			// 保存审核标准
			saveStandardList(projectDeclare.getAuditStandardDetailInsList(), pai.getId());
			oaNotifyService.sendOaNotifyByType(send_User, rec_User, "项目中期检查通知",
					"你的" + DictUtils.getDictLabel("1", "project_style", "") + projectDeclare.getName() + "中期检查结果为"
							+ DictUtils.getDictLabel(projectDeclare.getMidResult(), "project_result", ""),
					OaNotify.Type_Enum.TYPE14.getValue(), projectDeclare.getTeamId());
		}
	}

	/**
	 * 提交结项报告，触发工作流
	 *
	 * @param projectDeclare
	 *            必须包含id,level,procInsId
	 * @Author zhangzheng
	 */
	@Transactional(readOnly = false)
	public void closeSave(ProjectDeclare projectDeclare) {
		// 封装 工作流的vars
		HashMap<String, Object> vars = getVars(projectDeclare.getId()); // 工作流下一步需要展示的字段
		vars.put("level", projectDeclare.getLevel());
		// 查询工作流下一步需要审批的人
		List<String> claims = new ArrayList<String>();
		if ("1".equals(projectDeclare.getLevel()) || "2".equals(projectDeclare.getLevel())) { // A级、A+级院级
																								// 校级审批
			claims = userService.getSchoolExperts(); // 找到校级专家；
			vars.put("schoolExperts", claims);

		}
		if ("3".equals(projectDeclare.getLevel()) || "4".equals(projectDeclare.getLevel())) { // B级、B+级院级
																								// 院级审批
			claims = userService.getCollegeExperts(projectDeclare.getCreateBy().getId()); // 根据创建人id，找到院级专家；
			vars.put("collegeExperts", claims);
		}
		vars.put("grade", projectDeclare.getLevel()); // 网关流转条件
		String taskId = actTaskService.getTaskidByProcInsId(projectDeclare.getProcInsId());
		taskService.complete(taskId, vars); // 执行工作流

		projectDeclare.preUpdate();
		// (0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止)
		projectDeclare.setStatus("7");
		updateStatus(projectDeclare);

	}

	// addBy zhangzheng 结项评分 执行一步工作流 保存评分、意见到子表
	@Transactional(readOnly = false)
	public void expCloseSave(ProjectDeclare projectDeclare) {
		// 完成工作流
		// 判断当前节点是否是最后一个、如果是把平均分计算出来，保存到主表中和工作流中
		String taskDefinitionKey = "";
		// 判断项目等级 A+、A给学校秘书评级 taskDefinitionKey为closeScore2 (参见流程图的设置）
		if ("1".equals(projectDeclare.getLevel()) || "2".equals(projectDeclare.getLevel())) {
			taskDefinitionKey = "closeScore2";
		}
		if ("3".equals(projectDeclare.getLevel()) || "4".equals(projectDeclare.getLevel())) {
			taskDefinitionKey = "closeScore3";
		}
		boolean isLast = actTaskService.isMultiLast("state_project_audit", taskDefinitionKey,
				projectDeclare.getProcInsId());
		HashMap<String, Object> vars = new HashMap<String, Object>();
		if (isLast) {
			// 取出原来的评分+现在的评分 ，取平均分
			ProjectAuditInfo infoSerch = new ProjectAuditInfo();
			infoSerch.setProjectId(projectDeclare.getId());
			// 审核阶段：1立项评审 2中期检查评分 3中期检查评级 4结项审核 5结项评级
			infoSerch.setAuditStep("4");
			List<ProjectAuditInfo> infos = projectAuditInfoService.getInfo(infoSerch);
			float total = 0;
			float average = 0;
			int number = 0;
			if (infos == null || infos.size() == 0) {
				average = projectDeclare.getFinalScore();
			} else {
				total = total + projectDeclare.getFinalScore();
				number++;
				for (ProjectAuditInfo info : infos) {
					total = total + info.getScore();
					number++;
				}
				average = FloatUtils.division(total, number);
			}
			vars = getVars(projectDeclare.getId());
			vars.put("level", projectDeclare.getLevel());
			vars.put("finalScore", average);
			// 更新主表结项评分分数
			ProjectDeclare pd = new ProjectDeclare();
			pd.setFinalScore(average);
			pd.setId(projectDeclare.getId());
			dao.updateFinalScore(pd);
			List<String> claims = new ArrayList<String>();
			if ("1".equals(projectDeclare.getLevel()) || "2".equals(projectDeclare.getLevel())) { // 判断项目等级
																									// A+、A给学校秘书评级
				claims = userService.getSchoolSecs();
				vars.put("schoolSec", claims);
			}
			if ("3".equals(projectDeclare.getLevel()) || "4".equals(projectDeclare.getLevel())) { // 判断项目等级
																									// B、C给学院秘书评级
				claims = userService.getCollegeSecs(projectDeclare.getCreateBy().getId());
				vars.put("collegeSec", claims);
			}

			taskService.complete(projectDeclare.getAct().getTaskId(), vars);
			// 下个节点 自动签收
			actTaskService.claimByProcInsId(projectDeclare.getProcInsId(), claims);

		} else {
			taskService.complete(projectDeclare.getAct().getTaskId(), vars);
		}

		ProjectAuditInfo pai = new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId()); // 主表id
		// 审核阶段：1立项评审 2中期检查评分 3中期检查评级 4结项审核 5结项评级
		pai.setAuditStep("4");
		// 评分
		pai.setScore(projectDeclare.getFinalScore());
		// 保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);

		// 保存审核标准
		saveStandardList(projectDeclare.getAuditStandardDetailInsList(), pai.getId());

	}

	// 结项答辩分保存 执行工作流 保存子表评级意见信息 主表记录答辩分 自动签收
	@Transactional(readOnly = false)
	public void secCloseSave(ProjectDeclare projectDeclare) {
		HashMap<String, Object> vars = getVars(projectDeclare.getId());
		List<String> claims = new ArrayList<String>();
		if ("1".equals(projectDeclare.getLevel()) || "2".equals(projectDeclare.getLevel())) { // 判断项目等级
																								// A+、A给学校秘书评级
			claims = userService.getSchoolSecs();
			vars.put("sec", claims);
		}
		if ("3".equals(projectDeclare.getLevel()) || "4".equals(projectDeclare.getLevel())) { // 判断项目等级
																								// B、C给学院秘书评级
			claims = userService.getCollegeSecs(projectDeclare.getCreateBy().getId());
			vars.put("sec", claims);
		}

		vars.put("replyScore", projectDeclare.getReplyScore());
		// 执行工作流
		taskService.complete(projectDeclare.getAct().getTaskId(), vars);

		// 保存子表答辩分及意见信息
		ProjectAuditInfo pai = new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId()); // 主表id
		// 审核阶段：1立项评审 2中期检查评分 3中期检查评级 4结项审核 5结项评级 6结果评定
		pai.setAuditStep("5");
		pai.setScore(projectDeclare.getReplyScore());
		// 保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);

		// 主表记录答辩分
		projectDeclare.preUpdate();
		updateStatus(projectDeclare);

		// 保存审核标准
		saveStandardList(projectDeclare.getAuditStandardDetailInsList(), pai.getId());

		// 下个节点 自动签收
		actTaskService.claimByProcInsId(projectDeclare.getProcInsId(), claims);

	}

	// 结果评定 执行工作流 保存子表 修改主表状态
	@Transactional(readOnly = false)
	public void secAssessSave(ProjectDeclare projectDeclare) {
		User send_User = UserUtils.getUser();
		User rec_User = UserUtils.get(projectDeclare.getLeader());
		// 执行工作流
		taskService.complete(projectDeclare.getAct().getTaskId());

		// 保存子表答辩分及意见信息
		ProjectAuditInfo pai = new ProjectAuditInfo();
		pai.setProjectId(projectDeclare.getId()); // 主表id
		// 审核阶段：1立项评审 2中期检查评分 3中期检查评级 4结项审核 5结项评级 6结果评定
		pai.setAuditStep("6");
		// 评级 合格、不合格、优秀
		pai.setGrade(projectDeclare.getFinalResult());
		// 保存审核意见
		pai.setSuggest(projectDeclare.getComment());
		projectAuditInfoService.save(pai);

		// 修改主表状态
		projectDeclare.preUpdate();
		// (0未提交，1待学院立项审核，2待学校立项审核，3.待提交中期报告，4待修改中期报告,5待中期检查,6待提交结项报告,7.待结项审核，8项目终止,9.项目结项)
		projectDeclare.setStatus("9");
		updateStatus(projectDeclare);

		// 保存学分
		saveScore(projectDeclare.getId());

		// 保存审核标准
		saveStandardList(projectDeclare.getAuditStandardDetailInsList(), pai.getId());
		oaNotifyService.sendOaNotifyByType(send_User, rec_User, "项目结项审核通知",
				"你的" + DictUtils.getDictLabel("1", "project_style", "") + projectDeclare.getName() + "结项审核结果为"
						+ DictUtils.getDictLabel(projectDeclare.getFinalResult(), "project_result", ""),
				OaNotify.Type_Enum.TYPE14.getValue(), projectDeclare.getTeamId());
	}

	@Transactional(readOnly = false)
	public void saveProjectDeclareVo(ProjectDeclareVo vo) {
		vo.getProjectDeclare().setStatus(ProjectStatusEnum.S0.getValue());
		if (StringUtil.isEmpty(vo.getProjectDeclare().getNumber())) {
			vo.getProjectDeclare().setNumber(IdUtils.getProjectNumberByDb());
		}
		try {
			super.save(vo.getProjectDeclare());
		} catch (DuplicateKeyException e) {
			if (e.getCause().getMessage().indexOf("'project_num'") != -1) {// 违反项目编号唯一约束
				saveProjectDeclareVo(vo);
			}
		}
		projectPlanDao.deleteByProjectId(vo.getProjectDeclare().getId());
		int sort = 0;
		if (vo.getPlans() != null) {
			for (ProjectPlan plan : vo.getPlans()) {
				plan.setSort(sort + "");
				plan.setId(IdGen.uuid());
				plan.setProjectId(vo.getProjectDeclare().getId());
				plan.setCreateDate(new Date());
				projectPlanDao.insert(plan);
				sort++;
			}
		}
		sysAttachmentService.saveByVo(vo.getAttachMentEntity(), vo.getProjectDeclare().getId(), FileTypeEnum.S0,
				FileStepEnum.S100);
		/* 保存时处理TeamUserHistory信息 */
		commonService.disposeTeamUserHistoryOnSave(vo.getTeamUserRelationList(), vo.getProjectDeclare().getActywId(),
				vo.getProjectDeclare().getTeamId(), vo.getProjectDeclare().getId());

	}

	@Transactional(readOnly = false)
	public void submitProjectDeclareVo(ProjectDeclareVo vo) {
		vo.getProjectDeclare().setStatus(ProjectStatusEnum.S1.getValue());
		vo.getProjectDeclare().setApplyTime(new Date());
		if (StringUtil.isEmpty(vo.getProjectDeclare().getNumber())) {
			vo.getProjectDeclare().setNumber(IdUtils.getProjectNumberByDb());
		}
		try {
			super.save(vo.getProjectDeclare());
		} catch (DuplicateKeyException e) {
			if (e.getCause().getMessage().indexOf("'project_num'") != -1) {// 违反项目编号唯一约束
				saveProjectDeclareVo(vo);
			}
		}
		projectPlanDao.deleteByProjectId(vo.getProjectDeclare().getId());
		int sort = 0;
		for (ProjectPlan plan : vo.getPlans()) {
			plan.setSort(sort + "");
			plan.setId(IdGen.uuid());
			plan.setProjectId(vo.getProjectDeclare().getId());
			plan.setCreateDate(new Date());
			projectPlanDao.insert(plan);
			sort++;
		}
		sysAttachmentService.saveByVo(vo.getAttachMentEntity(), vo.getProjectDeclare().getId(), FileTypeEnum.S0,
				FileStepEnum.S100);
		/* 提交时处理TeamUserHistory信息 */
		commonService.disposeTeamUserHistoryOnSubmit(vo.getTeamUserRelationList(), vo.getProjectDeclare().getActywId(),
				vo.getProjectDeclare().getTeamId(), vo.getProjectDeclare().getId());

		excellentShowService.saveExcellentShow(vo.getProjectDeclare().getIntroduction(),
				vo.getProjectDeclare().getTeamId(), ExcellentShow.Type_Project, vo.getProjectDeclare().getId(),
				vo.getProjectDeclare().getActywId());// 保存优秀展示
		startPojectProcess(vo.getProjectDeclare()); // 启动工作流 addBy zhangzheng
	}

	@Transactional(readOnly = false)
	public void delete(ProjectDeclare projectDeclare, String ftb) {
		if ("0".equals(ftb)) {
			dao.myDelete(projectDeclare.getId());
		} else {
			proModelDao.myDelete(projectDeclare.getId());
		}
		teamUserHistoryDao.updateDelByProid(projectDeclare.getId());
	}

	public int findByTeamId(String teamId) {
		return dao.findByTeamId(teamId);
	}

	public ProjectDeclare getProjectByTimeId(String tid) {
		return dao.getProjectByTeamId(tid);
	}

	// public List<ProjectExpVo> getExps(String userId) {
	// return dao.getExps(userId);
	// }

	public List<ProjectExpVo> getExpsByUserId(String userId) {
		return dao.getExpsByUserId(userId);
	}

	// public Page<ProjectExpVo> findPage(Page<ProjectExpVo> page, ProjectExpVo
	// entity) {
	// entity.setPage(page);
	// page.setList(dao.getExpsByUserId(entity));
	// return page;
	// }

	public ProjectDeclare getScoreConfigure(String id) {
		return dao.getScoreConfigure(id);
	}

	@Transactional(readOnly = false)
	public void updateFinalResult(ProjectDeclare projectDeclare) {
		dao.updateFinalResult(projectDeclare);
		updateTeamUserHistoryByStatus(projectDeclare);
	}

	// 保存学分
	@Transactional(readOnly = false)
	public void saveScore(String projectId) {
		// 插入学分前，先根据项目id删除对应的学分信息（sco_affirm表和sco_score表）
		scoAffirmDao.deleteByProId(projectId);
		scoScoreDao.deleteProject(projectId);

		// 保存学分到对应的表（先保存到sco_affirm表、再保存到soc_score表）
		ProjectDeclare pro = getScoreConfigure(projectId);
		// 根据
		// type（学分类型)、item（学分项）、category（课程、项目、大赛、技能大类）、subdivision（课程、项目、大赛小类）、number(人数)查询后台配比
		ScoAffirm scoAffirm = new ScoAffirm();
		scoAffirm.setProId(pro.getId());
		scoAffirm.setProType(pro.getpType());

		ScoRatioVo scoRatioVo = new ScoRatioVo();
		ScoAffrimCriterionVo scoAffrimCriterionVo = new ScoAffrimCriterionVo();
		if (StringUtil.equals(pro.getType(), "1") || StringUtil.equals(pro.getType(), "2")) { // 创新训练、创业训练
			scoRatioVo.setType("0000000123"); // 设置查询的学分类型（创新学分）
			scoAffrimCriterionVo.setType("0000000123");
			scoAffirm.setType("1");
		}
		if (StringUtil.equals(pro.getType(), "3")) { // 创业实践
			scoRatioVo.setType("0000000124"); // 设置查询的学分类型（创业学分）
			scoAffrimCriterionVo.setType("0000000124");
			scoAffirm.setType("2");
		}
		scoRatioVo.setItem("0000000128"); // 双创项目
		scoAffrimCriterionVo.setItem("0000000128");
		scoRatioVo.setCategory("1"); // 大学生创新创业训练项目
		scoAffrimCriterionVo.setCategory("1");
		scoRatioVo.setSubdivision(pro.getType());
		scoAffrimCriterionVo.setSubdivision(pro.getType());
		scoRatioVo.setNumber(pro.getSnumber());
		ScoRatioVo ratioResult = scoAllotRatioDao.findRatio(scoRatioVo);
		scoAffrimCriterionVo.setCategory2(pro.getLevel());
		scoAffrimCriterionVo.setResult(pro.getFinalResult());
		boolean hasConfig; // 判断后台是否配置了规则
		if (ratioResult != null) {
			hasConfig = true;
		} else {
			hasConfig = false;
		}
		// 查找 学分认定标准 scoAffirmCriterion
		ScoAffrimCriterionVo criterionResult = scoAffirmCriterionDao.findCriter(scoAffrimCriterionVo);
		if (criterionResult != null) { // 有学分认定标准，则插入值
			// 插入scoAffirm表
			scoAffirm.setScoreStandard(criterionResult.getScore());
			scoAffirm.setScoreVal(criterionResult.getScore());
			scoAffirm.setAffirmDate(new Date());
			scoAffirm.preInsert();
			scoAffirmDao.insert(scoAffirm);
			// 查找组成员的信息
			// TeamUserRelation teamUserRelation = new TeamUserRelation();
			// teamUserRelation.setTeamId(pro.getTeamId());
			// List<TeamUserRelation> studentList=
			// teamUserRelationDao.getStudents(teamUserRelation);
			List<Map<String, String>> studentList = findTeamStudentFromTUH(pro.getTeamId(), pro.getId());

			if (!hasConfig) { // 如果后台没有配比规则、则所有成员一样的分数
				for (Map<String, String> teamUser : studentList) {
					ScoScore scoScore = new ScoScore();
					User user = new User();
					user.setId(teamUser.get("userId"));
					scoScore.setUser(user);
					if (StringUtil.equals(pro.getType(), "1") || StringUtil.equals(pro.getType(), "2")) { // 保存创新学分
						float score = criterionResult.getScore();
						if (score > 0 & score < 0.5) {
							score = 0.5f;
						}
						scoScore.setBusinessScore((double) score);
						scoScore.setBusinessProId(pro.getId());
					}
					if (StringUtil.equals(pro.getType(), "3")) { // 创业学分
						float score = criterionResult.getScore();
						if (score > 0 & score < 0.5) {
							score = 0.5f;
						}
						scoScore.setInnovateScore((double) score);
						scoScore.setInnovateProId(pro.getId());
					}
					scoScore.preInsert();
					scoScoreDao.insert(scoScore);
				}
			} else { // 有配比，则查询team_user_relation中的配比
				// 查找学分分配权重之和
				int weigthTotal = teamUserHistoryService.getWeightTotalByTeamId(pro.getTeamId(), pro.getId());

				for (Map<String, String> teamUser : studentList) {
					ScoScore scoScore = new ScoScore();
					User user = new User();
					user.setId(teamUser.get("userId"));
					scoScore.setUser(user);
					String number = String.valueOf(teamUser.get("weightVal"));
					int weightVal = Integer.parseInt(number);
					float score = FloatUtils
							.getOnePoint(((float) weightVal / (float) weigthTotal) * criterionResult.getScore());
					if (StringUtil.equals(pro.getType(), "1") || StringUtil.equals(pro.getType(), "2")) { // 保存创新学分
						if (score > 0 & score < 0.5) {
							score = 0.5f;
						}
						scoScore.setBusinessScore((double) score);
						scoScore.setBusinessProId(pro.getId());
					}
					if (StringUtil.equals(pro.getType(), "3")) { // 创业学分
						if (score > 0 & score < 0.5) {
							score = 0.5f;
						}
						scoScore.setInnovateScore((double) score);
						scoScore.setInnovateProId(pro.getId());
					}
					scoScore.preInsert();
					scoScoreDao.insert(scoScore);

				}
			}

		}

	}

	@Transactional(readOnly = false)
	public void saveStandardList(List<AuditStandardDetailIns> asdiList, String infoId) {
		for (AuditStandardDetailIns ins : asdiList) {
			ins.setAuditInfoId(infoId);
			ins.preInsert();
			auditStandardDetailInsDao.insert(ins);
		}
	}

}