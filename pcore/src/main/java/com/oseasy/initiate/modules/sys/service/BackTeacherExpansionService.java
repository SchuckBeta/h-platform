package com.oseasy.initiate.modules.sys.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.sys.dao.TeacherKeywordDao;
import com.oseasy.initiate.modules.sys.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.dao.TeamUserRelationDao;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;
import com.oseasy.initiate.modules.oa.dao.OaNotifyDao;
import com.oseasy.initiate.modules.oa.dao.OaNotifyRecordDao;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.sys.dao.BackTeacherExpansionDao;
import com.oseasy.initiate.modules.sys.dao.UserDao;

/**
 * 导师扩展信息表Service
 * 
 * @author l
 * @version 2017-03-31
 */
@Service
@Transactional(readOnly = true)
public class BackTeacherExpansionService extends CrudService<BackTeacherExpansionDao, BackTeacherExpansion> {
	@Autowired
	private UserDao userDao;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private BackTeacherExpansionDao backTeacherExpansionDao;
	@Autowired
	private TeamUserRelationDao relationDao;
	@Autowired
	private OaNotifyRecordDao notifyRecordDao;
	@Autowired
	private OaNotifyDao notifyDao;
	@Autowired
	private TeacherKeywordDao teacherKeywordDao;
	@Autowired
	private TeacherKeywordService teacherKeywordService;

	public BackTeacherExpansion get(String id) {
		return super.get(id);
	}

	public List<BackTeacherExpansion> findList(BackTeacherExpansion backTeacherExpansion) {
		return super.findList(backTeacherExpansion);
	}

	public List<BackTeacherExpansion> findTeacherList(BackTeacherExpansion backTeacherExpansion) {
			return backTeacherExpansionDao.findTeacherList(backTeacherExpansion);
	}

	public Page<BackTeacherExpansion> findPage(Page<BackTeacherExpansion> page,
			BackTeacherExpansion backTeacherExpansion) {
		return super.findPage(page, backTeacherExpansion);
	}

	public Page<BackTeacherExpansion> findTeacherPage(Page<BackTeacherExpansion> page, BackTeacherExpansion backTeacherExpansion) {
		page.setPageSize(6);
		backTeacherExpansion.setPage(page);
		List<BackTeacherExpansion> siteTeacherList=findTeacherList(backTeacherExpansion);
		List<BackTeacherExpansion> siteTeacherListnew =new ArrayList<BackTeacherExpansion> ();
		for(int i=0;i<siteTeacherList.size();i++){
			if(siteTeacherList.get(i).getId()!=null){
				List <String> tes=teacherKeywordService.getStringKeywordByTeacherid(siteTeacherList.get(i).getId());
				if(tes.size()>0){
					siteTeacherList.get(i).setKeywords(tes);
				}
			}
			siteTeacherListnew.add(siteTeacherList.get(i));
		}


		page.setList(siteTeacherListnew);
		return page;
	}

	@Transactional(readOnly = false)
	public void save(BackTeacherExpansion backTeacherExpansion) {
		super.save(backTeacherExpansion);
	}

	@Transactional(readOnly = false)
	public void delete(BackTeacherExpansion backTeacherExpansion) {
		super.delete(backTeacherExpansion);
	}

	@Transactional(readOnly = false)
	public void updateAll(BackTeacherExpansion backTeacherExpansion) {
		try {
			save(backTeacherExpansion);
			User user = backTeacherExpansion.getUser();
			userDao.update(user);
			logger.info("保存老师信息");
		} catch (Exception e) {
			logger.info(e.toString());
			logger.info("异常信息");
		}
	}

	@Transactional(readOnly = false)
	public void saveAll(BackTeacherExpansion backTeacherExpansion) {
		try {
			String uid = IdGen.uuid();
			backTeacherExpansion.getUser().setId(uid);
			backTeacherExpansion.setDelFlag("0");
			save(backTeacherExpansion);
			User user = backTeacherExpansion.getUser();
			//String password = user.getPassword();
			String password = "123456";
			password = SystemService.entryptPassword(password);
			user.setPassword(password);
			user.setLoginName(user.getMobile());
			user.setCreateBy(UserUtils.getUser());
			Date now = new Date();
			user.setCreateDate(now);
			user.setDelFlag("0");
			user.setUserType("2");
			user.setUpdateBy(UserUtils.getUser());
			user.setUpdateDate(now);
			userDao.insert(user);
			logger.info("保存成功");
		} catch (Exception e) {
			logger.info("保存失败");
			logger.error(e.getMessage(),e);
		}
	}

	public List<Office> findOffice() {
		return officeService.findAll();
	}

