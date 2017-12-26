<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@include file="/WEB-INF/views/include/backtable.jsp" %>
<html>
<head>
    <title>课程管理</title>
    <link rel="stylesheet" type="text/css" href="/static/common/tablepage.css"/>
    <link rel="stylesheet" type="text/css" href="/css/state/titlebar.css">
    <link rel="stylesheet" type="text/css" href="/css/topSearchForm.css">
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
    <style type="text/css">
        td, th {
            text-align: center !important;
        }

        th {
            background: #f4e6d4 !important;
        }

        .ul_info {
            margin-left: -32px;
        }

        .btns {
            float: right !important;
        }

        #btnSubmit {
            background: #e9432d !important;
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
<sys:message content="${message}"/>
<div class="container-fluid" style="padding-right:20px;padding-left: 20px;">
    <div class="edit-bar edit-bar-tag clearfix" style="margin-top: 30px">
        <div class="edit-bar-left">
            <span>课程管理</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <ul class="nav nav-tabs nav-lesson-tabs">
        <li class="active"><a href="${ctx}/course/list">课程列表</a></li>
        <li>
            <a href="${ctx}/course/form">课程添加</a>
        </li>
    </ul>
    <div class="tab-pane active">
        <form:form id="searchForm" modelAttribute="course" action="${ctx}/course/list" method="post"
                   class="form-inline form-lesson-box">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <div class="row">
                <div class="right-form pull-right">
                    <form:input path="name" htmlEscape="false" maxlength="200" class="input-medium"
                                placeholder='名称或授课教师'/>
                    <input id="btnSubmit" class="btn btn-danger" type="submit" value="查询"/>
                </div>
                <div class="left-form">
                    <div class="form-group">
                        <label for="categoryId">专业课程分类</label>
                        <form:select path="categoryId" class="input-medium">
                            <form:option value="" label="--请选择--"/>
                            <form:options items="${fns:getDictList('0000000086')}" itemLabel="label" itemValue="value"
                                          htmlEscape="false"/>
                        </form:select>
                    </div>
                    <div class="form-group">
                        <label for="type">课程类型分类</label>
                        <form:select path="type" class="input-medium">
                            <form:option value="" label="--请选择--"/>
                            <form:options items="${fns:getDictList('0000000078')}" itemLabel="label" itemValue="value"
                                          htmlEscape="false"/>
                        </form:select>
                    </div>
                    <div class="form-group">
                        <label for="status">状态分类</label>
                        <form:select path="status" class="input-medium">
                            <form:option value="" label="--请选择--"/>
                            <form:options items="${fns:getDictList('0000000082')}" itemLabel="label" itemValue="value"
                                          htmlEscape="false"/>
                        </form:select>
                    </div>
                    <div class="form-group">
                        <label for="publishFlag">是否发布</label>
                        <form:select path="publishFlag" class="input-medium">
                            <form:option value="" label="--请选择--"/>
                            <form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value"
                                          htmlEscape="false"/>
                        </form:select>
                    </div>
                </div>

            </div>
        </form:form>
        <table id="contentTable" class="table table-hover table-bordered table-condensed table-no-radius">
            <thead>
            <tr>
                <th>名称</th>
                <th>授课教师</th>
                <th>专业课程分类</th>
                <th>课程类型分类</th>
                <th>状态分类</th>
                <th>是否发布</th>
                <th>是否置顶</th>
                <th>发布时间</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="course">
                <tr>
                    <td>
                            ${fns:abbr(course.name,50)}
                    </td>
                    <td>
                            ${course.teacherNames}
                    </td>
                    <td>
                            ${course.categoryNames}
                    </td>
                    <td>
                            ${fns:getDictLabel(course.type, '0000000078', '')}
                    </td>
                    <td>
                            ${fns:getDictLabel(course.status, '0000000082', '')}
                    </td>
                    <td>
                            ${fns:getDictLabel(course.publishFlag,"yes_no" , "")}
                    </td>
                    <td>
                            ${fns:getDictLabel(course.topFlag,"yes_no" , "")}
                    </td>
                    <td>
                        <fmt:formatDate value="${course.publishDate}" pattern="yyyy-MM-dd"/>
                    </td>
                    <td style="white-space:nowrap">
                        <a href="${ctx}/course/form?id=${course.id}" class="btn">修改</a>
                        <a href="${ctx}/course/delete?id=${course.id}"
                           onclick="return confirmx('确认要删除该课程吗？', this.href)" class="btn">删除</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        ${page.footer}
    </div>


</div>
</body>
</html>