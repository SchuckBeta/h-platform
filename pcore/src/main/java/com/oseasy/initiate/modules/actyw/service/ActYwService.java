package com.oseasy.initiate.modules.actyw.service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.oseasy.initiate.modules.actyw.tool.project.impl.*;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.act.service.ActModelService;
import com.oseasy.initiate.modules.act.vo.ActRstatus;
import com.oseasy.initiate.modules.actyw.dao.ActYwDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwGnodeDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwGroupDao;
import com.oseasy.initiate.modules.actyw.dao.ActYwGtimeDao;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.entity.ActYwGtime;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwGnodeTool;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwResult;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwTool;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.GnodeTypeRt;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtBounds;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtBoundsX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtChildShapes;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtChildShapesMap;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtChildShapesParamMap;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtLowerRight;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtLowerRightX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtModel;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtPropertiesX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtSvl;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtUpperLeft;
import com.oseasy.initiate.modules.actyw.tool.process.vo.RtUpperLeftX;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;
import com.oseasy.initiate.modules.actyw.tool.project.ActProParamVo;
import com.oseasy.initiate.modules.actyw.tool.project.ActProRunner;
import com.oseasy.initiate.modules.actyw.tool.project.ActProStatus;
import com.oseasy.initiate.modules.actyw.tool.project.IActProDeal;
import com.oseasy.initiate.modules.cms.entity.Category;
import com.oseasy.initiate.modules.cms.service.CategoryService;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.proproject.service.ProProjectService;
import com.oseasy.initiate.modules.sys.entity.Menu;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.tpl.vo.Rstatus;

import net.sf.json.JSONObject;

/**
 * 项目流程关联Service.
 *
 * @author chenhao
 * @version 2017-05-23
 */
@Service
@Transactional(readOnly = true)
public class ActYwService extends CrudService<ActYwDao, ActYw> {

	protected static final Logger LOGGER = Logger.getLogger(ActYwService.class);
	@Autowired
	ActYwGroupDao actYwGroupDao;

	@Autowired
	ActYwGtimeDao actYwGtimeDao;

	@Autowired
	ActYwGnodeDao actYwGnodeDao;

	@Autowired
	private ActYwGtimeService actYwGtimeService;
	@Autowired
	private ProProjectService proProjectService;

	@Autowired
	private SystemService systemService;
	@Autowired
	private CategoryService categoryService;

	public ActYw get(String id) {
		return super.get(id);
	}

	public List<ActYw> getByKeyss(String keyss) {
		return dao.getByKeyss(keyss);
	}

	@Transactional(readOnly = false)
	public void updateIsShowAxisPL(List<ActYw> actYws, Boolean isShowAxis) {
		dao.updateIsShowAxisPL(actYws, isShowAxis);
	}

	/**
	 * 验证项目流程标识是否存在.
	 *
	 * @param keyss Key
	 * @param isNew 是否新增
	 * @return Boolean
	 */
	public Boolean validKeyss(String keyss, Boolean isNew) {
		List<ActYw> actYws = getByKeyss(keyss);
		if ((actYws == null) || (actYws.size() <= 0)) {
			return true;
		}

		int size = actYws.size();
		if (!isNew && (size == 1)) {
			return true;
		}
		return false;
	}

	public List<ActYw> findList(ActYw actYw) {
		return super.findList(actYw);
	}

	/**
	 * 根据条件查询已部署的流程.
	 *
	 * @param actYw 项目流程
	 * @return List
	 */
	public List<ActYw> findListByDeploy(ActYw actYw) {
		actYw.setIsDeploy(true);
		return dao.findListByDeploy(actYw);
	}

	/**
	 * 根据流程类型条件查询已部署的流程.
	 *
	 * @return List
	 */
	public List<ActYw> findListByDeploy(FlowType flowType) {
		if (flowType == null) {
			return null;
		}
		ActYw pactYw = new ActYw();
		ActYwGroup pactYwGroup = new ActYwGroup();
		pactYwGroup.setStatus(ActYwGroup.GROUP_DEPLOY_1);
		pactYwGroup.setDelFlag(Global.NO);
		pactYwGroup.setFlowType(flowType.getKey());
		pactYw.setGroup(pactYwGroup);
		pactYw.setDelFlag(Global.NO);
		return findListByDeploy(pactYw);
	}

	/**
	 * 根据流程类型条件查询已部署的流程. 如果类型为空，返回所有已部署流程.
	 *
	 * @param ftype 排除的ID
	 * @return
	 */
	public List<ActYw> findListByDeploy(String ftype) {
		List<ActYw> actYws = Lists.newArrayList();
		if (StringUtil.isNotEmpty(ftype)) {
			FlowType flowType = FlowType.getByKey(ftype);
			if (flowType != null) {
				actYws = findListByDeploy(flowType);
			} else {
				logger.warn("类型参数未定义!");
			}
		} else {
			actYws = findListByDeploy(new ActYw());
		}
		return actYws;
	}

	public Page<ActYw> findPage(Page<ActYw> page, ActYw actYw) {
		return super.findPage(page, actYw);
	}

	@Transactional(readOnly = false)
	public void save(ActYw actYw) {
		if ((actYw.getIsNewRecord())) {
			if (actYw.getIsDeploy() == null) {
				actYw.setIsDeploy(false);
			}

			if (actYw.getIsShowAxis() == null) {
				actYw.setIsShowAxis(false);
			}
		}

		super.save(actYw);
	}

