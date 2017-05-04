package Main;

import java.io.*;
import java.net.Socket;

/**
 * Created by Bohdan Yevdokymov
 *
 * Class that manages all requests and receives information
 * from the server, each request is processed separately
 */
public class Connection {
    private static Connection instance = null;
    //private static final String address = "127.0.0.1";

    private static String address = "172.20.10.6";
    //private static final String address = "passman.ddns.net";

    private static final int port = 9898;
    private Socket socket;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static boolean isConnected = false;

    /**
     * This is "singleton" type of class and instance can only
     * be obtained from "getInstance" method to avoid multiple
     * instances.
     */
    protected Connection(){
    }

    /**
     * Method that is used to obtain and use an instance of
     * Connection class
     * @return instance of Connection Class
     */
    public static Connection getInstance(){
        if(instance == null){
            return instance = new Connection();
        } else {
            return instance;
        }
    }

    /**
     * Opens a connection with a server using static address and port,
     * creates in and output Streams.
     * @return
     */
    private boolean openConnection(){
        try{
            socket = new Socket(address, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            return true;
        } catch(IOException e){
            System.out.println("Could not connect to the Server!");
            e.printStackTrace();
            isConnected = false;
            return false;
        }
    }

    /**
     * Closes a connection with a server
     */
    private void closeConnection(){
        try {
            isConnected = false;
            out.close();
            in.close();
            socket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Requests encrypted unique ID for given username from the server
     * @param username username to ger encrypted id for
     * @return Encrypted ID
     */
    public String getEncryptedId(String username){
        String[] request = new String[2];
        request[0] = "getEncrypted";
        request[1] = username;
        if(openConnection()) {
            if (send(request)) {
                String r = receive()[0];
                closeConnection();
                return r;
            }
        }
        closeConnection();
        return null;
    }

    /**
     * Sends username and possible decrypted ID to the server
     * Server compares it to existing one and gives the result
     * @param username username to check for
     * @param decrypted possible decrypted ID
     * @return true if decrypted ID is correct
     */
    public boolean checkDecryptedId(String username, String decrypted){
        String[] request = new String[3];
        request[0] = "checkDecrypted";
        request[1] = username;
        request[2] = decrypted;

        if(openConnection()) {
            if (send(request)) {
                String response = receive()[0];
                if (response.toLowerCase().equals("true")) ;
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Loads array of encrypted Strings - that are accounts from the server
     * @param decryptedId decrypted id to identify
     * @return array of Strings (formatted Accounts)
     */
    public String[] getAccounts(String decryptedId){
        String[] accounts = new String[0];
        String[] request  = new String[2];
        request[0] = "getAccounts";
        request[1] = decryptedId;

        if(openConnection()){
            if(send(request)){
                accounts = receive();
            }
        }
        closeConnection();
        return accounts;
    }

    /**
     * Adds new Account for the user.
     * @param dec identifier
     * @param enc formatted and encrypted
     * @return
     */
    public int insertAccount(String dec, String enc){
        String[] request = new String[3];
        request[0] = "insertAccount";
        request[1] = dec;
        request[2] = enc;
        String[] response;
        if(openConnection()){
            if(send(request)){
                response = receive();
                closeConnection();
                return Integer.parseInt(response[0]);
            }
            closeConnection();
        }
        return 0;
    }

    /**
     * Method to remove specified account from the server
     * @param decID identifier
     * @param id title of an account to remove
     * @return true if was removed
     */
    public boolean deleteAccount(String decID, int id){
        String[] request = new String[3];
        request[0] = "deleteAccount";
        request[1] = decID;
        request[2] = Integer.toString(id);
        String[] response;
        if(openConnection()){
            if(send(request)){
                response = receive();
                if(response[0].equals("true")){
                    closeConnection();
                    return true;
                }
            }
            closeConnection();
        }
        return false;
    }

    /**
     * Updates an account information for specified user with specified title
     * @param decId identifier
     * @param id title to update
     * @param enc String to replace old one
     * @return
     */
    public boolean updateAccount(String decId, int id, String enc){
        String[] request = new String[4];
        request[0] = "updateAccount";
        request[1] = decId;
        request[2] = Integer.toString(id);
        request[3] = enc;
        String[] response;
        if(openConnection()){
            if(send(request)){
                response = receive();
                if(response[0].equals("true")){
                    closeConnection();
                    return true;
                }
            }
            closeConnection();
        }
        return false;
    }

    /**
     * Changes title to existing account on the server, needed to be done
     * before updating an account
     * @param decId identifier
     * @param id old title to replace
     * @param newTitle new title to replace with
     * @return true if sucessful
     */
    public boolean changeTitle(String decId, int id, String newTitle){
        String[] request = new String[4];
        request[0] = "changeTitle";
        request[1] = decId;
        request[2] = Integer.toString(id);
        request[3] = newTitle;
        String[] response;
        if(openConnection()){
            if(send(request)){
                response = receive();
                if(response[0].equals("true")){
                    closeConnection();
                    return true;
                }
            }
            closeConnection();
        }
        return false;
    }

    /**
     * Method to check on the server if username is not taken already
     * and can be used
     * @param username username to check
     * @return true if available
     */
    public boolean isUserAvailable(String username){
        String[] request = new String[2];
        request[0] = "isUserAvailable";
        request[1] = username;

        if(openConnection()){
            if(send(request)){
                String response = receive()[0];
                if(response.toLowerCase().equals("true")){
                    closeConnection();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Register new user to the server, has to be confirmed that username is available before
     * using method "isUserAvailable"
     * @param username username to register
     * @param encryptedId generated unique id
     * @param decryptedId encrypted unique id
     * @return true if was successful
     */
    public boolean registerUser(String username, String encryptedId, String decryptedId){
        String[] request = new String [4];
        request[0] = "registerUser";
        request[1] = username;
        request[2] = encryptedId;
        request[3] = decryptedId;

        if(openConnection()){
            if(send(request)){
                String response = receive()[0];
                if(response.toLowerCase().equals("true"));{
                    closeConnection();
                    return true;
                }
            }
        }
        closeConnection();
        return false;
    }

    /**
     * Sends request to the server in format of String array,
     * where first string is method name and all of the others -
     * parameters.
     * @param ar String array to send.
     * @return
     */
    private  boolean send(String[] ar){
        if(isConnected || openConnection()){
            try {
                out.writeObject(ar);
                return true;
            }catch(IOException e){
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Receives information from the server in format of string arrays.
     * @return response from the server
     */
    private String[] receive(){
        if(isConnected){
            try{
                String[] receieve = (String[])in.readObject();
                return receieve;
            } catch(ClassNotFoundException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setIP(String ip){
        this.address = ip;
    }
}
