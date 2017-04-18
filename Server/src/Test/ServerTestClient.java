package Test;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.*;

/**
 * Created by darki on 3/30/2017.
 */
public class ServerTestClient {

    static ObjectOutput out;
    static ObjectInputStream in;
    //static String serverName = "passman.ddns.net";
    static String serverName = "127.0.0.1";
    static int port = 9898;

    public static void main(String[] args){
        insertUser("Darkking271", "abcdef", "123456");
        insertUser("Bodika", "efrgth", "132435");
        insertUser("scusemae", "jnhbgv", "134679");
        //testCheckID();
    }

    public static void insertUser(String userID, String encID, String decID){
        try{
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            out = new ObjectOutputStream(outToServer);
            in = new ObjectInputStream(client.getInputStream());

            String[] commands = {"registeruser"};
            out.writeObject(commands);
            String[] info = {userID, encID, decID};
            out.writeObject(info);
            String[] response = (String[])in.readObject();

            if (response[0].equals("true"))
                System.out.println("User Added");
            else if (response[0].equals("false"))
                System.out.println("User already exists");

            client.close();
        }
        catch(ClassNotFoundException e){e.printStackTrace();}
        catch(IOException e){e.printStackTrace();}
    }

    public static void testCheckID(){
        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            out = new ObjectOutputStream(outToServer);
            in = new ObjectInputStream(client.getInputStream());

            String[] commands = {"checkid", "Darkking271"};
            out.writeObject(commands);

            String r = (String) in.readObject();
            System.out.println("uniqueID: " + r);
            String decrypted = "123456";
            out.writeObject(decrypted);

            String check[] = (String[])in.readObject();
            if (check[0].equals("true"))
                System.out.println("ID passed");
            else if (check[0].equals("false"))
                System.out.println("false");

        }
        catch(ClassNotFoundException e){e.printStackTrace();}
        catch(IOException e){e.printStackTrace();}

    }
}
