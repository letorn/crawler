Ext.define('Platform.store.PostTaskStore', {
  extend: 'Ext.data.Store',
  xtype: 'postTaskStore',
  storeId: 'postTaskStore',
  fields: ['cid', 'norm', 'region', 'area', 'ignoredBillSize', 'billSize', 'ignoredPostSize', 'insertedPostSize', 'updatedPostSize', 'postSize', 'explorerStatus', 'collectorStatus'],
  proxy: {
    type: 'ajax',
    url: 'posttask/tasks.do',
    reader: {
      type: 'json',
      root: 'data'
    }
  }
});