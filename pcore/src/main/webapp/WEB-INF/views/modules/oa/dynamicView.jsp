<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="utf-8" />
<meta name="decorator" content="site-decorator" />
<link rel="stylesheet" href="/common/common-css/common.css" />
<link rel="stylesheet" href="/common/common-css/bootstrap.min.css" />
<link rel="stylesheet" href="/css/scStyle.css" />
<title>${frontTitle}</title>
</head>

<body>
	<div class="container" style="margin-top: 40px;">
		<div class="scinfo">
			<div style="clear: both;"></div>
			<div class="sctitle">
				<h3>${oaNotify.title }</h3>
				<h5>发布时间:<fmt:formatDate value="${oaNotify.updateDate}" pattern="yyyy-MM-dd" />   来源:${oaNotify.source } 浏览器:${oaNotify.views }</h5>
			</div>
			<div style="clear: both;"></div>
			${oaNotify.content }
			<div class="more">

				<h3>相关推荐</h3>
				<div style="border: 1px solid red;"></div>
			</div>

			<div class="clearfix"></div>
			<div class="newinfo">
				<ul>
					<c:forEach items="${more}" var="item">
						<li><span><fmt:formatDate value="${item.update_date}" pattern="yyyy-MM-dd" /></span><a href="${ctxFront}/oa/oaNotify/viewDynamic?id=${item.id}" >${item.title}</a></li>
                    </c:forEach>
				</ul>
			</div>
		</div>
	</div>
</body>

</html>