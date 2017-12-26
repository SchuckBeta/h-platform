<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>双创大赛分析</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css">
    <script src="/static/jquery/jquery-1.8.3.min.js"></script>
    <script src="/js/echarts.min.js"></script>
    <%--<script src="/js/scdasai.js"></script>--%>
    <script src="/js/echart/theme.default.js"></script>
    <style type="text/css">
        .mybreadcrumbs {
            margin: 20px 1.5em;
            margin-left: 27px;
            border-bottom: 3px solid #f4e6d4;
        }

        .mybreadcrumbs span {
            position: relative;
            top: 9px;
            font-size: 16px;
            font-weight: bold;
            color: #e9432d;
            display: inline-block;
            background-color: #FFF;
            padding-right: 10px;
        }

        .view-content {
            padding: 0 27px;
        }

        .container-fluid {
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        .title-bar {
            line-height: 3;
            font-family: "Microsoft YaHei";
        }

        .mt-inline {
            display: inline-block;
        }

        .mt-inline:first-child {
            margin-right: 30px;
        }
    </style>
</head>
<body style="padding-bottom: 60px;">
<div class="mybreadcrumbs"><span>双创大赛分析</span></div>
<div class="view-content">
    <div class="container-fluid">
        <div class="row title-bar">
            <div class="col-md-12">双创大赛申报年增长趋势分析</div>
        </div>
        <div class="row" style="border: solid #ccc;border-width: 1px 0">
            <div class="col-md-12">
                <div id="tendencyChart" style="width:100%; height:450px;"></div>
            </div>
        </div>
        <div class="row title-bar">
            <div class="col-md-6">
                大赛统计分析
            </div>
            <div class="col-md-6 text-right">
                <div class="mt-inline">
                    选择统计的大赛:
                    <select id="matchTypes">
                        <option value="">全部</option>
                        <option value="2017">创青春</option>
                        <option value="2016">挑战杯</option>
                        <option value="2015">蓝桥杯</option>
                        <option value="2014">新大赛</option>
                    </select>
                </div>
                <div class="mt-inline">
                    统计年份：
                    <select id="seminaryMatchYear">
                        <option value="">全部</option>
                        <option value="2017">2017年</option>
                        <option value="2016">2016年</option>
                        <option value="2015">2015年</option>
                        <option value="2014">2014年</option>
                    </select>
                </div>
            </div>
        </div>
        <div class="row" style="border: solid #ccc;border-width: 1px 0">
            <div class="col-md-6">
                <div id="seminaryMatch" style="width: 100%;height: 500px;"></div>
            </div>
            <div class="col-md-6">
                <div id="seminaryMatch2" style="width: 100%;height: 500px;"></div>
            </div>
        </div>
        <div class="row title-bar">
            <div class="col-md-6">
                双创学生参赛统计
            </div>
            <div class="col-md-6 text-right">
                统计年份：
                <select id="matchYear">
                    <option value="">全部</option>
                    <option value="2017">2017年</option>
                    <option value="2016">2016年</option>
                    <option value="2015">2015年</option>
                    <option value="2014">2014年</option>
                </select>
            </div>
        </div>
        <div class="row" style="border-top: 1px solid #ccc;">
            <div class="col-md-12">
                <div id="stuChartBar" style="width:100%; height:500px;"></div>
            </div>
        </div>
    </div>
</div>
<script>


    $(function () {
        var tendencyChartEle = document.getElementById('tendencyChart');
        var tendencyChart = echarts.init(tendencyChartEle, 'macarons');

        tendencyChart.setOption({
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['创青春', '挑战杯', '蓝桥杯', '新大赛'],
                right: 0,
                orient: 'vertical',
                y: 'center'
            },
            grid: [{
                x: 'center',
                y: 'center',
                width: '75%',
                height: '70%'
            }],
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: ['2014年', '2015年', '2016年', '2017年']
            },
            yAxis: {
                type: 'value'
            },
            series: [{
                type: 'line',
                name: '创青春',
                data: [30, 60, 80, 100]
            }, {
                type: 'line',
                name: '挑战别',
                data: [50, 60, 80, 120]
            }, {
                type: 'line',
                name: '蓝桥杯',
                data: [30, 80, 120, 200]
            }, {
                type: 'line',
                name: '新大赛',
                data: [30, 80, 120, 500]
            }]
        });


        var seminaryMatchChartEle1 = document.getElementById('seminaryMatch');
        var seminaryMatchChart = echarts.init(seminaryMatchChartEle1, 'macarons');
        var seminaryMatchChartEle2 = document.getElementById('seminaryMatch2');
        var seminaryMatchChart2 = echarts.init(seminaryMatchChartEle2, 'macarons');
        var $seminaryMatchYear = $('#seminaryMatchYear');
        var $matchTypes = $('#matchTypes');
        $seminaryMatchYear.on('change', function (e) {
            pieSeminaryMatchChart();
            barSeminaryMatchChart()
        });
        $matchTypes.on('change', function (e) {
            pieSeminaryMatchChart();
            barSeminaryMatchChart()
        });

        function pieSeminaryMatchChart() {
            var legendData = ['在校参赛人数', '毕业参赛人数', '休学参赛人数'];
            var seriesData = [];
            legendData.forEach(function (item, i) {
                seriesData.push({
                    name: item,
                    value: Math.floor((i + 1) * Math.random() * 5000)
                })
            });
            seminaryMatchChart.setOption({
                width: 'auto',
                title: {
                    y: 30,
                    x: 'center',
                    text: '双创入围赛制分布'
                },
                tooltip: {
                    trigger: 'item'
                },
                selectedMode: 'single',
                legend: [{
                    x: 'center',
                    bottom: 30,
                    data: legendData
                }],
                grid: [{
                    x: 'center',
                    y: 'center'
                }],
                series: {
                    type: 'pie',
                    center: ['50%', '50%'],
                    radius: [0, '50%'],
                    label: {
                        normal: {
                            position: 'inner',
                            formatter: '{d}%'
                        }
                    },
                    data: seriesData
                }
            })
        }

        function barSeminaryMatchChart() {
            var legendData = ['校级初赛', '省市复赛', '全国决赛', '得奖总数'];
            var seriesData = [];
            var xAxisData = ['机械与运载工程学院', '材料科学与工程学院', '建筑学院', '土木工程学院', '化学化工学院', '数学与计量经济学院', '经济与贸易学院', '工商管理学院'];
            legendData.forEach(function (item, i) {
                seriesData.push({
                    type: 'bar',
                    barWidth: 15,
                    name: item,
                    stack: '学院',
                    data: (function (xAxisData) {
                        var list = [];
                        xAxisData.forEach(function (item, i) {
                            list.push(Math.floor(Math.random() * 500))
                        });
                        return list
                    })(xAxisData)
                })
            });

            seminaryMatchChart2.setOption({
                title: {
                    y: 30,
                    x: 'center',
                    text: '各院系参赛对比'

                },
                legend: [{
                    x: 'center',
                    bottom: 30,
                    data: legendData
                }],
                tooltip: {
                    trigger: 'axis'
                },
                grid: {
                    y: 'center',
                    x: 'center',
                    width: '75%',
                    height: '55%'
                },
                xAxis: {
                    type: 'category',
                    axisLabel: {
                        interval: 0,
                        rotate: 30
                    },
                    data: xAxisData
                },
                yAxis: [
                    {
                        type: 'value',
                        name: ''
                    }
                ],
                series: seriesData
            })
        }

        $seminaryMatchYear.trigger('change');

        var stuChartBarEle = document.getElementById('stuChartBar');
        var stuChartBarChart = echarts.init(stuChartBarEle, 'macarons');
        var $matchYear = $('#matchYear');


        $matchYear.on('change', function (e) {
            pieBarStuChart()
        });


        function barStuChartBarChart() {
            var legendData = ['在校参赛人数', '毕业参赛人数', '休学参赛人数'];
            var seriesData = [];
            var xAxisData = ['互联网+大赛', '创青春大赛', '蓝桥杯大赛', '挑战杯大赛'];
            legendData.forEach(function (item, i) {
                seriesData.push({
                    type: 'bar',
                    barWidth: 15,
                    name: item,
                    data: (function (xAxisData) {
                        var list = [];
                        xAxisData.forEach(function (item, i) {
                            list.push(Math.floor(Math.random() * 500))
                        });
                        return list
                    })(xAxisData)
                })
            });

            stuChartBarChart.setOption({
                title: {
                    y: 30,
                    x: 'center',
                    text: '双创大赛各类学生分布统计'

                },
                legend: [{
                    x: 'center',
                    bottom: 30,
                    data: legendData
                }],
                tooltip: {
                    trigger: 'axis'
                },
                grid: {
                    y: 'center',
                    x: 'center',
                    width: '75%',
                    height: '55%'
                },
                xAxis: {
                    type: 'category',
                    axisLabel: {
                        interval: 0,
                        rotate: 30
                    },
                    data: xAxisData
                },
                yAxis: [
                    {
                        type: 'value',
                        name: ''
                    }
                ],
                series: seriesData
            })
        }

        function pieBarStuChart() {
            var legendData = ['在校参赛人数', '毕业参赛人数', '休学参赛人数'];
            var seriesData = [];
            var pieData = [];
            var pie = {
                type: 'pie',
                center: ['20%', '50%'],
                radius: [0, '50%'],
                label: {
                    normal: {
                        position: 'inner',
                        formatter: '{d}%'
                    }
                },
                data: []
            };
            var xAxisData = ['互联网+大赛', '创青春大赛', '蓝桥杯大赛', '挑战杯大赛'];

            legendData.forEach(function (item, i) {
                pieData.push({
                    name: item,
                    value: Math.floor((i + 1) * Math.random() * 5000)
                });
                seriesData.push({
                    type: 'bar',
                    barWidth: 15,
                    name: item,
                    data: (function (xAxisData) {
                        var list = [];
                        xAxisData.forEach(function (item, i) {
                            list.push(Math.floor(Math.random() * 500))
                        });
                        return list
                    })(xAxisData)
                })
            });
            pie['data'] = pieData;
            seriesData.push(pie);
            stuChartBarChart.setOption({
                title: [{
                    show: false,
                    y: 30,
                    x: 'center',
                    text: '双创入围赛制分布'
                }, {
                    y: 30,
                    x: 'center',
                    text: '双创大赛各类学生分布统计'

                }],
                legend: [{
                    show: false,
                    x: 'center',
                    bottom: 30,
                    data: legendData
                }, {
                    x: 'center',
                    bottom: 30,
                    data: legendData
                }],
                tooltip: {
                    trigger: 'axis'
                },
                grid: [{
                    y: 'center',
                    x: '40%',
                    right: 30,
                    height: '55%'
                }],
                xAxis: {
                    type: 'category',
                    data: xAxisData
                },
                yAxis: [
                    {
                        type: 'value',
                        name: ''
                    }
                ],
                series: seriesData
            })

        }

        $matchYear.trigger('change')
    })

</script>
</body>


</html>