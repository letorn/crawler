Ext.define('Platform.store.NormStore', {
  extend: 'Ext.data.Store',
  xtype: 'normStore',
  storeId: 'normStore',
  fields: ['name'],
  sorters: ['name'],
  proxy: {
    type: 'ajax',
    url: 'posttask/norms.do',
    reader: {
      type: 'json',
      root: 'data'
    }
  }
});