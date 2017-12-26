/**
 *
 */
package com.oseasy.initiate.modules.cms.web.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.security.shiro.session.SessionDAO;
import com.oseasy.initiate.common.servlet.ValidateCodeServlet;
import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.CookieUtils;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.utils.sms.SMSUtilAlidayu;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.cms.entity.Article;
import com.oseasy.initiate.modules.cms.entity.Category;
import com.oseasy.initiate.modules.cms.entity.Comment;
import com.oseasy.initiate.modules.cms.entity.Link;
import com.oseasy.initiate.modules.cms.entity.Site;
import com.oseasy.initiate.modules.cms.service.ArticleDataService;
import com.oseasy.initiate.modules.cms.service.ArticleService;
import com.oseasy.initiate.modules.cms.service.CategoryService;
import com.oseasy.initiate.modules.cms.service.CmsIndexResourceService;
import com.oseasy.initiate.modules.cms.service.CommentService;
import com.oseasy.initiate.modules.cms.service.LinkService;
import com.oseasy.initiate.modules.cms.service.SiteService;
import com.oseasy.initiate.modules.cms.utils.CmsUtils;
import com.oseasy.initiate.modules.course.entity.Course;
import com.oseasy.initiate.modules.course.service.CourseService;
import com.oseasy.initiate.modules.excellent.entity.ExcellentShow;
import com.oseasy.initiate.modules.excellent.service.ExcellentShowService;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifySent;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.sys.entity.BackTeacherExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.security.AdminFormAuthenticationFilter;
import com.oseasy.initiate.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.oseasy.initiate.modules.sys.service.BackTeacherExpansionService;
import com.oseasy.initiate.modules.sys.service.TeacherKeywordService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 网站Controller


 */
@Controller
@RequestMapping(value = "${frontPath}")
public class FrontController extends BaseController{

	@Autowired
	private ArticleService articleService;
	@Autowired
	private ArticleDataService articleDataService;
	@Autowired
	private LinkService linkService;
	@Autowired
	CourseService courseService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private SiteService siteService;
	@Autowired
	private SessionDAO sessionDAO;
	@Autowired
	private OaNotifyService oaNotifyService;

	@Autowired
	private CmsIndexResourceService cmsIndexResourceService;
	@Autowired
	private BackTeacherExpansionService backTeacherExpansionService;
	@Autowired
	private TeacherKeywordService teacherKeywordService;
	@Autowired
	ExcellentShowService excellentShowService;
	@RequestMapping(value = "/loginUserId", method = RequestMethod.GET)
	@ResponseBody
	public String loginUserId() {
		return UserUtils.getUser().getId();
	}

  /*静态页面*/
  @RequestMapping(value = "page-{pageName}")
  public String viewPages(@PathVariable String pageName, Model model) {

    return "modules/website/pages/"+pageName;
  }

  /*静态页面2*/
  @RequestMapping(value = "html-{pageName}")
  public String htmlviewPages(@PathVariable String pageName, Model model) {
    return "modules/website/html/"+pageName;
  }

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
		Principal principal = UserUtils.getPrincipal();

//		// 默认页签模式

		if (logger.isDebugEnabled()) {
			logger.debug("login, active session size: {}", sessionDAO.getActiveSessions(false).size());
		}

