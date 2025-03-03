import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost"; // Server address
    private static final int SERVER_PORT = 12345; // Server port

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            // Set up input and output streams for communication
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Start a new thread to listen for incoming messages
            new Thread(new IncomingMessageListener(in)).start();

            // Send user input messages to the server
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thread to listen for incoming messages from the server
    private static class IncomingMessageListener implements Runnable {
        private BufferedReader in;

        public IncomingMessageListener(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message); // Print received message to console
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
