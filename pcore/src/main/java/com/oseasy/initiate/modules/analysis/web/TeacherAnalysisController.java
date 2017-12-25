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
import com.oseasy.initiate.modules.sys.entity.BackTeacherExpansion;
import com.oseasy.initiate.modules.sys.entity.StudentExpansion;
import com.oseasy.initiate.modules.sys.entity.TeacherExpansion;
import com.oseasy.initiate.modules.sys.service.BackTeacherExpansionService;

@Controller
@RequestMapping(value = "${adminPath}/analysis/teacherAnalysis")
public class TeacherAnalysisController extends BaseController {
	@Autowired
	private BackTeacherExpansionService backTeacherExpansionService;

	@RequestMapping(value = "toPage")
	public String toPage(BackTeacherExpansion backTeacherExpansion,HttpServletRequest request,HttpServletResponse response,Model model) {

		Page<BackTeacherExpansion> newPage= new Page<BackTeacherExpansion>(request, response);
		String size = request.getParameter("pageSize");
		if (StringUtil.isEmpty(size)) {
			newPage.setPageSize(2);
		}
		/*Page<BackTeacherExpansion> page = backTeacherExpansionService.findTeacherExpansionPage(newPage, backTeacherExpansion); */
		Page<BackTeacherExpansion> page = backTeacherExpansionService.findPage(newPage, backTeacherExpansion);

		model.addAttribute("page", page);
		return "modules/analysis/teacherAnalysis";
	}
}
