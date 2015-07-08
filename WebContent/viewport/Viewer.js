Ext.define('Platform.viewport.Viewer', {
  extend: 'Ext.panel.Panel',
  xtype: 'platform-viewport-viewer',
  region: 'center',
  layout: 'card',
  border: false,
  setActiveView: function(viewId, text) {
    var me = this, view = me.queryById(viewId);
    if (view) {
      me.setActiveItem(view);
    } else {
      try {
        view = Platform.widget(viewId, {
          itemId: viewId,
          title: text
        });
        me.setActiveItem(view);
      } catch (e) {
        console.log(e);
      }
    }
  }
});