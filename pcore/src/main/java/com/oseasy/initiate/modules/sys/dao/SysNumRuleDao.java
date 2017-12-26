package com.hch.platform.pcore.modules.sys.dao;

import java.util.Map;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sys.entity.SysNumRule;

/**
 * 编号规则DAO接口
 * @author zdk
 * @version 2017-04-01
 */
@MyBatisDao
public interface SysNumRuleDao extends CrudDao<SysNumRule> {
	public void onEventScheduler(Map<String,Object> param);
	public void dropNumResetEvent(Map<String,Object> param);
	public void createNumResetEvent(Map<String,Object> param);
	public SysNumRule getByType(String type);
}