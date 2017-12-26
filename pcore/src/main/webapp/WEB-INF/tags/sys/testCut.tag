<%@ tag language="java" pageEncoding="UTF-8" %>
<%--<link rel="stylesheet" href="/static/bootstrap/2.3.1/css_default/bootstrap.min.css">--%>
<style type="text/css">
    .modal-avatar {
        /*width: 700px;*/
        /*margin-left: -350px;*/
    }

    .modal-avatar .avatar-content {
        /*overflow: hidden;*/
    }

    .modal-avatar .avatar-area {
        float: left;
    }

    .modal-avatar .preview-avatar-lg {
        width: 150px;
    }

    .modal-avatar .preview-avatar-md {
        width: 100px;
    }

    .modal-avatar .preview-avatar-sm {
        width: 50px;
    }

    .modal-avatar .preview-avatars {
        float: right;
    }

    .modal-avatar .preview-avatar {
        margin-top: 8px;
        overflow: hidden;
    }

    .modal-avatar .cut-avatar-area {
        max-width: 400px;
        max-height: 400px;
    }

    .modal-avatar .modal-footer .btn {
        width: auto;
        height: auto;
        background-image: none;
    }

    .modal-avatar .modal-footer .btn-default:hover, .modal-avatar .modal-footer .btn-default:focus {
        border: 1px solid #ccc;
    }

    .modal-avatar .btn-primary-oe {
        color: #fff;
        width: auto;
        height: auto;
        background-color: #e9442d;
        background-image: none;
    }

    .modal-avatar .btn-primary-oe:hover, .modal-avatar .btn-primary-oe:focus {
        color: #fff;
        border: 1px solid #e9442d;
        background-color: #e9442d;
    }
</style>
<%--<script src="/static/jquery/jquery.min.js"></script>
<script src="/common/common-js/bootstrap.min.js"></script>--%>
<link rel="stylesheet" type="text/css" href="/static/cropper/cropper.min.css">
<script type="text/javascript" src="/static/cropper/cropper.min.js"></script>

<%@ attribute name="width" type="java.lang.String" required="true" description="图片最小宽度" %>
<%@ attribute name="height" type="java.lang.String" required="true" description="图片最小高度" %>
<%@ attribute name="btnid" type="java.lang.String" required="true" description="上传按钮Id" %>
<%@ attribute name="column" type="java.lang.String" required="true" description="图片地址的字段值" %>
<%@ attribute name="imgId" type="java.lang.String" required="true" description="图片ID值" %>
<%@ attribute name="filepath" type="java.lang.String" required="true" description="文件目录" %>
<%@ attribute name="className" type="java.lang.String" required="true" description="modal样式" %>
<%@ attribute name="modalId" type="java.lang.String" required="false" description="modal样式" %>

<!--弹层部分-->
<div id="${modalId}" class="modal fade ${className}">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <p style="margin-bottom: 0">更改图片</p>
            </div>
            <div class="modal-body">
                <div class="avatar-content clearfix">
                    <div class="avatar-area">
                        <div style="margin-bottom: 10px;">
                            <input class="avatar-file" type="file" style="display: none">
                            <button type="button" class="btn-upload-image btn btn-primary-oe">上传图片</button>
                        </div>
                        <div class="cut-avatar-area"></div>
                    </div>
                    <div class="preview-avatars" style="padding-top: 36px;display: none">
                        <div class="preview-avatar preview-avatar-lg"></div>
                        <div class="preview-avatar preview-avatar-md"></div>
                        <div class="preview-avatar preview-avatar-sm"></div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button class="btn btn-default" data-dismiss="modal" aria-hidden="true">取消</button>
                <button class="btn btn-primary-oe btn-modal-upload">上传</button>
            </div>
        </div>
    </div>
</div>