	/**
	 * 流程部署方法的重写,需要继续维护.
	 *
	 * @param actYw
	 * @return
	 */
	@Transactional(readOnly = false)
	public ActProStatus saveDeployTime2(ActYw actYw) {
		FlowType flowType = null;
		if (actYw.getGroup() != null) {
			if (StringUtil.isNotEmpty(actYw.getGroupId())) {
				actYw.setGroup(actYwGroupDao.get(actYw.getGroupId()));
			}
		}
		if (actYw.getGroup() == null) {
			return null;
		}
		if ((actYw.getGroup() != null) && (actYw.getGroup().getFlowType() != null)) {
			flowType = FlowType.getByKey(actYw.getGroup().getFlowType());
		}
		if (flowType == null) {
			return null;
		}

		ActProRunner<IActProDeal> proRunner = new ActProRunner<IActProDeal>();
		ActProParamVo actProParamVo = new ActProParamVo();
		if ((flowType).equals(FlowType.FWT_QINGJIA)) {
			ActProQingJia actProQingJia = new ActProQingJia();
			proRunner.setActProDeal(actProQingJia);
			proRunner.setFlowType(flowType);
			actProParamVo.setActYw(actYw);
			actProParamVo.setProProject(actYw.getProProject());
		} else if ((flowType).equals(FlowType.FWT_SCORE)) {
			ActProScore actProScore = new ActProScore();
			proRunner.setActProDeal(actProScore);
			proRunner.setFlowType(flowType);
			actProParamVo.setActYw(actYw);
			actProParamVo.setProProject(actYw.getProProject());
		} else if ((flowType).equals(FlowType.FWT_XM)) {
			ActProModel actProModel = new ActProModel();
			proRunner.setActProDeal(actProModel);
			proRunner.setFlowType(flowType);
			actProParamVo.setActYw(actYw);
			actProParamVo.setProProject(actYw.getProProject());
		} else if ((flowType).equals(FlowType.FWT_DASAI)) {
			// ActProProject actProProject=new ActProProject();
			ActProModelGcontest actProModelGcontest = new ActProModelGcontest();
			proRunner.setActProDeal(actProModelGcontest);
			proRunner.setFlowType(flowType);
			actProParamVo.setActYw(actYw);
			actProParamVo.setProProject(actYw.getProProject());
		} else if ((flowType).equals(FlowType.FWT_ENTER)) {
			// ActProProject actProProject=new ActProProject();
			ActProEnter actProEnter = new ActProEnter();
			proRunner.setActProDeal(actProEnter);
			proRunner.setFlowType(flowType);
			actProParamVo.setActYw(actYw);
			actProParamVo.setProProject(actYw.getProProject());
		} else if ((flowType).equals(FlowType.FWT_APPOINTMENT)) {
			ActProAppointment actProAppointment = new ActProAppointment();
			proRunner.setActProDeal(actProAppointment);
			proRunner.setFlowType(flowType);
			actProParamVo.setActYw(actYw);
			actProParamVo.setProProject(actYw.getProProject());
		} else {
			logger.warn("流程类型未定义!!!");
		}
		return proRunner.execute(actProParamVo);
	}

	/**
	 * 流程发布.
	 *
	 * @param actYw
	 * @param repositoryService
	 * @param request
	 * @return
	 */
	@Transactional(readOnly = false)
	public Boolean saveDeployTime(ActYw actYw, RepositoryService repositoryService,
								  HttpServletRequest request) {
		return saveDeployTime(actYw, repositoryService, null, null, request);
	}

	/**
	 * 流程发布和部署.
	 *
	 * @param actYw
	 * @param repositoryService 流程服务
	 * @param actModelService   流程模型服务
	 * @param isUpdateYw        标识是否更新到业务表
	 * @param request
	 * @return
	 */
	@Transactional(readOnly = false)
	public Boolean saveDeployTime(ActYw actYw, RepositoryService repositoryService,
								  ActModelService actModelService, Boolean isUpdateYw, HttpServletRequest request) {
		// 新建
		if (actYw.getIsNewRecord()) {
			if ((actYw.getProProject() == null)
					|| StringUtil.isEmpty(actYw.getProProject().getImgUrl())) {
				return false;
			}
			if (actYw.getIsDeploy() == null) {
				actYw.setIsDeploy(false);
			}
			actYw.setId(IdGen.uuid());
			actYw.setIsNewRecord(true);
			// 新建发布
			proProjectService.save(actYw.getProProject());
			actYw.setRelId(actYw.getProProject().getId());
			save(actYw);
		} else {
			// 修改
			ActYw actOld = get(actYw.getId());
			// 修改发布
			if (actYw.getIsDeploy()) {
				if (actYw.getGroupId() != null) {
					// 修改发布状态永远都要发布流程
					// Menu menu =systemService.getMenuById(actYw.getProProject().getMenuRid());
					// Category category= categoryService.get(actYw.getProProject().getCategoryRid());
					deleteModel(actOld);
					saveDeployTime2(actYw);
					// proProjectService.changeProProjectModel(actYw, request);
					proProjectService.save(actYw.getProProject());
					// deploy(actYw, repositoryService);//只执行发布流程模型，未执行部署
					deploy(actYw, repositoryService, actModelService, isUpdateYw);// 执行流程模型发布并部署
				}
			} else {
				// 项目无法修改
				proProjectService.save(actYw.getProProject());
				save(actYw);
			}
		}
		if (StringUtil.isNotEmpty(actYw.getShowTime()) && ((actYw.getShowTime()).equals(Global.SHOW))) {
			addGtime2(actYw, request);
		}
		return true;
	}

