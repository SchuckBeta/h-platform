<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>
<html>

<head>
<meta charset="utf-8" />
<meta name="decorator" content="site-decorator" />
<title>首页</title>
<style type="text/css">
.carousel {
	/*width: 600px;*/
	/*background: #000;*/
	opacity: .6;
}

.carousel-inner {
	/*padding:10px 20%;*/
	width: 1230px;
	margin: 0px auto;
	height: 40px;
	font-size: 14px;
}

.carousel-inner>.item {
	-webkit-transition: -webkit-transform 6s ease-in-out;
	-o-transition: -o-transform 6s ease-in-out;
	transition: transform 6s ease-in-out;
	text-align: center;
	font-size: 14px;
	color:#666;
	line-height: 40px;
}

.carousel-inner>.img-wrap>img {
	width: 20px;
	height: 20px;
}

.carousel-indicators {
	bottom: -10px;
}

.box-wrap {
    border-radius: 3px;
    position: fixed;
    bottom: 0;
    right: 0;
}

.content .msg{
	color: #000!important;
	text-align: center;
}
.content span{
  color:#000!important;
}


.btn-group  {
	width:100%;
	text-align:center;
}

.footer {
	text-align: right;
}

.btn_info {
	padding:5px 11px;
	color: #fff;
	border: none!important;
	background-color: #e04527!important;
	text-transform: none;
	height: 28px;
	border-radius: 4px;
}
 #refuse-btn{
 	width: 50px;
	border: none!important;
	background-color:#e04527!important;
	text-transform: none;
	height: 28px;
	border-radius: 4px;
 }
 .btns a{
    color:#fff!important;
 }
 .btns a{
   text-decoration: none;
 }
 .title{
    height:initial!important;
 }
</style>
</head>

