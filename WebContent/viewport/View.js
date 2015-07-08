Ext.define('Platform.viewport.View', {
  extend: 'Ext.container.Viewport',
  xtype: 'platform-viewport',
  layout: 'border',
  uses: ['Platform.viewport.Dashboard', 'Platform.viewport.Viewer', 'Platform.viewport.Statusbar'],
  initComponent: function() {
    var me = this;

    me.dashboard = Platform.widget('viewport-dashboard');
    me.viewer = Platform.widget('viewport-viewer');
    me.statusbar = Platform.widget('viewport-statusbar');

    me.items = [me.dashboard, me.viewer, me.statusbar];

    me.listeners = {
      afterrender: me.onAfterRender
    };

    me.callParent();
  },
  onAfterRender: function() {
    this.dashboard.select(0);
  }
});