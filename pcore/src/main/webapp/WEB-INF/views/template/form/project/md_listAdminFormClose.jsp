<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ page errorPage="/WEB-INF/views/error/formMiss.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title></title>
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

        function claim(taskId) {
            $.get('${ctx}/act/task/claim', {taskId: taskId}, function (data) {
                /*	top.$.jBox.tip('签收完成');*/
                location.reload();
            });
        }
        function expData(){
        	$.get('/a/proprojectmd/checkExpClose?gnodeId='+$("#gnodeId").val()+"&actywId="+$("#actywId").val(), {}, function (data) {
                if(data.ret=="0"){
                	confirmx("没有需要导出的数据，继续导出？",function(){
                		location.href="/a/proprojectmd/expClose?gnodeId="+$("#gnodeId").val()+"&actywId="+$("#actywId").val();
                	});
                }else{
       				location.href="/a/proprojectmd/expClose?gnodeId="+$("#gnodeId").val()+"&actywId="+$("#actywId").val();
                }
            });
       	}
       	function impData(){
       		location.href="/a/impdata/mdlist?type=8";
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
</div>
<div class="content_panel">
    <form:form id="searchForm" modelAttribute="act" action="${actionUrl}" method="post"
               class="form-inline form-content-box">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input id="gnodeId" name="gnodeId" type="hidden" value="${gnodeId}"/>
        <input id="actywId" name="actywId" type="hidden" value="${actywId}"/>
        <shiro:hasPermission name="sys:user:import">
        <div class="search-form-wrap">
                <div class="text-right">
                    <input class="btn btn-back-oe btn-primaryBack-oe"	onclick="expData()" value="导出" type="button">
                    <input class="btn btn-back-oe btn-primaryBack-oe"	onclick="impData()" value="导入" type="button">
                </div>
            </div>
         </shiro:hasPermission>
    </form:form>
    <table id="contentTable" class="table table-bordered table-condensed table-hover  table-sort table-theme-default table-center">
            <thead>
            <tr>
                <th width="160px">项目编号</th>
                <th>参赛项目名称</th>
                <th>申报人</th>
                <th>申报类别</th>
                <th>申报级别</th>
                <!-- <th>组成员</th>
                <th>指导老师</th> -->

                <th>所属学院</th>
                <th>状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="proModelMd">
               <%-- <c:set var="proModelMd" value="${fns:getProModelMdById(act.vars.map.id)}" />--%>
                <tr>
                    <td>
                    ${proModelMd.proModel.competitionNumber}
                    </td>
                    <td>${proModelMd.proModel.pName}</td>
                    <td>${fns:getUserById(proModelMd.proModel.declareId).name}</td>
                    <td>${fns:getDictLabel(proModelMd.proModel.proCategory, "project_type", "")}</td>
                    <td>${fns:getDictLabel(proModelMd.appLevel, "0000000196", "")}</td>
                    <td>${fns:getUserById(proModelMd.proModel.declareId).office.name}</td>

                    <td>
                        <c:choose>
                            <c:when test="${proModelMd.closeState!=null &&proModelMd.closeState=='1'}">通过</c:when>
                            <c:when test="${proModelMd.closeState!=null &&proModelMd.closeState=='0'}">不通过</c:when>
                            <c:otherwise >
                            	<a href="${ctx}/actyw/actYwGnode/designView?groupId=${auditGonde.group.id}&proInsId=${proModelMd.proModel.procInsId}" class="check_btn btn-pray btn-lx-primary" target="_blank">待审核</a>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small" href="${ctx}/promodel/proModel/viewForm?id=${proModelMd.proModel.id}">查看</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

    ${page.footer}

</div>
</body>
</html>