/**
 *
 */
package com.oseasy.initiate.modules.cms.entity;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 站点Entity


 */
public class Site extends DataEntity<Site> {

	private static final long serialVersionUID = 1L;
	private String name;	// 站点名称
	private String title;	// 站点标题
	private String logo;	// 站点租户logo
	private String logoSite;	// 站点logoSite
	private String description;// 描述，填写有助于搜索引擎优化
	private String keywords;// 关键字，填写有助于搜索引擎优化
	private String theme;	// 主题
	private String copyright;// 版权信息
	private String customIndexView;// 自定义首页视图文件
	private String domain;
	private String index;//站点首页栏目ID

	private List<Category> categorys;

	public Site() {
		super();
	}

	public Site(String id) {
		this();
		this.id = id;
	}

	@Length(min=1, max=100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min=1, max=100)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Length(min=0, max=255)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Length(min=0, max=255)
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	@Length(min=1, max=255)
	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getCustomIndexView() {
		return customIndexView;
	}

	public void setCustomIndexView(String customIndexView) {
		this.customIndexView = customIndexView;
	}

	/**
	 * 获取默认站点ID
	 */
	public static String defaultSiteId() {
		return SysIds.SITE_TOP.getId();
	}

	/**
	 * 判断是否为默认（官方）站点
	 */
	public static boolean isDefault(String id) {
		return id != null && id.equals(defaultSiteId());
	}

	/**
	 * 获取当前编辑的站点编号
	 */
	public static String getCurrentSiteId() {
		String siteId = (String)UserUtils.getCache("siteId");
		return StringUtils.isNotBlank(siteId)?siteId:defaultSiteId();
	}

    /**
   	 * 模板路径
   	 */
   	public static final String TPL_BASE = "/WEB-INF/views/modules/cms/front/themes";

    /**
   	 * 获得模板方案路径。如：/WEB-INF/views/modules/cms/front/themes/initiate
   	 *
   	 * @return
   	 */
   	public String getSolutionPath() {
   		return TPL_BASE + "/" + getTheme();
   	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public List<Category> getCategorys() {
		return categorys;
	}

	public void setCategorys(List<Category> categorys) {
		this.categorys = categorys;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

  public String getLogoSite() {
    return logoSite;
  }

  public void setLogoSite(String logoSite) {
    this.logoSite = logoSite;
  }
}