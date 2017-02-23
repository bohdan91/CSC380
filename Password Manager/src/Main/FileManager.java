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
            String result = decrypt(control);
            System.out.println(result);

            if(result.equals(controlKey)){
                br.close();
                return true;
            }

            br.close();
        } catch(Exception e){
            System.err.println(e);
            return false;
        }
        return false;
    }

    public boolean createNewDB(String name, byte[] pas, String path){
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
        try{
            dbFile = new File(path + "/" + name + ".db");
            BufferedWriter bw = new BufferedWriter(new FileWriter(dbFile));
            bw.write(encrypt(controlKey));
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
}
