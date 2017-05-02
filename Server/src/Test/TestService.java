package Test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestService 
{
	static ObjectOutputStream out;
	static ObjectInputStream in;
	static Socket client;
//	static String serverName = "passman.ddns.net";
	static String serverName = "127.0.0.1";
	static int port = 9898;
	static Connection connect;
	static String userID, encID, decID, title;
	
	@Before 
	public void ConnectBeforeEach() throws UnknownHostException, IOException
	{
		client = new Socket(serverName, port);
		client.getRemoteSocketAddress();
		out = new ObjectOutputStream(client.getOutputStream());
		in = new ObjectInputStream(client.getInputStream());
	}
	
	@After
	public void DisconnectAfterEach() throws IOException, SQLException, NullPointerException
	{
		Statement stmt = connect.createStatement();
		ResultSet rs = stmt.executeQuery("DELETE FROM accounts WHERE user = \"" + userID);
		stmt.close();
		out.close();
		in.close();
	}
	
	@Test
	public void CheckUserTest_Positive() throws IOException, ClassNotFoundException
	{
		String user = "exampleEntry345";
		String[] output = {"isUserAvailable", user};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertEquals("true", input[0]);
	}
	
	@Test
	public void CheckUserTest_Negative() throws IOException, ClassNotFoundException
	{
		String user = "testUser123";
		String[] output = {"isUserAvailable", user};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertEquals("false", input[0]);
	}
	
	@Test
	public void InsertUserTest_Negative() throws IOException, ClassNotFoundException 
	{
		String userID = "testUser123";
		String encID = "dGVzdFBhc3N3b3JkMTIz";
		String decID = "testPassword123";
		String[] output = {"registeruser", userID, encID, decID};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertEquals("false", input[0]);
	}
	
	@Test
	public void InsertUserTest_Positive() throws IOException, ClassNotFoundException
	{
		String userID = "AtteptedInputOfUser";
		String encID = "SW5zZXJ0UGFzc3dvcmRUcmlhbA==";
		String decID = "InsertPasswordTrial";
		String[] output = {"registeruser", userID, encID, decID};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertEquals("true", input[0]);
	}

	
	@Test
	public void GetEncryptedTest() throws IOException, ClassNotFoundException
	{
		String userID = "Darkking271";
		String[] output = {"getencrypted", userID};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertEquals("Ff9MQt6qSCUeHEZecX6rF8QN5M9a5cI2xD/BFypDLQV24eImjydbMKB3kyayDqki", input[0]);
	}
	
	@Test
	public void checkDecryptedTest_Positive() throws IOException, ClassNotFoundException
	{
		String userID = "Bodika";
		String decID = "132435";
		String[] output = {"checkdecrypted", userID, decID};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertTrue(input[0].equals("true"));
	}
	
	@Test
	public void checkDecryptedTest_Negative() throws IOException, ClassNotFoundException
	{
		String userID = "scusemae";
		String decID = "134567";
		String[] output = {"checkdecrypted", userID, decID};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertFalse(input[0].equals("false"));
	}

}
