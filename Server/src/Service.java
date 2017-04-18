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
                    case "checkdecrypted":
                        checkDecrypted(recieved[1], recieved[2]);
                        break;
                    case "registeruser":
                        registerUser(recieved[2], recieved[3]);
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
            Connection conn = this.connect;

            //Sending sql request
            String sql = "SELECT uniqueID_enc FROM users WHERE user = \"" + user + "\"";
            Statement stmt = conn.createStatement();
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
            Connection conn = this.connect;

            //Sending sql request
            String sql = "SELECT uniqueID_enc FROM users WHERE user = \"" + user + "\"";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            //Check id
            String decID = rs.getString("uniqueID_dec");
            String[] response = new String[1];
            if (dec.equals(decID))
                response[0] = "true";
            else response[0] = "false";
            out.writeObject(response);

        }catch(SQLException e) {
            log("Bad sql request as : " + e);
            e.printStackTrace();
        }catch(IOException e){
            log("Bad write : " + e);
            e.printStackTrace();
        }
    }

    private void registerUser() throws ClassNotFoundException, IOException{
        log("addUser running");

        try{
            Connection conn = this.connect;

            //Send
            String[] info = (String[])in.readObject();
            String sql = "INSERT INTO users values(\"" + info[0] + "\", \"" + info[1] + "\", \"" + info[2] + "\")";
            Statement stmt = conn.createStatement();

            //Adding user to table
            stmt.execute(sql);
            String[] response = {"true"};
            out.writeObject(response);

            stmt.close();
        }catch(SQLException e){
            String[] response = {"false"};
            out.writeObject(response);
            log("Bad sql request as: " + e);
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