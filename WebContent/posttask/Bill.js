Ext.define('Platform.posttask.Bill', {
  extend: 'Ext.window.Window',
  xtype: 'platform-posttask-bill',
  title: '清单',
  closeAction: 'hide',
  width: 1000,
  height: 500,
  layout: 'fit',
  initComponent: function() {
    var me = this;

    var gridStore = Store.create({
      pageSize: 50,
      fields: ['date', 'postUrl', 'postName', 'enterpriseUrl', 'enterpriseName', 'status'],
      proxy: {
        type: 'ajax',
        url: 'posttask/pagedBill.do',
        extraParams: {},
        reader: {
          type: 'json',
          root: 'data',
          totalProperty: 'total'
        }
      }
    });

    me.gridPanel = Ext.widget('grid', {
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
        dataIndex: 'postUrl',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 280
      }, {
        text: '职位名称',
        dataIndex: 'postName',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 220
      }, {
        text: '公司链接',
        dataIndex: 'enterpriseUrl',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 320
      }, {
        text: '公司名称',
        dataIndex: 'enterpriseName',
        renderer: Ext.bind(me.columnFormatter, me),
        width: 240
      }, {
        text: '状态',
        dataIndex: 'status',
        renderer: Ext.bind(me.statusColumnRenderer, me),
        width: 80
      }],
      bbar: Ext.widget('pagingtoolbar', {
        store: gridStore,
        displayInfo: true,
        displayMsg: '显示 {0} - {1} / 共 {2} 条'
      })
    });

    me.items = [me.gridPanel];

    me.callParent();
  },
  loadData: function() {
    var me = this, cid = me.cid, gridStore = me.gridPanel.getStore();
    gridStore.proxy.extraParams = {
      cid: cid
    };
    gridStore.loadPage(1);
  },
  columnFormatter: function(value, metaData, record, rowIndex, colIndex, store, el, e) {
    if (value != null) {
      value = Ext.String.htmlEncode(value);
      metaData.tdAttr = 'data-qtip="' + value + '"';
      return value;
    }
    return "";
  },
  statusColumnRenderer: function(value) {
    var status = '未知';
    if (value == 0) {
      status = '未处理';
    } else if (value == 1) {
      status = '已忽略';
    } else if (value == 2) {
      status = '已处理';
    }
    return status;
  }
});