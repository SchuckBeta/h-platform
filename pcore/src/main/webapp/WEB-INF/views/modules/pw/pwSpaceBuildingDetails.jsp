<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${backgroundTitle}</title>
    <%@include file="/WEB-INF/views/include/backcyjd.jsp" %>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#inputForm").validate({
                submitHandler: function(form){
                    beforeSubmit();
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function(error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")){
                        error.appendTo(element.parent().parent());
                    }else if(element.attr('name') === 'floorNum'){
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
            <span>场地管理</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <ul class="nav nav-tabs">
        <li><a href="${ctx}/pw/pwSpace/">场地列表</a></li>
        <li class="active"><a
                href="${ctx}/pw/pwSpace/form?id=${pwSpace.id}&parent.id=${pwSpaceparent.id}">楼栋查看</a></li>
    </ul>
    <sys:message content="${message}"/>
    <div class="content_panel">
        <form:form id="inputForm" modelAttribute="pwSpace" action="${ctx}/pw/pwSpace/save" method="post" class="form-horizontal addInput">
            <form:hidden path="id"/>
            <form:hidden path="parent.id" value="${pwSpace.parent.id}" />
            <form:hidden path="type" value="3" />
            <form:hidden path="openWeek" id="openWeek" value="" />

            <c:if test="${not empty school}">
                <div class="control-group">
                    <label class="control-label">所属学院：</label>
                    <div class="controls">
                        <p class="control-static">${school}</p>
                    </div>
                </div>
            </c:if>

            <c:if test="${not empty campus}">
                <div class="control-group">
                    <label class="control-label">所属校区：</label>
                    <div class="controls">
                        <p class="control-static">${campus}</p>
                    </div>
                </div>
            </c:if>

            <c:if test="${not empty base}">
                <div class="control-group">
                    <label class="control-label">所属基地：</label>
                    <div class="controls">
                        <p class="control-static">${base}</p>
                    </div>
                </div>
            </c:if>



            <div class="control-group">
                <label class="control-label">楼栋名称：</label>
                <div class="controls">
                    <p class="control-static">${pwSpace.name}</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">负责人：</label>
                <div class="controls">
                    <p class="control-static">${pwSpace.person}</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">联系方式：</label>
                <div class="controls">
                    <p class="control-static">${pwSpace.phone}</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">开放时间：</label>
                <div class="controls" style="padding-top: 10px;">
                    <div class="controls-checkbox" style="padding: 10px 15px 15px 15px;border-radius: 5px; border: 1px solid #ddd;display: inline-block">
                        <form:checkboxes path="openWeeks" items="${fns:getDictList('pw_space_week')}" itemLabel="label" itemValue="value" htmlEscape="false" disabled="true"/>
                    </div>
                    <span style="position: absolute;left: 15px; top: 0; background-color: #fff; padding: 0 8px;">选择开放日</span>
                </div>
            </div>
            <div class="control-group">
                <div class="controls" style="padding-top: 10px;">
                    <div style="padding: 25px 15px 15px 15px;border-radius: 5px; border: 1px solid #ddd;display: inline-block">
                        <div style="margin-bottom: 15px;">
                            <span style="margin-right: 20px;">上午：</span>
                            <input name="amOpenStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
                                   value="${pwSpace.amOpenStartTime}"/>

                            <span style="margin: 0 4px;">至</span>
                            <input name="amOpenEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
                                   value="${pwSpace.amOpenEndTime}"/>
                        </div>
                        <div>
                            <span style="margin-right: 20px;">下午：</span>
                            <input name="pmOpenStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
                                   value="${pwSpace.pmOpenStartTime}"/>

                            <span style="margin: 0 4px;">至</span>
                            <input name="pmOpenEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
                                   value="${pwSpace.pmOpenEndTime}"/>
                        </div>
                    </div>
                    <span style="position: absolute;left: 15px; top: 0; background-color: #fff; padding: 0 8px;">设置开放时间段</span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">楼层：</label>
                <div class="controls">
                    <p class="control-static">${pwSpace.floorNum}层</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">占地面积：</label>
                <c:if test="${not empty pwSpace.area}">
                    <div class="controls">
                        <p class="control-static">${pwSpace.area}平方米</p>
                    </div>
                </c:if>
            </div>

            <div class="control-group">
                <label class="control-label">建筑图片：</label>
                <div class="controls controlBox">
                    <div class="imageBox">
                        <img id="areaImage" src="${empty pwSpace.imageUrl ? '/images/upload-default-image1.png' : fns:ftpImgUrl(pwSpace.imageUrl)}">
                    </div>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">备注：</label>
                <div class="controls">
                    <p class="control-static">${pwSpace.remarks}</p>
                </div>
            </div>
            <div class="form-actions">
                <input class="btn btn-default" type="button" value="返 回" onclick="history.go(-1)"/>
            </div>
        </form:form>
    </div>
    <div id="dialog-message" title="信息">
        <p id="dialog-content"></p>
    </div>
</div>
<script>
    function beforeSubmit() {
        var openWeek = '';
        $(":checkbox").each(function(){
            if($(this).attr("checked")){
                openWeek += 1;
            }else{
                openWeek += 0;
            }
        });
        $("#openWeek").val(openWeek);
    }


</script>
</body>
</html>