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
    css: ['/public/ext/resources/ext-theme-all.css', '/public/ext/resources/ext-theme-custom.css'],
    js: ['/public/ext/ext-all.js', '/public/ext/ext-locale-zh_CN.js', '/public/ext/ext-custom.js']
  };

  FileLoader.css(cfg.css, ctx);
  FileLoader.js(cfg.js, ctx);
})();