package Main;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/** Created by Bohdan Yevdokymov
 *
 * Class for managing connections, reading, writing, encrypting, decrypting
 * Basically all of the backend functionality
 */
public class FileManager {

    private static String ALGO = "AES";
    private static int keyLength = 16;
    private Key key;
    private String uniqueId;
    private String username;

    /**
     * Default constructor for file manager, key has to be checked
     * first using static tryOpen method
     * @param username username to use
     * @param key correct encryption key for the file
     */
    public FileManager(String username, Key key, String dec){
        this.username = username;
        this.key = key;
        this. uniqueId = dec;
    }

    /**
     * Tries to decrypt given file with given password
     * if successful - initializes Main.fileManager object using given parameters
     *  and loads accounts into Main.accountTable after
     * @param username db encrypted file
     * @param pas byte array formatted password
     * @return
     */
    public static boolean tryOpen(String username, byte[] pas){
        try {

            byte[] password = formatPassword(pas);
            Key key = generateKey(password);
            Connection conn = Connection.getInstance();
            String enc = conn.getEncryptedId(username);
            String dec = tryDecrypt(key, enc);

            if(conn.checkDecryptedId(username, dec)){
                Main.fileManager = new FileManager(username, key, dec);
                Main.fileManager.load();
                return true;
            }else{
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println("Incorrect password!");
        } catch (NoSuchPaddingException e) {
            System.out.println("Incorrect password!");
        } catch (BadPaddingException e) {
            System.out.println("Incorrect password!");
        } catch (IllegalBlockSizeException e) {
            System.out.println("Incorrect password!");
        }
        return false;
    }

    /**
     *  Generates new unique ID for user, and registers a user on the server
     * @param username username to register
     * @param pas unformatted password in byte array
     * @return true if user was successfully registered, false if error occurred
     */
    public static boolean registerUser(String username, byte[] pas){
        byte[] password =formatPassword(pas);
        Key key = generateKey(password);
        SecureRandom random = new SecureRandom();
        String uniqueId = new BigInteger(160, random).toString(32);
        Connection conn = Connection.getInstance();


        try{
            String encryptedUID = tryEncrypt(key, uniqueId);
            conn.registerUser(username, encryptedUID, uniqueId);

        }catch (Exception e ) {
            System.err.println(e);
            return false;
        }

        return true;
    }

    /**
     * For AES 128 bit encryption 16 character password is needed
     * If password is less then 16 characters - this method repeats it
     * If password is more then 16 characters - password is cut at 16
     * @param pas password to format
     * @return formatted password
     */
    public static byte[] formatPassword(byte[] pas){
        byte[] formatedPassword = new byte[keyLength];
        int c = 0;
        for(int i = 0; i < keyLength; i ++)
        {
            if(pas.length > c){
                formatedPassword[i] = pas[c];
                c++;
            } else {
                c = 0;
            }
        }
        return formatedPassword;
    }

    /**
     * Public helper method for encryption, can only be used when
     * Main.fileManager was initialized, therefore has key initialized
     * @param data string to encrypt using existing key
     * @return encrypted String
     */
    public String encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return tryEncrypt(this.key, data);
    }

