package Test;

import java.io.*;
import java.net.*;

/**
 * Created by darki on 3/30/2017.
 */
public class ServerTestClient {

    public static void main(String[] args){
        args = new String[2];
        //args[0] = "passman.ddns.net";
        args[0] = "127.0.0.1";
        args[1] = "9898";
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        try{
            System.out.println("Connecting to " + serverName + " on port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            ObjectOutput out = new ObjectOutputStream(outToServer);

            String[] things = {"getencryptedid", "Darkking271"};
            out.writeObject(things);
            //InputStream inFromServer = client.getInputStream();
            //DataInputStream in = new DataInputStream(inFromServer);

            client.close();
        }catch(IOException e){e.printStackTrace();}
    }
}