	@Transactional(readOnly = false)
	public Rstatus addGtime(ActYw actYw, HttpServletRequest request) {
		if (!(actYw.getShowTime()).equals("1")) {
			return new Rstatus(true, "不显示时间！");
		}

		if (StringUtil.isEmpty(actYw.getGroupId())) {
			return new Rstatus(false, "流程ID不能为空！");
		}

		String[] gNodeId = request.getParameterValues("nodeId");
		String[] beginDate = request.getParameterValues("beginDate");
		String[] endDate = request.getParameterValues("endDate");
		if ((beginDate == null) || (beginDate.length <= 0)) {
			return new Rstatus(false, "开始时间不能为空！");
		}

		if ((endDate == null) || (endDate.length <= 0)) {
			return new Rstatus(false, "结束时间不能为空！");
		}

		if ((gNodeId == null) || (gNodeId.length <= 0)) {
			return new Rstatus(false, "节点ID不能为空！");
		}

		ActYwGtime actYwGtimeOld = new ActYwGtime();
		// actYwGtimeOld.setGrounpId(actYw.getGroupId());
		actYwGtimeOld.setProjectId(actYw.getRelId());
		actYwGtimeService.deleteByGroupId(actYwGtimeOld);

		Rstatus rstatus = new Rstatus();
		for (int i = 0; i < beginDate.length; i++) {
			String status = request.getParameter("status" + i);
			String rate = request.getParameter("rate" + i);
			String rateStatus = request.getParameter("rateStatus" + i);

			ActYwGtime actYwGtime = new ActYwGtime();
			actYwGtime.setGrounpId(actYw.getGroupId());
			actYwGtime.setProjectId(actYw.getRelId());

			if (StringUtil.isEmpty(status)) {
				actYwGtime.setStatus(Global.HIDE);
			} else {
				actYwGtime.setStatus(status);
			}

			if (StringUtil.isEmpty(rate)) {
				actYwGtime.setRate(0.0f);
			} else {
				actYwGtime.setRate(Float.parseFloat(rate));
			}

			if (StringUtil.isEmpty(rateStatus)) {
				actYwGtime.setRateStatus(Global.HIDE);
			} else {
				actYwGtime.setRateStatus(rateStatus);
			}

			if (StringUtil.isNotEmpty(beginDate[i])) {
				actYwGtime.setBeginDate(DateUtil.parseDate(beginDate[i]));
			}

			if (StringUtil.isNotEmpty(endDate[i])) {
				actYwGtime.setEndDate(DateUtil.parseDate(endDate[i]));
			}

			if (StringUtil.isNotEmpty(gNodeId[i])) {
				actYwGtime.setGnodeId(gNodeId[i]);
			}
			actYwGtimeService.save(actYwGtime);
		}
		return rstatus;
	}

	@Transactional(readOnly = false)
	public Rstatus addGtime2(ActYw actYw, HttpServletRequest request) {
		if ((actYw.getShowTime()).equals(Global.HIDE)) {
			return new Rstatus(true, "不显示时间！");
		}

		if (StringUtil.isEmpty(actYw.getGroupId())) {
			return new Rstatus(false, "流程ID不能为空！");
		}

		String[] gNodeId = request.getParameterValues("nodeId");
		if ((gNodeId == null) || (gNodeId.length <= 0)) {
			return new Rstatus(false, "节点ID不能为空！");
		}

		ActYwGtime actYwGtimeOld = new ActYwGtime();
		// actYwGtimeOld.setGrounpId(actYw.getGroupId());
		actYwGtimeOld.setProjectId(actYw.getRelId());
		actYwGtimeService.deleteByGroupId(actYwGtimeOld);

		Rstatus rstatus = new Rstatus();
		for (int i = 0; i < gNodeId.length; i++) {
			String beginDate = request.getParameter("beginDate" + i);
			String endDate = request.getParameter("endDate" + i);
			String status = request.getParameter("status" + i);
			String rate = request.getParameter("rate" + i);
			String rateStatus = request.getParameter("rateStatus" + i);

			ActYwGtime actYwGtime = new ActYwGtime();
			actYwGtime.setGrounpId(actYw.getGroupId());
			actYwGtime.setProjectId(actYw.getRelId());

			if (StringUtil.isEmpty(status)) {
				actYwGtime.setStatus(Global.HIDE);
			} else {
				actYwGtime.setStatus(status);
			}

			if (StringUtil.isEmpty(rate)) {
				actYwGtime.setRate(0.0f);
			} else {
				actYwGtime.setRate(Float.parseFloat(rate));
			}

			if (StringUtil.isEmpty(rateStatus)) {
				actYwGtime.setRateStatus(Global.HIDE);
			} else {
				actYwGtime.setRateStatus(rateStatus);
			}

			if (StringUtil.isNotEmpty(beginDate)) {
				actYwGtime.setBeginDate(DateUtil.parseDate(beginDate));
			}

			if (StringUtil.isNotEmpty(endDate)) {
				actYwGtime.setEndDate(DateUtil.parseDate(endDate));
			}

			if (StringUtil.isNotEmpty(gNodeId[i])) {
				actYwGtime.setGnodeId(gNodeId[i]);
			}
			actYwGtimeService.save(actYwGtime);
		}
		return rstatus;
	}

	@Transactional(readOnly = false)
	public void delete(ActYw actYw) {
		super.delete(actYw);
	}

	@Transactional(readOnly = false)
	public void deleteAll(ActYw actYw) {
		ProProject proProject = actYw.getProProject();
		Menu menu = systemService.getMenu(proProject.getMenuRid());
		// 删除菜单
		if (menu != null) {
			systemService.deleteMenu(menu);
		}

		// 删除栏目
		Category category = categoryService.get(proProject.getCategoryRid());
		if (category != null) {
			categoryService.delete(category);
		}

		// 删除项目
		proProjectService.delete(proProject);
		super.delete(actYw);
	}

	@Transactional(readOnly = false)
	public void deleteModel(ActYw actYw) {
		ProProject proProject = actYw.getProProject();

		// 删除菜单
		if (proProject.getMenuRid() != null) {
			Menu menu = systemService.getMenu(proProject.getMenuRid());
			if (menu != null) {
				systemService.deleteMenu(menu);
			}
		}

		// 删除栏目
		if (proProject.getCategoryRid() != null) {
			Category category = categoryService.get(proProject.getCategoryRid());
			if (category != null) {
				categoryService.delete(category);
			}
		}
	}

	/**
	 * 发布项目流程. 以流程标识和项目标识生成流程模板标识和版本（防止多项目共用一个流程是出现菜单、栏目重合）
	 *
	 * @param actYw 项目流程
	 * @return Boolean
	 * @author chenhao
	 */
	@Transactional(readOnly = false)
	public Boolean deploy(ActYw actYw, RepositoryService repositoryService) {
		return deploy(actYw, repositoryService, null, null);
	}

