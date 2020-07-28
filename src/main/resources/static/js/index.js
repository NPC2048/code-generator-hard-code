var index = {
  toolbar: {
    addColumnBtn: $('#addColumn'),
    removeBtn: $('#remove')
  },
  checkBoxList: [],
  table: $('#columnTables'),
  init: function (options) {
    this.options = $.extend(options);
    // 初始化提示
    $('[data-toggle=popover]').popover();
    // 初始化表格
    this.initTable(this);
    // 添加列点击事件
    this.addColumnBtnClick();
    this.removeBtnClick(this);
  },
  initTable: function (that) {
    that.table.bootstrapTable({
      clickEdit: true,
      toolbar: '#toolbar',
      columns: [{
        field: 'checkbox',
        checkbox: true,
        formatter: function (value, row, index) {
          if ($.inArray(row.id, that.checkBoxList) != -1) {
            return {
              checked: true
            };
          }
        }
      }, {
        field: 'name',
        title: '字段名'
      }, {
        field: 'showName',
        title: '显示的名称'
      }, {
        field: 'javaType',
        title: 'java类型',
        formatter: function (value, row, index) {
          for (var i = 0; i < that.options.javaTypes.length; i++) {
            var type = that.options.javaTypes[i];
            if (type.name == value) {
              return type.name;
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
      }],
      onClickCell: function (field, value, row, $e) {
        $e.attr('contenteditable', true).focus();
        $e.blur(function () {
          let index = $e.parent().data('index');
          console.log(index);
          let tdValue = $e.html();
          console.log(tdValue);
          that.saveData(that, index, field, tdValue);
          $e.blur();
        })
      },
      onCheck: function (row) {
        that.checkBoxList.push(row.name)
      },
      onUncheck: function (row) {
        that.checkBoxList.splice($.inArray(row.name, that.checkBoxList), 1);
      },
      onCheckAll: function (rows) {
        for (var i = 0; i < rows.length; i++) {
          if ($.inArray(rows[i].name, that.checkBoxList) == -1) { //未曾选过才加进去
            that.checkBoxList.push(rows[i].name);
          }
        }
      },
      onUncheckAll: function (rows) {
        for (var i = 0; i < rows.length; i++) {
          that.checkBoxList.splice($.inArray(rows[i].name, that.checkBoxList), 1);
        }
      }
    })
  },
  saveData: function (that, index, field, value) {
    that.table.bootstrapTable('updateCell', {
      index, field, value
    })
  },
  // 添加列按钮点击事件
  addColumnBtnClick: function () {
    var that = this;
    that.toolbar.addColumnBtn.on('click', function () {
      // that.table.bootstrapTable('append', {
      //   index: 0,
      //   row: {
      //     id: ' ',
      //     showName: '',
      //     javaType: '',
      //     sqlType: '',
      //     tableFiled: '',
      //     query: ''
      //   }
      // })
    })
  },
  // 删除事件
  removeBtnClick: function (that) {
    that.toolbar.removeBtn.on('click', function () {
      that.table.bootstrapTable('remove', {
        field: 'name',
        values: that.checkBoxList
      });
      that.checkBoxList = [];
    })
  },
  // 初始化 select
  initBootSelectSelect: function (that) {

  }

};

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