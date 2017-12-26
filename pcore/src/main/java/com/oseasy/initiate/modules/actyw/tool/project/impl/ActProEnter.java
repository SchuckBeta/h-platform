package com.oseasy.initiate.modules.actyw.tool.project.impl;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGnode;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.tool.project.ActProParamVo;
import com.oseasy.initiate.modules.actyw.tool.project.IActProDeal;
import com.oseasy.initiate.modules.cms.entity.Category;
import com.oseasy.initiate.modules.cms.service.CategoryService;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.sys.entity.Menu;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.service.SystemService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
//入驻栏目和菜单创建
public class ActProEnter implements IActProDeal {
	//@Autowired
	// CategoryService categoryService;

	ActProUtil actProUtil = (ActProUtil) SpringContextHolder.getBean(ActProUtil.class);
	@Override
	@Transactional(readOnly = false)
	public Boolean dealMenu(ActProParamVo actProParamVo) 	{
		//根据入驻 固定菜单生成流程子菜单
		return actProUtil.dealMenu1(actProParamVo, SysIds.SITE_PW_ENTER_ROOT.getId());

	}

	@Override
	@Transactional(readOnly = false)
	public Boolean dealCategory(ActProParamVo actProParamVo) {
		//return actProUtil.dealCategory(actProParamVo,SysIds.SITE_CATEGORYS_PROJECT_ROOT.getId(),"");
		return true;
	}

  @Override
  public Boolean dealTime(ActProParamVo actProParamVo) {
    return true;
  }

  @Override
  public Boolean dealIcon(ActProParamVo actProParamVo) {
    return true;
  }

  @Override
  public Boolean dealActYw(ActProParamVo actProParamVo) {
    return true;
  }

  @Override
  public Boolean dealDeploy(ActProParamVo actProParamVo) {
    return true;
  }

  @Override
  public Boolean requireMenu() {
    return true;
  }

  @Override
  public Boolean requireCategory() {
    return true;
  }

  @Override
  public Boolean requireTime() {
    return true;
  }

  @Override
  public Boolean requireIcon() {
    return true;
  }

  @Override
  public Boolean requireActYw() {
    return true;
  }

  @Override
  public Boolean requireDeploy() {
    return true;
  }
}
