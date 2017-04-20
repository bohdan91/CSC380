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

        testCheckID("Darkking271", "123456");

        testCheckUser("Darkking271");
        testCheckUser("kjfgoij");
    }

    public static void insertUser(String userID, String encID, String decID){
        try{
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());

            String[] commands = {"registeruser", userID, encID, decID};
            out.writeObject(commands);
            String[] response = (String[])in.readObject();

            if (response[0].equals("true"))
                System.out.println("User " + userID + " Added");
            else if (response[0].equals("false"))
                System.out.println("User already exists");

            client.close();
        }
        catch(ClassNotFoundException e){e.printStackTrace();}
        catch(IOException e){e.printStackTrace();}
    }

    public static void testCheckID(String user, String dec){
        getEncrypted(user);
        checkDecrypted(user, dec);
    }

    public static void getEncrypted(String user){
        try {
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            out = new ObjectOutputStream(outToServer);
            in = new ObjectInputStream(client.getInputStream());

            String[] commands = {"getencrypted", user};
            out.writeObject(commands);

            String[] r = (String[]) in.readObject();
            System.out.println("uniqueID: " + r[0]);
        }
        catch(ClassNotFoundException e){e.printStackTrace();}
        catch(IOException e){e.printStackTrace();}
    }

    public static void checkDecrypted(String user, String dec){
        try{
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            out = new ObjectOutputStream(outToServer);
            in = new ObjectInputStream(client.getInputStream());

            String[] commands = {"checkdecrypted", user, dec};
            out.writeObject(commands);

            String[] r = (String[])in.readObject();
            if (r[0].equals("true"))
                System.out.println("ID passed");
            else if (r[0].equals("false"))
                System.out.println("ID failed");
        }
        catch(ClassNotFoundException e){e.printStackTrace();}
        catch(IOException e){e.printStackTrace();}
    }

    public static void testCheckUser(String user){
        try{
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            out = new ObjectOutputStream(outToServer);
            in = new ObjectInputStream(client.getInputStream());

            String[] commands = {"isUserAvailable", user};
            out.writeObject(commands);

            String[] r = (String[])in.readObject();
            if (r[0].equals("false"))
                System.out.println("UserName " + user + " unavailable");
            else if (r[0].equals("true")){
                System.out.println("UserName " + user + " available");
            }
        }
        catch(IOException e){e.printStackTrace();}
        catch(ClassNotFoundException e){e.printStackTrace();}
    }

}
