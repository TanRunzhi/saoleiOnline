loadProperties();

var dataTableDefaultArgs = {
  "processing": $.i18n.prop('dataTable.processing'),
  "lengthMenu": $.i18n.prop('dataTable.lengthMenu'),
  "zeroRecords": $.i18n.prop('dataTable.zeroRecords'),
  "emptyTable": $.i18n.prop('dataTable.emptyTable'),
  "info": $.i18n.prop('dataTable.info'),
  "infoFiltered": $.i18n.prop('dataTable.infoFiltered'),
  "paginate": {
    "first": $.i18n.prop('page.first'),
    "previous": $.i18n.prop('page.prev'),
    "next": $.i18n.prop('page.next'),
    "last": $.i18n.prop('page.last')
  }
};

var inputLen = true;

msg = function (msg) {
  bootbox.alert({
    title: '<b>' + $.i18n.prop('sys.info') + '</b>',
    /*size: 'small',*/
    message: $.i18n.prop('alert.msg', msg),
    buttons: {
      ok: {
        label: $.i18n.prop("sys.ok")
      }
    }
  });
  /*$.messager.alert({
    title: $.i18n.properties"sys.info,
    msg: msg
  });*/
}

alertMsg = function (msg, callback) {
  bootbox.alert({
    title: '<b>' + $.i18n.prop('sys.info') + '</b>',
    /*size: 'small',*/
    message: msg,
    buttons: {
      ok: {
        label: $.i18n.prop("sys.ok")
      }
    },
    callback: callback
  });
}

msgShow = function (msg) {
  var dialog = bootbox.dialog({
    closeButton: false,
    message: $.i18n.prop('alert.msg', msg),
    backdrop: true,
    onEscape: true
  });
  setTimeout(function () {
    dialog.modal('hide');
  }, 3000)
  /*$.messager.show({
    title: $.i18n.properties"sys.info,
    msg: msg,
    timeout: 3000,
    showType: 'fade'
  });*/

}

confirmMsg = function (title, msg, callback) {
  bootbox.confirm({
    title: "<b>" + title + "</b>",
    /*size: 'small',*/
    message: $.i18n.prop('alert.msg', msg),
    buttons: {
      confirm: {
        label: $.i18n.prop("sys.ok")
      },
      cancel: {
        label: $.i18n.prop("sys.cancel")
      }
    },
    callback: callback
  })
  /*$.messager.confirm(title,msg,callback);*/
}

/*
alertMsg = function (msg) {
  $.messager.show({
    title: $.i18n.properties"sys.info,
    msg: msg,
    showType: 'fade',
    timeout: 800,
    style: {
      right: '',
      bottom: ''
    }
  });
}
*/


$('input,select,textarea').bind("focus", function () {
  if ($(this).next().attr('class') == 'required')
    $(this).next().remove();
});

viewMode = function () {
  $('form input,textarea').prop("readonly", true);
  $("select,input[type='file']").prop("disabled", true);
  $("form [onclick='doSubmit();']").hide();
  $("form [onclick='form.submit();']").hide();
  $("form [onclick='doLocalSubmit();']").hide();
  $("form [onclick='doAppend();']").hide();
  $("form button[class='ico_send']").hide();
  $("form button[class='ico_tmpSave']").hide();
  $(".uploadFile input").hide();
  $('.addtable').hide();
  $('#content').find('div').hide();
  $("#lot a:not(.opt_bck)").hide();
  $(".btn:not(.btn-show):not(.modal-content button)").hide();
}

bindDataTable = function (single) {

  $('table.dataTable tbody').on('click', 'tr', function () {
    if ($(this).hasClass('selected')) {
      $(this).find("input").prop("checked", false);
      $(this).removeClass('selected');
    } else {
      if (typeof(single) != 'undefined') {
        $('.dataTable tbody tr input:checked').prop("checked", false);
        $('.dataTable tbody tr.selected').removeClass('selected');
      }
      $(this).addClass('selected');
      $(this).find("input").prop("checked", true);
    }
    /*window.event.stopPropagation();
    return false;*/
  });


  $('table.dataTable tbody tr').on('click', 'input:checkbox', function () {
    //由于在触发此事件的时候，点击后的勾还没打上或者取消  所以下方用 if (未被勾选){ add 'selected'}
    if (!$(this).prop('checked')) {
      if (typeof(single) != 'undefined') {
        $('.dataTable tbody tr input:checked').prop("checked", false);
        $('.dataTable tbody tr.selected').removeClass('selected');
      }
      $(this).parent().parent().parent().addClass('selected');
    } else {
      $(this).parent().parent().parent().removeClass('selected');
    }
    window.event.stopPropagation();
    return false
  });

  $('table.dataTable thead tr').on('click', 'input:checkbox', function () {
    if (typeof(single) != 'undefined' && $('table.dataTable tbody tr').length >= 1) {
      msgShow($.i18n.prop('dataTable.single'));
      return false;
    }
    selectAll(this, "ids");
    if ($(this).prop("checked")) {
      $(".dataTable tbody tr").addClass("selected");
    } else {
      $(".dataTable tbody tr.selected").removeClass("selected");
    }
    window.event.stopPropagation();
    return false
  })

  $('table.dataTable tbody tr input:checkbox:checked').each(function (idx, obj) {
    $(this).parent().parent().parent().addClass('selected');
  });

}

/**
 * 当一个页面import两个弹框时候，为了保证两个table互不干扰  增加tableId加以区分
 *      ( 其实是上面那个bindDataTable用的地方太多了，改了怕影响稳定性，用下面这个兼容上面那个bindDataTable用着 )
 * */
