Ext.define('Platform.post.View', {
  extend: 'Ext.tab.Panel',
  xtype: 'platform-post',
  uses: ['Platform.post.Map'],
  title: '所有岗位',
  initComponent: function() {
    var me = this;

    me.items = [Platform.widget('post-map')];

    me.callParent();
  }
});