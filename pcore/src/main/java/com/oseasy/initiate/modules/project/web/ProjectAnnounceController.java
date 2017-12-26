package com.hch.platform.pcore.modules.project.web;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.pcore.common.utils.FtpUtil;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.attachment.enums.FileTypeEnum;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.ftp.service.FtpService;
import com.hch.platform.pcore.modules.project.entity.ProjectAnnounce;
import com.hch.platform.pcore.modules.project.service.ProjectAnnounceService;

/**
 * 项目通告Controller
 * @author zdk
 * @version 2017-03-30
 */
@Controller
@RequestMapping(value = "${adminPath}/project/projectAnnounce")
public class ProjectAnnounceController extends BaseController {

	@Autowired
	private ProjectAnnounceService projectAnnounceService;

	@Autowired
	private SysAttachmentService sysAttachmentService;

	@Autowired
	private FtpService ftpService;

	@ModelAttribute
	public ProjectAnnounce get(@RequestParam(required=false) String id) {
		ProjectAnnounce entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = projectAnnounceService.get(id);
		}
		if (entity == null) {
			entity = new ProjectAnnounce();
		}
		return entity;
	}

	@RequiresPermissions("project:projectAnnounce:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProjectAnnounce projectAnnounce, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProjectAnnounce> page = projectAnnounceService.findPage(new Page<ProjectAnnounce>(request, response), projectAnnounce);
		model.addAttribute("page", page);
		return "modules/project/projectAnnounceList";
	}

	@RequiresPermissions("project:projectAnnounce:view")
	@RequestMapping(value = "form")
	public String form(ProjectAnnounce projectAnnounce, Model model,HttpServletRequest request) {
		SysAttachment sysAttachment=new SysAttachment();
		if (projectAnnounce.getId()!=null) {
			sysAttachment.setUid(projectAnnounce.getId());
			List<SysAttachment> sysAttachments=sysAttachmentService.findList(sysAttachment);
			model.addAttribute("sysAttachments", sysAttachments);
		}
		String operationType = request.getParameter("operationType");

    if (StringUtil.isNotEmpty(operationType)) {
			model.addAttribute("operationType", operationType);
		}
		model.addAttribute("projectAnnounce", projectAnnounce);
		return "modules/project/projectAnnounceForm";
	}

	@RequiresPermissions("project:projectAnnounce:view")
	@RequestMapping(value = "formEdit")
	public String formEdit(ProjectAnnounce projectAnnounce, Model model,HttpServletRequest request) {
		SysAttachment sysAttachment=new SysAttachment();
		if (projectAnnounce.getId()!=null) {
			sysAttachment.setUid(projectAnnounce.getId());
			List<SysAttachment> sysAttachments=sysAttachmentService.findList(sysAttachment);
			model.addAttribute("sysAttachments", sysAttachments);
		}
		String operationType = request.getParameter("operationType");
    if (StringUtil.isNotEmpty(operationType)) {
			model.addAttribute("operationType", operationType);
		}
		model.addAttribute("projectAnnounce", projectAnnounce);
		return "modules/project/projectAnnounceFormEdit";
	}

	private Date addSuffix(Date d) {
		if (d==null) {
			return null;
		}
		try {
			return DateUtil.parseDate(DateUtil.formatDate(d, "yyyy-MM-dd")+" 23:59:59","yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			return null;
		}
	}
	@RequiresPermissions("project:projectAnnounce:edit")
	@RequestMapping(value = "save")
	public String save(ProjectAnnounce projectAnnounce,HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, projectAnnounce)) {
			return form(projectAnnounce, model,request);
		}
		String[] arrUrl= request.getParameterValues("arrUrl");
		String[] arrNames= request.getParameterValues("arrName");
		projectAnnounce.setEndDate(addSuffix(projectAnnounce.getEndDate()));
		projectAnnounce.setMidEndDate(addSuffix(projectAnnounce.getMidEndDate()));
		projectAnnounce.setFinalEndDate(addSuffix(projectAnnounce.getFinalEndDate()));
		projectAnnounce.setpIniEnd(addSuffix(projectAnnounce.getpIniEnd()));
		projectAnnounceService.save(projectAnnounce);
		if (arrUrl!=null&&arrUrl.length>0) {
			for(int i=0;i<arrUrl.length;i++) {
				 FtpUtil t = new FtpUtil();
				 try {
					String moveEnd=t.moveFile(t.getftpClient(), arrUrl[i]);
					arrUrl[i]=moveEnd;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SysAttachment sysAttachment=new SysAttachment();
				sysAttachment.setUid(projectAnnounce.getId());
				sysAttachment.setType(FileTypeEnum.S3);
				sysAttachment.setName(arrNames[i]);
				sysAttachment.setUrl(arrUrl[i]);
				sysAttachment.setSuffix(arrNames[i].substring(arrNames[i].lastIndexOf(".")+1));
				sysAttachmentService.save(sysAttachment);
			}
		}
	//	addMessage(redirectAttributes, "保存项目通告成功");
		return "redirect:"+Global.getAdminPath()+"/project/projectAnnounce/?repage";
	}

	@RequiresPermissions("project:projectAnnounce:edit")
	@RequestMapping(value = "delete")
	public String delete(ProjectAnnounce projectAnnounce, RedirectAttributes redirectAttributes) {
		projectAnnounceService.delete(projectAnnounce);
	//	addMessage(redirectAttributes, "删除项目通告成功");
		return "redirect:"+Global.getAdminPath()+"/project/projectAnnounce/?repage";
	}

	//删除文件
	@RequestMapping(value = {"delload"})
	@ResponseBody
	public JSONObject delload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//path ftp上文件 目录
		//String path=(String)request.getParameter("path");//
		//fileName ftp上文件名
		String fileName=(String)request.getParameter("fileName");
		boolean ftpdel=ftpService.del(fileName);
		/*FtpUtil ftpUtil = new FtpUtil();
		FTPClient ftpClient=ftpUtil.getftpClient();*/
		//ftpUtil.remove(ftpClient, fileName.substring(0,fileName.lastIndexOf("/")+1), fileName.substring(fileName.lastIndexOf("/")+1));
		JSONObject obj = new JSONObject();
		SysAttachment sysAttachment = new SysAttachment();
		sysAttachment = sysAttachmentService.getByUrl(fileName);
		sysAttachmentService.delete(sysAttachment);
		if (ftpdel) {
			obj.put("state",1);//删除成功
		}else{
			obj.put("state", 2);
			obj.put("msg", "文件太大");
		}
		//response.getWriter().print(obj.toString());
		return obj;
		//downloadFile(ftpClient, response, fileName.substring(fileName.lastIndexOf("/")+1), fileName.substring(0,fileName.lastIndexOf("/")+1));
	}
	@RequestMapping(value="validateName")
	@ResponseBody
	public String validateName(HttpServletRequest request) {
		String id=(String)request.getParameter("id");
		String name=(String)request.getParameter("name");
		Map<String,String> map=new HashMap<String,String>();
		map.put("name",name);
		if (!StringUtil.isEmpty(id)) {
			map.put("id",id);
		}
		if (projectAnnounceService.getProjectByName(map)>0) {
			return "1";
		}
		return "0";

	}

	@RequestMapping(value="publish")
	@ResponseBody
	public String publish(String type) {
		ProjectAnnounce projectAnnounce = new ProjectAnnounce();
		projectAnnounce.setProType(type);
		List<ProjectAnnounce> list = projectAnnounceService.findList(projectAnnounce);
		for (ProjectAnnounce projectAnnounce2 : list) {
			String status = projectAnnounce2.getStatus();
			if ("1".equals(status)) {
				return "1";
			}else if ("0".equals(status)) {
				return "2";
			}else {
				continue;
			}
		}
		return "-1";
	}
}