bindDataTableByTableId = function (single,tableId) {

  $(tableId + '.dataTable tbody').on('click', 'tr', function () {
    if ($(this).hasClass('selected')) {
      $(this).find("input").prop("checked", false);
      $(this).removeClass('selected');
    } else {
      if (single) {
        $(tableId + '.dataTable tbody tr input:checked').prop("checked", false);
        $(tableId + '.dataTable tbody tr.selected').removeClass('selected');
      }
      $(this).addClass('selected');
      $(this).find("input").prop("checked", true);
    }
    window.event.stopPropagation();
    return false
  });


  $(tableId + '.dataTable tbody tr').on('click', 'input:checkbox', function () {
    if ($(this).prop('checked')) {
      if (single) {
        $(tableId + '.dataTable tbody tr input:checked').prop("checked", false);
        $(tableId + '.dataTable tbody tr.selected').removeClass('selected');
      }
      $(this).parent().parent().parent().addClass('selected');
    } else {
      $(this).parent().parent().parent().removeClass('selected');
    }
    window.event.stopPropagation();
    return false
  });


  $(tableId + '.dataTable thead tr').on('click', 'input:checkbox', function () {
    if (single && $(tableId + '.dataTable tbody tr').length >= 1) {
      msgShow($.i18n.prop('dataTable.single'));
      return false;
    }
    $(tableId + " input:checkbox").prop("checked", $(this).prop("checked"));
    if ($(this).prop("checked")) {
      $(tableId + ".dataTable tbody tr").addClass("selected");
    } else {
      $(tableId + ".dataTable tbody tr.selected").removeClass("selected");
    }
  })

  $(tableId + '.dataTable tbody tr input:checkbox:checked').each(function (idx, obj) {
    $(this).parent().parent().parent().addClass('selected');
  });

}

function showOrHideTab(obj) {
  var tabContent = $(obj).parent().next();
  if (tabContent.find("div.tab-pane.active").length > 0) {
    $(obj).find("i").removeClass("fa-chevron-up").addClass("fa-chevron-down");
    tabContent.find("div.tab-pane.active").removeClass("active");
  } else {
    var idx = $(obj).parent().find("li.active").index();
    $(obj).find("i").removeClass("fa-chevron-down").addClass("fa-chevron-up");
    tabContent.find("div.tab-pane").eq(idx).addClass("active");
  }
}

$(document).ready(function () {

  bindDataTable();

  if (inputLen) {
    bindInputAndSelectStyle()
  }

  $(".nav-tabs>li.tab-close").click(function () {
    showOrHideTab(this);
  })
  //如果搜索框里有值，显示搜索区域
  /*$('.searchField').find('input').each(function (idx, obj) {
    if ($(obj).val() != '' && $(obj).val() != null) {
      $('#searchBar').find('select').prop('selectedIndex', idx);
      $(obj).show();
      $('#searchBar').show();
      $('#showSearchBar').html('<span>隐藏搜索栏</span>');
    }
  });
  //全选框动作
  $('#selectAll').bind('change', function () {
    $('.dataTable').find('tr[id^=tr_]').find('input').prop('checked', $(this).prop('checked'));
  });*/
  //自动绑定日期框
  $("input[name*='Time'][type!='hidden']:not(:checkbox)").bind('click', function () {
    WdatePicker({dateFmt: 'yyyy-MM-dd HH:mm:ss'});
  });
  $("input[name*='Birthday'][type!='hidden'],input[name*='Date'][type!='hidden'][readonly!='readonly']:not(:checkbox)").bind('click', WdatePicker);
  $("input[name*='Birthday'][type!='hidden'],input[name*='Date'][type!='hidden'][readonly!='readonly']:not(:checkbox)").prop("readonly", true);

  //查看操作各功能按钮禁用
  if ($('#form #op').val() != null) {
    if ($('#form #op').val() == 'v' || $('#form #op').val().indexOf('view') >= 0) {
      $('form').find('input:not(.extra),select:not(.extra),textarea:not(.extra)').attr("disabled", "true");
      $("form [onclick='doSubmit();']").hide();
      $("form [onclick='form.submit();']").hide();
      $("form [onclick='doLocalSubmit();']").hide();
      $("form [onclick='doAppend();']").hide();
      $("form button[class='ico_send']").hide();
      $("form button[class='ico_tmpSave']").hide();
      $(".uploadFile input").hide();
      $('.addtable').hide();
      //$('#content').find('div').hide();
      $("#lot a:not(.opt_bck)").hide();
      $(".btn:not(.btn-show):not(.modal-content button)").hide();
    }
    if ($('#form #op').val() == 'audit') {
      $('input,select').attr("disabled", "true");
    }
  }

  $("input.number").keyup(function () {
    checkNum(this);
  })

  setRequiredListTips()

  //检查重复项
  if (typeof(checkDupl) != 'undefined') {
    if (checkDupl != null && checkDupl.length > 0) {
      for (var key in checkDupl) {
        $(checkDupl[key]).bind('blur', function () {
          if ($(this).val() != null && $(this).val() != '')
            checkDuplicated($(this), $(this).attr('id'), $(this).val());
        });
      }
    }
  }
  //排序部分自动处理
  if (typeof(orderByList) != 'undefined') {
    if (orderByList != null && orderByList.length > 0) {
      $.each($("#dynamic-table [class='sorting']"), function (idx, obj) {
        $(obj).bind('click', function () {
          var org = $('#orderBy').val();
          if (org.indexOf(orderByList[idx]) >= 0) {
            if (org.indexOf('desc') > 0) {
              $('#orderBy').val(orderByList[idx]);
            } else {
              $('#orderBy').val(orderByList[idx] + " desc");
            }
          } else {
            $('#orderBy').val(orderByList[idx]);
          }
          $('#searchForm').submit();
        })
      })
    }
    for (var key in orderByList) {
      if ($('#orderBy').val().indexOf(orderByList[key]) >= 0) {
        if ($('#orderBy').val().indexOf("desc") > 0) {
          $('.table thead tr th:not(.sorting_disabled)').eq(key).attr('class', 'sorting_desc');
        } else {
          $('.table thead tr th:not(.sorting_disabled)').eq(key).attr('class', 'sorting_asc');
        }
      }
    }
  }


  if (typeof(numberFields) != 'undefined') {
    if (numberFields != null && numberFields.length > 0) {
      for (var key in numberFields) {
        $(numberFields[key]).bind('blur', function () {
          if ($(this).val().length > 0) {
            if (!$(this).val().match(/^[0-9]+[/.]?[0-9]{0,2}$/)) {
              $(this).after($.i18n.prop('msg.msgNum'));
            } else {
              if ($(this).next().attr('class') == 'red')
                $(this).next().remove();
            }
          }
        });
      }
    }
  }

  if (typeof(numFields) != 'undefined') {
    if (numFields != null && numFields.length > 0) {
      for (var key in numFields) {
        $(numFields[key]).bind('blur', function () {
          if ($(this).val().length > 0) {
            if (!$(this).val().match(/^[0-9]+[/.]?[0-9]{0,2}$/)) {
              $(this).after($.i18n.prop('msg.msgNum'));
            } else {
              if ($(this).next().attr('class') == 'red')
                $(this).next().remove();
            }
          }
        });
      }
    }
  }


  if (typeof(intFields) != 'undefined') {
    if (intFields != null && intFields.length > 0) {
      for (var key in intFields) {
        $(intFields[key]).bind('blur', function () {
          if ($(this).val().length > 0) {
            if (!$(this).val().match(/^[0-9]+$/)) {
              $(this).after($.i18n.prop('msg.msgInt'));
            } else {
              if ($(this).next().attr('class') == 'red')
                $(this).next().remove();
            }
          }
        });
      }
    }
  }
});