    /**
     * Public helper method for decryption, can only be used when
     * Main.fileManager was initialized, therefore has key initialized
     * @param encryptedData string with encrypted data
     * @return decrypted String
     */
    public String decrypt(String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {

        return tryDecrypt(key, encryptedData);
    }

    /**
     * Unlike "decrypt" method, this method is static and can be used even
     * if Main.fileManager is not initialized (in tryOpen method to check if password is correct)
     * @param key formatted and generated Key to decrypt with
     * @param encryptedData string with encrypted data
     * @throws BadPaddingException this exception is usually thrown if password is incorrect
     * @throws InvalidKeyException probably incorrect key as well
     * @return decrypted string if password was correct
     */
    public static String tryDecrypt (Key key, String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    /**
     * Static method for encryption with specified Key, can be used
     * even if Main.fileManager is not initialized
     * (if long lines are passed in parameter, then output will be divided into
     * multiple lines, so you should send line by pieces (of 30 prefferd) and then gluing encrypted output)
     * @param key formatted and generated key to encrypt with
     * @param data String to encrypt
     * @return
     * @throws BadPaddingException this exception is usually thrown if password is incorrect
     * @throws InvalidKeyException probably incorrect key as well
     */
    public static String tryEncrypt(Key key, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException{
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    /**
     * We use "generateKey()" method to generate a secret key for AES algorithm with a given key.
     * before generating a Key it has to be formatted using "formatPassword" method, because key has to be
     * exactly 16 characters long
     * @param keyValue formatted password
     * @return Key that is ready to use for encryption or decryption
     */
    public static Key generateKey(byte[] keyValue) {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
    }

    /**
     * Getter fot default key Length
     * @return default key length
     */
    public int getKeyLength(){
        return keyLength;
    }


    /**
     * Converts an Account into formatted string and encrypts it using
     * existing key. Usually is used to send account to the server as a String.
     * @param ac Account to encrypt
     * @return Encrypted String
     */
   public String encryptAccount(Account ac){
       try{
           String s = "/title=" + ac.getTitle() + "/username=" + ac.getUserName() + "/comment=" + ac.getComment() + "/type=" + ac.getType();
           s += "/url=" + ac.getURL() + "/password=" + ac.getPassword() + "/time=" + ac.getLastModified() + "/";

           //this part encrypts string by pieces (read in encrypt description why)
           //probably should be created in a separate method and called in public "encrypt" method
           if (s.length() > 30) {
               String t = "";
               int c = s.length() / 30;
               for (int i = 0; i < c; i++) {
                   t += encrypt(s.substring(i * 30, (i + 1) * 30));
               }
               t += encrypt(s.substring(c * 30));
               s = t;
               return s;
           } else {
               s = encrypt(s);
               return s;

           }
       } catch (IOException e) {
           e.printStackTrace();
       } catch (NoSuchAlgorithmException e) {
           e.printStackTrace();
       } catch (InvalidKeyException e) {
           e.printStackTrace();
       } catch (NoSuchPaddingException e) {
           e.printStackTrace();
       } catch (BadPaddingException e) {
           e.printStackTrace();
       } catch (IllegalBlockSizeException e) {
           e.printStackTrace();
       }
       return null;
   }

    /**
     * Saves new account to the server and if successful
     * adds it to the main table
     * @param ac Account to add
     * @return
     */
   public boolean insertAccount(Account ac){

           String s = encryptAccount(ac);
           Connection conn = Connection.getInstance();
           int id = conn.insertAccount(uniqueId, s);
           if(id != 0){
               ac.setId(id);
               Main.accountTable.put(ac.getTitle(), ac);
               return true;
           }else{
               return false;
           }
   }


   /**
     * Method to use if while editing an account title was used
     * @param oldTitle
     * @param newTitle
     * @return
     */
   public boolean changeTitle(String oldTitle, String newTitle){
           Main.accountTable.get(oldTitle).setTitle(newTitle);
           return true;
   }



    /**
     * Updates information about the account and uploads it to the server
     * as well as to main table
     * @param ac Account to update
     * @return
     */
   public boolean updateAccount(Account ac){
       Connection conn = Connection.getInstance();
       if(conn.updateAccount(uniqueId, ac.getId(),encryptAccount(ac) )){
           Main.accountTable.remove(ac.getTitle());
           Main.accountTable.put(ac.getTitle(), ac);
           return true;
       } else {
           return false;
       }
   }

    /**
     * Removes an Account from the server
     * @param ac
     * @return
     */
   public boolean deleteAccount(Account ac){
       Connection conn = Connection.getInstance();
       if(conn.deleteAccount(uniqueId, ac.getId())){
           return true;
       } else {
           return false;
       }
   }

    /**
     * Reads accounts from the server and puts them into Main.accountTable
     * every other lines -> accounts formatted "title/userName/note/type/url/password/date"
     * @return true if successfully read the file
     */
    public boolean load(){
        try {
            Connection conn = Connection.getInstance();
            String[] accounts = conn.getAccounts(uniqueId);

            //for(String line : accounts){
            for(int k = 0; k < accounts.length; k += 2){
                String line = accounts[k];
                String s = line;
                s = decrypt(s);
                String t ="";
                for(int i = 0; i < s.length(); i++){
                    if((int)s.charAt(i) != 2){
                        t += s.charAt(i);
                    }
                }

                s = t;

                String title = s.substring(s.lastIndexOf("title=") + 6,s.indexOf("/", s.lastIndexOf("title=")));

                String userName = s.substring(s.lastIndexOf("username=") + 9,s.indexOf("/", s.lastIndexOf("username=")));

                String note  = s.substring(s.lastIndexOf("comment=") + 8,s.indexOf("/", s.lastIndexOf("comment=")));

                String type = s.substring(s.lastIndexOf("type=") + 5,s.indexOf("/", s.lastIndexOf("type=")));

                String url = s.substring(s.lastIndexOf("url=") + 4,s.indexOf("/", s.lastIndexOf("url=")));

                String password = s.substring(s.lastIndexOf("password=") + 9, s.indexOf("/", s.lastIndexOf("password=")));

                long lastModified = Long.valueOf(s.substring(s.lastIndexOf("time=") + 5, s.indexOf("/",s.lastIndexOf("time=") )));

                Account ac = new Account(title, userName, password, note, type, url, lastModified);

                ac.setId(Integer.parseInt(accounts[k+1]));

                Main.accountTable.put(title, ac);


            }


        } catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Clears the Key file, for security purpose
     */
    public void resetPassword(){
        key = null;
        uniqueId = null;
    }

    /**
     * Return current DB file that is used
     * @return .db file
     */
    public String getUsername(){
        return username;
    }

}

