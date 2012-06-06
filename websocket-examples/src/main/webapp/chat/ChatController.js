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
				window.WEB_SOCKET_SWF_LOCATION = "../scripts/WebSocketMain.swf";
				window.WEB_SOCKET_DEBUG = true;
				loadScript("../scripts/swfobject.js",function(){
					console.log("swfobject.js loaded")
					loadScript("../scripts/FABridge.js",function(){
						console.log("FABridge.js loaded")
						loadScript("../scripts/web_socket.js",function(){
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
			//remove any deleted users
			var sessionMap={};
			for (var i=0;i<userList.length;i++){
				var user = userList[i];
				sessionMap[user.sessionId]=1;
			}
			var optsToRemove=[];
			for (i=0;i<options.length;i++){
				var option = options[i];
				if(!sessionMap[option.value]){
					optsToRemove.push(option);
				}
			}
			while(optsToRemove.length){
				var option = optsToRemove.pop();
				option.parentNode.removeChild(option);
			}
			sessionMap=null;
			//add new users
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