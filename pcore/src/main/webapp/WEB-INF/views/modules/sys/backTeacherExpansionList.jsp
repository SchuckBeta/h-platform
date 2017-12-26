<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>

    <meta charset="UTF-8">
    <title>导师库管理</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <script type="text/javascript" src="/js/backTable/sort.js"></script><!--排序js-->
    <script type="text/javascript">
        $(document).ready(function () {
            $("#ps").val($("#pageSize").val());
            $('.pagination_num').removeClass('row')
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
    <style>
        .btn-sort,.btn-sort:hover,.btn-sort:focus{
            text-decoration: none;
            color: #646464;
        }
        .form-top-search .condition-checkbox{
            width: auto;
        }
        .condition-checkbox .control-label{
            width: 90px;
        }
        .condition-checkbox .controls{
            margin-left: 110px;
        }
        .condition-checkbox span{
            min-height: 20px;
            padding-left: 20px;
            display: inline-block;
            padding-top: 5px;
            margin-right: 10px;
            margin-bottom: 0;
            vertical-align: middle;
        }
        .condition-checkbox span input[type="radio"], .condition-checkbox span input[type="checkbox"] {
            float: left;
            margin-left: -20px;
        }
        .table>thead>tr>th{
            white-space: nowrap;
            text-align: center;
            vertical-align: middle;
        }
        .table>tbody>tr>td{
            text-align: center;
        }
        .table>tbody>tr:last-child>td{
            white-space: nowrap;
        }
    </style>
</head>
<body>
<div class="container-fluid container-fluid-oe">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>导师资源</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form id="searchForm" modelAttribute="backTeacherExpansion" action="${ctx}/sys/backTeacherExpansion/"
               method="post" class="form-top-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <div class="search-form-wrap form-inline">
            <form:input class="search-input input-medium" type="text" path="keyWords" htmlEscape="false"
                        placeholder="职工号/姓名/职务/职称"/>
            <input id="btnSubmit" class="btn btn-back-oe btn-primaryBack-oe" type="submit" value="查询"/>
            <input id="btnAdd" onclick="create()" class="btn btn-back-oe btn-primaryBack-oe" type="button" value="添加"/>
            <input id="btnSubmit1" class="btn btn-back-oe btn-primaryBack-oe" type="button" style="width: auto; height: auto;" value="批量删除"
                   data-toggle="modal"
                   data-target="#myModal"/>
            <input type="hidden" id="orderBy" name="orderBy" value="${page.orderBy}"/>
            <input type="hidden" id="orderByType" name="orderByType" value="${page.orderByType}"/>
        </div>
        <div class="condition-main form-horizontal">
            <div class="condition-row">

                <div class="condition-item">
                    <div class="control-group">
                        <label for="teachertype" class="control-label">导师来源</label>
                        <div class="controls">
                            <form:select path="teachertype" id="teachertype" class="input-medium form-control">
                                <form:option value="" label="--请选择--"/>
                                <form:options items="${fns:getDictList('master_type')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
				<div class="condition-item">
                    <div class="control-group">
                        <label for="category" class="control-label">导师类型</label>
                        <div class="controls">
                            <form:select path="category" id="category" class="input-medium form-control">
                                <form:option value="" label="--请选择--"/>
                                <form:options items="${fns:getDictList('0000000215')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>


                <div class="condition-item">
                    <div class="control-group">
                        <label for="user.education" class="control-label">学历</label>
                        <div class="controls">
                            <form:select path="user.education" class="input-medium form-control">
                                <form:option value="" label="--请选择--"/>
                                <form:options items="${fns:getDictList('enducation_level')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>

                <div class="condition-item">
                    <div class="control-group">
                        <label for="user.degree" class="control-label">学位</label>
                        <div class="controls">
                            <form:select path="user.degree" class="input-medium form-control">
                                <form:option value="" label="--请选择--"/>
                                <form:options items="${fns:getDictList('degree_type')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="workUnitType" class="control-label">单位类别</label>
                        <div class="controls">
                            <form:select path="workUnitType" id="workUnitType" class="input-medium form-control">
                                <form:option value="" label="--请选择--"/>
                                <form:options items="${fns:getDictList('0000000218')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item condition-checkbox">
                    <div class="control-group">
                        <label for="serviceIntentionIds" class="control-label" style="width: 64px;">服务意向</label>
                        <div class="controls" style=" margin-left: 79px;">
                            <form:checkboxes path="serviceIntentionIds" items="${fns:getDictList('master_help')}"
                                             itemValue="value"
                                             itemLabel="label" htmlEscape="false"/>
                        </div>
                    </div>
                </div>
                <div class="condition-item condition-checkbox" style="width: 100%">
                    <div class="control-group">
                        <label for="curJoinStr" class="control-label">当前指导</label>
                        <div class="controls">
                             <form:checkboxes path="curJoinStr" items="${fns:getPublishDictList()}" 
                                             itemValue="value"
                                             itemLabel="label" htmlEscape="false"/>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable" class="table table-bordered table-condensed table-theme-default table-hover">
        <thead>
        <tr>
            <th><input type="checkbox" id="check_all" data-flag="false"></th>
            <th data-name="u.no"><a class="btn-sort" href="javascript:void(0)"><span>职工号</span><i class="icon-sort"></i></a></th>
            <th>姓名</th>
            <th>性别</th>
            <th>导师来源</th>
            <th>指导<br>项目/大赛</th>
            <th>服务意向</th>
            <th>职务</th>
            <th>职称</th>
            <th>学历</th>
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
                        ${backTeacherExpansion.curJoin}
                </td>
                <td>
                        ${backTeacherExpansion.serviceIntentionStr}
                </td>
                <td>
                        ${backTeacherExpansion.postTitle}
                </td>
                <td>
                        ${backTeacherExpansion.technicalTitle}
                </td>
                <td>
                        ${fns:getDictLabel(backTeacherExpansion.user.education, 'enducation_level', '')}
                </td>
                <td>
                    <a href="${ctx}/sys/backTeacherExpansion/form?id=${backTeacherExpansion.id}"
                       class="btn btn-back-oe btn-primaryBack-oe btn-small">编辑</a>
                    <a href="${ctx}/sys/backTeacherExpansion/delete?id=${backTeacherExpansion.id}" class="btn btn-small"
                       onclick="return confirmx('确认要删除该导师信息吗？', this.href)">删除</a>
                </td>

            </tr>
        </c:forEach>
        </tbody>
    </table>
    ${page.footer}
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
                <button class="btn" aria-hidden="true" id="confirmBtn"
                        onclick="doBatch('/a/sys/backTeacherExpansion/deleteBatch');">确定
                </button>
                <button class="btn btn-default" data-dismiss="modal">取消</button>
            </div>
        </div>
    </div>
</div>
<script src="/js/student/checkboxChoose.js"></script>  <!--checkbox 全选js -->
</body>
</html>