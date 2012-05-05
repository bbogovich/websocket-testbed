package websocket.server;

import java.io.IOException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import websocket.WebSocket;

public abstract class SSLWebSocketServer extends DefaultWebSocketServer {
	
	@Override
	public void run() {
		try{
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			logger.debug("Starting SSL server on port "+this.getPort());
			SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(this.getPort());
			while(true){
				logger.debug("Waiting for new connection");
				SSLSocket sslSocket = (SSLSocket) sslserversocket.accept();
				WebSocket connection = new WebSocket(this,sslSocket);
				this.connections.add(connection);
				Thread t = new Thread(connection);
				t.start();
			}
		} catch (IOException ioe) {
			logger.debug("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}
	}
}
