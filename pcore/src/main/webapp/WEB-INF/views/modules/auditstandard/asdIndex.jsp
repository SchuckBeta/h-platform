<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${asdVo.name}</title>
    <%@include file="/WEB-INF/views/include/backtable.jsp" %>
    <script src="/js/common.js" type="text/javascript"></script>
    <link rel="stylesheet" href="/css/credit-module.css">
    <style>
        .flow-example {
            margin-bottom: 30px;
        }

        .flow-example .flow-col {
            padding-top: 30px;
            width: 110px;
            margin-right: 0;
        }

        .flow-example .flow-arrow {
            padding-top: 50px;
            width: 80px;
        }

        * {
            box-sizing: content-box;
        }

        .container-big-match {
            background: #fff url('/images/bigMatchIconbg.jpg') no-repeat center;
        }

        .container-big-match .wrap {
            position: relative;
            width: 100%;
            margin: 0 auto;
            max-width: 800px;
        }

        .container-big-match .circles-bg {
            padding-top: 50%;
            background: url('/images/circles.png') no-repeat center/cover;
        }

        .container-big-match .projects {
            position: absolute;
            left: 0;
            top: 0;
            z-index: 1000;
        }

        .container-big-match .project-intro {
            position: absolute;
            padding-top: 40px;

        }

        .container-big-match .project-pic {
            position: absolute;
            left: 50%;
            bottom: 70px;
            margin-left: -35px;
            width: 70px;
            height: 70px;
            background: url('/images/bigMatchIcon.png') no-repeat;
        }

        .container-big-match .circle {
            width: 101px;
            height: 101px;
            padding: 8px;
            border-radius: 50%;
        }

        .container-big-match .circle-inner {
            padding-top: 40px;
            height: 61px;
            border-radius: 50%;
        }

        .container-big-match .circle-pink {
            background-color: rgba(254, 211, 219, 0.43);
        }

        .container-big-match .circle-pink .circle-inner {
            background-color: #fc98ab;
        }

        .container-big-match .circle-purple {
            background-color: rgba(213, 165, 251, 0.54);
        }

        .container-big-match .circle-purple .circle-inner {
            background-color: #d5a5fb;
        }

        .container-big-match .circle-blue {
            background-color: rgba(134, 189, 245, 0.54);
        }

        .container-big-match .circle-blue .circle-inner {
            background-color: #288aee;
        }

        .container-big-match .circle-yellow {
            background-color: rgba(249, 216, 190, 0.54);
        }

        .container-big-match .circle-yellow .circle-inner {
            background-color: #f3b786;
        }

        .container-big-match .circle-inner p {
            margin-bottom: 0;
            color: #fff;
            font-weight: bold;
            text-align: center;
        }

        .container-big-match .circle-inner .circle-label {
            position: relative;
            font-size: 16px;
        }

        .container-big-match .circle-inner .number {
            position: relative;
            font-size: 20px;
        }

        .project-declaration {
            left: 121px;
            top: 137px;
        }

        .project-creative {
            top: 121px;
            left: 318px;
        }

        .project-creating {
            top: 40px;
            left: 531px;
        }

        .project-teacher {
            top: 228px;
            left: 500px;
        }

        .container-big-match .project-pic-declaration {
            background-position: -10px -263px;
        }

        .container-big-match .project-pic-declaration:hover {
            background-position: -93px -263px;
        }

        .container-big-match .project-pic-creative {
            background-position: -6px -84px;
        }

        .container-big-match .project-pic-creative:hover {
            background-position: -89px -84px;
        }

        .container-big-match .project-pic-creating {
            background-position: -10px -164px;
        }

        .container-big-match .project-pic-creating:hover {
            background-position: -93px -164px;
        }

        .container-big-match .project-pic-teacher {
            background-position: 2px 0;
        }

        .container-big-match .project-pic-teacher:hover {
            background-position: -81px 0;
        }

    </style>
