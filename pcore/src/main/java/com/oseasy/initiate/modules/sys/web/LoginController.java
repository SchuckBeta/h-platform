/**
 *
 */
package com.oseasy.initiate.modules.sys.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.security.shiro.session.SessionDAO;
import com.oseasy.initiate.common.servlet.ValidateCodeServlet;
import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.CookieUtils;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.sys.security.AdminFormAuthenticationFilter;
import com.oseasy.initiate.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 登录Controller
 * @author ThinkGem
 * @version 2013-5-31
 */
@Controller
public class LoginController extends BaseController{

	@Autowired
	private SessionDAO sessionDAO;

	@RequestMapping(value = "${adminPath}/sysMenuIndex", method = RequestMethod.GET)
	public String sysMenuIndex(HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/sys/sysMenuIndex";
	}

	@RequestMapping(value = "${adminPath}/sysOldIndex", method = RequestMethod.GET)
	public String sysOldIndex(HttpServletRequest request, HttpServletResponse response, Model model) {
	  return "modules/sys/sysIndex";
	}

	/**
	 * 管理登录
	 */
	@RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
		Principal principal = UserUtils.getPrincipal();

//		// 默认页签模式
//		String tabmode = CookieUtils.getCookie(request, "tabmode");
//		if (tabmode == null) {
//			CookieUtils.setCookie(response, "tabmode", "1");
//		}

		if (logger.isDebugEnabled()) {
			logger.debug("login, active session size: {}", sessionDAO.getActiveSessions(false).size());
		}

		// 如果已登录，再次访问主页，则退出原账号。
		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))) {
			CookieUtils.setCookie(response, "LOGINED", "false");
		}

		// 如果已经登录，则跳转到管理首页
		if (principal != null && !principal.isMobileLogin()) {
			return "redirect:" + adminPath;
		}
//		String view;
//		view = "/WEB-INF/views/modules/sys/sysLogin.jsp";
//		view = "classpath:";
//		view += "jar:file:/D:/GitHub/initiate/src/main/webapp/WEB-INF/lib/initiate.jar!";
//		view += "/"+getClass().getName().replaceAll("\\.", "/").replace(getClass().getSimpleName(), "")+"view/sysLogin";
//		view += ".jsp";
		return "modules/sys/sysLogin";
	}

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	@RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
	public String loginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
		Principal principal = UserUtils.getPrincipal();

		// 如果已经登录，则跳转到管理首页
		if (principal != null) {
			return "redirect:" + adminPath;
		}

		String username = WebUtils.getCleanParam(request, AdminFormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
		boolean rememberMe = WebUtils.isTrue(request, AdminFormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
		boolean mobile = WebUtils.isTrue(request, AdminFormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
		String exception = (String)request.getAttribute(AdminFormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
		String message = (String)request.getAttribute(AdminFormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);

		if (StringUtil.isBlank(message) || StringUtil.equals(message, "null")) {
			message = "用户或密码错误, 请重试.";
		}

		model.addAttribute(AdminFormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
		model.addAttribute(AdminFormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
		model.addAttribute(AdminFormAuthenticationFilter.DEFAULT_MOBILE_PARAM, mobile);
		model.addAttribute(AdminFormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, exception);
		model.addAttribute(AdminFormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);

		if (logger.isDebugEnabled()) {
			logger.debug("login fail, active session size: {}, message: {}, exception: {}",
					sessionDAO.getActiveSessions(false).size(), message, exception);
		}

		// 非授权异常，登录失败，验证码加1。
		if (!UnauthorizedException.class.getName().equals(exception)) {
			model.addAttribute("isValidateCodeLogin", isValidateCodeLogin(username, true, false));
		}

		// 验证失败清空验证码
		request.getSession().setAttribute(ValidateCodeServlet.VALIDATE_CODE, IdGen.uuid());

		// 如果是手机登录，则返回JSON字符串
		if (mobile) {
	        return renderString(response, model);
		}

		return "modules/sys/sysLogin";
	}

	/**
	 * 登录成功，进入管理首页
	 */
	@RequiresPermissions("user")
	@RequestMapping(value = "${adminPath}")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		Principal principal = UserUtils.getPrincipal();

		// 登录成功后，验证码计算器清零
		isValidateCodeLogin(principal.getLoginName(), false, true);

		if (logger.isDebugEnabled()) {
			logger.debug("show index, active session size: {}", sessionDAO.getActiveSessions(false).size());
		}

		// 如果已登录，再次访问主页，则退出原账号。
		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))) {
			String logined = CookieUtils.getCookie(request, "LOGINED");
			if (StringUtil.isBlank(logined) || "false".equals(logined)) {
				CookieUtils.setCookie(response, "LOGINED", "true");
			}else if (StringUtil.equals(logined, "true")) {
				UserUtils.getSubject().logout();
				return "redirect:" + adminPath + "/login";
			}
		}

		// 如果是手机登录，则返回JSON字符串
		if (principal.isMobileLogin()) {
			if (request.getParameter("login") != null) {
				return renderString(response, principal);
			}
			if (request.getParameter("index") != null) {
				return "modules/sys/sysMenuIndex";
			}
			return "redirect:" + adminPath + "/login";
		}

//		// 登录成功后，获取上次登录的当前站点ID
//		UserUtils.putCache("siteId", StringUtil.toLong(CookieUtils.getCookie(request, "siteId")));

//		System.out.println("==========================a");
//		try {
//			byte[] bytes = com.oseasy.initiate.common.utils.FileUtils.readFileToByteArray(
//					com.oseasy.initiate.common.utils.FileUtils.getFile("c:\\sxt.dmp"));
//			UserUtils.getSession().setAttribute("kkk", bytes);
//			UserUtils.getSession().setAttribute("kkk2", bytes);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
////		for (int i=0; i<1000000; i++) {
////			//UserUtils.getSession().setAttribute("a", "a");
////			request.getSession().setAttribute("aaa", "aa");
////		}
//		System.out.println("==========================b");
		return "modules/sys/sysMenuIndex";
	}

	/**
	 * 获取主题方案
	 */
	@RequestMapping(value = "/theme/{theme}")
	public String getThemeInCookie(@PathVariable String theme, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtil.isNotBlank(theme)) {
			CookieUtils.setCookie(response, "theme", theme);
//		}else{
//			theme = CookieUtils.getCookie(request, "theme");
		}
		return "redirect:"+request.getParameter("url");
	}

	/**
	 * 是否是验证码登录
	 * @param useruame 用户名
	 * @param isFail 计数加1
	 * @param clean 计数清零
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean) {
		Integer loginFailNum =(Integer)CacheUtils.get("loginFailMap", useruame);
		if (loginFailNum==null) {
			loginFailNum = 0;
		}
		if (isFail) {
			loginFailNum++;
			CacheUtils.put("loginFailMap", useruame, loginFailNum);
		}
		if (clean) {
			CacheUtils.remove("loginFailMap", useruame);
		}
		return loginFailNum >= 3;
	}
}
