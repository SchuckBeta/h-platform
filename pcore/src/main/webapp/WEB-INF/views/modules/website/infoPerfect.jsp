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
		<script type="text/javascript">
			
			$(function(){
				$('#teaPage').on('click', function(e){
					var id = $(this).attr('data-id');
					location.href="/f/sys/frontTeacherExpansion/form?custRedict=1&id="+id;
					return false;
				})
				$('#stPage').on('click', function(e){
					location.href="/f/sys/frontStudentExpansion/findUserInfoById?custRedict=1";
					return false;
				})
			})
		</script>
	</head>
	<body>
		<!--顶部header公用部分-->
		<div class="wholebox">
			<div class="content">
				<div class="mainbox">
					<h5>信息完善</h5>
					<div class="box">
						<div class="redtop"></div>
						<div class="fillboxone">
							<img src="/img/u4110.png" alt="" />
							<!-- <p>您的基本信息还未完善</p> -->
							<c:if test="${userType=='1' }">
								<form id="userForm" class="form-signin" action="#" method="post">
									<button id="stPage" type="button">完善基本信息</button>
								</form>
							</c:if>
							<c:if test="${userType=='2' }">
								<form id="userForm" class="form-signin" action="#" method="post">
									<button id="teaPage" type="button" data-id="${teaEid }">完善基本信息</button>
								</form>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
