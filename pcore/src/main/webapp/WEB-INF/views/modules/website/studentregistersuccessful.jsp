<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
	<head>
	<meta name="decorator" content="site-decorator"/>
		<meta charset="UTF-8">
		<!--公用重置样式文件-->
		<link rel="stylesheet" type="text/css" href="/common/common-css/common.css" />
		<!--头部导航公用样式-->
		<link rel="stylesheet" type="text/css" href="/common/common-css/header.css" />
		<!--学生登录页面样式表-->
		<link rel="stylesheet" type="text/css" href="/css/studentregistersuccessful.css"/>
		<title>${frontTitle}</title>
	    <script src="/common/common-js/jquery.min.js" type="text/javascript" charset="utf-8"></script>
		
	</head>
	<body>
		<!--顶部header公用部分-->
		<div class="wholebox">
			<div class="content">
				<div class="mainbox">
					<h5>学生注册账号</h5>
					<div class="box">
						<div class="redtop"></div>
						<div class="fillboxone">
							<img src="/img/u4110.png" alt="" />
							<p>恭喜你，成功注册“开创啦”！</p>
							<form id="userForm" class="form-signin" action="/f/sys/frontStudentExpansion/findUserInfoById" method="post">
								<button onclick="javascirpt:$('#userForm').submit();">完善基本信息</button>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
