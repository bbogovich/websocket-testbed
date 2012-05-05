package websocket.protocol;

import java.io.IOException;
import java.util.Properties;

import websocket.WebSocket;


public interface WebSocketProtocol {
	public void setWebSocket(WebSocket websocket);
	public void setHeaders(Properties headers);
	public Properties getHeaders();
	public byte[] processHandshake(byte[] handshake);
	public byte[] processFrame(byte[] frameData) throws IOException;
	public byte[] prepareSendMessage(String message);
	public void close() throws IOException;
	public Frame createFrame();
}
