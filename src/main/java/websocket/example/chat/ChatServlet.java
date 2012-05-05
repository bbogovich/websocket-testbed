package websocket.example.chat;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import websocket.example.chat.response.StatusResponse;
import org.codehaus.jackson.map.ObjectMapper;


public class ChatServlet extends HttpServlet {
	ChatServer chatServer;
	private Thread websocketThread;
	private ObjectMapper mapper = new ObjectMapper();
	/**
	 * 
	 */
	private static final long serialVersionUID = -2740602113376136616L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("ChatServlet.init");
		//super.init(config);
		chatServer = new ChatServer(Integer.parseInt(config.getInitParameter("port")));
		
		websocketThread = new Thread(chatServer);
		websocketThread.start();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("ChatServlet.doPost");
		String uri = req.getRequestURI().toString();
		String servletPath = req.getServletPath();
		System.out.println(servletPath);
		System.out.println(uri);
		System.out.println(req.getContextPath());
		if(("/chatsocket/initialize.do").equals(servletPath)){
			resp.setStatus(200);
			resp.getWriter().write(req.getSession().getId());
		}else if(("/chatsocket/status.do").equals(servletPath)){
			resp.setStatus(200);
			StatusResponse response = new StatusResponse();
			response.setSessionId(req.getSession().getId());
			response.setPort(chatServer.getPort());
			response.setUsersConnected(chatServer.getNumConnections());
			response.setRunning(this.websocketThread.isAlive());
			//resp.getWriter().write(req.getSession().getId());
			mapper.writeValue(resp.getWriter(), response);
		}else{
			resp.setStatus(403);
		}
	}
	
}
