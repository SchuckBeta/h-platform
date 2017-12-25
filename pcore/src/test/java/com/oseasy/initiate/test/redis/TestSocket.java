/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.test.redis
 * @Description [[_TestSocket_]]文件
 * @date 2017年6月13日 下午2:50:47
 *
 */

package com.oseasy.initiate.test.redis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.Test;

import redis.clients.util.RedisInputStream;
import redis.clients.util.RedisOutputStream;

/**
 * TODO 添加类/接口功能描述.
 *
 * @author chenhao
 * @date 2017年6月13日 下午2:50:47
 *
 */
public class TestSocket {
  String host = "172.18.0.18";
//  String host = "172.18.0.17";
//   String host = "127.0.0.1";
   int port = 6379;
  int timeout = 3000;

  @Test
  public void testRedisSocketConnect() {
    System.out.println("testSocketConnect-客户端启动...");
    while (true) {
      Socket socket = null;
      try {
        socket = new Socket();
        socket.setReuseAddress(true);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        socket.setSoLinger(true, 0);
        System.out.println("testSocketConnect-客户端connect start...");
        socket.connect(new InetSocketAddress(host, port), timeout);
        System.out.println("testSocketConnect-客户端connect...");
        socket.setSoTimeout(timeout);


        RedisOutputStream outputStream = new RedisOutputStream(socket.getOutputStream());
        RedisInputStream inputStream = new RedisInputStream(socket.getInputStream());
        inputStream.close();
        outputStream.close();
        System.out.println("testSocketConnect-客户端end...");
      } catch (IOException ex) {
        ex.printStackTrace();
//        throw new JedisConnectionException(ex);
      } finally {
        if (socket != null) {
          try {
            socket.close();
          } catch (IOException e) {
            socket = null;
            System.out.println("客户端 finally 异常:" + e.getMessage());
          }
        }
      }
    }
  }

//  @Test
//  public void testSocketConnect() {
//    System.out.println("testMain-客户端启动...");
//    System.out.println("testMain-当接收到服务器端字符为 \"OK\" 的时候, 客户端将终止\n");
//    while (true) {
//      Socket socket = null;
//      try {
//        // 创建一个流套接字并将其连接到指定主机上的指定端口号
//        socket = new Socket(host, port);
//        socket.setSoTimeout(timeout);
//        // 读取服务器端数据
//        DataInputStream input = new DataInputStream(socket.getInputStream());
//        // 向服务器端发送数据
//        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//        System.out.print("请输入: \t");
//        String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
//        out.writeUTF(str);
//
//        String ret = input.readUTF();
//        System.out.println("服务器端返回过来的是: " + ret);
//        // 如接收到 "OK" 则断开连接
//        if ("OK".equals(ret)) {
//          System.out.println("客户端将关闭连接");
//          Thread.sleep(500);
//          break;
//        }
//
//        out.close();
//        input.close();
//      } catch (Exception e) {
//        System.out.println("客户端异常:" + e.getMessage());
//      } finally {
//        if (socket != null) {
//          try {
//            socket.close();
//          } catch (IOException e) {
//            socket = null;
//            System.out.println("客户端 finally 异常:" + e.getMessage());
//          }
//        }
//      }
//    }
//  }
}
