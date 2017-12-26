package com.hch.platform.pcore.test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hch.platform.pcore.common.utils.FtpUtil;

public class RegexTest {
	public static void main(String[] args) {
		
//		String content="fsdfdsfdf<img src=\"img/Video.png1?r=''\" />fdfdsfdsf<img name='' src='img/Video.png2' />fsdfsd<img src='img/Video.png3' />fdsfsd";
		String content="<video class=\"edui-upload-video  vjs-default-skin video-js\" controls=\"\" preload=\"none\" width=\"420\" height=\"280\" src=\"http://192.168.0.105:38888/oseasy/temp/excellentContent/2017-08-11/c0d5cd5375f249febcd05a6f54564695.mp4?fileSize=16799736&fielTitle=test.mp4&fielType=mp4&fielFtpUrl=/tool/oseasy/temp/excellentContent/2017-08-11/c0d5cd5375f249febcd05a6f54564695.mp4\" data-setup=\"{}\">"
        +"<source src=\"http://192.168.0.105:38888/oseasy/temp/excellentContent/92017-08-11/c0d5cd5375f249febcd05a6f54564695.mp4?fileSize=16799736&fielTitle=test.mp4&fielType=mp4&fielFtpUrl=/tool/oseasy/temp/excellentContent/2017-08-11/c0d5cd5375f249febcd05a6f54564695.mp4\" type=\"video/mp4\"/></video>";
		showSet(getImgSet(content));
	}
	public static void showSet(Set<String> set){
		for(String s:set){
			System.out.println(s);
		}
	}
	private static Set<String> getImgSet(String content){
		Set<String> set=new HashSet<String>();
//        String regxpForTag = "<img\\s+[^>]*src=\\s*(['\"])([^>]*(?=\\1))[^>]*>" ;  
		String regxpForTag = "(['\"])("+FtpUtil.ftpImgUrl("/tool/oseasy/temp/")+"(.(?!\\1))*.)\\1";
        Pattern patternForTag = Pattern.compile (regxpForTag,Pattern. CASE_INSENSITIVE );  
        Matcher matcherForTag = patternForTag.matcher(content);  
        while (matcherForTag.find()) {  
        	set.add(matcherForTag.group(2));  
        }
        return set;
	}
}