	/**
	 * 发布并部署项目流程. 以流程标识和项目标识生成流程模板标识和版本（防止多项目共用一个流程是出现菜单、栏目重合）
	 *
	 * @param actYw             项目流程
	 * @param repositoryService 流程服务
	 * @param actModelService   流程模型服务
	 * @param isUpdateYw        标识是否更新到业务表
	 * @return Boolean
	 * @author chenhao
	 */
	@Transactional(readOnly = false)
	public Boolean deploy(ActYw actYw, RepositoryService repositoryService,
						  ActModelService actModelService, Boolean isUpdateYw) {
		try {
			if ((actYw == null) || (!actYw.getIsNewRecord()) && StringUtil.isEmpty(actYw.getId())) {
				return false;
			}

			ActYw actYwNew = get(actYw.getId());
			if (((actYwNew == null) || (StringUtil.isEmpty(actYwNew.getId())))
					&& actYw.getIsNewRecord()) {
				save(actYw);
				actYwNew = get(actYw.getId());
			}

			if (actYwNew == null) {
				return false;
			}

			if ((actYwNew.getGroup() == null) && StringUtil.isNotEmpty(actYwNew.getGroupId())) {
				actYwNew.setGroup(actYwGroupDao.get(actYwNew.getGroupId()));
			}
			actYw.setGroup(actYwNew.getGroup());
			ActYwGroup actYwGroup = actYw.getGroup();
			if (actYwGroup == null) {
				return false;
			}

			ProProject proProject = actYw.getProProject();
			if (proProject == null) {
				return false;
			}

			if (StringUtil.isEmpty(actYwGroup.getKeyss())
					|| StringUtil.isEmpty(proProject.getProjectMark())) {
				return false;
			}

			List<ActYwGnode> actYwGnodes = actYwGnodeDao.findList(new ActYwGnode(actYwGroup));
			String modelKey = ActYw.getPkey(actYw);
			if (modelKey == null) {
				return false;
			}
			RtModel rtModel = new RtModel(
					FlowType.getByKey(actYwGroup.getFlowType()).getName() + ActYw.KEY_SEPTOR
							+ actYwGroup.getName(),
					modelKey, actYwGroup.getRemarks(), actYwGroup.getFlowType(), null, null);
			org.activiti.engine.repository.Model modelData = ActYwTool.genModelData(rtModel,
					repositoryService);
			repositoryService.saveModel(modelData);

			Model repModel = repositoryService.getModel(modelData.getId());
			rtModel.setJsonXml(genJsonBySubProcessActYwGnodes(actYwGroup, actYwGnodes, rtModel));
			repositoryService.addModelEditorSource(repModel.getId(),
					rtModel.getJsonXml().getBytes(RtSvl.RtModelVal.UTF_8));

			/**
			 * 如果部署服务不为空，执行部署！
			 */
			if (actModelService != null) {
				ActRstatus result = actModelService.deploy(modelData.getId());
				/**
				 * 流程发布，流程ID回填到业务表.
				 */
				if (isUpdateYw) {
					actYw.setFlowId(result.getId());
					actYw.setDeploymentId(result.getDeploymentId());
					save(actYw);
				}
			}
			return true;
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("不支持编码格式", e);
			return false;
		}
	}

	/**
	 * 根据流程生成json .
	 *
	 * @param actYwGroup  流程对象
	 * @param actYwGnodes 流程节点对象
	 * @param rtModel     模型
	 * @return String
	 * @author chenhao
	 */
	@Transactional(readOnly = false)
	private String genJsonBySubProcessActYwGnodes(ActYwGroup actYwGroup, List<ActYwGnode> actYwGnodes,
												  RtModel rtModel) {
		ActYwResult rt = ActYwTool.initRtMin(new ActYwResult(),
				RtSvl.RtModelVal.M_PREFIX + IdGen.uuid(), rtModel.getKey(), rtModel.getName(),
				actYwGroup.getAuthor(), actYwGroup.getVersion());

		/**
		 * 过滤出一级节点和二级节点.
		 */
		ActYwGnodeTool actYwGnodeVo = ActYwGnodeTool.gen(actYwGnodes);
		List<ActYwGnode> actYwGnodeProcess = actYwGnodeVo.getActYwGnodeProcess();
		List<ActYwGnode> actYwGnodePsubs = actYwGnodeVo.getActYwGnodePsubs();

		/**
		 * 节点序号最大值.
		 */
		int count = 0;
		/**
		 * 当前前一个业务节点.
		 */
		RtChildShapes curRtCspPre = null;
		RtChildShapes curRtSubCspPre = null;
		/**
		 * 当前父级节点.
		 */
		RtChildShapes curRtCspParent = null;
		Boolean isRootFlowFirst = false;
		Boolean isSubRootFlowFirst = false;
		Boolean isSubRootTaskFirst = false;
		for (int i = 0; i < actYwGnodeProcess.size(); i++) {
			ActYwGnode actYwGnode = actYwGnodeProcess.get(i);
			ActYwNode node = actYwGnode.getNode();
			/**
			 * 判断是否子流程节点.
			 */
			GnodeTypeRt gnodeGtrt = GnodeTypeRt.validate(actYwGnode);
			gnodeGtrt.setRootId(actYwGnodeVo.getRootId());
			gnodeGtrt.setRootStartId(actYwGnodeVo.getRootStartId());
			gnodeGtrt.setRootEndId(actYwGnodeVo.getRootEndId());
			if ((!isRootFlowFirst) && gnodeGtrt.getIsRootFlow()) {
				isRootFlowFirst = true;
			}
			gnodeGtrt.setIsRootFlowFirst(isRootFlowFirst);

			if ((node != null)
					&& (node.getIsFlow() || (node.getLevel()).equals(RtSvl.RtLevelVal.RT_LV1))) {
				if (gnodeGtrt.getIsSub()) {
					// 重置子流程
					isSubRootFlowFirst = false;
					isSubRootTaskFirst = false;
					List<RtChildShapes> childShapes = Lists.newArrayList();

					/**
					 * 获取子节点数量.
					 */
					List<ActYwGnode> curChildGnode = Lists.newArrayList();
					for (int j = 0; j < actYwGnodePsubs.size(); j++) {
						ActYwGnode subActYwGnode = actYwGnodePsubs.get(j);
						ActYwNode subNode = subActYwGnode.getNode();

						if ((subActYwGnode.getParentId()).equals(actYwGnode.getId())) {
							if ((subNode != null) && subNode.getIsFlow()) {
								curChildGnode.add(subActYwGnode);
							}
						}
					}

					RtChildShapes subChildShapes = addRtcsNode(rt, i, curChildGnode.size(), actYwGnode, gnodeGtrt, curRtCspPre, null);
					curRtCspParent = subChildShapes;
					curRtSubCspPre = subChildShapes;
					count++;
					for (int j = 0; j < actYwGnodePsubs.size(); j++) {
						ActYwGnode subActYwGnode = actYwGnodePsubs.get(j);
						ActYwNode subNode = subActYwGnode.getNode();
						GnodeTypeRt subgnodeGtrt = GnodeTypeRt.validate(subActYwGnode);
						if ((!isSubRootFlowFirst) && subgnodeGtrt.getIsSubRootFlow()) {
							isSubRootFlowFirst = true;
						}
						if ((!isSubRootTaskFirst) && subgnodeGtrt.getIsSubRootTask()) {
							isSubRootTaskFirst = true;
						}
						subgnodeGtrt.setIsSubRootFlowFirst(isSubRootFlowFirst);
						subgnodeGtrt.setIsSubRootFlowFirst(isSubRootTaskFirst);
						if ((subActYwGnode.getParentId()).equals(actYwGnode.getId())
								&& ((subNode != null) && subNode.getIsFlow())) {
							RtChildShapes subCshapes = addRtcsNode(rt, count, 0, subActYwGnode, subgnodeGtrt,
									curRtSubCspPre, curRtCspParent);
							childShapes.add(subCshapes);
							curRtSubCspPre = subCshapes;
							count++;
						}
					}

					subChildShapes.setChildShapes(childShapes);
					ActYwTool.addRtChildShapes(rt, subChildShapes);
					curRtCspPre = subChildShapes;
					curRtCspParent = null;
				} else {
					RtChildShapes rtCsp = addRtcsNode(rt, count, 0, actYwGnode, gnodeGtrt, curRtCspPre, null);
					ActYwTool.addRtChildShapes(rt, rtCsp);
					curRtCspPre = rtCsp;
					count++;
				}
			}
		}
		return JSONObject.fromObject(rt).toString();
	}

