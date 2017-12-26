package com.oseasy.initiate.modules.sys.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.service.CrudService;
import com.oseasy.initiate.common.utils.IdGen;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.oa.dao.OaNotifyRecordDao;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.sys.dao.StudentExpansionDao;
import com.oseasy.initiate.modules.sys.entity.GContestUndergo;
import com.oseasy.initiate.modules.sys.entity.Office;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.dao.TeamUserRelationDao;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

import net.sf.json.JSONObject;

/**
 * 学生扩展信息表Service
 * @author zy
 * @version 2017-03-27
 */
@Service
@Transactional(readOnly = true)
public class StudentExpansionService extends CrudService<StudentExpansionDao, StudentExpansion> {

	@Autowired
	private UserService userService;	
	@Autowired
	private OaNotifyRecordDao recordDao;
	@Autowired
	private OaNotifyService oaNotifyService;
	@Autowired
	private TeamUserRelationDao relationDao;
	@Autowired
	private StudentExpansionDao studentExpansionDao;
	@Autowired
	private OfficeService officeService;
	@Autowired
	private ProjectDeclareService projectDeclareService;
	@Autowired
	private TeamUserRelationService teamUserRelationService;
	public List<ProjectExpVo> findProjectByStudentId(String id) {
		return dao.findProjectByStudentId(id);
	}

	public List<GContestUndergo> findGContestByStudentId(String id) {
		return dao.findGContestByStudentId(id);
	}
	@Transactional(readOnly = false)
	public JSONObject toInvite(String userIds,String userType,String teamId) {
		JSONObject js=new JSONObject();
		List<String> list=new ArrayList<String>();
		js.put("ret", 0);
		js.put("msg", "邀请失败");
		int count=0;
		if(StringUtil.isEmpty(userIds)||StringUtil.isEmpty(userType)||StringUtil.isEmpty(teamId)){
			js.put("msg", "参数错误");
			return js;
		}
		for(String tid:teamId.split(",")){
			JSONObject tem=teamUserRelationService.frontToInvite(null, userIds, userType, tid);
			if(tem.getBoolean("success")&&tem.getInt("res")!=0){
				count++;
				list.add(tid);
			}
		}
		if(count==0){
			js.put("msg", "邀请失败(人员已满或者该团队不在建设中或者检查该人员是否已在团队)");
			return js;
		}
		if(count==teamId.split(",").length){
			js.put("msg", "邀请成功");
			js.put("teamIds",  org.apache.commons.lang3.StringUtils.join(list, ","));
			js.put("ret", 1);
			return js;
		}
		if(count<=teamId.split(",").length){
			js.put("msg", "成功为"+count+"个团队发出邀请(人员已满或者该团队不在建设中或者检查该人员是否已在团队)");
			js.put("teamIds",  org.apache.commons.lang3.StringUtils.join(list, ","));
			js.put("ret", 1);
			return js;
		}
		return js;
	}
	public StudentExpansion get(String id) {
		return super.get(id);
	}
	
	public List<StudentExpansion> findList(StudentExpansion studentExpansion) {
		return super.findList(studentExpansion);
	}
	
	public Page<StudentExpansion> findPage(Page<StudentExpansion> page, StudentExpansion studentExpansion) {
		return super.findPage(page, studentExpansion);
	}
	
	@Transactional(readOnly = false)
	public void save(StudentExpansion studentExpansion) {
		super.save(studentExpansion);
	}
	
	@Transactional(readOnly = false)
	public void delete(StudentExpansion studentExpansion) {
		super.delete(studentExpansion);
	}

