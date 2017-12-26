package com.oseasy.initiate.modules.promodel.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.util.StringUtils;
import com.oseasy.initiate.modules.sysconfig.utils.SysConfigUtil;
import com.oseasy.initiate.modules.sysconfig.vo.ConfRange;
import com.oseasy.initiate.modules.sysconfig.vo.PersonNumConf;
import com.oseasy.initiate.modules.sysconfig.vo.SysConfigVo;
import com.oseasy.initiate.modules.team.entity.Team;
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
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.actyw.entity.ActYw;
import com.oseasy.initiate.modules.actyw.entity.ActYwGroup;
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
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.team.entity.TeamUserRelation;
import com.oseasy.initiate.modules.team.service.TeamService;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;
import com.oseasy.initiate.modules.tpl.vo.IWparam;
import com.oseasy.initiate.modules.tpl.vo.Wtype;

import net.sf.json.JSONObject;

/**
 * proModelController.
 * @author zy
 * @version 2017-07-13
 */
@Controller
@RequestMapping(value = "${frontPath}/promodel/proModel")
public class FrontProModelController extends BaseController {

	@Autowired
	private ProModelService proModelService;
	@Autowired
	private ProModelMdService proModelMdService;
	@Autowired
	private ProjectDeclareService projectDeclareService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private ActYwService actYwService;
	@Autowired
	private TeamService teamService;
	@Autowired
	SysAttachmentService sysAttachmentService;
	@Autowired
	TeamUserRelationService teamUserRelationService;

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


	@RequestMapping(value = {"list", ""})
	public String list(ProModel proModel, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<ProModel> page = proModelService.findPage(new Page<ProModel>(request, response), proModel);
		model.addAttribute("page", page);
		return "modules/promodel/proModelList";
	}


