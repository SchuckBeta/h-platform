package com.oseasy.initiate.modules.analysis.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.oseasy.initiate.common.web.BaseController;

@Controller
@RequestMapping(value = "${adminPath}/analysis/grandPrizeAnalysis")
public class GrandPrizeAnalysisController extends BaseController {
//	@Autowired
//	private ProjectAnalysisService projectAnalysisService;
	@RequestMapping(value = "toPage")
	public String toPage(HttpServletRequest request, HttpServletResponse response) {
		return "modules/analysis/grandPrizeAnalysis";
	}
//	@RequestMapping(value = "getData")
//	@ResponseBody
//	public JSONObject getData(HttpServletRequest request, HttpServletResponse response) {
//		return projectAnalysisService.getData();
//	}
}
