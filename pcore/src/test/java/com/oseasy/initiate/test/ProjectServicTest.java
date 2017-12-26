package com.hch.platform.pcore.test;

import com.hch.platform.pcore.common.utils.SpringContextHolder;
import com.hch.platform.pcore.modules.project.dao.ProjectDeclareDao;
import com.hch.platform.pcore.modules.project.service.ProjectDeclareService;
import com.hch.platform.pcore.modules.project.vo.ProjectDeclareVo;
import com.hch.platform.pcore.modules.project.vo.ProjectExpVo;
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
 * Created by zhangzheng on 2017/2/23.
 * 国创大赛后台测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml"
                , "classpath:spring-context-activiti.xml"
                , "classpath:spring-context-jedis.xml"
                , "classpath:spring-context-shiro.xml"
                     })
public class ProjectServicTest {
     @Autowired
     ProjectDeclareDao projectDeclareDao;

    @Test
    public  void test() {
      List<ProjectExpVo> expVos= projectDeclareDao.getExpsByUserId("5214a6467d67489081d5f0bbbd23084b");
      System.out.println(expVos.size());
    }



}
