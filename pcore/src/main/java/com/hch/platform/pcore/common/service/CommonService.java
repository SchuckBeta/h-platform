/**
 * 
 */
package com.hch.platform.pcore.common.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.putil.common.utils.IdGen;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.actyw.dao.ActYwDao;
import com.hch.platform.pcore.modules.actyw.entity.ActYw;
import com.hch.platform.pcore.modules.gcontest.dao.GContestDao;
import com.hch.platform.pcore.modules.gcontest.entity.GContest;
import com.hch.platform.pcore.modules.project.dao.ProjectDeclareDao;
import com.hch.platform.pcore.modules.project.entity.ProjectDeclare;
import com.hch.platform.pcore.modules.promodel.dao.ProModelDao;
import com.hch.platform.pcore.modules.promodel.entity.ProModel;
import com.hch.platform.pcore.modules.proproject.entity.ProProject;
import com.hch.platform.pcore.modules.sys.dao.BackTeacherExpansionDao;
import com.hch.platform.pcore.modules.sys.entity.BackTeacherExpansion;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.DictUtils;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.sysconfig.utils.SysConfigUtil;
import com.hch.platform.pcore.modules.sysconfig.vo.ConfRange;
import com.hch.platform.pcore.modules.sysconfig.vo.PersonNumConf;
import com.hch.platform.pcore.modules.sysconfig.vo.SysConfigVo;
import com.hch.platform.pcore.modules.team.dao.TeamDao;
import com.hch.platform.pcore.modules.team.dao.TeamUserHistoryDao;
import com.hch.platform.pcore.modules.team.dao.TeamUserRelationDao;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.entity.TeamUserHistory;
import com.hch.platform.pcore.modules.team.entity.TeamUserRelation;

import net.sf.json.JSONObject;

/**
 * Service公共处理逻辑
 *
 * @version 2014-05-16
 */

@Service
@Transactional(readOnly = true)
public  class CommonService {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private TeamUserHistoryDao teamUserHistoryDao;
	@Autowired
	private TeamUserRelationDao teamUserRelationDao;
	@Autowired
	private ActYwDao actYwDao;
	@Autowired
	private ProjectDeclareDao projectDeclareDao;
	@Autowired
	private GContestDao gContestDao;
	@Autowired
	private ProModelDao proModelDao;
	@Autowired
	private BackTeacherExpansionDao backTeacherExpansionDao;
	@Autowired
	private TeamDao teamDao;
	
