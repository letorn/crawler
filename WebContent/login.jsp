<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/public/root.jsp"%>
<html>
<head>
<meta charset="UTF-8">
<title>登录</title>
<script type="text/javascript" src="${ctx}/public/ext/bootstrap.js"></script>
<script type="text/javascript">
  Ext.onReady(function() {
    var formpanel = Ext.widget('form', {
      border: false,
      margin: '7 10 3 5',
      defaults: {
        labelAlign: 'right',
        labelWidth: 40,
        width: 250
      },
      items: [{
        xtype: 'textfield',
        fieldLabel: '账号',
        name: 'name',
        allowBlank: false
      }, {
        xtype: 'textfield',
        inputType: 'password',
        fieldLabel: '密码',
        name: 'password',
        allowBlank: false
      }]
    });
    Ext.create('Ext.window.Window', {
      title: '登录',
      closable: false,
      modal: true,
      items: [formpanel],
      buttons: [{
        text: '确定',
        handler: function() {
          if (formpanel.isValid()) {
            formpanel.submit({
              url: ctx + '/platform/login.do',
              success: function(form, action) {
                window.location = ctx + "/platform/index.jsp"
              },
              failure: function(form, action) {
                Ext.toast({
                  title: '提示',
                  html: '登录失败，账号或密码错误',
                  align: 't',
                  slideInDuration: 100,
                  slideBackDuration: 800,
                  hideDuration: 100,
                  autoCloseDelay: 1000,
                });
              }
            });
          }
        }
      }]
    }).show();
  });
</script>
</head>
<body>
</body>
</html>