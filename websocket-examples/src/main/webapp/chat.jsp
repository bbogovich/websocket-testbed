<!doctype HTML>
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=9" /><!-- stop intranet IE9 clients from defaulting to IE7 rendering mode -->
		<style>
#chatContainer{
	border-radius: 10px 10px 10px 10px;
	behavior: url(PIE.htc);
	border-color: black;
	border-width: 2px;
	border-style: solid;
	padding: 10px 10px 10px 10px;
	width: 650px;
	background-color: #DED7DE;
}
#chatContainer h2{
	text-align: center;
	margin: 5px 0px 10px 0px;
	padding-top:5px;padding-bottom:5px;
	font-size: 14pt;
	font-family: Arial,sans-serif;
	background-color: white;
	border-radius: 10px 10px 10px 10px;
	behavior: url(PIE.htc);
	border-width:2px;
	border-color: gray;
	border-style: solid;
}
#chatOutput{
	height: 250px;
	border: 1px black solid;
	overflow-y: scroll;
	width: 520px;
	overflow-x: auto;
}
#userList{
	float:left;
	width: 110px;
	text-align: center;
	background-color: #f0f0f0;
	border-right: 1px gray solid;
}
#chatContainer .inner{
	background-color: #f0f0f0;
	padding: 5px 5px 5px 5px;
	border: 1px #0a0a0a solid;
	border-radius: 0px 0px 5px 5px;
	behavior: url(PIE.htc);
	
}
#chatContainer #chatInputContainer{
	margin-top: 5px;
}
#chatContainer #chatInput input{
	border: 1px gray solid;
	width: 520px;
}
#chatContainer #chatInput button{
	width: 100px;
}
#chatOutput{
	float:left;
	background-color: white;
}
#chatOutput .receivedWebSocketMessage{
	color: gray;
	font-style: italic;
}
#userList select{
	height: 215px;
	width: 100px;
}
.clear{
	clear:both;
}
#chatContainer h3{
	font-size: 12pt;
	margin: 5px 0 5px 5px;
	font-family: Arial,sans-serif;
	background-color: white;
	border-radius: 7px 7px 7px 7px;
	behavior: url(PIE.htc);
	border-width: 2px;
	border-style: solid;
	border-color: lightgray;
	width: 98px
}
#chatOutput .label{width: 75px;background-color: beige;color:black;font-weight:bold;float:left;padding-left:2px;}
#chatOutput .text{background-color: white;color:black;font-weight:normal;padding-left: 5px;float:left;}
#chatOutput .message{border-bottom: 1px gray solid;}
.console{
	position: absolute;
	top: 0px;
	right: 0px;
	width: 500px;
	height: 500px;
	border: 2px gray solid;
	overflow: scroll;
	font-family: monospace;
	font-size: 10pt;
}
.console div{
	border-bottom: 1px gray solid;
	padding: 5px 5px 5px 5px;
}
.console .log{
	color: black;
}
.console .warn{
	color: #0000ff;
}
.console .error{
	color: #ff0000;
	font-weight: bold;
}
.header{
	margin-right: 500px;
}
.hidden{
	display:none;
}
		</style>
		<script>

		</script>
		<script>
