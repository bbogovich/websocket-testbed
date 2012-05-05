package websocket.example.chat.response;

public class InfoMessage extends DefaultChatResponseMessage {
	private static final long serialVersionUID = -5495818260488886237L;
	private String message;
	public InfoMessage(String message){
		this.message=message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
