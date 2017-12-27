/**
 *
 */
package com.hch.platform.pcore.modules.sys.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.pcore.modules.authorize.service.AbsAuthorizeService;
import com.hch.platform.pcore.modules.sys.dao.MenuDao;
import com.hch.platform.pcore.modules.sys.dao.RoleDao;
import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.security.AbsSystemAuthorizingRealm.Principal;
import com.hch.platform.putil.common.utils.StringUtil;

/**
 * 用户工具类


 */
public class UserUtils {
	private static AbsAuthorizeService authorizeService = SpringContextHolder.getBean(AbsAuthorizeService.class);
	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);

	private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
	private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);

	public static final String USER_CACHE = "userCache";
	public static final String USER_CACHE_ID_ = "id_";
	public static final String USER_CACHE_LOGIN_NAME_ = "ln";
	public static final String USER_CACHE_LIST_BY_OFFICE_ID_ = "oid_";

	public static final String CACHE_AUTH_INFO = "authInfo";
	public static final String CACHE_ROLE_LIST = "roleList";
	public static final String CACHE_MENU_LIST = "menuList";
	public static final String CACHE_AREA_LIST = "areaList";
	public static final String CACHE_OFFICE_LIST = "officeList";
	public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";
	public static final String CACHE_PROJECT_ANNOUNCE = "projectAnnouceList";
	public static final String CACHE_OANOTIFY_LIST = "oaNotifyList";
	public static final String CACHE_OFFICE = "office";

	/**根据编号判断授权信息
	 * @param num MenuPlusEnum 枚举值序号从0开始
	 * @return
	 */
	public static boolean checkMenuByNum(Integer num){
		return authorizeService.checkMenuByNum(num);
	}
	public static boolean checkMenu(String id){
		return authorizeService.checkMenu(id);
	}
	public static boolean checkChildMenu(String id){
		return authorizeService.checkChildMenu(id);
	}
	public static boolean checkCategory(String id){
		return authorizeService.checkCategory(id);
	}
	public static String hiddenMobile(String mobile){
		if(StringUtil.isEmpty(mobile)){
			return mobile;
		}
		return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
	}

	public static AbsUser getByLoginNameOrNo(String loginNameOrNo) {
		AbsUser user = (AbsUser)CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginNameOrNo);
		if (user == null) {
			user = userDao.getByLoginNameOrNo(loginNameOrNo,null);
			if (user == null) {
				return null;
			}
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}


	public static boolean isExistNo(String no) {
		AbsUser	user = userDao.getByLoginNameOrNo(no,null);
		if (user == null) {
			return false;
		}
		return true;
	}

	/**
	 * 清除当前用户缓存
	 */
	public static void clearCache() {
		removeCache(CACHE_AUTH_INFO);
		removeCache(CACHE_ROLE_LIST);
		removeCache(CACHE_MENU_LIST);
		removeCache(CACHE_AREA_LIST);
		removeCache(CACHE_OFFICE_LIST);
		removeCache(CACHE_OFFICE_ALL_LIST);
		removeCache(CACHE_OANOTIFY_LIST);
	}

	/**
	 * 获取授权主要对象
	 */
	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	/**
	 * 获取当前登录者对象
	 */
	public static Principal getPrincipal() {
		try{
			Subject subject = SecurityUtils.getSubject();
			Principal principal = (Principal)subject.getPrincipal();
			if (principal != null) {
				return principal;
			}
//			subject.logout();
		}catch (UnavailableSecurityManagerException e) {

		}catch (InvalidSessionException e) {

		}
		return null;
	}

	public static Session getSession() {
		try{
			Subject subject = SecurityUtils.getSubject();
			Session session = subject.getSession(false);
			if (session == null) {
				session = subject.getSession();
			}
			if (session != null) {
				return session;
			}
//			subject.logout();
		}catch (InvalidSessionException e) {

		}
		return null;
	}

	// ============== User Cache ==============

	public static Object getCache(String key) {
		return getCache(key, null);
	}

	public static Object getCache(String key, Object defaultValue) {
//		Object obj = getCacheMap().get(key);
		Object obj = getSession().getAttribute(key);
		return obj==null?defaultValue:obj;
	}

	public static void putCache(String key, Object value) {
//		getCacheMap().put(key, value);
		getSession().setAttribute(key, value);
	}

	public static void removeCache(String key) {
//		getCacheMap().remove(key);
		getSession().removeAttribute(key);
	}

//	public static Map<String, Object> getCacheMap() {
//		Principal principal = getPrincipal();
//		if (principal!=null) {
//			return principal.getCacheMap();
//		}
//		return new HashMap<String, Object>();
//	}

	/**
	 * 跳转登录页面.
	 * @return String
	 */
	public static String toLogin() {
	  return "redirect:" + Global.getFrontPath() + "/toLogin";
	}
}
