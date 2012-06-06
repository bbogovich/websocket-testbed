package websocket.example.chat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import websocket.WebSocket;
import websocket.example.chat.request.ChatRequest;
import websocket.example.chat.response.ChatResponseMessage;
import websocket.example.chat.response.InfoMessage;
import websocket.example.chat.response.UserListItem;
import websocket.example.chat.response.UserListUpdateMessage;
import websocket.protocol.hybi.StatusCode;
import websocket.server.DefaultWebSocketServer;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class ChatServer extends DefaultWebSocketServer {
	
	private Map<String,ChatUser> chatUsers;
	private Map<WebSocket,String> sessionMap;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public ChatServer(int port) {
		super(port);
		chatUsers = new TreeMap<String,ChatUser>();
		sessionMap = new HashMap<WebSocket,String>();
	}

	public void sendToAll(ChatResponseMessage response) throws IOException{
		String message=null;
		try {
			message = mapper.writeValueAsString(response);
		} catch(Exception e){
			
		}
		for (WebSocket websocket:this.connections){
			try {
				websocket.send(message);
			} catch (IOException e) {
				websocket.disconnect();
			}
		}
	}
	
	public void send(ChatResponseMessage response,WebSocket websocket){
		try {
			websocket.send(mapper.writeValueAsString(response));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String prepareResponseMessage(ChatResponseMessage response){
		try {
			return mapper.writeValueAsString(response);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see websocket.server.WebSocketServer#onMessage(websocket.WebSocket, java.lang.String)
	 * Messages for the chat server are expected to be in the format
	 * 	<messageType>\|<JSONObject>
	 * Where messageType is the class name the object is mapped to and JSONObject is a JSON-encoded object 
	 */
	public void onMessage(WebSocket websocket, String message) throws IOException {
		logger.debug("Message Received: "+message);
		int nullSplit = message.indexOf("|");
		String messageClassName = message.substring(0,nullSplit);
		String messageBody = message.substring(nullSplit+1,message.length());
		try {
			ChatRequest request = (ChatRequest)mapper.readValue(messageBody,Class.forName(messageClassName));
			request.handleRequest(this, websocket);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			websocket.disconnect(StatusCode.UNSUPPORTED_DATA_RECEIVED,"An unknown request type was received.");
			e.printStackTrace();
		}
		//this.sendToAll("New Message:  "+message);
	}

	@Override
	public void onClientDisconnect(WebSocket websocket) throws IOException {
		// TODO Auto-generated method stub
		super.onClientDisconnect(websocket);
		/*
		 * try {
			websocket.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		if(sessionMap.containsKey(websocket)){
			String sessionId = sessionMap.get(websocket);
			chatUsers.remove(sessionId);
			sessionMap.remove(websocket);
			sendToAll(new UserListUpdateMessage(this));
		}
	}

	public void onClientConnect(WebSocket websocket) throws IOException{
		this.sendToAll(new InfoMessage("A new client has connected.  Welcome, client!"));
	}
	
	public void registerUser(String userName,String sessionId,WebSocket websocket) throws IOException{
		if(!chatUsers.containsKey(sessionId)){
			ChatUser user = new ChatUser();
			user.setSessionId(sessionId);
			user.setUserName(userName);
			user.setSessionId(sessionId);
			chatUsers.put(sessionId, user);
			sessionMap.put(websocket, sessionId);
			sendToAll(new UserListUpdateMessage(this));
		}
	}
	public Map<String, ChatUser> getChatUsers() {
		return chatUsers;
	}
	public SortedSet<UserListItem> getUserList(){
		SortedSet<UserListItem> userlist = new TreeSet<UserListItem>();
		Set<String> keys = chatUsers.keySet();
		for (String sessionId:keys){
			ChatUser user = chatUsers.get(sessionId);
			if(user!=null){
				userlist.add(new UserListItem(user.getUserName(),user.getSessionId()));
			}
		}
		return userlist;
	}
}
