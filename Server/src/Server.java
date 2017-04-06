
import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {

    public static void main(String[] args) {
        Connection conn = null;
        try{
            String url = "jdbc:sqlite3:test.db";
            conn = DriverManager.getConnection(url);

            System.out.println("Connection has been established to database");
        }catch(SQLException e){
            e.printStackTrace();
        }
        try {
            int clientNumber = 0;
            ServerSocket listener = new ServerSocket(9898);
            try {
                while (true) {
                    new Service(listener.accept(), clientNumber++).start();
                }
            } finally {
                listener.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    private static class Service extends Thread{
        private Socket socket;
        private int clientNumber;
        private ObjectInputStream in;
        private ObjectOutputStream out;


        public Service(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        //run
        public void run(){
            try {

                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());

                Object recieved = in.readObject();
                String[] rqst = new String[0];

                if(recieved.getClass() == rqst.getClass()) {

                    rqst = (String[]) recieved;
                    String method = rqst[0];
                    System.out.println("Processing request " + method);

                    switch (method) {
                        case "getEncrypted":
                            String id = getEncrypted(rqst[1]);
                            String[] sendBack = new String[]{id};
                            out.writeObject(sendBack);
                            break;
                        case "check":
                            compare();
                            break;
                    }
                } else{
                    log("Wrong Data Type was recieved!: " + recieved.getClass());
                }

                in.close();
                out.close();
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
                log("ERROR\n" + e);
            } catch(ClassNotFoundException e){
                e.printStackTrace();
                log("WRONG OBJECT WAS RECIEVED\n" + e);
            }
        }

        //get encrypted
        public  String getEncrypted(String userID){
            System.out.println("getEncrypted run");

            return null;
        }

        //compare
        public  void compare(){
            System.out.println("compare run");
        }
        //update acnt
        public  void update(){

        }

        //get accounts
        public void/*return account table*/ getAct(){

        }

        /**
         * Logs a simple message.  In this case we just write the
         * message to the server applications standard output.
         */
        private void log(String message) {
            System.out.println(message);
        }
    }



}

