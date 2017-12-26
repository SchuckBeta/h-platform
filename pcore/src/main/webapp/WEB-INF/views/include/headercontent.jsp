<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
		<!--------顶部------->
		<div class="top">
			<ul>
				<li><a href="/a/logout"><i class="icon-signout"></i> 退出</a></li>
				<li><a href="/a/sysMenuIndex"><i class="icon-reply-all"></i> 返回首页 </a></li>
				<li><a href="#"><i class="icon-user"></i> ${fns:getUser().name}</a></li>
				<!-- <li><span></span><a href="#">提醒通知</a></li> -->
			</ul>
		</div>
		<div id="dialog-message" title="信息" style="display: none;">
		  <p id="dialog-content"></p>
		</div>