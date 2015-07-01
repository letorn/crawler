Ext.define('Platform.view.posttask.bill.View', {
  extend: 'Ext.window.Window',
  xtype: 'platform-posttask-bill',
  uses: ['Platform.view.posttask.bill.ViewController', 'Platform.store.BillStore'],
  controller: 'posttask-bill',
  title: '清单',
  closeAction: 'hide',
  width: 1000,
  height: 500,
  layout: 'fit',
  initComponent: function() {
    var me = this;

    var gridStore = Store.get('billStore');
    me.grid = Ext.widget('grid', {
      store: gridStore,
      columns: [{
        xtype: 'rownumberer',
        width: 32
      }, {
        xtype: 'datecolumn',
        text: '发布日期',
        dataIndex: 'date',
        format: 'Y-m-d',
        width: 100
      }, {
        text: '职位链接',
        dataIndex: 'postURL',
        renderer: 'htmlFormatter',
        width: 280
      }, {
        text: '职位名称',
        dataIndex: 'postName',
        renderer: 'htmlFormatter',
        width: 220
      }, {
        text: '公司链接',
        dataIndex: 'enterpriseURL',
        renderer: 'htmlFormatter',
        width: 320
      }, {
        text: '公司名称',
        dataIndex: 'enterpriseName',
        renderer: 'htmlFormatter',
        width: 240
      }, {
        text: '状态',
        dataIndex: 'status',
        renderer: 'statusRenderer',
        width: 80
      }],
      bbar: Ext.widget('pagingtoolbar', {
        store: gridStore,
        displayInfo: true,
        displayMsg: '显示 {0} - {1} / 共 {2} 条'
      })
    });

    me.items = [me.grid];

    me.callParent();
  },
  loadData: function() {
    var me = this, cid = me.cid, gridStore = me.grid.getStore();
    gridStore.proxy.extraParams = {
      cid: cid
    };
    gridStore.loadPage(1);
  }
});