<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>机构管理</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <%@include file="/WEB-INF/views/include/treetable.jsp" %>
    <style>
        body::-webkit-scrollbar-thumb{
            background-color: #6e6e6e;
            outline: 1px solid #333;
        }
        body::-webkit-scrollbar{
            height: 6px;
            width: 6px;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g, "");
            var data = ${fns:toJson(list)}, rootId = "${not empty office.id ? office.id : '0'}";
            addRow("#treeTableList", tpl, data, rootId, true);
            $("#treeTable").treeTable({expandLevel: 5});
        });
        function addRow(list, tpl, data, pid, root) {
            for (var i = 0; i < data.length; i++) {
                var row = data[i];
                if ((${fns:jsGetVal('row.parentId')}) == pid) {
                    $(list).append(Mustache.render(tpl, {
                        dict: {
                            type: getDictLabel(${fns:toJson(fns:getDictList('sys_office_type'))}, row.type)
                        }, pid: (root ? 0 : pid), row: row
                    }));
                    addRow(list, tpl, data, row.id);
                }
            }
        }
    </script>
</head>
<body>
<div class="container-fluid" style="padding-left: 0">
    <ul class="nav nav-tabs">
        <li class="active"><a
                href="${ctx}/sys/office/list?id=${office.id}&parentIds=${office.parentIds}">机构列表</a></li>
        <shiro:hasPermission name="sys:office:edit">
            <li><a href="${ctx}/sys/office/form?parent.id=${office.id}">机构添加</a></li>
        </shiro:hasPermission>
    </ul>
    <sys:message content="${message}"/>
    <%--<div class="table-responsive">--%>
        <table id="treeTable" class="table table-hover table-bordered table-condensed table-theme-default table-nowrap">
            <thead>
            <tr>
                <th>机构名称</th>
                <%--<th>归属区域</th>--%>
                <th>机构编码</th>
                <th>机构类型</th>
                <th>备注</th>
                <shiro:hasPermission name="sys:office:edit">
                    <th>操作</th>
                </shiro:hasPermission>
            </tr>
            </thead>
            <tbody id="treeTableList"></tbody>
        </table>
    <%--</div>--%>
</div>
<script type="text/template" id="treeTableTpl">
    <tr id="{{row.id}}" pId="{{pid}}">
        <td><a href="${ctx}/sys/office/form?id={{row.id}}" class="prj-name">{{row.name}}</a></td>
        <!-- <td>{{row.area.name}}</td> -->
        <td>{{row.code}}</td>
        <td>{{dict.type}}</td>
        <td>{{row.remarks}}</td>
        <shiro:hasPermission name="sys:office:edit">
            <td>
                <a href="${ctx}/sys/office/form?id={{row.id}}" class="btn btn-back-oe btn-primaryBack-oe btn-small">修改</a>
                <a href="${ctx}/sys/office/form?parent.id={{row.id}}" class="btn btn-back-oe btn-primaryBack-oe btn-small">添加下级机构</a>
                <a href="${ctx}/sys/office/delete?id={{row.id}}" class="btn btn-small"
                   onclick="return confirmx('要删除该机构及所有子机构项吗？', this.href)">删除</a>

            </td>
        </shiro:hasPermission>
    </tr>
</script>
</body>
</html>