package com.hch.platform.pcore.modules.team.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.modules.team.dao.TeamUserHistoryDao;
import com.hch.platform.pcore.modules.team.entity.TeamUserHistory;


/**
 * 团队历史纪录Service.
 * @author chenh
 * @version 2017-11-14
 */
@Service
@Transactional(readOnly = true)
public class TeamUserHistoryService extends CrudService<TeamUserHistoryDao, TeamUserHistory> {
	public int getBuildingCountByUserId(String uid){
		return dao.getBuildingCountByUserId(uid);
	}
	public List<TeamUserHistory> getByProId(String proId,String teamId){
		return dao.getByProId(proId,teamId);
	}

	public TeamUserHistory get(String id) {
		return super.get(id);
	}

	public List<TeamUserHistory> findList(TeamUserHistory teamUserHistory) {
		return super.findList(teamUserHistory);
	}

	public Page<TeamUserHistory> findPage(Page<TeamUserHistory> page, TeamUserHistory teamUserHistory) {
		return super.findPage(page, teamUserHistory);
	}

	@Transactional(readOnly = false)
	public void save(TeamUserHistory teamUserHistory) {
		super.save(teamUserHistory);
	}

	@Transactional(readOnly = false)
	public void delete(TeamUserHistory teamUserHistory) {
		super.delete(teamUserHistory);
	}

	public List<TeamUserHistory> getGcontestInfoByActywId(String id, String actywId ,String gcontesId) {
		return dao.getGcontestInfoByActywId(id,actywId,gcontesId);
	}

	/*
	* teamId 团队id
	* proid 大赛或项目id
	* userId 创建者id
	* actywId 业务id
	* */



	public int getWeightTotalByTeamId(String teamId,String proId) {
		return dao.getWeightTotalByTeamId(teamId,proId);
	}
	@Transactional(readOnly = false)
	public void updateWeight(TeamUserHistory tur) {
		dao.updateWeight(tur);
	}

	public void updateFinishAsClose(String proid) {
		dao.updateFinishAsClose(proid);
	}

	public void updateDelByProid(String proid) {
		dao.updateDelByProid(proid);
	}
}