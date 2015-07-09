Ext.define('Platform.posttask.View', {
  extend: 'Ext.grid.Panel',
  xtype: 'platform-posttask',
  uses: ['Platform.posttask.Bill', 'Platform.posttask.Post'],
  title: '岗位作业',
  initComponent: function() {
    var me = this;

    me.store = Store.create({
      fields: ['cid', 'norm', 'region', 'area', 'ignoredBillSize', 'billSize', 'failedPostSize', 'ignoredPostSize', 'insertedPostSize', 'updatedPostSize', 'postSize', 'explorerStatus', 'collectorStatus'],
      proxy: {
        type: 'ajax',
        url: ctx + '/posttask/tasks.do',
        reader: {
          type: 'json',
          root: 'data'
        }
      }
    });

    me.tbar = [{
      xtype: 'label',
      text: '添加作业'
    }, {
      itemId: 'normComboBox',
      xtype: 'combobox',
      displayField: 'name',
      valueField: 'name',
      queryMode: 'local',
      fieldLabel: '数据源',
      labelAlign: 'right',
      labelWidth: 60,
      editable: false,
      allowBlank: false,
      store: Store.create({
        fields: ['name'],
        sorters: ['name'],
        proxy: {
          type: 'ajax',
          url: ctx + '/posttask/norms.do',
          reader: {
            type: 'json',
            root: 'data'
          }
        }
      })
    }, {
      itemId: 'regionComboBox',
      xtype: 'combobox',
      displayField: 'name',
      valueField: 'name',
      queryMode: 'local',
      fieldLabel: '地区',
      labelAlign: 'right',
      labelWidth: 45,
      editable: false,
      allowBlank: false,
      store: Store.create({
        type: 'array',
        fields: ['name', 'areas'],
        data: regions
      }),
      listeners: {
        change: Ext.bind(me.onRegionComboBoxChange, me)
      }
    }, {
      itemId: 'areaComboBox',
      xtype: 'combobox',
      displayField: 'name',
      valueField: 'name',
      queryMode: 'local',
      editable: false,
      store: Store.create({
        type: 'array',
        fields: ['name']
      })
    }, {
      text: '确定',
      iconCls: 'add',
      handler: Ext.bind(me.onSubmitBtnClick, me)
    }, '-', {
      text: '刷新',
      iconCls: 'refresh',
      handler: Ext.bind(me.onRefreshBtnClick, me)
    }];

    me.columns = [{
      xtype: 'rownumberer',
      width: 32
    }, {
      xtype: 'templatecolumn',
      text: '作业',
      tpl: '{norm}: {region} {area}',
      width: 180
    }, {
      text: '状态',
      renderer: Ext.bind(me.statusColumnRenderer, me),
      width: 480
    }, {
      xtype: 'actioncolumn',
      text: '操作',
      width: 120,
      items: [{
        iconCls: 'start',
        tooltip: '启动',
        handler: Ext.bind(me.onStartBtnClick, me)
      }, '-', {
        iconCls: 'pause',
        tooltip: '暂停',
        handler: Ext.bind(me.onPauseBtnClick, me)
      }, '-', {
        iconCls: 'stop',
        tooltip: '停止',
        handler: Ext.bind(me.onStopBtnClick, me)
      }, '-', {
        iconCls: 'delete',
        tooltip: '删除',
        handler: Ext.bind(me.onDeleteBtnClick, me)
      }]
    }];

    me.listeners = {
      afterrender: me.onAfterRender,
      itemclick: me.onItemClick,
      cellclick: me.onCellClick
    };

    me.callParent();
  },
  onAfterRender: function() {
    var me = this, normComboBox = me.down('#normComboBox'), regionComboBox = me.down('#regionComboBox');
    normComboBox.getStore().load(function(records) {
      if (records[0]) {
        normComboBox.setValue(records[0].get(normComboBox.valueField));
      }
    });
    regionComboBox.setValue('广东');
    me.getStore().load();
  },
  onItemClick: function() {
    this.getStore().load();
  },
  onCellClick: function(table, td, cellIndex, record, tr, rowIndex, e) {
    var me = this, target = e.getTarget(), className = target.className, statusValue = target.getAttribute('status');
    if ('billSize' == className) {
      if (!me.billWindow) {
        me.billWindow = Platform.widget('posttask-bill');
      }
      me.billWindow.cid = record.get('cid');
      me.billWindow.loadData();
      me.billWindow.show();
    } else if (className == 'insertedPostSize' || className == 'updatedPostSize' || className == 'ignoredPostSize' || className == 'failedPostSize') {
      if (!me.postWindow) {
        me.postWindow = Platform.widget('posttask-post');
      }
      me.postWindow.loadData(record.get('cid'), statusValue);
      me.postWindow.show();
    }
  },
  onRegionComboBoxChange: function(field, newValue) {
    var me = this, areaComboBox = me.down('#areaComboBox');
    var areas = field.findRecordByValue(newValue).get('areas');
    var data = areas ? ['- - -'].concat(areas) : ['- - -'];
    areaComboBox.getStore().loadData(data.map(function(d) {
      return [d]
    }));
    areaComboBox.setValue('- - -');
  },
  onSubmitBtnClick: function() {
    var me = this, normComboBox = me.down('#normComboBox'), regionComboBox = me.down('#regionComboBox'), areaComboBox = me.down('#areaComboBox');
    if (normComboBox.isValid() && regionComboBox.isValid() && areaComboBox.isValid()) {
      me.setLoading(true);
      var params = {
        norm: normComboBox.getValue(),
        region: regionComboBox.getValue()
      }, area = areaComboBox.getValue();
      if ('- - -' != area) {
        params.area = area;
      }
      Ext.Ajax.request({
        async: false,
        url: ctx + '/posttask/addTask.do',
        params: params,
        callback: function(options, success, response) {
          var response = Ext.decode(response.responseText);
          if (response.success) {
            if (response.repeated) {
              Ext.Msg.alert('提示', '已经存在！');
            } else {
              me.getStore().load();
            }
          }
          me.setLoading(false);
        }
      });
    }
  },
  onRefreshBtnClick: function() {
    this.getStore().load();
  },
  statusColumnRenderer: function(value, metaData, record) {
    var ignoredBillSize = record.get('ignoredBillSize'), billSize = record.get('billSize');
    var failedPostSize = record.get('failedPostSize'), ignoredPostSize = record.get('ignoredPostSize'), insertedPostSize = record.get('insertedPostSize'), updatedPostSize = record.get('updatedPostSize'), processedPostSize = record.get('processedPostSize');
    var collectorStatus = record.get('collectorStatus'), explorerStatus = record.get('explorerStatus');
    var progressbarId = Ext.id(), progressbarValue = processedPostSize == 0 ? 0 : processedPostSize / (billSize - failedPostSize), tip1, tip2;
    if (collectorStatus == 0) {
      tip1 = '已停止';
      tip2 = Ext.String.format('已处理: {0} [新增: <a class="insertedPostSize" status=2 href="javascript:">{1}</a>, 更新: <a class="updatedPostSize" status=3 href="javascript:">{2}</a>, 忽略: <a class="ignoredPostSize" status=1 href="javascript:">{3}</a>], 已收集: <a class="billSize" href="javascript:">{4}</a> [不可处理: <a class="failedPostSize" status=-1 href="javascript:">{5}</a>]', processedPostSize, insertedPostSize, updatedPostSize, ignoredPostSize, billSize, failedPostSize);
    } else if (collectorStatus == 1) {
      if (explorerStatus == 1) {
        tip1 = '正在收集...';
        tip2 = Ext.String.format('已处理: {0} [新增: <a class="insertedPostSize" status=2 href="javascript:">{1}</a>, 更新: <a class="updatedPostSize" status=3 href="javascript:">{2}</a>, 忽略: <a class="ignoredPostSize" status=1 href="javascript:">{3}</a>], 已收集: <a class="billSize" href="javascript:">{4}</a> [不可处理: <a class="failedPostSize" status=-1 href="javascript:">{5}</a>]', processedPostSize, insertedPostSize, updatedPostSize, ignoredPostSize, billSize, failedPostSize);
      } else if (explorerStatus == 3) {
        tip1 = Ext.String.format('已处理: {0}', Ext.util.Format.percent(progressbarValue));
        tip2 = Ext.String.format('已处理: {0} [新增: <a class="insertedPostSize" status=2 href="javascript:">{1}</a>, 更新: <a class="updatedPostSize" status=3 href="javascript:">{2}</a>, 忽略: <a class="ignoredPostSize" status=1 href="javascript:">{3}</a>], 共收集: <a class="billSize" href="javascript:">{4}</a> [不可处理: <a class="failedPostSize" status=-1 href="javascript:">{5}</a>]', processedPostSize, insertedPostSize, updatedPostSize, ignoredPostSize, billSize, failedPostSize);
      }
    } else if (collectorStatus == 2) {
      tip1 = Ext.String.format('已暂停: {0}', Ext.util.Format.percent(progressbarValue));
      tip2 = Ext.String.format('已处理: {0} [新增: <a class="insertedPostSize" status=2 href="javascript:">{1}</a>, 更新: <a class="updatedPostSize" status=3 href="javascript:">{2}</a>, 忽略: <a class="ignoredPostSize" status=1 href="javascript:">{3}</a>], 已收集: <a class="billSize" href="javascript:">{4}</a> [不可处理: <a class="failedPostSize" status=-1 href="javascript:">{5}</a>]', processedPostSize, insertedPostSize, updatedPostSize, ignoredPostSize, billSize, failedPostSize);
    } else if (collectorStatus == 3) {
      tip1 = '已完成';
      tip2 = Ext.String.format('共处理: {0} [新增: <a class="insertedPostSize" status=2 href="javascript:">{1}</a>, 更新: <a class="updatedPostSize" status=3 href="javascript:">{2}</a>, 忽略: <a class="ignoredPostSize" status=1 href="javascript:">{3}</a>], 共收集: <a class="billSize" href="javascript:">{4}</a> [不可处理: <a class="failedPostSize" status=-1 href="javascript:">{5}</a>]', processedPostSize, insertedPostSize, updatedPostSize, ignoredPostSize, billSize, failedPostSize);
    }
    Ext.defer(function() {
      var progressbar = Ext.widget('progressbar', {
        renderTo: progressbarId,
        width: 440,
        value: progressbarValue
      });
      if (1 == collectorStatus && 1 == explorerStatus) {
        progressbar.wait({
          interval: 1000
        });
      }
      progressbar.updateText(tip1);
    }, 100);
    return Ext.String.format('<div id={0} style="margin:0px; padding:0px;"></div><div>{1}</div>', progressbarId, tip2);
  },
  onStartBtnClick: function(table, rowIndex, colIndex, item, e, record) {
    this.executeTask('posttask/startCollector.do', record.get('cid'));
  },
  onPauseBtnClick: function(table, rowIndex, colIndex, item, e, record) {
    this.executeTask('posttask/pauseCollector.do', record.get('cid'));
  },
  onStopBtnClick: function(table, rowIndex, colIndex, item, e, record) {
    this.executeTask('posttask/stopCollector.do', record.get('cid'));
  },
  onDeleteBtnClick: function(table, rowIndex, colIndex, item, e, record) {
    this.executeTask('posttask/deleteCollector.do', record.get('cid'));
  },
  executeTask: function(url, cid) {
    var me = this, view = me.getView();
    view.setLoading(true);
    Ext.Ajax.request({
      method: 'post',
      url: url,
      params: {
        cid: cid
      },
      callback: function(options, success, response) {
        var response = Ext.decode(response.responseText);
        if (response.success) {
          me.onRefreshBtnClick();
          view.setLoading(false);
        }
      }
    });
  }
});