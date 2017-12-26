<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title></title>
    <%@ include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/css/credit-module.css">
    <style>
        .table-thead-bg thead tr{
            background-color: #f4e6d4;
        }
        .table th{
            background: none;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
        });
        function page(n, s) {
            location.href = "/a/auditstandard/auditStandard?pageNo=" + n + "&pageSize=" + s;
        }
    </script>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>评审标准管理</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <c:if test="${not empty message}">
        <c:if test="${not empty type}"><c:set var="ctype" value="${type}"/></c:if>
        <c:if test="${empty type}"><c:set var="ctype"
                                          value="${fn:indexOf(message,'失败') eq -1?'success':'error'}"/></c:if>
        <div class="alert alert-${ctype}">
            <button data-dismiss="alert" class="close">×</button>
                ${message}</div>
    </c:if>
    <form:form id="form-search-bar" class="form-inline" cssStyle="margin-bottom: 20px; overflow: hidden">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <div class="form-inline-right form-inline-right-w240">
            <button type="button" class="btn-oe btn-primary-oe"
                    onclick="javascript:location.href='${ctx}/auditstandard/auditStandard/form?id=${conf.id}'">新建评审标准
            </button>
            <button type="button" class="btn-oe btn-primary-oe"
                    onclick="javascript:location.href='${ctx}/auditstandard/auditStandard/listFlow'">关联项目
            </button>
        </div>
    </form:form>
    <table class="table table-bordered table-hover table-theme-default table-vertical-middle">
        <thead>
        <tr>
            <th width="150">评审标准</th>
            <th>评审标准说明</th>
            <th width="100">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="sta">
            <tr>
                <td>${sta.name }</td>
                <td>${sta.remarks }</td>
                <td>
                    <button class="btn-oe btn-primary-oe btn-sm-oe btn-link-oe"
                       onclick="javascript:location.href='${ctx}/auditstandard/auditStandard/form?id=${sta.id}'">编辑</button>
                    <button type="button" class="btn-oe btn-default-oe btn-sm-oe"
                       onclick="javascript:return confirmx('确认要删除吗？', function(){location.href='${ctx}/auditstandard/auditStandard/delete?id=${sta.id}'})">删除</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    ${page.footer}
</div>
</body>
</html>