Ext.define('Platform.Application', {
  extend: 'Ext.app.Application',
  appFolder: '.',
  name: 'Platform',
  views: function() {
    var views = ['viewport.View'], suffix = '.View';
    Ext.Object.each(Views, function(view, text) {
      views.push(view + suffix);
    });
    return views;
  }.call(),
  launch: function() {
    Platform.widget('viewport');
  }
});