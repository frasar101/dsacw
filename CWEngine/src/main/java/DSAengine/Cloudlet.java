package DSAengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sss.crypto.data.Share;
import sss.crypto.data.SerializableShare;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

public class Cloudlet {

	// Define the connection-string with your values
	public static final String storageConnectionString = ("DefaultEndpointsProtocol=http;"
			+ "AccountName=frasersstore;"
			+ "AccountKey=WYCwuyF59BvroUUx2WEUMuq5yrIcz7tPPtie02X6rd0b6DQxWboZNPQ8PQjnos1QND5x/04MgTO304zxHNllaw==");

	public static void upload(HttpServletRequest request,
			HttpServletResponse response, Share[] shares)
			throws ServletException, IOException {

		System.out.println("hello upload cloud");
		HttpSession loginsession = request.getSession(false);
		String username = (String) loginsession.getAttribute("Loginname");

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet query = null;
		ResultSet store_accounts = null;
		String file = null;
		int shareid = 0;
		int x=0;

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "users";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "password";
		
		try{
			Class.forName(driver).newInstance();
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			
			pst = conn
					.prepareStatement("select Connection_String, Container_Ref from storage_accounts where Username=?");
			pst.setString(1, username);
			store_accounts = pst.executeQuery();
			
			
			do {
			
			for (; x <= (shares.length - 1); x++){
				System.out.println(x);
				store_accounts.next();
				String connection_string = store_accounts.getString("Connection_String");
				String containerref = store_accounts.getString("Container_Ref");
				if (store_accounts.isLast()){
					store_accounts.beforeFirst();
					//store_accounts.next();
				}
				
				CloudStorageAccount storageAccount = CloudStorageAccount
						.parse(connection_string);

				// Create the blob client.
				CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

				// Retrieve reference to a previously created container.
				CloudBlobContainer container = blobClient
						.getContainerReference(containerref);
				
				
				pst = conn.prepareStatement("select Filename, Shareid from file_with_shares where Username=? and Share=?");
				pst.setString(1, username);
				pst.setString(2, shares[x].toString());
				query = pst.executeQuery();

				while (query.next()) {
					file = query.getString("Filename");
					shareid = query.getInt("Shareid");
				}
				
				
				CloudBlockBlob blob = container.getBlockBlobReference(file
						+ shareid);
				// blob.uploadText(S.toString());
				blob.uploadFromByteArray(shares[x].serialize(), 0, shares[x].serialize().length);
				System.out.println(shares[x]);
				
				
				pst = conn.prepareStatement("update file_with_shares set Conn_String=?, Container_Ref=? where Shareid=?");
				pst.setString(1, connection_string);
				pst.setString(2, containerref);
				pst.setInt(3, shareid);
				
				pst.execute();
					
			}
			
			} while (x != shares.length);
			
			/*do {
				store_accounts.beforeFirst();
			while (store_accounts.next()){
				String connection_string = store_accounts.getString("Connection_String");
				String containerref = store_accounts.getString("Container_Ref");
				
				for (;x <= shares.length;){
					CloudStorageAccount storageAccount = CloudStorageAccount
							.parse(connection_string);

					// Create the blob client.
					CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

					// Retrieve reference to a previously created container.
					CloudBlobContainer container = blobClient
							.getContainerReference(containerref);
					
					
					pst = conn.prepareStatement("select Filename, Shareid from file_with_shares where Username=? and Share=?");
					pst.setString(1, username);
					pst.setString(2, shares[x].toString());
					query = pst.executeQuery();

					while (query.next()) {
						file = query.getString("Filename");
						shareid = query.getInt("Shareid");
					}
					
					
					CloudBlockBlob blob = container.getBlockBlobReference(file
							+ shareid);
					// blob.uploadText(S.toString());
					blob.uploadFromByteArray(shares[x].serialize(), 0, shares[x].serialize().length);
					System.out.println(shares[x]);
					
					
					pst = conn.prepareStatement("update file_with_shares set Conn_String=?, Container_Ref=? where Shareid=?");
					pst.setString(1, connection_string);
					pst.setString(2, containerref);
					pst.setInt(3, shareid);
					
					pst.execute();
					
					
					
				} x++;
			} x++;
				} while (x != shares.length);*/
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		

		/*try {
			Class.forName(driver).newInstance();
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			System.out.println("hello from cloudlet");
			// Retrieve storage account from connection-string.
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(storageConnectionString);

			// Create the blob client.
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			// Retrieve reference to a previously created container.
			CloudBlobContainer container = blobClient
					.getContainerReference("mycontainer");

			// Create the container if it does not exist.
			container.createIfNotExists();

			for (Share S : shares) {
				// System.out.println(S);

				pst = conn
						.prepareStatement("select Filename, Shareid from file_with_shares where Username=? and Share=?");
				pst.setString(1, username);
				pst.setString(2, S.toString());
				query = pst.executeQuery();

				while (query.next()) {
					file = query.getString("Filename");
					shareid = query.getInt("Shareid");
				}

				CloudBlockBlob blob = container.getBlockBlobReference(file
						+ shareid);
				// blob.uploadText(S.toString());
				blob.uploadFromByteArray(S.serialize(), 0, S.serialize().length);
			}

		} catch (Exception f) {
			// Output the stack trace.
			f.printStackTrace();
		}*/

	}

	public static void download(String downfile, String Usersname)
			throws ServletException, IOException {

		System.out.println("hello from the cloudlet download...the file: "
				+ downfile + " shall be retrieved");
		
		//System.out.println("downfile: " + downfile);

		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet dbquery = null;
		ResultSet storage = null;
		String sharename = null;
		int Shareid = 0;
		byte[] bytearr = new byte[1000000];
		ArrayList<Share> myshares = new ArrayList<Share>();
		ArrayList<String> sharenamestodownload = new ArrayList<String>();
		ArrayList<String> connstrings = new ArrayList<String>();
		ArrayList<String> connrefs = new ArrayList<String>();

		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "users";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "password";

		try {
			Class.forName(driver).newInstance();
			conn = DriverManager
					.getConnection(url + dbName, userName, password);
			
			pst = conn.prepareStatement(" select Shareid, Conn_String, Container_Ref from file_with_shares where Username=? and Filename=?");

			//pst = conn.prepareStatement(" select Shareid from file_with_shares where Username=? and Filename=?");
			pst.setString(1, Usersname);
			pst.setString(2, downfile);
			
			storage = pst.executeQuery();
			
			while (storage.next()){
				
				Shareid = storage.getInt("Shareid");
			    sharenamestodownload.add((String) downfile + Shareid);
			    
			    if (!connstrings.contains(storage.getString("Conn_String"))){
			    connstrings.add(storage.getString("Conn_String"));}
			    
			    if (!connrefs.contains(storage.getString("Container_Ref"))){
			    connrefs.add(storage.getString("Container_Ref"));}
			    
			}
			
			for (int x=0; x <= (connrefs.size() - 1); x++){
			
			 // Retrieve storage account from connection-string.
				System.out.println("the error string: " + (connstrings.get(x)));
				CloudStorageAccount storageAccount = CloudStorageAccount.parse(connstrings.get(x));

				// Create the blob client.
				CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

				// Retrieve reference to a previously created container.
				CloudBlobContainer container = blobClient
						.getContainerReference(connrefs.get(x));

				// Loop through each blob item in the container.
				System.out.println("before for");
				for (ListBlobItem blobItem : container.listBlobs()) {
					// If the item is a blob, not a virtual directory.
					System.out.println("in for before if");
					if ((blobItem instanceof CloudBlob)) {

						System.out.println("inside if");

						// Download the item and save it to a file with the same
						// name.
						CloudBlob blob = (CloudBlob) blobItem;
						String blobname = blob.getName().toString();

						if (sharenamestodownload.contains(blobname)){
							blob.downloadToByteArray(bytearr, 0);
							Share downshare = sss.crypto.data.SerializableShare.deserialize(bytearr);
							myshares.add(downshare);

							System.out.println(blobname);
							System.out.println("downloaded 1");
						}

					}

				}
			    
			
			/*dbquery = pst.executeQuery();
			
			
			while (dbquery.next()) {
			    Shareid = dbquery.getInt("Shareid");
			    shareids.add((String) downfile + Shareid);
			    
			}*/ 

			}
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*try {
			// Retrieve storage account from connection-string.
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(storageConnectionString);

			// Create the blob client.
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

			// Retrieve reference to a previously created container.
			CloudBlobContainer container = blobClient
					.getContainerReference("mycontainer");

			// Loop through each blob item in the container.
			System.out.println("before for");
			for (ListBlobItem blobItem : container.listBlobs()) {
				// If the item is a blob, not a virtual directory.
				System.out.println("in for before if");
				if ((blobItem instanceof CloudBlob)) {

					System.out.println("inside if");

					// Download the item and save it to a file with the same
					// name.
					CloudBlob blob = (CloudBlob) blobItem;
					String blobname = blob.getName().toString();

					if (shareids.contains(blobname)){
						blob.downloadToByteArray(bytearr, 0);
						Share downshare = sss.crypto.data.SerializableShare.deserialize(bytearr);
						myshares.add(downshare);

						System.out.println(blobname);
						System.out.println("downloaded 1");
					}

				}

			}

		} catch (Exception e) {
			// Output the stack trace.
			e.printStackTrace();
		}*/

		Share[] downloadshares = new Share[myshares.size()];
		downloadshares = myshares.toArray(downloadshares);
		System.out.println("Cloudlet Shares!!!" + downloadshares);
		for (Share S : downloadshares){
			System.out.println("Cloudlets shares 2!!" + S);
		}
		DSAengine.Engine.download(downfile, downloadshares, Usersname);
	
}
	}
