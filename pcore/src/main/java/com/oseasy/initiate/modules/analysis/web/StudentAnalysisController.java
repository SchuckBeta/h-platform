package com.oseasy.initiate.modules.analysis.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.service.StudentExpansionService;

@Controller
@RequestMapping(value = "${adminPath}/analysis/studentAnalysis")
public class StudentAnalysisController extends BaseController {
//	@Autowired
//	private ProjectAnalysisService projectAnalysisService;
	@Autowired
	private StudentExpansionService studentExpansionService;


	@RequestMapping(value = "toPage")
	public String toPage( StudentExpansion studentExpansion,HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<StudentExpansion> newPage= new Page<StudentExpansion>(request, response);
		Map<String,Object> param =new HashMap<String,Object>();
		String size = request.getParameter("pageSize");
		if (StringUtil.isEmpty(size)) {
			newPage.setPageSize(2);
		}
		if (studentExpansion.getUser()!=null&&studentExpansion.getUser().getOffice()!=null) {
			param.put("collegeId", studentExpansion.getUser().getOffice().getId());
		}
		if (studentExpansion.getUser()!=null&&studentExpansion.getUser().getProfessional()!=null) {
			param.put("professional", studentExpansion.getUser().getProfessional());
		}
		if (studentExpansion.getUser()!=null&&studentExpansion.getUser().getName()!=null) {
			param.put("name", studentExpansion.getUser().getName());
		}


		Page<StudentExpansion> pageNew = studentExpansionService.findStudentPage(newPage, param);
		model.addAttribute("page", pageNew);
		model.addAttribute("param", param);
		return "modules/analysis/studentAnalysis";
	}
//	@RequestMapping(value = "getData")
//	@ResponseBody
//	public JSONObject getData(HttpServletRequest request, HttpServletResponse response) {
//		return projectAnalysisService.getData();
//	}
}
