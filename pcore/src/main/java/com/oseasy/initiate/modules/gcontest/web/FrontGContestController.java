package com.oseasy.initiate.modules.gcontest.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.enums.FileSourceEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.ftp.service.FtpService;
import com.oseasy.initiate.modules.gcontest.entity.GAuditInfo;
import com.oseasy.initiate.modules.gcontest.entity.GContest;
import com.oseasy.initiate.modules.gcontest.entity.GContestAnnounce;
import com.oseasy.initiate.modules.gcontest.entity.GContestAward;
import com.oseasy.initiate.modules.gcontest.service.GAuditInfoService;
import com.oseasy.initiate.modules.gcontest.service.GContestAnnounceService;
import com.oseasy.initiate.modules.gcontest.service.GContestAwardService;
import com.oseasy.initiate.modules.gcontest.service.GContestService;
import com.oseasy.initiate.modules.gcontest.vo.GContestVo;
import com.oseasy.initiate.modules.project.entity.ProjectDeclare;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.sys.entity.SysStudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SysStudentExpansionService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.service.TeamService;

import net.sf.json.JSONObject;

/**
 * 大赛信息Controller
 * @author zy
 * @version 2017-03-11
 */
@Controller
@RequestMapping(value = "${frontPath}/gcontest/gContest")
public class FrontGContestController extends BaseController {

	@Autowired
	private TeamService teamService;
	@Autowired
	private ProjectDeclareService projectDeclareService;
	@Autowired
	private UserService userService;
	@Autowired
	private FtpService ftpService;
	@Autowired
	private SysAttachmentService sysAttachmentService;

	@Autowired
	private GContestService gContestService;

	@Autowired
	private GAuditInfoService gAuditInfoService;

	@Autowired
	private GContestAnnounceService gContestAnnounceService;

	@Autowired
	private SysStudentExpansionService sysStudentExpansionService;

	@Autowired
	GContestAwardService gContestAwardService;


