package websocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import websocket.protocol.Frame;
import websocket.protocol.ProtocolVersion;
import websocket.protocol.WebSocketProtocol;
import websocket.protocol.hixie.Hixie76WebSocket;
import websocket.protocol.hybi.Hybi07WebSocket;
import websocket.protocol.hybi.StatusCode;
import websocket.server.WebSocketServer;
import org.apache.log4j.Logger;


/**
 * @author bbogovich
 * Wrapper for a WebSocket implementation
 * On connect, determines the implementation type by the
 * handshake message.
 */
public class WebSocket implements Runnable {
	protected Socket socket;

	protected Logger logger = Logger.getLogger(WebSocket.class);
	private boolean handshakeComplete=false;
	private boolean connected=false;
	private WebSocketProtocol websocketImpl=null;
	private WebSocketServer websocketServer;
	byte[] byteBuffer=new byte[0];
	private String webSocketURL;
	DataOutputStream out;
	private ProtocolVersion protocolVersion;
	private Set<WebSocketMessageListener> messageListeners = new HashSet<WebSocketMessageListener>();
	
	public WebSocket(WebSocketServer websocketServer,Socket socket) throws IOException{
		this.websocketServer=websocketServer;
		this.socket = socket;
		out=new DataOutputStream(socket.getOutputStream());
	}
	
