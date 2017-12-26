package com.oseasy.initiate.modules.actyw.tool.apply;

import java.util.List;

import com.oseasy.initiate.modules.actyw.tool.apply.impl.SuAstatus;
import com.oseasy.initiate.modules.actyw.tool.apply.impl.SuAudit;
import com.oseasy.initiate.modules.actyw.tool.apply.impl.SuoAppointment;
import com.oseasy.initiate.modules.actyw.tool.apply.impl.SuoEnter;
import com.oseasy.initiate.modules.actyw.tool.apply.vo.SuRstatus;
import com.oseasy.initiate.modules.actyw.tool.apply.vo.SuRstatusParam;
import com.oseasy.initiate.modules.actyw.tool.apply.vo.SuStatusAparam;
import com.oseasy.initiate.modules.actyw.tool.apply.vo.SuStatusGrade;
import com.oseasy.initiate.modules.actyw.tool.apply.vo.SuStatusParam;
import com.oseasy.initiate.modules.actyw.tool.process.vo.FlowType;

/**
 * 主题.
 * @author chenhao
 *
 */
public interface ISubject<T extends ISuObserver, R extends IRstatus> {
  //添加观察者
  void add(T sobj);

  //移除观察者
  void delete(T sobj);

  //当主题方法改变时,这个方法被调用,通知所有的观察者
  List<R> notifys();

  public static void main(String[] args) {
    SuAstatus status = new SuAstatus();
    SuAudit audit = new SuAudit();
    String type = "3";
    if((type).equals("1")){
      new SuoEnter("SuoEnter1000", audit, status);
      SuStatusParam param = status.dealAstatus("SuoEnter1000");
      System.out.println("【入驻】获取到的状态为：" + SuStatusGrade.getGradeByGnode(param.getStatus(), "节点A").getGrades().toString());

      List<SuRstatusParam> isAuditTrue = audit.dealAuditss(new SuStatusAparam("1", "0"));
      System.out.println("【入驻】审核结果为1，业务处理结果为：" + isAuditTrue);
    }else if((type).equals("2")){
      new SuoAppointment("SuoAppointment1000", audit, status);
      SuStatusParam param = status.dealAstatus("SuoAppointment1000");
      System.out.println("【预约】获取到的状态为：" + SuStatusGrade.getGradeByGnode(param.getStatus(), "节点B").getGrades().toString());
      SuRstatus isAuditTrue = audit.dealAudit(new SuStatusAparam("2", "0"), "", FlowType.FWT_DASAI);
      System.out.println("【预约】审核结果为2，业务处理结果为：" + isAuditTrue);
      if(isAuditTrue.getStatus()){
        //处理成功.流程执行下一步
        System.out.println("【预约】处理成功.流程执行下一步");
      }else{
        //处理失败，流程执行结束
        System.out.println("【预约】处理失败，流程执行结束");
      }
    }else if((type).equals("3")){
      new SuoEnter("SuoEnter1000", audit, status);
      new SuoAppointment("SuoAppointment1000", audit, status);
      SuStatusParam param = status.dealAstatus("SuoEnter1000");
      System.out.println("【入驻】获取到的状态为：" + SuStatusGrade.getGradeByGnode(param.getStatus(), "节点A").getGrades().toString());

      SuStatusParam param2 = status.dealAstatus("SuoAppointment1000");
      System.out.println("【预约】获取到的状态为：" + SuStatusGrade.getGradeByGnode(param2.getStatus(), "节点B").getGrades().toString());
      SuRstatusParam isAuditTrues = audit.dealAudits(new SuStatusAparam("2", "0"), "", FlowType.FWT_DASAI);
      SuRstatus isAuditTrue = isAuditTrues.getStatus().get(0);
      System.out.println("【预约】审核结果为2，业务处理结果为：" + isAuditTrue);
      if(isAuditTrue.getStatus()){
        if(("0").equals(isAuditTrue.getRtVal())){
          //处理成功.流程执行下一步
          System.out.println("【预约】处理成功.流程执行下一步");
        }else{
          //处理成功.流程执行非预期结果
          System.out.println("【预约】处理成功.流程执行下一步");
        }
      }else{
        if(("1").equals(isAuditTrue.getRtVal())){
          //处理成功.流程执行下一步
          System.out.println("【预约】处理成功.流程执行下一步");
        }else if(("2").equals(isAuditTrue.getRtVal())){
          //处理成功.流程执行非预期结果
          System.out.println("【预约】处理失败，流程执行结果2");
        }else{
          //处理失败，流程执行结束
          System.out.println("【预约】处理失败，流程执行结束");
        }
      }
    }
  }
}
