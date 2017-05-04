import javax.management.StandardEmitterMBean;
import javax.swing.plaf.nimbus.State;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by Alex Voytovich
 * The service class is what allows communication between the client and
 * the database. It will receive a string of commands and data, and
 * retrieve, insert, or update what is necessary from the database
 *
 * Received Array rules:
 * Received[0] = method call
 * Received[1] = user name
 * Received[2+] = additional info
 *
 * tables:
 * users(user text UNIQUE PRIMARY KEY, uniqueID_enc text, uniqueID_dec text)
 * accounts(user text primarykey, id int, enc text)
 */
public class Service extends Thread{
    private Socket socket;
    private int clientNumber;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Connection connect;
    private String db;

    /**
     * Constructor for Service class
     *
     * @param socket
     * @param clientNumber
     * @param db
     */
    public Service(Socket socket, int clientNumber, String db) {
        connect = null;
        this.db = db;
        this.socket = socket;
        this.clientNumber = clientNumber;
        log("New connection with client# " + clientNumber + " at " + socket);
    }

    /**
     * Main running method
     *
     * This method runs the Service class.
     * It first connects to the database and the client.
     * It listens for a string of commands, and executes a
     * method according to what the user passed in.
     * Finally, it closes the connection
     *
     * possible commands
     * getencrypted :
     *      [0] = "getencrypted"
     *      [1] = user
     * checkdecrypted :
     *      [0] = "checkdecrypted
     *      [1] = user
     *      [2] = uniqueID_dec
     * registeruser :
     *      [0] = "registeruser"
     *      [1] = user
     *      [2] = uniqueID_enc
     *      [3] = uniqueID_dec
     * isuseravailable :
     *      [0] = "isuseravailable"
     *      [1] = user
     * getaccounts :
     *      [0] = "getaccounts"
     *      [1] = uniqueID_dec
     * insertaccount :
     *      [0] = "insertaccount"
     *      [1] = uniqueID_dec
     *      [2] = enc (Encrypted account string)
     * deleteaccount :
     *      [0] = "deleteaccount"
     *      [1] = uniqueID_dec
     *      [2] = id
     * updateaccount :
     *      [0] = "updateaccount"
     *      [1] = uniqueID_dec
     *      [2] = id
     *      [3] = enc (Encrypted account string)
     */
    public void run(){
        try(Connection connect = DriverManager.getConnection(db))
        {
            this.connect = connect;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            String[] recieved = (String[])in.readObject();
            //0 = method
            //1 = username
            //2+= other data
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
                        insertAccount(recieved[1], recieved[2]);
                        break;
                    case "deleteaccount":
                        deleteAccount(recieved[1], recieved[2]);
                        break;
                    case "updateaccount":
                        updateAccount(recieved[1], recieved[2], recieved[3]);
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
            log("WRONG OBJECT WAS RECIEVED\n" + e);
        }catch(SQLException e){
            log("Unable to connect to database");
        }
    }

    /**
     * Retrieve encrypted id
     *
     * Takes the username, and sends back an encrypted id for user to unlock
     *
     * @param user
     */
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


    /**
     * Check Decrypted ID
     *
     * Recieves user and decrypted id from user.
     * Retrieves the dycrpted id from the database using the username.
     * Compares the two decrypted id's, and response true or false
     * according to result.
     *
     * @param user
     * @param dec
     */
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


    /**
     * Register New User
     *
     * Registers a new user into the database.
     * Sends back true if the user was added.
     * Sends back false if user already exists
     *
     * @param user
     * @param encID
     * @param decID
     * @throws IOException
     */
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


    /**
     * Check User Availability
     *
     * Receives a username to check.
     * If the user exists, it returns false,
     * if the user does no exist, it return true.
     *
     * @param user
     * @throws IOException
     */
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


    /**
     * Get Accounts
     *
     * Receives a decrypted id, which is processed by getUser(decID)
     * to receive a username. The username is then used to retrieve
     * all saved accounts associated with that user.
     * It stores them into an ArrayList, and returns an array of accounts
     * using the .toArray() method.
     *
     * @param decID
     */
    private void getAccounts(String decID){
        try{
            //Retrieve id
            String user = getUser(decID);
            if (user == null){
                out.writeObject(singleRespond("false"));
                return;
            }

            //Retrieve accounts
            String sql = "SELECT id, enc FROM accounts WHERE user = \"" + user + "\"";
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<String> act = new ArrayList<>();
            while(rs.next()){
                act.add(rs.getString("enc"));
                act.add(rs.getString("id"));
            }
            rs.close();
            stmt.close();

            String[] response = new String[act.size()];
            for(int i = 0; i < act.size(); i++){
                response[i] = act.get(i);
            }

            out.writeObject(response);
        }catch(SQLException e){
            log("Bad sql request as : " + e);
        }
        catch(IOException e){
            log("Bad write : " + e);
        }
    }