		// 如果已登录，再次访问主页，则退出原账号。
		if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))) {
			CookieUtils.setCookie(response, "LOGINED", "false");
		}

		// 如果已经登录，则跳转到管理首页
		if (principal != null && !principal.isMobileLogin()) {
			return "redirect:" + frontPath;
		}
		request.setAttribute("loginType", request.getParameter("loginType"));
		request.setAttribute("loginPage", "1");
		return "modules/website/frontLogin";
	}

	/**
	 * 登录失败，真正登录的POST请求由Filter完成
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
		Principal principal = UserUtils.getPrincipal();

		// 如果已经登录，则跳转到管理首页
		if (principal != null) {
			return "redirect:" + frontPath;
		}

		String username = WebUtils.getCleanParam(request, AdminFormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
		boolean rememberMe = WebUtils.isTrue(request, AdminFormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
		boolean mobile = WebUtils.isTrue(request, AdminFormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
		String exception = (String)request.getAttribute(AdminFormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
		String message = (String)request.getAttribute(AdminFormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);

		if (StringUtil.isBlank(message) || StringUtil.equals(message, "null")) {
			message = "账号或密码错误, 请重试.";
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
		request.setAttribute("loginType", request.getParameter("loginType"));
		request.setAttribute("loginPage", "1");
		return "modules/website/frontLogin";
	}

  @RequestMapping(value = "help", method = RequestMethod.GET)
  public String help(HttpServletRequest request, HttpServletResponse response, Model model) {
    return "modules/website/frontHelp";
  }

	@RequestMapping(value="toLogin")
	public String toLogin(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("loginType", request.getParameter("loginType"));
		request.setAttribute("loginPage", "1");
		return "modules/website/frontLogin";
	}
	@RequestMapping(value="toRegister")
	public String toRegister(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("loginPage", "1");
		return "modules/website/studentregister";
	}
	@RequestMapping(value="resetNotifyShow")
	@ResponseBody
	public String resetNotifyShow(HttpServletRequest request, HttpServletResponse response) {
		if(request.getSession().getAttribute("notifyShow")!=null){//登录成功后重置是否弹出消息
			request.getSession().removeAttribute("notifyShow");
		}
		return "1";
	}
	/**
	 * 登录成功，进入管理首页
	 */
	@RequestMapping
	public String index(HttpServletRequest request,Model model, HttpServletResponse response) {
		Principal principal = UserUtils.getPrincipal();

		// 登录成功后，验证码计算器清零
		if (principal!=null)isValidateCodeLogin(principal.getLoginName(), false, true);

		if (logger.isDebugEnabled()) {
			logger.debug("show index, active session size: {}", sessionDAO.getActiveSessions(false).size());
		}
		User user = UserUtils.getUser();
		if (StringUtil.isNotEmpty(user.getName())) {
			model.addAttribute("user", user);
		}
//		return "modules/website/indexForTemplate";
//		return "modules/website/indexNew";

		//查找优秀项目展示
		Map<String,Object> map=excellentShowService.findForIndex();
		model.addAttribute("excellentShowList", map);
		//查找栏目导师
		BackTeacherExpansion btef =new BackTeacherExpansion();
		btef.setFirstShow("1");
		List<BackTeacherExpansion> firstTeacherList=backTeacherExpansionService.findList(btef);
		List<BackTeacherExpansion> firstTeacherListnew =new ArrayList<BackTeacherExpansion> ();
		//首页只展示3个
		if(firstTeacherList!=null){
			if(firstTeacherList.size()>3){
				for(int i=0;i<3;i++){
					firstTeacherListnew.add(firstTeacherList.get(i));
				}
			}else{
				for(int i=0;i<firstTeacherList.size();i++){
					firstTeacherListnew.add(firstTeacherList.get(i));
				}
			}
			model.addAttribute("firstTeacherList", firstTeacherListnew);
		}
		//查找首页导师
		BackTeacherExpansion btes =new BackTeacherExpansion();
		btes.setSiteShow("1");
		List<BackTeacherExpansion> siteTeacherList=backTeacherExpansionService.findList(btes);
		List<BackTeacherExpansion> siteTeacherListnew =new ArrayList<BackTeacherExpansion> ();
		//栏位只展示4个
		if(siteTeacherList!=null) {
			if (siteTeacherList.size() > 4) {
				for (int i = 0; i < 4; i++) {
					if (siteTeacherList.get(i).getId() != null) {
						List<String> tes = teacherKeywordService.getStringKeywordByTeacherid(siteTeacherList.get(i).getId());
						if (tes.size() > 0) {
							siteTeacherList.get(i).setKeywords(tes);
						}
					}
					siteTeacherListnew.add(siteTeacherList.get(i));
				}
			} else {
				for (int i = 0; i < siteTeacherList.size(); i++) {
					if (siteTeacherList.get(i).getId() != null) {
						List<String> tes = teacherKeywordService.getStringKeywordByTeacherid(siteTeacherList.get(i).getId());
						if (tes.size() > 0) {
							siteTeacherList.get(i).setKeywords(tes);
						}
					}
					siteTeacherListnew.add(siteTeacherList.get(i));
				}
			}
			model.addAttribute("siteTeacherList", siteTeacherListnew);
		}
		//查找课程 addbBy zhangzheng
		List<Course> courseList=courseService.findFrontCourse();
		model.addAttribute("courseList", courseList);
		return "modules/website/indexForTemplate";
	}

	/*导师风采页面*/
	@RequestMapping(value = "pageTeacher")
	public String pageTeacher( BackTeacherExpansion backTeacherExpansion,HttpServletRequest request,HttpServletResponse response, Model model) {
		String teacherType=request.getParameter("teacherType");
		if(StringUtil.isEmpty(teacherType)){
			teacherType="1";
		}
		backTeacherExpansion.setTeachertype(teacherType);
		Page<BackTeacherExpansion> page = backTeacherExpansionService.findTeacherPage(new Page<BackTeacherExpansion>(request, response),backTeacherExpansion);
		//model.addAttribute("siteTeacherList", siteTeacherListnew);
		BackTeacherExpansion backTeacherExpansionNew =backTeacherExpansionService.findTeacherByTopShow(teacherType);
		if(backTeacherExpansionNew!=null){
			model.addAttribute("backTeacherExpansionNew", backTeacherExpansionNew);
		}
		model.addAttribute("page", page);
		model.addAttribute("teacherType", teacherType);
		return "modules/website/pages/pageTeacher";
	}

	/**
	 * 优秀项目页面.
	 **/
	@RequestMapping(value = "pageProject")
	public String pageProject(ExcellentShow excellentShow, HttpServletRequest request,HttpServletResponse response, Model model) {
		Map<String,Object> param =new HashMap<String,Object>();
    if(StringUtil.isNotEmpty(excellentShow.getType())){
			param.put("type", excellentShow.getType());
			model.addAttribute("type", excellentShow.getType());
		}
		if(StringUtil.isNotEmpty(excellentShow.getContent())){
			param.put("key",excellentShow.getContent());
			model.addAttribute("key", excellentShow.getContent());
		}
		Page<Map<String,String>> page = excellentShowService.findAllProjectShow(new Page<Map<String,String>>(request, response), param);
		//Page<ExcellentShow> page=excellentShowService.findPage(new Page<ExcellentShow>(request, response),excellentShow);

		model.addAttribute("page", page);
		model.addAttribute("excellentShow", excellentShow);
		return "modules/website/pages/pageProject";
	}
	//@RequiresPermissions("cms:cmsIndexResource:view")
	/*	@RequestMapping(value = "indexResourceList")
		public String indexResourceList(CmsIndexResource cmsIndexResource, HttpServletRequest request, HttpServletResponse response, Model model) {
			Page<CmsIndexResource> indexPage = new Page<CmsIndexResource>(request, response);
			indexPage.setPageSize(-1);
			cmsIndexResource.setIsIndex("1");
			Page<CmsIndexResource> page = cmsIndexResourceService.findPage(indexPage, cmsIndexResource);
			//model.addAttribute("page", page);
			if (page!=null) {
			List<CmsIndexResource> allList = page.getList();
			}
			return "modules/cms/cmsIndexResourceList";
		}*/



	/**
	 * 是否是验证码登录
	 * @param useruame 用户名
	 * @param isFail 计数加1
	 * @param clean 计数清零
	 * @return
	 */
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



	@RequestMapping(value = "sendSmsCode", method = RequestMethod.GET)
	@ResponseBody
	public String sendSmsCode(HttpServletRequest request, HttpServletResponse response, Model model) {
		String p=request.getParameter("mobile");
    	String code=SMSUtilAlidayu.sendSms(p);
		if (code!=null) {
			request.getSession().setAttribute("server_sms_code", code);
			return "1";
		}else{
			return "0";
		}
	}


	@RequestMapping(value = "staticPage-{resid}")
	public String htmlResViewPages(@PathVariable String resid, Model model) {
		model.addAttribute("resource", cmsIndexResourceService.get(resid));
		return "modules/website/staticPage";
	}
	/*内容模板静态文件*/
	@RequestMapping(value = "cms/{template}/{pageName}")
	public String modelCms(@PathVariable String pageName, @PathVariable String template,Model model) {
		return "template/cms/"+template+"/"+pageName;
	}

	/**
	 * 网站首页
	 */
	@RequestMapping(value = "index-{siteId}${urlSuffix}")
	public String index(@PathVariable String siteId, Model model) {
		if (siteId.equals("1")) {
			return "redirect:"+Global.getFrontPath();
		}
		Site site = CmsUtils.getSite(siteId);
		// 子站有独立页面，则显示独立页面
		if (StringUtil.isNotBlank(site.getCustomIndexView())) {
			model.addAttribute("site", site);
			model.addAttribute("isIndex", true);
			return "modules/cms/front/themes/"+site.getTheme()+"/frontIndex"+site.getCustomIndexView();
		}
		// 否则显示子站第一个栏目
		List<Category> mainNavList = CmsUtils.getMainNavList(siteId);
		if (mainNavList.size() > 0) {
			String firstCategoryId = CmsUtils.getMainNavList(siteId).get(0).getId();
			return "redirect:"+Global.getFrontPath()+"/list-"+firstCategoryId+Global.getUrlSuffix();
		}else{
			model.addAttribute("site", site);
			return "modules/cms/front/themes/"+site.getTheme()+"/frontListCategory";
		}
	}

	/**
	 * 内容列表
	 */
	@RequestMapping(value = "list-{categoryId}${urlSuffix}")
	public String list(@PathVariable String categoryId, @RequestParam(required=false, defaultValue="1") Integer pageNo,
					   @RequestParam(required=false, defaultValue="15") Integer pageSize, Model model) {
		Category category = categoryService.get(categoryId);
		if (category==null) {
			Site site = CmsUtils.getSite(Site.defaultSiteId());
			model.addAttribute("site", site);
			return "error/404";
		}
		Site site = siteService.get(category.getSite().getId());
		model.addAttribute("site", site);
		// 2：简介类栏目，栏目第一条内容
		if ("2".equals(category.getShowModes()) && "article".equals(category.getModule())) {
			// 如果没有子栏目，并父节点为跟节点的，栏目列表为当前栏目。
			List<Category> categoryList = Lists.newArrayList();
			if (category.getParent().getId().equals("1")) {
				categoryList.add(category);
			}else{
				categoryList = categoryService.findByParentId(category.getParent().getId(), category.getSite().getId());
			}
			model.addAttribute("category", category);
			model.addAttribute("categoryList", categoryList);
			// 获取文章内容
			Page<Article> page = new Page<Article>(1, 1, -1);
			Article article = new Article(category);
			page = articleService.findPage(page, article, false);
			if (page.getList().size()>0) {
				article = page.getList().get(0);
				article.setArticleData(articleDataService.get(article.getId()));
				articleService.updateHitsAddOne(article.getId());
			}
			model.addAttribute("article", article);
			CmsUtils.addViewConfigAttribute(model, category);
			CmsUtils.addViewConfigAttribute(model, article.getViewConfig());
			return "modules/cms/front/themes/"+site.getTheme()+"/"+getTpl(article);
		}else{
			List<Category> categoryList = categoryService.findByParentId(category.getId(), category.getSite().getId());
			// 展现方式为1 、无子栏目或公共模型，显示栏目内容列表
			if ("1".equals(category.getShowModes())||categoryList.size()==0) {
				// 有子栏目并展现方式为1，则获取第一个子栏目；无子栏目，则获取同级分类列表。
				if (categoryList.size()>0) {
					category = categoryList.get(0);
				}else{
					// 如果没有子栏目，并父节点为跟节点的，栏目列表为当前栏目。
					if (category.getParent().getId().equals("1")) {
						categoryList.add(category);
					}else{
						categoryList = categoryService.findByParentId(category.getParent().getId(), category.getSite().getId());
					}
				}
				model.addAttribute("category", category);
				model.addAttribute("categoryList", categoryList);
				// 获取内容列表
				if ("article".equals(category.getModule())) {
					Page<Article> page = new Page<Article>(pageNo, pageSize);
					//System.out.println(page.getPageNo());
					page = articleService.findPage(page, new Article(category), false);
					model.addAttribute("page", page);
					// 如果第一个子栏目为简介类栏目，则获取该栏目第一篇文章
					if ("2".equals(category.getShowModes())) {
						Article article = new Article(category);
						if (page.getList().size()>0) {
							article = page.getList().get(0);
							article.setArticleData(articleDataService.get(article.getId()));
							articleService.updateHitsAddOne(article.getId());
						}
						model.addAttribute("article", article);
						CmsUtils.addViewConfigAttribute(model, category);
						CmsUtils.addViewConfigAttribute(model, article.getViewConfig());
						return "modules/cms/front/themes/"+site.getTheme()+"/"+getTpl(article);
					}
				}else if ("link".equals(category.getModule())) {
					Page<Link> page = new Page<Link>(1, -1);
					page = linkService.findPage(page, new Link(category), false);
					model.addAttribute("page", page);
				}
				String view = "/frontList";
				if (StringUtil.isNotBlank(category.getCustomListView())) {
					view = "/"+category.getCustomListView();
				}
				CmsUtils.addViewConfigAttribute(model, category);
				site =siteService.get(category.getSite().getId());
				//System.out.println("else 栏目第一条内容 _2 :"+category.getSite().getTheme()+","+site.getTheme());
				return "modules/cms/front/themes/"+siteService.get(category.getSite().getId()).getTheme()+view;
				//return "modules/cms/front/themes/"+category.getSite().getTheme()+view;
			}
			// 有子栏目：显示子栏目列表
			else{
				model.addAttribute("category", category);
				model.addAttribute("categoryList", categoryList);
				String view = "/frontListCategory";
				if (StringUtil.isNotBlank(category.getCustomListView())) {
					view = "/"+category.getCustomListView();
				}
				CmsUtils.addViewConfigAttribute(model, category);
				return "modules/cms/front/themes/"+site.getTheme()+view;
			}
		}
	}

	/**
	 * 内容列表（通过url自定义视图）
	 */
	@RequestMapping(value = "listc-{categoryId}-{customView}${urlSuffix}")
	public String listCustom(@PathVariable String categoryId, @PathVariable String customView, @RequestParam(required=false, defaultValue="1") Integer pageNo,
							 @RequestParam(required=false, defaultValue="15") Integer pageSize, Model model) {
		Category category = categoryService.get(categoryId);
		if (category==null) {
			Site site = CmsUtils.getSite(Site.defaultSiteId());
			model.addAttribute("site", site);
			return "error/404";
		}
		Site site = siteService.get(category.getSite().getId());
		model.addAttribute("site", site);
		List<Category> categoryList = categoryService.findByParentId(category.getId(), category.getSite().getId());
		model.addAttribute("category", category);
		model.addAttribute("categoryList", categoryList);
		CmsUtils.addViewConfigAttribute(model, category);
		return "modules/cms/front/themes/"+site.getTheme()+"/frontListCategory"+customView;
	}

	/**
	 * 显示内容
	 */
	@RequestMapping(value = "view-{categoryId}-{contentId}${urlSuffix}")
	public String view(@PathVariable String categoryId, @PathVariable String contentId, Model model) {
		Category category = categoryService.get(categoryId);
		if (category==null) {
			Site site = CmsUtils.getSite(Site.defaultSiteId());
			model.addAttribute("site", site);
			return "error/404";
		}
		model.addAttribute("site", category.getSite());
		if ("article".equals(category.getModule())) {
			// 如果没有子栏目，并父节点为跟节点的，栏目列表为当前栏目。
			List<Category> categoryList = Lists.newArrayList();
			if (category.getParent().getId().equals("1")) {
				categoryList.add(category);
			}else{
				categoryList = categoryService.findByParentId(category.getParent().getId(), category.getSite().getId());
			}
			// 获取文章内容
			Article article = articleService.get(contentId);
			if (article==null || !Article.DEL_FLAG_NORMAL.equals(article.getDelFlag())) {
				return "error/404";
			}
			// 文章阅读次数+1
			articleService.updateHitsAddOne(contentId);
			// 获取推荐文章列表
			List<Object[]> relationList = articleService.findByIds(articleDataService.get(article.getId()).getRelation());
			// 将数据传递到视图
			model.addAttribute("category", categoryService.get(article.getCategory().getId()));
			model.addAttribute("categoryList", categoryList);
			article.setArticleData(articleDataService.get(article.getId()));
			model.addAttribute("article", article);
			model.addAttribute("relationList", relationList);
			CmsUtils.addViewConfigAttribute(model, article.getCategory());
			CmsUtils.addViewConfigAttribute(model, article.getViewConfig());
			Site site = siteService.get(category.getSite().getId());
			model.addAttribute("site", site);
//			return "modules/cms/front/themes/"+category.getSite().getTheme()+"/"+getTpl(article);
			return "modules/cms/front/themes/"+site.getTheme()+"/"+getTpl(article);
		}
		return "error/404";
	}

	/**
	 * 内容评论
	 */
	@RequestMapping(value = "comment", method=RequestMethod.GET)
	public String comment(String theme, Comment comment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Comment> page = new Page<Comment>(request, response);
		Comment c = new Comment();
		c.setCategory(comment.getCategory());
		c.setContentId(comment.getContentId());
		c.setDelFlag(Comment.DEL_FLAG_NORMAL);
		page = commentService.findPage(page, c);
		model.addAttribute("page", page);
		model.addAttribute("comment", comment);
		return "modules/cms/front/themes/"+theme+"/frontComment";
	}

	/**
	 * 内容评论保存
	 */
	@ResponseBody
	@RequestMapping(value = "comment", method=RequestMethod.POST)
	public String commentSave(Comment comment, String validateCode,@RequestParam(required=false) String replyId, HttpServletRequest request) {
		if (StringUtil.isNotBlank(validateCode)) {
			if (ValidateCodeServlet.validate(request, validateCode)) {
				if (StringUtil.isNotBlank(replyId)) {
					Comment replyComment = commentService.get(replyId);
					if (replyComment != null) {
						comment.setContent("<div class=\"reply\">"+replyComment.getName()+":<br/>"
								+replyComment.getContent()+"</div>"+comment.getContent());
					}
				}
				comment.setIp(request.getRemoteAddr());
				comment.setCreateDate(new Date());
				comment.setDelFlag(Comment.DEL_FLAG_AUDIT);
				commentService.save(comment);
				return "{result:1, message:'提交成功。'}";
			}else{
				return "{result:2, message:'验证码不正确。'}";
			}
		}else{
			return "{result:2, message:'验证码不能为空。'}";
		}
	}

	/**
	 * 站点地图
	 */
	@RequestMapping(value = "map-{siteId}${urlSuffix}")
	public String map(@PathVariable String siteId, Model model) {
		Site site = CmsUtils.getSite(siteId!=null?siteId:Site.defaultSiteId());
		model.addAttribute("site", site);
		return "modules/cms/front/themes/"+site.getTheme()+"/frontMap";
	}

	private String getTpl(Article article) {
		if (StringUtil.isBlank(article.getCustomContentView())) {
			String view = null;
			Category c = article.getCategory();
			boolean goon = true;
			do{
				if (StringUtil.isNotBlank(c.getCustomContentView())) {
					view = c.getCustomContentView();
					goon = false;
				}else if (c.getParent() == null || c.getParent().isRoot()) {
					goon = false;
				}else{
					c = c.getParent();
				}
			}while(goon);
			return StringUtil.isBlank(view) ? Article.DEFAULT_TEMPLATE : view;
		}else{
			return article.getCustomContentView();
		}
	}


	

	//页面未读通知消息
	@RequestMapping(value="unReadOaNotify")
	@ResponseBody
	public List<OaNotifySent> unReadOaNotify(OaNotify oaNotify) {
		return oaNotifyService.unRead(oaNotify);

	}
	//关闭操作
	@RequestMapping(value="closeButton")
	@ResponseBody
	public String closeButton(HttpServletRequest request) {
		String oaNotifyId = request.getParameter("send_id");
		OaNotify oaNotify = oaNotifyService.get(oaNotifyId);
		oaNotifyService.updateReadFlag(oaNotify);
		return "1";
	}
	//完善信息
	@RequestMapping(value="infoPerfect")
	public String infoPerfect(HttpServletRequest request,Model model) {
		String userType=request.getParameter("userType");
		model.addAttribute("userType", userType);
		if("2".equals(userType)){
			model.addAttribute("teaEid", backTeacherExpansionService.getByUserId(UserUtils.getUser().getId()).getId());
		}
		return "modules/website/infoPerfect";
	}
}
