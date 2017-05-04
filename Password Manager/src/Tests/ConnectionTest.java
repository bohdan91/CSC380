//import Main.Connection;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

import org.junit.Test;
import org.junit.*;
import java.sql.*;

public class ConnectionTest 
{
	static Connection conn;
	static Main.Connection connection = Main.Connection.getInstance();
	static final String address = "127.0.0.1";
	static final int port = 9898;
	static Socket socket;
	static ObjectInputStream in;
	static ObjectOutputStream out;
	static boolean isConnected = false;
	static String userID = "ExampleUser123";
	static String encID = "EncryptedId123";
	static String decID = "DecryptedId123";
	static String account1 = "1_TestAccount";
	static String account2 = "2_TestAccount";
	static String account3 = "3_TestAccount";

	@BeforeClass
	public static void beforeClassCreateTables() throws SQLException
	{
		String url = "jdbc:sqlite:" + "/users/marcusgiarrusso/documents/workspace2/csc380/server" + "/test.db";
		conn = DriverManager.getConnection(url);
		String stmt_1 = "INSERT INTO users values(\"" + userID + "\", \"" + encID + "\", \"" + decID + "\")";
		String stmt_2 = "INSERT INTO accounts values(\"" + userID + "\", " + 1 + ", \"" + account1 + "\")";
        String stmt_3 = "INSERT INTO accounts values(\"" + userID + "\", " + 2 + ", \"" + account2 + "\")";
        String stmt_4 = "INSERT INTO accounts values(\"" + userID + "\", " + 3 + ", \"" + account3 + "\")";
        Statement stmt = conn.createStatement();
        stmt.executeQuery(stmt_1);
        stmt.execute(stmt_2);
        stmt.execute(stmt_3);
        stmt.execute(stmt_4);
        stmt.close();
	}
	
	@AfterClass
	public static void afterClassClearTables() throws SQLException, IOException
	{
		String users_sql = "DELETE FROM users";
		String accounts_sql = "DELECT FROM accounts";
		Statement stmt = conn.createStatement();
		stmt.execute(users_sql);
		stmt.execute(accounts_sql);
		conn.close();
	}
	
	@Before
	public void beforeOpenConnection() throws UnknownHostException, IOException
	{
		socket = new Socket(address, port);
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		isConnected = true;
	}
	
	@After
	public void AfterCloseConnection() throws IOException
	{
		isConnected = false;
		out.close();
		in.close();
		socket.close();
	}
	
	@Test
	public void testGetEncryptedID()
	{
		String actual = connection.getEncryptedId(userID);
		Assert.assertEquals(encID, actual);
		
	}
	
	@Test
	public void testCheckDecryptedId()
	{
		boolean response = connection.checkDecryptedId(userID, decID);
		Assert.assertEquals(true, response);
	}
	
	@Test
	public void testGetAccounts()
	{
		String[] response = connection.getAccounts(decID);
		String[] accounts = {account1, account2, account3};
		Assert.assertEquals(accounts, response);
	}
	
	@Test
	public void testInsertAccount()
	{
		int response = connection.insertAccount(decID, encID);
		Assert.assertEquals(0, response);
	}
	
	@Test
	public void testDeleteAccount()
	{
		boolean response = connection.deleteAccount(decID, 1);
		Assert.assertEquals(true, response);
	}
	
	@Test
	public void testUpdateAccount()
	{
		boolean response = connection.updateAccount(decID, 1, encID);
		Assert.assertEquals(true, response);
	}

	@Test
	public void testChangeTitle()
	{
		boolean response = connection.changeTitle(decID, 1, "NewTitle");
		Assert.assertEquals(true, response);
	}
	
	
	
	
	
	
	
	
	
	
	
}
