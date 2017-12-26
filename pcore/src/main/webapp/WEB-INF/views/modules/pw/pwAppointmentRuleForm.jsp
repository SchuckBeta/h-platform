<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${backgroundTitle}</title>
    <%@include file="/WEB-INF/views/include/backcyjd.jsp" %>
    <script src="${ctxStatic}/fullcalendar/moment.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else if (element.attr('name') === 'afterDays' || element.attr('name') === 'autoTime') {
                        error.appendTo(element.parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        });
    </script>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>预约规则</span>
            <i class="line weight-line"></i>
        </div>
        <form:form id="inputForm" modelAttribute="pwAppointmentRule" action="${ctx}/pw/pwAppointmentRule/save"
                   method="post"
                   class="form-horizontal form-horizontal-rule">
            <form:hidden path="id"/>
            <sys:message content="${message}"/>
            <div class="control-group">
                <label class="control-label"><i>*</i>是否自动审核：</label>
                <div class="controls controls-radio">
                    <form:radiobuttons path="isAuto" items="${fns:getDictList('yes_no')}" itemLabel="label"
                                       itemValue="value" htmlEscape="false" class="required"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label"><i>*</i>自动审核时间：</label>
                <div class="controls">
                    <form:input path="autoTime" htmlEscape="false" maxlength="4"
                                class="required input-mini number digits"/>
                    <span class="help-inline">分</span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label"><i>*</i>提前多少天可以预约：</label>
                <div class="controls">
                    <form:input path="afterDays" htmlEscape="false" maxlength="2"
                                class="input-mini number digits required"/>
                    <span class="help-inline">天</span>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label"><i>*</i>预约开始时间(24小时制)：</label>
                <div class="controls">
                    <form:input path="beginTime" htmlEscape="false" maxlength="6" readonly="true"
                                class="Wdate required"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label"><i>*</i>预约结束时间(24小时制)：</label>
                <div class="controls">
                    <form:input path="endTime" htmlEscape="false" maxlength="6" readonly="true" class="Wdate required"/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label"><i>*</i>预约时间：</label>
                <div class="controls controls-checkbox">

                    <form:checkboxes path="isAppDayList" items="${weekList}" cssClass="required"
                                     itemLabel="label" itemValue="value"/>
                        <%--<form:input path="isAppDay" htmlEscape="false" maxlength="6" readonly="true" />--%>
                </div>
            </div>

            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="保 存"/>
                <input class="btn btn-default" type="button" value="返 回" onclick="history.go(-1)"/>
            </div>
        </form:form>
    </div>
</div>

<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>

<script>
    $(function () {
        var $beginTime = $('#beginTime');
        var $endTime = $('#endTime');

        function getDate() {
            var date = new Date();
            return {
                year: date.getFullYear(),
                month: date.getMonth() + 1,
                day: date.getDate()
            }
        }

        $beginTime.on('click', function (e) {
            var maxDate = $endTime.val();
            var da = getDate();
            var cdate = da.year + '-' + da.month + '-' + da.day + ' ' + maxDate;
            WdatePicker({
                dateFmt: 'HH:mm',
                isShowClear: false,
                maxDate: moment(new Date(cdate).getTime() - 30 * 60 * 1000).format('HH:mm')
            });
        });

        $endTime.on('click', function (e) {
            var startDate = $beginTime.val();
            var da = getDate();
            var cdate = da.year + '-' + da.month + '-' + da.day + ' ' + startDate;
            WdatePicker({
                dateFmt: 'HH:mm',
                isShowClear: false,
                minDate: moment(new Date(cdate).getTime() + 30 * 60 * 1000).format('HH:mm')
            });
        })
    })
</script>

</body>
</html>