/**
 *
 */
package com.hch.platform.putil.common.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;


/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类


 */
public class StringUtil extends StringUtils {

    public static final String EMPTY = "";
    public static final String MAOH = ":";
    public static final String DOTH = ",";
    public static final String KUOHDLR = "{}";
    public static final String KUOHDL = "{";
    public static final String KUOHDR = "}";
    public static final String KUOHZLR = "[]";
    public static final String KUOHZL = "[";
    public static final String KUOHZR = "]";
    public static final String KUOHL = "(";
    public static final String KUOHR = ")";
    public static final String SHUX = "|";
    public static final char SEPARATOR = '_';
    public static final String ISO8859_1 = "ISO8859-1";
    public static final String UTF_8 = "UTF-8";
    public static final String DOT = ".";
    public static final String LINE = "/";
    public static final String LOG = ".log";
    public static final String LINE_M = "-";
    public static final String LINE_D = "_";
    public static final String CHARSET_NAME = "UTF-8";
    public static String getString(Object o){
		if(o==null){
			return "";
		}else{
			if(o instanceof BigDecimal){
				return ((BigDecimal)o).intValue()+"";
			}else{
				return o.toString();
			}
		}
	}
    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static byte[] getBytes(String str) {
    	if (str != null) {
    		try {
				return str.getBytes(CHARSET_NAME);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
    	}else{
    		return null;
    	}
    }

    /**
	 * 转换为Boolean类型
	 * 'true', 'on', 'y', 't', 'yes' or '1' (case insensitive) will return true. Otherwise, false is returned.
	 */
	public static Boolean toBoolean(final Object val) {
		if (val == null) {
			return false;
		}
		return BooleanUtils.toBoolean(val.toString()) || "1".equals(val.toString());
	}

    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static String toString(byte[] bytes) {
    	try {
			return new String(bytes, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			return EMPTY;
		}
    }

    /**
	 * 如果对象为空，则使用defaultVal值
	 * 	see: ObjectUtils.toString(obj, defaultVal)
	 * @param obj
	 * @param defaultVal
	 * @return
	 */
    public static String toString(final Object obj, final String defaultVal) {
    	 return obj == null ? defaultVal : obj.toString();
    }

    /**
     * 是否包含字符串
     * @param str 验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... strs) {
    	if (str != null) {
        	for (String s : strs) {
        		if (str.equals(trim(s))) {
        			return true;
        		}
        	}
    	}
    	return false;
    }

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)) {
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceEscapeHtml(String html) {
		if (isBlank(html)) {
			return "";
		}
		html = StringEscapeUtils.unescapeHtml4(html);
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}

	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * @param html
	 * @return
	 */
	public static String replaceMobileHtml(String html) {
		if (html == null) {
			return "";
		}
		return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
	}

	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * @param txt
	 * @return
	 */
	public static String toHtml(String txt) {
		if (txt == null) {
			return "";
		}
		return replace(replace(Encodes.escapeHtml(txt), "\n", "<br/>"), "\t", "&nbsp; &nbsp; ");
	}

	public static String unescapeHtml3(String txt) {
		String str1= StringEscapeUtils.unescapeHtml3(txt);
		return replace(replace(str1,"\r",""),"\n","");
	}

	public static Boolean contains(String src, String csrc) {
	  if (StringUtil.isEmpty(src)) {
      return false;
    }
	  if (StringUtil.isEmpty(csrc)) {
      return false;
    }
	  int idx= src.indexOf(csrc);
	  return (idx == -1)?false:true;
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String abbr2(String param, int length) {
		if (param == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		int n = 0;
		char temp;
		boolean isCode = false; // 是不是HTML代码
		boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;
		for (int i = 0; i < param.length(); i++) {
			temp = param.charAt(i);
			if (temp == '<') {
				isCode = true;
			} else if (temp == '&') {
				isHTML = true;
			} else if (temp == '>' && isCode) {
				n = n - 1;
				isCode = false;
			} else if (temp == ';' && isHTML) {
				isHTML = false;
			}
			try {
				if (!isCode && !isHTML) {
					n += String.valueOf(temp).getBytes("GBK").length;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			if (n <= length - 3) {
				result.append(temp);
			} else {
				result.append("...");
				break;
			}
		}
		// 取出截取字符串中的HTML标记
		String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)",
				"$1$2");
		// 去掉不需要结素标记的HTML标记
		temp_result = temp_result
				.replaceAll(
						"</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
						"");
		// 去掉成对的HTML标记
		temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>",
				"$2");
		// 用正则表达式取出标记
		Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
		Matcher m = p.matcher(temp_result);
		List<String> endHTML = Lists.newArrayList();
		while (m.find()) {
			endHTML.add(m.group(1));
		}
		// 补全不成对的HTML标记
		for (int i = endHTML.size() - 1; i >= 0; i--) {
			result.append("</");
			result.append(endHTML.get(i));
			result.append(">");
		}
		return result.toString();
	}

	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val) {
		if (val == null) {
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val) {
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val) {
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val) {
		return toLong(val).intValue();
	}

	/**
	 * 获得用户远程地址
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String remoteAddr = request.getHeader("X-Real-IP");
        if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("X-Forwarded-For");
        }else if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("Proxy-Client-IP");
        }else if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
	}

	/**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld"
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld"
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld"
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 转换为JS获取对象值，生成三目运算返回结果
     * @param objectString 对象串
     *   例如：row.user.id
     *   返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
     */
    public static String jsGetVal(String objectString) {
    	StringBuilder result = new StringBuilder();
    	StringBuilder val = new StringBuilder();
    	String[] vals = split(objectString, ".");
    	for (int i=0; i<vals.length; i++) {
    		val.append("." + vals[i]);
    		result.append("!"+(val.substring(1))+"?'':");
    	}
    	result.append(val.substring(1));
    	return result.toString();
    }


	public static String getSuffixValue(String suffix) {
		String extname = "unknow";
		if (equals(suffix, "xls") || equals(suffix, "xlsx")) {
			extname = "excel";
		} else if (equals(suffix, "doc") || equals(suffix, "docx")) {
			extname = "word";
		} else if (equals(suffix, "ppt") || equals(suffix, "pptx")) {
			extname = "ppt";
		} else if (equals(suffix, "jpg") || equals(suffix, "jpeg") || equals(suffix, "gif") || equals(suffix, "png") || equals(suffix, "bmp")) {
			extname = "image";
		} else if (equals(suffix, "rar") || equals(suffix, "zip") || equals(suffix, "txt") || equals(suffix, "zip") || equals(suffix, "project")) {
			extname = suffix;
		}
		return extname;
	}

	/**
	 * 判断能否整除, 可设定误差范围.
	 * @param dividend 被除数
	 * @param divisor 除数
	 * @return Boolean
	 */
  public static Boolean exeDivision(Integer dividend, Integer divisor) {
    return exeDivision(dividend, divisor, null);
  }

  /**
   * 判断能否整除, 可设定误差范围.
   * @param dividend 被除数
   * @param divisor 除数
   * @return Boolean
   */
  public static Boolean exeDivision(String dividend, Integer divisor) {
    return exeDivision(dividend, divisor, null);
  }

  /**
   * 判断能否整除, 可设定误差范围.
   * @param dividend 被除数
   * @param divisor 除数
   * @param deviation 误差+-
   * @return Boolean
   */
  public static Boolean exeDivision(Integer dividend, Integer divisor, Integer deviation) {
    if(deviation == null){
      return (dividend / divisor >= 1) && (dividend % divisor == 0);
    }else{
      return (dividend / divisor >= 1) && (dividend % divisor >= -deviation) && (dividend % divisor <= deviation);
    }
  }

  /**
   * 判断能否整除, 可设定误差范围.
   * @param dividend 被除数
   * @param divisor 除数
   * @param deviation 误差+-
   * @return Boolean
   */
  public static Boolean exeDivision(String dividend, Integer divisor, Integer deviation) {
    if(dividend != null){
      int intDividend = Integer.valueOf(dividend);
      return exeDivision(intDividend, divisor, deviation);
    }
    return false;
  }

  /**
   * 移除末尾的符号.
   * @param num 被处理的字符
   * @param ibyte 保留num的长度位数字
   * @return
   */
  public static String removeLastDotH(String str) {
      return removeLastDotH(str, DOTH);
  }
  /**
   * 移除末尾的符号.
   * @param str
   * @return
   */
  public static String removeLastDotH(String str, String dt) {
    if(isNotEmpty(str) && (str).endsWith(dt)){
      return str.substring(0, str.length()-1);
    }
    return str;
  }

  /**
   * 不够位数的在前面补0，保留num的长度位数字.
   * @param num 被处理的字符
   * @param ibyte 保留num的长度位数字
   * @return
   */
  public static String autoGenZeroLong(int ibyte, String numStr) {
    return autoGenZero(ibyte, Long.parseLong(numStr));
  }

  /**
   * 不够位数的在前面补0，保留num的长度位数字.
   * @param num 被处理的字符
   * @param ibyte 保留num的长度位数字
   * @return
   */
  public static String autoGenZero(int ibyte, Long num) {
    /**
     * 保留num的位数
     * 0 代表前面补充0
     * num 代表长度为4
     * d 代表参数为正数型
     */
    return String.format("%0" + ibyte + "d", num);
  }

  /**
   * 列表转sql
   * @param ids sql字符
   * @return
   * @author Chenh
   * @date 2016年5月27日 下午2:52:59
   */
  public static String sqlInByList(List<String> ids) {
    StringBuffer sb = new StringBuffer();
    boolean isFirst = true;
    for (String id : ids) {
      if(isFirst){
        sb.append("'"+id+"'");
        isFirst = false;
      }else{
        sb.append(",'"+id+"'");
      }
    }
    return sb.toString();
  }

  /**
   * 字符串转sql
   * @param ids sql字符
   * @return
   * @author Chenh
   * @date 2016年5月27日 下午2:52:59
   */
  public static String sqlInByStr(String ids) {
    return sqlInByStr(ids, StringUtil.DOTH);
  }

  /**
   * 字符串转sql
   * @param ids sql字符
   * @param spilt 分隔符
   * @return
   * @author Chenh
   * @date 2016年5月27日 下午2:52:59
   */
  public static String sqlInByStr(String ids, String spilt) {
    if(isEmpty(spilt)){
      spilt = StringUtil.DOTH;
    }
    return sqlInByList(Arrays.asList((ids).split(spilt)));
  }

  public static String getStr(String str){
   return isEmpty(str) ? "" : str;
  }
	public static String getFloatStr(Float flt){
   return (flt == null) ? "0.0" : flt.toString();
  }

	public static void main(String[] args) {
//    JsonAliUtils.writeBean(SvalPw.ENTER_EXPIRE_LOGFILE + StringUtil.LINE + DateUtil.formatDate(new Date(), DateUtil.FMT_YYYY_MM_DD_HH) + StringUtil.LOG,
//        JSONObject.fromObject(new PwEnterExpireVo()));
  }
}
