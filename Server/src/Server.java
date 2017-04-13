
import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {

    public static void main(String[] args) {
        Connection conn = null;
        try{
            String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "test.db";
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
                    new Service(listener.accept(), clientNumber++, conn).start();
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
        private Connection connect;


        public Service(Socket socket, int clientNumber, Connection connect) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            this.connect = connect;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        //run
        public void run(){
            try {

                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());

                String[] recieved = (String[])in.readObject();

                if(recieved.getClass() == recieved.getClass()) {

                    String method = recieved[0];
                    System.out.println("Processing request " + method);

                    switch (method.toLowerCase()) {
                        case "getencryptedid":
                            String id = getEncrypted(recieved[1]);
                            String[] sendBack = new String[]{id};
                            //out.writeObject(sendBack);
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
            String sql = "SELECT uniqueID_enc FROM users WHERE user = \"" + userID + "\"";

            try(Connection conn = this.connect;
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)){

                System.out.println(rs.getString("uniqueID_enc"));

            }catch(SQLException e){e.printStackTrace();}
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

