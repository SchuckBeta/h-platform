<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>资源管理</title>
    <!-- <meta name="decorator" content="default"/> -->
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <!-- <link href="/common/common-css/pagenation.css" rel="stylesheet"/>-->
    <link rel="stylesheet" type="text/css"
          href="/static/common/tablepage.css"/>
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


<style>
    td, th {
        text-align: center !important;
    }

    th {
        background: #f4e6d4 !important;
    }

    #btnSubmit {
        background: #e9432d !important;
    }

    .form-actions {
        border-top: none;
    }

    .btns {
        float: right !important;
    }

    table td, table th {
        color: #656565;
    }

    table td .prj-name {
        color: #289ff2;
    }

    table td .prj-name:hover {
        text-decoration: underline;
    }

    table td .btn {
        padding: 3px 11px;
        background: #e5e5e5;
        color: #666;
    }

    table td .btn:hover {
        background: #e9432d;
        color: #fff;
    }
</style>

<body>
<div class="mybreadcrumbs"><span>资源管理</span></div>
<div class="table-page">
    <ul class="nav nav-tabs">
        <li class="active"><a href="${ctx}/cms/cmsIndexResource/">资源列表</a></li>
        <shiro:hasPermission name="cms:cmsIndexResource:edit">
            <li><a href="${ctx}/cms/cmsIndexResource/form">资源添加</a></li>
        </shiro:hasPermission>
    </ul>
    <form:form id="searchForm" modelAttribute="cmsIndexResource" action="${ctx}/cms/cmsIndexResource/" method="post"
               class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <ul class="ul-form">
            <li><label>栏目：</label>
                <sys:treeselect id="category" name="cmsIndexRegion.category.id"
                                value="${cmsIndexResource.cmsIndexRegion.category.id}"
                                labelName="cmsIndexRegion.category.name"
                                labelValue="${cmsIndexResource.cmsIndexRegion.category.name}" title="栏目"
                                url="/cms/category/treeData" extId="${cmsIndexRegion.category.id}"
                                cssClass="required" allowClear="true" cssStyle="width:100px;"/>
            </li>
                <%-- <li><label>区域：</label>
                    <form:input path="cmsIndexRegion.regionName" htmlEscape="false" maxlength="64" class="input-medium"/>
                </li> --%>
                <%-- <li><label>区域类型：</label>
                    <form:select path="regionType" class="input-medium"  cssStyle="width:100px;">
                        <form:option value="" label="--请选择--"/>
                        <form:options items="${fns:getDictList('regiontype_flag')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                    </form:select>
                </li> --%>
            <li><label>资源名称：</label>
                <form:input path="resName" htmlEscape="false" maxlength="64" class="input-medium"
                            cssStyle="width:100px;"/>
            </li>
                <%-- <li><label>标题：</label>
                    <form:input path="title" htmlEscape="false" maxlength="64" class="input-medium"  cssStyle="width:100px;"/>
                </li> --%>
            <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
            <li class="clearfix"></li>
        </ul>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable" class="table  table-bordered table-condensed table-hover">
        <thead>
        <tr>
            <th width="300px">栏目</th>
            <th>区域</th>
            <th>资源名称</th>
            <th>模式</th>
            <th>状态</th>
            <!-- <th>排序</th> -->
            <th>更新时间</th>
            <shiro:hasPermission name="cms:cmsIndexResource:edit">
                <th>操作</th>
            </shiro:hasPermission>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="cmsIndexResource">
            <tr>
                <td><a
                        href="${ctx}/cms/category/form?id=${cmsIndexResource.cmsIndexRegion.category.id}"
                        class="prj-name">
                    <c:if test="${(not empty cmsIndexResource.cmsIndexRegion.category.parent.parent.parent) && (cmsIndexResource.cmsIndexRegion.category.parent.parent.parent.id ne '1')}">
                        ${cmsIndexResource.cmsIndexRegion.category.parent.parent.parent.name}/
                    </c:if>
                    <c:if test="${(not empty cmsIndexResource.cmsIndexRegion.category.parent.parent)  && (cmsIndexResource.cmsIndexRegion.category.parent.parent.id ne '1')}">
                        ${cmsIndexResource.cmsIndexRegion.category.parent.parent.name}/
                    </c:if>
                    <c:if test="${(not empty cmsIndexResource.cmsIndexRegion.category.parent)  && (cmsIndexResource.cmsIndexRegion.category.parent.id ne '1')}">
                        ${cmsIndexResource.cmsIndexRegion.category.parent.name}/
                    </c:if>
                        ${cmsIndexResource.cmsIndexRegion.category.name}
                </a></td>
                <td><a href="${ctx}/cms/cmsIndexRegion/form?id=${cmsIndexResource.cmsIndexRegion.id}" class="prj-name">
                        ${cmsIndexResource.cmsIndexRegion.regionName}
                </a></td>
                <td><a href="${ctx}/cms/cmsIndexResource/form?id=${cmsIndexResource.id}" class="prj-name">
                        ${cmsIndexResource.resName}
                </a></td>
                <td>
                        ${fns:getDictLabel(cmsIndexResource.resModel, 'region_model', '')}
                </td>
                <td>
                        ${fns:getDictLabel(cmsIndexResource.resState, 'resstate_flag', '')}
                </td>
                    <%-- <td>
                        ${cmsIndexResource.resSort}
                    </td> --%>
                <td>
                    <fmt:formatDate value="${cmsIndexResource.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
                <shiro:hasPermission name="cms:cmsIndexResource:edit">
                    <td>
                        <a href="${ctx}/cms/cmsIndexResource/form?id=${cmsIndexResource.id}" class="btn">修改</a>
                        <a href="${ctx}/cms/cmsIndexResource/delete?id=${cmsIndexResource.id}" class="btn"
                           onclick="return confirmx('确认要删除该首页资源管理吗？', this.href)">删除</a>
                    </td>
                </shiro:hasPermission>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%-- <div class="pagination">${page}</div> --%>
    ${page.footer}
</div>
</body>
</html>