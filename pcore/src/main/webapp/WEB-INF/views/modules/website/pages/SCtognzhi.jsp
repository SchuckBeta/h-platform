<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="decorator" content="site-decorator"/>
    <!--双创通知样式页面-->

    <!--公用重置样式文件-->
    <link rel="stylesheet" type="text/css" href="/common/common-css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/css/sctz.css"/>
    <script src="/js/projectShow.js"></script>
    <script type="text/javascript">
        var viewUrl = "${ctxFront}/oa/oaNotify/viewDynamic";
        $(document).ready(function () {
            $('#TAB>li').on('click', function () {
                $(this).addClass('active').siblings().removeClass('active')
                $('#tab_chang .tab_select').eq($(this).index()).show().siblings().hide();
            })
            page1(1, 10);
            page2(1, 10);
            page3(1, 10);
            var hash = window.location.hash;
            $(hash).trigger('click');
        });
        function page1(pageNo, pageSize) {
            $.ajax({
                url: "/f/oa/oaNotify/getPageJson",
                dataType: 'json',
                data: {
                    funcName: "page1",
                    type: "4",
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                success: function (data) {
                    $("#page1").html("");
                    var dataList = data.list;
                    var pageHtml = data.courseFooter;

                    $(dataList).each(function (i, oaNotify) {
                        var href = viewUrl + "?id=" + oaNotify.id;
                        var liHtml = '<li> <a href="' +
                                href +
                                '">' +
                                oaNotify.title +
                                ' <span>' +
                                oaNotify.updateDate +
                                '</span></a> </li>';

                        $("#page1").append(liHtml)
                    });
                    $("#page1").append(pageHtml);
                }
            })
        }
        function page2(pageNo, pageSize) {
            $.ajax({
                url: "/f/oa/oaNotify/getPageJson",
                dataType: 'json',
                data: {
                    funcName: "page2",
                    type: "8",
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                success: function (data) {
                    $("#page2").html("");
                    var dataList = data.list;
                    var pageHtml = data.courseFooter;

                    $(dataList).each(function (i, oaNotify) {
                        var href = viewUrl + "?id=" + oaNotify.id;
                        var liHtml = '<li> <a href="' +
                                href +
                                '">' +
                                oaNotify.title +
                                ' <span>' +
                                oaNotify.updateDate +
                                '</span></a> </li>';

                        $("#page2").append(liHtml)
                    });
                    $("#page2").append(pageHtml);
                }
            })
        }
        function page3(pageNo, pageSize) {
            $.ajax({
                url: "/f/oa/oaNotify/getPageJson",
                dataType: 'json',
                data: {
                    funcName: "page3",
                    type: "9",
                    pageNo: pageNo,
                    pageSize: pageSize
                },
                success: function (data) {
                    $("#page3").html("");
                    var dataList = data.list;
                    var pageHtml = data.courseFooter;

                    $(dataList).each(function (i, oaNotify) {
                        var href = viewUrl + "?id=" + oaNotify.id;
                        var liHtml = '<li> <a href="' +
                                href +
                                '">' +
                                oaNotify.title +
                                ' <span>' +
                                oaNotify.updateDate +
                                '</span></a> </li>';

                        $("#page3").append(liHtml)
                    });
                    $("#page3").append(pageHtml);
                }
            })
        }


    </script>
    <title>${frontTitle}</title>
    <style>
        .sxtz_li ul li a:hover, .sxtz_li ul li a:focus {
            color: #e9432d;
            text-decoration: none;
        }

        ul li {
            list-style: none;
        }

        .pagination_num .pagination_list li {
            line-height: 30px;
        }

        .pagination_num .pagination_list li a {
            text-decoration: none;
        }

        .pagination_num {
            margin: 15px 0 30px !important;
        }

        .tab_clum .tab li.active {
            border-bottom: 3px solid #e92f27;
        }

        .tab_clum .tab li.active a {
            color: #e9432d;
        }
    </style>
</head>

<body>
<div class="container" style="margin-top: 60px;min-height: 700px">
    <div class="scinfo">
        <!-- <div class="scinfonavigation">
            <ul>
                <li><img src="/img/bc_home.png"></li>
                <li><a href="/f/">首页></a>  </li>
                <li>双创通知</li>
            </ul>
        </div> -->
    </div>
    <div style="clear: both;"></div>
    <div class="tab_clum" style="margin-top: 0">
        <ul class="tab" id="TAB">
            <li id="SC" class="active">
                <a href="javascript:void(0);">双创动态</a>
            </li>
            <li id="TZ">
                <a href="javascript:void(0);">双创通知</a>
            </li>
            <li id="SS">
                <a href="javascript:void(0);">省市动态</a>
            </li>
        </ul>
    </div>
    <div style="clear: both;"></div>
    <div id="tab_chang">
        <div class="sxtz_li tab_select">
            <ul id="page1">

            </ul>
        </div>
        <div class="sxtz_li tab_select" style="display: none;">
            <ul id="page2">

            </ul>
        </div>
        <div class="sxtz_li tab_select" style="display: none;">
            <ul id="page3">

            </ul>
        </div>
    </div>
</div>
</body>
</html>
