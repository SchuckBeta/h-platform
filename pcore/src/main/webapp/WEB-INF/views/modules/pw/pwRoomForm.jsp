<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${backgroundTitle}</title>
    <%@include file="/WEB-INF/views/include/backcyjd.jsp" %>
</head>
<body>
<ul class="nav nav-tabs">
    <%-- <li><a href="${ctx}/pw/pwRoom/list?pwSpace.id=${pwRoom.pwSpace.id}">房间列表</a></li> --%>
    <%-- <li><a href="${ctx}/pw/pwRoom/list>房间列表</a></li> --%>
    <li class="active"><a href="${ctx}/pw/pwRoom/form?id=${pwRoom.id}">房间<shiro:hasPermission
            name="pw:pwRoom:edit">${not empty pwRoom.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="pw:pwRoom:edit">查看</shiro:lacksPermission></a></li>
</ul>
<form:form id="inputForm" class="form-horizontal" modelAttribute="pwRoom" action="${ctx}/pw/pwRoom/save"
           method="post">
    <form:hidden path="id"/>
    <div class="control-group">
        <label class="control-label"><i>*</i>房间名：</label>
        <div class="controls">
            <c:if test="${empty pwRoom.id}"><form:input path="name" htmlEscape="false" maxlength="20" class="required"/></c:if>
            <c:if test="${not empty pwRoom.id}"><p class="control-static">${pwRoom.name}</p></c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">别名：</label>
        <div class="controls">
            <form:input path="alias" htmlEscape="false" maxlength="100"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><i>*</i>房间类型：</label>
        <div class="controls controls-radio">
            <form:radiobuttons path="type" cssClass="required" items="${fns:getDictList('pw_room_type')}"
                               itemLabel="label" itemValue="value" htmlEscape="false"></form:radiobuttons>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><i>*</i>允许多团队入驻：</label>
        <div class="controls controls-radio">
            <form:radiobuttons path="isAllowm" cssClass="required" items="${fns:getDictList('yes_no')}"
                               itemLabel="label" itemValue="value" htmlEscape="false"></form:radiobuttons>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><i>*</i>容纳人数：</label>
        <div class="controls">
            <form:input path="num" htmlEscape="false" maxlength="6" class="input-mini required number"/><span
                class="help-inline">人</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><i>*</i>楼/层：</label>
        <div class="controls">
            <c:if test="${empty pwRoom.id}">
                <sys:treeselectRoom id="parent" name="pwSpace.id" value="${pwRoom.pwSpace.id}" labelName="pwSpace.name"
                                    labelValue="${pwRoom.pwSpace.name}"
                                    title="父级编号" url="/pw/pwSpace/treeData" extId="${pwRoom.pwSpace.id}" cssClass="required"
                                    allowClear="true" notAllowSelectRoot="true" notAllowSelectParent="true"/>
            </c:if>
            <c:if test="${not empty pwRoom.id}"><p class="control-static">${pwRoom.pwSpace.name}</p></c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><i>*</i>负责人：</label>
        <div class="controls">
            <form:input path="person" htmlEscape="false" maxlength="100" class="required"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><i>*</i>手机：</label>
        <div class="controls">
            <form:input path="mobile" htmlEscape="false"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">电话：</label>
        <div class="controls">
            <form:input path="phone" htmlEscape="false"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label"><i>*</i>预约房间色值：</label>
        <div class="controls">
                <%--<form:input path="mobile" htmlEscape="false"/>--%>
                <%--<span class="help-inline color-block-before">#</span>--%>
            <c:if test="${ not empty pwRoom.color}">
                <form:hidden path="color" cssClass="input-mini color-block required" minlength="6"
                             maxlength="6" htmlEscape="false"></form:hidden>
            </c:if>
            <c:if test="${ empty pwRoom.color}">
                <form:hidden path="color" cssClass="input-mini color-block required" minlength="6"
                             maxlength="6" htmlEscape="false" value="e9432d"></form:hidden>
            </c:if>
            <span class="help-inline"><i class="color-i" style="background-color:
            <c:if test="${ not empty pwRoom.color}">#${pwRoom.color}</c:if>
            <c:if test="${ empty pwRoom.color}">#e9432d</c:if>
                    " onclick="$('#color').trigger('click')"></i></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">备注：</label>
        <div class="controls">
            <form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge "/>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="pw:pwRoom:edit">
            <button class="btn btn-primary" type="submit">保存</button>
        </shiro:hasPermission>
        <button class="btn btn-default" type="button" onclick="history.go(-1)">返回</button>
    </div>
</form:form>
<script>
    console.log($('input[name="pwSpace.id"]'))
    $(function () {
        var $inputForm = $('#inputForm');
        var formValidate = $inputForm.validate({
            rules: {
                name: {
                    remote: {
                        url: '${ctx}/pw/pwRoom/ajaxVerifyName',
                        data: {
//                    		name: $('input[name="name"]').val(),
                            sid: function () {
                                return $('input[name="pwSpace.id"]').val()
                            }
                        }
                    }
                },
                mobile: {
                    required: true,
                    digits: true,
                    phone_number: true//自定义的规则
                },
                <%--'pwSpace.name': {--%>
                <%--required: true,--%>
                <%--remote: {--%>
                <%--url: '${ctx}/pw/pwRoom/ajaxVerifyName',--%>
                /* data: {
                 name: function () {
                 return $('input[name="name"]').val()
                 },
                 sid: function () {
                 return $('input[name="pwSpace.id"]').val()
                 }
                 }*/
                <%--}--%>
                <%--}--%>
            },
            messages: {
                mobile: {
                    required: "必填信息",
                    digits: "请输入正确的手机号码"
                },
                'pwSpace.name': {
                    required: '必填消息'
//                    remote: '同楼层房间名已存在'
                },
                name: {
                    remote: '同楼层房间名已存在'
                }
            },
            submitHandler: function (form) {
//            	console.info($('input[name="name"]').val());
//            	console.info($('input[name="pwSpace.id"]').val());
                var xhr = $.get('${ctx}/pw/pwRoom/ajaxVerifyName', {
                    name: function () {
                        return $('input[name="name"]').val()
                    },
                    sid: function () {
                        return $('input[name="pwSpace.id"]').val()
                    }
                })

                xhr.success(function (data) {
                    if(data){
                        loading('正在提交，请稍等...');
                        form.submit()
                    }else {
                        $('<label for="name" class="error">同楼层房间名已存在</label>').insertAfter($('input[name="name"]'))
                    }
                })



            },
            errorPlacement: function (error, element) {
                if (element.is(":checkbox") || element.is(":radio")) {
                    error.appendTo(element.parent().parent());
                } else if (element.attr('name') == 'num' || element.parent().is(".input-append")) {
                    error.appendTo(element.parent());
                } else {
                    error.insertAfter(element);
                }
            }
        })

    })


</script>
</body>
</html>