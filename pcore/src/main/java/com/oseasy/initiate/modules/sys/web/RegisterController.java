package com.oseasy.initiate.modules.sys.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.common.utils.sms.SMSUtilAlidayu;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.sms.SMSUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.security.MyUsernamePasswordToken;
import com.oseasy.initiate.modules.sys.service.StudentExpansionService;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;

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
	@Autowired
	private StudentExpansionService studentExpansionService;

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
		password = systemService.entryptPassword(password);
		user = new User();
		user.setId(IdGen.uuid());
		user.setMobile(mobile);
		user.setPassword(password);
		user.setLoginName(mobile);
		user.setName(mobile);
		user.setCreateBy(user);
		Date date = new Date();
		user.setCreateDate(date);
		user.setUpdateBy(user);
		user.setUpdateDate(date);
		user.setDelFlag("0");
		user.setUserType("1");
		userService.saveUser(user);
		model.addAttribute("name",mobile);

		StudentExpansion studentExpansion = new StudentExpansion();
		studentExpansion.setUser(user);
		studentExpansion.setIsOpen("1");
		studentExpansionService.save(studentExpansion);
		try {
			Subject subject = SecurityUtils.getSubject();
			MyUsernamePasswordToken token=new MyUsernamePasswordToken();
			token.setUsername(mobile);
			token.setPassword(pw.toCharArray());
			token.setLoginType("2");
			subject.login(token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "modules/website/studentregistersuccessful";

	}

	@RequestMapping(value="/studentExpanSion")
	public String studentExpanSion() {
		return "modules/website/studentregistersuccess";

	}

}