<script>
    var imgId = "#${imgId}";
    var width = "${width}";
    var height = "${height}";
    var btnid = "#${btnid}";
    var column = "${column}";
    var modalId = "#${modalId}";
    var uploadUrl = '/ftp/ueditorUpload/cutImg?folder='
            + '${filepath}'
            + '&x={{x}}&y={{y}}&width={{width}}&height={{height}}';
    $.fn.uploadAvatar = function (option) {
        var options = $.extend(true, {}, $.fn.uploadAvatar.default, option);
        var $this = $(this);
        var $modal = $(options.modal);
        var $modalAvatarArea = $modal.find('.cut-avatar-area');
        var cropperOption = options.cropperOption;
        var $avatarFile = $modal.find('.avatar-file');
        var braceReg = /\{\{(.)+?\}\}/g;
        var url = options.url;
        var mValueResults = url.match(braceReg);
        var $btnModalUpload = $modal.find('.btn-modal-upload');
        var fileVal = options.fileVal;
        var oMyForm;
        var minCropBoxWidth = cropperOption.minCropBoxWidth;
        var minCropBoxHeight = cropperOption.minCropBoxHeight;
        var $btnUploadImage = $modal.find('.btn-upload-image');

        var uploadFile;
        var oFReader;

        $btnUploadImage.on('click', function () {
            $avatarFile.trigger('click')
        });

        $this.on('click.show.modal', function (e) {
            e.stopPropagation();
            $modal.modal('show');
//            $modalAvatarArea.find('>img').cropper('destroy');
        });


        $avatarFile.on('change', function () {
            var aFiles = this.files;
            var rFilter = /^(?:image\/bmp|image\/cis\-cod|image\/gif|image\/ief|image\/jpeg|image\/jpeg|image\/jpeg|image\/pipeg|image\/png|image\/svg\+xml|image\/tiff|image\/x\-cmu\-raster|image\/x\-cmx|image\/x\-icon|image\/x\-portable\-anymap|image\/x\-portable\-bitmap|image\/x\-portable\-graymap|image\/x\-portable\-pixmap|image\/x\-rgb|image\/x\-xbitmap|image\/x\-xpixmap|image\/x\-xwindowdump)$/i;
            oFReader = new FileReader();
            if (!rFilter.test(aFiles[0].type)) {
                alert('上传图片必须大于200×200');
                return false;
            }
            if (window.FileReader) {
                oFReader.onload = function (oFREvent) {
                    var oPreviewImg = new Image();
                    var $modalAvatarAreaImg = $modalAvatarArea.find('img');
                    oPreviewImg.src = oFREvent.target.result;
                    if ($modalAvatarAreaImg.size() > 0) {
                        $modalAvatarAreaImg.cropper('replace', oFREvent.target.result);
                    } else {
                        $modalAvatarArea.append($(oPreviewImg));
                        setModalAvatarPic.call(oPreviewImg);
                    }
                    $(options.previewAvatar).find('>img').show().attr('src', oFREvent.target.result);
                };
                oFReader.readAsDataURL(aFiles[0]);
            }
            if (navigator.appName === "Microsoft Internet Explorer") {
                $modalAvatarArea[0].filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = $(this).value;
            }
            uploadFile = aFiles[0];
        });

        $btnModalUpload.on('click', function () {
            var $previewImg = $modalAvatarArea.find('>img');
            var imgData = $previewImg.cropper('getData');
            var rUrl;
            var oReq = new XMLHttpRequest();
            var $btn = $(this);
            if (!$avatarFile.val()) {
                alert('请先上传图片');
                return false;
            }
//            console.log($previewImg.width(),minCropBoxWidth,imgData)
            if (imgData.x < 0 || imgData.y < 0 || $previewImg.parent().width() < minCropBoxWidth || $previewImg.parent().height() < minCropBoxHeight) {
                alert('请上传尺寸大于' + minCropBoxWidth + '×' + minCropBoxHeight + '的图片');
                return false;
            }

            $(this).addClass('load').prop('disabled', true);
            oMyForm = new FormData();
            for (var i = 0; i < mValueResults.length; i++) {
                var key = mValueResults[i].replace('{{', '');
                key = key.replace('}}', '');
                rUrl = url.replace(mValueResults[i], Math.floor(imgData[key]));
                url = rUrl;
            }
            url = options.url;
            oMyForm.append(fileVal, uploadFile);
            oReq.open('post', rUrl);
            oReq.onload = function (oEvent) {
                if (oReq.status == 200) {
                    options.success.call(null, JSON.parse(oReq.responseText));
                    $modal.modal('hide');
                } else {
                    options.error.call(null, oReq.responseText)
                }
                $btn.prop('disabled', false);
            };
            oReq.send(oMyForm);
        });


        $modal.on('hide.bs.modal', function () {
            var $previewImg = $modalAvatarArea.find('>img');
            $previewImg.cropper('destroy');
            $previewImg.detach();
            oMyForm = null;
            $avatarFile.val('');
            $(options.previewAvatar).height('auto').find('img').hide();
        });


        function setModalAvatarPic() {
            $(this).cropper(cropperOption);
        }


    };


    $.fn.uploadAvatar.default = {
        modal: modalId,
        url: uploadUrl,
        avatarPic: imgId,
        cropperOption: {
            aspectRatio: 1,
            viewMode: 1,
            minCropBoxWidth: width,
            minCropBoxHeight: height,
            zoomOnWheel: false,
            crop: function (e) {
            }
        },
        //canvas
        fileVal: 'upfile',
        previewAvatar: '.preview-avatar',
        error: function () {
        },
        success: function () {
        }
    };

    $(function () {
        var cropperPreviewFlag = false;
        var $previews = $('.preview-avatar');
        $(btnid).uploadAvatar({
            cropperOption: {

                crop: function (e) {
                    if (!cropperPreviewFlag) {
                        cropperPreviewFlag = true;
                        var $clone = $('img.cropper-hidden').clone().removeClass('cropper-hidden');
                        $clone.css({
                            display: 'block',
                            width: '100%',
                            minWidth: 0,
                            minHeight: 0,
                            maxWidth: 'none',
                            maxHeight: 'none'
                        });
                        $previews.html($clone);
                    }
                    var imageData = $(this).cropper('getImageData');
                    var previewAspectRatio = e.width / e.height;
                    $previews.each(function () {
                        var $preview = $(this);
                        var previewWidth = $preview.width();
                        var previewHeight = previewWidth / previewAspectRatio;
                        var imageScaledRatio = e.width / previewWidth;
                        $preview.height(previewHeight).find('img').css({
                            width: imageData.naturalWidth / imageScaledRatio,
                            height: imageData.naturalHeight / imageScaledRatio,
                            marginLeft: -e.x / imageScaledRatio,
                            marginTop: -e.y / imageScaledRatio
                        });
                    });
                }
            },
            success: function (data) {
                var $inputPhoto = $('input[name="' + column + '"]');
                $(imgId).attr('src', data.url);
                if ($inputPhoto.size() > 0) {
                    $inputPhoto.val(data.ftpUrl)
                } else {
                    $('<input name="' + column + '" type="hidden" value="' + data.ftpUrl + '">').insertAfter($(imgId));
                }
            }
        })
    })

</script>