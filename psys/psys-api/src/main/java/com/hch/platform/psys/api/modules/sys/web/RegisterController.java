package com.hch.platform.pcore.modules.sys.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hch.platform.pcore.common.config.SysIds;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.pcore.common.utils.sms.SMSUtilAlidayu;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.sys.entity.Role;
import com.hch.platform.pcore.modules.sys.entity.User;
import com.hch.platform.pcore.modules.sys.security.MyUsernamePasswordToken;
import com.hch.platform.pcore.modules.sys.service.SystemService;
import com.hch.platform.pcore.modules.sys.service.UserService;

/**
 * 注册Controller
 * @author zhang
 * @version 2017-3-22
 */

@Controller
@RequestMapping(value = "${frontPath}/register")
public class RegisterController extends BaseController {

	@Autowired
	private UserService userService;

	@Autowired
	private SystemService systemService;

	/**
	 * 通过手机验证用户是否已存在
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/validatePhone")
	public Boolean validatePhone(HttpServletRequest request) {
		String mobile =  request.getParameter("mobile");
		User user = new User();
		user.setMobile(mobile);
		user.setDelFlag("0");
		user = userService.getByMobile(user);
		if (user!=null) {
			return false;
		}
		return true;
	}
	/**
	 * 获取短信验证码
	 * @param mobile
	 */
	@ResponseBody
	@RequestMapping(value = "/getVerificationCode")
	public String getVerificationCode(HttpServletRequest request) {
		String mobile = request.getParameter("mobile");
		String sm = SMSUtilAlidayu.sendSms(mobile);
//		CacheUtils.put("hqyazhenma", sm);
		CacheUtils.put("hqyazhenma"+request.getSession().getId(), sm);
		return sm;
	}

	/**
	 * 验证验证码是否输入正确
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="validateYZM")
	public int validateYZM(HttpServletRequest request) {
		String sruyazhengma = request.getParameter("yzma");
//		String hqyazhengma = (String) CacheUtils.get("hqyazhenma");
		String hqyazhengma = (String) CacheUtils.get("hqyazhenma"+request.getSession().getId());
		if (sruyazhengma.equals(hqyazhengma)) {
			return 1;
		}
		return 0;

	}

	/**
	 * 保存用户
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/saveRegister")
	public String register(User user,Model model,HttpServletRequest request, HttpServletResponse response) {
		String pw = user.getPassword();
		String mobile = user.getMobile();
		String password = user.getPassword();
		password = SystemService.entryptPassword(password);
		user = new User();
		user.setMobile(mobile);
		user.setPassword(password);
		user.setLoginName(mobile);
		//user.setName(mobile);
		user.setCreateBy(user);
		Date date = new Date();
		user.setCreateDate(date);
		user.setUpdateBy(user);
		user.setUpdateDate(date);
		user.setDelFlag("0");
		user.setUserType("1");
		List<Role> roleList=new ArrayList<Role>();
		roleList.add(new Role(SysIds.SYS_ROLE_USER.getId()));
		user.setRoleList(roleList);
		systemService.saveUser(user);
		model.addAttribute("name",mobile);
		try {
			Subject subject = SecurityUtils.getSubject();
			MyUsernamePasswordToken token=new MyUsernamePasswordToken();
			token.setUsername(mobile);
			token.setPassword(pw.toCharArray());
			token.setLoginType("2");
			subject.login(token);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "modules/website/studentregistersuccessful";

	}

	@RequestMapping(value="/studentExpanSion")
	public String studentExpanSion() {
		return "modules/website/studentregistersuccess";

	}

}
