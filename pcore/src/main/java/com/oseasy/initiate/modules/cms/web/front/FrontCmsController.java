/**
 *
 */
package com.oseasy.initiate.modules.cms.web.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwForm;
import com.oseasy.initiate.modules.actyw.service.ActYwFormService;
import com.oseasy.initiate.modules.actyw.service.ActYwService;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowProjectType;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.enums.FileStepEnum;
import com.oseasy.initiate.modules.attachment.enums.FileTypeEnum;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.promodel.entity.ProModel;
import com.oseasy.initiate.modules.promodel.service.ProModelService;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.proprojectmd.entity.ProModelMd;
import com.oseasy.initiate.modules.proprojectmd.service.ProModelMdService;
import com.oseasy.initiate.modules.sys.entity.Dict;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.tpl.vo.IWparam;
import com.oseasy.initiate.modules.tpl.vo.Wtype;

/**
 * 内容管理Controller

 * @version 2013-4-21
 */
@Controller
@RequestMapping(value = "${frontPath}/cms")
public class FrontCmsController extends BaseController {

	@Autowired
	private ActYwFormService actYwFormService;
	@Autowired
	private ActYwService actYwService;
	@Autowired
	private ProjectDeclareService projectDeclareService;
	@Autowired
	private ProModelService proModelService;
	@Autowired
	private ProModelMdService proModelMdService;
	@Autowired
	SysAttachmentService sysAttachmentService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private SystemService systemService;

