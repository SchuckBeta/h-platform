package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd;

import com.oseasy.initiate.modules.actyw.tool.process.ActYwEngine;

/**
 * 节点操作.
 * @author chenhao
 *
 */
public abstract class IGnodeOper<T, E extends ActYwEngine<?, ?>, R extends IGnodeValidate<?, ?, ?>>{
  public R validate;
  public E engine;

  public IGnodeOper() {
    super();
  }
  public IGnodeOper(E engine) {
    super();
    this.engine = engine;
  }
  public IGnodeOper(E engine, R validate) {
    super();
    this.validate = validate;
    this.engine = engine;
  }
  public E getEngine() {
    return engine;
  }
  public void setEngine(E engine) {
    this.engine = engine;
  }
  public R getValidate() {
    return validate;
  }
  public void setValidate(R validate) {
    this.validate = validate;
  }
}
