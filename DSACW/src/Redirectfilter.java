
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
				"/filemanip.html", 
				"/Manage.html"
		})
public class Redirectfilter implements Filter {

  
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
		System.out.println("filter has been invoked");
		
		HttpServletRequest request = (HttpServletRequest) req;
	    String requestURI = request.getRequestURI();
	    
	    HttpSession session = request.getSession(false);
	    
	    if (session == null){
	    	System.out.println("null");
	    }
	    
	    if (session == null && !requestURI.startsWith("/Login.html")) {
	    	
	        //String toReplace = requestURI.substring(requestURI.indexOf("/Login.html"), requestURI.lastIndexOf("/") + 1);
	        String newURI = ("/Login.html");
	        req.getRequestDispatcher(newURI).forward(req, res);
	        System.out.println("restricted access!");
	    } else if (session != null && !requestURI.startsWith("/Login.html")){
	    	chain.doFilter(req, res);
	    	System.out.println("valid session");
	    }
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
	}

}

/////////////////////////////////


