Ext.define('Platform.store.PostStatusStore', {
  extend: 'Ext.data.ArrayStore',
  xtype: 'postStatusStore',
  storeId: 'postStatusStore',
  fields: ['name', 'value'],
  data: [['新增', 2], ['更新', 3], ['忽略', 1]]
});