	@Transactional(readOnly = false)
	public void saveAll(StudentExpansion studentExpansion) {
		String id = IdGen.uuid();
		studentExpansion.getUser().setId(id);
		studentExpansion.getUser().setCreateBy(UserUtils.getUser());
		Date now = new Date();
		studentExpansion.getUser().setCreateDate(now);
		studentExpansion.getUser().setUpdateBy(UserUtils.getUser());
		studentExpansion.getUser().setUpdateDate(now);
		studentExpansion.getUser().setUserType("1");
		studentExpansion.getUser().setDelFlag("0");
		String loginName = studentExpansion.getUser().getMobile();
		studentExpansion.getUser().setLoginName(loginName);
		//String password = studentExpansion.getUser().getPassword();
		String password = "123456";
		password = SystemService.entryptPassword(password);
		studentExpansion.getUser().setPassword(password);
		
		
		String companyId = officeService.selelctParentId(studentExpansion.getUser().getOffice().getId());
		studentExpansion.getUser().setCompany(new Office());
		studentExpansion.getUser().getCompany().setId(companyId);//设置学校id
		
		try {
			logger.info("保存到学生扩展信息表");
			save(studentExpansion);
			logger.info("保存到用户表");
			userService.saveUser(studentExpansion.getUser());
			logger.info("保存完成");
		} catch (Exception e) {
			logger.error("操作异常");
			logger.error(e.getMessage(),e);
		}
	}
	@Transactional(readOnly = false)
	public void updateAll(StudentExpansion studentExpansion) {
		try {
			save(studentExpansion);
			userService.updateUser(studentExpansion.getUser());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	@Transactional(readOnly = false)
	public void invite(StudentExpansion studentExpansion) {
		try {
			OaNotify oaNotify = new OaNotify();
			oaNotify.setTitle("邀请信息");
			oaNotify.setCreateBy(UserUtils.getUser());
			oaNotify.setCreateDate(new Date());
			oaNotify.setUpdateBy(UserUtils.getUser());
			oaNotify.setUpdateDate(new Date());
			oaNotify.setDelFlag("0");
			oaNotifyService.save(oaNotify);
			OaNotifyRecord notifyRecord = new OaNotifyRecord();
			notifyRecord.setCreateBy(UserUtils.getUser());
			String rid = UUID.randomUUID().toString().replaceAll("-", "");
			notifyRecord.setId(rid);
			notifyRecord.setUser(studentExpansion.getUser());
			notifyRecord.setOaNotify(oaNotify);
			notifyRecord.setReadFlag("0");
			notifyRecord.setDelFlag("0");
			recordDao.insert(notifyRecord);		
			TeamUserRelation teamUserRelation = new TeamUserRelation();
			if (teamUserRelation != null) {
				teamUserRelation.setState("2");
				teamUserRelation.setUpdateBy(UserUtils.getUser());
				teamUserRelation.setUpdateDate(new Date());
				teamUserRelation.setUser(studentExpansion.getUser());
				teamUserRelation.setTeamId(studentExpansion.getTeam().getId());
				relationDao.update(teamUserRelation);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

    public StudentExpansion getByUserId(String userId) {
		return studentExpansionDao.getByUserId(userId);
	}

	public Page<StudentExpansion> findStudentPage(Page<StudentExpansion> page, Map<String, Object> param) {

		if (page.getPageNo()<=0) {
			page.setPageNo(1);
		}
		int count=dao.getStuExListCount(param);
		param.put("offset", (page.getPageNo()-1)*page.getPageSize());
		param.put("pageSize", page.getPageSize());
		List<Map<String,String>> listStudentExpansion=null;
		List<StudentExpansion> listSt=new ArrayList<StudentExpansion>();
		if (count>0) {
			listStudentExpansion=dao.getStuExList(param);
			for(Map<String,String> map:listStudentExpansion) {
				StudentExpansion newse=get(String.valueOf(map.get("id")));//查出用户基本信息
				List<ProjectExpVo> projectList=projectDeclareService.getExpsByUserId(newse.getUser().getId());//查询项目经历
			    List<GContestUndergo> gContestList=userService.findContestByUserId(newse.getUser().getId());
			    
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
	}

	public List<StudentExpansion> getStudentByTeamId(String teamId) {
		return studentExpansionDao.getStudentByTeamId(teamId);
	}
}