Ext.define('Platform.view.viewport.Viewer', {
  extend: 'Ext.panel.Panel',
  xtype: 'platform-viewport-viewer',
  uses: ['Platform.view.viewport.ViewController'],
  controller: 'viewport',
  region: 'center',
  layout: 'card',
  border: false,
  setActiveView: function(viewId) {
    var me = this, view = me.queryById(viewId);
    if (!view) {
      view = Platform.widget(viewId, {
        itemId: viewId
      });
    }
    me.setActiveItem(view);
  }
});