package websocket.example.chat.response;

public class RegistrationSuccessResponse extends DefaultChatResponseMessage{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7226510297560635280L;

	private int userId=0;
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
