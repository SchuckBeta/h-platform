<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>国创项目中期评级</title>
    <%@include file="/WEB-INF/views/include/backCommon.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
            //左侧菜单加入数字
            $('#sub_list_wrap', window.parent.document).find('li a').each(function () {
                var hrefReg = $(this).attr('href');
                var count = "${page.todoCount}";
                if (hrefReg.indexOf("middleAuditList") != -1) {
                    if (count > 0) {
                        if (count > 99) {
                            count = "99+";
                        }
                        if ($(this).find('i').size() > 0) {
                            $(this).find('i').text(count);
                        } else {
                            $(this).append('<i class="unread-tag" style="box-sizing: border-box">' + count + '</i>');
                        }
                    } else {
                        $(this).find('i').detach();
                    }
                    return false;
                }
            });
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
                location.reload();
            });
        }


    </script>
    <style>
        label.error {
            background-position: 0px 3px;
        }

        .redSpan {
            color: red;
            font-weight: 900;
        }
    </style>
</head>
<body>

<div class="content_panel">
    <div class="edit-bar edit-bar-tag clearfix">
        <div class="edit-bar-left">
            <span>中期检查</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form id="searchForm" modelAttribute="act" action="${ctx}/state/middleRatingList" method="post"
               class="form-top-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <div class="search-form-wrap form-inline">
            <button id="btnSubmit" class="btn btn-back-oe btn-primaryBack-oe" type="submit">查询</button>
            <button id="btnSubmit1" class="btn btn-back-oe btn-primaryBack-oe" type="button" data-toggle="modal" style="width: auto; height: auto"
                    data-target="#myModal">批量审核
            </button>
        </div>
        <div class="condition-main form-horizontal">
            <div class="condition-row">
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['number']" class="control-label">项目编号</label>
                        <div class="controls">
                            <form:input type="text" path="map['number']" cssClass="form-control input-medium"/>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['name']" class="control-label">项目名称</label>
                        <div class="controls">
                            <form:input type="text" path="map['name']" cssClass="form-control input-medium"/>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['type']" class="control-label">项目类别</label>
                        <div class="controls">
                            <form:select path="map['type']" cssClass="form-control input-medium">
                                <form:option value="" label="所有项目类别"/>
                                <form:options items="${fns:getDictList('project_type')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label for="map['level']" class="control-label">项目级别</label>
                        <div class="controls">
                            <form:select path="map['level']" cssClass="form-control input-medium">
                                <form:option value="" label="请选择"/>
                                <form:options items="${fns:getDictList('project_degree')}" itemLabel="label"
                                              itemValue="value"
                                              htmlEscape="false"/>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="condition-item">
                    <div class="control-group">
                        <label path="map['leader']" class="control-label">负责人</label>
                        <div class="controls">
                            <form:input type="text" path="map['leader']" cssClass="form-control input-medium"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
    <sys:message content="${message}"/>
    <table id="contentTable" class="table table-bordered table-condensed table-hove table-theme-default table-center">
        <thead>
        <tr>
            <th>项目编号</th>
            <th>项目名称</th>
            <th width="72">项目类别</th>
            <th width="90">负责人</th>
            <th>项目组成员</th>
            <th width="72">指导老师</th>
            <th width="72">项目级别</th>
            <th>中期评分</th>
            <th>项目审核状态</th>
            <th class="white-nowrap">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="act">
            <tr>
                <td>
                        ${act.status == "other"? act.projectDeclare.number :act.vars.map.number} <%--${act.vars.map.number}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.name :act.vars.map.name} <%--${act.vars.map.name}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.typeString :fns:getDictLabel(act.vars.map.type, "project_type", "")} <%--${fns:getDictLabel(act.vars.map.type, "project_type", "")}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.leaderString :act.vars.map.leader} <%--${act.vars.map.leader}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.snames :act.vars.map.teamList} <%--${act.vars.map.teamList}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.tnames :act.vars.map.teacher} <%--${act.vars.map.teacher}--%>
                </td>
                <td>
                        ${act.status == "other"? act.projectDeclare.levelString :fns:getDictLabel(act.vars.map.level, "project_degree", "")} <%--${fns:getDictLabel(act.vars.map.level, "project_degree", "")}--%>
                </td>
                <td>
                        ${act.status == "other"? "" :act.vars.map.midScore} <%--${act.vars.map.midScore}--%>
                </td>
                <td>

                    <c:if test="${act.status=='todo'||act.status=='claim'}">
                        <c:set var="projectDeclare" value="${pj:getProjectDeclareListVoById(act.vars.map.id)}" />
                        <%-- <a href="${ctx}/act/task/processMap?procDefId=${act.task.processDefinitionId}&proInstId=${act.task.processInstanceId}" target="_blank">待${act.taskName}</a> --%>
                    	<a href="${ctx}/actyw/actYwGnode/designView/${projectDeclare.status_code}?groupId=${projectDeclare.groupId}" target="_blank">
                             ${pj:getAuditStatus(projectDeclare.status_code)}
                            </a>
                    </c:if>
                    <c:if test="${act.status=='finish'}">
                        <c:set var="projectDeclare" value="${pj:getProjectDeclareListVoById(act.vars.map.id)}" />
                        ${pj:getAuditStatus(projectDeclare.status_code)}
                    </c:if>
                    <c:if test="${act.status=='other'}">
                        <c:set var="projectDeclare" value="${pj:getProjectDeclareListVoById(act.projectDeclare.id)}" />
                        <%-- <a href="${ctx}/act/task/processMap2?proInsId=${act.projectDeclare.procInsId}" target="_blank">待${act.taskName}</a> --%>
                        <a href="${ctx}/actyw/actYwGnode/designView/${projectDeclare.status_code}?groupId=${projectDeclare.groupId}" target="_blank">
                         ${pj:getAuditStatus(projectDeclare.status_code)}
                        </a>
                    </c:if>
                    <c:if test="${act.status=='middleScore'}">
                        <c:set var="projectDeclare" value="${pj:getProjectDeclareListVoById(act.vars.map.id)}" />
                        <%-- <a href="${ctx}/act/task/processMap2?proInsId=${act.histTask.processInstanceId}" target="_blank">${act.taskName}</a> --%>
                        <a href="${ctx}/actyw/actYwGnode/designView/${projectDeclare.status_code}?groupId=${projectDeclare.groupId}" target="_blank">
                        ${pj:getAuditStatus(projectDeclare.status_code)}
                        </a>
                    </c:if>

                </td>
                <td>
                    <c:if test="${act.status=='claim'}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small" href="javascript:void(0)"
                           onclick="claim('${act.task.id}');">签收</a>
                    </c:if>
                    <c:if test="${act.status=='todo'}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                           href="${ctx}/act/task/form?taskId=${act.task.id}&taskName=${fns:urlEncode(act.task.name)}&taskDefKey=${act.task.taskDefinitionKey}&procInsId=${act.task.processInstanceId}&procDefId=${act.task.processDefinitionId}&status=${act.status}">审核</a>
                    </c:if>
                    <c:if test="${act.status=='finish'}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                           href="${ctx}/state/infoView?id=${act.vars.map.id}&taskDefinitionKey=${act.histTask.taskDefinitionKey}">查看</a>
                    </c:if>
                    <c:if test="${act.status=='other'}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                           href="${ctx}/state/projectDetail?id=${act.projectDeclare.id}&editFlag=1">
                            审核
                        </a>
                    </c:if>
                    <c:if test="${act.status =='middleScore'}">
                        <a class="btn btn-back-oe btn-primaryBack-oe btn-small"
                           href="${ctx}/state/infoView?id=${act.vars.map.id}&taskDefinitionKey=middleScore">
                            查看
                        </a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    ${page.footer}
