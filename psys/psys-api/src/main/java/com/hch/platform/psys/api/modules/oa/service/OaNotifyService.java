/**
 *
 */
package com.hch.platform.pcore.modules.oa.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.pcore.common.utils.FtpUtil;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.putil.common.utils.IdGen;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.attachment.enums.FileStepEnum;
import com.hch.platform.pcore.modules.attachment.enums.FileTypeEnum;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.interactive.entity.SysViews;
import com.hch.platform.pcore.modules.oa.dao.OaNotifyDao;
import com.hch.platform.pcore.modules.oa.dao.OaNotifyKeywordDao;
import com.hch.platform.pcore.modules.oa.dao.OaNotifyRecordDao;
import com.hch.platform.pcore.modules.oa.entity.OaNotify;
import com.hch.platform.pcore.modules.oa.entity.OaNotifyKeyword;
import com.hch.platform.pcore.modules.oa.entity.OaNotifyRecord;
import com.hch.platform.pcore.modules.oa.entity.OaNotifySent;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.SystemService;
import com.hch.platform.pcore.modules.sys.service.UserService;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.team.dao.TeamDao;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.entity.TeamUserRelation;
import com.hch.platform.pcore.modules.websocket.WebSockectUtil;
import com.hch.platform.pcore.modules.websocket.WsMsg;
import com.hch.platform.pcore.modules.websocket.WsMsgBtn;

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
	private UserService userService;

	@Autowired
	private SystemService systemService;

	@Autowired
	private TeamDao teamDao;
	@Autowired
	private SysAttachmentService sysAttachmentService;
	
	public OaNotify get(String id) {
		OaNotify entity = dao.get(id);
		return entity;
	}
	public Integer getUnreadCount(String uid){
		return dao.getUnreadCount(uid);
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
			dao.updateViews(map);
		}
		return count;
	}
	public List<Map<String,Object>> getMore(String type,String id,List<String> keys) {
		return dao.getMore(type,id,keys);
	}
	@Transactional(readOnly = false)
	/**
	 * 发送通告
	 * @param apply_User 发送人
	 * @param rec_User 接受人
	 * @param title 标题
	 * @param content 内容
	 * @param type 类型 OaNotify.Type_Enum
	 * @param sid 关联大赛或者项目的id
	 * @return
	 */
	public int sendOaNotifyByType(AbsUser apply_User,AbsUser rec_User,String title,String content, String type, String sid) {
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
			logger.error(e.getMessage(),e);
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
	public String getReadFlag(String oid,String uid){
		return oaNotifyRecordDao.getReadFlag(oid, uid);
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
		/*向浏览器发送消息******************/
		WsMsg wsMsg=new WsMsg();
		wsMsg.setContent(oaNotify.getContent());
		wsMsg.setNotifyId(oaNotify.getId());
		wsMsg.setTeamId(oaNotify.getsId());
		if(OaNotify.Type_Enum.TYPE13.getValue().equals(oaNotify.getType())
				||OaNotify.Type_Enum.TYPE10.getValue().equals(oaNotify.getType())
				||OaNotify.Type_Enum.TYPE11.getValue().equals(oaNotify.getType())
				||OaNotify.Type_Enum.TYPE14.getValue().equals(oaNotify.getType())){
		}else if(OaNotify.Type_Enum.TYPE6.getValue().equals(oaNotify.getType())){
			WsMsgBtn b1=new WsMsgBtn();
			b1.setName("接受");
			b1.setClick("notifyModule.acceptP(this,'"+oaNotify.getId()+"')");
			wsMsg.addBtn(b1);
			WsMsgBtn b2=new WsMsgBtn();
			b2.setName("拒绝");
			b2.setClick("notifyModule.refuseP(this,'"+oaNotify.getId()+"')");
			wsMsg.addBtn(b2);
			WsMsgBtn b3=new WsMsgBtn();
			b3.setName("查看详情");
			b3.setClick("notifyModule.openViewP(this,'"+oaNotify.getsId()+"','"+oaNotify.getId()+"')");
			wsMsg.addBtn(b3);
		}else if(OaNotify.Type_Enum.TYPE7.getValue().equals(oaNotify.getType())){
			WsMsgBtn b1=new WsMsgBtn();
			b1.setName("查看详情");
			b1.setClick("notifyModule.openViewP(this,'"+oaNotify.getsId()+"','"+oaNotify.getId()+"')");
			wsMsg.addBtn(b1);
		}else if(OaNotify.Type_Enum.TYPE5.getValue().equals(oaNotify.getType())){
			WsMsgBtn b1=new WsMsgBtn();
			b1.setName("接受");
			b1.setClick("notifyModule.acceptP(this,'"+oaNotify.getId()+"')");
			wsMsg.addBtn(b1);
			WsMsgBtn b2=new WsMsgBtn();
			b2.setName("拒绝");
			b2.setClick("notifyModule.refuseP(this,'"+oaNotify.getId()+"')");
			wsMsg.addBtn(b2);
		}
		for(OaNotifyRecord onr:oaNotify.getOaNotifyRecordList()){
			WebSockectUtil.pushToRedis(onr.getUser().getId(), wsMsg);
		}
		/*向浏览器发送消息******************/
	}

	@Transactional(readOnly = false)
	public void saveCollege(OaNotify oaNotify) {
		super.save(oaNotify);
		// 更新发送接受人记录
		oaNotifyRecordDao.deleteByOaNotifyId(oaNotify.getId());
		String officeIds=oaNotify.getOaNotifyRecordIds();
		List<OaNotifyRecord> oaNotifyRecordList = Lists.newArrayList();
		for (String id : StringUtil.split(officeIds, ",")) {
			List<AbsUser> list = systemService.findUserByOfficeId(id);
			for(AbsUser user:list) {
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
		oaNotifyRecordDao.updateReadFlag(oaNotifyRecord);
	}
	/**
	 * 更新操作状态
	 */
	@Transactional(readOnly = false)
	public void updateOperateFlag(OaNotify oaNotify) {
		OaNotifyRecord oaNotifyRecord = new OaNotifyRecord(oaNotify);
		oaNotifyRecord.setUser(oaNotifyRecord.getCurrentUser());
		oaNotifyRecord.setOperateFlag("1");
		oaNotifyRecordDao.updateOperateFlag(oaNotifyRecord);
	}
	/**
	 * 更新阅读、操作状态
	 */
	@Transactional(readOnly = false)
	public void updateReadOperateFlag(OaNotify oaNotify) {
		OaNotifyRecord oaNotifyRecord = new OaNotifyRecord(oaNotify);
		oaNotifyRecord.setUser(oaNotifyRecord.getCurrentUser());
		oaNotifyRecord.setReadDate(new Date());
		oaNotifyRecord.setReadFlag("1");
		oaNotifyRecord.setOperateFlag("1");
		oaNotifyRecordDao.updateReadOperateFlag(oaNotifyRecord);
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
	private boolean saveoaNotifyContent(OaNotify es){
		Map<String,String> map=sysAttachmentService.moveAndSaveTempFile(es.getContent(), es.getId(), FileTypeEnum.S8, FileStepEnum.S801);
		if("1".equals(map.get("ret"))){
			es.setContent(map.get("content"));
			return true;
		}
		return false;
	}
	@Transactional(readOnly = false)
	public void saveCollegeBroadcast(OaNotify oaNotify) {
		if(oaNotify.getViews()==null){
			oaNotify.setViews("0");
		}
		if (StringUtil.isNotEmpty(oaNotify.getContent())) {
			oaNotify.setContent(oaNotify.getContent().replaceAll(FtpUtil.FTP_HTTPURL, FtpUtil.FTP_MARKER));
		}
		super.save(oaNotify);
		//处理内容里的临时url---start(需要在entity保存之后，需要id)
		//反转义
		oaNotify.setContent(StringEscapeUtils.unescapeHtml4(oaNotify.getContent()));
		//处理之前替换回占位字符串
		oaNotify.setContent(oaNotify.getContent().replaceAll(FtpUtil.FTP_MARKER,FtpUtil.FTP_HTTPURL));
		boolean ret=saveoaNotifyContent(oaNotify);
		if(ret){
			oaNotify.setContent(oaNotify.getContent().replaceAll(FtpUtil.FTP_HTTPURL, FtpUtil.FTP_MARKER));
			//转义
			oaNotify.setContent(StringEscapeUtils.escapeHtml4(oaNotify.getContent()));
			super.save(oaNotify);//更新
		}
		//处理内容里的临时url---end
		//处理关键字
		if("4".equals(oaNotify.getType())||"8".equals(oaNotify.getType())||"9".equals(oaNotify.getType())){
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
				List<AbsUser> list = systemService.findUserByOfficeId(id);
				for(AbsUser user:list) {
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
		return dao.loginList(number);
	}

	public OaNotifyRecord getMine(OaNotifyRecord oaNotifyRecord) {
		return oaNotifyRecordDao.getMine(oaNotifyRecord);

	}

	public List<OaNotify> unReadOaNotifyList(OaNotify oaNotify) {
		return dao.unReadOaNotifyList(oaNotify);

	}
	@Transactional(readOnly = false)
	public List<OaNotifySent> unRead(OaNotify oaNotify) {
		List<OaNotifySent> list1 =null;
		try {
		AbsUser acceptUser = UserUtils.getUser();
		oaNotify.setIsSelf(true);
		oaNotify.setReadFlag("0");
		oaNotify.setCurrentUser(acceptUser);
		List<OaNotify> list = new ArrayList<OaNotify>();
	    list1 = new ArrayList<OaNotifySent>();
		list = unReadOaNotifyList(oaNotify);
		for (OaNotify oaNotify2 : list) {
			OaNotifySent oaNotifySent = new OaNotifySent();
			String userId = oaNotify2.getCreateBy().getId();
			AbsUser sentUser = userService.findUserById(userId);
			TeamUserRelation teamUserRelation = new TeamUserRelation();
			teamUserRelation.setCreateBy(sentUser);
			teamUserRelation.setUser(acceptUser);
			Team team = teamDao.get(oaNotify2.getsId());
			if (team != null) {
				oaNotifySent.setTeamName(team.getName());
			}
			oaNotifySent.setType(oaNotify2.getType());
			oaNotifySent.setNotifyId(oaNotify2.getId());
			
			if ((sentUser != null) && StringUtil.isNotEmpty(sentUser.getName())) {
				oaNotifySent.setSentName(sentUser.getName());
			}
			oaNotifySent.setTeamId(oaNotify2.getsId());
			oaNotifySent.setContent(oaNotify2.getContent());
			list1.add(oaNotifySent);
		}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return list1;
	}


	/** 检验是否邀请过
	 * @param userId
	 * @param teamId
	 * @return
	 */
	public Integer findNotifyCount(String userId,String teamId) {
		return dao.findNotifyCount( userId,teamId);
	}

	public OaNotify findOaNotifyByTeamID(String userId,String sId) {
		return dao.findOaNotifyByTeamID(userId, sId);

	}

	@Transactional(readOnly = false)
	public void deleteRec(OaNotify oaNotify) {
		AbsUser currUser = UserUtils.getUser();
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
		AbsUser recUser = userService.findUserById(oaNotify.getUserId());
		oaNotifyRecord.setUser(recUser);
		oaNotifyRecord=oaNotifyRecordDao.getMine(oaNotifyRecord);
		oaNotifyRecordDao.delete(oaNotifyRecord);
	}
}