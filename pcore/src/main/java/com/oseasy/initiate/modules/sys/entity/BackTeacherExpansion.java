package com.oseasy.initiate.modules.sys.entity;

import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.entity.OaNotifyRecord;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.oseasy.initiate.common.persistence.DataEntity;
import com.oseasy.initiate.common.utils.StringUtil;

/**
 * 导师扩展信息表Entity
 * @author l
 * @version 2017-03-31
 */
public class BackTeacherExpansion extends DataEntity<BackTeacherExpansion> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// 人员基本信息ID
	private String arrangement;		// 层次
	private Integer discipline;		// 学科门类
	private String industry;		// 行业
	private String technicalTitle;		// 职称
	private String serviceIntention;		// 服务意向
	private String workUnit;		// 工作单位
	private String address;		// 联系地址
	private String resume;		// 工作简历
	private String recommendedUnits;		// 推荐单位
	private String result;		// 成果名称
	private String award;		// 获奖名称
	private Integer level;		// 级别
	private String reviewName;		// 评审项目名称
	private String joinReviewTime;		// 参与评审年份
	private String firstBank;		// 开户银行
	private Integer bankAccount;		// 银行账号
	private String teachertype;		// 导师类型
	private String isOpen="1";		// 是否公开  0:不公开 1:公开
	private String educationType;  //学历类别
 	private Team  team;
 	private String currentId;  //当前用户Id
 	private TeamUserRelation teamUserRelation;
 	private OaNotify oaNotify;
 	private String msg;
 	private Date awardTime;//获奖年份
 	private String awardLevel;//获奖级别
 	private String isFront;//是否是前台查询 0或空：否 1：是
 	private String mainExp;//主要经历
	private String postTitle;//职务

	private String nowProject;   //是否在研项目  1是 0否
	private String topPrise; // 最高奖项

	private String firstShow;   //是否首页显示  1是 0否
	private String siteShow; // 是否栏目显示 1是 0否
	private List<String> keywords; //关键字

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getFirstShow() {
		return firstShow;
	}

	public void setFirstShow(String firstShow) {
		this.firstShow = firstShow;
	}

	public String getSiteShow() {
		return siteShow;
	}

	public void setSiteShow(String siteShow) {
		this.siteShow = siteShow;
	}

	public String getTopPrise() {
		return topPrise;
	}

	public void setTopPrise(String topPrise) {
		this.topPrise = topPrise;
	}

	public String getNowProject() {
		return nowProject;
	}

	public void setNowProject(String nowProject) {
		this.nowProject = nowProject;
	}

	private String myFind; //前台关键字查询

	private String keyWords; //后台关键字

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getMyFind() {
		return myFind;
	}

	public void setMyFind(String myFind) {
		this.myFind = myFind;
	}

	public String getPostTitle() {
		return postTitle;
	}

	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	public String getIsFront() {
		return isFront;
	}
	public void setIsFront(String isFront) {
		this.isFront = isFront;
	}
	public boolean getInviteState() {
		if (teamUserRelation != null) {
			if ("2".equals(teamUserRelation.getState())||"0".equals(teamUserRelation.getState())) {
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
 	public OaNotify getOaNotify() {
		return oaNotify;
	}

	public void setOaNotify(OaNotify oaNotify) {
		this.oaNotify = oaNotify;
	}



	public String getMainExp() {
		return mainExp;
	}
	public void setMainExp(String mainExp) {
		this.mainExp = mainExp;
	}
	//获取导师年龄
 	public int getAgeNow() {
 		if (user!=null && user.getBirthday()!=null)  {
 			Date birthDay = user.getBirthday();
 	 		Calendar cal = Calendar.getInstance();  
 	 		  
 	        int yearNow = cal.get(Calendar.YEAR);  
 	        int monthNow = cal.get(Calendar.MONTH);  
 	        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
 	        cal.setTime(birthDay);  
 	  
 	        int yearBirth = cal.get(Calendar.YEAR);  
 	        int monthBirth = cal.get(Calendar.MONTH);  
 	        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
 	  
 	        int age = yearNow - yearBirth;  
 	  
 	        if (monthNow <= monthBirth) {  
 	            if (monthNow == monthBirth) {  
 	                if (dayOfMonthNow < dayOfMonthBirth) age--;  
 	            }else{  
 	                age--;  
 	            }  
 	        }  
 	        return age; 
 		}
 		return 0;
 	}
 	
 	
	public TeamUserRelation getTeamUserRelation() {
		return teamUserRelation;
	}

	public void setTeamUserRelation(TeamUserRelation teamUserRelation) {
		this.teamUserRelation = teamUserRelation;
	}

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}
 	public String getEducationType() {
		return educationType;
	}

	public void setEducationType(String educationType) {
		this.educationType = educationType;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public BackTeacherExpansion() {
		super();
	}

	public BackTeacherExpansion(String id) {
		super(id);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=0, max=64, message="层次长度必须介于 0 和 64 之间")
	public String getArrangement() {
		return arrangement;
	}

	public void setArrangement(String arrangement) {
		this.arrangement = arrangement;
	}
	
	public Integer getDiscipline() {
		return discipline;
	}

	public void setDiscipline(Integer discipline) {
		this.discipline = discipline;
	}
	
	@Length(min=0, max=32, message="行业长度必须介于 0 和 32 之间")
	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}
	
	@Length(min=0, max=20, message="职称长度必须介于 0 和 20 之间")
	public String getTechnicalTitle() {
		return technicalTitle;
	}

	public void setTechnicalTitle(String technicalTitle) {
		this.technicalTitle = technicalTitle;
	}
	
	@Length(min=0, max=16, message="服务意向长度必须介于 0 和 16 之间")
	public String getServiceIntention() {
		return serviceIntention;
	}

	public void setServiceIntention(String serviceIntention) {
		this.serviceIntention = serviceIntention;
	}
	
	@Length(min=0, max=128, message="工作单位长度必须介于 0 和 128 之间")
	public String getWorkUnit() {
		return workUnit;
	}

	public void setWorkUnit(String workUnit) {
		this.workUnit = workUnit;
	}
	
	@Length(min=0, max=128, message="联系地址长度必须介于 0 和 128 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Length(min=0, max=512, message="工作简历长度必须介于 0 和 512 之间")
	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}
	
	@Length(min=0, max=128, message="推荐单位长度必须介于 0 和 128 之间")
	public String getRecommendedUnits() {
		return recommendedUnits;
	}

	public void setRecommendedUnits(String recommendedUnits) {
		this.recommendedUnits = recommendedUnits;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public String getAward() {
		return award;
	}

	public void setAward(String award) {
		this.award = award;
	}
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
	@Length(min=0, max=128, message="评审项目名称长度必须介于 0 和 128 之间")
	public String getReviewName() {
		return reviewName;
	}

	public void setReviewName(String reviewName) {
		this.reviewName = reviewName;
	}
	
	@Length(min=0, max=128, message="参与评审年份长度必须介于 0 和 128 之间")
	public String getJoinReviewTime() {
		return joinReviewTime;
	}

	public void setJoinReviewTime(String joinReviewTime) {
		this.joinReviewTime = joinReviewTime;
	}
	
	@Length(min=0, max=128, message="开户银行长度必须介于 0 和 128 之间")
	public String getFirstBank() {
		return firstBank;
	}

	public void setFirstBank(String firstBank) {
		this.firstBank = firstBank;
	}
	
	public Integer getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(Integer bankAccount) {
		this.bankAccount = bankAccount;
	}
	
	@Length(min=0, max=20, message="导师类型长度必须介于 0 和 20 之间")
	public String getTeachertype() {
		return teachertype;
	}

	public void setTeachertype(String teachertype) {
		this.teachertype = teachertype;
	}
	
	@Length(min=0, max=11, message="是否公开长度必须介于 0 和 11 之间")
	public String getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Date getAwardTime() {
		return awardTime;
	}
	public void setAwardTime(Date awardTime) {
		this.awardTime = awardTime;
	}
	public String getAwardLevel() {
		return awardLevel;
	}
	public void setAwardLevel(String awardLevel) {
		this.awardLevel = awardLevel;
	}
	
   
	
}