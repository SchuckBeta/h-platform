/**
 *
 */
package com.hch.platform.pcore.modules.oa.web;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hch.platform.pcore.common.persistence.Page;
import com.hch.platform.pcore.common.utils.FtpUtil;
import com.hch.platform.pcore.common.utils.cache.CacheUtils;
import com.hch.platform.putil.common.utils.StringUtil;
import com.hch.platform.pcore.common.web.BaseController;
import com.hch.platform.pcore.modules.interactive.util.InteractiveUtil;
import com.hch.platform.pcore.modules.oa.entity.OaNotify;
import com.hch.platform.pcore.modules.oa.service.OaNotifyKeywordService;
import com.hch.platform.pcore.modules.oa.service.OaNotifyService;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import com.hch.platform.pcore.modules.sys.utils.UserUtils;

import net.sf.json.JSONObject;

/**
 * 通知通告Controller

 * @version 2014-05-16
 */
@Controller
@RequestMapping(value = "${frontPath}/oa/oaNotify")
public class FrontOaNotifyController extends BaseController {
	@Autowired
	private OaNotifyKeywordService oaNotifyKeywordService;
	@Autowired
	private OaNotifyService oaNotifyService;
	@ModelAttribute
	public OaNotify get(@RequestParam(required=false) String id) {
		OaNotify entity = null;
		if (StringUtil.isNotBlank(id)) {
			entity = oaNotifyService.get(id);
		}
		if (entity == null) {
			entity = new OaNotify();
		}
		return entity;
	}
	@RequestMapping(value = {"viewList"})
	public String viewList(OaNotify oaNotify, HttpServletRequest request, HttpServletResponse response, Model model) {
//		User currUser = UserUtils.getUser();
//		//logger.info("curre========="+currUser.getId());
//		if (currUser!=null&&currUser.getId()!=null&&!"1".equals(currUser.getId())) {
//			oaNotify.setUserId(String.valueOf(currUser.getId()));
//		}
//		oaNotify.setIsSelf(true);
//		Page<OaNotify> page = oaNotifyService.find(new Page<OaNotify>(request, response), oaNotify);
//		model.addAttribute("page", page);
		return "modules/oa/oaNotifyViewList";
	}
	@RequestMapping(value = {"getUnreadCount"})
	@ResponseBody
	public JSONObject getUnreadCount() {
		String uid=UserUtils.getUser().getId();
		JSONObject js=new JSONObject();
		js.put("ret", "1");
		if(StringUtil.isEmpty(uid)){
			js.put("ret", "0");
			js.put("count", 0);
			return js;
		}
		Integer c=oaNotifyService.getUnreadCount(uid);
		if(c==null){
			c=0;
		}
		js.put("count",c);
		return js;
	}


	/**
	 * 首页我的通知列表
	 */
	@RequestMapping(value = "indexMyNoticeList")
	public String indexMyNoticeList(OaNotify oaNotify, HttpServletRequest request, HttpServletResponse response, Model model) {
		AbsUser currUser = UserUtils.getUser();
		//logger.info("curre========="+currUser.getId());
		if (currUser!=null&&currUser.getId()!=null) {
			oaNotify.setUserId(String.valueOf(currUser.getId()));
		}else{
			oaNotify.setType("error");
		}

		oaNotify.setIsSelf(true);
		Page<OaNotify> page = oaNotifyService.find(new Page<OaNotify>(request, response), oaNotify);
		model.addAttribute("page", page);
		return "modules/oa/indexOaNotifyList";
	}


	/**
	 * 首页我的通知列表
	 */
	@RequestMapping(value = "indexMySendNoticeList")
	public String indexMySendNoticeList(OaNotify oaNotify, HttpServletRequest request, HttpServletResponse response, Model model) {
		AbsUser currUser = UserUtils.getUser();
		//logger.info("curre========="+currUser.getId());
		if (currUser!=null&&currUser.getId()!=null) {
			oaNotify.setUserId(String.valueOf(currUser.getId()));
		}else{
			oaNotify.setType("error");
		}

		Page<OaNotify> page = oaNotifyService.findSend(new Page<OaNotify>(request, response), oaNotify);
		model.addAttribute("page", page);
		return "modules/oa/indexOaNotifySendList";
	}

