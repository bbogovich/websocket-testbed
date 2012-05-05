package websocket.example.chat;

import websocket.WebSocket;

public class ChatUser {
	private WebSocket websocket;
	private String userName;
	private String sessionId;
	
	public WebSocket getWebsocket() {
		return websocket;
	}
	public void setWebsocket(WebSocket websocket) {
		this.websocket = websocket;
	}
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
}
