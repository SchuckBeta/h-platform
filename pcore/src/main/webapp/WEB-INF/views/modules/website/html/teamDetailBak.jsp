<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="decorator" content="site-decorator"/>
    <title>${frontTitle}</title>
    <link rel="stylesheet" type="text/css" href="/css/team.css">
    <script type="text/javascript">
        function backPage(){
            window.location.href="/f/team/indexMyTeamList";
        }
    </script>
    <style>
        .his-pro-list{
            overflow: hidden;
        }
        .his-pro-list .pro-pic{
            float: left;
            width: 160px;
            height: 100px;
            overflow: hidden;
        }

        .his-pro-list .pro-intro{
            margin-left: 190px;
            height: 100px;
        }

        .his-pro-list .pro-item{
            padding: 10px;
            overflow: hidden;
        }
        .his-pro-list .pro-item+div{
            border-top: 1px dashed #eee;
        }
        .his-pro-list .pro-title{
            line-height: 1.42857143;
            font-size: 16px;
            margin-top: 0;
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
        }
        .his-pro-list .pro-title>a{
           color: #000;
        }
        .his-pro-list .pro-title>a:hover{
            color: #e9442d;
        }

        .his-pro-list  .pro-text{
            font-size: 12px;
            height: 67px;
            overflow : hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 4;
            -webkit-box-orient: vertical;
            margin-bottom: 0;
        }

        .his-pro-footer{
            text-align: center;
            line-height: 2;
            border-top: 1px dashed #eee;
        }
        .his-pro-footer>a{
            color: #e9442d;
        }
        .pro-card{
            margin-bottom: 20px;
            border: 1px solid #eee;
            overflow: hidden;
        }

        .pro-card .pro-card-header{
            padding: 5px 0;
            font-size: 16px;
            background-color: #f4e6d4;
            text-align: center;
            margin: 0;
        }
        .pro-card .pro-card-header>a{
            color: #000;
        }
        .pro-card .pro-card-header>a:hover{
            color: #e9442d;
        }
        .pro-card .pro-card-body{
            margin:10px 10px 0;
            padding-bottom: 10px;
            border-bottom: 1px dashed #eee;
            overflow: hidden;
        }
        .pro-card .pro-card-body .pro-pic{
            float: left;
            width: 160px;
            height: 100px;
            overflow: hidden;
        }
        .pro-card .pro-card-body .pro-intro{
            float: left;
            width: 240px;
            height: 100px;
            margin-left: 18px;
        }
        .pro-card .pro-card-body .pro-intro-item{
            margin-bottom: 6px;
            overflow: hidden;
        }
        .pro-card .pro-card-body .pro-intro-label{
            float: left;
            width: 80px;
            color: #666;
            text-align: right;
        }
        .pro-card .pro-card-footer{
            padding: 10px;
            overflow: hidden;
        }
        .pro-card  .pro-intro-text-box{
            float: left;
            width: 636px;
            margin: 0;
            overflow: hidden;
        }
        .pro-card  .pro-intro-text-box .pro-intro-text{
            margin-left: 80px;
            height: 100px;
            margin-bottom: 0;
            text-align: right;
            overflow : hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 5;
            -webkit-box-orient: vertical;
        }
        .pro-card  .pro-intro-text-box .pro-intro-text-label{
            float: left;
            width: 80px;
            color: #666;
        }

        .pro-time-line-container{
            position: relative;
            padding-top: 50px;
            height: 100px;
            margin: 0 auto;
            overflow: hidden;
        }
        .pro-time-line-container:before{
            content: '';
            position: absolute;
            left: 0;
            right: 0;
            height: 1px;
            background-color: #ddd;
        }
        .pro-time-line-container .pro-tl-item{
            position: relative;
            float: left;
            z-index: 100;
        }
        .pro-time-line-container .node-name{
            display: block;
            position: absolute;
            text-align: center;
            background-color: #ddd;
            font-size: 12px;
            white-space: nowrap;
            color: #333;
            text-overflow: ellipsis;
        }

        .pro-time-line-container .node-start,.pro-time-line-container .node-end{
            top: -25px;
            width: 50px;
            height: 50px;
            line-height: 50px;
            border-radius: 25px;
        }
        .pro-time-line-container .pro-tl-intro{
            padding-top: 30px;
            min-width: 100px;
            min-height: 1px;
        }
        .pro-time-line-container .node-name-custom{
            width: auto;
            padding: 5px 10px;
            top: -14px;
            margin-left: 0;
        }
        .pro-time-line-container .pro-tl-custom-item .pro-tl-intro{
            min-width: 130px;
        }
        .pro-time-line-container .node-name .arrow{
            position: absolute;
            right: 100%;
            top: 50%;
            transform: translateY(-50%);
            border-bottom: 4px solid transparent;
            border-top: 4px solid transparent;
            border-left: 6px solid #ddd;
        }


        .pro-time-line-container .pro-tl-item.completed .node-name{
            color: #fff;
            background-color: #e9442d;
        }
        .pro-time-line-container .pro-tl-item.completed .pro-tl-intro:before{
            content: '';
            position: absolute;
            left: 0;
            right: 0;
            top: 0;
            height: 1px;
            background-color: #e9442d;
            z-index: -1;
        }
        .pro-time-line-container .pro-tl-item.completed .arrow{
            border-left: 6px solid #e9442d;
        }
        .pro-time-line-container .pro-tl-item.completed+.proceed .arrow{
            border-left: 6px solid #e9442d;
        }
        .pro-time-line-container .pro-tl-item.completed+.proceed+div .arrow{
            border-left: 6px solid #f4e6d4;
        }

        .pro-time-line-container .pro-tl-item.proceed .node-name{
            background-color: #f4e6d4;
        }
        .pro-time-line-container .pro-tl-item.proceed .pro-tl-intro:before{
            content: '';
            position: absolute;
            left: 0;
            right: 0;
            top: 0;
            height: 1px;
            background-color: #f4e6d4;
            z-index: -1;
        }
        .pro-time-line-container .pro-tl-item:last-child .pro-tl-intro{
            min-width: 50px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="edit-bar clearfix">
        <div class="edit-bar-left">
            <span>基本信息</span>
            <i class="line"></i>
        </div>
    </div>
    <div class="team-info" role="main">
        <h4 class="title team-title">查看团队信息</h4>
        <div class="wrap">
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>团队信息</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-5">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>团队名称：</label>
                        <div class="ti-box">
                            <p>team1</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>所属学院：</label>
                        <div class="ti-box">
                            <p>其他</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-3">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>团队负责人：</label>
                        <div class="ti-box">
                            <p>张洲源</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>团队介绍：</label>
                        <div class="ti-box">
                            <p>2323</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>项目介绍：</label>
                        <div class="ti-box">
                            <p>11</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>团队成员要求</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-4">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>项目组人数：</label>
                        <div class="ti-box">
                            <p>2人</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>校内导师：</label>
                        <div class="ti-box">
                            <p>1人</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="team-info-group">
                        <label class="ti-label">企业导师：</label>
                        <div class="ti-box">
                            <p>0人</p>
                        </div>
                    </div>
                </div>
                <div class="col-sm-12">
                    <div class="team-info-group">
                        <label class="ti-label"><i class="icon-require">*</i>技术&nbsp;&nbsp;&nbsp;要求：</label>
                        <div class="ti-box">
                            <p>1</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>团队组建情况</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="team-table-title">
				<span class="team-status">
					已组建完成

				</span>
                <span class="team-name">学生团队</span>
            </div>
            <table class="table table-bordered table-team-type">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>姓名</th>
                    <th>工号</th>
                    <th>学院</th>
                    <th>专业</th>
                    <th>手机号</th>
                    <th>现状</th>
                    <th>技术领域</th>
                </tr>
                </thead>
                <tbody>

                <tr>
                    <td>1</td>
                    <td>张洲源</td>
                    <td>201494582083</td>
                    <td>其他</td>
                    <td>其他</td>
                    <td>15801861804</td>
                    <td>在校</td>
                    <td></td>
                </tr>

                <tr>
                    <td>2</td>
                    <td>程轩阳</td>
                    <td>201418187433</td>
                    <td>其他</td>
                    <td>其他</td>
                    <td>15830948621</td>
                    <td></td>
                    <td></td>
                </tr>

                </tbody>
            </table>
            <div class="team-table-title">
				<span class="team-status">
					已组建完成

				</span>
                <span class="team-name">指导教师</span>
            </div>
            <table class="table table-bordered table-team-type">
                <thead>
                <tr>
                    <th>序号</th>
                    <th>姓名</th>
                    <th>工号</th>
                    <th>单位(学院或企业、机构)</th>
                    <th>导师来源</th>
                    <th>技术领域</th>
                </tr>
                </thead>
                <tbody>

                <tr>
                    <td>1</td>
                    <td>郝北成</td>
                    <td>268316</td>
                    <td></td>
                    <td>校园导师</td>
                    <td></td>
                </tr>

                </tbody>
            </table>
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>进行中的项目</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="pro-container">
                <div class="pro-card">
                    <h4 class="pro-card-header"><a href="#">项目名称</a></h4>
                    <div class="pro-card-body">
                        <div class="pro-pic">
                            <a href="#">
                                <img class="img-responsive" src="/img/video-default.jpg">
                            </a>
                        </div>
                        <div class="pro-intro">
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">负责人：</div>
                                <div class="pro-intro-box">
                                    <a href=""><span>王清腾</span></a>
                                </div>
                            </div>
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">指导老师：</div>
                                <div class="pro-intro-box">
                                    <span>大创项目</span>
                                </div>
                            </div>
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">项目类别：</div>
                                <div class="pro-intro-box">
                                    <span>大创项目</span>
                                </div>
                            </div>
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">学院：</div>
                                <div class="pro-intro-box">
                                    <span>材料科学与工程学院</span>
                                </div>
                            </div>
                        </div>
                        <div class="pro-intro-text-box">
                            <div class="pro-intro-text-label">
                                项目介绍：
                            </div>
                            <p class="pro-intro-text">项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字</p>
                        </div>
                    </div>
                    <div class="pro-card-footer">
                        <div class="pro-time-line-container">
                            <div class="pro-tl-item completed">
                                <span class="node-name node-start"><span class="arrow"></span>开始</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item pro-tl-custom-item completed">
                                <span class="node-name node-name-custom"><span class="arrow"></span>立项审核</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item pro-tl-custom-item proceed">
                                <span class="node-name node-name-custom"><span class="arrow"></span>中期检查</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item pro-tl-custom-item">
                                <span class="node-name node-name-custom"><span class="arrow"></span>结项审核</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item">
                                <span class="node-name node-end"><span class="arrow"></span>结束</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>进行中的大赛</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="pro-container">
                <div class="pro-card">
                    <h4 class="pro-card-header"><a href="#">大赛名称</a></h4>
                    <div class="pro-card-body">
                        <div class="pro-pic">
                            <a href="#">
                                <img class="img-responsive" src="/img/video-default.jpg">
                            </a>
                        </div>
                        <div class="pro-intro">
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">负责人：</div>
                                <div class="pro-intro-box">
                                    <a href=""><span>王清腾</span></a>
                                </div>
                            </div>
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">指导老师：</div>
                                <div class="pro-intro-box">
                                    <span>大创项目</span>
                                </div>
                            </div>
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">项目类别：</div>
                                <div class="pro-intro-box">
                                    <span>大创项目</span>
                                </div>
                            </div>
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">学院：</div>
                                <div class="pro-intro-box">
                                    <span>材料科学与工程学院</span>
                                </div>
                            </div>
                        </div>
                        <div class="pro-intro-text-box">
                            <div class="pro-intro-text-label">
                                项目介绍：
                            </div>
                            <p class="pro-intro-text">项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字</p>
                        </div>
                    </div>
                    <div class="pro-card-footer">
                        <div class="pro-time-line-container">
                            <div class="pro-tl-item completed">
                                <span class="node-name node-start"><span class="arrow"></span>开始</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item pro-tl-custom-item completed">
                                <span class="node-name node-name-custom"><span class="arrow"></span>立项审核</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item pro-tl-custom-item proceed">
                                <span class="node-name node-name-custom"><span class="arrow"></span>中期检查</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item pro-tl-custom-item">
                                <span class="node-name node-name-custom"><span class="arrow"></span>结项审核</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item">
                                <span class="node-name node-end"><span class="arrow"></span>结束</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="pro-card">
                    <h4 class="pro-card-header"><a href="#">大赛名称</a></h4>
                    <div class="pro-card-body">
                        <div class="pro-pic">
                            <a href="#">
                                <img class="img-responsive" src="/img/video-default.jpg">
                            </a>
                        </div>
                        <div class="pro-intro">
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">负责人：</div>
                                <div class="pro-intro-box">
                                    <a href=""><span>王清腾</span></a>
                                </div>
                            </div>
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">指导老师：</div>
                                <div class="pro-intro-box">
                                    <span>大创项目</span>
                                </div>
                            </div>
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">项目类别：</div>
                                <div class="pro-intro-box">
                                    <span>大创项目</span>
                                </div>
                            </div>
                            <div class="pro-intro-item">
                                <div class="pro-intro-label">学院：</div>
                                <div class="pro-intro-box">
                                    <span>材料科学与工程学院</span>
                                </div>
                            </div>
                        </div>
                        <div class="pro-intro-text-box">
                            <div class="pro-intro-text-label">
                                项目介绍：
                            </div>
                            <p class="pro-intro-text">项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字</p>
                        </div>
                    </div>
                    <div class="pro-card-footer">
                        <div class="pro-time-line-container">
                            <div class="pro-tl-item completed">
                                <span class="node-name node-start"><span class="arrow"></span>开始</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item pro-tl-custom-item completed">
                                <span class="node-name node-name-custom"><span class="arrow"></span>立项审核</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item pro-tl-custom-item proceed">
                                <span class="node-name node-name-custom"><span class="arrow"></span>中期检查</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item pro-tl-custom-item">
                                <span class="node-name node-name-custom"><span class="arrow"></span>结项审核</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                            <div class="pro-tl-item">
                                <span class="node-name node-end"><span class="arrow"></span>结束</span>
                                <div class="pro-tl-intro">

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>已完成的项目</span>
                    <i class="line"></i>
                </div>
            </div>

            <div class="his-pro-container">
                <div class="his-pro-list">
                    <div class="pro-item">
                        <div class="pro-pic">
                            <a href=""><!-- 链接到项目详情 -->
                                <img class="img-responsive" src="/img/video-default.jpg">
                            </a>
                        </div>
                        <div class="pro-intro">
                            <h4 class="pro-title"><a href="#">222</a></h4>
                            <p class="pro-text">项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字项目介绍文字</p>
                        </div>
                    </div>
                    <div class="pro-item">
                        <div class="pro-pic">
                            <a href=""><!-- 链接到项目详情 -->
                                <img class="img-responsive" src="/img/video-default.jpg">
                            </a>
                        </div>
                        <div class="pro-intro">
                            <h4 class="pro-title"><a href="#">项目名称</a></h4>
                            <p class="pro-text">王清腾</p>
                        </div>
                    </div>
                </div>
                <div class="his-pro-footer">
                    <a href="#">查看更多</a>
                </div>
            </div>

            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>已完成的大赛</span>
                    <i class="line"></i>
                </div>
            </div>

            <div class="his-pro-container">
                <div class="his-pro-list">
                    <div class="pro-item">
                        <div class="pro-pic">
                            <a href=""><!-- 链接到项目详情 -->
                                <img class="img-responsive" src="/img/video-default.jpg">
                            </a>
                        </div>
                        <div class="pro-intro">
                            <h4 class="pro-title"><a href="#">我的大赛</a></h4>
                            <p class="pro-text">我的大赛</p>
                        </div>
                    </div>
                    <div class="pro-item">
                        <div class="pro-pic">
                            <a href=""><!-- 链接到项目详情 -->
                                <img class="img-responsive" src="/img/video-default.jpg">
                            </a>
                        </div>
                        <div class="pro-intro">
                            <h4 class="pro-title"><a href="#">我的大赛</a></h4>
                            <p class="pro-text">王清腾</p>
                        </div>
                    </div>
                </div>
                <div class="his-pro-footer">
                    <a href="#">查看更多</a>
                </div>
            </div>

        </div>
        <div class="back-box">
            <button type="button" class="btn btn-back-w" onclick="backPage()">返回</button>
        </div>
    </div>
</div>
<script>
    $(function () {
        $('.pro-time-line-container').each(function (i,item) {
            var $proTlIntro = $(item).find('.pro-tl-intro');
            var width = 0;
            $proTlIntro.each(function (i,intro) {
                width += $(intro).width();
            });
            $(item).css('width',width )
        })
    })
</script>
</body>
</html>