	@RequestMapping(value = "getReadFlag")
	@ResponseBody
	public String getReadFlag(String oaNotifyId) {
		String flag="0";
		String uid=UserUtils.getUser().getId();
		if(StringUtil.isNotEmpty(uid)){
			return oaNotifyService.getReadFlag(oaNotifyId, uid);
		}
		return flag;
	}
	/**
	 * 查看我的通知
	 */
	@RequestMapping(value = "view")
	@ResponseBody
	public OaNotify view(String oaNotifyId, Model model) {
		OaNotify oaNotify = new OaNotify();
		oaNotify.setId(oaNotifyId);
		oaNotifyService.updateReadFlag(oaNotify);
		oaNotify = oaNotifyService.get(oaNotify.getId());
		if(oaNotify == null){
		  return null;
		}
		OaNotify  oaNotifyTmp = oaNotifyService.getRecordList(oaNotify);
		if(oaNotifyTmp == null){
      		return null;
    	}
		if((oaNotify == null) || (oaNotifyTmp == null)){
		  return oaNotify;
		}
		oaNotify.setOaNotifyRecordList(oaNotifyTmp.getOaNotifyRecordList());
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (oaNotify.getEffectiveDate()!=null) {
			String effDate = sFormat.format(oaNotify.getEffectiveDate());
			oaNotify.setPublishDate(effDate);
		}
		if (oaNotify.getContent()!=null) {
			oaNotify.setContent(StringUtil.replaceEscapeHtml(oaNotify.getContent()));
		}
		if (oaNotify!=null&&StringUtil.isNotEmpty(oaNotify.getContent())) {
			oaNotify.setContent(oaNotify.getContent().replaceAll(FtpUtil.FTP_MARKER,FtpUtil.FTP_HTTPURL));
		}
		return oaNotify;
	}
	/**
	 * 查看双创动态、双创通知、省市动态
	 */
	@RequestMapping(value = "viewDynamic")
	public String viewDynamic(OaNotify oaNotify,Model model,HttpServletRequest request) {
		if(oaNotify != null){
	  	  	if(StringUtil.isEmpty(oaNotify.getViews())){
	  			oaNotify.setViews("0");
	  		}
	  		if(StringUtil.isNotEmpty(oaNotify.getId())){
	  			oaNotify.setKeywords(oaNotifyKeywordService.findListByEsid(oaNotify.getId()));
	  		}
	  		if(StringUtil.isNotEmpty(oaNotify.getContent())){
	  			oaNotify.setContent(StringEscapeUtils.unescapeHtml4(oaNotify.getContent()));
	  		}
	  		if (oaNotify!=null&&StringUtil.isNotEmpty(oaNotify.getContent())) {
	  			oaNotify.setContent(oaNotify.getContent().replaceAll(FtpUtil.FTP_MARKER,FtpUtil.FTP_HTTPURL));
	  		}
	  		if(StringUtil.isNotEmpty(oaNotify.getId())){
	  			model.addAttribute("more",oaNotifyService.getMore(oaNotify.getType(),oaNotify.getId(),oaNotify.getKeywords()));
	  		}
	  		if(StringUtil.isNotEmpty(oaNotify.getId())){
	  			InteractiveUtil.updateViews(oaNotify.getId(), request,CacheUtils.DYNAMIC_VIEWS_QUEUE);
	  		}
	    }
		return "modules/oa/dynamicView";
	}

	@RequestMapping(value = "deleteRec")
	public String deleteRec(OaNotify oaNotify, RedirectAttributes redirectAttributes) {
		oaNotifyService.deleteRec(oaNotify);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:" + frontPath + "/oa/oaNotify/indexMyNoticeList/?repage";
	}


	@RequestMapping(value = "deleteSend")
	public String deleteSend(OaNotify oaNotify, RedirectAttributes redirectAttributes) {
		oaNotifyService.deleteSend(oaNotify);
		addMessage(redirectAttributes, "删除成功");
		return "redirect:" + frontPath + "/oa/oaNotify/indexMySendNoticeList/?repage";
	}

	/**
	 * 批量删除接收消息
	 * @param ids OaNotify的ids，以逗号分隔
	 */
	@RequestMapping(value = "deleteRevBatch")
	@ResponseBody
	public boolean deleteRevBatch(String ids) {
		String[] idStr=ids.split(",");
		for (int i=0;i<idStr.length;i++) {
			OaNotify oaNotify=new OaNotify();
			oaNotify.setId(idStr[i]);
			oaNotifyService.deleteRec(oaNotify);
		}
		return true;
	}

	/**
	 * 批量删除发送消息
	 * @param ids OaNotify的ids，以逗号分隔
	 */
	@RequestMapping(value = "deleteSendBatch")
	@ResponseBody
	public boolean deleteSendBatch(String ids) {
		String[] idStr=ids.split(",");
		for (int i=0;i<idStr.length;i++) {
			OaNotify oaNotify=new OaNotify();
			oaNotify.setId(idStr[i]);
			oaNotifyService.deleteSend(oaNotify);
		}
		return true;
	}

	/**
	 * 双创动态，双创通知，省市动态分页
	 */
	@RequestMapping(value = "getPageJson")
	@ResponseBody
	public Page<OaNotify> getPageJson(OaNotify oaNotify,int pageNo,int pageSize,String funcName){
		oaNotify.setStatus("1");
		Page<OaNotify> page = new Page<OaNotify>(pageNo, pageSize);
		page.setFuncName(funcName);
		page = oaNotifyService.findPage(page, oaNotify);
		return page;
	}
}