package Main;

import java.io.*;
import java.net.Socket;

/**
 * Created by Bohdan on 3/30/17.
 */
public class Connection {
    private static Connection instance = null;
    private static final String address = "127.0.0.1";
    private static final int port = 9898;
    private Socket socket;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static boolean isConnected = false;

    protected Connection(){
    }

    public static Connection getInstance(){
        if(instance == null){
            return instance = new Connection();
        } else {
            return instance;
        }
    }

    public static void main(String[] args) {
        //getInstance().openConnection();
        System.out.println(getInstance().getEncryptedId("Darkking271"));

        //getInstance().closeConnection();
    }

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

    public boolean insertAccount(String dec, String title, String enc){
        String[] request = new String[4];
        request[0] = "insertAccount";
        request[1] = dec;
        request[2] = title;
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

    public boolean updateAccount(String decId, String title, String enc){
        String[] request = new String[4];
        request[0] = "updateAccount";
        request[1] = decId;
        request[2] = title;
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

    public boolean changeTitle(String decId, String title, String newTitle){
        String[] request = new String[4];
        request[0] = "changeTitle";
        request[1] = decId;
        request[2] = title;
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

    public ObjectInputStream getIn(){
        return this.in;
    }
    public ObjectOutputStream getOut(){
        return this.out;
    }

}
