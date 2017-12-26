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
        function backPage() {
            window.location.href = "/f/team/indexMyTeamList";
        }
    </script>
    <style>
        .table-nowrap > thead > tr > th {
            white-space: nowrap;
        }

        .table-project .pro-info {
            width: 350px;

        }
        .table-project .pro-info > h5{
            margin-top: 0;
            line-height: 20px;
        }
        .table-project .pro-info > h5 > a {
            display: inline-block;
            max-width: 290px;
            color: #222c38;
            letter-spacing: 0;
            font-weight: bold;
            text-decoration: none;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
            vertical-align: middle;
        }

        .table-project .pro-info > p {
            margin-bottom: 0;
            height: 60px;
            overflow: hidden;
        }

        .teacher-leaders > a {
            display: block;
            color: #333;
            text-decoration: none;
        }

        .teacher-leaders > a:hover {
            color: #e9442d;
        }

        .table-center > tbody > tr > td {
            text-align: center;
        }

        .table-center > thead > tr > th {
            text-align: center;
        }

        .table-center > tbody > tr > td:first-child {
            text-align: left;
        }

        .students-member {
            display: inline-block;
            vertical-align: middle;
        }

        .students-member > p {
            margin-bottom: 0;
        }

        .students-member a {
            display: inline-block;
            width: 160px;
            color: #333;
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
            text-decoration: none;
            text-align: center;
        }
        .students-member a+a{
            margin-left: 10px;
        }

        .students-member a:hover {
            color: #e9442d;
        }

        .table-middle > tbody > tr > td {
            vertical-align: middle;
        }

        .table-project > thead > tr > th {
            border-bottom: 1px solid #ddd;
        }

        .table-project > tbody > tr > td {
            border-bottom: 1px solid #ddd;
        }

        .table-project .project-status{
            display: inline-block;
            padding: 0 6px;
            margin-left: 4px;
            border-radius: 3px;
            color: #fff;
            background-color: #e9442d;
            vertical-align: middle;
        }
        .table-project .project-status.completed{
            color: #333;
            background-color: #f4e6d4;
        }

    </style>
</head>
<body>
<div class="container container-fluid-oe">
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
                    <span>项目</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="pro-container">
                <table class="table table-project table-nowrap table-center table-middle ">
                    <thead>
                    <tr style="background-color: #f4e6d4">
                        <th>项目</th>
                        <th>指导老师</th>
                        <th>成员/(学号)</th>
                        <th>负责人</th>
                        <th>评级</th>
                        <th>结果</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>
                            <div class="pro-info">
                                <h5><a href="#">北极光创投</a><span class="project-status">进行中</span></h5>
                                <p>北极光创投创立于2005年，）是一家中国概念的风险投资公司，专注于早期和成长期技术驱动型的商业机会。管理4支美...</p>
                            </div>
                        </td>
                        <td>
                            <div class="teacher-leaders">
                                <a href="#">王清腾</a>
                            </div>
                        </td>
                        <td>
                            <div class="students-member">
                                <p><a href="#">王清腾（56465465）</a><a href="#">王清腾（56465465）</a></p>
                                <p><a href="#">王清腾（56465465）</a><a href="#">王清腾（56465465）</a></p>
                                <p><a href="#">王清腾（56465465）</a><a href="#">王清腾（56465465）</a></p>
                                <%--<p> <a href="#">王清腾（56465465）</a><a href="#">王清腾（56465465）</a></p>--%>
                            </div>
                        </td>
                        <td>
                            王清腾
                        </td>
                        <td>
                            优秀
                        </td>
                        <td>
                            A+
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="pro-info">
                                <h5><a href="#">北极光创投</a><span class="project-status completed">已完成</span></h5>
                                <p>北极光创投创立于2005年，）是一家中国概念的风险投资公司，专注于早期和成长期技术驱动型的商业机会。管理4支美...</p>
                            </div>
                        </td>
                        <td>
                            <div class="teacher-leaders">
                                <a href="#">王清腾</a>
                            </div>
                        </td>
                        <td>
                            <div class="students-member">
                                <p><a href="#">王清腾（56465465）</a><a href="#">王清腾（56465465）</a></p>
                                <p><a href="#">王清腾（56465465）</a><a href="#">王清腾（56465465）</a></p>
                                <p><a href="#">王清腾（56465465）</a><a href="#">王清腾（56465465）</a></p>
                                <%--<p> <a href="#">王清腾（56465465）</a><a href="#">王清腾（56465465）</a></p>--%>
                            </div>
                        </td>
                        <td>
                            王清腾
                        </td>
                        <td>
                            优秀
                        </td>
                        <td>
                            A+
                        </td>
                    </tr>
                    </tbody>

                </table>
            </div>
            <div class="edit-bar edit-bar-sm clearfix">
                <div class="edit-bar-left">
                    <span>项目</span>
                    <i class="line"></i>
                </div>
            </div>
            <div class="pro-container">

            </div>
        </div>
        <div class="back-box">
            <button type="button" class="btn btn-back-w" onclick="backPage()">返回</button>
        </div>
    </div>
</div>

</body>
</html>
