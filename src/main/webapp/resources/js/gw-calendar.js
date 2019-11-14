$(function () {

  $(".r_btn").click(function () {
    var now = new Date();
    gwCalendar.show(now.getFullYear(), now.getMonth(), now.getDate());
    $(".calendar_cont").show();
  });

  $(".calendar_cont .esc_i").click(function () {
    $(".calendar_cont").hide();
  });

})

var gwCalendar = {
  a: function (year) {
    return (year % 100 == 0 ? (year % 400 == 0 ? 1 : 0) : (year % 4 == 0 ? 1 : 0));
  }, //是否为闰年

  show: function (y, m, d) {
    var nstr = new Date(); //当前Date资讯
    var ynow;
    var mnow;
    var dnow;
    if (y == null)
      ynow = nstr.getFullYear(); //年份
    else
      ynow = y;
    if (m == null)
      mnow = nstr.getMonth(); //月份
    else
      mnow = m;
    if (d == null)
      dnow = nstr.getDate(); //今日日期
    else
      dnow = d;

    var n1str = new Date(ynow, mnow, 1); //当月第一天Date资讯

    var firstDay = n1str.getDay(); //当月第一天星期几

    var m_days = [31, 28 + this.a(ynow), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]; //各月份的总天数

    var tr_str = 6; //表格所需要行数(固定6行)
    var htmlContent = '';
    //打印表格第一行（有星期标志）
    htmlContent += $.i18n.prop('calendar.title');
    for (var i = 0; i < tr_str; i++) { //表格的行
      htmlContent += "<tr>";
      for (var k = 0; k < 7; k++) { //表格每行的单元格
        var idx = i * 7 + k; //单元格自然序列号
        var date_str = idx - firstDay + 1; //计算日期
        //过滤无效日期（小于等于零的、大于月总天数的）
        //(date_str <= 0 || date_str > m_days[mnow]) ? date_str = "&nbsp;" : date_str = idx - firstday + 1;
        //打印日期：今天底色为红
        htmlContent += "<td";
        if (date_str <= 0) {
          htmlContent += " class='d_up'";
          date_str = Number(m_days[mnow]) + Number(date_str);
        } else if (date_str > m_days[mnow]) {
          htmlContent += " class='d_up'";
          date_str = Number(date_str) - Number(m_days[mnow]);
        } else if (ynow == nstr.getFullYear() && mnow == nstr.getMonth() && date_str == nstr.getDate()) {
          htmlContent += "  class='on'";
        } else {
          htmlContent += "  class='cur'";
        }
        htmlContent += "><span>" + date_str + "</span></td>";
      }
      htmlContent += "</tr>"; //表格的行结束
    }
    $('#gw-calendar').html(htmlContent);
    $('#nYear').html(ynow);

    $('#nMonth').html(Number(mnow) + 1);
    //buildEventCalendar(ynow, Number(mnow) + 1);
  },
  nextYear: function () {
    this.show(Number($('#nYear').text()) + 1, Number($('#nMonth').html()) - 1, null);
  },
  prevYear: function () {
    this.show(Number($('#nYear').html()) - 1, Number($('#nMonth').html()) - 1, null);
  },
  prevMonth: function () {
    var year = Number($('#nYear').text());
    var month = Number($('#nMonth').text()) - 2;
    if (month < 0) {
      month = 11;
      year = year - 1;
    }
    this.show(year, month, null);
  },
  nextMonth: function () {
    var year = Number($('#nYear').text());
    var month = Number($('#nMonth').text());
    if (month >= 12) {
      month = 0;
      year = year + 1;
    }
    this.show(year, month, null);
  }
}
