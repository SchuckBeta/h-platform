package com.oseasy.initiate.modules.interactive.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.interactive.service.SysCommentService;
import com.oseasy.initiate.modules.interactive.util.InteractiveUtil;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.utils.UserUtils;

import net.sf.json.JSONObject;

/**
 * 评论表Controller.
 * @author 9527
 * @version 2017-06-30
 */
@Controller
@RequestMapping(value = "${frontPath}/interactive/sysComment")
public class SysCommentController extends BaseController {

	@Autowired
	private SysCommentService sysCommentService;



	@RequestMapping(value = "save")
	@ResponseBody
	public JSONObject save(@RequestBody JSONObject param,HttpServletRequest request) {
		return sysCommentService.save(param,request);
	}
	@RequestMapping(value = "getCommentData")
	@ResponseBody
	public Map<String,Object> getCommentData(HttpServletRequest request) {
		return sysCommentService.getCommentData(request);
	}
	@RequestMapping(value = "getNextPage")
	@ResponseBody
	public List<Map<String,String>> getNextPage(HttpServletRequest request) {
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("foreignId", request.getParameter("foreignId"));
		param.put("pageNo", request.getParameter("pageNo"));
		String queryType=request.getParameter("queryType");
		User user=UserUtils.getUser();
		String token=request.getParameter("token");
		String userid=user.getId();
		if(StringUtil.isEmpty(userid)){
			userid="-9999";
		}
		String ip=InteractiveUtil.getIpAddr(request);
		param.put("userId", userid);
		param.put("token",token );
		param.put("ip", ip);
		if("2".equals(queryType)){
			if(StringUtil.isEmpty(UserUtils.getUser().getId())){
				return null;
			}
			param.put("userid", UserUtils.getUser().getId());
		}
		return sysCommentService.getPageList(param);
	}
}