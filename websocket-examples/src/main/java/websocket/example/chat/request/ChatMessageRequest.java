package websocket.example.chat.request;

import java.io.IOException;

import websocket.WebSocket;
import websocket.example.chat.ChatServer;
import websocket.example.chat.ChatUser;
import websocket.example.chat.response.ChatUserMessage;


public class ChatMessageRequest implements ChatRequest {
	private String chatMessage;
	private String sessionId;
	
	public long getTransactionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setTransactionId(long transactionId) {
		// TODO Auto-generated method stub

	}

	public void handleRequest(ChatServer chatServer, WebSocket websocket) throws IOException {
		// TODO Auto-generated method stub
		ChatUser user = chatServer.getChatUsers().get(sessionId);
		if(user!=null){
			chatServer.sendToAll(new ChatUserMessage(user.getUserName(),chatMessage,sessionId));
		}
	}

	public String getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(String chatMessage) {
		this.chatMessage = chatMessage;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
