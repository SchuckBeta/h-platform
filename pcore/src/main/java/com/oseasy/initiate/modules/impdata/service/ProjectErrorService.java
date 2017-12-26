package com.hch.platform.pcore.modules.impdata.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.service.CommonService;
import com.hch.platform.pcore.common.service.CrudService;
import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.putil.common.utils.IdGen;
import com.hch.platform.pcore.common.utils.IdUtils;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.modules.excellent.entity.ExcellentShow;
import com.hch.platform.pcore.modules.excellent.service.ExcellentShowService;
import com.hch.platform.pcore.modules.impdata.dao.ProjectErrorDao;
import com.hch.platform.pcore.modules.impdata.entity.ProjectError;
import com.hch.platform.pcore.modules.project.dao.ProjectDeclareDao;
import com.hch.platform.pcore.modules.project.entity.ProjectDeclare;
import com.hch.platform.pcore.modules.project.vo.ProjectNodeVo;
import com.hch.platform.pcore.modules.sys.dao.BackTeacherExpansionDao;
import com.hch.platform.pcore.modules.sys.entity.BackTeacherExpansion;
import com.hch.platform.pcore.modules.sys.entity.Dict;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.DictUtils;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;
import com.hch.platform.pcore.modules.team.dao.TeamDao;
import com.hch.platform.pcore.modules.team.dao.TeamUserRelationDao;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.entity.TeamUserRelation;

/**
 * 导入项目错误数据表Service
 * @author 9527
 * @version 2017-05-26
 */
@Service
@Transactional(readOnly = true)
public class ProjectErrorService extends CrudService<ProjectErrorDao, ProjectError> {
	@Autowired
	private TeamDao teamDao;
	@Autowired
	private ProjectDeclareDao projectDeclareDao;
	@Autowired
	private TeamUserRelationDao teamUserRelationDao;
	@Autowired
	private BackTeacherExpansionDao backTeacherExpansionDao;
	@Autowired
	private ExcellentShowService excellentShowService;
	@Autowired
	private CommonService commonService;
	public ProjectError get(String id) {
		return super.get(id);
	}
	
	public List<ProjectError> findList(ProjectError projectError) {
		return super.findList(projectError);
	}
	
	public Page<ProjectError> findPage(Page<ProjectError> page, ProjectError projectError) {
		return super.findPage(page, projectError);
	}
	
	@Transactional(readOnly = false)
	public void save(ProjectError projectError) {
		super.save(projectError);
	}
	
