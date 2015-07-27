Ext.define('Platform.posttask.Bill', {
  extend: 'Ext.window.Window',
  xtype: 'platform-posttask-bill',
  title: '清单',
  closeAction: 'hide',
  resizable: false,
  width: 1000,
  height: 500,
  layout: 'fit',
  initComponent: function() {
    var me = this;

    me.gridStore = Store.create({
      pageSize: 50,
      fields: [{
        name: 'date',
        convert: function(value) {
          return Ext.Date.parse(value, 'Y-m-d H:i:s')
        }
      }, 'postUrl', 'postName', 'enterpriseUrl', 'enterpriseName', 'status'],
      proxy: {
        type: 'ajax',
        url: ctx + '/posttask/pagedBill.do',
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
        store: me.gridStore,
        displayInfo: true,
        displayMsg: '显示 {0} - {1} / 共 {2} 条'
      })
    });

    me.items = [me.gridPanel];

    me.callParent();
  },
  loadData: function(cid) {
    var me = this;
    if (cid !== undefined) {
      me.cid = cid;
    }
    me.gridStore.proxy.extraParams = {
      cid: me.cid
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