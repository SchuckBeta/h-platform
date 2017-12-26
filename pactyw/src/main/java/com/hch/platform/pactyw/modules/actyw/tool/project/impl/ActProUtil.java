package com.oseasy.initiate.modules.actyw.tool.project.impl;

import com.hch.platform.pconfig.common.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.common.utils.StringUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
@Service
@Transactional(readOnly = true)
public class ActProUtil {
	public static final String ACT_REMARKS = "act预约流程";
	@Autowired
	CategoryService categoryService ;//= (CategoryService) SpringContextHolder.getBean(CategoryService.class);
	@Autowired
	SystemService systemService ;//= (SystemService) SpringContextHolder.getBean(SystemService.class);
	@Autowired
	ActYwGnodeService actYwGnodeService ;//= (ActYwGnodeService) SpringContextHolder.getBean(ActYwGnodeService.class);

	public Boolean dealMenu(ActProParamVo actProParamVo, String menuId) {
		ProProject proProject = actProParamVo.getProProject();
		ActYw actYw = actProParamVo.getActYw();
		if (proProject != null) {
			Menu menu = systemService.getMenu(menuId);
			if (actYw.getGroupId() != null) {
				Role sAdminRole = systemService.getRole(SysIds.SYS_ADMIN_ROLE.getId());
				ActYwGnode actYwGnode = new ActYwGnode();
				actYwGnode.setGroupId(actYw.getGroupId());
				List<ActYwGnode> sourcelist = actYwGnodeService.findListByYwProcess(actYwGnode);
				Menu menuForm = new Menu();
				menuForm.setParent(menu);
				menuForm.setName(proProject.getProjectName());
				menuForm.setIsShow(Global.SHOW);
				menuForm.setHref("/auditstandard/index?ywid=" + actYw.getId());
				//menuForm.setHref("form/" + proProject.getProjectMark() + "/" + sourcelist.get(i).getFormId() + "?id=" + actYw.getId());
				menuForm.setSort(10);
				menuForm.setRemarks(ACT_REMARKS);
				systemService.saveMenu(menuForm);

				proProject.setMenu(menuForm);
				proProject.setMenuRid(menuForm.getId());
				actYw.setProProject(proProject);
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
									"&gnodeId=" + sourcelist.get(i).getId()
							);
							menuNextForm.setRemarks(ACT_REMARKS);
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
										if (!roleMenuList.contains(menuForm)) {
											roleMenuList.add(menuForm);
										}
										if (!roleMenuList.contains(menuNextForm)) {
											roleMenuList.add(menuNextForm);
										}
										role.setMenuList(roleMenuList);
										systemService.saveRole(role);
									}

									if (sAdminRole != null) {
										List<Menu> roleMenuList = sAdminRole.getMenuList();
										if (!roleMenuList.contains(menuForm)) {
											roleMenuList.add(menuForm);
										}
										if (!roleMenuList.contains(menuNextForm)) {
											roleMenuList.add(menuNextForm);
										}
										sAdminRole.setMenuList(roleMenuList);
										systemService.saveRole(sAdminRole);
									}
								}
							}
						}
					}
				}
				//添加查询表单
				Menu menuQueryForm = new Menu();
				menuQueryForm.setParent(menuForm);
				menuQueryForm.setName("查询列表");
				menuQueryForm.setIsShow(Global.SHOW);
				menuQueryForm.setSort(30);
				menuQueryForm.setHref("/cms/form/queryMenuList/?actywId=" + actYw.getId());
				menuQueryForm.setRemarks(ACT_REMARKS);
				systemService.saveMenu(menuQueryForm);
				if (sAdminRole != null) {
					List<Menu> roleMenuList = sAdminRole.getMenuList();
					if (!roleMenuList.contains(menuForm)) {
						roleMenuList.add(menuForm);
					}
					if (!roleMenuList.contains(menuQueryForm)) {
						roleMenuList.add(menuQueryForm);
					}
					sAdminRole.setMenuList(roleMenuList);
					systemService.saveRole(sAdminRole);
				}
			}
		}
		return true;
	}



	public Boolean dealMenu1(ActProParamVo actProParamVo, String menuId) {
		ProProject proProject = actProParamVo.getProProject();
		ActYw actYw = actProParamVo.getActYw();
		if (proProject != null) {
			if (actYw.getGroupId() != null) {
				Role sAdminRole = systemService.getRole(SysIds.SYS_ADMIN_ROLE.getId());
				ActYwGnode actYwGnode = new ActYwGnode();
				actYwGnode.setGroupId(actYw.getGroupId());
				List<ActYwGnode> sourcelist = actYwGnodeService.findListByYwProcess(actYwGnode);
				Menu menuForm = systemService.getMenu(menuId);

				actYw.setProProject(proProject);
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
									"&gnodeId=" + sourcelist.get(i).getId()
							);
							menuNextForm.setRemarks(ACT_REMARKS);
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
										if (!roleMenuList.contains(menuForm)) {
											roleMenuList.add(menuForm);
										}
										if (!roleMenuList.contains(menuNextForm)) {
											roleMenuList.add(menuNextForm);
										}
										role.setMenuList(roleMenuList);
										systemService.saveRole(role);
									}

									if (sAdminRole != null) {
										List<Menu> roleMenuList = sAdminRole.getMenuList();
										if (!roleMenuList.contains(menuForm)) {
											roleMenuList.add(menuForm);
										}
										if (!roleMenuList.contains(menuNextForm)) {
											roleMenuList.add(menuNextForm);
										}
										sAdminRole.setMenuList(roleMenuList);
										systemService.saveRole(sAdminRole);
									}
								}
							}
						}
					}
				}
			}
		}
		return true;
	}


	public Boolean dealCategory(ActProParamVo actProParamVo, String cateId, String checkName) {
		ProProject proProject = actProParamVo.getProProject();
		ActYw actYw = actProParamVo.getActYw();
		//修改栏目为可见
		Category category = categoryService.get(cateId);
		//默认添加申报表单
		Category categoryapp = new Category();
		categoryapp.setParent(category);
		categoryapp.setSite(category.getSite());
		categoryapp.setOffice(category.getOffice());
		categoryapp.setName(proProject.getProjectName());
		categoryapp.setDescription(proProject.getContent());
		categoryapp.setInMenu(Global.SHOW);
		categoryapp.setInList(Global.SHOW);
		categoryapp.setIsAudit(Global.NO);
		if (StringUtil.isEmpty(checkName)) {
			categoryapp.setHref("/cms/form/" + proProject.getProjectMark() + "/applyForm?actywId=" + actYw.getId());
		} else {
			categoryapp.setHref("javascript:" + checkName + "('/cms/form/" + proProject.getProjectMark() + "/applyForm','" + actYw.getId() + "');");
		}
		categoryapp.setSort(10);
		categoryapp.setRemarks(ACT_REMARKS);
		categoryService.save(categoryapp);
		proProject.setCategory(categoryapp);
		proProject.setCategoryRid(categoryapp.getId());
		actYw.setProProject(proProject);
		return true;
	}

	public Boolean dealTime(ActProParamVo actProParamVo) {
		return true;
	}

	public Boolean dealIcon(ActProParamVo actProParamVo) {
		return true;
	}

	public Boolean dealActYw(ActProParamVo actProParamVo) {
		return true;
	}

	public Boolean dealDeploy(ActProParamVo actProParamVo) {
		return true;
	}

	public Boolean requireMenu() {
		return false;
	}

	public Boolean requireCategory() {
		return false;
	}

	public Boolean requireTime() {
		return true;
	}

	public Boolean requireIcon() {
		return false;
	}

	public Boolean requireActYw() {
		return true;
	}

	public Boolean requireDeploy() {
		return true;
	}
}
