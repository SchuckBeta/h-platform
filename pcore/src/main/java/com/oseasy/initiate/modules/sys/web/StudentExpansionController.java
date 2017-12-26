package com.oseasy.initiate.modules.sys.web;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.ftp.service.FtpService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.project.vo.ProjectExpVo;
import com.oseasy.initiate.modules.sys.entity.Dict;
import com.oseasy.initiate.modules.sys.entity.GContestUndergo;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.StudentExpansionService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

import net.sf.json.JSONObject;

/**
 * 学生扩展信息表Controller
 *
 * @author zy
 * @version 2017-03-27
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/studentExpansion")
public class StudentExpansionController extends BaseController {

	@Autowired
	private StudentExpansionService studentExpansionService;
	@Autowired
	private UserService  userService;
	@Autowired
	private FtpService ftpService;
	@Autowired
	private TeamUserRelationService teamUserRelationService;
	@Autowired
	ProjectDeclareService projectDeclareService;

	@ModelAttribute
	public StudentExpansion get(@RequestParam(required = false) String id,Model model) {
		StudentExpansion entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = studentExpansionService.get(id);
		}
		if (entity == null) {
			entity = new StudentExpansion();
		}
		return entity;
	}

	@RequiresPermissions("sys:studentExpansion:view")
	@RequestMapping(value = { "list", "" })
	public String list(StudentExpansion studentExpansion, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Page<StudentExpansion> page = studentExpansionService.findPage(new Page<StudentExpansion>(request, response),studentExpansion);
		model.addAttribute("page", page);

		return "modules/sys/studentExpansionList";
	}

	@RequiresPermissions("sys:studentExpansion:view")
	@RequestMapping(value = "form")
	public String form(StudentExpansion studentExpansion, Model model,HttpServletRequest request) {
		User user=null;
		if (studentExpansion.getUser()!=null) {
			user = studentExpansion.getUser();
			if (user!=null) {
				if (StringUtil.isNotBlank(user.getPhoto())) {
					model.addAttribute("user", user);
				}

			List<ProjectExpVo> projectExpVo=studentExpansionService.findProjectByStudentId(studentExpansion.getUser().getId());//查询项目经历
			List<GContestUndergo> gContest=studentExpansionService.findGContestByStudentId(studentExpansion.getUser().getId()); //查询大赛经历
			model.addAttribute("projectExpVo", projectExpVo);
			model.addAttribute("gContestExpVo", gContest);
			model.addAttribute("cuser", studentExpansion.getUser().getId());
			}
		}
		model.addAttribute("studentExpansion", studentExpansion);
		List<Dict> dictList = DictUtils.getDictList("technology_field");
		model.addAttribute("allDomains", dictList);
		model.addAttribute("userName", studentExpansion.getUser()==null?null:studentExpansion.getUser().getName());

		return "modules/sys/studentForm";
	}


	@RequiresPermissions("sys:studentExpansion:edit")
	@RequestMapping(value = "save")
	public String save(StudentExpansion studentExpansion,HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
	    if (StringUtil.isNotBlank(studentExpansion.getId())) {
			studentExpansionService.updateAll(studentExpansion);
		}else {
			studentExpansionService.saveAll(studentExpansion);
		}
		return "redirect:" + Global.getAdminPath() + "/sys/studentExpansion/?repage";

	}

	@RequiresPermissions("sys:studentExpansion:edit")
	@RequestMapping(value = "delete")
	public String delete(StudentExpansion studentExpansion, RedirectAttributes redirectAttributes) {
		TeamUserRelation teamUserRelation = new TeamUserRelation();
		teamUserRelation.setUser(studentExpansion.getUser());
		teamUserRelation = teamUserRelationService.findUserById(teamUserRelation);
		if (teamUserRelation!=null) {
			addMessage(redirectAttributes, "该学生已加入团队，不能删除!");
			return "redirect:"+Global.getAdminPath()+"/sys/studentExpansion/?repage";
		}
		studentExpansionService.delete(studentExpansion);
		addMessage(redirectAttributes, "删除学生扩展信息表成功");
		//删除用户表
		User user= UserUtils.get(studentExpansion.getUser().getId());
		userService.delete(user);
		return "redirect:" + Global.getAdminPath() + "/sys/studentExpansion/?repage";
	}

	//批量删除 返回成功删除数，失败删除数
	@RequestMapping(value = "deleteBatch")
	@ResponseBody
	public String deleteBatch(String ids) {
		String[] idStr=ids.split(",");
		int successCount=0;
		int failCount=0;
		for (int i=0;i<idStr.length;i++) {
			StudentExpansion studentExpansion = studentExpansionService.get(idStr[i]);
			TeamUserRelation teamUserRelation = new TeamUserRelation();
			teamUserRelation.setUser(studentExpansion.getUser());
			teamUserRelation = teamUserRelationService.findUserById(teamUserRelation);
			if (teamUserRelation!=null) {
				failCount++;
			}else{
				successCount++;
				studentExpansionService.delete(studentExpansion);
				User user= UserUtils.get(studentExpansion.getUser().getId());
				userService.delete(user);
			}
		}
		String message;
		if (failCount==0) {
			message="成功删除"+successCount+"个学生。";
		}else{
			message="成功删除"+successCount+"个学生。"+failCount+"个学生已加入团队，不能删除!";
		}
		return message;
	}


	@RequiresPermissions("sys:studentExpansion:edit")
	@RequestMapping(value = "addStu")
	public String AddStu(StudentExpansion studentExpansion, RedirectAttributes redirectAttributes) {
		String uid = UUID.randomUUID().toString().replaceAll("-", "");
		studentExpansion.getUser().setId(uid);
		studentExpansionService.save(studentExpansion);
		return "redirect:" + Global.getAdminPath() + "/sys/studentExpansion/?repage";
	}
	@RequestMapping(value = "/toAdd")
	public String toAdd() {
		return  "modules/sys/addStudentExpansion";
	}

	//删除文件
	@RequestMapping(value = {"delload"})
	@ResponseBody
	public JSONObject delload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//path ftp上文件 目录
		//String path=(String)request.getParameter("path");//
		//fileName ftp上文件名
		String fileName=(String)request.getParameter("fileName");
		String studentId = request.getParameter("studentId");
		StudentExpansion studentExpansion = studentExpansionService.get(studentId);
		String userId = studentExpansion.getUser().getId();
		boolean ftpdel=ftpService.del(fileName);
		/*FtpUtil ftpUtil = new FtpUtil();
		FTPClient ftpClient=ftpUtil.getftpClient();*/
		//ftpUtil.remove(ftpClient, fileName.substring(0,fileName.lastIndexOf("/")+1), fileName.substring(fileName.lastIndexOf("/")+1));
		JSONObject obj = new JSONObject();
		User user = userService.findUserById(userId);
		user.setPhoto("");
		userService.updateUser(user);
		if (ftpdel) {
			obj.put("state",1);//删除成功
		}else{
			obj.put("state", 2);
			obj.put("msg", "文件太大");
		}
		return obj;
	}

}