<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>双创项目统计</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.min.css">
    <script src="/static/jquery/jquery-1.8.3.min.js"></script>
    <script src="/js/echarts.min.js"></script>
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

        .title-bar .radio {
            height: 20px;
            line-height: 20px;
            margin-top: 11px;
            margin-bottom: 11px;
        }
    </style>
</head>
<body style="padding-bottom: 60px;">
<div class="mybreadcrumbs"><span>项目统计</span></div>
<div class="view-content">
    <div class="container-fluid">
        <div class="row title-bar">
            <div class="col-md-12">
                双创项目申报年增长趋势
            </div>
        </div>
        <div class="row" style="border-bottom: 1px solid #ccc">
            <div class="col-md-12">
                <div id="annalsChart" style="width: 100%;height:400px;"></div>
            </div>
        </div>
        <div class="row title-bar">
            <div class="col-md-6 print-md-6">
                双创项目分布统计
            </div>
            <div class="col-md-6 text-right">
                统计年份：
                <select id="proScatterYear">
                    <option value="">全部</option>
                    <option value="2017">2017年</option>
                    <option value="2016">2016年</option>
                    <option value="2015">2015年</option>
                    <option value="2014">2014年</option>
                </select>
            </div>
        </div>
        <div class="row" style="border: solid #ccc;border-width: 1px 0">
            <div class="col-md-6">
                <div id="proportionsChart" style="width: 100%; height:500px;"></div>
            </div>
            <div class="col-md-6">
                <div id="distributionSubChart" style="width: 100%; height:500px;"></div>
            </div>
        </div>
        <div class="row title-bar">
            <div class="col-sm-6 col-md-3">
                各学院项目立项统计
            </div>
            <div class="col-sm-6 col-md-3">
                <div class="radio">
                    <label>
                        <input type="radio" name="optionsRadios" id="optionsRadios1" value="1" checked>双创项目立项统计
                    </label>
                </div>
            </div>
            <div class="col-sm-6 col-md-3">
                <div class="radio">
                    <label>
                        <input type="radio" name="optionsRadios" id="optionsRadios2" value="2">双创项目类型统计
                    </label>
                </div>
            </div>
            <div class="col-md-3 text-right">
                统计年份：
                <select id="approvalYear">
                    <option value="">全部</option>
                    <option value="2017">2017年</option>
                    <option value="2016">2016年</option>
                    <option value="2015">2015年</option>
                    <option value="2014">2014年</option>
                </select>
            </div>
        </div>
        <div class="row approval-charts" style="border: solid #ccc;border-width: 1px 0">
            <div class="col-md-12">
                <div id="approvalsSubChart" style="width:100%; height:500px;"></div>
            </div>
            <div class="col-md-12 hide">
                <div id="approvalsChart" style="width:100%; height:500px;"></div>
            </div>
        </div>
    </div>
