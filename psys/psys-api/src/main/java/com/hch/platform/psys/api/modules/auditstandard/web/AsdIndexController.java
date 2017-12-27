package com.hch.platform.pcore.modules.auditstandard.web;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.actyw.entity.ActYw;
import com.hch.platform.pcore.modules.actyw.entity.ActYwGnode;
import com.hch.platform.pcore.modules.actyw.service.ActYwService;
import com.hch.platform.pcore.modules.auditstandard.service.AuditStandardFlowService;
import com.hch.platform.pcore.modules.auditstandard.vo.AsdVo;
import com.hch.platform.pcore.modules.auditstandard.vo.AsdYwGnode;
import com.hch.platform.pcore.modules.gcontest.service.GContestService;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.sys.utils.DictUtils;

@Controller
@RequestMapping(value = "${adminPath}/auditstandard/index")
public class AsdIndexController extends BaseController {
  @Autowired
  private AuditStandardFlowService auditStandardFlowService;
  @Autowired
  private ActYwService actYwService;
  @Autowired
  private ProjectDeclareService projectDeclareService;
  @Autowired
  private GContestService gContestService;

  @RequestMapping(value = {"home", ""})
  public String home( HttpServletRequest request, HttpServletResponse response, Model model) {
	  	String ywid=request.getParameter("ywid");
	  	ActYw ay=actYwService.get(ywid);
	  	AsdVo asdVo = new AsdVo();
	  	if(ay!=null){
	  		String year=DateUtil.formatDate(DateUtil.addYear(new Date(), 1), "yyyy");
	  		asdVo.setYear(year);
	  		asdVo.setSubType(ay.getProProject().getType());
	  		asdVo.setDataYear(DateUtil.formatDate(new Date(), "yyyy"));
	  		asdVo.setActYw(ay);
	  		if("1,".equals(ay.getProProject().getProType())){//双创项目
	  			asdVo.setType("1");
	  			asdVo.setName(DictUtils.getDictLabel(ay.getProProject().getType(), "project_style", ""));
	  			if("1".equals(ay.getProProject().getType())){//大创项目
	  				projectDeclareService.getPersonNumForAsdIndex(asdVo);
	  			}else{
	  				projectDeclareService.getPersonNumForAsdIndexFromModel(asdVo);
	  			}
	  		}
	  		if("7,".equals(ay.getProProject().getProType())){//双创大赛
	  			asdVo.setType("2");
	  			asdVo.setName(DictUtils.getDictLabel(ay.getProProject().getType(), "competition_type", ""));
	  			if("1".equals(ay.getProProject().getType())){//互联网+大赛
	  				gContestService.getPersonNumForAsdIndex(asdVo);
	  			}else{
	  				gContestService.getPersonNumForAsdIndexFromModel(asdVo);
	  			}
	  		}
	      List<AsdYwGnode> sortlist = Lists.newArrayList();
	      AsdYwGnode.sortList2(sortlist, auditStandardFlowService.getGnodeListByYwid(ywid), ActYwGnode.getRootId(), true);
		    asdVo.setAsdYwGnodes(sortlist);
	  	}
	    model.addAttribute("asdVo", asdVo);
	    return "modules/auditstandard/asdIndex";
  }
}
