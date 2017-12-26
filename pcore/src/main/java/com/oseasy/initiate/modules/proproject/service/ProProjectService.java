package com.oseasy.initiate.modules.proproject.service;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.thoughtworks.xstream.mapper.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGtime;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.service.ActYwGroupService;
import com.oseasy.initiate.modules.actyw.service.ActYwGtimeService;
import com.oseasy.initiate.modules.actyw.tool.project.ActProParamVo;
import com.oseasy.initiate.modules.actyw.tool.project.ActProRunner;
import com.oseasy.initiate.modules.actyw.tool.project.ActProStatus;
import com.oseasy.initiate.modules.actyw.tool.project.impl.ActProProject;
import com.oseasy.initiate.modules.cms.entity.Category;
import com.oseasy.initiate.modules.cms.service.CategoryService;
import com.oseasy.initiate.modules.proproject.dao.ProProjectDao;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.sys.entity.Menu;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.tool.SysNoType;
import com.oseasy.initiate.modules.sys.tool.SysNodeTool;

/**
 * 创建项目Service.
 * @author zhangyao
 * @version 2017-06-15
 */
@Service
@Transactional(readOnly = true)
public class ProProjectService extends CrudService<ProProjectDao, ProProject> {
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private SystemService systemService;
	@Autowired
	ActYwGtimeService actYwGtimeService;

	@Autowired
	private ActYwGnodeService actYwGnodeService;

	public ProProject get(String id) {
		return super.get(id);
	}

	public List<ProProject> findList(ProProject proProject) {
		return super.findList(proProject);
	}

	public Page<ProProject> findPage(Page<ProProject> page, ProProject proProject) {
		return super.findPage(page, proProject);
	}

  	@Transactional(readOnly = false)
  	public void save(ProProject proProject) {
		if(StringUtil.isEmpty(proProject.getProjectMark())){
		  	proProject.setProjectMark(SysNodeTool.genByKeyss(SysNoType.NO_PROJECT));
		}
		//判断是否已经生成过栏目和菜单
		//将生成栏目和菜单隐藏不可见
		/*if(StringUtil.isEmpty(proProject.getId())){
		  //生成栏目表
		  	Category category=new Category();
		  //category.setParent(new Category(SysIds.SITE_CATEGORYS_SYS_ROOT.getId()));
		  	Category parent = categoryService.get(SysIds.SITE_CATEGORYS_TOP_ROOT.getId());
		  	category.setParent(parent);
		  	category.setSite(parent.getSite());
		  	category.setOffice(parent.getOffice());
		  	category.setName(proProject.getProjectName());
		  	category.setDescription(proProject.getContent());
		  	category.setInMenu(Global.SHOW);
		  	category.setInList(Global.HIDE);
		  	category.setIsAudit(Global.NO);
		  	category.setSort(10);
			category.setRemarks("流程栏目");
		  	categoryService.save(category);
		  	proProject.setCategory(category);
		  //生成后台菜单
		  	Menu menu=new Menu();
		  	menu.setParent(systemService.getMenu(Menu.getRootId()));
		  	menu.setName(proProject.getProjectName());
		  	menu.setIsShow(Global.HIDE);
		  	menu.setImgUrl(proProject.getImgUrl());
		  	menu.setRemarks(proProject.getContent());
		  	menu.setSort(10);
			menu.setRemarks("流程菜单");
		  	systemService.saveMenu(menu);
		  	proProject.setMenu(menu);
		}*/
    try {
      proProject.setStartDate(DateUtil.getStartDate(proProject.getStartDate()));
      proProject.setEndDate(DateUtil.getEndDate(proProject.getEndDate()));
      proProject.setNodeStartDate(DateUtil.getStartDate(proProject.getNodeStartDate()));
      proProject.setNodeEndDate(DateUtil.getEndDate(proProject.getNodeEndDate()));
    } catch (ParseException e) {
      logger.error(e.getMessage());
    }
		super.save(proProject);
	}

	@Transactional(readOnly = false)
	public void delete(ProProject proProject) {
		super.delete(proProject);
	}
	public ProProject getProProjectByName(String name) {
		return dao.getProProjectByName(name);
	}

