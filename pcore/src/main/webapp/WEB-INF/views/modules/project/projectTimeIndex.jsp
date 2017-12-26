<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta charset="utf-8">
    <title>${frontTitle}</title>
    <meta name="decorator" content="site-decorator"/>
    <script type="text/javascript" src="/js/actYwDesign/vue.min.js"></script>
    <script type="text/javascript" src="/js/actYwDesign/vue-router.js"></script>
    <style>
        .time-line {
            position: relative;
            width: 600px;
            margin: 40px auto;
        }

        .time-line::before {
            content: '';
            position: absolute;
            top: 0;
            left: 50%;
            height: 100%;
            width: 2px;
            background: #ccc;
            margin-left: 3px;
        }

        .time-line .time-item {
            position: relative;
            width: 124px;
            height: 150px;
            margin: 0 auto 35px;
            z-index: 100;
        }

        .time-line .time-item-bg {
            position: absolute;
            left: 0;
            right: 0;
            top: 0;
            bottom: 0;
            background: url('/images/half_circle.png') no-repeat left top;
            z-index: -1;
        }

        .time-line .time-item.last .time-inner {
            top: 26px;
        }

        .time-line .time-item.active .time-item-bg {
            background-image: url('/images/hover_half_circle.png');
        }

        .time-line .time-item.last .time-item-bg {
            transform: rotateX(180deg);
        }

        .time-line .time-inner {
            position: relative;
            width: 124px;
            height: 124px;
            border-radius: 62px;
            background-color: #c3c3c3;
            transition: width .15s linear;
            overflow: hidden;
        }

        .time-line .time-item.active .time-inner {
            background-color: #e9422f;
        }

        .time-line .time-inner .time-inner-header {
            float: left;
            width: 124px;
        }

        .time-line .time-inner .time-inner-header .cell {
            display: table-cell;
            height: 124px;
            width: 1%;
            text-align: center;
            font-size: 12px;
            color: #fff;
            vertical-align: middle;
        }

        .time-line .time-inner .time-inner-content {
            margin-left: 124px;
            height: 124px;
        }

        .time-line .time-item.active.hasContent:hover .time-inner {
            width: 300px;
        }

        .time-line .time-item-intro {
            position: relative;
        }

        .time-line .intro-dot {
            position: absolute;
            left: 50%;
            top: 3px;
            width: 12px;
            height: 12px;
            margin-left: -2px;
            border: 1px solid #fcc177;
            border-radius: 50%;
        }

        .time-line .intro-dot > span {
            display: block;
            width: 6px;
            height: 6px;
            background-color: #e9422f;
            margin: 2px auto 0;
            border-radius: 50%;
        }

        .time-line .intro-date {
            text-align: right;

        }

        .time-line .intro-inner {
            /*float: right;*/
            width: 280px;
            border: 1px solid #e9422d;
            border-radius: 3px;
            margin-top: -4px;
        }

        .time-line .right .intro-inner {
            float: right;
        }

        .time-line .left .intro-inner {
            float: left;
        }

        .time-line .left .intro-date {
            width: 400px;
        }

        .time-line .right .intro-date {
            width: 290px;
        }

        .time-line .intro-header {
            position: relative;
            padding: 3px 8px;
            background-color: #e9422d;
            color: #fff;
        }

        .time-line .intro-content {
            padding: 3px 8px;
            font-size: 12px;
            line-height: 1.42857143;
            overflow: hidden;
        }

        .time-line .intro-inner .title {
            margin: 0;
            line-height: 20px;
            height: auto;
        }

        .time-line .intro-inner .arrow {
            position: absolute;
            left: -7px;
            top: 6px;
            border-top: 6px solid transparent;
            border-bottom: 6px solid transparent;
            border-right: 6px solid #e9422d;
        }

        .time-line-box {
            position: relative;
            padding-bottom: 30px;
            z-index: 100;
        }

        .time-line-box:last-child {
            padding-bottom: 0;
        }

        .time-line .time-line-box-arrow {
            position: absolute;
            left: 50%;
            bottom: 100%;
            margin-left: -4px;
            border-right: 8px solid transparent;
            border-left: 8px solid transparent;
            border-top: 12px solid #ccc;
        }

        .time-line-box.completed .time-line-box-arrow {
            border-top: 12px solid #e9422d;
        }

        .time-line-box.completed:before {
            content: '';
            position: absolute;
            top: 0;
            left: 50%;
            height: 100%;
            width: 2px;
            background: #e9422d;
            margin-left: 3px;
        }

        .time-line-box.completed.gray:before {
            display: none;
        }

        .btn-milestone {
            text-decoration: none;
            color: #fff;
            background: -webkit-linear-gradient(#fd9a69, #fb4f2e);
            background: -o-linear-gradient(#fd9a69, #fb4f2e);
            background: -moz-linear-gradient(#fd9a69, #fb4f2e);
            background: linear-gradient(#fd9a69, #fb4f2e);
            border-radius: 5px;
        }

        .btn-milestone:hover, .btn-milestone:active {
            color: #fff;
            background: -webkit-linear-gradient(#fd9a69, #fb4f2e);
            background: -o-linear-gradient(#fd9a69, #fb4f2e);
            background: -moz-linear-gradient(#fd9a69, #fb4f2e);
            background: linear-gradient(#fd9a69, #fb4f2e);
        }

        .time-line .time-inner .time-inner-content .cell {
            display: table-cell;
            width: 1%;
            height: 124px;
            text-align: center;
            vertical-align: middle;
        }

        .time-line .time-inner .time-inner-content .approval {
            color: #fff;
        }

        .time-line-container {
            margin-bottom: 40px;
            /*border: 1px solid #ececec;*/
        }

        .time-line-title {
            margin: 0;
            line-height: 50px;
            text-align: center;
        }

        .project-info > span {
            font-size: 12px;
            margin-right: 10px;
            line-height: 30px;
        }

        .time-line-header {
            padding: 6px 10px;
            background-color: #f4e6d4;
        }

        .btn-primary {
            color: #fff;
            background-color: #e9432d;
            border-color: #e53018;
        }

        .btn-primary:focus,
        .btn-primary.focus {
            color: #fff;
            background-color: #cd2b16;
            border-color: #71180c;
        }

        .btn-primary:hover {
            color: #fff;
            background-color: #cd2b16;
            border-color: #ad2412;
        }

        .btn-primary.active.focus, .btn-primary.active:focus, .btn-primary.active:hover, .btn-primary:active.focus, .btn-primary:active:focus, .btn-primary:active:hover, .open > .dropdown-toggle.btn-primary.focus, .open > .dropdown-toggle.btn-primary:focus, .open > .dropdown-toggle.btn-primary:hover {
            color: #fff;
            background-color: #cd2b16;
            border-color: #ad2412;
        }

        .btn-pro-list {
            color: #4b4b4b;
            background-color: #bebebe;
        }

        .btn-pro-list:hover, .btn-pro-list:focus {
            color: #4b4b4b;
            background-color: #bebebe;
        }

        .time-actions {
            margin: 40px 0 20px;
        }

        .categories-column {
            margin-bottom: 20px;
        }

        .categories-column .categories span {
            display: inline-block;
            width: 93px;
            padding: 0 4px;
            margin-bottom: 8px;
            height: 20px;
            line-height: 20px;
            white-space: nowrap;
            overflow: hidden;
            vertical-align: top;
            text-overflow: ellipsis;
        }

        .categories-column .category-label {
            width: 75px;
            line-height: 20px;
            float: left;
        }

        .categories-column .categories {
            margin-left: 75px;
        }

        .categories-column .categories a {
            color: #333;
            text-decoration: none;
        }

        .categories-column .categories a.router-link-active {
            color: #e9422f;
        }

        .no-time-line, .loading-data {
            margin: 60px auto;
            text-align: center;
        }

        .time-sidebar {
            float: left;
            width: 48px;
        }

        .time-content {
            margin-left: 48px;
            border: solid #ddd;
            border-width: 0 1px 1px;
            /*padding-left: 10px;*/
            /*border-left: 1px dashed #ccc;*/
        }

        .time-name-nav {
            margin: 49px 0 0;
            padding: 0;
            list-style: none;
        }

        .time-name-nav > li {
            padding: 10px 8px;
            margin-bottom: 10px;
            border-radius: 5px 0 0 5px;
            border: solid #ddd;
            border-width: 1px 0 1px 1px;
        }

        .time-name-nav > li > a {
            display: block;
            line-height: 20px;
            color: #333;
            text-decoration: none;
            text-align: center;
            height: 100px;
            overflow: hidden;
        }

        .time-name-nav > li > a > span {
            display: block;
            height: 100px;
            overflow: hidden;
            word-wrap: break-word;
        }

        .time-name-nav > li:hover {
            background-color: #e9422f;
            border-bottom: 1px solid #fff;
        }

        .time-name-nav > li:hover a{
            color: #fff;
        }
        .time-name-nav > li.router-link-active{
            background-color: #e9422f;
            border-bottom: 1px solid #fff;
        }
        .time-name-nav > li.router-link-active a{
            color: #fff;
        }

        .time-line-header {
            height: 42px;
        }

        .nav-tabs > li.router-link-active > a, .nav-tabs > li.router-link-active > a:focus, .nav-tabs > li.router-link-active > a:hover {
            color: #555;
            cursor: default;
            background-color: #fff;
            border: 1px solid #ddd;
            border-bottom-color: transparent;
        }

        .nav-wrap {
            margin-left: 48px;
        }

        .time-item-intro {
            margin-bottom: 20px;
        }

        .time-line .left .time-double .intro-inner {
            float: right;
        }

        .time-line .left .time-double .intro-date {
            width: 290px;
        }

        .time-line .left .intro-inner .arrow {
            left: auto;
            right: -7px;
            top: 6px;
            border-left: 6px solid #e9422d;
            border-right: 0;
        }

        .time-line .weekly-content {
            position: relative;
        }

        .time-line .weekly-content:after {
            content: '';
            position: absolute;
            top: 50%;
        }

        .time-line .right .intro-weekly {
            float: right;
        }

        .time-line .right .weekly-content {
            margin-left: 10px;
            width: 280px;
        }

        .weekly-list {
            margin: 0;
        }

        .time-line .weekly-list > li {
            padding: 3px 0;
            line-height: 24px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .time-line .right .weekly-list {
            text-align: right;
        }

        .time-line .right .weekly-content:after {
            border-top: 1px dashed #ccc;
            left: 0;
            right: 140px;
        }

        .time-line .right .weekly-list {
            margin-left: 150px;
        }

        .time-line .double .intro-inner {
            float: right;
        }

        .time-line .double .intro-weekly {
            float: left;
        }

        .time-line .double .intro-date {
            width: 290px;
        }

        .time-line .double .weekly-content:after {
            border-top: 1px dashed #ccc;
            left: 100px;
            right: 0;
        }

        .time-line .double .weekly-content {
            width: 205px;
        }

        .time-line .left .intro-weekly {
            float: left;
        }

        .time-line .intro-weekly {
            margin-top: 8px;
        }

        .time-line .left .weekly-content:after {
            border-top: 1px dashed #ccc;
            left: 140px;
            right: 0;
        }

        .time-line .left .weekly-list {
            padding-right: 140px;
        }

        .time-line .left .weekly-content {
            width: 280px;
        }

    </style>
</head>
<body>
<input type="hidden" id="userType" value="${user.userType}">
<div id="appTime">
    <div class="container" style="width: 1270px;padding-top: 60px;">
        <div class="nav-wrap">
            <ul class="nav nav-tabs nav-tab-timeline">
                <router-link v-for="tab in tabList" tag="li"
                             :to="{path: tab.id, query: {type: tab.type, actYwId: tab.actywId}}" :key="tab.id">
                    <a>{{tab.label}}</a>
                </router-link>
            </ul>
        </div>
        <router-view></router-view>
    </div>
</div>

<script type="text/x-template" id="projectTimeLine">
    <div class="time-line-container">
        <div class="time-sidebar">
            <ul class="time-name-nav">
                <li :class="{active: $route.query.projectId === sTab.id}" v-for="sTab in secondTabs">
                    <router-link :title="sTab.name"
                            :to="{path: $route.query.id, query: {type: $route.query.type, actYwId: $route.query.actYwId, projectId: sTab.id}}"
                            :key="sTab.projectId">
                        {{sTab.name}}
                    </router-link>
                </li>
            </ul>
        </div>
        <div class="time-content">
            <h4 style="display: none" class="time-line-title">{{proname}}</h4>
            <div class="time-line-header clearfix">
                <%--{{$route}}--%>
                <div class="pull-right">
                    <a class="btn btn-primary btn-sm" href="/f/project/projectDeclare/curProject">当前项目</a>
                    <a class="btn btn-pro-list btn-sm" href="/f/project/projectDeclare/list">项目列表</a>
                </div>
                <div class="project-info">
                    <span>项目申报时间：{{apply_time}}</span><span>项目编号：{{number}}</span><span>项目负责人：{{leader}}</span>
                </div>
            </div>
            <div v-show="isLoaded" style="display: none">
                <div v-if="hasCorrectTime" class="time-actions text-center">
                    <a v-if="userType == 1 && $route.query.projectId" :href="weeklyLink"
                       class="btn btn-primary">新建周报</a>
                </div>
                <div class="time-line" v-if="hasCorrectTime">
                    <div class="time-line-box" v-for="(item, index) in groupData" :class="{completed: item.current}">
                        <template v-if="item.type=== 'milestone'">
                            <div class="time-item"
                                 :class="{active: item.current, hasContent: !!item.btn_option || item.approvalTime, last: (index === groupData.length - 1)}">
                                <div class="time-item-bg"></div>
                                <div class="time-inner clearfix">
                                    <div class="time-inner-header">
                                        <div class="cell">
                                            <span>{{item.start_date | year}}年</span><br>
                                            <span>{{item.start_date | month}}月{{item.start_date | day}}日至{{item.end_date | month}}月{{item.end_date | day}}日</span><br>
                                            <span>{{item.title}}</span>
                                        </div>
                                    </div>
                                    <div v-if="!!item.btn_option || item.approvalTime" class="time-inner-content">
                                        <div class="cell">
                                            <template v-if="!!item.btn_option">
                                                <a class="btn btn-milestone"
                                                   :href="item.btn_option.url">{{item.btn_option.name}}</a>
                                            </template>
                                            <template v-if="item.approvalTime">
                                                <span class="approval">{{item.approvalTime}}<br>{{item.approvalDesc}}</span>
                                            </template>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <template v-if="item.children" v-for="(item2, index2) in item.children">
                                <div class="time-item-intro clearfix"
                                     :style="{'height': item2.files && item2.files.length*30 + 'px'}"
                                     :class="{left: index2%2 == 1, right: index2%2 === 0, weekly: !item2.type}">
                                    <div v-if="!item2.type" class="intro-weekly">
                                        <div class="weekly-content">
                                            <ul v-if="item2.files" class="weekly-list weekly-list-more"
                                                :style="{'margin-top': -(15*item2.files.length)+'px'}">
                                                <li v-for="file in item2.files">
                                                    <a :title="file.name"
                                                       :href="file.url">{{file.name}}
                                                    </a>
                                                </li>
                                            </ul>
                                        </div>
                                    </div>
                                    <div v-if="item2.type === 'date'" class="intro-inner">
                                        <div class="intro-header">
                                            <span class="arrow"></span>
                                            <p class="title">{{item2.desc}}</p></div>
                                        <div class="intro-content">{{item2.intro}}</div>
                                    </div>
                                    <div class="intro-date">{{item2.start_date || item2.create_date | format}}</div>
                                    <div class="intro-dot">
                                        <span></span>
                                    </div>
                                </div>
                            </template>
                            <span v-show="index > 0" class="time-line-box-arrow"></span>
                        </template>
                    </div>
                </div>
            </div>
            <p v-show="!hasCorrectTime && isLoaded" class="text-center no-time-line">项目配置错误或者该项目不存在</p>
            <div class="loading-data" v-show="!isLoaded && hasProjectId">
                数据加载中...
            </div>
        </div>
        <div class="loading-data" v-show="!secondTabs.length && !isLoaded">
            没有项目
        </div>
        <%--<div class="loading-data" v-show="!isLoaded && hasProjectId">--%>
        <%--数据加载中...--%>
        <%--</div>--%>
    </div>
</script>

<script type="text/x-template"></script>
<script>
function canSumitClose(projectId, url) {
    $.ajax({
        type: "GET",
        url: "canSumitClose",
        data: "projectId=" + projectId,
        dataType: "text",
        success: function (data) {
            if (data == "false") {
                showModalMessage(0, "未创建学分配比，请先完成该信息", {
                    创建学分配比: function () {
                        top.location = "scoreConfig?projectId=" + projectId;
                    },
                    取消: function () {
                        $(this).dialog("close");
                    }
                });
            } else {
                top.location = url + "?projectId=" + projectId;
            }
        },
        error: function (msg) {
            showModalMessage(0, msg);
        }
    });
}
    var $userType = $('#userType');
    var userType = $userType.val();

    $('#appTime').css('minHeight', function () {
        return $(window).height() - 408;
    });


    function compareObj(time, dTime) {
        return function (obj1, obj2) {
            var date1 = obj1[time] || obj1[dTime];
            var date2 = obj2[time] || obj2[dTime];
            var time1 = new Date(date1).getTime();
            var time2 = new Date(date2).getTime();
            if (time1 < time2) {
                return -1;
            } else if (time1 > time2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    function compareObj2(time, dTime) {
        return function (obj1, obj2) {
            var date1 = obj1[0][time] || obj1[0][dTime];
            var date2 = obj2[0][time] || obj2[0][dTime];


            var time1 = new Date(date1).getTime();
            var time2 = new Date(date2).getTime();
            if (time1 < time2) {
                return -1;
            } else if (time1 > time2) {
                return 1;
            } else {
                return 0;
            }
        }
    }


    var TimeLine = Vue.component('time-line', {
        template: '#projectTimeLine',
        data: function () {
            return {
                timeList: [],
                proname: '',
                pid: '',
                number: '',
                apply_time: '',
                leader: '',
                hasCorrectTime: false,
                milestones: [],
                dates: [],
                secondTabs: [],
                groupData: [],
                hasProjectId: false,
                isLoaded: false,
                userType: userType
            }
        },
        computed: {
            projectId: function () {
                return this.$route.params.id;
            },
            weeklyLink: function () {
                var type = this.$route.query.type;
                var linkPart = type == 1 ? '/f/project/weekly/createWeekly?projectId=' : '/f/project/weekly/createWeeklyPlus?projectId=';
                return linkPart += this.pid
            }
        },
        filters: {
            format: function (time) {
                if (!time) {
                    return '';
                }
                var timeStr = time.toString();
                return timeStr.substring(0, 4) + '-' + timeStr.substring(5, 7) + '-' + timeStr.substring(8);
            },
            year: function (time) {
                if (!time) {
                    return '';
                }
                var timeStr = time.toString();
                return timeStr.substring(0, 4);
            },
            month: function (time) {
                if (!time) {
                    return '';
                }
                var timeStr = time.toString();
                return timeStr.substring(5, 7);
            },
            day: function (time) {
                if (!time) {
                    return '';
                }
                var timeStr = time.toString();
                return timeStr.substring(8);
            },
            fileExt: function (ext) {
                var extname;
                switch (ext) {
                    case "xls":
                    case "xlsx":
                        extname = "excel";
                        break;
                    case "doc":
                    case "docx":
                        extname = "word";
                        break;
                    case "ppt":
                    case "pptx":
                        extname = "ppt";
                        break;
                        // 我不太确定这个文件格式
//                    case "project":
                    case "jpg":
                    case "jpeg":
                    case "gif":
                    case "png":
                    case "bmp":
                        extname = "image";
                        break;
                    case "rar":
                    case "zip":
                    case "txt":
                    case "project":
                        // just break
                        break;
                    default:
                        extname = "unknow";
                }
                return "/img/filetype/" + extname + ".png"
            }
        },
        watch: {
            // 如果路由有变化，会再次执行该方法
            '$route': 'getTimeList'
        },
        methods: {
            resetBaseData: function () {
                this.timeList = [];
                this.proname = '';
                this.pid = '';
                this.number = '';
                this.apply_time = '';
                this.leader = '';
                this.hasCorrectTime = false;
                this.milestones = [];
                this.dates = [];
                this.groupData = [];
                this.isLoaded = false;
                this.hasProjectId = false;
            },

            getSecondTabs: function () {
                var postData = {
                    pptype: this.$route.query.type,
                    actywId: this.$route.query.actYwId
                };
                var self = this;
                var xhr = $.post('/f/project/projectDeclare/getTimeIndexSecondTabs', postData);
                xhr.success(function (data) {
                    if (data.length > 0) {
                        self.secondTabs = data;
                        self.$router.push({
                            path: '/' + self.$route.params.id,
                            query: {
                                type: self.$route.query.type,
                                actYwId: self.$route.query.actYwId,
                                projectId: data[0].id
                            }
                        })
                    } else {
                        self.secondTabs = [];
                        self.getNoProjectId();
                    }
                })
            },

            refreshSt: function () {
                var postData = {
                    pptype: this.$route.query.type,
                    actywId: this.$route.query.actYwId
                };
                var self = this;
                var xhr = $.post('/f/project/projectDeclare/getTimeIndexSecondTabs', postData);
                xhr.success(function (data) {
                    if (data) {
                        self.secondTabs = data;
                    }
                })
            },

            getNoProjectId: function () {
                this.resetBaseData();
                var postData = {
                    pptype: this.$route.query.type,
                    actywId: this.$route.query.actYwId,
                    ppid: this.projectId
                };
                var xhr = $.post('/f/project/projectDeclare/getTimeIndexData', postData);
                var self = this;
                var everyTime = false;
                var files;
                var filesDate = [];
                var typeDates = [];
                var sortFilesDates = [];
                this.hasProjectId = true;
                xhr.success(function (data) {
                    if (data) {
                        self.timeList = data.contents;
                        self.leader = data.leader;
                        self.number = data.number;
                        self.pid = data.pid;
                        self.apply_time = data.apply_time;
                        self.proname = data.proname;
                        self.hasCorrectTime = true;
                        everyTime = self.timeList.every(function (item) {
                            return item.start_date && item.end_date
                        });

                        self.milestones = self.timeList.filter(function (item) {
                            return item.type === 'milestone'
                        });
                        self.dates = self.timeList.filter(function (item) {
                            return item.type === 'date'
                        });
                        files = data.files || [];
                        typeDates = self.timeList.filter(function (item) {
                                    return item.type === 'date'
                                }) || [];
                        filesDate = files.concat(typeDates);


                        sortFilesDates = filesDate.sort(compareObj('start_date', 'create_date'));
                        self.milestones.forEach(function (t) {
                            var startTimes = new Date(t.start_date).getTime();
                            var endTimes = new Date(t.end_date).getTime();
                            t.children = [];
                            sortFilesDates.forEach(function (t2, i) {
                                var times = new Date(t2.start_date || t2.create_date).getTime();
                                if (times >= startTimes && times <= endTimes) {
                                    t.children.push(t2);
                                }
                            })
                        });
                        self.groupData = self.milestones;
                        self.$nextTick(function () {
                            var $completed = $('.time-line-box.completed');
                            $completed.eq(-1).addClass('gray');
                        });
                    }

                    self.isLoaded = true;
                });

                xhr.error(function (err) {
                    self.isLoaded = true;
                })
            },

            getTimeList: function () {
                if (!this.$route.query.projectId) {
                    this.resetBaseData();
                    this.getSecondTabs();
                    return
                }
                if (this.secondTabs.length < 1) {
                    this.refreshSt()
                }

                var postData = {
                    pptype: this.$route.query.type,
                    actywId: this.$route.query.actYwId,
                    ppid: this.projectId,
                    projectId: this.$route.query.projectId
                };

                var xhr = $.post('/f/project/projectDeclare/getTimeIndexData', postData);
                var self = this;
                var everyTime = false;
                var files;
                var filesDate = [];
                var typeDates = [];
                var sortFilesDates = [];
                this.hasProjectId = true;
                xhr.success(function (data) {
                    if (data) {
                        self.timeList = data.contents;
                        self.leader = data.leader;
                        self.number = data.number;
                        self.pid = data.pid;
                        self.apply_time = data.apply_time;
                        self.proname = data.proname;
                        self.hasCorrectTime = true;
                        everyTime = self.timeList.every(function (item) {
                            return item.start_date && item.end_date
                        });

                        self.milestones = self.timeList.filter(function (item) {
                            return item.type === 'milestone'
                        });
                        self.dates = self.timeList.filter(function (item) {
                            return item.type === 'date'
                        });
                        files = data.files || [];
                        typeDates = self.timeList.filter(function (item) {
                                    return item.type === 'date'
                                }) || [];
                        filesDate = files.concat(typeDates);


                        sortFilesDates = filesDate.sort(compareObj('start_date', 'create_date'));

//                        var sortRes = [sortFilesDates[0]];
//                        combinationArr = [[sortFilesDates[0]]];
//                        sortFilesDates.forEach(function (t2, i) {
//                            if (i > 0) {
//                                var curItem = sortRes[sortRes.length - 1];
//                                if (t2.type === 'date' || !t2.type) {
//                                    var time1 = t2.start_date || t2.create_date;
//                                    var time2 = curItem.start_date || curItem.create_date;
//                                    if (time1 != time2) {
////                                        t2.direction = count%2 === 0 ? 'right' : 'left';
//                                        sortRes.push(t2);
//                                        combinationArr.push([t2]);
//                                    } else {
////                                        count++;
//                                        if(t2.type == 'date'){
//                                            combinationArr.push([t2]);
//                                        }else {
//                                            combinationArr[combinationArr.length - 1].push(t2)
//                                        }
//                                    }
//                                } else {
//                                    sortRes.push(t2);
//                                    combinationArr.push([t2])
//                                }
//                            }
//                        });
                        self.milestones.forEach(function (t) {
                            var startTimes = new Date(t.start_date).getTime();
                            var endTimes = new Date(t.end_date).getTime();
                            t.children = [];
                            sortFilesDates.forEach(function (t2, i) {
                                var times = new Date(t2.start_date || t2.create_date).getTime();
                                if (times >= startTimes && times <= endTimes) {
                                    t.children.push(t2);
                                }
                            })
                        });
//                        combinationArr = combinationArr.concat(milestones);
//                        combinationArr = combinationArr.sort(compareObj2('start_date', 'create_date'));
                        self.groupData = self.milestones;
                        self.$nextTick(function () {
                            var $completed = $('.time-line-box.completed');
                            $completed.eq(-1).addClass('gray');
                        });
                    }

                    self.isLoaded = true;
                });

                xhr.error(function (err) {
                    self.isLoaded = true;
                })

            }
        },
        beforeMount: function () {
            this.getTimeList()
        },
        mounted: function () {
        }
    });

    var fTabList = '${pp}';
    fTabList = JSON.parse(fTabList);
    var firstId = fTabList[0].id;
    var firstActYwId = fTabList[0].actywId;
    var type = fTabList[0].type;
    var redirect = '/' + firstId;

    var router = new VueRouter({
        routes: [
            {
                path: '',
                redirect: redirect
            }, //默认指向第一个
            // 动态路径参数 以冒号开头
            {path: '/:id', name: 'timeLine', component: TimeLine}
        ]
    });


    var app = new Vue({
        router: router,
        data: function () {
            return {
                tabList: fTabList,
                collapsedText: '展开',
                collapsed: false
            }
        },
        computed: {
            isCollapse: function () {
                return this.tabList.length < 12;
            }
        },
        methods: {
            collapse: function () {
                this.collapsed = !this.collapsed;
                this.collapsedText = this.collapsed ? '收起' : '展开'
            }
        },
        beforeMount: function () {
            if (!this.$route.query.type) {
                this.$router.push({
                    path: '/' + this.$route.params.id,
                    query: {
                        type: type,
                        actYwId: firstActYwId
                    }
                })
            }
        }
    });

    app.$mount('#appTime');

</script>
</body>
</html>