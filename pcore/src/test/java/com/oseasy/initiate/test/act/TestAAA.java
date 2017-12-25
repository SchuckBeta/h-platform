/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.test.act
 * @Description [[_TestAAA_]]文件
 * @date 2017年6月7日 下午2:17:26
 *
 */

package com.oseasy.initiate.test.act;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * TODO 添加类/接口功能描述.
 * @author chenhao
 * @date 2017年6月7日 下午2:17:26
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-*.xml"})
public class TestAAA extends AbstractJUnit4SpringContextTests {


  @Test
  public void startProcess() throws Exception {
    System.out.println("---------------");
//    System.out.println(dictService.get("0"));
  }
}
