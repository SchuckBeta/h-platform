package com.oseasy.initiate.modules.sys.web.front;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.modules.oa.dao.OaNotifyRecordDao;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
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
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.service.TeamService;

@Controller
@RequestMapping(value = "${frontPath}/sys/user")
public class UserfrontController extends BaseController {
	@Autowired
	private SystemService systemService;
	@Autowired
	private OaNotifyService oaNotifyService;
	@Autowired
	private TeamService teamService;
	@Autowired
	UserService userService;

	@ModelAttribute
	public User get(@RequestParam(required=false) String id) {
		if (StringUtil.isNotBlank(id)) {
			User user = systemService.getUser(id);
			return user;
		}else{
			return new User();
		}
	}

	@RequestMapping(value = {"index"})
	public String index(User user, Model model,HttpServletRequest request) {
		String teamId = request.getParameter("teamId");
		String opType = request.getParameter("opType");
		String userType = request.getParameter("userType");
		model.addAttribute("teamId", teamId);
		model.addAttribute("opType", opType);
		model.addAttribute("userType", userType);
		return "modules/sys/userIndex";

	}


	@RequestMapping(value = {"indexPublish"})
	public String indexPublish(User user, Model model,HttpServletRequest request) {
		String teamId = request.getParameter("teamId");
		String opType = request.getParameter("opType");
		String userType = request.getParameter("userType");
		model.addAttribute("teamId", teamId);
		model.addAttribute("opType", opType);
		model.addAttribute("userType", userType);
		return "modules/sys/userIndexPublish";

	}


	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String officeId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<User> list = systemService.findUserByOfficeId(officeId);
		for (int i=0; i<list.size(); i++) {
			User e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", "u_"+e.getId());
			map.put("pId", officeId);
			map.put("name", StringUtil.replace(e.getName(), " ", ""));
			mapList.add(map);
		}
		return mapList;
	}
	@RequestMapping("userListTree")
	public String userListTree(User user,String grade,String professionId,String allTeacher,HttpServletRequest request, HttpServletResponse response, Model model) {
		String userType = request.getParameter("userType");
		String teacherType = request.getParameter("teacherType");
		logger.info("=========userType:"+userType);
		logger.info("grade:"+grade);
		logger.info("teacherType:"+teacherType);
		if (StringUtil.isNotBlank(teacherType)) {
			user.setTeacherType(teacherType);
			model.addAttribute("teacherType", teacherType);
		}
		if ("1".equals(allTeacher)) {
			user.setTeacherType(null);
		}
		user.setUserType(userType);
		if (StringUtil.isNotBlank(grade)&&"3".equals(grade)) {
			user.setProfessional(professionId);
		}
		Page<User> page = systemService.findListTree(new Page<User>(request, response), user);
      /* if (page!=null) {
    	   List<User> userList = page.getList();
    	   if (userList!=null&&userList.size()>0) {
    		   for(User usertmp:userList) {
    			   List<Role> roleList = systemService.findListByUserId(usertmp.getId());
    			   usertmp.setRoleList(roleList);
    		   }
    	   }
       }

        List<Role>  roleList = systemService.findAllRole();

        model.addAttribute("roleList",roleList);*/

		model.addAttribute("page", page);
		model.addAttribute("userType", userType);
		//	return "modules/sys/userList";
		return "modules/sys/userListTree";
	}


	@RequestMapping("userListTreePublish")
	public String userListTreePublish(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userType = request.getParameter("userType");
		logger.info("=========userType:"+userType);
		user.setUserType(userType);
		Page<User> page = systemService.findListTree(new Page<User>(request, response), user);
		if (page!=null) {
			List<User> userList = page.getList();
			if (userList!=null&&userList.size()>0) {
				for(User usertmp:userList) {
					List<Role> roleList = systemService.findListByUserId(usertmp.getId());
					usertmp.setRoleList(roleList);
				}
			}
		}

		List<Role>  roleList = systemService.findAllRole();

		model.addAttribute("roleList",roleList);

		model.addAttribute("page", page);
		model.addAttribute("userType", userType);
		//	return "modules/sys/userList";
		return "modules/sys/userListTreePublish";
	}


	@RequestMapping(value = "delete")
	public String delete(OaNotify oaNotify, RedirectAttributes redirectAttributes) {
		oaNotifyService.delete(oaNotify);
		addMessage(redirectAttributes, "删除发送通知成功");
		return "redirect:" + frontPath + "/sys/user/indexMySendNoticeList/?repage";
	}



	@ResponseBody
	@RequestMapping(value = "checkMobile")
	public String checkMobile(@RequestParam(value="user.mobile")String mobile,@RequestParam(value="id")String id) {
		if (mobile !=null && systemService.getUserByMobile(mobile,id) == null) {
			return "true";
		}
		return "false";
	}
	/**
	 * 检查loginName 登录名不能与其他人的登录名相同，不能与其他人的no相同
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@RequestMapping(value = "checkLoginName")
	@ResponseBody
	public String checkLoginName(String oldLoginName, String loginName,String oldNo) {
		if ( loginName.equals(oldLoginName)||loginName.equals(oldNo)) { //与旧loginName可以相同，与旧学号可以相同
			return "true";
		} else if (userService.getByLoginNameOrNo(loginName) == null) {
			return "true";
		}
		return "false";
	}


	/**
	 * 检查学号，学号不能与其他人的学号相同，不能与其他人的loginName相同
	 * @param oldNo
	 * @param no
	 * @return
	 */
	@RequestMapping(value = "checkNo")
	@ResponseBody
	public String checkNo(String oldNo, String no,String oldLoginName) {
		if (no.equals(oldNo)||no.equals(oldLoginName)) { //与旧学号可以相同，与旧loginName可以相同
			return "true";
		} else if (userService.getByLoginNameOrNo(no) == null) {
			return "true";
		}
		return "false";
	}

	/**
	 * addby zhangzheng 检查输入的手机号是否已经注册过
	 * @param mobile
	 * @return true:没注册，允许修改
	 */
	@RequestMapping("checkMobileExist")
	@ResponseBody
	public Boolean checkMobileExist(String mobile) {
		User userForSearch=new User();
		userForSearch.setMobile(mobile);
		User user = userService.getByMobile(userForSearch);
		if (user==null) {
			return true;
		}else{
			return false;
		}

	}


	@ResponseBody
	@RequestMapping(value = "ifTeamNameExist")
	public String ifTeamNameExist(String name,String teamId) {
		logger.info("name:"+name);
		logger.info("teamId:"+teamId);
		List<Team> teamList  = teamService.selectTeamByName(name);
		if (teamList !=null && teamList.size()>0) {
			if (StringUtil.isNotBlank(teamId)) {
				if (teamId.equals(teamList.get(0).getId())) {
					return  "true";
				}
			}
			return "false";
		}
		return "true";
	}

	@ResponseBody
	@RequestMapping(value="uploadPhoto")
	public boolean uploadFTP(HttpServletRequest request,User user) {
		String arrUrl = request.getParameter("arrUrl");
		if (user!=null) {
			user.setPhoto(arrUrl);
			userService.updateUser(user);
		}
		return true;
	}



}
