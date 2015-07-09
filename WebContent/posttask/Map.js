Ext.define('Platform.posttask.Map', {
  extend: 'Ext.window.Window',
  xtype: 'platform-posttask-map',
  title: '地图',
  closeAction: 'hide',
  width: 1000,
  height: 500,
  layout: 'fit',
  initComponent: function() {
    var me = this;

    me.listeners = {
      afterrender: me.onAfterRender
    };

    me.callParent();
  },
  onAfterRender: function() {
    // this.initBMap();
  },
  initBMap: function() {
    var me = this;
    me.map = new BMap.Map(Ext.getDom(me.getTargetEl()));
    var scaleControl = new BMap.ScaleControl({
      anchor: BMAP_ANCHOR_TOP_LEFT
    });
    var navigationControl = new BMap.NavigationControl();
    me.map.addControl(scaleControl);
    me.map.addControl(navigationControl);
    me.map.enableScrollWheelZoom();
    me.map.centerAndZoom(new BMap.Point(116.404, 39.915), 4);
    me.map.addEventListener('zoomend', function(type, target) {
      console.log(type);
    });
    me.mapTip = Ext.widget('window', {
      title: 'AA',
      width: 200,
      height: 200,
      html: 'dddd'
    });
  },
  loadData: function(cid) {
    var me = this;
    me.setLoading(true);
    if (!me.map) {
      me.initBMap();
    }
    var markers = [];
    for (var i = 0; i < 10; i++) {
      var marker = new BMap.Marker(new BMap.Point(Math.random() * 40 + 85, Math.random() * 30 + 21));
      marker.setTitle('标题');
      marker.setLabel('标注');

      marker.addEventListener('click', function(type, target) {
        var mapTip = me.mapTip;
        var infoWindow = new BMap.InfoWindow('sss');
        this.openInfoWindow(infoWindow);
      })
      markers.push(marker);
    }
    var markerClusterer = new BMapLib.MarkerClusterer(me.map, {
      markers: markers
    });

    me.setLoading(false);
  },
  loadData2: function(cid) {
    var me = this;
    if (!me.map) {
      me.map = new BMap.Map(Ext.getDom(me.getTargetEl()));
      me.map.enableScrollWheelZoom();
    }
    me.setLoading(true);
    Ext.Ajax.request({
      method: 'post',
      url: ctx + '/posttask/postPoints.do',
      params: {
        cid: cid
      },
      callback: function(options, success, response) {
        var response = Ext.decode(response.responseText);
        if (response.success) {
          me.map.centerAndZoom(new BMap.Point(116.404, 39.915), 4);
          var points = response.data;
          var markers = [];
          for (var i = 0; i < points.length; i++) {
            markers.push(new BMap.Marker(new BMap.Point(points[i][0], points[i][1])));
          }
          var markerClusterer = new BMapLib.MarkerClusterer(me.map, {
            markers: markers
          });
        }
        me.setLoading(false);
      }
    });
  }
});