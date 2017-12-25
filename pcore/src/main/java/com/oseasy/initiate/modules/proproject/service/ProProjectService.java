package com.oseasy.initiate.modules.proproject.service;

import java.util.List;

import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
import com.oseasy.initiate.modules.actyw.entity.ActYwGtime;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.service.ActYwGroupService;
import com.oseasy.initiate.modules.actyw.service.ActYwGtimeService;
import com.oseasy.initiate.modules.actyw.service.ActYwService;
import com.oseasy.initiate.modules.cms.entity.Category;
import com.oseasy.initiate.modules.cms.service.CategoryService;
import com.oseasy.initiate.modules.gcontest.entity.GContestAnnounce;
import com.oseasy.initiate.modules.sys.entity.Menu;
import com.oseasy.initiate.modules.sys.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.proproject.dao.ProProjectDao;

import javax.servlet.http.HttpServletRequest;

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
	private ActYwGroupService actYwGroupService;
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
		//category.setParent(new Category(SysIds.SITE_CATEGORYS_SYS_ROOT.getId()));
		Category parent = categoryService.get(SysIds.SITE_CATEGORYS_TOP_ROOT.getId());
		category.setParent(parent);
		category.setSite(parent.getSite());
		category.setOffice(parent.getOffice());
		category.setName(proProject.getProjectName());
		category.setDescription(proProject.getContent());
		category.setInMenu("1");
		category.setInList("1");
		category.setIsAudit("0");
		category.setSort(40);
		categoryService.save(category);
		//生成后台菜单
		Menu menu=new Menu();
		menu.setParent(systemService.getMenu(Menu.getRootId()));
		menu.setName(proProject.getProjectName());
		menu.setIsShow("1");
		menu.setRemarks(proProject.getContent());
		menu.setSort(10);
		systemService.saveMenu(menu);

		save(proProject);
	}

	@Transactional(readOnly = false)
	public void changeProProjectModel(ActYw actYw, HttpServletRequest request)  {
		ProProject proProject = actYw.getProProject();
		Category category = new Category();
		Menu menu = new Menu();
		if(proProject!=null){

			Category parent = categoryService.get(SysIds.SITE_CATEGORYS_TOP_ROOT.getId());
			category.setParent(parent);
			category.setSite(parent.getSite());
			category.setOffice(parent.getOffice());
			category.setName(proProject.getProjectName());
			category.setDescription(proProject.getContent());
			category.setInMenu("1");
			category.setInList("1");
			category.setIsAudit("0");
			category.setSort(40);
			categoryService.save(category);
			proProject.setCategory(category);
			/**
			 * 生成后台菜单.
			 */

			menu.setParent(systemService.getMenu(Menu.getRootId()));
			menu.setName(proProject.getProjectName());
			menu.setIsShow("1");
			menu.setRemarks(proProject.getContent());
			menu.setSort(10);
			menu.setImgUrl(proProject.getImgUrl());
			systemService.saveMenu(menu);
			proProject.setMenu(menu);
		}
		//根据流程生成子菜单
		if (actYw.getGroupId() != null) {
			ActYwGnode actYwGnode = new ActYwGnode();
			actYwGnode.setGroupId(actYw.getGroupId());
			List<ActYwGnode> sourcelist = actYwGnodeService.findListByYwProcess(actYwGnode);
			if (sourcelist.size() > 0) {
				for (int i = 0; i < sourcelist.size(); i++) {
					if (sourcelist.get(i) != null) {
						Menu menuForm = new Menu();
						menuForm.setParent(menu);
						menuForm.setName(sourcelist.get(i).getNode().getName());
						menuForm.setIsShow("1");
						menuForm.setHref("form/" + proProject.getProjectMark() + "/" + sourcelist.get(i).getFormId() + "?id=" + actYw.getId());
						menuForm.setSort(10);
						systemService.saveMenu(menuForm);

						Menu menuNextForm = new Menu();
						menuNextForm.setParent(menuForm);
						menuNextForm.setName(sourcelist.get(i).getNode().getName());
						menuNextForm.setIsShow("1");
						menuNextForm.setHref("cms/form/" + proProject.getProjectMark() + "/" + sourcelist.get(i).getFormId() + "?id=" + actYw.getId());
						menuNextForm.setSort(10);
						systemService.saveMenu(menuNextForm);
					}
				}
			}

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

	@Transactional(readOnly = false)
	public void editProProject(ActYw actYw, HttpServletRequest request) {
		ProProject proProject = actYw.getProProject();
		Menu menu = proProject.getMenu();
		if(StringUtil.isNotEmpty(proProject.getMenuRid())){
			menu = systemService.getMenu(proProject.getMenuRid());
		}
		Category category = proProject.getCategory();
		if((category == null) && StringUtil.isNotEmpty(proProject.getCategoryRid())){
			category = categoryService.get(proProject.getCategoryRid());
		}
		if(category!=null){
			category.setName(proProject.getProjectName());
			category.setDescription(proProject.getContent());
			categoryService.save(category);
			proProject.setCategory(category);
		}
		if(menu!=null) {
			menu.setParent(systemService.getMenu(Menu.getRootId()));
			menu.setName(proProject.getProjectName());
			menu.setRemarks(proProject.getContent());
			menu.setImgUrl(proProject.getImgUrl());
			systemService.saveMenu(menu);
			proProject.setMenu(menu);
		}
		//是否变换菜单
		if(proProject.isRestMenu()){
			Menu menuOld =systemService.getMenu(proProject.getMenuRid());
		  //删除菜单
			if(menuOld!=null) {
				systemService.deleteMenu(menuOld);
			}
			createMenu(proProject);
			if(actYw.getGroupId()!=null){
				ActYwGnode actYwGnode=new ActYwGnode();
				actYwGnode.setGroupId(actYw.getGroupId());
				List<ActYwGnode> sourcelist = actYwGnodeService.findListByYwProcess(actYwGnode);
				if(sourcelist.size()>0){
					for(int i=0;i<sourcelist.size();i++) {
						if(sourcelist.get(i)!=null){
							Menu menuForm = new Menu();
							menuForm.setParent(proProject.getMenu());
							menuForm.setName(sourcelist.get(i).getNode().getName());
							menuForm.setIsShow("1");
							menuForm.setHref("form/"+proProject.getProjectMark()+"/"+sourcelist.get(i).getFormId()+"?id="+ actYw.getId());
							menuForm.setSort(10);
							systemService.saveMenu(menuForm);

							Menu menuNextForm = new Menu();
							menuNextForm.setParent(menuForm);
							menuNextForm.setName(sourcelist.get(i).getNode().getName());
							menuNextForm.setIsShow("1");
							menuNextForm.setHref("cms/form/"+proProject.getProjectMark()+"/"+sourcelist.get(i).getFormId()+"?id="+ actYw.getId());
							menuNextForm.setSort(10);
							systemService.saveMenu(menuNextForm);
						}
					}
				}
			}
		}
		//是否变换栏目
		if(proProject.isRestCategory()){
			Category categoryOld =categoryService.get(proProject.getCategoryRid());
		  //删除栏目
			if(categoryOld!=null) {
				categoryService.delete(categoryOld);
			}
			createMenu(proProject);
		}
	}
	//创建菜单
	@Transactional(readOnly = false)
	public void createMenu(ProProject proProject) {
		Menu menu=new Menu();
		menu.setParent(systemService.getMenu(Menu.getRootId()));
		menu.setName(proProject.getProjectName());
		menu.setIsShow("1");
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
		category.setInMenu("1");
		category.setInList("1");
		category.setIsAudit("0");
		category.setSort(40);
		categoryService.save(category);
		//默认添加申报表单
		Category categoryapp=new Category();

		categoryapp.setParent(category);
		categoryapp.setSite(category.getSite());
		categoryapp.setOffice(category.getOffice());
		categoryapp.setName(proProject.getProjectName());
		categoryapp.setDescription(proProject.getContent());
		categoryapp.setInMenu("1");
		categoryapp.setInList("1");
		categoryapp.setIsAudit("0");
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
			category.setInMenu("0");
			category.setName(proProject.getProjectName());
			category.setDescription(proProject.getContent());
			categoryService.save(category);
			proProject.setCategory(category);
		}
		if(menu!=null) {
			menu.setIsShow("0");
			menu.setParent(systemService.getMenu(Menu.getRootId()));
			menu.setName(proProject.getProjectName());
			menu.setRemarks(proProject.getContent());
			menu.setImgUrl(proProject.getImgUrl());
			systemService.saveMenu(menu);
			proProject.setMenu(menu);
		}

	}

	@Transactional(readOnly = false)
	public void saveProProject(ActYw actYw, HttpServletRequest request)  {
		ProProject proProject = actYw.getProProject();
		if(proProject != null && StringUtil.isNotEmpty(proProject.getProjectName())){
			/**
			 * 生成前台栏目.
			 */
			createCategory(proProject,actYw);
			/**
			 * 生成后台菜单.
			 */
			createMenu(proProject);
			//根据流程生成子菜单
			if(actYw.getGroupId()!=null){
				ActYwGnode actYwGnode=new ActYwGnode();
				actYwGnode.setGroupId(actYw.getGroupId());
				List<ActYwGnode> sourcelist = actYwGnodeService.findListByYwProcess(actYwGnode);
				if(sourcelist.size()>0){
					for(int i=0;i<sourcelist.size();i++) {
						if(sourcelist.get(i)!=null){
							Menu menuForm = new Menu();
							menuForm.setParent(proProject.getMenu());
							menuForm.setName(sourcelist.get(i).getNode().getName());
							menuForm.setIsShow("1");
							menuForm.setHref("form/"+proProject.getProjectMark()+"/"+sourcelist.get(i).getFormId()+"?id="+ actYw.getId());
							menuForm.setSort(10);
							systemService.saveMenu(menuForm);

							Menu menuNextForm = new Menu();
							menuNextForm.setParent(menuForm);
							menuNextForm.setName(sourcelist.get(i).getNode().getName());
							menuNextForm.setIsShow("1");
							menuNextForm.setHref("/cms/form/"+proProject.getProjectMark()+"/"+sourcelist.get(i).getFormId()+"?id="+ actYw.getId());
							menuNextForm.setSort(10);
							systemService.saveMenu(menuNextForm);
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