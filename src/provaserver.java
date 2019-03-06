import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class provaserver {
    public static final int PORT = 7777;
    public static void main(String[] args) throws IOException {
        ServerSocket serv = new ServerSocket(PORT);
        while (true) {
            Socket sock = serv.accept();
            ThreadedEchoServer server = new ThreadedEchoServer(sock);
            Thread t = new Thread(server);
            t.start();
        }
    }
}
