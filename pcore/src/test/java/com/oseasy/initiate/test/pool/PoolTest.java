package com.oseasy.initiate.test.pool;


import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * Created by victor on 2017/7/20.
 */
public class PoolTest {
    public static void main(String[] args) {
    		BaseObject bo = null;
    		PoolableObjectFactory factory = new TestPoolableFactory();
    		GenericObjectPool pool = new GenericObjectPool(factory);
    		//这里两种池都可以，区别下文会提到
    		//ObjectPool pool = new StackObjectPool(factory);
    		try {
    			for(int i = 0; i < 5; i++) {
    				System.out.println("\n==========="+i+"===========");
    				System.out.println("池中处于闲置状态的实例pool.getNumIdle()："+pool.getNumIdle());
    				//从池里取一个对象，新创建makeObject或将以前闲置的对象取出来
    				bo = (BaseObject)pool.borrowObject();
    				System.out.println("bo:"+bo);
    				System.out.println("池中所有在用实例数量pool.getNumActive()："+pool.getNumActive());
    				if((i%2) == 0) {
    					//用完之后归还对象
    					pool.returnObject(bo);
    					System.out.println("归还对象！！！！");
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			try {
    				if(bo != null) {
    					pool.returnObject(bo);
    				}
    				//关闭池
    				pool.close();
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}

}
