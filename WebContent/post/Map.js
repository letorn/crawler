Ext.define('Platform.post.Map', {
  extend: 'Ext.panel.Panel',
  xtype: 'platform-post-map',
  title: '地图',
  layout: 'fit',
  initComponent: function() {
    var me = this;

    me.listeners = {
      afterlayout: me.onAfterLayout
    };

    me.callParent();
  },
  onAfterLayout: function() {
    if (!this.map) {
      this.initMap();
    }
    this.loadData(5, true);
  },
  initMap: function() {
    var me = this;
    me.map = new BMap.Map(me.body.dom, {
      minZoom: 5
    });
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
      me.loadData(zoom);
    });
    Ext.defer(function() {
      Ext.query('.anchorBL').forEach(function(el) {
        el.style.display = 'none';
      });
    }, 200);
  },
  addMarkers: function(data, autoViewport) {
    var me = this, points = [];
    for (var i = 0; i < data.length; i++) {
      var d = data[i];
      var point = new BMap.Point(d.center[0], d.center[1]);
      points.push(point);
      var marker = new BMap.Marker(point);
      marker.data = d;
      marker.setTitle(d.postCount);
      var label = new BMap.Label(d.postCount);
      label.setOffset(new BMap.Size(6 - (d.postCount + '').length * 3, 3));
      label.setStyle({
        borderColor: 'transparent',
        backgroundColor: 'transparent'
      });
      marker.setLabel(label);
      marker.addEventListener('click', function(e) {
        var marker = this, data = marker.data;
        if (!me.map.detailWindow) {
          me.map.detailWindow = Platform.widget('post-map-marker');
        }
        me.map.detailWindow.loadData(me.zoom, data.center);
        me.map.detailWindow.showAt(e.clientX, e.clientY);
      });
      me.map.addOverlay(marker);
    }
    if (false) {
      me.map.setViewport(points);
    }
  },
  loadData: function(zoom, autoViewport) {
    var me = this;
    if (zoom !== undefined) {
      me.zoom = zoom;
    }
    me.setLoading(true);
    me.map.clearOverlays();
    Ext.Ajax.request({
      method: 'post',
      url: ctx + '/posttask/mapData.do',
      params: {
        zoom: me.zoom
      },
      callback: function(options, success, response) {
        var response = Ext.decode(response.responseText);
        if (response.success) {
          me.addMarkers(response.data);
        }
        me.setLoading(false);
      }
    });
  }
});
Ext.define('Platform.post.MapMarker', {
  extend: 'Ext.window.Window',
  xtype: 'platform-post-map-marker',
  title: '岗位',
  closeAction: 'hide',
  resizable: false,
  width: 500,
  height: 300,
  layout: 'fit',
  initComponent: function() {
    var me = this;

    me.gridStore = Store.create({
      fields: ['dataUrl', 'updateDate', 'name', 'category', 'numberText', 'nature', 'salaryText', 'experience', 'education', 'welfare', 'address', 'introduction'],
      proxy: {
        type: 'ajax',
        url: ctx + '/posttask/mapMarkerData.do',
        extraParams: {},
        reader: {
          type: 'json',
          root: 'data',
          totalProperty: 'total'
        }
      }
    });

    me.gridPanel = Ext.widget('grid', {
      store: me.gridStore,
      columns: [{
        xtype: 'rownumberer',
        width: 32
      }, {
        text: '链接',
        dataIndex: 'dataUrl',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 160
      }, {
        xtype: 'datecolumn',
        text: '发布日期',
        dataIndex: 'updateDate',
        format: 'Y-m-d',
        width: 100
      }, {
        text: '职位',
        dataIndex: 'name',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 180
      }, {
        text: '职能',
        dataIndex: 'category',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 160
      }, {
        text: '招聘人数',
        dataIndex: 'numberText',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 80
      }, {
        text: '薪酬',
        dataIndex: 'salaryText',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 100
      }, {
        text: '工作经验',
        dataIndex: 'experience',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 80
      }, {
        text: '最低学历',
        dataIndex: 'education',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 80
      }, {
        text: '福利',
        dataIndex: 'welfare',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 120
      }, {
        text: '工作地点',
        dataIndex: 'address',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 80
      }],
      bbar: Ext.widget('pagingtoolbar', {
        store: me.gridStore,
        displayInfo: true,
        displayMsg: '显示 {0} - {1} / 共 {2} 条'
      }),
      listeners: {
        itemdblclick: Ext.bind(me.onGridItemDblClick, me)
      }
    });

    me.items = [me.gridPanel];

    me.callParent();
  },
  loadData: function(zoom, center) {
    var me = this;
    me.gridStore.proxy.extraParams = {
      zoom: zoom,
      center: center
    };
    me.gridStore.loadPage(1);
  },
  columnFormatter: function(value, metaData, record, rowIndex, colIndex, store, el, e) {
    if (value != null) {
      value = Ext.String.htmlEncode(value);
      metaData.tdAttr = 'data-qtip="' + value + '"';
      return value;
    }
    return "";
  },
  onGridItemDblClick: function(gridview, record, item, index) {
    var me = this;
    if (!me.detailWindow) {
      me.detailWindow = Platform.widget('post-map-marker-detail');
      me.detailWindow.postGridStore = me.gridStore;
    }
    me.detailWindow.loadData(record.get('dataUrl'));
    me.detailWindow.show();
  }
});
Ext.define('Platform.post.MapMarkerDetail', {
  extend: 'Ext.window.Window',
  xtype: 'platform-post-map-marker-detail',
  title: '详情',
  closeAction: 'hide',
  resizable: false,
  layout: {
    type: 'hbox',
    align: 'stretch'
  },
  initComponent: function() {
    var me = this;

    me.postView = Ext.widget('form', {
      trackResetOnLoad: false,
      border: false,
      padding: '5 0 0 0',
      defaults: {
        labelAlign: 'right',
        labelWidth: 65,
        width: 400
      },
      items: [{
        xtype: 'textfield',
        fieldLabel: '链接',
        name: 'dataUrl',
        readOnly: true
      }, {
        xtype: 'datefield',
        fieldLabel: '发布日期',
        format: 'Y-m-d',
        name: 'updateDate',
        readOnly: true
      }, {
        xtype: 'textfield',
        fieldLabel: '职位',
        name: 'name'
      }, {
        itemId: 'categoryComboBox',
        xtype: 'combobox',
        fieldLabel: '职能',
        name: 'categoryCode',
        displayField: 'name',
        valueField: 'code',
        matchFieldWidth: false,
        editable: false,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name', 'group']
        }),
        tpl: ['<tpl for=".">', '<tpl if="this.group != values.group">', '<tpl exec="this.group = values.group, this.itemSize = 0"></tpl>', '<hr><div style="color:gray;">{group}</div>', '</tpl>', '<tpl exec="this.itemSize ++"></tpl>', '<tpl if="this.itemSize &gt; 7">', '<tpl exec="this.itemSize = 0"></tpl>', '<br />', '</tpl>', '<span class="x-boundlist-item">{name}</span>', '</tpl>']
      }, {
        xtype: 'textfield',
        fieldLabel: '招聘人数',
        name: 'numberText',
        readOnly: true
      }, {
        xtype: 'textfield',
        fieldLabel: '薪酬',
        name: 'salaryText',
        readOnly: true
      }, {
        itemId: 'experienceComboBox',
        xtype: 'combobox',
        fieldLabel: '工作经验',
        name: 'experienceCode',
        displayField: 'name',
        valueField: 'code',
        editable: false,
        readOnly: true,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name']
        })
      }, {
        itemId: 'educationComboBox',
        xtype: 'combobox',
        fieldLabel: '最低学历',
        name: 'educationCode',
        displayField: 'name',
        valueField: 'code',
        editable: false,
        readOnly: true,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name']
        })
      }, {
        xtype: 'textfield',
        fieldLabel: '福利',
        name: 'welfare',
        readOnly: true
      }, {
        itemId: 'postAddress',
        xtype: 'textfield',
        fieldLabel: '地址',
        name: 'address'
      }, {
        layout: 'column',
        border: false,
        items: [{
          itemId: 'postLbsLon',
          columnWidth: 0.4,
          xtype: 'displayfield',
          fieldLabel: '经度',
          name: 'lbsLon',
          labelAlign: 'right',
          labelWidth: 65
        }, {
          itemId: 'postLbsLat',
          columnWidth: 0.4,
          xtype: 'displayfield',
          fieldLabel: '纬度',
          name: 'lbsLat',
          labelAlign: 'right',
          labelWidth: 40
        }, {
          columnWidth: 0.2,
          xtype: 'button',
          text: '定位',
          handler: Ext.bind(me.popupPostAddrMap, me)
        }]
      }, {
        xtype: 'textarea',
        fieldLabel: '介绍',
        name: 'introduction',
        readOnly: true,
        height: 220
      }]
    })

    me.enterpriseView = Ext.widget('form', {
      trackResetOnLoad: false,
      border: false,
      padding: '5 10 0 0',
      defaults: {
        labelAlign: 'right',
        labelWidth: 65,
        width: 400
      },
      items: [{
        xtype: 'textfield',
        fieldLabel: '链接',
        name: 'dataUrl',
        readOnly: true
      }, {
        xtype: 'textfield',
        fieldLabel: '企业',
        name: 'name'
      }, {
        itemId: 'categoryComboBox',
        xtype: 'combobox',
        fieldLabel: '行业',
        name: 'categoryCode',
        displayField: 'name',
        valueField: 'code',
        editable: false,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name']
        })
      }, {
        itemId: 'natureComboBox',
        xtype: 'combobox',
        fieldLabel: '性质',
        name: 'natureCode',
        displayField: 'name',
        valueField: 'code',
        editable: false,
        readOnly: true,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name']
        })
      }, {
        itemId: 'scaleComboBox',
        xtype: 'combobox',
        fieldLabel: '规模',
        name: 'scaleCode',
        displayField: 'name',
        valueField: 'code',
        editable: false,
        readOnly: true,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name']
        })
      }, {
        xtype: 'textfield',
        fieldLabel: '主页',
        name: 'website',
        readOnly: true
      }, {
        itemId: 'enterpriseAddress',
        xtype: 'textfield',
        fieldLabel: '地址',
        name: 'address'
      }, {
        layout: 'column',
        border: false,
        items: [{
          itemId: 'enterpriseLbsLon',
          columnWidth: 0.4,
          xtype: 'displayfield',
          fieldLabel: '经度',
          name: 'lbsLon',
          labelAlign: 'right',
          labelWidth: 65
        }, {
          itemId: 'enterpriseLbsLat',
          columnWidth: 0.4,
          xtype: 'displayfield',
          fieldLabel: '纬度',
          name: 'lbsLat',
          labelAlign: 'right',
          labelWidth: 40
        }, {
          columnWidth: 0.2,
          xtype: 'button',
          text: '定位',
          handler: Ext.bind(me.popupEnterpriseAddrMap, me)
        }]
      }, {
        xtype: 'textarea',
        fieldLabel: '介绍',
        name: 'introduction',
        readOnly: true,
        height: 220
      }]
    })

    me.items = [me.postView, me.enterpriseView];

    me.buttons = [{
      text: '确定',
      handler: Ext.bind(me.submitBtnClick, me)
    }, {
      text: '取消',
      handler: Ext.bind(me.cancelBtnClick, me)
    }];

    me.callParent();
  },
  loadCodes: function() {
    var me = this, postView = me.postView, enterpriseView = me.enterpriseView;
    if (!me.inited) {
      var postCategoryComboBox = postView.down('#categoryComboBox'), postExperienceComboBox = postView.down('#experienceComboBox'), postEducationComboBox = postView.down('#educationComboBox');
      var enterpriseCategoryComboBox = enterpriseView.down('#categoryComboBox'), enterpriseNatureComboBox = enterpriseView.down('#natureComboBox'), enterpriseScaleComboBox = enterpriseView.down('#scaleComboBox');
      Ext.Ajax.request({
        async: false,
        url: ctx + '/posttask/codes.do',
        callback: function(options, success, response) {
          var response = Ext.decode(response.responseText);
          if (response.success) {
            me.inited = true;
            postCategoryComboBox.getStore().loadData(response.postCategories);
            postExperienceComboBox.getStore().loadData(response.postExperiences);
            postEducationComboBox.getStore().loadData(response.postEducations);
            enterpriseCategoryComboBox.getStore().loadData(response.enterpriseCategories);
            enterpriseNatureComboBox.getStore().loadData(response.enterpriseNatures);
            enterpriseScaleComboBox.getStore().loadData(response.enterpriseScales);
          }
        }
      })
    }
  },
  loadData: function(dataUrl) {
    var me = this;
    me.loadCodes();
    me.postView.reset();
    me.enterpriseView.reset();
    Ext.Ajax.request({
      async: false,
      url: ctx + '/posttask/postDetail.do',
      params: {
        url: dataUrl
      },
      callback: function(options, success, response) {
        var response = Ext.decode(response.responseText);
        if (response.success) {
          response.post.updateDate = new Date(response.post.updateDate);
          me.postView.getForm().setValues(response.post);
          me.enterpriseView.getForm().setValues(response.enterprise);
        }
      }
    })
  },
  submitBtnClick: function() {
    var me = this, postView = me.postView, enterpriseView = me.enterpriseView;
    me.setLoading(true);
    var map = {
      post: postView.form.getFieldValues(),
      enterprise: enterpriseView.form.getFieldValues()
    };
    Ext.Ajax.request({
      async: false,
      url: ctx + '/posttask/savePost.do',
      jsonData: map,
      callback: function(options, success, response) {
        var response = Ext.decode(response.responseText);
        if (response.success) {
          me.postGridStore.reload();
          me.close();
          Ext.toast({
            title: '提示',
            html: '保存成功！',
            align: 't',
            slideInDuration: 100,
            slideBackDuration: 800,
            hideDuration: 100,
            autoCloseDelay: 1000,
          });
        } else {
          Ext.toast({
            title: '提示',
            html: '保存失败！',
            align: 't',
            slideInDuration: 100,
            slideBackDuration: 800,
            hideDuration: 100,
            autoCloseDelay: 1000,
          });
        }
        me.setLoading(false);
      }
    })
  },
  cancelBtnClick: function() {
    this.close();
  },
  initAddressMap: function() {
    var me = this;
    me.geocoder = new BMap.Geocoder();
    me.addressMap = Ext.widget('window', {
      width: 400,
      height: 280,
      header: false,
      closable: false,
      resizable: false,
      closeAction: 'hide',
      modal: true,
      bbar: [{
        id: 'autoText',
        itemId: 'text',
        xtype: 'combobox',
        fieldLabel: '地址',
        labelWidth: 40,
        labelAlign: 'right',
        width: 270,
        queryMode: 'local',
        triggerAction: 'all',
        displayField: 'text',
        valueField: 'text',
        store: Store.create({
          type: 'array',
          fields: ['text'],
          data: [],
          listeners: {
            datachanged: function(store) {
              store.picker.doQuery('', true);
            }
          }
        }),
        listeners: {
          change: function(field, newValue) {
            var win = field.ownerCt.ownerCt;
            me.delayedSearch.delay(800);
          }
        }
      }, {
        text: '确定',
        handler: function(btn) {
          var win = btn.ownerCt.ownerCt, textCmp = win.down('#text'), point = win.getPoint();
          if (win.closeCallback) {
            win.closeCallback('', point.lng, point.lat);
          }
          win.addressCmp.setValue(textCmp.getValue());
          win.lngCmp.setValue(point.lng);
          win.latCmp.setValue(point.lat);
          win.close();
        }
      }, {
        text: '取消',
        handler: function(btn) {
          var win = btn.ownerCt.ownerCt;
          win.close();
        }
      }],
      listeners: {
        afterlayout: function(win) {
          var textCmp = win.down('#text'), textStore = textCmp.getStore();
          win.map = new BMap.Map(win.body.dom, {
            minZoom: 5
          });
          win.map.enableScrollWheelZoom();
          win.map.centerAndZoom(new BMap.Point(113.203125, 33.884664), 5);
          var local = new BMap.Autocomplete({
            location: win.map,
            input: 'autoText',
            onSearchComplete: function(rs) {
              textStore.picker = textCmp;
              textStore.loadData(rs.Nq.map(function(item) {
                var done = false, include = false, address = item.business, strs = [item.province, item.city, item.district];
                for (var i = strs.length - 1; i >= 0; i--) {
                  var str = strs[i].replace(/省$|市$|区$/g, '');
                  if (str.length > 0) {
                    if (address.startsWith(str)) {
                      address = strs.slice(0, i).join('') + address;
                      done = true;
                      break;
                    } else {
                      if (address.includes(str))
                        include = true;
                    }
                  }
                }
                if (!done && !include)
                  address = strs.join('') + address;
                return [address];
              }));
            }
          });
          me.delayedSearch = new Ext.util.DelayedTask(function() {
            var newValue = textCmp.getValue();
            local.search(newValue);
            me.geocoder.getPoint(newValue, function(point) {
              var point = new BMap.Point(point.lng, point.lat);
              win.addressMarker.setPosition(point);
              win.map.centerAndZoom(point, 16);
            });
          });
          Ext.defer(function() {
            Ext.query('.anchorBL').forEach(function(el) {
              el.style.display = 'none';
            });
          }, 200);
        }
      },
      setPoint: function(addressCmp, lngCmp, latCmp) {
        var win = this, textCmp = win.down('#text');
        win.addressCmp = addressCmp;
        win.lngCmp = lngCmp;
        win.latCmp = latCmp;
        textCmp.reset();
        textCmp.setRawValue(addressCmp.getValue());
        var point = new BMap.Point(lngCmp.getValue() || 113.203125, latCmp.getValue() || 33.884664);
        win.map.centerAndZoom(point, 16);
        win.addressMarker = new BMap.Marker(point);
        win.addressMarker.addEventListener('dragend', function(e) {
          var marker = this;
          me.geocoder.getLocation(e.point, function(rs) {
            textCmp.setRawValue(rs.address);
          });
        });
        win.addressMarker.enableDragging();
        win.map.clearOverlays();
        win.map.addOverlay(win.addressMarker);
      },
      getPoint: function() {
        var win = this;
        return win.addressMarker.getPosition();
      }
    });
  },
  popupPostAddrMap: function() {
    var me = this, addressField = me.down('#postAddress'), lbsLonField = me.down('#postLbsLon'), lbsLatField = me.down('#postLbsLat');
    if (!me.addressMap) {
      me.initAddressMap();
    }
    me.addressMap.showAt(addressField.el.getX(), addressField.el.getY() - me.addressMap.height - 2)
    me.addressMap.setPoint(addressField, lbsLonField, lbsLatField);
  },
  popupEnterpriseAddrMap: function() {
    var me = this, addressField = me.down('#enterpriseAddress'), lbsLonField = me.down('#enterpriseLbsLon'), lbsLatField = me.down('#enterpriseLbsLat');
    if (!me.addressMap) {
      me.initAddressMap();
    }
    me.addressMap.showAt(addressField.el.getX(), addressField.el.getY() - me.addressMap.height - 2)
    me.addressMap.setPoint(addressField, lbsLonField, lbsLatField);
  }
});