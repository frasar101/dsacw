import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/GetSession")
public class GetSession extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public GetSession() {
        super();
    }

protected void doGet(HttpServletRequest request, 
                     HttpServletResponse response)
               throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        try {
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.println("<html><body>");
            if (session == null) {
                writer.println("<p>Sorry, you are not logged in.</p>");
                writer.println("<p><a href=\"Login.html\">Return" +
                               "</a> to login page");
            }
            
            else {
                writer.println("<p>Thank you, you are already logged in.");
                writer.println("<p>Here is the data in your session:</p>");
                Enumeration names = session.getAttributeNames();
                while (names.hasMoreElements()) {
                    String name = (String) names.nextElement();
                    Object value = session.getAttribute(name);
                    writer.println("<p>name=" + name + " value=" + value);
                }
                String newURL = response.encodeURL("LogoutServlet");
                writer.println("Click <a href=\"" + newURL + 
                               "\">here</a> to log out");
            }
            writer.println("</body></html>");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

