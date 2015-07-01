fetchBaseDir = function() {
  var baseDir = '', basePath = '', publicPath = '';
  var scripts = document.getElementsByTagName("script");
  for (var i = 0; i < scripts.length; i++) {
    var src = scripts[i].src;
    if (/bootstrap\.js$/.test(src)) {
      publicPath = src.substring(0, src.lastIndexOf('/'));
      basePath = publicPath.substring(0, publicPath.lastIndexOf('/'));
      baseDir = basePath.substring(basePath.lastIndexOf('/'));
      return baseDir;
    }
  }
};

FileLoader = {
  css: function(file, dir) {
    dir = dir || '';
    if (dir.length > 0) {
      if (!(/\/$/.test(dir))) {
        dir += '/';
      }
    }
    if (typeof (file) === 'string' || file instanceof String) {
      document.write('<link rel="stylesheet" type="text/css" href="' + dir + file + '">');
    } else if (file instanceof Array) {
      for (var i = 0; i < file.length; i++) {
        document.write('<link rel="stylesheet" type="text/css" href="' + dir + file[i] + '">');
      }
    }
  },
  js: function(file, dir) {
    dir = dir || '';
    if (dir.length > 0) {
      if (!(/\/$/.test(dir))) {
        dir += '/';
      }
    }
    if (typeof (file) === 'string' || file instanceof String) {
      document.write('<script type="text/javascript" src="' + dir + file + '"></script>');
    } else if (file instanceof Array) {
      for (var i = 0; i < file.length; i++) {
        document.write('<script type="text/javascript" src="' + dir + file[i] + '"></script>');
      }
    }
  },
  json: function(file, dir) {
    dir = dir || '';
    if (dir.length > 0) {
      if (!(/\/$/.test(dir))) {
        dir += '/';
      }
    }
    if (typeof (file) === 'string' || file instanceof String) {
      document.write('<script type="text/javascript" src="' + dir + file + '"></script>');
    } else if (file instanceof Array) {
      for (var i = 0; i < file.length; i++) {
        document.write('<script type="text/javascript" src="' + dir + file[i] + '"></script>');
      }
    }
  }
};

(function() {
  var cfg = {
    css: ['platform/ext/resources/ext-theme-crisp-all.css', 'platform/app.css'],
    js: ['platform/ext/ext-all.js', 'platform/ext/ext-custom.js', 'platform/ext/locale/ext-lang-zh_CN.js', 'platform/app.js']
  };

  baseDir = fetchBaseDir();

  FileLoader.css(cfg.css, baseDir);
  FileLoader.js(cfg.js, baseDir);
})();