bindInputAndSelectStyle = function(){
  $("table.table tbody tr td input:not(:hidden):not(:file):not(:radio):not(:checkbox):not(.extra),select:not(.extra)").addClass("col-sm-10");
}

setRequiredListTips = function(){
  if (typeof(requiredList) != 'undefined') {
    if (requiredList != null && requiredList.length > 0) {
      for (var i = 0; i < requiredList.length; i++) {
        if (typeof($(requiredList[i])) != 'undefined' && $(requiredList[i]).parent().prev().find("i").length == 0) {
          if ($(requiredList[i]).parent().prev("th").length > 0)
            $(requiredList[i]).parent().prev().prepend("<i class='fa fa-fw fa-hand-o-right red'></i>");
        }
      }
    }
  }
}


bindCheck = function () {
  $('tr').bind('click', function () {
    $(this).find('input').attr('checked', 'checked');
  });
}

selectAll = function (obj, checkName) {
  $("input[name='" + checkName + "']").prop("checked", $(obj).prop("checked"));
}


ajDel = function (id, callback) {
  confirmMsg($.i18n.prop("sys.info"), $.i18n.prop("sys.msg.del"), function (r) {
    if (r) {
      $.post('ajDel.htm', {"id": id}, function (msg) {
        if (msg.split(',')[0] == '1')
          $('#tr_' + id).remove();
        msgShow(msg.split(',')[1]);
      });
      if (callback != null)
        callback();
    }
  });
}

resetQuery = function (str) {
  $('.form input').each(function () {
    $(this).val("");
  });
  if (str == null)
    window.location.href = 'list.htm';
  else
    window.location.href = str;
}


var idTmr;

function getExplorer() {
  var explorer = window.navigator.userAgent;
  //ie
  if (explorer.indexOf("MSIE") >= 0) {
    return 'ie';
  }
  //firefox
  else if (explorer.indexOf("Firefox") >= 0) {
    return 'Firefox';
  }
  //Chrome
  else if (explorer.indexOf("Chrome") >= 0) {
    return 'Chrome';
  }
  //Opera
  else if (explorer.indexOf("Opera") >= 0) {
    return 'Opera';
  }
  //Safari
  else if (explorer.indexOf("Safari") >= 0) {
    return 'Safari';
  }
}

