/**
 *
 *
 */
(function mapWrapper($) {

  // constructor

  var GisMap = function GisMap(options) {
    // 绑定选项
    this.options = $.extend({}, $.fn.gisMap.defaults, options)
    // 创建地图
    bmap = new BMap.Map(this.attr('id'), {enableMapClick: false})

    // 聚焦默认中心点
    var _point = stringToPoint(this.options.centerPoint)
    bmap.centerAndZoom(_point, this.options.centerLevel)

    // 加载全局地图设置
    if (this.options.enableScrollWheelZoom)
      bmap.enableScrollWheelZoom()
    if (this.options.minZoom)
      bmap.setMinZoom(this.options.minZoom)
    if (this.options.maxZoom)
      bmap.setMaxZoom(this.options.maxZoom)
    if (this.options.disableDoubleClickZoom)
      bmap.disableDoubleClickZoom()
    if (this.options.enableContinuousZoom)
      bmap.enableContinuousZoom()
    // 加载控件
    for (index in this.options.controllers)
      bmap.addControl(new BMap[this.options.controllers[index]])

    // 创建api对象
    var retval = new GisAPI(bmap)

    // 绑定列表点击回调
    $("div[data-type='estate']").delegate("h2", "click", function () {
      var target = $(this).data('estate');
      //坐标信息在 target[point][lng] 和 point[point][lat] 里面
      if( target['point']['lng'] <= point_min_x &&  target['point']['lat'] <= point_min_y){
        // 弹出框模版代码 ： msgShow($.i18n.prop('contract.house.error'));
        msgShow($.i18n.prop('map.noPosition'));
        return;
      }
      BMapLib.EventWrapper.trigger(target, 'click', {'target': target})
    })

    // 地块
    /*$("div[data-type='tenant']").delegate("li", "click", function () {
      var target = $(this).data('estate')
      var e = document.createEvent('MouseEvents');
      BMapLib.EventWrapper.trigger(target, 'click', {'target': target})
    })// 入驻企业*/


    // TODO:加载过滤器
    this._filters = new Array()
    for (key in this.options.filters) {
      var fx = this.options.filters[i]
      this._filters.push({
        status: 'off',
        vote: fx
      })
    }

    // 放置原型属性
    retval.clusterLevel = this.options.clusterLevel
    /*retval._markerIcon = new BMap.Symbol(
      this.options.markerStyle.icon.shape,
      this.options.markerStyle.icon.options)*/
    retval._labelTemplate = this.options.labelTemplate
    retval._labelOffset = this.options.labelOffset
    retval._labelCss = this.options.labelCss

    /*retval._cmarkerIcon = new BMap.Symbol(
      this.options.cmarkerStyle.icon.shape,
      this.options.cmarkerStyle.icon.options)*/
    retval._clabelTemplate = this.options.clabelTemplate
    retval._clabelOffset = this.options.clabelOffset
    retval._clabelCss = this.options.clabelCss

    retval._estateTag = this.options.estateFlyer
    retval._tenantTag = this.options.tenantFlyer
    retval._estateLi = this.options.estateItem
    retval._tenantLi = this.options.tenantItem
    retval.boxMsg = this.options.boxItem;
    retval.corpBoxMsg = this.options.corpBoxItem;
    return retval
  }
  var point_min_x = 120.549156;
  var point_min_y = 31.905664;
  // utility functions
  var stringToPoint = function stringToPoint(str) {
    if(str) {
      var _cords = str.split(",")
      return new BMap.Point(_cords[0], _cords[1])
    }else{
      //return new BMap.Point(121.076017+Math.random()*0.7,31.529246+Math.random()*0.8);
       return new BMap.Point(point_min_x - Math.random(),point_min_y - Math.random());
    }
  }
  // api class
  var GisAPI = function (map) {
    this._map = map //百度api对象
    this.markers = new Array() //地块数组
    this.clusterMarkers = new Array() //行政区数组

    this.renderDots = function () {
      // 切换地块/行政区
      if (this._map.getZoom() <= this.clusterLevel) {
        for (var i = 0; i < this.markers.length; i++)
          this.markers[i].hide();
        for (var i = 0; i < this.clusterMarkers.length; i++)
          this.clusterMarkers[i].show();
      } else {
        for (var i = 0; i < this.markers.length; i++)
          this.markers[i].show()
        for (var i = 0; i < this.clusterMarkers.length; i++)
          this.clusterMarkers[i].hide();
      }
    }

    this.fillData = function (template, attr) {
      // 展示板和标签静态参数填充
      var retval = "#".concat(template)
      // 如果类型不等于 99,也就是说这个attr不是企业数据,是房屋数据    由于房屋地址信息已经写在alias里面了,所以删去后面的地址描述
      if(attr['type'] != '99'){
        retval = retval.replace("<p>地址：{3}</p>", "")
      }
      //填充占位符，若attr中无对应的属性时会填充"(无)"
      retval = retval.replace("{0}", this.handleNull( attr["alias"]))
                      .replace("{1}", this.handleNull( attr["name"]))
                      //暂时未发现{2}的占位符 若有，则对应为 case "attr"
                      .replace("{3}", this.handleNull( attr["address"]))
                      .replace("{4}", this.handleNull( attr["owner"]))
                      .replace("{5}", this.handleNull( attr["operator"]))
                      .replace("{6}", this.handleNull( attr["area"]))
                      .replace("{7}", this.handleNull( attr["rent"]))
                      .replace("{8}", this.handleNull( attr["bz"]))
                      .replace("{9}", this.handleNull( attr["type"]))
                      //暂时未发现{10}的占位符 若有，则对应为 case "district"
                      .replace("{11}", this.handleNull( attr["id"]))
                      //暂时未发现{12}的占位符 若有，则对应为 case "num"
      return retval.substring(1);
    }



    this.handleNull = function (obj) {
      return obj ? obj : $.i18n.prop("map.empty");
    }

    this.fillDynamicData = function (template, attr) {
      // 展示板和标签动态参数填充
      var retval = "#".concat(template)
      var i = 0;
      for (key in attr) {
        retval = retval.replace("{d" + i + "}", attr[key])
        i++
      }
      return retval.substring(1)
    }

    this.findTarget = function (t) {
      // 选择事件绑定对象
      if (t == "marker")
        return this.markers
      if (t == "clusterMarker")
        return this.clusterMarkers
      if (t == "map")
        return this._map

    }

    this.doFilter = function (target, f) {
      if (!f) return
      switch (f.type) {
        case "display":
          if (!f.options.display) target.hide()
          return target

        case "style":
          for (key in f.options) {
            var str = "set" + f.options[key]
            target[str]
          }
          return target
        case "data":
          return this.fillDynamicData(target, f.options)

        default:
          return null
      }
    }
  }
  // api prototype
  GisAPI.prototype = {

    constructor: GisAPI,

    // 设定聚合缩放等级
    setClusterLevel: function setClusterLevel(lvl) {
      this.clusterLevel = lvl
      return this
    },

    // 设置标签样式及内容
    setLabel: function setLabel(text, style) {
      this._labelTemplate = text
      this._labelOffset = style.offset
      this._labelCss = style.css
      return this
    },
    // 加载过滤器，调用方法
    /**
     * function renderdot(f){ if (marker.filter.f[i]) marker.show else
		 * marker.hide() }
     *
     *
     * {in renderDots} traverse this.filterSwitch { attr = this.filters[i]
		 *
		 * traverse data { if !data.hasAttr(attr) data.hide }
		 *  }
     */

    // 加载数据
    loadEstateData: function loadEstateData(data,list) {
      // build estate markers
      if(list) {
        $("#pList").html("");
      }
      for (index in data) {
        console.debug('加载数据 loadEstateData data[' + index + '].type : ' + data[index].type)
        var t = data[index]
        var _point = stringToPoint(t.position)
          var _marker = new BMap.Marker(_point, {
            icon: new BMap.Icon("/resources/skin/default/images/" + t.type + ".png", new BMap.Size(100, 65), {})
          })
          /*if (this._labelCss) {
            var _text = this.fillData(this._labelTemplate, t.attr)
            var _label = new BMap.Label(_text, {
              offset: this._labelOffset
            })
            _label.setStyle(this._labelCss)
            _marker.setLabel(_label)
            _label.markerObj = _marker
          }*/
          _marker.dataIndex = index;
          _marker.dataType = "estate";


          if (t.type == "99") {
            _marker.msg = this.fillData(this.corpBoxMsg, t);
            _marker.dataType = "corp";
          } else {
            t["type2"] = t.attr.type;
            _marker.dataType = "parcel";
            _marker.msg = this.fillData(this.boxMsg, t);
          }
          _marker.type = t.type;
          _marker.dis = t.attr.district;
          data[index].overlay = _marker;
          this.markers.push(_marker);
          this._map.addOverlay(_marker);
        if (list) {
          var a = document.createElement("div");
          a.setAttribute('class', 'location location_border dis_'+t.attr.district+" type_"+t.type);
          $(a).append(this.fillData(this._estateLi, t))
          $("#pList").append(a);
          $.data($(a).find("h2")[0], 'estate', _marker);
        }
      }
      return this
    },
    loadDistrictData: function loadDistrictData(data, map_data,list) {
      // process district marker
      for (index in data) {
        var t = data[index]
        var _point = stringToPoint(t.position)
        var _marker = new BMap.Marker(_point, {
          /*icon: this._cmarkerIcon*/
          icon: new BMap.Icon("/resources/skin/default/images/mark_zone.png", new BMap.Size(200, 131), {
            // 指定定位位置。 当标注显示在地图上时，其所指向的地理位置距离图标左上 角各偏移10像素和25像素。
            // 您可以看到在本例中该位置即是 图标中央下端的尖角位置。
            anchor: new BMap.Size(10, 25),
            // 设置图片偏移。
            //imageOffset: new BMap.Size(0, 0 - index * 25)
          })
        })
        var sw = {}
        var ne = {}
        var count = 0
        for (var i in map_data) {
          //map_data 从319到400的data数据
          var dist = map_data[i].attr.district;
          if (t.alias === dist) {
            count++
            var pt = stringToPoint(map_data[i].position);
            //刚开始的时候sw和ne为空  第二次循环时候sw和ne继承了上一次循环的时候pt的值  开始做比较
            // sw取大于号 最后取最小坐标xy      ne小于号  去最大坐标xy
              if (sw.lng ) {
                if (sw.lng > pt.lng) sw.lng = pt.lng;
                if (sw.lat > pt.lat) sw.lat = pt.lat;
              } else sw = new BMap.Point(pt.lng, pt.lat);
              if (ne.lng) {
                if (ne.lng < pt.lng) ne.lng = pt.lng;
                if (ne.lat < pt.lat) ne.lat = pt.lat;
              } else ne = new BMap.Point(pt.lng, pt.lat);
          }
        }
        if (count > 0) {
          if (this._clabelCss) {
            var _text = this.fillDynamicData(this.fillData(this._clabelTemplate, t), {'count': count})
            var _label = new BMap.Label(_text, {
              offset: this._clabelOffset
            })
            _label.setStyle(this._clabelCss)
            _marker.setLabel(_label)
            _label.markerObj = _marker
          }
          _marker.dataIndex = index;
          _marker.dataType = "district";

          data[index].overlay = _marker;
          _marker.boundary = [sw, ne];
          _marker.count = count;
          _marker.type = t.alias;
          this.clusterMarkers.push(_marker);
          this._map.addOverlay(_marker);
          if(list) {
            if(t.alias=="99"){
              $.data($("div[data-type='estate'] div.option ul").find("li[type='corp']")[0], 'marker', _marker);
            }else {
              $.data($("div[data-type='estate'] div.option ul").find("li[dis='" + t.alias + "']")[0], 'marker', _marker);
            }
          }
        }
      }
      return this;
    },
    populate: function populate() {
      this.renderDots();
      return this;
    },
    // 绑定对象事件
    on: function on(event, fx) {
      var _e = event.split(".");
      var _event = _e[0];
      var _t = _e[1];
      var _target = this.findTarget(_t);
      if (_target.length) {
        for (key in _target) {
          (function (api) {
            BMapLib.EventWrapper.addListener(_target[key], _event, function (e) {
              fx(e.target, api)
            })
            if (_target[key].getLabel())
              BMapLib.EventWrapper.addListener(_target[key].getLabel(), _event, function (e) {
                fx(e.target, api)
              })
          })(this)
        }
      } else {
        (function (api) {
          BMapLib.EventWrapper.addListener(_target, _event, function (e) {
            fx(e.target, api)
          })
        })(this)
      }
      return this;
    }

  }

  // 定义jq插件
  $.fn.gisMap = GisMap;

  // api默认项
  $.fn.gisMap.defaults = {
    enableScrollWheelZoom: true,
    centerPoint: "121.426333,31.214599",
    centerLevel: 14,
    estateItem: $.i18n.prop("map.estateItem"),
    tenantItem: "",
    boxItem: $.i18n.prop("map.boxItem"),
    corpBoxItem:$.i18n.prop("map.corpBoxItem"),
    labelOffset: new BMap.Size(-80, 15),
    labelTemplate: $.i18n.prop("map.labelTemplate"),//单个地块内容
    labelCss: {
      //color: "black",
      fontSize: "15px",
      //fontWeight: "600",
      border: "0px",
      //height: "25px",
      //lineHeight: "25px",
      //maxWidth: "50px",
      //backgroundColor: "red",
      zIndex: "3000"
    },
    clabelOffset: new BMap.Size(75, 40),
    clabelTemplate: $.i18n.prop("map.clabelTemplate"), //区县内容
    clabelCss: {
      //color: "#bc3f2c",
      fontSize: "15px",
      height: "30px",
      lineHeight: "20px",
      border: "0px",
      maxWidth: "200px",
      zIndex: "3000",
      backgroundColor: "rgba(0,0,0,0)"
    }
  }
}(jQuery))

