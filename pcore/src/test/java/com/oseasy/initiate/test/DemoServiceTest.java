package com.hch.platform.pcore.test;

import com.hch.platform.pcore.modules.sys.entity.TeacherExpansion;
import com.hch.platform.pcore.modules.sys.service.TeacherExpansionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hch.platform.pcore.modules.cms.service.DemoService;
import com.hch.platform.pcore.modules.sys.entity.AbsUser;

import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-context.xml")
public class DemoServiceTest {
    @Autowired
     DemoService service;

    @Autowired
    TeacherExpansionService teacherExpansionService;
    @Test
    public void saveTest() {
        AbsUser user = new AbsUser();
        user.setName("zy1");
        service.find(user);

     }

    @Test
    public void saveteTest() {
        TeacherExpansion teacherExpansion=new TeacherExpansion();
        teacherExpansion.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        teacherExpansion.setPost(1);
        teacherExpansion.setUserId("11");
        teacherExpansionService.save(teacherExpansion);
    }



}
