package com.hch.platform.pcore.modules.project.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.project.entity.ProSituation;

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