	/*流程模板静态文件*/
	@RequestMapping(value = "form/{template}/{pageName}")
	public String modelForm(@PathVariable String pageName, @PathVariable String template,HttpServletRequest request,Model model) {
		//标识流程以及模型
		String actywId=request.getParameter("actywId");
		//标识流程那个环节
		String gnodeId=request.getParameter("gnodeId");

		//项目Id
		String proModelId=request.getParameter("promodelId");
		ActYw actYw=actYwService.get(actywId);
		if(actYw!=null){
			ProProject proProject=actYw.getProProject();
			model.addAttribute("sysdate", DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
			model.addAttribute("proProject",proProject);
			model.addAttribute("actYw",actYw);
			if(proProject!=null){
				//配置展示信息
				showFrontMessage(proProject,model);
			}
			String flowTypeKey=actYw.getGroup().getFlowType();
			FlowType flowType=FlowType.getByKey(flowTypeKey);
			String projectType=flowType.getType().getKey();
			User user = UserUtils.getUser();
			if(StringUtil.isNotEmpty(gnodeId)){
				model.addAttribute("gnodeId", gnodeId);
			}
			//根据匹配传页面需要参数，数据
			if(StringUtil.isNotEmpty(pageName)){
				model.addAttribute("projectName", actYw.getProProject().getProjectName());
				//appfrom 申报表单
				if(pageName.contains("applyForm")){
					//区分是否为民大申报表单
					if(actYw.getKeyType()!=null){
						ProModelMd proModelMd=new ProModelMd();
						model.addAttribute("proModelMd",proModelMd);
						//判断是查看还是申报 非空为查看
						if(proModelId!=null){
							proModelMd =proModelMdService.getByProModelId(proModelId);
							SysAttachment sa=new SysAttachment();
							sa.setUid(proModelMd.getProModel().getId());
							sa.setType(FileTypeEnum.S10);
							sa.setFileStep(FileStepEnum.S2000);
							List<SysAttachment> fileListMap =  sysAttachmentService.getFiles(sa);
							if(fileListMap!=null){
								model.addAttribute("sysAttachments",fileListMap);
							}
							if(proModelMd.getProModel().getTeamId()!=null){
								//查找学生
								model.addAttribute("teams", projectDeclareService.findTeams(proModelMd.getProModel().getDeclareId(),proModelMd.getProModel().getId()));
								model.addAttribute("teamStu",projectDeclareService.findTeamStudentFromTUH(proModelMd.getProModel().getTeamId(),proModelMd.getProModel().getId()));
								model.addAttribute("teamTea",projectDeclareService.findTeamTeacherFromTUH(proModelMd.getProModel().getTeamId(),proModelMd.getProModel().getId()));
							}
							model.addAttribute("proModelMd",proModelMd);
							model.addAttribute("proModel",proModelMd.getProModel());
							model.addAttribute("sse", systemService.getUser(proModelMd.getProModel().getDeclareId()));
							model.addAttribute("proId", proModelMd.getId());
							model.addAttribute("showStepNumber",2);
							model.addAttribute("isSubmit", 1);
						}else{
							//申报页面
							proModelMd =proModelMdService.getByDeclareId(user.getId(),actywId);
							if(proModelMd!=null){
								//判断申报是否提交
								if(proModelMd.getProModel().getProcInsId()!=null){
									model.addAttribute("isSubmit",1);
								}else{
									model.addAttribute("isSubmit",0);
								}
								SysAttachment sa=new SysAttachment();
								sa.setUid(proModelMd.getProModel().getId());
								sa.setType(FileTypeEnum.S10);
								sa.setFileStep(FileStepEnum.S2000);
								List<SysAttachment> fileListMap =  sysAttachmentService.getFiles(sa);
								if(fileListMap!=null){
									model.addAttribute("sysAttachments",fileListMap);
								}
								if(proModelMd.getProModel().getTeamId()!=null){
									//查找学生
									model.addAttribute("teams", projectDeclareService.findTeams(user.getId(),""));
									model.addAttribute("teamStu",projectDeclareService.findTeamStudentFromTUH(proModelMd.getProModel().getTeamId(),proModelMd.getProModel().getId()));
									model.addAttribute("teamTea",projectDeclareService.findTeamTeacherFromTUH(proModelMd.getProModel().getTeamId(),proModelMd.getProModel().getId()));
								}
								model.addAttribute("proModelMd",proModelMd);
								model.addAttribute("proModel",proModelMd.getProModel());
								model.addAttribute("sse", systemService.getUser(proModelMd.getProModel().getDeclareId()));
								model.addAttribute("proId", proModelMd.getId());
								model.addAttribute("showStepNumber",2);
							}else {
								model.addAttribute("teams", projectDeclareService.findTeams(user.getId(),""));
								model.addAttribute("sse", user);
								model.addAttribute("showStepNumber", 1);
								model.addAttribute("isSubmit", 0);
							}
						}
						model.addAttribute("wprefix", IWparam.getFileTplPreFix());
						model.addAttribute("wtypes", Wtype.toJson());
						return "template/form/"+projectType+"/"+actYw.getKeyType()+pageName;
					}else{
						//自定义项目查看
						if(proModelId!=null){
							ProModel proModel =proModelService.get(proModelId);
							model.addAttribute("sse", systemService.getUser(proModel.getDeclareId()));
							model.addAttribute("projectName", actYw.getProProject().getProjectName());
							model.addAttribute("team", teamService.get(proModel.getTeamId()));
							model.addAttribute("teamStu", projectDeclareService.findTeamStudentFromTUH(proModel.getTeamId(),proModel.getId()));
							model.addAttribute("teamTea", projectDeclareService.findTeamTeacherFromTUH(proModel.getTeamId(),proModel.getId()));
							model.addAttribute("proModel",proModel);
							return "template/form/"+projectType+"/viewForm";
						}else{
							//自定义项目申报
							ProModel proModel =new ProModel();
							proModel.setActYwId(actywId);
							model.addAttribute("teams", projectDeclareService.findTeams(user.getId(),""));
							model.addAttribute("sse", user);
							model.addAttribute("proModel",proModel);
							return "template/form/"+projectType+"/"+pageName;
						}
					}

				}else{
					//中期表单以及结项表单等其他前台表单
					if(StringUtil.isEmpty(gnodeId)){
						return "modules/website/html/formMiss";
					}
					ActYwForm actYwForm = actYwFormService.get(pageName);
					String url = actYwForm.getPath();
					if(StringUtil.isEmpty(url)){
						return "modules/website/html/formMiss";
					}
					//民大项目
					if(StringUtil.isNotEmpty(actYw.getKeyType())){
						//表单查看 在promodel中已经处理
						if(proModelId!=null){

						}else {
						//表单申报
							ProModelMd proModelMd = new ProModelMd();
							proModelMd = proModelMdService.getByDeclareId(user.getId(),actywId);
							if (proModelMd != null) {
								model.addAttribute("proModelMd", proModelMd);
								model.addAttribute("proModel", proModelMd.getProModel());
								model.addAttribute("sse", systemService.getUser(proModelMd.getProModel().getDeclareId()));
								model.addAttribute("proProject", proProject);
								model.addAttribute("wprefix", IWparam.getFileTplPreFix());
								model.addAttribute("wtypes", Wtype.toJson());
								ProModel proModelapp = proModelMd.getProModel();
								if (proModelapp.getTeamId() != null) {
									//查找团队信息
									model.addAttribute("team", teamService.get(proModelapp.getTeamId()));
									model.addAttribute("teamStu", projectDeclareService.findTeamStudentFromTUH(proModelapp.getTeamId(), proModelapp.getId()));
									model.addAttribute("teamTea", projectDeclareService.findTeamTeacherFromTUH(proModelapp.getTeamId(), proModelapp.getId()));
								}
								SysAttachment sa = new SysAttachment();
								sa.setUid(proModelMd.getProModel().getId());
								/*sa.setType(FileTypeEnum.S10);
								sa.setFileStep(FileStepEnum.S2000);*/
								sa.setGnodeId(gnodeId);
								List<SysAttachment> fileListMap = sysAttachmentService.getFiles(sa);
								if (fileListMap != null) {
									model.addAttribute("sysAttachments", fileListMap);
								}
							}
						}
					}else{
						//自定义项目
						ProModel proModel =proModelService.get(proModelId);
						model.addAttribute("sse", systemService.getUser(proModel.getDeclareId()));
						model.addAttribute("projectName", actYw.getProProject().getProjectName());
						model.addAttribute("team", teamService.get(proModel.getTeamId()));
						model.addAttribute("teamStu", projectDeclareService.findTeamStudentFromTUH(proModel.getTeamId(),proModel.getId()));
						model.addAttribute("teamTea", projectDeclareService.findTeamTeacherFromTUH(proModel.getTeamId(),proModel.getId()));
						model.addAttribute("proModel",proModel);
					}
					return url;
				}
			}else{
				return "modules/website/html/formMiss";
			}
		}else{
			return "modules/website/html/formMiss";
		}
	}


	private  void  showFrontMessage(ProProject proProject ,Model model){
		//审核级别
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
		//前台项目级别
		List<Dict> prolevelMap=new ArrayList<Dict>();
		if(proProject.getLevel()!=null){
			String proLevel=proProject.getLevel();
			if(proLevel!=null){
				String[] proLevels=proLevel.split(",");
				if(proLevels.length>0){
					for(int i=0;i<proLevels.length;i++){
						Dict dict=new Dict();
						dict.setValue(proLevels[i]);
						dict.setLabel(DictUtils.getDictLabel(proLevels[i],"competition_format",""));
						prolevelMap.add(dict);
					}
				}
				model.addAttribute("prolevelMap",prolevelMap);
			}
		}
	}

}
