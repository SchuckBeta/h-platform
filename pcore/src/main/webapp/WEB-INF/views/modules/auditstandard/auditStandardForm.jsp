<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title></title>
    <%@ include file="/WEB-INF/views/include/backtable.jsp" %>
    <link rel="stylesheet" type="text/css" href="/css/credit-module.css">
    <script type="text/javascript" src="/js/tableFormReview.js"></script>
    <style>
        .table-thead-bg thead tr{
            background-color: #f4e6d4;
        }
        .table th{
            background: none;
        }
    </style>
    <script type="text/javascript">
        var scoreReg = /^[1-9]\d{0,2}$/;
        jQuery.validator.addMethod("ckScore", function (value, element) {
            return this.optional(element) || scoreReg.test(value);
        }, "最多三位的正整数");
        $(document).ready(function () {
            $("#inputForm").validate({
                submitHandler: function (form) {
                    $("#datatb").find("textarea,input").each(function (i, v) {
                        var temname = $(v).attr("name");
                        $(v).attr("name", temname.substring(0, temname.indexOf("_")));
                    });
                    form.submit();
                },
                rules: {
                    "name": {
                        remote: {
                            async: true,
                            url: "/a/auditstandard/auditStandard/checkName",     //后台处理程序
                            type: "post",               //数据发送方式
                            data: {                     //要传递的数据
                                id: function () {
                                    return $("#id").val();
                                }
                            }
                        }
                    }
                },
                messages: {
                    "name": {
                        remote: "标准程名已经存在"
                    }
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element);
                }
            });

        });
        function scoreChange() {
            var tem = 0;
            $(".ckScore").each(function (i, v) {
                tem = tem + parseInt(($(v).val() | 0));
            });
            $("#totalScore").val(tem);
            $("#totalScoreV").html(tem);
        }
    </script>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>评审标准管理</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <form:form class="form-horizontal" id="inputForm"
               modelAttribute="auditStandard" action="save" method="post">
        <form:input type="hidden" path="id"/>
        <form:input type="hidden" path="totalScore"/>
        <div class="control-group">
            <label class="control-label" for="name"><i class="require-star">*</i>标准名称：</label>
            <div class="controls">
                <form:input type="text" maxLength="50" path="name" class="required"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="remarks">评审标准说明：</label>
            <div class="controls">
                <textarea id="remarks" maxLength="200" name="remarks" rows="5">${auditStandard.remarks}</textarea>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">评审标准详情：</label>
            <div class="controls">
                <!--编辑页面data-status="edit"-->
                <table id="tableFormReview" data-status=""
                       class="table table-bordered table-hover table-theme-default table-form-review">
                    <thead>
                    <tr>
                        <th width="270"><i class="require-star">*</i>检查要点</th>
                        <th><i class="require-star">*</i>审核元素</th>
                        <th width="150"><i class="require-star">*</i>分值</th>
                        <th width="150">操作</th>
                    </tr>
                    </thead>
                    <tbody id="datatb">
                    <c:if test="${empty auditStandard.id }">
                        <tr>
                            <td>
                                <div class="form-control-box">
                                    <textarea class="form-control-check required" maxLength="50"
                                              name="checkPoint_${fns:getUuid() }" rows="4"></textarea>
                                </div>
                            </td>
                            <td>
                                <div class="form-control-box">
                                    <textarea class="form-control-elements required" maxLength="500"
                                              name="checkElement_${fns:getUuid() }" rows="4"></textarea>
                                </div>
                            </td>
                            <td>
                                <div class="form-control-box">
                                    <input type="text" name="viewScore_${fns:getUuid() }"
                                           class="form-control-credit ckScore required" oninput="scoreChange()">
                                </div>
                            </td>
                            <td>
                                <a class="btn-add-row" href="javascript:void (0)"><img src="/img/plus2.png"> </a>
                                <a class="btn-delete-row" href="javascript:void (0)"><img src="/img/minuse.png"></a>
                            </td>
                        </tr>
                    </c:if>
                    <c:if test="${not empty auditStandard.id }">
                        <c:forEach items="${details}" var="de">
                            <tr>
                                <td>
                                    <div class="form-control-box">
                                        <textarea class="form-control-check required" maxLength="50"
                                                  name="checkPoint_${fns:getUuid() }"
                                                  rows="4">${de.checkPoint }</textarea>
                                    </div>
                                </td>
                                <td>
                                    <div class="form-control-box">
                                        <textarea class="form-control-elements required" maxLength="500"
                                                  name="checkElement_${fns:getUuid() }"
                                                  rows="4">${de.checkElement }</textarea>
                                    </div>
                                </td>
                                <td>
                                    <div class="form-control-box">
                                        <input type="text" name="viewScore_${fns:getUuid() }" value="${de.viewScore }"
                                               class="form-control-credit ckScore required" oninput="scoreChange()">
                                    </div>
                                </td>
                                <td>
                                    <a class="btn-add-row" href="javascript:void (0)"><img src="/img/plus2.png"> </a>
                                    <a class="btn-delete-row" href="javascript:void (0)"><img src="/img/minuse.png"></a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    </tbody>
                    <tfoot>
                    <tr>
                        <td>总分</td>
                        <td colspan="3" id="totalScoreV">${auditStandard.totalScore}</td>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>
        <div class="text-center">
            <button type="submit" class="btn-oe btn-primary-oe">保存</button>
            <button type="button" class="btn" onclick="history.go(-1)">返回</button>
        </div>
    </form:form>
</div>
</body>
</html>