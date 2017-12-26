package com.oseasy.initiate.test.sco;

import com.oseasy.initiate.modules.sco.vo.IScoScore;

public class IScoScoreImpl implements IScoScore<IScoScoreImpl> {
  private String id;
  private Double scoreA;
  private Double scoreB;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Double getScoreA() {
    return scoreA;
  }

  public void setScoreA(Double scoreA) {
    this.scoreA = scoreA;
  }

  public Double getScoreB() {
    return scoreB;
  }

  public void setScoreB(Double scoreB) {
    this.scoreB = scoreB;
  }

  @Override
  public Double calculate(IScoScoreImpl entity) {
    return entity.getScoreA() + entity.getScoreB();
  }

  @Override
  public String getId(IScoScoreImpl entity) {
    return entity.getId();
  }
}
