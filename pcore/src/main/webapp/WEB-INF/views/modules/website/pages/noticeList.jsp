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
        .table>thead>tr>th{
            white-space: nowrap;
        }
        .table>tbody>tr:last-child>td{
            white-space: nowrap;
        }
    </style>
    <script>
        $(function () {
            $('.pagination_num').removeClass('row')
            var minHeight = $(window).height() - $('.footerBox').height() - $('.header').height();
            $('#content').css('min-height',minHeight)
        })
    </script>
</head>
<body>
<div class="container-fluid container-fluid-oe">
    <ol class="breadcrumb">
        <li><a href="/f/"><i class="icon-home"></i>首页</a></li>
        <li class="active">通知公告列表</li>
    </ol>
    <form id="searchForm" action="/f/frontNotice/noticeList">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    </form>
    <div class="edit-bar">
        <div class="edit-bar-left">
            <span>通知公告</span>
            <i class="line"></i>
        </div>
    </div>
    <div class="list-group">
        <c:forEach items="${page.list}" var="notify" varStatus="status">
            <a href="/f/frontNotice/noticeView?id=${notify.id}" class="list-group-item">
                <span class="date">
                 <fmt:formatDate value="${notify.updateDate}" pattern="yyyy-MM-dd "/>
                </span>
                    ${notify.title}
            </a>
        </c:forEach>
    </div>
    ${page.footer}
</div>
<script type="text/javascript">
    $(function(){
        var minHeight = $(window).height() - $('.footerBox').height() - $('.header').height();
        $('.notice-container').css('min-height',minHeight);
        $("#ps").val($("#pageSize").val());
    })

    function page(n, s) {
        $("#pageNo").val(n);
        $("#pageSize").val(s);
        $("#searchForm").submit();
        return false;
    }
</script>
</body>
</html>