package com.oseasy.initiate.modules.project.dao;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.project.entity.ProSituation;

import java.util.List;

/**
 * 国创项目完成情况表单DAO接口
 * @author 9527
 * @version 2017-03-29
 */
@MyBatisDao
public interface ProSituationDao extends CrudDao<ProSituation> {

    public void deleteByMidId(String fid);

    public List<ProSituation> getByFid(String fid);
	
}