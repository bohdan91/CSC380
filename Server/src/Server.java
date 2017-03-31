
import java.io.*;
import java.net.*;

public class Server {


    public static void main(String[] args) {
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
                            //getEncrypted();
                            String[] sendBack = new String[]{"yourencryptedID"};
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
        public  String getEncrypted(){
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

