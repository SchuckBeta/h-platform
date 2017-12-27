package com.hch.platform.pcore.modules.sco.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sco.entity.ScoAllotRatio;
import com.hch.platform.pcore.modules.sco.vo.ScoRatioVo;

/**
 * 学分分配比例DAO接口.
 * @author 9527
 * @version 2017-07-18
 */
@MyBatisDao
public interface ScoAllotRatioDao extends CrudDao<ScoAllotRatio> {
	public List<ScoAllotRatio> findAll(String confId);
	public ScoRatioVo findRatio(ScoRatioVo scoRatioVo);
	public int checkNumber(@Param("id") String id,@Param("number") String number,@Param("confid") String confid);
	public void delByFid(String fid);
}