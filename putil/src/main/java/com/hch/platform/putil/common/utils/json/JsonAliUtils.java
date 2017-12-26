/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.oseasy.initiate.common.utils.json
 * @Description [[_JsonAUtils_]]文件
 * @date 2017年6月2日 上午10:19:00
 *
 */

package com.oseasy.initiate.common.utils.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.oseasy.initiate.common.utils.FileUtil;
import com.oseasy.initiate.common.utils.StringUtil;
import com.oseasy.initiate.modules.actyw.tool.process.vo.StenType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * com.alibaba.fastjson.JSON.
 *
 * @Description JSON工具类
 * @author chenhao
 * @date 2017年6月2日 上午10:19:00
 *
 */
public class JsonAliUtils {
  private static final String UTF_8 = "UTF-8";
  protected static final Logger LOGGER = Logger.getLogger(JsonAliUtils.class);
  /**
   * @Description: 读取json文件 .
   * @param path 文件路径
   */
  public static String readFile(String path) {
    String result = null;
    try {
      File file = new File(path);
      result = FileUtil.readFileToString(file, UTF_8);
    } catch (IOException e) {
      LOGGER.warn("读取文件失败！", e);
    }
    return result;
  }

  /**
   * @Description: 写json文件.
   * @param path 文件路径
   */
  public static Boolean writeFile(String path, String content) {
    Boolean isTrue = false;
    try {
      File file = new File(path);
      if(!file.exists()){
        FileUtil.createFile(file.getPath());
        System.out.println(file.getAbsolutePath());
      }
      FileUtil.writeStringToFile(file, content, UTF_8);
      isTrue = true;
    } catch (IOException e) {
      LOGGER.warn("保存文件失败！", e);
    }
    return isTrue;
  }

  /**
   * 将JSON文本反序列化为主从关系的实体.
   * @param <T> 泛型类
   * @param 主实体类型
   * @param jsonString
   *          JSON文本
   * @param mainClass
   *          主实体类型
   */
  public static <T> List<T> toBean(String jsonString, Class<T> mainClass) {
    return (ArrayList<T>) JSON.parseArray(jsonString, mainClass);
  }

  /***
   * @Description: 读取json文件 将JSON文本反序列化为主从关系的实体.
   * @param <T> 泛型类
   * @param 主实体类型
   * @param mainClass
   *          主实体类型
   * @param path 文件路径
   */
  public static <T> List<T> readBean(String path, Class<T> mainClass) {
    return toBean(readFile(path), mainClass);
  }

  /***
   * @Description: 写json文件 将对象序列化为JSon.
   * @param json
   *          JSON文本
   * @param path 文件路径
   */
  public static boolean writeBean(String path, JSONObject json) {
    if ((json != null) && StringUtil.isNotEmpty(path)) {
      return writeFile(path, json.toString());
    }
    return false;
  }

  /**
   * @Description: 写json文件 将对象序列化为JSon.
   * @param json
   *          JSON文本
   * @param path 文件路径
   */
  public static boolean writeBean(String path, JSONArray json) {
    if ((json != null) && StringUtil.isNotEmpty(path)) {
      return writeFile(path, json.toString());
    }
    return false;
  }

  /**
   * 读取json文件 .
   * @author chenhao
   * @param path 路径
   */
  public static String readJson(String path) throws Exception {
    InputStream stencilsetStream = StenType.class.getClassLoader().getResourceAsStream(path);
    try {
      return IOUtils.toString(stencilsetStream, "utf-8");
    } catch (Exception e) {
      throw new Exception("Error while loading json", e);
    }
  }


}