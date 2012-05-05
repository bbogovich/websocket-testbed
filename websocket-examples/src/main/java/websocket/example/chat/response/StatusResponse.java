package websocket.example.chat.response;

public class StatusResponse extends DefaultChatResponseMessage{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7719362630577190147L;
	private String sessionId;
	private int port;
	private boolean running;
	private int usersConnected;
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public int getUsersConnected() {
		return usersConnected;
	}
	public void setUsersConnected(int usersConnected) {
		this.usersConnected = usersConnected;
	}
	
}
