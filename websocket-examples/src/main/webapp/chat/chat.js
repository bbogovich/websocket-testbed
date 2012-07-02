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
		loadScript("../scripts/json2.js");
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
				try{
					console.log(i+":  "+request[i]);
				}catch(e){
					
				}
			}
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