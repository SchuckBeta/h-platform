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
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function(error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")){
                        error.appendTo(element.parent().parent());
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
       <li class="active"><a href="${ctx}/pw/pwSpace/form?id=${pwSpace.id}&parent.id=${pwSpaceparent.id}">基地查看</a></li>
    </ul>
    <sys:message content="${message}"/>
    <div class="content_panel">
        <form:form id="inputForm" modelAttribute="pwSpace" action="${ctx}/pw/pwSpace/save" method="post" class="form-horizontal addInput">
            <form:hidden path="id"/>
            <form:hidden path="parent.id" value="${pwSpace.parent.id}" />
            <form:hidden path="type" value="2" />

            <c:if test="${not empty school}">
                <div class="control-group">
                    <label class="control-label">所属院：</label>
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



            <div class="control-group">
                <label class="control-label">基地名称：</label>
                <div class="controls">
                    <p class="control-static">${pwSpace.name}</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">占地面积：</label>
                <div class="controls">
                    <p class="control-static">${pwSpace.area}平方米</p>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">基地背景图：</label>
                <div class="controls">
                    <div class="imageBox">
                        <img id="areaImage" src="${empty pwSpace.imageUrl ? '/images/upload-default-image1.png' : fns:ftpImgUrl(pwSpace.imageUrl)}"
                             style="display:block;max-width: 270px;">
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
</div>
<div id="dialog-message" title="信息">
    <p id="dialog-content"></p>
</div>
</body>
</html>