/**
 *
 */
package com.oseasy.initiate.modules.cms.web;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oseasy.initiate.common.config.Global;
import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.modules.actyw.entity.*;
import com.oseasy.initiate.modules.actyw.service.ActYwGtimeService;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;
import com.oseasy.initiate.modules.promodel.entity.ProModel;
import com.oseasy.initiate.modules.promodel.service.ProModelService;
import com.oseasy.initiate.modules.proprojectmd.entity.ProModelMd;
import com.oseasy.initiate.modules.proprojectmd.service.ProModelMdService;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.act.entity.Act;
import com.oseasy.initiate.modules.act.service.ActTaskService;
import com.oseasy.initiate.modules.actyw.service.ActYwFormService;
import com.oseasy.initiate.modules.actyw.service.ActYwGnodeService;
import com.oseasy.initiate.modules.actyw.service.ActYwService;
import com.oseasy.initiate.modules.actyw.tool.process.ActYwTool;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowProjectType;
import com.oseasy.initiate.modules.gcontest.entity.GContest;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.cms.service.CategoryService;
import com.oseasy.initiate.modules.proproject.entity.ProProject;
import com.oseasy.initiate.modules.sys.entity.Dict;
import com.oseasy.initiate.modules.sys.utils.DictUtils;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

/**
 * 内容管理Controller

 * @version 2013-4-21
 */
@Controller
@RequestMapping(value = "${adminPath}/cms")
public class CmsController extends BaseController {
	public static final String OPEN_TIME_LIMIT = Global.getConfig("openTimeLimit");
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ActTaskService actTaskService;
	@Autowired
	private ActYwFormService actYwFormService;
	@Autowired
	private ActYwService actYwService;
	@Autowired
	private ActYwGtimeService actYwGtimeService;
	@Autowired
	private ActYwGnodeService actYwGnodeService;
	@Autowired
	private ProModelMdService proModelMdService;
	@Autowired
	private ProModelService proModelService;

	@RequiresPermissions("cms:view")
	@RequestMapping(value = "")
	public String index() {
		return "modules/cms/cmsIndex";
	}

	@RequiresPermissions("cms:view")
	@RequestMapping(value = "tree")
	public String tree(Model model) {
		model.addAttribute("categoryList", categoryService.find(true, null));
		return "modules/cms/cmsTree";
	}

	@RequiresPermissions("cms:view")
	@RequestMapping(value = "none")
	public String none() {
		return "modules/cms/cmsNone";
	}


	@RequestMapping(value = "form/{template}/{pageName}")
/*	@Log(operationType="访问类别",operationName="test")*/
	public String modelForm(@PathVariable String pageName,String template,
							HttpServletRequest request, HttpServletResponse response, Model model) {
		String url ="";
		//根据匹配传页面需要参数，数据
		String actywId=request.getParameter("actywId");
		ActYw actYw=actYwService.get(actywId);
		String gnodeId=request.getParameter("gnodeId");
		ActYwGnode actYwGnode=actYwGnodeService.get(gnodeId);
		if(actYwGnode!=null){
			model.addAttribute("menuName",actYwGnode.getName());
		}


		String actionUrl=pageName+"?actywId="+actywId+"&gnodeId="+gnodeId;
		model.addAttribute("actionUrl", actionUrl);
		model.addAttribute("gnodeId",gnodeId);
		model.addAttribute("actywId",actywId);
		if(actYw!=null){
			ProProject proProject=actYw.getProProject();
			model.addAttribute("proProject",proProject);
			if(proProject!=null) {
				model.addAttribute("proProject", proProject);
				//后台审核结果
				showBankMessage(proProject,model);
			}


			//根据节点审核角色 匹配角色id
			String auditGonde = getGnodeName(gnodeId,actYw,model);

			String key = ActYw.getPkey(actYw.getGroup(),actYw.getProProject());
			//model.addAttribute(new GContest());
			Act act= new Act();
			act.setProcDefKey(key);  //流程标识
			//act.setProcDefKey("9a28f2f503d247b3a2640c4c171d2877");
			act.setTaskDefKey(ActYwTool.FLOW_ID_PREFIX+auditGonde);

			 //Page<Act> page=actTaskService.historicList(pageForSearch,act);
			ActYwGtime actYwGtime =new ActYwGtime();
			actYwGtime.setGnodeId(gnodeId);
			actYwGtime.setGrounpId(actYw.getGroupId());
			actYwGtime.setProjectId(actYw.getRelId());
			if("Y".equals(OPEN_TIME_LIMIT)) {
				actYwGtime=actYwGtimeService.getTimeByGnodeId(actYwGtime);
				Date startDate=actYwGtime.getBeginDate();
				Date endDate=actYwGtime.getEndDate();
				boolean isInTime= DateUtil.betweenDay(startDate,endDate);
				if (!isInTime) {
					return "modules/website/html/timeMiss";
				}
			}
			if(actYw.getKeyType()!=null && "md_".equals(actYw.getKeyType())){
				Page<ProModelMd> pageForSearch =new Page<ProModelMd>(request, response);
				Page<ProModelMd> page = new Page<ProModelMd>() ;
				page = proModelMdService.modelMdTodoList(pageForSearch,gnodeId,act,ActYwTool.FLOW_ID_PREFIX+auditGonde);
				model.addAttribute("page", page);
			}else {
				Page<Act> pageForSearch =new Page<Act>(request, response);
				Page<Act> page = actTaskService.modeltodoList(pageForSearch, act, ActYwTool.FLOW_ID_PREFIX + auditGonde);
				model.addAttribute("page", page);
			}

			ActYwForm actYwForm=actYwFormService.get(pageName);

			url =actYwForm.getPath();

			if(url!=null){
				return url;
			}else{
				return "modules/website/html/formMiss";
			}
		}else{
			return "modules/website/html/formMiss";
		}
	}

