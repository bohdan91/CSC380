package Tests;
import Main.FileManager;
import org.junit.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


/**
 * Created by Bohdan Yevdokymov
 */
public class FileMenegerTest {

    private static FileManager file;
    private static int keyLegth;
    private static byte[] key;
    private static File dbFile;


    @BeforeClass
    public static void initFileManager(){
        file = new FileManager();
        keyLegth = file.getKeyLength();
        dbFile = new File(System.getProperty("user.dir") + "/" + "testFile2" + ".db");

        key = new byte[keyLegth];
        Random rand = new Random();

        for(int i =0; i < keyLegth; i ++){

            key[i] = (byte) rand.nextInt(10);
        }

        file.setKey(key);

        try{
            file.createNewDB("testFile2", key, System.getProperty("user.dir"));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void finishTest(){
        dbFile.delete();
    }

    @Test
    public void testEncryptDecrypt () throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {

            String send     = "testmessage123";
            String encoded  = file.encrypt(send);

            Assert.assertNotEquals(send, encoded);

            String recieved = file.decrypt(encoded);

            Assert.assertEquals(send, recieved);


    }

    @Test
    public void testCreateNewDB(){

        String fileName = "testFile";
        String path     =  System.getProperty("user.dir");

        file.createNewDB(fileName, key, path);

        try{
            File f = new File(path + "/" + fileName + ".db");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            br.close();
            fr.close();
            f.delete();

        }catch(Exception e){
            Assert.fail();
        }

    }

    @Test
    public void testTryOpenPositive(){

        Assert.assertTrue(file.tryOpen(dbFile, key));

    }

    @Test
    public void testTryOpenNegative(){
        byte []newKey = new byte[keyLegth];
        Random rand = new Random();

        for(int i =0; i < keyLegth; i ++){

            newKey[i] = (byte) rand.nextInt(10);
        }

        Assert.assertFalse(file.tryOpen(dbFile, newKey));
    }

}


