	/**
	 * 构建流程单个节点.
	 *
	 * @param rt          结果集对象
	 * @param i           序号
	 * @param childSize   子元素数量
	 * @param gnode       当前gnode结点
	 * @param gtrt        结点
	 * @param preCshap    上一个结点
	 * @param parentCshap 当前父结点
	 * @return RtChildShapes
	 * @author chenhao
	 */
	private RtChildShapes addRtcsNode(ActYwResult rt, int i, int childSize, ActYwGnode gnode,
									  GnodeTypeRt gtrt, RtChildShapes preCshap, RtChildShapes parentCshap) {
		if (gnode == null) {
			return null;
		}

		String curOverrideId = null;
		String curTargetResourceId = null;
		String[] curOutgingResourceId = null;
		String curResourceId = null;
		// String curResourceId = RtSvl.RtModelVal.M_PREFIX + IdGen.uuid();

		RtPropertiesX rtPropertiesX = new RtPropertiesX();
		if ((gnode.getForm() != null) && StringUtil.isNotEmpty(gnode.getForm().getPath())) {
			rtPropertiesX.setFormkeydefinition(gnode.getForm().getPath());
			// rtPropertiesX.setFormproperties(new RtPxFormproperties(gnode.getForm().getParams()));
		}
		if (gtrt.getIsRootStart()) {
			curOutgingResourceId = new String[]{ActYwTool.FLOW_ID_PREFIX + gnode.getNextId()};
			curTargetResourceId = null;
			curResourceId = ActYwTool.FLOW_ID_START + gnode.getId();
			curOverrideId = curResourceId;

			rtPropertiesX.setDefaultflow(null);
			rtPropertiesX.setMultiinstance_type(RtPropertiesX.MULTIINSTANCE_TYPE_PARALLEL);
			rtPropertiesX.setIsforcompensation(RtPropertiesX.ISFORCOMPENSATION_FALSE);
			rtPropertiesX.setExclusivedefinition(RtPropertiesX.EXCLUSIVEDEFINITION_TRUE);
			rtPropertiesX.setAsynchronousdefinition(RtPropertiesX.ASYNCHRONOUSDEFINITION_FALSE);
			rtPropertiesX = RtPropertiesX.setUsertaskassignment(rtPropertiesX, gnode.getFlowGroup());
		} else if (gtrt.getIsRootEnd()) {
			curTargetResourceId = null;
			curOutgingResourceId = null;
			curResourceId = ActYwTool.FLOW_ID_PREFIX + gnode.getId();
			curOverrideId = curResourceId;

			rtPropertiesX.setDefaultflow(null);
			rtPropertiesX.setMultiinstance_type(RtPropertiesX.MULTIINSTANCE_TYPE_PARALLEL);
			rtPropertiesX.setIsforcompensation(RtPropertiesX.ISFORCOMPENSATION_FALSE);
			rtPropertiesX.setExclusivedefinition(RtPropertiesX.EXCLUSIVEDEFINITION_TRUE);
			rtPropertiesX.setAsynchronousdefinition(RtPropertiesX.ASYNCHRONOUSDEFINITION_FALSE);
			rtPropertiesX = RtPropertiesX.setUsertaskassignment(rtPropertiesX, gnode.getFlowGroup());
		} else if (gtrt.getIsRootFlow()) {
			curTargetResourceId = ActYwTool.FLOW_ID_PREFIX + gnode.getNextFunId();
			curOutgingResourceId = new String[]{ActYwTool.FLOW_ID_PREFIX + gnode.getNextId()};
			curResourceId = ActYwTool.FLOW_ID_PREFIX + gnode.getId();
			curOverrideId = curResourceId;

			/**
			 * 第一个流程线.
			 */
			if (gtrt.getIsRootFlowFirst()) {
				rtPropertiesX.setShowdiamondmarker(RtPropertiesX.SHOWDIAMONDMARKER_FALSE);
			}
			rtPropertiesX.setDefaultflow(RtPropertiesX.DEFAULT_FLOW_FALSE);
			rtPropertiesX.setMultiinstance_type(RtPropertiesX.MULTIINSTANCE_TYPE_PARALLEL);
			rtPropertiesX.setIsforcompensation(RtPropertiesX.ISFORCOMPENSATION_FALSE);
			rtPropertiesX.setExclusivedefinition(RtPropertiesX.EXCLUSIVEDEFINITION_TRUE);
			rtPropertiesX.setAsynchronousdefinition(RtPropertiesX.ASYNCHRONOUSDEFINITION_FALSE);
			rtPropertiesX = RtPropertiesX.setUsertaskassignment(rtPropertiesX, gnode.getFlowGroup());
		} else if (gtrt.getIsSub()) {
			curTargetResourceId = null;
			curOutgingResourceId = new String[]{ActYwTool.FLOW_ID_PREFIX + gnode.getNextId()};
			curResourceId = ActYwTool.FLOW_ID_PREFIX + gnode.getId();
			curOverrideId = ActYwTool.FLOW_ID_PREFIX + gnode.getParentId();
			curOverrideId = curResourceId;

			rtPropertiesX.setDefaultflow(null);
			rtPropertiesX.setMultiinstance_type(RtPropertiesX.MULTIINSTANCE_TYPE_NONE);
			rtPropertiesX.setIsforcompensation(RtPropertiesX.ISFORCOMPENSATION_FALSE);
			rtPropertiesX.setExclusivedefinition(RtPropertiesX.EXCLUSIVEDEFINITION_TRUE);
			rtPropertiesX.setAsynchronousdefinition(RtPropertiesX.ASYNCHRONOUSDEFINITION_FALSE);
			rtPropertiesX = RtPropertiesX.setUsertaskassignment(rtPropertiesX, gnode.getFlowGroup());
		} else if (gtrt.getIsSubRootStart()) {
			curTargetResourceId = null;
			curOutgingResourceId = new String[]{ActYwTool.FLOW_ID_PREFIX + gnode.getNextId()};
			curResourceId = ActYwTool.FLOW_ID_PREFIX + gnode.getId();
			curOverrideId = curResourceId;

			rtPropertiesX.setDefaultflow(null);
			rtPropertiesX.setMultiinstance_type(RtPropertiesX.MULTIINSTANCE_TYPE_PARALLEL);
			rtPropertiesX.setIsforcompensation(RtPropertiesX.ISFORCOMPENSATION_FALSE);
			rtPropertiesX.setExclusivedefinition(RtPropertiesX.EXCLUSIVEDEFINITION_TRUE);
			rtPropertiesX.setAsynchronousdefinition(RtPropertiesX.ASYNCHRONOUSDEFINITION_FALSE);
			rtPropertiesX = RtPropertiesX.setUsertaskassignment(rtPropertiesX, gnode.getFlowGroup());
		} else if (gtrt.getIsSubRootEnd()) {
			curTargetResourceId = null;
			curOutgingResourceId = null;
			curResourceId = ActYwTool.FLOW_ID_PREFIX + gnode.getId();
			curOverrideId = curResourceId;

			rtPropertiesX.setDefaultflow(null);
			rtPropertiesX.setMultiinstance_type(RtPropertiesX.MULTIINSTANCE_TYPE_PARALLEL);
			rtPropertiesX.setIsforcompensation(RtPropertiesX.ISFORCOMPENSATION_FALSE);
			rtPropertiesX.setExclusivedefinition(RtPropertiesX.EXCLUSIVEDEFINITION_TRUE);
			rtPropertiesX.setAsynchronousdefinition(RtPropertiesX.ASYNCHRONOUSDEFINITION_FALSE);
			rtPropertiesX = RtPropertiesX.setUsertaskassignment(rtPropertiesX, gnode.getFlowGroup());
		} else if (gtrt.getIsSubRootFlow()) {
			curTargetResourceId = ActYwTool.FLOW_ID_PREFIX + gnode.getNextFunId();
			curOutgingResourceId = new String[]{ActYwTool.FLOW_ID_PREFIX + gnode.getNextId()};
			curResourceId = ActYwTool.FLOW_ID_PREFIX + gnode.getId();
			curOverrideId = curResourceId;

			rtPropertiesX.setDefaultflow(RtPropertiesX.DEFAULT_FLOW_FALSE);
			rtPropertiesX.setMultiinstance_type(RtPropertiesX.MULTIINSTANCE_TYPE_PARALLEL);
			rtPropertiesX.setIsforcompensation(RtPropertiesX.ISFORCOMPENSATION_FALSE);
			rtPropertiesX.setExclusivedefinition(RtPropertiesX.EXCLUSIVEDEFINITION_TRUE);
			rtPropertiesX.setAsynchronousdefinition(RtPropertiesX.ASYNCHRONOUSDEFINITION_FALSE);
			rtPropertiesX = RtPropertiesX.setUsertaskassignment(rtPropertiesX, gnode.getFlowGroup());
		} else {
			curTargetResourceId = ActYwTool.FLOW_ID_PREFIX + gnode.getNextFunId();
			curOutgingResourceId = new String[]{ActYwTool.FLOW_ID_PREFIX + gnode.getNextId()};
			curResourceId = ActYwTool.FLOW_ID_PREFIX + gnode.getId();
			curOverrideId = curResourceId;

			rtPropertiesX.setDefaultflow(null);
			rtPropertiesX.setMultiinstance_type(RtPropertiesX.MULTIINSTANCE_TYPE_PARALLEL);
			rtPropertiesX.setIsforcompensation(RtPropertiesX.ISFORCOMPENSATION_FALSE);
			rtPropertiesX.setExclusivedefinition(RtPropertiesX.EXCLUSIVEDEFINITION_TRUE);
			rtPropertiesX.setAsynchronousdefinition(RtPropertiesX.ASYNCHRONOUSDEFINITION_FALSE);
			rtPropertiesX = RtPropertiesX.setUsertaskassignment(rtPropertiesX, gnode.getFlowGroup());
		}

		ActYwNode node = gnode.getNode();
		List<Double[]> doSxys = Lists.newArrayList();
		RtChildShapes rtcs = new RtChildShapes();

		RtBounds rtb = rt.getBounds();
		RtUpperLeft rtbul = rtb.getUpperLeft();
		RtLowerRight rtblr = rtb.getLowerRight();

		Double centerx = (double) ((rtblr.getX() - rtbul.getX()) / 2);
		Double zeroX = (double) (centerx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2);
		/**
		 * 横向定位.
		 */
		// doSxys = sortPostionOrientation(i, childSize, node, preCshap, parentCshap, zeroX, maxx);
		/**
		 * 纵向定位.
		 */
		doSxys = sortPostionPortrait(i, childSize, node, preCshap, parentCshap, zeroX);

		return ActYwTool.initRtChildShapesByName(rt, rtcs, curResourceId, // rtcSresourceId
				curOverrideId, // prXoverrideid
				gnode.getName(), // prXname
				gnode.getName(), // prXdocumentation
				rtPropertiesX, StenType.getByKey(node.getNodeKey()), // stXstenType
				(i + 1), // boXsort
				curOutgingResourceId, // outgingSresourceId
				curTargetResourceId, // targetResourceId
				doSxys, gnode, gtrt, preCshap, parentCshap);
	}