	//true 表示有变更
	private boolean checkTeamHaveChange(List<TeamUserHistory> stus,List<TeamUserHistory> teas,String teamId){
		Team team=teamDao.get(teamId);
		if(team==null){
			return false;
		}
		String fzr=null;
		for(TeamUserHistory t:stus){
			if("0".equals(t.getUserzz())){
				fzr=t.getUserId();
				break;
			}
		}
		if(!team.getSponsor().equals(fzr)){
			return true;
		}
		List<TeamUserRelation> old=teamUserRelationDao.getByTeamId(teamId);
		if(old==null||old.size()==0){
			return true;
		}
		int stnum=stus.size();
		int teanum=0;
		if(teas!=null){
			teanum=teas.size();
		}
		if(old.size()!=stnum+teanum){
			return true;
		}
		Map<String,String> map=new HashMap<String,String>();
		for(TeamUserRelation t:old){
			map.put(t.getUser().getId(), t.getUser().getId());
		}
		for(TeamUserHistory t:stus){
			if(map.get(t.getUserId())==null){
				return true;
			}
		}
		if(teas!=null&&teas.size()>0){
			for(TeamUserHistory t:teas){
				if(map.get(t.getUserId())==null){
					return true;
				}
			}
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public void disposeTeamUserHistoryForModify(List<TeamUserHistory> stus,List<TeamUserHistory> teas,String actywId,String teamId,String proId) {
		if(checkTeamHaveChange(stus, teas, teamId)){
			Date now=new Date();
			AbsUser cuser=UserUtils.getUser();
			if(stus==null||stus.size()==0){
				throw new RuntimeException("TeamUserHistory不能为空");
			}
			if(StringUtil.isEmpty(actywId)||StringUtil.isEmpty(teamId)||StringUtil.isEmpty(proId)){
				throw new RuntimeException("参数不能为空");
			}
			ActYw actYw=actYwDao.get(actywId);
			if(actYw==null){
				throw new RuntimeException("找不到ActYw");
			}
			ProProject proProject=actYw.getProProject();
			if(proProject==null){
				throw new RuntimeException("找不到ProProject");
			}
			if(StringUtil.isEmpty(proProject.getProType())||StringUtil.isEmpty(proProject.getType())){
				throw new RuntimeException("ProProject的类型不能为空");
			}
			String proType=proProject.getProType();
			String proSubType=proProject.getType();
			String finish=null;
			ProjectDeclare p=projectDeclareDao.get(proId);
			GContest g=gContestDao.get(proId);
			ProModel pm=proModelDao.get(proId);
			if(p!=null){
				if("0".equals(p.getStatus())){
					finish="2";
				}else if("8".equals(p.getStatus())||"9".equals(p.getStatus())){
					finish="1";
				}else{
					finish="0";
				}
			}else if(g!=null){
				if("0".equals(g.getAuditState())){
					finish="2";
				}else if("7".equals(g.getAuditState())){
					finish="1";
				}else{
					finish="0";
				}
			}else if(pm!=null){
				if("1".equals(pm.getState())){
					finish="1";
				}else if(StringUtil.isNotEmpty(pm.getProcInsId())){
					finish="0";
				}else{
					finish="2";
				}
			}
			if("1".equals(finish)){//结束的,只需修改his表信息
				String fzr=null;
				for(TeamUserHistory t:stus){
					if("0".equals(t.getUserzz())){
						fzr=t.getUserId();
						break;
					}
				}
				if("1,".equals(proType)){
					if("1".equals(proSubType)){//大创
						projectDeclareDao.modifyLeaderAndTeam(fzr,null, proId);
					}else{
						proModelDao.modifyLeaderAndTeam(fzr,null, proId);
					}
				}else if("7,".equals(proType)){
					if("1".equals(proSubType)){//互联网+
						gContestDao.modifyLeaderAndTeam(fzr,null, proId);
					}else{
						proModelDao.modifyLeaderAndTeam(fzr,null, proId);
					}
				}
				for(TeamUserHistory tuh:stus){
					tuh.setId(IdGen.uuid());
					tuh.setUser(new AbsUser(tuh.getUserId()));
					tuh.setTeamId(teamId);
					tuh.setCreateBy(cuser);
					tuh.setUpdateBy(cuser);
					tuh.setCreateDate(now);
					tuh.setUpdateDate(now);
					tuh.setProId(proId);
					tuh.setProType(proType);
					tuh.setProSubType(proSubType);
					tuh.setUtype("1");
					tuh.setFinish(finish);
					tuh.setDelFlag("0");
					tuh.setWeightVal(tuh.getWeightVal());
				}
				if(teas!=null&&teas.size()>0){
					for(TeamUserHistory tuh:teas){
						tuh.setId(IdGen.uuid());
						tuh.setUser(new AbsUser(tuh.getUserId()));
						tuh.setTeamId(teamId);
						tuh.setCreateBy(cuser);
						tuh.setUpdateBy(cuser);
						tuh.setCreateDate(now);
						tuh.setUpdateDate(now);
						tuh.setProId(proId);
						tuh.setProType(proType);
						tuh.setProSubType(proSubType);
						tuh.setUtype("2");
						tuh.setFinish(finish);
						tuh.setDelFlag("0");
					};
					stus.addAll(teas);
				}
				teamUserHistoryDao.deleteByProId(proId);
				teamUserHistoryDao.insertAll(stus);
			}else if("0".equals(finish)||"2".equals(finish)){//未结束的
				Integer c=teamUserHistoryDao.getDoingCountByTeamId(teamId);
				if(c!=null&&c>=2){//有两个以上正在进行的项目、大赛,需要新建一个team
					String fzr=null;
					for(TeamUserHistory t:stus){
						if("0".equals(t.getUserzz())){
							fzr=t.getUserId();
							break;
						}
					}
					Team t=teamDao.get(teamId);
					t.setId(IdGen.uuid());
					t.setSponsor(fzr);
					t.setMemberNum(stus.size());
					t.setUpdateBy(cuser);
					t.setCreateBy(cuser);
					t.setCreateDate(now);
					t.setUpdateDate(now);
					getTeamTeacherNum(t, teas);//算出导师人数
					teamDao.insert(t);
					for(TeamUserHistory tuh:stus){
						tuh.setId(IdGen.uuid());
						tuh.setUser(new AbsUser(tuh.getUserId()));
						tuh.setTeamId(t.getId());
						tuh.setCreateBy(cuser);
						tuh.setUpdateBy(cuser);
						tuh.setCreateDate(now);
						tuh.setUpdateDate(now);
						tuh.setProId(proId);
						tuh.setProType(proType);
						tuh.setProSubType(proSubType);
						tuh.setUtype("1");
						tuh.setFinish(finish);
						tuh.setDelFlag("0");
						tuh.setWeightVal(tuh.getWeightVal());
					}
					if(teas!=null&&teas.size()>0){
						for(TeamUserHistory tuh:teas){
							tuh.setId(IdGen.uuid());
							tuh.setUser(new AbsUser(tuh.getUserId()));
							tuh.setTeamId(t.getId());
							tuh.setCreateBy(cuser);
							tuh.setUpdateBy(cuser);
							tuh.setCreateDate(now);
							tuh.setUpdateDate(now);
							tuh.setProId(proId);
							tuh.setProType(proType);
							tuh.setProSubType(proSubType);
							tuh.setUtype("2");
							tuh.setFinish(finish);
							tuh.setDelFlag("0");
						};
						stus.addAll(teas);
					}
					if("1,".equals(proType)){
						if("1".equals(proSubType)){//大创
							projectDeclareDao.modifyLeaderAndTeam(fzr,t.getId(), proId);
						}else{
							proModelDao.modifyLeaderAndTeam(fzr,t.getId(), proId);
						}
					}else if("7,".equals(proType)){
						if("1".equals(proSubType)){//互联网+
							gContestDao.modifyLeaderAndTeam(fzr,t.getId(), proId);
						}else{
							proModelDao.modifyLeaderAndTeam(fzr,t.getId(), proId);
						}
					}
					teamUserRelationDao.insertAll(stus);
					teamUserHistoryDao.deleteByProId(proId);
					teamUserHistoryDao.insertAll(stus);
				}else{//直接同步team信息
					String fzr=null;
					for(TeamUserHistory t:stus){
						if("0".equals(t.getUserzz())){
							fzr=t.getUserId();
							break;
						}
					}
					if("1,".equals(proType)){
						if("1".equals(proSubType)){//大创
							projectDeclareDao.modifyLeaderAndTeam(fzr,null, proId);
						}else{
							proModelDao.modifyLeaderAndTeam(fzr,null, proId);
						}
					}else if("7,".equals(proType)){
						if("1".equals(proSubType)){//互联网+
							gContestDao.modifyLeaderAndTeam(fzr,null, proId);
						}else{
							proModelDao.modifyLeaderAndTeam(fzr,null, proId);
						}
					}
					Team t=teamDao.get(teamId);
					t.setSponsor(fzr);
					t.setMemberNum(stus.size());
					getTeamTeacherNum(t, teas);//算出导师人数
					teamDao.updateAllInfo(t);
					for(TeamUserHistory tuh:stus){
						tuh.setId(IdGen.uuid());
						tuh.setUser(new AbsUser(tuh.getUserId()));
						tuh.setTeamId(teamId);
						tuh.setCreateBy(cuser);
						tuh.setUpdateBy(cuser);
						tuh.setCreateDate(now);
						tuh.setUpdateDate(now);
						tuh.setProId(proId);
						tuh.setProType(proType);
						tuh.setProSubType(proSubType);
						tuh.setUtype("1");
						tuh.setFinish(finish);
						tuh.setDelFlag("0");
						tuh.setWeightVal(tuh.getWeightVal());
					}
					if(teas!=null&&teas.size()>0){
						for(TeamUserHistory tuh:teas){
							tuh.setId(IdGen.uuid());
							tuh.setUser(new AbsUser(tuh.getUserId()));
							tuh.setTeamId(teamId);
							tuh.setCreateBy(cuser);
							tuh.setUpdateBy(cuser);
							tuh.setCreateDate(now);
							tuh.setUpdateDate(now);
							tuh.setProId(proId);
							tuh.setProType(proType);
							tuh.setProSubType(proSubType);
							tuh.setUtype("2");
							tuh.setFinish(finish);
							tuh.setDelFlag("0");
						};
						stus.addAll(teas);
					}
					teamUserRelationDao.deleteByTeamId(teamId);
					teamUserRelationDao.insertAll(stus);
					teamUserHistoryDao.deleteByProId(proId);
					teamUserHistoryDao.insertAll(stus);
				}
			}
		}
	}
	public JSONObject checkProjectTeamForModify(List<TeamUserHistory> tuhs_stu,List<TeamUserHistory> teas,String proid,String actywId,String lowType,String teamid) {
    	String proType="1,";//双创项目
    	JSONObject js=new JSONObject();
    	js.put("ret", 0);
    	if (StringUtil.isEmpty(lowType)) {
			js.put("msg", "请选择项目类别");
			return js;
		}
    	if (tuhs_stu==null||tuhs_stu.size()==0) {
			js.put("msg", "请添加团队成员");
			return js;
		}
		if (StringUtil.isEmpty(actywId)) {
			js.put("msg", "无项目申报配置信息,请联系管理员");
			return js;
		}
		ActYw actYw=actYwDao.get(actywId);
		if (actYw==null||!actYw.getIsDeploy()||"1".equals(actYw.getDelFlag())) {
			js.put("msg", "未找到项目申报配置信息,请联系管理员");
			return js;
		}
		if (actYw.getProProject()==null||StringUtil.isEmpty(actYw.getProProject().getType())||"1".equals(actYw.getProProject().getDelFlag())) {
			js.put("msg", "项目申报配置信息有误,请联系管理员");
			return js;
		}
		boolean haveFzr=false;
		for(TeamUserHistory t:tuhs_stu){
			if("0".equals(t.getUserzz())){
				haveFzr=true;
				break;
			}
		}
		if (!haveFzr) {
			js.put("msg", "请选择负责人");
			return js;
		}
		String subType=actYw.getProProject().getType();//项目分类
    	SysConfigVo scv=SysConfigUtil.getSysConfigVo();
		if(scv!=null){
			Team teamNums=new Team();
			teamNums.setMemberNum(tuhs_stu.size());
			getTeamTeacherNum(teamNums, teas);//算出导师人数
			if("0".equals(scv.getApplyConf().getaOnOff())){//是否允许同一个学生用不同项目既申报项目又参加大赛，1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn1(tuhs_stu, proid,proType)>0){
					js.put("msg", "有团队成员已参加大赛");
					return js;
				}
			}
			if("0".equals(scv.getApplyConf().getProConf().getbOnOff())){//是否允许同一个学生申报多个不同类型的项目,1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn2(tuhs_stu, proid,proType,subType)>0){
					js.put("msg", "有团队成员已申报其他类型的项目");
					return js;
				}
			}
			if("0".equals(scv.getApplyConf().getProConf().getaOnOff())){//同一个项目周期内，是否允许同一个学生在同一类项目中申报多个项目,1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn3(tuhs_stu, proid,proType,subType)>0){
					js.put("msg", "有团队成员已申报该类型的项目");
					return js;
				}
			}
			if ("1".equals(scv.getTeamConf().getIntramuralValiaOnOff())) {
				ConfRange cr = scv.getTeamConf().getIntramuralValia();
				int min = Integer.valueOf(cr.getMin());
				int max = Integer.valueOf(cr.getMax());
				if (teamNums.getSchoolTeacherNum() < min || teamNums.getSchoolTeacherNum() > max) {
					if(min==max){
						js.put("msg",  "校内导师人数为" +  min+ "人!");
					}else{
						js.put("msg", "校内导师人数为" +  min + "-" + max + "人!");
					}
					return js;
				}
			}
			String maxms=scv.getTeamConf().getMaxMembers();
			if(StringUtil.isNotEmpty(maxms)&&teamNums.getMemberNum()>Integer.valueOf(maxms)){
				js.put("msg",  "团队成员人数不能超过" + maxms + "人!");
				return js;
			}
			PersonNumConf pnc=SysConfigUtil.getProPersonNumConf(scv, subType, lowType);
			if(pnc!=null){
				if("1".equals(pnc.getTeamNumOnOff())){//团队人数范围
					ConfRange cr=pnc.getTeamNum();
					int min=Integer.valueOf(cr.getMin());
					int max=Integer.valueOf(cr.getMax());
					if(teamNums.getMemberNum()<min||teamNums.getMemberNum()>max){
						if(min==max){
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目团队成员人数为"+min+"人");
						}else{
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目团队成员人数为"+min+"~"+max+"人");
						}
						return js;
					}
				}
				if("1".equals(pnc.getSchoolTeacherNumOnOff())){//校园导师人数范围
					ConfRange cr=pnc.getSchoolTeacherNum();
					int min=Integer.valueOf(cr.getMin());
					int max=Integer.valueOf(cr.getMax());
					if(teamNums.getSchoolTeacherNum()<min||teamNums.getSchoolTeacherNum()>max){
						if(min==max){
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目团队校园导师为"+min+"人");
						}else{
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目团队校园导师为"+min+"~"+max+"人");
						}
						return js;
					}
				}
				if("1".equals(pnc.getEnTeacherNumOnOff())){//企业导师人数范围
					ConfRange cr=pnc.getEnTeacherNum();
					int min=Integer.valueOf(cr.getMin());
					int max=Integer.valueOf(cr.getMax());
					if(teamNums.getEnterpriseTeacherNum()<min||teamNums.getEnterpriseTeacherNum()>max){
						if(min==max){
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目企业导师为"+min+"人");
						}else{
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目企业导师为"+min+"~"+max+"人");
						}
						return js;
					}
				}
			}
		}
		js.put("ret", 1);
    	return js;
	}
	private void getTeamTeacherNum(Team teamNums,List<TeamUserHistory> teas){
		int s=0;
		int e=0;
		if(teas!=null&&teas.size()>0){
			for(TeamUserHistory t:teas){
				BackTeacherExpansion b=backTeacherExpansionDao.getByUserId(t.getUserId());
				if(b!=null){
					if("1".equals(b.getTeachertype())){
						s++;
					}
					if("2".equals(b.getTeachertype())){
						e++;
					}
				}
			}
		}
		teamNums.setSchoolTeacherNum(s);
		teamNums.setEnterpriseTeacherNum(e);
	}
	public JSONObject checkProjectTeam(String proid,String actywId,String lowType,String teamid) {
    	String proType="1,";//双创项目
    	JSONObject js=new JSONObject();
    	js.put("ret", 0);
    	if (StringUtil.isEmpty(lowType)) {
			js.put("msg", "请选择项目类别");
			return js;
		}
    	if (StringUtil.isEmpty(teamid)) {
			js.put("msg", "请选择团队");
			return js;
		}
		if (StringUtil.isEmpty(actywId)) {
			js.put("msg", "无项目申报配置信息,请联系管理员");
			return js;
		}
		ActYw actYw=actYwDao.get(actywId);
		if (actYw==null||!actYw.getIsDeploy()||"1".equals(actYw.getDelFlag())) {
			js.put("msg", "未找到项目申报配置信息,请联系管理员");
			return js;
		}
		if (actYw.getProProject()==null||StringUtil.isEmpty(actYw.getProProject().getType())||"1".equals(actYw.getProProject().getDelFlag())) {
			js.put("msg", "项目申报配置信息有误,请联系管理员");
			return js;
		}
		String subType=actYw.getProProject().getType();//项目分类
    	SysConfigVo scv=SysConfigUtil.getSysConfigVo();
		if(scv!=null){
			List<TeamUserHistory> tuhs_stu=teamUserHistoryDao.getTeamUserHistoryFromTUR(teamid, "1");
			if("0".equals(scv.getApplyConf().getaOnOff())){//是否允许同一个学生用不同项目既申报项目又参加大赛，1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn1(tuhs_stu, proid,proType)>0){
					js.put("msg", "有团队成员已参加大赛");
					return js;
				}
			}
			if("0".equals(scv.getApplyConf().getProConf().getbOnOff())){//是否允许同一个学生申报多个不同类型的项目,1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn2(tuhs_stu, proid,proType,subType)>0){
					js.put("msg", "有团队成员已申报其他类型的项目");
					return js;
				}
			}
			if("0".equals(scv.getApplyConf().getProConf().getaOnOff())){//同一个项目周期内，是否允许同一个学生在同一类项目中申报多个项目,1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn3(tuhs_stu, proid,proType,subType)>0){
					js.put("msg", "有团队成员已申报该类型的项目");
					return js;
				}
			}
			PersonNumConf pnc=SysConfigUtil.getProPersonNumConf(scv, subType, lowType);
			if(pnc!=null){
				Team teamNums=teamDao.findTeamJoinInNums(teamid);
				if("1".equals(pnc.getTeamNumOnOff())){//团队人数范围
					ConfRange cr=pnc.getTeamNum();
					int min=Integer.valueOf(cr.getMin());
					int max=Integer.valueOf(cr.getMax());
					if(teamNums.getMemberNum()<min||teamNums.getMemberNum()>max){
						if(min==max){
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目团队成员人数为"+min+"人");
						}else{
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目团队成员人数为"+min+"~"+max+"人");
						}
						return js;
					}
				}
				if("1".equals(pnc.getSchoolTeacherNumOnOff())){//校园导师人数范围
					ConfRange cr=pnc.getSchoolTeacherNum();
					int min=Integer.valueOf(cr.getMin());
					int max=Integer.valueOf(cr.getMax());
					if(teamNums.getSchoolTeacherNum()<min||teamNums.getSchoolTeacherNum()>max){
						if(min==max){
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目团队校园导师为"+min+"人");
						}else{
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目团队校园导师为"+min+"~"+max+"人");
						}
						return js;
					}
				}
				if("1".equals(pnc.getEnTeacherNumOnOff())){//企业导师人数范围
					ConfRange cr=pnc.getEnTeacherNum();
					int min=Integer.valueOf(cr.getMin());
					int max=Integer.valueOf(cr.getMax());
					if(teamNums.getEnterpriseTeacherNum()<min||teamNums.getEnterpriseTeacherNum()>max){
						if(min==max){
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目企业导师为"+min+"人");
						}else{
							js.put("msg", DictUtils.getDictLabel(lowType, "project_type", "")+"项目企业导师为"+min+"~"+max+"人");
						}
						return js;
					}
				}
			}
		}
		js.put("ret", 1);
    	return js;
	}

	public JSONObject checkGcontestTeam(String proid,String actywId,String teamid) {
		String proType="7,";//双创项目
		JSONObject js=new JSONObject();
		js.put("ret", 0);
		if (StringUtil.isEmpty(actywId)) {
			js.put("msg", "无大赛申报配置信息,请联系管理员");
			return js;
		}
		ActYw actYw=actYwDao.get(actywId);
		if (actYw==null||!actYw.getIsDeploy()||"1".equals(actYw.getDelFlag())) {
			js.put("msg", "未找到大赛申报配置信息,请联系管理员");
			return js;
		}
		if (actYw.getProProject()==null||StringUtil.isEmpty(actYw.getProProject().getType())||"1".equals(actYw.getProProject().getDelFlag())) {
			js.put("msg", "大赛申报配置信息有误,请联系管理员");
			return js;
		}
		String subType=actYw.getProProject().getType();//项目分类
		SysConfigVo scv=SysConfigUtil.getSysConfigVo();
		if(scv!=null){
			List<TeamUserHistory> tuhs_stu=teamUserHistoryDao.getTeamUserHistoryFromTUR(teamid, "1");
			if("0".equals(scv.getApplyConf().getaOnOff())){//是否允许同一个学生用不同项目既申报项目又参加大赛，1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn1(tuhs_stu, proid,proType)>0){
					js.put("msg", "有团队成员已参加项目");
					return js;
				}
			}
			if("0".equals(scv.getApplyConf().getGconConf().getbOnOff())){//是否允许同一个学生申报多个不同类型的大赛,1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn2(tuhs_stu, proid,proType,subType)>0){
					js.put("msg", "有团队成员已申报其他类型的大赛");
					return js;
				}
			}
			if("0".equals(scv.getApplyConf().getGconConf().getaOnOff())){//同一个大赛周期内，是否允许同一个学生在同一类项目中申报多个大赛,1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn3(tuhs_stu, proid,proType,subType)>0){
					js.put("msg", "有团队成员已申报该类型的大赛");
					return js;
				}
			}
			PersonNumConf pnc=SysConfigUtil.getGconPersonNumConf(scv, subType);
			if(pnc!=null){
				Team teamNums=teamDao.findTeamJoinInNums(teamid);
				if("1".equals(pnc.getTeamNumOnOff())){//团队人数范围
					ConfRange cr=pnc.getTeamNum();
					int min=Integer.valueOf(cr.getMin());
					int max=Integer.valueOf(cr.getMax());
					if(teamNums.getMemberNum()<min||teamNums.getMemberNum()>max){
						if(min==max){
							js.put("msg", DictUtils.getDictLabel(subType, "competition_type", "")+"大赛团队成员人数为"+min+"人");
						}else{
							js.put("msg", DictUtils.getDictLabel(subType, "competition_type", "")+"大赛团队成员人数为"+min+"~"+max+"人");
						}
						return js;
					}
				}
				if("1".equals(pnc.getSchoolTeacherNumOnOff())){//校园导师人数范围
					ConfRange cr=pnc.getSchoolTeacherNum();
					int min=Integer.valueOf(cr.getMin());
					int max=Integer.valueOf(cr.getMax());
					if(teamNums.getSchoolTeacherNum()<min||teamNums.getSchoolTeacherNum()>max){
						if(min==max){
							js.put("msg", DictUtils.getDictLabel(subType, "competition_type", "")+"大赛团队校园导师为"+min+"人");
						}else{
							js.put("msg", DictUtils.getDictLabel(subType, "competition_type", "")+"大赛团队校园导师为"+min+"~"+max+"人");
						}
						return js;
					}
				}
				if("1".equals(pnc.getEnTeacherNumOnOff())){//企业导师人数范围
					ConfRange cr=pnc.getEnTeacherNum();
					int min=Integer.valueOf(cr.getMin());
					int max=Integer.valueOf(cr.getMax());
					if(teamNums.getEnterpriseTeacherNum()<min||teamNums.getEnterpriseTeacherNum()>max){
						if(min==max){
							js.put("msg", DictUtils.getDictLabel(subType, "competition_type", "")+"大赛企业导师为"+min+"人");
						}else{
							js.put("msg", DictUtils.getDictLabel(subType, "competition_type", "")+"大赛企业导师为"+min+"~"+max+"人");
						}
						return js;
					}
				}
			}
		}
		js.put("ret", 1);
		return js;
	}


	public JSONObject onProjectApply(String actywId){
		String proType="1,";//双创项目
    	Date now =new Date();
    	JSONObject js=new JSONObject();
    	js.put("ret", 0);
    	AbsUser user = UserUtils.getUser();
    	if (StringUtil.isEmpty(user.getId())) {
    		js.put("ret", 1);
			return js;
		}
		if ("2".equals(user.getUserType())) {
			js.put("msg", "无法申报,导师不能申报项目");
			return js;
		}
		if (StringUtil.isEmpty(actywId)) {
			js.put("msg", "无法申报,无项目申报配置信息,请联系管理员");
			return js;
		}
		ActYw actYw=actYwDao.get(actywId);
		if (actYw==null||!actYw.getIsDeploy()||"1".equals(actYw.getDelFlag())) {
			js.put("msg", "无法申报,未找到项目申报配置信息,请联系管理员");
			return js;
		}
		if (actYw.getProProject()==null||StringUtil.isEmpty(actYw.getProProject().getType())||"1".equals(actYw.getProProject().getDelFlag())) {
			js.put("msg", "无法申报,项目申报配置信息有误,请联系管理员");
			return js;
		}
		String subType=actYw.getProProject().getType();//项目分类
		SysConfigVo scv=SysConfigUtil.getSysConfigVo();
		if(scv!=null){
			List<TeamUserHistory> tuhs_stu=new ArrayList<TeamUserHistory>();
			TeamUserHistory tem=new TeamUserHistory();
			tem.setUserId(user.getId());
			tuhs_stu.add(tem);
			if("0".equals(scv.getApplyConf().getaOnOff())){//是否允许同一个学生用不同项目既申报项目又参加大赛，1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn1(tuhs_stu, null,proType)>0){
					js.put("msg", "无法申报,你已参加大赛");
					return js;
				}
			}
			if("0".equals(scv.getApplyConf().getProConf().getbOnOff())){//是否允许同一个学生申报多个不同类型的项目,1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn2(tuhs_stu, null,proType,subType)>0){
					js.put("msg", "无法申报,你已申报其他类型的项目");
					return js;
				}
			}
			if("0".equals(scv.getApplyConf().getProConf().getaOnOff())){//同一个项目周期内，是否允许同一个学生在同一类项目中申报多个项目,1-允许，0-不允许
				if(teamUserHistoryDao.countByCdn3(tuhs_stu, null,proType,subType)>0){
					js.put("msg", "无法申报,你已申报该类型的项目");
					js.put("ret", 2);
					js.put("btn1name", "查看");
					if("1".equals(subType)){
						js.put("btn1url", "/f/project/projectDeclare/viewForm?id="+teamUserHistoryDao.getProIdByCdn(user.getId(), proType, subType,"0"));
					}else{// 自定义项目查看
						js.put("btn1url", "/f/promodel/proModel/viewForm?id="+teamUserHistoryDao.getProIdByCdn(user.getId(), proType, subType,"0"));
					}
					return js;
				}
			}
			
		}
		
		Date start=actYw.getProProject().getNodeStartDate();
		Date end=actYw.getProProject().getNodeEndDate();
		if (start!=null&&start.after(now)) {
			js.put("msg", "无法申报，距离第"+DateUtil.formatDate(DateUtil.addYear(start, 1), "yyyy")
			+"届"+DictUtils.getDictLabel(subType, "project_style", "")
			+"申报还有"+DateUtil.formatDateTime2(start.getTime()-now.getTime()));
			return js;
		}
		if (end!=null&&end.before(now)) {
			js.put("msg", "无法申报，本届"+DictUtils.getDictLabel(subType, "project_style", "")+"申报截止");
			return js;
		}
		String proid=teamUserHistoryDao.getProIdByCdn(user.getId(), proType, subType,"2");//未提交的
		if(StringUtil.isNotEmpty(proid)){
			js.put("msg", "你有未提交的该类型项目");
			js.put("ret", 2);
			js.put("btn1name", "继续完善表单");
			if("1".equals(subType)){
				js.put("btn1url", "/f/project/projectDeclare/form?id="+proid);
			}else{// 自定义项目修改
				js.put("btn1url", "/f/promodel/proModel/form?id="+proid);
			}
			return js;
		}
		js.put("ret", 1);
		return js;
	}

	public JSONObject onGcontestApply(String actywId){
			String proType="7,";//双创项目
	    	Date now =new Date();
	    	JSONObject js=new JSONObject();
	    	js.put("ret", 0);
	    	AbsUser user = UserUtils.getUser();
	    	if (StringUtil.isEmpty(user.getId())) {
	    		js.put("ret", 1);
				return js;
			}
			if ("2".equals(user.getUserType())) {
				js.put("msg", "无法申报,导师不能申报大赛");
				return js;
			}
			if (StringUtil.isEmpty(actywId)) {
				js.put("msg", "无法申报,无大赛申报配置信息,请联系管理员");
				return js;
			}
			ActYw actYw=actYwDao.get(actywId);
			if (actYw==null||!actYw.getIsDeploy()||"1".equals(actYw.getDelFlag())) {
				js.put("msg", "无法申报,未找到大赛申报配置信息,请联系管理员");
				return js;
			}
			if (actYw.getProProject()==null||StringUtil.isEmpty(actYw.getProProject().getType())||"1".equals(actYw.getProProject().getDelFlag())) {
				js.put("msg", "无法申报,大赛申报配置信息有误,请联系管理员");
				return js;
			}
			String subType=actYw.getProProject().getType();//大赛分类
			SysConfigVo scv=SysConfigUtil.getSysConfigVo();
			if(scv!=null){
				List<TeamUserHistory> tuhs_stu=new ArrayList<TeamUserHistory>();
				TeamUserHistory tem=new TeamUserHistory();
				tem.setUserId(user.getId());
				tuhs_stu.add(tem);
				if("0".equals(scv.getApplyConf().getaOnOff())){//是否允许同一个学生用不同项目既申报项目又参加大赛，1-允许，0-不允许
					if(teamUserHistoryDao.countByCdn1(tuhs_stu, null,proType)>0){
						js.put("msg", "无法申报,你已参加大赛");
						return js;
					}
				}
				if("0".equals(scv.getApplyConf().getGconConf().getbOnOff())){//是否允许同一个学生申报多个不同类型的项目,1-允许，0-不允许
					if(teamUserHistoryDao.countByCdn2(tuhs_stu, null,proType,subType)>0){
						js.put("msg", "无法申报,你已申报其他类型的项目");
						return js;
					}
				}
				if("0".equals(scv.getApplyConf().getGconConf().getaOnOff())){//同一个大赛周期内，是否允许同一个学生在同一类项目中申报多个项目,1-允许，0-不允许
					if(teamUserHistoryDao.countByCdn3(tuhs_stu, null,proType,subType)>0){
						js.put("msg", "无法申报,你已申报该类型的项目");
						js.put("ret", 2);
						js.put("btn1name", "查看");
						//互联网+
						if("1".equals(subType)){
							js.put("btn1url",
									"/f/gcontest/gContest/viewForm?id="+teamUserHistoryDao.getProIdByCdn(user.getId(), proType, subType,"0"));
						}else{//自定义大赛项目查看
							js.put("btn1url",
									"/f/promodel/proModel/viewForm?id="+teamUserHistoryDao.getProIdByCdn(user.getId(), proType, subType,"0"));
						}
						return js;
					}
				}

			}

			Date start=actYw.getProProject().getNodeStartDate();
			Date end=actYw.getProProject().getNodeEndDate();
			if (start!=null&&start.after(now)) {
				js.put("msg", "无法申报，距离第"+DateUtil.formatDate(DateUtil.addYear(start, 1), "yyyy")
				+"届"+DictUtils.getDictLabel(subType, "competition_type", "")
				+"申报还有"+DateUtil.formatDateTime2(start.getTime()-now.getTime()));
				return js;
			}
			if (end!=null&&end.before(now)) {
				js.put("msg", "无法申报，本届"+DictUtils.getDictLabel(subType, "competition_type", "")+"申报截止");
				return js;
			}
			String proid=teamUserHistoryDao.getProIdByCdn(user.getId(), proType, subType,"2");//未提交的
			if(StringUtil.isNotEmpty(proid)){
				js.put("msg", "你有未提交的该类型大赛");
				js.put("ret", 2);
				js.put("btn1name", "继续完善表单");
				if("1".equals(subType)){
					js.put("btn1url", "/f/gcontest/gContest/form?id="+proid);
				}else{
					js.put("btn1url", "/f/promodel/proModel/form?id="+proid);
				}
				return js;
			}
			js.put("ret", 1);
			return js;
		}
	/**
	 * @param tuhs 带有学分配比
	 * @param actywId
	 * @param teamId
	 * @param proId
	 */
	@Transactional(readOnly = false)
	public void disposeTeamUserHistoryOnSave(List<TeamUserHistory> tuhs,String actywId,String teamId,String proId) {
		disposeTeamUserHistory(tuhs, actywId, teamId, proId, "2");
	}
	
	/**
	 * @param tuhs 带有学分配比
	 * @param actywId
	 * @param teamId
	 * @param proId
	 */
	@Transactional(readOnly = false)
	public void disposeTeamUserHistoryOnSubmit(List<TeamUserHistory> tuhs,String actywId,String teamId,String proId) {
		disposeTeamUserHistory(tuhs,actywId, teamId, proId, "0");
	}
	private void disposeTeamUserHistory(List<TeamUserHistory> tuhs,String actywId,String teamId,String proId,String finish) {
		Date now=new Date();
		AbsUser cuser=UserUtils.getUser();
		if(tuhs==null||tuhs.size()==0){
			throw new RuntimeException("TeamUserHistory不能为空");
		}
		if(StringUtil.isEmpty(actywId)||StringUtil.isEmpty(teamId)||StringUtil.isEmpty(proId)){
			throw new RuntimeException("参数不能为空");
		}
		ActYw actYw=actYwDao.get(actywId);
		if(actYw==null){
			throw new RuntimeException("找不到ActYw");
		}
		ProProject proProject=actYw.getProProject();
		if(proProject==null){
			throw new RuntimeException("找不到ProProject");
		}
		if(StringUtil.isEmpty(proProject.getProType())||StringUtil.isEmpty(proProject.getType())){
			throw new RuntimeException("ProProject的类型不能为空");
		}
		String proType=proProject.getProType();
		String proSubType=proProject.getType();
		for(TeamUserHistory tuh:tuhs){
			tuh.setId(IdGen.uuid());
			tuh.setUser(new AbsUser(tuh.getUserId()));
			tuh.setTeamId(teamId);
			tuh.setCreateBy(cuser);
			tuh.setUpdateBy(cuser);
			tuh.setCreateDate(now);
			tuh.setUpdateDate(now);
			tuh.setProId(proId);
			tuh.setProType(proType);
			tuh.setProSubType(proSubType);
			tuh.setUtype("1");
			tuh.setFinish(finish);
			tuh.setDelFlag("0");
			tuh.setWeightVal(tuh.getWeightVal());
		}
		List<TeamUserHistory> tuhs_teas=teamUserHistoryDao.getTeamUserHistoryFromTUR(teamId, "2");
		if(tuhs_teas!=null&&tuhs_teas.size()>0){
			for(TeamUserHistory tuh:tuhs_teas){
				tuh.setId(IdGen.uuid());
				tuh.setUser(new AbsUser(tuh.getUserId()));
				tuh.setTeamId(teamId);
				tuh.setCreateBy(cuser);
				tuh.setUpdateBy(cuser);
				tuh.setCreateDate(now);
				tuh.setUpdateDate(now);
				tuh.setProId(proId);
				tuh.setProType(proType);
				tuh.setProSubType(proSubType);
				tuh.setUtype("2");
				tuh.setFinish(finish);
				tuh.setDelFlag("0");
			};
			tuhs.addAll(tuhs_teas);
		}
		teamUserHistoryDao.deleteByProId(proId);
		teamUserHistoryDao.insertAll(tuhs);
	}
	@Transactional(readOnly = false)
	public void copyTeamUserHistoryFromTUR(String actywId,String teamId,String proId,String finish) {
		Date now=new Date();
		AbsUser cuser=UserUtils.getUser();
		if(StringUtil.isEmpty(actywId)||StringUtil.isEmpty(teamId)||StringUtil.isEmpty(proId)){
			throw new RuntimeException("参数不能为空");
		}
		ActYw actYw=actYwDao.get(actywId);
		if(actYw==null){
			throw new RuntimeException("找不到ActYw");
		}
		ProProject proProject=actYw.getProProject();
		if(proProject==null){
			throw new RuntimeException("找不到ProProject");
		}
		if(StringUtil.isEmpty(proProject.getProType())||StringUtil.isEmpty(proProject.getType())){
			throw new RuntimeException("ProProject的类型不能为空");
		}
		String proType=proProject.getProType();
		String proSubType=proProject.getType();
		List<TeamUserRelation> turs=teamUserRelationDao.getByTeamId(teamId);
		List<TeamUserHistory> tuhs=new ArrayList<TeamUserHistory>();
		for(TeamUserRelation tur:turs){
			TeamUserHistory tuh=new TeamUserHistory();
			tuh.setId(IdGen.uuid());
			tuh.setUser(tur.getUser());
			tuh.setTeamId(teamId);
			tuh.setCreateBy(cuser);
			tuh.setUpdateBy(cuser);
			tuh.setCreateDate(now);
			tuh.setUpdateDate(now);
			tuh.setProId(proId);
			tuh.setProType(proType);
			tuh.setProSubType(proSubType);
			tuh.setUtype(tur.getUserType());
			tuh.setFinish(finish);
			tuh.setDelFlag("0");
			tuh.setWeightVal(tuh.getWeightVal());
			tuhs.add(tuh);
		}
		if(tuhs.size()>0){
			teamUserHistoryDao.deleteByProId(proId);
			teamUserHistoryDao.insertAll(tuhs);
		}
	}
	public JSONObject checkProjectOnModify(List<TeamUserHistory> stus,List<TeamUserHistory> teas,String proid,String actywId,String lowType,String teamid) {
    	JSONObject js=new JSONObject();
    	js.put("ret", 0);
		if (StringUtil.isEmpty(actywId)) {
			js.put("msg", "保存失败，无项目申报配置信息,请联系管理员");
			return js;
		}
		ActYw actYw=actYwDao.get(actywId);
		if (actYw==null||!actYw.getIsDeploy()||"1".equals(actYw.getDelFlag())) {
			js.put("msg", "保存失败，未找到项目申报配置信息,请联系管理员");
			return js;
		}
		if (actYw.getProProject()==null||StringUtil.isEmpty(actYw.getProProject().getType())||"1".equals(actYw.getProProject().getDelFlag())) {
			js.put("msg", "保存失败，项目申报配置信息有误,请联系管理员");
			return js;
		}
		//团队校验
		js=checkProjectTeamForModify(stus,teas,proid, actywId, lowType, teamid);
		if("0".equals(js.getString("ret"))){
			js.put("msg", "保存失败，"+js.getString("msg"));
			return js;
		}
		
		if (!StringUtil.isEmpty(proid)) {//修改
			ProjectDeclare pd=projectDeclareDao.get(proid);
			if (pd!=null&&"1".equals(pd.getDelFlag())) {
				js.put("msg", "保存失败，项目已被删除");
				return js;
			}
		}
		js.put("ret", 1);
    	return js;
    }
    /**
     * @param proid 项目id
     * @param actywId
     * @param lowType 项目类别
     * @param teamid 团队id
     * @return JSONObject {ret:1-成功\0-失败，msg:成功或失败信息}
     */
    public JSONObject checkProjectApplyOnSave(String proid,String actywId,String lowType,String teamid) {
    	Date now =new Date();
    	JSONObject js=new JSONObject();
    	js.put("ret", 0);
    	AbsUser user = UserUtils.getUser();
		if ("2".equals(user.getUserType())) {
			js.put("msg", "保存失败，导师不能申报项目");
			return js;
		}
		if (StringUtil.isEmpty(actywId)) {
			js.put("msg", "保存失败，无项目申报配置信息,请联系管理员");
			return js;
		}
		ActYw actYw=actYwDao.get(actywId);
		if (actYw==null||!actYw.getIsDeploy()||"1".equals(actYw.getDelFlag())) {
			js.put("msg", "保存失败，未找到项目申报配置信息,请联系管理员");
			return js;
		}
		if (actYw.getProProject()==null||StringUtil.isEmpty(actYw.getProProject().getType())||"1".equals(actYw.getProProject().getDelFlag())) {
			js.put("msg", "保存失败，项目申报配置信息有误,请联系管理员");
			return js;
		}
		String subType=actYw.getProProject().getType();//项目分类
		Date start=actYw.getProProject().getNodeStartDate();
		Date end=actYw.getProProject().getNodeEndDate();
		if (start!=null&&start.after(now)) {
			js.put("msg", "保存失败，距离第"+DateUtil.formatDate(DateUtil.addYear(start, 1), "yyyy")
			+"届"+DictUtils.getDictLabel(subType, "project_style", "")
			+"申报还有"+DateUtil.formatDateTime2(start.getTime()-now.getTime()));
			return js;
		}
		if (end!=null&&end.before(now)) {
			js.put("msg", "保存失败，本届"+DictUtils.getDictLabel(subType, "project_style", "")+"申报截止");
			return js;
		}
		//团队校验
		js=checkProjectTeam(proid, actywId, lowType, teamid);
		if("0".equals(js.getString("ret"))){
			js.put("msg", "保存失败，"+js.getString("msg"));
			return js;
		}
		
		if (!StringUtil.isEmpty(proid)) {//修改
			ProjectDeclare pd=projectDeclareDao.get(proid);
			if (pd!=null&&"1".equals(pd.getDelFlag())) {
				js.put("msg", "保存失败，项目已被删除");
				return js;
			}
		}
		js.put("ret", 1);
    	return js;
    }
    /**
     * @param proid 项目id
     * @param actywId
     * @param lowType 项目类别
     * @param teamid 团队id
     * @return JSONObject {ret:1-成功\0-失败，msg:成功或失败信息}
     */
    public JSONObject checkProjectApplyOnSubmit(String proid,String actywId,String lowType,String teamid) {
    	Date now =new Date();
    	JSONObject js=new JSONObject();
    	js.put("ret", 0);
    	AbsUser user = UserUtils.getUser();
		if ("2".equals(user.getUserType())) {
			js.put("msg", "提交失败，导师不能申报项目");
			return js;
		}
		if (StringUtil.isEmpty(actywId)) {
			js.put("msg", "提交失败，无项目申报配置信息,请联系管理员");
			return js;
		}
		ActYw actYw=actYwDao.get(actywId);
		if (actYw==null||!actYw.getIsDeploy()||"1".equals(actYw.getDelFlag())) {
			js.put("msg", "提交失败，未找到项目申报配置信息,请联系管理员");
			return js;
		}
		if (actYw.getProProject()==null||StringUtil.isEmpty(actYw.getProProject().getType())||"1".equals(actYw.getProProject().getDelFlag())) {
			js.put("msg", "提交失败，项目申报配置信息有误,请联系管理员");
			return js;
		}
		String subType=actYw.getProProject().getType();//项目分类
		Date start=actYw.getProProject().getNodeStartDate();
		Date end=actYw.getProProject().getNodeEndDate();
		if (start!=null&&start.after(now)) {
			js.put("msg", "提交失败，距离第"+DateUtil.formatDate(DateUtil.addYear(start, 1), "yyyy")
			+"届"+DictUtils.getDictLabel(subType, "project_style", "")
			+"申报还有"+DateUtil.formatDateTime2(start.getTime()-now.getTime()));
			return js;
		}
		if (end!=null&&end.before(now)) {
			js.put("msg", "提交失败，本届"+DictUtils.getDictLabel(subType, "project_style", "")+"申报截止");
			return js;
		}
		//团队校验
		js=checkProjectTeam(proid, actywId, lowType, teamid);
		if("0".equals(js.getString("ret"))){
			js.put("msg", "提交失败，"+js.getString("msg"));
			return js;
		}
		
		if (!StringUtil.isEmpty(proid)) {//修改
			ProjectDeclare pd=projectDeclareDao.get(proid);
			if (pd!=null&&"1".equals(pd.getDelFlag())) {
				js.put("msg", "提交失败，项目已被删除");
				return js;
			}
			if (pd!=null&&!"0".equals(pd.getStatus())) {
				js.put("msg", "提交失败，项目已被提交");
				return js;
			}
		}
		js.put("ret", 1);
    	return js;
    }
    /**
     * @param proid 大赛id
	 * @param actywId
     * @param teamid 团队id
     * @return JSONObject {ret:1-成功\0-失败，msg:成功或失败信息}
     */
    public JSONObject checkGcontestApplyOnSave(String proid,String actywId,String teamid) {
		Date now =new Date();
		JSONObject js=new JSONObject();
		js.put("ret", 0);
		AbsUser user = UserUtils.getUser();
		if ("2".equals(user.getUserType())) {
			js.put("msg", "保存失败，导师不能申报大赛");
			return js;
		}
		if (StringUtil.isEmpty(actywId)) {
			js.put("msg", "保存失败，无大赛申报配置信息,请联系管理员");
			return js;
		}
		ActYw actYw=actYwDao.get(actywId);
		if (actYw==null||!actYw.getIsDeploy()||"1".equals(actYw.getDelFlag())) {
			js.put("msg", "保存失败，未找到大赛申报配置信息,请联系管理员");
			return js;
		}
		if (actYw.getProProject()==null||StringUtil.isEmpty(actYw.getProProject().getType())||"1".equals(actYw.getProProject().getDelFlag())) {
			js.put("msg", "保存失败，大赛申报配置信息有误,请联系管理员");
			return js;
		}
		String subType=actYw.getProProject().getType();//大赛分类
		Date start=actYw.getProProject().getNodeStartDate();
		Date end=actYw.getProProject().getNodeEndDate();
		if (start!=null&&start.after(now)) {
			js.put("msg", "保存失败，距离第"+DateUtil.formatDate(DateUtil.addYear(start, 1), "yyyy")
			+"届"+DictUtils.getDictLabel(subType, "competition_type", "")
			+"申报还有"+DateUtil.formatDateTime2(start.getTime()-now.getTime()));
			return js;
		}
		if (end!=null&&end.before(now)) {
			js.put("msg", "保存失败，本届"+DictUtils.getDictLabel(subType, "competition_type", "")+"申报截止");
			return js;
		}
		//团队校验
		js=checkGcontestTeam(proid, actywId, teamid);
		if("0".equals(js.getString("ret"))){
			js.put("msg", "保存失败，"+js.getString("msg"));
			return js;
		}

		if (!StringUtil.isEmpty(proid)) {//修改
			GContest pd=gContestDao.get(proid);
			if (pd!=null&&"1".equals(pd.getDelFlag())) {
				js.put("msg", "保存失败，大赛已被删除");
				return js;
			}
		}
		js.put("ret", 1);
		return js;
	}

	public JSONObject checkGcontestApplyOnSubmit(String proid,String actywId,String teamid) {
			Date now =new Date();
			JSONObject js=new JSONObject();
			js.put("ret", 0);
			AbsUser user = UserUtils.getUser();
			if ("2".equals(user.getUserType())) {
				js.put("msg", "保存失败，导师不能申报大赛");
				return js;
			}
			if (StringUtil.isEmpty(actywId)) {
				js.put("msg", "保存失败，无大赛申报配置信息,请联系管理员");
				return js;
			}
			ActYw actYw=actYwDao.get(actywId);
			if (actYw==null||!actYw.getIsDeploy()||"1".equals(actYw.getDelFlag())) {
				js.put("msg", "保存失败，未找到大赛申报配置信息,请联系管理员");
				return js;
			}
			if (actYw.getProProject()==null||StringUtil.isEmpty(actYw.getProProject().getType())||"1".equals(actYw.getProProject().getDelFlag())) {
				js.put("msg", "保存失败，大赛申报配置信息有误,请联系管理员");
				return js;
			}
			String subType=actYw.getProProject().getType();//大赛分类
			Date start=actYw.getProProject().getNodeStartDate();
			Date end=actYw.getProProject().getNodeEndDate();
			if (start!=null&&start.after(now)) {
				js.put("msg", "保存失败，距离第"+DateUtil.formatDate(DateUtil.addYear(start, 1), "yyyy")
				+"届"+DictUtils.getDictLabel(subType, "competition_type", "")
				+"申报还有"+DateUtil.formatDateTime2(start.getTime()-now.getTime()));
				return js;
			}
			if (end!=null&&end.before(now)) {
				js.put("msg", "保存失败，本届"+DictUtils.getDictLabel(subType, "competition_type", "")+"申报截止");
				return js;
			}
			//团队校验
			js=checkGcontestTeam(proid, actywId, teamid);
			if("0".equals(js.getString("ret"))){
				js.put("msg", "保存失败，"+js.getString("msg"));
				return js;
			}

			if (!StringUtil.isEmpty(proid)) {//修改
				GContest gc=gContestDao.get(proid);
				if (gc!=null&&"1".equals(gc.getDelFlag())) {
					js.put("msg", "保存失败，大赛已被删除");
					return js;
				}
				if (gc!=null&&!"0".equals(gc.getAuditState())) {
					js.put("msg", "提交失败，大赛已被提交");
					return js;
				}
			}
			js.put("ret", 1);
			return js;
		}


}
