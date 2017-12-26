
package com.hch.platform.pcore.modules.analysis.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.analysis.service.GcontestAnalysisService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Controller
@RequestMapping(value = "${adminPath}/analysis/gcontestAnalysis")
public class GcontestAnalysisController extends BaseController {
	@Autowired
	private GcontestAnalysisService gcontestAnalysisService;
	@RequestMapping(value = "toPage")
	public String toPage(HttpServletRequest request, HttpServletResponse response,Model model) {
		Calendar calendar=Calendar.getInstance();
		List<String> years=new ArrayList<String>();
		for(int i=calendar.get(Calendar.YEAR);i>=2010;i--){
			years.add(i+"");
		}
		model.addAttribute("years", years);
		return "modules/analysis/gcontestAnalysis";
	}
	@RequestMapping(value = "getData")
	@ResponseBody
	public JSONObject getData(HttpServletRequest request, HttpServletResponse response) {
		return gcontestAnalysisService.getData();
	}
	@RequestMapping(value = "getGcontestNum")
	@ResponseBody
	public JSONArray getGcontestNum(HttpServletRequest request, HttpServletResponse response) {
		return gcontestAnalysisService.getGcontestNum();
	}
	@RequestMapping(value = "getGcontestOfficeNum")
	@ResponseBody
	public JSONArray getGcontestOfficeNum(HttpServletRequest request, HttpServletResponse response) {
		String type=request.getParameter("type");
		String year=request.getParameter("year");
		return gcontestAnalysisService.getGcontestOfficeNum(type,year);
	}
	@RequestMapping(value = "getGcontestMemNum")
	@ResponseBody
	public JSONArray getGcontestMemNum(HttpServletRequest request, HttpServletResponse response) {
		String year=request.getParameter("year");
		return gcontestAnalysisService.getGcontestMemNum(year);
	}
}