var SESSION_ID;
function loadScript(scriptURL /*,Function <callback>*/){
	console.log("loading "+scriptURL);
	var callback=(arguments.length>1)?arguments[1]:null;
	var ele=document.createElement('SCRIPT');
	if(ele.readyState==null){
		ele.addEventListener("load",function(e){
			if(typeof(callback)=='function')callback()
		},false);
	}else{
		ele.onreadystatechange=function(){
			if((ele.readyState=='loaded')||(ele.readyState=='complete')){
				setTimeout("",0);
				if(typeof(callback)=='function')callback();
			}
		}
	};
	ele.setAttribute('type','text/javascript');
	ele.setAttribute('language','JavaScript');
	ele.setAttribute('src',scriptURL);
	document.getElementsByTagName('head')[0].appendChild(ele);
	return true;
}
function init(){
	/*Console replacement for IE6-8.  Toggle with F12*/
	(function(){
		if(!window.console){
			var consoleEle = document.createElement("div");
			document.body.appendChild(consoleEle);
			consoleEle.className = "console";
			function writeMsg(msg,level){
				var ele = document.createElement("div");
				ele.appendChild(document.createTextNode(msg));
				ele.className=level;
				consoleEle.appendChild(ele);
				consoleEle.scrollTop = consoleEle.scrollHeight;
			}
			window.console={
				log:function(msg){
					writeMsg(msg,"log");
				},
				warn:function(msg){
					writeMsg(msg,"warn");
				},
				error:function(msg){
					writeMsg(msg,"error");
				},
				debug:function(msg){
					writeMsg(msg,"debug");
				}
			};
			function toggleConsole(e){
				if(e.keyCode==123){
					consoleEle.className = consoleEle.className=="console hidden"?"console":"console hidden";
				}
			}
			if(window.attachEvent){
				window.document.body.attachEvent("onkeyup",toggleConsole);
			}else{
				window.document.body.addEventListener("keyup",toggleConsole);
			}
		}
	})();
	/*XMLHttpRequest Wrapper for IE6-8 and IE9 with "native XMLHttpRequest" disabled*/
	if(!window.XMLHttpRequest){
		console.log("declare XMLHttpRequest");
		XMLHttpRequest = function(){
			for (var i=0;a=["Microsoft.XMLHTTP","Msxml3.XMLHTTP","Msxml2.XMLHTTP","Msxml.XMLHTTP"];i++){
				try{
					return new ActiveXObject(a[i]);
				}catch(e){
					console.error(a[i]+"-"+e.message);
				};
			}
			console.error("Unable to create an XMLHttpRequest object.");
			return null;
		}
	}
	if(!window.JSON){
		loadScript("json2.js");
	}
	var request = new XMLHttpRequest();
	request.open("POST","chatsocket/initialize.do",false);
	request.send(null);
	console.log(request.responseText);
	SESSION_ID=request.responseText;
	var usernameEle = document.getElementById("userName");
	if(typeof(usernameEle.addEventListener)!="undefined"){
		usernameEle.addEventListener("keydown",function(e){if(e.keyCode==13){register();}},false);
	}else{
		usernameEle.attachEvent("onkeydown",function(e){if(e.keyCode==13){register();}});
	}
}

function getStatus(){
	var request = new XMLHttpRequest();
	request.open("POST","chatsocket/status.do",true);
	request.send(null);
	request.onreadystatechange=function(){
		if(request.readyState==4){
			console.log(request.responseText);
			for (var i in request){
				console.log(i+":  "+request[i]);
			}
		}
	}
}
		/*
		*/
