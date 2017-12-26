package com.hch.platform.pcore.modules.proproject.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.proproject.entity.ProProject;

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