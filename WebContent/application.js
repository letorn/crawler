var dashboard = {
  post: '所有岗位',
  posttask: '岗位作业'
};

Ext.application({
  appFolder: '.',
  viewFolder: '.',
  name: 'Platform',
  views: function() {
    var views = ['viewport.View'], suffix = '.View';
    Ext.Object.each(dashboard, function(view, text) {
      views.push(view + suffix);
    });
    return views;
  }.call(),
  launch: function() {
    Platform.widget('viewport');
  }
});

function decodeResponse(response) {
  if (response.status != 200) {
    Ext.Msg.alert('提示', '系统异常，请重试！');
    return false;
  }
  var contentType = response.getResponseHeader('content-type').toLowerCase();
  if (contentType == 'application/json;charset=utf-8') {
    var object = Ext.decode(response.responseText);
    if (object.success) {
      return object;
    } else {
      return false;
    }
  }
  if (contentType == 'text/html;charset=utf-8') {
    var html = response.responseText;
    if (html.includes('<title>登录</title>')) {
      var loginWindow = Platform.get('loginwindow');
      if (!loginWindow) {
        loginWindow = Platform.widget('loginwindow');
      }
      loginWindow.requestOptions = response.request.options;
      loginWindow.show();
    }
  }
}

Ext.define('Platform.LoginWindow', {
  extend: 'Ext.window.Window',
  xtype: 'platform-loginwindow',
  title: '登录',
  closable: false,
  closeAction: 'hide',
  modal: true,
  initComponent: function() {
    var me = this;

    me.formpanel = Ext.widget('form', {
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

    me.items = [me.formpanel];

    me.buttons = [{
      text: '确定',
      handler: Ext.bind(me.onSubmitBtnClick, me)
    }]

    me.callParent();
  },
  onSubmitBtnClick: function() {
    var me = this, formpanel = me.formpanel;
    if (formpanel.isValid()) {
      formpanel.submit({
        url: ctx + '/platform/login.do',
        success: function(form, action) {
          me.close();
          Ext.Ajax.request(me.requestOptions);
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
});

var regions = [['北京'], ['上海'], ['深圳'], ['天津'], ['重庆'], ['广东', ['广州', '惠州', '汕头', '珠海', '佛山', '中山', '东莞', '韶关', '江门', '湛江', '肇庆', '清远', '潮州', '河源', '揭阳', '茂名', '汕尾', '顺德', '梅州', '开平', '阳江', '云浮']], ['江苏', ['南京', '苏州', '无锡', '常州', '昆山', '常熟', '扬州', '南通', '镇江', '徐州', '连云港', '盐城', '张家港', '太仓', '泰州', '淮安', '宿迁', '丹阳', '泰兴', '靖江']], ['浙江', ['杭州', '宁波', '温州', '绍兴', '金华', '嘉兴', '台州', '湖州', '丽水', '舟山', '衢州', '义乌', '海宁']], ['四川', ['成都', '绵阳', '乐山', '泸州', '德阳', '宜宾', '自贡', '内江', '攀枝花', '南充', '眉山', '广安', '资阳', '遂宁', '广元', '达州', '雅安', '西昌', '巴中', '甘孜', '阿坝', '凉山']], ['海南', ['海口', '三亚', '洋浦经济开发区', '文昌', '琼海', '万宁', '儋州', '东方', '五指山', '定安', '屯昌', '澄迈', '临高', '三沙']], ['福建', ['福州', '厦门', '泉州', '漳州', '莆田', '三明', '南平', '宁德', '龙岩']], ['山东', ['济南', '青岛', '烟台', '潍坊', '威海', '淄博', '临沂', '济宁', '东营', '泰安', '日照', '德州', '菏泽', '滨州', '枣庄', '聊城', '莱芜']], ['江西', ['南昌', '九江', '景德镇', '萍乡', '新余', '鹰潭', '赣州', '吉安', '宜春', '抚州', '上饶']], ['广西', ['南宁', '桂林', '柳州', '北海', '玉林', '梧州', '防城港', '钦州', '贵港', '百色', '河池', '来宾', '崇左', '贺州']], ['安徽', ['合肥', '芜湖', '安庆', '马鞍山', '蚌埠', '阜阳', '铜陵', '滁州', '黄山', '淮南', '六安', '宣城', '池州', '宿州', '淮北', '亳州']], ['河北', ['石家庄', '廊坊', '保定', '唐山', '秦皇岛', '邯郸', '沧州', '张家口', '承德', '邢台', '衡水', '燕郊开发区']], ['河南', ['郑州', '洛阳', '开封', '焦作', '南阳', '新乡', '周口', '安阳', '平顶山', '许昌', '信阳', '商丘', '驻马店', '漯河', '濮阳', '鹤壁', '三门峡', '济源', '邓州']], ['湖北', ['武汉', '宜昌', '黄石', '襄阳', '十堰', '荆州', '荆门', '孝感', '鄂州', '黄冈', '随州', '咸宁', '仙桃', '潜江', '天门', '神农架', '恩施']], ['湖南', ['长沙', '株洲', '湘潭', '衡阳', '岳阳', '常德', '益阳', '郴州', '邵阳', '怀化', '娄底', '永州', '张家界', '湘西']], ['陕西', ['西安', '咸阳', '宝鸡', '铜川', '延安', '渭南', '榆林', '汉中', '安康', '商洛', '杨凌']], ['山西', ['太原', '运城', '大同', '临汾', '长治', '晋城', '阳泉', '朔州', '晋中', '忻州', '吕梁']], ['黑龙江', ['哈尔滨', '伊春', '绥化', '大庆', '齐齐哈尔', '牡丹江', '佳木斯', '鸡西', '鹤岗', '双鸭山', '黑河', '七台河', '大兴安岭']], ['辽宁', ['沈阳', '大连', '鞍山', '营口', '抚顺', '锦州', '丹东', '葫芦岛', '本溪', '辽阳', '铁岭', '盘锦', '朝阳', '阜新']], ['吉林省', ['长春', '吉林', '辽源', '通化', '四平', '松原', '延吉', '白山', '白城', '延边']], ['云南', ['昆明', '曲靖', '玉溪', '大理', '丽江', '红河州', '普洱', '保山', '昭通', '文山', '西双版纳', '德宏', '楚雄', '临沧', '怒江', '迪庆']], ['贵州', ['贵阳', '遵义', '六盘水', '安顺', '铜仁', '毕节', '黔西南', '黔东南', '黔南']], ['甘肃', ['兰州', '金昌', '嘉峪关', '酒泉', '天水', '武威', '白银', '张掖', '平凉', '定西', '陇南', '庆阳', '临夏', '甘南']], ['内蒙古', ['呼和浩特', '赤峰', '包头', '通辽', '鄂尔多斯', '巴彦淖尔', '乌海', '呼伦贝尔', '乌兰察布', '兴安盟', '锡林郭勒盟', '阿拉善盟']], ['宁夏', ['银川', '吴忠', '中卫', '石嘴山', '固原']], ['西藏', ['拉萨', '日喀则', '林芝', '山南', '昌都', '那曲', '阿里']], ['新疆', ['乌鲁木齐', '克拉玛依', '喀什地区', '伊犁', '阿克苏', '哈密', '石河子', '阿拉尔', '五家渠', '图木舒克', '昌吉', '阿勒泰', '吐鲁番', '塔城', '和田', '克孜勒苏柯尔克孜', '巴音郭楞', '博尔塔拉']], ['青海', ['西宁', '海东', '海西', '海北', '黄南', '海南', '果洛', '玉树']]];