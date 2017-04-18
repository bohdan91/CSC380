
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

}

