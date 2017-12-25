package com.oseasy.initiate.modules.team.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamDetails;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.team.dao.TeamDao;

/**
 * 团队管理Service
 * @author 刘波
 * @version 2017-03-30
 */
@Service
@Transactional(readOnly = true)
public class TeamService extends CrudService<TeamDao, Team> {
	@Autowired
	private OaNotifyService oaNotifyService;
	@Autowired
	private TeamUserRelationService teamUserRelationService;

	public Team get(String id) {
		return super.get(id);
	}

	public List<Team> findList(Team team) {
		return super.findList(team);
	}

	public Page<Team> findPage(Page<Team> page, Team team) {
		return super.findPage(page, team);
	}

	@Transactional(readOnly = false)
	public void save(Team team) {
		try {

			super.save(team);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional(readOnly = false)
	public void delete(Team team) {
		super.delete(team);
	}

	public TeamDetails findTeamDetails(String id) {
		return dao.findTeamDetails(id);
	}

	public List<TeamDetails> findTeamInfo(String id,String usertype) {
		return dao.findTeamInfo(id,usertype);
	}

	@Transactional(readOnly = false)
	public void saveTeamUserRelation(TeamUserRelation teamUserRelation) {
		dao.saveTeamUserRelation(teamUserRelation);
	}

	public List<Team> findTeamUserName(String teamId) {
		return dao.findTeamUserName(teamId);
	}

	@Transactional(readOnly = false)
	public void updateTeamState(Team team) {
		dao.updateTeamState(team);
	}


	/**
	 * 发送通知team负责人  type=5
	 * @param apply_User 申请人User
	 * @param team_User 团队负责人User
	 * @param team 申请团队
	 * @param post 职务
	 * @return >0 成功
	 */
//	@Test
	@Transactional(readOnly = false)
	public int sendOaNotify(User apply_User,User team_User,Team team,String post) {
		try {
			OaNotifyRecord oaNotifyRecord=new OaNotifyRecord();
			OaNotify oaNotify=new OaNotify();
			oaNotify.setTitle(team.getName()+"团队申请记录");
			oaNotify.setContent("收到"+":"+apply_User.getName()+"的申请记录");
			oaNotify.setType("5");
			oaNotify.setsId(team.getId());
/*			oaNotify.preInsert();
			oaNotify.setIsNewRecord(true);
			oaNotify.setId(IdGen.uuid());*/
			oaNotify.setCreateBy(apply_User);
			oaNotify.setCreateDate(new Date());
			oaNotify.setUpdateBy(apply_User);
			oaNotify.setUpdateDate(new Date());
			oaNotify.setEffectiveDate(new Date());
			oaNotify.setStatus("1");
			oaNotify.setSendType("2");

			List<OaNotifyRecord>  recList=new ArrayList<OaNotifyRecord>();
			oaNotifyRecord.setId(IdGen.uuid());
			oaNotifyRecord.setOaNotify(oaNotify);
			oaNotifyRecord.setUser(team_User);
			oaNotifyRecord.setReadFlag("0");
			oaNotifyRecord.setOperateFlag("0");
			recList.add(oaNotifyRecord);

			oaNotify.setOaNotifyRecordList(recList);
			oaNotifyService.save(oaNotify);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;

		}
		return 1;
	}


	/**
	 * 审核信息
	 * @param teamUserRelation 申请人id  申请团队id
	 * @return int >0成功
	 */
	@Transactional(readOnly = false)
	public int checkInfo(TeamUserRelation teamUserRelation) {
		try {
			if (teamUserRelation.getTeamId()!=null && !teamUserRelation.getTeamId().equals("")
			   && teamUserRelation.getUser().getId()!=null && !teamUserRelation.getUser().getId().equals("")) {
				//查询团队里面已经存在的组员人数和申请人的类型
				List<Team> teamNum=dao.findNumByTeamId(teamUserRelation);
				if (teamNum.size()>0) {
					Team teamUserNum=dao.findRealityNum(teamUserRelation.getTeamId());//查询出原始人数
					if (teamUserNum != null) {

						TeamUserRelation  teamUserRelationIsExist = teamUserRelationService.findUserById(teamUserRelation);
						if (teamUserRelationIsExist!=null) {
							return 0;
						}
						teamUserRelation.setState("0");//将状态改为0加入状态
						teamUserRelationService.updateStateByInfo(teamUserRelation);
						Team team= this.get(teamUserRelation.getTeamId());
						teamUserRelationService.repTeamstate(teamUserRelation, team);
					}
					//判断该团队申请人的类型是否达到上限
					//如果上限不执行save并提示审核失败达到上限
					//如果没上限执行save
				}
			}else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}



	/**
	 * 拒绝申请
	 * @param teamUserRelation  拒绝的申请记录
	 *
	 */
	@Transactional(readOnly = false)
	public void disTeam(TeamUserRelation teamUserRelation) {
		try {
			teamUserRelation.setState("3");
			teamUserRelationService.save(teamUserRelation);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 解散团队
	 * @param team
	 *
	 */
	@Transactional(readOnly = false)
	public void disTeam(Team team) {
		try {
			team.setState("2");
			this.save(team);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<TeamDetails> findTeamByTeamId(String id,String usertype) {
		return dao.findTeamByTeamId(id, usertype);
	}

	public Long countBuildByUserId(User curUser) {
		return dao.countBuildByUserId(curUser);
	}

	public List<Team> selectTeamByName(String name) {
		return dao.selectTeamByName(name);
	}

	public  List<Team>   findListByCreatorId(Team team) {return  dao.findListByCreatorId(team);}
}