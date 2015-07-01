Ext.define('Platform.view.viewport.Dashboard', {
  extend: 'Ext.panel.Panel',
  xtype: 'platform-viewport-dashboard',
  uses: ['Platform.view.viewport.ViewController', 'Platform.store.DashboardStore'],
  controller: 'viewport',
  region: 'west',
  width: 180,
  title: '菜单',
  collapsible: true,
  rootVisible: false,
  initComponent: function() {
    var me = this;

    me.store = Store.get('dashboardStore');

    var view = Ext.widget('dataview', {
      store: me.store,
      itemSelector: 'div.dashboard-item',
      selectedItemCls: 'dashboard-item-selected',
      focusCls: '',
      itemTpl: new Ext.XTemplate('<div class="dashboard-item"><span class="image"></span><span class="text">{text}</span></div>'),
      listeners: {
        selectionchange: 'onDashboardSelectionChange'
      }
    });

    me.items = me.view = view;

    me.callParent();
  },
  select: function(index) {
    this.view.getSelectionModel().select(index);
  }
});