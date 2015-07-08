Ext.app.Application.$onExtended[1]['fn'] = function(cls, data, hooks) {
  var onBeforeClassCreated = hooks.onBeforeCreated;
  hooks.onBeforeCreated = function(cls, data) {
    var Controller = Ext.app.Controller, requires = [], namespace, proto;
    proto = cls.prototype;
    namespace = Controller.resolveNamespace(cls, data);
    if (namespace) {
      proto.$namespace = namespace;
    }
    Controller.processDependencies(proto, requires, namespace, 'model', data.models, data.modelFolder);
    Controller.processDependencies(proto, requires, namespace, 'view', data.views, data.viewFolder);
    Controller.processDependencies(proto, requires, namespace, 'store', data.stores, data.storeFolder);
    Controller.processDependencies(proto, requires, namespace, 'controller', data.controllers, data.controllerFolder);
    Ext.require(requires, Ext.Function.pass(onBeforeClassCreated, arguments, this));
  };
};

Ext.override(Ext.app.Controller, {
  statics: {
    processDependencies: function(cls, requires, namespace, kind, names, dir) {
      if (!names || !names.length) {
        return;
      }
      var me = this, strings = me.strings[kind], o, absoluteName, shortName, name, j, subLn, getterName, getter;
      if (!Ext.isArray(names)) {
        names = [names];
      }
      for (j = 0, subLn = names.length; j < subLn; j++) {
        name = names[j];
        o = me.getFullName(name, dir || kind, namespace);
        absoluteName = o.absoluteName;
        shortName = o.shortName;
        requires.push(absoluteName);
        getterName = me.getGetterName(shortName, strings.upper);
        cls[getterName] = getter = me.createGetter(strings.getter, name);
        if (kind !== 'controller') {
          getter['Ext.app.getter'] = true;
        }
      }
    },
    getFullName: function(name, dir, namespace) {
      var shortName = name, sep, absoluteName;
      if ((sep = name.indexOf('@')) > 0) {
        shortName = name.substring(0, sep);
        absoluteName = name.substring(sep + 1) + '.' + shortName;
      } else if (name.indexOf('.') > 0 && (Ext.ClassManager.isCreated(name) || this.hasRegisteredPrefix(name))) {
        absoluteName = name;
      } else {
        var pathname = name;
        if (!namespace) {
          Ext.log.warn("Cannot find namespace for " + (dir && dir != '.' ? dir + " " + name : name) + ", " + "assuming it is fully qualified class name");
        }
        if (namespace) {
          absoluteName = namespace + '.' + (dir && dir != '.' ? dir + "." + name : name);
          shortName = name;
        } else {
          absoluteName = name;
        }
      }
      return {
        absoluteName: absoluteName,
        shortName: shortName
      };
    }
  }
});

Ext.application = function(config) {
  var createApp = function(App) {
    Ext.onReady(function() {
      Ext.app.Application.instance = new App();
    });
  }, paths = config.paths, ns;
  if (typeof config === "string") {
    Ext.require(config, function() {
      createApp(Ext.ClassManager.get(config));
    });
  } else {
    config = Ext.apply({
      extend: 'Ext.app.Application'
    }, config);

    Ext.Loader.setPath(config.name, config.appFolder || 'app');
    if (paths) {
      for (ns in paths) {
        if (paths.hasOwnProperty(ns)) {
          Ext.Loader.setPath(ns, paths[ns]);
        }
      }
    }
    config['paths processed'] = true;
    Ext.define(config.name + ".$application", config, function() {
      createApp(this);
      Ext.define(config.name, {
        statics: {
          prefix: config.name.toLowerCase() + '-',
          views: {},
          widget: function(xtype, config) {
            var view = Ext.widget(this.prefix + xtype, config);
            this.views[xtype] = view;
            return view;
          },
          get: function(xtype) {
            return this.views[xtype];
          }
        }
      });
    });
  }
};

Ext.define('Store', {
  statics: {
    create: function(store) {
      if (Ext.isString(store)) {
        return Ext.widget(store);
      } else {
        if (store.type == 'json') {
          return new Ext.data.JsonStore(store);
        } else if (store.type == 'array') {
          return new Ext.data.ArrayStore(store);
        } else if (store.type == 'tree') {
          return new Ext.data.TreeStore(store);
        } else {
          return new Ext.data.Store(store);
        }
      }
    },
    get: function(store) {
      return Ext.getStore(store);
    }
  }
});