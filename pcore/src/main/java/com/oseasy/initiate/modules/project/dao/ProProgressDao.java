package com.hch.platform.pcore.modules.project.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.project.entity.ProProgress;

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