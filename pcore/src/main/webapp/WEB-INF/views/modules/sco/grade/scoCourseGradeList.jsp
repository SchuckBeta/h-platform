<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>课程学分认定列表</title>
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/css/credit-module.css">
    <style>
        .form-inline select {
            height: 30px;
            max-width: none;
        }

        .table th {
            background: none;
        }

        .form-inline .form-control {
            width: 160px;
        }

        .form-inline select.form-control {
            width: 174px;
        }
    </style>
    <script type="text/javascript">
        $(document).ready(function () {
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
                            if (val.id == "${scoCourseVo.user.professional}") {
                                $("#professionalSelect").append('<option selected="selected" value="' + val.id + '">' + val.name + '</option>')
                            } else {
                                $("#professionalSelect").append('<option value="' + val.id + '">' + val.name + '</option>')
                            }

                        })
                    }
                });

            })
            $("#collegeId").trigger('change');
        });


        //查看详情
        function view(url) {
            window.location.href = url;
        }

        $(function () {
            if ('${message}' != '') {
                showModalMessage(1, '${message}');
            }
            $("#ps").val($("#pageSize").val());
        })

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchCourseForm").submit();
        }
    </script>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>课程学分认定</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <div class="form-table-row">
        <form id="searchCourseForm" modelAttribute="scoCourseVo" action="/a/sco/scoreGrade/courseList"
              class="form-inline">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>

            <div class="form-inline-right form-inline-right-w240">
                <form:input type="text" path="scoCourseVo.keyWord" placeholder="关键字"/>
                <button type="submit" class="btn-oe btn-primary-oe">查询</button>
            </div>
            <div class="form-inline-left form-inline-left-mr240">
                <div class="form-group-inline">
                    <label class="control-mr control-w60">学号</label>
                    <form:input type="text" class="form-control" path="scoCourseVo.user.no"/>
                </div>
                <div class="form-group-inline">
                    <label class="control-mr control-w60">所属学院</label>
                    <form:select path="scoCourseVo.office.id" class="form-control" id="collegeId">
                        <form:option value="" label="所有学院"/>
                        <form:options items="${fns:findColleges()}" itemLabel="name" itemValue="id"
                                      htmlEscape="false"/>
                    </form:select>
                </div>
                <div class="form-group-inline">
                    <label class="control-mr control-w60">所属专业</label>
                    <form:select path="scoCourseVo.user.professional" class="form-control"
                                 id="professionalSelect">
                        <form:option value="" label="所有专业"/>
                    </form:select>
                </div>
                <div class="form-group-inline">
                    <label class="control-mr control-w60">课程代码</label>
                    <form:input type="text" class="form-control" path="scoCourseVo.scoCourse.code"/>
                </div>
                <div class="form-group-inline">
                    <label class="control-mr control-w60">课程类型</label>
                    <form:select id="type" path="scoCourseVo.scoCourse.type" class="form-control">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('0000000102')}" itemLabel="label"
                                      itemValue="value" htmlEscape="false"/>
                    </form:select>
                </div>
                <%--  <div class="form-group-inline">
                      <label class="control-mr control-w60">课程状态</label>
                      <form:select path="scoCourseVo.scoApply.courseStatus" class="form-control">
                          <form:option value="" label="请选择"/>
                          <form:option value="1" label="达标"/>
                          <form:option value="2" label="未达标"/>
                      </form:select>
                  </div>--%>
                <div class="form-group-inline">
                    <label class="control-mr control-w60">审核状态</label>
                    <form:select path="scoCourseVo.scoApply.auditStatus" class="form-control">
                        <form:option value="" label="请选择"/>
                        <form:option value="1" label="待提交认定"/>
                        <form:option value="2" label="待审核"/>
                        <form:option value="3" label="未通过"/>
                        <form:option value="4" label="通过"/>
                    </form:select>
                </div>
            </div>
        </form>
        <table class="table table-condensed table-bordered table-hover table-theme-default table-has-sort table-vertical-middle">
            <thead>
            <tr>
                <th>学号</th>
                <th class="none-wrap">姓名</th>
                <th class="none-wrap">课程代码</th>
                <th class="none-wrap">课程名</th>
                <th class="none-wrap">课程性质</th>
                <th class="none-wrap">课程类型</th>
                <th class="none-wrap">完成课时</th>
                <th class="none-wrap">成绩</th>
                <th class="none-wrap">计划学分</th>
                <th class="none-wrap">认定学分</th>
                <%--  <th class="none-wrap">课程状态</th>--%>
                <th class="none-wrap">审核状态</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="scoCourseVo">
                <tr>
                    <td>${scoCourseVo.user.no}</td>
                    <td>${scoCourseVo.user.name}</td>
                    <td>${scoCourseVo.scoCourse.code}</td>
                    <td>${scoCourseVo.scoCourse.name}</td>
                        <%--<td>${scoCourseVo.scoCourse.nature}</td>
                        <td>${scoCourseVo.scoCourse.type}</td>
                        --%>
                    <td>${fns:getDictLabel(scoCourseVo.scoCourse.nature, '0000000108', '')}</td>
                    <td>${fns:getDictLabel(scoCourseVo.scoCourse.type, '0000000102', '')}</td>

                    <td>${fns:deleteZero(scoCourseVo.scoApply.realTime)}</td>
                    <td>${fns:deleteZero(scoCourseVo.scoApply.realScore)}</td>
                    <td>${fns:deleteZero(scoCourseVo.scoCourse.planScore)}</td>
                    <td>
                        <c:choose>
                            <c:when test="${scoCourseVo.scoApply.auditStatus eq '1' ||scoCourseVo.scoApply.auditStatus eq '2' }">

                            </c:when>
                            <c:otherwise>
                                ${fns:deleteZero(scoCourseVo.scoApply.score)}
                            </c:otherwise>
                        </c:choose>
                            <%--${fns:deleteZero(scoCourseVo.scoApply.score)}--%>
                    </td>
                        <%--<td>
                            <c:if test="${scoCourseVo.scoApply.courseStatus eq '1'}">
                                <span class="danger-color">课程未达标</span>
                            </c:if>
                            <c:if test="${scoCourseVo.scoApply.courseStatus eq '2'}">
                                <span class="success-color">课程已达标</span>
                            </c:if>
                        </td>--%>
                    <td>
                        <c:if test="${scoCourseVo.scoApply.auditStatus eq '1'}">
                        <span class="primary-color">待提交认定</span>
                        </c:if>
                        <c:if test="${scoCourseVo.scoApply.auditStatus eq '2'}">
                        <span class="primary-color">
                                待审核
                               <%-- <a target="_blank" href="${ctx}/act/task/processActMap?proInstId=${scoCourseVo.scoApply.procInsId}">
                                    待审核
                                </a>--%>

                            </span>
                        </c:if>
                        <c:if test="${scoCourseVo.scoApply.auditStatus eq '3'}">
                        <span class="fail-color">未通过</span>
                        </c:if>
                        <c:if test="${scoCourseVo.scoApply.auditStatus eq '4'}">
                        <span class="info-color">通过</span>
                        </c:if>
                    <td class="none-wrap">
                        <a class="btn-oe btn-primary-oe btn-sm-oe"
                           onclick="view('/a/scoapply/view?id=${scoCourseVo.scoApply.id}')">查看</a>
                        <c:if test="${scoCourseVo.scoApply.auditStatus eq '2'}">
                            <a class="btn-oe btn-primary-oe btn-sm-oe"
                               onclick="view('/a/scoapply/auditView?id=${scoCourseVo.scoApply.id}')">审核</a>
                        </c:if>
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