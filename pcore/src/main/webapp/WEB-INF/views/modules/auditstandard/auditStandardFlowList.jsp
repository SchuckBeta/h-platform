<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title></title>
    <%@ include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/css/credit-module.css">
    <style>
        .form-group-inline select{
            height: 30px;
        }
         .table-thead-bg thead tr{
             background-color: #f4e6d4;
         }
        .table th{
            background: none;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
            if ($("#flow").val()) {
                $.ajax({
                    type: 'get',
                    url: '/a/actyw/actYwGnode/treeDataByYwId?level=1&ywId=' + $("#flow").val(),
                    success: function (data) {
                        if (data) {
                            $.each(data, function (i, v) {
                                if ($("#nodeid").val() == v.id) {
                                    $("#node").append('<option selected value="' + v.id + '" >' + v.name + '</option>');
                                } else {
                                    $("#node").append('<option value="' + v.id + '" >' + v.name + '</option>');
                                }
                            });
                        }
                    }
                });
            }
            $("#form-search-bar").validate({
                submitHandler: function (form) {
                    if (!$("#name").val()) {
                        alertx("请选择评审标准");
                        return;
                    }
                    if (!$("#flow").val()) {
                        alertx("请选择关联项目");
                        return;
                    }
                    if (!$("#node").val()) {
                        alertx("请选择关联审核节点");
                        return;
                    }
                    form.submit();
                }
            });

        });
        function page(n, s) {
            location.href = "/a/auditstandard/auditStandard?pageNo=" + n + "&pageSize=" + s;
        }
        function flowChange() {
            $("#node").find("option:gt(0)").remove();
            if ($("#flow").val()) {
                $.ajax({
                    type: 'get',
                    url: '/a/actyw/actYwGnode/treeDataByYwId?level=1&ywId=' + $("#flow").val(),
                    success: function (data) {
                        if (data) {
                            $.each(data, function (i, v) {
                                $("#node").append('<option value="' + v.id + '" >' + v.name + '</option>');
                            });
                        }
                    }
                });
            }
        }
        function confirmxChange(rid, ob) {
            confirmx("确认设置？", function () {
                onChildChange(rid, ob);
            }, function () {
                if ($(ob).attr("checked")) {
                    $(ob).removeAttr("checked");
                } else {
                    $(ob).attr("checked", true);
                }
            });
        }
        function onChildChange(rid, ob) {
            var str = "";
            var obs = $(ob).parent().parent().find("input[type='checkbox']:checked");
            if (obs) {
                var arr = new Array(obs.length);
                $.each(obs, function (i, v) {
                    arr.push($(v).val());
                });
                str = arr.join(",");
            }
            $.ajax({
                type: 'post',
                url: '${ctx}/auditstandard/auditStandard/saveChild',
                datatype: "json",
                data: {relationId: rid, isEscoreNodes: str},
                success: function (data) {
                    var st = "success";
                    if (data.ret == "0") {
                        st = "error";
                    }
                    top.$.jBox.tip(data.msg, st, {persistent: true, opacity: 0});
                }
            });
        }
    </script>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>关联项目</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <c:if test="${not empty message}">
        <c:if test="${not empty type}"><c:set var="ctype" value="${type}"/></c:if>
        <c:if test="${empty type}"><c:set var="ctype"
                                          value="${fn:indexOf(message,'失败') eq -1?'success':'error'}"/></c:if>
        <div class="alert alert-${ctype}">
            <button data-dismiss="alert" class="close">×</button>
                ${message}</div>
    </c:if>
    <form:form id="form-search-bar" modelAttribute="vo" action="${ctx}/auditstandard/auditStandard/saveDetail"
               class="form-inline">
        <input id="nodeid" name="nodeid" type="hidden" value="${vo.node}"/>
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <div class="form-inline-right form-inline-right-w240">
            <button type="submit" class="btn-oe btn-primary-oe">添加</button>
            <button type="button" class="btn-oe btn-primary-oe"
                    onclick="javascript:location.href='${ctx}/auditstandard/auditStandard/list'">返回
            </button>
        </div>
        <div class="form-inline-left form-inline-left-mr240">
            <div class="form-group-inline">
                <label class="control-mr control-w60">评审标准</label>
                <form:select path="name" class="input-medium form-control">
                    <form:option value="" label="--请选择--"/>
                    <form:options items="${fns:getAuditStandardList()}" itemLabel="name" itemValue="id"
                                  htmlEscape="false"/>
                </form:select>
            </div>
            <div class="form-group-inline">
                <label class="control-mr control-w60">关联项目</label>
                <form:select path="flow" class="input-medium form-control" onchange="flowChange()">
                    <form:option value="" label="--请选择--"/>
                    <form:options items="${fns:getActListData('')}" itemLabel="proProject.projectName" itemValue="id"
                                  htmlEscape="false"/>
                </form:select>
            </div>
            <div class="form-group-inline">
                <label class="control-mr control-w80">关联审核节点</label>
                <select id="node" name="node" class="form-control">
                    <option value="">--请选择--</option>
                </select>
            </div>
        </div>
    </form:form>
    <table class="table table-bordered table-hover table-theme-default table-vertical-middle table-thead-bg">
        <thead>
        <tr>
            <th width="150">评审标准</th>
            <th width="150">关联项目</th>
            <th width="150">关联审核节点</th>
            <th width="150">是否需要评分</th>
            <th width="150">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="sta">
            <tr>
                <td>${sta.name }</td>
                <td>${sta.flowName }</td>
                <td>${sta.nodeName }</td>
                <td>
                    <c:forEach items="${sta.childs}" var="child">
	            <span>
		            <input onclick="confirmxChange('${sta.relationId}',this)" id="${child.id }" value="${child.id }"
                           <c:if test="${child.sel=='1' }">checked</c:if> type="checkbox">
		            <label for="${child.id }">${child.name }</label>
	            </span>
                        </br>
                    </c:forEach>
                </td>
                <td>
                    <button class="btn-oe btn-default-oe btn-sm-oe"
                       onclick="javascript:return confirmx('确认要删除吗？', function(){location.href='${ctx}/auditstandard/auditStandard/deleteDetail?id=${sta.id}&flow=${sta.flow}&node=${sta.node}'})">删除</button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    ${page.footer}
</div>
</body>
</html>