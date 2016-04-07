import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.ResultSet;  
import java.sql.SQLException; 

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public RegisterServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String username = request.getParameter("username");
		String userpassword = request.getParameter("password");
		String N = request.getParameter("Nvalue");
		String T = request.getParameter("Tvalue");
		
        Connection conn = null;  
        PreparedStatement pst = null;  
        ResultSet rs = null;  
  
        String url = "jdbc:mysql://localhost:3306/";  
        String dbName = "users";  
        String driver = "com.mysql.jdbc.Driver";  
        String userName = "root";  
        String password = "password";
        
        ////////////
        

        String passwordToHash = userpassword;
        String securePassword = null;
        try {
            // Create MessageDigest instance for SHA512
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            //Add password bytes to digest
            md.update(passwordToHash.getBytes());
            //Get the hash's bytes 
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            securePassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        System.out.println(securePassword);
     
        ////////////
		
		try {
			Class.forName(driver).newInstance();  
            conn = DriverManager  
                    .getConnection(url + dbName, userName, password);  
  
            pst = conn.prepareStatement(" insert into users (Username, Password, Nvalue, Tvalue) values (?, ?, ?, ?)");  
            pst.setString(1, username);  
            pst.setString(2, securePassword);
            pst.setString(3, N);
            pst.setString(4, T);
  
            pst.executeUpdate();  
			
			
			response.setContentType("text/html");
			PrintWriter writer = response.getWriter();
			
			
			
			writer.println("<html><body>");
			writer.println("<p>Thank you, " + username + ". You are now Registered on the System.</p>");
			writer.println("<p><a href=\"Login.html\">Return</a> to login page");
			writer.println("</body></html>");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {  
            if (conn != null) {  
                try {  
                    conn.close();  
                } catch (SQLException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (pst != null) {  
                try {  
                    pst.close();  
                } catch (SQLException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (rs != null) {  
                try {  
                    rs.close();  
                } catch (SQLException e) {  
                    e.printStackTrace();  
                }  
            }  
        } 
	}
	
    
    
}


