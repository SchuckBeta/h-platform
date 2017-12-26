package com.hch.platform.pcore.test.sco;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hch.platform.pcore.modules.sco.entity.ScoScore;
import com.hch.platform.pcore.modules.sco.service.ScoScoreService;
import com.hch.platform.pcore.modules.sco.vo.ScoScoreVo;
import com.hch.platform.pcore.modules.sco.vo.ScoType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml",
    "classpath:spring-context-activiti.xml", "classpath:spring-context-shiro.xml",
    "classpath:spring-context-jedis.xml", "classpath:spring-context-scheduler.xml",
    "classpath:spring-mvc.xml"})
public class ScoScoreVoTest {
  @Autowired
  ScoScoreService scoScoreService;

  @Test
  public void testAdd() {
    IScoScoreImpl iScoScoreImpl = new IScoScoreImpl();
    iScoScoreImpl.setId("1");
    iScoScoreImpl.setScoreA(50.0);
    iScoScoreImpl.setScoreB(50.0);

    ScoScoreVo scoScoreVo = new ScoScoreVo(iScoScoreImpl, new ScoScore());
    System.out.println(scoScoreVo.getIsco().getId(iScoScoreImpl));
    System.out.println(scoScoreVo.getIsco().calculate(iScoScoreImpl));
    ScoScore scoScore = scoScoreService.add(scoScoreVo, ScoType.ST_BUSINESS);
    assertTrue((scoScore.getId()).equals("1"));
    assertTrue((scoScore.getBusinessScore() == 100));
  }
}
