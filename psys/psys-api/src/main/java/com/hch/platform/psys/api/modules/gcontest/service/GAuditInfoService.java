package com.hch.platform.pcore.modules.gcontest.service;

import java.util.List;

import com.hch.platform.pcore.modules.project.entity.ProjectAuditInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.common.utils.FloatUtils;
import com.hch.platform.pcore.modules.gcontest.entity.GAuditInfo;
import com.hch.platform.pcore.modules.gcontest.dao.GAuditInfoDao;

/**
 * 大赛信息Service
 * @author zy
 * @version 2017-03-11
 */
@Service
@Transactional(readOnly = true)
public class GAuditInfoService extends CrudService<GAuditInfoDao, GAuditInfo> {

	public GAuditInfo get(String id) {
		return super.get(id);
	}
	
	public List<GAuditInfo> findList(GAuditInfo gAuditInfo) {
		return super.findList(gAuditInfo);
	}
	
	public Page<GAuditInfo> findPage(Page<GAuditInfo> page, GAuditInfo gAuditInfo) {
		return super.findPage(page, gAuditInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(GAuditInfo gAuditInfo) {
		super.save(gAuditInfo);
	}
	
	@Transactional(readOnly = false)
	public void saveByOther(GAuditInfo gAuditInfo) {
		dao.insertByOther(gAuditInfo);
	}
	
	@Transactional(readOnly = false)
	public void updateData(GAuditInfo gAuditInfo) {
		dao.updateData(gAuditInfo);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(GAuditInfo gAuditInfo) {
		super.delete(gAuditInfo);
	}

	public List<GAuditInfo> getSortInfo(GAuditInfo gAuditInfo) {
		return dao.getSortInfo(gAuditInfo);
	}
	
	public GAuditInfo getSortInfoByIdAndState(GAuditInfo gAuditInfo) {
		return dao.getSortInfoByIdAndState(gAuditInfo);
	}
	
	public GAuditInfo getGAuditInfoByIdAndState(GAuditInfo gAuditInfo) {
		return dao.getGAuditInfoByIdAndState(gAuditInfo);
	}
	
	public List<GAuditInfo> getInfo(GAuditInfo gAuditInfo) {
		return dao.getInfo(gAuditInfo);
	}

	public GAuditInfo getInfoByLoginname(GAuditInfo gAuditInfo) {
		return dao.getInfoByLoginname(gAuditInfo);
	}

	public float getAuditAvgInfo(GAuditInfo infoSerch) {
		// TODO Auto-generated method stub
		List<GAuditInfo> infos=dao.getInfo(infoSerch);
		float total=0;
		float average=0;
		int number=0;
		if (infos==null||infos.size()==0) {
			average=infoSerch.getScore();
		}else{
			for (GAuditInfo info:infos) {
				total=total+info.getScore();
				number++;
			}
			average= FloatUtils.division(total,number);
		}
		return average;
	}

	public int getCollegeCount(String state, String collegeId) {
		// TODO Auto-generated method stub
		return dao.getCollegeCount(state,collegeId);
	}
	

	public int getSchoolCount(String state, String collegeId) {
		// TODO Auto-generated method stub
		return dao.getSchoolCount(state,collegeId);
	}
	
	public void changeScoreOrGrade(GAuditInfo gAuditInfo) {
		 dao.update(gAuditInfo);
	}

	public void deleteByGid(String oldId) {
		// TODO Auto-generated method stub
		 dao.deleteByGid(oldId);
	}
	
}