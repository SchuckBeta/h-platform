package com.hch.platform.pcore.modules.interactive.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hch.platform.putil.common.utils.IdGen;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.interactive.service.SysLikesService;

import net.sf.json.JSONObject;

/**
 * 点赞表Controller.
 * @author 9527
 * @version 2017-06-30
 */
@Controller
@RequestMapping(value = "${frontPath}/interactive/sysLikes")
public class SysLikesController extends BaseController {

	@Autowired
	private SysLikesService sysLikesService;

	@RequestMapping(value = "save")
	@ResponseBody
	public JSONObject save(@RequestBody JSONObject param,HttpServletRequest request) {
		return sysLikesService.save(param,request);
	}
	@RequestMapping(value = "saveForUserInfo")
	@ResponseBody
	public JSONObject saveForUserInfo(@RequestBody JSONObject param,HttpServletRequest request) {
		return sysLikesService.saveForUserInfo(param,request);
	}
	@RequestMapping(value = "uuid")
	@ResponseBody
	public String uuid() {
		return IdGen.uuid();
	}

}