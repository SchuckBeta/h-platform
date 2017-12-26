package com.hch.platform.pcore.test;

import com.hch.platform.pcore.modules.attachment.entity.SysAttachment;
import com.hch.platform.pcore.modules.attachment.enums.FileTypeEnum;
import com.hch.platform.pcore.modules.attachment.service.SysAttachmentService;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.project.vo.ProjectDeclareVo;
import com.hch.platform.pcore.modules.sys.dao.UserDao;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 附件后台测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml","classpath:spring-context-activiti.xml"})
public class AttServiceTest {  


	@Autowired
	private SysAttachmentService sysAttachmentService;

	@Test
    public void saveTest() {
		SysAttachment sysAttachment=new SysAttachment();
		sysAttachment.setUid("111111");
		sysAttachment.setType(FileTypeEnum.S2);
		sysAttachment.setName("jiaose.txt");
		sysAttachment.setUrl("/tool/oseasy/temp/gcontest/2017-03-24/jiaose.txt");
		sysAttachmentService.save(sysAttachment);
    }




}