	@ModelAttribute
	public GContest get(@RequestParam(required=false) String id) {
		GContest entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = gContestService.get(id);
		}
		if (entity == null) {
			entity = new GContest();
		}
		return entity;
	}


	@RequestMapping(value = {"list", ""})
	public String list(GContest gContest, HttpServletRequest request, HttpServletResponse response, Model model) {
		/*Page<GContest> page = gContestService.findPage(new Page<GContest>(request, response), gContest);
		model.addAttribute("page", page);*/
		Map<String,Object> param =new HashMap<String,Object>();
		User user = UserUtils.getUser();
		param.put("userid", user.getId());
		Page<Map<String,String>> page = gContestService.getMyGcontestList(new Page<Map<String,String>>(request, response), param);
		model.addAttribute("page", page);
		return "modules/gcontest/gContestList";
	}

	@ResponseBody
	@RequestMapping(value = "findTeamPerson")
	public List<Map<String,String>> findTeamPerson(@RequestParam(required=true) String id) {
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		List<Map<String,String>> list1=projectDeclareService.findTeamStudent(id);
		List<Map<String,String>> list2=projectDeclareService.findTeamTeacher(id);
		for(Map<String,String> map:list1) {
			list.add(map);
		}
		for(Map<String,String> map:list2) {
			list.add(map);
		}
		return list;
	}


	@RequestMapping(value = "form")
	public String form(GContest gContest, Model model) {
		User user = UserUtils.getUser();
		GContestAnnounce gContestAnnounce=new GContestAnnounce();
        gContestAnnounce.setType("1");
        gContestAnnounce.setStatus("1");
        gContestAnnounce=gContestAnnounceService.getGContestAnnounce(gContestAnnounce);
        if (gContestAnnounce!=null) {
        	model.addAttribute("gContestAnnounce", gContestAnnounce);
        }
        model.addAttribute("user", user);
		model.addAttribute("studentExpansion", sysStudentExpansionService.getByUserId(user.getId()));
		if (StringUtil.isNotBlank(gContest.getId())) {
			gContest = gContestService.get(gContest.getId());
			user=userService.findUserById(gContest.getDeclareId());
			SysStudentExpansion sse = new SysStudentExpansion();
			sse.setName(user.getName());
			sse.setEmail(user.getEmail());
			sse.setMobile(user.getMobile());
			if (user.getOffice()!=null) {
				sse.setOffice(user.getOffice());
			}
			sse.setCompany(user.getCompany());
			sse.setProfessional(user.getProfessional());
			sse.setNo(user.getNo());
			model.addAttribute("sse", sse);
			model.addAttribute("gContest", gContest);
			model.addAttribute("competitionNumber", gContest.getCompetitionNumber());
			//关联附件
			SysAttachment sysAttachment=new SysAttachment();
			sysAttachment.setUid(gContest.getId());
			Map<String,String> map=new HashMap<String,String>();
			map.put("uid", gContest.getId());
			map.put("type",FileSourceEnum.S2.getValue());
			List<Map<String, String>>   sysAttachments=sysAttachmentService.getFileInfo(map);
			model.addAttribute("sysAttachments", sysAttachments);
			//model.addAttribute("studentExpansion", sysStudentExpansionService.getByUserId(user.getId()));
			GContestVo vo=new GContestVo();
			vo.setTeamStudent(projectDeclareService.findTeamStudent(gContest.getTeamId()));
			vo.setTeamTeacher(projectDeclareService.findTeamTeacher(gContest.getTeamId()));
			model.addAttribute("gContestVo", vo);
			//model.addAttribute("gContest", gContest);
			//关联团队
			/*Team team=new Team();
			team.setSponsor(user.getId());
			List<Team> teams=	teamService.findList(team);*/
			//model.addAttribute("teams", teams);
			model.addAttribute("sysdate", DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
		}else {
			SysStudentExpansion sse = new SysStudentExpansion();
			sse.setName(user.getName());
			sse.setEmail(user.getEmail());
			sse.setMobile(user.getMobile());
			sse.setCompany(user.getCompany());
			if (user.getOffice()!=null) {
				sse.setOffice(user.getOffice());
			}
			sse.setProfessional(user.getProfessional());
			sse.setNo(user.getNo());
			gContest.setDeclareId(user.getId());
			model.addAttribute("gContest", gContest);
			model.addAttribute("sse", sse);
			model.addAttribute("isHave", true);
		}
		//关联团队
		model.addAttribute("teams", projectDeclareService.findTeams(user.getId(),gContest.getTeamId()));
		//关联项目
		ProjectDeclare projectDeclare = new ProjectDeclare();
		projectDeclare.setLeader(user.getId());
		List<ProjectDeclare> projects=	projectDeclareService.getCurProjectInfoByLeader(user.getId());
		model.addAttribute("projects", projects);
		return "modules/gcontest/gContestForm";
	}
	@RequestMapping(value = "viewForm")
	public String viewForm(GContest gContest, Model model) {
		User user = UserUtils.getUser();
		if (StringUtil.isNotBlank(gContest.getId())) {
			gContest = gContestService.get(gContest.getId());
			user=userService.findUserById(gContest.getDeclareId());
			SysStudentExpansion sse = new SysStudentExpansion();
			sse.setName(user.getName());
			sse.setEmail(user.getEmail());
			sse.setMobile(user.getMobile());
			sse.setCompany(user.getCompany());
			sse.setOffice(user.getOffice());
			sse.setProfessional(user.getProfessional());
			sse.setNo(user.getNo());
			gContest.setDeclareId(user.getId());
			model.addAttribute("sse", sse);
			model.addAttribute("gContest", gContest);
			model.addAttribute("competitionNumber", gContest.getCompetitionNumber());
			if (gContest.getAuditState().equals("2")) {
				model.addAttribute("isHave", true);
			}
		}
		SysAttachment sysAttachment=new SysAttachment();
		sysAttachment.setUid(gContest.getId());
		List<SysAttachment> sysAttachments=sysAttachmentService.findList(sysAttachment);
		model.addAttribute("sysAttachments", sysAttachments);
		GContestVo vo=new GContestVo();
		model.addAttribute("studentExpansion", sysStudentExpansionService.getByUserId(user.getId()));
		vo.setTeamStudent(projectDeclareService.findTeamStudent(gContest.getTeamId()));
		vo.setTeamTeacher(projectDeclareService.findTeamTeacher(gContest.getTeamId()));
		model.addAttribute("gContestVo", vo);
		//关联团队
		if (gContest.getTeamId()!=null) {
			Team team=teamService.get(gContest.getTeamId());
			model.addAttribute("team", team);
		}
		//关联项目
		ProjectDeclare projectDeclare = new ProjectDeclare();
		projectDeclare.setLeader(user.getId());
		if (gContest.getpId()!=null) {
			ProjectDeclare pd=projectDeclareService.get(gContest.getpId());
			if (pd!=null) {
				model.addAttribute("relationProject", pd.getName());
			}
		}
		//审核记录
		List<GAuditInfo> collegeinfos= getSortInfo(gContest.getId(),"2");
		model.addAttribute("collegeinfos", collegeinfos);
		List<GAuditInfo> wpinfos= getSortInfo(gContest.getId(),"4");
		model.addAttribute("wpinfos", wpinfos);
  	 	List<GAuditInfo> lyinfos= getSortInfo(gContest.getId(),"5");
  	 	model.addAttribute("lyinfos", lyinfos);
  	 	GContestAward gca=gContestAwardService.getByGid(gContest.getId());
  		model.addAttribute("gca", gca);
		return "modules/gcontest/gContestDetail";
	}
	 private  List<GAuditInfo> getSortInfo(String gId,String auditStep) {
		GAuditInfo pai=new GAuditInfo();
        pai.setGId(gId);
        pai.setAuditLevel(auditStep);
        List<GAuditInfo> infos= gAuditInfoService.getSortInfo(pai);
        return infos;
    }
	@RequestMapping(value = "save")
	public String save(GContest gContest, Model model, HttpServletRequest request,  RedirectAttributes redirectAttributes) {
		String[] arrUrl= request.getParameterValues("arrUrl");
		String[] arrNames= request.getParameterValues("arrName");
		gContestService.save(gContest);
		if (arrUrl!=null&&arrUrl.length>0) {
			for(int i=0;i<arrUrl.length;i++) {
				try {
					String moveEnd = FtpUtil.moveFile(FtpUtil.getftpClient(), arrUrl[i]);
					arrUrl[i]=moveEnd;
				} catch (IOException e) {
					e.printStackTrace();
				}

				SysAttachment sysAttachment=new SysAttachment();
				sysAttachment.setUid(gContest.getId());
				sysAttachment.setType("2");
				sysAttachment.setName(arrNames[i]);
				sysAttachment.setUrl(arrUrl[i]);
				sysAttachment.setSuffix(arrNames[i].substring(arrNames[i].lastIndexOf(".")+1));
				sysAttachmentService.save(sysAttachment);
			}
		}
		addMessage(redirectAttributes, "保存大赛信息成功");
		return "redirect:"+Global.getFrontPath()+"/gcontest/gContest/?repage";
	}

	@RequestMapping(value = "delFile")
	@ResponseBody
	public JSONObject delFile( HttpServletRequest request) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "删除成功");
		String arrUrl= request.getParameter("arrUrl");
		String id= request.getParameter("id");
		try {
			if (id!=null&&!"null".equals(id))sysAttachmentService.delete(new SysAttachment(id));
			ftpService.del(arrUrl);
		} catch (Exception e) {
			js.put("ret", 0);
			js.put("msg", "删除失败");
		}
		return js;
	}

	@RequestMapping(value = {"viewList"})
	public String viewList(GContest gContest, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/gcontest/front/calender";
	}

	@RequestMapping(value = "getGcontestTimeIndexData")
	@ResponseBody
	public JSONObject viewLit(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		JSONObject js=new JSONObject();
		GContest gContest=new GContest();
		User user = UserUtils.getUser();
        List<GContest> glist=gContestService.getGcontestInfo(user.getId());
        if (glist.size()>0) {
        	gContest=glist.get(0);
        	gContest.setId(gContest.getId());
        	gContest=gContestService.get(gContest);
        }else{
        	gContest=gContestService.getLastGcontestInfo(user.getId());
        	if (gContest!=null) {
        		gContest.setId(gContest.getId());
             	gContest=gContestService.get(gContest);
        	}
        }
        //后台做判断
        js=gContestService.getListData(gContest);
		return js;
	}

	@RequestMapping(value = "submit")
	@ResponseBody
	public JSONObject submit(GContest gContest, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		js.put("msg", "恭喜您，大赛申报成功!");
		if (gContest.getId()==null||"".equals(gContest.getId())) {
			User user = UserUtils.getUser();
			List<GContest> glist=gContestService.getGcontestInfo(user.getId());
			if (glist!=null&&glist.size()>0) {
				js.put("ret", 0);
				js.put("msg", "提交失败，当前已存在未完成的大赛");
				return js;
			}
		}
		if (gContest.getPName()!=null) {
			if (gContest.getId()!=null) {
				List<GContest> gname=gContestService.getGcontestByNameNoId(gContest.getId(),gContest.getPName());
				if (gname!=null&&gname.size()!=0) {
					js.put("ret", 0);
					js.put("msg", "提交失败，存在名字相同项目");
					return js;
				}
			}else{
				List<GContest> gname=gContestService.getGcontestByName(gContest.getPName());
				if (gname!=null&&gname.size()!=0) {
					js.put("ret", 0);
					js.put("msg", "提交失败，存在名字相同项目");
					return js;
				}
			}
		}

		int collExportNum=1;
		collExportNum=gContestService.submit(gContest);
		if (collExportNum==0) {
			js.put("ret", 0);
			js.put("msg", "提交失败，该学院无学院专家");
			return js;
		}
		String[] arrUrl= request.getParameterValues("arrUrl");
		String[] arrNames= request.getParameterValues("arrName");
		if (arrUrl!=null&&arrUrl.length>0) {
			for(int i=0;i<arrUrl.length;i++) {
				 try {
					String moveEnd=FtpUtil.moveFile(FtpUtil.getftpClient(), arrUrl[i]);
					arrUrl[i]=moveEnd;
				} catch (IOException e) {
					logger.info("大赛更新文件目录地址出错");
					e.printStackTrace();
				}
				SysAttachment sysAttachment=new SysAttachment();
				sysAttachment.setUid(gContest.getId());
				sysAttachment.setType("2");
				sysAttachment.setName(arrNames[i]);
				sysAttachment.setUrl(arrUrl[i]);
				sysAttachment.setSuffix(arrNames[i].substring(arrNames[i].lastIndexOf(".")+1));
				sysAttachmentService.save(sysAttachment);
			}
		}
		addMessage(redirectAttributes, "提交大赛信息成功");
		//return "redirect:"+Global.getFrontPath()+"/gcontest/gContest/?repage";
		return js;
	}


	@RequestMapping(value = "delete")
	public String delete(GContest gContest, RedirectAttributes redirectAttributes) {
		gContestService.delete(gContest);
		addMessage(redirectAttributes, "删除大赛信息成功");
		return "redirect:"+Global.getFrontPath()+"/gcontest/gContest/?repage";
	}

}