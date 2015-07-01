Ext.define('Platform.view.posttask.post.detail.ViewController', {
  extend: 'Ext.app.ViewController',
  alias: 'controller.posttask-post-detail',
  onViewShow: function() {
    var view = this.getView();
    view.postView.reset();
    view.enterpriseView.reset();
    Ext.Ajax.request({
      async: false,
      url: 'posttask/postDetail.do',
      params: {
        cid: view.cid,
        url: view.url
      },
      callback: function(options, success, response) {
        var response = Ext.decode(response.responseText);
        if (response.success) {
          response.post.date = new Date(response.post.date);
          view.postView.getForm().setValues(response.post);
          view.enterpriseView.getForm().setValues(response.enterprise);
        }
      }
    })
  }
});