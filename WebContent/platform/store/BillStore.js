Ext.define('Platform.store.BillStore', {
  extend: 'Ext.data.Store',
  xtype: 'billStore',
  storeId: 'billStore',
  pageSize: 50,
  fields: ['date', 'postURL', 'postName', 'enterpriseURL', 'enterpriseName', 'status'],
  proxy: {
    type: 'ajax',
    url: 'posttask/pagedBill.do',
    extraParams: {},
    reader: {
      type: 'json',
      root: 'data',
      totalProperty: 'total'
    }
  }
});