Ext.define('Platform.view.viewport.ViewController', {
  extend: 'Ext.app.ViewController',
  alias: 'controller.viewport',
  onViewAfterRender: function(view) {
    view.dashboard.select(0);
  },
  onDashboardSelectionChange: function(model, records) {
    var dashboard = this.getView(), viewport = dashboard.ownerCt, viewer = viewport.viewer;
    viewer.setActiveView(records[0].get('view'));
  }
});