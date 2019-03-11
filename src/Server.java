import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Creiamo una variabile static final con il numero di porta desiserato per aprire
 * un canale di comunicazione su cui restare in ascolto.
 */
public class Server {
    public static final int PORT = 7777;

    public static void main(String[] args) throws IOException {
//        Ciclo continuo in attesa di richieste di comunicazione dai client
        ServerSocket serverSocket = new ServerSocket( PORT );
        HashMap<String, Socket> listUser = new HashMap<>();
        while (true) {
            Socket socket = serverSocket.accept();
            ThreadedEchoServer server = new ThreadedEchoServer( socket, listUser );
            Thread t = new Thread( server );
            t.start();
        }
    }
}
