<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>学分查询管理</title>
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/css/state/titlebar.css">
    <link rel="stylesheet" type="text/css" href="/css/topSearchForm.css">
    <script type="text/javascript" src="/js/backTable/sort.js"></script><!--排序js-->
    <style>
        .table th{
            background: #f4e6d4;
        }

        /*排序样式*/
        .btn-sort{
            color: #646464;
        }
        .btn-sort>span{
            margin-right: 4px;
        }
        .btn-sort:hover{
            color: #646464;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
			$("#ps").val($("#pageSize").val());
	        //增加学院下拉框change事件
	        $("#collegeId").change(function () {
	            var parentId = $(this).val();
	            //根据当前学院id 更改
	            $("#professionalSelect").empty();
	            $("#professionalSelect").append('<option value="">所有专业</option>');
	            $.ajax({
	                type: "post",
	                url: "/a/sys/office/findProfessionals",
	                data: {"parentId": parentId},
	                async: true,
	                success: function (data) {
	                    $.each(data, function (i, val) {
	                        if (val.id == "${studentExpansion.user.professional}") {
	                            $("#professionalSelect").append('<option selected="selected" value="' + val.id + '">' + val.name + '</option>')
	                        } else {
	                            $("#professionalSelect").append('<option value="' + val.id + '">' + val.name + '</option>')
	                        }

	                    })
                        <%--$("#professionalSelect").val("${studentExpansion.user.professional}")--%>
	                }
	            });

	        })
	        $("#collegeId").trigger('change');
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
<div class="content_panel">
    <div class="edit-bar edit-bar-tag clearfix" style="margin-top: 30px">
        <div class="edit-bar-left">
            <span>学分查询</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <%--<ul class="nav nav-tabs">--%>
        <%--<li class="active"><a href="${ctx}/sco/scoScore/listGbyUser">学分查询列表</a></li>--%>
        <%--&lt;%&ndash; <li><a href="${ctx}/sco/scoScore/list">学分详情查询列表</a></li> &ndash;%&gt;--%>
    <%--</ul>--%>
    <div style="padding-top: 20px">
        <form:form id="searchForm" modelAttribute="scoScore" action="${ctx}/sco/scoScore/listGbyUser" method="post"
                   class="form-inline form-content-box">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <input type="hidden" id="orderBy" name="orderBy" value="${page.orderBy}"/>
            <input type="hidden" id="orderByType" name="orderByType" value="${page.orderByType}"/><!--desc向下 asc向上-->
            <div class="row">
                <div class="right-form pull-right">
                    <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
                </div>
                <div class="left-form">
                    <div class="form-group">
                        <label>学号</label>
                        <form:input path="user.no" htmlEscape="false" maxlength="128"/>
                    </div>
                    <div class="form-group">
                        <label>姓名</label>
                        <form:input path="user.name" htmlEscape="false" maxlength="128"/>
                    </div>
                    <div class="form-group">
                        <label class="control-mr control-w60">所属学院</label>
	                    <form:select path="user.office.id" class="form-control" id="collegeId">
	                        <form:option value="" label="所有学院"/>
	                        <form:options items="${fns:findColleges()}" itemLabel="name" itemValue="id"
	                                      htmlEscape="false"/>
	                    </form:select>
                    </div>

                    <div class="form-group">
	                    <label class="control-mr control-w60">所属专业</label>
	                    <form:select path="user.professional" class="form-control"
	                                id="professionalSelect">
	                        <form:option value="" label="所有专业"/>
	                    </form:select>
	                </div>
                </div>
            </div>
        </form:form>
        <sys:message content="${message}"/>
        <table id="contentTable" class="table table-bordered table-condensed table-sort">
            <thead>
            <tr>
                <th>学号</th>
                <th>姓名</th>
                <th data-name="courseScore">
                    <a class="btn-sort" href="javascript:void(0)"><span>课程学分</span><i class="icon-sort"></i></a>
                </th>
                <th data-name="businessScore">
                    <a class="btn-sort" href="javascript:void(0)"><span>创新学分</span><i class="icon-sort"></i></a>
                </th>
                <th data-name="innovateScore">
                    <a class="btn-sort" href="javascript:void(0)"><span>创业学分</span><i class="icon-sort"></i></a>
                </th>
                <th data-name="creditScore">
                    <a class="btn-sort" href="javascript:void(0)"><span>素质学分</span><i class="icon-sort"></i></a>
                </th>
                <%--<th data-name="skillScore">--%>
                    <%--<a class="btn-sort" href="javascript:void(0)"><span>技能学分</span><i class="icon-sort"></i></a>--%>
                <%--</th>--%>
                <th data-name="totalScore">
                    <a class="btn-sort" href="javascript:void(0)"><span>总学分</span><i class="icon-sort"></i></a>
                </th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="scoScore">
                <tr>
                    <td>
                            ${scoScore.user.no}
                    </td>
                    <td>
                            ${scoScore.user.name}
                    </td>
                    <td>
                    	<c:if test="${empty scoScore.courseScore}">0.0</c:if>
                    	<c:if test="${not empty scoScore.courseScore}">
                            ${fns:deleteZero(scoScore.courseScore)}
                    	</c:if>
                    </td>
                    <td>
                    	<c:if test="${empty scoScore.businessScore}">0.0</c:if>
                    	<c:if test="${not empty scoScore.businessScore}">
                            ${fns:deleteZero(scoScore.businessScore)}
                    	</c:if>
                    </td>
                    <td>
                    	<c:if test="${empty scoScore.innovateScore}">0.0</c:if>
                    	<c:if test="${not empty scoScore.innovateScore}">
                            ${fns:deleteZero(scoScore.innovateScore)}
                    	</c:if>
                    </td>
                    <td>
                    	<c:if test="${empty scoScore.creditScore}">0.0</c:if>
                    	<c:if test="${not empty scoScore.creditScore}">
                            ${fns:deleteZero(scoScore.creditScore)}
                    	</c:if>
                    </td>
                    <%--<td>--%>
                    	<%--<c:if test="${empty scoScore.skillScore}">0.0</c:if>--%>
                    	<%--<c:if test="${not empty scoScore.skillScore}">--%>
                            <%--${fns:deleteZero(scoScore.skillScore)}--%>
                    	<%--</c:if>--%>
                    <%--</td>--%>
                    <td>
                    	<c:if test="${empty scoScore.totalScore}">0.0</c:if>
                    	<c:if test="${not empty scoScore.totalScore}">
                            ${fns:deleteZero(scoScore.totalScore)}
                    	</c:if>
                    </td>
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