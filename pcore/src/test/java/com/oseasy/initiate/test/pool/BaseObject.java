package com.hch.platform.pcore.test.pool;

/**
 * Created by victor on 2017/7/20.
 */
public class BaseObject {

    //记录从池中取出次数
       private int num;
       private boolean active;

       public BaseObject(){
           active = true;
           System.out.println("new BaseObject!!!!!");
       }


    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