	@Transactional(readOnly = false)
	public void saveProProject(ProProject proProject) {
		//生成栏目表
		Category category=new Category();
		Category parent = categoryService.get(SysIds.SITE_CATEGORYS_TOP_ROOT.getId());
		category.setParent(parent);
		category.setSite(parent.getSite());
		category.setOffice(parent.getOffice());
		category.setName(proProject.getProjectName());
		category.setDescription(proProject.getContent());
		category.setInMenu(Global.SHOW);
		category.setInList(Global.SHOW);
		category.setIsAudit(Global.NO);
		category.setSort(10);
		categoryService.save(category);

		//生成后台菜单
		Menu menu=new Menu();
		menu.setParent(systemService.getMenu(Menu.getRootId()));
		menu.setName(proProject.getProjectName());
		menu.setIsShow(Global.SHOW);
		menu.setRemarks(proProject.getContent());
		menu.setSort(10);
		systemService.saveMenu(menu);

		save(proProject);
	}

	@Transactional(readOnly = false)
	public void changeProProjectModel(ActYw actYw, HttpServletRequest request)  {
		ProProject proProject = actYw.getProProject();

		//根据流程生成子菜单
		if (actYw.getGroupId() != null) {


			String[] gNodeId = request.getParameterValues("nodeId");
			String[] beginDate = request.getParameterValues("beginDate");
			String[] endDate = request.getParameterValues("endDate");
			if (beginDate != null && beginDate.length > 0 && endDate != null && endDate.length > 0) {
				for (int i = 0; i < beginDate.length; i++) {
					String status = request.getParameter("status" + i);
					ActYwGtime actYwGtime = new ActYwGtime();
					actYwGtime.setGrounpId(actYw.getGroupId());
					actYwGtime.setProjectId(actYw.getRelId());
					actYwGtime.setGnodeId(gNodeId[i]);
					actYwGtime.setStatus(status);
					actYwGtime.setBeginDate(DateUtil.parseDate(beginDate[i]));
					actYwGtime.setEndDate(DateUtil.parseDate(endDate[i]));
					actYwGtimeService.save(actYwGtime);
				}
			}
	    save(proProject);
		}
	}

	//创建菜单
	@Transactional(readOnly = false)
	public void createMenu(ProProject proProject) {
		Menu menu=new Menu();
		menu.setParent(systemService.getMenu(Menu.getRootId()));
		menu.setName(proProject.getProjectName());
		menu.setIsShow(Global.SHOW);
		menu.setRemarks(proProject.getContent());
		menu.setSort(10);
		menu.setImgUrl(proProject.getImgUrl());
		systemService.saveMenu(menu);
		proProject.setMenu(menu);
	}

	//创建栏目
	@Transactional(readOnly = false)
	public void createCategory(ProProject proProject,ActYw actYw) {
		Category category=new Category();
		Category parent = categoryService.get(SysIds.SITE_CATEGORYS_TOP_ROOT.getId());
		category.setParent(parent);
		category.setSite(parent.getSite());
		category.setOffice(parent.getOffice());
		category.setName(proProject.getProjectName());
		category.setDescription(proProject.getContent());
		category.setInMenu(Global.SHOW);
		category.setInList(Global.SHOW);
		category.setIsAudit(Global.NO);
		category.setSort(40);
		categoryService.save(category);
		//默认添加申报表单
		Category categoryapp=new Category();

		categoryapp.setParent(category);
		categoryapp.setSite(category.getSite());
		categoryapp.setOffice(category.getOffice());
		categoryapp.setName(proProject.getProjectName());
		categoryapp.setDescription(proProject.getContent());
		categoryapp.setInMenu(Global.SHOW);
		categoryapp.setInList(Global.SHOW);
		categoryapp.setIsAudit(Global.NO);
		categoryapp.setHref("/form/"+proProject.getProjectMark()+"/applyForm?id="+actYw.getId());

		categoryapp.setSort(40);
		categoryService.save(categoryapp);

		proProject.setCategory(category);
	}

