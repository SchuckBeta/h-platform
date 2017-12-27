/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.hch.platform.pcore.modules.dasai.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.dasai.entity.OaDasai;

/**
 * 大赛测试DAO接口
 * @author zhangzheng
 * @version 2017-02-24
 */
@MyBatisDao
public interface OaDasaiDao extends CrudDao<OaDasai> {

    public int updateStateAndScore(OaDasai oaDasai);
	
}