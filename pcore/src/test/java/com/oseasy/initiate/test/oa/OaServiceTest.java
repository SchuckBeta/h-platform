package com.hch.platform.pcore.test.oa;

import com.hch.platform.pcore.modules.oa.service.OaNotifyService;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 附件后台测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-context-activiti.xml"})
public class OaServiceTest {
	@Autowired
	private OaNotifyService oaNotifyService;

	@Test
    public void saveTest() {

		AbsUser apply_User=new AbsUser();
		apply_User.setId("fb07ce5ac34448609a76604672436134");
		AbsUser rec_User=new AbsUser();
		rec_User.setId("00becc26570540da8f8c418a9c6b4546");
		oaNotifyService.sendOaNotifyByType(apply_User,rec_User,"title","content","","");
    }




}
