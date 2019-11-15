<%@ page import="com.gwideal.core.config.Constants" %><%--
  Created by IntelliJ IDEA.
  User: tan_runzhi
  Date: 2019/2/21
  Time: 10:16
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE htmlPUBLIC"-//W3C//DTD HTML 4.01 Transitional//EN""http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type"content="text/html; charset=ISO-8859-1">
  <title>WebSocket/SockJS Echo Sample (Adapted from Tomcat's echo sample)</title>
  <style type="text/css">

  </style>
  <link href="//netdna.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
  <script src="/resources/js/jquery-3.2.1.min.js"></script>
  <script src="/resources/js/sockjs.min.js"></script>
</head>
<body>
<div>
  <div id="user-container" style="height: 65px;margin-bottom: 5px;background-color: aqua;">

  </div>
  <div id="connect-container">
    <table></table>

  </div>
  <div id="console-container" style="height: 25px;margin-top: 5px;background-color: aqua">
    <div id="console"></div>
  </div>
</div>
</body>

<style>
  .table{
    margin: 0px auto;
  }
  .table tr{

  }
  .table td{
    background-color: rgba(222,184,135,0.5);
  }
  .center{
    margin: auto;
  }
  .player1,td[player='1']{
    background-color: aqua;
  }
  .player2,td[player='2']{
    background-color: hotpink;
  }
</style>
<script type="text/javascript">
  TABLE_INIT = '<%= Constants.TABLE_INIT %>';
  TABLE_PRINT = '<%= Constants.TABLE_PRINT %>';
  USER_INFO = '<%= Constants.USER_INFO %>';
  TD_CLICK = '<%= Constants.TD_CLICK %>';
  TD_CHANGE = '<%= Constants.TD_CHANGE %>';
  MSG_SEND_MYSELF = '<%= Constants.MSG_SEND_MYSELF %>';
  MSG_SEND_ANOTHER = '<%= Constants.MSG_SEND_ANOTHER %>';
  MSG_SEND_ALL = '<%= Constants.MSG_SEND_ALL %>';

  var boomHtml = " <i class=\"fa fa-spin fa-gear\"></i> ";

  var row = 16 , col = 30 , boom = 99 ;
  var player ;
  var tableArr = null;


  $(function(){
    connect();
  })

  wsOpenCallBackFunction = function(){
    if(!tableArr){
      resetArr();
    }
  }

  wsMessageCallBackFunction = function(msg){
    log('Received: ' + msg);
    var obj = JSON.parse(msg);
    if(obj.message == TABLE_PRINT){
      tableArr = obj.data
      printTable(tableArr)
    }else if(obj.message == USER_INFO){
      player = obj.data.player
    }else if(obj.message == TD_CHANGE ){
      var data = obj.data
      $.each(data,function(ind,cell){
        var td = $("td[r=" + cell.row + "][c=" + cell.col + "]")
        // 0 默认 1 被右键插旗  2 被左键点开  -1 左键点到炸弹
        td.attr("player",cell.player)
        if(cell.state == -1){
          // endGame();
        }else if(cell.state == 1){
          td.html(boomHtml)
        }else if(cell.state == 2){
          td.attr("player",cell.player)
          td.attr("num",cell.num ? cell.num : "")
        }
      })
    }
  }

  resetArr = function(){
    var msg = JSON.stringify(getSendMsgObj(TABLE_INIT,{row:row,col:col,boom:boom},MSG_SEND_MYSELF));
    sendMsg(msg);
  }

  printTable = function(){
    var windowHeight = window.innerHeight
    var html = "<table id='dataTable' class='table'>";
    for(var r = 1 ; r <= row ; r++ ){
      html += "<tr height='" + parseInt(100/row) + "%' r=" + r + ">";
      for(var c = 1 ; c <= col ; c++ ){
        var cell = tableArr[r-1][c-1]
        html += "<td width='" + parseInt(100/col) + "%' r=" + r + " c=" + c + " style='text-align: center'  >" +
          ( cell.hasBoom ? " <i class=\"fa fa-spin fa-gear\"></i> " : (cell.num ? cell.num : "") ) +
          "</td>";
      }
      html += "</tr>";
    }
    html +=  "</table>";
    //
    var height = windowHeight - 100 - 30
    $("#connect-container").html(html)
    //
    $("#dataTable").css("height",height)
    $("#dataTable").css("width",height * col / row)
    //
    document.getElementById("dataTable").oncontextmenu = function(){return false};
    $("#dataTable tr").find("td").each(function(){
      this.onmousedown = function (e) {
        cellClick(this,e.button)
      }
    })
  }

  cellClick =function(td,clickButton){
    var a = function(attr){return parseInt($(td).attr(attr))}
    var msg = JSON.stringify(getSendMsgObj(TD_CLICK,{row:a("r"),col:a("c"),player:player,button:clickButton},MSG_SEND_ALL));
    if( !$(td).attr("num") ){
      sendMsg(msg)
    }
  }

  getCellObj = function(row,col,num,hasBoom,state,player){
    return {
      row : row ,
      col : col ,
      num : num ? num : 0 , // 0-8
      hasBoom : hasBoom ? true : false ,
      state : state ? state : 0 , // 0 默认  1 插旗
      player : player ? player : null   //  1 / 2
    }
  }

  getSendMsgObj = function(msg,data,sendTo){
    return {
      msg : msg ,
      data : data ,
      sendTo : sendTo ? sendTo : 2
    }
  }



  <%-- WebSocket 相关 js --%>
  var ws = null;
  connect = function () {
    // ?????? ?? WebSocket
    if ('WebSocket' in window) {
      ws = new WebSocket("ws://localhost:8088/websocket.html");
    } else if ('MozWebSocket' in window) {
      ws = new MozWebSocket("ws://localhost:8088/websocket.html");
    } else {
      ws = new SockJS("http://localhost:8088/sockjs/websocket.html");
    }

    //websocket = new SockJS("http://localhost:8084/SpringWebSocketPush/sockjs/websck");
    ws.onopen = function () {
      log('Info: connection opened.');
      wsOpenCallBackFunction()
    };
    ws.onmessage = function (event) {
      wsMessageCallBackFunction(event.data)
    };
    ws.onerror = function (evnt) {
      log("  websocket.onerror  ");
    };
    ws.onclose = function (event) {
      log('Info: connection closed.');
      log(event);
    };
    // 关闭方法 ws.close();
  }

  log = function (message) {
    var console = document.getElementById('console');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(message));
    console.innerHTML = ""
    console.appendChild(p);
    while (console.childNodes.length > 25) {
      console.removeChild(console.firstChild);
    }
    console.scrollTop = console.scrollHeight;
  }

  sendMsg = function(msg){
    if(ws.readyState == 1){
      console.log( " send msg : " + msg)
      ws.send(msg);
    }else{
      alert("正在连接服务器......")
    }
  }

  /*  console.log("window.screen.height : " + window.screen.height)
  console.log("window.screen.availHeight : " + window.screen.availHeight)
  console.log("window.innerHeight : " + window.innerHeight)
  console.log("window.outerHeight : " + window.outerHeight)
  console.log("window.screenX : " + window.screenX)
  console.log("window.screenY : " + window.screenY)
  console.log("window.screenLeft : " + window.screenLeft)
  console.log("window.screenTop : " + window.screenTop)*/
</script>
</html>
