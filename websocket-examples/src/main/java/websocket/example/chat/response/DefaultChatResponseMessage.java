package websocket.example.chat.response;

public class DefaultChatResponseMessage implements ChatResponseMessage {

	private static final long serialVersionUID = -2795461953646712341L;

	public String getMessageType() {
		return this.getClass().getName();
	}

}
