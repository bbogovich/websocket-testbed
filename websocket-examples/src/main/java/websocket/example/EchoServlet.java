package websocket.example;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EchoServlet extends HttpServlet {

	private static final long serialVersionUID = 6896868039692723650L;
	Thread websocketThread;
	private EchoServer websocketServer;
	
	public EchoServlet(){
		websocketServer = new EchoServer(8083);
		websocketThread = new Thread(websocketServer);
		websocketThread.start();
	}
	
	public void init(ServletConfig config){
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setStatus(200);
		resp.getWriter().write(req.getSession().getId());
	}
	
}
