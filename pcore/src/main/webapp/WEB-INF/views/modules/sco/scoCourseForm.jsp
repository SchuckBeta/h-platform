<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ include file="/WEB-INF/views/include/backtable.jsp" %>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" type="text/css" href="/css/credit-module.css">
    <script type="text/javascript">
        var scoreReg = /^[1-9]\d{0,2}(\.\d{1})?$/;
        var scoTimeReg = /^[1-9]\d{0,2}$/;
        jQuery.validator.addMethod("ckScore", function (value, element) {
            return this.optional(element) || scoreReg.test(value);
        }, "最多三位正整数,可带一位小数");
        jQuery.validator.addMethod("ckTime", function (value, element) {
            return this.optional(element) || scoTimeReg.test(value);
        }, "最多三位的正整数");
        $(document).ready(function () {
            $("#inputForm").validate({
                submitHandler: function (form) {
                    form.submit();
                },
                rules: {
                    "name": {
                        remote: {
                            async: true,
                            url: "/a/sco/scoCourse/checkName",     //后台处理程序
                            type: "post",               //数据发送方式
                            data: {                     //要传递的数据
                                id: function () {
                                    return $("#id").val();
                                }
                            }
                        }
                    },
                    "code": {
                        remote: {
                            async: true,
                            url: "/a/sco/scoCourse/checkCode",     //后台处理程序
                            type: "post",               //数据发送方式
                            data: {                     //要传递的数据
                                id: function () {
                                    return $("#id").val();
                                }
                            }
                        }
                    },
                    "planScore": {
                        ckScore: true
                    },
                    "overScore": {
                        ckScore: true
                    },
                    "planTime": {
                        ckTime: true
                    }
                },
                messages: {
                    "name": {
                        remote: "该课程名已经存在"
                    },
                    "code": {
                        remote: "该课程代码已经存在"
                    }
                },
                errorPlacement: function (error, element) {
                    if (element.is(":checkbox")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }

                }
            });
            $('.control-checkbox span').addClass('checkbox inline')
        });
    </script>
    <style>
       select.form-control{
           height: 30px;
       }
       .radio input[type="radio"], .checkbox input[type="checkbox"]{
           margin: 4px 0 0 -20px;
       }
       .radio.inline, .checkbox.inline{
           margin-left: 0;
           margin-right: 10px;
       }
       .radio.inline + .radio.inline, .checkbox.inline + .checkbox.inline{
           margin-left: 0;
       }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>创建课程</span> <i class="line weight-line"></i>
        </div>
    </div>
    <form:form class="form-horizontal" id="inputForm"
               modelAttribute="scoCourse" action="save" method="post">
        <input type="hidden" id="id" name="id" value="${scoCourse.id }"/>
        <div class="control-group">
            <label class="control-label" for="code"><i
                    class="require-star">*</i>课程代码：</label>
            <div class="controls">
                <form:input path="code" class="required" maxlength="255"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="name"><i
                    class="require-star">*</i>课程名：</label>
            <div class="controls">
                <form:input path="name" class="required" maxlength="255"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="type"><i
                    class="require-star">*</i>课程类型：</label>
            <div class="controls">
                <form:select path="type" class="form-control required">
                    <form:option value="" label="--请选择--"/>
                    <form:options items="${fns:getDictList('0000000102')}"
                                  itemValue="value" itemLabel="label" htmlEscape="false"/>
                </form:select>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="nature"><i
                    class="require-star">*</i>课程性质：</label>
            <div class="controls">
                <form:select path="nature" class="form-control required">
                    <form:option value="" label="--请选择--"/>
                    <form:options items="${fns:getDictList('0000000108')}"
                                  itemValue="value" itemLabel="label" htmlEscape="false"/>
                </form:select>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="planScore"><i
                    class="require-star">*</i>计划学分：</label>
            <div class="controls">
                <form:input path="planScore" class="required"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="planTime"><i
                    class="require-star">*</i>计划课时：</label>
            <div class="controls">
                <form:input path="planTime" class="required"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="overScore"><i
                    class="require-star">*</i>合格成绩：</label>
            <div class="controls">
                <form:input path="overScore" class="required"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label"><i
                    class="require-star">*</i>面向专业科类：</label>
            <div class="controls">
                <div class="control-checkbox">
                    <form:checkboxes path="professionalList"
                                     items="${fns:getDictList('0000000111')}" itemLabel="label"
                                     itemValue="value" class="required"></form:checkboxes>
                </div>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="remarks">备注</label>
            <div class="controls">
                <textarea id="remarks" name="remarks" class="input-xxlarge" rows="5" maxlength="255">${scoCourse.remarks}</textarea>
            </div>
        </div>
        <div class="control-group">
            <div class="controls">
                <button type="submit" class="btn-oe btn-primary-oe">保存</button>
                <button type="button" class="btn" onclick="history.go(-1);">返回</button>
            </div>
        </div>
    </form:form>
</div>
</body>
</html>