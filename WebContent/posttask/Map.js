Ext.define('Platform.posttask.Map', {
  extend: 'Ext.window.Window',
  xtype: 'platform-posttask-map',
  title: '地图',
  closeAction: 'hide',
  maximizable: true,
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
    if (!this.map) {
      this.initBMap();
    }
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
    me.map.centerAndZoom(new BMap.Point(113.203125, 33.884664), 5);
    me.map.addEventListener('zoomend', function(e) {
      var target = e.target, zoom = target.getZoom();
      me.loadData(me.cid, zoom);
    });
    me.map.addEventListener('tilesloaded', function(e) {
      Ext.query('.anchorBL').forEach(function(el) {
        el.style.display = 'none';
      });
    });
  },
  loadData: function(cid, zoom, autoViewport) {
    var me = this;
    if (cid !== undefined) {
      me.cid = cid;
    }
    me.setLoading(true);
    me.map.clearOverlays();
    Ext.Ajax.request({
      method: 'post',
      url: ctx + '/posttask/postPoints.do',
      params: {
        cid: me.cid,
        zoom: zoom
      },
      callback: function(options, success, response) {
        var response = Ext.decode(response.responseText);
        if (response.success) {
          var points = [];
          for (var i = 0; i < response.data.length; i++) {
            var data = response.data[i];
            var point = new BMap.Point(data.center[0], data.center[1]);
            points.push(point);
            var marker = new BMap.Marker(point);
            marker.data = data;
            marker.setTitle(data.postCount);
            var label = new BMap.Label(data.postCount);
            label.setOffset(new BMap.Size(6 - (data.postCount + '').length * 3, 3));
            label.setStyle({
              borderColor: 'transparent',
              backgroundColor: 'transparent'
            });
            marker.setLabel(label);
            marker.addEventListener('click', function(e) {
              var marker = this, data = marker.data;
              if (!marker.infoWindow) {
                marker.infoWindow = new BMap.InfoWindow('共：' + data.postCount + ' 岗位<br />想看具体岗位，敬请期待！');
              }
              marker.openInfoWindow(marker.infoWindow);
            });
            me.map.addOverlay(marker);
          }
          if (false) {
            me.map.setViewport(points);
          }
        }
        me.setLoading(false);
      }
    });
  }
});