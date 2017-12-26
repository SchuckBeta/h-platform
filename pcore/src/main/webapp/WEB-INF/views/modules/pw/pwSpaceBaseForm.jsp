<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${backgroundTitle}</title>
    <%@include file="/WEB-INF/views/include/backcyjd.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
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
                    }else if(element.attr('name') == 'area'){
                        error.appendTo(element.parent());
                    }else {
                        error.insertAfter(element);
                    }
                }
            });
        });
    </script>
    <style>
        .webuploader-pick {
            width: 100%;
            padding: 0;
            background-color: transparent;
        }

        .upload-image-container:hover .webuploader-container {
            visibility: visible;
        }

        .webuploader-container {
            height: 104px;
            visibility: hidden;
        }
        .upload-image-container .delete-image{
            display: none;
            position: absolute;
            right: -10px;
            top: -10px;
            width: 16px;
            height: 16px;
            border: 1px solid #e9432d;
            border-radius: 50%;
            cursor: pointer;
        }
        .upload-image-container:hover .delete-image{
            display: block;
        }
        .upload-image-container .delete-image img{
            vertical-align: top;
        }
    </style>
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
                href="${ctx}/pw/pwSpace/form?id=${pwSpace.id}&parent.id=${pwSpaceparent.id}">基地<shiro:hasPermission
                name="pw:pwSpace:edit">${not empty pwSpace.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
                name="pw:pwSpace:edit">查看</shiro:lacksPermission></a></li>
    </ul>
    <sys:message content="${message}"/>
    <div class="content_panel">
        <form:form id="inputForm" modelAttribute="pwSpace" action="${ctx}/pw/pwSpace/save" method="post"
                   class="form-horizontal addInput">
            <form:hidden path="id"/>
            <form:hidden path="parent.id" value="${pwSpace.parent.id}"/>
            <form:hidden path="type" value="2"/>

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
                <label class="control-label"><i>*</i>基地名称：</label>
                <div class="controls">
                    <form:input path="name" htmlEscape="false" maxlength="20" class="required"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">占地面积：</label>
                <div class="controls">
                    <form:input path="area" htmlEscape="false" maxlength="6" class="number input-mini"/>
                    <span class="help-inline">平方米</span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">基地背景图：</label>
                <div class="controls">
                    <div class="upload-image-container">
                        <div class="imageBox">
                            <img id="areaImage"
                                 src="${empty pwSpace.imageUrl ? '/images/upload-default-image1.png' : fns:ftpImgUrl(pwSpace.imageUrl)}">
                        </div>
                        <div class="imageBtnBox" id="imageBtn">
                            <span>更换背景图片</span>
                                <%--<button id="imageBtn" type="button" class="btn btn-primary">上传背景图片</button>--%>
                        </div>
                        <div class="upload-file-cyjd imageBtnBox" style="display: block">更换背景图片</div>
                        <div class="loadding" style="display: none"><img src="/images/loading.gif"></div>
                        <input type="hidden" name="imageUrl">
                        <div class="delete-image">
                            <img src="/img/remove-accessory.png">
                        </div>
                    </div>
                    <div style="line-height: 104px; display: inline-block; vertical-align: top;font-size: 12px;">
                        <span class="help-inline">建议背景图片大小：270 × 200（像素）</span>
                    </div>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">备注：</label>
                <div class="controls">
                    <form:textarea path="remarks" htmlEscape="false" class="form-control input-xxlarge" rows="4"
                                   maxlength="200"></form:textarea>
                </div>
            </div>
            <div class="form-actions">
                <shiro:hasPermission name="pw:pwSpace:edit"><input class="btn btn-primary" type="submit"
                                                                   value="保 存"/>&nbsp;</shiro:hasPermission>
                <input class="btn btn-default" type="button" value="返 回" onclick="history.go(-1)"/>
            </div>
        </form:form>
    </div>
    <div id="dialog-message" title="信息">
        <p id="dialog-content"></p>
    </div>
</div>
<div id="dialogCyjd" class="dialog-cyjd"></div>

<script>
    $(function () {
        var $uploadFileCYbg = $('.upload-file-cyjd');


        var uploader = WebUploader.create({
            auto: true,
            server: '/ftp/ueditorUpload/normal?folder=temp/space', //文件上传地址 folder表示功能模块
            pick: {
                id: $uploadFileCYbg[0],
                multiple: false
            },
            fileVal: 'upfile'
        })
        uploader.on('beforeFileQueued', function (file) {
            var reg = /(JPEG|jpeg|JPG|jpg|GIF|gif|BMP|bmp|PNG|png)$/;
            if (!reg.test(file.ext)) {
                dialogCyjd.createDialog(0, '文件类型错误， 请上传图片');
                return false;
            }
        })

        uploader.on('fileQueued', function () {
            $uploadFileCYbg.next().show();
        })

        uploader.on('uploadSuccess', function (file, respons) {
            $uploadFileCYbg.next().hide();
            $('#areaImage').attr('src', respons.url);
            $('input[name="imageUrl"]').val(respons.ftpUrl)
            dialogCyjd.createDialog(0, file.name + '上传成功');
        })

        uploader.on('uploadError', function (file) {
            $uploadFileCYbg.find('.loadding').hide();
            dialogCyjd.createDialog(0, file.name + '上传失败');
        })

        uploader.on('error', function (state) {
            var errorMsg;
            switch (state) {
                case 'Q_TYPE_DENIED':
                    errorMsg = '文件类型错误';
                    break;
                case 'Q_EXCEED_SIZE_LIMIT':
                    errorMsg = '文件过大';
                    break;
                default:
                    errorMsg = '未知错误';
                    break;
            }
            $uploadFileCYbg.next().hide();
            dialogCyjd.createDialog(0, errorMsg);
        })

        $(document).on('click', '.delete-image', function (e) {
            uploader.reset()
            $('#areaImage').attr('src', '/images/upload-default-image1.png')
            $('input[name="imageUrl"]').val('')
        })

//
//        $('.upload-image-container').on('click', function () {
//            uploader.trigger('click')
//        })
    })
</script>

</body>
</html>