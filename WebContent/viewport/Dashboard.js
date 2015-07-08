Ext.define('Platform.viewport.Dashboard', {
  extend: 'Ext.panel.Panel',
  xtype: 'platform-viewport-dashboard',
  region: 'west',
  width: 180,
  title: '菜单',
  collapsible: true,
  collapseFirst: false,
  rootVisible: false,
  initComponent: function() {
    var me = this;

    me.store = Store.create({
      type: 'array',
      fields: ['view', 'text'],
      data: function() {
        var data = [];
        Ext.Object.each(dashboard, function(view, text) {
          data.push([view, text]);
        });
        return data;
      }.call()
    });

    me.view = Ext.widget('dataview', {
      store: me.store,
      itemSelector: 'div.dashboard-item',
      selectedItemCls: 'dashboard-item-selected',
      focusCls: '',
      itemTpl: new Ext.XTemplate('<div class="dashboard-item"><span class="image"></span><span class="text">{text}</span></div>'),
      listeners: {
        selectionchange: Ext.bind(me.onSelectionChange, me)
      }
    });

    me.items = me.view;

    me.callParent();
  },
  select: function(index) {
    this.view.getSelectionModel().select(index);
  },
  onSelectionChange: function(model, records) {
    this.ownerCt.viewer.setActiveView(records[0].get('view'), records[0].get('text'));
  }
});