checkField = function () {
  var flag = 0;
  if (typeof(requiredList) != 'undefined') {
    if (requiredList != null && requiredList.length > 0) {
      $("form span").each(function () {
        if ($(this).attr('class') == 'red') {
          $(this).remove();
        }
      });

      for (var i = 0; i < requiredList.length; i++) {
        if (typeof($(requiredList[i])) != 'undefined') {
          // console.log("requiredList["+i+"]:"+ requiredList[i] + "      requiredList[i].val():"+ $(requiredList[i]).val())
          if ($(requiredList[i]).length == 1 && ( !$(requiredList[i]).val() || $(requiredList[i]).val().length == 0)) {
            $(requiredList[i]).after($.i18n.prop('msg.msgRq'));
            flag = 1;
          } else {
            $.each($(requiredList[i]), function (idx, obj) {
              if ($(obj).val().length == 0) {
                $(obj).after($.i18n.prop('msg.msgRq'));
                flag = 1;
              }
            })
          }
        }
      }
    }
  }
  if (typeof(maxLengthList) != 'undefined') {
    if (maxLengthList != null && maxLengthList.length > 0) {
      $("form span").each(function () {
        if ($(this).attr('class') == 'red') {
          $(this).remove();
        }
      });
      for (var i = 0; i < maxLengthList.length; i++) {

        if (typeof($(maxLengthList[i])) != 'undefined') {
          var len = parseInt($(maxLengthList[i]).attr("length"));
          if (len < $(maxLengthList[i]).val().length) {
            $(maxLengthList[i]).after($.i18n.prop("msg.maxLength", len));
          }
          flag = 1;
        }
      }
    }
  }
  if (typeof(numberFields) != 'undefined') {
    if (numberFields != null && numberFields.length > 0) {
      for (var i = 0; i < numberFields.length; i++) {
        var _field = $(numberFields[i]);
        if (_field.length > 0) {
          if (!_field.val().match(/^[0-9]+[/.]?[0-9]{0,2}$/)) {
            _field.after($.i18n.prop('msg.msgNum'));
            flag = 1;
          } else {
            if (_field.next().attr('class') == 'red')
              _field.next().remove();
          }
        }
      }
    }
  }

  if (typeof(intFields) != 'undefined') {
    if (intFields != null && intFields.length > 0) {
      for (var i = 0; i < intFields.length; i++) {
        var _field = $(intFields[i]);
        if (_field.val().length > 0) {
          if (!_field.val().match(/^[0-9]+$/)) {
            _field.after($.i18n.prop('msg.msgInt'));
            flag = 1;
          } else {
            if (_field.next().attr('class') == 'red')
              _field.next().remove();
          }
        }
      }
    }
  }
  if (typeof(iDCardNoList) != 'undefined') {
    if (iDCardNoList != null && iDCardNoList.length > 0) {
      var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
      for (var i = 0; i < iDCardNoList.length; i++) {
        var _field = $(iDCardNoList[i]);
        if (_field.val().length > 0 && !reg.test(_field.val())) {
          _field.after($.i18n.prop('msg.msgIDCardNo'));
          flag = 1;
        } else {
          if (_field.next().attr('class') == 'red' && _field.next().text()==$.i18n.prop('msg.msgIDCardNo') )
            _field.next().remove();
        }
      }
    }
  }

  if (typeof(mobilePhoneList) != 'undefined' && mobilePhoneList != null && mobilePhoneList.length > 0) {
    var reg = /^1[3|4|5|7|8]\d{9}$/;
    var reg2 = /^(\d{3,4}-)?\d{7,8}$/;
    for (var i = 0; i < mobilePhoneList.length; i++) {
      var _field = $(mobilePhoneList[i]);
      if (_field.val().length > 0 && !reg.test(_field.val()) && !reg2.test(_field.val())) {
        _field.after($.i18n.prop('msg.msgMobilePhone'));
        flag = 1;
      } else {
        if (_field.next().attr('class') == 'red' && _field.next().text()==$.i18n.prop('msg.msgMobilePhone') )
          _field.next().remove();
      }
    }
  }

  if (typeof(selectList) != 'undefined') {
    if (selectList != null && selectList.length > 0) {
      for (var i = 0; i < selectList.length; i++) {
        var _field = $(selectList[i]);
        if (_field.val() == "" || _field.val() == 0) {
          _field.after($.i18n.prop('msg.msgSelectItem'));
          flag = 1;
        } else {
          if (_field.next().attr('class') == 'red')
            _field.next().remove();
        }
      }
    }
  }

  if (typeof(emailList) != 'undefined' && emailList != null && emailList.length > 0) {
    var reg = /^([a-zA-Z0-9_\.-]+)@([\da-zA-Z\.-]+)\.([a-zA-Z\.]{2,6})$/;
    for (var i = 0; i < emailList.length; i++) {
      var _field = $(emailList[i]);
      if (_field.val().length > 0 && !reg.test(_field.val())) {
        _field.after($.i18n.prop('msg.msgEmail'));
        flag = 1;
      } else {
        if (_field.next().attr('class') == 'red')
          _field.next().remove();
      }
    }
  }

  //通过name属性，验证radio必选顶
  if (typeof(radioRequiredList) != 'undefined' && radioRequiredList != null && radioRequiredList.length > 0) {
    for (var i = 0; i < radioRequiredList.length; i++) {
      if ("undefined" == typeof($("input[name='" + radioRequiredList[i] + "']:checked").val())) {
        if ("undefined" == typeof($("input[name='" + radioRequiredList[i] + "']:last").next("label").html())) {
          $("input[name='" + radioRequiredList[i] + "']:last").after($.i18n.prop('msg.msgRq'));
        } else {
          $("input[name='" + radioRequiredList[i] + "']:last").next("label").after($.i18n.prop('msg.msgRq'));
        }
      } else {
        if ($("input[name='" + radioRequiredList[i] + "']:last").siblings("span").attr('class') == 'red')
          $("input[name='" + radioRequiredList[i] + "']:last").siblings("span").remove();
      }
    }
  }

  //以下为多项验证，通过class验证一组元素
  //多项验证：必填项
  if (typeof(multiRequiredList) != 'undefined' && multiRequiredList != null && multiRequiredList.length > 0) {
    for (var i = 0; i < multiRequiredList.length; i++) {
      $(multiRequiredList[i]).each(function () {
        if ($(this).val().length == 0) {
          $(this).after($.i18n.prop('msg.msgRq'));
          flag = 1;
        } else {
          if ($(this).next().attr('class') == 'red')
            $(this).next().remove();
        }
      });
    }
  }

  //多项验证：手机号
  if (typeof(multiMobilePhoneList) != 'undefined' && multiMobilePhoneList != null && multiMobilePhoneList.length > 0) {
    for (var i = 0; i < multiMobilePhoneList.length; i++) {
      var reg = /^1[3|4|5|7|8]\d{9}$/;
      $(multiMobilePhoneList[i]).each(function () {
        if (!reg.test($(this).val())) {
          $(this).after($.i18n.prop('msg.msgMobilePhone'));
          flag = 1;
        } else {
          if ($(this).next().attr('class') == 'red')
            $(this).next().remove();
        }
      });
    }
  }

  //多项验证：姓名（汉字2-5个、英文2-30个）
  if (typeof(multiPersonNameList) != 'undefined' && multiPersonNameList != null && multiPersonNameList.length > 0) {
    for (var i = 0; i < multiPersonNameList.length; i++) {
      var reg = /^(([\u4E00-\u9FA5]{2,5}$)|(^[a-zA-Z]+[\s\.]?([a-zA-Z]+[\s\.]?){0,4}[a-zA-Z]))$/;
      $(multiPersonNameList[i]).each(function () {
        if (!reg.test($(this).val())) {
          $(this).after($.i18n.prop('msg.msgPersonName'));
          flag = 1;
        } else {
          if ($(this).next().attr('class') == 'red')
            $(this).next().remove();
        }
      });
    }
  }
  return flag;
}

