Ext.define('Platform.view.posttask.View', {
  extend: 'Ext.grid.Panel',
  xtype: 'platform-posttask',
  uses: ['Platform.view.posttask.ViewController', 'Platform.store.NormStore', 'Platform.store.RegionStore', 'Platform.store.AreaStore', 'Platform.store.PostTaskStore', 'Platform.view.posttask.bill.View', 'Platform.view.posttask.post.View'],
  controller: 'posttask',
  title: '岗位作业',
  listeners: {
    afterrender: 'onViewAfterRender',
    itemclick: 'onViewItemClick',
    cellclick: 'onViewCellClick'
  },
  initComponent: function() {
    var me = this;

    me.store = Store.get('postTaskStore');
    me.normStore = Store.get('normStore');

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
      store: me.normStore
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
      store: Store.get('regionStore'),
      listeners: {
        change: 'onRegionComboBoxChange'
      }
    }, {
      itemId: 'areaComboBox',
      xtype: 'combobox',
      displayField: 'name',
      valueField: 'name',
      queryMode: 'local',
      editable: false,
      store: Store.get('areaStore')
    }, {
      text: '确定',
      iconCls: 'add',
      handler: 'onSubmitBtnClick'
    }, '-', {
      text: '刷新',
      iconCls: 'refresh',
      handler: 'onRefreshBtnClick'
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
      renderer: 'statusRenderer',
      width: 480
    }, {
      xtype: 'actioncolumn',
      text: '操作',
      width: 120,
      items: [{
        iconCls: 'start',
        tooltip: '启动',
        handler: 'onStartBtnClick'
      }, '-', {
        iconCls: 'pause',
        tooltip: '暂停',
        handler: 'onPauseBtnClick'
      }, '-', {
        iconCls: 'stop',
        tooltip: '停止',
        handler: 'onStopBtnClick'
      }, '-', {
        iconCls: 'delete',
        tooltip: '删除',
        handler: 'onDeleteBtnClick'
      }]
    }];

    me.callParent();
  }
});