	@Transactional(readOnly = false)
	public void delete(ProjectError projectError) {
		super.delete(projectError);
	}
	public List<Map<String,String>> getListByImpId(String impid) {
		return dao.getListByImpId(impid);
	}
	@Transactional(readOnly = false)
	public void insert(ProjectError projectError) {
		AbsUser user = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getId())) {
			projectError.setUpdateBy(user);
			projectError.setCreateBy(user);
		}
		projectError.setUpdateDate(new Date());
		projectError.setCreateDate(projectError.getUpdateDate());
		dao.insert(projectError);
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	public void saveProject(ProjectError pe) throws ParseException{
		AbsUser leader=UserUtils.get(pe.getLeaderNo());
		/*保存团队信息*/
		Team team=new Team();
		team.setId(IdGen.uuid());
		team.setName("项目"+pe.getNumber()+"的团队");
		team.setSponsor(pe.getLeaderNo());
		team.setState("2");
		team.setNumber(IdUtils.getTeamNumberByDb());
		String localCollege="";
		if(leader.getOffice()!=null){
			localCollege=leader.getOffice().getId();
		}
		if(StringUtil.isNotEmpty(leader.getProfessional())){
			localCollege=leader.getProfessional();
		}
		team.setLocalCollege(localCollege);
		team.setProjectName(pe.getName());
		team.setProjectIntroduction(pe.getIntroduction());
		int member_num=1;//成员人数
		int enterprise_teacher_num=0;//企业导师人数
		int school_teacher_num=0;//校园导师人数
		String[] mebids=null;//组员id
		for(String teaid:pe.getTeacher().split(",")) {
			BackTeacherExpansion tea=backTeacherExpansionDao.findTeacherByUserId(teaid);
			/*保存团队导师信息*/
			TeamUserRelation teamur=new TeamUserRelation();
			teamur.setId(IdGen.uuid());
			teamur.setTeamId(team.getId());
			teamur.setUser(UserUtils.get(teaid));
			teamur.setUpdateBy(leader);
			teamur.setCreateBy(leader);
			teamur.setUpdateDate(new Date());
			teamur.setCreateDate(teamur.getUpdateDate());
			teamur.setState("0");
			teamur.setUserType("2");
			teamUserRelationDao.insert(teamur);
			if (tea.getTeachertype().equals("1")) {
				school_teacher_num++;
			}else if (tea.getTeachertype().equals("2")) {
				enterprise_teacher_num++;
			}
		}
		team.setEnterpriseTeacherNum(enterprise_teacher_num);
		team.setSchoolTeacherNum(school_teacher_num);
		if (StringUtils.isNotEmpty(pe.getTeamStuInfo())) {
			mebids=pe.getTeamStuInfo().split(",");
		}
		if (mebids!=null) {
			member_num=member_num+mebids.length;
		}
		team.setMemberNum(member_num);
		AbsUser user = UserUtils.get(pe.getLeaderNo());
		
		team.setUpdateBy(user);
		team.setCreateBy(user);
		team.setUpdateDate(new Date());
		team.setCreateDate(team.getUpdateDate());
		teamDao.insert(team);
		/*保存团队组员信息*/
		TeamUserRelation teamurLeader=new TeamUserRelation();
		teamurLeader.setId(IdGen.uuid());
		teamurLeader.setTeamId(team.getId());
		teamurLeader.setUser(leader);
		teamurLeader.setUpdateBy(leader);
		teamurLeader.setCreateBy(leader);
		teamurLeader.setUpdateDate(new Date());
		teamurLeader.setCreateDate(teamurLeader.getUpdateDate());
		teamurLeader.setState("0");
		teamurLeader.setUserType("1");
		teamUserRelationDao.insert(teamurLeader);
		if (mebids!=null) {
			for(String mebid:mebids) {
				TeamUserRelation teamur=new TeamUserRelation();
				teamur.setId(IdGen.uuid());
				teamur.setTeamId(team.getId());
				teamur.setUser(UserUtils.get(mebid));
				teamur.setUpdateBy(leader);
				teamur.setCreateBy(leader);
				teamur.setUpdateDate(new Date());
				teamur.setCreateDate(teamur.getUpdateDate());
				teamur.setState("0");
				teamur.setUserType("1");
				teamUserRelationDao.insert(teamur);
			}
		}
		/*保存项目信息*/
		ProjectDeclare pd=new ProjectDeclare();
		pd.setId(IdGen.uuid());
		pd.setTeamId(team.getId());
		pd.setUpdateBy(leader);
		pd.setCreateBy(leader);
		pd.setUpdateDate(new Date());
		pd.setCreateDate(pd.getUpdateDate());
		pd.setLevel(getProjectLevelByGrant(pe.getFinanceGrant()));
		pd.setApplyTime(DateUtil.parseDate(pe.getApprovingYear()+"-01-01","yyyy-MM-dd"));
		pd.setNumber(pe.getNumber());
		pd.setName(pe.getName());
		pd.setType(pe.getType());
		pd.setLeader(pe.getLeaderNo());
		pd.setFinanceGrant(pe.getFinanceGrant());
		pd.setUniversityGrant(pe.getUniversityGrant());
		pd.setTotalGrant(pe.getTotalGrant());
		pd.setIntroduction(pe.getIntroduction());
		pd.setStatus("9");
		pd.setFinalResult("0");
		pd.setProvince(pe.getProvince());
		pd.setUniversityCode(pe.getUniversityCode());
		pd.setUniversityName(pe.getUniversityName());
		pd.setActywId(ProjectNodeVo.YW_ID);
		projectDeclareDao.insert(pd);
		commonService.copyTeamUserHistoryFromTUR(pd.getActywId(), pd.getTeamId(), pd.getId(), "1");
		excellentShowService.saveExcellentShow(pd.getIntroduction(), pd.getTeamId(),ExcellentShow.Type_Project,pd.getId(),pd.getActywId());//保存优秀展示
	}
	private String getProjectLevelByGrant(String grant) {
		List<Dict> list=DictUtils.getDictList("project_degree_fund");
		for(Dict d:list) {
			if (d.getLabel().equals(grant)) {
				return d.getValue();
			}
		}
		return null;
	}
}