doSubmit = function (formId, currentState) {
  if (!formId) {
    formId = "form";
  }
  if (currentState) {
    $("#" + formId + " input[name='currentState']").val(currentState);
  } else {
    currentState = "01";
  }
  if (currentState != '01') {
    $('#' + formId).submit();
    return false;
  }
  if (checkField() == 0) {
    var flag = Number(0);
    if (typeof(checkDupl) != 'undefined') {
      if (checkDupl != null && checkDupl.length > 0) {
        for (var key in checkDupl) {
          $(checkDupl[key]).each(function (idx, obj) {
            if (flag == 0 && $(this).val() != null && $(this).val() != '')
              flag += checkDuplicated($(this), $(this).attr('id'), $(this).val(), false);
          });
        }
      }
    }
    if (flag == 0) {
      confirmMsg($.i18n.prop('sys.info'), $.i18n.prop('sys.msg.save'), function (r) {
        if (r) {
          $('#' + formId).submit();
        }
      })
    }
  } else {
    $("tr td span.red:eq(0)").prev().focus();
    msgShow($.i18n.prop('msg.req.error'));
  }
}

doAppend = function () {
  if (checkField() == 0) {
    $('#op').val('append');
    $('#form').submit();
  }
}

logout = function (act) {
  confirmMsg($.i18n.prop('sys.alert'), $.i18n.prop('sys.msg.out'), function (r) {
    if (r) {
      window.location.href = base + (act == null ? '/logout.htm' : act);
    }
  });
}

isUndefined = function (str) {
  return (typeof(str) == 'undefined' || str == undefined || str == null) ? "" : str;
}

isNum = function (num) {
  return (typeof(num) == 'undefined' || num == undefined || num == null || isNaN(num)) ? "0.00" : num;
}
/**
 * 检查是否重复
 * @param obj 要检查的表单元素
 * @param key 表单元素的id（类的字段名）
 * @param value 表单元素的值
 */
checkDuplicated = function (obj, key, value, async) {
  var flag = 0;
  if (typeof(async) == 'undefined') {
    async = true;
  }
  var id = $("#id").val();
  $.ajax({
    url: 'checkDuplicated.htm',
    data: {id: id, key: key, value: value},
    async: async,
    success: function (data) {
      if (data != 'ok') {
        var title = $(obj).parent().prev().text();
        msgShow($.i18n.prop('sys.msg.duplicated', title));
        flag = 1;
        //$(obj).focus();
      }
    }
  })
  return Number(flag);
}

doInput = function (obj, op) {
  var _id
  if (obj != null)
    _id = $(obj).parent().parent().parent().attr('id').substr(3);
  else {
    _id = null;
  }
  $('#id').val(_id);
  if (op != null && op != '')
    $('#op').val(op);
  $('#searchForm').attr('action', 'input.htm').submit();
}

doEdit = function (obj, op) {
  var _id
  if (obj != null)
    _id = $(obj).parent().parent().parent().attr('id').substr(3);
  else {
    _id = null;
  }
  $('#id').val(_id);
  $('#op').val("E");
  $('#searchForm').attr('action', 'input.htm').submit();
}

doReturn = function (obj, op) {
  confirmMsg($.i18n.prop("sys.info"), $.i18n.prop("sys.confirm"), function (r) {
    if (r) {
      var _id
      if (obj != null)
        _id = $(obj).parent().parent().parent().attr('id').substr(3);
      else {
        _id = null;
      }
      $('#id').val(_id);
      $('#op').val("R");
      $('#searchForm').attr('action', 'update.htm').submit();
    }
  })
}
doApplyUpdate = function (obj, op) {
  confirmMsg($.i18n.prop("sys.info"), $.i18n.prop("sys.confirm"), function (r) {
    if (r) {
      var _id
      if (obj != null)
        _id = $(obj).parent().parent().parent().attr('id').substr(3);
      else {
        _id = null;
      }
      $('#id').val(_id);
      $('#op').val("AU");
      $('#searchForm').attr('action', 'update.htm').submit();
    }
  })
}

doInput = function (obj, op, act) {
  var _id
  if (obj != null)
    _id = $(obj).parent().parent().parent().attr('id').substr(3);
  else {
    _id = null;
  }
  $('#id').val(_id);
  if (op != null && op != '')
    $('#op').val(op);
  $('#searchForm').attr('action', act == null ? 'input.htm' : act + '.htm').submit();
}

doDel = function (obj) {
  var _id = $(obj).parent().parent().parent().attr('id').substr(3);
  ajDel(_id, null);
}

/**
 * 操作按钮中的查看
 * @param obj
 * @param act
 */
doView = function (obj, act) {
  var _id = $(obj).parent().parent().parent().attr('id').substr(3);
  $('#id').val(_id);
  $('#op').val('v');
  $('#searchForm').attr('action', act == null ? 'input.htm' : act + '.htm').submit();
}
/**
 * 操作按钮查看，操作类型非v,防止页面某些选项不能使用
 */
