<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8"/>
    <meta name="decorator" content="site-decorator"/>
    <title>${frontTitle}</title>
    <link type="text/css" rel="stylesheet" href="/css/noticeCommon.css">
    <style type="text/css">
        .breadcrumb{
            margin-top: 30px;
            padding-left: 0;
            background-color: #fff;
        }
        .breadcrumb a{
            text-decoration: none;
        }
        .breadcrumb{
            margin-top: 40px;
        }
        .breadcrumb>li>a{
            color: #333;
        }
        .breadcrumb>li{
            color: #777;
        }
    </style>
</head>
<body>
<div class="container-fluid container-fluid-oe">
    <ol class="breadcrumb breadcrumb-primary">
        <li><a href="/f/"><i class="icon-home"></i>首页</a></li>
        <li><a href="/f/frontNotice/noticeList">通知公告列表</a></li>
        <li class="active">查看</li>
    </ol>
    <h3 class="letter-title">${title}</h3>
    <div class="letter-content" id="letter-content">

    </div>
</div>
<script type="text/javascript">
    $(function(){
        var minHeight = $(window).height() - $('.footerBox').height() - $('.header').height();
        $('#content').css('min-height',minHeight)
        $("#letter-content").html($('${content}'));
    })
</script>
</body>
</html>
