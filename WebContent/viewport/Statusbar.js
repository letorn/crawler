Ext.define('Platform.viewport.Statusbar', {
  extend: 'Ext.toolbar.Toolbar',
  xtype: 'platform-viewport-statusbar',
  region: 'south',
  initComponent: function() {
    var me = this;

    me.items = [{
      xtype: 'label',
      text: '状态: 拼命开发中......'
    }]

    me.callParent();
  }
});