<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>学院管理员、学院秘书结项审核</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <script src="/js/gcProject/download.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
        });
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>

<div class="container-fluid container-fluid-oe">
    <div class="edit-bar edit-bar-tag clearfix">
        <div class="edit-bar-left">
            <span>结项审核</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form id="searchForm" modelAttribute="act" action="${ctx}/state/secCloseSearch" method="post"
               class="form-top-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <div class="search-form-wrap form-inline">
            <button id="btnSubmit" class="btn btn-back-oe btn-primaryBack-oe" type="submit">查询</button>
        </div>
        <div class="condition-main form-horizontal">
            <div class="condition-row">
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['number']" class="control-label">项目编号</label>
                        <div class="controls">
                            <form:input type="text" path="map['number']" cssClass="form-control input-medium"/>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['name']" class="control-label">项目名称</label>
                        <div class="controls">
                            <form:input type="text" path="map['name']" cssClass="form-control input-medium"/>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['type']" class="control-label">项目类型</label>
                        <div class="controls">
                            <form:select path="map['type']" cssClass="form-control input-medium">
                                <form:option value="" label="所有项目类型"/>
                                <form:options items="${fns:getDictList('project_type')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['level']" class="control-label">项目级别</label>
                        <div class="controls">
                            <form:select path="map['level']" cssClass="form-control input-medium">
                                <form:option value="" label="请选择"/>
                                <form:options items="${fns:getDictList('project_degree')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label path="map['leader']" class="control-label">负责人</label>
                        <div class="controls">
                            <form:input type="text" path="map['leader']" cssClass="form-control input-medium"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable" class="table  table-bordered table-condensed table-hover table-theme-default table-center">
        <thead>
        <tr>
            <th>项目编号</th>
            <th>项目名称</th>
            <th width="72">项目类型</th>
            <th width="90">负责人</th>
            <th>项目组成员</th>
            <th width="72">指导老师</th>
            <th width="72">项目级别</th>
            <th>项目审核状态</th>
            <th class="white-nowrap">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="act">
            <tr>
                <td>
                        ${act.status == "other"? act.projectDeclare.number :act.vars.map.number} <%--${act.vars.map.number}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.name :act.vars.map.name} <%--${act.vars.map.name}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.typeString :fns:getDictLabel(act.vars.map.type, "project_type", "")} <%--${fns:getDictLabel(act.vars.map.type, "project_type", "")}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.leaderString :act.vars.map.leader} <%--${act.vars.map.leader}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.snames :act.vars.map.teamList} <%--${act.vars.map.teamList}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.tnames :act.vars.map.teacher} <%-- ${act.vars.map.teacher}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.levelString :fns:getDictLabel(act.vars.map.level, "project_degree", "")} <%-- ${fns:getDictLabel(act.vars.map.level, "project_degree", "")}--%>
                </td>
                <td>
                    <c:if test="${act.status!='other'}">
                        <c:set var="projectDeclare" value="${pj:getProjectDeclareListVoById(act.vars.map.id)}" />
                        <%-- <a href="${ctx}/act/task/processMap2?proInsId=${act.histTask.processInstanceId}" target="_blank">${act.taskName}</a> --%>
                    	<a href="${ctx}/actyw/actYwGnode/designView/${projectDeclare.status_code}?groupId=${projectDeclare.groupId}" target="_blank">
                    	 ${pj:getAuditStatus(projectDeclare.status_code)}
                        </a>
                    </c:if>
                    <c:if test="${act.status=='other'}">
                        <c:set var="projectDeclare" value="${pj:getProjectDeclareListVoById(act.projectDeclare.id)}" />
                        <%-- <a href="${ctx}/act/task/processMap2?proInsId=${act.projectDeclare.procInsId}" target="_blank">待${act.taskName}</a> --%>
                    	<a href="${ctx}/actyw/actYwGnode/designView/${projectDeclare.status_code}?groupId=${projectDeclare.groupId}" target="_blank">
                    	 ${pj:getAuditStatus(projectDeclare.status_code)}</a>
                    </c:if>
                </td>
                <td>
                    <c:if test="${act.status!='other'}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small" href="${ctx}/state/infoView?id=${act.vars.map.id}&taskDefinitionKey=closeScore">查看</a>
                    </c:if>
                    <c:if test="${act.status=='other'}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small" href="${ctx}/state/projectDetail?id=${act.projectDeclare.id}"> 查看</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    ${page.footer}
</div>
</body>
</html>