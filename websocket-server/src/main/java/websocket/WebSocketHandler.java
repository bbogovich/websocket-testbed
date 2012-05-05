package websocket;

public interface WebSocketHandler {
	public void setUrl();
	public String getUrl();
	public void registerWebSocket(WebSocket websocket);
	public void onMessage(WebSocket websocket,String message);
	public void onConnect(WebSocket websocket);
	public void onDisconnect(WebSocket websocket);
}
