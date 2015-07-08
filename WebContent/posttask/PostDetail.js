Ext.define('Platform.posttask.PostDetail', {
  extend: 'Ext.window.Window',
  xtype: 'platform-posttask-post-detail',
  title: '详情',
  closeAction: 'hide',
  resizable: false,
  layout: {
    type: 'hbox',
    align: 'stretch'
  },
  initComponent: function() {
    var me = this;

    me.postView = Ext.widget('form', {
      trackResetOnLoad: false,
      border: false,
      padding: '5 0 0 0',
      defaults: {
        labelAlign: 'right',
        labelWidth: 65,
        width: 320
      },
      items: [{
        xtype: 'textfield',
        fieldLabel: '链接',
        name: 'url',
        readOnly: true
      }, {
        xtype: 'datefield',
        fieldLabel: '发布日期',
        format: 'Y-m-d',
        name: 'date',
        readOnly: true
      }, {
        xtype: 'textfield',
        fieldLabel: '职位',
        name: 'name'
      }, {
        itemId: 'categoryComboBox',
        xtype: 'combobox',
        fieldLabel: '职能',
        name: 'categoryCode',
        displayField: 'name',
        valueField: 'code',
        matchFieldWidth: false,
        editable: false,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name', 'group']
        }),
        tpl: ['<tpl for=".">', '<tpl if="this.group != values.group">', '<tpl exec="this.group = values.group, this.itemSize = 0"></tpl>', '<hr><div style="color:gray;">{group}</div>', '</tpl>', '<tpl exec="this.itemSize ++"></tpl>', '<tpl if="this.itemSize &gt; 7">', '<tpl exec="this.itemSize = 0"></tpl>', '<br />', '</tpl>', '<span class="x-boundlist-item">{name}</span>', '</tpl>']
      }, {
        xtype: 'textfield',
        fieldLabel: '招聘人数',
        name: 'numberText'
      }, {
        xtype: 'textfield',
        fieldLabel: '薪酬',
        name: 'salaryText'
      }, {
        itemId: 'experienceComboBox',
        xtype: 'combobox',
        fieldLabel: '工作经验',
        name: 'experienceCode',
        displayField: 'name',
        valueField: 'code',
        editable: false,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name']
        })
      }, {
        itemId: 'educationComboBox',
        xtype: 'combobox',
        fieldLabel: '最低学历',
        name: 'educationCode',
        displayField: 'name',
        valueField: 'code',
        editable: false,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name']
        })
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
        labelWidth: 65,
        width: 320
      },
      items: [{
        xtype: 'textfield',
        fieldLabel: '链接',
        name: 'url',
        readOnly: true
      }, {
        xtype: 'textfield',
        fieldLabel: '企业',
        name: 'name'
      }, {
        itemId: 'categoryComboBox',
        xtype: 'combobox',
        fieldLabel: '行业',
        name: 'categoryCode',
        displayField: 'name',
        valueField: 'code',
        editable: false,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name']
        })
      }, {
        itemId: 'natureComboBox',
        xtype: 'combobox',
        fieldLabel: '性质',
        name: 'natureCode',
        displayField: 'name',
        valueField: 'code',
        editable: false,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name']
        })
      }, {
        itemId: 'scaleComboBox',
        xtype: 'combobox',
        fieldLabel: '规模',
        name: 'scaleCode',
        displayField: 'name',
        valueField: 'code',
        editable: false,
        queryMode: 'local',
        store: Store.create({
          fields: ['code', 'name']
        })
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

    me.buttons = [{
      text: '确定',
      handler: Ext.bind(me.submitBtnClick, me)
    }, {
      text: '取消',
      handler: Ext.bind(me.cancelBtnClick, me)
    }];

    me.callParent();
  },
  loadCodes: function() {
    var me = this, postView = me.postView, enterpriseView = me.enterpriseView;
    if (!me.inited) {
      var postCategoryComboBox = postView.down('#categoryComboBox'), postExperienceComboBox = postView.down('#experienceComboBox'), postEducationComboBox = postView.down('#educationComboBox');
      var enterpriseCategoryComboBox = enterpriseView.down('#categoryComboBox'), enterpriseNatureComboBox = enterpriseView.down('#natureComboBox'), enterpriseScaleComboBox = enterpriseView.down('#scaleComboBox');
      Ext.Ajax.request({
        async: false,
        url: ctx + '/posttask/codes.do',
        callback: function(options, success, response) {
          var response = Ext.decode(response.responseText);
          if (response.success) {
            me.inited = true;
            postCategoryComboBox.getStore().loadData(response.postCategories);
            postExperienceComboBox.getStore().loadData(response.postExperiences);
            postEducationComboBox.getStore().loadData(response.postEducations);
            enterpriseCategoryComboBox.getStore().loadData(response.enterpriseCategories);
            enterpriseNatureComboBox.getStore().loadData(response.enterpriseNatures);
            enterpriseScaleComboBox.getStore().loadData(response.enterpriseScales);
          }
        }
      })
    }
  },
  loadData: function(cid, url) {
    var me = this;
    me.loadCodes();
    me.postView.reset();
    me.enterpriseView.reset();
    Ext.Ajax.request({
      async: false,
      url: ctx + '/posttask/postDetail.do',
      params: {
        cid: cid,
        url: url
      },
      callback: function(options, success, response) {
        var response = Ext.decode(response.responseText);
        if (response.success) {
          response.post.date = new Date(response.post.date);
          me.postView.getForm().setValues(response.post);
          me.enterpriseView.getForm().setValues(response.enterprise);
        }
      }
    })
  },
  submitBtnClick: function() {
    var me = this, postView = me.postView, enterpriseView = me.enterpriseView;
    me.setLoading(true);
    var map = {
      post: postView.getValues(),
      enterprise: enterpriseView.getValues()
    };
    Ext.Ajax.request({
      async: false,
      url: ctx + '/posttask/savePost.do',
      jsonData: map,
      callback: function(options, success, response) {
        var response = Ext.decode(response.responseText);
        if (response.success) {
          response.post.date = new Date(response.post.date);
          me.postView.getForm().setValues(response.post);
          me.enterpriseView.getForm().setValues(response.enterprise);
        }
        me.setLoading(false);
      }
    })
  },
  cancelBtnClick: function() {
    this.close();
  }
});