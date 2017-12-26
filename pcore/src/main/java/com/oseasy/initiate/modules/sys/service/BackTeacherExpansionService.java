package com.oseasy.initiate.modules.sys.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.config.SysIds;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.IdUtils;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.oa.dao.OaNotifyDao;
import com.oseasy.initiate.modules.oa.dao.OaNotifyRecordDao;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.sys.dao.BackTeacherExpansionDao;
import com.oseasy.initiate.modules.sys.dao.RoleDao;
import com.oseasy.initiate.modules.sys.dao.TeacherKeywordDao;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.BackTeacherExpansion;
import com.oseasy.initiate.modules.sys.entity.GContestUndergo;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.TeacherKeyword;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.RegexUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.dao.TeamDao;
import com.oseasy.initiate.modules.team.dao.TeamUserRelationDao;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

import net.sf.json.JSONObject;

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
	private TeamUserRelationDao relationDao;
	@Autowired
	private OaNotifyRecordDao notifyRecordDao;
	@Autowired
	private OaNotifyDao notifyDao;
	@Autowired
	private TeacherKeywordDao teacherKeywordDao;
	@Autowired
	private TeacherKeywordService teacherKeywordService;
	@Autowired
	private TeamUserRelationService teamUserRelationService;
	@Autowired
	private TeamDao teamDao;
	
	@Autowired
	private RoleDao roleDao;
	@Transactional(readOnly = false)
	public JSONObject addTeacherByStu(String name,String no,String mobile,String type,String office,String profes){
		if(name!=null){
			name=name.trim();
		}
		if(no!=null){
			no=no.trim();
		}
		if(mobile!=null){
			mobile=mobile.trim();
		}
		if(type!=null){
			type=type.trim();
		}
		JSONObject js=new JSONObject();
		js.put("ret", 0);
		if(StringUtil.isEmpty(name)){
			js.put("msg", "请填写导师名称");
			return js;
		}
		if(StringUtil.isEmpty(type)){
			js.put("msg", "请选择导师来源");
			return js;
		}
		if(StringUtil.isEmpty(mobile)){
			js.put("msg", "请填写手机号");
			return js;
		}
		if("1".equals(type)&&StringUtil.isEmpty(name)){
			js.put("msg", "请填写导师工号");
			return js;
		}
		if (!Pattern.matches(RegexUtils.mobileRegex, mobile)) {
			js.put("msg", "手机号格式不正确");
			return js;
		}
		if (StringUtil.isNotEmpty(mobile)&&UserUtils.isExistMobile(mobile)) {
			js.put("msg", "手机号已存在");
			return js;
		}
		if (StringUtil.isNotEmpty(no)&&UserUtils.isExistNo(no)) {
			js.put("msg", "工号已存在");
			return js;
		}
		if(StringUtil.isEmpty(no)){
			no=getAutoNo();
		}
		BackTeacherExpansion tc=new BackTeacherExpansion();
		tc.setTeachertype(type);
		User nuser=new User();
		nuser.setName(name);
		nuser.setNo(no);
		nuser.setMobile(mobile);
		if(StringUtil.isNotEmpty(office)){
			nuser.setOffice(new Office(office));
		}
		if(StringUtil.isNotEmpty(profes)){
			nuser.setProfessional(profes);
		}
		User user = UserUtils.getUser();
		nuser.setPassword(SystemService.entryptPassword("123456"));
		List<Role> roleList=new ArrayList<Role>();
		roleList.add(roleDao.get(SysIds.SYS_ROLE_USER.getId()));
		nuser.setRoleList(roleList);
		nuser.setId(IdGen.uuid());
		nuser.setUserType("2");
		if (StringUtils.isNotBlank(user.getId())) {
			nuser.setUpdateBy(user);
			nuser.setCreateBy(user);
		}
		nuser.setUpdateDate(new Date());
		nuser.setCreateDate(nuser.getUpdateDate());
		userDao.insert(nuser);
		userDao.insertUserRole(nuser);
		tc.setUser(nuser);
		tc.setId(IdGen.uuid());
		if (StringUtils.isNotBlank(user.getId())) {
			tc.setUpdateBy(user);
			tc.setCreateBy(user);
		}
		tc.setUpdateDate(new Date());
		tc.setCreateDate(tc.getUpdateDate());
		dao.insert(tc);
		js.put("ret", 1);
		js.put("msg", "添加成功");
		return js;
	}
	private String getAutoNo(){
		String no=IdUtils.getDictNumberByDb();
		if(UserUtils.isExistNo(no)){
			return getAutoNo();
		}else{
			return no;
		}
	}
	public BackTeacherExpansion getByUserId(String uid) {
		return dao.getByUserId(uid);
	}
	public BackTeacherExpansion get(String id) {
		return super.get(id);
	}

	public List<BackTeacherExpansion> findList(BackTeacherExpansion backTeacherExpansion) {
		return super.findList(backTeacherExpansion);
	}

	public List<BackTeacherExpansion> findTeacherList(BackTeacherExpansion backTeacherExpansion) {
			return dao.findTeacherList(backTeacherExpansion);
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
		if(StringUtil.isNotEmpty(backTeacherExpansion.getId())){
			resetAllTeamState(backTeacherExpansion.getUser().getId());
		}
	}

	@Transactional(readOnly = false)
	public void delete(BackTeacherExpansion backTeacherExpansion) {
		super.delete(backTeacherExpansion);
	}
	//根据用户id重置所有加入的团队状态
	@Transactional(readOnly = false)
	public void resetAllTeamState(String userid) {
		List<Team> list=teamDao.findTeamListByUserId(userid);
		if(list!=null&&list.size()>0){
			for(Team team:list){
				teamUserRelationService.repTeamstate(team);
			}
		}
	}
	@Transactional(readOnly = false)
	public void updateAll(BackTeacherExpansion backTeacherExpansion) {
		try {
			save(backTeacherExpansion);
			User user = backTeacherExpansion.getUser();
			userDao.update(user);

			if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
				UserUtils.clearCache();
				//UserUtils.getCacheMap().clear();
			}

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
			//默认密码
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
	//邀请导师加入团队
	public void ivite(BackTeacherExpansion backTeacherExpansion) {
		try {

			//String oid = UUID.randomUUID().toString().replaceAll("-", "");
			String oid = IdGen.uuid();
			OaNotify oaNotify = backTeacherExpansion.getOaNotify();
			oaNotify.setId(oid);
			oaNotify.setTitle("邀请通知");
			oaNotify.setCreateBy(UserUtils.getUser());
			oaNotify.setCreateDate(new Date());
			oaNotify.setUpdateBy(UserUtils.getUser());
			oaNotify.setUpdateDate(new Date());
			oaNotify.setDelFlag("0");
			notifyDao.insert(oaNotify);
			//String rid = UUID.randomUUID().toString().replaceAll("-", "");
			String rid = IdGen.uuid();
			OaNotifyRecord notifyRecord = new OaNotifyRecord();
			notifyRecord.setId(rid);
			notifyRecord.setUser(backTeacherExpansion.getUser());
			notifyRecord.setOaNotify(oaNotify);
			//readFlag：阅读标记0：未读
			notifyRecord.setReadFlag("0");
			notifyRecord.setDelFlag("0");
			notifyRecordDao.insert(notifyRecord);
			TeamUserRelation teamUserRelation = backTeacherExpansion.getTeamUserRelation();
			if (teamUserRelation != null) {
				//state：2 负责人发送邀请
				teamUserRelation.setState("2");
				teamUserRelation.setUpdateBy(UserUtils.getUser());
				teamUserRelation.setUpdateDate(new Date());
				teamUserRelation.setUser(backTeacherExpansion.getUser());
				teamUserRelation.setTeamId(backTeacherExpansion.getTeam().getId());
				relationDao.update(teamUserRelation);
			} else {
				String tid = IdGen.uuid();;
				TeamUserRelation relation = new TeamUserRelation();
				relation.setId(tid);
				relation.setCreateBy(UserUtils.getUser());
				relation.setCreateDate(new Date());
				relation.setDelFlag("0");
				//state：2 负责人发送邀请
				relation.setState("2");
				// usertype：2 导师来源
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
		return dao.findTeamById(user.getId());
	}
	public List<BackTeacherExpansion> findTeacherAward(String userId) {
		return dao.findTeacherAward(userId);
	}
	public String findTeacherIdByUser(String userId) {
		return dao.findTeacherIdByUserId(userId);
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

	public BackTeacherExpansion findTeacherByTopShow(String teacherType) {
		return dao.findTeacherByTopShow(teacherType);
	}

	public List<BackTeacherExpansion> getQYTeacher(String teamId) {
		return dao.getQYTeacher(teamId);
	}

	public List<BackTeacherExpansion> getXYTeacher(String teamId) {
		return dao.getXYTeacher(teamId);
	}

	public List<ProjectExpVo> findProjectByTeacherId(String id) {
		return dao.findProjectByTeacherId(id);
	}

	public List<GContestUndergo> findGContestByTeacherId(String id) {
		return dao.findGContestByTeacherId(id);
	}
}