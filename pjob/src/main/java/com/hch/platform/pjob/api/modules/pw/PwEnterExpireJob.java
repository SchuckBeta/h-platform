package com.oseasy.initiate.jobs.pw;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oseasy.initiate.jobs.AbstractJobDetail;
import com.oseasy.initiate.modules.actyw.tool.process.cmd.ActYwRstatus;
import com.oseasy.initiate.modules.pw.service.PwEnterService;
import com.oseasy.initiate.modules.pw.vo.PwEnterExpireVo;

@Service("pwEnterExpireJob")
public class PwEnterExpireJob extends AbstractJobDetail{
  public static Logger logger = Logger.getLogger(PwEnterExpireJob.class);

  @Autowired
  private PwEnterService pwEnterService;

  @Override
  public void doWork() {
    try {
      ActYwRstatus<PwEnterExpireVo> expireVoRstatus = pwEnterService.expireEnterAll();
      logger.info("处理完成、处理结果请查看日志文件:" + expireVoRstatus.getDatas().getLogFile());
    } catch (Exception e) {
      logger.error("处理导师、学生详情点赞队列任务出错",e);
    }
  }
}