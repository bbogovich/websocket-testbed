package websocket.example.chat.response;

import java.io.Serializable;

public interface ChatResponseMessage extends Serializable {
	public String getMessageType();
}
