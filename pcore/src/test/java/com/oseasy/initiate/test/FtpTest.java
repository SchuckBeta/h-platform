package com.hch.platform.pcore.test;

import com.hch.platform.putil.common.utils.DateUtil;
import com.hch.platform.pcore.common.utils.FtpUtil;
import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.gcontest.entity.GContest;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.project.vo.ProjectDeclareVo;
import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.*;

/**
 * 附件后台测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class FtpTest {

	public static void main(String[] args) throws IOException {
      //  FtpUtil.upload(ftpUrl, userName, port, password, directory, srcFileName, destName)
      	/*FtpUtil.upload("192.168.0.122","ftponly",21,"1qazse4",
        		"/tool/oseasy/gcontest",
        		"C:\\Users\\Administrator\\Desktop\\Sonar持续集成安装配置.docx","work中文.docx");*/
		//FtpClient ftpClient =FtpUtil.getftpClient();
		//t.download()
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
	/*	String s="yyyyMMdd HH:mm:ss";
		//DateUtil.formatDate(new Date(), s);
		DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");*/
		//DateFormatUtils.format(new Date(), s);
		List<String> idsList=new ArrayList<String>();
		idsList.add("1");
		idsList.add("2");
		idsList.add("3");
		String ids ="";
		for(String id:idsList){
			ids=ids+id+",";
		}
		ids=ids.substring(0,ids.lastIndexOf(","));
		System.out.println(ids);
	}

	public static void changeState(GContest gc) {
		gc.setAuditState("3");
	}

	public  void changeAuditState(GContest gc) {
		gc.setAuditState("4");
	}
}
