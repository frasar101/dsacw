package DSAengine;

import java.awt.List;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import sss.Facade;
import sss.config.Algorithms;
import sss.config.Encryptors;
import sss.config.RandomSources;
import sss.crypto.data.Share;

//@Path("/myeng")
public class Engine {

	//@POST
	//@Consumes(MediaType.TEXT_PLAIN)
	
	public static void download(String Downloadfile, Share[] downshares, String username) throws ServletException, IOException {
		//System.out.println("Hello from engine ");

	    Connection conn = null;  
        PreparedStatement pst = null;  
        ResultSet rs = null;  
        ResultSet down = null;
        byte[] bytes = null;
        String downloadloc = null;
  
        String url = "jdbc:mysql://localhost:3306/";  
        String dbName = "users";  
        String driver = "com.mysql.jdbc.Driver";  
        String userName = "root";  
        String password = "password";
        
        int n = 0, t = 0;
        
        try {
        	
        	Class.forName(driver).newInstance();  
        	conn = DriverManager.getConnection(url + dbName, userName, password); 
        	
        	pst = conn.prepareStatement("select Download_Folder from users where Username=?");
        	pst.setString(1, username);
        	down = pst.executeQuery();
        	
        	while (down.next()){
        		downloadloc = down.getString("Download_Folder");
        	}
        	
        	
        } catch (Exception a){
        	a.printStackTrace();
        }
        
        try {
        	Class.forName(driver).newInstance();  
        	conn = DriverManager.getConnection(url + dbName, userName, password); 
        	
        	pst = conn.prepareStatement("select Nvalue, Tvalue from users_files where Username=? and Filename=?");
        	pst.setString(1, username);
        	pst.setString(2, Downloadfile);
        	rs = pst.executeQuery();
        	
        	while (rs.next()){
        		n = rs.getInt("Nvalue");
        		t = rs.getInt("Tvalue");
        	}
        	
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

		RandomSources r = RandomSources.SHA1;
		Encryptors e = Encryptors.ChaCha20;
		Algorithms a = Algorithms.CSS;
		
		//System.out.println("Engine!!!!!!!!!!!" + downshares);
		for (Share S : downshares){
			System.out.println("SHARE: " + S);
		}
		
		
		try {
			Facade fa = new Facade(n, t, r, e, a);
			/*for (Share S : downshares){
				System.out.println("share: " + S);
			}*/
			//Share[] shares = null;
			bytes = fa.join(downshares);
			//String output = new String(bytes);
			//System.out.println(output);
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		
		String fpath = downloadloc + "\\";
		
		File someFile = new File(fpath + Downloadfile);
        FileOutputStream fos = new FileOutputStream(someFile);
        fos.write(bytes);
        fos.flush();
        fos.close();
        
        JFrame downloadsuccess = new JFrame();

		downloadsuccess.setVisible(true);
		downloadsuccess.setLocation(100, 100);
		downloadsuccess.setAlwaysOnTop(true);
		JOptionPane.showMessageDialog(downloadsuccess,
				"Your file has been downloaded! To go back to the mian index page, just replace the URI with 'index.html'");
		downloadsuccess.dispose();
		
		
	}
	
	public static void upload(HttpServletRequest request,
			HttpServletResponse response, String upfilename, File uploadfile) throws ServletException, IOException {
		System.out.println("hello" + upfilename);
		
		HttpSession loginsession = request.getSession();
		String username = (String) loginsession.getAttribute("Loginname");
		Share[] shares = null;
		
		Connection conn = null;  
        PreparedStatement pst = null;  
        ResultSet rs = null;  
  
        String url = "jdbc:mysql://localhost:3306/";  
        String dbName = "users";  
        String driver = "com.mysql.jdbc.Driver";  
        String userName = "root";  
        String password = "password";
        
        int n = 0, t = 0;
		
		try {
        	Class.forName(driver).newInstance();  
        	conn = DriverManager.getConnection(url + dbName, userName, password); 
        	
        	pst = conn.prepareStatement("select Nvalue, Tvalue from users where Username=?");
        	pst.setString(1, username);
        	rs = pst.executeQuery();
        	
        	while (rs.next()){
        		n = rs.getInt("Nvalue");
        		t = rs.getInt("Tvalue");
        	}       	
        	
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
		
		try {
			
			Class.forName(driver).newInstance();  
        	conn = DriverManager.getConnection(url + dbName, userName, password); 
        	
        	System.out.println(upfilename);
			
			pst = conn.prepareStatement("insert into users_files (Username, Filename, NValue, TValue) values (?, ?, ?, ?)");
			pst.setString(1, username);
			pst.setString(2, upfilename);
			pst.setInt(3, n);
			pst.setInt(4, t);
			pst.execute();
		} catch (Exception g){
			g.printStackTrace();
		}

		RandomSources r = RandomSources.SHA1;
		Encryptors e = Encryptors.ChaCha20;
		Algorithms a = Algorithms.CSS;
		
		
		//FileInputStream fis = new FileInputStream(uploadfile);
        //System.out.println(file.exists() + "!!");
        //InputStream in = resource.openStream();
		FileInputStream fis = new FileInputStream(uploadfile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum); //no doubt here is 0
                //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                //System.out.println("read " + readNum + " bytes,");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
		try {
			Facade f = new Facade(n, t, r, e, a);
			//File secret = uploadfile;
			byte[] bytes = bos.toByteArray();
			//byte[] bytes =  secret.
			shares = f.split(bytes);
			
			for (Share S:shares){
				System.out.println(S);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();

		}
			
			try {
	        	Class.forName(driver).newInstance();  
	        	conn = DriverManager.getConnection(url + dbName, userName, password); 
	        	
	        	for (Share S:shares){
					System.out.println(S);
	        	
					pst = conn.prepareStatement(" insert into file_with_shares (Username, Filename, Share) values (?, ?, ?)"); 
					pst.setString(1, username);
					pst.setString(2, upfilename);
					pst.setString(3, S.toString());
					
					pst.execute();
	        	}
	        	
	        } catch (Exception f) {
				f.printStackTrace();
			} finally {  
	            if (conn != null) {  
	                try {  
	                    conn.close();  
	                } catch (SQLException f) {  
	                    f.printStackTrace();  
	                }  
	            }  
	            if (pst != null) {  
	                try {  
	                    pst.close();  
	                } catch (SQLException f) {  
	                    f.printStackTrace();  
	                }  
	            }   
	        }
		
		Cloudlet.upload(request, response, shares);
		
	}
}