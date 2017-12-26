package com.hch.platform.pcore.modules.promodel.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hch.platform.pconfig.common.Global;
import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.actyw.entity.ActYw;
import com.hch.platform.pcore.modules.actyw.entity.ActYwGroup;
import com.hch.platform.pcore.modules.actyw.service.ActYwService;
import com.hch.platform.pcore.modules.actyw.tool.process.vo.FlowProjectType;
import com.hch.platform.pcore.modules.actyw.tool.process.vo.FlowType;
import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.attachment.enums.FileStepEnum;
import com.hch.platform.pcore.modules.attachment.enums.FileTypeEnum;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.promodel.entity.ProModel;
import com.hch.platform.pcore.modules.promodel.service.ProModelService;
import com.hch.platform.pcore.modules.proproject.entity.ProProject;
import com.hch.platform.pcore.modules.proprojectmd.entity.ProModelMd;
import com.hch.platform.pcore.modules.proprojectmd.service.ProModelMdService;
import com.hch.platform.pcore.modules.sys.entity.Dict;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.service.SystemService;
import com.hch.platform.pcore.modules.sys.utils.DictUtils;
import com.hch.platform.pcore.modules.team.entity.Team;
import com.hch.platform.pcore.modules.team.service.TeamService;

/**
 * proModelController.
 * @author zy
 * @version 2017-07-13
 */
@Controller
@RequestMapping(value = "${adminPath}/promodel/proModel")
public class ProModelController extends BaseController {
	@Autowired
	private ProModelMdService proModelMdService;
	@Autowired
	private ProModelService proModelService;
	@Autowired
	private ProjectDeclareService projectDeclareService;
	@Autowired
	private SysAttachmentService sysAttachmentService;
	@Autowired
	private ActYwService actYwService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private SystemService systemService;

	@ModelAttribute
	public ProModel get(@RequestParam(required=false) String id) {
		ProModel entity = null;
		if (StringUtil.isNotBlank(id)){
			entity = proModelService.get(id);
		}
		if (entity == null){
			entity = new ProModel();
		}
		return entity;
	}

	@RequiresPermissions("promodel:proModel:view")
	@RequestMapping(value = {"list", ""})
	public String list(ProModel proModel, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProModel> page = proModelService.findPage(new Page<ProModel>(request, response), proModel);
		model.addAttribute("page", page);
		return "modules/promodel/proModelList";
	}

	@RequestMapping(value = "view")
	public String view(ProModel proModel, HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("proModel", proModel);
		return "modules/promodel/view";
	}

