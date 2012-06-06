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

function sendChatMessage(form){
	EchoController.sendMessage(form.message.value)
}