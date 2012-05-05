package websocket.example.chat.response;

public class ChatUserMessage extends DefaultChatResponseMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 598843383333044690L;
	private String message;
	private String userName;
	private String userSessionId;
	
	public ChatUserMessage(String userName,String message,String sessionId){
		this.userName= userName;
		this.message = message;
		this.userSessionId = sessionId;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserSessionId() {
		return userSessionId;
	}
	public void setUserSessionId(String userSessionId) {
		this.userSessionId = userSessionId;
	}
	
}
