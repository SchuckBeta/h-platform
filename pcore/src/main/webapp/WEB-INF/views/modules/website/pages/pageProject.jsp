<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<html>

<head>
    <meta charset="utf-8"/>
    <meta name="decorator" content="site-decorator"/>
    <title>${frontTitle}</title>
    <link rel="shortcut icon" href="/images/bitbug_favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="/other/icofont/iconfont.css">
    <link rel="stylesheet" type="text/css" href="/css/projectShow.css">
    <link rel="stylesheet" type="text/css" href="/css/pagination.css">
    <script type="text/javascript">
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        function changeType(type) {
            $("#type").val(type);
            $("#pageNo").val('0');
            $("#searchForm").submit();
            return false;
        }
        function changeFind() {
            $("#searchForm").submit();
            return false;
        }
        $(document).ready(function(){
            $('.pagination_num').removeClass('row')
        })
    </script>
</head>
<body>
<div id="projectShowPages" class="container-fluid container-fluid-oe">
    <ol class="breadcrumb" style="margin-top: 0">
        <li><a href="/f/"><i class="icon-home"></i>首页</a></li>
        <li class="active">优秀展示</li>
    </ol>
    <div class="tab-container">
        <div class="tab-header">
            <ul class="nav-inline clearfix" role="tablist">
                <c:choose>
                    <c:when test="${type=='0000000076'}">
                        <li role="presentation">
                            <a onclick="changeType('')" role="tab" data-toggle="tab">全部</a>
                        </li>
                        <li role="presentation">
                            <a onclick="changeType('0000000075')" role="tab" data-toggle="tab">优秀项目</a>
                        </li>
                        <li role="presentation" class="active">
                            <a onclick="changeType('0000000076')" role="tab" data-toggle="tab">获奖大赛</a>
                        </li>

                    </c:when>
                    <c:when test="${type=='0000000075'}">
                        <li role="presentation">
                            <a  onclick="changeType('')" role="tab" data-toggle="tab">全部</a>
                        </li>
                        <li role="presentation" class="active">
                            <a  onclick="changeType('0000000075')" role="tab" data-toggle="tab">优秀项目</a>
                        </li>
                        <li role="presentation">
                            <a  onclick="changeType('0000000076')" role="tab" data-toggle="tab">获奖大赛</a>
                        </li>

                    </c:when>
                    <c:otherwise>
                        <li role="presentation" class="active">
                            <a  onclick="changeType('')" role="tab" data-toggle="tab">全部</a>
                        </li>
                        <li role="presentation">
                            <a  onclick="changeType('0000000075')" role="tab" data-toggle="tab">优秀项目</a>
                        </li>
                        <li role="presentation">
                            <a onclick="changeType('0000000076')" role="tab" data-toggle="tab">获奖大赛</a>
                        </li>

                    </c:otherwise>
                </c:choose>
            </ul>
            <form:form id="searchForm" modelAttribute="excellentShow" action="/f/pageProject" method="post"
                       class="form-search clearfix">
                <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
                <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
                <input id="type" name="type" type="hidden" value="${type}"/>
                <div class="clearfix">
                    <div class="form-search-box">
                        <div class="input-group">
                            <input type="text" name="content" value="${key}" placeholder="关键字输入" class="form-control">
                            <span class="input-group-addon" id="basic-addon2">
                                <i class="iconfont ico_search icon-sousuo-sousuo"
                                   style="display: inline-block;height: 13px;font-size: 14px"
                                   onclick="changeFind()"></i>
                            </span>
                        </div>
                    </div>
                </div>
            </form:form>
        </div>
        <div class="tab-content">
            <div role="tabpanel" class="tab-pane active">
                <div class="row" style="margin-right: -15px;margin-left: -15px">
                    <c:forEach var="project" items="${page.list}">
                        <div class="col-md-3">
                            <div class="project-item">
                                <a class="project-pic" href="/f/frontExcellentView-${project.id}" style="background: url('/img/video-default.jpg') no-repeat center/cover;">
                                    <img class="img-responsive"
                                         src="${not empty project.coverImg ? fns:ftpImgUrl(project.coverImg) : '/img/1X1placeholderimg.png'}"
                                         alt="${project.name}">
                                </a>
                                <h4 class="pro-title">
                                    <a title="${project.name}"
                                       href="/f/frontExcellentView-${project.id}">${project.name}</a>
                                </h4>
                                <div class="pro-tags">
                                    <c:forEach items="${project.keywords}" var="item">
                                        <span>${item}</span>
                                    </c:forEach>
                                </div>
                                <div class="pro-info">
                                    <p>项目来源:${project.subType}</p>
                                    <p style="display: none">荣获奖项: 优秀项目</p>
                                    <p>项目负责人: ${fns:getUserById(project.leaderId).name}</p>
                                    <p>学院: ${fns:getUserById(project.leaderId).office.name}</p>
                                </div>
                                <div class="pro-time-look-wx">
                                    <span class="time"><i class="iconfont ico icon-rili"></i>
                                        <fmt:formatDate value="${project.update_date}" pattern="yyyy-MM-dd "/>
                                    </span>
                                    <span class="look"><i class="iconfont icon-yanjing1"></i>${project.views}</span>
                                    <span class="wx"><i class="iconfont icon-tuxing1"></i>${project.comments}</span>
                                    <span class="wx"><i class="iconfont icon-iconfontdianzan"></i>${project.likes}</span>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
    ${page.courseFooter}
</div>


</body>
</html>