    /**
     * Insert Accounts
     *
     * Takes in the decID for username retrieval. Then it finds the next available number
     * id to be used for the account. Once the username and account id are found, the accounts
     * is inserted, and the client receives the account id.
     *
     * @param decID
     * @param enc
     * @throws IOException
     */
    private void insertAccount(String decID, String enc) throws IOException{
        try{
            String user = getUser(decID);
            if (user == null){
                out.writeObject(singleRespond("0"));
                return;
            }

            String sql = "SELECT id FROM accounts WHERE user = \"" + user + "\"";
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            TreeSet<Integer> tree = new TreeSet<>();
            while(rs.next()){
                tree.add(rs.getInt("id"));
            }
            if (tree.isEmpty()) {
                addAccId(user, 1, enc);
            }else{
                int id = tree.last() + 1;
                addAccId(user, id, enc);
            }

        }catch(SQLException e){
            out.writeObject("0");

            log("Bad sql request as " + e);
        }
    }

    /**
     * Insert Account with id
     *
     * It receives the username, id and enc, and inserts the account into
     * the database.
     * It then returns the id if the account was added, or 0 if it was not.
     *
     * @param user
     * @param id
     * @param enc
     * @throws IOException
     */
    private void addAccId(String user, int id, String enc) throws IOException{
        try {

            //Retrieve accounts
            String sql = "INSERT INTO accounts values(\"" + user + "\", " + id + ", \"" + enc + "\")";
            Statement stmt = connect.createStatement();
            stmt.execute(sql);
            stmt.close();

            out.writeObject(singleRespond(Integer.toString(id)));

        }catch(SQLException e){
            out.writeObject(singleRespond("0"));

            log("Bad sql request as : " + e);
        }
    }

    /**
     * Delete Account
     *
     * Recieves the decID, and account id that the user desires to delete. It then processes
     * the sql request to remove the account. The function then writes out true or false
     * whether the account was deleted or not.
     *
     * @param decID
     * @param id
     * @throws IOException
     */
    private void deleteAccount(String decID, String id)throws IOException{
        try{
            //Retrieve id
            String user = getUser(decID);
            if (user == null) {
                out.writeObject(singleRespond("false"));
                return;
            }

            String sql = "DELETE FROM accounts WHERE user = \"" + user + "\" AND id = \"" + Integer.parseInt(id) + "\"";
            Statement stmt = connect.createStatement();
            stmt.execute(sql);
            stmt.close();

            out.writeObject(singleRespond("true"));
        }catch(SQLException e){
            out.writeObject(singleRespond("false"));

            log("Bad sql request as : " + e);
        }
    }


    /**
     * Update Account
     *
     * It receives the decID, account id, and encrypted account String.
     * It uses the getUser(decID) method to retrieve the username.
     * It then uses the username to update and existing account in the
     * account table.
     *
     * @param decID
     * @param id
     * @param enc
     * @throws IOException
     */
    private void updateAccount(String decID, String id, String enc) throws IOException{
        try{
            //Retrieve id
            String user = getUser(decID);
            if (user == null) {
                out.writeObject(singleRespond("false"));
                return;
            }

            //Update info
            String sql = "UPDATE accounts SET enc = \"" + enc + "\" WHERE user = \"" + user + "\" AND id = \"" + Integer.parseInt(id) + "\"";
            Statement stmt = connect.createStatement();
            stmt.execute(sql);
            stmt.close();

            out.writeObject(singleRespond("true"));
        }catch(SQLException e){
            out.writeObject(singleRespond("false"));

            log("Bad sql request as : " + e);
        }
    }

    /**
     * Get Username
     *
     * It receives a decrypted id, and finds the username
     * associated with it for use in other methods.
     *
     * @param decID
     * @return id
     */
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

    /**
     * Single String Respond
     *
     * It takes a message String,
     * and returns a single element array for responding
     * to a client's request.
     *
     * @param msg
     * @return message[]
     */
    private String[] singleRespond(String msg){
        String[] message = {msg};
        return message;
    }

    /**
     * Log
     *
     * Prints a message to the output stream
     * and logs it into a log file*.
     *
     * @param message
     */
    private void log(String message) {
        System.out.println(message);
    }
}