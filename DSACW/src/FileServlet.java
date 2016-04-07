import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.ws.rs.*;

import DSAengine.Engine;

import java.net.HttpURLConnection;
import java.net.Proxy.Type;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;

import org.glassfish.jersey.client.ClientResponse;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

@WebServlet("/FileServlet")
@MultipartConfig
public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String downfile = null;
	private File uploadfile = null;

	// private Response eresponse;

	public FileServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		final String username = (String) session.getAttribute("Loginname");
		System.out.println(username);

		final JFrame downloadchoice = new JFrame("Download a file");

		if (request.getParameter("downloadsubmit") != null) {

			class RadioListener implements ActionListener { // for curiosity
															// only
				public void actionPerformed(ActionEvent e) {

					System.out.print("ActionEvent received: ");

					downfile = e.getActionCommand();

					String message = "Your file: " + downfile
							+ ", will be downloaded";

					JOptionPane.showMessageDialog(downloadchoice, message);
					try {
						DSAengine.Cloudlet.download(downfile, username);
					} catch (Exception f) {
						f.printStackTrace();
					}
					downloadchoice.dispose();
				}
			}

			// display files for user to choose to download
			// search database and show user their specific files that have
			// been
			// uploaded
			// get user to select using popup message
			// call Cloudlet download to retrieve the shares
			// call the engine from the cloudlet to join each of the shares

			// add radio buttons to a ButtonGroup

			Connection conn = null;
			PreparedStatement pst = null;
			ResultSet userfiles = null;
			String Filename = null;

			String url = "jdbc:mysql://localhost:3306/";
			String dbName = "users";
			String driver = "com.mysql.jdbc.Driver";
			String userName = "root";
			String password = "password";

			final ButtonGroup group = new ButtonGroup();

			downloadchoice.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			downloadchoice.setSize(300, 200);
			Container cont = downloadchoice.getContentPane();

			cont.setLayout(new GridLayout(0, 1));
			cont.add(new JLabel(
					"Please select one of your files that you would like to download: "));

			RadioListener listener = new RadioListener();

			try {
				Class.forName(driver).newInstance();
				conn = DriverManager.getConnection(url + dbName, userName,
						password);

				pst = conn
						.prepareStatement("select Filename from users_files where Username=?");
				pst.setString(1, username);
				userfiles = pst.executeQuery();

				while (userfiles.next()) {
					Filename = userfiles.getString("Filename");
					// System.out.println(Filename);

					JRadioButton filebutton = new JRadioButton(Filename);
					filebutton.setMnemonic(KeyEvent.VK_F);
					filebutton.setActionCommand(Filename);
					System.out.println(Filename);

					group.add(filebutton);
					cont.add(filebutton);

					filebutton.addActionListener(listener);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			downloadchoice.setVisible(true);
		}

		if (request.getParameter("uploadsubmit") != null) {
			System.out.println("hello");

			String upfilename = null;

			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			
			JFrame frame = new JFrame("JComboBox Test");
			frame.setLayout(new FlowLayout());
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			JFileChooser fileChooser = new JFileChooser();
			
			int returnValue = fileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				uploadfile = fileChooser.getSelectedFile();
				System.out.println(uploadfile.getName());
			}

			frame.pack();
			frame.setVisible(true);

			upfilename = uploadfile.getName();

			System.out.println("file name: " + upfilename
					+ ":::::::::::::file : " + uploadfile);
			frame.dispose();

			// File uploadfile = (File) upfilecontent;
			// ... (do your job here)

			// String uploadfile = request.getParameter("uploadfile");
			// Part uploadfilePart = request.getPart("uploadfile");
			// File uploadfile = (File) uploadfilePart;
			// System.out.println(uploadfile);
			// File uploadfile = (File) request.getAttribute("uploadfile");

			// String downloadfile = request.getParameter("downloadfile");

			String Username = (String) session.getAttribute("Loginname");

			/*
			 * URL url = new URL
			 * ("http://192.168.1.183:9763/CWEngine_1.0.0/services/engine");
			 * HttpURLConnection conn = (HttpURLConnection)
			 * url.openConnection(); conn.setDoOutput(true);
			 * conn.setRequestMethod("PUT");
			 * conn.setRequestProperty("Content-Type", MediaType.TEXT_PLAIN);
			 * 
			 * Map<String, String> uname = new HashMap<String, String>();
			 * 
			 * Map<String, String> ufile = new HashMap<String, String>();
			 * 
			 * String output = uname.put("loginname", Username) +
			 * ufile.put("ufile", uploadfile);
			 * 
			 * OutputStreamWriter os = new
			 * OutputStreamWriter(conn.getOutputStream()); os.write(output);
			 * os.close();
			 */

			/*
			 * javax.ws.rs.client.Client client = ClientBuilder.newClient();
			 * WebTarget target = client.target(
			 * "http://localhost:9763/CWEngine_1.0.0/services/engine/myeng" );
			 * Builder builder = target.request(MediaType.TEXT_PLAIN);
			 * builder.post(Entity.entity(Username, MediaType.TEXT_PLAIN));
			 * builder.post(Entity.entity(Username, MediaType.TEXT_PLAIN));
			 * builder.put(Entity.entity(uploadfile, MediaType.TEXT_PLAIN));
			 * 
			 * Map<String, String> uname = new HashMap<String, String>();
			 * Map<String, String> ufile = new HashMap<String, String>(); String
			 * output = uname.put("loginname", Username) + ufile.put("ufile",
			 * uploadfile);
			 * 
			 * eresponse =
			 * target.request(MediaType.TEXT_PLAIN).post(Entity.entity (output,
			 * MediaType.TEXT_PLAIN)); System.out.println(eresponse);
			 */

			DSAengine.Engine.upload(request, response, upfilename, uploadfile);

			JFrame uploadmess = new JFrame();

			uploadmess.setVisible(true);
			uploadmess.setLocation(100, 100);
			uploadmess.setAlwaysOnTop(true);
			JOptionPane.showMessageDialog(uploadmess,
					"Thank you, your file has been uploaded!");
			uploadmess.dispose();

			response.sendRedirect(response
					.encodeRedirectURL("/DSACW/index.html"));

		}
	}
}