	@RequestMapping(value = "form")
	public String form(ProModel proModel, Model model) {
		User user=systemService.getUser(proModel.getDeclareId());
		ActYw actYw=actYwService.get(proModel.getActYwId());
		ProProject proProject=actYw.getProProject();
		if(proProject!=null){
			showFrontMessage(proProject,model);
		}
		if(proModel.getSubTime()!=null){
			model.addAttribute("sysdate", DateUtil.formatDate(proModel.getSubTime(),"yyyy-MM-dd"));
		}else {
			model.addAttribute("sysdate", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
		}
		model.addAttribute("proProject",proProject);
		model.addAttribute("actYw",actYw);
		model.addAttribute("sse", user);
		//关联团队
		model.addAttribute("teams", projectDeclareService.findTeams(user.getId(),""));
		model.addAttribute("projectName", actYw.getProProject().getProjectName());
		model.addAttribute("team", teamService.get(proModel.getTeamId()));
		model.addAttribute("teamStu", projectDeclareService.findTeamStudentFromTUH(proModel.getTeamId(),proModel.getId()));
		model.addAttribute("teamTea", projectDeclareService.findTeamTeacherFromTUH(proModel.getTeamId(),proModel.getId()));
		if(actYw.getKeyType()!=null){
			ProModelMd proModelMd=proModelMdService.getByProModelId(proModel.getId());
			if(proModelMd!=null){
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
				model.addAttribute("proModelMd",proModelMd);

				model.addAttribute("proModel",proModelMd.getProModel());
				 model.addAttribute("proId", proModelMd.getId());
				model.addAttribute("showStepNumber",2);
			}else{
				model.addAttribute("showStepNumber",1);
				model.addAttribute("isSubmit",0);
			}
			model.addAttribute("wprefix", IWparam.getFileTplPreFix());
			model.addAttribute("wtypes", Wtype.toJson());
			return "template/form/project/"+actYw.getKeyType()+"applyForm";
		}else {
			SysAttachment sa = new SysAttachment();
			sa.setUid(proModel.getId());

			List<SysAttachment> fileListMap = sysAttachmentService.getFiles(sa);
			model.addAttribute("sysAttachments", fileListMap);
			return "template/form/project/applyForm";
		}
	}



	@RequestMapping(value = "viewForm")
	public String viewForm(ProModel proModel, Model model) {
		model.addAttribute("proModel", proModel);
		User user=systemService.getUser(proModel.getDeclareId());
		ActYw actYw=actYwService.get(proModel.getActYwId());
		ProProject proProject=actYw.getProProject();
		if(proProject!=null){
			showFrontMessage(proProject,model);
		}
		if(proModel.getDeclareId()!=null){
			model.addAttribute("sse", systemService.getUser(proModel.getDeclareId()));
		}else{
			model.addAttribute("sse", user);
		}

		model.addAttribute("projectName", actYw.getProProject().getProjectName());
		model.addAttribute("team", teamService.get(proModel.getTeamId()));
		model.addAttribute("teamStu", projectDeclareService.findTeamStudentFromTUH(proModel.getTeamId(),proModel.getId()));
		model.addAttribute("teamTea", projectDeclareService.findTeamTeacherFromTUH(proModel.getTeamId(),proModel.getId()));
		if(actYw.getKeyType()!=null){
			ProModelMd proModelMd=proModelMdService.getByProModelId(proModel.getId());
			SysAttachment sa=new SysAttachment();
			sa.setUid(proModel.getId());
			/*sa.setFileStep(FileStepEnum.S2000);
			sa.setType(FileTypeEnum.S10);*/
			List<SysAttachment> fileListMap =  sysAttachmentService.getFiles(sa);
			model.addAttribute("sysAttachments", fileListMap);
			model.addAttribute("proModelMd",proModelMd);
			return "template/form/project/"+actYw.getKeyType()+"applyView";
		}
		ActYwGroup actYwGroup=actYw.getGroup();
		model.addAttribute("groupId",actYw.getGroupId());
		String flowType=actYwGroup.getFlowType();
		String type= FlowType.getByKey(flowType).getType().getKey();
		SysAttachment sa=new SysAttachment();
		sa.setUid(proModel.getId());

		List<SysAttachment> fileListMap =  sysAttachmentService.getFiles(sa);
		model.addAttribute("sysAttachments", fileListMap);
		return "template/form/"+type+"/viewForm";
	}



	@RequestMapping(value = "save")
	public String save(ProModel proModel, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, proModel)){
			return form(proModel, model);
		}
		proModelService.save(proModel);
		addMessage(redirectAttributes, "保存proModel成功");
		return "redirect:"+Global.getAdminPath()+"/promodel/proModel/?repage";
	}


	@ResponseBody
	@RequestMapping(value = "findTeamPerson")
	public JSONObject findTeamPerson(@RequestParam(required=true) String id, @RequestParam(required=false) String type,	@RequestParam(required=true) String actywId) {
		//List<Map<String,String>>
		JSONObject js=new JSONObject();

		js.put("teamId",id);
		js.put("ret",0);

		ActYw actYw=actYwService.get(actywId);
		String subType=actYw.getProProject().getType();//项目分类
		SysConfigVo scv= SysConfigUtil.getSysConfigVo();
		PersonNumConf pnc=new PersonNumConf();
		if(StringUtil.isNotEmpty(type)){
			pnc=SysConfigUtil.getProPersonNumConf(scv, subType, type);
		}else{
			pnc=SysConfigUtil.getGconPersonNumConf(scv, subType);
		}
		if(pnc!=null){
			Team teamNums=teamService.findTeamJoinInNums(id);
			if("1".equals(pnc.getTeamNumOnOff())){//团队人数范围
				ConfRange cr=pnc.getTeamNum();
				int min=Integer.valueOf(cr.getMin());
				int max=Integer.valueOf(cr.getMax());
				if(teamNums.getMemberNum()<min||teamNums.getMemberNum()>max){
					if(min==max){
						js.put("msg", DictUtils.getDictLabel(subType, "project_type", "")+"项目团队成员人数为"+min+"人");
					}else{
						js.put("msg", DictUtils.getDictLabel(subType, "project_type", "")+"项目团队成员人数为"+min+"~"+max+"人");
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
						js.put("msg", DictUtils.getDictLabel(subType, "project_type", "")+"项目团队校园导师为"+min+"人");
					}else{
						js.put("msg", DictUtils.getDictLabel(subType, "project_type", "")+"项目团队校园导师为"+min+"~"+max+"人");
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
						js.put("msg", DictUtils.getDictLabel(subType, "project_type", "")+"项目企业导师为"+min+"人");
					}else{
						js.put("msg", DictUtils.getDictLabel(subType, "project_type", "")+"项目企业导师为"+min+"~"+max+"人");
					}
					return js;
				}
			}
		}

		/*if(type != null){
			if((type.equals("1")||type.equals("2"))  && (stuNum>5||stuNum<1)){
				js.put("ret",0);
				js.put("msg","该团队人数不符合，创新创业训练人数为1-5人。");
				return  js;
			}
			if((type.equals("3"))  &&(stuNum>7||stuNum<1)){
				js.put("ret",0);
				js.put("msg","该团队人数不符合，创业实践人数为1-7人。");
				return  js;
			}
		}*/

		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		List<Map<String,String>> list1=projectDeclareService.findTeamStudent(id);
		List<Map<String,String>> list2=projectDeclareService.findTeamTeacher(id);
		for(Map<String,String> map:list1) {
			list.add(map);
		}
		for(Map<String,String> map:list2) {
			list.add(map);
		}

		js.put("ret",1);
		js.put("map",list);
		return js;
	}


	@RequestMapping(value = "submit")
	@ResponseBody
	public JSONObject submit(ProModel proModel, Model model, HttpServletRequest request,RedirectAttributes redirectAttributes) {
		JSONObject js=new JSONObject();
		String gnodeId=request.getParameter("gnodeId");
		if(StringUtil.isNotEmpty(gnodeId)){
			proModel.getAttachMentEntity().setGnodeId(gnodeId);
		}
		js=proModelService.submit(proModel,js);
		return js;
	}

	@RequestMapping(value = "submitMid")
	@ResponseBody
	public JSONObject submitMid(ProModel proModel, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		JSONObject js=new JSONObject();
		js.put("ret", 1);
		String gnodeId=request.getParameter("gnodeId");
		if(StringUtil.isNotEmpty(gnodeId)){
			proModel.getAttachMentEntity().setGnodeId(gnodeId);
		}
		String msg=proModelService.submitMid(proModel);

		js.put("msg", msg);
		return js;
	}

	@RequestMapping(value = "delete")
	public String delete(ProModel proModel, RedirectAttributes redirectAttributes) {
		proModelService.delete(proModel);
		addMessage(redirectAttributes, "删除proModel成功");
		return "redirect:"+Global.getAdminPath()+"/promodel/proModel/?repage";
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