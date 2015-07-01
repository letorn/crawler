Ext.define('Platform.store.PostStore', {
  extend: 'Ext.data.Store',
  xtype: 'postStore',
  storeId: 'postStore',
  pageSize: 50,
  fields: ['url', 'date', 'name', 'category', 'numberText', 'nature', 'salaryText', 'experience', 'education', 'welfare', 'address', 'introduction'],
  proxy: {
    type: 'ajax',
    url: 'posttask/pagedPost.do',
    extraParams: {},
    reader: {
      type: 'json',
      root: 'data',
      totalProperty: 'total'
    }
  }
});