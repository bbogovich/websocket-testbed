package websocket.example;

import websocket.WebSocket;
import websocket.server.DefaultWebSocketServer;
import org.apache.log4j.Logger;

public class EchoServer extends DefaultWebSocketServer {
	public EchoServer(int port) {
		super(port);
	}
	
	public void onMessage(WebSocket websocket, String message) {
		logger.debug("Message Received: "+message);
		this.sendToAll("New Message:  "+message);
	}

	public void onClientConnect(WebSocket websocket){
		this.sendToAll("A new client has connected.  Welcome, client!");
	}

}
