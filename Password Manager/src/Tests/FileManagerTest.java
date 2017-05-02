import Main.Account;
import Main.Connection;
import Main.FileManager;
import Main.Main;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class FileManagerTest
{

	@BeforeClass
	public static void initialize(){
		Main.accountTable = new HashMap<>();
		Connection conn = Connection.getInstance();
		conn.setIP("127.0.0.1");
	}
	@Test
	public void testFormatPassword()
	{
		String password = "testPassword1234";
		byte[] initial = password.getBytes();
		byte[] formatted = FileManager.formatPassword(initial);
		Assert.assertArrayEquals(formatted, initial);
	}
	
	@Test
	public void testTryEncrypt() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException
	{
		String password = "testPassword1";
		byte[] initial = password.getBytes();
		byte[] formatted = FileManager.formatPassword(initial);
		Key key = FileManager.generateKey(formatted);
		String result = FileManager.tryEncrypt(key, "testmessage");
		Assert.assertEquals(result, "b0dw5qQkPYREeOKNafgTfA==");
	}
	
	@Test
	public void testTryDecrypt() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, IOException
	{
		String password = "testPassword1";
		byte[] initial = password.getBytes();
		byte[] formatted = FileManager.formatPassword(initial);
		Key key = FileManager.generateKey(formatted);
		String result = FileManager.tryDecrypt(key, "b0dw5qQkPYREeOKNafgTfA==");
		Assert.assertEquals(result, "testmessage");
	}
	
	@Test
	public void testCreateNewDB()
	{
		String userName = "testUser1";
		byte[] key = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
		FileManager.registerUser(userName, key);
		Connection conn = Connection.getInstance();
		Assert.assertTrue(!conn.isUserAvailable("testUser1"));
	}

	@Test
	public void testTryOpen_TRUE() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, IOException
	{
		String userName = "testUser2";
		byte[] key = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
		FileManager.registerUser(userName, key);
		Assert.assertTrue(FileManager.tryOpen(userName, key));
	}
	
	@Test
public void testTryOpen_FALSE() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, IOException
{
	String userName = "testUser3";
	byte[] key = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
	FileManager.registerUser(userName, key);
	byte[] key2 = {7,6,5,4,3,2,1,7,6,5,4,3,2,1,7,6};
	Assert.assertFalse(FileManager.tryOpen(userName, key2));
}

	@Test
	public void testLoad() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
		String userName = "testUser4";
		byte[] key = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
		FileManager.registerUser(userName, key);
		FileManager.tryOpen(userName, key);
		Account ac = new Account("mytitle", userName, "123", "comment", "type", "url");
		Main.fileManager.insertAccount(ac);
		Assert.assertTrue(Main.accountTable.containsKey("title"));
	}

	@Test
	public void testUpdateAccount() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
		String userName = "testUser5";
		byte[] key = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
		FileManager.registerUser(userName, key);
		FileManager.tryOpen(userName, key);
		Account ac = new Account("oldtitle", userName, "123", "comment", "type", "url");
		Main.fileManager.insertAccount(ac);
		ac.setTitle("newTitle");
		Main.fileManager.updateAccount(ac);
		Assert.assertTrue(Main.accountTable.containsKey("newTitle"));
	}

	@Test
	public void testEncryptAccount() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
		String userName = "testUser6";
		byte[] key = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
		FileManager.registerUser(userName, key);
		FileManager.tryOpen(userName, key);
		Account ac = new Account("title", userName, "123", "comment", "type", "url");
		System.out.println(Main.fileManager.encryptAccount(ac));
	}
}
