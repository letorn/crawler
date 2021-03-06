Ext.define('Platform.posttask.Post', {
  extend: 'Ext.window.Window',
  xtype: 'platform-posttask-post',
  uses: ['Platform.posttask.PostDetail'],
  title: '岗位',
  closeAction: 'hide',
  resizable: false,
  width: 1000,
  height: 500,
  layout: 'fit',
  initComponent: function() {
    var me = this;

    me.tbar = [{
      itemId: 'postStatusComboBox',
      xtype: 'combobox',
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
    }];

    me.gridStore = Store.create({
      fields: ['dataUrl', {
        name: 'updateDate',
        convert: function(value) {
          return Ext.Date.parse(value, "Y-m-d H:i:s")
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
  loadData: function(cid, statusValue) {
    var me = this, postStatusComboBox = me.down('#postStatusComboBox');
    if (cid !== undefined) {
      me.cid = cid;
    }
    if (statusValue !== undefined) {
      postStatusComboBox.suspendEvent('change');
      postStatusComboBox.setValue(statusValue);
      postStatusComboBox.resumeEvent('change');
    }
    me.gridStore.proxy.extraParams = {
      cid: me.cid,
      postStatus: postStatusComboBox.getValue()
    };
    me.gridStore.loadPage(1);
  },
  onStatusComboBoxChange: function(field, newValue) {
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
      me.detailWindow.postGridStore = me.gridStore;
    }
    me.detailWindow.loadData(me.cid, record.get('dataUrl'));
    me.detailWindow.show();
  }
});