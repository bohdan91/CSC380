import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Bohdan on 3/29/17.
 */
public class Client {

    public static void main(String[] args) {
        try {
            // Make connection and initialize streams
            Socket socket = new Socket("passman.ddns.net", 9898);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Consume the initial welcoming messages from the server
            for (int i = 0; i < 3; i++) {
                System.out.println(in.readLine() + "\n");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("Enter message: ");
                String s = br.readLine();
                out.println(s);

                String response = in.readLine();
                System.out.println(response);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
