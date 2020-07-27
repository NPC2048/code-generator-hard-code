var index = {

  init: function (options) {
    this.options = $.extend(options);
    // 初始化提示
    $('[data-toggle=popover]').popover();
    // 初始化表格
    this.initTable(this);
  },
  initTable: function (that) {
    $('#columnTables').bootstrapTable({
      columns: [{
        field: 'name',
        title: '字段名'
      }, {
        field: 'showName',
        title: '显示的名称'
      }, {
        field: 'javaType',
        title: 'java类型',
        formatter: function (value, row, index) {
          console.log('value: ', value)
          console.log('row: ', row)
          console.log('index: ', index)
          for (var i = 0; i < that.options.javaTypes.length; i++) {
            // that.options.javaTypes[i].
            if (that.options.javaTypes[i].name == value) {
              return that.options.javaTypes[i].name;
            }
          }
        }
      }, {
        field: 'sqlType',
        title: '数据库类型'
      }, {
        field: 'tableFiled',
        title: '数据库字段名'
      }, {
        field: 'query',
        title: '是否有查询'
      }],
      data: [{
        name: 'id',
        showName: 'id主键',
        javaType: 'String',
      }]
    })
  }

}

function getConfig() {
  var config = localStorage.getItem('config');
  if (!config) {
    config = '{}';
    localStorage.setItem('config', JSON.stringify(config));
  }
  return JSON.parse(config);
}

function addConfig(key, value) {
  var config = getConfig();
  config[key] = value;
  localStorage.setItem('config', JSON.stringify(config));
}

function updateConfig(config) {
  localStorage.setItem('config', JSON.stringify(config))
}