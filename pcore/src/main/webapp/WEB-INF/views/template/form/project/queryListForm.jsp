<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <style type="text/css">
        .table .team-name{
            width: 160px;
            min-width: 160px;
            max-height: 40px;
            margin: 0 auto;
            text-align: center;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
            line-height: 20px;
            overflow: hidden;
        }
        .table .team-number{
            width: 100px;
            max-width: 174px;
            height: 20px;
            margin: 0 auto;
            text-align: center;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        @media (min-width: 1200px) {
            .table .team-name{
                width:auto;
            }
        }
        @media (min-width: 1100px) {
            .table .team-number{
                width: 174px;
            }
        }
    </style>
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
        function expData() {
            $.get('/a/proprojectmd/checkExpAll?actywId=' + $("#actywId").val(), {}, function (data) {
                if (data.ret == "0") {
                    confirmx("没有需要导出的数据，继续导出？", function () {
                        location.href = "/a/proprojectmd/expAll?actywId=" + $("#actywId").val();
                    });
                } else {
                    location.href = "/a/proprojectmd/expAll?actywId=" + $("#actywId").val();
                }
            });
        }
        function selectAll(ob) {
            if ($(ob).attr("checked")) {
                $("input[name='subck']:checkbox").attr("checked", true);
            } else {
                $("input[name='subck']:checkbox").attr("checked", false);
            }
        }
        function resall() {
            var temarr = [];
            $("input[name='subck']:checked").each(function (i, v) {
                temarr.push($(v).val());
            });
            if (temarr.length == 0) {
                alertx("请选择要发布的项目");
                return;
            }
            confirmx("确定发布所选项目到门户网站？", function () {
                $.ajax({
                    type: 'post',
                    url: '/a/excellent/resall',
                    dataType: "json",
                    data: {
                        fids: temarr.join(",")
                    },
                    success: function (data) {
                        if (data) {
                            alertx(data.msg);
                        }
                    }
                });
            });
        }
        function subckchange(ob) {
            if (!$(ob).attr("checked")) {
                $("#selectAllbtn").attr("checked", false);
            }
            /* if($("input[name='subck']:checked").length==0){
             $("#resallbtn").removeAttr("onclick");
             }else{
             $("#resallbtn").attr("onclick","resall()");
             } */
        }
    </script>
</head>
<body>
<div class="container-fluid container-fluid-oe">
    <div class="edit-bar edit-bar-tag clearfix">
        <div class="edit-bar-left">
            <span>${menuName}</span>
            <i class="line weight-line"></i>
        </div>
    </div>
</div>
<div class="content_panel">
    <form:form id="searchForm" modelAttribute="proModel" action="/a/cms/form/queryMenuList" method="post"
               class="form-inline form-content-box">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input id="actywId" name="actywId" type="hidden" value="${actywId}"/>

    </form:form>
    <table id="contentTable"
           class="table table-bordered table-condensed table-hover  table-sort table-theme-default table-center">
        <thead>
        <tr>
            <th><input type="checkbox" id="selectAllbtn" onclick="selectAll(this)"></th>
            <th>项目编号</th>
            <th>参赛项目名称</th>
            <th style="white-space: nowrap">申报人</th>
            <th style="white-space: nowrap">项目类别</th>
            <th style="white-space: nowrap">项目来源</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="proModel">
            <tr>
                <td><input type="checkbox" name="subck" onclick="subckchange(this)" value="${proModel.id}"></td>
                <td><p class="team-number">${proModel.competitionNumber}</p></td>
                <td><p class="team-name">
                    <a href="${ctx}/promodel/proModel/viewForm?id=${proModel.id}">${proModel.pName}</a>
                </p></td>
                <td>${fns:getUserById(proModel.declareId).name}</td>
                <td>
                        ${fns:getDictLabel(proModel.proCategory, "project_type", "")}
                </td>
                <td>
                        ${fns:getDictLabel(proModel.projectSource, "project_source", "")}
                </td>
                <td>
                        <c:choose>
                            <c:when test="${proModel.state!=null &&  proModel.state=='1'}">
                            <a href="${ctx}/actyw/actYwGnode/designView?groupId=${groupId}&proInsId=${proModel.procInsId}&grade=1" class="check_btn btn-pray btn-lx-primary" target="_blank">
                                <c:choose >
                                    <c:when test="${proModel.grade!=null &&proModel.grade=='0'}">
                                        ${pj:getProModelAuditNameById(proModel.procInsId)}不通过
                                    </c:when>
                                    <c:otherwise>
                                        ${pj:getProModelAuditNameById(proModel.procInsId)}通过
                                    </c:otherwise>
                                </c:choose>
                            </a>
                            </c:when >
                            <c:otherwise>
                                <a href="${ctx}/actyw/actYwGnode/designView?groupId=${groupId}&proInsId=${proModel.procInsId}" class="check_btn btn-pray btn-lx-primary" target="_blank">

                                ${pj:getProModelAuditNameById(proModel.procInsId)}
                                </a>
                            </c:otherwise>
                        </c:choose>

                </td>
                <td style="white-space: nowrap">
                    <a href="${ctx}/promodel/proModel/viewForm?id=${proModel.id}">查看</a>

                    <shiro:hasPermission name="excellent:projectShow:edit">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                           href="${ctx}/excellent/projectShowForm?projectId=${proModel.id}">
                            展示
                        </a>
                    </shiro:hasPermission>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    ${page.footer}
</div>
</body>
</html>