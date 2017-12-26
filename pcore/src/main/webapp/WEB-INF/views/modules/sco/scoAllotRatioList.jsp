<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
    <%@ include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/css/credit-module.css">
    <style>
        .table th {
            background: none;
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>创新学分分配比例</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form class="form-inline text-right">
        <input id="confId" name="confId" type="hidden" value="${confId}"/>
        <button type="button" class="btn-oe btn-primary-oe"
                onclick="javascript:location.href='${ctx}/sco/scoAllotRatio/form?confId=${confId}'">创建学分配比
        </button>
        <button type="button" class="btn" onclick="javascript:location.href='${ctx}/sco/scoAffirmConf'">返回</button>
    </form>
    <table class="table table-bordered table-vertical-middle table-hover table-theme-default">
        <thead>
        <tr>
            <th width="64">组人数</th>
            <th class="none-wrap" width="150">学分分配比例</th>
            <th>备注</th>
            <th width="120">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${list}" var="allot">
            <tr>
                <td>${allot.number }</td>
                <td class="none-wrap">${allot.ratio }</td>
                <td class="text-left">
                        ${allot.remarks }
                </td>
                <td>
                    <button class="btn-oe btn-primary-oe btn-sm-oe" type="button"
                            onclick="javascript:location.href='${ctx}/sco/scoAllotRatio/form?id=${allot.id}'">编辑
                    </button>
                    <button class="btn btn-handle-table btn-small" type="button"
                            onclick="javascript:return confirmx('确认要删除吗？', function(){location.href='${ctx}/sco/scoAllotRatio/delete?id=${allot.id}&affirmConfId=${confId}'})">
                        删除
                    </button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>