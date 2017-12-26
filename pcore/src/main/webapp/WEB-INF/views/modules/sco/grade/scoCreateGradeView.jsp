<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title></title>
    <%@ include file="/WEB-INF/views/include/backtable.jsp"%>
    <link rel="stylesheet" type="text/css" href="/css/credit-module.css">
    <style>
        .table th{
            background: none;
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>查看学分详情</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <table class="table table-bordered table-has-caption table-hover table-vertical-middle">
        <caption>${projectName}</caption>
        <thead>
        <tr>
            <th class="none-wrap">学号</th>
            <th class="none-wrap">姓名</th>
            <th class="none-wrap">职责</th>
            <th class="none-wrap">任务权重</th>
            <th class="none-wrap">学分配比</th>
            <th class="none-wrap">认定学分</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach  items="${scoProjectVoList}" var="scoProjectVo">
        <tr>
            <td class="none-wrap">${scoProjectVo.user.no}</td>
            <td class="none-wrap">${scoProjectVo.user.name}</td>
            <td class="none-wrap">
                <c:choose>
                    <c:when test="${scoProjectVo.user.id eq scoProjectVo.projectDeclare.leader}">
                    项目负责人
                    </c:when>
                    <c:otherwise>
                    项目成员
                    </c:otherwise>
                </c:choose>
            </td>
            <td class="none-wrap">${scoProjectVo.weightVal}</td>
            <td class="none-wrap">
                <c:if test="${scoProjectVo.percent<1}">
                    <fmt:formatNumber type="percent" minFractionDigits="0" value="${scoProjectVo.percent}" />
                </c:if>
            </td>
            <td class="none-wrap">
                <c:if test="${scoProjectVo.percent<1}">
                     ${fns:saveNum(scoProjectVo.scoAffirm.scoreVal*scoProjectVo.percent,1)}
                </c:if>
            </td>
        </tr>
        </c:forEach>
        </tbody>
    </table>
    <div class="text-center">
        <button type="button" class="btn-oe btn-primary-oe" onclick="history.go(-1)">返回</button>
    </div>
</div>


</body>
</html>