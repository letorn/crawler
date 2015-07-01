Ext.define('Platform.view.posttask.post.ViewController', {
  extend: 'Ext.app.ViewController',
  alias: 'controller.posttask-post',
  onGridChange: function() {
    this.getView().loadData();
  },
  htmlFormatter: function(value, metaData, record, rowIndex, colIndex, store, el, e) {
    value = Ext.String.htmlEncode(value);
    metaData.tdAttr = 'data-qtip="' + value + '"';
    return value;
  },
  onGridItemDblClick: function(gridview, record, item, index) {
    var view = this.getView();
    if (!view.detailWindow) {
      view.detailWindow = Ext.widget('platform-posttask-post-detail');
    }
    view.detailWindow.cid = view.cid;
    view.detailWindow.url = record.get('url');
    view.detailWindow.show();
  }
});