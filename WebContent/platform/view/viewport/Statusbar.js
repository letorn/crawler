Ext.define('Platform.view.viewport.Statusbar', {
  extend: 'Ext.toolbar.Toolbar',
  xtype: 'platform-viewport-statusbar',
  uses: ['Platform.view.viewport.ViewController'],
  controller: 'viewport',
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