package com.oseasy.initiate.test;

import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.gcontest.entity.GAuditInfo;
import com.oseasy.initiate.modules.gcontest.entity.GContest;
import com.oseasy.initiate.modules.gcontest.service.GAuditInfoService;
import com.oseasy.initiate.modules.gcontest.service.GContestService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.project.vo.ProjectDeclareVo;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangzheng on 2017/2/23.
 * 国创大赛后台测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-context-activiti.xml"})
public class GcontestServicTest {
    @Autowired
    private GContestService gcontestService;
    @Autowired
	private SysAttachmentService sysAttachmentService;
    @Autowired
	private GAuditInfoService gAuditInfoService;
    @Autowired
  	private UserService userService;
    @Autowired
    private ProjectDeclareService projectDeclareService;
    
    @Test
    public void test2() {
        List<GContest> vars=  gcontestService.getGcontestByName("tt");
        System.out.println(vars);
    }

    @Test
    public void getDataJson() {
    	GContest gContest=new GContest();
    	JSONObject obj = new JSONObject();
    	obj=gcontestService.getListData(gContest);
    	System.out.println(obj.toString());
    }
    @Test
    public void getJson() {
    	//List<GContest> vars=  gcontestService.getGcontestByName("tt");
    	//System.out.println(vars);
    	GContest gContest=new GContest();
    	gContest.setId("dfd0f79e1be74f0aacd6fe9faebad34b");
    	gContest=gcontestService.get(gContest);
        User uesr=userService.findUserById(gContest.getDeclareId());
        //jsondata 生产
    	JSONObject obj = new JSONObject();
		//项目基础信息表头
		JSONObject gContestobj = new JSONObject();
		gContestobj.put("code", gContest.getCompetitionNumber());
		gContestobj.put("name", gContest.getpName());
		
		//参赛组别
		String gcontestLevel="初创组";
				//DictUtils.getDictLabel(gContest.getLevel(), "gcontest_level", "");//.getDictList("gcontest_level").;
		
		//项目内容table_first
		JSONObject gContestContentlist = new JSONObject();
		//list
		JSONArray  gContestContentlistArray = new JSONArray ();
			//报名信息
			JSONObject arraySb= new JSONObject();
			arraySb.put("type", "0");
			//(从大赛申报表里面读取)
			arraySb.put("Date", "2017-04-15");
			arraySb.put("SpeedOfProgress", "提交报名表");
			
			//添加附件
			SysAttachment sysAttachment=new SysAttachment();
			sysAttachment.setUid(gContest.getId());
			List<SysAttachment> sysAttachments=sysAttachmentService.findList(sysAttachment);
			JSONArray  arraySbAtt = new JSONArray ();
			for(int i=0;i<sysAttachments.size();i++) {
				JSONObject attachmentobj = new JSONObject();
				attachmentobj.put("link", sysAttachments.get(i).getName());
				attachmentobj.put("url", sysAttachments.get(i).getUrl());
				arraySbAtt.add(attachmentobj);
			}
			arraySb.put("list", arraySbAtt);
		gContestContentlistArray.add(arraySb);
		
		//院赛信息
			JSONObject arrayYs= new JSONObject();
				arrayYs.put("type", "1");
				//(从大赛申报表里面读取)
				arrayYs.put("Date", "2017-05-15");
				arrayYs.put("SpeedOfProgress", "院级");
				arrayYs.put("type", "1");
				arrayYs.put("group", gcontestLevel);
				arrayYs.put("School", uesr.getOffice().getName());
				String num="30"; //从大赛表中查询出来大赛学院排名
				arrayYs.put("Number_of_entries",num);
		
				JSONArray  arrayYsList = new JSONArray ();	
				//院赛信息排名
					JSONObject arrayPm= new JSONObject();
					GAuditInfo collegeinfos= getSortInfoByIdAndState(gContest.getId(),"2");
					if (collegeinfos!=null) {
						arrayPm.put("College_score",collegeinfos.getScore());
						arrayPm.put("Proposal", collegeinfos.getSuggest());
						arrayPm.put("ranking", collegeinfos.getSort());
						arrayYsList.add(arrayPm);
						arrayYs.put("list", arrayYsList);
						gContestContentlistArray.add(arrayYs);	
						//校赛信息
						JSONObject arrayXs= new JSONObject();
						arrayXs.put("type", "2");
						arrayXs.put("Date", "2017-06-15");
						arrayXs.put("SpeedOfProgress", "校赛");
						arrayXs.put("group", gcontestLevel);
						arrayXs.put("School", uesr.getCompany().getName());
						//从获奖表中获取
						arrayXs.put("Awards","冠军");
						arrayXs.put("bonus","$50000");
						String numXs="300"; //从大赛表中查询出来大赛学校排名
						arrayXs.put("Number_of_entries",numXs);
						JSONArray  arrayXsList = new JSONArray ();
						GAuditInfo wangpinginfos= getSortInfoByIdAndState(gContest.getId(),"4");
						JSONObject xsWangpin= new JSONObject();
						if (wangpinginfos!=null) {
							xsWangpin.put("getScore",wangpinginfos.getScore());
							xsWangpin.put("Review_the_content", wangpinginfos.getAuditName());
							xsWangpin.put("Current_rank", wangpinginfos.getSort());
							xsWangpin.put("advice", wangpinginfos.getSuggest());
							arrayXsList.add(xsWangpin);
						}
						GAuditInfo luyaninfos= getSortInfoByIdAndState(gContest.getId(),"5");
						JSONObject xsLuyan= new JSONObject();
						if (luyaninfos!=null) {
							xsLuyan.put("getScore",luyaninfos.getScore());
							xsLuyan.put("Review_the_content", luyaninfos.getAuditName());
							xsLuyan.put("Current_rank", luyaninfos.getSort());
							xsLuyan.put("advice", luyaninfos.getSuggest());
							arrayXsList.add(xsLuyan);
						}
						if (arrayXsList.size()>0) {
							arrayXs.put("list", arrayXsList);
							gContestContentlistArray.add(arrayXs);
						}	
					}

		gContestContentlist.put("list",gContestContentlistArray);
		
		obj.put("project_code", gContestobj);
		obj.put("table_first", gContestContentlist);
		System.out.println(obj.toString());
    }
    
    private  GAuditInfo getSortInfoByIdAndState(String gId,String auditStep) {
		GAuditInfo pai=new GAuditInfo();
        pai.setGId(gId);
        pai.setAuditLevel(auditStep);
        GAuditInfo infos= gAuditInfoService.getSortInfoByIdAndState(pai);
        return infos;
    }
    
    @Test
    public void saveTest() {
    	List<Map<String,String>> vars =projectDeclareService.getCurProjectInfo("fc050e4b736844d9ab1e49a762e2635a");
    	System.out.println(vars);
    	List<GContest> var=gcontestService.getGcontestInfo("fc050e4b736844d9ab1e49a762e2635a");
     	System.out.println(var);
    }

}
