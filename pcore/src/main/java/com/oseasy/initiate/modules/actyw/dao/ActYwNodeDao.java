package com.oseasy.initiate.modules.actyw.dao;

import java.util.List;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.actyw.entity.ActYwNode;

/**
 * 项目流程节点DAO接口
 * @author chenhao
 * @version 2017-05-23
 */
@MyBatisDao
public interface ActYwNodeDao extends CrudDao<ActYwNode> {

  List<ActYwNode> findListByTypeNoZero(ActYwNode actYwNode);

}