package websocket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import websocket.WebSocket;



public abstract class DefaultWebSocketServer implements WebSocketServer {
	private int port;
	protected Set<WebSocket> connections;
	
	public int getPort(){
		return this.port;
	}
	
	public DefaultWebSocketServer(){
		this.connections = new HashSet<WebSocket>();
	}
	
	public DefaultWebSocketServer(int port){
		this();
		this.port = port;
	}

	public void run() {
		try{
			System.out.println("Starting server on port "+port);
			ServerSocket listener = new ServerSocket(port);
			while(true){
				System.out.println("Waiting for new connection");
				Socket server = listener.accept();
				WebSocket connection = new WebSocket(this,server);
				connections.add(connection);
				Thread t = new Thread(connection);
				t.start();
			}
		} catch (IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}

	public void sendToAll(String message) {
		for (WebSocket ws:connections){
			try {
				ws.send(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onClientDisconnect(WebSocket websocket) throws IOException{
		if(this.connections.contains(websocket)){
			this.connections.remove(websocket);
		}
	}
	
	public int getNumConnections(){
		return connections.size();
	}
}
