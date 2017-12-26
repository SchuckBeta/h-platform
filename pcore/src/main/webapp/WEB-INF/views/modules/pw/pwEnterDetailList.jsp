<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${backgroundTitle}</title>
    <%@include file="/WEB-INF/views/include/backcyjd.jsp" %>
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
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>${pwEnterDetail.pename}入驻管理</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <ul class="nav nav-tabs nav-tabs-default">
        <li class="active"><a href="${ctx}/pw/pwEnterDetail/list?petype=${pwEnterDetail.petype}">${pwEnterDetail.pename}入驻申报列表</a>
        </li>
        <%-- <shiro:hasPermission name="pw:pwEnterDetail:edit"><li><a href="${ctx}/pw/pwEnterDetail/form?petype=${pwEnterDetail.petype}">${pwEnterDetail.pename} 入驻申报添加</a></li></shiro:hasPermission> --%>
    </ul>
    <div class="tab-content-default">
        <form:form id="searchForm" modelAttribute="pwEnterDetail"
                   action="${ctx}/pw/pwEnterDetail/list?petype=${pwEnterDetail.petype}" method="post"
                   class="form-horizontal clearfix form-search-block">
            <input id="type" name="type" type="hidden" value="${type}"/>
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <div class="col-control-group">
                <div class="control-group">
                    <label class="control-label">入驻编号</label>
                    <div class="controls">
                        <form:input path="pwEnter.no" htmlEscape="false" maxlength="255" class="input-medium"/>
                    </div>
                </div>
                <c:if test="${not empty pwEnterDetail.petype}">
                    <c:if test="${pwEnterDetail.petype eq 0}">
                        <div class="control-group">
                            <label class="control-label">团队名称</label>
                            <div class="controls">
                                <form:input path="team.name" htmlEscape="false" maxlength="255" class="input-medium"/>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${pwEnterDetail.petype eq 1}">
                        <div class="control-group">
                            <label class="control-label">项目名称</label>
                            <div class="controls">
                                <form:input path="project.name" htmlEscape="false" maxlength="255"
                                            class="input-medium"/>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${pwEnterDetail.petype eq 2}">
                        <div class="control-group">
                            <label class="control-label">企业名称</label>
                            <div class="controls">
                                <form:input path="pwCompany.name" htmlEscape="false" maxlength="255"
                                            class="input-medium"/>
                            </div>
                        </div>
                    </c:if>
                </c:if>
                <div class="control-group">
                    <label class="control-label">负责人</label>
                    <div class="controls">
                        <form:input path="pwEnter.applicant.name" htmlEscape="false" maxlength="255"
                                    class="input-medium"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">所属院校</label>
                    <div class="controls">
                        <form:input path="pwEnter.applicant.office.name" htmlEscape="false" maxlength="255"
                                    class="input-medium"/>
                    </div>
                </div>
                <%-- <div class="control-group">
                    <label class="control-label">学号</label>
                    <div class="controls">
                        <form:input path="pwEnter.applicant.no" htmlEscape="false" maxlength="255"
                                    class="input-medium"/>
                    </div>
                </div> --%>
                    <%-- <li><label>类型</label>
                        <form:select id="type" path="type" class="input-medium">
                            <form:option value="" label="--请选择--"/>
                            <form:options items="${fns:getDictList('pw_enter_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                        </form:select>
                    </li> --%>
            </div>
            <div class="search-btn-box">
                <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
            </div>
        </form:form>
        <sys:message content="${message}"/>
        <table id="contentTable"
               class="table table-bordered table-condensed table-hover table-center table-orange table-sort table-nowrap table-subscribe">
            <thead>
            <tr>
                <th>入驻编号</th>
                <th>
                    <c:if test="${not empty pwEnterDetail.petype}">
                        <c:if test="${pwEnterDetail.petype eq 0}">团队名称</c:if>
                        <c:if test="${pwEnterDetail.petype eq 1}">项目名称</c:if>
                        <c:if test="${pwEnterDetail.petype eq 2}">企业名称</c:if>
                    </c:if>
                </th>
                <th>负责人</th>
                <th>所属院校</th>
                <!-- <th>学号</th> -->
                <th>周期</th>
                <th>状态</th>
                <%--<th>最后更新时间</th>--%>
                <%--<th>备注</th>--%>
                <%--<shiro:hasPermission name="pw:pwEnterDetail:edit"><th>操作</th></shiro:hasPermission>--%>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="cpwEnterDetail">
                <tr>
                    <td><a href="${ctx}/pw/pwEnter/form?id=${cpwEnterDetail.pwEnter.id}&isView=true">
                            ${cpwEnterDetail.pwEnter.no}
                    </a></td>
                    <td>
                        <c:if test="${pwEnterDetail.petype eq 0}">
                            <c:if test="${not empty cpwEnterDetail.team}">${cpwEnterDetail.team.name}</c:if><c:if
                                test="${empty cpwEnterDetail.team}">-</c:if>
                        </c:if>
                        <c:if test="${pwEnterDetail.petype eq 1}">
                            <c:if test="${not empty cpwEnterDetail.project}">${cpwEnterDetail.project.name}</c:if><c:if
                                test="${empty cpwEnterDetail.project}">-</c:if>
                        </c:if>
                        <c:if test="${pwEnterDetail.petype eq 2}">
                            <c:if test="${not empty cpwEnterDetail.pwCompany}">${cpwEnterDetail.pwCompany.name}</c:if><c:if
                                test="${empty cpwEnterDetail.pwCompany}">-</c:if>
                        </c:if>
                    </td>
                    <td>
                            ${cpwEnterDetail.pwEnter.applicant.name}
                    </td>
                    <td>
                            ${cpwEnterDetail.pwEnter.applicant.office.name}
                    </td>
                    <%-- <td>
                            ${cpwEnterDetail.pwEnter.applicant.no}
                    </td> --%>
                    <td>
                    	<c:if test="${not empty cpwEnterDetail.pwEnter.startDate}"><fmt:formatDate value="${cpwEnterDetail.pwEnter.startDate}" pattern="yyyy-MM-dd"/></c:if>
                        <c:if test="${empty cpwEnterDetail.pwEnter.startDate}">-</c:if>至
                        <c:if test="${not empty cpwEnterDetail.pwEnter.endDate}"><fmt:formatDate value="${cpwEnterDetail.pwEnter.endDate}" pattern="yyyy-MM-dd"/></c:if>
                        <c:if test="${empty cpwEnterDetail.pwEnter.startDate}">-</c:if>
                    </td>
                    <td>
                            ${fns:getDictLabel(cpwEnterDetail.status, 'pw_enter_shstatus', '')}
                    </td>
                    <%--<td>--%>
                        <%--<fmt:formatDate value="${cpwEnterDetail.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>--%>
                    <%--</td>--%>
                        <%--<td>--%>
                        <%--${cpwEnterDetail.remarks}--%>
                        <%--</td>--%>
                        <%--<shiro:hasPermission name="pw:pwEnterDetail:edit"><td>--%>
                        <%--<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/pw/pwEnter/form?id=${cpwEnterDetail.pwEnter.id}&isView=true">查看</a>--%>
                        <%--</td></shiro:hasPermission>--%>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    ${page.footer}
</div>
<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>
</body>
</html>