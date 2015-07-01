Ext.define('Platform.store.EnterpriseStore', {
  extend: 'Ext.data.Store',
  xtype: 'enterpriseStore',
  storeId: 'enterpriseStore',
  pageSize: 50,
  fields: ['url', 'name', 'category', 'nature', 'scale', 'website', 'address', 'introduction'],
  proxy: {
    type: 'ajax',
    url: 'posttask/pagedEnterprise.do',
    extraParams: {},
    reader: {
      type: 'json',
      root: 'data',
      totalProperty: 'total'
    }
  }
});