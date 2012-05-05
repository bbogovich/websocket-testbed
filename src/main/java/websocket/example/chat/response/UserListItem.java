package websocket.example.chat.response;

public class UserListItem implements Comparable<UserListItem> {
	private String userName;
	private String sessionId;
	public UserListItem(){}
	public UserListItem(String userName,String sessionId){
		this.userName = userName;
		this.sessionId = sessionId;
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
	public int compareTo(UserListItem that) {
		return this.userName.compareTo(that.getUserName());
	}
}
