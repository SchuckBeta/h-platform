package com.oseasy.initiate.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.oseasy.initiate.modules.sys.tool.SysNodeFresult;
import com.oseasy.initiate.modules.sys.tool.SysNodeFresultTool;
import com.oseasy.initiate.modules.sys.tool.SysNodeFresultVo;

public class SysNodeFresultToolTest {
  @Test
  public void testFormatDeal() {
    String fff = "yyyy${c}MMdd${a}dd${b}dd${e}d${f}d${g}";

    SysNodeFresult fresult = new SysNodeFresult(fff);
    SysNodeFresult vo = SysNodeFresultTool.formatDeal(fresult.setResult(fff));
    SysNodeFresult vo4 = SysNodeFresultTool.formatDeal(vo);
    SysNodeFresult vo5 = SysNodeFresultTool.formatDeal(vo4);
    SysNodeFresult vo6 = SysNodeFresultTool.formatDeal(vo5);
    SysNodeFresult vo7 = SysNodeFresultTool.formatDeal(vo6);
    SysNodeFresult vo8 = SysNodeFresultTool.formatDeal(vo7);
    SysNodeFresult vo9 = SysNodeFresultTool.formatDeal(vo8);
    SysNodeFresult vo10 = SysNodeFresultTool.formatDeal(vo9);
    SysNodeFresult vo11 = SysNodeFresultTool.formatDeal(vo10);
    System.out.println("---------|"+ vo.getIsEnable() + vo.getResult()+"|---------");
    System.out.println("---------|"+ vo4.getIsEnable() + vo4.getResult()+"|---------");
    System.out.println("---------|"+ vo5.getIsEnable() + vo5.getResult()+"|---------");
    System.out.println("---------|"+ vo6.getIsEnable() + vo6.getResult()+"|---------");
    System.out.println("---------|"+ vo7.getIsEnable() + vo7.getResult()+"|---------");
    System.out.println("---------|"+ vo8.getIsEnable() + vo8.getResult()+"|---------");
    System.out.println("---------|"+ vo9.getIsEnable() + vo9.getResult()+"|---------");
    System.out.println("---------|"+ vo10.getIsEnable() + vo10.getResult()+"|---------");
    System.out.println("---------|"+ vo11.getIsEnable() + vo11.getResult()+"|---------");
  }

  @Test
  public void testFormatRunner() throws Exception {
    String fff = "yyyy${c}MMdd${a}dd${b}dd${e}d${f}d${g}";
    SysNodeFresultVo vo21 = SysNodeFresultTool.formatRunner(new SysNodeFresult(fff));
    System.out.println("---------|"+ vo21.getFormatResult().getIsEnable() + vo21.getFormatResult().getResult()+"|---------");
  }

  @Test
  public void testGenDateFormat() throws Exception {
    String fff = "yyyy${c}MMdd${a}dd${b}dd${e}d${f}d${g}";
    System.out.println("---------|"+ SysNodeFresultTool.genDateFormat(new Date(), new SysNodeFresult(fff))+"|---------");
  }

  @Test
  public void testGenFormat() throws Exception {
    String fff = "yyyy${c}MMdd${a}dd${b}dd${e}d${f}d${g}";
    System.out.println("---------|"+ SysNodeFresultTool.genFormat(new Date(), new SysNodeFresult(fff))+"|---------");
    System.out.println("---------|"+ SysNodeFresultTool.genFormat(new Date(), SysNodeFresultTool.formatRunner(new SysNodeFresult(fff)))+"|---------");
  }

  @Test
  public void testRenderFormat() throws Exception {
    String fff = "yyyy${c}MMdd${a}dd${b}dd${e}d${f}d${g}";
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("c", "KKK");
    model.put("b", "ZZZ");
    model.put("a", "ZZZ");
    model.put("e", "XX");
    model.put("f", "YY");
    model.put("g", "MM");
    System.out.println("---------|"+ SysNodeFresultTool.renderFormat(new Date(), new SysNodeFresult(fff), model)+"|---------");
  }
}
