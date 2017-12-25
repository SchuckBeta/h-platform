/**
 *
 */
package com.oseasy.initiate.modules.oa.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.CacheUtils;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.interactive.entity.SysViews;
import com.oseasy.initiate.modules.oa.dao.OaNotifyDao;
import com.oseasy.initiate.modules.oa.dao.OaNotifyKeywordDao;
import com.oseasy.initiate.modules.oa.dao.OaNotifyRecordDao;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyKeyword;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.oa.entity.OaNotifySent;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.dao.TeamDao;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;

/**
 * 通知通告Service

 * @version 2014-05-16
 */
@Service
@Transactional(readOnly = true)
public class OaNotifyService extends CrudService<OaNotifyDao, OaNotify> {
	@Autowired
	private OaNotifyKeywordDao oaNotifyKeywordDao;
	@Autowired
	private OaNotifyRecordDao oaNotifyRecordDao;

	@Autowired
	private OaNotifyDao oaNotifyDao;

	@Autowired
	private UserService userService;

	@Autowired
	private SystemService systemService;

	@Autowired
	private TeamDao teamDao;

	public OaNotify get(String id) {
		OaNotify entity = dao.get(id);
		return entity;
	}
	/**
	 *双创动态浏览量队列的处理
	 * @return 处理的数据条数
	 */
	@Transactional(readOnly = false)
	public int handleViews(){
		Map<String,Integer> map=new HashMap<String,Integer>();//需要更新浏览量数量的map
		int tatol=10000;
		int count=0;
		Integer up=null;
		SysViews sv=(SysViews)CacheUtils.rpop(CacheUtils.DYNAMIC_VIEWS_QUEUE);
		while(count<tatol&&sv!=null){
			count++;//增加了一条数据
			up=map.get(sv.getForeignId());
			if(up==null){
				map.put(sv.getForeignId(), 1);	
			}else{
				map.put(sv.getForeignId(), up+1);
			}
			if(count<tatol){
				sv=(SysViews)CacheUtils.rpop(CacheUtils.DYNAMIC_VIEWS_QUEUE);
			}
		}
		if(count>0){//有数据需要处理
			oaNotifyDao.updateViews(map);
		}
		return count;
	}
	public List<Map<String,Object>> getMore(String type,String id,List<String> keys) {
		return oaNotifyDao.getMore(type,id,keys);
	}
	@Transactional(readOnly = false)
	/**
	 * 发送通告
	 * @param apply_User 发送人
	 * @param rec_User 接受人
	 * @param title 标题
	 * @param content 内容
	 * @param type 类型
	 * @param sid 关联大赛或者项目的id
	 * @return
	 */
	public int sendOaNotifyByType(User apply_User,User rec_User,String title,String content, String type, String sid) {
		try {
			OaNotify oaNotify=new OaNotify();
			oaNotify.setTitle(title);
			oaNotify.setContent(content);
			oaNotify.setType(type);
			oaNotify.setsId(sid);
			oaNotify.setCreateBy(apply_User);
			oaNotify.setCreateDate(new Date());
			oaNotify.setUpdateBy(apply_User);
			oaNotify.setUpdateDate(new Date());
			oaNotify.setEffectiveDate(new Date());
			oaNotify.setStatus("1");
			oaNotify.setSendType("2");

			OaNotifyRecord oaNotifyRecord=new OaNotifyRecord();
			List<OaNotifyRecord> recList=new ArrayList<OaNotifyRecord>();
			oaNotifyRecord.setId(IdGen.uuid());
			oaNotifyRecord.setOaNotify(oaNotify);
			oaNotifyRecord.setUser(rec_User);
			oaNotifyRecord.setReadFlag("0");
			oaNotifyRecord.setOperateFlag("0");
			recList.add(oaNotifyRecord);
			oaNotify.setOaNotifyRecordList(recList);
			this.save(oaNotify);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;

		}
		return 1;
	}

	/**
	 * 获取通知发送记录
	 * @param oaNotify
	 * @return
	 */
	public OaNotify getRecordList(OaNotify oaNotify) {
		oaNotify.setOaNotifyRecordList(oaNotifyRecordDao.findList(new OaNotifyRecord(oaNotify)));
		return oaNotify;
	}

	public Page<OaNotify> find(Page<OaNotify> page, OaNotify oaNotify) {
		oaNotify.setPage(page);
		page.setList(dao.findList(oaNotify));
		return page;
	}

