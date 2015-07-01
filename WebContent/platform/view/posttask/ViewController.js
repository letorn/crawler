Ext.define('Platform.view.posttask.ViewController', {
  extend: 'Ext.app.ViewController',
  alias: 'controller.posttask',
  onViewAfterRender: function(view) {
    view.normStore.load(function() {
      var normField = view.down('#normComboBox');
      var record = normField.getStore().getAt(0);
      if (record) {
        normField.setValue(record.get(normField.valueField));
      }
    });
    view.down('#regionComboBox').setValue('广东');
    this.onRefreshBtnClick();
  },
  statusRenderer: function(value, metaData, record) {
    var ignoredBillSize = record.get('ignoredBillSize'), billSize = record.get('billSize');
    var ignoredPostSize = record.get('ignoredPostSize'), insertedPostSize = record.get('insertedPostSize'), updatedPostSize = record.get('updatedPostSize'), processedPostSize = record.get('processedPostSize');
    var collectorStatus = record.get('collectorStatus'), explorerStatus = record.get('explorerStatus');
    var progressbarId = Ext.id(), progressbarValue = processedPostSize == 0 ? 0 : processedPostSize / (billSize - ignoredBillSize), tip1, tip2;
    if (0 == collectorStatus) {
      tip1 = '已停止';
      tip2 = Ext.String.format('已处理: {0} [新增: <a class="insertedPostSize" status=2 href="javascript:">{1}</a>, 更新: <a class="updatedPostSize" status=3 href="javascript:">{2}</a>, 忽略: <a class="ignoredPostSize" status=1 href="javascript:">{3}</a>], 已收集: <a class="billSize" href="javascript:">{4}</a> [不可处理: {5}]', processedPostSize, insertedPostSize, updatedPostSize, ignoredPostSize, billSize, ignoredBillSize);
    } else if (1 == collectorStatus) {
      if (1 == explorerStatus) {
        tip1 = '正在收集...';
        tip2 = Ext.String.format('已处理: {0} [新增: <a class="insertedPostSize" status=2 href="javascript:">{1}</a>, 更新: <a class="updatedPostSize" status=3 href="javascript:">{2}</a>, 忽略: <a class="ignoredPostSize" status=1 href="javascript:">{3}</a>], 已收集: <a class="billSize" href="javascript:">{4}</a> [不可处理: {5}]', processedPostSize, insertedPostSize, updatedPostSize, ignoredPostSize, billSize, ignoredBillSize);
      } else if (3 == explorerStatus) {
        tip1 = Ext.String.format('已处理: {0}', Ext.util.Format.percent(progressbarValue));
        tip2 = Ext.String.format('已处理: {0} [新增: <a class="insertedPostSize" status=2 href="javascript:">{1}</a>, 更新: <a class="updatedPostSize" status=3 href="javascript:">{2}</a>, 忽略: <a class="ignoredPostSize" status=1 href="javascript:">{3}</a>], 共收集: <a class="billSize" href="javascript:">{4}</a> [不可处理: {5}]', processedPostSize, insertedPostSize, updatedPostSize, ignoredPostSize, billSize, ignoredBillSize);
      }
    } else if (2 == collectorStatus) {
      tip1 = Ext.String.format('已暂停: {0}', Ext.util.Format.percent(progressbarValue));
      tip2 = Ext.String.format('已处理: {0} [新增: <a class="insertedPostSize" status=2 href="javascript:">{1}</a>, 更新: <a class="updatedPostSize" status=3 href="javascript:">{2}</a>, 忽略: <a class="ignoredPostSize" status=1 href="javascript:">{3}</a>], 已收集: <a class="billSize" href="javascript:">{4}</a> [不可处理: {5}]', processedPostSize, insertedPostSize, updatedPostSize, ignoredPostSize, billSize, ignoredBillSize);
    } else if (3 == collectorStatus) {
      tip1 = '已完成';
      tip2 = Ext.String.format('共处理: {0} [新增: <a class="insertedPostSize" status=2 href="javascript:">{1}</a>, 更新: <a class="updatedPostSize" status=3 href="javascript:">{2}</a>, 忽略: <a class="ignoredPostSize" status=1 href="javascript:">{3}</a>], 共收集: <a class="billSize" href="javascript:">{4}</a> [不可处理: {5}]', processedPostSize, insertedPostSize, updatedPostSize, ignoredPostSize, billSize, ignoredBillSize);
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
  onViewItemClick: function() {
    this.onRefreshBtnClick();
  },
  onViewCellClick: function(table, td, cellIndex, record, tr, rowIndex, e) {
    var view = this.getView(), target = e.getTarget(), className = target.className, statusValue = target.getAttribute('status'), postStatus = '1';
    if ('billSize' == className) {
      if (!view.billWindow) {
        view.billWindow = Ext.widget('platform-posttask-bill');
      }
      view.billWindow.cid = record.get('cid');
      view.billWindow.loadData();
      view.billWindow.show();
    } else if ('insertedPostSize' == className || 'updatedPostSize' == className || 'ignoredPostSize' == className || 'processedPostSize' == className) {
      if (!view.postWindow) {
        view.postWindow = Ext.widget('platform-posttask-post');
      }
      view.postWindow.postStatusField.setValue(statusValue);
      view.postWindow.cid = record.get('cid');
      view.postWindow.loadData();
      view.postWindow.show();
    }
  },
  onRegionComboBoxChange: function(field, newValue) {
    var view = this.getView();
    var areas = field.findRecordByValue(newValue).get('areas'), areaField = view.down('#areaComboBox'), areaStore = areaField.getStore();
    var data = areas ? ['- - -'].concat(areas) : ['- - -'];
    areaStore.loadData(data.map(function(d) {
      return [d]
    }));
    areaField.setValue('- - -');
  },
  onSubmitBtnClick: function() {
    var me = this, view = me.getView(), normField = view.down('#normComboBox'), regionField = view.down('#regionComboBox'), areaField = view.down('#areaComboBox');
    if (normField.isValid() && regionField.isValid() && areaField.isValid()) {
      view.setLoading(true);
      var params = {
        norm: normField.getValue(),
        region: regionField.getValue()
      }, area = areaField.getValue();
      if ('- - -' != area) {
        params.area = area;
      }
      Ext.Ajax.request({
        async: false,
        url: 'posttask/addTask.do',
        params: params,
        callback: function(options, success, response) {
          var response = Ext.decode(response.responseText);
          if (response.success) {
            me.onRefreshBtnClick();
          } else {
            Ext.Msg.alert('提示', '已经存在！');
          }
          view.setLoading(false);
        }
      });
    }
  },
  onRefreshBtnClick: function() {
    this.getView().getStore().load();
  },
  onAddBtnClick: function() {
    var view = this.getView();
    if (!view.addWindow) {
      view.addWindow = Ext.widget('platform-posttask-add');
    }
    view.addWindow.show();
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