	@Transactional(readOnly = false)
	public void ivite(BackTeacherExpansion backTeacherExpansion) {
		try {
			String oid = UUID.randomUUID().toString().replaceAll("-", "");
			OaNotify oaNotify = backTeacherExpansion.getOaNotify();
			oaNotify.setId(oid);
			oaNotify.setTitle("邀请通知");
			oaNotify.setCreateBy(UserUtils.getUser());
			oaNotify.setCreateDate(new Date());
			oaNotify.setUpdateBy(UserUtils.getUser());
			oaNotify.setUpdateDate(new Date());
			oaNotify.setDelFlag("0");
			notifyDao.insert(oaNotify);
			String rid = UUID.randomUUID().toString().replaceAll("-", "");
			OaNotifyRecord notifyRecord = new OaNotifyRecord();
			notifyRecord.setId(rid);
			notifyRecord.setUser(backTeacherExpansion.getUser());
			notifyRecord.setOaNotify(oaNotify);
			notifyRecord.setReadFlag("0");
			notifyRecord.setDelFlag("0");
			notifyRecordDao.insert(notifyRecord);
			TeamUserRelation teamUserRelation = backTeacherExpansion.getTeamUserRelation();
			if (teamUserRelation != null) {
				teamUserRelation.setState("2");
				teamUserRelation.setUpdateBy(UserUtils.getUser());
				teamUserRelation.setUpdateDate(new Date());
				teamUserRelation.setUser(backTeacherExpansion.getUser());
				teamUserRelation.setTeamId(backTeacherExpansion.getTeam().getId());
				relationDao.update(teamUserRelation);
			} else {
				String tid = UUID.randomUUID().toString().replaceAll("-", "");
				TeamUserRelation relation = new TeamUserRelation();
				relation.setId(tid);
				relation.setCreateBy(UserUtils.getUser());
				relation.setCreateDate(new Date());
				relation.setDelFlag("0");
				// 发送邀请状态
				relation.setState("2");
				// 导师类型
				relation.setUserType("2");
				relation.setTeamId(backTeacherExpansion.getTeam().getId());
				relation.setUser(backTeacherExpansion.getUser());
				relation.setUpdateBy(UserUtils.getUser());
				relation.setUpdateDate(new Date());
				relationDao.insert(relation);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}

	}

	public List<Team> findTeamByUserId(User user) {
		return backTeacherExpansionDao.findTeamById(user.getId());
	}
	public List<BackTeacherExpansion> findTeacherAward(String userId) {
		return backTeacherExpansionDao.findTeacherAward(userId);
	}
	public String findTeacherIdByUser(String userId) {
		return backTeacherExpansionDao.findTeacherIdByUserId(userId);
	}
	public  BackTeacherExpansion findTeacherByUserId(String userId) {
		return dao.findTeacherByUserId(userId);
	}
	@Transactional(readOnly = false)
	public void updateKeyword(BackTeacherExpansion backTeacherExpansion) {
		if (StringUtil.isNotEmpty(backTeacherExpansion.getId())) {
			teacherKeywordDao.delByTeacherid(backTeacherExpansion.getId());
		}
		if (backTeacherExpansion.getKeywords()!=null) {
			for(String ek:backTeacherExpansion.getKeywords()) {
				TeacherKeyword tk=new TeacherKeyword();
				tk.setKeyword(ek);
				tk.setTeacherId(backTeacherExpansion.getId());
				tk.preInsert();
				teacherKeywordDao.insert(tk);
			}
		}
	}
/*
	public Page<BackTeacherExpansion> findTeacherExpansionPage(Page<BackTeacherExpansion> page,
			BackTeacherExpansion param) {

		if (page.getPageNo()<=0) {
			page.setPageNo(1);
		}
		int count=dao.findAllList(param);
				getStuExListCount(param);
		param.put("offset", (page.getPageNo()-1)*page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String,String>> listStudentExpansion=null;
		List<BackTeacherExpansion> listSt=new ArrayList<BackTeacherExpansion>();
		if (count>0) {
			listStudentExpansion=dao.findAllList(param);
			for(Map<String,String> map:listStudentExpansion) {
				StudentExpansion newse=get(String.valueOf(map.get("id")));//查出用户基本信息
				List<ProjectExpVo> projectList=projectDeclareService.getExpsByUserId(newse.getUser().getId());//查询项目经历
			    List<gContestUndergo> gContestList=userService.findContestByUserId(newse.getUser().getId()); 
			    
			    if (gContestList.size()>0) {
			    	 newse.setgContestList(gContestList);
			    }
			    if (projectList.size()>0) {
			    	newse.setProjectList(projectList);
			    }
			    listSt.add(newse);
			}
		}
		page.setCount(count);
		page.setList(listSt);
		page.initialize();
		return page;
	}*/
	
}