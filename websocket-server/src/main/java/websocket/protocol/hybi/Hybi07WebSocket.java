package websocket.protocol.hybi;

import biz.source_code.base64Coder.Base64Coder;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import websocket.WebSocket;
import websocket.protocol.Frame;
import websocket.protocol.WebSocketProtocol;


/**
 * @author bbogovich
 * http://tools.ietf.org/html/draft-ietf-hybi-thewebsocketprotocol-07
 */
public class Hybi07WebSocket implements WebSocketProtocol {
	//private enum PayloadLengthType {SHORT_7BIT,EXTENDED_16BIT,EXTENDED_64BIT};
	enum FrameType {CONTINUATION,TEXT,BINARY,RESERVED,CONNECTION_CLOSE,PING,PONG};
	private Properties headers;
	private WebSocket websocket;
	
	public Hybi07WebSocket(WebSocket websocket,Properties headers){
		this.headers=headers;
		this.websocket = websocket;
	}
	
	public void setHeaders(Properties headers) {
		this.headers=headers;
	}

	public Properties getHeaders() {
		return this.headers;
	}

	/* (non-Javadoc)
	 * @see websocket.WebSocketProtocol#processHandshake(byte[])
	 */
	public byte[] processHandshake(byte[] handshake) {
		String acceptHeader=generateSecWebSocketAcceptHeader(headers.getProperty("Sec-WebSocket-Key"));
		System.out.println("Response Key:"+acceptHeader);
		String response = "HTTP/1.1 101 Web Socket Protocol Handshake\r\n" +
				"Upgrade: WebSocket\r\n" +
				"Connection: Upgrade\r\n" +
				"Sec-WebSocket-Accept: " + acceptHeader + "\r\n" +
				"\r\n";
		return response.getBytes(Charset.forName("UTF-8"));
	}

	private String generateSecWebSocketAcceptHeader(String secWebSocketKey){
		final String responseKeyBaseGUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"; //see draft-ietf-hybi-thewebsocketprotocol-07 section 1.3
		String result=null;
		if(secWebSocketKey!=null&&secWebSocketKey.length()>0){
			try {
				result = new String(Base64Coder.encode(MessageDigest.getInstance("SHA-1").digest(secWebSocketKey.concat(responseKeyBaseGUID).getBytes())));
			} catch (NoSuchAlgorithmException e) {
				//this will never happen, it's bundled with the JRE
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public byte[] processFrame(byte[] frameData) throws IOException {
		System.out.println("Hybi07-processFrame");
		Hybi07Frame newFrame = new Hybi07Frame();
		byte[] unusedData = newFrame.populate(frameData);
		if(!newFrame.isFinalFragment()){
			/*TODO: ADD SUPPORT FOR MESSAGE FRAGMENTATION*/
		}else{
			OpCode frameType = newFrame.getOpCode();
			if(frameType==OpCode.TEXT_FRAME){
				this.websocket.onMessageReceived(newFrame.getPayloadText());
			}else if (frameType==OpCode.CONNECTION_CLOSE){
				this.close();
			}else if(frameType==OpCode.PING){
				Hybi07Frame responseFrame = new Hybi07Frame();
				responseFrame.setOpCode(OpCode.PONG);
				responseFrame.setPayloadData(newFrame.payloadData);
				this.websocket.send(responseFrame);
			}else if(frameType==OpCode.PONG){
				//keep-alive response, ignore.
			}else if(frameType==OpCode.BINARY_FRAME){
			}else if(frameType==OpCode.CONTINUATION_FRAME){
				
			}
		}
		return unusedData;
	}

	public byte[] prepareSendMessage(String text) {
		System.out.println("prepareSendMessage("+text+")");
		Hybi07Frame frame = new Hybi07Frame();
		frame.setOpCode(OpCode.TEXT_FRAME);
		frame.setFinalFragment(true);
		frame.setPayloadData(text);
		return frame.getFrameBytes();
	}

	public void setWebSocket(WebSocket websocket) {
		// TODO Auto-generated method stub
		this.websocket = websocket;
	}

	public void close(StatusCode statusCode,String message) throws IOException {
		Hybi07Frame closeFrame = new Hybi07Frame();
		closeFrame.setOpCode(OpCode.CONNECTION_CLOSE);
		byte[] payload = new byte[message.length()+2];
		payload[0] = (byte)((statusCode.getCode() >> 8)&0x0FF);
		payload[1] = (byte)(statusCode.getCode()&0x0FF);
		int messageSize = message.length();
		byte[] messageBytes=message.getBytes(Charset.forName("UTF-8"));
		for (int i=0;i<messageSize;i++){
			payload[i+2]=messageBytes[i];
		}
		closeFrame.setPayloadData(payload);
		Socket socket = this.websocket.getSocket();
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		out.write(closeFrame.getFrameBytes());
		out.flush();
		socket.close();
		this.websocket.onClose();
	}
	
	public void close() throws IOException{
		close(null,null);
	}

	public Frame createFrame() {
		return new Hybi07Frame();
	};

	public Frame createFrame(byte[] frameData) {
		return new Hybi07Frame(frameData);
	};

}
