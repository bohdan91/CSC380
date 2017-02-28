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

	@BeforeClass
    public static void init(){
        Main.fileManager = new FileManager();
	    String password = "mysecretpassword";
        byte[] passUnFormatted = password.getBytes();
        byte[] passFormatted = new byte[Main.fileManager.getKeyLength()];
        int c =0;
        for(int i = 0; i < Main.fileManager.getKeyLength(); i ++)
        {
            if(password.length() > c){
                passFormatted[i] = passUnFormatted[c];
                c++;
            } else {
                c = 0;
            }
        }
        Main.fileManager.setKey(passFormatted);
    }

    @Test
    public void testCreation()
    throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, 
    NoSuchAlgorithmException, NoSuchPaddingException{
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