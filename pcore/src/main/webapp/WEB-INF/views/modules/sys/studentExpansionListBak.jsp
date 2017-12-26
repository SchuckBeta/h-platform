<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=8,IE=9,IE=10"/>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-store">
    <%--<meta name="decorator" content="site-decorator">--%>
    <title>学生信息管理</title>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/common/common-css/pagenation.css"/>
    <link rel="stylesheet" type="text/css" href="/common/common-css/backtable.css"/>
    <link rel="stylesheet" type="text/css" href="/css/talentsList.css"/>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.css"/>
    <script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.js"></script>
    <link href="${ctxStatic}/jquery-jbox/2.3/Skins/Bootstrap/jbox.min.css" rel="stylesheet"/>
    <script src="${ctxStatic}/jquery-jbox/2.3/jquery.jBox-2.3.min.js" type="text/javascript"></script>
    <script src="${ctxStatic}/common/initiate.js" type="text/javascript"></script>
    <script type="text/javascript" src="${ctxStatic}/My97DatePicker/WdatePicker.js"></script>
    <script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
    <style>
        .header, .footerBox {
            display: none;
        }
        .modal-backdrop{
            background-color:#fff;
        }
        @media (min-width:768px){
            .search-form-wrap{
                width:357px;
            }
            .condition-main{
                margin-right: 357px;
            }
        }
    </style>
    <script type="text/javascript">var ctx = '${ctx}', ctxStatic = '${ctxStatic}';</script>
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

        function create() {
            window.location = "${ctx}/sys/studentExpansion/form";
        }
    </script>
</head>
<body>
<div class="container-fluid" role="main">
    <div class="table-page">
        <div class="edit-bar clearfix">
            <div class="edit-bar-left">
                <span>学生库</span>
                <i class="line"></i>
            </div>
        </div>
        <form:form id="searchForm" modelAttribute="studentExpansion" action="${ctx}/sys/studentExpansion/" method="post"
                   class="breadcrumb form-search form-horizontal">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <div class="row">
                <div class="search-form-wrap">
                    <div class="form-inline">
                        <div class="form-group">
                            <label class="sr-only" for="myFind">关键字</label>
                            <form:input id="myFind" class="form-control search-input" type="text" path="myFind"
                                        htmlEscape="false" placeholder="关键字"/>
                        </div>
                        <input id="btnSubmit" class="btn btn-search" type="submit" value="查询"/>
                        <input id="btnAdd" onclick="create()" class="btn btn-add" type="button" value="添加"/>
                        <input id="btnSubmit1"  class="btn btn-search" type="button" value="批量删除" data-toggle="modal" data-target="#myModal"/>
                    </div>
                </div>
                <div class="condition-main">
                    <div class="row">
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="collegeId" class="control-label">学院</label>
                                <div class="input-box">
                                    <form:select path="user.office.id" class="input-medium form-control" id="collegeId">
                                        <form:option value="" label="所有学院"/>
                                        <form:options items="${fns:findColleges()}" itemLabel="name" itemValue="id"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="professionalSelect" class="control-label">专业</label>
                                <div class="input-box">
                                    <form:select path="user.professional" class="input-medium form-control"
                                                 id="professionalSelect">
                                        <form:option value="" label="所有专业"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="currState" class="control-label">学生现状</label>
                                <div class="input-box">
                                    <form:select id="currState" path="currState" class="input-medium form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('current_sate')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="nowProject" class="control-label">在研项目</label>
                                <div class="input-box">
                                    <form:select id="nowProject" path="nowProject" class="input-medium form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('yes_no')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="userEducation" class="control-label">学历</label>
                                <div class="input-box">
                                    <form:select id="userEducation" path="user.education"
                                                 class="input-medium form-control">
                                        <form:option value="" label="所有学历"/>
                                        <form:options items="${fns:getDictList('enducation_level')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="userDegree" class="control-label">学位</label>
                                <div class="input-box">
                                    <form:select id="userDegree" path="user.degree" class="input-medium form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('degree_type')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form:form>
        <sys:message content="${message}"/>
        <table id="contentTable" class="table table-hover table-bordered   table-condensed"
               style="border-collapse: collapse">
            <thead>
            <tr>
                <th><input type="checkbox" id="check_all" data-flag="false"></th>
                <th>学号</th>
                <th>姓名</th>
                <th>性别</th>
                <th>在研项目</th>
                <th>现状</th>
                <th>学院</th>
                <th>专业</th>
                <th>学历</th>
                <th>学位</th>
                <th>毕业时间</th>
                <th>休学时间</th>
                <th>是否公开</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="studentExpansion">
                <tr>
                        <td class="checkone"><input type="checkbox" value="${studentExpansion.id}" name="boxTd"></td>
                    <td>
                            ${studentExpansion.user.no}
                    </td>
                    <td>
                            ${studentExpansion.user.name}
                    </td>
                    <td>
                            ${fns:getDictLabel(studentExpansion.user.sex,"sex" , "")}
                    </td>
                    <td>
                            ${fns:getDictLabel(studentExpansion.nowProject,"yes_no" , "")}
                    </td>
                    <td>
                            ${fns:getDictLabel(studentExpansion.currState,"current_sate" , "")}
                    </td>
                    <td>
                            ${studentExpansion.user.office.name}
                    </td>
                    <td>
                            ${fns:getOffice(studentExpansion.user.professional).name}
                    </td>
                    <td>
                            ${fns:getDictLabel(studentExpansion.user.education, 'enducation_level', '')}
                    </td>
                    <td>
                            ${fns:getDictLabel(studentExpansion.user.degree, 'degree_type', '')}
                    </td>
                    <td>
                            ${studentExpansion.graduationStr}
                    </td>
                    <td>
                            ${studentExpansion.temporaryDateStr}
                    </td>
                    <td>
                            ${fns:getDictLabel(studentExpansion.isOpen,"yes_no" , "")}
                    </td>
                    <td style="white-space:nowrap">
                        <a href="${ctx}/sys/studentExpansion/form?id=${studentExpansion.id}"
                           class="btn btn-watch">编辑/查看</a>
                        <a href="${ctx}/sys/studentExpansion/delete?id=${studentExpansion.id}" class="btn btn-delete"
                           onclick="return confirmx('确认要删除该学生信息吗？', this.href)">删除</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        ${page.footer}
    </div>
</div>

<!-- Modal  批量删除弹窗 -->
<div id="myModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title">批量删除</h4>
            </div>
            <div class="modal-body">
                <p class="text-center" style="font-size: 14px"  id="selectArea">确认要删除所选学生信息吗？</p>
                <div class="buffer_gif" style="text-align:center;padding:20px 0px;display:none;" id="bufferImg">
                    <img src="/img/jbox-loading1.gif" alt="缓冲图片">
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn"  aria-hidden="true" id="confirmBtn" onclick="doBatch('/a/sys/studentExpansion/deleteBatch');">确定</button>
                <button class="btn btn-default" data-dismiss="modal">取消</button>
            </div>
        </div>
    </div>
</div>

<script src="/js/student/checkboxChoose.js"></script>  <!--checkbox 全选js -->
</body>
</html>