	/**
	 * 横向定位排序展示流程节点.
	 *
	 * @param i
	 * @param childSize
	 * @param node
	 * @param preCshap
	 * @param parentCshap
	 * @param zeroX
	 * @return
	 */
	private List<Double[]> sortPostionOrientation(int i, int childSize, ActYwNode node,
												  RtChildShapes preCshap, RtChildShapes parentCshap, Double zeroX, Double maxx) {
		Double ulx, uly, lrx, lry;
		List<Double[]> doSxys;
		/**
		 * 若前置节点为空且不是第一个节点！说明参数有误返回空.
		 */
		if (preCshap == null) {
			if (i == 0) {
				ulx = zeroX;
				uly = RtSvl.RtBoundsVal.RT_ZERO;

				lrx = ulx + RtSvl.RtBoundsVal.RT_COLS_WIDTH;
				lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT;
			} else {
				return null;
			}
		} else {
			StenType stenType = StenType.getByKey(node.getNodeKey());

			if (parentCshap == null) {
				// 初始化一级节点
				RtBoundsX preBsx = preCshap.getBounds();
				RtUpperLeftX preUl = preBsx.getUpperLeft();
				RtLowerRightX preLr = preBsx.getLowerRight();

				Double preCenterX = (preUl.getX() + preLr.getX()) / 2;
				Double preCenterY = (preUl.getY() + preLr.getY()) / 2;

				if (stenType.equals(StenType.ST_JG_SUB_PROCESS)) {
					ulx = RtSvl.RtBoundsVal.RT_ZERO;
					uly = preCenterY + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2;
					lrx = maxx - RtSvl.RtBoundsVal.RT_ZERO;
					lry = uly
							+ ((childSize - 1) * (RtSvl.RtBoundsVal.RT_RATE * RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2
							+ RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 4))
							+ RtSvl.RtBoundsVal.RT_ZERO;
				} else if (stenType.equals(StenType.ST_FLOW_SEQUENCE)) {
					ulx = preCenterX;
					uly = preLr.getY();
					lrx = ulx;
					lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;
					if (i == 1) {
						uly = preCenterY - RtSvl.RtBoundsVal.RT_ZERO;
					}
				} else if (stenType.equals(StenType.ST_END_EVENT_NONE)) {
					ulx = preCenterX;
					uly = preLr.getY();
					lrx = ulx;
					lry = uly;
				} else {
					ulx = preCenterX;
					uly = preCenterY;
					lrx = ulx;
					lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;
				}
			} else {
				// 初始化子级节点
				RtBoundsX preBsx = preCshap.getBounds();
				RtBoundsX parentBsx = parentCshap.getBounds();

				RtUpperLeftX preUl = preBsx.getUpperLeft();
				RtLowerRightX preLr = preBsx.getLowerRight();
				RtUpperLeftX parentUl = parentBsx.getUpperLeft();
				RtLowerRightX parentLr = parentBsx.getLowerRight();

				Double preCenterX = (preUl.getX() + preLr.getX()) / 2;
				Double preCenterY = (preUl.getY() + preLr.getY()) / 2;
				Double parentCenterX = (parentUl.getX() + parentLr.getX()) / 2;
				Double parentCenterY = (parentUl.getY() + parentLr.getY()) / 2;
				Double parentZeroX = parentUl.getX();
				Double parentZeroY = parentUl.getY();

				if (stenType.equals(StenType.ST_START_EVENT_NONE)) {
					ulx = RtSvl.RtBoundsVal.RT_ZERO;
					uly = preCenterY - parentZeroY;
					lrx = ulx;
					lry = uly;
				} else if (stenType.equals(StenType.ST_FLOW_SEQUENCE)) {
					// ulx = RtSvl.RtBoundsVal.RT_ZERO+RtSvl.RtBoundsVal.RT_ZERO;
					// uly = preCenterY - parentZeroY;
					// lrx = ulx + RtSvl.RtBoundsVal.RT_COLS_WIDTH * RtSvl.RtBoundsVal.RT_RATE;
					// lry = uly;

					// ulx = zeroX - 100;
					// uly = parentZeroY + preLr.getY();
					// lrx = ulx + 150;
					// lry = uly + 100;

					ulx = 253.07421154834836;
					uly = 374.26890023433293;
					lrx = 91.85938220165163;
					lry = 188.9107872656671;
					// lrx = ulx + 150.0;
					// lry = uly;
					// lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;

					// ulx = zeroX + preCenterX;
					// uly = parentZeroY + preLr.getY();
					// lrx = ulx;
					// lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;

				} else if (stenType.equals(StenType.ST_END_EVENT_NONE)) {
					ulx = preCenterX - parentZeroX;
					uly = preLr.getY() - parentZeroY;
					lrx = ulx;
					lry = uly;
				} else {
					ulx = preCenterX - parentZeroX + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 8;
					uly = preLr.getY() - parentZeroY;
					lrx = ulx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 4;
					lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2;
				}
			}
		}

		doSxys = Lists.newArrayList(Arrays.asList(new Double[][]{{lrx, lry}, {ulx, uly}}));
		LOGGER.info(node.getName() + "--> lrx=" + i + "-" + node.getName() + "--> lrx=" + lrx + " lry="
				+ lry + " ulx=" + ulx + " uly=" + uly);
		return doSxys;
	}