</head>
<body>
<div class="container-fluid">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>${asdVo.year}${asdVo.name}信息统计</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <div class="container-fluid container-big-match">
        <div class="wrap">
            <div class="circles-bg"></div>
            <div class="projects">
                <c:if test="${asdVo.type=='1'}">
                    <div class="project-intro project-declaration">
                        <div class="project-pic project-pic-declaration"></div>
                        <div class="circle circle-pink">
                            <div class="circle-inner">
                                <p class="circle-label">申报学生</p>
                                <p class="number">${asdVo.applyNum}人</p>
                            </div>
                        </div>
                    </div>
                    <div class="project-intro project-creative">
                        <div class="project-pic project-pic-creative"></div>
                        <div class="circle circle-purple">
                            <div class="circle-inner">
                                <p class="circle-label">创新项目</p>
                                <p class="number">${asdVo.innovateNum}人</p>
                            </div>
                        </div>
                    </div>
                    <div class="project-intro project-creating">
                        <div class="project-pic project-pic-creating"></div>
                        <div class="circle circle-blue">
                            <div class="circle-inner">
                                <p class="circle-label">创业项目</p>
                                <p class="number">${asdVo.businessNum}人</p>
                            </div>
                        </div>
                    </div>
                    <div class="project-intro project-teacher">
                        <div class="project-pic project-pic-teacher"></div>
                        <div class="circle circle-yellow">
                            <div class="circle-inner">
                                <p class="circle-label">项目导师</p>
                                <p class="number">${asdVo.teacherNum}人</p>
                            </div>
                        </div>
                    </div>
                </c:if>
                <c:if test="${asdVo.type=='2'}">
                    <div class="project-intro project-declaration">
                        <div class="project-pic project-pic-declaration"></div>
                        <div class="circle circle-pink">
                            <div class="circle-inner">
                                <p class="circle-label">参赛总人数</p>
                                <p class="number">${asdVo.innovateNum}人</p>
                            </div>
                        </div>
                    </div>
                    <div class="project-intro project-creative">
                        <div class="project-pic project-pic-creative"></div>
                        <div class="circle circle-purple">
                            <div class="circle-inner">
                                <p class="circle-label">参赛项目数</p>
                                <p class="number">${asdVo.applyNum}个</p>
                            </div>
                        </div>
                    </div>
                    <div class="project-intro project-teacher">
                        <div class="project-pic project-pic-teacher"></div>
                        <div class="circle circle-yellow">
                            <div class="circle-inner">
                                <p class="circle-label">指导老师数</p>
                                <p class="number">${asdVo.teacherNum}人</p>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>${asdVo.name}审核流程及标准</span>
            <i class="line weight-line"></i>
        </div>
    </div>
    <div id="flowExample" class="flow-example" style="margin: 0 auto 60px;">
    </div>
</div>


<script type="text/javascript">
    var $flowExample = $('#flowExample');
    $(function () {
        console.log(${fns:toJson(asdVo.asdYwGnodes)})
        init(JSON.parse('${fns:toJson(asdVo.asdYwGnodes)}'));
    });
    function init(data) {
        var html = '';

        $.each($(data), function (i, item) {
            var gnode = item;
            var name = gnode.name || '';
            console.log(name)
            var auditStandardName = item.auditStandardName;
            if (!auditStandardName) {
                auditStandardName = "";
            }
            var auditStandardId = item.auditStandardId;
            var iconurl = gnode.iconUrl;
            if (iconurl == undefined) {
                iconurl = "/images/gnode.png";
            } else {
                iconurl = ftpHttpUrlByIp("${fns:getConfig('ftp.httpUrl')}", iconurl);
            }
            if (i == 0) {
                name = '开始';
                html += flowCol(iconurl, name, auditStandardName, auditStandardId, false) + flowArrow()
            } else if (i == data.length - 1) {
                name = '结束';
                html += flowCol(iconurl, name, auditStandardName, auditStandardId, false);
            } else {
            	if(name && name.indexOf('（') != -1){
                	name = name.substring(0, name.indexOf('（'));
            	}
                html += flowCol(iconurl, name, auditStandardName, auditStandardId, true) + flowArrow();
            }
        });
        $flowExample.html(html).width(180 * data.length);
    }

    function flowArrow() {
        return '<div class="flow-col flow-arrow"> <p class="flow-arrow-text flow-arrow-text-hiddle">通过</p> <div class="flow-arrow-pic" style="background-image: url(/img/flow-arrow.png)"></div> </div>'
    }

    function flowCol(img, name, auditStandardName, auditStandardId, isShow) {
        var flowIntro = '';
        if (isShow) {
            flowIntro += '<div class="flow-intro"><a style="cursor:pointer;"  title="' + auditStandardName +  '评审标准" data-id="' + auditStandardId + '">' + auditStandardName + '</a></div>';
        }
        return '<div class="flow-col"> <div class="flow-step"> <img src="' + img + '"><p class="flow-title">' + name + '</p></div>' + flowIntro + ' </div>';
    }


    $(document).on('click', '.flow-intro>a', function (e) {
        e.preventDefault();
        var id = $(this).attr('data-id');
        var name = $(this).attr('title');
        if (id) {
            top.$.jBox.open('iframe:' + '${ctx}/auditstandard/auditStandard/view?id=' + id, name, 800, $(top.document).height() - 300, {
                buttons: {"关闭": true},
                loaded: function (h) {
                    $(".jbox-content", top.document).css("overflow-y", "hidden");
                }
            });
        }
    })

</script>
</body>
</html>