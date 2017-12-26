<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <title>创新学分认定列表</title>
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/css/credit-module.css">
    <style>
        .form-inline select{
            height: 30px;
            max-width: none;
        }
        .table th{
            background: none;
        }
        .form-inline .form-control{
            width: 160px;
        }
        .form-inline select.form-control {
            width: 174px;
        }
        /*.btn-sort{*/
            /*color: #646464;*/
        /*}*/
        /*.btn-sort:hover,.btn-sort:focus{*/
            /*text-decoration: none;*/
        /*}*/
        /*.btn-sort>span{*/
            /*margin-right: 4px;*/
        /*}*/
        /*.btn-sort:hover{*/
            /*color: #646464;*/
        /*}*/
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
        function view(url){
            window.location.href=url;
        }

        $(function () {
            if('${message}'!=''){
                showModalMessage(1,'${message}');
            }
            $("#ps").val($("#pageSize").val());
        })

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchCreateForm").submit();
        }
      </script>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>创新学分认定</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <div class="form-table-row">
        <form id="searchCreateForm" class="form-inline" modelAttribute="scoProjectVo" action="/a/sco/scoreGrade/createList">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <div class="form-inline-right form-inline-right-w240">
                <form:input type="text" path="scoProjectVo.keyWord" placeholder="项目组成员/项目名称"/>
                <button type="submit" class="btn-oe btn-primary-oe">查询</button>
            </div>

            <div class="form-inline-left form-inline-left-mr240">
                <div class="form-group-inline">
                    <label class="control-mr">开始时间</label>
                    <input  type="text" class="form-control w-date-picker" name="beginDate" id="beginDate"
                          onClick="WdatePicker({maxDate:'#F{$dp.$D(\'endDate\')}'})"
                          value='<fmt:formatDate value="${scoProjectVo.beginDate}" pattern="yyyy-MM-dd"/>'
                   />
                </div>
                <div class="form-group-inline">
                    <label class="control-mr">结束时间</label>
                    <input  type="text" class="form-control w-date-picker" name="endDate" id="endDate"
                       onClick="WdatePicker({minDate:'#F{$dp.$D(\'beginDate\')}'})"
                       value='<fmt:formatDate value="${scoProjectVo.endDate}" pattern="yyyy-MM-dd"/>'/>
                </div>
                <div class="form-group-inline">
                    <label class="control-mr">所属学院</label>
                    <form:select path="scoProjectVo.office.id" class="form-control" id="collegeId">
                        <form:option value="" label="所有学院"/>
                        <form:options items="${fns:findColleges()}" itemLabel="name" itemValue="id"
                                      htmlEscape="false"/>
                    </form:select>
                </div>
                <div class="form-group-inline">
                    <label class="control-mr">所属专业</label>
                    <form:select path="scoProjectVo.user.professional" class="form-control"
                                id="professionalSelect">
                        <form:option value="" label="所有专业"/>
                    </form:select>
                </div>
                <div class="form-group-inline">
                    <label class="control-mr">项目编号</label>
                    <form:input type="text" cssClass="form-control" path="scoProjectVo.projectDeclare.number" />
                </div>
                <div class="form-group-inline">
                    <label class="control-mr">项目类型</label>
                    <form:select id="type" path="scoProjectVo.pType" class="form-control">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('project_style')}" itemLabel="label"
                                   itemValue="value" htmlEscape="false"/>
                    </form:select>
                </div>
                <div class="form-group-inline">
                    <label class="control-mr">项目结果</label>
                    <form:select id="type" path="scoProjectVo.projectDeclare.finalResult" class="form-control">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('project_result')}" itemLabel="label"
                                   itemValue="value" htmlEscape="false"/>
                    </form:select>
                </div>
            </div>
        </form>
        <table class="table table-condensed table-bordered table-hover table-theme-default table-has-sort table-vertical-middle">
            <thead>
            <tr>
                <th>项目编号</th>
                <th>项目名称</th>
                <th class="none-wrap">负责人</th>
                <th>组成员</th>
                <th class="none-wrap">项目类型</th>
                <th class="none-wrap">项目类别</th>
                <th class="none-wrap">项目级别</th>
                <th class="none-wrap">项目结果</th>
                <th class="none-wrap">计划学分</th>
                <th class="none-wrap">认定学分</th>
                <th class="none-wrap">学分配比</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach  items="${page.list}" var="scoProjectVo">
                <tr>
                    <td>${scoProjectVo.projectDeclare.number}</td>
                    <td>${scoProjectVo.projectDeclare.name}</td>
                    <td>${fns:getUserById(scoProjectVo.projectDeclare.leader).name}</td>
                    <td>${scoProjectVo.teamUsers}</td>
                    <td>${fns:getDictLabel(scoProjectVo.pType,"project_style" , "")}</td>
                    <td>${scoProjectVo.projectDeclare.typeString}</td>
                    <td>${scoProjectVo.projectDeclare.levelString}</td>
                    <td>${scoProjectVo.projectDeclare.finalResultString}</td>
                    <td>${fns:deleteZero(scoProjectVo.scoAffirm.scoreStandard)}</td>
                    <td>
                        ${fns:deleteZero(scoProjectVo.score)}
                    </td>
                    <td> ${scoProjectVo.ratioResult}</td>
                    <td  class="none-wrap">
                        <a class="btn-oe btn-primary-oe btn-sm-oe" href="javascript:void (0)" onclick="view('/a/sco/scoreGrade/createView?id=${scoProjectVo.scoAffirm.id}')">查看</a>
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