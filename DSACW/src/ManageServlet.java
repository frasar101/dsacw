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

import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.ResultSet;  
import java.sql.SQLException; 
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@WebServlet("/ManageServlet")
public class ManageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ManageServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		String N = request.getParameter("Nvalue");
		String T = request.getParameter("Tvalue");
		String Password = request.getParameter("password");
        String passwordToHash = Password;
        String downloadlocation = request.getParameter("downloadloc");
        String securePassword = null;
        String Username = (String)session.getAttribute("Loginname");

		session.setAttribute("Nvalue", N);
		session.setAttribute("Tvalue", T);
		
        Connection conn = null;  
        PreparedStatement pst = null;  
        ResultSet rs = null;  
        ResultSet accounts = null;
        
        int accountnum = 0;
        int Nint = 0;
        int Tint = 0;
  
        String url = "jdbc:mysql://localhost:3306/";  
        String dbName = "users";  
        String driver = "com.mysql.jdbc.Driver";  
        String userName = "root";  
        String password = "password";
        
        try {
        Class.forName(driver).newInstance();  
        conn = DriverManager  
                .getConnection(url + dbName, userName, password);  
        } catch (Exception f){
        	f.printStackTrace();
        }
        
        //if (File) downloadlocation.
        
        
       if (request.getParameter("downlocbutt") != null){
    	   try {
    	   pst = conn.prepareStatement("update users set Download_Folder=? where Username=?");
			
			pst.setString(1, downloadlocation); 
			pst.setString(2, Username);
			pst.executeUpdate();
    	   } catch (Exception e){
    		   e.printStackTrace();
    	   }
    	   
    	   response.sendRedirect(response
					.encodeRedirectURL("/DSACW/index.html"));
    
        
       } else if (request.getParameter("addstoreaccount") != null){
    	   System.out.println("hi");
    	   String connectionstring = request.getParameter("connstring");
    	   String containerref = request.getParameter("contref");
    	   
    	   if (connectionstring != null && containerref != null){
    		   System.out.println("1");
    		   
    		   try {
               pst = conn.prepareStatement("insert into storage_accounts (Username, Connection_String, Container_Ref) values (?,?,?)");
				
				
            	pst.setString(1, Username);
            	pst.setString(2, connectionstring);  
				pst.setString(3, containerref);
				
				
				pst.execute();
    		   } catch (Exception f){
    			   f.printStackTrace();
    		   }
    		   
    		   //Success message and redirect
    		   JFrame addaccountsuccess = new JFrame();

				addaccountsuccess.setVisible(true);
				addaccountsuccess.setLocation(100, 100);
				addaccountsuccess.setAlwaysOnTop(true);
				JOptionPane.showMessageDialog(addaccountsuccess,
						"Your storage account has been added!");
				addaccountsuccess.dispose();

				response.sendRedirect(response
						.encodeRedirectURL("/DSACW/index.html"));
    		   
    		   
    		   
    	   } else {
    		   //error message
    		   JFrame addaccounterror = new JFrame();

				addaccounterror.setVisible(true);
				addaccounterror.setLocation(100, 100);
				addaccounterror.setAlwaysOnTop(true);
				JOptionPane.showMessageDialog(addaccounterror,
						"Sorry, you didn't seem to enter a connection string and a container name. Both are required. Try again");
				addaccounterror.dispose();

				response.sendRedirect(response
						.encodeRedirectURL("/DSACW/index.html"));
    	   }
    	   
    	   
    	   
    	   
       }
        
       else if (request.getParameter("chpass") != null){
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
            
            try {
            pst = conn.prepareStatement("update users set Password=? where Username=?");
			
			pst.setString(1, securePassword);  
			pst.setString(2, Username);
			
			pst.executeUpdate();
            } catch (Exception e){
				e.printStackTrace();
			}
            
            response.sendRedirect(response
					.encodeRedirectURL("/DSACW/index.html"));
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        
       }
       
       else if (request.getParameter("changevalues") != null) {
    	   if (N != "" && T != ""){
    		   
    		   try {
    		   pst = conn.prepareStatement("select count(*) AS total from storage_accounts where username=?");
    		   pst.setString(1, Username);
    		   
    		   accounts = pst.executeQuery();
    		   
    		   Nint = Integer.parseUnsignedInt(N);
    		   Tint = Integer.parseUnsignedInt(T);
    		   
    		  
    		   
    		   while (accounts.next()){
    			   accountnum = accounts.getInt("total");
    		   }
    		   
    		   } catch (Exception e){
    			   e.printStackTrace();
    		   }
    		   
    		   if (!((Nint/accountnum) >= Tint)){
    			   
    		   try {
    	   pst = conn.prepareStatement("update users set Nvalue=?, Tvalue=? where Username=?");
			
           pst.setString(1, N);
           pst.setString(2, T);
           pst.setString(3, Username);
           
           pst.executeUpdate();
    		   } catch (Exception e){
    			   e.printStackTrace();
    		   }
    		   
    		   response.sendRedirect(response
						.encodeRedirectURL("/DSACW/index.html"));
    		   
    		   } else {
    			   
    			   JFrame ntsecureerror = new JFrame();

    			   ntsecureerror.setVisible(true);
    			   ntsecureerror.setLocation(100, 100);
    			   ntsecureerror.setAlwaysOnTop(true);
   				JOptionPane.showMessageDialog(ntsecureerror,
   						"Some accounts will hold enough shares to combine them. This is unsafe. Please either: reduce the share number, add more storage accounts, or increase the threshold.");
   				ntsecureerror.dispose();
   				
   				response.sendRedirect(response
						.encodeRedirectURL("/DSACW/index.html"));
    			   
    		   }
    		   
    	   } else {
    		   JFrame ntvalueserror = new JFrame();

				ntvalueserror.setVisible(true);
				ntvalueserror.setLocation(100, 100);
				ntvalueserror.setAlwaysOnTop(true);
				JOptionPane.showMessageDialog(ntvalueserror,
						"Sorry, you didn't seem to enter any N and T values. Please try again!");
				ntvalueserror.dispose();
    	   }
    		   
    	   }
    	   
       }
		
            /*JFrame manage = new JFrame();

			manage.setVisible(true);
			manage.setLocation(100, 100);
			manage.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(manage,"Thank you, your values have been changed!");
			manage.dispose();

            
            
            response.sendRedirect(response
					.encodeRedirectURL("/DSACW/index.html"));*/
			
	}
	
   
