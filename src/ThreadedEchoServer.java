import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class ThreadedEchoServer implements Runnable {
    public static final int PORT = 7777;

    private Socket sock;

    public ThreadedEchoServer(Socket s) {
        System.out.println("nuova richiesta");
        sock = s;
        System.out.println(s.getInetAddress());
    }

    //    public void open () throws IOException {
//        ServerSocket serv = new ServerSocket(PORT);
//        while (true) {
//            Socket sock = serv.accept();
//            ThreadedEchoServer server = new ThreadedEchoServer(sock);
//            Thread t = new Thread(server);
//            t.start();
//        }
//    }
    public void run() {
        while (true) {
            BufferedReader brd = null;
            try {
                brd = new BufferedReader(new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_16));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = null;
            try {
                s = brd.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter prw = null;
            try {
                prw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(), StandardCharsets.UTF_16));
            } catch (IOException e) {
                e.printStackTrace();
            }
//            controlla se l'username è già utilizzato
            if (s.contains("<login>")) {
                prw.println("ack");
            } else {
//                user già connesso
                System.out.println(s);
                prw.println(s);
            }
            prw.flush();
        }
    }
//        finally {
//            try {
//                sock.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
}

