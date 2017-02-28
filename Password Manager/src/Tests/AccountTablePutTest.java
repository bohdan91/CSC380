package Tests;

import Main.Account;
import Main.AccountTable;
import Main.FileManager;
import Main.Main;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.*;

public class AccountTablePutTest{
	AccountTable table;
	Account ac;
	Account bc;
	Account cc;
	FileManager manage;
	
    @Test
    public void testCreation()
    throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, 
    NoSuchAlgorithmException, NoSuchPaddingException{
        Main.fileManager = new FileManager();
    	table = new AccountTable();
        ac = new Account("Email", "darking271", "123456", "My Email", "Email", "Gmail.com");
        bc = new Account("Bank", "avoytovich", "123123","My Bank Account", "Banking", "Chase.com");
        cc = new Account("Steam", "Darkking271", "qwerty","My Gaming Account", "Gaming", "SteamPowered.com");
        table.put(ac.getTitle(), ac);
        Assert.assertEquals(3, table.size());
    }
    
    //@Test
    //public void test
}