ChatController = new function(){
	var $this=this;
	var websocket=null;
	var port=8084;
	
	this.outputContainer=null;
	this.connect=function(){
		openWebSocket();
	}
	this.disconnect = function(){
		closeWebSocket();
	}
	this.sendMessage=function(data){
		if(!websocket){
			alert("websocket is not yet open");
		}else{
			websocket.send("websocket.example.chat.request.ChatMessageRequest|"+JSON.stringify({sessionId:SESSION_ID,chatMessage:data}));
		}
	}
	function onMessage(/*String*/data){
		console.log(data);
		try{
			var message = JSON.parse(data);
			var messageType = message.messageType;
			var messageHandler = messageHandlers[messageType];
			if(typeof(messageHandler)=="function"){
				messageHandler(message);
			}else{
				console.warn("Unregistered message type "+message.messageType);
			}
		} catch(e) {
			console.warn("Unable to parse incoming message - "+data+" -- "+e.message);
		}
	}
	function openWebSocket(){
		if(!window.WebSocket){
			console.log("native websockets are not available.");
			if(window.MozWebSocket){
				window.WebSocket = window.MozWebSocket;
				openWebSocket();
				return;
			}else{
				console.log("load the flash implementation");
				window.WEB_SOCKET_SWF_LOCATION = "WebSocketMain.swf";
				window.WEB_SOCKET_DEBUG = true;
				loadScript("swfobject.js",function(){
					console.log("swfobject.js loaded")
					loadScript("FABridge.js",function(){
						console.log("FABridge.js loaded")
						loadScript("web_socket.js",function(){
							console.log("web_socket.js loaded");
							WebSocket.__initialize();
							setTimeout(openWebSocket,100);
						});
					});
				});
				console.log("waiting for dependencies");
			}
		}else{
			if(!websocket){
				console.log("Instantiate new WebSocket object");
				websocket = new WebSocket("ws://"+window.location.hostname+":"+port);
				$this.websocket=websocket;
				websocket.onopen = function(e){
					websocketOpen=true;
					console.log("websocket.onopen");
				}
				websocket.onmessage = function(e){
					console.log("websocket.onmessage");
					console.log(e);
					onMessage(e.data);
				}
				websocket.onclose=function(e){
					websocket=null;
				}
				console.log(websocket);
			}
		}
	}	

	function closeWebSocket(){
		if(websocket){
			websocket.close();
		}
	}
	var messageHandlers={
		"websocket.example.chat.response.RegistrationSuccessResponse":function(/*Object*/message){
			console.log("Registration Successful");
		},
		"websocket.example.chat.response.InfoMessage":function(/*Object {message:String <message>,messageType:String <messageType>}*/message){
			var msgDiv=document.createElement("div");
			msgDiv.appendChild(document.createTextNode(message.message));
			msgDiv.className = "receivedWebSocketMessage";
			var chatWindow =document.getElementById("chatOutput");
			chatWindow.appendChild(msgDiv);
			chatWindow.scrollTop = chatWindow.scrollHeight;
		},
		"websocket.example.chat.response.UserListUpdateMessage":function(message){
			var userListSelect = document.getElementById("chatUserListUsers");
			var options=userListSelect.options;
			var userList = message.userlist;
			for (var i=0,ct=userList.length;i<ct;i++){
				var user=userList[i];
				var sessionId=user["sessionId"];
				console.log("looking for user with session id "+sessionId);
				for (var j=0,found=false,opts=userListSelect.options,ct2=opts.length;j<ct2&&!found;j++){
					found=opts[j].value==sessionId;
				}
				if(found){
					opts[j-1].text=user.userName;
				}else{
					var opt = document.createElement("option");
					opt.value=sessionId;
					opt.text=user.userName;
					userListSelect.add(opt);
				}
			}
		},
		"websocket.example.chat.response.ChatUserMessage":function(message){
			var sendingUser=message.userName;
			var messageText=message.message;
			var msgDiv = document.createElement("div");
			var labelDiv=document.createElement("div");
			var textDiv = document.createElement("div");
			var clearDiv=document.createElement("div");
			clearDiv.className="clear";
			labelDiv.className="label";
			textDiv.className="text";
			msgDiv.className="message";
			textDiv.appendChild(document.createTextNode(messageText));
			labelDiv.appendChild(document.createTextNode(sendingUser));
			msgDiv.appendChild(labelDiv);
			msgDiv.appendChild(textDiv);
			msgDiv.appendChild(clearDiv);
			var chatWindow =document.getElementById("chatOutput");
			chatWindow.appendChild(msgDiv);
			chatWindow.scrollTop = chatWindow.scrollHeight;
		}
		
		/*
		
		{"userSessionId":"D327F9A8EBFEDB91B891408ECF13C6C6","message":"hello","userName":"firefox","messageType":"websocket.example.chat.response.ChatUserMessage"}
chat.jsp (line 1
		*/
	};
	this.register=function(username){
		if(websocket){
			websocket.send(
					"websocket.example.chat.request.RegisterRequest|"+
					JSON.stringify({
							transactionId:new Date().getTime(),
							userName:username,sessionId:SESSION_ID
						}));
		}
	}
}
function sendChatMessage(form){
	ChatController.sendMessage(form.message.value)
	form.message.value="";
}
function register(){
	var username = document.getElementById("userName").value;
	if(username&&SESSION_ID){
		ChatController.register(username);
	}
}

		</script>
	</head>
	<body onload="init()">
		<div class="header">
			<h1>WebSocket Chat test</h1>
			<h2>(Celerity Server Implementation)</h2>
			<p>Test implementation of a WebSocket server</p>
			<h3>Notes:</h3>
			<ul>
				<li>Browsers confirmed as working: IE6 (see notes below),IE9,FireFox 6/7,Chrome 14,Opera 1.51,Safari 5.1</li>
				<li>Internet Explorer 6 will not accept the Flash security policy provided by the websocket server.  A Flash Socket Policy server MUST be running on port 843 for IE6 to work.</li>
				<li>Browsers using the Flash wrapper will intermittently fail the opening handshake.  Loading the page a second time will usually fix the issue.</li>
				<li>Server still does not properly handle connection close; may stop responding on explicit disconnect</li>
				<li>Not yet tested with IE 7-8</li>
			</ul>
		</div>
		<h2>Chat Test</h2>
		<div>
			<button onclick="ChatController.connect();">Connect</button>
			<button onclick="ChatController.disconnect();">Disconnect</button>
			<button onclick="getStatus()">Status</button>
		</div>
		<div>User Name: <input type="text" id="userName"/><button type="button" onclick="register()">Register</button></div>
		<div id="chatContainer">
			<h2>My Chat Session</h2>
			<div class="inner">
				<div id="userList">
					<h3>Users</h3>
					<select size="20" multiple="multiple" id="chatUserListUsers">
					</select>
				</div>
				<div id="chatOutput">
					
				</div>
				<div class="clear"></div>
				<div id="chatInputContainer">
					<form href="#" onsubmit="sendChatMessage(this);return false;" name="chatInput" id="chatInput">
						<input type="text" size="75" name="message"/>
						<button type="button" onclick="sendChatMessage(this.form)">Send</button>
					</form>
				</div>
			</div>
		</div>
	</body>
</html>
