import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public LogoutServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		session.invalidate();
		
		try {
			/*response.setContentType("text/html");
			PrintWriter writer = response.getWriter();
			writer.println("<html><body>");
			writer.println("<p>You have logged out.</p>");
			writer.println("<p><a href=\"Login.html\">Return" + "</a> to login page");
			writer.println("</body></html>");
			writer.close();*/
			
			JFrame logout = new JFrame();

			logout.setVisible(true);
			logout.setLocation(100, 100);
			logout.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(logout,"Thank you, you have now been logged out!");
			logout.dispose();

			
			response.sendRedirect(response
					.encodeRedirectURL("/DSACW/Login.html"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
