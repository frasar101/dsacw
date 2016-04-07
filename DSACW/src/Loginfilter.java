

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class Redirectfilter
*/
@WebFilter(
		dispatcherTypes = {
				DispatcherType.REQUEST, 
				DispatcherType.FORWARD, 
				DispatcherType.INCLUDE
		}
					, 
		urlPatterns = {
				"/Login.html", 
		})
public class Loginfilter implements Filter {

  
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
		System.out.println("filter has been invoked");
		
		HttpServletRequest request = (HttpServletRequest) req;
	    
	    HttpSession session = request.getSession(false);
	    
	    if (session != null) {
	    	String currentuser = (String) session.getAttribute("Loginname");
	        String newURI = ("/index.html");
	        req.getRequestDispatcher(newURI).forward(req, res);
	        System.out.println(currentuser + " is already logged in");
	    } else if (session == null) {
	    	chain.doFilter(req, res);
	    	System.out.println("valid session");
	    }
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
	}

}

/////////////////////////////////
