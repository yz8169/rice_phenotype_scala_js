Highcharts.setOptions({
    lang: {
        contextButtonTitle: "图表导出菜单",
        decimalPoint: ".",
        downloadJPEG: "下载JPEG图片",
        downloadPDF: "下载PDF文件",
        downloadPNG: "下载PNG文件",
        downloadSVG: "下载SVG文件",
        drillUpText: "返回 {series.name}",
        loading: "加载中",
        months: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
        noData: "没有数据",
        numericSymbols: ["千", "兆", "G", "T", "P", "E"],
        printChart: "打印图表",
        resetZoom: "恢复缩放",
        resetZoomTitle: "恢复图表",
        shortMonths: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
        thousandsSep: ",",
        weekdays: ["星期一", "星期二", "星期三", "星期三", "星期四", "星期五", "星期六", "星期天"]
    }
});

function boxPlot(data) {
    $("#charts").highcharts({
        credits: {
            enabled: false
        },
        chart: {
            type: 'boxplot',
            height: 550,
            marginTop: 40,
            marginBottom: 80
        },
        title: {
            text: "Boxplot"
        },
        legend: {
            enabled: false
        },
        xAxis: {
            labels: {
                style: {
                    fontSize: '12px',
                    fontFamily: 'Arial, sans-serif'
                }
            },
            categories: data.tissue,
            title: {
                text: null
            }
        },
        yAxis: {
            title: {
                text: null
            },
            min: 0
        },
        plotOptions: {
            boxplot: {
                fillColor: '#F0F0E0',
                lineWidth: 2,
                medianColor: '#0C5DA5',
                medianWidth: 3,
                stemColor: '#A63400',
                stemDashStyle: 'dot',
                stemWidth: 1,
                whiskerColor: '#3D9200',
                whiskerLength: '20%',
                whiskerWidth: 3
            }
        },
        series: [{
            data: data.ev
        },
            {
                name: 'Outlier',
                color: Highcharts.getOptions().colors[0],
                type: 'scatter',
                marker: {
                    fillColor: 'white',
                    lineWidth: 1,
                    lineColor: Highcharts.getOptions().colors[0]
                },
                tooltip: {
                    pointFormat: 'RPM: {point.y}'
                }
            }]
    });
}

var showTime=400
var hideTime=400





