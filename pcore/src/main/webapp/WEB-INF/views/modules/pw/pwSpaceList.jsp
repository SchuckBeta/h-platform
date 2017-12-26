<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${backgroundTitle}</title>
    <%@include file="/WEB-INF/views/include/backcyjd.jsp" %>
    <%--<%@include file="/WEB-INF/views/include/backtable.jsp" %>--%>
    <script src="${ctxStatic}/common/mustache.min.js" type="text/javascript"></script>
    <script src="${ctxStatic}/common/initiate.js" type="text/javascript"></script>

    <%@include file="/WEB-INF/views/include/treetable.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {

            var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g, "");
            var data = ${fns:toJson(list)}, ids = [], rootIds = [];
            for (var i = 0; i < data.length; i++) {
                ids.push(data[i].id);
            }
            ids = ',' + ids.join(',') + ',';
            for (var i = 0; i < data.length; i++) {
                if (ids.indexOf(',' + data[i].parentId + ',') == -1) {
                    if ((',' + rootIds.join(',') + ',').indexOf(',' + data[i].parentId + ',') == -1) {
                        rootIds.push(data[i].parentId);
                    }
                }
            }
            for (var i = 0; i < rootIds.length; i++) {
                addRow("#treeTableList", tpl, data, rootIds[i], true);
            }
            $("#treeTable").treeTable({expandLevel: 5});
        });
        function addRow(list, tpl, data, pid, root) {
            for (var i = 0; i < data.length; i++) {
                var row = data[i];
                if ((${fns:jsGetVal('row.parentId')}) == pid) {
                    $(list).append(Mustache.render(tpl, {
                        dict: {
                            type: getDictLabel(${fns:toJson(fns:getDictList('pw_space_type'))}, row.type),
                            openWeek: getDictLabel(${fns:toJson(fns:getDictList('pw_space_week'))}, row.openWeek),
                            remarks: getDictLabel(${fns:toJson(fns:getDictList(''))}, row.remarks),
                            isSchool: row.type == '0',
                            isCampus: row.type == '1',
                            isBase: row.type == '2',
                            isBuilding: row.type == '3',
                            isFloor: row.type == '4',
                            blank123: 0,
                            openWeeksStr: openWeeksStr(row)
                        }, pid: (root ? 0 : pid), row: row
                    }));
                    addRow(list, tpl, data, row.id);
                }
            }
        }

        function openWeeksStr(row) {
            var arr = row.openWeeks;
            if(arr){
                if (arr.length == 7) {
                    return '不限';
                }
                var r = '';
                $.each(arr, function (i, item) {
                    switch (item) {
                        case '1':
                            r += '每周一、';
                            break;
                        case '2':
                            r += '每周二、';
                            break;
                        case '3':
                            r += '每周三、';
                            break;
                        case '4':
                            r += '每周四、';
                            break;
                        case '5':
                            r += '每周五、';
                            break;
                        case '6':
                            r += '每周六、';
                            break;
                        case '7':
                            r += '每周日、';
                            break;
                        default:
                            break;
                    }

                })
                if(r != ''){
                    return r.substring(0, r.length - 1);
                }
            }

        }

    </script>

    <style type="text/css">
        #open-time-left{
            text-align: left;
        }
        #open-time-left-pm{
            display: block;
            margin-left:36px;
        }

    </style>

</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>场地管理</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <ul class="nav nav-tabs nav-tabs-default">
        <li class="active"><a href="${ctx}/pw/pwSpace/">场地列表</a></li>
        <%--<shiro:hasPermission name="pw:pwSpace:edit"><li><a href="${ctx}/pw/pwSpace/form">设施添加</a></li></shiro:hasPermission>--%>
    </ul>
    <sys:message content="${message}"/>
    <div class="tab-content-default">
        <table id="treeTable"
               class="table table-bordered table-condensed table-hover table-center table-orange table-nowrap">
            <thead>
            <tr>
                <th>基地结构</th>
                <th>场地类型</th>
                <th width="33%">开放时间</th>
                <shiro:hasPermission name="pw:pwSpace:edit">
                    <th width="280">操作</th>
                </shiro:hasPermission>
            </tr>
            </thead>
            <tbody id="treeTableList"></tbody>
        </table>
    </div>

</div>
<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>
<script type="text/template" id="treeTableTpl">
    <tr id="{{row.id}}" pId="{{pid}}" style="">
        <td>
            <a href="${ctx}/pw/pwSpace/details?id={{row.id}}">
                {{row.name}}
            </a>
        </td>
        <td>
            {{dict.type}}
        </td>
        <td id="open-time-left">
            {{#dict.isBuilding}}
            <div>
                <div>周次：{{dict.openWeeksStr}}</div>
                <div>时间：<span>上午：{{row.amOpenStartTime}}-{{row.amOpenEndTime}}</span>
                    <span id="open-time-left-pm">下午：{{row.pmOpenStartTime}}-{{row.pmOpenEndTime}}</span></div>
            </div>
            {{/dict.isBuilding}}

        </td>
        <shiro:hasPermission name="pw:pwSpace:edit">
            <td style="text-align: right">

                {{#dict.isSchool}}
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?parent.id={{row.id}}&type=1">添加校区</a>
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?parent.id={{row.id}}&type=2">添加基地</a>
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?parent.id={{row.id}}&type=3">添加楼栋</a>
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?id={{row.id}}">修改</a>
                {{/dict.isSchool}}
                {{#dict.isCampus}}

                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?parent.id={{row.id}}&type=2">添加基地</a>
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?parent.id={{row.id}}&type=3">添加楼栋</a>
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?id={{row.id}}">修改</a>
                <a class="btn btn-small btn-default" href="${ctx}/pw/pwSpace/delete?id={{row.id}}"
                   onclick="return confirmx('确认要删除该设施及所有子设施吗？', this.href)">删除</a>
                {{/dict.isCampus}}
                {{#dict.isBase}}
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?parent.id={{row.id}}&type=3">添加楼栋</a>
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?id={{row.id}}">修改</a>
                <a class="btn btn-small btn-default" href="${ctx}/pw/pwSpace/delete?id={{row.id}}"
                   onclick="return confirmx('确认要删除该设施及所有子设施吗？', this.href)">删除</a>
                {{/dict.isBase}}
                {{#dict.isBuilding}}
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?parent.id={{row.id}}&type=4">添加楼层</a>
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?id={{row.id}}">修改</a>
                <a class="btn btn-small btn-default" href="${ctx}/pw/pwSpace/delete?id={{row.id}}"
                   onclick="return confirmx('确认要删除该设施及所有子设施吗？', this.href)">删除</a>
                {{/dict.isBuilding}}
                {{#dict.isFloor}}
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/designForm?id={{row.id}}">设计</a>
                <a class="btn btn-small btn-primary" href="${ctx}/pw/pwSpace/form?id={{row.id}}">修改</a>
                <a class="btn btn-small btn-default" href="${ctx}/pw/pwSpace/delete?id={{row.id}}"
                   onclick="return confirmx('确认要删除该设施及所有子设施吗？', this.href)">删除</a>
                {{/dict.isFloor}}
            </td>
        </shiro:hasPermission>
    </tr>
</script>
</body>
</html>