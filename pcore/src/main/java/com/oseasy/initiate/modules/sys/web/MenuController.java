/**
 *
 */
package com.oseasy.initiate.modules.sys.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIdx;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.authorize.service.AuthorizeService;
import com.oseasy.initiate.modules.ftp.service.FtpService;
import com.oseasy.initiate.modules.sys.entity.Menu;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 菜单Controller


 */
@Controller
@RequestMapping(value = "${adminPath}/sys/menu")
public class MenuController extends BaseController {

	@Autowired
	private AuthorizeService authorizeService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private FtpService ftpService;
	@ModelAttribute("menu")
	public Menu get(@RequestParam(required=false) String id) {
		if (StringUtil.isNotBlank(id)) {
			return systemService.getMenu(id);
		}else{
			return new Menu();
		}
	}

	@RequiresPermissions("sys:menu:view")
	@RequestMapping(value = {"list", ""})
	public String list(Model model) {
		List<Menu> list = Lists.newArrayList();
		List<Menu> sourcelist = systemService.findAllMenu();
		Menu.sortList(list, sourcelist, Menu.getRootId(), true);
        model.addAttribute("list", list);
		return "modules/sys/menuList";
	}

	@RequiresPermissions("sys:menu:view")
	@RequestMapping(value = "form")
	public String form(Menu menu, Model model) {
		if (menu.getParent()==null||menu.getParent().getId()==null) {
			menu.setParent(new Menu(Menu.getRootId()));
		}
		menu.setParent(systemService.getMenu(menu.getParent().getId()));
		// 获取排序号，最末节点排序号+30
		if (StringUtil.isBlank(menu.getId())) {
			List<Menu> list = Lists.newArrayList();
			List<Menu> sourcelist = systemService.findAllMenu();
			Menu.sortList(list, sourcelist, menu.getParentId(), false);
			if (list.size() > 0) {
				menu.setSort(list.get(list.size()-1).getSort() + 30);
			}
		}
		model.addAttribute("menu", menu);
		return "modules/sys/menuForm";
	}

	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "save")
	public String save(Menu menu, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		if (!(UserUtils.getUser().getAdmin() || UserUtils.getUser().getSysAdmin())) {
			addMessage(redirectAttributes, "越权操作，只有超级管理员才能添加或修改数据！");
			return "redirect:" + adminPath + "/sys/role/?repage";
		}
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/menu/";
		}
		if (!beanValidator(model, menu)) {
			return form(menu, model);
		}
		try {
			String arrUrl= request.getParameter("arrUrl");
			if (arrUrl!=null&&!"".equals(arrUrl)) {
				if (menu.getImgUrl()!=null) {
					ftpService.del(menu.getImgUrl());
				}
					String img= FtpUtil.moveFile(FtpUtil.getftpClient(),arrUrl);
					menu.setImgUrl(img);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		systemService.saveMenu(menu);
		addMessage(redirectAttributes, "保存菜单'" + menu.getName() + "'成功");
		return "redirect:" + adminPath + "/sys/menu/";
	}

	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "delete")
	public String delete(Menu menu, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/menu/";
		}
//		if (Menu.isRoot(id)) {
//			addMessage(redirectAttributes, "删除菜单失败, 不允许删除顶级菜单或编号为空");
//		}else{
			systemService.deleteMenu(menu);
			addMessage(redirectAttributes, "删除菜单成功");
//		}
		return "redirect:" + adminPath + "/sys/menu/";
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "tree")
	public String tree() {
		return "modules/sys/menuTree";
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "treePlus")
	public String treePlus(String parentId,Model model,String href) {
		if(!authorizeService.checkMenu(parentId)){
			return "redirect:/a/authorize";
		}
		List<Menu> list = Lists.newArrayList();
		List<Menu> sourcelist = systemService.findAllMenu();
		Menu.sortList(list, sourcelist, parentId, true);
		//没有下级菜单
		if (list.size()==0) {
			model.addAttribute("msg","无权限访问该页面");
			return "error/msg";
		}
		Menu firstMenu = systemService.getMenu(parentId);
		List<Menu> secondMenus=Lists.newArrayList();
		List<Menu> threeMenus=Lists.newArrayList();

		for (Menu menu:list) {
			if (menu.getParent().getId().equals(parentId)) {
				if (StringUtil.equals("1",menu.getIsShow())) {
					secondMenus.add(menu);
				}
			}else{
				threeMenus.add(menu);
			}
		}
		for (Menu menu2:secondMenus) {
			List<Menu> children=Lists.newArrayList();
			for (Menu menu3:threeMenus) {
				if (menu3.getParent().getId().equals(menu2.getId())) {
					if (StringUtil.equals("1",menu3.getIsShow())) {
						children.add(menu3);
					}

				}
			}
			menu2.setChildren(children);
		}

	    if(StringUtil.isNotEmpty(firstMenu.getHref()) && StringUtil.isNotEmpty((firstMenu.getHref()).replaceAll(" ", ""))){
	      model.addAttribute("hasHome", true);
	    }else{
	      model.addAttribute("hasHome", false);
	    }
	    model.addAttribute("firstMenu", firstMenu);
		model.addAttribute("secondMenus",secondMenus);

		model.addAttribute("href",href); //addBy张正，根据href跳转。如果href不为空，跳转到指定href
		return SysIdx.SYSIDX_BACK_V4.getIdxUrl();
	}

	@RequiresPermissions("user")
	@RequestMapping(value = "treeselect")
	public String treeselect(String parentId, Model model) {
		model.addAttribute("parentId", parentId);
		return "modules/sys/menuTreeselect";
	}

	/**
	 * 批量修改菜单排序
	 */
	@RequiresPermissions("sys:menu:edit")
	@RequestMapping(value = "updateSort")
	public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/menu/";
		}
    	for (int i = 0; i < ids.length; i++) {
    		Menu menu = new Menu(ids[i]);
    		menu.setSort(sorts[i]);
    		systemService.updateMenuSort(menu);
    	}
    	addMessage(redirectAttributes, "保存菜单排序成功!");
		return "redirect:" + adminPath + "/sys/menu/";
	}

	/**
	 * isShowHide是否显示隐藏菜单
	 * @param extId
	 * @param isShowHidden 是否显示
	 * @param response
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId,@RequestParam(required=false) String isShowHide, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<Menu> list = systemService.findAllMenu();
		for (int i=0; i<list.size(); i++) {
			Menu e = list.get(i);
			if (StringUtil.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)) {
				if (isShowHide != null && isShowHide.equals("0") && e.getIsShow().equals("0")) {
					continue;
				}
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
}
