import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;

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
 * accounts(user text primarykey, title text, enc text)
 */
public class Service extends Thread{
    private Socket socket;
    private int clientNumber;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Connection connect;

    /**
     * Constructor for Service class
     *
     * @param socket
     * @param clientNumber
     * @param connect
     */
    public Service(Socket socket, int clientNumber, Connection connect) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        this.connect = connect;
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
     *      [2] = title
     *      [3] = enc (Encrypted account string)
     * deleteaccount :
     *      [0] = "deleteaccount"
     *      [1] = uniqueID_dec
     *      [2] = title
     * updateaccount :
     *      [0] = "updateaccount"
     *      [1] = uniqueID_dec
     *      [2] = title
     *      [3] = enc (Encrypted account string)
     * changetitle :
     *      [0] = "changetitle"
     *      [1] = uniqueID_dec
     *      [2] = title
     *      [3] = new title
     * istitleavailable
     *      [0] = "istitleavailable"
     *      [1] = uniqueID_dec
     *      [2] = title
     */
    public void run(){
        try {

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
                        insertAccount(recieved[1], recieved[2], recieved[3]);
                        break;
                    case "deleteaccount":
                        deleteAccount(recieved[1], recieved[2]);
                        break;
                    case "updateaccount":
                        updateAccount(recieved[1], recieved[2], recieved[3]);
                        break;
                    case "changetitle":
                        changeTitle(recieved[1], recieved[2], recieved[3]);
                        break;
                    case "istitleavailable":
                        checkTitle(recieved[1], recieved[2]);
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
            String id = getUser(decID);

            //Retrieve accounts
            String sql = "SELECT enc FROM accounts WHERE user = \"" + id + "\"";
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            ArrayList<String> act = new ArrayList<>();
            while(rs.next()){
                act.add(rs.getString("enc"));
            }
            String[] response = new String[act.size()];
            for(int i =0; i < act.size(); i++){
                response[i] = act.get(i);
            }
            rs.close();
            stmt.close();
            out.writeObject(response);
        }catch(SQLException e){
            log("Bad sql request as : " + e);
        }
        catch(IOException e){
            log("Bad write : " + e);
        }
    }

    /**
     * Insert Account
     *
     * Receives a decID, title, and encrypted account string.
     * It uses the getUser(decID) method to retrieve the username from
     * the database, and uses the username to store the account into
     * the accounts table.
     * It then returns true if the account was added, or false if it was not.
     *
     * @param decID
     * @param title
     * @param enc
     * @throws IOException
     */
    private void insertAccount(String decID, String title, String enc) throws IOException{
        try {
            //Retrieve id
            String id = getUser(decID);

            if (checkTitle(decID, title)) {
                //Retrieve accounts
                String sql = "INSERT INTO accounts values(\"" + id + "\", \"" + title + "\", \"" + enc + "\")";
                Statement stmt = connect.createStatement();
                stmt.execute(sql);
                stmt.close();

                out.writeObject(singleRespond("true"));
            }else{
                out.writeObject(singleRespond("false"));
            }
        }catch(SQLException e){
            out.writeObject(singleRespond("false"));

            log("Bad sql request as : " + e);
        }
    }

    /**
     * Delete Account
     *
     * Recieves the decID, and title that the user desires to delete. It then processes
     * the sql request to remove the account. The function then writes out true or false
     * whether the account was deleted or not.
     * 
     * @param decID
     * @param title
     * @throws IOException
     */
    private void deleteAccount(String decID, String title)throws IOException{
        try{
            //Retrieve id
            String id = getUser(decID);

            String sql = "DELETE FROM accounts WHERE user = \"" + id + "\" AND title = \"" + title + "\"";
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
     * It receives the decID, title, and encrypted account String.
     * It uses the getUser(decID) method to retrieve the username.
     * It then uses the username to update and existing account in the
     * account table.
     *
     * @param decID
     * @param title
     * @param enc
     * @throws IOException
     */
    private void updateAccount(String decID, String title, String enc) throws IOException{
        try{
            //Retrieve id
            String id = getUser(decID);

            //Update info
            String sql = "UPDATE accounts SET enc = \"" + enc + "\" WHERE user = \"" + id + "\" AND title = \"" + title + "\"";
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
     * Change Account Title
     *
     * This class takes in a decID, title, and a new title.
     * It uses the getUser(decID) method to retrieve the username.
     * It then changes the title of an account.
     *
     * @param decID
     * @param title
     * @param newTitle
     * @throws IOException
     */
    private void changeTitle(String decID, String title, String newTitle) throws IOException{
        try{
            //Retrieve id
            String id = getUser(decID);

            if (checkTitle(decID, newTitle)) {
                //Update title
                String sql = "UPDATE accounts SET title = \"" + newTitle + "\" WHERE user = \"" + id + "\" AND title = \"" + title + "\"";
                Statement stmt = connect.createStatement();
                stmt.execute(sql);
                stmt.close();

                out.writeObject(singleRespond("true"));
            }else{
                out.writeObject(singleRespond("false"));
            }
        }catch(SQLException e){
            out.writeObject(singleRespond("false"));

            log("Bad sql request as : " + e);
        }
    }

    /**
     * Check Title
     *
     * It receives a decID, and a title to check.
     * It receives a user id from the getUser(decID) method.
     * It then proceeds to check the existence of the title.
     * If the title exists, it returns false;
     * if the title does not exist, it return true;
     * This method can be used internally by other methods,
     * or be used externally by the client.
     *
     * @param decID
     * @param title
     * @return boolean
     */
    private boolean checkTitle(String decID, String title){
        try{
            //Retrieve id
            String id = getUser(decID);

            //Check title
            String sql = "SELECT 1 FROM accounts WHERE user = \"" + id + "\" AND title = \"" + title + "\"";
            Statement stmt = connect.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            String r = rs.getString("1");
            if (r.equals("1")){
                try{
                    out.writeObject(singleRespond("false"));
                    return false;
                }catch(IOException e){
                    return false;
                }
            }
        }catch(SQLException e){
            try{
                out.writeObject(singleRespond("true"));

                log("Bad sql request as : " + e);
                return true;
            }catch(IOException i){

                log("Bad sql request as : " + e);
                return true;
            }
        }
        return false;
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