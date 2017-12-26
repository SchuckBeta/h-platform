/**
 * 源代码版权归[[os-easy]]公司所有.
 * @Project: ROOT
 * @Package com.hch.platform.putil.common.utils.json
 * @Description [[_JsonAUtils_]]文件
 * @date 2017年6月2日 上午10:19:00
 *
 */

package com.hch.platform.pactyw.common.utils.json;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.hch.platform.putil.modules.actyw.tool.process.vo.StenType;

/**
 * com.alibaba.fastjson.JSON.
 *
 * @Description JSON工具类
 * @author chenhao
 * @date 2017年6月2日 上午10:19:00
 *
 */
public class JsonAliUtils {
  protected static final Logger LOGGER = Logger.getLogger(JsonAliUtils.class);

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