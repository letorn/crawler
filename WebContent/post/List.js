Ext.define('Platform.post.List', {
  extend: 'Ext.grid.Panel',
  xtype: 'platform-post-list',
  uses: ['Platform.posttask.PostDetail'],
  title: '列表',
  initComponent: function() {
    var me = this;

    me.tbar = [{
      itemId: 'postNameKey',
      xtype: 'textfield'
    }, {
      xtype: 'button',
      text: '查询',
      handler: Ext.bind(me.loadData, me)
    }];

    me.store = Store.create({
      pageSize: 50,
      fields: ['dataUrl', {
        name: 'updateDate',
        convert: function(value) {
          return Ext.Date.parse(value, 'Y-m-d H:i:s')
        }
      }, 'name', 'category', 'numberText', 'nature', 'salaryText', 'experience', 'education', 'welfare', 'address', 'introduction'],
      proxy: {
        type: 'ajax',
        url: ctx + '/posttask/pagedPost.do',
        extraParams: {},
        reader: {
          type: 'json',
          root: 'data',
          totalProperty: 'total'
        }
      }
    });

    me.columns = [{
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
    }];

    me.bbar = {
      xtype: 'pagingtoolbar',
      store: me.store,
      displayInfo: true,
      displayMsg: '显示 {0} - {1} / 共 {2} 条'
    };

    me.listeners = {
      afterrender: me.loadData,
      itemdblclick: me.onItemDblClick
    };

    me.callParent();
  },
  loadData: function() {
    var me = this, postNameKeyField = me.down('#postNameKey');
    me.store.proxy.extraParams = {
      name: postNameKeyField.getValue()
    };
    me.store.loadPage(1);
  },
  columnFormatter: function(value, metaData, record, rowIndex, colIndex, store, el, e) {
    if (value != null) {
      value = Ext.String.htmlEncode(value);
      metaData.tdAttr = 'data-qtip="' + value + '"';
      return value;
    }
    return "";
  },
  onItemDblClick: function(gridview, record, item, index) {
    var me = this;
    if (!me.detailWindow) {
      me.detailWindow = Platform.widget('post-map-marker-detail');
      me.detailWindow.postGridStore = me.store;
    }
    me.detailWindow.loadData(record.get('dataUrl'));
    me.detailWindow.show();
  }
});