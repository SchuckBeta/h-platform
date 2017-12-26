package com.oseasy.initiate.modules.actyw.tool.project.impl;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.tool.project.ActProParamVo;
import com.oseasy.initiate.modules.actyw.tool.project.IActProDeal;
import com.oseasy.initiate.modules.cms.entity.Category;
import com.oseasy.initiate.modules.cms.service.CategoryService;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.sys.entity.Menu;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.service.SystemService;

/**
 * Created by Administrator on 2017/7/29 0029.
 */

public class ActProProject  implements IActProDeal {
	//@Autowired
	// CategoryService categoryService;

	CategoryService categoryService = (CategoryService) SpringContextHolder.getBean(CategoryService.class);
	//@Autowired
	//SystemService systemService;
	SystemService systemService = (SystemService) SpringContextHolder.getBean(SystemService.class);
	//@Autowired
	//ActYwGnodeService actYwGnodeService;
	ActYwGnodeService actYwGnodeService = (ActYwGnodeService) SpringContextHolder.getBean(ActYwGnodeService.class);

	@Override
	@Transactional(readOnly = false)
	public Boolean dealMenu(ActProParamVo actProParamVo) {
		ProProject proProject =actProParamVo.getProProject();
		ActYw actYw =actProParamVo.getActYw();
		if(proProject != null){
			Role sAdminRole = systemService.getRole("f9c12c05add2409dac0fcdec9387e63c");
			Menu  menu = new Menu();
			menu.setParent(systemService.getMenu(Menu.getRootId()));
			menu.setName(proProject.getProjectName());
			menu.setImgUrl(proProject.getImgUrl());
			menu.setIsShow(Global.SHOW);
			menu.setRemarks(proProject.getContent());
			menu.setHref("/auditstandard/index?ywid="+actYw.getId());
			menu.setSort(10);
			menu.setRemarks(ActProModel.ACT_REMARKS);
			systemService.saveMenu(menu);
			proProject.setMenu(menu);
			proProject.setMenuRid(menu.getId());
			actYw.setProProject(proProject);
			if (actYw.getGroupId() != null) {
				ActYwGnode actYwGnode = new ActYwGnode();
				actYwGnode.setGroupId(actYw.getGroupId());
				List<ActYwGnode> sourcelist = actYwGnodeService.findListByYwProcess(actYwGnode);
				Menu menuForm = new Menu();
				menuForm.setParent(menu);
				menuForm.setName(menu.getName());
				menuForm.setIsShow(Global.SHOW);
				//menuForm.setHref("form/" + proProject.getProjectMark() + "/" + sourcelist.get(i).getFormId() + "?id=" + actYw.getId());
				menuForm.setSort(10);
				menuForm.setRemarks(ActProModel.ACT_REMARKS);
				systemService.saveMenu(menuForm);

				if (sourcelist.size() > 0) {
					for (int i = 0; i < sourcelist.size(); i++) {
						ActYwGnode actYwGnodeIndex = sourcelist.get(i);
						if (actYwGnodeIndex != null) {
							Menu menuNextForm = new Menu();
							menuNextForm.setParent(menuForm);
							menuNextForm.setName(actYwGnodeIndex.getName());
							menuNextForm.setIsShow(Global.SHOW);
							menuNextForm.setSort(10 + i);
							menuNextForm.setHref("/cms/form/" + proProject.getProjectMark() + "/" + actYwGnodeIndex.getFormId() +
									"?actywId=" + actYw.getId() +
									"&gnodeId=" +  sourcelist.get(i).getId()
							);
							menuNextForm.setRemarks("act流程菜单");
							systemService.saveMenu(menuNextForm);
							ActYwGnode actYwGnodePar = new ActYwGnode();
							actYwGnodePar.setParentIds(sourcelist.get(i).getId());
							actYwGnodePar.setNextIdss(null);
							actYwGnodePar.setPreIdss(null);
							List<ActYwGnode> ActYwGnodes = actYwGnodeService.findList(actYwGnodePar);
							for (int j = 0; j < ActYwGnodes.size(); j++) {
								//给相应角色赋权访问
								String roleId = ActYwGnodes.get(j).getFlowGroup();
								if (roleId != null) {
									Role role = systemService.getRole(roleId);
									if (role != null) {
										List<Menu> roleMenuList = role.getMenuList();
										if(!roleMenuList.contains(menuForm)){
											roleMenuList.add(menuForm);
										}
										if(!roleMenuList.contains(menuNextForm)){
											roleMenuList.add(menuNextForm);
										}
										role.setMenuList(roleMenuList);
										systemService.saveRole(role);
									}
								}
								
								if (sAdminRole != null) {
									List<Menu> roleMenuList = sAdminRole.getMenuList();
									if(!roleMenuList.contains(menuForm)){
										roleMenuList.add(menuForm);
									}
									if(!roleMenuList.contains(menuNextForm)){
										roleMenuList.add(menuNextForm);
									}
									sAdminRole.setMenuList(roleMenuList);
									systemService.saveRole(sAdminRole);
								}
							}
						}
					}
				}
				//添加查询表单
				Menu menuQueryForm = new Menu();
				menuQueryForm.setParent(menuForm);
				menuQueryForm.setName("项目查询列表");
				menuQueryForm.setIsShow(Global.SHOW);
				menuQueryForm.setSort(30);
				menuQueryForm.setHref("/cms/form/queryMenuList/?actywId=" + actYw.getId());
				menuQueryForm.setRemarks(ActProModel.ACT_REMARKS);
				systemService.saveMenu(menuQueryForm);
				if (sAdminRole != null) {
					List<Menu> roleMenuList = sAdminRole.getMenuList();
					if(!roleMenuList.contains(menuForm)){
						roleMenuList.add(menuForm);
					}
					if(!roleMenuList.contains(menuQueryForm)){
						roleMenuList.add(menuQueryForm);
					}
					sAdminRole.setMenuList(roleMenuList);
					systemService.saveRole(sAdminRole);
				}
			}
		}

		return true;
	}

	@Override
	@Transactional(readOnly = false)
	public Boolean dealCategory(ActProParamVo actProParamVo) {
		ProProject proProject =actProParamVo.getProProject();
		ActYw actYw =actProParamVo.getActYw();
		//修改栏目为可见
		Category  category = new Category();
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
		category.setRemarks(ActProModel.ACT_REMARKS);
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
		categoryapp.setHref("/cms/form/"+proProject.getProjectMark()+"/applyForm?actywId="+actYw.getId());
		categoryapp.setSort(10);
		categoryapp.setRemarks(ActProModel.ACT_REMARKS);
		categoryService.save(categoryapp);
		proProject.setCategory(category);
		proProject.setCategoryRid(category.getId());
		actYw.setProProject(proProject);
		return true;
	}

  @Override
  public Boolean dealTime(ActProParamVo actProParamVo) {
    return true;
  }

  @Override
  public Boolean dealIcon(ActProParamVo actProParamVo) {
    return true;
  }

  @Override
  public Boolean dealActYw(ActProParamVo actProParamVo) {
    return true;
  }

  @Override
  public Boolean dealDeploy(ActProParamVo actProParamVo) {
    return true;
  }

  @Override
  public Boolean requireMenu() {
    return true;
  }

  @Override
  public Boolean requireCategory() {
    return true;
  }

  @Override
  public Boolean requireTime() {
    return true;
  }

  @Override
  public Boolean requireIcon() {
    return true;
  }

  @Override
  public Boolean requireActYw() {
    return true;
  }

  @Override
  public Boolean requireDeploy() {
    return true;
  }
}