</div>
<!-- Modal -->
<div id="myModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <h3 id="myModalLabel">批量审核</h3>
    </div>
    <div class="modal-body" id="selectArea">
        <form class="form-horizontal" id="addForm" name="addForm" method="post" novalidate>
            <div class="control-group">
                <label class="control-label">批量将高于等于</label>
                <div class="controls">
                    <input type="text" class="form-control input-mini number required" id="midScore" name="midScore"/>
                    <span class="help-inline">分，定义为合格</span>
                </div>
            </div>
        </form>
        <div style="margin-left: 60px;">项目数共<span id="total" class="redSpan">--</span>个,被选为合格的有<span id="passNo"
                                                                                                     class="redSpan">--</span>个,被选为不合格的有<span
                id="failedNo" class="redSpan">--</span>个
        </div>
        <div class="buffer_gif" style="text-align:center;padding:20px 0px;display:none;" id="bufferImg">
            <img src="/img/jbox-loading1.gif" alt="缓冲图片">
        </div>
    </div>
    <div class="modal-footer">
        <button class="btn" aria-hidden="true" id="confirmBtn">确定</button>
        <button class="btn btn-default" data-dismiss="modal">取消</button>
    </div>
</div>
<script>

    $(function () {
        var $midScore = $('#midScore');
        var passIds = [];
        var passTaskIds = [];
        var failedIds = [];
        var failedTaskIds = [];
        var $passNo = $("#passNo");
        var $failedNo = $("#failedNo");
        var $total = $("#total");
        var $addForm = $('#addForm');
        var addFormValidate;
        $midScore.on('change', function (e) {
            var val = $(this).val();
            var xhr = $.post('/a/projectBatch/getNoByScore', {score: val});
            xhr.success(function (data) {
                var passes = data.pass;
                var fails = data.failed;
                var passLen = data.pass.length;
                var failedLen = data.failed.length;
                $passNo.text(passLen);
                $failedNo.text(failedLen);
                $total.text(passLen + failedLen);
                passIds.length = 0;
                passTaskIds.length = 0;
                failedIds.length = 0;
                failedTaskIds.length = 0;
                $.each(passes, function (i, item) {
                    passIds.push(item.bussinessId);
                    passTaskIds.push(item.taskId)
                });
                $.each(fails, function (i, item) {
                    failedIds.push(item.bussinessId);
                    failedTaskIds.push(item.taskId);
                })
            })
        });

        addFormValidate = $addForm.validate({
            errorPlacement: function (error, element) {
                if (element.attr('id') == 'midScore') {
                    error.insertAfter(element.next().next())
                }
            },
            submitHandler: function (form) {
                $('#selectArea').hide();
                $('#bufferImg').show();  //显示等待
                $('#confirmBtn').attr("disabled", true);
                var xhr = $.post('/a/projectBatch/middleRatingBatch', {
                    passIds: passIds.join(','),
                    passTaskIds: passTaskIds.join(','),
                    failedIds: failedIds.join(','),
                    failedTaskIds: failedTaskIds.join(',')
                });
                xhr.success(function (data) {
                    window.location.reload();
                })
            }
        });

        $('#confirmBtn').on('click', function () {
            $addForm.submit();
        })
    });

    //    $("#midScore").blur(function () {
    //        getActiviti();
    //    });
    //
    //    var validate;
    //    $(document).ready(function () {
    //        validate = $("#addForm").validate({
    //            errorPlacement: function (error, element) {
    //                error.insertAfter(element.next("label"));
    //            }
    //        });
    //    });
    //
    //
    //    function ini() {
    //        passIds = [];
    //        passTaskIds = [];
    //        failedIds = [];
    //        failedTaskIds = [];
    //    }
    //    //根据分数查询合格数、不合格数
    //    function getActiviti() {
    //        //先校验分数是否为数字 ，再ajax请求
    //        if (!validate.form()) {
    //            return false;
    //        }
    //        var score = $("#midScore").val();
    //        $.ajax({
    //            type: "post",
    //            url: "/a/projectBatch/getNoByScore",
    //            data: {"score": score},
    //            async: true,
    //            success: function (data) {
    //                ini();
    //                $("#passNo").text(data.pass.length);//处理合格数量
    //                $("#failedNo").text(data.failed.length);//处理不合格数量
    //                $("#total").text(data.pass.length + data.failed.length); //总数
    //                for (var i = 0; i < data.pass.length; i++) {
    //                    passIds.push(data.pass[i].bussinessId);
    //                    passTaskIds.push(data.pass[i].taskId);
    //                }
    //                for (var i = 0; i < data.failed.length; i++) {
    //                    failedIds.push(data.failed[i].bussinessId);
    //                    failedTaskIds.push(data.failed[i].taskId);
    //                }
    //                //console.log(passIds.join(","))
    //
    //            }
    //        });
    //
    //    }
    //
    //    function doBatch(url) {
    //        //先校验有没有选择
    //        if (!validate.form()) {
    //            return false;
    //        }
    //
    //
    //        //ajax 后台处理
    //        $.ajax({
    //            type: "post",
    //            url: url,
    //            data: {
    //                "passIds": passIds.join(","),
    //                "passTaskIds": passTaskIds.join(","),
    //                "failedIds": failedIds.join(","),
    //                "failedTaskIds": failedTaskIds.join("")
    //            },
    //            async: true,
    //            success: function (data) {
    //                location.reload();
    //            }
    //        });
    //    }

</script>
</body>
</html>