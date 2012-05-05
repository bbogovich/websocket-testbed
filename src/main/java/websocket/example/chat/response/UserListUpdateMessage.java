package websocket.example.chat.response;

import java.util.SortedSet;

import websocket.example.chat.ChatServer;


public class UserListUpdateMessage extends DefaultChatResponseMessage {
	private static final long serialVersionUID = 4611578121030245989L;
	private SortedSet<UserListItem> userlist;
	public UserListUpdateMessage(ChatServer server){
		userlist = server.getUserList();
	}
	public SortedSet<UserListItem> getUserlist() {
		return userlist;
	}
	public void setUserlist(SortedSet<UserListItem> userlist) {
		this.userlist = userlist;
	}
}
