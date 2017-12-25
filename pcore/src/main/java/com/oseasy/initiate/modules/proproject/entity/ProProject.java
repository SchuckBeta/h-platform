package com.oseasy.initiate.modules.proproject.entity;

import java.beans.Transient;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.cms.entity.Category;
import com.oseasy.initiate.modules.sys.entity.Menu;

/**
 * 创建项目Entity.
 * @author zhangyao
 * @version 2017-06-15
 */
public class ProProject extends DataEntity<ProProject> {

	private static final long serialVersionUID = 1L;
  private String categoryRid;   //项目对应前台栏目根结点
  private String menuRid;   //项目对应后台菜单根结点
	private Menu menu;		//后台菜单
	private Category category;		//前台栏目
	private String content;		// 内容
	private String projectName;		// 项目名称
	private String projectMark;		// 项目标识（英文）gcontest
	private String imgUrl;		// 项目图片
  private String state;   // 发布状态 1：是发布 0：是未发布
	private boolean restCategory;		//是否重置子栏目
	private boolean restMenu;		//是否重置子菜单

	public ProProject() {
		super();
	}

	public ProProject(String id) {
		super(id);
	}

	public String getProjectMark() {
		return projectMark;
	}

	public void setProjectMark(String projectMark) {
		this.projectMark = projectMark;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Length(min=0, max=64, message="项目名称长度必须介于 0 和 64 之间")
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	@Length(min=0, max=2, message="发布状态 1：是发布 0：是未发布长度必须介于 0 和 2 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

  public String getCategoryRid() {
    if(StringUtil.isEmpty(this.categoryRid) && (this.category != null)){
      this.categoryRid = this.category.getId();
    }
    return categoryRid;
  }

  public void setCategoryRid(String categoryRid) {
    this.categoryRid = categoryRid;
  }

  public String getMenuRid() {
    if(StringUtil.isEmpty(this.menuRid) && (this.menu != null)){
      this.menuRid = this.menu.getId();
    }
    return menuRid;
  }

  public void setMenuRid(String menuRid) {
    this.menuRid = menuRid;
  }

  @Transient
  public Menu getMenu() {
    return menu;
  }

  @Transient
  public void setMenu(Menu menu) {
    this.menu = menu;
  }

  @Transient
  public Category getCategory() {
    return category;
  }

  @Transient
  public void setCategory(Category category) {
    this.category = category;
  }

  @Transient
  public String getImgUrl() {
    if(StringUtil.isEmpty(this.imgUrl) && (this.menu != null)){
      this.imgUrl = this.menu.getImgUrl();
    }
    return imgUrl;
  }

  @Transient
  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  @Transient
  public boolean isRestCategory() {
    return restCategory;
  }

  @Transient
  public void setRestCategory(boolean restCategory) {
    this.restCategory = restCategory;
  }

  @Transient
  public boolean isRestMenu() {
    return restMenu;
  }

  @Transient
  public void setRestMenu(boolean restMenu) {
    this.restMenu = restMenu;
  }
}