package websocket.example.chat.response;

public class ConnectionSuccessMessage extends DefaultChatResponseMessage {
	private static final long serialVersionUID = -1688425423934091377L;
	private String welcomeMessage;

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}
}