doViewNop = function (obj, act) {
  var _id = $(obj).parent().parent().parent().attr('id').substr(3);
  $('#id').val(_id);
  $('#op').val('v');
  $('#searchForm').attr('action', act == null ? 'input.htm' : act + '.htm').submit();
}

/**
 * 列表中的查看
 * @param obj
 */
doTitleView = function (obj) {
  var _id = $(obj).parent().parent().attr('id').substr(3);
  $('#id').val(_id);
  $('#op').val('v');
  $('#searchForm').attr('action', 'input.htm').submit();
}

search = function (act) {
  $('#searchForm').attr('action', act == null ? 'list.htm' : act + '.htm').submit();
}

expInfo = function (act) {
  if ($("#content tr").size() < 1) {
    msg($.i18n.prop('sys.exp.noData'));
    return false;
  }
  confirmMsg($.i18n.prop('sys.info'), $.i18n.prop('sys.exp.all'), function (r) {
    if (r) {
      search(act);
    }
  });
}

expSingleInfo = function (act, id) {
  confirmMsg($.i18n.prop('sys.info'), $.i18n.prop('sys.exp.detail'), function (r) {
    if (r) {
      window.open(act + "?id=" + id);
    }
  });
}

doAudit = function (obj, act) {
  var _id = $(obj).parent().parent().parent().attr('id').substr(3);
  $('#id').val(_id);
  $('#searchForm').attr('action', act == null ? 'doAudit.htm' : act + '.htm').submit();
}

showSearchBar = function (obj) {
  $(obj).html($('#searchBar').css('display') == 'none' ? $.i18n.prop('sys.msg.hideBar') : $.i18n.prop('sys.msg.showBar'));
  $('#searchBar').slideToggle();
  $('#btnArea').slideDown();
}

changeSearchField = function (sel) {
  $('.searchField').find('input').each(function (idx, obj) {
    if (idx == $(sel).prop('selectedIndex')) {
      $(obj).show();
    }
    else {
      $(obj).val('');
      $(obj).hide();
    }
  });
}

movePanel = function (cls) {
  $('#' + cls).slideToggle();
}

setSelectValue = function (renderTo, obj) {
  if ($(obj).find('option:selected').length > 0) {
    var str = $(obj).find('option:selected').text();
  }
  else {
    var str = $(obj).find('option:first').text();
  }
  if ($(obj).val() != '')
    $('#' + renderTo).val(str);
}

doMultiDel = function () {
  confirmMsg($.i18n.prop('sys.info'), $.i18n.prop('sys.msg.batchDel'), function (r) {
    if (r) {
      var ids = '';
      $.each($('.table_01').find("tr[id^='tr_']").find("input[type='checkbox']:checked"), function (idx, obj) {
        ids += $(obj).attr('id').substr(4) + ',';
      });
      if (ids.length > 0 && ids.indexOf(','))
        $.post('ajDel.htm', {id: ids}, function () {
          location.href = location.href;
        })
      else
        msg($.i18n.prop('sys.msg.choose'));
    }
  });
}

reloadPage = function () {
  location.href = location.href;
};

ajSub = function (obj, _result, act) {
  confirmMsg($.i18n.prop("sys.info"), $.i18n.prop("sys.confirm"), function (r) {
    if (r) {
      var _id = $(obj).parent().parent().parent().attr('id').substr(3);
      $.post(act, {"id": _id, "result": _result}, function (data) {
        if (data == "1") {
          msg($.i18n.prop('sys.msg.refresh'))
        }
        setTimeout(function () {
          reloadPage();
        }, 3000);
      }, "json");
    }
  });
};

function getExplorer() {
  var explorer = window.navigator.userAgent;
  //ie
  if (explorer.indexOf(".NET") >= 0) {
    return 'ie';
  } else {
    return 'notIe';
  }

}

function exp(url) {
  confirmMsg($.i18n.prop("sys.info"), $.i18n.prop("sys.exp"), function (r) {
    if (r) {
      window.location.href = url;
    }
  });
}

var tableToExcel = (function () {
  var uri = 'data:application/vnd.ms-excel;base64,',
    template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>';
  base64 = function (s) {
    return window.btoa(unescape(encodeURIComponent(s)))
  },
    format = function (s, c) {
      return s.replace(/{(\w+)}/g,
        function (m, p) {
          return c[p];
        })
    }
  return function (table, name) {
    if (!table.nodeType) table = document.getElementById(table);
    var ctx = {worksheet: name || 'sheet', table: table.innerHTML}
    document.getElementById("export").href = uri + base64(format(template, ctx));
    document.getElementById("export").download = table.title;

  }
})()

doExport = function (tableid) {
  if (getExplorer() == 'ie') {
    var curTbl = document.getElementById(tableid);
    var oXL = new ActiveXObject("Excel.Application");
    //创建AX对象excel
    var oWB = oXL.Workbooks.Add();
    //获取workbook对象
    var xlsheet = oWB.Worksheets(1);
    //激活当前sheet
    var sel = document.body.createTextRange();
    sel.moveToElementText(curTbl);
    //把表格中的内容移到TextRange中
    //sel.select();
    //全选TextRange中内容
    sel.execCommand("Copy");
    //复制TextRange中内容
    xlsheet.Paste();
    //粘贴到活动的EXCEL中
    oXL.Visible = true;
    //设置excel可见属性
    try {
      var fname = oXL.Application.GetSaveAsFilename(curTbl.title + ".xls", "Excel Spreadsheets (*.xls), *.xls");
    } catch (e) {
      print("Nested catch caught " + e);
    } finally {
      oWB.SaveAs(fname);
      oWB.Close(savechanges = false);
      xls.visible = false;
      oXL.Quit();
      oXL = null;
      oXL.Selection.Borders.Weight = 2;
      //结束excel进程，退出完成
      window.setInterval("Cleanup();", 1);
    }
  }
  else {
    tableToExcel(tableid)
  }
}