	@RequestMapping(value = "form/{template}/")
	/*	@Log(operationType="访问类别",operationName="test")*/
	public String modelFormMiss(@PathVariable String template,
							HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/website/html/formMiss";
	}

	@RequestMapping(value = "form/queryMenuList")
	/*	@Log(operationType="访问类别",operationName="test")*/
	public String queryMenuList(Page<ProModel> page, HttpServletRequest request, HttpServletResponse response, Model model) {
		String actywId=request.getParameter("actywId");
		ActYw actYw=actYwService.get(actywId);
		//String gourpID= actYw.getGroupId();
		ActYwGroup actYwGroup=actYw.getGroup();
		model.addAttribute("groupId",actYw.getGroupId());
		String flowType=actYwGroup.getFlowType();
		String type=FlowType.getByKey(flowType).getType().getKey();
		ProModel proModel = new ProModel() ;
		proModel.setActYwId(actywId);
		//page = proModelService.getPromodelList(proModel);
		if(actYw.getKeyType()!=null){
			Page mdPage=new Page<ProModelMd>();
			mdPage.setPageSize(page.getPageSize());
			mdPage.setPageNo(page.getPageNo());
			mdPage = proModelMdService.findMdPage(mdPage, proModel);
			model.addAttribute("page", mdPage);
		}else {
			page = proModelService.findInPage(page, proModel);
			model.addAttribute("page", page);
		}
		model.addAttribute("menuName", "项目查询");

		model.addAttribute("actywId", actywId);
		if(actYw.getKeyType()!=null){
			return "template/form/"+type+"/"+actYw.getKeyType()+"queryListForm";
		}
		return "template/form/"+type+"/queryListForm";
	}

	public String getGnodeName(String gnodeId,ActYw actYw,Model model){
		String auditGonde = null;
		ActYwGnode actYwGnode=new ActYwGnode();
		actYwGnode.setParent(new ActYwGnode(gnodeId));
		actYwGnode.setGroupId(actYw.getGroupId());
		ActYwNode actYwNode=new ActYwNode();
		actYwNode.setLevel("2");
		actYwGnode.setNode(actYwNode);
		//查询当前节点下面任务节点id
		List<ActYwGnode> actYwGnodes=actYwGnodeService.findListByYwGroupAndParent(actYwGnode);
		//查询当前角色 角色id
		List<String> roleIds = UserUtils.getUser().getRoleIdList();

		for(int i=0;i<actYwGnodes.size();i++){
			for(int j=0;j<roleIds.size();j++) {
				if (actYwGnodes.get(i).getFlowGroup().equals(roleIds.get(j))) {
					//model.addAttribute("actYwGnode",actYwGnodes.get(i));
					ActYwForm actYwForm=actYwFormService.get(actYwGnodes.get(i).getFormId());
					model.addAttribute("path",actYwForm.getPath());

					auditGonde=actYwGnodes.get(i).getId();
					model.addAttribute("auditGonde", actYwGnodes.get(i));
					break;
				}

			}
			if(StringUtil.isNotEmpty(auditGonde)){
				break;
			}
		}
		return auditGonde;
	}


	private  void  showBankMessage(ProProject proProject ,Model model){
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
						Map map=new HashMap<String, String>();
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
						dict.setLabel(DictUtils.getDictLabel(proLevels[i],"competition_format",""));
						prolevelMap.add(dict);
					}
				}
				model.addAttribute("prolevelMap",prolevelMap);
			}
		}
	}


}
