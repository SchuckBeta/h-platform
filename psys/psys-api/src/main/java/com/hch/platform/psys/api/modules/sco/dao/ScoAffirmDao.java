package com.hch.platform.pcore.modules.sco.dao;

import com.hch.platform.pcore.common.persistence.CrudDao;
import com.hch.platform.pcore.common.persistence.annotation.MyBatisDao;
import com.hch.platform.pcore.modules.sco.entity.ScoAffirm;
import com.hch.platform.pcore.modules.sco.vo.ScoProjectVo;
import com.hch.platform.pcore.modules.sco.vo.ScoTeamRatioVo;

import java.util.List;

/**
 * 创新、创业、素质学分认定表DAO接口.
 * @author chenhao
 * @version 2017-07-18
 */
@MyBatisDao
public interface ScoAffirmDao extends CrudDao<ScoAffirm> {
     List<ScoProjectVo> findInnovationList(ScoProjectVo scoProjectVo);
     List<ScoProjectVo> findBusinessList(ScoProjectVo scoProjectVo);
     List<ScoProjectVo> findQualityList(ScoProjectVo scoProjectVo);
     //根据项目id或者大赛id删除
     public int deleteByProId(String proId);

     List<ScoProjectVo> findScoGontestVoList(ScoProjectVo scoProjectVo);
     //创新项目
     List<ScoProjectVo> findScoProjectCreateVoPage(ScoProjectVo scoProjectVo);
     //创业项目
     List<ScoProjectVo> findScoProjectStartVoPage(ScoProjectVo scoProjectVo);
     ScoAffirm  findProjectScore(ScoAffirm scoAffirm);

     List<ScoProjectVo> getScoGradeQuality(String id);

     List<ScoProjectVo>  getScoGradeProject(String id);
}