package com.oseasy.initiate.modules.project.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.project.entity.ProProgress;

import java.util.List;

/**
 * 国创项目进度表单DAO接口
 * @author 9527
 * @version 2017-03-29
 */
@MyBatisDao
public interface ProProgressDao extends CrudDao<ProProgress> {
    public void deleteByMidId(String fid);
    public List<ProProgress> getByFid(String fid);
}