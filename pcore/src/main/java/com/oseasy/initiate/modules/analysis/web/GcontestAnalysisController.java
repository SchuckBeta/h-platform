
package com.oseasy.initiate.modules.analysis.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.analysis.service.GcontestAnalysisService;

import net.sf.json.JSONObject;


@Controller
@RequestMapping(value = "${adminPath}/analysis/gcontestAnalysis")
public class GcontestAnalysisController extends BaseController {
	@Autowired
	private GcontestAnalysisService gcontestAnalysisService;
	@RequestMapping(value = "toPage")
	public String toPage(HttpServletRequest request, HttpServletResponse response) {
		return "modules/analysis/gcontestAnalysis";
	}
	@RequestMapping(value = "getData")
	@ResponseBody
	public JSONObject getData(HttpServletRequest request, HttpServletResponse response) {
		return gcontestAnalysisService.getData();
	}
}
