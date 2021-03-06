<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${backgroundTitle}</title>
    <%@include file="/WEB-INF/views/include/backcyjd.jsp" %>
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
    <style>
        .form-modal .controls .radio.inline {
            margin-left: 0;
            margin-right: 10px;
        }
        .modal-backdrop, .modal-backdrop.fade.in{
            opacity: 0;
            z-index: -1;
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>续期管理</span>
            <i class="line weight-line"></i>
        </div>
    </div>

    <ul class="nav nav-tabs nav-tabs-default">
        <li class="active"><a href="${ctx}/pw/pwEnter/listXQRZ">续期管理列表</a></li>
    </ul>


    <div class="tab-content-default">
        <form:form id="searchForm" modelAttribute="pwEnter" action="${ctx}/pw/pwEnter/listXQRZ" method="post"
                   class="form-horizontal clearfix form-search-block">
            <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
            <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
            <div class="col-control-group">
                <div class="control-group">
                    <label class="control-label">入驻编号 </label>
                    <div class="controls">
                        <form:input path="no" htmlEscape="false" maxlength="64" class="input-medium"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">状态 </label>
                    <div class="controls">
                        <form:select path="status" class="input-medium" cssStyle="width: 164px;">
                            <form:option value="" label="--请选择--"/>
                            <form:options items="${fns:getDictList('pw_enter_status')}" itemLabel="label"
                                          itemValue="value"
                                          htmlEscape="false"/>
                        </form:select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">申请人 </label>
                    <div class="controls">
	                    <form:input path="applicant.name" htmlEscape="false" maxlength="64" class="input-medium"/>
                        <%-- <sys:treeselect id="applicant" name="applicant" value="${pwEnter.applicant}"
                                        labelName="user.name"
                                        labelValue="${pwEnter.applicant.name}"
                                        title="用户" url="/sys/office/treeData?type=3" cssClass="input-small"
                                        cssStyle="width: 150px;"
                                        allowClear="true"
                                        notAllowSelectParent="true"/> --%>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">开始时间 </label>
                    <div class="controls">
                        <input name="startDate" type="text" readonly="readonly" maxlength="20"
                               class="input-medium Wdate"
                               value="<fmt:formatDate value="${pwEnter.startDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">结束时间 </label>
                    <div class="controls">
                        <input name="endDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                               value="<fmt:formatDate value="${pwEnter.endDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
                    </div>
                </div>
            </div>
            <div class="search-btn-box">
                <button id="btnSubmit" type="submit" class="btn btn-primary">查询</button>
            </div>
        </form:form>
        <sys:message content="${message}"/>
        <table id="contentTable"
               class="table table-bordered table-condensed table-orange table-center table-nowrap table-hover">
            <thead>
            <tr>
                <th>入驻编号</th>
                <th>申请人</th>

                <th>类型</th>
                <!-- <th>类型|状态|名称</th> -->
                <!-- <th>期限(天)</th> -->
                <th>周期</th>
                <th>最后更新时间</th>
                <%--<th>备注</th>--%>
                <th>状态</th>
                <shiro:hasPermission name="pw:pwEnter:edit">
                    <th>操作</th>
                </shiro:hasPermission>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${page.list}" var="pwEnter" varStatus="idx">
                <tr>
                    <td><a href="${ctx}/pw/pwEnter/form?id=${pwEnter.id}">
                            ${pwEnter.no}
                    </a></td>
                    <td>${pwEnter.applicant.name}</td>
					<td>
		                <c:if test="${not empty pwEnter.eteam}"> <span class="bd1 mlr5"> ${fns:getDictLabel(pwEnter.eteam.type, 'pw_enter_type', '')} </span> </c:if>
		                <c:if test="${not empty pwEnter.eproject}"> <span class="bd1 mlr5"> ${fns:getDictLabel(pwEnter.eproject.type, 'pw_enter_type', '')} </span> </c:if>
		                <c:if test="${not empty pwEnter.ecompany}"> <span class="bd1 mlr5"> ${fns:getDictLabel(pwEnter.ecompany.type, 'pw_enter_type', '')} </span> </c:if>
	                </td>
                    <%-- <td>
                        <table style="width: 100%;">
                            <tr>
                                <td style="text-align: left; border-left: none;">
                                    <c:if test="${not empty pwEnter.eteam}">${fns:getDictLabel(pwEnter.eteam.type, 'pw_enter_type', '')}
                                    | ${fns:getDictLabel(pwEnter.eteam.status, 'pw_enter_shstatus', '')}
                                    | ${pwEnter.eteam.team.name}</c:if>
                                    <c:if test="${empty pwEnter.eteam}"> - | - | - </c:if>
                            <tr>
                                <td style="text-align: left; border-left: none;">
                                    <c:if test="${not empty pwEnter.eproject}">${fns:getDictLabel(pwEnter.eproject.type, 'pw_enter_type', '')}
                                    | ${fns:getDictLabel(pwEnter.eproject.status, 'pw_enter_shstatus', '')}
                                    | ${pwEnter.eproject.project.name}</c:if>
                                    <c:if test="${empty pwEnter.eproject}"> - | - | - </c:if>
                            <tr>
                                <td style="text-align: left; border-left: none;">
                                    <c:if test="${not empty pwEnter.ecompany}">${fns:getDictLabel(pwEnter.ecompany.type, 'pw_enter_type', '')} | ${fns:getDictLabel(pwEnter.ecompany.status, 'pw_enter_shstatus', '')} | ${pwEnter.ecompany.pwCompany.name}</c:if>
                                    <c:if test="${empty pwEnter.ecompany}"> - | - | - </c:if>
                                </td>
                            </tr>
                        </table>
                    </td> --%>
                    <%-- <td>
                        <c:set var="iterm" value="${fns:getDictLabel(pwEnter.term, 'pw_enter_term', '')}"></c:set>
                        <c:if test="${empty iterm}">${pwEnter.term} 天</c:if>
                        <c:if test="${not empty iterm}">${iterm}</c:if>
                    </td> --%>
                    <td>
                        <c:if test="${not empty pwEnter.startDate}"><fmt:formatDate value="${pwEnter.startDate}" pattern="yyyy-MM-dd"/></c:if>
                        <c:if test="${empty pwEnter.startDate}">-</c:if>至
                        <c:if test="${not empty pwEnter.endDate}"><fmt:formatDate value="${pwEnter.endDate}" pattern="yyyy-MM-dd"/></c:if>
                        <c:if test="${empty pwEnter.startDate}">-</c:if>
                    </td>
                    <td>
                        <fmt:formatDate value="${pwEnter.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                    </td>
                        <%--<td>--%>
                        <%--${pwEnter.remarks}--%>
                        <%--</td>--%>
                    <td>
                            ${fns:getDictLabel(pwEnter.status, 'pw_enter_status', '')}
                    </td>
                    <shiro:hasPermission name="pw:pwEnter:edit">
                        <td>
                            <c:if test="${(pwEnter.status eq 1) || (pwEnter.status eq 3) || (pwEnter.status eq 4) || (pwEnter.status eq 6) }">
                                <a data-id="${pwEnter.id}" class="btn btn-small btn-primary btn-renewal"
                                   data-toggle="modal"
                                   data-target="#modal-renwal-form"
                                   href="#modal-renwal-form">续期</a>
                            </c:if>
                                <%--<c:if--%>
                                <%--test="${(pwEnter.status ne 1) && (pwEnter.status ne 3) && (pwEnter.status ne 4) && (pwEnter.status ne 6) }">--%>
                                <%--<a class="check_btn btn-pray btn-lx-primary" href="${ctx}/pw/pwEnter/form?id=${pwEnter.id}">查看</a>--%>
                                <%--</c:if>--%>
                        </td>
                    </shiro:hasPermission>
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

<div id="modal-renwal-form" class="modal hide " data-backdrop="false" tabindex="-1" role="dialog">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
        <span>资产类别</span>
    </div>
    <div class="modal-body">
        <form id="renewalForm" class="form-horizontal form-modal">
            <div class="control-group">
                <label class="control-label"><i>*</i>续期时间：</label>
                <div class="controls">
                    <c:forEach items="${fns:getDictList('pw_enter_term')}" var="term">
                        <label class="radio inline">
                            <input type="radio" name="term" value="${term.value}" class="required">${term.label}
                        </label>
                    </c:forEach>
                    <%--<form:radiobuttons path="term" items="${fns:getDictList('pw_enter_term')}" itemLabel="label"--%>
                    <%--cssClass="required"--%>
                    <%--itemValue="value" htmlEscape="false"></form:radiobuttons>--%>
                </div>
            </div>
        </form>
    </div>
    <div class="modal-footer">
        <button type="button" class="btn btn-default btn-cancel" data-dismiss="modal" aria-hidden="true">关闭</button>
        <button type="button" class="btn btn-primary btn-renewal-save">保存</button>
    </div>
    <div class="modal-backdrop in"></div>
</div>

<script type="text/javascript">
    $(function () {
        var $renewalForm = $('#renewalForm');
        var $terms = $('input[name="term"]');
        var $btnRenewalSave = $('#modal-renwal-form .btn-renewal-save')
        var $btnCacel = $('#modal-renwal-form .btn-cancel')
        var id;
        var renewalForm = $renewalForm.validate({
            errorPlacement: function (error, element) {
                if (element.is(":checkbox") || element.is(":radio")) {
                    error.appendTo(element.parent().parent());
                } else {
                    error.insertAfter(element);
                }
            }
        })
        $('.btn-renewal').on('click', function () {
            id = $(this).data('id')
        })

        function reload() {
            var href = window.location.href;
            location.href = href
        }

        $btnRenewalSave.on('click', function () {
            if (renewalForm.form()) {
                var val = $('input[name="term"]:checked').val();
                var xhr = $.get('${ctx}/pw/pwEnter/ajaxXq?id=' + id + '&term=' + val)
                xhr.success(function (data) {
                    if (data.status) {
                        resetTip();
                        $('#modal-renwal-form').modal('hide')

                        top.$.jBox.info('续期成功', '系统提示', {
                            closed: function () {
                                reload()
                            }
                        });
                        top.$('.jbox-body .jbox-icon').css('top','55px');
                    }
                })
                xhr.error(function (error) {
                    alertx('续期失败')
                })
            }
        })
        $btnCacel.on('click', function () {
            renewalForm.resetForm();
            $renewalForm[0].reset()
        })
    })
</script>
</body>
</html>