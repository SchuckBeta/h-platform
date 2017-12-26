<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>自定义流程管理</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
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
    <style>
        .actyw-remark {
            display: block;
            max-width: 300px;
            overflow: hidden;
            margin: 0 auto;
            text-align: center;
        }
    </style>
</head>
<div class="container-fluid container-fluid-oe">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>自定义流程</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <ul class="nav nav-tabs" style="margin-bottom: 0">
        <li class="active"><a href="${ctx}/actyw/actYwGroup/">自定义流程列表</a></li>
        <li><a href="${ctx}/actyw/actYwGroup/form">自定义流程添加</a></li>
    </ul>
    <div style="padding-top: 20px; border: solid #ddd; border-width: 0 1px 0 1px" >
        <form:form id="searchForm" modelAttribute="actYwGroup" action="${ctx}/actyw/actYwGroup/" method="post"
                   class="breadcrumb form-search">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <ul class="ul-form">
                <li class="btns" style="float: right"><input id="btnSubmit" class="btn btn-back-oe btn-primaryBack-oe" type="submit" value="查询"/></li>
                <li><label>流程名称</label>
                    <form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
                </li>
                <li><label>发布状态</label>
                    <form:select path="status" class="input-medium">
                        <form:option value="" label="--请选择--"/>
                        <form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value"
                                      htmlEscape="false"/>
                    </form:select>
                </li>
                <li><label>流程类型</label>
                    <form:select path="flowType" class="input-medium">
                        <form:option value="" label="--请选择--"/>
                        <form:options items="${fns:getDictList('act_category')}" itemLabel="label" itemValue="value"
                                      htmlEscape="false"/>
                    </form:select>
                </li>
                <li><label>项目类型</label>
                    <form:select path="type" class="input-medium">
                        <form:option value="" label="--请选择--"/>
                        <form:options items="${fns:getDictList('act_project_type')}" itemLabel="label" itemValue="value"
                                      htmlEscape="false"/>
                    </form:select>
                </li>
            </ul>
        </form:form>
        <sys:message content="${message}"/>
        <div style="margin: 0 -1px;">
            <table class="table table-hover table-bordered table-condensed table-center table-theme-default">
                <thead>
                <tr>
                    <th>流程名称</th>
                    <th>发布状态</th>
                    <th>流程/表单类型</th>
                    <th>关联项目类型</th>
                    <th>备注</th>
                    <th>最后更新时间</th>
                    <shiro:hasPermission name="actyw:actYwGroup:edit">
                        <th>操作</th>
                    </shiro:hasPermission>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${page.list}" var="actYwGroup">
                    <tr>
                        <td title="${actYwGroup.keyss}">
                            <a href="${ctx}/actyw/actYwGroup/form?id=${actYwGroup.id}">
                                    ${actYwGroup.name}
                                <c:if test="${not empty actYwGroup.flowId}">
                                    <a href="${ctx}/act/process/resource/read?procDefId=${actYwGroup.flowId}&resType=xml"
                                       target="_blank"><i class="icon-eye-open" title="Xml"/></a>
                                    <a href="${ctx}/act/process/resource/read?procDefId=${actYwGroup.flowId}&resType=image"
                                       target="_blank"><i class="icon-picture" title="Image"/></a>
                                </c:if>
                            </a>
                        </td>
                        <td>${fns:getDictLabel(actYwGroup.status, 'yes_no', '')}</td>
                        <td>${fns:getDictLabel(actYwGroup.flowType, 'act_category', '')}</td>
                        <td>
                            <c:forEach var="item" items="${actYwGroup.types}" varStatus="idx">
                                ${fns:getDictLabel(item, 'act_project_type', '')}
                                <c:if test="${(idx.index + 1) ne fn:length(actYwGroup.types)}">/</c:if>
                            </c:forEach>
                        </td>
                        <td><span class="actyw-remark">${fns:abbr(actYwGroup.remarks, 60)}</span></td>
                        <td><fmt:formatDate value="${actYwGroup.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                        <shiro:hasPermission name="actyw:actYwGroup:edit">
                            <td>
                                <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                                   href="${ctx}/actyw/actYwGnode/${actYwGroup.id}/view">预览</a>
                                <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                                   href="${ctx}/actyw/actYwGroup/form?id=${actYwGroup.id}">修改</a>
                                <c:if test="${actYwGroup.status eq '0'}">
                                    <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                                       href="${ctx}/actyw/actYwGnode/designNew?group.id=${actYwGroup.id}&groupId=${actYwGroup.id}">配置</a>
                                    <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                                       href="${ctx}/actyw/actYwGroup/ajaxDeploy?id=${actYwGroup.id}&status=1&isUpdateYw=true"
                                       onclick="return confirmx('确认要发布方案[${actYwGroup.name}]吗？', this.href)">发布</a>
                                    <c:if test="${empty actYwGroup.actYws}">
                                        <a href="${ctx}/actyw/actYwGroup/delete?id=${actYwGroup.id}"
                                           onclick="return confirmx('确认要删除该自定义流程吗？', this.href)"
                                           class="btn btn-small">删除</a>
                                    </c:if>
                                </c:if>
                                <c:if test="${actYwGroup.status eq '1'}">
                                    <a class="btn btn-small"
                                       href="${ctx}/actyw/actYwGroup/ajaxDeploy?id=${actYwGroup.id}&status=0&isUpdateYw=true"
                                       onclick="return confirmx('确认要取消发布方案[${actYwGroup.name}]吗？', this.href)">取消发布</a>
                                </c:if>
                            </td>
                        </shiro:hasPermission>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    ${page.footer}
</div>

<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>
</body>
</html>