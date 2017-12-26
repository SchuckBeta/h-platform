<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>用户管理</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <link rel="stylesheet" type="text/css" href="/static/common/tablepage.css"/>
    <link href="${ctxStatic}/jquery-jbox/2.3/Skins/Bootstrap/jbox.min.css" rel="stylesheet" />
    <script src="${ctxStatic}/jquery-jbox/2.3/jquery.jBox-2.3.min.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
            $("#btnExport").click(function () {
                top.$.jBox.confirm("确认要导出用户数据吗？", "系统提示", function (v, h, f) {
                    if (v == "ok") {
                        $("#searchForm").attr("action", "${ctx}/sys/user/export");
                        $("#searchForm").submit();
                    }
                }, {buttonsFocus: 1});
                top.$('.jbox-body .jbox-icon').css('top', '55px');
            });
            $("#btnImport").click(function () {
                $.jBox($("#importBox").html(), {
                    title: "导入数据", buttons: {"关闭": true},
                    bottomText: "导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"
                });
            });
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            var grade = $('#findGrade', parent.document).html();
            var id = $('#findId', parent.document).html();
            var ids = $('#ids', parent.document).html();
            var teacherType = $('#teacherType', parent.document).html();
            var userName = $('#username', parent.document).val();
            var currState = $("#currState", parent.document).val();
            var arrs = new Array();
            $("input[name='curJoinStr']:checkbox", parent.document).each(function () {
                if ($(this).attr("checked")) {
                    arrs.push($(this).val());
                }
            });
            if (grade == '1') {
                $('#searchForm').attr(
                        "action",
                        "${ctx}/sys/user/backUserListTree?ids="+ids+"&userType=${userType}&userName=" + userName + "&teacherType=" + teacherType + "&currState=" + currState + "&curJoinStr=" + arrs.join(","));
            } else if (grade == '2') {
                $('#searchForm').attr(
                        "action",
                        "${ctx}/sys/user/backUserListTree?ids="+ids+"&office.id=" + id
                        + "&userType=" + ${userType} +"&grade=2&userName=" + userName + "&teacherType=" + teacherType + "&currState=" + currState + "&curJoinStr=" + arrs.join(","));
            } else if (grade == '3') {
                $('#searchForm').attr(
                        "action",
                        "${ctx}/sys/user/backUserListTree?ids="+ids+"&professionId=" + id
                        + "&userType=" + ${userType} +"&grade=3&userName=" + userName + "&teacherType=" + teacherType + "&currState=" + currState + "&curJoinStr=" + arrs.join(","));
            } else {
                $('#searchForm').attr(
                        "action",
                        "${ctx}/sys/user/backUserListTree?ids="+ids+"&userType=${userType}&userName=" + userName + "&teacherType=" + teacherType + "&currState=" + currState + "&curJoinStr=" + arrs.join(","));
            }
            $("#searchForm").submit();
            return false;
        }
    </script>
    <style>
        .table thead tr th{
            white-space: nowrap;
            text-align: center;
            vertical-align: middle;
        }
        .table tbody tr td{
            text-align: center;
            white-space: nowrap;
            vertical-align: middle;
        }
    </style>
</head>
<body>

<form:form id="searchForm" style="display:none" modelAttribute="user" action="${ctx}/sys/user/backUserListTree"
           method="post" class="breadcrumb form-search ">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
</form:form>
<table id="contentTable" class="table  table-bordered table-condensed table-theme-default">
    <thead>
    <tr>
        <th><input type="checkbox" id="check_all" data-flag="false"></th>
        <th>姓名</th>
        <c:if test="${user.userType=='1'}">
        <th>学号</th>
        </c:if>
        <c:if test="${user.userType=='2'}">
        <th>工号</th>
        </c:if>
        <c:if test="${user.userType=='1'}">
        <th>现状</th>
        <th>当前在研</th>
        </c:if>
        <c:if test="${user.userType=='2'}">
        <th>当前指导</th>
        </c:if>
        <th>技术领域</th>
        <c:if test="${user.userType=='2'}">
        <th>导师来源</th>
        </c:if>
    <tbody>
    <c:forEach items="${page.list}" var="user">
        <tr>
            <td class="checkone"><input type="checkbox" value="${user.id}" name="boxTd"></td>
            <td>${user.name}</td>
            <td>${user.no}</td>
            <c:if test="${user.userType=='1'}">
                <td>${user.currStateStr}</td>
                <td>${user.curJoin}</td>
            </c:if>
            <c:if test="${user.userType=='2'}">
                <td>${user.curJoin}</td>
            </c:if>
            <td>${user.domainlt}</td>
            <c:if test="${user.userType=='2'}">
                <td>
                    <c:if test="${user.userType=='1'}">
                        ${fns:getDictLabel(user.userType, 'sys_user_type', '')}
                    </c:if>
                    <c:if test="${user.userType=='2' && user.teacherType=='1'}">
                        校内导师
                    </c:if>
                    <c:if test="${user.userType=='2' && user.teacherType=='2'}">
                        企业导师
                    </c:if>
                </td>
            </c:if>
        </tr>
    </c:forEach>
    </tbody>
</table>
${page.footer}
<script src="/js/userTreeList/userTreeList.js"></script>
</body>
</html>