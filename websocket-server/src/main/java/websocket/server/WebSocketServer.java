package websocket.server;

import java.io.IOException;

import websocket.WebSocket;


public interface WebSocketServer extends Runnable {
	public int getPort();
	public void sendToAll(String message);
	public void onMessage(WebSocket websocket,String message) throws IOException;
	public void onClientConnect(WebSocket websocket) throws IOException;
	public void onClientDisconnect(WebSocket websocket) throws IOException;
}