	@RequestMapping(value = "auditForm")
	public String audit(ProModel proModel, HttpServletRequest request, HttpServletResponse response, Model model) {
		String urlPath=request.getParameter("urlPath");
		String actionPath=request.getParameter("actionPath");
		String gnodeId=request.getParameter("gnodeId");
		String proModelId=request.getParameter("proModelId");
		ActYw actYw=actYwService.get(proModel.getActYwId());
		if(proModelId!=null){
			proModel=proModelService.get(proModelId);

			//查找学生
			/*TeamUserRelation tur1=new TeamUserRelation();
			tur1.setTeamId(proModel.getTeamId());
			List<TeamUserRelation> turStudents=teamUserRelationService.getStudents(tur1);
			model.addAttribute("turStudents",turStudents);
			//查找导师
			List<TeamUserRelation>  turTeachers=teamUserRelationService.getTeachers(tur1);
			model.addAttribute("turTeachers",turTeachers);*/


			if(actYw!=null && actYw.getKeyType()!=null){
				ProModelMd proModelMd=proModelMdService.getByProModelId(proModelId);
				SysAttachment sa=new SysAttachment();
				sa.setUid(proModelMd.getProModel().getId());
				sa.setType(FileTypeEnum.S10);
				sa.setFileStep(FileStepEnum.S2000);
				List<SysAttachment> fileListMap =  sysAttachmentService.getFiles(sa);
				if(fileListMap!=null){
					model.addAttribute("sysAttachments",fileListMap);
				}
				model.addAttribute("proModelMd",proModelMd);
			}
		}
		if(proModel.getSubTime()!=null){
			model.addAttribute("sysdate", DateUtil.formatDate(proModel.getSubTime(),"yyyy-MM-dd"));
		}else {
			model.addAttribute("sysdate", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
		}

		if(proModel.getTeamId()!=null){
			Team team=teamService.get(proModel.getTeamId());
			model.addAttribute("team",team);
			model.addAttribute("teamStu", projectDeclareService.findTeamStudentFromTUH(proModel.getTeamId(),proModel.getId()));
			model.addAttribute("teamTea", projectDeclareService.findTeamTeacherFromTUH(proModel.getTeamId(),proModel.getId()));
		}

		SysAttachment sa=new SysAttachment();
		sa.setUid(proModel.getId());
		sa.setFileStep(FileStepEnum.S1100);
		sa.setType(FileTypeEnum.S11);
		List<SysAttachment> fileListMap =  sysAttachmentService.getFiles(sa);
		if(fileListMap.size()>0){
			model.addAttribute("sysAttachments", fileListMap);
		}
		model.addAttribute("proModel", proModel);

		if(actYw != null){
		  model.addAttribute("actYw", actYw);
		  model.addAttribute("proProject", actYw.getProProject());
		}
		model.addAttribute("actionPath", actionPath);
		model.addAttribute("gnodeId", gnodeId);
		return urlPath;
	}

	@RequestMapping(value = "promodelAudit")
	public String promodelAudit(ProModel proModel,HttpServletRequest request, HttpServletResponse response, Model model) {
		proModelService.audit(proModel);
		//model.addAttribute("proModel", proModel);
		String actionPath=request.getParameter("actionPath");
		String gnodeId=request.getParameter("gnodeId");
		return  "redirect:"+Global.getAdminPath()+"/cms/form/gContest/"+actionPath+"&gnodeId="+gnodeId;
	}

	@RequiresPermissions("promodel:proModel:edit")
	@RequestMapping(value = "delete")
	public String delete(ProModel proModel, RedirectAttributes redirectAttributes) {
		proModelService.delete(proModel);
		addMessage(redirectAttributes, "删除proModel成功");
		return "redirect:"+Global.getAdminPath()+"/promodel/proModel/?repage";
	}

	@RequestMapping(value = "viewForm")
	public String viewForm(ProModel proModel, Model model) {
		model.addAttribute("proModel", proModel);
		AbsUser user=systemService.getUser(proModel.getDeclareId());
		ActYw actYw=actYwService.get(proModel.getActYwId());
		ProProject proProject=actYw.getProProject();
		if(proProject!=null){
			showFrontMessage(proProject,model);
		}
		model.addAttribute("sse", user);
		model.addAttribute("projectName", actYw.getProProject().getProjectName());
		model.addAttribute("team", teamService.get(proModel.getTeamId()));
		model.addAttribute("teamStu", projectDeclareService.findTeamStudentFromTUH(proModel.getTeamId(),proModel.getId()));
		model.addAttribute("teamTea", projectDeclareService.findTeamTeacherFromTUH(proModel.getTeamId(),proModel.getId()));
		if(actYw.getKeyType()!=null){
			ProModelMd proModelMd=proModelMdService.getByProModelId(proModel.getId());
			SysAttachment sa=new SysAttachment();
			sa.setUid(proModel.getId());
			/*sa.setFileStep(FileStepEnum.S2000);*/
			sa.setType(FileTypeEnum.S10);
			List<SysAttachment> fileListMap =  sysAttachmentService.getFiles(sa);
			model.addAttribute("sysAttachments", fileListMap);
			model.addAttribute("proModelMd",proModelMd);
			return "template/form/project/"+actYw.getKeyType()+"viewForm";
		}
		ActYwGroup actYwGroup=actYw.getGroup();
		model.addAttribute("groupId",actYw.getGroupId());
		String flowType=actYwGroup.getFlowType();
		String type= FlowType.getByKey(flowType).getType().getKey();
		SysAttachment sa=new SysAttachment();
		sa.setUid(proModel.getId());
//		sa.setFileStep(FileStepEnum.S300);
//		sa.setType(FileTypeEnum.S2);
		List<SysAttachment> fileListMap =  sysAttachmentService.getFiles(sa);
		model.addAttribute("sysAttachments", fileListMap);
		return "template/form/"+type+"/viewBackForm";
	}

	public static void  showFrontMessage(ProProject proProject ,Model model){
		List<Dict> finalStatusMap=new ArrayList<Dict>();
		if(proProject.getFinalStatus()!=null){
			String finalStatus=proProject.getFinalStatus();
			if(finalStatus!=null){
				String[] finalStatuss=finalStatus.split(",");
				if(finalStatuss.length>0){
					for(int i=0;i<finalStatuss.length;i++){
						Dict dict=new Dict();
						dict.setValue(finalStatuss[i]);
						if(proProject.getProType().contains(FlowProjectType.PMT_DASAI.getKey())){
							dict.setLabel(DictUtils.getDictLabel(finalStatuss[i],"competition_college_prise",""));
						}else if(proProject.getProType().contains(FlowProjectType.PMT_XM.getKey())){
							dict.setLabel(DictUtils.getDictLabel(finalStatuss[i],"project_result",""));
						}
						finalStatusMap.add(dict);
					}
				}
				model.addAttribute("finalStatusMap",finalStatusMap);
			}
		}
		//前台项目类型
		List<Dict> proTypeMap=new ArrayList<Dict>();
		if(proProject.getType()!=null){
			String proType=proProject.getType();
			if(proType!=null){
				String[] proTypes=proType.split(",");
				if(proTypes.length>0){
					for(int i=0;i<proTypes.length;i++){
						Dict dict=new Dict();

						dict.setValue(proTypes[i]);
						if(proProject.getProType().contains(FlowProjectType.PMT_DASAI.getKey())){
							dict.setLabel(DictUtils.getDictLabel(proTypes[i],"competition_type",""));
						}else if(proProject.getProType().contains(FlowProjectType.PMT_XM.getKey())){
							dict.setLabel(DictUtils.getDictLabel(proTypes[i],"project_style",""));
						}
						//proCategoryMap.add(map);
						proTypeMap.add(dict);

					}
				}
				model.addAttribute("proTypeMap",proTypeMap);
			}
		}
		//前台项目类别
		/*List<Map<Dict>> proCategoryMap=new ArrayList<Map<String, String>>();*/
		List<Dict> proCategoryMap=new ArrayList<Dict>();
		if(proProject.getProCategory()!=null){
			String proCategory=proProject.getProCategory();
			if(proCategory!=null){
				String[] proCategorys=proCategory.split(",");
				if(proCategorys.length>0){
					for(int i=0;i<proCategorys.length;i++){
						Map<String, String> map=new HashMap<String, String>();
						Dict dict=new Dict();
						map.put("value",proCategorys[i]);
						dict.setValue(proCategorys[i]);
						if(proProject.getProType().contains(FlowProjectType.PMT_DASAI.getKey())){
							map.put("label",DictUtils.getDictLabel(proCategorys[i],"competition_net_type",""));
							dict.setLabel(DictUtils.getDictLabel(proCategorys[i],"competition_net_type",""));
						}else if(proProject.getProType().contains(FlowProjectType.PMT_XM.getKey())){
							map.put("label",DictUtils.getDictLabel(proCategorys[i],"project_type",""));
							dict.setLabel(DictUtils.getDictLabel(proCategorys[i],"project_type",""));
						}
						//proCategoryMap.add(map);
						proCategoryMap.add(dict);
					}
				}
				model.addAttribute("proCategoryMap",proCategoryMap);
			}
		}
		//前台项目类别
		List<Dict> prolevelMap=new ArrayList<Dict>();
		if(proProject.getLevel()!=null){
			String proLevel=proProject.getLevel();
			if(proLevel!=null){
				String[] proLevels=proLevel.split(",");
				if(proLevels.length>0){
					for(int i=0;i<proLevels.length;i++){
						Dict dict=new Dict();
						dict.setValue(proLevels[i]);
						dict.setLabel(DictUtils.getDictLabel(proLevels[i],"gcontest_level",""));
						prolevelMap.add(dict);
					}
				}
				model.addAttribute("prolevelMap",prolevelMap);
			}
		}
	}
}