	/**
	 * 纵向定位排序展示流程节点.
	 *
	 * @param i
	 * @param childSize
	 * @param node
	 * @param preCshap
	 * @param parentCshap
	 * @param zeroX
	 * @return
	 */
	private List<Double[]> sortPostionPortrait(int i, int childSize, ActYwNode node,
											   RtChildShapes preCshap, RtChildShapes parentCshap, Double zeroX) {
		Double ulx, uly, lrx, lry;
		List<Double[]> doSxys;
		/**
		 * 若前置节点为空且不是第一个节点！说明参数有误返回空.
		 */
		if (preCshap == null) {
			if (i == 0) {
				ulx = zeroX;
				uly = RtSvl.RtBoundsVal.RT_ZERO;

				lrx = ulx;
				lry = uly;
				// ulx = zeroX;
				// uly = RtSvl.RtBoundsVal.RT_ZERO;
				//
				// lrx = ulx + RtSvl.RtBoundsVal.RT_COLS_WIDTH;
				// lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT;
			} else {
				return null;
			}
		} else {
			StenType stenType = StenType.getByKey(node.getNodeKey());

			if (parentCshap == null) {
				// 初始化一级节点
				RtBoundsX preBsx = preCshap.getBounds();
				RtUpperLeftX preUl = preBsx.getUpperLeft();
				RtLowerRightX preLr = preBsx.getLowerRight();

				Double preCenterX = (preUl.getX() + preLr.getX()) / 2;
				Double preCenterY = (preUl.getY() + preLr.getY()) / 2;

				if (stenType.equals(StenType.ST_JG_SUB_PROCESS)) {
					ulx = preCenterX - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 2;
					uly = preCenterY + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2;
					lrx = ulx + RtSvl.RtBoundsVal.RT_COLS_WIDTH;
					lry = uly
							+ ((childSize - 1) * (RtSvl.RtBoundsVal.RT_RATE * RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2
							+ RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 4))
							+ RtSvl.RtBoundsVal.RT_ZERO;
				} else if (stenType.equals(StenType.ST_FLOW_SEQUENCE)) {
					ulx = preCenterX;
					uly = preLr.getY();
					lrx = ulx;
					lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;
					if (i == 1) {
						uly = preCenterY - RtSvl.RtBoundsVal.RT_ZERO;
					}
				} else if (stenType.equals(StenType.ST_END_EVENT_NONE)) {
					ulx = preCenterX;
					uly = preLr.getY();
					lrx = ulx;
					lry = uly;
				} else {
					ulx = preCenterX;
					uly = preCenterY;
					lrx = ulx;
					lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;
				}
			} else {
				// 初始化子级节点
				RtBoundsX preBsx = preCshap.getBounds();
				RtBoundsX parentBsx = parentCshap.getBounds();

				RtUpperLeftX preUl = preBsx.getUpperLeft();
				RtLowerRightX preLr = preBsx.getLowerRight();
				RtUpperLeftX parentUl = parentBsx.getUpperLeft();
				RtLowerRightX parentLr = parentBsx.getLowerRight();

				Double preCenterX = (preUl.getX() + preLr.getX()) / 2;
				Double preCenterY = (preUl.getY() + preLr.getY()) / 2;
				Double parentCenterX = (parentUl.getX() + parentLr.getX()) / 2;
				Double parentCenterY = (parentUl.getY() + parentLr.getY()) / 2;
				Double parentZeroX = parentUl.getX();
				Double parentZeroY = parentUl.getY();

				if (stenType.equals(StenType.ST_START_EVENT_NONE)) {
					ulx = parentCenterX - parentZeroX;
					uly = RtSvl.RtBoundsVal.RT_ZERO;
					lrx = ulx;
					lry = uly;
				} else if (stenType.equals(StenType.ST_FLOW_SEQUENCE)) {
					ulx = zeroX + preCenterX;
					uly = parentZeroY + preLr.getY();
					lrx = ulx;
					lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT * RtSvl.RtBoundsVal.RT_RATE;
				} else if (stenType.equals(StenType.ST_END_EVENT_NONE)) {
					ulx = preCenterX - parentZeroX;
					uly = preLr.getY() - parentZeroY;
					lrx = ulx;
					lry = uly;
				} else {
					ulx = preCenterX - parentZeroX + RtSvl.RtBoundsVal.RT_COLS_WIDTH / 8;
					uly = preLr.getY() - parentZeroY;
					lrx = ulx - RtSvl.RtBoundsVal.RT_COLS_WIDTH / 4;
					lry = uly + RtSvl.RtBoundsVal.RT_COLS_HEIGHT / 2;
				}
			}
		}

		doSxys = Lists.newArrayList(Arrays.asList(new Double[][]{{lrx, lry}, {ulx, uly}}));
		LOGGER.info(node.getName() + "--> lrx=" + i + "-" + node.getName() + "--> lrx=" + lrx + " lry=" + lry + " ulx=" + ulx + " uly=" + uly);
		return doSxys;
	}

