Ext.define('Platform.view.posttask.bill.ViewController', {
  extend: 'Ext.app.ViewController',
  alias: 'controller.posttask-bill',
  htmlFormatter: function(value, metaData, record, rowIndex, colIndex, store, el, e) {
    value = Ext.String.htmlEncode(value);
    metaData.tdAttr = 'data-qtip="' + value + '"';
    return value;
  },
  statusRenderer: function(value) {
    var status = '未知';
    if (0 == value) {
      status = '未处理';
    } else if (1 == value) {
      status = '已忽略';
    } else if (2 == value) {
      status = '已处理';
    }
    return status;
  }
});