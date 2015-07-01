Ext.define('Platform.store.DashboardStore', {
  extend: 'Ext.data.ArrayStore',
  xtype: 'dashboardStore',
  storeId: 'dashboardStore',
  fields: ['view', 'text'],
  data: function() {
    var data = [];
    Ext.Object.each(Views, function(view, text) {
      data.push([view, text]);
    });
    return data;
  }.call()
});