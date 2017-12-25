package com.oseasy.initiate.test;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.oseasy.initiate.common.utils.sms.SMSUtil;

/**
 * 附件后台测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-context-activiti.xml"})
public class SmsTest {

	public static void main(String[] args) throws IOException {
		String mobile="13554344939";
		String mobilelt="13733515910";
		String sm = SMSUtil.sendSms(mobilelt);
		System.out.println(sm);
	}
	
	@Test
    public void saveTest() {
		String mobile="13554344939";
		String sm = SMSUtil.sendSms(mobile);
		System.out.println(sm);
    }
}
