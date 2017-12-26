package com.oseasy.initiate.modules.actyw.tool.project.impl;

import com.oseasy.initiate.modules.actyw.tool.project.ActProParamVo;
import com.oseasy.initiate.modules.actyw.tool.project.IActProDeal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
@Service
@Transactional(readOnly = true)
public class ActProScore implements IActProDeal{
	@Override
	public Boolean dealMenu(ActProParamVo actProParamVo) {
		return true;
	}

	@Override
	public Boolean dealCategory(ActProParamVo actProParamVo) {
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
    return false;
  }

  @Override
  public Boolean requireCategory() {
    return false;
  }

  @Override
  public Boolean requireTime() {
    return true;
  }

  @Override
  public Boolean requireIcon() {
    return false;
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
