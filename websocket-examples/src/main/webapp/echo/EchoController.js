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