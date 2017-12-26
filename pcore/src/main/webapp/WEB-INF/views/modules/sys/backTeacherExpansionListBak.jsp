<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>导师库管理</title>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=8,IE=9,IE=10"/>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-store">
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="/common/common-css/pagenation.css"/>
    <link rel="stylesheet" type="text/css" href="/common/common-css/backtable.css"/>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/jquery-jbox/2.3/Skins/Bootstrap/jbox.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/talentsList.css"/>
    <link rel="stylesheet" type="text/css" href="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.css"/>
    <script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/jquery-jbox/2.3/jquery.jBox-2.3.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/My97DatePicker/WdatePicker.js"></script>
    <script type="text/javascript" src="${ctxStatic}/common/mustache.min.js"></script>
    <script type="text/javascript" src="${ctxStatic}/common/initiate.js"></script>
    <script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>

    <script type="text/javascript">var ctx = '${ctx}', ctxStatic = '${ctxStatic}';</script>
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
        function create() {
            window.location = "${ctx}/sys/backTeacherExpansion/form?operateType=1";
        }
    </script>
</head>
<body>
<div class="container-fluid">
    <div class="table-page">
        <div class="edit-bar clearfix">
            <div class="edit-bar-left">
                <span>导师资源</span>
                <i class="line"></i>
            </div>
        </div>
        <form:form id="searchForm" modelAttribute="backTeacherExpansion" action="${ctx}/sys/backTeacherExpansion/"
                   method="post" class="breadcrumb form-search form-horizontal">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <div class="row">
                <div class="search-form-wrap">
                    <div class="form-inline text-right">
                        <form:input  class="form-control search-input" type="text" path="keyWords"   htmlEscape="false" placeholder="关键字"/>
                        <input id="btnSubmit" class="btn btn-search" type="submit" value="查询"/>
                        <input id="btnAdd" onclick="create()" class="btn btn-add" type="button" value="添加"/>
                        <input id="btnSubmit1"  class="btn btn-search" type="button" value="批量删除" data-toggle="modal" data-target="#myModal"/>
                    </div>
                </div>
                <div class="condition-main">
                    <div class="row">

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
                                <label for="teachertype" class="control-label">导师来源</label>
                                <div class="input-box">
                                    <form:select path="teachertype" id="teachertype" class="input-medium form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('master_type')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="serviceIntention" class="control-label">服务意向</label>
                                <div class="input-box">
                                    <form:select  path="serviceIntention" class="input-medium form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('master_help')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="user.education" class="control-label">学历</label>
                                <div class="input-box">
                                    <form:select  path="user.education" class="input-medium form-control">
                                        <form:option value="" label="--请选择--"/>
                                        <form:options items="${fns:getDictList('enducation_level')}" itemLabel="label"
                                                      itemValue="value"
                                                      htmlEscape="false"/>
                                    </form:select>
                                </div>
                            </div>
                        </div>

                        <div class="col-sm-6 col-md-4 col-lg-2">
                            <div class="form-group">
                                <label for="user.degree" class="control-label">学位</label>
                                <div class="input-box">
                                    <form:select  path="user.degree" class="input-medium form-control">
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
        <table id="contentTable" class="table  table-bordered table-condensed">
            <thead>
            <tr>
                <th><input type="checkbox" id="check_all" data-flag="false"></th>
                <th>职工号</th>
                <th>姓名</th>
                <th>性别</th>
                <th>导师来源</th>
                <th>在研项目</th>
                <th>服务意向</th>
                <th>职务</th>
                <th>职称</th>
                <th>学历</th>
                <th>学位</th>
                <th>学院</th>
                <th>专业</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="backTeacherExpansion">
                <tr>
                    <td class="checkone"><input type="checkbox" value="${backTeacherExpansion.id}" name="boxTd"></td>
                    <td>
                            ${backTeacherExpansion.user.no}
                    </td>
                    <td>
                            ${backTeacherExpansion.user.name }
                    </td>
                    <td>
                            ${fns:getDictLabel(backTeacherExpansion.user.sex, 'sex', '')}
                    </td>
                    <td>
                            ${fns:getDictLabel(backTeacherExpansion.teachertype, 'master_type', '')}
                    </td>
                    <td>
                            ${fns:getDictLabel(backTeacherExpansion.nowProject,"yes_no" , "")}
                    </td>
                    <td>
                            ${fns:getDictLabel(backTeacherExpansion.serviceIntention, 'master_help', '')}
                    </td>
                    <td>
                            ${backTeacherExpansion.postTitle}
                    </td>
                    <td>
                            ${fns:getDictLabel(backTeacherExpansion.technicalTitle, 'postTitle_type', '')}
                    </td>
                    <td>
                            ${fns:getDictLabel(backTeacherExpansion.user.education, 'enducation_level', '')}
                    </td>
                    <td>
                            ${fns:getDictLabel(backTeacherExpansion.user.degree, 'degree_type', '')}
                    </td>
                    <td>
                            ${backTeacherExpansion.user.office.name}
                    </td>
                    <td>
                            ${fns:getOffice(backTeacherExpansion.user.professional).name}
                    </td>
                    <td style="white-space:nowrap">
                        <a href="${ctx}/sys/backTeacherExpansion/form?id=${backTeacherExpansion.id}"
                           class="btn">编辑/查看</a>
                        <a href="${ctx}/sys/backTeacherExpansion/delete?id=${backTeacherExpansion.id}" class="btn"
                           onclick="return confirmx('确认要删除该导师信息吗？', this.href)">删除</a>
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
                <p class="text-center" style="font-size: 14px">确认要删除所选导师信息吗？</p>
                <div class="buffer_gif" style="text-align:center;padding:20px 0px;display:none;" id="bufferImg">
                    <img src="/img/jbox-loading1.gif" alt="缓冲图片">
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn"  aria-hidden="true" id="confirmBtn" onclick="doBatch('/a/sys/backTeacherExpansion/deleteBatch');">确定</button>
                <button class="btn btn-default" data-dismiss="modal">取消</button>
            </div>
        </div>
    </div>
</div>
<script src="/js/student/checkboxChoose.js"></script>  <!--checkbox 全选js -->
</body>
</html>