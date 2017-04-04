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
        getInstance().openConnection();
            System.out.println(getInstance().getEncryptedId("bodika"));

        getInstance().closeConnection();
    }

    public boolean openConnection(){
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

    public void closeConnection(){
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
        request[0] = "getEncryptedId";
        request[1] = username;
        if(send(request)){
            return receive()[0];
        }else {
            return null;
        }
    }

    public boolean checkDecryptedId(String decrypted){
        String[] request = new String[1];
        request[0] = decrypted;

        if(send(request)){
            String response = receive()[0];
            if(response.toLowerCase().equals("true"));{
                return true;
            }
        }
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
