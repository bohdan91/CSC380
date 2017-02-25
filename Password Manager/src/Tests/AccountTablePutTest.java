package Tests;

import Main.Account;
import Main.AccountTable;
import Main.FileManager;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.*;

public class AccountTablePutTest
{
	AccountTable table;
	Account ac;
	FileManager manage;
	
    @Test
    public void testCreation()
    throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, 
    NoSuchAlgorithmException, NoSuchPaddingException  
    {
        table = new AccountTable();
        ac = new Account("Email", "Darkking271", "123456", 
			     "Me Email", "Email", "Gmail.com");
        manage = new FileManager();
    }
    
    @Test
    public void testPut()
    throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, 
    NoSuchAlgorithmException, NoSuchPaddingException  
    {
    	table.put(ac.getTitle(), ac);
    	Assert.assertEquals("Darkking271", table.get(ac.getTitle()).getTitle());
    }
}