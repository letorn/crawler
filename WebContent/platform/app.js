Ext.Ajax.request({
  url: baseDir + '/platform/views.store',
  async: false,
  success: function(response, options) {
    Views = eval('(' + response.responseText + ')');
  }
});

Ext.application({
  extend: 'Platform.Application',
  appFolder: '.',
  name: 'Platform'
});