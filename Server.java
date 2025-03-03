import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345; // Port for communication
    private static Set<PrintWriter> clientWriters = new HashSet<>(); // Store writers for broadcasting

    public static void main(String[] args) {
        System.out.println("Server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start(); // Accept and start a new thread for each client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast messages to all clients
    private static void broadcast(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    // Handle individual clients
    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out); // Add client writer to the set
                }

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                    broadcast(message); // Broadcast message to all clients
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out); // Remove client writer from the set
                }
            }
        }
    }
}
