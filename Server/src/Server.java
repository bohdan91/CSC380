
import java.io.*;
import java.net.*;
import java.sql.*;

/**
 * Created by Alex and Bohdan
 *
 * Server
 *
 * A simple server program. On start it runs infinitely, and listens for a user
 * connection. When the user connects, it reroutes them to the service class.
 */
public class Server {

    /**
     * Main Method
     *
     * The main class for the server program. This method connects to the sqlite
     * database, then proceeds to loop infinitely until the server program is terminated.
     * During the infinite loop, it listens for user connections, and allows for parallel
     * connection for multiple users to connect to at once.
     *
     * @param args
     */
    public static void main(String[] args) {
        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "test.db";
        try {
            int clientNumber = 0;
            ServerSocket listener = new ServerSocket(9898);
            try {
                while (true) {
                    new Service(listener.accept(), clientNumber++, url).start();
                }
            } finally {
                listener.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}

