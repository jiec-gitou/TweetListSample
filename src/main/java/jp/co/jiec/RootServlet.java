package jp.co.jiec;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.TwitterException;

/**
 * 「/」のサーブレット
 * @author K.Taira
 */
@WebServlet(name = "root", urlPatterns = "/")
public class RootServlet extends HttpServlet {
	private static final long serialVersionUID = -8640089794240692044L;
	
	@Inject
	private TweetList tl;
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	if(request.getPathInfo().equals("/favicon.ico")){
    		response.getWriter().close();
    		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    		return;
    	}
    	String hash = request.getParameter("hash");
    	try {
        	if(hash != null){
        		tl.setHash(new String(hash.getBytes("ISO-8859-1"), "UTF-8"));
        	}
			tl.action();
		} catch (TwitterException e) {
			throw new IOException(e);
		}
    	request.getRequestDispatcher("./index.xhtml").forward(request, response);
    }
}