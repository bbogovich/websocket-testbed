package websocket.example.chat.request;

import java.io.IOException;

import websocket.WebSocket;
import websocket.example.chat.ChatServer;


public interface ChatRequest {
	public long getTransactionId();
	public void setTransactionId(long transactionId);
	public void handleRequest(ChatServer chatServer, WebSocket websocket) throws IOException;
}
