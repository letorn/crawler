Ext.define('Platform.view.posttask.post.detail.View', {
  extend: 'Ext.window.Window',
  xtype: 'platform-posttask-post-detail',
  uses: ['Platform.view.posttask.post.detail.ViewController'],
  controller: 'posttask-post-detail',
  title: '详情',
  closeAction: 'hide',
  resizable: false,
  layout: {
    type: 'hbox',
    align: 'stretch'
  },
  listeners: {
    show: 'onViewShow'
  },
  initComponent: function() {
    var me = this;

    me.postView = Ext.widget('form', {
      trackResetOnLoad: false,
      border: false,
      padding: '5 0 0 0',
      defaults: {
        labelAlign: 'right',
        readOnly: true,
        labelWidth: 65,
        width: 320
      },
      items: [{
        xtype: 'textfield',
        fieldLabel: '链接',
        name: 'url'
      }, {
        xtype: 'datefield',
        fieldLabel: '发布日期',
        format: 'Y-m-d',
        name: 'date'
      }, {
        xtype: 'textfield',
        fieldLabel: '职位',
        name: 'name'
      }, {
        xtype: 'textfield',
        fieldLabel: '职能',
        name: 'category'
      }, {
        xtype: 'textfield',
        fieldLabel: '招聘人数',
        name: 'numberText'
      }, {
        xtype: 'textfield',
        fieldLabel: '语言要求',
        name: 'language'
      }, {
        xtype: 'textfield',
        fieldLabel: '薪酬',
        name: 'salaryText'
      }, {
        xtype: 'textfield',
        fieldLabel: '工作经验',
        name: 'experience'
      }, {
        xtype: 'textfield',
        fieldLabel: '最低学历',
        name: 'education'
      }, {
        xtype: 'textfield',
        fieldLabel: '福利',
        name: 'welfare'
      }, {
        xtype: 'textfield',
        fieldLabel: '工作地点',
        name: 'address'
      }, {
        xtype: 'textarea',
        fieldLabel: '介绍',
        name: 'introduction',
        height: 220
      }]
    })

    me.enterpriseView = Ext.widget('form', {
      trackResetOnLoad: false,
      border: false,
      padding: '5 10 0 0',
      defaults: {
        labelAlign: 'right',
        readOnly: true,
        labelWidth: 65,
        width: 320
      },
      items: [{
        xtype: 'textfield',
        fieldLabel: '链接',
        name: 'url'
      }, {
        xtype: 'textfield',
        fieldLabel: '企业',
        name: 'name'
      }, {
        xtype: 'textfield',
        fieldLabel: '行业',
        name: 'category'
      }, {
        xtype: 'textfield',
        fieldLabel: '性质',
        name: 'nature'
      }, {
        xtype: 'textfield',
        fieldLabel: '规模',
        name: 'scale'
      }, {
        xtype: 'textfield',
        fieldLabel: '主页',
        name: 'website'
      }, {
        xtype: 'textfield',
        fieldLabel: '地址',
        name: 'address'
      }, {
        xtype: 'textarea',
        fieldLabel: '介绍',
        name: 'introduction',
        height: 220
      }]
    })

    me.items = [me.postView, me.enterpriseView];

    me.callParent();
  }
});