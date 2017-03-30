package Main;

import java.io.*;
import java.net.*;

public class Server extends Thread{

    private ServerSocket serverSocket;

    /*

     */
    public Server(int port)throws IOException{
        serverSocket = new ServerSocket(port);
    }

    public static void main(String[] args) {
        args = new String[1];
        args[0] = "6066";
        int port = Integer.parseInt(args[0]);
        try {
            Thread t = new Server(port);
            t.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //run
    public void run(){
        while(true){
            try{
                System.out.println("Waiting for connection to client");
                Socket server = this.serverSocket.accept();
                DataInputStream in = new DataInputStream(server.getInputStream());
                System.out.println("Just ceonnected to " + server.getRemoteSocketAddress());

                String rqst = in.readUTF();
                System.out.println("Processing request " + rqst);
                switch (rqst){
                    case "pull":
                        getEncrypted();
                        break;
                    case "check":
                        compare();
                        break;
                }
                server.close();
                System.out.println();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    //get encrypted
    public static String getEncrypted(){
        System.out.println("getEncrypted run");
        return null;
    }

    //compare
    public static void compare(){
        System.out.println("compare run");
    }
    //update acnt
    public static void update(){

    }

    //get accounts
    public static void/*return account table*/ getAct(){

    }

}