ajUpdate = function (obj, _name, _result, act) {
  confirmMsg($.i18n.prop("sys.info"), $.i18n.prop("sys.confirm"), function (r) {
    if (r) {
      var _id = $(obj).parent().parent().parent().attr('id').substr(3);
      $.post(act, {"id": _id, "field": _name, "result": _result}, function (data) {
        if (data == "1") {
          msg($.i18n.prop('sys.msg.refresh'))
        }
        setTimeout(function () {
          reloadPage();
        }, 3000);
      }, "json");
    }
  });
};

removeByValue = function (arr, val) {
  for (var i = 0; i < arr.length; i++) {
    if (arr[i] == val) {
      arr.splice(i, 1);
      break;
    }
  }
}


ajaxUpdate = function (id, fieldName, fieldValue) {
  confirmMsg($.i18n.prop("sys.info"), $.i18n.prop('sys.confirm'), function (r) {
    if (r)
      $.post('ajaxUpdate.htm', {id: id, fieldName: fieldName, fieldValue: fieldValue}, function (data) {
        if (data.success)
          reloadPage();
      }, 'json');
  });
}
/**
 *
 * @param obj 操作对象
 * @param val 值
 * @param acl 地址
 * @param url 操作完成后跳转地址
 * */
ajSubInput = function (obj, val, option, acl, url) {
  confirmMsg($.i18n.prop("sys.info"), $.i18n.prop("sys.confirm"), function (r) {
    if (r) {
      var _id = $("#" + obj).val()
      var _option = $("#" + option).val()
      $.post(acl, {"id": _id, "result": val, "option": _option}, function (data) {
        if (data == "1") {
          msg($.i18n.prop('sys.msg.refresh'))
          setTimeout(function () {
            window.location.href = url;
          }, 3000)
        }

      }, 'json')
    }
  })
}


ajaxSub = function (msg, form, acl, url) {
  confirmMsg($.i18n.prop('sys.info'), $.i18n.prop('sys.msg.confirm', msg), function (r) {
    if (r) {
      $.post(acl, $("#" + form).serialize(), function (data) {
        if (data.success) {
          alertMsg(msg + '已成功!点击确定返回列表页面', function () {
            window.location.href = (url == null ? 'list.htm' : url);
          });
          /*msg(msg+"已成功,3秒后页面自动刷新");
          setTimeout(function () {
              window.location.href = (url == null ? 'list.htm' : url);
          }, 3000)*/
        }
      }, 'json');
    }
  })
}

ajOp = function (obj, act) {
  var msg = $(obj).prop("title");
  confirmMsg($.i18n.prop('sys.info'), $.i18n.prop('sys.msg.confirm', msg), function (r) {
    if (r) {
      var _id = $(obj).parent().parent().parent().attr('id').substr(3);
      $.post(act, {"id": _id}, function (data) {
        if (data.success) {
          alertMsg(msg + '已成功!点击确定刷新页面', function () {
            reloadPage();
          });
        } else {
          msg(data.message);
        }
      }, "json");
    }
  })
}

ajCancel = function (obj, entityName, className, act) {
  confirmMsg($.i18n.prop('sys.info'), $.i18n.prop('sys.msg.cancel'), function (r) {
    if (r) {
      if (!act) {
        act = window.location.href.replace("list.htm", "input.htm");
      }
      var _id = $(obj).parent().parent().parent().attr('id').substr(3);
      $.post(base + "/common/ajCancel.htm", {
        "id": _id, "entity": entityName,
        "className": className, "act": act
      }, function (data) {
        if (data.success) {
          alertMsg($.i18n.prop('sys.msg.cancelOK'), function () {
            reloadPage();
          });
        } else {
          msg(data.message);
        }
      }, "json");
    }
  })
}


checkNum = function (obj) {
  obj.value = obj.value.replace(/[^\-?{0,}\d{1,}\.\d{1,}|\d{1,}]/g, '');
}

clearInput = function () {
  $("form input[type='text']:not([readonly])").val("");
}

function getDate(date) {
  var curYear = date.getFullYear()
  var curMonth = date.getMonth() + 1;  //获取当前月份(0-11,0代表1月)
  var curDay = date.getDate();       //获取当前日(1-31)
  return curYear + '-' + (curMonth < 10 ? ('0' + curMonth) : curMonth) + '-' + (curDay < 10 ? ('0' + curDay) : curDay);
}

