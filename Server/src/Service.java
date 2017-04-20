import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;

/** Received Array rules
 * Received[0] = method call
 * Received[1] = user name
 * Received[2+] = additional info
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
                    case "getaccounts":
                        getAccounts(recieved[1]);
                        break;
                    case "insertaccount":
                        insertAccount(recieved[1], recieved[2], recieved[3]);
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
            out.writeObject(singleRespond(rs.getString("uniqueID_enc")));
            rs.close();
            stmt.close();

        }catch(SQLException e) {
            log("Bad sql request as : " + e);
        }catch(IOException e){
            log("Bad write : " + e);
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
            if (dec.equals(decID))
                out.writeObject(singleRespond("true"));
            else
                out.writeObject(singleRespond("false"));

            rs.close();
            stmt.close();
        }catch(SQLException e) {
            log("Bad sql request as : " + e);
        }catch(IOException e){
            log("Bad write : " + e);
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
            out.writeObject(singleRespond("true"));

            stmt.close();
        }catch(SQLException e){
            out.writeObject(singleRespond("false"));

            log("Bad sql request as : " + e);
        }
    }

    private void checkAvailable(String user) throws IOException{
        try{
            //Send sql request
            String sql = "SELECT 1 FROM users WHERE user = \"" + user + "\"";
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            String r = rs.getString("1");
            if (r.equals("1"))
                out.writeObject(singleRespond("false"));

            rs.close();
            stmt.close();
        }catch(SQLException e){
            out.writeObject(singleRespond("true"));

            log("User does not exist or ");
            log("Bad sql request as : " + e);
        }
    }

    private void getAccounts(String decID){
        try{
            //Retrieve id
            String id = getUser(decID);

            //Retrieve accounts
            String sql = "SELECT encrypted FROM accounts WHERE user = \"" + id + "\"";
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<String> act = new ArrayList<>();
            while(rs.next()){
                act.add(rs.getString("ecrypted"));
            }
            rs.close();
            stmt.close();
            out.writeObject(act.toArray());
        }catch(SQLException e){
            log("Bad sql request as : " + e);
        }
        catch(IOException e){
            log("Bad write : " + e);
        }
    }

    private void insertAccount(String decID, String title, String enrypted) throws IOException{
        try {
            //Retrieve id
            String id = getUser(decID);

            //Retrieve accounts
            String sql = "INSERT INTO accounts WHERE user = \"" + id + "\"";
            Statement stmt = connect.createStatement();
            stmt.execute(sql);

            out.writeObject(singleRespond("true"));
        }catch(SQLException e){
            out.writeObject(singleRespond("false"));

            log("Bad sql request as : " + e);
        }
    }

    private String getUser(String decID){
        try{
            //Retrieve userID
            String sql = "SELECT user FROM users WHERE uniqueID_dec = \"" + decID + "\"";
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            String id = rs.getString("user");
            rs.close();
            stmt.close();
            return id;
        }catch(SQLException e){
            log("Bad sql request as : " + e);
            return null;
        }
    }

    private String[] singleRespond(String msg){
        String[] message = {msg};
        return message;
    }

    /**
     * Logs a simple message.  In this case we just write the
     * message to the server applications standard output.
     */
    private void log(String message) {
        System.out.println(message);
    }
}