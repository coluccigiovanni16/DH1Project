import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class provaserver {
    public static final int PORT = 7777;

    public static void main(String[] args) throws IOException {
        ServerSocket serv = new ServerSocket(PORT);
        HashMap<String, Socket> listUser = new HashMap<String, Socket>();
        while (true) {
            Socket sock = serv.accept();
            System.out.println(listUser.toString());
            ThreadedEchoServer server = new ThreadedEchoServer(sock, listUser);
            Thread t = new Thread(server);
            t.start();
        }
    }
}