</div>
</body>
<script>
    $(function () {
        var annalsChartEle = document.getElementById('annalsChart');
        var annalsChart = echarts.init(annalsChartEle, 'macarons');
        annalsChart.setOption({
            tooltip: {
                trigger: 'axis'
            },
            legend: {
                data: ['国创项目', '开放实验项目', '创新性实验项目'],
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
                name: '国创项目',
                data: [30, 60, 80, 100]
            }, {
                type: 'line',
                name: '开放实验项目',
                data: [50, 60, 80, 120]
            }, {
                type: 'line',
                name: '创新性实验项目',
                data: [30, 80, 120, 200]
            }]
        });


        var proportionsChartEle = document.getElementById('proportionsChart');
        var proportionsChart = echarts.init(proportionsChartEle, 'macarons');
        var distributionSubChartEle = document.getElementById('distributionSubChart');
        var distributionSubChart = echarts.init(distributionSubChartEle, 'macarons');
        var $proScatterYear = $('#proScatterYear');

        $proScatterYear.on('change', function () {
            autoProportionsChart();
            autoDistributionSubChart()
        });

        function autoProportionsChart() {
            var legendData = ['创业项目', '大创项目'];
            var seriesData = [];
            legendData.forEach(function (item, i) {
                seriesData.push({
                    name: item,
                    value: Math.floor((i + 1) * Math.random() * 5000)
                })
            });
            proportionsChart.setOption({
                width: 'auto',
                title: {
                    text: '双创项目分布比例',
                    y: 50,
                    x: 'center'
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

        function autoDistributionSubChart() {
            var legendData = ['创新训练', '创业训练', '创业实践'];
            var seriesData = [];
            legendData.forEach(function (item, i) {
                seriesData.push({
                    name: item,
                    value: Math.floor((i + 1) * Math.random() * 5000)
                })
            });
            distributionSubChart.setOption({
                width: 'auto',
                title: {
                    text: '大创项目类型分布',
                    y: 50,
                    x: 'center'
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

        $proScatterYear.trigger('change');

        proportionsChart.on('pieselectchanged', function (params) {
            autoDistributionSubChart()
        });

        var approvalsSubChartEle = document.getElementById('approvalsSubChart');
        var approvalsSubChart;
        var approvalsChartEle = document.getElementById('approvalsChart');
        var approvalsChart;
        var $approvalYear = $('#approvalYear');
        var $optionsRadios = $('input[name="optionsRadios"]');
        var showIndex = 0;
        var $approvalCharts = $('.approval-charts');

        $optionsRadios.on('change', function () {
            var val = $(this).val();
            showIndex = $optionsRadios.index($(this));
            $approvalCharts.children().eq(showIndex).removeClass('hide').siblings().addClass('hide');
            setTimeout(function () {
                if (val == 1) {
                    if (!approvalsSubChart) {
                        approvalsSubChart = echarts.init(approvalsSubChartEle, 'macarons');
                    }
                    autoApprovalsSubChart()
                } else {
                    if (!approvalsChart) {
                        approvalsChart = echarts.init(approvalsChartEle, 'macarons');
                    }
                    autoApprovalsChart()
                }
            }, 0)
        });

        $approvalYear.on('change', function () {
            var val = $(this).val();
            if (showIndex == 1) {
                autoApprovalsChart()
            } else {
                autoApprovalsSubChart()
            }
        });

        function autoApprovalsSubChart() {
            var legendData = ['国创项目申报数', '开放实验项目申报数', '创新性实验项目申报数', '国创项目立项数'];
            var seriesData = [];
            var xAxisData = ['机械与运载工程学院', '材料科学与工程学院', '建筑学院', '土木工程学院', '化学化工学院', '数学与计量经济学院', '经济与贸易学院', '工商管理学院', '马克思主义学院', '教育科学研究院', '中国语言文学学院', '外国语与国际教育学院', '汽车车身先进设计制造国家重点实验室', '经济管理研究中心', '电子与信息工程学院', '信息工程与工程学院', '环境工程与工程学院', '设计艺术学院', '生物学院', '物理与微电子科学学院'];
            legendData.forEach(function (item, i) {
                seriesData.push({
                    type: 'bar',
                    barWidth: 15,
                    name: item,
                    stack: '项目',
                    data: (function (xAxisData) {
                        var list = [];
                        xAxisData.forEach(function (item, i) {
                            list.push(Math.floor(Math.random() * 500))
                        });
                        return list
                    })(xAxisData)
                })
            });
            approvalsSubChart.setOption({
                legend: [{
                    x: 'center',
                    bottom: 30,
                    data: legendData
                }],
                tooltip: {
                    trigger: 'axis'
                },
                grid: {
                    x: 'center',
                    y: 50,
                    width: '75%',
                    height: '50%'
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

        function autoApprovalsChart() {
            var legendData = ['国创项目申报数', '开放实验项目申报数', '创新性实验项目申报数', '国创项目立项数'];
            var seriesData = [];
            var xAxisData = ['机械与运载工程学院', '材料科学与工程学院', '建筑学院', '土木工程学院', '化学化工学院', '数学与计量经济学院', '经济与贸易学院', '工商管理学院', '马克思主义学院', '教育科学研究院', '中国语言文学学院', '外国语与国际教育学院', '汽车车身先进设计制造国家重点实验室', '经济管理研究中心', '电子与信息工程学院', '信息工程与工程学院', '环境工程与工程学院', '设计艺术学院', '生物学院', '物理与微电子科学学院'];
            legendData.forEach(function (item, i) {
                seriesData.push({
                    type: 'bar',
                    barWidth: 15,
                    name: item,
                    stack: '项目',
                    data: (function (xAxisData) {
                        var list = [];
                        xAxisData.forEach(function (item, i) {
                            list.push(Math.floor(Math.random() * 500))
                        });
                        return list
                    })(xAxisData)
                })
            });
            approvalsChart.setOption({
                legend: [{
                    x: 'center',
                    bottom: 30,
                    data: legendData
                }],
                tooltip: {
                    trigger: 'axis'
                },
                grid: {
                    x: 'center',
                    y: 50,
                    width: '75%',
                    height: '50%'
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

        $('input[name="optionsRadios"]:checked').trigger('change')
    })
</script>

</html>