	/**
	 * 根据resourceId获取RtChildShapes节点对象.
	 *
	 * @param rt       结果集
	 * @param preCshap 前一个节点
	 * @return RtChildShapesMap
	 */
	public RtChildShapesMap getRtShapesByResourceId(ActYwResult rt, RtChildShapes preCshap) {
		List<RtChildShapes> curRtChildShapess = rt.getChildShapes();
		Integer idx = 0;
		for (RtChildShapes rtcshapes : curRtChildShapess) {
			if ((rtcshapes.getResourceId()).equals(preCshap.getResourceId())) {
				return new RtChildShapesMap(idx, rtcshapes);
			}
			idx++;
		}
		return null;
	}

	/**
	 * 根据resourceId获取并更新RtChildShapes节点对象.
	 *
	 * @param rt       结果集
	 * @param paramMap 更新参数
	 * @return RtChildShapesMap
	 */
	public ActYwResult updateRtCurChildShapes(ActYwResult rt, String resourceId,
											  RtChildShapesParamMap paramMap) {
		List<RtChildShapes> curRtChildShapess = rt.getChildShapes();

		for (RtChildShapes rtcshapes : curRtChildShapess) {
			if ((rtcshapes != null) && (resourceId != null)
					&& (rtcshapes.getResourceId()).equals(resourceId)) {
				rtcshapes = paramMap.update(rtcshapes);
				break;
			}
		}

		rt.setChildShapes(curRtChildShapess);
		return rt;
	}
}