<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<html>

<head>
    <meta charset="utf-8"/>
    <meta name="decorator" content="site-decoratorNew"/>
    <link rel="shortcut icon" href="/images/bitbug_favicon.ico">
    <title>${frontTitle}</title>
</head>

<body>
<c:forEach var="site" items="${fns:site() }">
    <c:forEach var="category" items="${site.categorys}">
        <c:forEach var="region" items="${category.childRegionList}">
            <c:if test="${region.regionState eq '1' }">
                <cms:frontBanner region="${region }"/>
                <cms:frontCarousel region="${region }" user="${user }"/>
                <cms:frontHots region="${region }"/>
                <cms:frontShow region="${region }"/>
                <cms:frontDataGroups region="${region }"/>
                <cms:frontDataBlocks region="${region }"/>
            </c:if>
        </c:forEach>
        <div class="sc-news">
            <input id="userId" type="hidden" value="${user }"/>
            <c:forEach var="region" items="${category.childRegionList}">
                <c:if test="${(region.id eq '2367106550234879b0bedd745fdd4c96')}">
                    <cms:frontDataPanels region="${region }" oaNotifys="${fns:getOaNotifysSC() }" type="SC"/>
                </c:if>
                <c:if test="${(region.id eq 'b0c0a68c40624a5c8a44fa29dbcac539')}">
                    <cms:frontDataPanels region="${region }" oaNotifys="${fns:getOaNotifysTZ() }" type="TZ"/>
                </c:if>
                <c:if test="${(region.id eq '7ae366ba21a44934b820364edaa8b292')}">
                    <cms:frontDataPanels region="${region }" oaNotifys="${fns:getOaNotifysSS() }" type="SS"/>
                </c:if>
            </c:forEach>
        </div>
    </c:forEach>
</c:forEach>

<!--本页面相关js部分-->
<script src="/js/index.js" type="text/javascript" charset="utf-8"></script>
</body>
</html>