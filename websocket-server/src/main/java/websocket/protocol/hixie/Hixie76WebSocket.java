package websocket.protocol.hixie;

import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import websocket.WebSocket;
import websocket.protocol.Frame;
import websocket.protocol.WebSocketProtocol;


public class Hixie76WebSocket implements WebSocketProtocol {
	private Properties headers;
	private String path;
	private WebSocket websocket;
	/**
	   * The byte representing the beginning of a WebSocket text frame.
	   */
	public static final byte START_OF_FRAME = (byte) 0x00;
	/**
	   * The byte representing the end of a WebSocket text frame.
	   */
	public static final byte END_OF_FRAME = (byte) 0xFF;

	public Hixie76WebSocket(WebSocket websocket,Properties headers,String path){
		this.headers=headers;
		this.path=path;
		this.websocket = websocket;
	}
	
	public void setHeaders(Properties headers) {
		this.headers=headers;
	}

	public Properties getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] processHandshake(byte[] handshake) {
		String response=null;
		String key1 = headers.getProperty("Sec-WebSocket-Key1");
		String key2 = headers.getProperty("Sec-WebSocket-Key2");
		int handshakeLength = handshake.length;
		if(key1!=null&&key2!=null&&handshakeLength>8){
			byte[] responseChallenge = null;
			byte[] key3 = {
					handshake[handshakeLength-8],
					handshake[handshakeLength-7],
					handshake[handshakeLength-6],
					handshake[handshakeLength-5],
					handshake[handshakeLength-4],
					handshake[handshakeLength-3],
					handshake[handshakeLength-2],
					handshake[handshakeLength-1]
			};
			byte[] part1 = this.getPart(key1);
			byte[] part2 = this.getPart(key2);
			byte[] challenge = new byte[16];
			challenge[0] = part1[0];
			challenge[1] = part1[1];
			challenge[2] = part1[2];
			challenge[3] = part1[3];
			challenge[4] = part2[0];
			challenge[5] = part2[1];
			challenge[6] = part2[2];
			challenge[7] = part2[3];
			challenge[8] = key3[0];
			challenge[9] = key3[1];
			challenge[10] = key3[2];
			challenge[11] = key3[3];
			challenge[12] = key3[4];
			challenge[13] = key3[5];
			challenge[14] = key3[6];
			challenge[15] = key3[7];
			MessageDigest md5 = null;
			try {
				md5 = MessageDigest.getInstance("MD5");
				responseChallenge = md5.digest(challenge);
				System.out.println("Response Challenge: "+responseChallenge);
			} catch (NoSuchAlgorithmException e) {
				//This will never happen, it's built into the JRE
				e.printStackTrace();
				return null;
			}
			StringBuffer responseStr = new StringBuffer("HTTP/1.1 101 Web Socket Protocol Handshake\r\n" +
					"Upgrade: WebSocket\r\n" +
					"Connection: Upgrade\r\n"+
					"Sec-WebSocket-Origin: " + headers.getProperty("Origin") + "\r\n"+
					"Sec-WebSocket-Location: ws://" + headers.getProperty("Host") + path + "\r\n");
			if (headers.containsKey("Sec-WebSocket-Protocol")) {
				responseStr.append("Sec-WebSocket-Protocol: " +headers.getProperty("WebSocket-Protocol") + "\r\n");
			}
			if (headers.containsKey("Cookie")){
				responseStr.append("Cookie: " + headers.getProperty("Cookie")+"\r\n");
			}
			responseStr.append("\r\n");
			responseStr.append(new String(responseChallenge));
			response = responseStr.toString();
		}
		return response.getBytes();
	}

	private byte[] getPart(String key) {
		long keyNumber = Long.parseLong(key.replaceAll("[^0-9]",""));
		long keySpace = key.split("\u0020").length - 1;
		long part = new Long(keyNumber / keySpace);
		return new byte[] {
			(byte)( part >> 24 ),
			(byte)( (part << 8) >> 24 ),
			(byte)( (part << 16) >> 24 ),
			(byte)( (part << 24) >> 24 )
		};
	}
	
	public byte[] processFrame(byte[] frameData) throws IOException {
		Hixie76Frame newFrame = new Hixie76Frame();
		byte[] unprocessedData = newFrame.populate(frameData);
		if(newFrame.isMessageFrame()){
			websocket.onMessageReceived(newFrame.getPayloadText());
		}
		return unprocessedData;
	}

	public byte[] prepareSendMessage(String text) {
		Hixie76Frame frame = new Hixie76Frame();
		frame.setPayloadData(text);
		return frame.getFrameBytes();/*
		byte[] textBytes = text.getBytes(Charset.forName("UTF-8"));
		ByteBuffer b = ByteBuffer.allocate(textBytes.length + 2);
		b.put(START_OF_FRAME);
		b.put(textBytes);
		b.put(END_OF_FRAME);
		b.rewind();
		return b.array();*/
	}
	public void setWebSocket(WebSocket websocket) {
		this.websocket = websocket;
	}

	public void close() throws IOException {
		Socket socket = this.websocket.getSocket();
		socket.close();
		this.websocket.onClose();
	}

	public Frame createFrame() {
		return new Hixie76Frame();
	}
}
