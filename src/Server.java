import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
    public static final int PORT = 7777;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        HashMap<String, Socket> listUser = new HashMap<>();
        while (true) {
            Socket socket = serverSocket.accept();
            ThreadedEchoServer server = new ThreadedEchoServer(socket, listUser);
            Thread t = new Thread(server);
            t.start();
        }
    }
}