function getNow(needHour) {
  var curDate = new Date();
  var curYear = curDate.getFullYear()
  var curMonth = curDate.getMonth() + 1;  //获取当前月份(0-11,0代表1月)
  var curDay = curDate.getDate();       //获取当前日(1-31)
  var curWeekDay = curDate.getDay();    //获取当前星期X(0-6,0代表星期天)
  var curHour = curDate.getHours();      //获取当前小时数(0-23)
  var curMinute = curDate.getMinutes();   // 获取当前分钟数(0-59)
  var curSec = curDate.getSeconds();      //获取当前秒数(0-59)
  var timeStr = $.i18n.prop('sys.date.format', curYear, curMonth, curDay);
  /*switch (curWeekDay) {
    case 0:
      timeStr += '星期日';
      break;
    case 1:
      timeStr += '星期一';
      break;
    case 2:
      timeStr += '星期二';
      break;
    case 3:
      timeStr += '星期三';
      break;
    case 4:
      timeStr += '星期四';
      break;
    case 5:
      timeStr += '星期五';
      break;
    case 6:
      timeStr += '星期六';
      break;
  }*/
  if (typeof(needHour) != 'undefined') {
    if (curHour < 10) {
      if (curMinute < 10) {
        if (curSec < 10) {
          timeStr += ' 0' + curHour + ':0' + curMinute + ':0' + curSec;
        }
        else {
          timeStr += ' 0' + curHour + ':0' + curMinute + ':' + curSec;
        }
      }
      else {
        if (curSec < 10) {
          timeStr += ' 0' + curHour + ':' + curMinute + ':0' + curSec;
        }
        else {
          timeStr += ' 0' + curHour + ':' + curMinute + ':' + curSec;
        }
      }
    }
    else {
      if (curMinute < 10) {
        if (curSec < 10) {
          timeStr += ' ' + curHour + ':0' + curMinute + ':0' + curSec;
        }
        else {
          timeStr += ' ' + curHour + ':0' + curMinute + ':' + curSec;
        }
      }
      else {
        if (curSec < 10) {
          timeStr += ' ' + curHour + ':' + curMinute + ':0' + curSec;
        }
        else {
          timeStr += ' ' + curHour + ':' + curMinute + ':' + curSec;
        }
      }
    }
  }
  return timeStr;
}

delObj = function (obj, len) {
  if (!len) {
    len = 1;
  }
  if ($(obj).index() == 0 || $(obj.replace(":last", "")).length == len) {
    $(obj).find("input").val("");
    return false;
  }
  $(obj).remove();
}

addObj = function (obj, type, objType) {
  var idx = parseInt($(obj).last().find("input[name*='[']:eq(0)").attr("name").replace(/[^\d]/g, ''));
  var html = $(obj).last().clone();
  html.find("script").remove();
  html.find("div.uploadFile div span,a").remove();
  html.find("div.uploadFile input[name=upload]").show();
  html.find("input").removeAttr("value");
  var nextIdx = (parseInt(idx) + 1);
  var input = "\\[" + idx + "\\]";
  if (typeof(objType) == "undefined") {
    objType = "";
  }
  var content = html.prop("outerHTML").replace(eval("/" + objType + input + "/g"), objType + "[" + nextIdx + "]");
  if (typeof(type) != 'undefined' && type != null && type.length > 0) {
    content = content.replace(eval("/" + type + idx + "/g"), type + nextIdx)
      .replace(eval("/" + type + "File" + idx + "/g"), type + "File" + nextIdx);
  }
  $(obj).last().after(content);
  $(obj).last().find("input[name*='Date'][type!='hidden']").bind('click', WdatePicker);
}

caleTableTotal = function (num, id, numLen) {
  var trs = $(id);
  if (trs.length > 1) {
    for (var i = 0; i < num.length; i++) {
      if (num[i]) {
        var total = 0;
        trs.find("td:eq(" + num[i] + ")").each(function (idx, obj) {
          if (idx < trs.length - 1) {
            var val = Number($(obj).html());
            if ($(obj).find("input").length > 0) {
              val = Number($(obj).find("input").val());
            }
            if (!isNaN(val)) {
              total = Number(total) + Number(val);
            }
          }
        })
        if (numLen) {
          total = total.toFixed(numLen);
        }
        trs.last().find("td:eq(" + i + ")").html(total);
      }
    }
  }
}


function loadProperties() {
  jQuery.i18n.properties({//加载资浏览器语言对应的资源文件
    name: 'strings', //资源文件名称
    path: base + '/resources/i18n/', //资源文件路径
    mode: 'map' //用Map的方式使用资源文件中的值
  });
}

function toView(id, act, inputId) {
  if (!inputId) {
    inputId = "id";
  }
  if ($("#viewForm").length == 0) {
    $("body").append(
      "<form action='' method='post' id='viewForm'>" +
      "  <input name='" + inputId + "' value='' type='hidden'>" +
      "  <input name='op' value='v' type='hidden'>" +
      "</form>");
  }
  $("#viewForm input[name='" + inputId + "']").val(id);
  $("#viewForm").attr("action", act ? act : 'input.htm');
  $("#viewForm").submit();
}


/**
 * 比较时间
 * @param d1
 * @param d2
 * @returns 1 >  0 =  -1 <
 */
function compareDate(d1, d2) {
  var date1 = new Date(d1.replace(/\-/g, "/")), date2 = new Date(d2.replace(/\-/g, "/"));
  if (date1.getTime() > date2.getTime()) {
    return 1;
  } else if (date1.getTime() == date2.getTime()) {
    return 0;
  } else {
    return -1;
  }
}


/**
 * 拓展Date的格式化日期函数
 *    调用此方法：如 new Date().Format("yyyy-MM-dd")
 * */
Date.prototype.Format = function(fmt) {
  var o = {
    "M+": this.getMonth() + 1, //月份
    "d+": this.getDate(), //日
    "h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时
    "H+": this.getHours(), //小时
    "m+": this.getMinutes(), //分
    "s+": this.getSeconds(), //秒
    "q+": Math.floor((this.getMonth() + 3) / 3), //季度
    "S": this.getMilliseconds() //毫秒
  };
  var week = {
    "0": "/u65e5",
    "1": "/u4e00",
    "2": "/u4e8c",
    "3": "/u4e09",
    "4": "/u56db",
    "5": "/u4e94",
    "6": "/u516d"
  };
  if(/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
  }
  if(/(E+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, ((RegExp.$1.length > 1) ? (RegExp.$1.length > 2 ? "/u661f/u671f" : "/u5468") : "") + week[this.getDay() + ""]);
  }
  for(var k in o) {
    if(new RegExp("(" + k + ")").test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    }
  }
  return fmt;
}