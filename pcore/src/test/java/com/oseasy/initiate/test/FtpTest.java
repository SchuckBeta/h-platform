package com.oseasy.initiate.test;

import com.oseasy.initiate.common.utils.DateUtil;
import com.oseasy.initiate.common.utils.FtpUtil;
import com.oseasy.initiate.modules.attachment.entity.SysAttachment;
import com.oseasy.initiate.modules.attachment.service.SysAttachmentService;
import com.oseasy.initiate.modules.gcontest.entity.GContest;
import com.oseasy.initiate.modules.project.service.ProjectDeclareService;
import com.oseasy.initiate.modules.project.vo.ProjectDeclareVo;
import com.oseasy.initiate.modules.sys.dao.UserDao;
import com.oseasy.initiate.modules.sys.entity.User;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 附件后台测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class FtpTest {

	public static void main(String[] args) throws IOException {
     /*   FtpUtil t = new FtpUtil();
      //  t.upload(ftpUrl, userName, port, password, directory, srcFileName, destName)
        t.upload("192.168.0.105","ftponly",2121,"os-easy",
        		"/tool/oseasy/gcontest",
        		"C:\\Users\\Administrator\\Desktop\\大赛审核步骤参数.txt","副本.txt");*/
		/*Map<String,Object> vars=new HashMap<String,Object>();
		vars.put("grade","1");
		vars.put("grade","0");
		System.out.println(vars.get("grade"));*/
		/*String str="4444";
		double num;
		java.text.DecimalFormat myformat=new java.text.DecimalFormat(".0");
		num=Double.parseDouble(str);//装换为double类型
		num=Double.parseDouble(myformat.format(num));//保留2为小数
		System.out.println(num);

		System.out.println(Float.parseFloat("90"));*/

		/*String s="袋猫团队&ldquo;学研商&rdquo;团队邀请记录";
	    String ss= StringEscapeUtils.unescapeHtml4(s);
	    System.out.println(ss);
		String str = "&lt;p&gt;";
		str = StringEscapeUtils.unescapeHtml4(str);
		System.out.println(str);*/
		String s="yyyyMMdd HH:mm:ss";
		//DateUtil.formatDate(new Date(), s);
		DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
		//DateFormatUtils.format(new Date(), s);
	}

	public static void changeState(GContest gc) {
		gc.setAuditState("3");
	}

	public  void changeAuditState(GContest gc) {
		gc.setAuditState("4");
	}
}
