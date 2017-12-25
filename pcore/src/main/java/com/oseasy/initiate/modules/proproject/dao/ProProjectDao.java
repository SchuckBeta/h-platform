package com.oseasy.initiate.modules.proproject.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.proproject.entity.ProProject;

/**
 * 创建项目DAO接口.
 * @author zhangyao
 * @version 2017-06-15
 */
@MyBatisDao
public interface ProProjectDao extends CrudDao<ProProject> {

    ProProject getProProjectByName(String name);

    ProProject getProProjectByMark(String mark);
}