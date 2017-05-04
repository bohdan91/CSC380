package Test;

import org.junit.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class TestService 
{
	static ObjectOutputStream out;
	static ObjectInputStream in;
	static Socket client;
	static String serverName = "127.0.0.1";
	static int port = 9898;
	static Connection connect;
	static String user = "TestUser1";
	static String encID = "EncryptedID";
	static String decID = "DecryptedID";
	static String act1 = "ThisIsAnAccount";
	static String act2 = "ThisIsAnotherAccount";
	static String act3 = "ThisIsTheLastAccount";
	static String act4 = "ThisAccountWillBeDeleted";
	static String act5 = "ThisAccountWillBeUpdated";

    @BeforeClass
    public static void buildTables()throws SQLException{
        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "test.db";
        connect = DriverManager.getConnection(url);
        String sql1 = "INSERT INTO users values(\"" + user + "\", \"" + encID + "\", \"" + decID + "\")";
        String sql2 = "INSERT INTO accounts values(\"" + user + "\", " + 1 + ", \"" + act1 + "\")";
        String sql3 = "INSERT INTO accounts values(\"" + user + "\", " + 2 + ", \"" + act2 + "\")";
        String sql4 = "INSERT INTO accounts values(\"" + user + "\", " + 3 + ", \"" + act3 + "\")";
        String sql5 = "INSERT INTO accounts values(\"" + user + "\", " + 4 + ", \"" + act4 + "\")";
        String sql6 = "INSERT INTO accounts values(\"" + user + "\", " + 5 + ", \"" + act5 + "\")";
        Statement stmt = connect.createStatement();
        stmt.execute(sql1);
        stmt.execute(sql2);
        stmt.execute(sql3);
        stmt.execute(sql4);
        stmt.execute(sql5);
        stmt.execute(sql6);
        stmt.close();
    }

    @AfterClass
    public static void clearTables()throws SQLException{
        String sqlUsers = "DELETE FROM users";
        String sqlAccounts = "DELETE FROM accounts";
        Statement stmt = connect.createStatement();
        stmt.execute(sqlUsers);
        stmt.execute(sqlAccounts);
        stmt.close();
        connect.close();
    }

	@Before 
	public void ConnectBeforeEach() throws UnknownHostException, IOException
	{
		client = new Socket(serverName, port);
		out = new ObjectOutputStream(client.getOutputStream());
		in = new ObjectInputStream(client.getInputStream());
	}
	
	@After
	public void DisconnectAfterEach() throws IOException, SQLException, NullPointerException
	{
	    client.close();
		out.close();
		in.close();
	}

	@Test
    public void InsertUserTest_Positive() throws IOException, ClassNotFoundException {
        String userID = "TestUser2";
        String encID = "EncryptedID";
        String decID = "DecryptedID";
        String[] output = {"registeruser", userID, encID, decID};
        out.writeObject(output);
        String[] input = (String[]) in.readObject();
        Assert.assertEquals("true", input[0]);
    }

    @Test
    public void InsertUserTest_Negative() throws IOException, ClassNotFoundException{
        String userID = "TestUser1";
        String encID = "dGVzdFBhc3N3b3JkMTIz";
        String decID = "testPassword123";
        String[] output = {"registeruser", userID, encID, decID};
        out.writeObject(output);
        String[] input = (String[])in.readObject();
        Assert.assertEquals("false", input[0]);
    }
	
	@Test
	public void CheckUserTest_Positive() throws IOException, ClassNotFoundException
	{
		String[] output = {"isUserAvailable", user};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertEquals("false", input[0]);
	}
	
	@Test
	public void CheckUserTest_Negative() throws IOException, ClassNotFoundException
	{
		String user = "testUser123";
		String[] output = {"isUserAvailable", user};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertEquals("true", input[0]);
	}

	@Test
	public void GetEncryptedTest() throws IOException, ClassNotFoundException
	{
		String[] output = {"getencrypted", user};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertEquals(encID, input[0]);
	}
	
	@Test
	public void checkDecryptedTest_Positive() throws IOException, ClassNotFoundException
	{
		String[] output = {"checkdecrypted", user, decID};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertTrue(input[0].equals("true"));
	}
	
	@Test
	public void checkDecryptedTest_Negative() throws IOException, ClassNotFoundException
	{
		String decID = "Decrypted";
		String[] output = {"checkdecrypted", user, decID};
		out.writeObject(output);
		String[] input = (String[])in.readObject();
		Assert.assertFalse(input[0].equals("true"));
	}

	@Test
    public void getAccountsTest_Positive() throws IOException, ClassNotFoundException{
        String[] output = {"getaccounts", decID};
        out.writeObject(output);
        String[] input = (String[])in.readObject();
        Assert.assertEquals(act1, input[0]);
        Assert.assertEquals(act2, input[2]);
        Assert.assertEquals(act3, input[4]);
    }

    @Test
    public void insertAccountsTest_Positive()throws IOException, ClassNotFoundException{
        String[] output = {"insertaccount", decID, "ThisIsAnotherTestAccount"};
        out.writeObject(output);
        String[] input = (String[])in.readObject();
        Assert.assertTrue(input[0].equals("6"));
    }

    @Test
    public void insertAccountsTest_Negative()throws IOException, ClassNotFoundException{
        String[] output = {"insertaccount", "TestUser3", "ThisIsAnAccount"};
        out.writeObject(output);
        String[] input = (String[])in.readObject();
        Assert.assertTrue(input[0].equals("0"));
    }

    @Test
    public void deleteaccountsTest_POsitive() throws IOException, ClassNotFoundException {
        String[] output = {"deleteaccount", decID, "4"};
        out.writeObject(output);
        String[] input = (String[])in.readObject();
        Assert.assertTrue(input[0].equals("true"));
    }

    @Test
    public void deleteaccountsTest_Negetave()throws IOException, ClassNotFoundException{
        String[] output = {"deleteaccount", "132435", "4"};
        out.writeObject(output);
        String[] input = (String[])in.readObject();
        Assert.assertTrue(input[0].equals("false"));
    }

    @Test
    public void updateaccountsTest_Positive()throws IOException, ClassNotFoundException{
        String[] output = {"updateaccount", decID, "5", "ThisIsUpdatedInfo"};
        out.writeObject(output);
        String[] input = (String[])in.readObject();
        Assert.assertTrue(input[0].equals("true"));
    }

    @Test
    public void updateaccountsTest_Negetive()throws IOException, ClassNotFoundException{
        String[] output = {"updateaccount", "132435", "6", "ThisIsUpdatedInfo"};
        out.writeObject(output);
        String[] input = (String[])in.readObject();
        Assert.assertTrue(input[0].equals("false"));
    }
}
