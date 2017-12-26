package com.oseasy.initiate.modules.actyw.tool.process.gnodcmd.cmd.impl;

import java.io.Serializable;

public class Gpoint implements Serializable{
  private static final long serialVersionUID = 1L;

  private String id;
  private String posLux;
  private String posLuy;
  private Float width;
  private Float height;

  private String posAlux;
  private String posAluy;
  private Float widtha;
  private Float heighta;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPosLux() {
    return posLux;
  }

  public void setPosLux(String posLux) {
    this.posLux = posLux;
  }

  public String getPosLuy() {
    return posLuy;
  }

  public void setPosLuy(String posLuy) {
    this.posLuy = posLuy;
  }

  public Float getWidth() {
    return width;
  }

  public void setWidth(Float width) {
    this.width = width;
  }

  public Float getHeight() {
    return height;
  }

  public void setHeight(Float height) {
    this.height = height;
  }

  public String getPosAlux() {
    return posAlux;
  }

  public void setPosAlux(String posAlux) {
    this.posAlux = posAlux;
  }

  public String getPosAluy() {
    return posAluy;
  }

  public void setPosAluy(String posAluy) {
    this.posAluy = posAluy;
  }

  public Float getWidtha() {
    return widtha;
  }

  public void setWidtha(Float widtha) {
    this.widtha = widtha;
  }

  public Float getHeighta() {
    return heighta;
  }

  public void setHeighta(Float heighta) {
    this.heighta = heighta;
  }
}
