<!doctype HTML>
<html>
	<head>
		<title>Chat</title>
		<meta http-equiv="X-UA-Compatible" content="IE=9" /><!-- stop intranet IE9 clients from defaulting to IE7 rendering mode -->
		<link rel="stylesheet" type="text/css" href="chat.css"/>
		<script src="ChatController.js"></script>
		<script src="chat.js"></script>
	</head>
	<body onload="init()">
		<div class="header">
			<h1>WebSocket Chat test</h1>
			<h3>Notes:</h3>
			<ul>
				<li>Internet Explorer 6 will not accept the Flash security policy provided by the websocket server.  A Flash Socket Policy server MUST be running on port 843 for IE6 to work.</li>
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