<body>

	<!--banner部分-->
	<div class="bannerBox">
		<div id="slideBox" class="slideBox">
			<div class="bd">
				<ul>
					<li>
						<p>主办部门：教务处创新创业教育办公室</p> <img src="/img/index1-banner.png">
						<a class="btn1" href="/f/html-2017gcsb">申报通知</a> <a class="btn2"
						href="/f/project/projectDeclare/form">申报</a>
					</li>
					<li>
						<!-- <p>主办部门：教务处创新创业教育办公室</p>  -->
						<img src="/img/index2-banner.jpg"> <a class="btn1"
						href="/f/html-sctzsj">申报通知</a> <a class="btn2"
						href="/f/gcontest/gContest/form">申报</a>
					</li>
					<li>
						<!-- <p>主办部门：教务处创新创业教育办公室</p>  -->
						<img src="/img/index3-banner.jpg"> <a class="btn1" href="#">申报通知</a>
						<a class="btn2" href="#">申报</a>
					</li>
				</ul>
			</div>
		</div>
	</div>


	<div id="myCarousel" class="carousel slide">
		    <input id="userId" type="hidden" value="${user }" />

		<!-- 轮播（Carousel）项目 -->
		<div class="carousel-inner" id="wrap" style="max-width: 1243px; position: relative; ">
			<section class="img-wrap" style="position: absolute; top: 10px; left: 0px;background: #fff;z-index:9999;">
				<img src="img/index-notify2.png" >
			</section>
		</div>
		<!-- 轮播（Carousel）上 ，下导航 -->
		<%--<a class="carousel-control left" href="#myCarousel" data-slide="next"--%>
			<%--style="background: #f7f7f7;">&lsaquo;</a> <a--%>
			<%--class="carousel-control right" href="#myCarousel" data-slide="prev"--%>
			<%--style="background: #f7f7f7;">&rsaquo;</a>--%>
	</div>
	<%--<img src="/img/index-notify1.png"--%>
		<%--style="margin-top: -66px; margin-left: 5%;height:16px;">--%>
	<!--大赛热点-->
	<div class="hotspotBox">
		<!--标题-->
		<a href="/f/page-gcontesthot" style="width:300px;display:block;margin:auto;"><img class="title"
			src="/img/index2.png" /></a>

		<!--大赛热点内容部分-->
		<div class="content">
			<!--视频播放-->
			<div class="leftVideoBox">
				<video id="media" height="413" preload="metadata" controls="controls" poster="/img/poster.png">
					<source src="/img/temple.mp4" type="video/mp4"></source>
					当前浏览器不支持 video直接播放，请使用高版本的浏览器查看。
				</video>
				<!--视频标题-->
				<p>“翱翔系列小卫星”夺冠第二届中国“互联网+”大学生创新创业“大赛</p>
				<!--播放按钮-->
				<img class="start" src="/img/start.png" />
			</div>

			<!--轮播-->
			<div class="rightScrollBox">
				<div id="slideBox2" class="slideBox2">
					<div class="bd">
						<ul>
							<li><img src="/img/indexscrollpic_03.png"></li>
							<li><img src="/img/review-fs-img002.jpg"></li>
						</ul>
					</div>
					<a class="prev" href="javascript:void(0)"></a> <a class="next"
						href="javascript:void(0)"></a>
				</div>
			</div>
		</div>
	</div>

	<!--装饰用的图-->
	<img class="indexdeco" src="/img/indexdecopic_14.png" alt="" />

	<!--优秀项目展示板块-->
	<div class="itemDisplayBox">

		<a href="/f/page-projectshow"><img class="title"
			src="/img/indexItemTitle.png" /></a>

		<ul class="threeBlock">
			<li>大赛获奖作品</li>
			<li class="current">优秀大创项目</li>
			<li>优秀科研成果</li>
		</ul>

		<ul class="content">
			<li><a href="/f/page-projectshowa38"><img src="/img/index-show-1.jpg" alt="" />
				<h5>基于搭载无线电中继系统UVA的紧急救灾相关研究</h5>
				<p id="spillout">以无人机作为升空平台搭载中继通信设备，通过对无人机或无人机编队的控制，将通信平台部署在目标任务区域，达到网络信号覆盖，实现地面任意两点间的通信的功能</p>
				</a>
			</li>
			<li><a href="/f/page-projectshowa03"><img src="/img/index-show-2.jpg" alt="" />
				<h5>野外求生多功能净水杯</h5>
				<p id="spillout2">本项目拟开发的多功能净水杯相比于传统净水器，具有体积小、重量轻、美观、净水简易的特点，且具有杀菌、消毒和加热的功能。</p>
			</a></li>
			<li><a href="/f/page-projectshowa21"><img src="/img/index-show-3.jpg" alt="" />
				<h5>可重构全向智能移动平台—X平台</h5>
				<p id="spillout3">X平台移动系统由移动模块和功能模块组成。移动模块负责行驶功能，功能模块负责完成各种功能。</p>
			</a>	</li>
		</ul>

	</div>

	<!--导师风采-->
	<div class="teacherGraceBox">
		<a href="/f/page-teacherGrace"><img class="title"
			src="/img/teacherTitle.png" alt="" /></a>
		<div class="content">
			<div class="left">
				<div id="slideBox3" class="slideBox3">
					<div class="hd">
						<ul>
							<li></li>
							<li></li>
							<li></li>
						</ul>
					</div>
					<div class="bd">
						<ul>
							<li>
								<div class="infor">
									<div class="people">
										<img src="/img/index111.png" />
									</div>
									<p>
										<strong>胡宏伟</strong>
									</p>
									<p>
										<span>副教授</span><span>博士研究生</span><span>工学博士</span><span>长沙理工大学汽车与机械工程学院副院长</span>
									</p>
								</div>
								<div class="intro">
									<p>胡宏伟，男，1980年出生，副教授，博士研究生，工学博士，长沙理工大学汽车与机械工程学院副院长。主要研究方向：无损检测及信号处理、机械动力学与控制。
										主要学习及工作经历</p>
									<p>2001年7月毕业于武汉大学机械设计制造及其自动化专业，获工学学士学位；2004年6月毕业于武汉大学机械设计及理论专业，获工学硕士学位；2008年6月毕业于浙江大学机械制造及其自动化专业，获工学博士学位。2008年7月至今，在长沙理工大学汽车与机械工程学院工作。2012年晋升为副教授，并担任机械工程学科硕士生导师。2015年1月起担任汽车与机械工程学院副院长。学术兼职：湖南省仪器仪表学会常务理事、湖南省机械工程学会无损检测分会理事。</p>
									<p>主讲本科生《汽车试验学》《汽车专业英语》《汽车电器与电子技术》等课程，主讲研究生《工程测试与信号分析》课程。主持完成省级教改课题1项。获得校优秀网络课程2门。指导大学生创新计划项目3项。担任车辆0902班及车辆1301班班主任。</p>
									<p>科研情况</p>
									<p>（1）纵向及基础研究类课题：主持国家自然科学基金、湖南省自然科学基金、教育部博士点基金、湖南省科技计划、中国博士后科学基金等科研项目。参与国家自然科学基金、总装预研基金等项目。</p>
								</div>
							</li>
							<li>
								<div class="infor">
									<div class="people">
										<img src="/img/index111.png" />
									</div>
									<p>
										<strong>胡宏伟</strong>
									</p>
									<p>
										<span>副教授</span><span>博士研究生</span><span>工学博士</span><span>长沙理工大学汽车与机械工程学院副院长</span>
									</p>
								</div>
								<div class="intro">
									<p>胡宏伟，男，1980年出生，副教授，博士研究生，工学博士，长沙理工大学汽车与机械工程学院副院长。主要研究方向：无损检测及信号处理、机械动力学与控制。
										主要学习及工作经历</p>
									<p>2001年7月毕业于武汉大学机械设计制造及其自动化专业，获工学学士学位；2004年6月毕业于武汉大学机械设计及理论专业，获工学硕士学位；2008年6月毕业于浙江大学机械制造及其自动化专业，获工学博士学位。2008年7月至今，在长沙理工大学汽车与机械工程学院工作。2012年晋升为副教授，并担任机械工程学科硕士生导师。2015年1月起担任汽车与机械工程学院副院长。学术兼职：湖南省仪器仪表学会常务理事、湖南省机械工程学会无损检测分会理事。</p>
									<p>主讲本科生《汽车试验学》《汽车专业英语》《汽车电器与电子技术》等课程，主讲研究生《工程测试与信号分析》课程。主持完成省级教改课题1项。获得校优秀网络课程2门。指导大学生创新计划项目3项。担任车辆0902班及车辆1301班班主任。</p>
									<p>科研情况</p>
									<p>（1）纵向及基础研究类课题：主持国家自然科学基金、湖南省自然科学基金、教育部博士点基金、湖南省科技计划、中国博士后科学基金等科研项目。参与国家自然科学基金、总装预研基金等项目。</p>
								</div>
							</li>
						</ul>
					</div>
					<a class="prev" href="javascript:void(0)"></a> <a class="next"
						href="javascript:void(0)"></a>
				</div>
			</div>

			<ul class="right">
				<li>
					<div class="people">
						<img src="/img/tanglijun.png" />
					</div>
					<p class="infor">
						<big class="name">唐立军</big> <span>信号</span> <span>电子信息</span>
					</p>
					<p class="intro" id="intro">教授，博士，湖南省优秀教师，长沙理工大学教学名师，现任物理与电子科学学院院长</p>
				</li>
				<li>
					<div class="people">
						<img src="/img/wenyongjun.png" />
					</div>
					<p class="infor">
						<big class="name">文勇军</big> <span>信息</span>
					</p>
					<p class="intro" id="intro2">硕士。1997年毕业于长沙电力学院现任长沙理工大学大学物电学院电子信息教研室主任……</p>
				</li>
				<li>
					<div class="people">
						<img src="/img/hehuiyong.png" />
					</div>
					<p class="infor">
						<big class="name">贺慧勇</big> <span>信号</span>
					</p>
					<p class="intro" id="intro3">学士，教授。1983年毕业于北京大学物理系，获学士学位。现任长沙理工大学大学物电学院副院长……</p>
				</li>
				<li>
					<div class="people">
						<img src="/img/duronghua.png" />
					</div>
					<p class="infor">
						<big class="name">杜荣华</big> <span>信息</span> <span>安全</span> <span>智能</span>
					</p>
					<p class="intro" id="intro4">教授，博士研究生，工学博士，长沙理工大学汽车与机械工程学院院长……</p>
				</li>
				<i><a href="#">更多</a></i>
			</ul>
		</div>

	</div>
	<!--装饰用的图-->
	<a href="/f/page-teacherlecture"><img class="indexdeco"
		src="/img/indexdecopic2_09.png" alt="" /></a>

	<script>
		// 名师讲堂数据
		var teaching_data = [
				{
					media_url : '/img/lesson1.gif', // 图片或者视频的 url
					video : false, // media 是否是视频，如果是图片就是 false
					day : 20, // 日
					month : '02', // 月
					title : '创•课十讲：下一只独角兽的摇篮课', // 讲堂标题
					// 讲堂描述
					description : '创青春《创•课十讲》采访了十多位有代表性的创业者和创业导师，从《自我修炼》等十个主题与大家分享创业路上的经历、故事、思考与洞见',
					view_count : 2000, // 查看数         \
					voteup : 1922, // 点赞               -> 这3个数据不知道对不对，可能要改
					votedown : 23
				// 不点赞              /
				},
				{
					media_url : '/img/lesson2.gif',
					video : false,
					day : 20,
					month : '02',
					title : '创业导引—与创业名家面对面',
					description : '创投圈的成功导师程方、郭宇航、李竹、林森、罗键霆、马东、沈博阳、盛希泰、吴朝阳、杨歌、朱波等大咖和同学们坐而论道，全力打造未来',
					view_count : 2000,
					voteup : 1922,
					votedown : 23
				},
				{
					media_url : '/img/lesson3.gif',
					video : false,
					day : 20,
					month : '02',
					title : '创客培养：趣味力学实验与制作（自主模式）',
					description : '课程包括三部分内容：（1）主讲者参与拍摄的《走进科学》《原来如此》等栏目，介绍其中的道具设计和实验过程；（2）展示趣味科学游戏',
					view_count : 2000,
					voteup : 1922,
					votedown : 23
				}, {
					media_url : '/img/lesson4.gif',
					video : false,
					day : 20,
					month : '02',
					title : '计算机科学和Python编程导论',
					description : '本课程将介绍把计算机科学作为工具解决现实世界中的分析问题。',
					view_count : 2000,
					voteup : 1922,
					votedown : 23
				} ];
	</script>
	<div class="teacher-wrap">
		<div class="teacherClassBox">
			<div class="teach-box">
				<div class="media">
					<div class="date">
						<strong>{day}</strong>/{month}
					</div>
					<video id="media" height="413" preload="metadata" controls="controls" poster="/img/poster.png">
						<source my-src="{media_url}" type="video/mp4"></source>
						当前浏览器不支持 video直接播放，请使用高版本的浏览器查看。
					</video>
					<img my-src="{media_url}" />
				</div>
				<div class="description">
					<h4>{title}</h4>
					<p>{description}</p>
					<div class="btns">
						<img src="/img/pl.png"/>
						<a class="view-count">{view_count}</a>
						&nbsp;&nbsp;
						<img src="/img/ll.png"/>
						<a class="voteup">{voteup}</a>
						&nbsp;&nbsp;
						<img src="/img/z.png"/>
						<a class="votedown">{votedown}</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="sc-news">
		<div class="card sc-dyn-news">
			<div class="title">双创动态</div>
			<div class="photo">
				<a href="/f/page-scdt"><img src="/img/sc1.png" /></a>
			</div>
			<ul class="news-list" data-key="dynamic"></ul>
			<div class="more">
				<a href="/f/page-SCtognzhi">更多...</a>
			</div>
		</div>
		<div class="card sc-notifications">
			<div class="title">双创通知</div>
			<div class="photo">
				<a href="/f/page-SCtognzhi"><img src="/img/sc2.png" /></a>
			</div>
			<ul class="news-list" data-key="notifications"></ul>
			<div class="more">
				<a href="/f/page-SCtognzhi">更多...</a>
			</div>
		</div>
		<div class="card sc-other-news">
			<div class="title">省市动态</div>
			<div class="photo">
				<img src="/img/sc3.png" />
			</div>
			<ul class="news-list" data-key="others"></ul>
			<div class="more">
				<a href="/f/page-SCtognzhi">更多...</a>
			</div>
		</div>
	</div>
	<!--本页面相关js部分-->
	<script src="/js/index.js" type="text/javascript" charset="utf-8"></script>
	<!--页面js初始化-->
	<script type="text/javascript" >
		//dom显示隐藏
		function domIsShow(dom, isShow){
			if(isShow){
				$(dom).show();
			}else{
				$(dom).hide();

			}
		}

		//视频播放器
		function initVBox(vid){
			var domVBoxP = $(".leftVideoBox p");
			var domVBoxStart = $(".leftVideoBox .start");

			//视频插件Hover显示隐藏文本
			$(vid).hover(function(){
				domIsShow(domVBoxP, false);
			},function(){
				domIsShow(domVBoxP, true);
			});

			//播放按钮Hover显示隐藏文本
			$(domVBoxStart).hover(function(){
				domIsShow(domVBoxP, false);
			},function(){
				domIsShow(domVBoxP, true);
			});

			vid.addEventListener('play', function() {
				domIsShow(domVBoxP, false);
			})
			vid.addEventListener('pause', function() {
				domIsShow(domVBoxP, false);
			})
			vid.addEventListener('ended', function() {
				vid.controls=false;
				domIsShow(domVBoxP, true);
			})
		}

		$(function() {
			//视频播放器初始化
			initVBox(document.getElementById("media"));

			//跑马灯及尾部弹窗
			//			banner轮播初始化js
			var initBannerBox = function() {
				bannerBox.focusScroll();
			}();
			//			视频播放初始化js
			var initHotspotBox = function() {
				hotspotBox.videoPlay();
				hotspotBox.focusScroll();
			}();

			var initTeacherGraceBox = function() {
				teacherGraceBox.focusScroll();
			}();

			$.ajax({
				url : "${ctx}/loginList",
				type : "post",
				dataType : "json",
				success : function(data) {
					data.forEach(function(it) {
						var item = $('<div class="item">' + it + '</div>');
						$('#wrap').append(item);
					})
					$('#wrap div').eq(0).addClass('active');
				},
				error : function() {
				}
			});

			  // 初始化轮播
			$(".myCarousel").carousel({
				interval : 8000
			});//每页切 换时间间隔


			//团队邀请通知
			var user = $("#userId").val();
			if (user != null) {
				var list = [];
				$.ajax({
					type : "get",
					dataType : "json",
					url : "${ctx}/unReadOaNotify",
					success : function(res) {
						if (!res||!res[0]) {
							return false;
						}
						list = res;
						console.log(list);
						if (list[0].notifyId != null) {
							$("#box-wrap").show();
						}
						if (list[0].type == '5') {
							$('#username').html(list[0].sentName);
							$('#prj').html(list[0].teamName);
							$('#notifyId').html(list[0].notifyId);
							$('#oaNotifyType').text("申请加入");
							$("#view").hide();
						} else if(list[0].type == '6'){
							$('#username').html(list[0].sentName);
							$('#prj').html(list[0].teamName);
							$('#notifyId').html(list[0].notifyId);
							$('#oaNotifyType').text("邀请加入");
							$("#view").hide();
						}else{
							$('#username').html('');
							$('#oaNotifyType').html('');
							$('#prj').html(list[0].teamName1+"<i>已发布,欢迎到</i>"+'<a href="${ctx}/team/indexMyTeamList">团队建设</a>'+"<span>中加入我们</span>");
							$('#notifyId').html(list[0].notifyId);
							$("#teamId").html(list[0].teamId);
							$("#accept-btn").hide();
							$("#refuse-btn").hide();
							$("#view").show().text("查看详情");
						}
					}
				});
				//查看详情

				$("#view").click(function(){
					var s_id=$("#teamId").html();
					var send_id = $('#notifyId').html();
 					$.ajax({
						type : "post",
						data : {"send_id" : send_id},
						url : "${ctx}/closeButton",
						success : function(res) {
							if (res == 1) {
								window.location.href="${ctx}/team/findByTeamId?id="+s_id;
							}
						},
						error : function(err) {
							console.log(err)
							// do something....
						}
					});

				});
				//接受
				var lock = false;
				$('#accept-btn').click(function() {
					if(lock){
						return;
					}
					lock=true;
					var send_id = $('#notifyId').html();
					$.ajax({
						type : "post",
						data : {
							"send_id" : send_id
						},
						url : "${ctx}/acceptInviation",
						success : function(res) {
							if (res == 1) {
								window.location.reload()
							}else{
								alert("你已经加入其他团队！");
								window.location.reload()
							}
							lock = false;
						},
						error : function(err) {
							console.log(err)
							lock = false;
							// do something....
						}
					});
				})
				//拒绝
				$('#refuse-btn').click(function() {
					if(lock){
						return;
					}
					lock=true;
					var send_id = $('#notifyId').html();
					$.ajax({
						type : "post",
						data : {
							"send_id" : send_id
						},
						url : "${ctx}/refuseInviation",
						success : function(res) {
							if (res == 1) {
								window.location.reload()
							}
							lock = false;
						},
						error : function(err) {
							console.log(err)
							// do something....
							lock = false;
						}
					});
				})

				//上一条点击按钮
				$('#prevItem').click(function() {
					var notifyId = $('#notifyId').html();
					prevItem(list, notifyId);
				});

				//下一条点击按钮
				$('#nextItem').click(function() {
					var notifyId = $('#notifyId').html();
					nextItem(list, notifyId);
				})

				/**
				 * [nextItem 下一条]
				 */
				function nextItem(list, notifyId) {
					var index = 0;
					list.forEach(function(item, i) {
						if (item.notifyId == notifyId) {
							index = i;
							return index;
						}
					});

					list.forEach(function(it, i) {
								if (index >= list.length - 1) {
									var type=list[list.length - 1].type;
									switch(type){
										case "5" :
											$('#username').html(
													list[list.length - 1].sentName);
											$('#prj').html(
													list[list.length - 1].teamName);
											$('#notifyId').html(
													list[list.length - 1].notifyId);
											$('#oaNotifyType').text('申请加入');
											$("#accept-btn").show();
											$("#refuse-btn").show();
											$("#view").hide();
											break;
										case "6" :
											$('#username').html(
													list[list.length - 1].sentName);
											$('#prj').html(
													list[list.length - 1].teamName);
											$('#notifyId').html(
													list[list.length - 1].notifyId);
											$('#oaNotifyType').text('邀请加入');
											$("#accept-btn").show();
											$("#refuse-btn").show();
											$("#view").hide();
											break;
										default:
											$('#username').html('');
											$('#oaNotifyType').html('');
											$('#prj').html(list[list.length - 1].teamName+"<span>已发布,欢迎到</span>"+'<a href="${ctx}/team/indexMyTeamList">团队建设</a>'+"<span>中加入我们</span>");
											$('#notifyId').html(list[list.length - 1].notifyId);
											$("#teamId").html(list[list.length - 1].teamId);
											$("#accept-btn").hide();
											$("#refuse-btn").hide();
											$("#view").show().text("查看详情");
											break;
									}
								} else {
									var type=list[index+1].type;
									switch(type){
										case "5" :
											$('#username').html(
													list[index+1].sentName);
											$('#prj').html(
													list[index+1].teamName);
											$('#notifyId').html(
													list[index+1].notifyId);
											$('#oaNotifyType').text('申请加入');
											$("#accept-btn").show();
											$("#refuse-btn").show();
											$("#view").hide();
											break;
										case "6" :
											$('#username').html(
													list[index+1].sentName);
											$('#prj').html(
													list[index+1].teamName);
											$('#notifyId').html(
													list[index+1].notifyId);
											$('#oaNotifyType').text('邀请加入');
											$("#accept-btn").show();
											$("#refuse-btn").show();
											$("#view").hide();
											break;
										default:
											$('#username').html('');
											$('#oaNotifyType').html('');
											$('#prj').html(list[index+1].teamName+"<span>已发布,欢迎到</span>"+'<a href="${ctx}/team/indexMyTeamList">团队建设</a>'+"<span>中加入我们</span>");
											$('#notifyId').html(list[index+1].notifyId);
											$("#teamId").html(list[index+1].teamId);
											$("#accept-btn").hide();
											$("#refuse-btn").hide();
											$("#view").show().text("查看详情");
											break;

								}
							}
					})
				}
				/**
				 * [prevItem  上一条]
				 */
				function prevItem(list, notifyId) {
					var index = 0;
					list.forEach(function(item, i) {
						if (item.notifyId == notifyId) {
							console.log(index);
							index = i;
							return index;
						}
					});

					list.forEach(function(it, i) {
						if (index > 0 && index < list.length) {
							var type=list[index - 1].type;
							switch(type)
							{
								case "5" :
									$('#username').html(
											list[index-1].sentName);
									$('#prj').html(
											list[index-1].teamName);
									$('#notifyId').html(
											list[index-1].notifyId);
									$('#oaNotifyType').text('申请加入');
									$("#accept-btn").show();
									$("#refuse-btn").show();
									$("#view").hide();
									break;
								case "6" :
									$('#username').html(
											list[index-1].sentName);
									$('#prj').html(
											list[index-1].teamName);
									$('#notifyId').html(
											list[index-1].notifyId);
									$('#oaNotifyType').text('邀请加入');
									$("#accept-btn").show();
									$("#refuse-btn").show();
									$("#view").hide();
									break;
								default:
									$('#username').html('');
									$('#oaNotifyType').html('');
									$('#prj').html(list[index-1].teamName+"<span>已发布,欢迎到</span>"+'<a href="${ctx}/team/indexMyTeamList">团队建设</a>'+"<span>中加入我们</span>");
									$('#notifyId').html(list[index-1].notifyId);
									$("#teamId").html(list[index-1].teamId);
									$("#accept-btn").hide();
									$("#refuse-btn").hide();
									$("#view").show().text("查看详情");
									break;
							}
						} else {
							var type=list[0].type;
							switch(type)
							{
								case "5" :
									$('#username').html(
											list[0].sentName);
									$('#prj').html(
											list[0].teamName);
									$('#notifyId').html(
											list[0].notifyId);
									$('#oaNotifyType').text('申请加入');
									$("#accept-btn").show();
									$("#refuse-btn").show();
									$("#view").hide();
									break;
								case "6" :
									$('#username').html(
											list[0].sentName);
									$('#prj').html(
											list[0].teamName);
									$('#notifyId').html(
											list[0].notifyId);
									$('#oaNotifyType').text('邀请加入');
									$("#accept-btn").show();
									$("#refuse-btn").show();
									$("#view").hide();
									break;
								default:
									$('#prj').html(list[0].teamName+"<span>已发布,欢迎到</span>"+'<a href="${ctx}/team/indexMyTeamList">团队建设</a>'+"<span>中加入我们</span>");
									$('#notifyId').html(list[0].notifyId);
									$("#teamId").html(list[0].teamId);
									$("#accept-btn").hide();
									$("#refuse-btn").hide();
									$("#view").show().text("查看详情");
									break;

							}
						}
					})
				}

				//关闭窗口
				$('#closeBtn').click(function() {
					var send_id = $('#notifyId').html();
					$.ajax({
						type : "post",
						data : {"send_id" : send_id},
						url : "${ctx}/closeButton",
						success : function(res) {
							if (res == 1) {
								window.location.reload()
							}
						},
						error : function(err) {
							console.log(err)
							// do something....
						}
					});
				});
			}
		});
	</script>


</body>
</html>