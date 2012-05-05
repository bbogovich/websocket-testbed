package websocket.example.chat.request;

import java.io.IOException;

import websocket.WebSocket;
import websocket.example.chat.ChatServer;
import websocket.example.chat.response.RegistrationSuccessResponse;


public class RegisterRequest implements ChatRequest {
	private long transactionId;
	private String userName;
	private String sessionId;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void handleRequest(ChatServer chatServer, WebSocket websocket) throws IOException {
		System.out.println("Registration request received for user "+userName+" with session id "+sessionId);
		chatServer.registerUser(userName, sessionId, websocket);
		chatServer.send(new RegistrationSuccessResponse(), websocket);
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId=transactionId;
	}


}
