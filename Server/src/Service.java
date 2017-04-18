import java.io.*;
import java.net.*;
import java.sql.*;

/* Received Array rules
 * Received[0] = method call
 * Received[1] = user name
 * Received[2] = additional info
 */
public class Service extends Thread{
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
            //0 = method
            //1 = username
            if (recieved.getClass() == recieved.getClass()) {

                String method = recieved[0];
                log("Processing request " + method);

                switch (method.toLowerCase()) {
                    case "getencrypted":
                        getEncrypted(recieved[1]);
                        break;
                    case "checkdecrypted":
                        checkDecrypted(recieved[1], recieved[2]);
                        break;
                    case "registeruser":
                        registerUser(recieved[1], recieved[2], recieved[3]);
                        break;
                    case "isuseravailable":
                        checkAvailable(recieved[1]);
                        break;
                }
            } else {
                log("Wrong Data Type was recieved!: " + recieved.getClass());
            }

            in.close();
            out.close();
            socket.close();
        }catch(IOException e){
            log("ERROR  " + e);
        } catch(ClassNotFoundException e){
            e.printStackTrace();
            log("WRONG OBJECT WAS RECIEVED\n" + e);
        }
    }

    public void getEncrypted(String user){
        log("Running getEncrypted");

        try{
            //Sending sql request
            String sql = "SELECT uniqueID_enc FROM users WHERE user = \"" + user + "\"";
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            //Send response to user
            String[] response = {rs.getString("uniqueID_enc")};
            out.writeObject(response);
            rs.close();
            stmt.close();

        }catch(SQLException e) {
            log("Bad sql request as : " + e);
            e.printStackTrace();
        }catch(IOException e){
            log("Bad write : " + e);
            e.printStackTrace();
        }
    }

    public void checkDecrypted(String user, String dec){
        log("Runngin checkDecrypted");

        try{
            //Sending sql request
            String sql = "SELECT uniqueID_dec FROM users WHERE user = \"" + user + "\"";
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            //Check id
            String decID = rs.getString("uniqueID_dec");
            String[] response = new String[1];
            if (dec.equals(decID))
                response[0] = "true";
            else response[0] = "false";
            out.writeObject(response);

            rs.close();
            stmt.close();
        }catch(SQLException e) {
            log("Bad sql request as : " + e);
            e.printStackTrace();
        }catch(IOException e){
            log("Bad write : " + e);
            e.printStackTrace();
        }
    }

    private void registerUser(String user, String encID, String decID) throws IOException{
        log("addUser running");

        try{
            //Send sql request
            String sql = "INSERT INTO users values(\"" + user + "\", \"" + encID + "\", \"" + decID + "\")";
            Statement stmt = connect.createStatement();

            //Adding user to table
            stmt.execute(sql);
            String[] response = {"true"};
            out.writeObject(response);

            stmt.close();
        }catch(SQLException e){
            String[] response = {"false"};
            out.writeObject(response);

            log("Bad sql request as : " + e);
            e.printStackTrace();
        }
    }

    private void checkAvailable(String user) throws IOException{
        try{
            //Send sql request
            String sql = "SELECT 1 FROM users WHERE user = \"" + user + "\"";
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            String r = rs.getString("1");
            String[] response = {"false"};
            out.writeObject(response);

            rs.close();
            stmt.close();
        }catch(SQLException e){
            String[] response = {"true"};
            out.writeObject(response);

            log("Bad sqk request as : " + e);
            e.printStackTrace();
        }
    }

    /**
     * Logs a simple message.  In this case we just write the
     * message to the server applications standard output.
     */
    private void log(String message) {
        System.out.println(message);
    }
}