	public Page<OaNotify> findSend(Page<OaNotify> page, OaNotify oaNotify) {
		oaNotify.setPage(page);
		page.setList(dao.findSendList(oaNotify));
		return page;
	}

	/**
	 * 获取通知数目
	 * @param oaNotify
	 * @return
	 */
	public Long findCount(OaNotify oaNotify) {
		return dao.findCount(oaNotify);
	}

	@Transactional(readOnly = false)
	public void save(OaNotify oaNotify) {
		super.save(oaNotify);
		// 更新发送接受人记录
		oaNotifyRecordDao.deleteByOaNotifyId(oaNotify.getId());
		if (oaNotify.getOaNotifyRecordList().size() > 0) {
			oaNotifyRecordDao.insertAll(oaNotify.getOaNotifyRecordList());
		}
	}

	@Transactional(readOnly = false)
	public void saveCollege(OaNotify oaNotify) {
		super.save(oaNotify);
		// 更新发送接受人记录
		oaNotifyRecordDao.deleteByOaNotifyId(oaNotify.getId());
		String officeIds=oaNotify.getOaNotifyRecordIds();
		List<OaNotifyRecord> oaNotifyRecordList = Lists.newArrayList();
		for (String id : StringUtil.split(officeIds, ",")) {
			List<User> list = systemService.findUserByOfficeId(id);
			for(User user:list) {
				if (user.getUserType().equals("1")) {
					OaNotifyRecord entity = new OaNotifyRecord();
					entity.setId(IdGen.uuid());
					entity.setOaNotify(oaNotify);
					entity.setUser(user);
					entity.setReadFlag("0");
					oaNotifyRecordList.add(entity);
				}
			}
		}
		if (oaNotifyRecordList.size() > 0) {
			oaNotifyRecordDao.insertAll(oaNotifyRecordList);
		}
	}


	/**
	 * 更新阅读状态
	 */
	@Transactional(readOnly = false)
	public void updateReadFlag(OaNotify oaNotify) {
		OaNotifyRecord oaNotifyRecord = new OaNotifyRecord(oaNotify);
		oaNotifyRecord.setUser(oaNotifyRecord.getCurrentUser());
		oaNotifyRecord.setReadDate(new Date());
		oaNotifyRecord.setReadFlag("1");
		oaNotifyRecordDao.update(oaNotifyRecord);
	}

	/**
	 * 保存广播通知
	 */
	@Transactional(readOnly = false)
	public void saveBroadcast(OaNotify oaNotify) {
		super.save(oaNotify);
		// 更新发送接受人记录
		if ("2".equals(oaNotify.getSendType())) {
			oaNotifyRecordDao.deleteByOaNotifyId(oaNotify.getId());
			if (oaNotify.getOaNotifyRecordList().size() > 0) {
				oaNotifyRecordDao.insertAll(oaNotify.getOaNotifyRecordList());
			}
		}
	}

	@Transactional(readOnly = false)
	public void saveCollegeBroadcast(OaNotify oaNotify) {
		super.save(oaNotify);
		//处理关键字
		if("4".equals(oaNotify.getType())||"8".equals(oaNotify.getType())||"9".equals(oaNotify.getType())){
			if(oaNotify.getViews()==null){
				oaNotify.setViews("0");
			}
			super.save(oaNotify);
			if (StringUtil.isNotEmpty(oaNotify.getId())) {
				oaNotifyKeywordDao.delByEsid(oaNotify.getId());
			}
			if (oaNotify.getKeywords()!=null) {
				for(String ek:oaNotify.getKeywords()) {
					OaNotifyKeyword ekk=new OaNotifyKeyword();
					ekk.setKeyword(ek);
					ekk.setNotifyId(oaNotify.getId());
					ekk.preInsert();
					oaNotifyKeywordDao.insert(ekk);
				}
			}
		}
		// 更新发送接收人记录
		if ("2".equals(oaNotify.getSendType())) {
			// 更新发送接受人记录
			oaNotifyRecordDao.deleteByOaNotifyId(oaNotify.getId());
			String officeIds=oaNotify.getOaNotifyRecordIds();
			List<OaNotifyRecord> oaNotifyRecordList = Lists.newArrayList();
			for (String id : StringUtil.split(officeIds, ",")) {
				List<User> list = systemService.findUserByOfficeId(id);
				for(User user:list) {
					OaNotifyRecord entity = new OaNotifyRecord();
					entity.setId(IdGen.uuid());
					entity.setOaNotify(oaNotify);
					entity.setUser(user);
					entity.setReadFlag("0");
					oaNotifyRecordList.add(entity);
				}
			}
			if (oaNotifyRecordList.size() > 0) {
				oaNotifyRecordDao.insertAll(oaNotifyRecordList);
			}
		}
	}

