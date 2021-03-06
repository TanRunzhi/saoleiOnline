<%--
&lt;%&ndash;
  Created by IntelliJ IDEA.
  User: fan_jinliang
  Date: 2019/2/21
  Time: 10:16
  To change this template use File | Settings | File Templates.
&ndash;%&gt;
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE htmlPUBLIC"-//W3C//DTD HTML 4.01 Transitional//EN""http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type"content="text/html; charset=ISO-8859-1">
  <title>WebSocket/SockJS Echo Sample (Adapted from Tomcat's echo sample)</title>
  <style type="text/css">

  </style>
  <script src="/resources/js/sockjs.min.js"></script>
  <script type="text/javascript">
    var ws = null;
    var url = null;
    var baseUrl = "localhost:8088";
    var transports = [];
    function setConnected(connected) {
      document.getElementById('connect').disabled = connected;
      document.getElementById('disconnect').disabled = !connected;
      document.getElementById('echo').disabled = !connected;
    }
    function connect() {

      var ws;

      if ('WebSocket' in window) {
        ws = new WebSocket("ws://" + baseUrl+ "/websocket.html");
      } else if ('MozWebSocket' in window) {
        // ws = new MozWebSocket("ws://" + baseUrl+ "/websocket.html");
      } else {
        ws = new SockJS("http://" + baseUrl+ "/sockjs/websocket.html");
      }

      ws.onopen = function () {
        setConnected(true);
        log('Info: connection opened.');
      };
      ws.onmessage = function (event) {
        log('Received: ' + event.data);
      };
      ws.onclose = function (event) {
        setConnected(false);
        log('Info: connection closed.');
        log(event);
      };
    }
    function disconnect() {
      if (ws != null) {
        ws.close();
        ws = null;
      }
      setConnected(false);
    }
    function echo() {
      if (ws != null) {
        var message = document.getElementById('message').value;
        log('Sent: ' + message);
        ws.send(message);
      } else {
        alert('connection not established, please connect.');
      }
    }
    function updateUrl(urlPath) {
      if (urlPath.indexOf('sockjs') != -1) {
        url = urlPath;
        document.getElementById('sockJsTransportSelect').style.visibility ='visible';
      }
      else {
        if (window.location.protocol =='http:') {
          url = 'ws://' + window.location.host + urlPath;
        } else {
          url = 'wss://' + window.location.host + urlPath;
        }
        document.getElementById('sockJsTransportSelect').style.visibility ='hidden';
      }
    }
    function updateTransport(transport) {
      transports = (transport == 'all') ? [] : [transport];
    }
    function log(message) {
      var console = document.getElementById('console');
      var p = document.createElement('p');
      p.style.wordWrap = 'break-word';
      p.appendChild(document.createTextNode(message));
      console.appendChild(p);
      while (console.childNodes.length > 25) {
        console.removeChild(console.firstChild);
      }
      console.scrollTop = console.scrollHeight;
    }
  </script>
</head>
<body>
<div>
  <div id="connect-container">
    <input id="radio1"type="radio"name="group1"onclick="updateUrl('/websocket');">
    <label for="radio1">W3C WebSocket</label>
    <br>
    <input id="radio2"type="radio"name="group1"onclick="updateUrl('/sockjs/websocket');">
    <label for="radio2">SockJS</label>
    <div id="sockJsTransportSelect" style="visibility:hidden;">
      SockJS transport:
      <select onchange="updateTransport(this.value)">
        <option value="all">all</option>
        <option value="websocket">websocket</option>
        <option value="xhr-polling">xhr-polling</option>
        <option value="jsonp-polling">jsonp-polling</option>
        <option value="xhr-streaming">xhr-streaming</option>
        <option value="iframe-eventsource">iframe-eventsource</option>
        <option value="iframe-htmlfile">iframe-htmlfile</option>
      </select>
    </div>
    <div>
      <button id="connect" onclick="connect();">Connect</button>
      <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
    </div>
    <div>
      <textarea id="message" style="width:350px">Here is a message!</textarea>
    </div>
    <div>
      <button id="echo" onclick="echo();" disabled="disabled">Echo message</button>
    </div>
  </div>
  <div id="console-container">
    <div id="console"></div>
  </div>
</div>
</body>
</html>
--%>
