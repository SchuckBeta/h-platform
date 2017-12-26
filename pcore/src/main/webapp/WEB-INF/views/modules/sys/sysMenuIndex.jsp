<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${backgroundTitle}</title>
    <meta name="decorator" content="backPath_header"/>
    <link rel="shortcut icon" href="/images/bitbug_favicon.ico"/>
    <!--引用装饰器-->
    <style type="text/css">

    </style>
</head>
<body>
<c:if test="${msg!=null}">
    ${msg}
</c:if>
<div class="sys-menu-container container">
    <div class="row sys-menu-row">
        <c:forEach items="${fns:getAllMenuList()}" var="menu" varStatus="idxStatus">
            
                <c:if test="${menu.parent.id eq '1'&&menu.isShow eq '1'}">
                    <div class="span3">
                        <div class="menu-item <c:if test="${not fns:checkMenu(menu.id)}">menu-item-qx</c:if> menu ${not empty firstMenu && firstMenu ? ' active' : ''}">
                            <c:if test="${empty menu.href}">
                                <a href="${ctx}/sys/menu/treePlus?parentId=${menu.id}">
                                    <img class="menu-pic" src="${fns:ftpImgUrl(menu.imgUrl)}" alt=""/>
                                    <span class="name">${menu.name}</span>
                                </a>
                                <p class="desc">${menu.remarks}</p>
                            </c:if>
                            <c:if test="${not empty menu.href}">
                                <a href="${ctx}/sys/menu/treePlus?parentId=${menu.id}">
                                    <img class="menu-pic" src="${fns:ftpImgUrl(menu.imgUrl)}" alt=""/>
                                    <span class="name">${menu.name}</span>
                                </a>
                                <p class="desc">${menu.remarks}</p>
                            </c:if>
                            <div class="layer-jurisdiction">
                                <span class="text">请开通权限访问</span>
                            </div>
                        </div>
                    </div>
                </c:if>
        </c:forEach>
    </div>
</div>

<%@ include file="../../../views/layouts/website/copyright.jsp" %>
</body>
</html>
