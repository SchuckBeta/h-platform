/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.modules.actyw.tool.process.cmd
 * @Description [[_ActYwRstatus_]]文件
 * @date 2017年6月30日 上午11:07:07
 *
 */
package com.hch.platform.putil.common.config;

/**
 * 获取命令处理状态信息.
 * @author chenhao
 * @date 2017年6月30日 上午11:07:07
 *
 */
public class ActYwRstatus<T> {
  private Boolean status;
  private String msg;
  private T datas;

  public ActYwRstatus() {
    super();
    this.status = true;
    this.msg = "执行成功";
  }

  public ActYwRstatus(Boolean status, String msg) {
    super();
    this.status = status;
    this.msg = msg;
  }

  public ActYwRstatus(Boolean status, String msg, T datas) {
    super();
    this.status = status;
    this.msg = msg;
    this.datas = datas;
  }

  public Boolean getStatus() {
    return status;
  }
  public void setStatus(Boolean status) {
    this.status = status;
  }
  public String getMsg() {
    return msg;
  }
  public void setMsg(String msg) {
    this.msg = msg;
  }

  public void setDatas(T datas) {
    this.datas = datas;
  }

  public T getDatas() {
    return datas;
  }
}
