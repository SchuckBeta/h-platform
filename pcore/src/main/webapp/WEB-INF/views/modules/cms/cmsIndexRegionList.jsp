<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>区域管理</title>
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
</head>
<body>
<div class="mybreadcrumbs"><span>区域管理</span></div>
<div class="table-page">
    <ul class="nav nav-tabs">
        <li class="active"><a href="${ctx}/cms/cmsIndexRegion/">区域列表</a></li>
        <shiro:hasPermission name="cms:cmsIndexRegion:edit">
            <li><a href="${ctx}/cms/cmsIndexRegion/form">区域添加</a></li>
        </shiro:hasPermission>
    </ul>
    <form:form id="searchForm" modelAttribute="cmsIndexRegion" action="${ctx}/cms/cmsIndexRegion/" method="post"
               class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <ul class="ul-form">
            <li><label>站点栏目：</label>
                <sys:treeselect id="category" name="category.id"
                                value="${cmsIndexRegion.category.id}" labelName="category.name"
                                labelValue="${cmsIndexRegion.category.name}" title="栏目"
                                url="/cms/category/treeData" extId="${category.id}"
                                cssClass="required" allowClear="true" cssStyle="width:100px;"/>
            </li>
                <%-- <li><label>区域编号：</label>
                    <form:input path="regionId" htmlEscape="false" maxlength="64" class="input-medium"/>
                </li> --%>
            <li><label>区域名：</label>
                <form:input path="regionName" htmlEscape="false" maxlength="64" class="input-small"
                            cssStyle="width:100px;"/>
            </li>
            <li><label>区域模式：</label>
                <form:select path="regionModel" class="input-small" cssStyle="width:100px;">
                    <form:option value="" label="--请选择--"/>
                    <form:options items="${fns:getDictList('region_model')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/>
                </form:select>
            </li>
            <li><label>区域类型：</label>
                <form:select path="regionType" class="input-small" cssStyle="width:100px;">
                    <form:option value="" label="--请选择--"/>
                    <form:options items="${fns:getDictList('regiontype_flag')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/>
                </form:select>
            </li>
            <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
            <li class="clearfix"></li>
        </ul>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable" class="table table-hover table-bordered table-condensed">
        <thead>
        <tr>
            <th width="300px">栏目</th>
            <th>区域名</th>
            <!-- <th>区域编号</th> -->
            <th>区域类型</th>
            <th>区域状态</th>
            <th>区域排序</th>
            <shiro:hasPermission name="cms:cmsIndexRegion:edit">
                <th>操作</th>
            </shiro:hasPermission>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="cmsIndexRegion">
            <tr>
                <td><a
                        href="${ctx}/cms/category/form?id=${cmsIndexRegion.category.id}" class="prj-name">
                    <c:if test="${(not empty cmsIndexRegion.category.parent.parent.parent) && (cmsIndexRegion.category.parent.parent.parent.id ne '1')}">
                        ${cmsIndexRegion.category.parent.parent.parent.name}/
                    </c:if>
                    <c:if test="${(not empty cmsIndexRegion.category.parent.parent)  && (cmsIndexRegion.category.parent.parent.id ne '1')}">
                        ${cmsIndexRegion.category.parent.parent.name}/
                    </c:if>
                    <c:if test="${(not empty cmsIndexRegion.category.parent)  && (cmsIndexRegion.category.parent.id ne '1')}">
                        ${cmsIndexRegion.category.parent.name}/
                    </c:if>
                        ${cmsIndexRegion.category.name}
                </a></td>
                <td><a href="${ctx}/cms/category/form?id=${cmsIndexRegion.category.id}" class="prj-name">
                        ${cmsIndexRegion.regionName}
                </a></td>
                    <%-- <td>
                    ${cmsIndexRegion.regionId}
                </td> --%>
                <td>
                        ${fns:getDictLabel(cmsIndexRegion.regionType, 'regiontype_flag', '')}
                </td>
                <td>
                        ${fns:getDictLabel(cmsIndexRegion.regionState, 'regionstate_flag', '')}
                </td>
                <td>
                        ${cmsIndexRegion.regionSort}
                </td>
                <shiro:hasPermission name="cms:cmsIndexRegion:edit">
                    <td style="white-space:nowrap">
                        <a href="${ctx}/cms/cmsIndexRegion/form?id=${cmsIndexRegion.id}" class="btn">修改</a>
                        <a href="${ctx}/cms/cmsIndexRegion/delete?id=${cmsIndexRegion.id}" class="btn"
                           onclick="return confirmx('确认要删除该区域吗？', this.href)">删除</a>
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