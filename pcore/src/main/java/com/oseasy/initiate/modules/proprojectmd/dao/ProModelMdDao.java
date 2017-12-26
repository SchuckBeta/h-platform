package com.oseasy.initiate.modules.proprojectmd.dao;

import com.oseasy.initiate.modules.promodel.entity.ProModel;
import org.apache.ibatis.annotations.Param;

import com.oseasy.initiate.common.persistence.CrudDao;
import com.oseasy.initiate.common.persistence.annotation.MyBatisDao;
import com.oseasy.initiate.modules.proprojectmd.entity.ProModelMd;

import java.util.List;
import java.util.Map;

/**
 * proProjectMdDAO接口.
 * @author zy
 * @version 2017-09-18
 */
@MyBatisDao
public interface ProModelMdDao extends CrudDao<ProModelMd> {

	List<ProModelMd> getByDeclareId(@Param("declareId")String declareId,@Param("actywId")String actywId);
	public void updatePnum(@Param("pnum") String pnum,@Param("id") String id);
	public void updateApprovalResult(@Param("result") String result,@Param("pid") String promodelid);
	public void updateMidResult(@Param("result") String result,@Param("pid") String promodelid);
	public void updateCloseResult(@Param("result") String result,@Param("pid") String promodelid);
	public int checkMdProNumber(@Param("pnum") String pnum,@Param("pid") String pid,@Param("type") String type);
	public int checkMdProName(@Param("pname") String pname,@Param("pid") String pid,@Param("type") String type);
	ProModelMd getByProModelId(String proModelId);

	List<String> getBySetNoPassList();

	List<String> getByMidNoPassList();

	List<String> getByCloseNoPassList();

	List<ProModelMd> getListByModelIds(@Param("ids") List<String> ids);

	List<String> getAllPromodelMd();

	List<ProModelMd> getListByModel(Map<String, Object> actywId);

	int getListByModelCount(@Param("actywId")String actywId);
}