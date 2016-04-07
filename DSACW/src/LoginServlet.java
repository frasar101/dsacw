import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
//import java.util.Timer;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public LoginServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession usercheck = request.getSession(false);

		String username = request.getParameter("username");
		String userpassword = request.getParameter("password");

		int Failedlogincount = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet results = null;
		ResultSet failedattempts = null;
		Timestamp LastFail = null;
		Timestamp storedlockout = null;
		Timestamp Lockouttime = null;
		java.util.Date currentdate = new java.util.Date();
		currentdate = new java.sql.Timestamp(currentdate.getTime());

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "users";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "password";

		try {

			try {
				Class.forName(driver).newInstance();
				conn = DriverManager.getConnection(url + dbName, userName,
						password);

				try {
					pst = conn

							.prepareStatement("select FailedLogins, Lockout from users where Username=?");
					pst.setString(1, username);

					failedattempts = pst.executeQuery();

					while (failedattempts.next()) {
						Failedlogincount = failedattempts
								.getInt("FailedLogins");
						storedlockout = failedattempts.getTimestamp("Lockout");
					}
					
					if (currentdate.before(storedlockout)){
						JFrame stilllock = new JFrame();

						stilllock.setVisible(true);
						stilllock.setLocation(100, 100);
						stilllock.setAlwaysOnTop(true);
						JOptionPane.showMessageDialog(stilllock, username + " is still locked out. 5 Minutes has not passed!","Lockout Message",JOptionPane.OK_OPTION);
						stilllock.dispose();
						response.sendRedirect(response
								.encodeRedirectURL("/DSACW/Login.html"));
						return;
						

					}
					else {
			
					}

				} catch (Exception f) {
					System.out.println(f);

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
				}

				try {

					Class.forName(driver).newInstance();
					conn = DriverManager.getConnection(url + dbName, userName,
							password);

					pst = conn
							.prepareStatement("select Username from users where Username=? and FailedLogins>=3");
					pst.setString(1, username);

					results = pst.executeQuery();

					while (results.next()) {
						if (username.equals(results.getString("Username"))) {
							
							java.util.Date today = new java.util.Date();
							LastFail = new java.sql.Timestamp(today.getTime());
							
							Lockouttime = new java.sql.Timestamp(today.getTime()+5*60*1000);
							
							pst = conn.prepareStatement("update users set Lockout=? where Username=? and FailedLogins>=3");
							pst.setTimestamp(1, Lockouttime);
							pst.setString(2, username);
							pst.execute();
							
							JFrame lockoutmess = new JFrame();

							lockoutmess.setVisible(true);
							lockoutmess.setLocation(100, 100);
							lockoutmess.setAlwaysOnTop(true);
							JOptionPane.showMessageDialog(lockoutmess,"You must wait 5 minutes before you can try again!","Lockout Message",JOptionPane.OK_OPTION);
							lockoutmess.dispose();
							response.sendRedirect(response
									.encodeRedirectURL("/DSACW/Login.html"));
							return;
							
							
						}
					}

				} catch (Exception f) {
					System.out.println(f);

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
				}

			} catch (Exception e) {
				System.out.println(e);

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
				if (results != null) {
					try {
						results.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

			if (LoginDao.validate(username, userpassword)) {
				Failedlogincount = 0;
				HttpSession loginsession = request.getSession();
				loginsession.setAttribute("Loginname", username);
				
				JFrame loginsuccess = new JFrame();

				loginsuccess.setVisible(true);
				loginsuccess.setLocation(100, 100);
				loginsuccess.setAlwaysOnTop(true);
				JOptionPane.showMessageDialog(loginsuccess, "Thank you, " + username
						+ ". You are now logged into the system.");
				loginsuccess.dispose();

				response.sendRedirect(response
						.encodeRedirectURL("/DSACW/index.html"));
			} else if (!LoginDao.validate(username, userpassword)) {
				Failedlogincount = Failedlogincount + 1;
				int retries = 3 - Failedlogincount;
				java.util.Date today = new java.util.Date();
				LastFail = new java.sql.Timestamp(today.getTime());

				JFrame loginerror = new JFrame();

				loginerror.setVisible(true);
				loginerror.setLocation(100, 100);
				loginerror.setAlwaysOnTop(true);
				JOptionPane.showMessageDialog(loginerror,
						"Username or password is incorrect! You have "
								+ retries + " tries left.");
				loginerror.dispose();

				response.sendRedirect(response
						.encodeRedirectURL("/DSACW/Login.html"));

			}

			try {
				Class.forName(driver).newInstance();
				conn = DriverManager.getConnection(url + dbName, userName,
						password);

				pst = conn
						.prepareStatement("update users set FailedLogins =?, LastLoginFail=? where Username=? ");

				pst.setInt(1, Failedlogincount);
				pst.setTimestamp(2, LastFail);
				pst.setString(3, username);
				pst.executeUpdate();
			} catch (Exception f) {
				System.out.println(f);

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
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
