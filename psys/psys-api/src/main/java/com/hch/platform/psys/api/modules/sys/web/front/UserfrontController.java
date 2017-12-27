package com.hch.platform.pcore.modules.sys.web.front;

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
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.oa.entity.OaNotify;
import com.hch.platform.pcore.modules.oa.service.OaNotifyService;
import com.hch.platform.pcore.modules.sys.entity.Role;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.SystemService;
import com.hch.platform.pcore.modules.sys.service.UserService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.service.TeamService;

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
	public AbsUser get(@RequestParam(required=false) String id) {
		if (StringUtil.isNotBlank(id)) {
			AbsUser user = systemService.getUser(id);
			return user;
		}else{
			return new AbsUser();
		}
	}

	@RequestMapping(value = {"index"})
	public String index(AbsUser user, Model model,HttpServletRequest request) {
		String teamId = request.getParameter("teamId");
		String opType = request.getParameter("opType");
		String userType = request.getParameter("userType");
		model.addAttribute("teamId", teamId);
		model.addAttribute("opType", opType);
		model.addAttribute("userType", userType);
		model.addAttribute("user", user);
		return "modules/sys/userIndex";

	}


	@RequestMapping(value = {"indexPublish"})
	public String indexPublish(AbsUser user, Model model,HttpServletRequest request) {
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
		List<AbsUser> list = systemService.findUserByOfficeId(officeId);
		for (int i=0; i<list.size(); i++) {
			AbsUser e = list.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", "u_"+e.getId());
			map.put("pId", officeId);
			map.put("name", StringUtil.replace(e.getName(), " ", ""));
			mapList.add(map);
		}
		return mapList;
	}

	@RequestMapping("userListTree")
	public String userListTree(AbsUser user, String grade, String professionId,String allTeacher,HttpServletRequest request, HttpServletResponse response, Model model) {
		String userType = request.getParameter("userType");
		String teacherType = request.getParameter("teacherType");
		String userName = request.getParameter("userName");
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

    Page<AbsUser> page = null;
    if(StringUtil.isNotEmpty(userType)){
      user.setUserType(userType);

      if((userType).equals("1")){
        page = systemService.findListTreeByStudent(new Page<AbsUser>(request, response), user);
      }else if((userType).equals("2")){
        page = systemService.findListTreeByTeacher(new Page<AbsUser>(request, response), user);
      }else{
        page = systemService.findListTreeByUser(new Page<AbsUser>(request, response), user);
      }
    }

		model.addAttribute("page", page);
		model.addAttribute("userType", userType);
		return "modules/sys/userListTree";
	}


	@RequestMapping("userListTreePublish")
	public String userListTreePublish(AbsUser user, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userType = request.getParameter("userType");
    Page<AbsUser> page = null;
    if(StringUtil.isNotEmpty(userType)){
      user.setUserType(userType);
      if((userType).equals("1")){
        page = systemService.findListTreeByStudent(new Page<AbsUser>(request, response), user);
      }else if((userType).equals("2")){
        page = systemService.findListTreeByTeacher(new Page<AbsUser>(request, response), user);
      }else{
        page = systemService.findListTreeByUser(new Page<AbsUser>(request, response), user);
      }
    }

		if (page!=null) {
			List<AbsUser> userList = page.getList();
			if (userList!=null&&userList.size()>0) {
				for(AbsUser usertmp:userList) {
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
	public String checkLoginName(String loginName,String userid) {
		if (userService.getByLoginNameOrNo(loginName, userid)== null) {
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
	public String checkNo(String no,String userid) {
		if (userService.getByLoginNameOrNo(no, userid)== null) {
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
		AbsUser userForSearch=new AbsUser();
		userForSearch.setMobile(mobile);
		AbsUser cuser=UserUtils.getUser();
		if(cuser==null||StringUtil.isEmpty(cuser.getId())){
			return false;
		}
		userForSearch.setId(cuser.getId());
		AbsUser user = userService.getByMobileWithId(userForSearch);
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
	public boolean uploadFTP(HttpServletRequest request,AbsUser user) {
		String arrUrl = request.getParameter("arrUrl");
		if (user!=null) {
			user.setPhoto(arrUrl);
			userService.updateUser(user);
		}
		return true;
	}
	@ResponseBody
	@RequestMapping(value="checkUserInfoPerfect")
	public boolean checkUserInfoPerfect() {
		if(UserUtils.checkInfoPerfect(UserUtils.getUser())){
			return true;
		}else{
			return false;
		}
	}


}
