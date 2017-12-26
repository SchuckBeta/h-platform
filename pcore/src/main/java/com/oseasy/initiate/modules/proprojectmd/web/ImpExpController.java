package com.hch.platform.pcore.modules.proprojectmd.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.act.service.ActTaskService;
import com.hch.platform.pcore.modules.proprojectmd.service.ImpExpService;
import com.hch.platform.pcore.modules.proprojectmd.service.ProModelMdService;

import net.sf.json.JSONObject;

@Controller
public class ImpExpController extends BaseController {
	@Autowired
	private ImpExpService impExpService;
	@Autowired
	private ProModelMdService proModelMdService;
	@Autowired
	private ActTaskService actTaskService;
	@RequestMapping(value = "${adminPath}/proprojectmd/expAll")
	public void expAll(HttpServletRequest request, HttpServletResponse response) {
		impExpService.expAll(request,response);
	}
	@RequestMapping(value = "${adminPath}/proprojectmd/expClose")
	public void expClose(HttpServletRequest request, HttpServletResponse response) {
		impExpService.expClose(request,response);
	}
	@RequestMapping(value = "${adminPath}/proprojectmd/expMid")
	public void expMid(HttpServletRequest request, HttpServletResponse response) {
		impExpService.expMid(request,response);
	}
	@RequestMapping(value = "${adminPath}/proprojectmd/expApproval")
	public void expApproval(HttpServletRequest request, HttpServletResponse response) {
		impExpService.expApproval(request,response);
	}
	@RequestMapping(value = "${adminPath}/proprojectmd/checkExpAll")
	@ResponseBody
	public JSONObject checkExpAll(HttpServletRequest request, HttpServletResponse response) {
		JSONObject js=new JSONObject();
//		String actywId=request.getParameter("actywId");
		List<String> pids=proModelMdService.getAllPromodelMd();
		if(pids!=null&&pids.size()>0){
			js.put("ret", "1");
		}else{
			js.put("ret", "0");
		}
		return js;
	}
	@RequestMapping(value = "${adminPath}/proprojectmd/checkExpApproval")
	@ResponseBody
	public JSONObject checkExpApproval(HttpServletRequest request, HttpServletResponse response) {
		JSONObject js=new JSONObject();
		String gnodeId=request.getParameter("gnodeId");
		String actywId=request.getParameter("actywId");
		List<String> pids=actTaskService.getAllTodoId(actywId,gnodeId);
		if(pids!=null&&pids.size()>0){
			js.put("ret", "1");
		}else{
			js.put("ret", "0");
		}
		return js;
	}
	@RequestMapping(value = "${adminPath}/proprojectmd/checkExpMid")
	@ResponseBody
	public JSONObject checkExpMid(HttpServletRequest request, HttpServletResponse response) {
		JSONObject js=new JSONObject();
		String gnodeId=request.getParameter("gnodeId");
		String actywId=request.getParameter("actywId");
		List<String> pids=actTaskService.getAllTodoId(actywId,gnodeId);
		if(pids!=null&&pids.size()>0){
			js.put("ret", "1");
		}else{
			js.put("ret", "0");
		}
		return js;
	}
	@RequestMapping(value = "${adminPath}/proprojectmd/checkExpClose")
	@ResponseBody
	public JSONObject checkExpClose(HttpServletRequest request, HttpServletResponse response) {
		JSONObject js=new JSONObject();
		String gnodeId=request.getParameter("gnodeId");
		String actywId=request.getParameter("actywId");
		List<String> pids=actTaskService.getAllTodoId(actywId,gnodeId);
		if(pids!=null&&pids.size()>0){
			js.put("ret", "1");
		}else{
			js.put("ret", "0");
		}
		return js;
	}
}