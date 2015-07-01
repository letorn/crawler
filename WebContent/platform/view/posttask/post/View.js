Ext.define('Platform.view.posttask.post.View', {
  extend: 'Ext.window.Window',
  xtype: 'platform-posttask-post',
  uses: ['Platform.view.posttask.post.ViewController', 'Platform.store.PostStore', 'Platform.store.PostStatusStore', 'Platform.view.posttask.post.detail.View'],
  controller: 'posttask-post',
  title: '岗位',
  closeAction: 'hide',
  width: 1000,
  height: 500,
  layout: 'fit',
  initComponent: function() {
    var me = this;

    me.postStatusField = Ext.widget('combobox', {
      displayField: 'name',
      valueField: 'value',
      queryMode: 'local',
      fieldLabel: '状态',
      labelAlign: 'right',
      labelWidth: 50,
      value: 2,
      store: Store.get('postStatusStore'),
      listeners: {
        change: 'onGridChange'
      }
    });

    me.tbar = [me.postStatusField];

    var gridStore = Store.get('postStore');

    me.grid = Ext.widget('grid', {
      store: gridStore,
      columns: [{
        xtype: 'rownumberer',
        width: 32
      }, {
        text: '链接',
        dataIndex: 'url',
        renderer: 'htmlFormatter',
        width: 160
      }, {
        xtype: 'datecolumn',
        text: '发布日期',
        dataIndex: 'date',
        format: 'Y-m-d',
        width: 100
      }, {
        text: '职位',
        dataIndex: 'name',
        renderer: 'htmlFormatter',
        width: 180
      }, {
        text: '职能',
        dataIndex: 'category',
        renderer: 'htmlFormatter',
        width: 160
      }, {
        text: '招聘人数',
        dataIndex: 'numberText',
        renderer: 'htmlFormatter',
        width: 80
      }, {
        text: '薪酬',
        dataIndex: 'salaryText',
        renderer: 'htmlFormatter',
        width: 100
      }, {
        text: '工作经验',
        dataIndex: 'experience',
        renderer: 'htmlFormatter',
        width: 80
      }, {
        text: '最低学历',
        dataIndex: 'education',
        renderer: 'htmlFormatter',
        width: 80
      }, {
        text: '福利',
        dataIndex: 'welfare',
        renderer: 'htmlFormatter',
        width: 120
      }, {
        text: '工作地点',
        dataIndex: 'address',
        renderer: 'htmlFormatter',
        width: 80
      }],
      bbar: Ext.widget('pagingtoolbar', {
        store: gridStore,
        displayInfo: true,
        displayMsg: '显示 {0} - {1} / 共 {2} 条'
      }),
      listeners: {
        itemdblclick: 'onGridItemDblClick'
      }
    });

    me.items = [me.grid];

    me.callParent();
  },
  loadData: function() {
    var me = this, cid = me.cid, postStatus = me.postStatusField.getValue(), gridStore = me.grid.getStore();
    gridStore.proxy.extraParams = {
      cid: cid,
      postStatus: postStatus
    };
    gridStore.loadPage(1);
  }
});