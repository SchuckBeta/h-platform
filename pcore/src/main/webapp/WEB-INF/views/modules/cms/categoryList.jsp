<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>栏目管理</title>
    <!-- <meta name="decorator" content="default"/> -->
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/static/common/tablepage.css">
    <%@include file="/WEB-INF/views/include/treetable.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#treeTable").treeTable({expandLevel: 3});
        });
        function updateSort() {
            loading('正在提交，请稍等...');
            $("#listForm").attr("action", "${ctx}/cms/category/updateSort");
            $("#listForm").submit();
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
            background: #e9432d;
            float: right;
        }

        .form-actions {
            border-top: none;
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
<div class="mybreadcrumbs"><span>栏目管理</span></div>
<div class="table-page">
    <ul class="nav nav-tabs">
        <li class="active"><a href="${ctx}/cms/category/">栏目列表</a></li>
        <shiro:hasPermission name="cms:category:edit">
            <li><a href="${ctx}/cms/category/form">栏目添加</a></li>
        </shiro:hasPermission>
    </ul>
    <sys:message content="${message}"/>
    <form id="listForm" method="post">
        <table id="treeTable" class="table table-hover table-bordered table-condensed">
            <tr>
                <th>栏目名称</th>
                <%--<th>链接</th>--%>
                <th>归属机构</th>
                <th>栏目模型</th>
                <th style="text-align:center;">排序</th>
                <th title="是否在导航中显示该栏目">导航菜单</th>
                <th title="是否在分类页中显示该栏目的文章列表">栏目列表</th>
                <th>展现方式</th>
                <th>操作</th>
            </tr>
            <c:forEach items="${list}" var="tpl">
                <tr id="${tpl.id}" pId="${tpl.parent.id ne '1'?tpl.parent.id:'0'}">
                    <td style="text-align:left !important;"><a href="${ctx}/cms/category/form?id=${tpl.id}"
                                                               class="prj-name">${tpl.name}</a></td>
                    <%--<td style="text-align:left;">${tpl.href}</td>--%>
                    <td>${tpl.office.name}</td>
                    <td>${fns:getDictLabel(tpl.module, 'cms_module', '公共模型')}</td>
                    <td style="text-align:center;">
                        <shiro:hasPermission name="cms:category:edit">
                            <input type="hidden" name="ids" value="${tpl.id}"/>
                            <input name="sorts" type="text" value="${tpl.sort}"
                                   style="width:50px;margin:0;padding:0;text-align:center;">
                        </shiro:hasPermission><shiro:lacksPermission name="cms:category:edit">
                        ${tpl.sort}
                    </shiro:lacksPermission>
                    </td>
                    <td>${fns:getDictLabel(tpl.inMenu, 'show_hide', '隐藏')}</td>
                    <td>${fns:getDictLabel(tpl.inList, 'show_hide', '隐藏')}</td>
                    <td>${fns:getDictLabel(tpl.showModes, 'cms_show_modes', '默认展现方式')}</td>
                    <td style="white-space:nowrap">
                            <%-- <a href="${pageContext.request.contextPath}${fns:getFrontPath()}/list-${tpl.id}${fns:getUrlSuffix()}" class="btn" target="_blank">访问</a> --%>
                        <shiro:hasPermission name="cms:category:edit">
                            <c:if test="${tpl.parent.id ne '1'}">
                                <a href="${ctx}/cms/category/form?id=${tpl.id}" class="btn">修改</a>
                                <a href="${ctx}/cms/category/delete?id=${tpl.id}" class="btn"
                                   onclick="return confirmx('要删除该栏目及所有子栏目项吗？', this.href)">删除</a>
                            </c:if>
                            <a href="${ctx}/cms/category/form?parent.id=${tpl.id}" class="btn">添加下级栏目</a>
                        </shiro:hasPermission>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <shiro:hasPermission name="cms:category:edit">
            <div class="form-actions pagination-left">
                <input id="btnSubmit" class="btn btn-primary" type="button" value="保存排序" onclick="updateSort();"/>
            </div>
        </shiro:hasPermission>
    </form>
</div>
</body>
</html>