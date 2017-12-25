/**
 *
 */
package com.oseasy.initiate.modules.sys.utils;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.SerialUtils;
import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.oa.dao.OaNotifyDao;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.project.dao.ProjectAnnounceDao;
import com.oseasy.initiate.modules.project.entity.ProjectAnnounce;
import com.oseasy.initiate.modules.sys.dao.AreaDao;
import com.oseasy.initiate.modules.sys.dao.MenuDao;
import com.oseasy.initiate.modules.sys.dao.OfficeDao;
import com.oseasy.initiate.modules.sys.dao.RoleDao;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.Area;
import com.oseasy.initiate.modules.sys.entity.Menu;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.SysNo;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.oseasy.initiate.modules.sys.service.SysNoService;

/**
 * 用户工具类


 */
public class UserUtils {

	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
	private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
	private static AreaDao areaDao = SpringContextHolder.getBean(AreaDao.class);
	private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);
	private static ProjectAnnounceDao projectAnnounceDao = SpringContextHolder.getBean(ProjectAnnounceDao.class);
	private static OaNotifyDao oaNotifyDao = SpringContextHolder.getBean(OaNotifyDao.class);
	private static SysNoService sysNoService = SpringContextHolder.getBean(SysNoService.class);

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

	/**
	 * 根据ID获取用户
	 * @param id
	 * @return 取不到返回null
	 */
	public static User get(String id) {
	User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_ID_ + id);
		if (user ==  null) {
			user = userDao.get(id);
			if (user == null) {
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}

	/**
	 * 根据登录名获取用户
	 * @param loginName
	 * @return 取不到返回null
	 */
	public static User getByLoginName(String loginName) {
		User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginName);
		if (user == null) {
			user = userDao.getByLoginName(new User(null, loginName));
			if (user == null) {
				return null;
			}
			user.setRoleList(roleDao.findList(new Role(user)));
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}

	public static User getByLoginNameOrNo(String loginNameOrNo) {
		User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_ + loginNameOrNo);
		if (user == null) {
			user = userDao.getByLoginNameOrNo(loginNameOrNo);
			if (user == null) {
				return null;
			}
			CacheUtils.put(USER_CACHE, USER_CACHE_ID_ + user.getId(), user);
			CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName(), user);
		}
		return user;
	}


	public static boolean isExistNo(String no) {
		User	user = userDao.getByLoginNameOrNo(no);
		if (user == null) {
			return false;
		}
		return true;
	}
	/**
	 * 根据手机号获取用户
	 * @param mobile
	 * @return 取不到返回null
	 */
	public static boolean isExistMobile(String mobile) {
		User p=new User();
		p.setMobile(mobile);
		User	user = userDao.getByMobile(p);
		if (user == null) {
			return false;
		}
		return true;
	}
	public static User getByMobile(String mobile) {
		User p=new User();
		p.setMobile(mobile);
		User	user = userDao.getByMobile(p);
		if (user == null) {
			return null;
		}
		user.setRoleList(roleDao.findList(new Role(user)));
		return user;
	}
	public static User getByMobile(String mobile,String id) {
		User p=new User();
		p.setMobile(mobile);
		p.setId(id);
		User	user = userDao.getByMobileWithId(p);
		if (user == null) {
			return null;
		}
		user.setRoleList(roleDao.findList(new Role(user)));
		return user;
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
		UserUtils.clearCache(getUser());
	}

	/**
	 * 清除指定用户缓存
	 * @param user
	 */
	public static void clearCache(User user) {
		CacheUtils.remove(USER_CACHE, USER_CACHE_ID_ + user.getId());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getLoginName());
		CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
		if (user.getOffice() != null && user.getOffice().getId() != null) {
			CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_ + user.getOffice().getId());
		}
	}

	/**
	 * 获取当前用户
	 * @return 取不到返回 new User()
	 */
	public static User getUser() {
		Principal principal = getPrincipal();
		if (principal!=null) {
			User user = get(principal.getId());
			if (user != null) {
				return user;
			}
			return new User();
		}
		// 如果没有登录，则返回实例化空的User对象。
		return new User();
	}

	/**
	 * 获取当前用户角色列表
	 * @return
	 */
	public static List<Role> getRoleList() {
		@SuppressWarnings("unchecked")
		List<Role> roleList = (List<Role>)getCache(CACHE_ROLE_LIST);
		if (roleList == null) {
			User user = getUser();
			if (user.getAdmin()) {
				roleList = roleDao.findAllList(new Role());
			}else{
				Role role = new Role();
				//role.getSqlMap().put("dsf", BaseService.dataScopeFilter(user.getCurrentUser(), "o", "u"));
				roleList = roleDao.findList(role);
			}
			putCache(CACHE_ROLE_LIST, roleList);
		}
		return roleList;
	}


	/**
	 * 获取专业名称
	 * @return
	 */
	public static String getProfessional(String id) {
		Office office = officeDao.get(id);
		if (office!=null) {
			return office.getName();
		}
		return null;
	}

	/**
	 * 获取当前用户授权菜单
	 * @return
	 */
	public static List<Menu> getMenuList() {
		@SuppressWarnings("unchecked")
		List<Menu> menuList = (List<Menu>)getCache(CACHE_MENU_LIST);
		if (menuList == null) {
			User user = getUser();
			if (user.getAdmin()) {
				menuList = menuDao.findAllList(new Menu());
			}else{
				Menu m = new Menu();
				m.setUserId(user.getId());
				menuList = menuDao.findByUserId(m);
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}

	/**
	 * 获取当前所有用户授权菜单
	 * @return
	 */
	public static List<Menu> getAllMenuList() {
		List<Menu> menuList = menuDao.findAllList(new Menu());
		return menuList;
	}

	/**
	 * 获取当前用户授权的区域
	 * @return
	 */
	public static List<Area> getAreaList() {
		@SuppressWarnings("unchecked")
		List<Area> areaList = (List<Area>)getCache(CACHE_AREA_LIST);
		if (areaList == null) {
			areaList = areaDao.findAllList(new Area());
			putCache(CACHE_AREA_LIST, areaList);
		}
		return areaList;
	}

	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeList() {
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_LIST);
		if (officeList == null) {
			User user = getUser();
			if (user.getAdmin()) {
				officeList = officeDao.findAllList(new Office());
			}else{
				Office office = new Office();
				//office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
				officeList = officeDao.findList(office);
			}
			putCache(CACHE_OFFICE_LIST, officeList);
		}
		return officeList;
	}
	//得到学院
	public static List<Office> findColleges() {
		return  officeDao.findColleges();
	}
	//根据学院id 得到其下面的专业
	public static List<Office> findProfessionals(String parentId) {
		return  officeDao.findProfessionals(parentId);
	}

	public static Office getAdminOffice() {
		Office office = (Office)CacheUtils.get(CACHE_OFFICE, SysIds.SYS_OFFICE_TOP.getId());
		if (office == null) {
			office = officeDao.get(SysIds.SYS_OFFICE_TOP.getId());
			if (office!=null)CacheUtils.put(CACHE_OFFICE, SysIds.SYS_OFFICE_TOP.getId(),office);
		}
		return office;
	}

	public static Office getOffice(String ofid) {
	  if (StringUtil.isNotEmpty(ofid)) {
	    Office office = (Office)CacheUtils.get(CACHE_OFFICE,ofid);
	    if (office == null) {
	      office=officeDao.get(ofid);
	      if (office!=null)CacheUtils.put(CACHE_OFFICE,ofid,office);
	    }
	    return office;
	  }
	  return null;
	}

	public static List<Office> getOfficeListFront() {
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache("officeListFront");
		if (officeList == null) {
			User user = getUser();
			/*if (user.isAdmin()) {*/
				officeList = officeDao.findAllList(new Office());
			/*}else{
				Office office = new Office();
			//	office.getSqlMap().put("dsf", BaseService.dataScopeFilter(user, "a", ""));
				officeList = officeDao.findList(office);
			}*/
			putCache("officeListFront", officeList);
		}
		return officeList;
	}


	/**
	 * 获取当前用户有权限访问的部门
	 * @return
	 */
	public static List<Office> getOfficeAllList() {
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_ALL_LIST);
		if (officeList == null) {
			officeList = officeDao.findAllList(new Office());
		}
		return officeList;
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

	/**
	 * 获取当前项目列表
	 * @return
	 */
	public static Page<ProjectAnnounce> getProjectAnnouceList(Page<ProjectAnnounce> page, ProjectAnnounce projectAnnouce) {
		@SuppressWarnings("unchecked")
		List<ProjectAnnounce> projectAnnouceList = (List<ProjectAnnounce>)getCache(CACHE_PROJECT_ANNOUNCE);
		projectAnnouce.setPage(page);
		if (projectAnnouceList == null) {
				projectAnnouceList = projectAnnounceDao.findList(projectAnnouce);
				page = page.setList(projectAnnouceList);
				putCache(CACHE_PROJECT_ANNOUNCE, projectAnnouceList);
				return page;
			}
		page.setList(projectAnnouceList);
		return page;
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
	public static List<OaNotify> getOaNotifyList(OaNotify oaNotify) {
		@SuppressWarnings("unchecked")
		List<OaNotify> oaNotifyList = (List<OaNotify>)getCache(CACHE_OANOTIFY_LIST);
		if (oaNotifyList == null) {
			oaNotifyList = oaNotifyDao.findList(oaNotify);
			putCache(CACHE_OANOTIFY_LIST, oaNotifyList);
		}
		return oaNotifyList;
	}


	/******************************************************************************
	 * 获取全局序号最大值
	 * @param officeId
	 * @return
	 */
	public static SysNo getMaxNo() {
		return sysNoService.getMaxNo();
	}

	/**
	 * 获取机构序号最大值
	 * @param officeId
	 * @return
	 */
	public static SysNo getMaxNoByOffice(String officeId) {
		return sysNoService.getMaxNo(officeId);
	}

	/**
	* @author chenhao
	* @date 2016年12月2日 上午11:45:47
	* @Description [[_编号标识定义_]] SNOKey类
	*/
	public static class SNOKey{
		public static final String SNO_PREFIX = "OS";
		public static final String SNO_OFFICE = SNO_PREFIX+"OFF";
		public static final String SNO_ORDER = SNO_PREFIX+"ORD";
		public static final String SNO_SITE = SNO_PREFIX+"SITE";//还款

	}

   /**
	* @author chenhao
	* @date 2016年12月2日 上午11:48:15
	* @Description [[_编号生成规则定义_]] SNOGener类
	*/
	public static class SNOGener{
		public static String getOrderSno() {
			//TODO
			return null;
		}

		public static String getOfficeSno() {
			//TODO
			return null;
		}

		public static String getSiteSno(String officeId) {
			SysNo sysNo = getMaxNoByOffice(officeId);
			sysNo.getOffice();
//			StringBuffer siteNo = new StringBuffer();
//			siteNo = StringUtil.formatStr("#####|#######|######", sysNo.getOffice())+ StringUtil.formatStr("#####|#######|######", sysNo.getSiteNo());
			return SerialUtils.getOrderNo(SNOKey.SNO_SITE);
		}
	}


//	public static String formatStr(String format, Office office) {
//		String disStr = "";
//		return "";
//	}
}
