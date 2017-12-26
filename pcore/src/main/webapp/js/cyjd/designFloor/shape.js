var BASICSHAPE = [
    {
        "type": "room",
        "shapeType": "rect",
        "transform": "matrix(1,0,0,1,0,0)",
        "roomId": "",
        "attr": {
            "fill": "transparent",
            "x": "0",
            "y": "0",
            "width": "100",
            "height": "100",
            "style": "stroke-width: 2px;stroke: #222138"
        },
        "children": [
            {
                "type": "roomAsset",
                "shapeType": "text",
                "text": "房间名",
                "roomId": "",
                "attr": {
                    "x": "50",
                    "y": "40",
                    "fill": "#e9442d",
                    "style": "font-size: 12px;font-family:Arial;text-anchor:middle;"
                }
            }
        ]
    }
]

var ASSETS = [{
    type: 'roomAsset',
    shapeType: 'text',
    name: '描述文字',
    text: '描述\n文字',
    attr: {
        "x": 15,
        "y": 0,
        "fill": "#e9442d",
        style: 'font-size: 12px;font-family:Arial;text-anchor:middle;'
    }
}, {
    type: 'roomAsset',
    shapeType: 'image',
    name: '单人办公桌',
    attr: {
        fill: 'transparent',
        x: 40,
        y: 0,
        width: 30,
        height: 30,
        style: 'stroke-width: 0',
        href: '/images/roomAsset/desk1.png'
    }
}, {
    type: 'roomAsset',
    shapeType: 'image',
    name: '双人办公桌',
    attr: {
        fill: 'transparent',
        x: 80,
        y: 0,
        width: 30,
        height: 30,
        style: '',
        href: '/images/roomAsset/desk2.png'
    }
}, {
    type: 'roomAsset',
    shapeType: 'image',
    name: '4人办公桌',
    attr: {
        fill: 'transparent',
        x: 0,
        y: 40,
        width: 30,
        height: 30,
        style: '',
        href: '/images/roomAsset/desk4.png'
    }
}, {
    type: 'roomAsset',
    shapeType: 'image',
    name: '10人办公桌',
    attr: {
        fill: 'transparent',
        x: 40,
        y: 40,
        width: 30,
        height: 30,
        style: '',
        href: '/images/roomAsset/desk10.png'
    }
}, {
    type: 'roomAsset',
    shapeType: 'image',
    name: '1人沙发',
    attr: {
        fill: 'transparent',
        x: 80,
        y: 40,
        width: 30,
        height: 30,
        style: '',
        href: '/images/roomAsset/sofa1.png'
    }
}, {
    type: 'roomAsset',
    shapeType: 'image',
    name: '3人沙发',
    attr: {
        fill: 'transparent',
        x: 0,
        y: 80,
        width: 30,
        height: 30,
        style: '',
        href: '/images/roomAsset/sofa3.png'
    }
}, {
    type: 'roomAsset',
    shapeType: 'rect',
    name: '文字背景',
    attr: {
        fill: '#ffffff',
        x: 80,
        y: 80,
        width: 30,
        height: 30,
        style: 'stroke-width: 2px; stroke: #e9442d'
    }
}]

var TRIMMING = [{
    type: 'trimming',
    shapeType: 'image',
    transform: 'matrix(1,0,0,1,0,0)',
    name:'双门',
    attr: {
        fill: 'transparent',
        x: 0,
        y: 0,
        width: 30,
        height: 30,
        style: '',
        href: '/images/trimming/ddoor.png'
    }
}, {
    type: 'trimming',
    shapeType: 'image',
    name:'单门',
    transform: 'matrix(1,0,0,1,0,0)',
    attr: {
        fill: 'transparent',
        x: 40,
        y: 0,
        width: 30,
        height: 30,
        style: '',
        href: '/images/trimming/door.png'
    }
}, {
    type: 'trimming',
    shapeType: 'image',
    name:'单门1',
    transform: 'matrix(1,0,0,1,0,0)',
    attr: {
        fill: 'transparent',
        x: 80,
        y: 0,
        width: 30,
        height: 30,
        style: '',
        href: '/images/trimming/door1.png'
    }
}, {
    type: 'trimming',
    shapeType: 'image',
    name:'窗户',
    transform: 'matrix(1,0,0,1,0,0)',
    attr: {
        fill: 'transparent',
        x: 0,
        y: 40,
        width: 30,
        height: 30,
        style: '',
        href: '/images/trimming/window1.png'
    }
}, {
    type: 'trimming',
    shapeType: 'image',
    name:'单门',
    transform: 'matrix(1,0,0,1,0,0)',
    attr: {
        fill: 'transparent',
        x: 40,
        y: 40,
        width: 30,
        height: 30,
        style: '',
        href: '/images/trimming/window2.png'
    }
}, {
    type: 'trimming',
    shapeType: 'rect',
    name:'补充背景',
    transform: 'matrix(1,0,0,1,0,0)',
    attr: {
        fill: '#ffffff',
        x: 80,
        y: 40,
        width: 30,
        height: 30,
        style: 'stroke-width: 2px; stroke: #e9432d'
    }
}]