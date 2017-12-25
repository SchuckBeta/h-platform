package com.oseasy.initiate.modules.sys.web.front;

import com.oseasy.initiate.common.persistence.Page;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.common.utils.sms.SMSUtilAlidayu;
import com.oseasy.initiate.common.web.BaseController;
import com.oseasy.initiate.modules.oa.entity.OaNotify;
import com.oseasy.initiate.modules.oa.service.OaNotifyService;
import com.oseasy.initiate.modules.sys.entity.Role;
import com.oseasy.initiate.modules.sys.entity.User;
import com.oseasy.initiate.modules.sys.service.SystemService;
import com.oseasy.initiate.modules.sys.service.UserService;
import com.oseasy.initiate.modules.sys.utils.UserUtils;
import com.oseasy.initiate.modules.team.entity.Team;
import com.oseasy.initiate.modules.team.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author  zhangzheng
 * 发送短信验证码，验证短信验证码
 */
@Controller
@RequestMapping(value = "${frontPath}/mobile")
public class MobilefrontController extends BaseController {

	private static String mobileValidateCode = "mobileValidateCode";


	/**
	 * 发送短信验证码
	 * @return
	 */
	@RequestMapping("sendMobileValidateCode")
	@ResponseBody
	public Boolean sendMobileValidateCode(HttpServletRequest request) {
		String mobile = request.getParameter("mobile");
		//发送短信 ，方法返回验证码
//		String code =  String.format("%06d", rand.nextInt(1000000));
		String code =  SMSUtilAlidayu.sendSms(mobile);
		//短信验证码保存到session
		request.getSession().setAttribute(mobileValidateCode, code);
		System.out.println(code);
		return true;
	}

	/**
	 * 验证短信验证码   true:验证通过    false：验证不通过
	 * @return
	 */
	@RequestMapping("checkMobileValidateCode")
	@ResponseBody
	public Boolean validateCode(HttpServletRequest request,String yzm) {
		//session  获取验证码
		String code = (String)request.getSession().getAttribute(MobilefrontController.mobileValidateCode);
		// 比较验证码
		if (StringUtil.equals(yzm, code)) {
			return true;
		}
		return false;
	}



}
