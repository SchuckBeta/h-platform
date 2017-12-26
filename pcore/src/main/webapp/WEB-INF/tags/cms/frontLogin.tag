<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<%@ attribute name="user" type="com.oseasy.initiate.modules.sys.entity.User" required="true" description="用户" %>
<!--用户名-->
<input type="hidden" id="84bcd363e9b24e25b8aaad7bed3d1dcf_userid" value="${user.id}">
<c:if test="${empty loginPage}">
    <c:if test="${user.id==null}">
        <!-- <div class="user" style="margin-right: -80px; "> -->
        <div class="user" style="margin-right: 0px; ">
            <form id="userForm" class="form-signin" action="/f/logout" method="post">
                <a href="/f/toLogin">登录门户</a>
                <a href="/f/toRegister">注册</a>
                <a href="${fns:getSysAdminIp()}">登录后台</a>
                <input type="hidden" name="fromFront" value="1"/>
            </form>
        </div>
    </c:if>

    <c:if test="${user.id!=null}">
        <!-- <div class="user" style="margin-right: -170px;"> -->
         <div class="user" style="margin-right: 0px;">
            <form id="userForm" class="form-signin" action="/f/logout" method="post">
                <%--<a href="/f/sys/frontStudentExpansion/findUserInfoById"--%>
                   <%--style="color: #df4526; text-decoration: underline;">--%>
                    <%--<c:choose>--%>
                        <%--<c:when test="${not empty user.name}">--%>
                            <%--${user.name}--%>
                        <%--</c:when>--%>
                        <%--<c:when test="${not empty user.mobile}">--%>
                            <%--${user.mobile}--%>
                        <%--</c:when>--%>
                        <%--<c:otherwise>--%>
                            <%--${user.loginName}--%>
                        <%--</c:otherwise>--%>
                    <%--</c:choose>--%>
                     <%--<i class="icon-user"></i></a>--%>
                <a href="/f/oa/oaNotify/indexMyNoticeList"><i class="icon-bell"></i>消息(<i id="84bcd363e9b24e25b8aaad7bed3d1dcf_unread">0</i>)</a>
                <%--<span onclick="javascrip:$('#userForm').submit();"><i class="icon-signout"></i>退出</span>--%>
                    <div class="user-profiles" style="display: inline-block">
                    		<div class="user-pic-box">
                    			<a href="/f/sys/frontStudentExpansion/findUserInfoById?id=${user.id}">
                    				<c:choose>
                                        <c:when test="${user.photo!=null && user.photo!='' }">
                                            <img class="user-pic" src="${fns:ftpImgUrl(user.photo) }"/>
                                        </c:when>
                                        <c:otherwise>
                                            <img class="user-pic" src="/img/u4110.png"/>
                                        </c:otherwise>
                                    </c:choose>
                    			</a>
                    			<img class="arrow-right" src="/images/red-right.png" alt="">
                    			<img class="arrow-down" src="/images/red-down.png" alt="">
                    		</div>
                        <div class="drop-user-back">
                    		<div class="drop-user-info">
                    			<img class="arrow-up" src="/images/gary-up.png" alt="">
                    			<a href="/f/sys/frontStudentExpansion/findUserInfoById?id=${user.id}">
                    				<c:choose>
                                        <c:when test="${user.photo!=null && user.photo!='' }">
                                            <img class="user-pic-big" src="${fns:ftpImgUrl(user.photo) }"/>
                                        </c:when>
                                        <c:otherwise>
                                            <img class="user-pic-big" src="/img/u4110.png"/>
                                        </c:otherwise>
                                    </c:choose>
                    				<span class="name">
                                    <c:choose>
                    			                        <c:when test="${not empty user.name}">
                    			                            ${user.name}
                    			                        </c:when>
                    			                        <c:when test="${not empty user.mobile}">
                    			                            ${user.mobile}
                    			                        </c:when>
                    			                        <c:otherwise>
                    			                            ${user.loginName}
                    			                        </c:otherwise>
                    			                    </c:choose></span>
                    				<span class="detail" style="color:#B4B4B4">${user.office.name}</span>
                    			</a>

                    			<ul>
                    				<li><a href="${ctxFront}/sys/frontStudentExpansion/frontUserPassword">修改密码</a></li>
                    				<li><a href="javascript:$('#userForm').submit();">退出登录</a></li>
                    			</ul>
                    		</div>
                        </div>

                    </div>
                    <input type="hidden" name="fromFront" value="1"/>
            </form>
        </div>
    </c:if>
</c:if>
<c:if test="${not empty loginPage}">
    <div class="user" style="margin-right: 0px;">
        <a href="/f/toRegister">注册</a>
        <a href="${fns:getSysAdminIp()}">登录后台</a>
    </div>
</c:if>
<script>
$(function(){
	getUnreadCountForHead();
});
function getUnreadCountForHead(){
	$.ajax({
		type:'post',
		url:'/f/oa/oaNotify/getUnreadCount',
		dataType: "json",
		success:function(data){
				$("#84bcd363e9b24e25b8aaad7bed3d1dcf_unread").html(data.count);
		}
	});
}
</script>