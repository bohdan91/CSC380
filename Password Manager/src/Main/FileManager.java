package Main;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.math.BigInteger;


/**
 * Class for managing db files, reading, writing, encrypting, decrypting
 *
 */
public class FileManager {

    private static String ALGO = "AES";
    private static int keyLength = 16;
    private File dbFile;
    private String controlKey = "abc123";
    private byte[] keyValue;
    private String uniqueId;


    public FileManager(){

    }

    public boolean tryOpen(File file, byte[] pas){
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            keyValue = new byte[keyLength];
            int c = 0;
            for(int i = 0; i < keyLength; i ++)
            {
                if(pas.length > c){
                    keyValue[i] = pas[c];
                    c++;
                } else {
                    c = 0;
                }
            }
            dbFile = file;

            String control = br.readLine();
            decrypt(control);

            load();



            br.close();
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean createNewDB(String name, byte[] pas, String path){
        keyValue = new byte[keyLength];
        SecureRandom random = new SecureRandom();
        uniqueId = new BigInteger(160, random).toString(32);
        int c = 0;
        for(int i = 0; i < keyLength; i ++)
        {
            if(pas.length > c){
                keyValue[i] = pas[c];
                c++;
            } else {
                c = 0;
            }
        }
        try{
            dbFile = new File(path + "/" + name + ".db");
            BufferedWriter bw = new BufferedWriter(new FileWriter(dbFile));
            bw.write(encrypt(uniqueId));
            bw.close();
        }catch (Exception e ) {
            System.err.println(e);
        }

        return true;
    }

    public String encrypt(String Data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key key = generateKey(keyValue);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        System.out.println("encrypted: " + Data + " to: " + encryptedValue);
        return encryptedValue;
    }

    public String decrypt(String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        Key key = generateKey(keyValue);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    public void populateTable(){

    }

    //We use "generateKey()" method to generate a secret key for AES algorithm with a given key.
    private Key generateKey(byte[] keyValue) {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    public void setKey(byte[] key){
        keyValue = key;
    }

    public int getKeyLength(){
        return keyLength;
    }

    /**
     * Saving accounts to the file
     * first line unique identifier
     * every other line -> account formatted "title/userName/note/type/url/password/date"
     * @return
     */
    public boolean save(){
        try {
            FileWriter fw = new FileWriter(dbFile);
            BufferedWriter out = new BufferedWriter(fw);
            System.out.println("Writess: " + uniqueId);
            out.write(encrypt(uniqueId));
            out.newLine();

            for(Account ac : Main.accountTable.values()){
                String s = "/title=" +ac.getTitle() + "/username=" + ac.getUserName() + "/comment=" + ac.getComment() + "/type=" + ac.getType() + "/url=" + ac.getURL() + "/";
                //s = encrypt(s);
                s += "***" + ac.getEncryptedPassword();
                s += "/time=" + ac.getLastModified();
                System.out.println("Writes: " + s);
                out.write(s);
                out.newLine();
            }

            out.close();
            fw.close();
            System.out.println("Saved");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean load(){
        try {
            FileReader fr = new FileReader(dbFile);
            BufferedReader in = new BufferedReader(fr);

            this.uniqueId = decrypt(in.readLine());

            String line = in.readLine();

            while(line != null){
                String s = line.substring(0, line.indexOf("***"));
                s = decrypt(s);
                System.out.println("s: "+ s);
                String titile = s.substring(s.lastIndexOf("title="),s.indexOf("/", s.lastIndexOf("title=")));

                System.out.println(titile);

                String userName = s.substring(s.lastIndexOf("username="),s.indexOf("/", s.lastIndexOf("username=")));

                System.out.println(userName);

                String note  = s.substring(s.lastIndexOf("note="),s.indexOf("/", s.lastIndexOf("note=")));

                String type = s.substring(s.lastIndexOf("type="),s.indexOf("/", s.lastIndexOf("type=")));

                String url = s.substring(s.lastIndexOf("url="),s.indexOf("/", s.lastIndexOf("url=")));

                String password = line.substring(line.lastIndexOf("***"), line.indexOf("/", line.lastIndexOf("***")));

                long lastModified = Long.valueOf(line.substring(line.lastIndexOf("time="), line.indexOf("/",line.lastIndexOf("time=") )));

                Account ac = new Account(titile, userName, password, note, type, url, lastModified);

                Main.accountTable.put(titile, ac);

                line = in.readLine();
            }
            in.close();
            fr.close();

        } catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

}