	public Page<OaNotify> findLoginPage(Page<OaNotify> page, OaNotify oaNotify) {
		oaNotify.setPage(page);
		page.setList(dao.findLoginList(oaNotify));
		return page;
	}

	@Transactional(readOnly = true)
	public List<OaNotify> loginList(Integer number) {
		return oaNotifyDao.loginList(number);
	}

	public OaNotifyRecord getMine(OaNotifyRecord oaNotifyRecord) {
		return oaNotifyRecordDao.getMine(oaNotifyRecord);

	}

	public List<OaNotify> unReadOaNotifyList(OaNotify oaNotify) {
		return oaNotifyDao.unReadOaNotifyList(oaNotify);

	}
	@Transactional(readOnly = false)
	public List<OaNotifySent> unRead(OaNotify oaNotify) {
		List<OaNotifySent> list1 =null;
		try {
		User acceptUser = UserUtils.getUser();
		oaNotify.setIsSelf(true);
		oaNotify.setReadFlag("0");
		oaNotify.setCurrentUser(acceptUser);
		List<OaNotify> list = new ArrayList<OaNotify>();
	    list1 = new ArrayList<OaNotifySent>();
		list = unReadOaNotifyList(oaNotify);
		for (OaNotify oaNotify2 : list) {
			OaNotifySent oaNotifySent = new OaNotifySent();
			String userId = oaNotify2.getCreateBy().getId();
			User sentUser = userService.findUserById(userId);
			TeamUserRelation teamUserRelation = new TeamUserRelation();
			teamUserRelation.setCreateBy(sentUser);
			teamUserRelation.setUser(acceptUser);
		/*	ProjectDeclare projectDeclare = projectDeclareService.getProjectByTimeId(oaNotify2.getsId());
			if (projectDeclare != null) {
				oaNotifySent.setProjectName(projectDeclare.getName());
			}*/
			/*Team team = teamService.get(oaNotify2.getsId());*/
			//oaNotifySent.setTeamName(team.getName());
			Team team = teamDao.get(oaNotify2.getsId());
			if (team != null) {
				oaNotifySent.setTeamName(team.getName());
				oaNotifySent.setType(oaNotify2.getType());
				oaNotifySent.setNotifyId(oaNotify2.getId());

				if ((sentUser != null) && StringUtil.isNotEmpty(sentUser.getName())) {
					oaNotifySent.setSentName(sentUser.getName());
				}
				oaNotifySent.setTeamId(oaNotify2.getsId());
				oaNotifySent.setContent(oaNotify2.getContent());
				list1.add(oaNotifySent);
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list1;
	}


	/** 检验是否邀请过
	 * @param userId
	 * @param teamId
	 * @return
	 */
	public Integer findNotifyCount(String userId,String teamId) {
		return oaNotifyDao.findNotifyCount( userId,teamId);
	}

	public OaNotify findOaNotifyByTeamID(String userId,String sId) {
		return oaNotifyDao.findOaNotifyByTeamID(userId, sId);

	}

	@Transactional(readOnly = false)
	public void deleteRec(OaNotify oaNotify) {
		User currUser = UserUtils.getUser();
		OaNotifyRecord oaNotifyRecord=new OaNotifyRecord();
		oaNotifyRecord.setOaNotify(oaNotify);
		oaNotifyRecord.setUser(currUser);
		oaNotifyRecord=oaNotifyRecordDao.getMine(oaNotifyRecord);
		oaNotifyRecordDao.delete(oaNotifyRecord);
	}
	@Transactional(readOnly = false)
	public void deleteSend(OaNotify oaNotify) {

		OaNotifyRecord oaNotifyRecord=new OaNotifyRecord();
		oaNotifyRecord.setOaNotify(oaNotify);
		User recUser = userService.findUserById(oaNotify.getUserId());
		oaNotifyRecord.setUser(recUser);
		oaNotifyRecord=oaNotifyRecordDao.getMine(oaNotifyRecord);
		oaNotifyRecordDao.delete(oaNotifyRecord);
	}
}