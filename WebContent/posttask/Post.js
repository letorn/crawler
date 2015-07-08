Ext.define('Platform.posttask.Post', {
  extend: 'Ext.window.Window',
  xtype: 'platform-posttask-post',
  uses: ['Platform.posttask.PostDetail'],
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
      store: Store.create({
        type: 'array',
        fields: ['name', 'value'],
        data: [['新增', 2], ['更新', 3], ['忽略', 1], ['不可处理', -1]]
      }),
      listeners: {
        change: Ext.bind(me.onStatusComboBoxChange, me)
      }
    });

    me.tbar = [me.postStatusField];

    var gridStore = Store.create({
      fields: ['url', 'date', 'name', 'category', 'numberText', 'nature', 'salaryText', 'experience', 'education', 'welfare', 'address', 'introduction'],
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

    me.grid = Ext.widget('grid', {
      store: gridStore,
      columns: [{
        xtype: 'rownumberer',
        width: 32
      }, {
        text: '链接',
        dataIndex: 'url',
        renderer: Ext.bind(me.columnFormatter, me),
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
        store: gridStore,
        displayInfo: true,
        displayMsg: '显示 {0} - {1} / 共 {2} 条'
      }),
      listeners: {
        itemdblclick: Ext.bind(me.onGridItemDblClick, me)
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
  },
  onStatusComboBoxChange: function() {
    this.loadData();
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
      me.detailWindow = Platform.widget('posttask-post-detail');
    }
    me.detailWindow.loadData(me.cid, record.get('url'));
    me.detailWindow.show();
  }
});