package websocket.example;

import websocket.WebSocket;
import websocket.server.DefaultWebSocketServer;

public class EchoServer extends DefaultWebSocketServer {
	public EchoServer(int port) {
		super(port);
	}
	
	public void onMessage(WebSocket websocket, String message) {
		System.out.println("Message Received: "+message);
		this.sendToAll("New Message:  "+message);
	}

	public void onClientConnect(WebSocket websocket){
		this.sendToAll("A new client has connected.  Welcome, client!");
	}

}
