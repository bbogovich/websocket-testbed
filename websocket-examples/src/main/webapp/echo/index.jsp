<!doctype HTML>
<html>
	<head>
		<title>Echo</title>
		<style>
#chatOutput{
	height: 250px;
	border: 1px black solid;
	overflow-y: scroll;
}
		</style>
		<script src="EchoController.js"></script>
		<script src="echo.js"></script>
	</head>
	<body>
		<h1>Echo</h1>
		<p>
			Echo example.  Click "Connect" to establish the websocket connection.  
			Sent messages will be echoed verbatim to the client.
		</p>
		<div id="chatContainer">
			<h2>Echo Test</h2>
			<div><button onclick="EchoController.connect();">Connect</button><button onclick="EchoController.disconnect();">Disconnect</button></div>
			<form href="#" onsubmit="sendChatMessage(this);return false;" name="chatInput"><input type="text" size="100" name="message"/><button type="button" onclick="sendChatMessage(this.form)">Send</button></form>
			<div id="chatOutput">
				
			</div>
		</div>
	</body>
</html>
