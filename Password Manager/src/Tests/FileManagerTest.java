import Main.FileManager;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class FileManagerTest
{
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
		String fileName = "testFile";
		String path = System.getProperty("user.dir");
		byte[] key = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
		Assert.assertTrue(FileManager.createNewDB(fileName, key, path));
	}

	@Test
	public void testTryOpen_TRUE() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, IOException
	{
		String fileName = "testFile";
		String path = System.getProperty("user.dir");
		byte[] key = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
		File file = new File(path + File.separator +fileName + ".db");
		FileManager.createNewDB(fileName, key, path);
		Assert.assertTrue(FileManager.tryOpen(file, key));
	}
	
	@Test
	public void testTryOpen_FALSE() throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, IOException 
	{
		String fileName = "testFile";
		String path = System.getProperty("user.dir");
		byte[] key = {1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
		File file = new File(path + File.separator +fileName + ".db");
		FileManager.createNewDB(fileName, key, path);
		byte[] key2 = {7,6,5,4,3,2,1,7,6,5,4,3,2,1,7,6};
		Assert.assertFalse(FileManager.tryOpen(file, key2));
	}
	
	@Test
	public void testSave()
	{
		
	}
	
	@Test
	public void testLoad()
	{
		
	}
}
