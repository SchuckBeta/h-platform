<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
	<%--<link rel="stylesheet" type="text/css" href="/common/common-css/bootstrap.min.css">--%>
	<link rel="shortcut icon" href="/images/bitbug_favicon.ico" />
	<link rel="stylesheet" type="text/css" href="/css/slide_nav.css">
	<title>${backgroundTitle}</title>
	<meta name="decorator" content="backPath_header"/>
</head>
<body>
	<div class="center-content">
		<div class="slide_nav">
			<ul class="list_wrap" id="list_wrap" >
				<li class="level_one">
					<c:if test="${empty firstMenu.href}">
						<a href="javascript:;" style="border-bottom:1px solid #df3b0a;">
					</c:if>
					<c:if test="${not empty firstMenu.href}">
						<a href="${ctx }/${firstMenu.href}" target="mainFrame" style="border-bottom:1px solid #df3b0a;">
					</c:if>
						<img src="/img/icon_sc.png">
						<span>${firstMenu.name}</span>
					</a>
					<ul class="sub_list_wrap" id="sub_list_wrap">
						<c:forEach items="${secondMenus}" var="menu2" varStatus="idx">
						<c:if test="${fns:checkChildMenu(menu2.id) }">
							<li class="level_two">
							<c:if test="${not empty menu2.href}" >
								<a href="/a${menu2.href}" class="level_two_a" target="mainFrame">
										${menu2.name}
								</a>
							</c:if>
							<c:if test="${empty menu2.href}" >
								<a href="${not empty menu2.href?menu2.href:'javascript:;'}" class="level_two_a">
										${menu2.name}
								</a>
							</c:if>
								<ul class="grand_sub_wrap">
									<c:forEach items="${menu2.children}" var="menu3">
										<li class="level_three">
											<a href="/a${menu3.href}" target="mainFrame">${menu3.name}
												<c:if test="${menu3.todoCount>0}">
													<i class="unread-tag">${menu3.todoCount}</i>
												</c:if>
												<%--<c:if test="${menu3.todoCount<=0}">--%>
													<%--<i class="unread-tag"></i>--%>
												<%--</c:if>--%>
											</a>
										</li>
									</c:forEach>
								</ul>
							</li>
							</c:if>
						</c:forEach>


					</ul>
				</li>
			</ul>
		</div>
		<div class="view-panel" id="iframe_wrap">
			<iframe id="mainFrame" name="mainFrame" src="" frameborder="0" height="100%"></iframe>
		</div>
	<%-- 	<div class="view-copyright" id="copyright_wrap">
			<%@ include file="../../../views/layouts/website/copyright.jsp"%>
		</div> --%>
	</div>
<script type="text/javascript" src="/js/slide_nav.js"></script>
<!----------页面js初始化----------->
<script type="text/javascript">
	$(function(){
		var currentHref ="${href}";
		if(currentHref ==""){
			var homeHref=$(".level_two").find("a")[0].href;
			if(homeHref&&homeHref!="javascript:;"){
				currentHref = homeHref;
			}else{
				//第一个 sub_list_wrap ul ul li 点中
				$(".sub_list_wrap li:eq(0) ul:eq(0) li:eq(0) ").click();
				currentHref=$(".sub_list_wrap li:eq(0) ul:eq(0) li:eq(0) ").children('a').attr('href');
			}
		}else{
			var $elements = $('.slide_nav a');
			$elements.each(function(i,item){
				var $item = $(item);
				var link = $item.attr('href');
				if(link.indexOf(currentHref) > -1){
					$item.click();
					return false;
				}
			});

		}
		$('#mainFrame').attr('src',currentHref);
	});


</script>
</body>
</html>
