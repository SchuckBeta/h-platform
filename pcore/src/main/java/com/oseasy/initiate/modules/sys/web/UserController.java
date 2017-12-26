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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.utils.excel.ExportExcel;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.sys.entity.BackTeacherExpansion;
import com.oseasy.initiate.modules.sys.entity.Dict;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.BackTeacherExpansionService;
import com.oseasy.initiate.modules.sys.service.OfficeService;
import com.oseasy.initiate.modules.sys.service.StudentExpansionService;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.dao.TeamDao;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamUserHistoryService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

/**
 * 用户Controller
 * 
 * @version 2013-8-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

	@Autowired
	private SystemService systemService;
	@Autowired
	private OfficeService officeService;
	@Autowired
	UserService userService;
	@Autowired
	StudentExpansionService studentExpansionService;
	@Autowired
	TeamUserRelationService teamUserRelationService;
	@Autowired
	private TeamUserHistoryService teamUserHistoryService;
	@Autowired
	BackTeacherExpansionService backTeacherExpansionService;

	@Autowired
	private TeamDao teamDao;

	@ModelAttribute
	public User get(@RequestParam(required = false) String id) {
		if (StringUtil.isNotBlank(id)) {
			User user = systemService.getUser(id);
			if ("2".equals(user.getUserType())) {
				String teacherType = systemService.getTeacherTypeByUserId(id);
				user.setTeacherType(teacherType);
			}
			return user;
		} else {
			return new User();
		}
	}

	// @RequiresPermissions("sys:user:view")
	@RequestMapping(value = { "index" })
	public String index(User user, Model model) {

		return "modules/sys/userIndex";

	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = { "list", "" })
	public String list(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);
		if (page != null) {
			List<User> userList = page.getList();
			if (userList != null && userList.size() > 0) {
				for (User usertmp : userList) {
					List<Role> roleList = systemService.findListByUserId(usertmp.getId());
					usertmp.setRoleList(roleList);
				}
			}
		}

		List<Role> roleList = systemService.findAllRole();

		model.addAttribute("roleList", roleList);

		model.addAttribute("page", page);
		// return "modules/sys/userList";
		return "modules/sys/userListReDefine";
	}

	// @RequiresPermissions("sys:user:view")
	@RequestMapping("userListTree")
	public String userListTree(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);
		if (page != null) {
			List<User> userList = page.getList();
			if (userList != null && userList.size() > 0) {
				for (User usertmp : userList) {
					List<Role> roleList = systemService.findListByUserId(usertmp.getId());
					usertmp.setRoleList(roleList);
				}
			}
		}

		List<Role> roleList = systemService.findAllRole();

		model.addAttribute("roleList", roleList);

		model.addAttribute("page", page);
		// return "modules/sys/userList";
		return "modules/sys/userListTree";
	}

	@RequestMapping("backUserListTree")
	public String backUserListTree(User user, String grade, String professionId, String allTeacher,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		String userType = request.getParameter("userType");
		String teacherType = request.getParameter("teacherType");
		String userName = request.getParameter("userName");
		String ids = request.getParameter("ids");
		if (StringUtil.isNotBlank(userName)) {
			user.setIds(ids.split(","));
			model.addAttribute("ids", ids);
		}
		if (StringUtil.isNotBlank(userName)) {
			user.setName(userName);
			model.addAttribute("userName", userName);
		}
		if (StringUtil.isNotBlank(teacherType)) {
			user.setTeacherType(teacherType);
			model.addAttribute("teacherType", teacherType);
		}
		if ("1".equals(allTeacher)) {
			user.setTeacherType(null);
		}
		if (StringUtil.isNotBlank(grade) && "3".equals(grade)) {
			user.setProfessional(professionId);
		}

		Page<User> page = null;
		if (StringUtil.isNotEmpty(userType)) {
			user.setUserType(userType);

			if ((userType).equals("1")) {
				page = systemService.findListTreeByStudent(new Page<User>(request, response), user);
			} else if ((userType).equals("2")) {
				page = systemService.findListTreeByTeacher(new Page<User>(request, response), user);
			} else {
				page = systemService.findListTreeByUser(new Page<User>(request, response), user);
			}
		}

		model.addAttribute("page", page);
		model.addAttribute("userType", userType);
		return "modules/sys/backUserListTree";
	}

	// @RequiresPermissions("sys:user:view")
	@RequestMapping("userQyListTree")
	public String userQyListTree(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);
		if (page != null) {
			List<User> userList = page.getList();
			if (userList != null && userList.size() > 0) {
				for (User usertmp : userList) {
					List<Role> roleList = systemService.findListByUserId(usertmp.getId());
					usertmp.setRoleList(roleList);
				}
			}
		}
		List<Role> roleList = systemService.findAllRole();
		model.addAttribute("roleList", roleList);
		model.addAttribute("page", page);
		// return "modules/sys/userList";
		return "modules/sys/userListTree";
	}

	@ResponseBody
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = { "listData" })
	public Page<User> listData(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<User> page = systemService.findUser(new Page<User>(request, response), user);
		return page;
	}

	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "form")
	public String form(User user, Model model) {
		if (user.getCompany() == null || user.getCompany().getId() == null) {
			user.setCompany(UserUtils.getUser().getCompany());
		}
		model.addAttribute("user", user);
		model.addAttribute("allRoles", systemService.findAllRole());

		List<Dict> dictList = DictUtils.getDictList("technology_field");
		model.addAttribute("allDomains", dictList);

		return "modules/sys/userForm";
	}

	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "save")
	public String save(User user, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if (Global.isDemoMode()) {
			addMessage(redirectAttributes, "演示模式，不允许操作！");
			return "redirect:" + adminPath + "/sys/user/list?repage";
		}
		// 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
		// user.setCompany(new Office(request.getParameter("company.id")));
		user.setOffice(new Office(request.getParameter("office.id")));
		String companyId = officeService.selelctParentId(user.getOffice().getId());
		user.setCompany(new Office());
		user.getCompany().setId((StringUtil.isNotEmpty(companyId)) ? companyId : SysIds.SYS_OFFICE_TOP.getId());

		// 如果新密码为空，则不更换密码
		if (StringUtil.isNotBlank(user.getNewPassword())) {
			user.setPassword(SystemService.entryptPassword(user.getNewPassword()));
		}
		if (!beanValidator(model, user)) {
			return form(user, model);
		}
		if (StringUtil.isNotEmpty(user.getId())) {// 修改时有加入的团队
			List<Team> tel = teamDao.findTeamListByUserId(user.getId());
			User old = UserUtils.get(user.getId());
			if (old != null && StringUtil.isNotEmpty(old.getId())) {
				if (tel != null && tel.size() > 0 && old.getUserType() != null
						&& !old.getUserType().equals(user.getUserType())) {// 用户类型变化了
					addMessage(model, "保存失败，该用户已加入团队，不能修改用户类型");
					return form(user, model);
				}
			}
		}
		if (StringUtil.isNotEmpty(user.getId()) && teamUserHistoryService.getBuildingCountByUserId(user.getId()) > 0) {// 修改时有正在进行的项目大赛
			User old = UserUtils.get(user.getId());
			if (old != null && StringUtil.isNotEmpty(old.getId())) {
				if (old.getUserType() != null && !old.getUserType().equals(user.getUserType())) {// 用户类型变化了
					addMessage(model, "保存失败，该用户有正在进行的项目或大赛，不能修改用户类型");
					return form(user, model);
				} else if (old.getUserType() != null && old.getUserType().equals(user.getUserType())
						&& "2".equals(old.getUserType())) {// 导师类型
					BackTeacherExpansion bte = backTeacherExpansionService.getByUserId(old.getId());
					if (bte != null && bte.getTeachertype() != null
							&& !bte.getTeachertype().equals(user.getTeacherType())) {// 导师类型的用户导师来源发生变化
						addMessage(model, "保存失败，该用户有正在进行的项目或大赛，不能修改导师来源");
						return form(user, model);
					}
				}
			}
		}

		if (!"true".equals(checkLoginName(user.getLoginName(), user.getId()))) {
			addMessage(model, "保存用户'" + user.getLoginName() + "'失败，登录名已存在");
			return form(user, model);
		}
		// 角色数据有效性验证，过滤不在授权内的角色
		List<Role> roleList = Lists.newArrayList();
		List<String> roleIdList = user.getRoleIdList();
		for (Role r : systemService.findAllRole()) {
			if (roleIdList.contains(r.getId())) {
				roleList.add(r);
			}
		}
		user.setRoleList(roleList);
		// 保存用户信息
		// logger.info("============user.domain:"+user.getDomain());
		systemService.saveUser(user);
		// 清除当前用户缓存
		if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
			UserUtils.clearCache();
			// UserUtils.getCacheMap().clear();
		}
		// addMessage(redirectAttributes, "保存用户'" + user.getLoginName() +
		// "'成功");
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "delete")
	public String delete(User user, RedirectAttributes redirectAttributes) {
		if (UserUtils.getUser().getId().equals(user.getId())) {
			addMessage(redirectAttributes, "删除用户失败, 不允许删除当前用户");
		} else if (User.getAdmin(user.getId())) {
			addMessage(redirectAttributes, "删除用户失败, 不允许删除超级管理员用户");
		} else {
			// 删除对应的学生信息
			if (StringUtil.equals("1", user.getUserType())) {
				TeamUserRelation teamUserRelation = new TeamUserRelation();
				StudentExpansion studentExpansion = studentExpansionService.getByUserId(user.getId());
				teamUserRelation.setUser(user);
				teamUserRelation = teamUserRelationService.findUserById(teamUserRelation);
				if (teamUserRelation != null) {
					addMessage(redirectAttributes, "该学生已加入团队，不能删除!");
					return "redirect:" + adminPath + "/sys/user/list?repage";
				}
				studentExpansionService.delete(studentExpansion);
			}
			// 删除对应的老师信息
			if (StringUtil.equals("2", user.getUserType())) {
				TeamUserRelation teamUserRelation = new TeamUserRelation();
				teamUserRelation.setUser(user);
				teamUserRelation = teamUserRelationService.findUserById(teamUserRelation);
				if (teamUserRelation != null) {
					addMessage(redirectAttributes, "该导师已加入团队，不能删除!");
					return "redirect:" + adminPath + "/sys/user/list?repage";
				}
				BackTeacherExpansion backTeacherExpansion = backTeacherExpansionService
						.findTeacherByUserId(user.getId());
				backTeacherExpansionService.delete(backTeacherExpansion);
			}

			systemService.deleteUser(user); // 删除用户

			addMessage(redirectAttributes, "删除用户成功");
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 导出用户数据
	 * 
	 * @param user
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "export", method = RequestMethod.POST)
	public String exportFile(User user, HttpServletRequest request, HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		try {
			String fileName = "用户数据" + DateUtil.getDate("yyyyMMddHHmmss") + ".xlsx";
			Page<User> page = systemService.findUser(new Page<User>(request, response, -1), user);
			new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出用户失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 导入用户数据
	 * 
	 * @param file
	 * @param redirectAttributes
	 * @return
	 */
	/*
	 * @RequiresPermissions("sys:user:edit")
	 * 
	 * @RequestMapping(value = "import", method=RequestMethod.POST) public
	 * String importFile(MultipartFile file, RedirectAttributes
	 * redirectAttributes) { if (Global.isDemoMode()) {
	 * addMessage(redirectAttributes, "演示模式，不允许操作！"); return "redirect:" +
	 * adminPath + "/sys/user/list?repage"; } try { int successNum = 0; int
	 * failureNum = 0; StringBuilder failureMsg = new StringBuilder();
	 * ImportExcel ei = new ImportExcel(file, 1, 0); List<User> list =
	 * ei.getDataList(User.class); for (User user : list) { try{ if
	 * ("true".equals(checkLoginName("", user.getLoginName(),""))) {
	 * user.setPassword(SystemService.entryptPassword("123456"));
	 * BeanValidators.validateWithException(validator, user);
	 * systemService.saveUser(user); successNum++; }else{ failureMsg.append(
	 * "<br/>登录名 "+user.getLoginName()+" 已存在; "); failureNum++; }
	 * }catch(ConstraintViolationException ex) { failureMsg.append("<br/>登录名 "
	 * +user.getLoginName()+" 导入失败："); List<String> messageList =
	 * BeanValidators.extractPropertyAndMessageAsList(ex, ": "); for (String
	 * message : messageList) { failureMsg.append(message+"; "); failureNum++; }
	 * }catch (Exception ex) { failureMsg.append("<br/>登录名 "
	 * +user.getLoginName()+" 导入失败："+ex.getMessage()); } } if (failureNum>0) {
	 * failureMsg.insert(0, "，失败 "+failureNum+" 条用户，导入信息如下："); }
	 * addMessage(redirectAttributes, "已成功导入 "+successNum+" 条用户"+failureMsg); }
	 * catch (Exception e) { addMessage(redirectAttributes,
	 * "导入用户失败！失败信息："+e.getMessage()); } return "redirect:" + adminPath +
	 * "/sys/user/list?repage"; }
	 */

	/**
	 * 下载导入用户数据模板
	 * 
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("sys:user:view")
	@RequestMapping(value = "import/template")
	public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String fileName = "用户数据导入模板.xlsx";
			List<User> list = Lists.newArrayList();
			list.add(UserUtils.getUser());
			new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
			return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/sys/user/list?repage";
	}

	/**
	 * 检查loginName 登录名不能与其他人的登录名相同，不能与其他人的no相同
	 * 
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sys:user:edit")
	@RequestMapping(value = "checkLoginName")
	public String checkLoginName(String loginName, String userid) {
		if (userService.getByLoginNameOrNo(loginName, userid) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 用户信息显示及保存
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "info")
	public String info(User user, HttpServletResponse response, Model model) {
		User currentUser = UserUtils.getUser();
		if (StringUtil.isNotBlank(user.getName())) {
			if (Global.isDemoMode()) {
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userInfo";
			}
			currentUser.setEmail(user.getEmail());
			currentUser.setPhone(user.getPhone());
			currentUser.setMobile(user.getMobile());
			currentUser.setRemarks(user.getRemarks());
			currentUser.setPhoto(user.getPhoto());
			systemService.updateUserInfo(currentUser);
			model.addAttribute("message", "保存用户信息成功");
		}
		model.addAttribute("user", currentUser);
		model.addAttribute("Global", new Global());
		return "modules/sys/userInfo";
	}

	/**
	 * 返回用户信息
	 * 
	 * @return
	 */
	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "infoData")
	public User infoData() {
		return UserUtils.getUser();
	}

	/**
	 * 修改个人用户密码
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @param model
	 * @return
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "modifyPwd")
	public String modifyPwd(String oldPassword, String newPassword, Model model) {
		User user = UserUtils.getUser();
		if (StringUtil.isNotBlank(oldPassword) && StringUtil.isNotBlank(newPassword)) {
			if (Global.isDemoMode()) {
				model.addAttribute("message", "演示模式，不允许操作！");
				return "modules/sys/userModifyPwd";
			}
			if (SystemService.validatePassword(oldPassword, user.getPassword())) {
				systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
				model.addAttribute("message", "修改密码成功");
			} else {
				model.addAttribute("message", "修改密码失败，旧密码错误");
			}
		}
		model.addAttribute("user", user);
		return "modules/sys/userModifyPwd";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required = false) String officeId,
			HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<User> list = systemService.findUserByOfficeId(officeId);
		for (int i = 0; i < list.size(); i++) {
			User e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", "u_" + e.getId());
			map.put("pId", officeId);
			map.put("name", StringUtil.replace(e.getName(), " ", ""));
			mapList.add(map);
		}
		return mapList;
	}

	@ResponseBody
	@RequestMapping(value = "checkMobile")
	public String checkMobile(@RequestParam(value = "mobile") String mobile, @RequestParam(value = "id") String id) {
		if (mobile != null && systemService.getUserByMobile(mobile, id) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * 检查学号，学号不能与其他人的学号相同，不能与其他人的loginName相同
	 * 
	 * @param oldNo
	 * @param no
	 * @return
	 */
	@RequestMapping(value = "checkNo")
	@ResponseBody
	public String checkNo(String no, String userid) {
		if (userService.getByLoginNameOrNo(no, userid) == null) {
			return "true";
		}
		return "false";
	}

	@ResponseBody
	@RequestMapping(value = "uploadPhoto")
	public boolean uploadFTP(HttpServletRequest request, User user) {
		String arrUrl = request.getParameter("arrUrl");
		if (user != null) {
			user.setPhoto(arrUrl);
			userService.updateUser(user);
		}
		return true;
	}

	/**
	 * 修复学生导师用户没有角色.
	 * 
	 * @param roleId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "repaireStudentRole/{rid}")
	public ActYwRstatus<List<String>> repaireStudentRole(@PathVariable("rid") String rid) {
		if (StringUtil.isNotEmpty(rid)) {
			return systemService.insertPLUserRole(rid, userService.findUserByRepair());
		}
		return new ActYwRstatus<List<String>>(false, "修复失败,角色ID为空!");
	}

}
