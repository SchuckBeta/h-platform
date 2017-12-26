<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>专业管理</title>
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/static/common/tablepage.css"/>
    <link href="${ctxStatic}/jquery-jbox/2.3/Skins/Bootstrap/jbox.min.css" rel="stylesheet"/>
    <script src="${ctxStatic}/jquery-jbox/2.3/jquery.jBox-2.3.min.js" type="text/javascript"></script>
    <script type="text/javascript">

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            //$("#searchForm").attr("action","${ctxFront}/sys/user/userListTreePublish?userType=${userType}");
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<%--<form:form id="searchForm" style="display:none" modelAttribute="office"--%>
<%--action="${ctxFront}/sys/user/userListTreePublish?userType=${userType}" method="post"--%>
<%--class="breadcrumb form-search ">--%>
<%--<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>--%>
<%--<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>--%>
<%--<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>--%>
<%--<ul class="ul-form">--%>
<%--<li class="btns">--%>
<%--<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"--%>
<%--onclick="return page();"/>--%>
<%--</li>--%>
<%--<li class="clearfix"></li>--%>
<%--</ul>--%>
<%--</form:form>--%>
<%--<sys:message content="${message}"/>--%>
<div style="padding-right: 15px">
    <table id="contentTable" class="table  table-bordered table-condensed" style="width: 100%;margin-bottom: 0;">
        <thead>
        <tr>
            <th><input type="checkbox" id="check_all" data-flag="false"></th>
            <th>专业</th>
        <tbody>
        <c:forEach items="${professionList}" var="office">
            <tr>
                <td class="checkone"><input type="checkbox" value="${office.id}" name="boxTd"></td>
                <td>${office.name}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%--${page.footer}--%>
<script src="/js/userTreeList/userTreeList.js"></script>
</body>
</html>