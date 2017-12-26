/**
 *
 */
package com.oseasy.initiate.modules.team.utils;

import com.oseasy.initiate.common.utils.SpringContextHolder;
import com.oseasy.initiate.modules.team.service.TeamUserRelationService;

/**
 * 内容管理工具类


 */
public class TeamUtils {
	private static TeamUserRelationService teamUserRelationService = SpringContextHolder.getBean(TeamUserRelationService.class);
	/**根据模板类型获取大赛结果html代码*/
	public static String getTeamStudentName(String teamId) {
		if(teamId!=null){
			return teamUserRelationService.getTeamStudentName(teamId);
		}
		return "";
	}
	/**根据模板类型获取大赛结果html代码*/
	public static String getTeamTeacherName(String teamId) {
		if(teamId!=null){
			return teamUserRelationService.getTeamTeacherName(teamId);
		}
		return "";
	}
}