	//屏蔽以发布流程
	@Transactional(readOnly = false)
	public void savedis(ProProject proProject) {
		Menu menu = proProject.getMenu();
		if(StringUtil.isNotEmpty(proProject.getMenuRid())){
			menu = systemService.getMenu(proProject.getMenuRid());
		}
		Category category = proProject.getCategory();
		if((category == null) && StringUtil.isNotEmpty(proProject.getCategoryRid())){
			category = categoryService.get(proProject.getCategoryRid());
		}
		if(category!=null){
			category.setInMenu(Global.HIDE);
			category.setName(proProject.getProjectName());
			category.setDescription(proProject.getContent());
			categoryService.save(category);
			proProject.setCategory(category);
		}
		if(menu!=null) {
			menu.setIsShow(Global.HIDE);
			menu.setParent(systemService.getMenu(Menu.getRootId()));
			menu.setName(proProject.getProjectName());
			menu.setRemarks(proProject.getContent());
			menu.setImgUrl(proProject.getImgUrl());
			systemService.saveMenu(menu);
			proProject.setMenu(menu);
		}
		save(proProject);
	}

	@Transactional(readOnly = false)
	public void saveProProject(ActYw actYw, HttpServletRequest request)  {
		ProProject proProject = actYw.getProProject();
		if(proProject != null && StringUtil.isNotEmpty(proProject.getProjectName())){
			/**
			 * 生成前台栏目.
			 */
			ActProParamVo actProParamVo =new ActProParamVo();
			actProParamVo.setActYw(actYw);
			actProParamVo.setProProject(proProject);

			ActProRunner<ActProProject> projectRunner = new ActProRunner<ActProProject>(new ActProProject());
			ActProStatus actProStatus = projectRunner.execute(actProParamVo);
			actProParamVo = actProStatus.getActProParamVo();
			proProject = actProParamVo.getProProject();

			/*createCategory(proProject,actYw);
			*//**
			 * 生成后台菜单.
			 *//*
			createMenu(proProject);*/
			//根据流程生成子菜单
			if(actYw.getGroupId()!=null){
				ActYwGnode actYwGnode=new ActYwGnode();
				actYwGnode.setGroupId(actYw.getGroupId());
				List<ActYwGnode> sourcelist = actYwGnodeService.findListByYwProcess(actYwGnode);
				if(proProject.getMenu()!=null){
					if(sourcelist.size()>0){
						for(int i=0;i<sourcelist.size();i++) {
							if(sourcelist.get(i)!=null){
								Menu menuForm = new Menu();
								menuForm.setParent(proProject.getMenu());
								menuForm.setName(sourcelist.get(i).getName());
								menuForm.setIsShow(Global.SHOW);
								menuForm.setHref("form/"+proProject.getProjectMark()+"/"+sourcelist.get(i).getFormId()+"?id="+ actYw.getId());
								menuForm.setSort(10);
								systemService.saveMenu(menuForm);

								Menu menuNextForm = new Menu();
								menuNextForm.setParent(menuForm);
								menuNextForm.setName(sourcelist.get(i).getNode().getName());
								menuNextForm.setIsShow(Global.SHOW);
								menuNextForm.setHref("/cms/form/"+proProject.getProjectMark()+"/"+sourcelist.get(i).getFormId()+"?id="+ actYw.getId());
								menuNextForm.setSort(10);
								systemService.saveMenu(menuNextForm);
							}
						}
					}
				}

				String[] gNodeId= request.getParameterValues("nodeId");
				String[] beginDate= request.getParameterValues("beginDate");
				String[] endDate= request.getParameterValues("endDate");
				if (gNodeId!=null && gNodeId.length>0) {
					for(int i=0;i<gNodeId.length;i++) {
						String status= request.getParameter("status" + i);
						ActYwGtime actYwGtime =new ActYwGtime();
						actYwGtime.setGrounpId(actYw.getGroupId());
						actYwGtime.setProjectId(actYw.getRelId());
						actYwGtime.setGnodeId(gNodeId[i]);
						actYwGtime.setStatus(status);
						if(beginDate[i]!=null){
							actYwGtime.setBeginDate(DateUtil.parseDate(beginDate[i]));
						}
						if(endDate[i]!=null){
							actYwGtime.setEndDate(DateUtil.parseDate(endDate[i]));
						}
						actYwGtimeService.save(actYwGtime);
					}
				}
			}
	    save(proProject);
		}
	}

}