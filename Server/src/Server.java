import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {

    public static void main(String[] args) {
        Connection conn = null;
        try{
            //String url = "jdbc:sqlite3:" + System.getProperty("user.dir") + File.separator + "test.db";
            String url = "jdbc:sqlite:C:/Users/darki/Documents/School/2017-Spring/CSC-380/CSC380/Server/test.db";
    //Class.forName("");
            conn = DriverManager.getConnection(url);

            System.out.println("Connection has been established to database");
        }catch(SQLException e){
            e.printStackTrace();
        }
        try {
            int clientNumber = 0;
            ServerSocket listener = new ServerSocket(9090);
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
            this.connect = connect;
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        //run
        public void run(){
            try {

                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());

<<<<<<< HEAD
                String[] recieved = (String[])in.readObject();
                String rqst = recieved[0];
                System.out.println("Processing request " + rqst);

                switch (rqst.toLowerCase()) {
                    case "getencryptedid":
                        String id = getEncrypted(recieved[1]);
                        String[] sendBack = new String[]{id};
                        out.writeObject(sendBack);
                        break;
                    case "check":
                        compare();
                        break;
=======
                Object recieved = in.readObject();
                String[] rqst = new String[0];

                if(recieved.getClass() == rqst.getClass()) {

                    rqst = (String[]) recieved;
                    String method = rqst[0];
                    System.out.println("Processing request " + method);

                    switch (method) {
                        case "getEncryptedId":
                            //getEncrypted(rqst[1]);
                            String[] sendBack = new String[]{"yourencryptedID"};
                            out.writeObject(sendBack);
                            break;
                        case "check":
                            compare();
                            break;
                    }
                } else{
                    log("Wrong Data Type was recieved!: " + recieved.getClass());
>>>>>>> origin/master
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
<<<<<<< HEAD
        public  String getEncrypted(String userID){
=======
        public  String getEncrypted(String username){
>>>>>>> origin/master
            System.out.println("getEncrypted run");
            String sql = "SELECT uniqueID_enc FROM users WHERE user = \"" + userID + "\"";

            try(Connection conn = connect;
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)){
                System.out.println(rs.getString("user"));
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

