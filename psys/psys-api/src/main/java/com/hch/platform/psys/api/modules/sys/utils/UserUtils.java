/**
 *
 */
package com.hch.platform.pcore.modules.sys.utils;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.config.SysIds;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.authorize.service.AuthorizeService;
import com.hch.platform.pcore.modules.interactive.dao.SysLikesDao;
import com.hch.platform.pcore.modules.interactive.entity.SysLikes;
import com.hch.platform.pcore.modules.oa.dao.OaNotifyDao;
import com.hch.platform.pcore.modules.oa.entity.OaNotify;
import com.hch.platform.pcore.modules.project.dao.ProjectAnnounceDao;
import com.hch.platform.pcore.modules.project.entity.ProjectAnnounce;
import com.hch.platform.pcore.modules.sys.dao.AreaDao;
import com.hch.platform.pcore.modules.sys.dao.BackTeacherExpansionDao;
import com.hch.platform.pcore.modules.sys.dao.MenuDao;
import com.hch.platform.pcore.modules.sys.dao.OfficeDao;
import com.hch.platform.pcore.modules.sys.dao.RoleDao;
import com.hch.platform.pcore.modules.sys.dao.StudentExpansionDao;
import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.Area;
import com.hch.platform.pcore.modules.sys.entity.BackTeacherExpansion;
import com.hch.platform.pcore.modules.sys.entity.Menu;
import com.hch.platform.pcore.modules.sys.entity.Office;
import com.hch.platform.pcore.modules.sys.entity.Role;
import com.hch.platform.pcore.modules.sys.entity.StudentExpansion;
import com.hch.platform.pcore.modules.sys.entity.User;
import com.hch.platform.pcore.modules.sys.security.SystemAuthorizingRealm.Principal;

/**
 * 用户工具类


 */
public class UserUtils {
	private static AuthorizeService authorizeService = SpringContextHolder.getBean(AuthorizeService.class);
	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);

	private static StudentExpansionDao studentExpansionDao = SpringContextHolder.getBean(StudentExpansionDao.class);
	private static BackTeacherExpansionDao backTeacherExpansionDao = SpringContextHolder.getBean(BackTeacherExpansionDao.class);
	private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
	private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
	private static AreaDao areaDao = SpringContextHolder.getBean(AreaDao.class);
	private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);
	private static ProjectAnnounceDao projectAnnounceDao = SpringContextHolder.getBean(ProjectAnnounceDao.class);
	private static SysLikesDao sysLikesDao = SpringContextHolder.getBean(SysLikesDao.class);
	private static OaNotifyDao oaNotifyDao = SpringContextHolder.getBean(OaNotifyDao.class);

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

	//返回true 说明未完善
	public static boolean checkInfoPerfect(User user){
		if(user==null||StringUtil.isEmpty(user.getId())){
			return false;
		}
		if("1".equals(user.getUserType())){//学生
			StudentExpansion stu=UserUtils.getStudentByUserId(user.getId());
			if(StringUtil.isEmpty(user.getLoginName())){
				return true;
			}
			if(StringUtil.isEmpty(user.getSex())){
				return true;
			}
			if(StringUtil.isEmpty(user.getName())){
				return true;
			}
			if(StringUtil.isEmpty(user.getIdType())){
				return true;
			}
			if(StringUtil.isEmpty(user.getIdNumber())){
				return true;
			}
			if(StringUtil.isEmpty(user.getMobile())){
				return true;
			}
			if(StringUtil.isEmpty(user.getEmail())){
				return true;
			}
			if(user.getOffice()==null||StringUtil.isEmpty(user.getOffice().getId())){
				return true;
			}
			if(StringUtil.isEmpty(stu.getCurrState())){
				return true;
			}
			if("1".equals(stu.getCurrState())){
				if(StringUtil.isEmpty(stu.getInstudy())){
					return true;
				}
				if(StringUtil.isEmpty(user.getNo())){
					return true;
				}
			}
			if("2".equals(stu.getCurrState())){
				if(StringUtil.isEmpty(user.getEducation())){
					return true;
				}
				if(StringUtil.isEmpty(user.getDegree())){
					return true;
				}
				if(stu.getGraduation()==null){
					return true;
				}
			}
			if("3".equals(stu.getCurrState())){
				if(stu.getTemporaryDate()==null){
					return true;
				}
			}
			if(stu.getEnterdate()==null){
				return true;
			}
		}
		if("2".equals(user.getUserType())){//导师
			BackTeacherExpansion bt=UserUtils.getTeacherByUserId(user.getId());
			if(!"2".equals(bt.getTeachertype())){//不是企业导师
				if(StringUtil.isEmpty(user.getLoginName())){
					return true;
				}
				if(StringUtil.isEmpty(user.getSex())){
					return true;
				}
				if(StringUtil.isEmpty(user.getNo())){
					return true;
				}
				if(StringUtil.isEmpty(user.getName())){
					return true;
				}
				if(StringUtil.isEmpty(bt.getTeachertype())){
					return true;
				}
				if(StringUtil.isEmpty(bt.getServiceIntention())){
					return true;
				}
				if(StringUtil.isEmpty(user.getIdType())){
					return true;
				}
				if(StringUtil.isEmpty(user.getIdNumber())){
					return true;
				}
				if(StringUtil.isEmpty(bt.getEducationType())){
					return true;
				}
				if(StringUtil.isEmpty(user.getEducation())){
					return true;
				}
				if(StringUtil.isEmpty(user.getEmail())){
					return true;
				}
				if(StringUtil.isEmpty(user.getMobile())){
					return true;
				}
			}
		}
		return false;
	}
	public static StudentExpansion getStudentByUserId(String uid){
		return studentExpansionDao.getByUserId(uid);
	}
	public static BackTeacherExpansion getTeacherByUserId(String uid){
		return backTeacherExpansionDao.getByUserId(uid);
	}
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
	/**检查当前用户对foreignId是否点过赞
	 * @param foreignId
	 * @return
	 */
	public static boolean checkIsLikeForUserInfo(String foreignId){
		User user=UserUtils.getUser();
		if(StringUtil.isEmpty(user.getId())){
			return true;
		}
		if(user.getId().equals(foreignId)){
			return true;
		}
		SysLikes sc=new SysLikes();
		sc.setUserId(user.getId());
		sc.setForeignId(foreignId);
		if(sysLikesDao.getExistsLike(sc)>0){
			return true;
		}else{
			return false;
		}
	}
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
		User	user = userDao.getByLoginNameOrNo(no,null);
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

	/**
	 * 跳转登录页面.
	 * @return String
	 */
	public static String toLogin() {
	  return "redirect:" + Global.getFrontPath() + "/toLogin";
	}
}