	public void run() {
		try {
			InputStream stream = socket.getInputStream();
			connected=true;
			while(connected){
				int byteCount = stream.available();
				if(byteCount>0){
					logger.debug(byteCount+" bytes are available for reading");
					byte[] oldByteBuffer = byteBuffer;
					//int oldByteBufferLength = oldByteBuffer==null?0:oldByteBuffer.length;
					int currentBufferLength = oldByteBuffer==null?0:oldByteBuffer.length;
					byteBuffer = new byte[currentBufferLength+byteCount];
					byte[] newBytes = new byte[byteCount];
					stream.read(newBytes, 0, byteCount);
					logger.debug(new String(newBytes, Charset.forName("UTF-8")));
					logger.debug("old byte buffer length: "+currentBufferLength);
					for (int i=0;i<currentBufferLength;i++){
						byteBuffer[i]=oldByteBuffer[i];
					}
					int newBufferLength=newBytes.length;
					logger.debug(new String(byteBuffer, Charset.forName("UTF-8")));
					for (int i=0;i<newBufferLength;i++){
						byteBuffer[currentBufferLength+i]=newBytes[i];
					}
					logger.debug(new String(byteBuffer, Charset.forName("UTF-8")));
					if(websocketImpl==null){
						handshake();
					}else{
						byte[] unusedBytes = websocketImpl.processFrame(byteBuffer);
						byteBuffer = unusedBytes;
					}
					//logger.debug(new String(byteBuffer));
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException ioe) {
			logger.debug("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}
	
	public void handshake() throws IOException{
		logger.debug("handshake");
		String data = new String(byteBuffer, Charset.forName("UTF-8"));
		if(data.startsWith("<policy-file-request/>")){
			logger.debug("Request is a flash security policy");
			handleFlashPolicyFileRequest();
		}else if(data.startsWith("GET")){
			
			int firstLineBreak = data.indexOf("\r\n");
			if(firstLineBreak>-1){
				String firstLine = data.substring(0,firstLineBreak);
				logger.debug(firstLine);
				if(firstLine.endsWith("HTTP/1.1")){
					logger.debug("This is probably a websocket request.");
					String[] firstlineSplit=firstLine.split(" ");
					if(firstlineSplit.length>1){
						webSocketURL=firstlineSplit[1];
					}
					logger.debug("The websocket URL is \""+webSocketURL+"\"");
					Properties headers = parseHandshakeHeaders(data);
					logger.debug("\nDetected Headers:");
					for (Object propertyName:headers.keySet()){
						logger.debug((String)propertyName+" = "+headers.getProperty((String)propertyName));
					}
					if(headers.containsKey("Upgrade")&&((String)(headers.get("Upgrade"))).toLowerCase().equals("websocket")&&
							headers.containsKey("Connection")&&((String)headers.get("Connection")).contains("Upgrade")){
						if(data.endsWith("\r\n\r\n")&&headers.containsKey("Sec-WebSocket-Version")&&headers.containsKey("Sec-WebSocket-Key")){
							logger.debug("headers are consistent with a hybi 7/8/13 client");
							int requestProtocolVersion=-1;
							try{
								requestProtocolVersion = Integer.parseInt((String)headers.get("Sec-WebSocket-Version"));
							}catch(NumberFormatException e){
								logger.debug("Invalid protocol version!");
								socket.close();
								connected=false;
							}
							if(requestProtocolVersion==7||requestProtocolVersion==8||requestProtocolVersion==13){
								this.protocolVersion = ProtocolVersion.HYBI_07;
								websocketImpl = new Hybi07WebSocket(this,headers);
								websocketImpl.setHeaders(headers);
							}else if(requestProtocolVersion==8){
								this.protocolVersion = ProtocolVersion.HYBI_08;
								websocketImpl = new Hybi07WebSocket(this,headers);
								websocketImpl.setHeaders(headers);
							}else if(requestProtocolVersion==13){
								this.protocolVersion = ProtocolVersion.HYBI_13;
								websocketImpl = new Hybi07WebSocket(this,headers);
								websocketImpl.setHeaders(headers);
							}

						}else if(headers.containsKey("Sec-WebSocket-Key1")&&(headers.containsKey("Sec-WebSocket-Key2"))){
							logger.debug("This appears to be an older Draft75/Draft76 client");
							int len=byteBuffer.length;
							for (int i=0;i<len;i++){
								System.out.print("0x"+Integer.toString(byteBuffer[i],16)+" "+((i%16==0)?"\n":""));
							}
							final byte CR = (byte) 0x0D;
							final byte LF = (byte) 0x0A;
							//\r\n\r\n followed by eight random bytes ends a draft 76 client handshake
							if(byteBuffer[len-12]==CR&&byteBuffer[len-11]==LF&&byteBuffer[len-10]==CR&&byteBuffer[len-9]==LF){
								websocketImpl = new Hixie76WebSocket(this,headers,webSocketURL);
							}else{
								logger.debug("Invalid, incomplete or not a Draft76 client.  Hold out for more data.");
							}
						}
						if(websocketImpl!=null){
							byte[] response = websocketImpl.processHandshake(byteBuffer);
							if(response!=null){
								this.clearByteBuffer();
								logger.debug("Sending Response:\n"+new String(response,Charset.forName("UTF-8")));
								out.write(response/*response.getBytes(Charset.forName("UTF-8"))*/);
								out.flush();
								this.websocketServer.onClientConnect(this);
							}else{
								websocketImpl=null;
							}
						}
					}else{
						logger.debug("Headers are incomplete; wait for more data.");
					}
				}
			}
		}else{
			logger.debug("Incomplete request or not a websocket; wait for more data.");
		}
	}
	
	public Properties parseHandshakeHeaders(String handshake){
		Properties props = new Properties();
		String[] lines = handshake.split("\r\n");
		Pattern linePattern = Pattern.compile("^([^:]+): (.+)$");
		for (String line:lines){
			Matcher lineMatch = linePattern.matcher(line.trim());
			if(lineMatch.matches()){
				if(lineMatch.group(1)!=null){
					props.put(lineMatch.group(1),lineMatch.group(2));
				}
			}
		}
		return props;
	}
	
	public void handleFlashPolicyFileRequest(){
		String flashSocketPolicy = "<cross-domain-policy><site-control permitted-cross-domain-policies=\"master-only\"/><allow-access-from domain=\"*\" to-ports=\""+websocketServer.getPort()+"\" /></cross-domain-policy>\0";
		try {
			logger.debug("handleFlashPolicyFileRequest");
			logger.debug("sending:\n"+flashSocketPolicy);
			clearByteBuffer();
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.write(flashSocketPolicy.getBytes(Charset.forName("UTF-8")));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Clears the current byte buffer.
	 * @return The old byte buffer
	 */
	public byte[] clearByteBuffer(){
		byte[] oldByteBuffer = byteBuffer;
		byteBuffer=new byte[0];
		return oldByteBuffer;
	}
	
	public void send(Frame frame) throws IOException{
		try{
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.write(frame.getFrameBytes());
			out.flush();
		}catch(IOException e){
			socket.close();
		}
	}
	
	public void send(String message) throws IOException{
		if(websocketImpl!=null){
			try{
				byte[] output = websocketImpl.prepareSendMessage(message);
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				out.write(output);
				out.flush();
			}catch(IOException e){
				socket.close();
			}
		}
	}
	
	public void onClose() throws IOException{
		this.websocketServer.onClientDisconnect(this);
		this.connected=false;
	}
	
	public void disconnect() throws IOException{
		if(websocketImpl!=null){
			websocketImpl.close();
		}
	}
	
	public void disconnect(StatusCode code,String message) throws IOException{
		if(websocketImpl!=null&&this.protocolVersion!=ProtocolVersion.HIXIE_76){
			websocketImpl.close();
		}else{
			disconnect();
		}
	}
	
	public boolean isHandshakeComplete() {
		return handshakeComplete;
	}

	public void setHandshakeComplete(boolean handshakeComplete) {
		this.handshakeComplete = handshakeComplete;
	}
	
	public void onMessageReceived(String message) throws IOException{
		websocketServer.onMessage(this, message);
		for (WebSocketMessageListener listener:this.messageListeners){
		    listener.onWebSocketMessage( message );
		}
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void addMessageListener(WebSocketMessageListener listener){
	    this.messageListeners.add( listener );
	}
	
	public void removeMessageListener(WebSocketMessageListener listener){
        this.messageListeners.remove( listener );
    }
}
