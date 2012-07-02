<!doctype HTML>
<html>
	<head>
		<style>
#chatOutput{
	height: 250px;
	border: 1px black solid;
	overflow-y: scroll;
}
		</style>
		<script>

		</script>
		<script>
var SESSION_ID;
(function initializeHttpSession(){
	var request = new XMLHttpRequest();
	request.open("POST","echosocket/initialize.do",false);
	SESSION_ID=request.send(null);
})();
function loadScript(scriptURL /*,Function <callback>*/){
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
EchoController = new function(){
	var $this=this;
	var websocket=null;
	var port=8083;
	
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
			websocket.send(data);
		}
	}
	function onMessage(data){
		var msgDiv=document.createElement("div");
		msgDiv.appendChild(document.createTextNode(data));
		document.getElementById("chatOutput").appendChild(msgDiv);
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
				
			}
		}else{
			if(!websocket){
				console.log("Instantiate new WebSocket object");
				websocket = new WebSocket("ws://"+window.location.hostname+":"+port);
				$this.websocket=websocket;
				websocket.onopen = function(e){
					websocketOpen=true;
					console.log("websocket.onopen");
					console.log(e);
					//websocketLog("open: "+e.data);
				}
				websocket.onmessage = function(e){
					console.log("websocket.onmessage");
					console.log(e);
					onMessage(e.data);
					/*var data=null;
					try{
						data = JSON.parse(e.data);
					}catch(e){
						console.error(e);
					}
					if(data && data.messageType){
						switch(data.messageType){
							case "testoutput":
								onTestOutput(data.data);
								break;
						}
					}*/
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
}
function sendChatMessage(form){
	EchoController.sendMessage(form.message.value)
}
		</script>
	</head>
	<body>
		<h1>WebSocket test</h1>
		<p>Test implementation of a WebSocket server</p>
		<h3>Compatibility:</h3>
		<ul>
			<li>Firefox 6.0.2: Native using MozWebSocket (hybi-07)</li>
			<li>Firefox 7: Native using MozWebSocket (hybi-08)</li>
			<li>Safari-Desktop: Native WebSocket (DRAFT-76)</li>
			<li>Chrome 14: Native using WebSocket (hybi-08)</li>
			<li>Internet Explorer 9: Flash Bridge (DRAFT-76)</li>
			<li>Opera: Flash Bridge (DRAFT-76)</li>
		</ul>
		<div id="chatContainer">
			<h2>Echo Test</h2>
			<div><button onclick="EchoController.connect();">Connect</button><button onclick="EchoController.disconnect();">Disconnect</button></div>
			<form href="#" onsubmit="sendChatMessage(this);return false;" name="chatInput"><input type="text" size="100" name="message"/><button type="button" onclick="sendChatMessage(this.form)">Send</button></form>
			<div id="chatOutput">
				
			</div>
		</div>
	</body>
</html>
