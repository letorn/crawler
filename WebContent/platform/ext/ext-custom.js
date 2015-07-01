Ext.define('Store', {
  statics: {
    get: function(storeId) {
      return Ext.getStore(storeId) || Ext.widget(storeId);
    }
  }
});

Ext.define('Platform', {
  statics: {
    prefix: 'platform-',
    views: {},
    widget: function(xtype, config) {
      var view = Ext.widget(this.prefix + xtype, config);
      this.views[xtype] = view;
      return view;
    },
    get: function(xtype) {
      return this.views[xtype];
    }
  }
});