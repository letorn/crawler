Ext.define('Platform.view.viewport.View', {
  extend: 'Ext.container.Viewport',
  xtype: 'platform-viewport',
  layout: 'border',
  uses: ['Platform.view.viewport.ViewController', 'Platform.view.viewport.Dashboard', 'Platform.view.viewport.Viewer', 'Platform.view.viewport.Statusbar'],
  controller: 'viewport',
  listeners: {
    afterrender: 'onViewAfterRender'
  },
  initComponent: function() {
    var me = this;

    me.dashboard = Platform.widget('viewport-dashboard');
    me.viewer = Platform.widget('viewport-viewer');
    me.statusbar = Platform.widget('viewport-statusbar');

    me.items = [me.dashboard, me.viewer, me.statusbar];

    me.callParent();
  }
});